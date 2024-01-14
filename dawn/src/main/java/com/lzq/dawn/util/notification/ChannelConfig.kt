package com.lzq.dawn.util.notification

import android.app.NotificationChannel
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import com.lzq.dawn.DawnBridge
import com.lzq.dawn.util.notification.NotificationUtils.Importance

/**
 * @Name :ChannelConfig
 * @Time :2022/8/16 10:09
 * @Author :  Lzq
 * @Desc :
 */
class ChannelConfig(id: String?, name: CharSequence?, @Importance importance: Int) {
    var notificationChannel: NotificationChannel? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(id, name, importance)
        }
    }

    /**
     * 设置发布到此频道的通知是否可以在 [android.app.NotificationManager.Policy.INTERRUPTION_FILTER_PRIORITY] 模式下中断用户。
     *
     *
     * 只能由系统和通知排序器修改
     */
    fun setBypassDnd(bypassDnd: Boolean): ChannelConfig {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel!!.setBypassDnd(bypassDnd)
        }
        return this
    }

    /**
     * 设置此频道的用户可见描述。
     *
     *
     * 建议的最大长度为 300 个字符；如果值太长，可能会被截断。
     */
    fun setDescription(description: String?): ChannelConfig {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel!!.description = description
        }
        return this
    }

    /**
     * 设置此频道所属的组。
     *
     *
     * 群组信息仅用于展示，不用于行为。
     *
     *
     * 只能在频道提交到 [NotificationManager.createNotificationChannel] 之前修改，除非频道当前不是组的一部分。
     *
     * @param groupId 由 [)][NotificationManager.createNotificationChannelGroup] 创建的组的 ID。
     */
    fun setGroup(groupId: String?): ChannelConfig {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel!!.group = groupId
        }
        return this
    }

    /**
     * 设置此通知通道的中断级别。
     *
     *
     * 只能在频道提交到 [NotificationManager.createNotificationChannel] 之前修改。
     *
     * @param importance 用户应该被来自该频道的通知打断的数量。
     */
    fun setImportance(@Importance importance: Int): ChannelConfig {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel!!.importance = importance
        }
        return this
    }

    /**
     * 如果此通道上的灯[enabled][NotificationChannel.enableLights] 且设备支持该功能，则为发布到此通道的通知设置通知灯颜色。
     *
     *
     * Only modifiable before the channel is submitted to
     * [NotificationManager.createNotificationChannel].
     */
    fun setLightColor(argb: Int): ChannelConfig {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel!!.lightColor = argb
        }
        return this
    }

    /**
     * 设置发布到此频道的通知是否显示在锁定屏幕上，如果是，它们是否以编辑形式显示。参见例如[Notification.VISIBILITY_SECRET].
     *
     *
     * 只能由系统和通知排序器修改。
     */
    fun setLockscreenVisibility(lockscreenVisibility: Int): ChannelConfig {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel!!.lockscreenVisibility = lockscreenVisibility
        }
        return this
    }

    /**
     * 设置此频道的用户可见名称。
     *
     *
     * 建议的最大长度为 40 个字符；如果值太长，可能会被截断。
     */
    fun setName(name: CharSequence?): ChannelConfig {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel!!.name = name
        }
        return this
    }

    /**
     * 设置发布到此频道的通知是否可以在启动器中显示为应用程序图标徽章。
     *
     *
     * 只能在频道提交前修改[NotificationManager.createNotificationChannel].
     *
     * @param showBadge 如果应允许显示徽章，则为 true。
     */
    fun setShowBadge(showBadge: Boolean): ChannelConfig {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel!!.setShowBadge(showBadge)
        }
        return this
    }

    /**
     * 设置应为发布到此频道的通知及其音频属性播放的声音。
     * [重要性][NotificationChannel.getImportance] 至少为 [NotificationManager.IMPORTANCE_DEFAULT] 的通知通道应该有声音。
     * 只能在频道提交前修改
     * [NotificationManager.createNotificationChannel].
     */
    fun setSound(sound: Uri?, audioAttributes: AudioAttributes?): ChannelConfig {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel!!.setSound(sound, audioAttributes)
        }
        return this
    }

    /**
     * 设置发布到此频道的通知的振动模式。如果提供的模式有效（非空、非空）
     * [NotificationChannel.enableVibration] 也将启用振动}。否则，振动将被禁用。
     * 只能在频道提交前修改
     * [NotificationManager.createNotificationChannel].
     */
    fun setVibrationPattern(vibrationPattern: LongArray?): ChannelConfig {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel!!.vibrationPattern = vibrationPattern
        }
        return this
    }

    companion object {
        @JvmField
        val DEFAULT_CHANNEL_CONFIG = ChannelConfig(
            DawnBridge.getApp().packageName,
            DawnBridge.getApp().packageName,
            NotificationUtils.IMPORTANCE_DEFAULT
        )
    }
}