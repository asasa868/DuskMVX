package com.lzq.dawn.webSocket

import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString

/**
 * @projectName com.lzq.dawn.webSocket
 * @author Lzq
 * @date : Created by Lzq on 2024/6/21 15:06
 * @version 0.0.25
 * @description: 默认的 WebSocket 事件处理策略类
 */
class DefaultWebSocketStrategy : WebSocketStrategy {
    private lateinit var webSocketClient: WebSocketClient

    override fun setWebSocketClient(webSocketClient: WebSocketClient) {
        this.webSocketClient = webSocketClient
    }

    override suspend fun handleOpen(webSocket: WebSocket, response: Response) {
        println("WebSocket opened: $response")
    }

    override suspend fun handleMessage(webSocket: WebSocket, text: String) {
        println("Received message: $text")
    }

    override suspend fun handleMessageBytes(webSocket: WebSocket, bytes: ByteString) {
        println("WebSocket handleMessageBytes: $bytes")
    }

    override suspend fun handleClosing(webSocket: WebSocket, code: Int, reason: String) {
        println("WebSocket handleClosing: code：$code, reason：$reason")
    }

    override suspend fun handleFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        println("WebSocket failure: $t")
    }
}