package com.lzq.dawn.util.path;

import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.lzq.dawn.DawnBridge;

import java.io.File;

/**
 * @Name :PathUtils
 * @Time :2022/8/16 13:44
 * @Author :  Lzq
 * @Desc : 文件路径
 */
public final class PathUtils {

    private PathUtils() {

    }

    /**
     * 返回系统路径。
     * /system
     *
     * @return 系统路径。
     */
    public static String getRootPath() {
        return getAbsolutePath(Environment.getRootDirectory());
    }

    /**
     * 返回数据的路径。
     * /data
     *
     * @return 数据的路径
     */
    public static String getDataPath() {
        return getAbsolutePath(Environment.getDataDirectory());
    }

    /**
     * 返回缓存的路径.
     * /data/cache
     *
     * @return 缓存的路径.
     */
    public static String getDownloadCachePath() {
        return getAbsolutePath(Environment.getDownloadCacheDirectory());
    }

    /**
     * 返回内部应用数据路径
     * /data/data/0/包名
     *
     * @return 返回内部应用数据路径
     */
    public static String getInternalAppDataPath() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return DawnBridge.getApp().getApplicationInfo().dataDir;
        }
        return getAbsolutePath(DawnBridge.getApp().getDataDir());
    }

    /**
     * 返回文件系统上用于存储缓存代码的应用程序特定缓存目录的绝对路径
     * /data/data/package/code_cache
     *
     * @return 内部应用程序缓存目录
     */
    public static String getInternalAppCodeCacheDir() {
        return getAbsolutePath(DawnBridge.getApp().getCodeCacheDir());
    }

    /**
     * 返回文件系统上应用程序特定缓存目录的绝对路径
     * /data/data/package/cache.
     *
     * @return 内部应用缓存路径
     */
    public static String getInternalAppCachePath() {
        return getAbsolutePath(DawnBridge.getApp().getCacheDir());
    }

    /**
     * 返回内部应用数据库的路径
     * /data/data/package/databases.
     *
     * @return 内部应用数据库的路径
     */
    public static String getInternalAppDbsPath() {
        return DawnBridge.getApp().getApplicationInfo().dataDir + "/databases";
    }

    /**
     * 返回内部应用数据库的路径
     * /data/data/package/databases/name.
     *
     * @param name 数据库的名称。
     * @return 内部应用数据库的路径
     */
    public static String getInternalAppDbPath(String name) {
        return getAbsolutePath(DawnBridge.getApp().getDatabasePath(name));
    }

    /**
     * 返回内部应用程序文件路径
     * /data/data/package/files
     *
     * @return 内部应用程序文件路径
     */
    public static String getInternalAppFilesPath() {
        return getAbsolutePath(DawnBridge.getApp().getFilesDir());
    }

    /**
     * 返回内部 App Sp 路径
     * /data/data/package/shared_prefs
     *
     * @return 内部 App Sp 路径
     */
    public static String getInternalAppSpPath() {
        return DawnBridge.getApp().getApplicationInfo().dataDir + "/shared_prefs";
    }

    /**
     * 返回内部应用程序无备份文件路径
     * /data/data/package/no_backup
     *
     * @return 内部应用程序无备份文件路径
     */
    public static String getInternalAppNoBackupFilesPath() {
        return getAbsolutePath(DawnBridge.getApp().getNoBackupFilesDir());
    }

    /**
     * 返回外部存储路径
     * /storage/emulated/0
     *
     * @return 外部存储路径 
     */
    public static String getExternalStoragePath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        return getAbsolutePath(Environment.getExternalStorageDirectory());
    }

    /**
     * 返回外部音乐路径
     * /storage/emulated/0/Music.
     *
     * @return 外部音乐路径
     */
    public static String getExternalMusicPath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        return getAbsolutePath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
    }

    /**
     * 返回外部播客路径
     * /storage/emulated/0/Podcasts.
     *
     * @return 外部播客路径
     */
    public static String getExternalPodcastsPath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        return getAbsolutePath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS));
    }

    /**
     * Return 外部铃声路径
     * /storage/emulated/0/Ringtones
     *
     * @return 外部铃声路径
     */
    public static String getExternalRingtonesPath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        return getAbsolutePath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES));
    }

    /**
     * Return 外部警报路径
     * /storage/emulated/0/Alarms.
     *
     * @return 外部警报路径
     */
    public static String getExternalAlarmsPath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        return getAbsolutePath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS));
    }

    /**
     * Return 外部通知路径
     * /storage/emulated/0/Notifications.
     *
     * @return 外部通知路径
     */
    public static String getExternalNotificationsPath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        return getAbsolutePath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS));
    }

    /**
     * Return 外部图片路径
     * /storage/emulated/0/Pictures.
     *
     * @return 外部图片路径
     */
    public static String getExternalPicturesPath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        return getAbsolutePath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
    }

    /**
     * Return 外部电影路径
     * /storage/emulated/0/Movies.
     *
     * @return 外部电影路径
     */
    public static String getExternalMoviesPath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        return getAbsolutePath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));
    }

    /**
     * Return 外部下载路径 /storage/emulated/0/Download.
     *
     * @return 外部下载路径
     */
    public static String getExternalDownloadsPath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        return getAbsolutePath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
    }

    /**
     * Return 外部 DCIM 路径 相册
     * /storage/emulated/0/DCIM.
     *
     * @return 外部 DCIM 路径
     */
    public static String getExternalDcimPath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        return getAbsolutePath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
    }

    /**
     * Return 外部文档路径
     * /storage/emulated/0/Documents.
     *
     * @return 外部文档路径
     */
    public static String getExternalDocumentsPath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        return getAbsolutePath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS));
    }

    /**
     * Return 外部应用数据路径
     * /storage/emulated/0/Android/data/package.
     *
     * @return 外部应用数据路径
     */
    public static String getExternalAppDataPath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        File externalCacheDir = DawnBridge.getApp().getExternalCacheDir();
        if (externalCacheDir == null) {
            return "";
        }
        return getAbsolutePath(externalCacheDir.getParentFile());
    }

    /**
     * Return 外部应用缓存路径
     * /storage/emulated/0/Android/data/package/cache.
     *
     * @return 外部应用缓存路径
     */
    public static String getExternalAppCachePath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        return getAbsolutePath(DawnBridge.getApp().getExternalCacheDir());
    }

    /**
     * Return 外部应用程序文件路径
     * /storage/emulated/0/Android/data/package/files.
     *
     * @return 外部应用程序文件路径
     */
    public static String getExternalAppFilesPath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        return getAbsolutePath(DawnBridge.getApp().getExternalFilesDir(null));
    }

    /**
     * Return 外部应用音乐路径
     * /storage/emulated/0/Android/data/package/files/Music.
     *
     * @return 外部应用音乐路径
     */
    public static String getExternalAppMusicPath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        return getAbsolutePath(DawnBridge.getApp().getExternalFilesDir(Environment.DIRECTORY_MUSIC));
    }

    /**
     * Return 外部应用播客路径
     * /storage/emulated/0/Android/data/package/files/Podcasts.
     *
     * @return 外部应用播客路径
     */
    public static String getExternalAppPodcastsPath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        return getAbsolutePath(DawnBridge.getApp().getExternalFilesDir(Environment.DIRECTORY_PODCASTS));
    }

    /**
     * Return 外部应用铃声路径
     * /storage/emulated/0/Android/data/package/files/Ringtones.
     *
     * @return 外部应用铃声路径
     */
    public static String getExternalAppRingtonesPath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        return getAbsolutePath(DawnBridge.getApp().getExternalFilesDir(Environment.DIRECTORY_RINGTONES));
    }

    /**
     * Return 外部应用警报路径
     * /storage/emulated/0/Android/data/package/files/Alarms.
     *
     * @return 外部应用警报路径
     */
    public static String getExternalAppAlarmsPath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        return getAbsolutePath(DawnBridge.getApp().getExternalFilesDir(Environment.DIRECTORY_ALARMS));
    }

    /**
     * Return 外部应用通知路径
     * /storage/emulated/0/Android/data/package/files/Notifications.
     *
     * @return 外部应用通知路径
     */
    public static String getExternalAppNotificationsPath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        return getAbsolutePath(DawnBridge.getApp().getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS));
    }

    /**
     * Return 外部应用图片路径 /storage/emulated/0/Android/data/package/files/Pictures.
     *
     * @return 外部应用图片路径
     */
    public static String getExternalAppPicturesPath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        return getAbsolutePath(DawnBridge.getApp().getExternalFilesDir(Environment.DIRECTORY_PICTURES));
    }

    /**
     * Return 外部应用电影路径
     * /storage/emulated/0/Android/data/package/files/Movies.
     *
     * @return 外部应用电影路径
     */
    public static String getExternalAppMoviesPath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        return getAbsolutePath(DawnBridge.getApp().getExternalFilesDir(Environment.DIRECTORY_MOVIES));
    }

    /**
     * Return 外部应用下载路径
     * /storage/emulated/0/Android/data/package/files/Download.
     *
     * @return 外部应用下载路径
     */
    public static String getExternalAppDownloadPath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        return getAbsolutePath(DawnBridge.getApp().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
    }

    /**
     * Return 外部应用程序 Dcim 路径
     * /storage/emulated/0/Android/data/package/files/DCIM.
     *
     * @return 外部应用程序 Dcim 路径
     */
    public static String getExternalAppDcimPath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        return getAbsolutePath(DawnBridge.getApp().getExternalFilesDir(Environment.DIRECTORY_DCIM));
    }

    /**
     * Return 外部应用文档路径 /storage/emulated/0/Android/data/package/files/Documents.
     *
     * @return 外部应用文档路径
     */
    public static String getExternalAppDocumentsPath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        return getAbsolutePath(DawnBridge.getApp().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS));
    }

    /**
     * Return 外部应用程序 Obb 路径
     * /storage/emulated/0/Android/obb/package.
     *
     * @return 外部应用程序 Obb 路径
     */
    public static String getExternalAppObbPath() {
        if (!DawnBridge.isSDCardEnableByEnvironment()) {
            return "";
        }
        return getAbsolutePath(DawnBridge.getApp().getObbDir());
    }

    public static String getRootPathExternalFirst() {
        String rootPath = getExternalStoragePath();
        if (TextUtils.isEmpty(rootPath)) {
            rootPath = getRootPath();
        }
        return rootPath;
    }

    public static String getAppDataPathExternalFirst() {
        String appDataPath = getExternalAppDataPath();
        if (TextUtils.isEmpty(appDataPath)) {
            appDataPath = getInternalAppDataPath();
        }
        return appDataPath;
    }

    public static String getFilesPathExternalFirst() {
        String filePath = getExternalAppFilesPath();
        if (TextUtils.isEmpty(filePath)) {
            filePath = getInternalAppFilesPath();
        }
        return filePath;
    }

    public static String getCachePathExternalFirst() {
        String appPath = getExternalAppCachePath();
        if (TextUtils.isEmpty(appPath)) {
            appPath = getInternalAppCachePath();
        }
        return appPath;
    }

    private static String getAbsolutePath(final File file) {
        if (file == null) {
            return "";
        }
        return file.getAbsolutePath();
    }
}
