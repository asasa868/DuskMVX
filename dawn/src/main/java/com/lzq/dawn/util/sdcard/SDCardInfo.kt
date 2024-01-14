package com.lzq.dawn.util.sdcard

import android.text.format.Formatter
import com.lzq.dawn.DawnBridge

/**
 * @Name :SDCardInfo
 * @Time :2022/8/29 15:51
 * @Author :  Lzq
 * @Desc :
 */
class SDCardInfo internal constructor(
    @JvmField var path: String,
    @JvmField var state: String,
    private var isRemovable: Boolean
) {
    private var totalSize: Long = DawnBridge.getFsTotalSize(path)
    private var availableSize: Long = DawnBridge.getFsAvailableSize(path)

    override fun toString(): String {
        return "SDCardInfo {path = $path, state = $state, isRemovable = $isRemovable, totalSize = " + Formatter.formatFileSize(
            DawnBridge.getApp(), totalSize
        ) + ", availableSize = " + Formatter.formatFileSize(DawnBridge.getApp(), availableSize) + '}'
    }
}