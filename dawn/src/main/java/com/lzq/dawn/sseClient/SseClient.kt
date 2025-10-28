package com.lzq.dawn.sseClient

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Headers.Companion.toHeaders
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException

/**
 * @projectName com.lzq.dawn.sseClient
 * @author Lzq
 * @date : Created by Lzq on 2025/04/16 09:31
 * @version 0.0.31
 * @description: SSE 客户端类
 */
class SseClient(
    private val config: SseConfig,
    private val callback: SseCallback? = null
) {

    private val client = OkHttpClient()
    private var call: Call? = null
    private var retryCount = 0
    private var isConnected = false

    fun connect(): Flow<SseEvent> = callbackFlow {
        emitState(SseState.Connecting)
        val request = Request.Builder()
            .url(config.url)
            .headers(config.headers.toHeaders())
            .build()

        call = client.newCall(request)
        call?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                emitState(SseState.Disconnected)
                if (retryCount < config.maxRetry) {
                    retryCount++
                    emitState(SseState.Reconnecting(retryCount))
                    launch {
                        delay(config.retryDelayMillis)
                        connect().collect { trySend(it) }
                    }
                } else {
                    close(e)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                isConnected = true
                emitState(SseState.Connected)
                val source = response.body.source()
                while (!source.exhausted()) {
                    val line = source.readUtf8Line() ?: continue
                    val event = parseEvent(line)
                    trySend(event)
                }
                emitState(SseState.Disconnected)
                close()
            }
        })

        awaitClose {
            call?.cancel()
        }
    }

    private fun emitState(state: SseState) {
        callback?.onStateChanged(state)
    }

    private fun parseEvent(line: String): SseEvent {
        return SseEvent(event = "message", data = line)
    }
}
