package com.lzq.dawn.util.brightness;

import android.content.ContentResolver;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.lzq.dawn.DawnBridge;

/**
 * @Name :BrightnessUtils
 * @Time :2022/7/12 15:34
 * @Author :  Lzq
 * @Desc : 屏幕亮度的工具类
 */
public final  class BrightnessUtils {

    private BrightnessUtils() {
    }

    /**
     * 返回是否启用自动亮度模式
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAutoBrightnessEnabled() {
        try {
            int mode = Settings.System.getInt(
                    DawnBridge.getApp().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE
            );
            return mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 启用或禁用自动亮度模式。
     * <p>Must hold {@code <uses-permission android:name="android.permission.WRITE_SETTINGS" />}</p>
     *
     * @param enabled 设置的值
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean setAutoBrightnessEnabled(final boolean enabled) {
        return Settings.System.putInt(
                DawnBridge.getApp().getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                enabled ? Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                        : Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        );
    }

    /**
     * 获取屏幕亮度
     *
     * @return 屏幕亮度 0-255
     */
    public static int getBrightness() {
        try {
            return Settings.System.getInt(
                    DawnBridge.getApp().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS
            );
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 设置屏幕亮度
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.WRITE_SETTINGS" />}</p>
     * 并得到授权
     *
     * @param brightness 亮度值
     */
    public static boolean setBrightness(@IntRange(from = 0, to = 255) final int brightness) {
        ContentResolver resolver = DawnBridge.getApp().getContentResolver();
        boolean b = Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
        resolver.notifyChange(Settings.System.getUriFor("screen_brightness"), null);
        return b;
    }

    /**
     * 设置窗口亮度
     *
     * @param window     窗口
     * @param brightness 亮度值
     */
    public static void setWindowBrightness(@NonNull final Window window,
                                           @IntRange(from = 0, to = 255) final int brightness) {
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness / 255f;
        window.setAttributes(lp);
    }

    /**
     * 获取窗口亮度
     *
     * @param window 窗口
     * @return 屏幕亮度 0-255
     */
    public static int getWindowBrightness(@NonNull final Window window) {
        WindowManager.LayoutParams lp = window.getAttributes();
        float brightness = lp.screenBrightness;
        if (brightness < 0) {
            return getBrightness();
        }
        return (int) (brightness * 255);
    }
}
