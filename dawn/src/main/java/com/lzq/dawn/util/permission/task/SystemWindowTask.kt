package com.lzq.dawn.util.permission.task

import android.Manifest
import android.provider.Settings
import com.lzq.dawn.util.permission.PermissionBuilder
import com.lzq.dawn.util.permission.util.AndroidVersion


/**
 * className :NormalTask
 * createTime :2022/5/18 16:03
 * @Author :  Lzq
 * 一般权限 请求类
 */
internal class SystemWindowTask constructor(builder: PermissionBuilder) : BaseTask(builder) {

    override fun request() {
        if (builder.isRequestSystemWindowPermission()) {
            if (AndroidVersion.isAndroid6() && builder.targetSdkVersion >= AndroidVersion.ANDROID_6) {
                if (Settings.canDrawOverlays(builder.activity)) {
                    //已经有权限
                    finish()
                    return
                }
                finish()
            } else {
                builder.grantedPermissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW)
                builder.specialPermissions.remove(Manifest.permission.SYSTEM_ALERT_WINDOW)
                finish()
            }
        } else {
            finish()
        }
    }

    override fun requestAgain(permissions: List<String>) {
        builder.requestSystemWindow(this)
    }
}