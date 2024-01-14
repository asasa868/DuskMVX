package com.lzq.dawn.util.encrypt;

import android.os.Build;

import com.lzq.dawn.DawnBridge;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Name :EncryptUtils
 * @Time :2022/7/21 11:57
 * @Author :  Lzq
 * @Desc : 加密
 */
public final  class EncryptUtils {

    private EncryptUtils() {
    }

//-----------------------------------------------hash encryption-----------------------------------------------//

    /**
     * 返回 MD2 加密的十六进制字符串。
     *
     * @param data data.
     * @return MD2 加密的十六进制字符串。
     */
    public static String encryptMD2ToString(final String data) {
        if (data == null || data.length() == 0) {
            return "";
        }
        return encryptMD2ToString(data.getBytes());
    }

    /**
     * 返回 MD2 加密的十六进制字符串。
     *
     * @param data data.
     * @return MD2 加密的十六进制字符串。
     */
    public static String encryptMD2ToString(final byte[] data) {
        return DawnBridge.bytes2HexString(encryptMD2(data));
    }

    /**
     * 返回 MD2 加密的字节。
     *
     * @param data data.
     * @return MD2 加密的字节。
     */
    public static byte[] encryptMD2(final byte[] data) {
        return hashTemplate(data, "MD2");
    }

    /**
     * 返回 MD5 加密的十六进制字符串。
     *
     * @param data data.
     * @return MD5 加密的十六进制字符串。
     */
    public static String encryptMD5ToString(final String data) {
        if (data == null || data.length() == 0) {
            return "";
        }
        return encryptMD5ToString(data.getBytes());
    }

    /**
     * 返回 MD5 加密的十六进制字符串。
     *
     * @param data data.
     * @param salt salt.
     * @return MD5 加密的十六进制字符串。
     */
    public static String encryptMD5ToString(final String data, final String salt) {
        if (data == null && salt == null) {
            return "";
        }
        if (salt == null) {
            return DawnBridge.bytes2HexString(encryptMD5(data.getBytes()));
        }
        if (data == null) {
            return DawnBridge.bytes2HexString(encryptMD5(salt.getBytes()));
        }
        return DawnBridge.bytes2HexString(encryptMD5((data + salt).getBytes()));
    }


    /**
     * 返回 MD5 加密的十六进制字符串。
     *
     * @param data data.
     * @return MD5 加密的十六进制字符串。
     */
    public static String encryptMD5ToString(final byte[] data) {
        return DawnBridge.bytes2HexString(encryptMD5(data));
    }

    /**
     * Return the hex string of MD5 encryption.
     *
     * @param data The data.
     * @param salt The salt.
     * @return the hex string of MD5 encryption
     */
    public static String encryptMD5ToString(final byte[] data, final byte[] salt) {
        if (data == null && salt == null) {
            return "";
        }
        if (salt == null) {
            return DawnBridge.bytes2HexString(encryptMD5(data));
        }
        if (data == null) {
            return DawnBridge.bytes2HexString(encryptMD5(salt));
        }
        byte[] dataSalt = new byte[data.length + salt.length];
        System.arraycopy(data, 0, dataSalt, 0, data.length);
        System.arraycopy(salt, 0, dataSalt, data.length, salt.length);
        return DawnBridge.bytes2HexString(encryptMD5(dataSalt));
    }

    /**
     * 返回 MD5 加密的字节数。
     *
     * @param data data.
     * @return MD5 加密的字节数。
     */
    public static byte[] encryptMD5(final byte[] data) {
        return hashTemplate(data, "MD5");
    }


    /**
     * 返回文件的 MD5 加密的十六进制字符串。
     *
     * @param filePath 文件路径
     * @return 文件的 MD5 加密的十六进制字符串。
     */
    public static String encryptMD5File2String(final String filePath) {
        File file = DawnBridge.isSpace(filePath) ? null : new File(filePath);
        return encryptMD5File2String(file);
    }

