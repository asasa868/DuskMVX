package com.lzq.dawn.sseClient

/**
 * @param url SSE 服务器地址
 * @param headers 请求头
 * @param maxRetry 最大重试次数
 * @param retryDelayMillis 重试延迟时间
 */
data class SseConfig(
    val url: String,
    val headers: Map<String, String> = emptyMap(),
    val maxRetry: Int = 3,
    val retryDelayMillis: Long = 3000,
)

/**
 * @param event 事件名称
 * @param data 事件数据
 * @param id 事件ID
 */
data class SseEvent(
    val event: String,
    val data: String,
    val id: String? = null,
)

/**
 * SSE 状态
 */
sealed class SseState {
    /**
     * 连接中
     */
    data object Connecting : SseState()

    /**
     * 已连接
     */
    data object Connected : SseState()

    /**
     * 正在重连
     */
    data class Reconnecting(
        val count: Int,
    ) : SseState()

    /**
     * 已断开
     */
    data object Disconnected : SseState()
}

/**
 * SSE 回调接口
 */
interface SseCallback {
    /**
     * 当 SSE 状态发生变化时调用
     * @param state SSE 状态
     * @link [SseState]
     */
    fun onStateChanged(state: SseState)
}
