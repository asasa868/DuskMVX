package com.lzq.dawn.util.network

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Android 17 上面向 API 37 的应用访问局域网前使用的权限检查。
 *
 * 宿主仅在需要局域网功能时声明并请求 [PERMISSION]。本工具不发起请求，
 * 不判断目标地址，也不替代网络操作自身的错误处理。普通公网访问不需要此权限。
 */
object LocalNetworkPermission {
    const val PERMISSION: String = "android.permission.ACCESS_LOCAL_NETWORK"

    /** 当前系统与应用目标版本是否启用局域网运行时权限要求。 */
    fun isRequired(context: Context): Boolean = requiresPermission(
        Build.VERSION.SDK_INT,
        context.applicationInfo.targetSdkVersion,
    )

    /** 旧系统或旧目标版本返回 true；启用权限要求时每次读取实际授权状态。 */
    fun isGranted(context: Context): Boolean = isGranted(context, Build.VERSION.SDK_INT)

    internal fun isGranted(context: Context, sdkInt: Int): Boolean =
        !requiresPermission(sdkInt, context.applicationInfo.targetSdkVersion) ||
            ContextCompat.checkSelfPermission(context, PERMISSION) == PackageManager.PERMISSION_GRANTED

    internal fun requiresPermission(sdkInt: Int, targetSdkVersion: Int): Boolean =
        sdkInt >= 37 && targetSdkVersion >= 37
}
