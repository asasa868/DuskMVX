package com.lzq.dawn.util

import android.Manifest
import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.Vibrator
import android.os.VibratorManager
import android.telephony.TelephonyManager
import androidx.core.app.NotificationCompat
import com.lzq.dawn.DawnBridge
import com.lzq.dawn.util.app.AndroidVersion
import com.lzq.dawn.util.components.intent.IntentUtils
import com.lzq.dawn.util.components.service.ServiceUtils
import com.lzq.dawn.util.device.LanguageUtils
import com.lzq.dawn.util.device.PhoneUtils
import com.lzq.dawn.util.device.VibrateUtils
import com.lzq.dawn.util.encode.EncodeUtils
import com.lzq.dawn.util.file.PathUtils
import com.lzq.dawn.util.network.NetworkUtils
import com.lzq.dawn.util.notification.ChannelConfig
import com.lzq.dawn.util.notification.NotificationUtils
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.util.Locale

/** 验证移除旧系统分支后，最低支持版本和目标版本的公开行为保持一致。 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26, 34, 37], application = Application::class, manifest = Config.NONE)
class PlatformUtilityCompatibilityTest {
    private val application get() = RuntimeEnvironment.getApplication()
    private val telephony get() = requireNotNull(application.getSystemService(TelephonyManager::class.java))

    @Before
    fun initializeApplication() {
        DawnBridge.init(application)
        shadowOf(application).grantPermissions(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.VIBRATE
        )
    }

    @Test fun channelConfigurationKeepsItsFluentSettersAndValues() {
        val pattern = longArrayOf(0, 40, 10, 60)
        val config = ChannelConfig("updates", "更新", NotificationUtils.IMPORTANCE_DEFAULT)

        val returned = config.setDescription("更新通知")
            .setGroup("messages")
            .setImportance(NotificationUtils.IMPORTANCE_HIGH)
            .setLightColor(0xff123456.toInt())
            .setLockscreenVisibility(Notification.VISIBILITY_PRIVATE)
            .setName("消息更新")
            .setShowBadge(false)
            .setBypassDnd(true)
            .setVibrationPattern(pattern)
            .setSound(null, null)
        val channel = requireNotNull(config.notificationChannel)

        assertSame(config, returned)
        assertEquals("updates", channel.id)
        assertEquals("消息更新", channel.name)
        assertEquals("更新通知", channel.description)
        assertEquals("messages", channel.group)
        assertEquals(NotificationManager.IMPORTANCE_HIGH, channel.importance)
        assertEquals(0xff123456.toInt(), channel.lightColor)
        assertEquals(Notification.VISIBILITY_PRIVATE, channel.lockscreenVisibility)
        assertFalse(channel.canShowBadge())
        assertTrue(channel.canBypassDnd())
        assertArrayEquals(pattern, channel.vibrationPattern)
        assertNull(channel.sound)
    }

    @Test fun notificationBuilderCreatesTheConfiguredChannelAndRunsTheConsumer() {
        val config = ChannelConfig("notifications", "通知", NotificationUtils.IMPORTANCE_DEFAULT)
        val consumer = object : DawnBridge.Consumer<NotificationCompat.Builder?> {
            override fun accept(t: NotificationCompat.Builder?) {
                requireNotNull(t).setContentTitle("测试通知").setSmallIcon(android.R.drawable.ic_dialog_info)
            }
        }

        val notification = NotificationUtils.getNotification(config, consumer)
        val manager = requireNotNull(application.getSystemService(NotificationManager::class.java))

        assertEquals("notifications", notification.channelId)
        assertEquals("测试通知", notification.extras.getCharSequence(Notification.EXTRA_TITLE))
        assertNotNull(manager.getNotificationChannel("notifications"))
    }

    @Test fun vibrationEffectsRetainOneShotWaveformAndCancellation() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requireNotNull(application.getSystemService(VibratorManager::class.java)).defaultVibrator
        } else {
            requireNotNull(application.getSystemService(Vibrator::class.java))
        }
        val state = shadowOf(vibrator)

        assertThrows(IllegalArgumentException::class.java) { VibrateUtils.vibrate(0) }
        try {
            VibrateUtils.vibrate(50)
            assertTrue(state.isVibrating)
            assertEquals(50L, state.milliseconds)

            VibrateUtils.vibrate(longArrayOf(0, 30, 10, 40), 1)
            assertTrue(state.isVibrating)
            assertTrue(state.repeat >= 0)
        } finally {
            VibrateUtils.cancel()
        }
        assertFalse(state.isVibrating)
        assertTrue(state.isCancelled)
    }

    @Test fun htmlDecodingPreservesEntitiesAndEmptyInput() {
        assertEquals("<你好>&", EncodeUtils.htmlDecode("&lt;你好&gt;&amp;").toString())
        assertEquals("", EncodeUtils.htmlDecode(null).toString())
        assertEquals("", EncodeUtils.htmlDecode("").toString())
    }

    @Test fun languageReadingUsesTheFirstConfiguredLocale() {
        val configuration = Configuration(application.resources.configuration)
        configuration.setLocale(Locale.FRANCE)
        val context = application.createConfigurationContext(configuration)

        assertEquals(Locale.FRANCE, LanguageUtils.getContextLanguage(context))
    }

    @Test fun supportedAndroidVersionsAndAppDataPathKeepTheirExistingValues() {
        assertTrue(AndroidVersion.isAndroid8())
        assertTrue(AndroidVersion.isAndroid6())
        assertEquals(application.dataDir.absolutePath, PathUtils.internalAppDataPath)
    }

    @Test fun installIntentPreservesContentUriPermissionsAndEmptyInput() {
        val uri = Uri.parse("content://example.provider/update.apk")

        val intent = requireNotNull(IntentUtils.getInstallAppIntent(uri))

        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals(uri, intent.data)
        assertEquals("application/vnd.android.package-archive", intent.type)
        assertTrue(intent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION != 0)
        assertTrue(intent.flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0)
        assertNull(IntentUtils.getInstallAppIntent(null as Uri?))
    }

    @Test fun shutdownIntentRetainsTheSupportedActionAndFlags() {
        val intent = IntentUtils.shutdownIntent

        assertEquals("com.android.internal.intent.action.REQUEST_SHUTDOWN", intent.action)
        assertFalse(intent.getBooleanExtra("android.intent.extra.KEY_CONFIRM", true))
        assertTrue(intent.flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0)
    }

    @Test fun serviceStartKeepsForegroundLaunchAndStoppedPackageFlag() {
        val intent = Intent(application, ExampleService::class.java)

        ServiceUtils.startService(intent)
        val started = requireNotNull(shadowOf(application).nextStartedService)

        assertEquals(intent.component, started.component)
        assertEquals(Intent.FLAG_INCLUDE_STOPPED_PACKAGES, started.flags)
    }

    @Test fun mobileDataGetterRetainsEnabledAndDisabledStates() {
        val state = shadowOf(telephony)
        state.setDataEnabled(true)
        assertTrue(NetworkUtils.mobileDataEnabled)

        state.setDataEnabled(false)
        assertFalse(NetworkUtils.mobileDataEnabled)
    }

    @Test fun phoneIdentifiersRetainLegacyPriorityAndModernPrivacyBehavior() {
        val state = shadowOf(telephony)
        state.setDeviceId("legacy")
        state.setImei("imei")
        state.setMeid("meid")
        state.setImei(0, "200")
        state.setImei(1, "100")
        state.setMeid(0, "400")
        state.setMeid(1, "300")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            assertEquals("", PhoneUtils.deviceId)
            assertEquals("", PhoneUtils.getImeiOrMeid(true))
            assertEquals("", PhoneUtils.getImeiOrMeid(false))
        } else {
            assertEquals("legacy", PhoneUtils.deviceId)
            assertEquals("100", PhoneUtils.getImeiOrMeid(true))
            assertEquals("300", PhoneUtils.getImeiOrMeid(false))
            state.setDeviceId("")
            assertEquals("imei", PhoneUtils.deviceId)
            state.setImei("")
            assertEquals("meid", PhoneUtils.deviceId)
            state.setMeid("")
            assertEquals("", PhoneUtils.deviceId)
        }
    }

    /** 提供可追踪前台启动意图的服务类型，不实际执行后台任务。 */
    class ExampleService : Service() {
        override fun onBind(intent: Intent?): IBinder? = null
    }
}
