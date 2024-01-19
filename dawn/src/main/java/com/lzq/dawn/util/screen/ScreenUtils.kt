package com.lzq.dawn.util.screen

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Point
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.view.Surface
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresPermission
import com.lzq.dawn.DawnBridge

/**
 * @Name :ScreenUtils
 * @Time :2022/8/29 15:35
 * @Author :  Lzq
 * @Desc : 屏幕
 */
object ScreenUtils  {
    /**
     * 返回给定视图的X（起点宽度）和屏幕宽度之间的距离。
     *
     * @return 给定视图的X（起点宽度）和屏幕宽度之间的距离。
     */
    fun calculateDistanceByX(view: View): Int {
        val point = IntArray(2)
        view.getLocationOnScreen(point)
        return screenWidth - point[0]
    }

    /**
     * 返回给定视图的Y（起点高度）和屏幕高度之间的距离。
     *
     * @return 给定视图的Y（起点高度）与屏幕高度之间的距离。
     */
    fun calculateDistanceByY(view: View): Int {
        val point = IntArray(2)
        view.getLocationOnScreen(point)
        return screenHeight - point[1]
    }

    /**
     * 返回屏幕上给定视图的X坐标。
     *
     * @return 屏幕上给定视图的X坐标。
     */
    fun getViewX(view: View): Int {
        val point = IntArray(2)
        view.getLocationOnScreen(point)
        return point[0]
    }

    /**
     * 返回屏幕上给定视图的Y坐标。
     *
     * @return 屏幕上给定视图的Y坐标。
     */
    fun getViewY(view: View): Int {
        val point = IntArray(2)
        view.getLocationOnScreen(point)
        return point[1]
    }

    val screenWidth: Int
        /**
         * 返回屏幕宽度，以像素为单位。
         *
         * @return 屏幕宽度，以像素为单位
         */
        get() {
            val wm = DawnBridge.app.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val point = Point()
            wm.defaultDisplay.getRealSize(point)
            return point.x
        }
    val screenHeight: Int
        /**
         * 返回屏幕高度，以像素为单位。
         *
         * @return 屏幕高度，以像素为单位。
         */
        get() {
            val wm = DawnBridge.app.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val point = Point()
            wm.defaultDisplay.getRealSize(point)
            return point.y
        }
    @JvmStatic
    val appScreenWidth: Int
        /**
         * 返回应用程序的屏幕宽度（以像素为单位）。
         *
         * @return 应用程序的屏幕宽度（以像素为单位）。
         */
        get() {
            val wm = DawnBridge.app.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val point = Point()
            wm.defaultDisplay.getSize(point)
            return point.x
        }
    val appScreenHeight: Int
        /**
         * 返回应用程序的屏幕高度（以像素为单位）。
         *
         * @return 应用程序的屏幕高度（以像素为单位）。
         */
        get() {
            val wm = DawnBridge.app.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val point = Point()
            wm.defaultDisplay.getSize(point)
            return point.y
        }
    val screenDensity: Float
        /**
         * 返回屏幕的密度。
         *
         * @return 屏幕密度
         */
        get() = Resources.getSystem().displayMetrics.density
    val screenDensityDpi: Int
        /**
         * 返回以每英寸点数表示的屏幕密度。
         *
         * @return 每英寸点数表示的屏幕密度。
         */
        get() = Resources.getSystem().displayMetrics.densityDpi
    val screenXDpi: Float
        /**
         * 返回X维度中屏幕每英寸的精确物理像素。
         *
         * @return X维度上屏幕每英寸的精确物理像素
         */
        get() = Resources.getSystem().displayMetrics.xdpi
    val screenYDpi: Float
        /**
         * 返回Y维度中屏幕每英寸的精确物理像素。
         *
         * @return Y维度中屏幕每英寸的精确物理像素。
         */
        get() = Resources.getSystem().displayMetrics.ydpi

    /**
     * 设置为全屏。
     *
     * @param activity activity.
     */
    fun setFullScreen(activity: Activity) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    /**
     * 设置为非全屏。
     *
     * @param activity activity.
     */
    fun setNonFullScreen(activity: Activity) {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    /**
     * 切换到全屏。
     *
     * @param activity activity.
     */
    fun toggleFullScreen(activity: Activity) {
        val isFullScreen = isFullScreen(activity)
        val window = activity.window
        if (isFullScreen) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    /**
     * 返回屏幕是否已满。
     *
     * @param activity activity.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFullScreen(activity: Activity): Boolean {
        val fullScreenFlag = WindowManager.LayoutParams.FLAG_FULLSCREEN
        return activity.window.attributes.flags and fullScreenFlag == fullScreenFlag
    }

    /**
     * 将屏幕设置为横向。
     *
     * @param activity activity.
     */
    @SuppressLint("SourceLockedOrientationActivity")
    fun setLandscape(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    /**
     * 将屏幕设置为纵向。
     *
     * @param activity activity.
     */
    @SuppressLint("SourceLockedOrientationActivity")
    fun setPortrait(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    val isLandscape: Boolean
        /**
         * 返回屏幕是否为横向。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = (DawnBridge.app.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
    val isPortrait: Boolean
        /**
         * 返回屏幕是否为纵向。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = (DawnBridge.app.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)

    /**
     * 返回屏幕的旋转。
     *
     * @param activity activity.
     * @return 屏幕的旋转
     */
    fun getScreenRotation(activity: Activity): Int {
        return when (activity.windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> 0
        }
    }

    val isScreenLock: Boolean
        /**
         * 返回屏幕是否已锁定。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() {
            val km = DawnBridge.app.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            return km.inKeyguardRestrictedInputMode()
        }

    @set:RequiresPermission(permission.WRITE_SETTINGS)
    var sleepDuration: Int
        /**
         * 返回睡眠时间。
         *
         * @return 睡眠时间。
         */
        get() = try {
            Settings.System.getInt(
                DawnBridge.app.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT
            )
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
            -123
        }
        /**
         * 设置睡眠时间。
         *
         * 必须持有`<uses-permission android:name="android.permission.WRITE_SETTINGS" />`
         *
         * @param duration 持续时间。
         */
        set(duration) {
            Settings.System.putInt(
                DawnBridge.app.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, duration
            )
        }
}