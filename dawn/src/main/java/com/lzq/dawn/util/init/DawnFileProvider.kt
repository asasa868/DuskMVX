package com.lzq.dawn.util.init

import android.app.Application
import androidx.core.content.FileProvider
import com.lzq.dawn.DawnBridge

/**
 * className :DawnFileProvider
 * createTime :2022/7/8 15:17
 *
 * @Author :  Lzq
 */
class DawnFileProvider : FileProvider() {
    override fun onCreate(): Boolean {
        DawnBridge.init(context!!.applicationContext as Application)
        return true
    }
}