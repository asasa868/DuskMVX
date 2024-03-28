package com.lzq.dawn.util.resource

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.core.content.ContextCompat
import com.lzq.dawn.DawnBridge
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

/**
 * @Name :ResourceUtils
 * @Time :2022/8/29 14:59
 * @Author :  Lzq
 * @Desc : 资源
 */
object ResourceUtils {
    private const val BUFFER_SIZE = 8192

    /**
     * 按标识符返回可绘制对象。
     *
     * @param id 标识符
     * @return drawable
     */
    fun getDrawable(
        @DrawableRes id: Int,
    ): Drawable? {
        return ContextCompat.getDrawable(DawnBridge.app, id)
    }

    /**
     * 按名称的 id 标识符
     *
     * @param name id.
     * @return 按名称的 id 标识符
     */
    fun getIdByName(name: String?): Int {
        return DawnBridge.app.resources.getIdentifier(name, "id", DawnBridge.app.packageName)
    }

    /**
     * 按名称返回字符串标识符。
     *
     * @param name 字符串的名称。
     * @return 按名称的字符串标识符
     */
    fun getStringIdByName(name: String?): Int {
        return DawnBridge.app.resources.getIdentifier(name, "string", DawnBridge.app.packageName)
    }

    /**
     * 按名称返回颜色标识符。
     *
     * @param name 颜色的名称。
     * @return 名称的颜色标识符
     */
    fun getColorIdByName(name: String?): Int {
        return DawnBridge.app.resources.getIdentifier(name, "color", DawnBridge.app.packageName)
    }

    /**
     * 按名称返回dimen标识符。
     *
     * @param name dimen的名字
     * @return 名称的dimen标识符
     */
    fun getDimenIdByName(name: String?): Int {
        return DawnBridge.app.resources.getIdentifier(name, "dimen", DawnBridge.app.packageName)
    }

    /**
     * 按名称返回Drawable标识符。
     *
     * @param name Drawable的名称。
     * @return 按名称绘制的Drawable标识符
     */
    fun getDrawableIdByName(name: String?): Int {
        return DawnBridge.app.resources.getIdentifier(name, "drawable", DawnBridge.app.packageName)
    }

    /**
     * 按名称返回 mipmap 标识符。
     *
     * @param name mipmap 的名称。
     * @return 名称的 mipmap 标识符
     */
    fun getMipmapIdByName(name: String?): Int {
        return DawnBridge.app.resources.getIdentifier(name, "mipmap", DawnBridge.app.packageName)
    }

    /**
     * 按名称返回布局标识符。
     *
     * @param name 布局的名称
     * @return 按名称排列的布局标识符
     */
    fun getLayoutIdByName(name: String?): Int {
        return DawnBridge.app.resources.getIdentifier(name, "layout", DawnBridge.app.packageName)
    }

    /**
     * 按名称返回样式标识符。
     *
     * @param name 样式的名称。
     * @return 按名称的样式标识符
     */
    fun getStyleIdByName(name: String?): Int {
        return DawnBridge.app.resources.getIdentifier(name, "style", DawnBridge.app.packageName)
    }

    /**
     * 按名称返回动画标识符。
     *
     * @param name 动画的名称。
     * @return 动画标识符的名称
     */
    fun getAnimIdByName(name: String?): Int {
        return DawnBridge.app.resources.getIdentifier(name, "anim", DawnBridge.app.packageName)
    }

    /**
     * 按名称返回菜单标识符。
     *
     * @param name 菜单的名称
     * @return 按名称的菜单标识符
     */
    fun getMenuIdByName(name: String?): Int {
        return DawnBridge.app.resources.getIdentifier(name, "menu", DawnBridge.app.packageName)
    }

    /**
     * 从assets复制文件.
     *
     * @param assetsFilePath assets中文件的路径。
     * @param destFilePath   目标文件的路径。
     * @return `true`: success<br></br>`false`: fail
     */
    fun copyFileFromAssets(
        assetsFilePath: String,
        destFilePath: String,
    ): Boolean {
        var res = true
        try {
            val assets = DawnBridge.app.assets.list(assetsFilePath)
            if (!assets.isNullOrEmpty()) {
                for (asset in assets) {
                    res = res and copyFileFromAssets("$assetsFilePath/$asset", "$destFilePath/$asset")
                }
            } else {
                res =
                    DawnBridge.writeFileFromIS(
                        destFilePath,
                        DawnBridge.app.assets.open(assetsFilePath),
                    )
            }
        } catch (e: IOException) {
            e.printStackTrace()
            res = false
        }
        return res
    }
    /**
     * 返回assets的内容。
     *
     * @param assetsFilePath assets中文件的路径
     * @param charsetName    字符集的名称。
     * @return assets的内容。
     */

    /**
     * 返回assets的内容。
     *
     * @param assetsFilePath assets中文件的路径。
     * @return assets的内容。
     */
    @JvmOverloads
    fun readAssets2String(
        assetsFilePath: String?,
        charsetName: String? = null,
    ): String {
        return try {
            val `is` = DawnBridge.app.assets.open(assetsFilePath!!)
            val bytes = DawnBridge.inputStream2Bytes(`is`) ?: return ""
            if (DawnBridge.isSpace(charsetName)) {
                String(bytes)
            } else {
                try {
                    String(bytes, Charset.forName(charsetName))
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                    ""
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }
    /**
     * 返回assets的内容
     *
     * @param assetsPath  assets中文件的路径。
     * @param charsetName 字符集的名称。
     * @return assets的内容
     */

    /**
     * 返回assets的内容
     *
     * @param assetsPath assets中文件的路径。
     * @return assets的内容
     */
    @JvmOverloads
    fun readAssets2List(
        assetsPath: String?,
        charsetName: String? = "",
    ): List<String> {
        return try {
            DawnBridge.inputStream2Lines(DawnBridge.app.resources.assets.open(assetsPath!!), charsetName)
                ?: List(1) { "" }
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 从raw复制文件。
     *
     * @param resId        资源id。
     * @param destFilePath 目标文件的路径。
     * @return `true`: success<br></br>`false`: fail
     */
    fun copyFileFromRaw(
        @RawRes resId: Int,
        destFilePath: String?,
    ): Boolean {
        return DawnBridge.writeFileFromIS(
            destFilePath,
            DawnBridge.app.resources.openRawResource(resId),
        )
    }

    /**
     * 返回原始资源的内容
     *
     * @param resId       资源id。
     * @param charsetName 字符集的名称。
     * @return 原始资源的内容
     */
    @JvmOverloads
    fun readRaw2String(
        @RawRes resId: Int,
        charsetName: String? = null,
    ): String? {
        val `is` = DawnBridge.app.resources.openRawResource(resId)
        val bytes = DawnBridge.inputStream2Bytes(`is`) ?: return null
        return if (DawnBridge.isSpace(charsetName)) {
            String(bytes)
        } else {
            try {
                String(bytes, Charset.forName(charsetName))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                ""
            }
        }
    }

    /**
     * 返回原始资源的内容。
     *
     * @param resId       资源id
     * @param charsetName 字符集的名称。
     * @return 中文件的内容
     */
    @JvmOverloads
    fun readRaw2List(
        @RawRes resId: Int,
        charsetName: String? = "",
    ): List<String> {
        return DawnBridge.inputStream2Lines(DawnBridge.app.resources.openRawResource(resId), charsetName)
            ?: List(0) { "" }
    }
}
