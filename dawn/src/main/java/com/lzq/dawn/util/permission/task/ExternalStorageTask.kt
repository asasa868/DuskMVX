package com.lzq.dawn.util.permission.task

import android.os.Environment
import com.lzq.dawn.util.permission.PermissionBuilder
import com.lzq.dawn.util.permission.util.AndroidVersion


/**
 * className :NormalTask
 * createTime :2022/5/18 16:03
 * @Author :  Lzq
 * 一般权限 请求类
 */
internal class ExternalStorageTask constructor(builder: PermissionBuilder) : BaseTask(builder) {

    override fun request() {
        if (builder.isRequestExternalStoragePermission() && AndroidVersion.isAndroid11()) {
            if (Environment.isExternalStorageManager()) {
                // 已授予
                finish()
                return
            }
            finish()
        } else {
            finish()
        }
    }

    override fun requestAgain(permissions: List<String>) {
        builder.requestExternalStorage(this)
    }


    companion object {
        const val MANAGE_EXTERNAL_STORAGE = "android.permission.MANAGE_EXTERNAL_STORAGE"
    }
}