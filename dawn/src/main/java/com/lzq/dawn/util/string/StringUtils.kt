package com.lzq.dawn.util.string;

import android.content.res.Resources;

import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.lzq.dawn.DawnBridge;

import java.util.IllegalFormatException;

/**
 * @Name :StringUtils
 * @Time :2022/8/31 14:45
 * @Author :  Lzq
 * @Desc : string
 */
public final class StringUtils {


    private StringUtils() {

    }

    /**
     * 返回字符串是null还是0长度。
     *
     * @param s string.
     * @return {@code true}: yes<br> {@code false}: no
     */
    public static boolean isEmpty(final CharSequence s) {
        return s == null || s.length() == 0;
    }

    /**
     * 返回字符串是空还是空白。
     *
     * @param s string.
     * @return {@code true}: yes<br> {@code false}: no
     */
    public static boolean isTrimEmpty(final String s) {
        return (s == null || s.trim().length() == 0);
    }

    /**
     * 返回字符串是空还是空白
     *
     * @param s string.
     * @return {@code true}: yes<br> {@code false}: no
     */
    public static boolean isSpace(final String s) {
        if (s == null) {
            return true;
        }
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 返回string1是否等于string2。
     *
     * @param s1 string.
     * @param s2 string.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean equals(final CharSequence s1, final CharSequence s2) {
        if (s1 == s2) {
            return true;
        }
        int length;
        if (s1 != null && s2 != null && (length = s1.length()) == s2.length()) {
            if (s1 instanceof String && s2 instanceof String) {
                return s1.equals(s2);
            } else {
                for (int i = 0; i < length; i++) {
                    if (s1.charAt(i) != s2.charAt(i)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 返回string1是否等于string2，忽略大小写考虑。。
     *
     * @param s1 string.
     * @param s2 string.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean equalsIgnoreCase(final String s1, final String s2) {
        return s1 == null ? s2 == null : s1.equalsIgnoreCase(s2);
    }

    /**
     * Return {@code ""} 如果字符串等于null
     *
     * @param s string.
     * @return {@code ""} 如果字符串等于null
     */
    public static String null2Length0(final String s) {
        return s == null ? "" : s;
    }

    /**
     *返回字符串的长度。
     *
     * @param s  string.
     * @return 字符串的长度。
     */
    public static int length(final CharSequence s) {
        return s == null ? 0 : s.length();
    }

    /**
     * 将字符串的第一个字母设置为大写。
     *
     * @param s  string.
     * @return 第一个字母大写的字符串
     */
    public static String upperFirstLetter(final String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        if (!Character.isLowerCase(s.charAt(0))) {
            return s;
        }
        return (char) (s.charAt(0) - 32) + s.substring(1);
    }

    /**
     * 将字符串的第一个字母设置小写。
     *
     * @param s  string.
     * @return 第一个字母小写的字符串。
     */
    public static String lowerFirstLetter(final String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        if (!Character.isUpperCase(s.charAt(0))) {
            return s;
        }
        return String.valueOf((char) (s.charAt(0) + 32)) + s.substring(1);
    }

    /**
     * 反转字符串
     *
     * @param s  string.
     * @return 反转字符串
     */
    public static String reverse(final String s) {
        if (s == null) {
            return "";
        }
        int len = s.length();
        if (len <= 1) {
            return s;
        }
        int mid = len >> 1;
        char[] chars = s.toCharArray();
        char c;
        for (int i = 0; i < mid; ++i) {
            c = chars[i];
            chars[i] = chars[len - i - 1];
            chars[len - i - 1] = c;
        }
        return new String(chars);
    }

    /**
     * 将字符串转换为DBC。
     *
     * @param s  string.
     * @return DBC字符串
     */
    public static String toDBC(final String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char[] chars = s.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == 12288) {
                chars[i] = ' ';
            } else if (65281 <= chars[i] && chars[i] <= 65374) {
                chars[i] = (char) (chars[i] - 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    /**
     * 将字符串转换为SBC。
     *
     * @param s  string.
     * @return SBC字符串
     */
    public static String toSBC(final String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char[] chars = s.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == ' ') {
                chars[i] = (char) 12288;
            } else if (33 <= chars[i] && chars[i] <= 126) {
                chars[i] = (char) (chars[i] + 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    /**
     * 返回与特定资源ID关联的字符串值。
     *
     * @param id  resource id
     * @return 与特定资源ID关联的字符串值。
     */
    public static String getString(@StringRes int id) {
        return getString(id, (Object[]) null);
    }

    /**
     * 返回与特定资源ID关联的字符串值。
     *
     * @param id       resource id
     * @param formatArgs 将用于替换的格式参数。
     * @return 与特定资源ID关联的字符串值
     */
    public static String getString(@StringRes int id, Object... formatArgs) {
        try {
            return format(DawnBridge.getApp().getString(id), formatArgs);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            return String.valueOf(id);
        }
    }

    /**
     * 返回与特定资源ID关联的字符串数组。
     *
     * @param id   resource id
     * @return 与资源关联的字符串数组。
     */
    public static String[] getStringArray(@ArrayRes int id) {
        try {
            return DawnBridge.getApp().getResources().getStringArray(id);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            return new String[]{String.valueOf(id)};
        }
    }

    /**
     *格式化字符串。
     *
     * @param str   string.
     * @param args  args.
     * @return  格式化字符串。
     */
    public static String format(@Nullable String str, Object... args) {
        String text = str;
        if (text != null) {
            if (args != null && args.length > 0) {
                try {
                    text = String.format(str, args);
                } catch (IllegalFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return text;
    }
}
