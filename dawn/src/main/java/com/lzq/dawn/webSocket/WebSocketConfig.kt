package com.lzq.dawn.webSocket

/**
 * @projectName com.lzq.dawn.webSocket
 * @author Lzq
 * @date : Created by Lzq on 2024/6/21 14:34
 * @version 0.0.25
 * @description: websocket 配置
 */
data class WebSocketConfig(
    val connectionConfig: ConnectionConfig = ConnectionConfig(),
    val retryConfig: RetryConfig = RetryConfig(),
    val messageQueueCapacity: Int = Int.MAX_VALUE
) {
    data class ConnectionConfig(
        val connectTimeout: Long = 10L, // 连接超时时间（秒）
        val readTimeout: Long = 3L, // 读取超时时间（秒）
        val writeTimeout: Long = 3L // 写入超时时间（秒）
    )

    data class RetryConfig(
        val retryInterval: Long = 5000L, // 重连间隔时间（毫秒）
        val maxRetries: Int = 3, // 最大重试次数
        val backoffMultiplier: Double = 2.0 // 重连延迟的倍数
    )
}

