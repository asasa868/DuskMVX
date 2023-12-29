package com.lzq.dawn.util.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.SigningInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lzq.dawn.DawnBridge;
import com.lzq.dawn.util.shell.CommandResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Name :AppUtils
 * @Time :2022/7/12 13:58
 * @Author :  Lzq
 * @Desc : app utils
 */
public final class AppUtils {
    private AppUtils() {
    }

    /**
     * 安装app
     * api大于25 必须请求权限
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}
     *
     * @param file The file.
     */
    public static void installApp(final File file) {
        Intent installAppIntent = DawnBridge.getInstallAppIntent(file);
        if (installAppIntent == null) {
            return;
        }
        DawnBridge.getApp().startActivity(installAppIntent);
    }

    /**
     * 安装app
     * api大于25 必须请求权限
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}
     *
     * @param uri The uri.
     */
    public static void installApp(final Uri uri) {
        Intent installAppIntent = DawnBridge.getInstallAppIntent(uri);
        if (installAppIntent == null) {
            return;
        }
        DawnBridge.getApp().startActivity(installAppIntent);
    }

    /**
     * 卸载app
     * api大于25 必须请求权限
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}
     *
     * @param packageName 包名
     */
    public static void uninstallApp(final String packageName) {
        if (DawnBridge.isSpace(packageName)) {
            return;
        }
        DawnBridge.getApp().startActivity(DawnBridge.getUninstallAppIntent(packageName));
    }

    /**
     * 返回是否安装了应用程序。
     *
     * @param pkgName 包名
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAppInstalled(final String pkgName) {
        if (DawnBridge.isSpace(pkgName)) {
            return false;
        }
        PackageManager pm = DawnBridge.getApp().getPackageManager();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                return pm.getApplicationInfo(pkgName, PackageManager.ApplicationInfoFlags.of(0)).enabled;
            } else {
                return pm.getApplicationInfo(pkgName, 0).enabled;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 返回 app是否有root权限
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAppRoot() {
        CommandResult result = DawnBridge.execCmd("echo root", true);
        return result.result == 0;
    }

    /**
     * 返回是否为调试应用程序。
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAppDebug() {
        return isAppDebug(DawnBridge.getApp().getPackageName());
    }

    /**
     * 返回是否为调试应用程序。
     *
     * @param packageName 包名.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAppDebug(final String packageName) {
        if (DawnBridge.isSpace(packageName)) {
            return false;
        }
        try {
            PackageManager pm = DawnBridge.getApp().getPackageManager();
            ApplicationInfo ai = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                ai = pm.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0));
            } else {
                ai = pm.getApplicationInfo(packageName, 0);
            }
            return (ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 返回是否为系统应用程序。
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAppSystem() {
        return isAppSystem(DawnBridge.getApp().getPackageName());
    }

    /**
     * 返回是否为系统应用程序.
     *
     * @param packageName 包名
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAppSystem(final String packageName) {
        if (DawnBridge.isSpace(packageName)) {
            return false;
        }
        try {
            PackageManager pm = DawnBridge.getApp().getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            return (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 返回app 是否在前台
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAppForeground() {
        return DawnBridge.isAppForeground();
    }

    /**
     * 返回app 是否在前台
     * api大于21 需要有权限
     * {@code <uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />}
     *
     * @param pkgName 包名
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAppForeground(@NonNull final String pkgName) {
        return !DawnBridge.isSpace(pkgName) && pkgName.equals(DawnBridge.getForegroundProcessName());
    }

    /**
     * 返回应用程序是否正在运行。
     *
     * @param pkgName 包名
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAppRunning(final String pkgName) {
        if (DawnBridge.isSpace(pkgName)) {
            return false;
        }
        ActivityManager am = (ActivityManager) DawnBridge.getApp().getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(Integer.MAX_VALUE);
            if (taskInfo != null && taskInfo.size() > 0) {
                for (ActivityManager.RunningTaskInfo aInfo : taskInfo) {
                    if (aInfo.baseActivity != null) {
                        if (pkgName.equals(aInfo.baseActivity.getPackageName())) {
                            return true;
                        }
                    }
                }
            }
            List<ActivityManager.RunningServiceInfo> serviceInfo = am.getRunningServices(Integer.MAX_VALUE);
            if (serviceInfo != null && serviceInfo.size() > 0) {
                for (ActivityManager.RunningServiceInfo aInfo : serviceInfo) {
                    if (pkgName.equals(aInfo.service.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 启动app
     *
     * @param packageName 包名
     */
    public static void launchApp(final String packageName) {
        if (DawnBridge.isSpace(packageName)) {
            return;
        }
        Intent launchAppIntent = DawnBridge.getLaunchAppIntent(packageName);
        if (launchAppIntent == null) {
            Log.e("AppUtils", "Didn't exist launcher activity.");
            return;
        }
        DawnBridge.getApp().startActivity(launchAppIntent);
    }

