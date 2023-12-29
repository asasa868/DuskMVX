package com.lzq.dawn;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Name :DawnConstants
 * @Time :2022/7/20 14:34
 * @Author :  Lzq
 * @Desc :
 */
public class DawnConstants {
    public static final class MemoryConstants {

        public static final int BYTE = 1;
        public static final int KB = 1024;
        public static final int MB = 1048576;
        public static final int GB = 1073741824;

        @IntDef({BYTE, KB, MB, GB})
        @Retention(RetentionPolicy.SOURCE)
        public @interface Unit {
        }
    }

    public static final class TimeConstants {

        public static final int MSEC = 1;
        public static final int SEC = 1000;
        public static final int MIN = 60000;
        public static final int HOUR = 3600000;
        public static final int DAY = 86400000;

        @IntDef({MSEC, SEC, MIN, HOUR, DAY})
        @Retention(RetentionPolicy.SOURCE)
        public @interface Unit {
        }
    }
}
