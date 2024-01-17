package com.lzq.dawn.util.network

import android.net.wifi.ScanResult
import android.text.TextUtils

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
    var filterResults: List<ScanResult> = ArrayList()
        private set

    companion object {
        private fun filterScanResult(results: List<ScanResult>?): List<ScanResult> {
            if (results == null || results.isEmpty()) {
                return ArrayList()
            }
            val map = LinkedHashMap<String, ScanResult>(results.size)
            for (result in results) {
                if (TextUtils.isEmpty(result.SSID)) {
                    continue
                }
                val resultInMap = map[result.SSID]
                if (resultInMap != null && resultInMap.level >= result.level) {
                    continue
                }
                map[result.SSID] = result
            }
            return ArrayList(map.values)
        }
    }
}