package com.lzq.dawn.util.encrypt

import android.os.Build
import com.lzq.dawn.DawnBridge
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.DigestInputStream
import java.security.InvalidKeyException
import java.security.Key
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * @Name :EncryptUtils
 * @Time :2022/7/21 11:57
 * @Author :  Lzq
 * @Desc : 加密
 */
object EncryptUtils {
    //-----------------------------------------------hash encryption-----------------------------------------------//
    /**
     * 返回 MD2 加密的十六进制字符串。
     *
     * @param data data.
     * @return MD2 加密的十六进制字符串。
     */
    fun encryptMD2ToString(data: String?): String {
        return if (data == null || data.length == 0) {
            ""
        } else encryptMD2ToString(data.toByteArray())
    }

    /**
     * 返回 MD2 加密的十六进制字符串。
     *
     * @param data data.
     * @return MD2 加密的十六进制字符串。
     */
    fun encryptMD2ToString(data: ByteArray?): String {
        return DawnBridge.bytes2HexString(encryptMD2(data))
    }

    /**
     * 返回 MD2 加密的字节。
     *
     * @param data data.
     * @return MD2 加密的字节。
     */
    fun encryptMD2(data: ByteArray?): ByteArray? {
        return hashTemplate(data, "MD2")
    }

    /**
     * 返回 MD5 加密的十六进制字符串。
     *
     * @param data data.
     * @return MD5 加密的十六进制字符串。
     */
    fun encryptMD5ToString(data: String?): String {
        return if (data == null || data.length == 0) {
            ""
        } else encryptMD5ToString(data.toByteArray())
    }

    /**
     * 返回 MD5 加密的十六进制字符串。
     *
     * @param data data.
     * @param salt salt.
     * @return MD5 加密的十六进制字符串。
     */
    fun encryptMD5ToString(data: String?, salt: String?): String {
        if (data == null && salt == null) {
            return ""
        }
        if (salt == null) {
            return DawnBridge.bytes2HexString(encryptMD5(data!!.toByteArray()))
        }
        return if (data == null) {
            DawnBridge.bytes2HexString(encryptMD5(salt.toByteArray()))
        } else DawnBridge.bytes2HexString(encryptMD5((data + salt).toByteArray()))
    }

    /**
     * 返回 MD5 加密的十六进制字符串。
     *
     * @param data data.
     * @return MD5 加密的十六进制字符串。
     */
    fun encryptMD5ToString(data: ByteArray?): String {
        return DawnBridge.bytes2HexString(encryptMD5(data))
    }

    /**
     * Return the hex string of MD5 encryption.
     *
     * @param data The data.
     * @param salt The salt.
     * @return the hex string of MD5 encryption
     */
    fun encryptMD5ToString(data: ByteArray?, salt: ByteArray?): String {
        if (data == null && salt == null) {
            return ""
        }
        if (salt == null) {
            return DawnBridge.bytes2HexString(encryptMD5(data))
        }
        if (data == null) {
            return DawnBridge.bytes2HexString(encryptMD5(salt))
        }
        val dataSalt = ByteArray(data.size + salt.size)
        System.arraycopy(data, 0, dataSalt, 0, data.size)
        System.arraycopy(salt, 0, dataSalt, data.size, salt.size)
        return DawnBridge.bytes2HexString(encryptMD5(dataSalt))
    }

    /**
     * 返回 MD5 加密的字节数。
     *
     * @param data data.
     * @return MD5 加密的字节数。
     */
    fun encryptMD5(data: ByteArray?): ByteArray? {
        return hashTemplate(data, "MD5")
    }

    /**
     * 返回文件的 MD5 加密的十六进制字符串。
     *
     * @param filePath 文件路径
     * @return 文件的 MD5 加密的十六进制字符串。
     */
    fun encryptMD5File2String(filePath: String?): String {
        val file = if (DawnBridge.isSpace(filePath)) null else File(filePath)
        return encryptMD5File2String(file)
    }

    /**
     * 返回文件的 MD5 加密字节数。
     *
     * @param filePath 文件路径
     * @return 文件的 MD5 加密字节数。
     */
    fun encryptMD5File(filePath: String?): ByteArray? {
        val file = if (DawnBridge.isSpace(filePath)) null else File(filePath)
        return encryptMD5File(file)
    }

