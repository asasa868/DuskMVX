package com.lzq.dawn.util.permission.task

import android.Manifest
import com.lzq.dawn.util.permission.PermissionBuilder
import com.lzq.dawn.util.permission.PermissionL
import com.lzq.dawn.util.permission.util.AndroidVersion


/**
 * className :NormalTask
 * createTime :2022/5/18 16:03
 * @Author :  Lzq
 * 一般权限 请求类
 */
internal class BackgroundLocationTask constructor(builder: PermissionBuilder) : BaseTask(builder) {

    companion object {
        const val ACCESS_BACKGROUND_LOCATION = "android.permission.ACCESS_BACKGROUND_LOCATION"
    }


    override fun request() {
        if (builder.isRequestBackgroundLocationPermission()) {
            if (!AndroidVersion.isAndroid10()) {
                builder.specialPermissions.remove(ACCESS_BACKGROUND_LOCATION)
                builder.permissionsWontRequest.add(ACCESS_BACKGROUND_LOCATION)
            }
            //已授权
            if (PermissionL.isGranted(builder.activity, ACCESS_BACKGROUND_LOCATION)) {
                finish()
            }
            val accessFindLocationGranted =
                PermissionL.isGranted(builder.activity, Manifest.permission.ACCESS_FINE_LOCATION)
            val accessCoarseLocationGranted =
                PermissionL.isGranted(builder.activity, Manifest.permission.ACCESS_COARSE_LOCATION)
            if (accessFindLocationGranted || accessCoarseLocationGranted) {
                requestAgain(emptyList())
            }else{
                finish()
            }
        } else {
            finish()
        }
    }

    override fun requestAgain(permissions: List<String>) {
        builder.requestBackgroundLocation(this)
    }
}