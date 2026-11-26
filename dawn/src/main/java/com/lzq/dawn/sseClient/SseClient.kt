package com.lzq.dawn.sseClient

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Headers.Companion.toHeaders
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.atomic.AtomicReference

/**
 * @projectName com.lzq.dawn.sseClient
 * @author Lzq
 * @date : Created by Lzq on 2025/04/16 09:31
 * @version 0.0.31
 * @description: SSE 客户端类
 *
 * 在现有网络客户端上读取事件流，每次收集独立拥有连接和重试计数。
 * 正常流结束不重连；仅传输失败按配置重试，取消立即释放连接。
 */
class SseClient(
    private val config: SseConfig,
    private val callback: SseCallback? = null
) {
    private val client = OkHttpClient()

    /**
     * 返回冷事件流；仅接受成功的标准事件流响应。
     * 非成功状态、错误媒体类型以及重试耗尽通过流异常通知，不作为普通事件派发。
     */
    fun connect(): Flow<SseEvent> = callbackFlow {
        val activeCall = AtomicReference<Call?>()
        var retryCount = 0
        var lastEventId: String? = null

        fun emitState(state: SseState): Boolean {
            if (!isActive) return false
            return try {
                callback?.onStateChanged(state)
                true
            } catch (failure: Throwable) {
                close(failure)
                activeCall.getAndSet(null)?.cancel()
                false
            }
        }

        fun connectAttempt() {
            if (!isActive) return
            val headers = config.headers.toHeaders().newBuilder().apply {
                val id = lastEventId
                if (id != null) {
                    removeAll("Last-Event-ID")
                    if (id.isNotEmpty()) addUnsafeNonAscii("Last-Event-ID", id)
                }
            }.build()
            val request = Request.Builder()
                .url(config.url)
                .headers(headers)
                .build()
            val nextCall = client.newCall(request)
            activeCall.set(nextCall)
            if (!isActive) {
                nextCall.cancel()
                return
            }
            nextCall.enqueue(object : Callback {
                private fun failTransport(failure: IOException) {
                    if (!isActive || nextCall.isCanceled()) return
                    if (!emitState(SseState.Disconnected)) return
                    if (retryCount < config.maxRetry) {
                        retryCount++
                        if (!emitState(SseState.Reconnecting(retryCount))) return
                        launch {
                            delay(config.retryDelayMillis)
                            connectAttempt()
                        }
                    } else {
                        close(failure)
                    }
                }

                override fun onFailure(call: Call, failure: IOException) {
                    failTransport(failure)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!isActive) return
                        val mediaType = response.body.contentType()
                        if (response.code != 200 || mediaType?.type != "text" || mediaType.subtype != "event-stream") {
                            if (emitState(SseState.Disconnected)) {
                                close(IOException("Invalid SSE response: HTTP " + response.code + ", Content-Type " + mediaType))
                            }
                            return
                        }
                        if (!emitState(SseState.Connected)) return
                        try {
                            SseEventReader.read(response.body.byteStream(), { lastEventId = it }) { event ->
                                if (!trySendBlocking(event).isSuccess) throw CollectionClosed()
                            }
                        } catch (_: CollectionClosed) {
                            // 消费者结束不属于传输失败，不发起重连或额外状态回调。
                            close()
                            return
                        } catch (failure: IOException) {
                            failTransport(failure)
                            return
                        } catch (failure: Throwable) {
                            if (isActive) {
                                if (emitState(SseState.Disconnected)) close(failure)
                            }
                            return
                        }
                        // EOF 不补派发未闭合事件，也不增加新的自动重连行为。
                        if (emitState(SseState.Disconnected)) close()
                    }
                }
            })
        }

        if (emitState(SseState.Connecting)) connectAttempt()
        awaitClose { activeCall.getAndSet(null)?.cancel() }
    }

    /** 标记事件收集通道关闭以终止当前读取过程。 */
    private class CollectionClosed : RuntimeException()
}
