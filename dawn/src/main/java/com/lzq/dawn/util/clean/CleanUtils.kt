package com.lzq.dawn.util.clean

import android.app.ActivityManager
import android.content.Context
import android.os.Environment
import com.lzq.dawn.DawnBridge
import java.io.File

/**
 * @Name :CleanDawnBridge
 * @Time :2022/7/18 17:51
 * @Author :  Lzq
 * @Desc : 清除文件
 */
object CleanUtils {
    /**
     * 清理内部缓存。
     *
     * directory: /data/data/package/cache
     *
     * @return `true`: success<br></br>`false`: fail
     */
    fun cleanInternalCache(): Boolean {
        return DawnBridge.deleteAllInDir(DawnBridge.getApp().cacheDir)
    }

    /**
     * 清理内部文件
     *
     * directory: /data/data/package/files
     *
     * @return `true`: success<br></br>`false`: fail
     */
    fun cleanInternalFiles(): Boolean {
        return DawnBridge.deleteAllInDir(DawnBridge.getApp().filesDir)
    }

    /**
     * 清理内部数据库
     *
     * directory: /data/data/package/databases
     *
     * @return `true`: success<br></br>`false`: fail
     */
    fun cleanInternalDbs(): Boolean {
        return DawnBridge.deleteAllInDir(File(DawnBridge.getApp().filesDir.parent, "databases"))
    }

    /**
     * 按名称清理内部数据库。
     *
     * directory: /data/data/package/databases/dbName
     *
     * @param dbName 数据库名字
     * @return `true`: success<br></br>`false`: fail
     */
    fun cleanInternalDbByName(dbName: String?): Boolean {
        return DawnBridge.getApp().deleteDatabase(dbName)
    }

    /**
     * 清除内部sp。
     *
     * directory: /data/data/package/shared_prefs
     *
     * @return `true`: success<br></br>`false`: fail
     */
    fun cleanInternalSp(): Boolean {
        return DawnBridge.deleteAllInDir(File(DawnBridge.getApp().filesDir.parent, "shared_prefs"))
    }

    /**
     * 清理外部缓存。
     *
     * directory: /storage/emulated/0/android/data/package/cache
     *
     * @return `true`: success<br></br>`false`: fail
     */
    fun cleanExternalCache(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() && DawnBridge.deleteAllInDir(
            DawnBridge.getApp().externalCacheDir
        )
    }

    /**
     * 清除自定义目录。
     *
     * @param dirPath 目录
     * @return `true`: success<br></br>`false`: fail
     */
    fun cleanCustomDir(dirPath: String?): Boolean {
        return DawnBridge.deleteAllInDir(DawnBridge.getFileByPath(dirPath))
    }

    /**
     * 清理app的数据
     */
    fun cleanAppUserData() {
        val am = DawnBridge.getApp().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        am.clearApplicationUserData()
    }
}