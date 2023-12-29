package com.lzq.dawn.util.permission.task

import com.lzq.dawn.util.permission.PermissionBuilder
import com.lzq.dawn.util.permission.PermissionL

internal class NotificationTask internal constructor(builder: PermissionBuilder) :
    BaseTask(builder) {
    override fun request() {
        if (builder.isRequestNotificationPermission()) {
            if (PermissionL.areNotificationsEnabled(builder.activity)) {
                // 通知权限已经授予
                finish()
                return
            }
            finish()
        }
        //此时不应该请求通知，所以我们调用finish（）来完成此任务。
        finish()
    }

    override fun requestAgain(permissions: List<String>) {
        builder.requestNotificationPermissionNow(this)
    }
}