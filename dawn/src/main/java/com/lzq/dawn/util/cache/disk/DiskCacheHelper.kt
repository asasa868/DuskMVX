package com.lzq.dawn.util.cache.disk

import java.util.Locale

/**
 * @Name :DiskCacheHelper
 * @Time :2022/7/18 14:31
 * @Author :  Lzq
 * @Desc : 磁盘缓存帮助类
 */
object DiskCacheHelper {
    private const val TIME_INFO_LEN = 14

    @JvmStatic
    fun newByteArrayWithTime(second: Int, data: ByteArray?): ByteArray {
        val time = createDueTime(second).toByteArray()
        val content = ByteArray(time.size + (data?.size ?: 0))
        System.arraycopy(time, 0, content, 0, time.size)
        if (data != null) {
            System.arraycopy(data, 0, content, time.size, data.size)
        }
        return content
    }

    /**
     * 返回到期时间字符串。
     *
     * @param seconds 秒
     * @return 到期时间字符串。
     */
    private fun createDueTime(seconds: Int): String {
        return String.format(
            Locale.getDefault(), "_" + "$%010d$" + "_", System.currentTimeMillis() / 1000 + seconds
        )
    }

    @JvmStatic
    fun isDue(data: ByteArray?): Boolean {
        val millis = getDueTime(data)
        return millis != -1L && System.currentTimeMillis() > millis
    }

    private fun getDueTime(data: ByteArray?): Long {
        if (hasTimeInfo(data)) {
            val millis = String(copyOfRange(data, 2, 12))
            return try {
                millis.toLong() * 1000
            } catch (e: NumberFormatException) {
                -1
            }
        }
        return -1
    }

    @JvmStatic
    fun getDataWithoutDueTime(data: ByteArray?): ByteArray {
        return if (hasTimeInfo(data)) {
            copyOfRange(data, TIME_INFO_LEN, (data?.size ?: 0))
        } else data ?: ByteArray(0)
    }

    private fun copyOfRange(original: ByteArray?, from: Int, to: Int): ByteArray {
        val newLength = to - from
        require(newLength >= 0) { "$from > $to" }
        val copy = ByteArray(newLength)
        if (original != null) {
            System.arraycopy(original, from, copy, 0, (original.size - from).coerceAtMost(newLength))
        }
        return copy
    }

    private fun hasTimeInfo(data: ByteArray?): Boolean {
        return data != null && data.size >= TIME_INFO_LEN && data[0] == '_'.code.toByte() && data[1] == '$'.code.toByte() && data[12] == '$'.code.toByte() && data[13] == '_'.code.toByte()
    }
}