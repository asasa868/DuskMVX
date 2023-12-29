package com.lzq.dawn.util.screen;

import static android.Manifest.permission.WRITE_SETTINGS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.provider.Settings;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import com.lzq.dawn.DawnBridge;

/**
 * @Name :ScreenUtils
 * @Time :2022/8/29 15:35
 * @Author :  Lzq
 * @Desc : 屏幕
 */
public class ScreenUtils {
    private ScreenUtils() {
    }

    /**
     * 返回屏幕宽度，以像素为单位。
     *
     * @return 屏幕宽度，以像素为单位
     */
    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) DawnBridge.getApp().getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) {
            return -1;
        }
        Point point = new Point();
        wm.getDefaultDisplay().getRealSize(point);
        return point.x;
    }

    /**
     * 返回屏幕高度，以像素为单位。
     *
     * @return 屏幕高度，以像素为单位。
     */
    public static int getScreenHeight() {
        WindowManager wm = (WindowManager) DawnBridge.getApp().getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) {
            return -1;
        }
        Point point = new Point();
        wm.getDefaultDisplay().getRealSize(point);
        return point.y;
    }

    /**
     * 返回应用程序的屏幕宽度（以像素为单位）。
     *
     * @return 应用程序的屏幕宽度（以像素为单位）。
     */
    public static int getAppScreenWidth() {
        WindowManager wm = (WindowManager) DawnBridge.getApp().getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) {
            return -1;
        }
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        return point.x;
    }

    /**
     * 返回应用程序的屏幕高度（以像素为单位）。
     *
     * @return 应用程序的屏幕高度（以像素为单位）。
     */
    public static int getAppScreenHeight() {
        WindowManager wm = (WindowManager) DawnBridge.getApp().getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) {
            return -1;
        }
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        return point.y;
    }

    /**
     * 返回屏幕的密度。
     *
     * @return 屏幕密度
     */
    public static float getScreenDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }

    /**
     * 返回以每英寸点数表示的屏幕密度。
     *
     * @return 每英寸点数表示的屏幕密度。
     */
    public static int getScreenDensityDpi() {
        return Resources.getSystem().getDisplayMetrics().densityDpi;
    }

    /**
     * 返回X维度中屏幕每英寸的精确物理像素。
     *
     * @return X维度上屏幕每英寸的精确物理像素
     */
    public static float getScreenXDpi() {
        return Resources.getSystem().getDisplayMetrics().xdpi;
    }

    /**
     * 返回Y维度中屏幕每英寸的精确物理像素。
     *
     * @return Y维度中屏幕每英寸的精确物理像素。
     */
    public static float getScreenYDpi() {
        return Resources.getSystem().getDisplayMetrics().ydpi;
    }

    /**
     * 返回给定视图的X（起点宽度）和屏幕宽度之间的距离。
     *
     * @return 给定视图的X（起点宽度）和屏幕宽度之间的距离。
     */
    public int calculateDistanceByX(View view) {
        int[] point = new int[2];
        view.getLocationOnScreen(point);
        return getScreenWidth() - point[0];
    }

    /**
     * 返回给定视图的Y（起点高度）和屏幕高度之间的距离。
     *
     * @return 给定视图的Y（起点高度）与屏幕高度之间的距离。
     */
    public int calculateDistanceByY(View view) {
        int[] point = new int[2];
        view.getLocationOnScreen(point);
        return getScreenHeight() - point[1];
    }

    /**
     * 返回屏幕上给定视图的X坐标。
     *
     * @return 屏幕上给定视图的X坐标。
     */
    public int getViewX(View view) {
        int[] point = new int[2];
        view.getLocationOnScreen(point);
        return point[0];
    }

    /**
     * 返回屏幕上给定视图的Y坐标。
     *
     * @return 屏幕上给定视图的Y坐标。
     */
    public int getViewY(View view) {
        int[] point = new int[2];
        view.getLocationOnScreen(point);
        return point[1];
    }

    /**
     * 设置为全屏。
     *
     * @param activity activity.
     */
    public static void setFullScreen(@NonNull final Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 设置为非全屏。
     *
     * @param activity activity.
     */
    public static void setNonFullScreen(@NonNull final Activity activity) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 切换到全屏。
     *
     * @param activity activity.
     */
    public static void toggleFullScreen(@NonNull final Activity activity) {
        boolean isFullScreen = isFullScreen(activity);
        Window window = activity.getWindow();
        if (isFullScreen) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    /**
     * 返回屏幕是否已满。
     *
     * @param activity activity.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isFullScreen(@NonNull final Activity activity) {
        int fullScreenFlag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        return (activity.getWindow().getAttributes().flags & fullScreenFlag) == fullScreenFlag;
    }

    /**
     * 将屏幕设置为横向。
     *
     * @param activity activity.
     */
    @SuppressLint("SourceLockedOrientationActivity")
    public static void setLandscape(@NonNull final Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * 将屏幕设置为纵向。
     *
     * @param activity activity.
     */
    @SuppressLint("SourceLockedOrientationActivity")
    public static void setPortrait(@NonNull final Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 返回屏幕是否为横向。
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isLandscape() {
        return DawnBridge.getApp().getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * 返回屏幕是否为纵向。
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isPortrait() {
        return DawnBridge.getApp().getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * 返回屏幕的旋转。
     *
     * @param activity activity.
     * @return 屏幕的旋转
     */
    public static int getScreenRotation(@NonNull final Activity activity) {
        switch (activity.getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
            default:
                return 0;
        }
    }



    /**
     * 返回屏幕是否已锁定。
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isScreenLock() {
        KeyguardManager km =
                (KeyguardManager) DawnBridge.getApp().getSystemService(Context.KEYGUARD_SERVICE);
        if (km == null) {
            return false;
        }
        return km.inKeyguardRestrictedInputMode();
    }

    /**
     * 设置睡眠时间。
     * <p>必须持有{@code <uses-permission android:name="android.permission.WRITE_SETTINGS" />}</p>
     *
     * @param duration 持续时间。
     */
    @RequiresPermission(WRITE_SETTINGS)
    public static void setSleepDuration(final int duration) {
        Settings.System.putInt(
                DawnBridge.getApp().getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT,
                duration
        );
    }

    /**
     * 返回睡眠时间。
     *
     * @return 睡眠时间。
     */
    public static int getSleepDuration() {
        try {
            return Settings.System.getInt(
                    DawnBridge.getApp().getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT
            );
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return -123;
        }
    }

}
