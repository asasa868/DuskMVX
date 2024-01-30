package com.lzq.dawn.util.crash

import com.lzq.dawn.DawnBridge
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @Name :CrashUtils
 * @Time :2022/7/20 14:52
 * @Author :  Lzq
 * @Desc : crash
 */
object CrashUtils {
    private val FILE_SEP: String? = System.getProperty("file.separator")
    private val DEFAULT_UNCAUGHT_EXCEPTION_HANDLER = Thread.getDefaultUncaughtExceptionHandler()

    /**
     * 初始化
     *
     * @param crashDir 保存崩溃信息的目录。
     */
    fun init(crashDir: File) {
        init(crashDir.absolutePath, null)
    }

    /**
     * 初始化
     *
     * @param onCrashListener 崩溃监听器。
     */
    fun init(onCrashListener: OnCrashListener?) {
        init("", onCrashListener)
    }

    /**
     * 初始化
     *
     * @param crashDir 保存崩溃信息的目录路径。
     * @param onCrashListener 崩溃监听器。
     */
    fun init(crashDir: File, onCrashListener: OnCrashListener?) {
        init(crashDir.absolutePath, onCrashListener)
    }

    /**
     * 初始化
     *
     * @param crashDirPath 保存崩溃信息的目录路径。
     * @param onCrashListener 崩溃监听器。
     */
    @JvmOverloads
    fun init(crashDirPath: String = "", onCrashListener: OnCrashListener? = null) {
        val dirPath: String = if (DawnBridge.isSpace(crashDirPath)) {
            if (DawnBridge.isSDCardEnableByEnvironment && DawnBridge.app.getExternalFilesDir(null) != null) {
                DawnBridge.app.getExternalFilesDir(null).toString() + FILE_SEP + "crash" + FILE_SEP
            } else {
                DawnBridge.app.filesDir.toString() + FILE_SEP + "crash" + FILE_SEP
            }
        } else {
            if (crashDirPath.endsWith(FILE_SEP ?: "/")) crashDirPath else crashDirPath + FILE_SEP
        }
        Thread.setDefaultUncaughtExceptionHandler(
            getUncaughtExceptionHandler(dirPath, onCrashListener)
        )
    }

    private fun getUncaughtExceptionHandler(
        dirPath: String, onCrashListener: OnCrashListener?
    ): Thread.UncaughtExceptionHandler {
        return Thread.UncaughtExceptionHandler { t: Thread?, e: Throwable? ->
            val time = SimpleDateFormat("yyyy_MM_dd-HH_mm_ss", Locale.getDefault()).format(Date())
            val info = CrashInfo(time, e)
            val crashFile = "$dirPath$time.txt"
            DawnBridge.writeFileFromString(crashFile, info.toString(), true)
            if (t != null && e != null) {
                DEFAULT_UNCAUGHT_EXCEPTION_HANDLER?.uncaughtException(t, e)
            }
            onCrashListener?.onCrash(info)
        }
    }
}