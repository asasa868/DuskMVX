package com.lzq.dawn.util.network;

/**
 * @Name :OnNetworkStatusChangedListener
 * @Time :2022/8/15 17:24
 * @Author :  Lzq
 * @Desc : 网络状态
 */
public interface OnNetworkStatusChangedListener {
    /**
     * 断开
     */
    void onDisconnected();

    /**
     * 连接
     *
     * @param networkType NetworkType
     */
    void onConnected(NetworkType networkType);
}
