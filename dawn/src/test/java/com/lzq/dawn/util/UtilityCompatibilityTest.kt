package com.lzq.dawn.util

import android.app.Activity
import android.app.Application
import android.content.pm.PackageInfo
import com.lzq.dawn.DawnBridge
import com.lzq.dawn.util.app.AppUtils
import com.lzq.dawn.util.components.acivity.ActivityLifecycleImpl
import com.lzq.dawn.util.components.acivity.ActivityUtils
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import org.robolectric.util.ReflectionHelpers

/** 验证版本读取和活动管理清理告警后保持原有公开行为。 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26, 34, 37], application = Application::class, manifest = Config.NONE)
class UtilityCompatibilityTest {
    private val application get() = RuntimeEnvironment.getApplication()
    private val controllers = mutableListOf<ActivityController<Activity>>()
    private val lifecycle = ActivityLifecycleImpl()

    @Before
    fun initializeApplication() {
        DawnBridge.init(application)
        lifecycle.init(application)
    }

    @After
    fun releaseActivitiesAndCallbacks() {
        controllers.forEach { it.destroy() }
        lifecycle.unInit(application)
    }

    @Test
    fun versionCodeAndAppInfoKeepTheSameIntegerVersion() {
        val packageName = installPackage(42)

        assertEquals(42, AppUtils.getAppVersionCode(packageName))
        assertEquals(42, AppUtils.getAppInfo(packageName)?.versionCode)
    }

    @Test
    fun integerVersionBoundaryRemainsAvailableWithoutTruncationChanges() {
        val packageName = installPackage(Int.MAX_VALUE)

        assertEquals(Int.MAX_VALUE, AppUtils.getAppVersionCode(packageName))
        assertEquals(Int.MAX_VALUE, AppUtils.getAppInfo(packageName)?.versionCode)
    }

    @Test
    fun absentOrBlankPackageKeepsTheExistingFailureValues() {
        assertEquals(-1, AppUtils.getAppVersionCode(null))
        assertEquals(-1, AppUtils.getAppVersionCode(" "))
        assertEquals(-1, AppUtils.getAppVersionCode("missing.package"))
        assertNull(AppUtils.getAppInfo("missing.package"))
    }

    @Test
    fun mostRecentlyStartedActivityMovesToFrontWithoutDuplicates() {
        val first = activity()
        val second = activity()

        lifecycle.onActivityStarted(first)
        lifecycle.onActivityStarted(first)

        assertEquals(listOf(first, second), lifecycle.activityList)
        assertSame(first, lifecycle.topActivity)
    }

    @Test
    fun directActivityContextAndMissingContextKeepTheirExistingResults() {
        val activity = activity()

        assertSame(activity, ActivityUtils.getActivityByContext(activity))
        assertNull(ActivityUtils.getActivityByContext(null))
    }

    private fun activity(): Activity = Robolectric.buildActivity(Activity::class.java).create().also {
        controllers.add(it)
    }.get()

    private fun installPackage(versionCode: Int): String {
        val info = PackageInfo().apply { packageName = "test.compatibility.package" }
        // 低版本测试数据需要设置历史字段，避免测试本身调用已弃用的读取接口。
        ReflectionHelpers.setField(info, "versionCode", versionCode)
        shadowOf(application.packageManager).installPackage(info)
        return info.packageName
    }
}
