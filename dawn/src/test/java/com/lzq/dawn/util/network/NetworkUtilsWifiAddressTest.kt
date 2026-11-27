package com.lzq.dawn.util.network

import android.Manifest
import android.app.Application
import android.net.DhcpInfo
import android.net.wifi.WifiManager
import com.lzq.dawn.DawnBridge
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

/** 验证无线网络地址格式化迁移后仍使用旧小端顺序和原有读取契约。 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26, 34], application = Application::class, manifest = Config.NONE)
class NetworkUtilsWifiAddressTest {
    private val application get() = RuntimeEnvironment.getApplication()
    private val wifiManager get() = requireNotNull(application.getSystemService(WifiManager::class.java))

    @Before fun initializeApplicationAndAddressState() {
        DawnBridge.init(application)
        shadowOf(application).grantPermissions(Manifest.permission.ACCESS_WIFI_STATE)
        shadowOf(wifiManager).setDhcpInfo(DhcpInfo())
    }

    @After fun resetAddressState() {
        shadowOf(wifiManager).setDhcpInfo(null)
    }

    @Test fun eachGetterReadsItsOwnAddressFieldInLittleEndianOrder() {
        val state = DhcpInfo().apply {
            ipAddress = 0x0101a8c0
            gateway = 0x0100000a
            netmask = 0x00ffffff
            serverAddress = 0x08080808
        }
        shadowOf(wifiManager).setDhcpInfo(state)

        val addresses = readAddresses()

        assertEquals(listOf("192.168.1.1", "10.0.0.1", "255.255.255.0", "8.8.8.8"), addresses)
    }

    @Test fun zeroAndSignedIntegerBoundariesRetainUnsignedOctets() {
        val cases = listOf(
            0 to "0.0.0.0",
            -1 to "255.255.255.255",
            Int.MIN_VALUE to "0.0.0.128",
            Int.MAX_VALUE to "255.255.255.127",
            0x01020304 to "4.3.2.1",
            -19088744 to "152.186.220.254"
        )

        for ((value, expected) in cases) {
            shadowOf(wifiManager).setDhcpInfo(allAddresses(value))

            val addresses = readAddresses()

            assertEquals(List(4) { expected }, addresses)
        }
    }

    @Test fun replacingAddressStateIsObservedByAllGetters() {
        shadowOf(wifiManager).setDhcpInfo(allAddresses(0x01020304))
        assertEquals(List(4) { "4.3.2.1" }, readAddresses())

        shadowOf(wifiManager).setDhcpInfo(allAddresses(0x08080808))

        assertEquals(List(4) { "8.8.8.8" }, readAddresses())
    }

    @Test fun missingAddressStateRetainsTheExistingNullFailure() {
        shadowOf(wifiManager).setDhcpInfo(null)

        assertThrows(NullPointerException::class.java) { NetworkUtils.ipAddressByWifi }
        assertThrows(NullPointerException::class.java) { NetworkUtils.gatewayByWifi }
        assertThrows(NullPointerException::class.java) { NetworkUtils.netMaskByWifi }
        assertThrows(NullPointerException::class.java) { NetworkUtils.serverAddressByWifi }
    }

    private fun allAddresses(value: Int) = DhcpInfo().apply {
        ipAddress = value
        gateway = value
        netmask = value
        serverAddress = value
    }

    private fun readAddresses() = listOf(
        NetworkUtils.ipAddressByWifi,
        NetworkUtils.gatewayByWifi,
        NetworkUtils.netMaskByWifi,
        NetworkUtils.serverAddressByWifi
    )
}
