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
internal class WriterTask constructor(builder: PermissionBuilder) : BaseTask(builder) {

    override fun request() {
       if (builder.isRequestWriterPermission()){
           if (AndroidVersion.isAndroid6() && builder.targetSdkVersion >= AndroidVersion.ANDROID_6) {
               if (Settings.System.canWrite(builder.activity)) {
                   finish()
                   return
               }
               finish()
           }else{
               //6.0以下自动授权
               builder.grantedPermissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW)
               builder.specialPermissions.remove(Manifest.permission.SYSTEM_ALERT_WINDOW)
               finish()
           }
       } else{
           finish()
       }
    }

    override fun requestAgain(permissions: List<String>) {
        builder.requestWriter(this)
    }
}