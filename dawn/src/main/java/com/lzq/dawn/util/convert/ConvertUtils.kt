package com.lzq.dawn.util.convert

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import com.lzq.dawn.DawnBridge
import com.lzq.dawn.DawnConstants
import com.lzq.dawn.DawnConstants.MemoryConstants
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.io.Serializable
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.Locale

/**
 * @Name :ConvertUtils
 * @Time :2022/7/20 14:18
 * @Author :  Lzq
 * @Desc : 转换
 */
object ConvertUtils {
    private const val BUFFER_SIZE = 8192
    private val HEX_DIGITS_UPPER =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
    private val HEX_DIGITS_LOWER =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

    /**
     * Int 转十六进制字符串
     *
     * @param num int
     * @return 十六进制字符串
     */
    fun int2HexString(num: Int): String {
        return Integer.toHexString(num)
    }

    /**
     * 十六进制字符串转 int
     *
     * @param hexString 十六进制字符
     * @return int
     */
    fun hexString2Int(hexString: String): Int {
        return hexString.toInt(16)
    }

    /**
     * Bytes 转 bits.
     *
     * @param bytes bytes.
     * @return bits
     */
    fun bytes2Bits(bytes: ByteArray?): String {
        if (bytes == null || bytes.isEmpty()) {
            return ""
        }
        val sb = StringBuilder()
        for (aByte in bytes) {
            for (j in 7 downTo 0) {
                sb.append(if (aByte.toInt() shr j and 0x01 == 0) '0' else '1')
            }
        }
        return sb.toString()
    }

    /**
     * Bits  转 bytes.
     *
     * @param bitStr bits.
     * @return bytes
     */
    fun bits2Bytes(bitStr: String): ByteArray {
        var bits = bitStr
        val lenMod = bits.length % 8
        var byteLen = bits.length / 8
        // add "0" until length to 8 times
        if (lenMod != 0) {
            for (i in lenMod..7) {
                bits = "0$bits"
            }
            byteLen++
        }
        val bytes = ByteArray(byteLen)
        for (i in 0 until byteLen) {
            for (j in 0..7) {
                bytes[i] = (bytes[i].toInt() shl 1).toByte()
                bytes[i] = (bytes[i].toInt() or bits[i * 8 + j].code - '0'.code).toByte()
            }
        }
        return bytes
    }

    /**
     * Bytes 转 chars.
     *
     * @param bytes The bytes.
     * @return chars
     */
    fun bytes2Chars(bytes: ByteArray?): CharArray? {
        if (bytes == null) {
            return null
        }
        val len = bytes.size
        if (len <= 0) {
            return null
        }
        val chars = CharArray(len)
        for (i in 0 until len) {
            chars[i] = (bytes[i].toInt() and 0xff).toChar()
        }
        return chars
    }

    /**
     * Chars 转 bytes.
     *
     * @param chars The chars.
     * @return bytes
     */
    fun chars2Bytes(chars: CharArray?): ByteArray? {
        if (chars == null || chars.isEmpty()) {
            return null
        }
        val len = chars.size
        val bytes = ByteArray(len)
        for (i in 0 until len) {
            bytes[i] = chars[i].code.toByte()
        }
        return bytes
    }
    /**
     * Bytes 转 16进制string.
     *
     * e.g. bytes2HexString(new byte[] { 0, (byte) 0xa8 }, true) returns "00A8"
     *
     * @param bytes       bytes.
     * @param isUpperCase True 使用大写，否则为 false。
     * @return 16进制string.
     */
    @JvmOverloads
    @JvmStatic
    fun bytes2HexString(bytes: ByteArray?, isUpperCase: Boolean = true): String {
        if (bytes == null) {
            return ""
        }
        val hexDigits = if (isUpperCase) HEX_DIGITS_UPPER else HEX_DIGITS_LOWER
        val len = bytes.size
        if (len <= 0) {
            return ""
        }
        val ret = CharArray(len shl 1)
        var i = 0
        var j = 0
        while (i < len) {
            ret[j++] = hexDigits[bytes[i].toInt() shr 4 and 0x0f]
            ret[j++] = hexDigits[bytes[i].toInt() and 0x0f]
            i++
        }
        return String(ret)
    }

    /**
     * 16进制string转bytes
     *
     * e.g. hexString2Bytes("00A8") returns { 0, (byte) 0xA8 }
     *
     * @param hexStr 16进制string
     * @return bytes
     */
    @JvmStatic
    fun hexString2Bytes(hexStr: String): ByteArray {
        var hexString = hexStr
        if (DawnBridge.isSpace(hexString)) {
            return ByteArray(0)
        }
        var len = hexString.length
        if (len % 2 != 0) {
            hexString = "0$hexString"
            len += 1
        }
        val hexBytes = hexString.uppercase(Locale.getDefault()).toCharArray()
        val ret = ByteArray(len shr 1)
        var i = 0
        while (i < len) {
            ret[i shr 1] = (hex2Dec(hexBytes[i]) shl 4 or hex2Dec(hexBytes[i + 1])).toByte()
            i += 2
        }
        return ret
    }

