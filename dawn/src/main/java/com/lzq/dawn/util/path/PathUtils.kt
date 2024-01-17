package com.lzq.dawn.util.path

import android.os.Build
import android.os.Environment
import android.text.TextUtils
import com.lzq.dawn.DawnBridge
import java.io.File

/**
 * @Name :PathUtils
 * @Time :2022/8/16 13:44
 * @Author :  Lzq
 * @Desc : 文件路径
 */
object PathUtils {
    val rootPath: String
        /**
         * 返回系统路径。
         * /system
         *
         * @return 系统路径。
         */
        get() = getAbsolutePath(Environment.getRootDirectory())
    val dataPath: String
        /**
         * 返回数据的路径。
         * /data
         *
         * @return 数据的路径
         */
        get() = getAbsolutePath(Environment.getDataDirectory())
    val downloadCachePath: String
        /**
         * 返回缓存的路径.
         * /data/cache
         *
         * @return 缓存的路径.
         */
        get() = getAbsolutePath(Environment.getDownloadCacheDirectory())
    val internalAppDataPath: String
        /**
         * 返回内部应用数据路径
         * /data/data/0/包名
         *
         * @return 返回内部应用数据路径
         */
        get() = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            DawnBridge.app.applicationInfo.dataDir
        } else getAbsolutePath(DawnBridge.app.dataDir)
    val internalAppCodeCacheDir: String
        /**
         * 返回文件系统上用于存储缓存代码的应用程序特定缓存目录的绝对路径
         * /data/data/package/code_cache
         *
         * @return 内部应用程序缓存目录
         */
        get() = getAbsolutePath(DawnBridge.app.codeCacheDir)
    val internalAppCachePath: String
        /**
         * 返回文件系统上应用程序特定缓存目录的绝对路径
         * /data/data/package/cache.
         *
         * @return 内部应用缓存路径
         */
        get() = getAbsolutePath(DawnBridge.app.cacheDir)
    val internalAppDbsPath: String
        /**
         * 返回内部应用数据库的路径
         * /data/data/package/databases.
         *
         * @return 内部应用数据库的路径
         */
        get() = DawnBridge.app.applicationInfo.dataDir + "/databases"

    /**
     * 返回内部应用数据库的路径
     * /data/data/package/databases/name.
     *
     * @param name 数据库的名称。
     * @return 内部应用数据库的路径
     */
    fun getInternalAppDbPath(name: String?): String {
        return getAbsolutePath(DawnBridge.app.getDatabasePath(name))
    }

    val internalAppFilesPath: String
        /**
         * 返回内部应用程序文件路径
         * /data/data/package/files
         *
         * @return 内部应用程序文件路径
         */
        get() = getAbsolutePath(DawnBridge.app.filesDir)
    val internalAppSpPath: String
        /**
         * 返回内部 App Sp 路径
         * /data/data/package/shared_prefs
         *
         * @return 内部 App Sp 路径
         */
        get() = DawnBridge.app.applicationInfo.dataDir + "/shared_prefs"
    val internalAppNoBackupFilesPath: String
        /**
         * 返回内部应用程序无备份文件路径
         * /data/data/package/no_backup
         *
         * @return 内部应用程序无备份文件路径
         */
        get() = getAbsolutePath(DawnBridge.app.noBackupFilesDir)
    val externalStoragePath: String
        /**
         * 返回外部存储路径
         * /storage/emulated/0
         *
         * @return 外部存储路径
         */
        get() = if (!DawnBridge.isSDCardEnableByEnvironment) {
            ""
        } else getAbsolutePath(
            Environment.getExternalStorageDirectory()
        )
    val externalMusicPath: String
        /**
         * 返回外部音乐路径
         * /storage/emulated/0/Music.
         *
         * @return 外部音乐路径
         */
        get() = if (!DawnBridge.isSDCardEnableByEnvironment) {
            ""
        } else getAbsolutePath(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC
            )
        )
    val externalPodcastsPath: String
        /**
         * 返回外部播客路径
         * /storage/emulated/0/Podcasts.
         *
         * @return 外部播客路径
         */
        get() = if (!DawnBridge.isSDCardEnableByEnvironment) {
            ""
        } else getAbsolutePath(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PODCASTS
            )
        )
    val externalRingtonesPath: String
        /**
         * Return 外部铃声路径
         * /storage/emulated/0/Ringtones
         *
         * @return 外部铃声路径
         */
        get() = if (!DawnBridge.isSDCardEnableByEnvironment) {
            ""
        } else getAbsolutePath(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_RINGTONES
            )
        )
    val externalAlarmsPath: String
        /**
         * Return 外部警报路径
         * /storage/emulated/0/Alarms.
         *
         * @return 外部警报路径
         */
        get() = if (!DawnBridge.isSDCardEnableByEnvironment) {
            ""
        } else getAbsolutePath(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS)
        )
    val externalNotificationsPath: String
        /**
         * Return 外部通知路径
         * /storage/emulated/0/Notifications.
         *
         * @return 外部通知路径
         */
        get() = if (!DawnBridge.isSDCardEnableByEnvironment) {
            ""
        } else getAbsolutePath(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_NOTIFICATIONS
            )
        )
    val externalPicturesPath: String
        /**
         * Return 外部图片路径
         * /storage/emulated/0/Pictures.
         *
         * @return 外部图片路径
         */
        get() = if (!DawnBridge.isSDCardEnableByEnvironment) {
            ""
        } else getAbsolutePath(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        )
    val externalMoviesPath: String
        /**
         * Return 外部电影路径
         * /storage/emulated/0/Movies.
         *
         * @return 外部电影路径
         */
        get() = if (!DawnBridge.isSDCardEnableByEnvironment) {
            ""
        } else getAbsolutePath(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
            )
        )
    val externalDownloadsPath: String
        /**
         * Return 外部下载路径 /storage/emulated/0/Download.
         *
         * @return 外部下载路径
         */
        get() = if (!DawnBridge.isSDCardEnableByEnvironment) {
            ""
        } else getAbsolutePath(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            )
        )
    val externalDcimPath: String
        /**
         * Return 外部 DCIM 路径 相册
         * /storage/emulated/0/DCIM.
         *
         * @return 外部 DCIM 路径
         */
        get() = if (!DawnBridge.isSDCardEnableByEnvironment) {
            ""
        } else getAbsolutePath(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM
            )
        )
    val externalDocumentsPath: String
        /**
         * Return 外部文档路径
         * /storage/emulated/0/Documents.
         *
         * @return 外部文档路径
         */
        get() = if (!DawnBridge.isSDCardEnableByEnvironment) {
            ""
        } else getAbsolutePath(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS
            )
        )
    val externalAppDataPath: String
        /**
         * Return 外部应用数据路径
         * /storage/emulated/0/Android/data/package.
         *
         * @return 外部应用数据路径
         */
        get() {
            if (!DawnBridge.isSDCardEnableByEnvironment) {
                return ""
            }
            val externalCacheDir = DawnBridge.app.externalCacheDir ?: return ""
            return getAbsolutePath(externalCacheDir.parentFile)
        }
    val externalAppCachePath: String
        /**
         * Return 外部应用缓存路径
         * /storage/emulated/0/Android/data/package/cache.
         *
         * @return 外部应用缓存路径
         */
        get() = if (!DawnBridge.isSDCardEnableByEnvironment) {
            ""
        } else getAbsolutePath(DawnBridge.app.externalCacheDir)
    val externalAppFilesPath: String
        /**
         * Return 外部应用程序文件路径
         * /storage/emulated/0/Android/data/package/files.
         *
         * @return 外部应用程序文件路径
         */
        get() = if (!DawnBridge.isSDCardEnableByEnvironment) {
            ""
        } else getAbsolutePath(DawnBridge.app.getExternalFilesDir(null))
    val externalAppMusicPath: String
        /**
         * Return 外部应用音乐路径
         * /storage/emulated/0/Android/data/package/files/Music.
         *
         * @return 外部应用音乐路径
         */
        get() = if (!DawnBridge.isSDCardEnableByEnvironment) {
            ""
        } else getAbsolutePath(
            DawnBridge.app.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        )
    val externalAppPodcastsPath: String
        /**
         * Return 外部应用播客路径
         * /storage/emulated/0/Android/data/package/files/Podcasts.
         *
         * @return 外部应用播客路径
         */
        get() = if (!DawnBridge.isSDCardEnableByEnvironment) {
            ""
        } else getAbsolutePath(
            DawnBridge.app.getExternalFilesDir(Environment.DIRECTORY_PODCASTS)
        )
    val externalAppRingtonesPath: String
        /**
         * Return 外部应用铃声路径
         * /storage/emulated/0/Android/data/package/files/Ringtones.
         *
         * @return 外部应用铃声路径
         */
        get() = if (!DawnBridge.isSDCardEnableByEnvironment) {
            ""
        } else getAbsolutePath(
            DawnBridge.app.getExternalFilesDir(Environment.DIRECTORY_RINGTONES)
        )
    val externalAppAlarmsPath: String
        /**
         * Return 外部应用警报路径
         * /storage/emulated/0/Android/data/package/files/Alarms.
         *
         * @return 外部应用警报路径
         */
        get() = if (!DawnBridge.isSDCardEnableByEnvironment) {
            ""
        } else getAbsolutePath(
            DawnBridge.app.getExternalFilesDir(Environment.DIRECTORY_ALARMS)
        )
    val externalAppNotificationsPath: String
        /**
         * Return 外部应用通知路径
         * /storage/emulated/0/Android/data/package/files/Notifications.
         *
         * @return 外部应用通知路径
         */
        get() = if (!DawnBridge.isSDCardEnableByEnvironment) {
            ""
        } else getAbsolutePath(
            DawnBridge.app.getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS)
        )
    val externalAppPicturesPath: String
        /**
         * Return 外部应用图片路径 /storage/emulated/0/Android/data/package/files/Pictures.
         *
         * @return 外部应用图片路径
         */
        get() = if (!DawnBridge.isSDCardEnableByEnvironment) {
            ""
        } else getAbsolutePath(
            DawnBridge.app.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
    val externalAppMoviesPath: String
        /**
         * Return 外部应用电影路径
         * /storage/emulated/0/Android/data/package/files/Movies.
         *
         * @return 外部应用电影路径
         */
        get() = if (!DawnBridge.isSDCardEnableByEnvironment) {
            ""
        } else getAbsolutePath(
            DawnBridge.app.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        )
    val externalAppDownloadPath: String
        /**
         * Return 外部应用下载路径
         * /storage/emulated/0/Android/data/package/files/Download.
         *
         * @return 外部应用下载路径
         */
        get() = if (!DawnBridge.isSDCardEnableByEnvironment) {
            ""
        } else getAbsolutePath(
            DawnBridge.app.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        )
    val externalAppDcimPath: String
        /**
         * Return 外部应用程序 Dcim 路径
         * /storage/emulated/0/Android/data/package/files/DCIM.
         *
         * @return 外部应用程序 Dcim 路径
         */
        get() = if (!DawnBridge.isSDCardEnableByEnvironment) {
            ""
        } else getAbsolutePath(
            DawnBridge.app.getExternalFilesDir(Environment.DIRECTORY_DCIM)
        )
    val externalAppDocumentsPath: String
        /**
         * Return 外部应用文档路径 /storage/emulated/0/Android/data/package/files/Documents.
         *
         * @return 外部应用文档路径
         */
        get() = if (!DawnBridge.isSDCardEnableByEnvironment) {
            ""
        } else getAbsolutePath(
            DawnBridge.app.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        )
    val externalAppObbPath: String
        /**
         * Return 外部应用程序 Obb 路径
         * /storage/emulated/0/Android/obb/package.
         *
         * @return 外部应用程序 Obb 路径
         */
        get() = if (!DawnBridge.isSDCardEnableByEnvironment) {
            ""
        } else getAbsolutePath(DawnBridge.app.obbDir)
    val rootPathExternalFirst: String
        get() {
            var rootPath = externalStoragePath
            if (TextUtils.isEmpty(rootPath)) {
                rootPath = PathUtils.rootPath
            }
            return rootPath
        }
    val appDataPathExternalFirst: String
        get() {
            var appDataPath = externalAppDataPath
            if (TextUtils.isEmpty(appDataPath)) {
                appDataPath = internalAppDataPath
            }
            return appDataPath
        }
    val filesPathExternalFirst: String
        get() {
            var filePath = externalAppFilesPath
            if (TextUtils.isEmpty(filePath)) {
                filePath = internalAppFilesPath
            }
            return filePath
        }
    val cachePathExternalFirst: String
        get() {
            var appPath = externalAppCachePath
            if (TextUtils.isEmpty(appPath)) {
                appPath = internalAppCachePath
            }
            return appPath
        }

    private fun getAbsolutePath(file: File?): String {
        return if (file == null) {
            ""
        } else file.absolutePath
    }
}