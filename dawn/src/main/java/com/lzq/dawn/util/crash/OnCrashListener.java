package com.lzq.dawn.util.crash;

/**
 * @Name :OnCrashListener
 * @Time :2022/7/20 14:57
 * @Author :  Lzq
 * @Desc :
 */
public interface OnCrashListener {
    /**
     * Crash
     * @param crashInfo  crashInfo
     */
    void onCrash(CrashInfo crashInfo);
}