    private fun hex2Dec(hexChar: Char): Int {
        return if (hexChar in '0'..'9') {
            hexChar.code - '0'.code
        } else if (hexChar in 'A'..'F') {
            hexChar.code - 'A'.code + 10
        } else {
            throw IllegalArgumentException()
        }
    }
    /**
     * Bytes 转 string.
     */
    /**
     * Bytes 转 string.
     */
    @JvmStatic
    fun bytes2String(bytes: ByteArray, charsetName: String = ""): String? {
        return try {
            String(bytes, Charset.forName(getSafeCharset(charsetName)))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            String(bytes)
        }
    }
    /**
     * String 转 bytes.
     */
    @JvmOverloads
    @JvmStatic
    fun string2Bytes(string: String?, charsetName: String = ""): ByteArray? {
        return if (string == null) {
            null
        } else try {
            string.toByteArray(charset(getSafeCharset(charsetName)))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            string.toByteArray()
        }
    }

    /**
     * Bytes 转 JSONObject.
     */
    @JvmStatic
    fun bytes2JSONObject(bytes: ByteArray?): JSONObject? {
        return if (bytes == null) {
            null
        } else try {
            JSONObject(String(bytes))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * JSONObject 转 bytes.
     */
    @JvmStatic
    fun jsonObject2Bytes(jsonObject: JSONObject?): ByteArray? {
        return jsonObject?.toString()?.toByteArray()
    }

    /**
     * Bytes 转 JSONArray.
     */
    @JvmStatic
    fun bytes2JSONArray(bytes: ByteArray?): JSONArray? {
        return if (bytes == null) {
            null
        } else try {
            JSONArray(String(bytes))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * JSONArray 转 bytes.
     */
    @JvmStatic
    fun jsonArray2Bytes(jsonArray: JSONArray?): ByteArray? {
        return jsonArray?.toString()?.toByteArray()
    }

    /**
     * Bytes 转 Parcelable
     */
    @JvmStatic
    fun <T> bytes2Parcelable(
        bytes: ByteArray?, creator: Parcelable.Creator<T?>
    ): T? {
        if (bytes == null) {
            return null
        }
        val parcel = Parcel.obtain()
        parcel.unmarshall(bytes, 0, bytes.size)
        parcel.setDataPosition(0)
        val result = creator.createFromParcel(parcel)
        parcel.recycle()
        return result
    }

    /**
     * Parcelable 转 bytes.
     */
    @JvmStatic
    fun parcelable2Bytes(parcelable: Parcelable?): ByteArray? {
        if (parcelable == null) {
            return null
        }
        val parcel = Parcel.obtain()
        parcelable.writeToParcel(parcel, 0)
        val bytes = parcel.marshall()
        parcel.recycle()
        return bytes
    }

    /**
     * Bytes 转 Serializable.
     */
    @JvmStatic
    fun bytes2Object(bytes: ByteArray?): Any? {
        if (bytes == null) {
            return null
        }
        var ois: ObjectInputStream? = null
        return try {
            ois = ObjectInputStream(ByteArrayInputStream(bytes))
            ois.readObject()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            try {
                ois?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Serializable 转 bytes.
     */
    @JvmStatic
    fun serializable2Bytes(serializable: Serializable?): ByteArray? {
        if (serializable == null) {
            return null
        }
        var baos: ByteArrayOutputStream
        var oos: ObjectOutputStream? = null
        return try {
            oos = ObjectOutputStream(ByteArrayOutputStream().also { baos = it })
            oos.writeObject(serializable)
            baos.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            try {
                oos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Bytes 转 bitmap.
     */
    fun bytes2Bitmap(bytes: ByteArray?): Bitmap? {
        return DawnBridge.bytes2Bitmap(bytes)
    }

    /**
     * Bitmap 转 bytes.
     */
    fun bitmap2Bytes(bitmap: Bitmap?, format: CompressFormat?, quality: Int): ByteArray? {
        return DawnBridge.bitmap2Bytes(bitmap, format, quality)
    }

    /**
     * Bitmap 转 bytes.
     */
    fun bitmap2Bytes(bitmap: Bitmap?): ByteArray? {
        return DawnBridge.bitmap2Bytes(bitmap)
    }

    /**
     * Bytes 转 drawable.
     */
    fun bytes2Drawable(bytes: ByteArray?): Drawable? {
        return DawnBridge.bytes2Drawable(bytes)
    }

    /**
     * Drawable 转 bytes.
     */
    fun drawable2Bytes(drawable: Drawable?): ByteArray? {
        return DawnBridge.drawable2Bytes(drawable)
    }

    /**
     * Drawable 转 bytes.
     */
    fun drawable2Bytes(drawable: Drawable?, format: CompressFormat?, quality: Int): ByteArray? {
        return DawnBridge.drawable2Bytes(drawable, format, quality)
    }

    /**
     * 以字节为单位的内存大小。
     *
     * @param memorySize 内存大小
     * @param unit       内存大小的单位
     *
     *  * [DawnConstants.MemoryConstants.BYTE]
     *  * [DawnConstants.MemoryConstants.KB]
     *  * [DawnConstants.MemoryConstants.MB]
     *  * [DawnConstants.MemoryConstants.GB]
     *
     * @return 字节大小
     */
    fun memorySize2Byte(
        memorySize: Long, @MemoryConstants.Unit unit: Int
    ): Long {
        return if (memorySize < 0) {
            -1
        } else memorySize * unit
    }

    /**
     * 字节大小到内存大小的单位
     *
     * @param byteSize 字节大小
     * @param unit     内存大小的单位。
     *
     *  * [DawnConstants.MemoryConstants.BYTE]
     *  * [DawnConstants.MemoryConstants.KB]
     *  * [DawnConstants.MemoryConstants.MB]
     *  * [DawnConstants.MemoryConstants.GB]
     *
     * @return size of memory in unit
     */
    fun byte2MemorySize(
        byteSize: Long, @MemoryConstants.Unit unit: Int
    ): Double {
        return if (byteSize < 0) {
            (-1).toDouble()
        } else byteSize.toDouble() / unit
    }

    /**
     * 字节大小以适应内存大小。
     *
     *保留三位小数
     *
     *
     *
     * @param byteSize 字节大小
     * @return 内存大小
     */
    @JvmStatic
    @SuppressLint("DefaultLocale")
    fun byte2FitMemorySize(byteSize: Long): String {
        return byte2FitMemorySize(byteSize, 3)
    }

    /**
     * 字节大小以适应内存大小。
     *
     *保留三位小数
     *
     *
     *
     * @param byteSize  字节大小。
     * @param precision 精度
     * @return 内存大小。
     */
    @SuppressLint("DefaultLocale")
    fun byte2FitMemorySize(byteSize: Long, precision: Int): String {
        require(precision >= 0) { "precision shouldn't be less than zero!" }
        return if (byteSize < 0) {
            throw IllegalArgumentException("byteSize shouldn't be less than zero!")
        } else if (byteSize < MemoryConstants.KB) {
            String.format("%." + precision + "fB", byteSize.toDouble())
        } else if (byteSize < MemoryConstants.MB) {
            String.format("%." + precision + "fKB", byteSize.toDouble() / MemoryConstants.KB)
        } else if (byteSize < MemoryConstants.GB) {
            String.format("%." + precision + "fMB", byteSize.toDouble() / MemoryConstants.MB)
        } else {
            String.format("%." + precision + "fGB", byteSize.toDouble() / MemoryConstants.GB)
        }
    }

    /**
     * 以毫秒为单位的时间跨度
     *
     * @param timeSpan 时间跨度
     * @param unit     单位
     *
     *  * [DawnConstants.TimeConstants.MSEC]
     *  * [DawnConstants.TimeConstants.SEC]
     *  * [DawnConstants.TimeConstants.MIN]
     *  * [DawnConstants.TimeConstants.HOUR]
     *  * [DawnConstants.TimeConstants.DAY]
     *
     * @return 毫秒
     */
    fun timeSpan2Millis(timeSpan: Long, @DawnConstants.TimeConstants.Unit unit: Int): Long {
        return timeSpan * unit
    }

    /**
     * 以毫秒为单位的时间跨度
     *
     * @param millis 毫秒。
     * @param unit   单位
     *
     *  * [DawnConstants.TimeConstants.MSEC]
     *  * [DawnConstants.TimeConstants.SEC]
     *  * [DawnConstants.TimeConstants.MIN]
     *  * [DawnConstants.TimeConstants.HOUR]
     *  * [DawnConstants.TimeConstants.DAY]
     *
     * @return time span in unit
     */
    fun millis2TimeSpan(millis: Long, @DawnConstants.TimeConstants.Unit unit: Int): Long {
        return millis / unit
    }

    /**
     * 毫秒以适应时间跨度
     *
     * @param millis    毫秒
     *
     * millis &lt;= 0, return null
     * @param precision 时间跨度的精度
     *
     *  * precision = 0, return null
     *  * precision = 1, return 天
     *  * precision = 2, return 天, 小时
     *  * precision = 3, return 天, 小时, 分钟
     *  * precision = 4, return 天, 小时, 分钟, 秒
     *  * precision &gt;= 5，return 天, 小时, 分钟, 秒, 毫秒
     *
     * @return fit time span
     */
    fun millis2FitTimeSpan(millis: Long, precision: Int): String? {
        return DawnBridge.millis2FitTimeSpan(millis, precision)
    }

    /**
     * Input stream 转 output stream.
     */
    @JvmStatic
    fun input2OutputStream(`is`: InputStream?): ByteArrayOutputStream? {
        return if (`is` == null) {
            null
        } else try {
            val os = ByteArrayOutputStream()
            val b = ByteArray(BUFFER_SIZE)
            var len: Int
            while (`is`.read(b, 0, BUFFER_SIZE).also { len = it } != -1) {
                os.write(b, 0, len)
            }
            os
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            try {
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Output stream 转 input stream.
     */
    fun output2InputStream(out: OutputStream?): ByteArrayInputStream? {
        return if (out == null) {
            null
        } else ByteArrayInputStream((out as ByteArrayOutputStream).toByteArray())
    }

    /**
     * Input stream 转 bytes.
     */
    @JvmStatic
    fun inputStream2Bytes(`is`: InputStream?): ByteArray? {
        return if (`is` == null) {
            null
        } else input2OutputStream(`is`)!!.toByteArray()
    }

    /**
     * Bytes 转 input stream.
     */
    fun bytes2InputStream(bytes: ByteArray?): InputStream? {
        return if (bytes == null || bytes.isEmpty()) {
            null
        } else ByteArrayInputStream(bytes)
    }

    /**
     * Output stream 转 bytes.
     */
    fun outputStream2Bytes(out: OutputStream?): ByteArray {
        return (out as ByteArrayOutputStream).toByteArray()
    }

    /**
     * Bytes 转 output stream.
     */
    fun bytes2OutputStream(bytes: ByteArray?): OutputStream? {
        if (bytes == null || bytes.isEmpty()) {
            return null
        }
        var os: ByteArrayOutputStream? = null
        return try {
            os = ByteArrayOutputStream()
            os.write(bytes)
            os
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            try {
                os?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Input stream 转 string.
     */
    fun inputStream2String(`is`: InputStream?, charsetName: String): String {
        return if (`is` == null) {
            ""
        } else try {
            val baos = input2OutputStream(`is`) ?: return ""
            baos.toString(getSafeCharset(charsetName))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * String to input stream.
     */
    fun string2InputStream(string: String?, charsetName: String): InputStream? {
        return if (string == null) {
            null
        } else try {
            ByteArrayInputStream(string.toByteArray(charset(getSafeCharset(charsetName))))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Output stream to string.
     */
    fun outputStream2String(out: OutputStream?, charsetName: String): String {
        return try {
            String(outputStream2Bytes(out),Charset.forName(getSafeCharset(charsetName)))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * String to output stream.
     */
    fun string2OutputStream(string: String?, charsetName: String): OutputStream? {
        return if (string == null) {
            null
        } else try {
            bytes2OutputStream(string.toByteArray(charset(getSafeCharset(charsetName))))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            null
        }
    }

    @JvmStatic
    @JvmOverloads
    fun inputStream2Lines(
        `is`: InputStream?, charsetName: String = ""
    ): List<String>? {
        var reader: BufferedReader? = null
        return try {
            val list: MutableList<String> = ArrayList()
            reader = BufferedReader(InputStreamReader(`is`, getSafeCharset(charsetName)))
            var line: String
            while (reader.readLine().also { line = it } != null) {
                list.add(line)
            }
            list
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            try {
                reader?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Drawable 转 bitmap.
     */
    fun drawable2Bitmap(drawable: Drawable?): Bitmap? {
        return DawnBridge.drawable2Bitmap(drawable)
    }

    /**
     * Bitmap 转 drawable.
     */
    fun bitmap2Drawable(bitmap: Bitmap?): Drawable? {
        return DawnBridge.bitmap2Drawable(bitmap)
    }

    /**
     * dp 的值到 px 的值
     */
    fun dp2px(dpValue: Float): Int {
        return DawnBridge.dp2px(dpValue)
    }

    /**
     * px 的值到 dp 的值
     */
    fun px2dp(pxValue: Float): Int {
        return DawnBridge.px2dp(pxValue)
    }

    /**
     * sp 的值到 px 的值
     */
    fun sp2px(spValue: Float): Int {
        return DawnBridge.sp2px(spValue)
    }

    /**
     * px 的值到 sp 的值
     */
    fun px2sp(pxValue: Float): Int {
        return DawnBridge.px2sp(pxValue)
    }

    private fun getSafeCharset(charsetName: String): String {
        var cn = charsetName
        if (DawnBridge.isSpace(charsetName) || !Charset.isSupported(charsetName)) {
            cn = "UTF-8"
        }
        return cn
    }
}