package com.lzq.dawn.util.crash

/**
 * @Name :OnCrashListener
 * @Time :2022/7/20 14:57
 * @Author :  Lzq
 * @Desc :
 */
interface OnCrashListener {
    /**
     * Crash
     * @param crashInfo  crashInfo
     */
    fun onCrash(crashInfo: CrashInfo?)
}