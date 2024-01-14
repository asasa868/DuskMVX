package com.lzq.dawn.util.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import com.lzq.dawn.DawnBridge;

/**
 * @Name :ChannelConfig
 * @Time :2022/8/16 10:09
 * @Author :  Lzq
 * @Desc :
 */
public class ChannelConfig {

    public static final ChannelConfig DEFAULT_CHANNEL_CONFIG = new ChannelConfig(
            DawnBridge.getApp().getPackageName(), DawnBridge.getApp().getPackageName(), NotificationUtils.IMPORTANCE_DEFAULT
    );

    private NotificationChannel mNotificationChannel;

    public ChannelConfig(String id, CharSequence name, @NotificationUtils.Importance int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationChannel = new NotificationChannel(id, name, importance);
        }
    }

    public NotificationChannel getNotificationChannel() {
        return mNotificationChannel;
    }

    /**
     * 设置发布到此频道的通知是否可以在 {@link android.app.NotificationManager.Policy#INTERRUPTION_FILTER_PRIORITY} 模式下中断用户。
     * <p>
     * 只能由系统和通知排序器修改
     */
    public ChannelConfig setBypassDnd(boolean bypassDnd) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationChannel.setBypassDnd(bypassDnd);
        }
        return this;
    }

    /**
     * 设置此频道的用户可见描述。
     * <p>
     * 建议的最大长度为 300 个字符；如果值太长，可能会被截断。
     */
    public ChannelConfig setDescription(String description) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationChannel.setDescription(description);
        }
        return this;
    }

    /**
     * 设置此频道所属的组。
     * <p>
     * 群组信息仅用于展示，不用于行为。
     * <p>
     * 只能在频道提交到 {@link NotificationManager#createNotificationChannel(NotificationChannel)} 之前修改，除非频道当前不是组的一部分。
     *
     * @param groupId 由 {@link NotificationManager#createNotificationChannelGroup)} 创建的组的 ID。
     */
    public ChannelConfig setGroup(String groupId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationChannel.setGroup(groupId);
        }
        return this;
    }

    /**
     * 设置此通知通道的中断级别。
     * <p>
     * 只能在频道提交到 {@link NotificationManager#createNotificationChannel(NotificationChannel)} 之前修改。
     *
     * @param importance 用户应该被来自该频道的通知打断的数量。
     */
    public ChannelConfig setImportance(@NotificationUtils.Importance int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationChannel.setImportance(importance);
        }
        return this;
    }

    /**
     * 如果此通道上的灯{@link NotificationChannel#enableLights(boolean) enabled} 且设备支持该功能，则为发布到此通道的通知设置通知灯颜色。
     * <p>
     * Only modifiable before the channel is submitted to
     * {@link NotificationManager#createNotificationChannel(NotificationChannel)}.
     */
    public ChannelConfig setLightColor(int argb) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationChannel.setLightColor(argb);
        }
        return this;
    }

    /**
     * 设置发布到此频道的通知是否显示在锁定屏幕上，如果是，它们是否以编辑形式显示。参见例如{@link Notification#VISIBILITY_SECRET}.
     * <p>
     * 只能由系统和通知排序器修改。
     */
    public ChannelConfig setLockscreenVisibility(int lockscreenVisibility) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationChannel.setLockscreenVisibility(lockscreenVisibility);
        }
        return this;
    }

    /**
     * 设置此频道的用户可见名称。
     * <p>
     * 建议的最大长度为 40 个字符；如果值太长，可能会被截断。
     */
    public ChannelConfig setName(CharSequence name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationChannel.setName(name);
        }
        return this;
    }

    /**
     * 设置发布到此频道的通知是否可以在启动器中显示为应用程序图标徽章。
     * <p>
     * 只能在频道提交前修改{@link NotificationManager#createNotificationChannel(NotificationChannel)}.
     *
     * @param showBadge 如果应允许显示徽章，则为 true。
     */
    public ChannelConfig setShowBadge(boolean showBadge) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationChannel.setShowBadge(showBadge);
        }
        return this;
    }

    /**
     * 设置应为发布到此频道的通知及其音频属性播放的声音。
     * {@link NotificationChannel#getImportance() 重要性} 至少为 {@link NotificationManager#IMPORTANCE_DEFAULT} 的通知通道应该有声音。
     * 只能在频道提交前修改
     * {@link NotificationManager#createNotificationChannel(NotificationChannel)}.
     */
    public ChannelConfig setSound(Uri sound, AudioAttributes audioAttributes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationChannel.setSound(sound, audioAttributes);
        }
        return this;
    }

    /**
     * 设置发布到此频道的通知的振动模式。如果提供的模式有效（非空、非空）
     * {@link NotificationChannel#enableVibration(boolean)} 也将启用振动}。否则，振动将被禁用。
     * 只能在频道提交前修改
     * {@link NotificationManager#createNotificationChannel(NotificationChannel)}.
     */
    public ChannelConfig setVibrationPattern(long[] vibrationPattern) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationChannel.setVibrationPattern(vibrationPattern);
        }
        return this;
    }
}
