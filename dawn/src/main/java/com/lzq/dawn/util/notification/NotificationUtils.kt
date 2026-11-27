package com.lzq.dawn.util.notification

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.IntDef
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.lzq.dawn.DawnBridge

/**
 * @Name :NotificationUtils
 * @Time :2022/8/16 10:07
 * @Author :  Lzq
 * @Desc : 通知
 */
object NotificationUtils {
    @field:Importance
    const val IMPORTANCE_UNSPECIFIED = NotificationManager.IMPORTANCE_UNSPECIFIED
    @field:Importance
    const val IMPORTANCE_NONE = NotificationManager.IMPORTANCE_NONE
    @field:Importance
    const val IMPORTANCE_MIN = NotificationManager.IMPORTANCE_MIN
    @field:Importance
    const val IMPORTANCE_LOW = NotificationManager.IMPORTANCE_LOW
    @field:Importance
    const val IMPORTANCE_DEFAULT = NotificationManager.IMPORTANCE_DEFAULT
    @field:Importance
    const val IMPORTANCE_HIGH = NotificationManager.IMPORTANCE_HIGH

    /**
     * 返回是否启用通知。
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun areNotificationsEnabled(): Boolean {
        return NotificationManagerCompat.from(DawnBridge.app).areNotificationsEnabled()
    }

    /**
     * 发布要在状态栏中显示的通知
     *
     * @param id       此通知的标识符
     * @param consumer 创建通知构建器的消费者
     */
    fun notify(id: Int, consumer: DawnBridge.Consumer<NotificationCompat.Builder?>?) {
        notify(null, id, ChannelConfig.DEFAULT_CHANNEL_CONFIG, consumer)
    }

    /**
     * 发布要在状态栏中显示的通知。
     *
     * @param tag      此通知的字符串标识符。可能是 `null`。
     * @param id       此通知的标识符。
     * @param consumer 创建通知构建器的消费者
     */
    fun notify(tag: String?, id: Int, consumer: DawnBridge.Consumer<NotificationCompat.Builder?>?) {
        notify(tag, id, ChannelConfig.DEFAULT_CHANNEL_CONFIG, consumer)
    }

    /**
     * 发布要在状态栏中显示的通知。
     *
     * @param id            此通知的标识符。
     * @param channelConfig config的通知通道。
     * @param consumer      创建通知构建器的消费者
     */
    fun notify(
        id: Int,
        channelConfig: ChannelConfig,
        consumer: DawnBridge.Consumer<NotificationCompat.Builder?>?
    ) {
        notify(null, id, channelConfig, consumer)
    }

    /**
     * 发布要在状态栏中显示的通知。
     *
     * @param tag           此通知的字符串标识符。可能是 `null`。
     * @param id            此通知的标识符。
     * @param channelConfig config的通知通道。
     * @param consumer      创建通知构建器的消费者
     */
    fun notify(
        tag: String?,
        id: Int,
        channelConfig: ChannelConfig,
        consumer: DawnBridge.Consumer<NotificationCompat.Builder?>?
    ) {
        if (ActivityCompat.checkSelfPermission(
                DawnBridge.app,
                permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(DawnBridge.app)
            .notify(tag, id, getNotification(channelConfig, consumer))
    }

    /**
     * 创建配置中的通知通道，并通过消费者完成通知内容设置。
     *
     * @param channelConfig 通知通道配置。
     * @param consumer 可选的通知构建回调。
     * @return 使用指定通道构建的通知。
     */
    @JvmStatic
    fun getNotification(
        channelConfig: ChannelConfig,
        consumer: DawnBridge.Consumer<NotificationCompat.Builder?>?
    ): Notification {
        val nm = DawnBridge.app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(channelConfig.notificationChannel!!)
        val builder = NotificationCompat.Builder(DawnBridge.app, channelConfig.notificationChannel!!.id)
        consumer?.accept(builder)
        return builder.build()
    }

    /**
     * 取消通知。
     *
     * @param tag The tag for the notification will be cancelled.
     * @param id  The identifier for the notification will be cancelled.
     */
    fun cancel(tag: String?, id: Int) {
        NotificationManagerCompat.from(DawnBridge.app).cancel(tag, id)
    }

    /**
     * 取消通知。
     *
     * @param id The identifier for the notification will be cancelled.
     */
    fun cancel(id: Int) {
        NotificationManagerCompat.from(DawnBridge.app).cancel(id)
    }

    /**
     * 取消所有通知。
     */
    fun cancelAll() {
        NotificationManagerCompat.from(DawnBridge.app).cancelAll()
    }

    /**
     * 设置通知栏的可见性。
     *
     *必须持有 `<uses-permission android:name="android.permission.EXPAND_STATUS_BAR" >`
     *
     *
     *
     * @param isVisible True 设置通知栏可见，否则为 false。
     */
    @RequiresPermission(permission.EXPAND_STATUS_BAR)
    fun setNotificationBarVisibility(isVisible: Boolean) {
        val methodName: String = if (isVisible) {
            "expandNotificationsPanel"
        } else {
            "collapsePanels"
        }
        invokePanels(methodName)
    }

    private fun invokePanels(methodName: String) {
        try {
            @SuppressLint("WrongConstant")
            val service = DawnBridge.app.getSystemService("statusbar")
            @SuppressLint("PrivateApi")
            val statusBarManager = Class.forName("android.app.StatusBarManager")
            val expand = statusBarManager.getMethod(methodName)
            expand.invoke(service)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @IntDef(
        NotificationManager.IMPORTANCE_UNSPECIFIED,
        NotificationManager.IMPORTANCE_NONE,
        NotificationManager.IMPORTANCE_MIN,
        NotificationManager.IMPORTANCE_LOW,
        NotificationManager.IMPORTANCE_DEFAULT,
        NotificationManager.IMPORTANCE_HIGH
    )
    @Retention(
        AnnotationRetention.SOURCE
    )
    annotation class Importance
}
