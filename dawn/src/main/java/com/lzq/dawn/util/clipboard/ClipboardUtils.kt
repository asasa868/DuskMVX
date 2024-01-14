package com.lzq.dawn.util.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ClipboardManager.OnPrimaryClipChangedListener
import android.content.Context
import com.lzq.dawn.DawnBridge

/**
 * @Name :ClipboardUtils
 * @Time :2022/7/19 16:20
 * @Author :  Lzq
 * @Desc : 粘贴板
 */
object ClipboardUtils {
    /**
     * 将文本复制到剪贴板
     *
     * label 等于 包名.
     *
     * @param text text.
     */
    fun copyText(text: CharSequence?) {
        copyText(DawnBridge.getApp().packageName, text)
    }

    /**
     * 将文本复制到剪贴板
     *
     * @param label label.
     * @param text  text.
     */
    fun copyText(label: CharSequence?, text: CharSequence?) {
        val cm = DawnBridge.getApp().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText(label, text))
    }

    /**
     * 清除剪贴板
     */
    fun clear() {
        copyText(null, "")
    }

    val label: CharSequence
        /**
         * 返回剪贴板的标签。
         *
         * @return 返回剪贴板的标签。
         */
        get() {
            val cm = DawnBridge.getApp().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val des = cm.primaryClipDescription ?: return ""
            return des.label ?: return ""
        }
    val text: CharSequence
        /**
         * 返回剪贴板的文本。
         *
         * @return 返回剪贴板的文本。
         */
        get() {
            val cm = DawnBridge.getApp().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = cm.primaryClip
            if (clip != null && clip.itemCount > 0) {
                val text = clip.getItemAt(0).coerceToText(DawnBridge.getApp())
                if (text != null) {
                    return text
                }
            }
            return ""
        }

    /**
     * 添加剪贴板更改的listener.
     */
    fun addChangedListener(listener: OnPrimaryClipChangedListener?) {
        val cm = DawnBridge.getApp().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.addPrimaryClipChangedListener(listener)
    }

    /**
     * 删除剪贴板更改的listener.
     */
    fun removeChangedListener(listener: OnPrimaryClipChangedListener?) {
        val cm = DawnBridge.getApp().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.removePrimaryClipChangedListener(listener)
    }
}