package com.lzq.dawn.util.encode;

import android.os.Build;
import android.text.Html;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @Name :EncodeUtils
 * @Time :2022/7/21 10:31
 * @Author :  Lzq
 * @Desc : 编码
 */
public final class EncodeUtils {
    private EncodeUtils() {
    }

    /**
     * 返回 url 编码 字符串。
     *
     * @param input input.
     * @return url 编码 字符串。
     */
    public static String urlEncode(final String input) {
        return urlEncode(input, "UTF-8");
    }


    /**
     * 返回 url 编码 字符串。
     *
     * @param input       input.
     * @param charsetName 字符集的名称。
     * @return url 编码 字符串。
     */
    public static String urlEncode(final String input, final String charsetName) {
        if (input == null || input.length() == 0) {
            return "";
        }
        try {
            return URLEncoder.encode(input, charsetName);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * 返回解码 url encoded 字符串的字符串。
     *
     * @param input input.
     * @return 解码 url encoded 字符串的字符串。
     */
    public static String urlDecode(final String input) {
        return urlDecode(input, "UTF-8");
    }

    /**
     * 返回解码 url encoded 字符串的字符串。
     *
     * @param input       input.
     * @param charsetName 字符集的名称。
     * @return 解码 url encoded 字符串的字符串。
     */
    public static String urlDecode(final String input, final String charsetName) {
        if (input == null || input.length() == 0) {
            return "";
        }
        try {
            String safeInput = input.replaceAll("%(?![0-9a-fA-F]{2})", "%25").replaceAll("\\+", "%2B");
            return URLDecoder.decode(safeInput, charsetName);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * 返回 Base64 编码字节。
     *
     * @param input input.
     * @return Base64 编码字节。
     */
    public static byte[] base64Encode(final String input) {
        return base64Encode(input.getBytes());
    }

    /**
     * 返回 Base64 编码字节。
     *
     * @param input input.
     * @return Base64 编码字节。
     */
    public static byte[] base64Encode(final byte[] input) {
        if (input == null || input.length == 0) {
            return new byte[0];
        }
        return Base64.encode(input, Base64.NO_WRAP);
    }

    /**
     * 返回 Base64 编码字符串。
     *
     * @param input input.
     * @return Base64 编码字符串。
     */
    public static String base64Encode2String(final byte[] input) {
        if (input == null || input.length == 0) {
            return "";
        }
        return Base64.encodeToString(input, Base64.NO_WRAP);
    }

    /**
     * 返回解码 Base64 编码字符串的字节。
     *
     * @param input input.
     * @return 解码 Base64 编码字符串的字节。
     */
    public static byte[] base64Decode(final String input) {
        if (input == null || input.length() == 0) {
            return new byte[0];
        }
        return Base64.decode(input, Base64.NO_WRAP);
    }


    /**
     * 返回解码 Base64 编码字节的字节。
     *
     * @param input input.
     * @return 解码 Base64 编码字节的字节。
     */
    public static byte[] base64Decode(final byte[] input) {
        if (input == null || input.length == 0) {
            return new byte[0];
        }
        return Base64.decode(input, Base64.NO_WRAP);
    }

    /**
     * 返回 html 编码字符串。
     *
     * @param input input.
     * @return html 编码字符串。
     */
    public static String htmlEncode(final CharSequence input) {
        if (input == null || input.length() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0, len = input.length(); i < len; i++) {
            c = input.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;"); //$NON-NLS-1$
                    break;
                case '>':
                    sb.append("&gt;"); //$NON-NLS-1$
                    break;
                case '&':
                    sb.append("&amp;"); //$NON-NLS-1$
                    break;
                case '\'':
                    //http://www.w3.org/TR/xhtml1
                    // The named character reference &apos; (the apostrophe, U+0027) was
                    // introduced in XML 1.0 but does not appear in HTML. Authors should
                    // therefore use &#39; instead of &apos; to work as expected in HTML 4
                    // user agents.
                    sb.append("&#39;"); //$NON-NLS-1$
                    break;
                case '"':
                    sb.append("&quot;"); //$NON-NLS-1$
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 返回 解码 html-encode 字符串的字符串。
     *
     * @param input input.
     * @return 解码 html-编码 字符串的字符串。
     */
    public static CharSequence htmlDecode(final String input) {
        if (input == null || input.length() == 0) {
            return "";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(input);
        }
    }

    /**
     * 返回用一个空格填充的二进制编码字符串
     *
     * @param input input.
     * @return 二进制字符串
     */
    public static String binaryEncode(final String input) {
        if (input == null || input.length() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (char i : input.toCharArray()) {
            sb.append(Integer.toBinaryString(i)).append(" ");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    /**
     * 从二进制返回 UTF-8 字符串
     *
     * @param input 二进制字符串
     * @return UTF-8 字符串
     */
    public static String binaryDecode(final String input) {
        if (input == null || input.length() == 0) {
            return "";
        }
        String[] splits = input.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String split : splits) {
            sb.append(((char) Integer.parseInt(split, 2)));
        }
        return sb.toString();
    }
}
