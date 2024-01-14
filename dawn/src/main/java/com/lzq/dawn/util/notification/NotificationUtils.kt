package com.lzq.dawn.util.notification;

import static android.Manifest.permission.EXPAND_STATUS_BAR;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.IntDef;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.lzq.dawn.DawnBridge;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;

/**
 * @Name :NotificationUtils
 * @Time :2022/8/16 10:07
 * @Author :  Lzq
 * @Desc : 通知
 */
public final class NotificationUtils {
    public static final int IMPORTANCE_UNSPECIFIED = -1000;
    public static final int IMPORTANCE_NONE = 0;
    public static final int IMPORTANCE_MIN = 1;
    public static final int IMPORTANCE_LOW = 2;
    public static final int IMPORTANCE_DEFAULT = 3;
    public static final int IMPORTANCE_HIGH = 4;

    @IntDef({IMPORTANCE_UNSPECIFIED, IMPORTANCE_NONE, IMPORTANCE_MIN, IMPORTANCE_LOW, IMPORTANCE_DEFAULT, IMPORTANCE_HIGH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Importance {
    }

    /**
     * 返回是否启用通知。
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean areNotificationsEnabled() {
        return NotificationManagerCompat.from(DawnBridge.getApp()).areNotificationsEnabled();
    }

    /**
     * 发布要在状态栏中显示的通知
     *
     * @param id       此通知的标识符
     * @param consumer 创建通知构建器的消费者
     */
    public static void notify(int id, DawnBridge.Consumer<NotificationCompat.Builder> consumer) {
        notify(null, id, ChannelConfig.DEFAULT_CHANNEL_CONFIG, consumer);
    }

    /**
     * 发布要在状态栏中显示的通知。
     *
     * @param tag      此通知的字符串标识符。可能是 {@code null}。
     * @param id       此通知的标识符。
     * @param consumer 创建通知构建器的消费者
     */
    public static void notify(String tag, int id, DawnBridge.Consumer<NotificationCompat.Builder> consumer) {
        notify(tag, id, ChannelConfig.DEFAULT_CHANNEL_CONFIG, consumer);
    }

    /**
     * 发布要在状态栏中显示的通知。
     *
     * @param id            此通知的标识符。
     * @param channelConfig config的通知通道。
     * @param consumer      创建通知构建器的消费者
     */
    public static void notify(int id, ChannelConfig channelConfig, DawnBridge.Consumer<NotificationCompat.Builder> consumer) {
        notify(null, id, channelConfig, consumer);
    }

    /**
     * 发布要在状态栏中显示的通知。
     *
     * @param tag           此通知的字符串标识符。可能是 {@code null}。
     * @param id            此通知的标识符。
     * @param channelConfig config的通知通道。
     * @param consumer      创建通知构建器的消费者
     */
    public static void notify(String tag, int id, ChannelConfig channelConfig, DawnBridge.Consumer<NotificationCompat.Builder> consumer) {
        if (ActivityCompat.checkSelfPermission(DawnBridge.getApp(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        NotificationManagerCompat.from(DawnBridge.getApp()).notify(tag, id, getNotification(channelConfig, consumer));
    }


    public static Notification getNotification(ChannelConfig channelConfig, DawnBridge.Consumer<NotificationCompat.Builder> consumer) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) DawnBridge.getApp().getSystemService(Context.NOTIFICATION_SERVICE);
            //noinspection ConstantConditions
            nm.createNotificationChannel(channelConfig.getNotificationChannel());
        }

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(DawnBridge.getApp(), channelConfig.getNotificationChannel().getId());
        } else {
            builder = new NotificationCompat.Builder(DawnBridge.getApp());
        }
        if (consumer != null) {
            consumer.accept(builder);
        }

        return builder.build();
    }

    /**
     * 取消通知。
     *
     * @param tag The tag for the notification will be cancelled.
     * @param id  The identifier for the notification will be cancelled.
     */
    public static void cancel(String tag, final int id) {
        NotificationManagerCompat.from(DawnBridge.getApp()).cancel(tag, id);
    }

    /**
     * 取消通知。
     *
     * @param id The identifier for the notification will be cancelled.
     */
    public static void cancel(final int id) {
        NotificationManagerCompat.from(DawnBridge.getApp()).cancel(id);
    }

    /**
     * 取消所有通知。
     */
    public static void cancelAll() {
        NotificationManagerCompat.from(DawnBridge.getApp()).cancelAll();
    }

    /**
     * 设置通知栏的可见性。 <p>必须持有 {@code <uses-permission android:name="android.permission.EXPAND_STATUS_BAR" >}<p>
     *
     * @param isVisible True 设置通知栏可见，否则为 false。
     */
    @RequiresPermission(EXPAND_STATUS_BAR)
    public static void setNotificationBarVisibility(final boolean isVisible) {
        String methodName;
        if (isVisible) {
            methodName = (Build.VERSION.SDK_INT <= 16) ? "expand" : "expandNotificationsPanel";
        } else {
            methodName = (Build.VERSION.SDK_INT <= 16) ? "collapse" : "collapsePanels";
        }
        invokePanels(methodName);
    }

    private static void invokePanels(final String methodName) {
        try {
            @SuppressLint("WrongConstant")
            Object service = DawnBridge.getApp().getSystemService("statusbar");
            @SuppressLint("PrivateApi")
            Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
            Method expand = statusBarManager.getMethod(methodName);
            expand.invoke(service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
