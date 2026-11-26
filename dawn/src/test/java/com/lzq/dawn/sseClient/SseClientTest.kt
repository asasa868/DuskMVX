package com.lzq.dawn.sseClient

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.Closeable
import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

/**
 * @projectName com.lzq.dawn.sseClient.SseClientTest
 * @author Lzq
 * @date : Created by Lzq on 2026/11/25 14:02:52
 * @version 0.0.30
 * @description: 验证事件流解析、重连、取消及连接资源释放行为
 */
class SseClientTest {
    @Test fun parserUsesBlankLinesAndPreservesMultilineDataEventAndId() {
        val events = parse("id: 42\nevent: update\ndata: first\ndata: second\n\ndata: next\n\n")

        assertEquals(listOf(SseEvent("update", "first\nsecond", "42"), SseEvent("message", "next", "42")), events)
    }

    @Test fun parserIgnoresCommentsAndUnknownFieldsAndDoesNotDispatchEmptyFrames() {
        val events = parse(": heartbeat\n\nunknown: value\nevent: ignored\n\ndata\n\n")

        assertEquals(listOf(SseEvent("message", "", "")), events)
    }

    @Test fun parserAcceptsCrLfCrAndLfAndRemovesOnlyOneLeadingValueSpace() {
        for (newline in listOf("\r\n", "\r", "\n")) {
            val events = parse("\uFEFFdata:  space" + newline + "data:value" + newline + newline)

            assertEquals(listOf(SseEvent("message", " space\nvalue", "")), events)
        }
    }

    @Test fun parserDiscardsUnterminatedFrameAtEof() {
        assertTrue(parse("data: incomplete").isEmpty())
        assertTrue(parse("data: incomplete\n").isEmpty())
        assertEquals(listOf(SseEvent("message", "complete", "")), parse("data: complete\n\ndata: incomplete\n"))
    }

    @Test fun parserKeepsIdAcrossEventsAndEmptyIdResetsItWhileNulIsIgnored() {
        val ids = mutableListOf<String?>()
        val events = mutableListOf<SseEvent>()
        SseEventReader.read(ByteArrayInputStream("id: saved\n\ndata: one\n\nid:\ndata: two\n\nid: bad\u0000id\ndata: three\n\n".toByteArray()), ids::add, events::add)

        assertEquals(listOf("saved", "saved", "", ""), ids)
        assertEquals(listOf("saved", "", ""), events.map { it.id })
    }

    @Test fun parserWaitsForFullUtf8AndLineBoundaryAcrossChunks() {
        val bytes = "data: 中文\r\n\r\n".toByteArray()
        val stream = object : ByteArrayInputStream(bytes) {
            override fun read(buffer: ByteArray, offset: Int, length: Int): Int = super.read(buffer, offset, minOf(length, 1))
        }
        val events = mutableListOf<SseEvent>()
        SseEventReader.read(stream, {}, events::add)

        assertEquals(listOf(SseEvent("message", "中文", "")), events)
    }

    @Test fun clientAcceptsStandardEventStreamAndNormalEofDoesNotReconnect() = runBlocking {
        val server = MockWebServer()
        server.enqueue(eventResponse("data: accepted\n\n"))
        server.start()
        try {
            val states = CopyOnWriteArrayList<SseState>()
            val events = withTimeout(5000) { client(server, states).connect().toList() }

            assertEquals(listOf(SseEvent("message", "accepted", "")), events)
            assertEquals(listOf(SseState.Connecting, SseState.Connected, SseState.Disconnected), states)
            assertEquals(1, server.requestCount)
        } finally { server.shutdown() }
    }

    @Test fun clientRejectsHttpErrorWithoutParsingBodyOrRetrying() = runBlocking {
        val server = MockWebServer()
        server.enqueue(eventResponse("data: must not dispatch\n\n").setResponseCode(503))
        server.start()
        try {
            val states = CopyOnWriteArrayList<SseState>()
            val events = mutableListOf<SseEvent>()
            try {
                withTimeout(5000) { client(server, states).connect().collect { events.add(it) } }
                fail("expected HTTP failure")
            } catch (expected: IOException) {
                assertTrue(expected.message!!.contains("503"))
            }

            assertTrue(events.isEmpty())
            assertFalse(states.contains(SseState.Connected))
            assertEquals(1, server.requestCount)
        } finally { server.shutdown() }
    }

