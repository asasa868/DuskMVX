package com.lzq.dawn.util.app

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import com.lzq.dawn.DawnBridge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

/** 验证重启入口的显式目标和找不到启动页时的失败约定。 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26, 34], application = Application::class, manifest = Config.NONE)
class AppRelaunchCompatibilityTest {
    private val application get() = RuntimeEnvironment.getApplication()

    @Before
    fun initializeApplication() {
        DawnBridge.init(application)
    }

    @Test
    fun relaunchPreservesTheExplicitLauncherAndTaskFlags() {
        val component = ComponentName(application.packageName, "example.LauncherActivity")
        val packageManager = shadowOf(application.packageManager)
        packageManager.addActivityIfNotPresent(component)
        packageManager.addIntentFilterForActivity(component, IntentFilter(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        })

        AppUtils.relaunchApp()
        val intent = requireNotNull(shadowOf(application).nextStartedActivity)

        assertEquals(component, intent.component)
        assertEquals(Intent.ACTION_MAIN, intent.action)
        assertTrue(intent.hasCategory(Intent.CATEGORY_LAUNCHER))
        assertEquals(
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK,
            intent.flags
        )
    }

    @Test
    fun noLauncherKeepsTheActivityNotFoundFailureWithoutStartingAnEmptyIntent() {
        assertThrows(ActivityNotFoundException::class.java) { AppUtils.relaunchApp() }
        assertNull(shadowOf(application).nextStartedActivity)
    }

    @Test
    fun failedRelaunchDoesNotKillTheProcessWhenKillWasRequested() {
        assertThrows(ActivityNotFoundException::class.java) { AppUtils.relaunchApp(true) }
        assertNull(shadowOf(application).nextStartedActivity)
    }
}
