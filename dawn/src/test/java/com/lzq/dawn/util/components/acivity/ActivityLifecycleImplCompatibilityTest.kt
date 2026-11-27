package com.lzq.dawn.util.components.acivity

import android.app.Activity
import android.os.Looper
import androidx.lifecycle.Lifecycle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadow.api.Shadow

/** 验证生命周期登记、次序与销毁清理保持既有公开行为。 */
@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [26, 34, 37],
    shadows = [ActivityLifecycleImplCompatibilityTest.RecordingCallbacksShadow::class],
    instrumentedPackages = ["com.lzq.dawn.util.components.acivity"],
)
class ActivityLifecycleImplCompatibilityTest {
    @Test fun activityCreationAndDestructionKeepSnapshotsIndependent() {
        val lifecycle = ActivityLifecycleImpl()
        Robolectric.buildActivity(Activity::class.java).create().use { controller ->
            val activity = controller.get()
            lifecycle.onActivityCreated(activity, null)
            val snapshot = lifecycle.activityList

            // 先移除系统活动记录，避免空缓存从尚未销毁的控制器重新回填。
            controller.destroy()
            lifecycle.onActivityDestroyed(activity)

            assertTrue(activity.isDestroyed)
            assertEquals(listOf(activity), snapshot)
            assertTrue(lifecycle.activityList.isEmpty())
        }
    }

    @Test fun resumingAnOlderActivityMakesItTheFirstRecordedActivity() {
        val lifecycle = ActivityLifecycleImpl()
        val first = Robolectric.buildActivity(Activity::class.java).get()
        val second = Robolectric.buildActivity(Activity::class.java).get()

        lifecycle.onActivityCreated(first, null)
        lifecycle.onActivityCreated(second, null)
        lifecycle.onActivityResumed(first)

        assertEquals(listOf(first, second), lifecycle.activityList)
    }

    @Test fun globalCallbacksAreDeduplicatedAndSurviveActivityDestruction() {
        val lifecycle = ActivityLifecycleImpl()
        val callbacks = ActivityLifecycleCallbacks()
        val first = Robolectric.buildActivity(Activity::class.java).get()
        val second = Robolectric.buildActivity(Activity::class.java).get()
        lifecycle.addActivityLifecycleCallbacks(callbacks)
        lifecycle.addActivityLifecycleCallbacks(callbacks)
        shadowOf(Looper.getMainLooper()).idle()

        lifecycle.onActivityCreated(first, null)
        lifecycle.onActivityDestroyed(first)
        lifecycle.onActivityCreated(second, null)

        assertEquals(
            listOf(
                first to Lifecycle.Event.ON_CREATE,
                first to Lifecycle.Event.ON_DESTROY,
                second to Lifecycle.Event.ON_CREATE,
            ),
            events(callbacks),
        )
        lifecycle.removeActivityLifecycleCallbacks(callbacks)
    }

    @Test fun removingGlobalCallbacksStopsFurtherDelivery() {
        val lifecycle = ActivityLifecycleImpl()
        val callbacks = ActivityLifecycleCallbacks()
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        lifecycle.addActivityLifecycleCallbacks(callbacks)
        shadowOf(Looper.getMainLooper()).idle()
        lifecycle.onActivityCreated(activity, null)

        lifecycle.removeActivityLifecycleCallbacks(callbacks)
        shadowOf(Looper.getMainLooper()).idle()
        lifecycle.onActivityDestroyed(activity)

        assertEquals(listOf(activity to Lifecycle.Event.ON_CREATE), events(callbacks))
    }

    @Test fun activityCallbacksRemainIsolatedFromOtherActivitiesAndGlobalCallbacks() {
        val lifecycle = ActivityLifecycleImpl()
        val global = ActivityLifecycleCallbacks()
        val firstOnly = ActivityLifecycleCallbacks()
        val secondOnly = ActivityLifecycleCallbacks()
        val first = Robolectric.buildActivity(Activity::class.java).get()
        val second = Robolectric.buildActivity(Activity::class.java).get()
        lifecycle.addActivityLifecycleCallbacks(global)
        lifecycle.addActivityLifecycleCallbacks(first, firstOnly)
        lifecycle.addActivityLifecycleCallbacks(second, secondOnly)
        shadowOf(Looper.getMainLooper()).idle()

        lifecycle.onActivityCreated(first, null)
        lifecycle.onActivityCreated(second, null)
        lifecycle.removeActivityLifecycleCallbacks(first)
        shadowOf(Looper.getMainLooper()).idle()
        lifecycle.onActivityDestroyed(first)
        lifecycle.onActivityStarted(second)

        assertEquals(listOf(first to Lifecycle.Event.ON_CREATE), events(firstOnly))
        assertEquals(
            listOf(second to Lifecycle.Event.ON_CREATE, second to Lifecycle.Event.ON_START),
            events(secondOnly),
        )
        assertEquals(
            listOf(
                first to Lifecycle.Event.ON_CREATE,
                second to Lifecycle.Event.ON_CREATE,
                first to Lifecycle.Event.ON_DESTROY,
                second to Lifecycle.Event.ON_START,
            ),
            events(global),
        )
        lifecycle.removeActivityLifecycleCallbacks(global)
        lifecycle.onActivityDestroyed(second)
    }

    private fun events(callbacks: ActivityLifecycleCallbacks): List<Pair<Activity, Lifecycle.Event?>> {
        return Shadow.extract<RecordingCallbacksShadow>(callbacks).events.toList()
    }

    /** 记录最终监听类的公开事件入口，避免修改生产代码或读取私有登记容器。 */
    @Implements(ActivityLifecycleCallbacks::class, isInAndroidSdk = false)
    class RecordingCallbacksShadow {
        val events = mutableListOf<Pair<Activity, Lifecycle.Event?>>()

        @Implementation
        fun onLifecycleChanged(activity: Activity, event: Lifecycle.Event?) {
            events.add(activity to event)
        }
    }
}
