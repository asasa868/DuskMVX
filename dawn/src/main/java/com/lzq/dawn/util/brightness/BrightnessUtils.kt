package com.lzq.dawn.util.brightness

import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.view.Window
import androidx.annotation.IntRange
import com.lzq.dawn.DawnBridge

/**
 * @Name :BrightnessUtils
 * @Time :2022/7/12 15:34
 * @Author :  Lzq
 * @Desc : 屏幕亮度的工具类
 */
object BrightnessUtils {
    val isAutoBrightnessEnabled: Boolean
        /**
         * 返回是否启用自动亮度模式
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = try {
            val mode = Settings.System.getInt(
                DawnBridge.getApp().contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE
            )
            mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
            false
        }

    /**
     * 启用或禁用自动亮度模式。
     *
     * Must hold `<uses-permission android:name="android.permission.WRITE_SETTINGS" />`
     *
     * @param enabled 设置的值
     * @return `true`: success<br></br>`false`: fail
     */
    fun setAutoBrightnessEnabled(enabled: Boolean): Boolean {
        return Settings.System.putInt(
            DawnBridge.getApp().contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            if (enabled) Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC else Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        )
    }

    val brightness: Int
        /**
         * 获取屏幕亮度
         *
         * @return 屏幕亮度 0-255
         */
        get() = try {
            Settings.System.getInt(
                DawnBridge.getApp().contentResolver, Settings.System.SCREEN_BRIGHTNESS
            )
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
            0
        }

    /**
     * 设置屏幕亮度
     *
     * 需添加权限 `<uses-permission android:name="android.permission.WRITE_SETTINGS" />`
     * 并得到授权
     *
     * @param brightness 亮度值
     */
    fun setBrightness(@IntRange(from = 0, to = 255) brightness: Int): Boolean {
        val resolver = DawnBridge.getApp().contentResolver
        val b = Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
        resolver.notifyChange(Settings.System.getUriFor("screen_brightness"), null)
        return b
    }

    /**
     * 设置窗口亮度
     *
     * @param window     窗口
     * @param brightness 亮度值
     */
    fun setWindowBrightness(
        window: Window, @IntRange(from = 0, to = 255) brightness: Int
    ) {
        val lp = window.attributes
        lp.screenBrightness = brightness / 255f
        window.attributes = lp
    }

    /**
     * 获取窗口亮度
     *
     * @param window 窗口
     * @return 屏幕亮度 0-255
     */
    fun getWindowBrightness(window: Window): Int {
        val lp = window.attributes
        val brightness = lp.screenBrightness
        return if (brightness < 0) {
            BrightnessUtils.brightness
        } else (brightness * 255).toInt()
    }
}