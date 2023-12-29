package com.lzq.dawn.util.permission.task

import android.Manifest
import android.os.Build
import android.os.Environment
import android.provider.Settings
import com.lzq.dawn.util.permission.PermissionBuilder
import com.lzq.dawn.util.permission.PermissionL
import com.lzq.dawn.util.permission.util.AndroidVersion
import java.util.ArrayList


/**
 * className :BaseTask
 * createTime :2022/5/18 14:04
 * @Author :  Lzq
 * 这里使用了internal 查阅资料得知  internal 是一个访问控制关键字，只有当前module可以访问
 * 这里使用了 ?: 符号  查阅资料得知  对象A ?: 对象B 表达式，意思为，当对象 A值为 null 时，那么它就会返回后面的对象 B。
 */
internal abstract class BaseTask(var builder: PermissionBuilder) : ChainTask {

    var nextTask: ChainTask? = null

    override fun finish() {

        //如果有下一个任务 就继续运行
        //没有就结束掉
        nextTask?.request() ?: run {
            val deniedList: MutableList<String> = ArrayList()
            deniedList.addAll(builder.deniedPermissions)
            deniedList.addAll(builder.permanentDeniedPermissions)
            deniedList.addAll(builder.permissionsWontRequest)
            if (builder.isRequestBackgroundLocationPermission()) {
                if (PermissionL.isGranted(
                        builder.activity, BackgroundLocationTask.ACCESS_BACKGROUND_LOCATION
                    )
                ) {
                    builder.grantedPermissions.add(BackgroundLocationTask.ACCESS_BACKGROUND_LOCATION)
                } else {
                    deniedList.add(BackgroundLocationTask.ACCESS_BACKGROUND_LOCATION)
                }
            }
            if (builder.isRequestSystemWindowPermission()
                && AndroidVersion.isAndroid6()
                && builder.targetSdkVersion >= AndroidVersion.ANDROID_6) {
                if (Settings.canDrawOverlays(builder.activity)) {
                    builder.grantedPermissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW)
                } else {
                    deniedList.add(Manifest.permission.SYSTEM_ALERT_WINDOW)
                }
            }
            if (builder.isRequestWriterPermission()
                && AndroidVersion.isAndroid6() && builder.targetSdkVersion >= AndroidVersion.ANDROID_6) {
                if (Settings.System.canWrite(builder.activity)) {
                    builder.grantedPermissions.add(Manifest.permission.WRITE_SETTINGS)
                } else {
                    deniedList.add(Manifest.permission.WRITE_SETTINGS)
                }
            }
            if (builder.isRequestExternalStoragePermission()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                    Environment.isExternalStorageManager()) {
                    builder.grantedPermissions.add(ExternalStorageTask.MANAGE_EXTERNAL_STORAGE)
                } else {
                    deniedList.add(ExternalStorageTask.MANAGE_EXTERNAL_STORAGE)
                }
            }
            if (builder.isRequestInstallPermission()) {
                if (AndroidVersion.isAndroid8() && builder.targetSdkVersion >= AndroidVersion.ANDROID_8) {
                    if (builder.activity.packageManager.canRequestPackageInstalls()) {
                        builder.grantedPermissions.add(InstallTask.REQUEST_INSTALL_PACKAGES)
                    } else {
                        deniedList.add(InstallTask.REQUEST_INSTALL_PACKAGES)
                    }
                } else {
                    deniedList.add(InstallTask.REQUEST_INSTALL_PACKAGES)
                }
            }
            if (builder.isRequestNotificationPermission()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                    Environment.isExternalStorageManager()) {
                    builder.grantedPermissions.add(ExternalStorageTask.MANAGE_EXTERNAL_STORAGE)
                } else {
                    deniedList.add(ExternalStorageTask.MANAGE_EXTERNAL_STORAGE)
                }
            }
            if (builder.result != null) {
                builder.result!!.onResult(deniedList.isEmpty(), ArrayList(builder.grantedPermissions), deniedList)
            }

            builder.endRequest()
        }
    }
}