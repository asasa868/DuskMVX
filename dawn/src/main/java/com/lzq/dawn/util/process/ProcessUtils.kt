package com.lzq.dawn.util.process

import android.Manifest.permission
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Process
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresPermission
import com.lzq.dawn.DawnBridge
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.Arrays
import java.util.Collections

/**
 * @Name :ProcessUtils
 * @Time :2022/8/16 14:40
 * @Author :  Lzq
 * @Desc : 进程
 */
object ProcessUtils {
    @JvmStatic
    val foregroundProcessName: String?
        /**
         * 返回前台进程名称。
         *
         * 大于 21 的目标 API 必须持有
         * `<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />`
         *
         * @return 前台进程名称。
         */
        get() {
            val am = DawnBridge.app.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val pInfo = am.runningAppProcesses
            if (pInfo != null && pInfo.size > 0) {
                for (aInfo in pInfo) {
                    if (aInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return aInfo.processName
                    }
                }
            }
            val pm = DawnBridge.app.packageManager
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            val list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            Log.i("ProcessUtils", list.toString())
            if (list.size <= 0) {
                Log.i(
                    "ProcessUtils", "getForegroundProcessName: noun of access to usage information."
                )
                return ""
            }
            try { // Access to usage information.
                val info = pm.getApplicationInfo(DawnBridge.app.packageName, 0)
                val aom = DawnBridge.app.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                if (aom.checkOpNoThrow(
                        AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName
                    ) != AppOpsManager.MODE_ALLOWED
                ) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    DawnBridge.app.startActivity(intent)
                }
                if (aom.checkOpNoThrow(
                        AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName
                    ) != AppOpsManager.MODE_ALLOWED
                ) {
                    Log.i(
                        "ProcessUtils", "getForegroundProcessName: refuse to device usage stats."
                    )
                    return ""
                }
                val usageStatsManager =
                    DawnBridge.app.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                var usageStatsList: List<UsageStats>? = null
                if (usageStatsManager != null) {
                    val endTime = System.currentTimeMillis()
                    val beginTime = endTime - 86400000 * 7
                    usageStatsList = usageStatsManager.queryUsageStats(
                            UsageStatsManager.INTERVAL_BEST, beginTime, endTime
                        )
                }
                if (usageStatsList == null || usageStatsList.isEmpty()) {
                    return ""
                }
                var recentStats: UsageStats? = null
                for (usageStats in usageStatsList) {
                    if (recentStats == null || usageStats.lastTimeUsed > recentStats.lastTimeUsed) {
                        recentStats = usageStats
                    }
                }
                return recentStats?.packageName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return ""
        }

    @get:RequiresPermission(permission.KILL_BACKGROUND_PROCESSES)
    val allBackgroundProcesses: Set<String>
        /**
         * 返回所有后台进程。
         *
         * 必须持有 `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />`
         *
         * @return 所有后台进程。
         */
        get() {
            val am = DawnBridge.app.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val info = am.runningAppProcesses
            val set: MutableSet<String> = mutableSetOf()
            if (info != null) {
                for (aInfo in info) {
                    Collections.addAll(set, *aInfo.pkgList)
                }
            }
            return set
        }

    /**
     * 杀死所有后台进程。
     *
     * 必须持有  `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />`
     *
     * @return 后台进程被杀死
     */
    @RequiresPermission(permission.KILL_BACKGROUND_PROCESSES)
    fun killAllBackgroundProcesses(): Set<String> {
        val am = DawnBridge.app.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        var info = am.runningAppProcesses
        val set: MutableSet<String> = HashSet()
        if (info == null) {
            return set
        }
        for (aInfo in info) {
            for (pkg in aInfo.pkgList) {
                am.killBackgroundProcesses(pkg)
                set.add(pkg)
            }
        }
        info = am.runningAppProcesses
        for (aInfo in info) {
            for (pkg in aInfo.pkgList) {
                set.remove(pkg)
            }
        }
        return set
    }

    /**
     * 杀死后台进程
     *
     * 必须持有 `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />`
     *
     * @param packageName 包名
     * @return `true`: success<br></br>`false`: fail
     */
    @RequiresPermission(permission.KILL_BACKGROUND_PROCESSES)
    fun killBackgroundProcesses(packageName: String): Boolean {
        val am = DawnBridge.app.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        var info = am.runningAppProcesses
        if (info == null || info.size == 0) {
            return true
        }
        for (aInfo in info) {
            if (Arrays.asList(*aInfo.pkgList).contains(packageName)) {
                am.killBackgroundProcesses(packageName)
            }
        }
        info = am.runningAppProcesses
        if (info == null || info.size == 0) {
            return true
        }
        for (aInfo in info) {
            if (Arrays.asList(*aInfo.pkgList).contains(packageName)) {
                return false
            }
        }
        return true
    }

    @JvmStatic
    val isMainProcess: Boolean
        /**
         * 返回应用程序是否在主进程中运行。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = DawnBridge.app.packageName == currentProcessName
    @JvmStatic
    val currentProcessName: String
        /**
         * 返回当前进程的名称。
         *
         * @return 当前进程的名称。
         */
        get() {
            var name = currentProcessNameByFile
            if (!TextUtils.isEmpty(name)) {
                return name
            }
            name = currentProcessNameByAms
            if (!TextUtils.isEmpty(name)) {
                return name
            }
            name = currentProcessNameByReflect
            return name
        }
    private val currentProcessNameByFile: String
        private get() = try {
            val file = File("/proc/" + Process.myPid() + "/" + "cmdline")
            val mBufferedReader = BufferedReader(FileReader(file))
            val processName = mBufferedReader.readLine().trim { it <= ' ' }
            mBufferedReader.close()
            processName
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    private val currentProcessNameByAms: String
        private get() {
            try {
                val am = DawnBridge.app.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                    ?: return ""
                val info = am.runningAppProcesses
                if (info == null || info.size == 0) {
                    return ""
                }
                val pid = Process.myPid()
                for (aInfo in info) {
                    if (aInfo.pid == pid) {
                        if (aInfo.processName != null) {
                            return aInfo.processName
                        }
                    }
                }
            } catch (e: Exception) {
                return ""
            }
            return ""
        }
    private val currentProcessNameByReflect: String
        private get() {
            var processName = ""
            try {
                val app = DawnBridge.app
                val loadedApkField = app.javaClass.getField("mLoadedApk")
                loadedApkField.isAccessible = true
                val loadedApk = loadedApkField[app]
                val activityThreadField = loadedApk.javaClass.getDeclaredField("mActivityThread")
                activityThreadField.isAccessible = true
                val activityThread = activityThreadField[loadedApk]
                val getProcessName = activityThread.javaClass.getDeclaredMethod("getProcessName")
                processName = getProcessName.invoke(activityThread) as String
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return processName
        }
}