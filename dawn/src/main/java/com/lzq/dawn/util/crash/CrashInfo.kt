package com.lzq.dawn.util.crash

import com.lzq.dawn.DawnBridge

/**
 * @Name :CrashInfo
 * @Time :2022/7/20 14:57
 * @Author :  Lzq
 * @Desc :
 */
class CrashInfo internal constructor(time: String, val throwable: Throwable?) {
    private val mFileHeadProvider: DawnBridge.FileHead = DawnBridge.FileHead("Crash")

    init {
        mFileHeadProvider.addFirst("Time Of Crash", time)
    }

    fun addExtraHead(extraHead: Map<String, String>?) {
        mFileHeadProvider.append(extraHead)
    }

    fun addExtraHead(key: String, value: String) {
        mFileHeadProvider.append(key, value)
    }

    override fun toString(): String {
        return mFileHeadProvider.toString() + DawnBridge.getFullStackTrace(throwable)
    }
}