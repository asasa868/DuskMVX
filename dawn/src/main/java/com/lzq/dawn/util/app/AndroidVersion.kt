package com.lzq.dawn.util.app

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast


/**
 * className :AndoridVersion
 * createTime :2022/5/16 16:52
 * @Author :  Lzq
 */
object AndroidVersion {

    const val ANDROID_14: Int = Build.VERSION_CODES.UPSIDE_DOWN_CAKE
    const val ANDROID_13: Int = Build.VERSION_CODES.TIRAMISU
    const val ANDROID_12_L: Int = Build.VERSION_CODES.S_V2
    const val ANDROID_12 = Build.VERSION_CODES.S
    const val ANDROID_11 = Build.VERSION_CODES.R
    const val ANDROID_10 = Build.VERSION_CODES.Q
    const val ANDROID_9 = Build.VERSION_CODES.P
    const val ANDROID_8_1 = Build.VERSION_CODES.O_MR1
    const val ANDROID_8 = Build.VERSION_CODES.O
    const val ANDROID_7_1 = Build.VERSION_CODES.N_MR1
    const val ANDROID_7 = Build.VERSION_CODES.N
    const val ANDROID_6 = Build.VERSION_CODES.M
    const val ANDROID_5_1 = Build.VERSION_CODES.LOLLIPOP_MR1
    const val ANDROID_5 = Build.VERSION_CODES.LOLLIPOP
    const val ANDROID_4_4 = Build.VERSION_CODES.KITKAT
    const val ANDROID_4_3 = Build.VERSION_CODES.JELLY_BEAN_MR2
    const val ANDROID_4_2 = Build.VERSION_CODES.JELLY_BEAN_MR1
    const val ANDROID_4_1 = Build.VERSION_CODES.JELLY_BEAN


    /**
     * 是否是 Android 14 及以上版本
     */
    @ChecksSdkIntAtLeast(api = ANDROID_14)
    fun isAndroid14(): Boolean {
        return Build.VERSION.SDK_INT >= ANDROID_14
    }

    /**
     * 是否是 Android 13 及以上版本
     */
    @ChecksSdkIntAtLeast(api = ANDROID_13)
    fun isAndroid13(): Boolean {
        return Build.VERSION.SDK_INT >= ANDROID_13
    }

    /**
     * 是否是 Android 12 及以上版本
     */
    @ChecksSdkIntAtLeast(api = ANDROID_12)
    fun isAndroid12(): Boolean {
        return Build.VERSION.SDK_INT >= ANDROID_12
    }

    /**
     * 是否是 Android 11 及以上版本
     */
    @ChecksSdkIntAtLeast(api = ANDROID_11)
    fun isAndroid11(): Boolean {
        return Build.VERSION.SDK_INT >= ANDROID_11
    }

    /**
     * 是否是 Android 10 及以上版本
     */
    @ChecksSdkIntAtLeast(api = ANDROID_10)
    fun isAndroid10(): Boolean {
        return Build.VERSION.SDK_INT >= ANDROID_10
    }

    /**
     * 是否是 Android 9.0 及以上版本
     */
    @ChecksSdkIntAtLeast(api = ANDROID_9)
    fun isAndroid9(): Boolean {
        return Build.VERSION.SDK_INT >= ANDROID_9
    }

    /**
     * 是否是 Android 8.0 及以上版本
     */
    @ChecksSdkIntAtLeast(api = ANDROID_8)
    fun isAndroid8(): Boolean {
        return Build.VERSION.SDK_INT >= ANDROID_8
    }

    /**
     * 是否是 Android 6.0 及以上版本
     */
    @ChecksSdkIntAtLeast(api = ANDROID_6)
    fun isAndroid6(): Boolean {
        return Build.VERSION.SDK_INT >= ANDROID_6
    }






}