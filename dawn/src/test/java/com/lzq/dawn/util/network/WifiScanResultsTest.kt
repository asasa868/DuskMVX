package com.lzq.dawn.util.network

import org.junit.Assert.assertEquals
import org.junit.Test

/** 验证新无线名称读取保持旧明文、隐藏名称和非法编码约定。 */
class WifiScanResultsTest {
    @Test
    fun utf8NamesRemainUnquotedPlainText() {
        assertEquals("办公网络", decodeNetworkName("办公网络".toByteArray()))
        assertEquals("alpha", decodeNetworkName("alpha".toByteArray()))
    }

    @Test
    fun emptyAndHiddenNamesRemainEmpty() {
        assertEquals("", decodeNetworkName(byteArrayOf()))
        assertEquals("", decodeNetworkName(byteArrayOf(0, 0, 0)))
    }

    @Test
    fun invalidUtf8KeepsTheSystemUnknownName() {
        assertEquals("<unknown ssid>", decodeNetworkName(byteArrayOf(0xc3.toByte(), 0x28)))
    }
}
