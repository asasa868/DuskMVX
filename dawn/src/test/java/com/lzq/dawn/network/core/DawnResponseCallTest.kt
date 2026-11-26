package com.lzq.dawn.network.core

import com.google.gson.Gson
import com.lzq.dawn.DawnConstants.NetWorkConstants.RESPONSE_BODY_NULL_CODE
import com.lzq.dawn.DawnConstants.NetWorkConstants.RESPONSE_EXCEPTION_NULL_CODE
import com.lzq.dawn.network.bean.DawnHttpResult
import com.lzq.dawn.network.error.DawnException
import com.lzq.dawn.network.error.IErrorHandler
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Timeout
import okio.buffer
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.CancellationException
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

/**
 * @projectName com.lzq.dawn.network.core.DawnResponseCallTest
 * @author Lzq
 * @date : Created by Lzq on 2026/11/25 14:00:55
 * @version 0.0.30
 * @description: 验证响应包装在成功、空体、异常和取消时的回调约定
 */
class DawnResponseCallTest {
    @Test fun successAndUnenumeratedSuccessKeepOriginalHttpResponse() {
        val response = Response.success("payload", raw(299))
        val fake = FakeCall(response)
        val handler = RecordingHandler()
        val result = record(DawnResponseCall(fake, handler))

        assertEquals(1, result.terminalCount)
        assertEquals("payload", (result.response!!.body() as DawnHttpResult.Success).data)
        assertEquals(299, result.response!!.code())
        assertEquals("header", result.response!!.headers()["X-Test"])
        assertSame(response, result.response!!.body()!!.httpResponse)
        assertTrue(handler.errors.isEmpty())
    }

    @Test fun emptySuccessAndNoContentUseExistingEmptyBodyCode() {
        for (code in listOf(200, 204, 205)) {
            val fake = FakeCall(Response.success<String>(null, raw(code)))
            val handler = RecordingHandler()
            val result = record(DawnResponseCall(fake, handler))

            assertEquals(RESPONSE_BODY_NULL_CODE, (result.response!!.body() as DawnHttpResult.Failure).code)
            assertEquals(1, result.terminalCount)
            assertTrue(handler.errors.isEmpty())
        }
    }

    @Test fun unknownHttpFailureTerminatesAndPreservesHeadersAndErrorBody() {
        val response = Response.error<String>("original error".toResponseBody(), raw(599))
        val handler = RecordingHandler()
        val result = record(DawnResponseCall(FakeCall(response), handler))
        val failure = result.response!!.body() as DawnHttpResult.Failure

        assertEquals(599, failure.code)
        assertEquals(599, failure.httpResponse!!.code())
        assertEquals("header", failure.httpResponse!!.headers()["X-Test"])
        assertEquals("original error", failure.httpResponse!!.errorBody()!!.string())
        assertEquals(listOf(599), handler.errors)
        assertEquals(1, result.terminalCount)
    }

    @Test fun redirectsKeepExistingAsyncEmptyBodyContract() {
        val result = record(DawnResponseCall(FakeCall(Response.error<String>("redirect".toResponseBody(), raw(301))), null))

        val failure = result.response!!.body() as DawnHttpResult.Failure
        assertEquals(RESPONSE_BODY_NULL_CODE, failure.code)
        assertEquals(301, failure.httpResponse!!.code())
        assertEquals("redirect", failure.httpResponse!!.errorBody()!!.string())
    }

    @Test fun transportAndParsingFailuresReturnOneExistingFailureCode() {
        for (failure in listOf(IOException("offline"), IllegalStateException("parse"))) {
            val handler = RecordingHandler()
            val result = record(DawnResponseCall(FakeCall<String>(failure = failure), handler))

            assertEquals(RESPONSE_EXCEPTION_NULL_CODE, (result.response!!.body() as DawnHttpResult.Failure).code)
            assertEquals(failure.message, (result.response!!.body() as DawnHttpResult.Failure).message)
            assertEquals(1, result.terminalCount)
            assertTrue(handler.errors.isEmpty())
        }
    }

    @Test fun businessFailureHandledByConverterIsNotHandledOrDeliveredTwice() {
        val handler = RecordingHandler()
        val failure = try {
            converter(handler).convert("{\"code\":7,\"message\":\"denied\",\"data\":null}".toResponseBody("application/json".toMediaType()))
            error("expected business failure")
        } catch (failure: DawnException) {
            failure
        }
        val result = record(DawnResponseCall(FakeCall<String>(failure = failure, repeat = true), handler))

        assertEquals(listOf(7), handler.errors)
        assertEquals(1, result.terminalCount)
        assertEquals(RESPONSE_EXCEPTION_NULL_CODE, (result.response!!.body() as DawnHttpResult.Failure).code)
    }

