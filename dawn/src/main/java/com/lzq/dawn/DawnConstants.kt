package com.lzq.dawn

import androidx.annotation.IntDef

/**
 * @Name :DawnConstants
 * @Time :2022/7/20 14:34
 * @Author :  Lzq
 * @Desc :
 */
class DawnConstants {
    object MemoryConstants {
        const val BYTE = 1
        const val KB = 1024
        const val MB = 1048576
        const val GB = 1073741824

        @IntDef(BYTE, KB, MB, GB)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Unit
    }

    object TimeConstants {
        const val MSEC = 1
        const val SEC = 1000
        const val MIN = 60000
        const val HOUR = 3600000
        const val DAY = 86400000

        @IntDef(MSEC, SEC, MIN, HOUR, DAY)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Unit
    }
}