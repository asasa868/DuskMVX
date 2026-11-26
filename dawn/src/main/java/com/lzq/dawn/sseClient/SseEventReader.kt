package com.lzq.dawn.sseClient

import java.io.InputStream

/**
 * @projectName com.lzq.dawn.sseClient.SseEventReader
 * @author Lzq
 * @date : Created by Lzq on 2026/11/25 13:58:35
 * @version 0.0.30
 * @description: 按事件流协议解析空行、换行与事件标识并回调完整事件
 *
 * 按标准空行边界解析事件，读取器同时接受三种换行并忽略开头的字节顺序标记。
 */
internal object SseEventReader {
    fun read(
        stream: InputStream,
        onEventId: (String?) -> Unit,
        onEvent: (SseEvent) -> Unit
    ) {
        val reader = stream.bufferedReader(Charsets.UTF_8)
        val data = StringBuilder()
        var eventType = ""
        // 每个新响应的编号缓冲从空值开始，重连请求携带的旧编号不继承到新帧。
        var eventId = ""
        var firstLine = true
        while (true) {
            var line = reader.readLine() ?: return
            if (firstLine) {
                line = line.removePrefix("\uFEFF")
                firstLine = false
            }
            if (line.isEmpty()) {
                onEventId(eventId)
                if (data.isNotEmpty()) {
                    onEvent(SseEvent(eventType.ifEmpty { "message" }, data.dropLast(1).toString(), eventId))
                }
                data.setLength(0)
                eventType = ""
                continue
            }
            if (line.startsWith(":")) continue
            val separator = line.indexOf(':')
            val field = if (separator < 0) line else line.substring(0, separator)
            val value = if (separator < 0) "" else line.substring(separator + 1).removePrefix(" ")
            when (field) {
                "data" -> data.append(value).append('\n')
                "event" -> eventType = value
                "id" -> if (!value.contains('\u0000')) {
                    eventId = value
                }
                // 保留调用方配置的等待时间，不借协议解析修复新增重连策略。
                "retry" -> Unit
            }
        }
    }
}

