package com.lzq.dawn.util.string

import android.content.res.Resources
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import com.lzq.dawn.DawnBridge
import java.util.IllegalFormatException

/**
 * @Name :StringUtils
 * @Time :2022/8/31 14:45
 * @Author :  Lzq
 * @Desc : string
 */
object StringUtils {
    /**
     * 返回字符串是null还是0长度。
     *
     * @param s string.
     * @return `true`: yes<br></br> `false`: no
     */
    fun isEmpty(s: CharSequence?): Boolean {
        return s.isNullOrEmpty()
    }

    /**
     * 返回字符串是空还是空白。
     *
     * @param s string.
     * @return `true`: yes<br></br> `false`: no
     */
    fun isTrimEmpty(s: String?): Boolean {
        return s == null || s.trim { it <= ' ' }.isEmpty()
    }

    /**
     * 返回字符串是空还是空白
     *
     * @param s string.
     * @return `true`: yes<br></br> `false`: no
     */
    @JvmStatic
    fun isSpace(s: String?): Boolean {
        if (s == null) {
            return true
        }
        var i = 0
        val len = s.length
        while (i < len) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
            ++i
        }
        return true
    }

    /**
     * 返回string1是否等于string2。
     *
     * @param s1 string.
     * @param s2 string.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun equals(s1: CharSequence?, s2: CharSequence?): Boolean {
        if (s1 === s2) {
            return true
        }
        var length = 0
        return if (s1 != null && s2 != null && s1.length.also { length = it } == s2.length) {
            if (s1 is String && s2 is String) {
                s1 == s2
            } else {
                for (i in 0 until length) {
                    if (s1[i] != s2[i]) {
                        return false
                    }
                }
                true
            }
        } else false
    }

    /**
     * 返回string1是否等于string2，忽略大小写考虑。。
     *
     * @param s1 string.
     * @param s2 string.
     * @return `true`: yes<br></br>`false`: no
     */
    fun equalsIgnoreCase(s1: String?, s2: String?): Boolean {
        return s1?.equals(s2, ignoreCase = true) ?: (s2 == null)
    }

    /**
     * Return `""` 如果字符串等于null
     *
     * @param s string.
     * @return `""` 如果字符串等于null
     */
    fun null2Length0(s: String?): String {
        return s ?: ""
    }

    /**
     * 返回字符串的长度。
     *
     * @param s  string.
     * @return 字符串的长度。
     */
    fun length(s: CharSequence?): Int {
        return s?.length ?: 0
    }

    /**
     * 将字符串的第一个字母设置为大写。
     *
     * @param s  string.
     * @return 第一个字母大写的字符串
     */
    fun upperFirstLetter(s: String?): String {
        if (s.isNullOrEmpty()) {
            return ""
        }
        return if (!Character.isLowerCase(s[0])) {
            s
        } else (s[0].code - 32).toChar().toString() + s.substring(1)
    }

    /**
     * 将字符串的第一个字母设置小写。
     *
     * @param s  string.
     * @return 第一个字母小写的字符串。
     */
    fun lowerFirstLetter(s: String?): String {
        if (s.isNullOrEmpty()) {
            return ""
        }
        return if (!Character.isUpperCase(s[0])) {
            s
        } else (s[0].code + 32).toChar().toString() + s.substring(
            1
        )
    }

    /**
     * 反转字符串
     *
     * @param s  string.
     * @return 反转字符串
     */
    fun reverse(s: String?): String {
        if (s == null) {
            return ""
        }
        val len = s.length
        if (len <= 1) {
            return s
        }
        val mid = len shr 1
        val chars = s.toCharArray()
        var c: Char
        for (i in 0 until mid) {
            c = chars[i]
            chars[i] = chars[len - i - 1]
            chars[len - i - 1] = c
        }
        return String(chars)
    }

    /**
     * 将字符串转换为DBC。
     *
     * @param s  string.
     * @return DBC字符串
     */
    fun toDBC(s: String?): String {
        if (s.isNullOrEmpty()) {
            return ""
        }
        val chars = s.toCharArray()
        var i = 0
        val len = chars.size
        while (i < len) {
            if (chars[i].code == 12288) {
                chars[i] = ' '
            } else if (chars[i].code in 65281..65374) {
                chars[i] = (chars[i].code - 65248).toChar()
            } else {
                chars[i] = chars[i]
            }
            i++
        }
        return String(chars)
    }

    /**
     * 将字符串转换为SBC。
     *
     * @param s  string.
     * @return SBC字符串
     */
    fun toSBC(s: String?): String {
        if (s.isNullOrEmpty()) {
            return ""
        }
        val chars = s.toCharArray()
        var i = 0
        val len = chars.size
        while (i < len) {
            if (chars[i] == ' ') {
                chars[i] = 12288.toChar()
            } else if (chars[i].code in 33..126) {
                chars[i] = (chars[i].code + 65248).toChar()
            } else {
                chars[i] = chars[i]
            }
            i++
        }
        return String(chars)
    }

    /**
     * 返回与特定资源ID关联的字符串值。
     *
     * @param id  resource id
     * @return 与特定资源ID关联的字符串值。
     */
    @JvmStatic
    fun getString(@StringRes id: Int): String? {
        return getString(id, *(null as Array<Any?>?)!!)
    }

    /**
     * 返回与特定资源ID关联的字符串值。
     *
     * @param id       resource id
     * @param formatArgs 将用于替换的格式参数。
     * @return 与特定资源ID关联的字符串值
     */
    @JvmStatic
    fun getString(@StringRes id: Int, vararg formatArgs: Any?): String? {
        return try {
            format(DawnBridge.getApp().getString(id), *formatArgs)
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            id.toString()
        }
    }

    /**
     * 返回与特定资源ID关联的字符串数组。
     *
     * @param id   resource id
     * @return 与资源关联的字符串数组。
     */
    fun getStringArray(@ArrayRes id: Int): Array<String> {
        return try {
            DawnBridge.getApp().resources.getStringArray(id)
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            arrayOf(id.toString())
        }
    }

    /**
     * 格式化字符串。
     *
     * @param str   string.
     * @param args  args.
     * @return  格式化字符串。
     */
    @JvmStatic
    fun format(str: String?, vararg args: Any?): String? {
        var text = str
        if (text != null) {
            if (args.isNotEmpty()) {
                try {
                    text = String.format(str!!, *args)
                } catch (e: IllegalFormatException) {
                    e.printStackTrace()
                }
            }
        }
        return text
    }
}