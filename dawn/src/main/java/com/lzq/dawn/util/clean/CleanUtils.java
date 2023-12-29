package com.lzq.dawn.util.clean;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;

import com.lzq.dawn.DawnBridge;

import java.io.File;

/**
 * @Name :CleanDawnBridge
 * @Time :2022/7/18 17:51
 * @Author :  Lzq
 * @Desc : 清除文件
 */
public final  class CleanUtils {

    private CleanUtils() {

    }

    /**
     * 清理内部缓存。
     * <p>directory: /data/data/package/cache</p>
     *
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean cleanInternalCache() {
        return DawnBridge.deleteAllInDir(DawnBridge.getApp().getCacheDir());
    }

    /**
     * 清理内部文件
     * <p>directory: /data/data/package/files</p>
     *
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean cleanInternalFiles() {
        return DawnBridge.deleteAllInDir(DawnBridge.getApp().getFilesDir());
    }

    /**
     * 清理内部数据库
     * <p>directory: /data/data/package/databases</p>
     *
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean cleanInternalDbs() {
        return DawnBridge.deleteAllInDir(new File(DawnBridge.getApp().getFilesDir().getParent(), "databases"));
    }

    /**
     * 按名称清理内部数据库。
     * <p>directory: /data/data/package/databases/dbName</p>
     *
     * @param dbName 数据库名字
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean cleanInternalDbByName(final String dbName) {
        return DawnBridge.getApp().deleteDatabase(dbName);
    }

    /**
     * 清除内部sp。
     * <p>directory: /data/data/package/shared_prefs</p>
     *
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean cleanInternalSp() {
        return DawnBridge.deleteAllInDir(new File(DawnBridge.getApp().getFilesDir().getParent(), "shared_prefs"));
    }

    /**
     * 清理外部缓存。
     * <p>directory: /storage/emulated/0/android/data/package/cache</p>
     *
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean cleanExternalCache() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && DawnBridge.deleteAllInDir(DawnBridge.getApp().getExternalCacheDir());
    }

    /**
     * 清除自定义目录。
     *
     * @param dirPath 目录
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean cleanCustomDir(final String dirPath) {
        return DawnBridge.deleteAllInDir(DawnBridge.getFileByPath(dirPath));
    }


    /**
     * 清理app的数据
     */
    public static void cleanAppUserData() {
        ActivityManager am = (ActivityManager) DawnBridge.getApp().getSystemService(Context.ACTIVITY_SERVICE);
        //noinspection ConstantConditions
        am.clearApplicationUserData();
    }
}
