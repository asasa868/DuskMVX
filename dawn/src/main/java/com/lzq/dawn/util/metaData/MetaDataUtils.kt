package com.lzq.dawn.util.metaData

import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.pm.PackageManager
import com.lzq.dawn.DawnBridge

/**
 * @Name :MetaDataUtils
 * @Time :2022/8/15 16:34
 * @Author :  Lzq
 * @Desc : 关于清单文件中 MetaData
 */
object MetaDataUtils {
    /**
     * 返回应用程序中 meta-data 的值。
     *
     * @param key meta-data 的 key（name）
     * @return 清单文件中 meta-data 的value
     */
    fun getMetaDataInApp(key: String): String {
        var value = ""
        val pm = DawnBridge.app.packageManager
        val packageName = DawnBridge.app.packageName
        try {
            val ai = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            value = ai.metaData[key].toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return value
    }

    /**
     * 返回activity中 meta-data 的值。
     *
     * @param activity  activity.
     * @param key      meta-data 的 key（name）
     * @return  清单文件中activity的 meta-data 的value
     */
    fun getMetaDataInActivity(
        activity: Activity, key: String
    ): String {
        return getMetaDataInActivity(activity.javaClass, key)
    }

    /**
     * 返回activity中 meta-data 的值。
     *
     * @param clz  activity class.
     * @param key  meta-data 的 key（name）
     * @return 清单文件中activity的 meta-data 的value
     */
    fun getMetaDataInActivity(
        clz: Class<out Activity?>, key: String
    ): String {
        var value = ""
        val pm = DawnBridge.app.packageManager
        val componentName = ComponentName(DawnBridge.app, clz)
        try {
            val ai = pm.getActivityInfo(componentName, PackageManager.GET_META_DATA)
            value = ai.metaData[key].toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return value
    }

    /**
     * 返回service中 meta-data 的值。
     *
     * @param service  service.
     * @param key      meta-data 的 key（name）
     * @return  清单文件中service的 meta-data 的value
     */
    fun getMetaDataInService(
        service: Service, key: String
    ): String {
        return getMetaDataInService(service.javaClass, key)
    }

    /**
     * 返回service中 meta-data 的值。
     *
     * @param clz  service class.
     * @param key      meta-data 的 key（name）
     * @return  清单文件中service的 meta-data 的value
     */
    fun getMetaDataInService(
        clz: Class<out Service?>, key: String
    ): String {
        var value = ""
        val pm = DawnBridge.app.packageManager
        val componentName = ComponentName(DawnBridge.app, clz)
        try {
            val info = pm.getServiceInfo(componentName, PackageManager.GET_META_DATA)
            value = info.metaData[key].toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return value
    }

    /**
     * 返回receiver中 meta-data 的值。
     *
     * @param receiver  receiver.
     * @param key      meta-data 的 key（name）
     * @return  清单文件中receiver的 meta-data 的value
     */
    fun getMetaDataInReceiver(
        receiver: BroadcastReceiver, key: String
    ): String {
        return getMetaDataInReceiver(receiver.javaClass, key)
    }

    /**
     * 返回receiver中 meta-data 的值。
     *
     * @param clz  receiver class.
     * @param key      meta-data 的 key（name）
     * @return  清单文件中receiver的 meta-data 的value
     */
    fun getMetaDataInReceiver(
        clz: Class<out BroadcastReceiver?>, key: String
    ): String {
        var value = ""
        val pm = DawnBridge.app.packageManager
        val componentName = ComponentName(DawnBridge.app, clz)
        try {
            val info = pm.getReceiverInfo(componentName, PackageManager.GET_META_DATA)
            value = info.metaData[key].toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return value
    }
}