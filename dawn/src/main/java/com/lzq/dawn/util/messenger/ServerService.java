package com.lzq.dawn.util.messenger;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.lzq.dawn.DawnBridge;
import com.lzq.dawn.util.notification.ChannelConfig;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Name :ServerService
 * @Time :2022/8/15 16:17
 * @Author :  Lzq
 * @Desc :
 */
public class ServerService extends Service {

    private final ConcurrentHashMap<Integer, Messenger> mClientMap = new ConcurrentHashMap<>();
    private final MessengerUtils messengerUtils;

    public ServerService(MessengerUtils messengerUtils){
        this.messengerUtils = messengerUtils;
    }

    @SuppressLint("HandlerLeak")
    private final Handler mReceiveClientMsgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessengerConstant.WHAT_SUBSCRIBE:
                    mClientMap.put(msg.arg1, msg.replyTo);
                    break;
                case MessengerConstant.WHAT_SEND:
                    sendMsg2Client(msg);
                    consumeServerProcessCallback(msg);
                    break;
                case MessengerConstant.WHAT_UNSUBSCRIBE:
                    mClientMap.remove(msg.arg1);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    private final Messenger messenger = new Messenger(mReceiveClientMsgHandler);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = DawnBridge.getNotification(
                    ChannelConfig.DEFAULT_CHANNEL_CONFIG, null
            );
            startForeground(1, notification);
        }
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Message msg = Message.obtain(mReceiveClientMsgHandler, MessengerConstant.WHAT_SEND);
                msg.replyTo = messenger;
                msg.setData(extras);
                sendMsg2Client(msg);
                consumeServerProcessCallback(msg);
            }
        }
        return START_NOT_STICKY;
    }

    private void sendMsg2Client(final Message msg) {
        final Message obtain = Message.obtain(msg); //Copy the original
        for (Messenger client : mClientMap.values()) {
            try {
                if (client != null) {
                    client.send(Message.obtain(obtain));
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        obtain.recycle(); //Recycled copy
    }

    private void consumeServerProcessCallback(final Message msg) {
        Bundle data = msg.getData();
        if (data != null) {
            String key = data.getString(MessengerConstant.KEY_STRING);
            if (key != null) {
                MessageCallback callback = messengerUtils.getSubscribers().get(key);
                if (callback != null) {
                    callback.messageCall(data);
                }
            }
        }
    }
}
