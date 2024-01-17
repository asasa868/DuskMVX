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
 */
class NetworkChangedReceiver : BroadcastReceiver() {
    private var mType: NetworkType? = null
    private val mListeners: MutableSet<OnNetworkStatusChangedListener> = HashSet()

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

    fun isRegistered(listener: OnNetworkStatusChangedListener?): Boolean {
        return if (listener == null) {
            false
        } else mListeners.contains(listener)
    }

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
            // debouncing
            runOnUiThreadDelayed(Runnable {
                val networkType = networkType
                if (mType === networkType) {
                    return@Runnable
                }
                mType = networkType
                if (networkType === NetworkType.NETWORK_NO) {
                    for (listener in mListeners) {
                        listener.onDisconnected()
                    }
                } else {
                    for (listener in mListeners) {
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