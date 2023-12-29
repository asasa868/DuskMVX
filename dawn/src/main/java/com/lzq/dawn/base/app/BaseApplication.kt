package com.lzq.dawn.base.app

import com.lzq.dawn.util.network.NetworkType
import com.lzq.dawn.util.network.NetworkUtils
import com.lzq.dawn.util.network.OnNetworkStatusChangedListener
import org.jetbrains.annotations.Contract

/**
 * @projectName com.lzq.dawn.base.app
 * @author Lzq
 * @date : Created by Lzq on 2023/12/25 09:35
 * @version 0.0.1
 * @description: Application基类多模块项目的唯一application可继承此基类
 */
abstract class BaseApplication : Application(), OnNetworkStatusChangedListener {


    override fun onCreate() {
        super.onCreate()
        NetworkUtils.registerNetworkStatusChangedListener(this)
    }

    override fun onDisconnected() {
        isNetWorkConnected(false)
    }

    override fun onConnected(networkType: NetworkType?) {
        isNetWorkConnected(true)
    }

    @Contract("Connected -> true")
    open fun isNetWorkConnected(isConnected: Boolean) {}


}