    @Test fun clientRejectsMissingOrWrongMediaType() = runBlocking {
        for (type in listOf<String?>(null, "application/json")) {
            val server = MockWebServer()
            val response = MockResponse().setBody("data: invalid\n\n")
            if (type != null) response.setHeader("Content-Type", type)
            server.enqueue(response)
            server.start()
            try {
                try {
                    withTimeout(5000) { SseClient(SseConfig(server.url("/").toString(), maxRetry = 0)).connect().toList() }
                    fail("expected media type failure")
                } catch (expected: IOException) {
                    assertTrue(expected.message!!.contains("Invalid SSE response"))
                }
                assertEquals(1, server.requestCount)
            } finally { server.shutdown() }
        }
    }

    @Test fun clientDiscardsIncompleteEofFrame() = runBlocking {
        val server = MockWebServer()
        server.enqueue(eventResponse("data: complete\n\ndata: incomplete\n"))
        server.start()
        try {
            val events = withTimeout(5000) { SseClient(SseConfig(server.url("/").toString())).connect().toList() }

            assertEquals(listOf(SseEvent("message", "complete", "")), events)
            assertEquals(1, server.requestCount)
        } finally { server.shutdown() }
    }

    @Test fun clientRetriesTransportFailureOnlyWithinConfiguredLimit() = runBlocking {
        val server = MockWebServer()
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START))
        server.enqueue(eventResponse("data: recovered\n\n"))
        server.start()
        try {
            val states = CopyOnWriteArrayList<SseState>()
            val events = withTimeout(5000) { client(server, states, maxRetry = 1).connect().toList() }

            assertEquals(listOf(SseEvent("message", "recovered", "")), events)
            assertEquals(2, server.requestCount)
            assertTrue(states.contains(SseState.Reconnecting(1)))
        } finally { server.shutdown() }
    }

    @Test fun clientExhaustionEndsWithAnErrorAndNoExtraAttempt() = runBlocking {
        val server = MockWebServer()
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START))
        server.start()
        try {
            val states = CopyOnWriteArrayList<SseState>()
            try {
                withTimeout(5000) { client(server, states, maxRetry = 0).connect().toList() }
                fail("expected transport failure")
            } catch (expected: IOException) {
                assertFalse(states.any { it is SseState.Reconnecting })
            }
            assertEquals(1, server.requestCount)
        } finally { server.shutdown() }
    }

    @Test fun transportRetryUsesLastCommittedEventIdAndDropsIncompleteFrame() = runBlocking {
        val server = MockWebServer()
        val first = "id: resumed\ndata: committed\n\nid: uncommitted\ndata: incomplete" + "x".repeat(300)
        server.enqueue(eventResponse(first).setSocketPolicy(SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY))
        server.enqueue(eventResponse("data: recovered\n\n"))
        server.start()
        try {
            val events = withTimeout(5000) {
                SseClient(SseConfig(server.url("/").toString(), maxRetry = 1, retryDelayMillis = 0)).connect().toList()
            }
            val initial = server.takeRequest(1, TimeUnit.SECONDS)!!
            val retry = server.takeRequest(1, TimeUnit.SECONDS)!!

            assertNull(initial.getHeader("Last-Event-ID"))
            assertEquals("resumed", retry.getHeader("Last-Event-ID"))
            assertEquals(listOf(SseEvent("message", "committed", "resumed"), SseEvent("message", "recovered", "")), events)
        } finally { server.shutdown() }
    }

    @Test fun cancellingOneCollectorDoesNotCancelAnotherCollector() = runBlocking {
        OpenEventStreamServer(expectedConnections = 2).use { server ->
            val sse = SseClient(SseConfig(server.url, maxRetry = 0))
            val first = launch { sse.connect().collect() }
            withTimeout(5000) {
                while (server.requestCount < 1) kotlinx.coroutines.delay(1)
            }
            val second = async { sse.connect().take(1).toList() }
            withTimeout(5000) {
                while (server.requestCount < 2) kotlinx.coroutines.delay(1)
            }
            assertTrue(first.isActive)
            assertTrue(second.isActive)

            withTimeout(5000) { first.cancelAndJoin() }
            server.awaitPeerClosed(0)
            assertTrue(second.isActive)
            server.sendEvent(1, "independent")
            val events = withTimeout(5000) { second.await() }
            server.awaitPeerClosed(1)

            assertEquals(listOf(SseEvent("message", "independent", "")), events)
            assertEquals(2, server.requestCount)
        }
    }

    @Test fun cancellationClosesActiveConnectionAndDoesNotRetryOrEmitLaterStates() = runBlocking {
        OpenEventStreamServer().use { server ->
            val states = CopyOnWriteArrayList<SseState>()
            val connected = CompletableDeferred<Unit>()
            val sse = SseClient(SseConfig(server.url, retryDelayMillis = 0), object : SseCallback {
                override fun onStateChanged(state: SseState) { states.add(state); if (state == SseState.Connected) connected.complete(Unit) }
            })
            val job = launch { sse.connect().collect() }
            withTimeout(5000) { connected.await() }
            withTimeout(5000) {
                while (server.requestCount < 1) kotlinx.coroutines.delay(1)
            }
            assertTrue(job.isActive)

            withTimeout(5000) { job.cancelAndJoin() }
            server.awaitPeerClosed(0)
            val count = states.size
            kotlinx.coroutines.delay(100)

            assertEquals(count, states.size)
            assertEquals(1, server.requestCount)
            assertFalse(states.any { it is SseState.Reconnecting })
        }
    }

    @Test fun concurrentCollectorsOwnIndependentConnections() = runBlocking {
        val server = MockWebServer()
        server.enqueue(eventResponse("data: first\n\n"))
        server.enqueue(eventResponse("data: second\n\n"))
        server.start()
        try {
            val sse = SseClient(SseConfig(server.url("/").toString(), maxRetry = 0))
            val first = async { sse.connect().toList() }
            val second = async { sse.connect().toList() }
            val results = withTimeout(5000) { listOf(first.await(), second.await()) }

            assertEquals(setOf("first", "second"), results.flatten().map { it.data }.toSet())
            assertEquals(2, server.requestCount)
        } finally { server.shutdown() }
    }

    @Test fun incompleteIdAtEofDoesNotCommitAResumeHeader() {
        var id: String? = null
        SseEventReader.read(ByteArrayInputStream("id: uncommitted\n".toByteArray()), { id = it }, {})

        assertNull(id)
    }

    @Test fun committedEmptyIdRemovesConfiguredResumeHeaderOnRetry() = runBlocking {
        val server = MockWebServer()
        server.enqueue(eventResponse("id:\n\n" + "x".repeat(300)).setSocketPolicy(SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY))
        server.enqueue(eventResponse("data: recovered\n\n"))
        server.start()
        try {
            val events = withTimeout(5000) {
                SseClient(SseConfig(server.url("/").toString(), headers = mapOf("Last-Event-ID" to "configured"), maxRetry = 1, retryDelayMillis = 0)).connect().toList()
            }
            val initial = server.takeRequest(1, TimeUnit.SECONDS)!!
            val retry = server.takeRequest(1, TimeUnit.SECONDS)!!

            assertEquals("configured", initial.getHeader("Last-Event-ID"))
            assertNull(retry.getHeader("Last-Event-ID"))
            assertEquals(listOf(SseEvent("message", "recovered", "")), events)
        } finally { server.shutdown() }
    }

    @Test fun consumerCompletionIsNotRetriedAsATransportFailure() = runBlocking {
        val server = MockWebServer()
        server.enqueue(eventResponse((1..200).joinToString("") { "data: $it\n\n" }))
        server.start()
        try {
            val states = CopyOnWriteArrayList<SseState>()
            val events = withTimeout(5000) { client(server, states).connect().buffer(0).take(1).toList() }
            kotlinx.coroutines.delay(100)

            assertEquals(listOf(SseEvent("message", "1", "")), events)
            assertEquals(1, server.requestCount)
            assertFalse(states.any { it is SseState.Reconnecting })
        } finally { server.shutdown() }
    }

    @Test fun stateCallbackFailuresTerminateInsteadOfLeavingCollectionOpen() = runBlocking {
        for (target in listOf(SseState.Connecting, SseState.Connected, SseState.Disconnected, SseState.Reconnecting(1))) {
            val server = MockWebServer()
            server.enqueue(if (target is SseState.Reconnecting) MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START) else eventResponse("data: event\n\n"))
            server.start()
            try {
                val expected = IllegalStateException("callback failed")
                val sse = SseClient(SseConfig(server.url("/").toString(), maxRetry = 1, retryDelayMillis = 0), object : SseCallback {
                    override fun onStateChanged(state: SseState) { if (state == target) throw expected }
                })
                try {
                    withTimeout(5000) { sse.connect().toList() }
                    fail("expected callback failure")
                } catch (actual: IllegalStateException) {
                    assertEquals(expected.message, actual.message)
                }

                assertTrue(server.requestCount <= 1)
            } finally { server.shutdown() }
        }
    }

    @Test fun clientRejectsSuccessfulStatusOtherThan200() = runBlocking {
        val server = MockWebServer()
        server.enqueue(eventResponse("data: wrong status\n\n").setResponseCode(201))
        server.start()
        try {
            val states = CopyOnWriteArrayList<SseState>()
            try {
                withTimeout(5000) { client(server, states).connect().toList() }
                fail("expected non-200 failure")
            } catch (expected: IOException) {
                assertTrue(expected.message!!.contains("201"))
            }

            assertFalse(states.contains(SseState.Connected))
            assertEquals(1, server.requestCount)
        } finally { server.shutdown() }
    }

    /** 保持分块消息体未结束，由服务端读取结束直接证明消费者取消关闭了底层连接。 */
    private class OpenEventStreamServer(expectedConnections: Int = 1) : Closeable {
        private val listener = ServerSocket(0, expectedConnections, InetAddress.getByName("127.0.0.1")).apply {
            soTimeout = 5000
        }
        private val sockets = CopyOnWriteArrayList<Socket>()
        private val workers = CopyOnWriteArrayList<Thread>()
        private val failures = CopyOnWriteArrayList<Throwable>()
        private val peerClosed = List(expectedConnections) { CountDownLatch(1) }
        private val stopping = AtomicBoolean(false)
        private val requests = AtomicInteger()
        val requestCount: Int get() = requests.get()
        val url = "http://127.0.0.1:${listener.localPort}/"
        private val accepting = thread(name = "sse-test-accept", isDaemon = true) {
            try {
                repeat(expectedConnections) { index ->
                    val socket = listener.accept().apply { soTimeout = 10000 }
                    sockets.add(socket)
                    workers.add(thread(name = "sse-test-stream-$index", isDaemon = true) {
                        try {
                            socket.use { connection ->
                                val input = connection.getInputStream().bufferedReader(Charsets.US_ASCII)
                                while (true) {
                                    val line = input.readLine() ?: throw IOException("请求头尚未完成")
                                    if (line.isEmpty()) break
                                }
                                connection.getOutputStream().apply {
                                    write(("HTTP/1.1 200 OK\r\nContent-Type: text/event-stream\r\n" +
                                        "Transfer-Encoding: chunked\r\n\r\n").toByteArray(Charsets.US_ASCII))
                                    flush()
                                }
                                sendChunk(connection, ": heartbeat\n\n")
                                requests.incrementAndGet()
                                val disconnected = try {
                                    input.read() == -1
                                } catch (failure: SocketException) {
                                    // 主动取消可能以连接复位到达，和读取结束同样证明对端已关闭。
                                    if (stopping.get()) throw failure
                                    true
                                }
                                if (!disconnected) throw IOException("连接上出现额外请求数据")
                                peerClosed[index].countDown()
                            }
                        } catch (failure: Throwable) {
                            if (!stopping.get()) failures.add(failure)
                        }
                    })
                }
            } catch (failure: Throwable) {
                if (!stopping.get()) failures.add(failure)
            }
        }

        fun sendEvent(connection: Int, data: String) {
            sendChunk(sockets[connection], "data: $data\n\n")
        }

        fun awaitPeerClosed(connection: Int) {
            assertTrue("服务端未在限定时间内观察到连接关闭：$failures", peerClosed[connection].await(5, TimeUnit.SECONDS))
        }

        private fun sendChunk(socket: Socket, content: String) {
            val bytes = content.toByteArray(Charsets.UTF_8)
            socket.getOutputStream().apply {
                write("${bytes.size.toString(16)}\r\n".toByteArray(Charsets.US_ASCII))
                write(bytes)
                write("\r\n".toByteArray(Charsets.US_ASCII))
                flush()
            }
        }

        override fun close() {
            stopping.set(true)
            listener.close()
            accepting.join(1000)
            sockets.forEach { it.close() }
            workers.forEach { it.join(1000) }
            assertFalse("监听线程未释放", accepting.isAlive)
            assertFalse("连接线程未释放", workers.any { it.isAlive })
            failures.firstOrNull()?.let { throw AssertionError("事件流服务端异常", it) }
        }
    }

    private fun parse(text: String): List<SseEvent> {
        val events = mutableListOf<SseEvent>()
        SseEventReader.read(ByteArrayInputStream(text.toByteArray()), {}, events::add)
        return events
    }

    private fun eventResponse(body: String) = MockResponse().setHeader("Content-Type", "text/event-stream; charset=utf-8").setBody(body)

    private fun client(server: MockWebServer, states: MutableList<SseState>, maxRetry: Int = 3) = SseClient(
        SseConfig(server.url("/").toString(), maxRetry = maxRetry, retryDelayMillis = 0),
        object : SseCallback { override fun onStateChanged(state: SseState) { states.add(state) } }
    )
}

