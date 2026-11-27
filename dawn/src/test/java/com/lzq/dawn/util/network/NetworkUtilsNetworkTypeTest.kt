package com.lzq.dawn.util.network

import android.telephony.TelephonyManager
import org.junit.Assert.assertEquals
import org.junit.Test

/** 验证蜂窝分代、边界类型及旧名称回退保持兼容。 */
class NetworkUtilsNetworkTypeTest {
    @Test
    fun radioTechnologyRetainsItsGeneration() {
        assertEquals(NetworkType.NETWORK_2G, classify(TelephonyManager.NETWORK_TYPE_GPRS))
        assertEquals(NetworkType.NETWORK_3G, classify(TelephonyManager.NETWORK_TYPE_HSPA))
        assertEquals(NetworkType.NETWORK_4G, classify(TelephonyManager.NETWORK_TYPE_LTE))
        assertEquals(NetworkType.NETWORK_5G, classify(TelephonyManager.NETWORK_TYPE_NR))
    }

    @Test
    fun missingAndUnexpectedRadioInformationRemainUnknown() {
        assertEquals(NetworkType.NETWORK_UNKNOWN, classify(TelephonyManager.NETWORK_TYPE_UNKNOWN))
        assertEquals(NetworkType.NETWORK_UNKNOWN, classify(-1))
        assertEquals(NetworkType.NETWORK_UNKNOWN, classify(Int.MAX_VALUE))
    }

    @Test
    fun legacyRadioNamesStillMapToThreeG() {
        for (name in listOf("TD-SCDMA", "wcdma", "CDMA2000")) {
            assertEquals(NetworkType.NETWORK_3G, NetworkUtils.classifyCellularNetworkType(0, name))
        }
    }

    private fun classify(subtype: Int): NetworkType {
        return NetworkUtils.classifyCellularNetworkType(subtype, null)
    }
}
