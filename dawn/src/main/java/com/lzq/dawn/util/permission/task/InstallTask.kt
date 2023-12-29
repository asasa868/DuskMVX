package com.lzq.dawn.util.permission.task

import com.lzq.dawn.util.permission.PermissionBuilder
import com.lzq.dawn.util.permission.util.AndroidVersion


/**
 * className :NormalTask
 * createTime :2022/5/18 16:03
 * @Author :  Lzq
 * 一般权限 请求类
 */
internal class InstallTask constructor(builder: PermissionBuilder) : BaseTask(builder) {

    override fun request() {
        if (builder.isRequestInstallPermission()
            && AndroidVersion.isAndroid8()
            && builder.targetSdkVersion >= AndroidVersion.ANDROID_8
        ) {
            if (builder.activity.packageManager.canRequestPackageInstalls()) {
                //已授予
                finish()
                return
            }
            finish()
        } else {
            finish()
        }
    }

    override fun requestAgain(permissions: List<String>) {
        builder.requestInstall(this)
    }


    companion object {

        const val REQUEST_INSTALL_PACKAGES = "android.permission.REQUEST_INSTALL_PACKAGES"
    }
}