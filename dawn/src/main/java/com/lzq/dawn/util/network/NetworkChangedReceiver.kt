package com.lzq.dawn.util.network;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import androidx.annotation.RequiresPermission;

import com.lzq.dawn.DawnBridge;

import java.util.HashSet;
import java.util.Set;

/**
 * @Name :NetworkChangedReceiver
 * @Time :2022/8/15 17:22
 * @Author :  Lzq
 * @Desc :
 */
public class NetworkChangedReceiver extends BroadcastReceiver {
    public static NetworkChangedReceiver getInstance() {
        return NetworkChangedReceiver.LazyHolder.INSTANCE;
    }

    private NetworkType                         mType;
    private Set<OnNetworkStatusChangedListener> mListeners = new HashSet<>();

    @RequiresPermission(ACCESS_NETWORK_STATE)
    void registerListener(final OnNetworkStatusChangedListener listener) {
        if (listener == null) {
            return;
        }
        DawnBridge.runOnUiThread(new Runnable() {
            @Override
            @RequiresPermission(ACCESS_NETWORK_STATE)
            public void run() {
                int preSize = mListeners.size();
                mListeners.add(listener);
                if (preSize == 0 && mListeners.size() == 1) {
                    mType = NetworkUtils.getNetworkType();
                    IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                    DawnBridge.getApp().registerReceiver(NetworkChangedReceiver.getInstance(), intentFilter);
                }
            }
        });
    }

    boolean isRegistered(final OnNetworkStatusChangedListener listener) {
        if (listener == null) {
            return false;
        }
        return mListeners.contains(listener);
    }

    void unregisterListener(final OnNetworkStatusChangedListener listener) {
        if (listener == null) {
            return;
        }
        DawnBridge.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int preSize = mListeners.size();
                mListeners.remove(listener);
                if (preSize == 1 && mListeners.size() == 0) {
                    DawnBridge.getApp().unregisterReceiver(NetworkChangedReceiver.getInstance());
                }
            }
        });
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            // debouncing
            DawnBridge.runOnUiThreadDelayed(new Runnable() {
                @Override
                @RequiresPermission(ACCESS_NETWORK_STATE)
                public void run() {
                    NetworkType networkType = NetworkUtils.getNetworkType();
                    if (mType == networkType) {
                        return;
                    }
                    mType = networkType;
                    if (networkType == NetworkType.NETWORK_NO) {
                        for ( OnNetworkStatusChangedListener listener : mListeners) {
                            listener.onDisconnected();
                        }
                    } else {
                        for ( OnNetworkStatusChangedListener listener : mListeners) {
                            listener.onConnected(networkType);
                        }
                    }
                }
            }, 1000);
        }
    }

    private static class LazyHolder {
        private static final  NetworkChangedReceiver INSTANCE = new  NetworkChangedReceiver();
    }
}
