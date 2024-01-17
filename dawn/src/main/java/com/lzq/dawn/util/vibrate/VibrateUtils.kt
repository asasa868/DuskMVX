package com.lzq.dawn.util.vibrate

import android.Manifest.permission
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresPermission
import com.lzq.dawn.DawnBridge

/**
 * @Name : VibrateUtils
 * @Time : 2022/12/21  16:12
 * @Author :  Lzq
 * @Desc : 振动
 */
object VibrateUtils {
    private var vibrator: Vibrator? = null
        get() {
            if (field == null) {
                field = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                    (DawnBridge.app
                        .getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
                } else {
                    DawnBridge.app.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                }
            }
            return field
        }

    /**
     * 震动.
     *
     * 必须要有 `<uses-permission android:name="android.permission.VIBRATE" />`
     *
     * @param milliseconds 振动的毫秒数。
     */
    @RequiresPermission(permission.VIBRATE)
    fun vibrate(milliseconds: Long) {
        val vibrator = vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(milliseconds)
        }
    }

    /**
     * 震动.
     *
     * 必须要有 `<uses-permission android:name="android.permission.VIBRATE" />`
     *
     * @param pattern 打开或关闭振动器的长时间数组。
     * @param repeat  索引到要重复的模式，如果不想重复，则为 -1。
     */
    @RequiresPermission(permission.VIBRATE)
    fun vibrate(pattern: LongArray?, repeat: Int) {
        val vibrator = vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, repeat))
        } else {
            vibrator.vibrate(pattern, repeat)
        }
    }

    /**
     * 取消震动
     *
     * 必须要有 `<uses-permission android:name="android.permission.VIBRATE" />`
     */
    @RequiresPermission(permission.VIBRATE)
    fun cancel() {
        val vibrator = vibrator ?: return
        vibrator.cancel()
    }
}