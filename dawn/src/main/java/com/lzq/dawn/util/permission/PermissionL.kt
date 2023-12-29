package com.lzq.dawn.util.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity


/**
 * 请求权限
 * className :PermissionL
 * createTime :2022/5/13 15:52
 * @Author :  Lzq
 */
class PermissionL {

    companion object {

        /**
         * 初始化请求工作
         * @param activity 栈顶的activity
         * @return 构建权限请求的对象
         */
        fun init(activity: FragmentActivity): PermissionTransit {
            return PermissionTransit(activity)
        }

        /**
         * 初始化请求工作
         * @param fragment 当前fragment
         * @return 构建权限请求的对象
         */
        fun init(fragment: Fragment): PermissionTransit {
            return PermissionTransit(fragment)
        }

        /**
         * 判断权限是否应授予
         */
        fun isGranted(context: Context, permission: String): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }

        /**
         * 帮助程序功能用于检查是否为当前应用程序启用了通知。
         * @param context context
         * @return 如果Android版本低于N，则返回值将始终为true。
         */
        fun areNotificationsEnabled(context: Context): Boolean {
            return NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }



    class Permission{

        companion object {
            val POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS"
        }
    }
}