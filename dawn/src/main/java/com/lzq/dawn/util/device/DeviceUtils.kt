package com.lzq.dawn.util.device

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.lzq.dawn.DawnBridge
import java.io.File
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.Locale
import java.util.UUID

/**
 * @Name :DeviceUtils
 * @Time :2022/7/21 09:42
 * @Author :  Lzq
 * @Desc : 设备信息
 */
object DeviceUtils {
    val isDeviceRooted: Boolean
        /**
         * 返回设备是否已root。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() {
            val su = "su"
            val locations = arrayOf(
                "/system/bin/",
                "/system/xbin/",
                "/sbin/",
                "/system/sd/xbin/",
                "/system/bin/failsafe/",
                "/data/local/xbin/",
                "/data/local/bin/",
                "/data/local/",
                "/system/sbin/",
                "/usr/bin/",
                "/vendor/bin/"
            )
            for (location in locations) {
                if (File(location + su).exists()) {
                    return true
                }
            }
            return false
        }
    val isAdbEnabled: Boolean
        /**
         * 返回是否启用 ADB。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = Settings.Secure.getInt(
            DawnBridge.app.contentResolver, Settings.Global.ADB_ENABLED, 0
        ) > 0
    val sDKVersionName: String
        /**
         * 返回设备系统的版本名称。
         *
         * @return 返回设备系统的版本名称。
         */
        get() = Build.VERSION.RELEASE
    val sDKVersionCode: Int
        /**
         * 返回设备系统的版本代码。
         *
         * @return 返回设备系统的版本代码。
         */
        get() = Build.VERSION.SDK_INT

    @get:SuppressLint("HardwareIds")
    val androidID: String
        /**
         * 返回设备的 android id。
         *
         * @return 返回设备的 android id。
         */
        get() {
            val id = Settings.Secure.getString(
                DawnBridge.app.contentResolver, Settings.Secure.ANDROID_ID
            )
            return if ("9774d56d682e549c" == id) {
                ""
            } else id ?: ""
        }

    @get:RequiresPermission(allOf = [Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE])
    val macAddress: String
        /**
         * 返回 MAC 地址。
         *
         * Must hold `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
         * `<uses-permission android:name="android.permission.INTERNET" />`,
         * `<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />`
         *
         * @return 返回 MAC 地址。
         */
        get() {
            val macAddress = getMacAddress(*(null as Array<String?>?)!!)
            if (!TextUtils.isEmpty(macAddress)) {
                return macAddress
            }
            return getMacAddress(*(null as Array<String?>?)!!)
        }