    @Test fun rawBusinessFailureNotYetHandledIsHandledOnce() {
        val handler = RecordingHandler()
        val result = record(DawnResponseCall(FakeCall<String>(failure = DawnException(9, "denied"), repeat = true), handler))

        assertEquals(listOf(9), handler.errors)
        assertEquals(1, result.terminalCount)
    }

    @Test fun converterClosesResponseBodyWhenJsonIsMalformed() {
        var closed = false
        val source = object : ForwardingSource(Buffer().writeUtf8("{")) {
            override fun close() { closed = true; super.close() }
        }.buffer()
        val body = object : ResponseBody() {
            override fun contentType() = "application/json".toMediaType()
            override fun contentLength() = -1L
            override fun source(): BufferedSource = source
        }

        try {
            converter(RecordingHandler()).convert(body)
            fail("expected malformed JSON failure")
        } catch (expected: com.google.gson.JsonParseException) {
            assertTrue(closed)
        }
    }

    @Test fun handlerFailureTerminatesThroughOnFailureWithoutSecondCallback() {
        val handler = object : IErrorHandler {
            override fun handleError(dawnException: DawnException) { throw IllegalStateException("handler failed") }
        }
        val result = record(DawnResponseCall(FakeCall(Response.error<String>("error".toResponseBody(), raw(500))), handler))

        assertEquals(1, result.terminalCount)
        assertEquals("handler failed", result.failure!!.message)
    }

    @Test fun explicitCancellationReachesUnderlyingCallAndNeverBusinessHandler() {
        val fake = FakeCall<String>(failure = IOException("Canceled"))
        val handler = RecordingHandler()
        val call = DawnResponseCall(fake, handler)
        call.cancel()
        val result = record(call)

        assertTrue(fake.isCanceled)
        assertEquals(1, result.terminalCount)
        assertTrue(result.failure is IOException)
        assertTrue(handler.errors.isEmpty())
    }

    @Test fun coroutineCancellationIsNotMappedToFailureValue() {
        val cancelled = CancellationException("cancelled")
        val result = record(DawnResponseCall(FakeCall<String>(failure = cancelled), null))

        assertSame(cancelled, result.failure)
        assertNull(result.response)
        assertEquals(1, result.terminalCount)
    }

    @Test fun duplicateResponsesDoNotDeliverOrConsumeOriginalErrorBodyTwice() {
        val response = Response.error<String>("error".toResponseBody(), raw(500))
        val handler = RecordingHandler()
        val result = record(DawnResponseCall(FakeCall(response, repeat = true), handler))

        assertEquals(1, result.terminalCount)
        assertEquals(listOf(500), handler.errors)
        assertEquals("error", result.response!!.body()!!.httpResponse!!.errorBody()!!.string())
    }

    @Test fun responseAndCancellationRaceHasOneTerminalResult() {
        repeat(100) {
            val fake = ManualCall<String>()
            val call = DawnResponseCall(fake, null)
            val record = record(call)
            val start = CountDownLatch(1)
            val responseThread = thread { start.await(); fake.callback!!.onResponse(fake, Response.success("value")) }
            val cancelThread = thread { start.await(); call.cancel(); fake.callback!!.onFailure(fake, IOException("Canceled")) }
            start.countDown()
            responseThread.join()
            cancelThread.join()

            assertEquals(1, record.terminalCount)
        }
    }

    @Test fun synchronousHttpErrorRetainsStatusHeadersAndOriginalErrorBody() {
        val handler = RecordingHandler()
        val response = DawnResponseCall(FakeCall(Response.error<String>("sync error".toResponseBody(), raw(599))), handler).execute()

        assertEquals(599, response.code())
        assertEquals("header", response.headers()["X-Test"])
        assertEquals("sync error", response.errorBody()!!.string())
        assertEquals(listOf(599), handler.errors)
    }

    @Test fun synchronousFailuresPropagateInsteadOfReturningSyntheticSuccess() {
        for (failure in listOf(IOException("offline"), CancellationException("cancelled"), DawnException(8, "business"))) {
            try {
                DawnResponseCall(FakeCall<String>(failure = failure), null).execute()
                fail("expected failure")
            } catch (actual: Throwable) {
                assertSame(failure, actual)
            }
        }
    }

    @Test fun synchronousEmptyBodyAndSuccessPreserveStatus() {
        val result = DawnResponseCall(FakeCall(Response.success<String>(null, raw(204))), null).execute()

        assertEquals(204, result.code())
        assertEquals(RESPONSE_BODY_NULL_CODE, (result.body() as DawnHttpResult.Failure).code)
    }

    @Test fun cancellationDiscardsAnAsyncStreamingSuccessBody() {
        val body = TrackedBody()
        val call = DawnResponseCall(FakeCall(Response.success<ResponseBody>(body)), null)
        call.cancel()
        val result = record(call)

        assertEquals(1, result.terminalCount)
        assertTrue(result.failure is IOException)
        assertTrue(body.closed)
    }

