package com.lzq.dawn.util.encode

import android.os.Build
import android.text.Html
import android.util.Base64
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * @Name :EncodeUtils
 * @Time :2022/7/21 10:31
 * @Author :  Lzq
 * @Desc : 编码
 */
object EncodeUtils {
    /**
     * 返回 url 编码 字符串。
     *
     * @param input       input.
     * @param charsetName 字符集的名称。
     * @return url 编码 字符串。
     */
    @JvmOverloads
    fun urlEncode(input: String?, charsetName: String? = "UTF-8"): String {
        return if (input.isNullOrEmpty()) {
            ""
        } else try {
            URLEncoder.encode(input, charsetName)
        } catch (e: UnsupportedEncodingException) {
            throw AssertionError(e)
        }
    }
    /**
     * 返回解码 url encoded 字符串的字符串。
     *
     * @param input       input.
     * @param charsetName 字符集的名称。
     * @return 解码 url encoded 字符串的字符串。
     */
    @JvmOverloads
    fun urlDecode(input: String?, charsetName: String? = "UTF-8"): String {
        return if (input.isNullOrEmpty()) {
            ""
        } else try {
            val safeInput =
                input.replace("%(?![0-9a-fA-F]{2})".toRegex(), "%25").replace("\\+".toRegex(), "%2B")
            URLDecoder.decode(safeInput, charsetName)
        } catch (e: UnsupportedEncodingException) {
            throw AssertionError(e)
        }
    }

    /**
     * 返回 Base64 编码字节。
     *
     * @param input input.
     * @return Base64 编码字节。
     */
    fun base64Encode(input: String): ByteArray {
        return base64Encode(input.toByteArray())
    }

    /**
     * 返回 Base64 编码字节。
     *
     * @param input input.
     * @return Base64 编码字节。
     */
    @JvmStatic
    fun base64Encode(input: ByteArray?): ByteArray {
        return if (input == null || input.isEmpty()) {
            ByteArray(0)
        } else Base64.encode(input, Base64.NO_WRAP)
    }

    /**
     * 返回 Base64 编码字符串。
     *
     * @param input input.
     * @return Base64 编码字符串。
     */
    fun base64Encode2String(input: ByteArray?): String {
        return if (input == null || input.isEmpty()) {
            ""
        } else Base64.encodeToString(input, Base64.NO_WRAP)
    }

    /**
     * 返回解码 Base64 编码字符串的字节。
     *
     * @param input input.
     * @return 解码 Base64 编码字符串的字节。
     */
    fun base64Decode(input: String?): ByteArray {
        return if (input.isNullOrEmpty()) {
            ByteArray(0)
        } else Base64.decode(input, Base64.NO_WRAP)
    }

    /**
     * 返回解码 Base64 编码字节的字节。
     *
     * @param input input.
     * @return 解码 Base64 编码字节的字节。
     */
    @JvmStatic
    fun base64Decode(input: ByteArray?): ByteArray {
        return if (input == null || input.isEmpty()) {
            ByteArray(0)
        } else Base64.decode(input, Base64.NO_WRAP)
    }

    /**
     * 返回 html 编码字符串。
     *
     * @param input input.
     * @return html 编码字符串。
     */
    fun htmlEncode(input: CharSequence?): String {
        if (input.isNullOrEmpty()) {
            return ""
        }
        val sb = StringBuilder()
        var c: Char
        var i = 0
        val len = input.length
        while (i < len) {
            c = input[i]
            when (c) {
                '<' -> sb.append("&lt;") //$NON-NLS-1$
                '>' -> sb.append("&gt;") //$NON-NLS-1$
                '&' -> sb.append("&amp;") //$NON-NLS-1$
                '\'' ->                     //http://www.w3.org/TR/xhtml1
                    // The named character reference &apos; (the apostrophe, U+0027) was
                    // introduced in XML 1.0 but does not appear in HTML. Authors should
                    // therefore use &#39; instead of &apos; to work as expected in HTML 4
                    // user agents.
                    sb.append("&#39;") //$NON-NLS-1$
                '"' -> sb.append("&quot;") //$NON-NLS-1$
                else -> sb.append(c)
            }
            i++
        }
        return sb.toString()
    }

    /**
     * 返回 解码 html-encode 字符串的字符串。
     *
     * @param input input.
     * @return 解码 html-编码 字符串的字符串。
     */
    fun htmlDecode(input: String?): CharSequence {
        if (input.isNullOrEmpty()) {
            return ""
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(input)
        }
    }

    /**
     * 返回用一个空格填充的二进制编码字符串
     *
     * @param input input.
     * @return 二进制字符串
     */
    fun binaryEncode(input: String?): String {
        if (input.isNullOrEmpty()) {
            return ""
        }
        val sb = StringBuilder()
        for (i in input.toCharArray()) {
            sb.append(Integer.toBinaryString(i.code)).append(" ")
        }
        return sb.deleteCharAt(sb.length - 1).toString()
    }

    /**
     * 从二进制返回 UTF-8 字符串
     *
     * @param input 二进制字符串
     * @return UTF-8 字符串
     */
    fun binaryDecode(input: String?): String {
        if (input.isNullOrEmpty()) {
            return ""
        }
        val splits = input.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val sb = StringBuilder()
        for (split in splits) {
            sb.append(split.toInt(2).toChar())
        }
        return sb.toString()
    }
}