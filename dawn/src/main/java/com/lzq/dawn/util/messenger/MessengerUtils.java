package com.lzq.dawn.util.messenger;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.lzq.dawn.DawnBridge;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Name :MessengerUtils
 * @Time :2022/8/15 16:06
 * @Author :  Lzq
 * @Desc : Messenger
 */
public final class MessengerUtils {

    private static ConcurrentHashMap<String, MessageCallback> subscribers = new ConcurrentHashMap<>();

    private static Map<String, Client> sClientMap = new HashMap<>();
    private static Client sLocalClient;

    public ConcurrentHashMap<String, MessageCallback> getSubscribers() {
        return subscribers;
    }

    public static MessengerUtils getInstance() {
       return MessengerUtils.SingletonHolder.INSTANCE;
    }


    public static void register() {
        if (DawnBridge.isMainProcess()) {
            if (DawnBridge.isServiceRunning(ServerService.class.getName())) {
                Log.i("MessengerUtils", "Server service is running.");
                return;
            }
            startServiceCompat(new Intent(DawnBridge.getApp(), ServerService.class));
            return;
        }
        if (sLocalClient == null) {
            Client client = new Client(null, getInstance());
            if (client.bind()) {
                sLocalClient = client;
            } else {
                Log.e("MessengerUtils", "Bind service failed.");
            }
        } else {
            Log.i("MessengerUtils", "The client have been bind.");
        }
    }

    public static void unregister() {
        if (DawnBridge.isMainProcess()) {
            if (!DawnBridge.isServiceRunning(ServerService.class.getName())) {
                Log.i("MessengerUtils", "Server service isn't running.");
                return;
            }
            Intent intent = new Intent(DawnBridge.getApp(), ServerService.class);
            DawnBridge.getApp().stopService(intent);
        }
        if (sLocalClient != null) {
            sLocalClient.unbind();
        }
    }

    public static void register(final String pkgName) {
        if (sClientMap.containsKey(pkgName)) {
            Log.i("MessengerUtils", "register: client registered: " + pkgName);
            return;
        }
        Client client = new Client(pkgName, getInstance());
        if (client.bind()) {
            sClientMap.put(pkgName, client);
        } else {
            Log.e("MessengerUtils", "register: client bind failed: " + pkgName);
        }
    }

    public static void unregister(final String pkgName) {
        if (!sClientMap.containsKey(pkgName)) {
            Log.i("MessengerUtils", "unregister: client didn't register: " + pkgName);
            return;
        }
        Client client = sClientMap.get(pkgName);
        sClientMap.remove(pkgName);
        if (client != null) {
            client.unbind();
        }
    }

    public static void subscribe(@NonNull final String key, @NonNull final MessageCallback callback) {
        subscribers.put(key, callback);
    }

    public static void unsubscribe(@NonNull final String key) {
        subscribers.remove(key);
    }

    public static void post(@NonNull String key, @NonNull Bundle data) {
        data.putString(MessengerConstant.KEY_STRING, key);
        if (sLocalClient != null) {
            sLocalClient.sendMsg2Server(data);
        } else {
            Intent intent = new Intent(DawnBridge.getApp(), ServerService.class);
            intent.putExtras(data);
            startServiceCompat(intent);
        }
        for (Client client : sClientMap.values()) {
            client.sendMsg2Server(data);
        }
    }

    private static void startServiceCompat(Intent intent) {
        try {
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DawnBridge.getApp().startForegroundService(intent);
            } else {
                DawnBridge.getApp().startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class SingletonHolder {
        private static final MessengerUtils INSTANCE = new MessengerUtils();
    }
}