    @Test fun cancellationDiscardsASynchronousStreamingSuccessBody() {
        val body = TrackedBody()
        val call = DawnResponseCall(FakeCall(Response.success<ResponseBody>(body)), null)
        call.cancel()

        try {
            call.execute()
            fail("expected cancellation")
        } catch (expected: IOException) {
            assertTrue(body.closed)
        }
    }

    @Test fun lateResponseAfterFailureClosesItsUnownedBodyWithoutAnotherCallback() {
        val fake = ManualCall<ResponseBody>()
        val result = record(DawnResponseCall(fake, null))
        val body = TrackedBody()
        fake.callback!!.onFailure(fake, IOException("offline"))
        fake.callback!!.onResponse(fake, Response.success(body))

        assertEquals(1, result.terminalCount)
        assertTrue(body.closed)
    }

    @Test fun duplicateDistinctResponseClosesOnlyDiscardedBody() {
        val fake = ManualCall<ResponseBody>()
        val result = record(DawnResponseCall(fake, null))
        val retained = TrackedBody()
        val discarded = TrackedBody()
        fake.callback!!.onResponse(fake, Response.success(retained))
        fake.callback!!.onResponse(fake, Response.success(discarded))

        assertEquals(1, result.terminalCount)
        assertFalse(retained.closed)
        assertTrue(discarded.closed)
        retained.close()
    }

    @Test fun synchronousHandlerFailureClosesUnreturnedErrorBody() {
        val body = TrackedBody()
        val call = DawnResponseCall(FakeCall(Response.error<String>(body, raw(500))), object : IErrorHandler {
            override fun handleError(dawnException: DawnException) { throw IllegalStateException("handler failed") }
        })

        try {
            call.execute()
            fail("expected handler failure")
        } catch (expected: IllegalStateException) {
            assertEquals("handler failed", expected.message)
            assertTrue(body.closed)
        }
    }

    private fun converter(handler: IErrorHandler) = DawnResponseBodyConverter(
        Gson(), Gson().getAdapter(String::class.java), object : IResponseCode {
            override fun getCodeName() = "code"
            override fun getMessageName() = "message"
            override fun getDataName() = "data"
            override fun isSuccessCode() = 0
        }, handler
    )

    private fun raw(code: Int) = okhttp3.Response.Builder().code(code).message("status")
        .protocol(Protocol.HTTP_1_1).request(Request.Builder().url("https://example.test").build())
        .header("X-Test", "header").build()

    private fun <T> record(call: Call<DawnHttpResult<T>>): ResultRecord<T> {
        val record = ResultRecord<T>()
        call.enqueue(record)
        return record
    }

    /** 记录响应包装调用的终态次数、响应与异常。 */
    private class ResultRecord<T> : Callback<DawnHttpResult<T>> {
        var terminalCount = 0
        var response: Response<DawnHttpResult<T>>? = null
        var failure: Throwable? = null
        @Synchronized override fun onResponse(call: Call<DawnHttpResult<T>>, response: Response<DawnHttpResult<T>>) {
            terminalCount++; this.response = response
        }
        @Synchronized override fun onFailure(call: Call<DawnHttpResult<T>>, failure: Throwable) {
            terminalCount++; this.failure = failure
        }
    }

    /** 记录响应处理触发的业务错误码。 */
    private class RecordingHandler : IErrorHandler {
        val errors = mutableListOf<Int>()
        override fun handleError(dawnException: DawnException) { errors.add(dawnException.code) }
    }

    /** 跟踪测试响应体的读取与关闭行为。 */
    private class TrackedBody : ResponseBody() {
        var closed = false
        private val source = object : ForwardingSource(Buffer().writeUtf8("resource")) {
            override fun close() { closed = true; super.close() }
        }.buffer()
        override fun contentType() = null
        override fun contentLength() = -1L
        override fun source(): BufferedSource = source
    }

    /** 模拟可配置响应与异常的网络调用。 */
    private open class FakeCall<T>(
        private val response: Response<T>? = null,
        private val failure: Throwable? = null,
        private val repeat: Boolean = false
    ) : Call<T> {
        @Volatile private var cancelled = false
        override fun enqueue(callback: Callback<T>) {
            kotlin.repeat(if (repeat) 2 else 1) {
                if (failure != null) callback.onFailure(this, failure)
                else callback.onResponse(this, response!!)
            }
        }
        override fun execute(): Response<T> { failure?.let { throw it }; return response!! }
        override fun isExecuted() = false
        override fun clone(): Call<T> = FakeCall(response, failure, repeat)
        override fun isCanceled() = cancelled
        override fun cancel() { cancelled = true }
        override fun request() = Request.Builder().url("https://example.test").build()
        override fun timeout() = Timeout.NONE
    }

    /** 手动触发网络回调以验证取消和终态竞态。 */
    private class ManualCall<T> : FakeCall<T>() {
        var callback: Callback<T>? = null
        override fun enqueue(callback: Callback<T>) { this.callback = callback }
    }
}

