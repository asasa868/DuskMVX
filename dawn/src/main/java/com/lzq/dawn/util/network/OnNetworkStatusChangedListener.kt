package com.lzq.dawn.util.network

/**
 * @Name :OnNetworkStatusChangedListener
 * @Time :2022/8/15 17:24
 * @Author :  Lzq
 * @Desc : 网络状态
 */
interface OnNetworkStatusChangedListener {
    /**
     * 断开
     */
    fun onDisconnected()

    /**
     * 连接
     *
     * @param networkType NetworkType
     */
    fun onConnected(networkType: NetworkType?)
}