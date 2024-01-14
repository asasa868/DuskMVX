package com.lzq.dawn.util.file

import java.io.File

/**
 * @Name :OnReplaceListener
 * @Time :2022/7/22 14:18
 * @Author :  Lzq
 * @Desc :
 */
interface OnReplaceListener {
    /**
     * 替换
     * @param srcFile srcFile
     * @param destFile destFile
     * @return  Replace
     */
    fun onReplace(srcFile: File?, destFile: File?): Boolean
}