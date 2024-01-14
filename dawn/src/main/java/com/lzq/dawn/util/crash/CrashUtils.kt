package com.lzq.dawn.util.crash;

import androidx.annotation.NonNull;

import com.lzq.dawn.DawnBridge;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Name :CrashUtils
 * @Time :2022/7/20 14:52
 * @Author :  Lzq
 * @Desc : crash
 */
public final  class CrashUtils {

    private CrashUtils(){}

    private static final String FILE_SEP = System.getProperty("file.separator");

    private static final Thread.UncaughtExceptionHandler DEFAULT_UNCAUGHT_EXCEPTION_HANDLER =
            Thread.getDefaultUncaughtExceptionHandler();

    /**
     * 初始化.
     */
    public static void init() {
        init("");
    }

    /**
     * 初始化
     *
     * @param crashDir 保存崩溃信息的目录。
     */
    public static void init(@NonNull final File crashDir) {
        init(crashDir.getAbsolutePath(), null);
    }

    /**
     * 初始化
     *
     * @param crashDirPath 保存崩溃信息的目录路径
     */
    public static void init(final String crashDirPath) {
        init(crashDirPath, null);
    }

    /**
     * 初始化
     *
     * @param onCrashListener 崩溃监听器。
     */
    public static void init(final OnCrashListener onCrashListener) {
        init("", onCrashListener);
    }

    /**
     * 初始化
     *
     * @param crashDir 保存崩溃信息的目录路径。
     * @param onCrashListener 崩溃监听器。
     */
    public static void init(@NonNull final File crashDir, final OnCrashListener onCrashListener) {
        init(crashDir.getAbsolutePath(), onCrashListener);
    }

    /**
     * 初始化
     *
     * @param crashDirPath 保存崩溃信息的目录路径。
     * @param onCrashListener 崩溃监听器。
     */
    public static void init(final String crashDirPath, final OnCrashListener onCrashListener) {
        String dirPath;
        if (DawnBridge.isSpace(crashDirPath)) {
            if (DawnBridge.isSDCardEnableByEnvironment()
                    && DawnBridge.getApp().getExternalFilesDir(null) != null) {
                dirPath = DawnBridge.getApp().getExternalFilesDir(null) + FILE_SEP + "crash" + FILE_SEP;
            } else {
                dirPath = DawnBridge.getApp().getFilesDir() + FILE_SEP + "crash" + FILE_SEP;
            }
        } else {
            dirPath = crashDirPath.endsWith(FILE_SEP) ? crashDirPath : crashDirPath + FILE_SEP;
        }
        Thread.setDefaultUncaughtExceptionHandler(
                getUncaughtExceptionHandler(dirPath, onCrashListener));
    }

    private static Thread.UncaughtExceptionHandler getUncaughtExceptionHandler(final String dirPath,
                                                                               final OnCrashListener onCrashListener) {
        return (t, e) -> {
            final String time = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(new Date());
            CrashInfo info = new CrashInfo(time, e);
            final String crashFile = dirPath + time + ".txt";
            DawnBridge.writeFileFromString(crashFile, info.toString(), true);

            if (DEFAULT_UNCAUGHT_EXCEPTION_HANDLER != null) {
                DEFAULT_UNCAUGHT_EXCEPTION_HANDLER.uncaughtException(t, e);
            }
            if (onCrashListener != null) {
                onCrashListener.onCrash(info);
            }
        };
    }
}
