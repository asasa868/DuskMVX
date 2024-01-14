package com.lzq.dawn.util.time

import androidx.annotation.IntDef

/**
 * @Name :TimeConstants
 * @Time :2022/8/31 15:43
 * @Author :  Lzq
 * @Desc :
 */
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