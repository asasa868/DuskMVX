package com.lzq.dawn.util.app

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Process
import com.lzq.dawn.DawnBridge
import java.io.File
import kotlin.system.exitProcess

/**
 * @Name :AppUtils
 * @Time :2022/7/12 13:58
 * @Author :  Lzq
 * @Desc : app utils
 */
object AppUtils {
    /**
     * 安装app
     * api大于25 必须请求权限
     * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
     *
     * @param file The file.
     */
    @JvmStatic
    fun installApp(file: File?) {
        val installAppIntent: Intent = DawnBridge.getInstallAppIntent(file) ?: return
        DawnBridge.getApp().startActivity(installAppIntent)
    }

    /**
     * 安装app
     * api大于25 必须请求权限
     * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
     *
     * @param uri The uri.
     */
    @JvmStatic
    fun installApp(uri: Uri?) {
        val installAppIntent: Intent = DawnBridge.getInstallAppIntent(uri) ?: return
        DawnBridge.getApp().startActivity(installAppIntent)
    }

    /**
     * 卸载app
     * api大于25 必须请求权限
     * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
     *
     * @param packageName 包名
     */
    @JvmStatic
    fun uninstallApp(packageName: String?) {
        if (DawnBridge.isSpace(packageName)) {
            return
        }
        DawnBridge.getApp().startActivity(DawnBridge.getUninstallAppIntent(packageName))
    }

