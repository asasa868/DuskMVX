package com.lzq.dawn.util.service

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import com.lzq.dawn.DawnBridge

/**
 * @Name :ServiceUtils
 * @Time :2022/8/29 15:55
 * @Author :  Lzq
 * @Desc : Service 服务
 */
object ServiceUtils {
    /**
     * 启动服务
     *
     * @param className 类的名称。
     */
    fun startService(className: String) {
        try {
            startService(Class.forName(className))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 启动服务
     *
     * @param cls 类
     */
    fun startService(cls: Class<*>) {
        startService(Intent(DawnBridge.app, cls))
    }

    /**
     * 启动服务
     *
     * @param intent intent.
     */
    fun startService(intent: Intent) {
        try {
            intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DawnBridge.app.startForegroundService(intent)
            } else {
                DawnBridge.app.startService(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 停止服务
     *
     * @param className 类的名称
     * @return `true`: success<br></br>`false`: fail
     */
    fun stopService(className: String): Boolean {
        return try {
            stopService(Class.forName(className))
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 停止服务
     *
     * @param cls 类的名称
     * @return `true`: success<br></br>`false`: fail
     */
    fun stopService(cls: Class<*>): Boolean {
        return stopService(Intent(DawnBridge.app, cls))
    }

    /**
     * 停止服务
     *
     * @param intent intent.
     * @return `true`: success<br></br>`false`: fail
     */
    fun stopService(intent: Intent): Boolean {
        return try {
            DawnBridge.app.stopService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 绑定服务
     *
     * @param className 类的名称。
     * @param conn      服务连接对象。
     * @param flags     绑定的操作选项。
     *
     *  * 0
     *  * [Context.BIND_AUTO_CREATE]
     *  * [Context.BIND_DEBUG_UNBIND]
     *  * [Context.BIND_NOT_FOREGROUND]
     *  * [Context.BIND_ABOVE_CLIENT]
     *  * [Context.BIND_ALLOW_OOM_MANAGEMENT]
     *  * [Context.BIND_WAIVE_PRIORITY]
     *
     */
    fun bindService(
        className: String,
        conn: ServiceConnection,
        flags: Int,
    ) {
        try {
            bindService(Class.forName(className), conn, flags)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 绑定服务
     *
     * @param cls   服务类
     * @param conn  服务连接对象。
     * @param flags 绑定的操作选项。
     *
     *  * 0
     *  * [Context.BIND_AUTO_CREATE]
     *  * [Context.BIND_DEBUG_UNBIND]
     *  * [Context.BIND_NOT_FOREGROUND]
     *  * [Context.BIND_ABOVE_CLIENT]
     *  * [Context.BIND_ALLOW_OOM_MANAGEMENT]
     *  * [Context.BIND_WAIVE_PRIORITY]
     *
     */
    fun bindService(
        cls: Class<*>,
        conn: ServiceConnection,
        flags: Int,
    ) {
        bindService(Intent(DawnBridge.app, cls), conn, flags)
    }

    /**
     * 绑定服务
     *
     * @param intent intent.
     * @param conn   服务连接对象。
     * @param flags  绑定的操作选项。
     *
     *  * 0
     *  * [Context.BIND_AUTO_CREATE]
     *  * [Context.BIND_DEBUG_UNBIND]
     *  * [Context.BIND_NOT_FOREGROUND]
     *  * [Context.BIND_ABOVE_CLIENT]
     *  * [Context.BIND_ALLOW_OOM_MANAGEMENT]
     *  * [Context.BIND_WAIVE_PRIORITY]
     *
     */
    fun bindService(
        intent: Intent,
        conn: ServiceConnection,
        flags: Int,
    ) {
        try {
            DawnBridge.app.bindService(intent, conn, flags)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 解除绑定服务。
     *
     * @param conn 服务连接对象。
     */
    fun unbindService(conn: ServiceConnection) {
        DawnBridge.app.unbindService(conn)
    }

    /**
     * 返回服务是否正在运行。
     *
     * @param cls 服务类
     * @return `true`: yes<br></br>`false`: no
     */
    fun isServiceRunning(cls: Class<*>): Boolean {
        return isServiceRunning(cls.name)
    }

    /**
     * 返回服务是否正在运行。
     *
     * @param className 类的名称。
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isServiceRunning(className: String): Boolean {
        return try {
            val am = DawnBridge.app.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val info = am.getRunningServices(0x7FFFFFFF)
            if (info == null || info.size == 0) {
                return false
            }
            for (aInfo in info) {
                if (className == aInfo.service.className) {
                    return true
                }
            }
            false
        } catch (ignore: Exception) {
            false
        }
    }
}
