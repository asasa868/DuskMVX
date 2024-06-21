package com.lzq.dawn.webSocket

import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString

/**
 * @projectName com.lzq.dawn.webSocket
 * @author Lzq
 * @date : Created by Lzq on 2024/6/21 14:33
 * @version 0.0.25
 * @description: websocket策略类
 */
interface WebSocketStrategy {

    /**
     * 设置 WebSocketClient 实例
     * @param webSocketClient @link[WebSocketClient]
     */
    fun setWebSocketClient(webSocketClient: WebSocketClient)

    /**
     *处理 WebSocket 打开事件
     * @param webSocket @link[WebSocket]
     * @param response @link[Response]
     */
    suspend fun handleOpen(webSocket: WebSocket, response: Response)

    /**
     * 处理 WebSocket 消息事件
     * @param webSocket @link[WebSocket]
     * @param text @link[String] 接收到的文本消息
     */
    suspend fun handleMessage(webSocket: WebSocket, text: String)

    /**
     * 处理 WebSocket 消息事件
     * @param webSocket @link[WebSocket]
     * @param bytes @link[ByteString] 接收到的二进制消息
     */
    suspend fun handleMessageBytes(webSocket: WebSocket, bytes: ByteString)

    /**
     * 处理 WebSocket 关闭事件
     * @param webSocket @link[WebSocket]
     * @param code @link[Int] 关闭码
     * @param reason @link[String] 关闭原因
     */
    suspend fun handleClosing(webSocket: WebSocket, code: Int, reason: String)

    /**
     * 处理 WebSocket 关闭事件
     * @param webSocket @link[WebSocket]
     * @param t @link[Throwable] 异常
     * @param response @link[Response] 响应
     */
    suspend fun handleFailure(webSocket: WebSocket, t: Throwable, response: Response?)
}