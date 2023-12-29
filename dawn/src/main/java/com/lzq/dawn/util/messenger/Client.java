package com.lzq.dawn.util.messenger;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.lzq.dawn.DawnBridge;

import java.util.LinkedList;

/**
 * @Name :Client
 * @Time :2022/8/15 16:08
 * @Author :  Lzq
 * @Desc : messenger 客户端
 */
public class Client {


    private final String         mPkgName;
    private Messenger            mServer;
    LinkedList<Bundle>           mCached = new LinkedList<>();
    private final MessengerUtils messengerUtils;

  public Client(String pkgName, MessengerUtils messengerUtils) {
        this.mPkgName = pkgName;
        this.messengerUtils = messengerUtils;
    }

    @SuppressLint("HandlerLeak")
    Handler mReceiveServeMsgHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            data.setClassLoader(MessengerUtils.class.getClassLoader());
            String key = data.getString(MessengerConstant.KEY_STRING);
            if (key != null) {
                MessageCallback callback = messengerUtils.getSubscribers().get(key);
                if (callback != null) {
                    callback.messageCall(data);
                }
            }
        }
    };

   private final Messenger         mClient = new Messenger(mReceiveServeMsgHandler);
   private final ServiceConnection mConn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("MessengerUtils", "client service connected " + name);
            mServer = new Messenger(service);
            int key = DawnBridge.getCurrentProcessName().hashCode();
            Message msg = Message.obtain(mReceiveServeMsgHandler, MessengerConstant.WHAT_SUBSCRIBE, key, 0);
            msg.getData().setClassLoader(MessengerUtils.class.getClassLoader());
            msg.replyTo = mClient;
            try {
                mServer.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            sendCachedMsg2Server();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w("MessengerUtils", "client service disconnected:" + name);
            mServer = null;
            if (!bind()) {
                Log.e("MessengerUtils", "client service rebind failed: " + name);
            }
        }
    };

    public boolean bind() {
        if (TextUtils.isEmpty(mPkgName)) {
            Intent intent = new Intent(DawnBridge.getApp(), ServerService.class);
            return DawnBridge.getApp().bindService(intent, mConn, Context.BIND_AUTO_CREATE);
        }
        if (DawnBridge.isAppInstalled(mPkgName)) {
            if (DawnBridge.isAppRunning(mPkgName)) {
                Intent intent = new Intent(mPkgName + ".messenger");
                intent.setPackage(mPkgName);
                return DawnBridge.getApp().bindService(intent, mConn, Context.BIND_AUTO_CREATE);
            } else {
                Log.e("MessengerUtils", "bind: the app is not running -> " + mPkgName);
                return false;
            }
        } else {
            Log.e("MessengerUtils", "bind: the app is not installed -> " + mPkgName);
            return false;
        }
    }

    public void unbind() {
        int key = DawnBridge.getCurrentProcessName().hashCode();
        Message msg = Message.obtain(mReceiveServeMsgHandler, MessengerConstant.WHAT_UNSUBSCRIBE, key, 0);
        msg.replyTo = mClient;
        try {
            mServer.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            DawnBridge.getApp().unbindService(mConn);
        } catch (Exception ignore) {/*ignore*/}
    }

    public void sendMsg2Server(Bundle bundle) {
        if (mServer == null) {
            mCached.addFirst(bundle);
            Log.i("MessengerUtils", "save the bundle " + bundle);
        } else {
            sendCachedMsg2Server();
            if (!send2Server(bundle)) {
                mCached.addFirst(bundle);
            }
        }
    }

    private void sendCachedMsg2Server() {
        if (mCached.isEmpty()) {
            return;
        }
        for (int i = mCached.size() - 1; i >= 0; --i) {
            if (send2Server(mCached.get(i))) {
                mCached.remove(i);
            }
        }
    }

    private boolean send2Server(Bundle bundle) {
        Message msg = Message.obtain(mReceiveServeMsgHandler, MessengerConstant.WHAT_SEND);
        bundle.setClassLoader(MessengerUtils.class.getClassLoader());
        msg.setData(bundle);
        msg.replyTo = mClient;
        try {
            mServer.send(msg);
            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }
}