    /**
     * 重启app
     */
    public static void relaunchApp() {
        relaunchApp(false);
    }

    /**
     * 重启app
     *
     * @param isKillProcess 是否杀死进程
     */
    public static void relaunchApp(final boolean isKillProcess) {
        Intent intent = DawnBridge.getLaunchAppIntent(DawnBridge.getApp().getPackageName());
        if (intent == null) {
            Log.e("AppUtils", "Didn't exist launcher activity.");
            return;
        }
        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );
        DawnBridge.getApp().startActivity(intent);
        if (!isKillProcess) {
            return;
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    /**
     * 启动app的详细信息设置。
     */
    public static void launchAppDetailsSettings() {
        launchAppDetailsSettings(DawnBridge.getApp().getPackageName());
    }

    /**
     * 启动app的详细信息设置。
     *
     * @param pkgName 包名
     */
    public static void launchAppDetailsSettings(final String pkgName) {
        if (DawnBridge.isSpace(pkgName)) {
            return;
        }
        Intent intent = DawnBridge.getLaunchAppDetailsSettingsIntent(pkgName, true);
        if (!DawnBridge.isIntentAvailable(intent)) {
            return;
        }
        DawnBridge.getApp().startActivity(intent);
    }

    /**
     * 启动应用程序的详细信息设置。
     *
     * @param activity    activity.
     * @param requestCode requestCode.
     */
    public static void launchAppDetailsSettings(final Activity activity, final int requestCode) {
        launchAppDetailsSettings(activity, requestCode, DawnBridge.getApp().getPackageName());
    }

    /**
     * 启动应用程序的详细信息设置。
     *
     * @param activity    activity.
     * @param requestCode requestCode.
     * @param pkgName     包名
     */
    public static void launchAppDetailsSettings(final Activity activity, final int requestCode, final String pkgName) {
        if (activity == null || DawnBridge.isSpace(pkgName)) {
            return;
        }
        Intent intent = DawnBridge.getLaunchAppDetailsSettingsIntent(pkgName, false);
        if (!DawnBridge.isIntentAvailable(intent)) {
            return;
        }
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 退出app
     */
    public static void exitApp() {
        DawnBridge.finishAllActivities();
        System.exit(0);
    }

    /**
     * 返回app的icon
     *
     * @return icon
     */
    @Nullable
    public static Drawable getAppIcon() {
        return getAppIcon(DawnBridge.getApp().getPackageName());
    }

    /**
     * 返回app的icon
     *
     * @param packageName 包名
     * @return icon
     */
    @Nullable
    public static Drawable getAppIcon(final String packageName) {
        if (DawnBridge.isSpace(packageName)) {
            return null;
        }
        try {
            PackageManager pm = DawnBridge.getApp().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.applicationInfo.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 返回app icon 的资源id
     *
     * @return 返回app icon 的资源id
     */
    public static int getAppIconId() {
        return getAppIconId(DawnBridge.getApp().getPackageName());
    }

    /**
     * 返回app icon 的资源id
     *
     * @param packageName 包名
     * @return 返回app icon 的资源id
     */
    public static int getAppIconId(final String packageName) {
        if (DawnBridge.isSpace(packageName)) {
            return 0;
        }
        try {
            PackageManager pm = DawnBridge.getApp().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? 0 : pi.applicationInfo.icon;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 返回是否第一次在设备上安装应用程序
     *
     * @return 返回是否第一次在设备上安装应用程序
     */
    public static boolean isFirstTimeInstall() {
        try {
            long firstInstallTime = DawnBridge.getApp().getPackageManager().getPackageInfo(getAppPackageName(), 0).firstInstallTime;
            long lastUpdateTime = DawnBridge.getApp().getPackageManager().getPackageInfo(getAppPackageName(), 0).lastUpdateTime;
            return firstInstallTime == lastUpdateTime;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @return 如果之前安装了应用程序并且此应用程序是对该应用程序的更新升级，则返回 true，如果这是全新安装而不是 update/upgrade，则返回 false。
     */
    public static boolean isAppUpgraded() {
        try {
            long firstInstallTime = DawnBridge.getApp().getPackageManager().getPackageInfo(getAppPackageName(), 0).firstInstallTime;
            long lastUpdateTime = DawnBridge.getApp().getPackageManager().getPackageInfo(getAppPackageName(), 0).lastUpdateTime;
            return firstInstallTime != lastUpdateTime;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 返回app 包名
     *
     * @return 包名
     */
    @NonNull
    public static String getAppPackageName() {
        return DawnBridge.getApp().getPackageName();
    }

    /**
     * 返回app名字
     *
     * @return app名字
     */
    @NonNull
    public static String getAppName() {
        return getAppName(DawnBridge.getApp().getPackageName());
    }

    /**
     * 返回app名字
     *
     * @param packageName 包名.
     * @return 名字
     */
    @NonNull
    public static String getAppName(final String packageName) {
        if (DawnBridge.isSpace(packageName)) {
            return "";
        }
        try {
            PackageManager pm = DawnBridge.getApp().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? "" : pi.applicationInfo.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 返回应用程序的路径。
     *
     * @return 返回应用程序的路径。
     */
    @NonNull
    public static String getAppPath() {
        return getAppPath(DawnBridge.getApp().getPackageName());
    }

    /**
     * 返回应用程序的路径。
     *
     * @param packageName 包名
     * @return 返回应用程序的路径。
     */
    @NonNull
    public static String getAppPath(final String packageName) {
        if (DawnBridge.isSpace(packageName)) {
            return "";
        }
        try {
            PackageManager pm = DawnBridge.getApp().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? "" : pi.applicationInfo.sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 返回app版本名.
     *
     * @return 版本名
     */
    @NonNull
    public static String getAppVersionName() {
        return getAppVersionName(DawnBridge.getApp().getPackageName());
    }

    /**
     * 返回app版本名。
     *
     * @param packageName 包名
     * @return 版本名
     */
    @NonNull
    public static String getAppVersionName(final String packageName) {
        if (DawnBridge.isSpace(packageName)) {
            return "";
        }
        try {
            PackageManager pm = DawnBridge.getApp().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? "" : pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 返回app版本号
     *
     * @return 版本号
     */
    public static int getAppVersionCode() {
        return getAppVersionCode(DawnBridge.getApp().getPackageName());
    }

    /**
     * 返回app版本号
     *
     * @param packageName 包名
     * @return 版本号
     */
    public static int getAppVersionCode(final String packageName) {
        if (DawnBridge.isSpace(packageName)) {
            return -1;
        }
        try {
            PackageManager pm = DawnBridge.getApp().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? -1 : pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 返回应用程序的签名。
     *
     * @return 返回应用程序的签名。
     */
    @Nullable
    public static Signature[] getAppSignatures() {
        return getAppSignatures(DawnBridge.getApp().getPackageName());
    }

    /**
     * 返回应用程序的签名。
     *
     * @param packageName 包名
     * @return 返回应用程序的签名。
     */
    @Nullable
    public static Signature[] getAppSignatures(final String packageName) {
        if (DawnBridge.isSpace(packageName)) {
            return null;
        }
        try {
            PackageManager pm = DawnBridge.getApp().getPackageManager();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES);
                if (pi == null) {
                    return null;
                }

                SigningInfo signingInfo = pi.signingInfo;
                if (signingInfo.hasMultipleSigners()) {
                    return signingInfo.getApkContentsSigners();
                } else {
                    return signingInfo.getSigningCertificateHistory();
                }
            } else {
                PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
                if (pi == null) {
                    return null;
                }

                return pi.signatures;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 返回应用程序的签名。
     *
     * @param file 文件路径
     * @return 返回应用程序的签名。
     */
    @Nullable
    public static Signature[] getAppSignatures(final File file) {
        if (file == null) {
            return null;
        }
        PackageManager pm = DawnBridge.getApp().getPackageManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            PackageInfo pi = pm.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_SIGNING_CERTIFICATES);
            if (pi == null) {
                return null;
            }

            SigningInfo signingInfo = pi.signingInfo;
            if (signingInfo.hasMultipleSigners()) {
                return signingInfo.getApkContentsSigners();
            } else {
                return signingInfo.getSigningCertificateHistory();
            }
        } else {
            PackageInfo pi = pm.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_SIGNATURES);
            if (pi == null) {
                return null;
            }

            return pi.signatures;
        }
    }

    /**
     * 返回应用程序对 SHA1 值的签名。
     *
     * @return 返回应用程序对 SHA1 值的签名。
     */
    @NonNull
    public static List<String> getAppSignaturesSHA1() {
        return getAppSignaturesSHA1(DawnBridge.getApp().getPackageName());
    }

    /**
     * 返回应用程序对 SHA1 值的签名。
     *
     * @param packageName 包名
     * @return 返回应用程序对 SHA1 值的签名。
     */
    @NonNull
    public static List<String> getAppSignaturesSHA1(final String packageName) {
        return getAppSignaturesHash(packageName, "SHA1");
    }

    /**
     * 返回应用程序的 SHA256 值签名。
     *
     * @return 返回应用程序的 SHA256 值签名。
     */
    @NonNull
    public static List<String> getAppSignaturesSHA256() {
        return getAppSignaturesSHA256(DawnBridge.getApp().getPackageName());
    }

    /**
     * 返回应用程序的 SHA256 值签名。
     *
     * @param packageName 包名
     * @return 返回应用程序的 SHA256 值签名。
     */
    @NonNull
    public static List<String> getAppSignaturesSHA256(final String packageName) {
        return getAppSignaturesHash(packageName, "SHA256");
    }

    /**
     * 返回应用程序对 MD5 值的签名。
     *
     * @return 返回应用程序对 MD5 值的签名。
     */
    @NonNull
    public static List<String> getAppSignaturesMD5() {
        return getAppSignaturesMD5(DawnBridge.getApp().getPackageName());
    }

    /**
     * 返回应用程序对 MD5 值的签名。
     *
     * @param packageName 包名
     * @return 返回应用程序对 MD5 值的签名。
     */
    @NonNull
    public static List<String> getAppSignaturesMD5(final String packageName) {
        return getAppSignaturesHash(packageName, "MD5");
    }

    /**
     * 返回应用程序的用户 ID。
     *
     * @return 返回应用程序的用户 ID。
     */
    public static int getAppUid() {
        return getAppUid(DawnBridge.getApp().getPackageName());
    }

    /**
     * 返回应用程序的用户 ID。
     *
     * @param pkgName 包名
     * @return 返回应用程序的用户 ID。
     */
    public static int getAppUid(String pkgName) {
        try {
            return DawnBridge.getApp().getPackageManager().getApplicationInfo(pkgName, 0).uid;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static List<String> getAppSignaturesHash(final String packageName, final String algorithm) {
        ArrayList<String> result = new ArrayList<>();
        if (DawnBridge.isSpace(packageName)) {
            return result;
        }
        Signature[] signatures = getAppSignatures(packageName);
        if (signatures == null || signatures.length <= 0) {
            return result;
        }
        for (Signature signature : signatures) {
            String hash = DawnBridge.bytes2HexString(DawnBridge.hashTemplate(signature.toByteArray(), algorithm))
                    .replaceAll("(?<=[0-9A-F]{2})[0-9A-F]{2}", ":$0");
            result.add(hash);
        }
        return result;
    }

    /**
     * 返回应用程序的信息
     * <ul>
     * <li>name of package</li>
     * <li>icon</li>
     * <li>name</li>
     * <li>path of package</li>
     * <li>version name</li>
     * <li>version code</li>
     * <li>is system</li>
     * </ul>
     *
     * @return 返回应用程序的信息
     */
    @Nullable
    public static AppInfo getAppInfo() {
        return getAppInfo(DawnBridge.getApp().getPackageName());
    }

    /**
     * 返回应用程序的信息
     * <ul>
     * <li>name of package</li>
     * <li>icon</li>
     * <li>name</li>
     * <li>path of package</li>
     * <li>version name</li>
     * <li>version code</li>
     * <li>is system</li>
     * </ul>
     *
     * @param packageName 包名
     * @return 返回应用程序的信息
     */
    @Nullable
    public static AppInfo getAppInfo(final String packageName) {
        try {
            PackageManager pm = DawnBridge.getApp().getPackageManager();
            if (pm == null) {
                return null;
            }
            return getBean(pm, pm.getPackageInfo(packageName, 0));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 返回应用程序的信息
     *
     * @return 返回应用程序的信息
     */
    @NonNull
    public static List<AppInfo> getAppsInfo() {
        List<AppInfo> list = new ArrayList<>();
        PackageManager pm = DawnBridge.getApp().getPackageManager();
        if (pm == null) {
            return list;
        }
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        for (PackageInfo pi : installedPackages) {
            AppInfo ai = getBean(pm, pi);
            if (ai == null) {
                continue;
            }
            list.add(ai);
        }
        return list;
    }

    /**
     * 返回应用程序的包信息
     *
     * @return 返回应用程序的包信息
     */
    @Nullable
    public static AppInfo getApkInfo(final File apkFile) {
        if (apkFile == null || !apkFile.isFile() || !apkFile.exists()) {
            return null;
        }
        return getApkInfo(apkFile.getAbsolutePath());
    }

    /**
     * 返回应用程序的包信息。
     *
     * @return 返回应用程序的包信息。
     */
    @Nullable
    public static AppInfo getApkInfo(final String apkFilePath) {
        if (DawnBridge.isSpace(apkFilePath)) {
            return null;
        }
        PackageManager pm = DawnBridge.getApp().getPackageManager();
        if (pm == null) {
            return null;
        }
        PackageInfo pi = pm.getPackageArchiveInfo(apkFilePath, 0);
        if (pi == null) {
            return null;
        }
        ApplicationInfo appInfo = pi.applicationInfo;
        appInfo.sourceDir = apkFilePath;
        appInfo.publicSourceDir = apkFilePath;
        return getBean(pm, pi);
    }


    /**
     * 返回应用程序是否首次安装。
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isFirstTimeInstalled() {
        try {
            PackageInfo pi = DawnBridge.getApp().getPackageManager().getPackageInfo(DawnBridge.getApp().getPackageName(), 0);
            return pi.firstInstallTime == pi.lastUpdateTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return true;
        }
    }

    private static AppInfo getBean(final PackageManager pm, final PackageInfo pi) {
        if (pi == null) {
            return null;
        }
        String versionName = pi.versionName;
        int versionCode = pi.versionCode;
        String packageName = pi.packageName;
        ApplicationInfo ai = pi.applicationInfo;
        if (ai == null) {
            return new AppInfo(packageName, "", null, "", versionName, versionCode, false);
        }
        String name = ai.loadLabel(pm).toString();
        Drawable icon = ai.loadIcon(pm);
        String packagePath = ai.sourceDir;
        boolean isSystem = (ApplicationInfo.FLAG_SYSTEM & ai.flags) != 0;
        return new AppInfo(packageName, name, icon, packagePath, versionName, versionCode, isSystem);
    }

}
