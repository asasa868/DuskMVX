package com.lzq.dawn.util.network

import android.Manifest.permission
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.annotation.RequiresPermission
import com.lzq.dawn.DawnBridge.app
import com.lzq.dawn.DawnBridge.runOnUiThread
import com.lzq.dawn.DawnBridge.runOnUiThreadDelayed
import com.lzq.dawn.util.network.NetworkUtils.networkType

/**
 * @Name :NetworkChangedReceiver
 * @Time :2022/8/15 17:22
 * @Author :  Lzq
 * @Desc :
 *
 * 在主线程向已注册的所有者广播网络变化。
 */
class NetworkChangedReceiver : BroadcastReceiver() {
    private var mType: NetworkType? = null
    private val mListeners: MutableSet<OnNetworkStatusChangedListener> = HashSet()

    /** 注册所有者，同一实例重复注册不会叠加。 */
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun registerListener(listener: OnNetworkStatusChangedListener?) {
        if (listener == null) {
            return
        }
        runOnUiThread {
            val preSize = mListeners.size
            mListeners.add(listener)
            if (preSize == 0 && mListeners.size == 1) {
                mType = networkType
                val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                app.registerReceiver(Companion.instance, intentFilter)
            }
        }
    }

    /** 返回所有者是否仍被本接收器持有；与页面回调同在主线程使用。 */
    fun isRegistered(listener: OnNetworkStatusChangedListener?): Boolean {
        return if (listener == null) {
            false
        } else mListeners.contains(listener)
    }

    /** 移除所有者引用；最后一个所有者退出时停止系统广播监听。 */
    fun unregisterListener(listener: OnNetworkStatusChangedListener?) {
        if (listener == null) {
            return
        }
        runOnUiThread {
            val preSize = mListeners.size
            mListeners.remove(listener)
            if (preSize == 1 && mListeners.size == 0) {
                app.unregisterReceiver(Companion.instance)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION == intent.action) {
            // 延迟合并系统网络抖动；回调可以同步退出页面或注销自身。
            runOnUiThreadDelayed(Runnable {
                val networkType = networkType
                if (mType === networkType) {
                    return@Runnable
                }
                mType = networkType
                val listeners = mListeners.toList()
                if (networkType === NetworkType.NETWORK_NO) {
                    for (listener in listeners) {
                        if (!mListeners.contains(listener)) continue
                        listener.onDisconnected()
                    }
                } else {
                    for (listener in listeners) {
                        if (!mListeners.contains(listener)) continue
                        listener.onConnected(networkType)
                    }
                }
            }, 1000)
        }
    }


    companion object {
        val instance: NetworkChangedReceiver by lazy {
            NetworkChangedReceiver()
        }
    }
}