    /**
     * 返回文件的 MD5 加密的十六进制字符串
     *
     * @param file file.
     * @return 文件的 MD5 加密的十六进制字符串
     */
    fun encryptMD5File2String(file: File?): String {
        return DawnBridge.bytes2HexString(encryptMD5File(file))
    }

    /**
     * 返回文件的 MD5 加密字节数。
     *
     * @param file file.
     * @return 文件的 MD5 加密字节数。
     */
    fun encryptMD5File(file: File?): ByteArray? {
        if (file == null) {
            return null
        }
        var fis: FileInputStream? = null
        val digestInputStream: DigestInputStream
        return try {
            fis = FileInputStream(file)
            var md = MessageDigest.getInstance("MD5")
            digestInputStream = DigestInputStream(fis, md)
            val buffer = ByteArray(256 * 1024)
            while (true) {
                if (digestInputStream.read(buffer) <= 0) {
                    break
                }
            }
            md = digestInputStream.messageDigest
            md.digest()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            try {
                fis?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 返回 SHA1 加密的十六进制字符串。
     *
     * @param data data.
     * @return SHA1 加密的十六进制字符串
     */
    fun encryptSHA1ToString(data: String?): String {
        return if (data == null || data.length == 0) {
            ""
        } else encryptSHA1ToString(data.toByteArray())
    }

    /**
     * 返回 SHA1 加密的十六进制字符串。
     *
     * @param data data.
     * @return SHA1 加密的十六进制字符串。
     */
    fun encryptSHA1ToString(data: ByteArray?): String {
        return DawnBridge.bytes2HexString(encryptSHA1(data))
    }

    /**
     * 返回 SHA1 加密的字节。
     *
     * @param data data.
     * @return SHA1 加密的字节。
     */
    fun encryptSHA1(data: ByteArray?): ByteArray? {
        return hashTemplate(data, "SHA-1")
    }

    /**
     * 返回 SHA224 加密的十六进制字符串。
     *
     * @param data data.
     * @return SHA224 加密的十六进制字符串。
     */
    fun encryptSHA224ToString(data: String?): String {
        return if (data == null || data.length == 0) {
            ""
        } else encryptSHA224ToString(data.toByteArray())
    }

    /**
     * 返回 SHA224 加密的十六进制字符串。
     *
     * @param data data.
     * @return SHA224 加密的十六进制字符串。
     */
    fun encryptSHA224ToString(data: ByteArray?): String {
        return DawnBridge.bytes2HexString(encryptSHA224(data))
    }

    /**
     * 返回 SHA224 加密的字节数。
     *
     * @param data data.
     * @return SHA224 加密的字节数。
     */
    fun encryptSHA224(data: ByteArray?): ByteArray? {
        return hashTemplate(data, "SHA224")
    }

    /**
     * 返回 SHA256 加密的十六进制字符串。
     *
     * @param data data.
     * @return SHA256 加密的十六进制字符串。
     */
    fun encryptSHA256ToString(data: String?): String {
        return if (data == null || data.length == 0) {
            ""
        } else encryptSHA256ToString(data.toByteArray())
    }

    /**
     * 返回 SHA256 加密的十六进制字符串。
     *
     * @param data data.
     * @return SHA256 加密的十六进制字符串。
     */
    fun encryptSHA256ToString(data: ByteArray?): String {
        return DawnBridge.bytes2HexString(encryptSHA256(data))
    }

    /**
     * 返回 SHA256 加密的字节。
     *
     * @param data data.
     * @return SHA256 加密的字节。
     */
    fun encryptSHA256(data: ByteArray?): ByteArray? {
        return hashTemplate(data, "SHA-256")
    }

    /**
     * 返回 SHA384 加密的十六进制字符串。
     *
     * @param data data.
     * @return SHA384 加密的十六进制字符串。
     */
    fun encryptSHA384ToString(data: String?): String {
        return if (data == null || data.length == 0) {
            ""
        } else encryptSHA384ToString(data.toByteArray())
    }

    /**
     * 返回 SHA384 加密的十六进制字符串。
     *
     * @param data data.
     * @return SHA384 加密的十六进制字符串。
     */
    fun encryptSHA384ToString(data: ByteArray?): String {
        return DawnBridge.bytes2HexString(encryptSHA384(data))
    }

    /**
     * 返回 SHA384 加密的字节。
     *
     * @param data data.
     * @return SHA384 加密的字节。
     */
    fun encryptSHA384(data: ByteArray?): ByteArray? {
        return hashTemplate(data, "SHA-384")
    }

    /**
     * 返回 SHA512 加密的十六进制字符串。
     *
     * @param data data.
     * @return SHA512 加密的十六进制字符串。
     */
    fun encryptSHA512ToString(data: String?): String {
        return if (data == null || data.length == 0) {
            ""
        } else encryptSHA512ToString(data.toByteArray())
    }

    /**
     * 返回 SHA512 加密的十六进制字符串
     *
     * @param data data.
     * @return SHA512 加密的十六进制字符串
     */
    fun encryptSHA512ToString(data: ByteArray?): String {
        return DawnBridge.bytes2HexString(encryptSHA512(data))
    }

    /**
     * 返回 SHA512 加密的字节数。
     *
     * @param data data.
     * @return SHA512 加密的字节数。
     */
    fun encryptSHA512(data: ByteArray?): ByteArray? {
        return hashTemplate(data, "SHA-512")
    }

    /**
     * 返回哈希加密的字节。
     *
     * @param data      data.
     * @param algorithm 哈希加密的名称。
     * @return 哈希加密的字节。
     */
    @JvmStatic
    fun hashTemplate(data: ByteArray?, algorithm: String?): ByteArray? {
        return if (data == null || data.size <= 0) {
            null
        } else try {
            val md = MessageDigest.getInstance(algorithm)
            md.update(data)
            md.digest()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            null
        }
    }
    //-----------------------------------------------Hmac encryption-----------------------------------------------//
    /**
     * 返回 HmacMD5 加密的十六进制字符串。
     *
     * @param data data.
     * @param key  key.
     * @return HmacMD5 加密的十六进制字符串。
     */
    fun encryptHmacMD5ToString(data: String?, key: String?): String {
        return if (data == null || data.length == 0 || key == null || key.length == 0) {
            ""
        } else encryptHmacMD5ToString(data.toByteArray(), key.toByteArray())
    }

    /**
     * 返回 HmacMD5 加密的十六进制字符串。
     *
     * @param data data.
     * @param key  key.
     * @return HmacMD5加密的十六进制字符串
     */
    fun encryptHmacMD5ToString(data: ByteArray?, key: ByteArray?): String {
        return DawnBridge.bytes2HexString(encryptHmacMD5(data, key))
    }

    /**
     * 返回 HmacMD5 加密的字节。
     *
     * @param data data.
     * @param key  key.
     * @return HmacMD5 加密的字节。
     */
    fun encryptHmacMD5(data: ByteArray?, key: ByteArray?): ByteArray? {
        return hmacTemplate(data, key, "HmacMD5")
    }

    /**
     * 返回 HmacSHA1 加密的十六进制字符串。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA1 加密的十六进制字符串。
     */
    fun encryptHmacSHA1ToString(data: String?, key: String?): String {
        return if (data == null || data.length == 0 || key == null || key.length == 0) {
            ""
        } else encryptHmacSHA1ToString(
            data.toByteArray(), key.toByteArray()
        )
    }

    /**
     * 返回 HmacSHA1 加密的十六进制字符串。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA1 加密的十六进制字符串。
     */
    fun encryptHmacSHA1ToString(data: ByteArray?, key: ByteArray?): String {
        return DawnBridge.bytes2HexString(encryptHmacSHA1(data, key))
    }

    /**
     * 返回 Hmac SHA1 加密的字节。
     *
     * @param data data.
     * @param key  key.
     * @return Hmac SHA1 加密的字节。
     */
    fun encryptHmacSHA1(data: ByteArray?, key: ByteArray?): ByteArray? {
        return hmacTemplate(data, key, "HmacSHA1")
    }

    /**
     * 返回 HmacSHA224 加密的十六进制字符串。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA224 加密的十六进制字符串。
     */
    fun encryptHmacSHA224ToString(data: String?, key: String?): String {
        return if (data == null || data.length == 0 || key == null || key.length == 0) {
            ""
        } else encryptHmacSHA224ToString(data.toByteArray(), key.toByteArray())
    }

    /**
     * 返回 HmacSHA224 加密的十六进制字符串。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA224 加密的十六进制字符串。
     */
    fun encryptHmacSHA224ToString(data: ByteArray?, key: ByteArray?): String {
        return DawnBridge.bytes2HexString(encryptHmacSHA224(data, key))
    }

    /**
     * 返回 HmacSHA224 加密的字节。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA224 加密的字节。
     */
    fun encryptHmacSHA224(data: ByteArray?, key: ByteArray?): ByteArray? {
        return hmacTemplate(data, key, "HmacSHA224")
    }

    /**
     * 返回 HmacSHA256 加密的十六进制字符串。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA256 加密的十六进制字符串。
     */
    fun encryptHmacSHA256ToString(data: String?, key: String?): String {
        return if (data == null || data.length == 0 || key == null || key.length == 0) {
            ""
        } else encryptHmacSHA256ToString(data.toByteArray(), key.toByteArray())
    }

    /**
     * 返回 HmacSHA256 加密的十六进制字符串。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA256 加密的十六进制字符串。
     */
    fun encryptHmacSHA256ToString(data: ByteArray?, key: ByteArray?): String {
        return DawnBridge.bytes2HexString(encryptHmacSHA256(data, key))
    }

    /**
     * 返回 HmacSHA256 加密的字节。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA256 加密的字节。
     */
    fun encryptHmacSHA256(data: ByteArray?, key: ByteArray?): ByteArray? {
        return hmacTemplate(data, key, "HmacSHA256")
    }

    /**
     * 返回 HmacSHA384 加密的十六进制字符串。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA384加密的十六进制字符串
     */
    fun encryptHmacSHA384ToString(data: String?, key: String?): String {
        return if (data == null || data.length == 0 || key == null || key.length == 0) {
            ""
        } else encryptHmacSHA384ToString(
            data.toByteArray(), key.toByteArray()
        )
    }

    /**
     * 返回 HmacSHA384 加密的十六进制字符串。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA384 加密的十六进制字符串。
     */
    fun encryptHmacSHA384ToString(data: ByteArray?, key: ByteArray?): String {
        return DawnBridge.bytes2HexString(encryptHmacSHA384(data, key))
    }

    /**
     * 返回 HmacSHA384 加密的字节。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA384 加密的字节。
     */
    fun encryptHmacSHA384(data: ByteArray?, key: ByteArray?): ByteArray? {
        return hmacTemplate(data, key, "HmacSHA384")
    }

    /**
     * 返回 HmacSHA512 加密的十六进制字符串。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA512 加密的十六进制字符串。
     */
    fun encryptHmacSHA512ToString(data: String?, key: String?): String {
        return if (data == null || data.length == 0 || key == null || key.length == 0) {
            ""
        } else encryptHmacSHA512ToString(data.toByteArray(), key.toByteArray())
    }

    /**
     * 返回 HmacSHA512 加密的十六进制字符串。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA512 加密的十六进制字符串。
     */
    fun encryptHmacSHA512ToString(data: ByteArray?, key: ByteArray?): String {
        return DawnBridge.bytes2HexString(encryptHmacSHA512(data, key))
    }

    /**
     * 返回 HmacSHA512 加密的字节。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA512 加密的字节。
     */
    fun encryptHmacSHA512(data: ByteArray?, key: ByteArray?): ByteArray? {
        return hmacTemplate(data, key, "HmacSHA512")
    }

    /**
     * 返回 hmac 加密的字节数。
     *
     * @param data      data.
     * @param key       key.
     * @param algorithm hmac 加密的名称。
     * @return hmac 加密的字节数。
     */
    private fun hmacTemplate(
        data: ByteArray?, key: ByteArray?, algorithm: String
    ): ByteArray? {
        return if (data == null || data.size == 0 || key == null || key.size == 0) {
            null
        } else try {
            val secretKey = SecretKeySpec(key, algorithm)
            val mac = Mac.getInstance(algorithm)
            mac.init(secretKey)
            mac.doFinal(data)
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
            null
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            null
        }
    }
    //-----------------------------------------------DES encryption-----------------------------------------------//
    /**
     * 返回 DES 加密的 Base64 编码字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  *DES/CBC/PKCS5Padding*.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return DES 加密的 Base64 编码字节。
     */
    fun encryptDES2Base64(
        data: ByteArray?, key: ByteArray?, transformation: String?, iv: ByteArray?
    ): ByteArray {
        return DawnBridge.base64Encode(encryptDES(data, key, transformation, iv))
    }

    /**
     * 返回 DES 加密的十六进制字符串。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  *DES/CBC/PKCS5Padding*.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return DES 加密的十六进制字符串。
     */
    fun encryptDES2HexString(
        data: ByteArray?, key: ByteArray?, transformation: String?, iv: ByteArray?
    ): String {
        return DawnBridge.bytes2HexString(encryptDES(data, key, transformation, iv))
    }

    /**
     * 返回 DES 加密的字节
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  *DES/CBC/PKCS5Padding*.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return DES 加密的字节
     */
    fun encryptDES(
        data: ByteArray?, key: ByteArray?, transformation: String?, iv: ByteArray?
    ): ByteArray? {
        return symmetricTemplate(data, key, "DES", transformation, iv, true)
    }

    /**
     * 返回 Base64 编码字节的 DES 解密字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  *DES/CBC/PKCS5Padding*.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return Base64 编码字节的 DES 解密字节。
     */
    fun decryptBase64DES(
        data: ByteArray?, key: ByteArray?, transformation: String?, iv: ByteArray?
    ): ByteArray? {
        return decryptDES(DawnBridge.base64Decode(data), key, transformation, iv)
    }

    /**
     * 返回十六进制字符串的 DES 解密字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  *DES/CBC/PKCS5Padding*.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return 十六进制字符串的 DES 解密字节。
     */
    fun decryptHexStringDES(
        data: String?, key: ByteArray?, transformation: String?, iv: ByteArray?
    ): ByteArray? {
        return decryptDES(DawnBridge.hexString2Bytes(data), key, transformation, iv)
    }

    /**
     * 返回 DES 解密的字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  *DES/CBC/PKCS5Padding*.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return DES 解密的字节。
     */
    fun decryptDES(
        data: ByteArray?, key: ByteArray?, transformation: String?, iv: ByteArray?
    ): ByteArray? {
        return symmetricTemplate(data, key, "DES", transformation, iv, false)
    }
    //-----------------------------------------------3DES encryption-----------------------------------------------//
    /**
     * 返回 3DES 加密的 Base64 编码字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  *DES/CBC/PKCS5Padding*.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return 3DES 加密的 Base64 编码字节。
     */
    fun encrypt3DES2Base64(
        data: ByteArray?, key: ByteArray?, transformation: String?, iv: ByteArray?
    ): ByteArray {
        return DawnBridge.base64Encode(encrypt3DES(data, key, transformation, iv))
    }

    /**
     * 返回 3DES 加密的十六进制字符串。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  *DES/CBC/PKCS5Padding*.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return 3DES 加密的十六进制字符串。
     */
    fun encrypt3DES2HexString(
        data: ByteArray?, key: ByteArray?, transformation: String?, iv: ByteArray?
    ): String {
        return DawnBridge.bytes2HexString(encrypt3DES(data, key, transformation, iv))
    }

    /**
     * 返回 3DES 加密的字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  *DES/CBC/PKCS5Padding*.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return 3DES 加密的字节。
     */
    fun encrypt3DES(
        data: ByteArray?, key: ByteArray?, transformation: String?, iv: ByteArray?
    ): ByteArray? {
        return symmetricTemplate(data, key, "DESede", transformation, iv, true)
    }

    /**
     * 返回 Base64 编码字节的 3DES 解密字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  *DES/CBC/PKCS5Padding*.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return Base64 编码字节的 3DES 解密字节。
     */
    fun decryptBase64_3DES(
        data: ByteArray?, key: ByteArray?, transformation: String?, iv: ByteArray?
    ): ByteArray? {
        return decrypt3DES(DawnBridge.base64Decode(data), key, transformation, iv)
    }

    /**
     * 返回十六进制字符串的 3DES 解密字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  *DES/CBC/PKCS5Padding*.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return 十六进制字符串的 3DES 解密字节。
     */
    fun decryptHexString3DES(
        data: String?, key: ByteArray?, transformation: String?, iv: ByteArray?
    ): ByteArray? {
        return decrypt3DES(DawnBridge.hexString2Bytes(data), key, transformation, iv)
    }

    /**
     * 返回 3DES 解密的字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  *DES/CBC/PKCS5Padding*.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return 3DES 解密的字节。
     */
    fun decrypt3DES(
        data: ByteArray?, key: ByteArray?, transformation: String?, iv: ByteArray?
    ): ByteArray? {
        return symmetricTemplate(data, key, "DESede", transformation, iv, false)
    }
    //-----------------------------------------------AES encryption-----------------------------------------------//
    /**
     * 返回 AES 加密的 Base64 编码字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  *DES/CBC/PKCS5Padding*.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return AES 加密的 Base64 编码字节。
     */
    fun encryptAES2Base64(
        data: ByteArray?, key: ByteArray?, transformation: String?, iv: ByteArray?
    ): ByteArray {
        return DawnBridge.base64Encode(encryptAES(data, key, transformation, iv))
    }

    /**
     * 返回 AES 加密的十六进制字符串。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  *DES/CBC/PKCS5Padding*.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return AES 加密的十六进制字符串。
     */
    fun encryptAES2HexString(
        data: ByteArray?, key: ByteArray?, transformation: String?, iv: ByteArray?
    ): String {
        return DawnBridge.bytes2HexString(encryptAES(data, key, transformation, iv))
    }

    /**
     * 返回 AES 加密的字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  *DES/CBC/PKCS5Padding*.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return AES 加密的字节。
     */
    fun encryptAES(
        data: ByteArray?, key: ByteArray?, transformation: String?, iv: ByteArray?
    ): ByteArray? {
        return symmetricTemplate(data, key, "AES", transformation, iv, true)
    }

    /**
     * 返回 Base64 编码字节的 AES 解密字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  *DES/CBC/PKCS5Padding*.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return Base64 编码字节的 AES 解密字节。
     */
    fun decryptBase64AES(
        data: ByteArray?, key: ByteArray?, transformation: String?, iv: ByteArray?
    ): ByteArray? {
        return decryptAES(DawnBridge.base64Decode(data), key, transformation, iv)
    }

    /**
     * 返回十六进制字符串的 AES 解密字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  *DES/CBC/PKCS5Padding*.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return 十六进制字符串的 AES 解密字节。
     */
    fun decryptHexStringAES(
        data: String?, key: ByteArray?, transformation: String?, iv: ByteArray?
    ): ByteArray? {
        return decryptAES(DawnBridge.hexString2Bytes(data), key, transformation, iv)
    }

    /**
     * 返回 AES 解密的字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  *DES/CBC/PKCS5Padding*.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return AES 解密的字节。
     */
    fun decryptAES(
        data: ByteArray?, key: ByteArray?, transformation: String?, iv: ByteArray?
    ): ByteArray? {
        return symmetricTemplate(data, key, "AES", transformation, iv, false)
    }

    /**
     * 返回对称加密或解密的字节。
     *
     * @param data           data.
     * @param key            key.
     * @param algorithm      算法的名称。
     * @param transformation 转换的名称，例如  *DES/CBC/PKCS5Padding*.
     * @param isEncrypt      加密为真，否则为假。
     * @return 对称加密或解密的字节。
     */
    private fun symmetricTemplate(
        data: ByteArray?,
        key: ByteArray?,
        algorithm: String,
        transformation: String?,
        iv: ByteArray?,
        isEncrypt: Boolean
    ): ByteArray? {
        return if (data == null || data.size == 0 || key == null || key.size == 0) {
            null
        } else try {
            val secretKey: SecretKey
            secretKey = if ("DES" == algorithm) {
                val desKey = DESKeySpec(key)
                val keyFactory = SecretKeyFactory.getInstance(algorithm)
                keyFactory.generateSecret(desKey)
            } else {
                SecretKeySpec(key, algorithm)
            }
            val cipher = Cipher.getInstance(transformation)
            if (iv == null || iv.size == 0) {
                cipher.init(if (isEncrypt) Cipher.ENCRYPT_MODE else Cipher.DECRYPT_MODE, secretKey)
            } else {
                val params: AlgorithmParameterSpec = IvParameterSpec(iv)
                cipher.init(if (isEncrypt) Cipher.ENCRYPT_MODE else Cipher.DECRYPT_MODE, secretKey, params)
            }
            cipher.doFinal(data)
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }
    //-----------------------------------------------RSA encryption-----------------------------------------------//
    /**
     * 返回 RSA 加密的 Base64 编码字节
     *
     * @param data           data.
     * @param publicKey      公钥。
     * @param keySize        密钥的大小，例如1024、2048……
     * @param transformation 转换的名称，例如  *RSA/CBC/PKCS1Padding*.
     * @return RSA 加密的 Base64 编码字节
     */
    fun encryptRSA2Base64(
        data: ByteArray?, publicKey: ByteArray?, keySize: Int, transformation: String
    ): ByteArray {
        return DawnBridge.base64Encode(encryptRSA(data, publicKey, keySize, transformation))
    }

    /**
     * 返回 RSA 加密的十六进制字符串。
     *
     * @param data           data.
     * @param publicKey      公钥。
     * @param keySize        密钥的大小，例如1024、2048……
     * @param transformation 转换的名称，例如  *RSA/CBC/PKCS1Padding*.
     * @return RSA 加密的十六进制字符串。
     */
    fun encryptRSA2HexString(
        data: ByteArray?, publicKey: ByteArray?, keySize: Int, transformation: String
    ): String {
        return DawnBridge.bytes2HexString(encryptRSA(data, publicKey, keySize, transformation))
    }

    /**
     * 返回 RSA 加密的字节。
     *
     * @param data           data.
     * @param publicKey      公钥。
     * @param keySize        密钥的大小，例如1024、2048……
     * @param transformation 转换的名称，例如  *RSA/CBC/PKCS1Padding*.
     * @return RSA 加密的字节。
     */
    fun encryptRSA(
        data: ByteArray?, publicKey: ByteArray?, keySize: Int, transformation: String
    ): ByteArray? {
        return rsaTemplate(data, publicKey, keySize, transformation, true)
    }

    /**
     * 返回 Base64 编码字节的 RSA 解密字节。
     *
     * @param data           data.
     * @param privateKey     私钥。
     * @param keySize        密钥的大小，例如1024、2048……
     * @param transformation 转换的名称，例如  *RSA/CBC/PKCS1Padding*.
     * @return Base64 编码字节的 RSA 解密字节。
     */
    fun decryptBase64RSA(
        data: ByteArray?, privateKey: ByteArray?, keySize: Int, transformation: String
    ): ByteArray? {
        return decryptRSA(DawnBridge.base64Decode(data), privateKey, keySize, transformation)
    }

    /**
     * 返回十六进制字符串的 RSA 解密字节。
     *
     * @param data           data.
     * @param privateKey     私钥。
     * @param keySize        密钥的大小，例如1024、2048……
     * @param transformation 转换的名称，例如  *RSA/CBC/PKCS1Padding*.
     * @return 十六进制字符串的 RSA 解密字节。
     */
    fun decryptHexStringRSA(
        data: String?, privateKey: ByteArray?, keySize: Int, transformation: String
    ): ByteArray? {
        return decryptRSA(DawnBridge.hexString2Bytes(data), privateKey, keySize, transformation)
    }

    /**
     * 返回 RSA 解密的字节。
     *
     * @param data           data.
     * @param privateKey     私钥。
     * @param keySize        密钥的大小，例如1024、2048……
     * @param transformation 转换的名称，例如  *RSA/CBC/PKCS1Padding*.
     * @return RSA 解密的字节。
     */
    fun decryptRSA(
        data: ByteArray?, privateKey: ByteArray?, keySize: Int, transformation: String
    ): ByteArray? {
        return rsaTemplate(data, privateKey, keySize, transformation, false)
    }

    /**
     * 返回 RSA 加密或解密的字节。
     *
     * @param data           data.
     * @param key            key.
     * @param keySize        密钥的大小，例如1024、2048……
     * @param transformation 转换的名称，例如  *RSA/CBC/PKCS1Padding*.
     * @param isEncrypt      加密为真，否则为假
     * @return RSA 加密或解密的字节。
     */
    private fun rsaTemplate(
        data: ByteArray?, key: ByteArray?, keySize: Int, transformation: String, isEncrypt: Boolean
    ): ByteArray? {
        if (data == null || data.size == 0 || key == null || key.size == 0) {
            return null
        }
        try {
            val rsaKey: Key
            val keyFactory: KeyFactory
            keyFactory = if (Build.VERSION.SDK_INT < 28) {
                KeyFactory.getInstance("RSA", "BC")
            } else {
                KeyFactory.getInstance("RSA")
            }
            rsaKey = if (isEncrypt) {
                val keySpec = X509EncodedKeySpec(key)
                keyFactory.generatePublic(keySpec)
            } else {
                val keySpec = PKCS8EncodedKeySpec(key)
                keyFactory.generatePrivate(keySpec)
            }
            if (rsaKey == null) {
                return null
            }
            val cipher = Cipher.getInstance(transformation)
            cipher.init(if (isEncrypt) Cipher.ENCRYPT_MODE else Cipher.DECRYPT_MODE, rsaKey)
            val len = data.size
            var maxLen = keySize / 8
            if (isEncrypt) {
                val lowerTrans = transformation.lowercase(Locale.getDefault())
                if (lowerTrans.endsWith("pkcs1padding")) {
                    maxLen -= 11
                }
            }
            val count = len / maxLen
            return if (count > 0) {
                var ret = ByteArray(0)
                var buff = ByteArray(maxLen)
                var index = 0
                for (i in 0 until count) {
                    System.arraycopy(data, index, buff, 0, maxLen)
                    ret = joins(ret, cipher.doFinal(buff))
                    index += maxLen
                }
                if (index != len) {
                    val restLen = len - index
                    buff = ByteArray(restLen)
                    System.arraycopy(data, index, buff, 0, restLen)
                    ret = joins(ret, cipher.doFinal(buff))
                }
                ret
            } else {
                cipher.doFinal(data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 返回 RC4 加密解密的字节。
     *
     * @param data data.
     * @param key  key.
     */
    fun rc4(data: ByteArray?, key: ByteArray?): ByteArray? {
        if (data == null || data.size == 0 || key == null) {
            return null
        }
        require(!(key.size < 1 || key.size > 256)) { "key must be between 1 and 256 bytes" }
        val iS = ByteArray(256)
        val iK = ByteArray(256)
        val keyLen = key.size
        for (i in 0..255) {
            iS[i] = i.toByte()
            iK[i] = key[i % keyLen]
        }
        var j = 0
        var tmp: Byte
        for (i in 0..255) {
            j = j + iS[i] + iK[i] and 0xFF
            tmp = iS[j]
            iS[j] = iS[i]
            iS[i] = tmp
        }
        val ret = ByteArray(data.size)
        var i = 0
        var k: Int
        var t: Int
        for (counter in data.indices) {
            i = i + 1 and 0xFF
            j = j + iS[i] and 0xFF
            tmp = iS[j]
            iS[j] = iS[i]
            iS[i] = tmp
            t = iS[i] + iS[j] and 0xFF
            k = iS[t].toInt()
            ret[counter] = (data[counter].toInt() xor k).toByte()
        }
        return ret
    }

    private fun joins(prefix: ByteArray, suffix: ByteArray): ByteArray {
        val ret = ByteArray(prefix.size + suffix.size)
        System.arraycopy(prefix, 0, ret, 0, prefix.size)
        System.arraycopy(suffix, 0, ret, prefix.size, suffix.size)
        return ret
    }
}