    /**
     * 返回是否安装了应用程序。
     *
     * @param pkgName 包名
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isAppInstalled(pkgName: String?): Boolean {
        if (DawnBridge.isSpace(pkgName)) {
            return false
        }
        val pm: PackageManager = DawnBridge.getApp().packageManager
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getApplicationInfo(pkgName?:"", PackageManager.ApplicationInfoFlags.of(0)).enabled
            } else {
                pm.getApplicationInfo(pkgName?:"", 0).enabled
            }
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
    
    @JvmStatic
    val isAppDebug: Boolean
        /**
         * 返回是否为调试应用程序。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = isAppDebug(DawnBridge.getApp().packageName)

    /**
     * 返回是否为调试应用程序。
     *
     * @param packageName 包名.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isAppDebug(packageName: String?): Boolean {
        return if (DawnBridge.isSpace(packageName)) {
            false
        } else try {
            val pm: PackageManager = DawnBridge.getApp().packageManager
            var ai: ApplicationInfo? = null
            ai = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getApplicationInfo(packageName?:"", PackageManager.ApplicationInfoFlags.of(0))
            } else {
                pm.getApplicationInfo(packageName?:"", 0)
            }
            ai.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    @JvmStatic
    val isAppSystem: Boolean
        /**
         * 返回是否为系统应用程序。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = isAppSystem(DawnBridge.getApp().packageName)

    /**
     * 返回是否为系统应用程序.
     *
     * @param packageName 包名
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isAppSystem(packageName: String?): Boolean {
        return if (DawnBridge.isSpace(packageName)) {
            false
        } else try {
            val pm: PackageManager = DawnBridge.getApp().packageManager
            val ai: ApplicationInfo = pm.getApplicationInfo(packageName?:"", 0)
            ai.flags and ApplicationInfo.FLAG_SYSTEM != 0
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    @JvmStatic
    val isAppForeground: Boolean
        /**
         * 返回app 是否在前台
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = DawnBridge.isAppForeground()

    /**
     * 返回app 是否在前台
     * api大于21 需要有权限
     * `<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />`
     *
     * @param pkgName 包名
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isAppForeground(pkgName: String): Boolean {
        return !DawnBridge.isSpace(pkgName) && pkgName == DawnBridge.getForegroundProcessName()
    }

    /**
     * 返回应用程序是否正在运行。
     *
     * @param pkgName 包名
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isAppRunning(pkgName: String): Boolean {
        if (DawnBridge.isSpace(pkgName)) {
            return false
        }
        val am: ActivityManager =
            DawnBridge.getApp().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val taskInfo: List<ActivityManager.RunningTaskInfo> = am.getRunningTasks(Int.MAX_VALUE)
        if (taskInfo.isNotEmpty()) {
            for (aInfo in taskInfo) {
                if (aInfo.baseActivity != null) {
                    if (pkgName == aInfo.baseActivity!!.packageName) {
                        return true
                    }
                }
            }
        }
        val serviceInfo: List<ActivityManager.RunningServiceInfo> = am.getRunningServices(Int.MAX_VALUE)
        if (serviceInfo.isNotEmpty()) {
            for (aInfo in serviceInfo) {
                if (pkgName == aInfo.service.packageName) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 启动app
     *
     * @param packageName 包名
     */
    @JvmStatic
    fun launchApp(packageName: String?) {
        if (DawnBridge.isSpace(packageName)) {
            return
        }
        val launchAppIntent: Intent = DawnBridge.getLaunchAppIntent(packageName)
        DawnBridge.getApp().startActivity(launchAppIntent)
    }
    /**
     * 重启app
     *
     * @param isKillProcess 是否杀死进程
     */
    /**
     * 重启app
     */
    @JvmOverloads
    @JvmStatic
    fun relaunchApp(isKillProcess: Boolean = false) {
        val intent: Intent = DawnBridge.getLaunchAppIntent(DawnBridge.getApp().packageName)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
        )
        DawnBridge.getApp().startActivity(intent)
        if (!isKillProcess) {
            return
        }
        Process.killProcess(Process.myPid())
        exitProcess(0)
    }
    /**
     * 启动app的详细信息设置。
     *
     * @param pkgName 包名
     */
    /**
     * 启动app的详细信息设置。
     */
    @JvmStatic
    @JvmOverloads
    fun launchAppDetailsSettings(pkgName: String? = DawnBridge.getApp().packageName) {
        if (DawnBridge.isSpace(pkgName)) {
            return
        }
        val intent: Intent = DawnBridge.getLaunchAppDetailsSettingsIntent(pkgName, true)
        if (!DawnBridge.isIntentAvailable(intent)) {
            return
        }
        DawnBridge.getApp().startActivity(intent)
    }
    /**
     * 启动应用程序的详细信息设置。
     *
     * @param activity    activity.
     * @param requestCode requestCode.
     * @param pkgName     包名
     */
    /**
     * 启动应用程序的详细信息设置。
     *
     * @param activity    activity.
     * @param requestCode requestCode.
     */
    @JvmStatic
    @JvmOverloads
    fun launchAppDetailsSettings(
        activity: Activity?,
        requestCode: Int,
        pkgName: String? = DawnBridge.getApp().packageName
    ) {
        if (activity == null || DawnBridge.isSpace(pkgName)) {
            return
        }
        val intent: Intent = DawnBridge.getLaunchAppDetailsSettingsIntent(pkgName, false)
        if (!DawnBridge.isIntentAvailable(intent)) {
            return
        }
        activity.startActivityForResult(intent, requestCode)
    }

    /**
     * 退出app
     */
    @JvmStatic
    fun exitApp() {
        DawnBridge.finishAllActivities()
        exitProcess(0)
    }

    @JvmStatic
    val appIcon: Drawable?
        /**
         * 返回app的icon
         *
         * @return icon
         */
        get() = getAppIcon(DawnBridge.getApp().packageName)

    /**
     * 返回app的icon
     *
     * @param packageName 包名
     * @return icon
     */
    @JvmStatic
    fun getAppIcon(packageName: String?): Drawable? {
        return if (DawnBridge.isSpace(packageName)) {
            null
        } else try {
            val pm: PackageManager = DawnBridge.getApp().packageManager
            val pi: PackageInfo = pm.getPackageInfo(packageName?:"", 0)
            if (pi == null) null else pi.applicationInfo.loadIcon(pm)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    @JvmStatic
    val appIconId: Int
        /**
         * 返回app icon 的资源id
         *
         * @return 返回app icon 的资源id
         */
        get() = getAppIconId(DawnBridge.getApp().packageName)

    /**
     * 返回app icon 的资源id
     *
     * @param packageName 包名
     * @return 返回app icon 的资源id
     */
    @JvmStatic
    fun getAppIconId(packageName: String?): Int {
        return if (DawnBridge.isSpace(packageName)) {
            0
        } else try {
            val pm: PackageManager = DawnBridge.getApp().packageManager
            val pi: PackageInfo = pm.getPackageInfo(packageName?:"", 0)
            pi?.applicationInfo?.icon ?: 0
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            0
        }
    }

    val isFirstTimeInstall: Boolean
        /**
         * 返回是否第一次在设备上安装应用程序
         *
         * @return 返回是否第一次在设备上安装应用程序
         */
        get() = try {
            val firstInstallTime: Long = DawnBridge.getApp().packageManager.getPackageInfo(
                appPackageName, 0
            ).firstInstallTime
            val lastUpdateTime: Long = DawnBridge.getApp().packageManager.getPackageInfo(
                appPackageName, 0
            ).lastUpdateTime
            firstInstallTime == lastUpdateTime
        } catch (e: Exception) {
            false
        }
    val isAppUpgraded: Boolean
        /**
         * @return 如果之前安装了应用程序并且此应用程序是对该应用程序的更新升级，则返回 true，如果这是全新安装而不是 update/upgrade，则返回 false。
         */
        get() = try {
            val firstInstallTime: Long = DawnBridge.getApp().packageManager.getPackageInfo(
                appPackageName, 0
            ).firstInstallTime
            val lastUpdateTime: Long = DawnBridge.getApp().packageManager.getPackageInfo(
                appPackageName, 0
            ).lastUpdateTime
            firstInstallTime != lastUpdateTime
        } catch (e: Exception) {
            false
        }
    val appPackageName: String
        /**
         * 返回app 包名
         *
         * @return 包名
         */
        get() = DawnBridge.getApp().packageName
    val appName: String
        /**
         * 返回app名字
         *
         * @return app名字
         */
        get() = getAppName(DawnBridge.getApp().packageName)

    /**
     * 返回app名字
     *
     * @param packageName 包名.
     * @return 名字
     */
    fun getAppName(packageName: String?): String {
        return if (DawnBridge.isSpace(packageName)) {
            ""
        } else try {
            val pm: PackageManager = DawnBridge.getApp().packageManager
            val pi: PackageInfo = pm.getPackageInfo(packageName?:"", 0)
            pi?.applicationInfo?.loadLabel(pm)?.toString() ?: ""
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
        }
    }

    val appPath: String
        /**
         * 返回应用程序的路径。
         *
         * @return 返回应用程序的路径。
         */
        get() = getAppPath(DawnBridge.getApp().packageName)

    /**
     * 返回应用程序的路径。
     *
     * @param packageName 包名
     * @return 返回应用程序的路径。
     */
    fun getAppPath(packageName: String?): String {
        return if (DawnBridge.isSpace(packageName)) {
            ""
        } else try {
            val pm: PackageManager = DawnBridge.getApp().packageManager
            val pi: PackageInfo = pm.getPackageInfo(packageName?:"", 0)
            if (pi == null) "" else pi.applicationInfo.sourceDir
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
        }
    }

    @JvmStatic
    val appVersionName: String
        /**
         * 返回app版本名.
         *
         * @return 版本名
         */
        get() = getAppVersionName(DawnBridge.getApp().packageName)

    /**
     * 返回app版本名。
     *
     * @param packageName 包名
     * @return 版本名
     */
    fun getAppVersionName(packageName: String?): String {
        return if (DawnBridge.isSpace(packageName)) {
            ""
        } else try {
            val pm: PackageManager = DawnBridge.getApp().packageManager
            val pi: PackageInfo = pm.getPackageInfo(packageName?:"", 0)
            if (pi == null) "" else pi.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
        }
    }

    @JvmStatic
    val appVersionCode: Int
        /**
         * 返回app版本号
         *
         * @return 版本号
         */
        get() = getAppVersionCode(DawnBridge.getApp().packageName)

    /**
     * 返回app版本号
     *
     * @param packageName 包名
     * @return 版本号
     */
    fun getAppVersionCode(packageName: String?): Int {
        return if (DawnBridge.isSpace(packageName)) {
            -1
        } else try {
            val pm: PackageManager = DawnBridge.getApp().packageManager
            val pi: PackageInfo = pm.getPackageInfo(packageName?:"", 0)
            pi?.versionCode ?: -1
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            -1
        }
    }

    val appSignatures: Array<Signature>?
        /**
         * 返回应用程序的签名。
         *
         * @return 返回应用程序的签名。
         */
        get() = getAppSignatures(DawnBridge.getApp().packageName)

    /**
     * 返回应用程序的签名。
     *
     * @param packageName 包名
     * @return 返回应用程序的签名。
     */
    fun getAppSignatures(packageName: String?): Array<Signature>? {
        return if (DawnBridge.isSpace(packageName)) {
            null
        } else try {
            val pm: PackageManager = DawnBridge.getApp().packageManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val pi: PackageInfo =
                    pm.getPackageInfo(packageName?:"", PackageManager.GET_SIGNING_CERTIFICATES) ?: return null
                val signingInfo = pi.signingInfo
                if (signingInfo.hasMultipleSigners()) {
                    signingInfo.apkContentsSigners
                } else {
                    signingInfo.signingCertificateHistory
                }
            } else {
                val pi: PackageInfo =
                    pm.getPackageInfo(packageName?:"", PackageManager.GET_SIGNATURES) ?: return null
                pi.signatures
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 返回应用程序的签名。
     *
     * @param file 文件路径
     * @return 返回应用程序的签名。
     */
    fun getAppSignatures(file: File?): Array<Signature>? {
        if (file == null) {
            return null
        }
        val pm: PackageManager = DawnBridge.getApp().packageManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val pi: PackageInfo =
                pm.getPackageArchiveInfo(file.absolutePath, PackageManager.GET_SIGNING_CERTIFICATES)
                    ?: return null
            val signingInfo = pi.signingInfo
            if (signingInfo.hasMultipleSigners()) {
                signingInfo.apkContentsSigners
            } else {
                signingInfo.signingCertificateHistory
            }
        } else {
            val pi: PackageInfo =
                pm.getPackageArchiveInfo(file.absolutePath, PackageManager.GET_SIGNATURES) ?: return null
            pi.signatures
        }
    }

    val appSignaturesSHA1: List<String>
        /**
         * 返回应用程序对 SHA1 值的签名。
         *
         * @return 返回应用程序对 SHA1 值的签名。
         */
        get() = getAppSignaturesSHA1(DawnBridge.getApp().packageName)

    /**
     * 返回应用程序对 SHA1 值的签名。
     *
     * @param packageName 包名
     * @return 返回应用程序对 SHA1 值的签名。
     */
    fun getAppSignaturesSHA1(packageName: String): List<String> {
        return getAppSignaturesHash(packageName, "SHA1")
    }

    val appSignaturesSHA256: List<String>
        /**
         * 返回应用程序的 SHA256 值签名。
         *
         * @return 返回应用程序的 SHA256 值签名。
         */
        get() = getAppSignaturesSHA256(DawnBridge.getApp().packageName)

    /**
     * 返回应用程序的 SHA256 值签名。
     *
     * @param packageName 包名
     * @return 返回应用程序的 SHA256 值签名。
     */
    fun getAppSignaturesSHA256(packageName: String): List<String> {
        return getAppSignaturesHash(packageName, "SHA256")
    }

    val appSignaturesMD5: List<String>
        /**
         * 返回应用程序对 MD5 值的签名。
         *
         * @return 返回应用程序对 MD5 值的签名。
         */
        get() = getAppSignaturesMD5(DawnBridge.getApp().packageName)

    /**
     * 返回应用程序对 MD5 值的签名。
     *
     * @param packageName 包名
     * @return 返回应用程序对 MD5 值的签名。
     */
    fun getAppSignaturesMD5(packageName: String): List<String> {
        return getAppSignaturesHash(packageName, "MD5")
    }

    val appUid: Int
        /**
         * 返回应用程序的用户 ID。
         *
         * @return 返回应用程序的用户 ID。
         */
        get() = getAppUid(DawnBridge.getApp().packageName)

    /**
     * 返回应用程序的用户 ID。
     *
     * @param pkgName 包名
     * @return 返回应用程序的用户 ID。
     */
    fun getAppUid(pkgName: String?): Int {
        return try {
            DawnBridge.getApp().packageManager.getApplicationInfo(pkgName?:"", 0).uid
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    private fun getAppSignaturesHash(packageName: String, algorithm: String): List<String> {
        val result = ArrayList<String>()
        if (DawnBridge.isSpace(packageName)) {
            return result
        }
        val signatures = getAppSignatures(packageName)
        if (signatures == null || signatures.size <= 0) {
            return result
        }
        for (signature in signatures) {
            val hash: String =
                DawnBridge.bytes2HexString(DawnBridge.hashTemplate(signature.toByteArray(), algorithm))
                    .replace("(?<=[0-9A-F]{2})[0-9A-F]{2}".toRegex(), ":$0")
            result.add(hash)
        }
        return result
    }

    val appInfo: AppInfo?
        /**
         * 返回应用程序的信息
         *
         *  * name of package
         *  * icon
         *  * name
         *  * path of package
         *  * version name
         *  * version code
         *  * is system
         *
         *
         * @return 返回应用程序的信息
         */
        get() = getAppInfo(DawnBridge.getApp().packageName)

    /**
     * 返回应用程序的信息
     *
     *  * name of package
     *  * icon
     *  * name
     *  * path of package
     *  * version name
     *  * version code
     *  * is system
     *
     *
     * @param packageName 包名
     * @return 返回应用程序的信息
     */
    fun getAppInfo(packageName: String?): AppInfo? {
        return try {
            val pm: PackageManager = DawnBridge.getApp().packageManager ?: return null
            getBean(pm, pm.getPackageInfo(packageName?:"", 0))
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    val appsInfo: List<AppInfo>
        /**
         * 返回应用程序的信息
         *
         * @return 返回应用程序的信息
         */
        get() {
            val list: MutableList<AppInfo> = ArrayList()
            val pm: PackageManager = DawnBridge.getApp().packageManager ?: return list
            val installedPackages: List<PackageInfo> = pm.getInstalledPackages(0)
            for (pi in installedPackages) {
                val ai = getBean(pm, pi) ?: continue
                list.add(ai)
            }
            return list
        }

    /**
     * 返回应用程序的包信息
     *
     * @return 返回应用程序的包信息
     */
    fun getApkInfo(apkFile: File?): AppInfo? {
        return if (apkFile == null || !apkFile.isFile || !apkFile.exists()) {
            null
        } else getApkInfo(apkFile.absolutePath)
    }

    /**
     * 返回应用程序的包信息。
     *
     * @return 返回应用程序的包信息。
     */
    fun getApkInfo(apkFilePath: String?): AppInfo? {
        if (DawnBridge.isSpace(apkFilePath)) {
            return null
        }
        val pm: PackageManager = DawnBridge.getApp().packageManager ?: return null
        val pi: PackageInfo = pm.getPackageArchiveInfo(apkFilePath?:"", 0) ?: return null
        val appInfo = pi.applicationInfo
        appInfo.sourceDir = apkFilePath
        appInfo.publicSourceDir = apkFilePath
        return getBean(pm, pi)
    }

    val isFirstTimeInstalled: Boolean
        /**
         * 返回应用程序是否首次安装。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = try {
            val pi: PackageInfo = DawnBridge.getApp().packageManager
                .getPackageInfo(DawnBridge.getApp().packageName, 0)
            pi.firstInstallTime == pi.lastUpdateTime
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            true
        }

    private fun getBean(pm: PackageManager, pi: PackageInfo?): AppInfo? {
        if (pi == null) {
            return null
        }
        val versionName = pi.versionName
        val versionCode = pi.versionCode
        val packageName = pi.packageName
        val ai =
            pi.applicationInfo ?: return AppInfo(packageName, "", null, "", versionName, versionCode, false)
        val name = ai.loadLabel(pm).toString()
        val icon = ai.loadIcon(pm)
        val packagePath = ai.sourceDir
        val isSystem = ApplicationInfo.FLAG_SYSTEM and ai.flags != 0
        return AppInfo(packageName, name, icon, packagePath, versionName, versionCode, isSystem)
    }
}