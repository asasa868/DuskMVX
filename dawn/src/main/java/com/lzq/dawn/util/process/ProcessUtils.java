package com.lzq.dawn.util.process;

import static android.Manifest.permission.KILL_BACKGROUND_PROCESSES;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Application;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import com.lzq.dawn.DawnBridge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Name :ProcessUtils
 * @Time :2022/8/16 14:40
 * @Author :  Lzq
 * @Desc : 进程
 */
public class ProcessUtils {
    private ProcessUtils() {
    }

    /**
     * 返回前台进程名称。
     * <p>大于 21 的目标 API 必须持有
     * {@code <uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />}</p>
     *
     * @return 前台进程名称。
     */
    public static String getForegroundProcessName() {
        ActivityManager am =
                (ActivityManager) DawnBridge.getApp().getSystemService(Context.ACTIVITY_SERVICE);
        //noinspection ConstantConditions
        List<ActivityManager.RunningAppProcessInfo> pInfo = am.getRunningAppProcesses();
        if (pInfo != null && pInfo.size() > 0) {
            for (ActivityManager.RunningAppProcessInfo aInfo : pInfo) {
                if (aInfo.importance
                        == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return aInfo.processName;
                }
            }
        }
        PackageManager pm = DawnBridge.getApp().getPackageManager();
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        List<ResolveInfo> list =
                pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        Log.i("ProcessUtils", list.toString());
        if (list.size() <= 0) {
            Log.i("ProcessUtils",
                    "getForegroundProcessName: noun of access to usage information.");
            return "";
        }
        try {// Access to usage information.
            ApplicationInfo info =
                    pm.getApplicationInfo(DawnBridge.getApp().getPackageName(), 0);
            AppOpsManager aom =
                    (AppOpsManager) DawnBridge.getApp().getSystemService(Context.APP_OPS_SERVICE);
            if (aom.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    info.uid,
                    info.packageName) != AppOpsManager.MODE_ALLOWED) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                DawnBridge.getApp().startActivity(intent);
            }
            if (aom.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    info.uid,
                    info.packageName) != AppOpsManager.MODE_ALLOWED) {
                Log.i("ProcessUtils",
                        "getForegroundProcessName: refuse to device usage stats.");
                return "";
            }
            UsageStatsManager usageStatsManager = (UsageStatsManager) DawnBridge.getApp()
                    .getSystemService(Context.USAGE_STATS_SERVICE);
            List<UsageStats> usageStatsList = null;
            if (usageStatsManager != null) {
                long endTime = System.currentTimeMillis();
                long beginTime = endTime - 86400000 * 7;
                usageStatsList = usageStatsManager
                        .queryUsageStats(UsageStatsManager.INTERVAL_BEST,
                                beginTime, endTime);
            }
            if (usageStatsList == null || usageStatsList.isEmpty()) {
                return "";
            }
            UsageStats recentStats = null;
            for (UsageStats usageStats : usageStatsList) {
                if (recentStats == null
                        || usageStats.getLastTimeUsed() > recentStats.getLastTimeUsed()) {
                    recentStats = usageStats;
                }
            }
            return recentStats == null ? null : recentStats.getPackageName();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 返回所有后台进程。
     * <p>必须持有 {@code <uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />}</p>
     *
     * @return 所有后台进程。
     */
    @RequiresPermission(KILL_BACKGROUND_PROCESSES)
    public static Set<String> getAllBackgroundProcesses() {
        ActivityManager am =
                (ActivityManager) DawnBridge.getApp().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> info = am.getRunningAppProcesses();
        Set<String> set = new HashSet<>();
        if (info != null) {
            for (ActivityManager.RunningAppProcessInfo aInfo : info) {
                Collections.addAll(set, aInfo.pkgList);
            }
        }
        return set;
    }

    /**
     * 杀死所有后台进程。
     * <p>必须持有  {@code <uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />}</p>
     *
     * @return 后台进程被杀死
     */
    @RequiresPermission(KILL_BACKGROUND_PROCESSES)
    public static Set<String> killAllBackgroundProcesses() {
        ActivityManager am =
                (ActivityManager) DawnBridge.getApp().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> info = am.getRunningAppProcesses();
        Set<String> set = new HashSet<>();
        if (info == null) {
            return set;
        }
        for (ActivityManager.RunningAppProcessInfo aInfo : info) {
            for (String pkg : aInfo.pkgList) {
                am.killBackgroundProcesses(pkg);
                set.add(pkg);
            }
        }
        info = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo aInfo : info) {
            for (String pkg : aInfo.pkgList) {
                set.remove(pkg);
            }
        }
        return set;
    }

    /**
     * 杀死后台进程
     * <p>必须持有 {@code <uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />}</p>
     *
     * @param packageName 包名
     * @return {@code true}: success<br>{@code false}: fail
     */
    @RequiresPermission(KILL_BACKGROUND_PROCESSES)
    public static boolean killBackgroundProcesses(@NonNull final String packageName) {
        ActivityManager am =
                (ActivityManager) DawnBridge.getApp().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> info = am.getRunningAppProcesses();
        if (info == null || info.size() == 0) {
            return true;
        }
        for (ActivityManager.RunningAppProcessInfo aInfo : info) {
            if (Arrays.asList(aInfo.pkgList).contains(packageName)) {
                am.killBackgroundProcesses(packageName);
            }
        }
        info = am.getRunningAppProcesses();
        if (info == null || info.size() == 0) {
            return true;
        }
        for (ActivityManager.RunningAppProcessInfo aInfo : info) {
            if (Arrays.asList(aInfo.pkgList).contains(packageName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 返回应用程序是否在主进程中运行。
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isMainProcess() {
        return DawnBridge.getApp().getPackageName().equals(getCurrentProcessName());
    }

    /**
     * 返回当前进程的名称。
     *
     * @return 当前进程的名称。
     */
    public static String getCurrentProcessName() {
        String name = getCurrentProcessNameByFile();
        if (!TextUtils.isEmpty(name)) {
            return name;
        }
        name = getCurrentProcessNameByAms();
        if (!TextUtils.isEmpty(name)) {
            return name;
        }
        name = getCurrentProcessNameByReflect();
        return name;
    }

    private static String getCurrentProcessNameByFile() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getCurrentProcessNameByAms() {
        try {
            ActivityManager am = (ActivityManager) DawnBridge.getApp().getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) {
                return "";
            }
            List<ActivityManager.RunningAppProcessInfo> info = am.getRunningAppProcesses();
            if (info == null || info.size() == 0) {
                return "";
            }
            int pid = android.os.Process.myPid();
            for (ActivityManager.RunningAppProcessInfo aInfo : info) {
                if (aInfo.pid == pid) {
                    if (aInfo.processName != null) {
                        return aInfo.processName;
                    }
                }
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }

    private static String getCurrentProcessNameByReflect() {
        String processName = "";
        try {
            Application app = DawnBridge.getApp();
            Field loadedApkField = app.getClass().getField("mLoadedApk");
            loadedApkField.setAccessible(true);
            Object loadedApk = loadedApkField.get(app);

            Field activityThreadField = loadedApk.getClass().getDeclaredField("mActivityThread");
            activityThreadField.setAccessible(true);
            Object activityThread = activityThreadField.get(loadedApk);

            Method getProcessName = activityThread.getClass().getDeclaredMethod("getProcessName");
            processName = (String) getProcessName.invoke(activityThread);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return processName;
    }
}
