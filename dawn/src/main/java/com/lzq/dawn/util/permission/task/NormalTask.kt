package com.lzq.dawn.util.permission.task

import com.lzq.dawn.util.permission.PermissionBuilder
import com.lzq.dawn.util.permission.PermissionL
import java.util.ArrayList


/**
 * className :NormalTask
 * createTime :2022/5/18 16:03
 * @Author :  Lzq
 * 一般权限 请求类
 */
internal class NormalTask constructor(builder: PermissionBuilder) : BaseTask(builder) {

    override fun request() {
        val requestList = ArrayList<String>()
        for (permission in builder.normalPermissions) {
            if (PermissionL.isGranted(builder.activity, permission)) {
                //已授权
                builder.grantedPermissions.add(permission)
            } else {
                //未授权 ，需要授权
                requestList.add(permission)
            }
        }
        if (requestList.isEmpty()) {
            finish()
            return
        }

        builder.requestNow(builder.normalPermissions, this)

    }

    /**
     * 再次请求
     */
    override fun requestAgain(permissions: List<String>) {
        val permissionsToRequestAgain: MutableSet<String> = HashSet(builder.grantedPermissions)
        permissionsToRequestAgain.addAll(permissions)
        if (permissionsToRequestAgain.isNotEmpty()) {
            builder.requestNow(permissionsToRequestAgain, this)
        } else {
            finish()
        }
    }
}