    /**
     * 返回文件的 MD5 加密字节数。
     *
     * @param filePath 文件路径
     * @return 文件的 MD5 加密字节数。
     */
    public static byte[] encryptMD5File(final String filePath) {
        File file = DawnBridge.isSpace(filePath) ? null : new File(filePath);
        return encryptMD5File(file);
    }


    /**
     * 返回文件的 MD5 加密的十六进制字符串
     *
     * @param file file.
     * @return 文件的 MD5 加密的十六进制字符串
     */
    public static String encryptMD5File2String(final File file) {
        return DawnBridge.bytes2HexString(encryptMD5File(file));
    }

    /**
     * 返回文件的 MD5 加密字节数。
     *
     * @param file file.
     * @return 文件的 MD5 加密字节数。
     */
    public static byte[] encryptMD5File(final File file) {
        if (file == null) {
            return null;
        }
        FileInputStream fis = null;
        DigestInputStream digestInputStream;
        try {
            fis = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("MD5");
            digestInputStream = new DigestInputStream(fis, md);
            byte[] buffer = new byte[256 * 1024];
            while (true) {
                if (!(digestInputStream.read(buffer) > 0)) {
                    break;
                }
            }
            md = digestInputStream.getMessageDigest();
            return md.digest();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 返回 SHA1 加密的十六进制字符串。
     *
     * @param data data.
     * @return SHA1 加密的十六进制字符串
     */
    public static String encryptSHA1ToString(final String data) {
        if (data == null || data.length() == 0) {
            return "";
        }
        return encryptSHA1ToString(data.getBytes());
    }

    /**
     * 返回 SHA1 加密的十六进制字符串。
     *
     * @param data data.
     * @return SHA1 加密的十六进制字符串。
     */
    public static String encryptSHA1ToString(final byte[] data) {
        return DawnBridge.bytes2HexString(encryptSHA1(data));
    }

    /**
     * 返回 SHA1 加密的字节。
     *
     * @param data data.
     * @return SHA1 加密的字节。
     */
    public static byte[] encryptSHA1(final byte[] data) {
        return hashTemplate(data, "SHA-1");
    }

    /**
     * 返回 SHA224 加密的十六进制字符串。
     *
     * @param data data.
     * @return SHA224 加密的十六进制字符串。
     */
    public static String encryptSHA224ToString(final String data) {
        if (data == null || data.length() == 0) {
            return "";
        }
        return encryptSHA224ToString(data.getBytes());
    }


    /**
     * 返回 SHA224 加密的十六进制字符串。
     *
     * @param data data.
     * @return SHA224 加密的十六进制字符串。
     */
    public static String encryptSHA224ToString(final byte[] data) {
        return DawnBridge.bytes2HexString(encryptSHA224(data));
    }

    /**
     * 返回 SHA224 加密的字节数。
     *
     * @param data data.
     * @return SHA224 加密的字节数。
     */
    public static byte[] encryptSHA224(final byte[] data) {
        return hashTemplate(data, "SHA224");
    }

    /**
     * 返回 SHA256 加密的十六进制字符串。
     *
     * @param data data.
     * @return SHA256 加密的十六进制字符串。
     */
    public static String encryptSHA256ToString(final String data) {
        if (data == null || data.length() == 0) {
            return "";
        }
        return encryptSHA256ToString(data.getBytes());
    }

    /**
     * 返回 SHA256 加密的十六进制字符串。
     *
     * @param data data.
     * @return SHA256 加密的十六进制字符串。
     */
    public static String encryptSHA256ToString(final byte[] data) {
        return DawnBridge.bytes2HexString(encryptSHA256(data));
    }

    /**
     * 返回 SHA256 加密的字节。
     *
     * @param data data.
     * @return SHA256 加密的字节。
     */
    public static byte[] encryptSHA256(final byte[] data) {
        return hashTemplate(data, "SHA-256");
    }


    /**
     * 返回 SHA384 加密的十六进制字符串。
     *
     * @param data data.
     * @return SHA384 加密的十六进制字符串。
     */
    public static String encryptSHA384ToString(final String data) {
        if (data == null || data.length() == 0) {
            return "";
        }
        return encryptSHA384ToString(data.getBytes());
    }

    /**
     * 返回 SHA384 加密的十六进制字符串。
     *
     * @param data data.
     * @return SHA384 加密的十六进制字符串。
     */
    public static String encryptSHA384ToString(final byte[] data) {
        return DawnBridge.bytes2HexString(encryptSHA384(data));
    }

    /**
     * 返回 SHA384 加密的字节。
     *
     * @param data data.
     * @return SHA384 加密的字节。
     */
    public static byte[] encryptSHA384(final byte[] data) {
        return hashTemplate(data, "SHA-384");
    }

    /**
     * 返回 SHA512 加密的十六进制字符串。
     *
     * @param data data.
     * @return SHA512 加密的十六进制字符串。
     */
    public static String encryptSHA512ToString(final String data) {
        if (data == null || data.length() == 0) {
            return "";
        }
        return encryptSHA512ToString(data.getBytes());
    }

    /**
     * 返回 SHA512 加密的十六进制字符串
     *
     * @param data data.
     * @return SHA512 加密的十六进制字符串
     */
    public static String encryptSHA512ToString(final byte[] data) {
        return DawnBridge.bytes2HexString(encryptSHA512(data));
    }

    /**
     * 返回 SHA512 加密的字节数。
     *
     * @param data data.
     * @return SHA512 加密的字节数。
     */
    public static byte[] encryptSHA512(final byte[] data) {
        return hashTemplate(data, "SHA-512");
    }

    /**
     * 返回哈希加密的字节。
     *
     * @param data      data.
     * @param algorithm 哈希加密的名称。
     * @return 哈希加密的字节。
     */
    public static byte[] hashTemplate(final byte[] data, final String algorithm) {
        if (data == null || data.length <= 0) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
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
    public static String encryptHmacMD5ToString(final String data, final String key) {
        if (data == null || data.length() == 0 || key == null || key.length() == 0) {
            return "";
        }
        return encryptHmacMD5ToString(data.getBytes(), key.getBytes());
    }

    /**
     * 返回 HmacMD5 加密的十六进制字符串。
     *
     * @param data data.
     * @param key  key.
     * @return HmacMD5加密的十六进制字符串
     */
    public static String encryptHmacMD5ToString(final byte[] data, final byte[] key) {
        return DawnBridge.bytes2HexString(encryptHmacMD5(data, key));
    }

    /**
     * 返回 HmacMD5 加密的字节。
     *
     * @param data data.
     * @param key  key.
     * @return HmacMD5 加密的字节。
     */
    public static byte[] encryptHmacMD5(final byte[] data, final byte[] key) {
        return hmacTemplate(data, key, "HmacMD5");
    }

    /**
     * 返回 HmacSHA1 加密的十六进制字符串。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA1 加密的十六进制字符串。
     */
    public static String encryptHmacSHA1ToString(final String data, final String key) {
        if (data == null || data.length() == 0 || key == null || key.length() == 0) {
            return "";
        }
        return encryptHmacSHA1ToString(data.getBytes(), key.getBytes());
    }

    /**
     * 返回 HmacSHA1 加密的十六进制字符串。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA1 加密的十六进制字符串。
     */
    public static String encryptHmacSHA1ToString(final byte[] data, final byte[] key) {
        return DawnBridge.bytes2HexString(encryptHmacSHA1(data, key));
    }

    /**
     * 返回 Hmac SHA1 加密的字节。
     *
     * @param data data.
     * @param key  key.
     * @return Hmac SHA1 加密的字节。
     */
    public static byte[] encryptHmacSHA1(final byte[] data, final byte[] key) {
        return hmacTemplate(data, key, "HmacSHA1");
    }

    /**
     * 返回 HmacSHA224 加密的十六进制字符串。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA224 加密的十六进制字符串。
     */
    public static String encryptHmacSHA224ToString(final String data, final String key) {
        if (data == null || data.length() == 0 || key == null || key.length() == 0) {
            return "";
        }
        return encryptHmacSHA224ToString(data.getBytes(), key.getBytes());
    }

    /**
     * 返回 HmacSHA224 加密的十六进制字符串。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA224 加密的十六进制字符串。
     */
    public static String encryptHmacSHA224ToString(final byte[] data, final byte[] key) {
        return DawnBridge.bytes2HexString(encryptHmacSHA224(data, key));
    }

    /**
     * 返回 HmacSHA224 加密的字节。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA224 加密的字节。
     */
    public static byte[] encryptHmacSHA224(final byte[] data, final byte[] key) {
        return hmacTemplate(data, key, "HmacSHA224");
    }

    /**
     * 返回 HmacSHA256 加密的十六进制字符串。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA256 加密的十六进制字符串。
     */
    public static String encryptHmacSHA256ToString(final String data, final String key) {
        if (data == null || data.length() == 0 || key == null || key.length() == 0) {
            return "";
        }
        return encryptHmacSHA256ToString(data.getBytes(), key.getBytes());
    }

    /**
     * 返回 HmacSHA256 加密的十六进制字符串。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA256 加密的十六进制字符串。
     */
    public static String encryptHmacSHA256ToString(final byte[] data, final byte[] key) {
        return DawnBridge.bytes2HexString(encryptHmacSHA256(data, key));
    }

    /**
     * 返回 HmacSHA256 加密的字节。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA256 加密的字节。
     */
    public static byte[] encryptHmacSHA256(final byte[] data, final byte[] key) {
        return hmacTemplate(data, key, "HmacSHA256");
    }

    /**
     * 返回 HmacSHA384 加密的十六进制字符串。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA384加密的十六进制字符串
     */
    public static String encryptHmacSHA384ToString(final String data, final String key) {
        if (data == null || data.length() == 0 || key == null || key.length() == 0) {
            return "";
        }
        return encryptHmacSHA384ToString(data.getBytes(), key.getBytes());
    }

    /**
     * 返回 HmacSHA384 加密的十六进制字符串。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA384 加密的十六进制字符串。
     */
    public static String encryptHmacSHA384ToString(final byte[] data, final byte[] key) {
        return DawnBridge.bytes2HexString(encryptHmacSHA384(data, key));
    }

    /**
     * 返回 HmacSHA384 加密的字节。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA384 加密的字节。
     */
    public static byte[] encryptHmacSHA384(final byte[] data, final byte[] key) {
        return hmacTemplate(data, key, "HmacSHA384");
    }

    /**
     * 返回 HmacSHA512 加密的十六进制字符串。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA512 加密的十六进制字符串。
     */
    public static String encryptHmacSHA512ToString(final String data, final String key) {
        if (data == null || data.length() == 0 || key == null || key.length() == 0) {
            return "";
        }
        return encryptHmacSHA512ToString(data.getBytes(), key.getBytes());
    }

    /**
     * 返回 HmacSHA512 加密的十六进制字符串。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA512 加密的十六进制字符串。
     */
    public static String encryptHmacSHA512ToString(final byte[] data, final byte[] key) {
        return DawnBridge.bytes2HexString(encryptHmacSHA512(data, key));
    }

    /**
     * 返回 HmacSHA512 加密的字节。
     *
     * @param data data.
     * @param key  key.
     * @return HmacSHA512 加密的字节。
     */
    public static byte[] encryptHmacSHA512(final byte[] data, final byte[] key) {
        return hmacTemplate(data, key, "HmacSHA512");
    }

    /**
     * 返回 hmac 加密的字节数。
     *
     * @param data      data.
     * @param key       key.
     * @param algorithm hmac 加密的名称。
     * @return hmac 加密的字节数。
     */
    private static byte[] hmacTemplate(final byte[] data,
                                       final byte[] key,
                                       final String algorithm) {
        if (data == null || data.length == 0 || key == null || key.length == 0) {
            return null;
        }
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key, algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(secretKey);
            return mac.doFinal(data);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
//-----------------------------------------------DES encryption-----------------------------------------------//


    /**
     * 返回 DES 加密的 Base64 编码字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  <i>DES/CBC/PKCS5Padding</i>.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return DES 加密的 Base64 编码字节。
     */
    public static byte[] encryptDES2Base64(final byte[] data,
                                           final byte[] key,
                                           final String transformation,
                                           final byte[] iv) {
        return DawnBridge.base64Encode(encryptDES(data, key, transformation, iv));
    }

    /**
     * 返回 DES 加密的十六进制字符串。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  <i>DES/CBC/PKCS5Padding</i>.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return DES 加密的十六进制字符串。
     */
    public static String encryptDES2HexString(final byte[] data,
                                              final byte[] key,
                                              final String transformation,
                                              final byte[] iv) {
        return DawnBridge.bytes2HexString(encryptDES(data, key, transformation, iv));
    }

    /**
     * 返回 DES 加密的字节
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  <i>DES/CBC/PKCS5Padding</i>.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return DES 加密的字节
     */
    public static byte[] encryptDES(final byte[] data,
                                    final byte[] key,
                                    final String transformation,
                                    final byte[] iv) {
        return symmetricTemplate(data, key, "DES", transformation, iv, true);
    }

    /**
     * 返回 Base64 编码字节的 DES 解密字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  <i>DES/CBC/PKCS5Padding</i>.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return Base64 编码字节的 DES 解密字节。
     */
    public static byte[] decryptBase64DES(final byte[] data,
                                          final byte[] key,
                                          final String transformation,
                                          final byte[] iv) {
        return decryptDES(DawnBridge.base64Decode(data), key, transformation, iv);
    }

    /**
     * 返回十六进制字符串的 DES 解密字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  <i>DES/CBC/PKCS5Padding</i>.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return 十六进制字符串的 DES 解密字节。
     */
    public static byte[] decryptHexStringDES(final String data,
                                             final byte[] key,
                                             final String transformation,
                                             final byte[] iv) {
        return decryptDES(DawnBridge.hexString2Bytes(data), key, transformation, iv);
    }

    /**
     * 返回 DES 解密的字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  <i>DES/CBC/PKCS5Padding</i>.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return DES 解密的字节。
     */
    public static byte[] decryptDES(final byte[] data,
                                    final byte[] key,
                                    final String transformation,
                                    final byte[] iv) {
        return symmetricTemplate(data, key, "DES", transformation, iv, false);
    }


//-----------------------------------------------3DES encryption-----------------------------------------------//


    /**
     * 返回 3DES 加密的 Base64 编码字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  <i>DES/CBC/PKCS5Padding</i>.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return 3DES 加密的 Base64 编码字节。
     */
    public static byte[] encrypt3DES2Base64(final byte[] data,
                                            final byte[] key,
                                            final String transformation,
                                            final byte[] iv) {
        return DawnBridge.base64Encode(encrypt3DES(data, key, transformation, iv));
    }

    /**
     * 返回 3DES 加密的十六进制字符串。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  <i>DES/CBC/PKCS5Padding</i>.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return 3DES 加密的十六进制字符串。
     */
    public static String encrypt3DES2HexString(final byte[] data,
                                               final byte[] key,
                                               final String transformation,
                                               final byte[] iv) {
        return DawnBridge.bytes2HexString(encrypt3DES(data, key, transformation, iv));
    }

    /**
     * 返回 3DES 加密的字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  <i>DES/CBC/PKCS5Padding</i>.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return 3DES 加密的字节。
     */
    public static byte[] encrypt3DES(final byte[] data,
                                     final byte[] key,
                                     final String transformation,
                                     final byte[] iv) {
        return symmetricTemplate(data, key, "DESede", transformation, iv, true);
    }

    /**
     * 返回 Base64 编码字节的 3DES 解密字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  <i>DES/CBC/PKCS5Padding</i>.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return Base64 编码字节的 3DES 解密字节。
     */
    public static byte[] decryptBase64_3DES(final byte[] data,
                                            final byte[] key,
                                            final String transformation,
                                            final byte[] iv) {
        return decrypt3DES(DawnBridge.base64Decode(data), key, transformation, iv);
    }

    /**
     * 返回十六进制字符串的 3DES 解密字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  <i>DES/CBC/PKCS5Padding</i>.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return 十六进制字符串的 3DES 解密字节。
     */
    public static byte[] decryptHexString3DES(final String data,
                                              final byte[] key,
                                              final String transformation,
                                              final byte[] iv) {
        return decrypt3DES(DawnBridge.hexString2Bytes(data), key, transformation, iv);
    }

    /**
     * 返回 3DES 解密的字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  <i>DES/CBC/PKCS5Padding</i>.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return 3DES 解密的字节。
     */
    public static byte[] decrypt3DES(final byte[] data,
                                     final byte[] key,
                                     final String transformation,
                                     final byte[] iv) {
        return symmetricTemplate(data, key, "DESede", transformation, iv, false);
    }
//-----------------------------------------------AES encryption-----------------------------------------------//


    /**
     * 返回 AES 加密的 Base64 编码字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  <i>DES/CBC/PKCS5Padding</i>.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return AES 加密的 Base64 编码字节。
     */
    public static byte[] encryptAES2Base64(final byte[] data,
                                           final byte[] key,
                                           final String transformation,
                                           final byte[] iv) {
        return DawnBridge.base64Encode(encryptAES(data, key, transformation, iv));
    }

    /**
     * 返回 AES 加密的十六进制字符串。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  <i>DES/CBC/PKCS5Padding</i>.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return AES 加密的十六进制字符串。
     */
    public static String encryptAES2HexString(final byte[] data,
                                              final byte[] key,
                                              final String transformation,
                                              final byte[] iv) {
        return DawnBridge.bytes2HexString(encryptAES(data, key, transformation, iv));
    }

    /**
     * 返回 AES 加密的字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  <i>DES/CBC/PKCS5Padding</i>.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return AES 加密的字节。
     */
    public static byte[] encryptAES(final byte[] data,
                                    final byte[] key,
                                    final String transformation,
                                    final byte[] iv) {
        return symmetricTemplate(data, key, "AES", transformation, iv, true);
    }

    /**
     * 返回 Base64 编码字节的 AES 解密字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  <i>DES/CBC/PKCS5Padding</i>.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return Base64 编码字节的 AES 解密字节。
     */
    public static byte[] decryptBase64AES(final byte[] data,
                                          final byte[] key,
                                          final String transformation,
                                          final byte[] iv) {
        return decryptAES(DawnBridge.base64Decode(data), key, transformation, iv);
    }

    /**
     * 返回十六进制字符串的 AES 解密字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  <i>DES/CBC/PKCS5Padding</i>.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return 十六进制字符串的 AES 解密字节。
     */
    public static byte[] decryptHexStringAES(final String data,
                                             final byte[] key,
                                             final String transformation,
                                             final byte[] iv) {
        return decryptAES(DawnBridge.hexString2Bytes(data), key, transformation, iv);
    }

    /**
     * 返回 AES 解密的字节。
     *
     * @param data           data.
     * @param key            key.
     * @param transformation 转换的名称，例如  <i>DES/CBC/PKCS5Padding</i>.
     * @param iv             带有 IV 的缓冲区。复制缓冲区的内容以防止后续修改。
     * @return AES 解密的字节。
     */
    public static byte[] decryptAES(final byte[] data,
                                    final byte[] key,
                                    final String transformation,
                                    final byte[] iv) {
        return symmetricTemplate(data, key, "AES", transformation, iv, false);
    }

    /**
     * 返回对称加密或解密的字节。
     *
     * @param data           data.
     * @param key            key.
     * @param algorithm      算法的名称。
     * @param transformation 转换的名称，例如  <i>DES/CBC/PKCS5Padding</i>.
     * @param isEncrypt      加密为真，否则为假。
     * @return 对称加密或解密的字节。
     */
    private static byte[] symmetricTemplate(final byte[] data,
                                            final byte[] key,
                                            final String algorithm,
                                            final String transformation,
                                            final byte[] iv,
                                            final boolean isEncrypt) {
        if (data == null || data.length == 0 || key == null || key.length == 0) {
            return null;
        }
        try {
            SecretKey secretKey;
            if ("DES".equals(algorithm)) {
                DESKeySpec desKey = new DESKeySpec(key);
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
                secretKey = keyFactory.generateSecret(desKey);
            } else {
                secretKey = new SecretKeySpec(key, algorithm);
            }
            Cipher cipher = Cipher.getInstance(transformation);
            if (iv == null || iv.length == 0) {
                cipher.init(isEncrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, secretKey);
            } else {
                AlgorithmParameterSpec params = new IvParameterSpec(iv);
                cipher.init(isEncrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, secretKey, params);
            }
            return cipher.doFinal(data);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }


//-----------------------------------------------RSA encryption-----------------------------------------------//

    /**
     * 返回 RSA 加密的 Base64 编码字节
     *
     * @param data           data.
     * @param publicKey      公钥。
     * @param keySize        密钥的大小，例如1024、2048……
     * @param transformation 转换的名称，例如  <i>RSA/CBC/PKCS1Padding</i>.
     * @return RSA 加密的 Base64 编码字节
     */
    public static byte[] encryptRSA2Base64(final byte[] data,
                                           final byte[] publicKey,
                                           final int keySize,
                                           final String transformation) {
        return DawnBridge.base64Encode(encryptRSA(data, publicKey, keySize, transformation));
    }

    /**
     * 返回 RSA 加密的十六进制字符串。
     *
     * @param data           data.
     * @param publicKey      公钥。
     * @param keySize        密钥的大小，例如1024、2048……
     * @param transformation 转换的名称，例如  <i>RSA/CBC/PKCS1Padding</i>.
     * @return RSA 加密的十六进制字符串。
     */
    public static String encryptRSA2HexString(final byte[] data,
                                              final byte[] publicKey,
                                              final int keySize,
                                              final String transformation) {
        return DawnBridge.bytes2HexString(encryptRSA(data, publicKey, keySize, transformation));
    }

    /**
     * 返回 RSA 加密的字节。
     *
     * @param data           data.
     * @param publicKey      公钥。
     * @param keySize        密钥的大小，例如1024、2048……
     * @param transformation 转换的名称，例如  <i>RSA/CBC/PKCS1Padding</i>.
     * @return RSA 加密的字节。
     */
    public static byte[] encryptRSA(final byte[] data,
                                    final byte[] publicKey,
                                    final int keySize,
                                    final String transformation) {
        return rsaTemplate(data, publicKey, keySize, transformation, true);
    }

    /**
     * 返回 Base64 编码字节的 RSA 解密字节。
     *
     * @param data           data.
     * @param privateKey     私钥。
     * @param keySize        密钥的大小，例如1024、2048……
     * @param transformation 转换的名称，例如  <i>RSA/CBC/PKCS1Padding</i>.
     * @return Base64 编码字节的 RSA 解密字节。
     */
    public static byte[] decryptBase64RSA(final byte[] data,
                                          final byte[] privateKey,
                                          final int keySize,
                                          final String transformation) {
        return decryptRSA(DawnBridge.base64Decode(data), privateKey, keySize, transformation);
    }

    /**
     * 返回十六进制字符串的 RSA 解密字节。
     *
     * @param data           data.
     * @param privateKey     私钥。
     * @param keySize        密钥的大小，例如1024、2048……
     * @param transformation 转换的名称，例如  <i>RSA/CBC/PKCS1Padding</i>.
     * @return 十六进制字符串的 RSA 解密字节。
     */
    public static byte[] decryptHexStringRSA(final String data,
                                             final byte[] privateKey,
                                             final int keySize,
                                             final String transformation) {
        return decryptRSA(DawnBridge.hexString2Bytes(data), privateKey, keySize, transformation);
    }

    /**
     * 返回 RSA 解密的字节。
     *
     * @param data           data.
     * @param privateKey     私钥。
     * @param keySize        密钥的大小，例如1024、2048……
     * @param transformation 转换的名称，例如  <i>RSA/CBC/PKCS1Padding</i>.
     * @return RSA 解密的字节。
     */
    public static byte[] decryptRSA(final byte[] data,
                                    final byte[] privateKey,
                                    final int keySize,
                                    final String transformation) {
        return rsaTemplate(data, privateKey, keySize, transformation, false);
    }

    /**
     * 返回 RSA 加密或解密的字节。
     *
     * @param data           data.
     * @param key            key.
     * @param keySize        密钥的大小，例如1024、2048……
     * @param transformation 转换的名称，例如  <i>RSA/CBC/PKCS1Padding</i>.
     * @param isEncrypt      加密为真，否则为假
     * @return RSA 加密或解密的字节。
     */
    private static byte[] rsaTemplate(final byte[] data,
                                      final byte[] key,
                                      final int keySize,
                                      final String transformation,
                                      final boolean isEncrypt) {
        if (data == null || data.length == 0 || key == null || key.length == 0) {
            return null;
        }
        try {
            Key rsaKey;
            KeyFactory keyFactory;
            if (Build.VERSION.SDK_INT < 28) {
                keyFactory = KeyFactory.getInstance("RSA", "BC");
            } else {
                keyFactory = KeyFactory.getInstance("RSA");
            }
            if (isEncrypt) {
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
                rsaKey = keyFactory.generatePublic(keySpec);
            } else {
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
                rsaKey = keyFactory.generatePrivate(keySpec);
            }
            if (rsaKey == null) {
                return null;
            }
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(isEncrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, rsaKey);
            int len = data.length;
            int maxLen = keySize / 8;
            if (isEncrypt) {
                String lowerTrans = transformation.toLowerCase();
                if (lowerTrans.endsWith("pkcs1padding")) {
                    maxLen -= 11;
                }
            }
            int count = len / maxLen;
            if (count > 0) {
                byte[] ret = new byte[0];
                byte[] buff = new byte[maxLen];
                int index = 0;
                for (int i = 0; i < count; i++) {
                    System.arraycopy(data, index, buff, 0, maxLen);
                    ret = joins(ret, cipher.doFinal(buff));
                    index += maxLen;
                }
                if (index != len) {
                    int restLen = len - index;
                    buff = new byte[restLen];
                    System.arraycopy(data, index, buff, 0, restLen);
                    ret = joins(ret, cipher.doFinal(buff));
                }
                return ret;
            } else {
                return cipher.doFinal(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回 RC4 加密解密的字节。
     *
     * @param data data.
     * @param key  key.
     */
    public static byte[] rc4(byte[] data, byte[] key) {
        if (data == null || data.length == 0 || key == null) {
            return null;
        }
        if (key.length < 1 || key.length > 256) {
            throw new IllegalArgumentException("key must be between 1 and 256 bytes");
        }
        final byte[] iS = new byte[256];
        final byte[] iK = new byte[256];
        int keyLen = key.length;
        for (int i = 0; i < 256; i++) {
            iS[i] = (byte) i;
            iK[i] = key[i % keyLen];
        }
        int j = 0;
        byte tmp;
        for (int i = 0; i < 256; i++) {
            j = (j + iS[i] + iK[i]) & 0xFF;
            tmp = iS[j];
            iS[j] = iS[i];
            iS[i] = tmp;
        }

        final byte[] ret = new byte[data.length];
        int i = 0, k, t;
        for (int counter = 0; counter < data.length; counter++) {
            i = (i + 1) & 0xFF;
            j = (j + iS[i]) & 0xFF;
            tmp = iS[j];
            iS[j] = iS[i];
            iS[i] = tmp;
            t = (iS[i] + iS[j]) & 0xFF;
            k = iS[t];
            ret[counter] = (byte) (data[counter] ^ k);
        }
        return ret;
    }

    private static byte[] joins(final byte[] prefix, final byte[] suffix) {
        byte[] ret = new byte[prefix.length + suffix.length];
        System.arraycopy(prefix, 0, ret, 0, prefix.length);
        System.arraycopy(suffix, 0, ret, prefix.length, suffix.length);
        return ret;
    }
}
