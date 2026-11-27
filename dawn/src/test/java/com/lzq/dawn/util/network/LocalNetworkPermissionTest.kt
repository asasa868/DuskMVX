package com.lzq.dawn.util.network

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/** 验证旧系统公开入口与 API37 沙箱中的授权、拒绝及撤销，不代替真实网络栈验证。 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26, 34], application = Application::class, manifest = Config.NONE)
class LocalNetworkPermissionTest {
    @Test
    fun oldSystemsDoNotCheckTheNewPermissionEvenWithANewTarget() {
        val context = permissionContext(targetSdk = 37)

        assertFalse(LocalNetworkPermission.isRequired(context))
        assertTrue(LocalNetworkPermission.isGranted(context))
        assertTrue(context.checkedPermissions.isEmpty())
    }

    @Test
    fun permissionPolicyRequiresBothNewSystemAndNewTarget() {
        val cases = listOf(
            Triple(36, 37, false),
            Triple(37, 36, false),
            Triple(37, 37, true),
            Triple(38, 37, true),
            Triple(37, 38, true),
        )
        for ((sdk, target, expected) in cases) {
            assertEquals(
                "sdk=$sdk target=$target",
                expected,
                LocalNetworkPermission.requiresPermission(sdk, target),
            )
        }
    }

    @Test
    fun oldTargetOnANewSystemKeepsItsExistingPermissionContract() {
        val context = permissionContext(targetSdk = 36)

        assertTrue(LocalNetworkPermission.isGranted(context, sdkInt = 37))
        assertTrue(context.checkedPermissions.isEmpty())
    }

    @Test
    fun enforcedPolicyChecksTheExactRuntimePermission() {
        val context = permissionContext(targetSdk = 37)

        assertFalse(LocalNetworkPermission.isGranted(context, sdkInt = 37))

        assertEquals(listOf("android.permission.ACCESS_LOCAL_NETWORK"), context.checkedPermissions)
        assertEquals("android.permission.ACCESS_LOCAL_NETWORK", LocalNetworkPermission.PERMISSION)
    }

    @Test
    fun grantedAndRevokedPermissionAreReadForEveryOperation() {
        val context = permissionContext(targetSdk = 37)
        context.permissionResult = PackageManager.PERMISSION_GRANTED
        assertTrue(LocalNetworkPermission.isGranted(context, sdkInt = 37))

        context.permissionResult = PackageManager.PERMISSION_DENIED

        assertFalse(LocalNetworkPermission.isGranted(context, sdkInt = 37))
        assertEquals(List(2) { LocalNetworkPermission.PERMISSION }, context.checkedPermissions)
    }

    @Test
    @Config(sdk = [37])
    fun android17PublicEntryPointsObserveDenialGrantAndRevocation() {
        val context = permissionContext(targetSdk = 37)
        assertTrue(LocalNetworkPermission.isRequired(context))
        assertFalse(LocalNetworkPermission.isGranted(context))

        context.permissionResult = PackageManager.PERMISSION_GRANTED
        assertTrue(LocalNetworkPermission.isGranted(context))

        context.permissionResult = PackageManager.PERMISSION_DENIED
        assertFalse(LocalNetworkPermission.isGranted(context))
        assertEquals(List(3) { LocalNetworkPermission.PERMISSION }, context.checkedPermissions)
    }

    @Test
    fun permissionCheckFailuresRemainVisibleToTheHost() {
        val context = permissionContext(targetSdk = 37)
        val failure = SecurityException("permission service unavailable")
        context.failure = failure

        val actual = assertThrows(SecurityException::class.java) {
            LocalNetworkPermission.isGranted(context, sdkInt = 37)
        }

        assertTrue(actual === failure)
    }

    private fun permissionContext(targetSdk: Int) = RecordingPermissionContext(
        RuntimeEnvironment.getApplication(),
        targetSdk,
    )

    private class RecordingPermissionContext(base: Context, targetSdk: Int) : ContextWrapper(base) {
        private val info = ApplicationInfo(base.applicationInfo).apply { targetSdkVersion = targetSdk }
        val checkedPermissions = mutableListOf<String>()
        var permissionResult = PackageManager.PERMISSION_DENIED
        var failure: SecurityException? = null

        override fun getApplicationInfo(): ApplicationInfo = info

        override fun checkPermission(permission: String, pid: Int, uid: Int): Int {
            checkedPermissions += permission
            failure?.let { throw it }
            return permissionResult
        }
    }
}
