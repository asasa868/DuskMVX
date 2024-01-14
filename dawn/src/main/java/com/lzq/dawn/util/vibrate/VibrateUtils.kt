package com.lzq.dawn.util.vibrate;

import static android.Manifest.permission.VIBRATE;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;

import androidx.annotation.RequiresPermission;

import com.lzq.dawn.DawnBridge;

/**
 * @Name : VibrateUtils
 * @Time : 2022/12/21  16:12
 * @Author :  Lzq
 * @Desc : 振动
 */
public final class VibrateUtils {

    private static Vibrator vibrator;

    private VibrateUtils(){}


    /**
     * 震动.
     * <p>必须要有 {@code <uses-permission android:name="android.permission.VIBRATE" />}</p>
     *
     * @param milliseconds 振动的毫秒数。
     */
    @RequiresPermission(VIBRATE)
    public static void vibrate(final long milliseconds) {
        Vibrator vibrator = getVibrator();
        if (vibrator == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(milliseconds);
        }
    }

    /**
     * 震动.
     * <p>必须要有 {@code <uses-permission android:name="android.permission.VIBRATE" />}</p>
     *
     * @param pattern 打开或关闭振动器的长时间数组。
     * @param repeat  索引到要重复的模式，如果不想重复，则为 -1。
     */
    @RequiresPermission(VIBRATE)
    public static void vibrate(final long[] pattern, final int repeat) {
        Vibrator vibrator = getVibrator();
        if (vibrator == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, repeat));
        } else {
            vibrator.vibrate(pattern,repeat);
        }

    }

    /**
     * 取消震动
     * <p>必须要有 {@code <uses-permission android:name="android.permission.VIBRATE" />}</p>
     */
    @RequiresPermission(VIBRATE)
    public static void cancel() {
        Vibrator vibrator = getVibrator();
        if (vibrator == null) {
            return;
        }
        vibrator.cancel();
    }

    private static Vibrator getVibrator() {
        if (vibrator == null) {
            if (Build.VERSION.SDK_INT>Build.VERSION_CODES.R){
                vibrator = ((VibratorManager) DawnBridge.getApp().getSystemService(Context.VIBRATOR_MANAGER_SERVICE)).getDefaultVibrator();
            }else {
                vibrator = (Vibrator) DawnBridge.getApp().getSystemService(Context.VIBRATOR_SERVICE);
            }
        }
        return vibrator;
    }
}
