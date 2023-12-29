package com.lzq.dawn.util.permission

import android.Manifest
import android.os.Build
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.lzq.dawn.util.permission.util.AndroidVersion
import com.lzq.dawn.util.permission.util.osSpecialPermissions
import com.lzq.dawn.util.permission.task.BackgroundLocationTask


/**
 * 构建请求对象的类。
 * className :PermissionTransit
 * createTime :2022/5/16 15:44
 * @Author :  Lzq
 *
 * 这里用到了 in
 * 查找资料得知
 * i in a…b 表示 i 是否在 a 到 b 区间
 *   in 可以检查字符区间，对象区间（实例对象的类必须实现Comparable），集合
 */
class PermissionTransit {

    private var activity: FragmentActivity? = null

    private var fragment: Fragment? = null

    constructor(activity: FragmentActivity) {
        this.activity = activity
    }

    constructor(fragment: Fragment) {
        this.fragment = fragment
    }

    /**
     * 添加请求权限的方法
     * vararg 关键字 代表参数长度可变 类似java 的...
     * @param permissions 需要请求的权限
     * @return 权限构造类
     */
    fun permissions(vararg permissions: String): PermissionBuilder {
        return permissions(listOf(*permissions))
    }


    /**
     * 添加请求权限的方法
     * @param permissions 需要请求的权限
     * @return 权限构造类
     */
    fun permissions(permissions: List<String>): PermissionBuilder {
        //普通权限
        val normalPermissions = LinkedHashSet<String>()
        //特殊权限
        val specialPermissions = LinkedHashSet<String>()
        //当前系统版本
        val osVersion = Build.VERSION.SDK_INT
        //目标系统版本
        val targetSdkVersion = if (activity != null) {
            activity!!.applicationInfo.targetSdkVersion
        } else {
            fragment!!.requireActivity().applicationInfo.targetSdkVersion
        }
        //开始区分权限
        for (permission in permissions) {
            if (permission in osSpecialPermissions) {
                specialPermissions.add(permission)
            } else {
                normalPermissions.add(permission)
            }
        }
        if (Manifest.permission.ACCESS_BACKGROUND_LOCATION in specialPermissions) {
            if (osVersion == AndroidVersion.ANDROID_10 ||
                (osVersion == AndroidVersion.ANDROID_11 && targetSdkVersion < AndroidVersion.ANDROID_11)
            ) {
                specialPermissions.remove(BackgroundLocationTask.ACCESS_BACKGROUND_LOCATION)
                normalPermissions.add(BackgroundLocationTask.ACCESS_BACKGROUND_LOCATION)
            }
        }

        return PermissionBuilder(activity,fragment,normalPermissions,specialPermissions)
    }
}