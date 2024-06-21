package com.lzq.dawn.webSocket

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.TimeUnit
import kotlin.math.pow

/**
 * @projectName com.lzq.dawn.webSocket
 * @author Lzq
 * @date : Created by Lzq on 2024/6/21 14:32
 * @version 0.0.25
 * @description: websocket 客户端类
 *
 * @property url WebSocket 服务器 URL
 * @property config WebSocket 配置选项
 * @property strategy WebSocket 事件处理策略
 */
class WebSocketClient private constructor(
    private val url: String,
    private val config: WebSocketConfig = WebSocketConfig(),
    private val strategy: WebSocketStrategy = DefaultWebSocketStrategy(),
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val mutex = Mutex()

    private val messageQueue = Channel<String>(config.messageQueueCapacity)

    //OkhttpClient 实例
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder().connectTimeout(config.connectionConfig.connectTimeout, TimeUnit.SECONDS)
            .readTimeout(config.connectionConfig.readTimeout, TimeUnit.SECONDS)
            .writeTimeout(config.connectionConfig.writeTimeout, TimeUnit.SECONDS).build()
    }

    private var webSocket: WebSocket? = null
    private var isConnected = false
    private var retryCount = 0

    init {
        strategy.setWebSocketClient(this)
        connect()
        scope.launch { messageSender() }
    }

    private suspend fun messageSender() {
        for (message in messageQueue) {
            mutex.withLock {
                if (!isConnected) {
                    return@withLock
                }
                webSocket?.send(message)
            }
        }
    }

    private fun connect() {
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, ClientWebSocketListener())
    }

    fun sendMessage(message: String) {
        scope.launch {
            messageQueue.send(message)
        }
    }

    fun close() {
        scope.cancel()
        webSocket?.close(1000, "正常关闭。。。")
    }

    inner class ClientWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            scope.launch {
                mutex.withLock {
                    isConnected = true
                    retryCount = 0
                    strategy.handleOpen(webSocket, response)
                }
            }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            scope.launch {
                strategy.handleMessage(webSocket, text)
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            scope.launch {
                strategy.handleMessageBytes(webSocket, bytes)
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            scope.launch {
                mutex.withLock {
                    isConnected = false
                    strategy.handleClosing(webSocket, code, reason)
                }
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            scope.launch {
                mutex.withLock {
                    isConnected = false
                    strategy.handleFailure(webSocket, t, response)
                    attemptReconnect()
                }
            }
        }
    }

    // 尝试重连的协程函数
    private suspend fun attemptReconnect() {
        if (retryCount >= config.retryConfig.maxRetries) {
            return
        }

        val delayMillis = (config.retryConfig.retryInterval * config.retryConfig.backoffMultiplier.pow(
            retryCount.toDouble()
        )).toLong()
        retryCount++

        delay(delayMillis)
        connect()
    }

}