    /**
     * 返回 MAC 地址。
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
     * `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @return 返回 MAC 地址。
     */
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_WIFI_STATE])
    fun getMacAddress(vararg excepts: String?): String {
        var macAddress = macAddressByNetworkInterface
        if (isAddressNotInExcepts(macAddress, *excepts)) {
            return macAddress
        }
        macAddress = macAddressByInetAddress
        if (isAddressNotInExcepts(macAddress, *excepts)) {
            return macAddress
        }
        macAddress = macAddressByWifiInfo
        if (isAddressNotInExcepts(macAddress, *excepts)) {
            return macAddress
        }
        return if (isAddressNotInExcepts(macAddress, *excepts)) {
            macAddress
        } else ""
    }

    private fun isAddressNotInExcepts(address: String, vararg excepts: String?): Boolean {
        if (TextUtils.isEmpty(address)) {
            return false
        }
        if ("02:00:00:00:00:00" == address) {
            return false
        }
        if (excepts.isEmpty()) {
            return true
        }
        for (filter in excepts) {
            if (filter != null && filter == address) {
                return false
            }
        }
        return true
    }

    @get:RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
    private val macAddressByWifiInfo: String
        get() {
            try {
                val wifi: WifiManager = DawnBridge.app.applicationContext
                    .getSystemService(Context.WIFI_SERVICE) as WifiManager
                val info: WifiInfo = wifi.getConnectionInfo()
                if (ActivityCompat.checkSelfPermission(
                        DawnBridge.app, Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return "02:00:00:00:00:00"
                }
                @SuppressLint("HardwareIds") val macAddress: String = info.macAddress
                if (!TextUtils.isEmpty(macAddress)) {
                    return macAddress
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return "02:00:00:00:00:00"
        }
    private val macAddressByNetworkInterface: String
        get() {
            try {
                val nis = NetworkInterface.getNetworkInterfaces()
                while (nis.hasMoreElements()) {
                    val ni = nis.nextElement()
                    if (ni == null || !"wlan0".equals(ni.name, ignoreCase = true)) {
                        continue
                    }
                    val macBytes = ni.hardwareAddress
                    if (macBytes != null && macBytes.isNotEmpty()) {
                        val sb = StringBuilder()
                        for (b in macBytes) {
                            sb.append(String.format("%02x:", b))
                        }
                        return sb.substring(0, sb.length - 1)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return "02:00:00:00:00:00"
        }
    private val macAddressByInetAddress: String
        get() {
            try {
                val inetAddress = inetAddress
                if (inetAddress != null) {
                    val ni = NetworkInterface.getByInetAddress(inetAddress)
                    if (ni != null) {
                        val macBytes = ni.hardwareAddress
                        if (macBytes != null && macBytes.isNotEmpty()) {
                            val sb = StringBuilder()
                            for (b in macBytes) {
                                sb.append(String.format("%02x:", b))
                            }
                            return sb.substring(0, sb.length - 1)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return "02:00:00:00:00:00"
        }
    private val inetAddress: InetAddress?
        get() {
            try {
                val nis = NetworkInterface.getNetworkInterfaces()
                while (nis.hasMoreElements()) {
                    val ni = nis.nextElement()
                    // To prevent phone of xiaomi return "10.0.2.15"
                    if (!ni.isUp) {
                        continue
                    }
                    val addresses = ni.inetAddresses
                    while (addresses.hasMoreElements()) {
                        val inetAddress = addresses.nextElement()
                        if (!inetAddress.isLoopbackAddress) {
                            val hostAddress = inetAddress.hostAddress
                            if (hostAddress != null) {
                                if (hostAddress.indexOf(':') < 0) {
                                    return inetAddress
                                }
                            }
                        }
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }
            return null
        }
    val manufacturer: String
        /**
         * Return 产品硬件的制造商。
         *
         * e.g. Xiaomi
         *
         * @return 产品硬件的制造商
         */
        get() = Build.MANUFACTURER
    val model: String
        /**
         * 返回设备型号。
         *
         * e.g. MI2SC
         *
         * @return 返回设备型号。
         */
        get() {
            var model = Build.MODEL
            model = model?.trim { it <= ' ' }?.replace("\\s*".toRegex(), "") ?: ""
            return model
        }
    val aBIs: Array<String>
        /**
         * 返回此设备支持的 ABI 的有序列表。最优选的 ABI 是列表中的第一个元素。
         *
         * @return 此设备支持的 ABI 的有序列表
         */
        get() = Build.SUPPORTED_ABIS
    val isTablet: Boolean
        /**
         * 返回设备是否为平板电脑
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = ((Resources.getSystem().configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE)
    val isEmulator: Boolean
        /**
         * 返回设备是否为模拟器。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() {
            val checkProperty =
                (Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.lowercase(Locale.getDefault())
                    .contains("vbox") || Build.FINGERPRINT.lowercase(Locale.getDefault())
                    .contains("test-keys") || Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator") || Build.MODEL.contains(
                    "Android SDK built for x86"
                ) || Build.MANUFACTURER.contains("Genymotion") || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith(
                    "generic"
                )) || "google_sdk" == Build.PRODUCT
            if (checkProperty) {
                return true
            }
            val operatorName: String?
            val tm: TelephonyManager =
                DawnBridge.app.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val name: String = tm.networkOperatorName
            operatorName = name
            val checkOperatorName = "android".equals(operatorName, ignoreCase = true)
            if (checkOperatorName) {
                return true
            }
            val url = "tel:" + "123456"
            val intent = Intent()
            intent.data = Uri.parse(url)
            intent.action = Intent.ACTION_DIAL
            return intent.resolveActivity(DawnBridge.app.packageManager) == null
        }
    val isDevelopmentSettingsEnabled: Boolean
        /**
         * 用户是否启用了开发设置。
         *
         * @return 用户是否启用了开发设置。
         */
        get() = Settings.Global.getInt(
            DawnBridge.app.contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
        ) > 0

    private fun getUdid(prefix: String, id: String): String {
        return if ("" == id) {
            prefix + UUID.randomUUID().toString().replace("-", "")
        } else prefix + UUID.nameUUIDFromBytes(id.toByteArray()).toString().replace("-", "")
    }
}