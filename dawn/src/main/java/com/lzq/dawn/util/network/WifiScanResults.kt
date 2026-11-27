package com.lzq.dawn.util.network

import android.net.wifi.ScanResult
import android.os.Build
import android.net.wifi.WifiManager
import java.nio.ByteBuffer
import java.nio.charset.CharacterCodingException
import java.nio.charset.CodingErrorAction

/**
 * @Name :WifiScanResults
 * @Time :2022/8/15 17:26
 * @Author :  Lzq
 * @Desc : wifi 扫描
 */
class WifiScanResults {
    var allResults: List<ScanResult> = ArrayList()
        set(allResults) {
            field = allResults
            filterResults = filterScanResult(allResults)
        }
    private var filterResults: List<ScanResult> = ArrayList()

    companion object {
        private fun filterScanResult(results: List<ScanResult>?): List<ScanResult> {
            if (results.isNullOrEmpty()) {
                return ArrayList()
            }
            val map = LinkedHashMap<String, ScanResult>(results.size)
            for (result in results) {
                val networkName = result.networkName
                if (networkName.isNullOrEmpty()) {
                    continue
                }
                val resultInMap = map[networkName]
                if (resultInMap != null && resultInMap.level >= result.level) {
                    continue
                }
                map[networkName] = result
            }
            return ArrayList(map.values)
        }
    }
}

internal val ScanResult.networkName: String?
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        wifiSsid?.bytes?.let(::decodeNetworkName) ?: SSID
    } else {
        SSID
    }

/** 保持扫描名称的旧约定，隐藏名称为空，非法编码使用系统占位名称。 */
internal fun decodeNetworkName(bytes: ByteArray): String {
    if (bytes.all { it == 0.toByte() }) return ""
    return try {
        Charsets.UTF_8.newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT)
            .decode(ByteBuffer.wrap(bytes)).toString()
    } catch (_: CharacterCodingException) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) WifiManager.UNKNOWN_SSID else "<unknown ssid>"
    }
}
