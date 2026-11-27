package com.lzq.dawn.util.device

import android.app.Application
import android.telephony.TelephonyManager
import com.lzq.dawn.DawnBridge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

/** 验证旧设备标识优先于制式专用标识，并保留旧系统的回退和权限失败。 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26, 28], application = Application::class, manifest = Config.NONE)
class PhoneDeviceIdentifierCompatibilityTest {
    private val application get() = RuntimeEnvironment.getApplication()
    private val telephony get() = requireNotNull(application.getSystemService(TelephonyManager::class.java))

    @Before
    fun initializeApplication() {
        DawnBridge.init(application)
        shadowOf(telephony).setReadPhoneStatePermission(true)
        shadowOf(telephony).setDeviceId(null)
        shadowOf(telephony).setImei("imei-primary")
        shadowOf(telephony).setMeid("meid-primary")
    }

    @Test
    fun gsmDeviceRetainsItsLegacyDeviceIdentifierPriority() {
        shadowOf(telephony).setPhoneType(TelephonyManager.PHONE_TYPE_GSM)
        shadowOf(telephony).setDeviceId("gsm-legacy")

        assertEquals("gsm-legacy", PhoneUtils.deviceId)

        shadowOf(telephony).setDeviceId("imei-primary")
        assertEquals("imei-primary", PhoneUtils.deviceId)
    }

    @Test
    fun cdmaDeviceRetainsItsLegacyDeviceIdentifierPriority() {
        shadowOf(telephony).setPhoneType(TelephonyManager.PHONE_TYPE_CDMA)
        shadowOf(telephony).setDeviceId("meid-primary")

        assertEquals("meid-primary", PhoneUtils.deviceId)
    }

    @Test
    fun missingTypeSpecificIdentityStillFallsBackToTheOtherIdentifier() {
        shadowOf(telephony).setPhoneType(TelephonyManager.PHONE_TYPE_GSM)
        shadowOf(telephony).setImei("")

        assertEquals("meid-primary", PhoneUtils.deviceId)
    }

    @Test
    fun emptyLegacyIdentifierStillPrefersInternationalIdentityOnCdma() {
        shadowOf(telephony).setPhoneType(TelephonyManager.PHONE_TYPE_CDMA)
        shadowOf(telephony).setDeviceId("")

        assertEquals("imei-primary", PhoneUtils.deviceId)
    }

    @Test
    fun unknownPhoneTypeKeepsTheExistingInternationalIdentityFallback() {
        shadowOf(telephony).setPhoneType(TelephonyManager.PHONE_TYPE_NONE)

        assertEquals("imei-primary", PhoneUtils.deviceId)
    }

    @Test
    fun unavailableIdentifiersKeepTheEmptyResult() {
        shadowOf(telephony).setPhoneType(TelephonyManager.PHONE_TYPE_GSM)
        shadowOf(telephony).setImei(null)
        shadowOf(telephony).setMeid(null)

        assertEquals("", PhoneUtils.deviceId)
    }

    @Test
    fun deniedPhonePermissionStillPropagatesItsSecurityException() {
        shadowOf(telephony).setPhoneType(TelephonyManager.PHONE_TYPE_GSM)
        shadowOf(telephony).setReadPhoneStatePermission(false)

        assertThrows(SecurityException::class.java) { PhoneUtils.deviceId }
    }

    @Test
    @Config(sdk = [34])
    fun privacyRestrictedSystemKeepsTheEmptyResultWithoutReadingHardwareIds() {
        shadowOf(telephony).setReadPhoneStatePermission(false)

        assertEquals("", PhoneUtils.deviceId)
    }
}
