package com.lzq.dawn.util.cache.disk;

import java.util.Locale;

/**
 * @Name :DiskCacheHelper
 * @Time :2022/7/18 14:31
 * @Author :  Lzq
 * @Desc : 磁盘缓存帮助类
 */
public final class DiskCacheHelper {

    static final int TIME_INFO_LEN = 14;

    static byte[] newByteArrayWithTime(final int second, final byte[] data) {
        byte[] time = createDueTime(second).getBytes();
        byte[] content = new byte[time.length + data.length];
        System.arraycopy(time, 0, content, 0, time.length);
        System.arraycopy(data, 0, content, time.length, data.length);
        return content;
    }

    /**
     * 返回到期时间字符串。
     *
     * @param seconds 秒
     * @return 到期时间字符串。
     */
    private static String createDueTime(final int seconds) {
        return String.format(
                Locale.getDefault(), "_$%010d$_",
                System.currentTimeMillis() / 1000 + seconds
        );
    }

    static boolean isDue(final byte[] data) {
        long millis = getDueTime(data);
        return millis != -1 && System.currentTimeMillis() > millis;
    }

    private static long getDueTime(final byte[] data) {
        if (hasTimeInfo(data)) {
            String millis = new String(copyOfRange(data, 2, 12));
            try {
                return Long.parseLong(millis) * 1000;
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    static byte[] getDataWithoutDueTime(final byte[] data) {
        if (hasTimeInfo(data)) {
            return copyOfRange(data, TIME_INFO_LEN, data.length);
        }
        return data;
    }

    private static byte[] copyOfRange(final byte[] original, final int from, final int to) {
        int newLength = to - from;
        if (newLength < 0) {
            throw new IllegalArgumentException(from + " > " + to);
        }
        byte[] copy = new byte[newLength];
        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
        return copy;
    }

    private static boolean hasTimeInfo(final byte[] data) {
        return data != null
                && data.length >= TIME_INFO_LEN
                && data[0] == '_'
                && data[1] == '$'
                && data[12] == '$'
                && data[13] == '_';
    }
}
