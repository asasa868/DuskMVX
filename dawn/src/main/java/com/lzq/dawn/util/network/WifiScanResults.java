package com.lzq.dawn.util.network;

import android.net.wifi.ScanResult;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Name :WifiScanResults
 * @Time :2022/8/15 17:26
 * @Author :  Lzq
 * @Desc : wifi 扫描
 */
public class WifiScanResults {

    private List<ScanResult> allResults    = new ArrayList<>();
    private List<ScanResult> filterResults = new ArrayList<>();

    public WifiScanResults() {
    }

    public List<ScanResult> getAllResults() {
        return allResults;
    }

    public List<ScanResult> getFilterResults() {
        return filterResults;
    }

    public void setAllResults(List<ScanResult> allResults) {
        this.allResults = allResults;
        filterResults = filterScanResult(allResults);
    }

    private static List<ScanResult> filterScanResult(final List<ScanResult> results) {
        if (results == null || results.isEmpty()) {
            return new ArrayList<>();
        }
        LinkedHashMap<String, ScanResult> map = new LinkedHashMap<>(results.size());
        for (ScanResult result : results) {
            if (TextUtils.isEmpty(result.SSID)) {
                continue;
            }
            ScanResult resultInMap = map.get(result.SSID);
            if (resultInMap != null && resultInMap.level >= result.level) {
                continue;
            }
            map.put(result.SSID, result);
        }
        return new ArrayList<>(map.values());
    }
}
