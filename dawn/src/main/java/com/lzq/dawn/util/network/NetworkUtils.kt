package com.lzq.dawn.util.network

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.text.format.Formatter
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.lzq.dawn.DawnBridge
import com.lzq.dawn.DawnBridge.app
import com.lzq.dawn.DawnBridge.doAsync
import com.lzq.dawn.DawnBridge.equals
import com.lzq.dawn.DawnBridge.runOnUiThread
import com.lzq.dawn.util.shell.ShellUtils
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.net.UnknownHostException
import java.util.LinkedList
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.CopyOnWriteArraySet

/**
 * @Name :NetworkUtils
 * @Time :2022/8/15 16:43
 * @Author :  Lzq
 * @Desc :网络
 */
object NetworkUtils {
    /**
     * 打开无线设置。
     */
    fun openWirelessSettings() {
        app.startActivity(
            Intent(Settings.ACTION_WIRELESS_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    @get:RequiresPermission(permission.ACCESS_NETWORK_STATE)
    val isConnected: Boolean
        /**
         * 返回网络是否连接。
         *
         * 必须持有 `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
         *
         * @return `true`: connected<br></br>`false`: disconnected
         */
        get() {
            val info = activeNetworkInfo
            return info != null && info.isConnected
        }

    /**
     * 返回网络是否可用。
     *
     * 必须持有 `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param consumer The consumer.
     * @return the task
     */
    @RequiresPermission(permission.INTERNET)
    fun isAvailableAsync(consumer: DawnBridge.Consumer<Boolean>?): DawnBridge.Task<Boolean> {
        return doAsync(object : DawnBridge.Task<Boolean>(consumer) {

            @RequiresPermission(permission.INTERNET)
            override fun doInBackground(): Boolean {
                return isAvailable
            }
        })
    }

    @get:RequiresPermission(permission.INTERNET)
    val isAvailable: Boolean
        /**
         * 返回网络是否可用。
         *
         * 必须持有 `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = isAvailableByDns || isAvailableByPing(null)

    /**
     * 使用 ping 返回网络是否可用。
     *
     * 必须持有 `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * The default ping ip: 223.5.5.5
     *
     * @param consumer The consumer.
     */
    @RequiresPermission(permission.INTERNET)
    fun isAvailableByPingAsync(consumer: DawnBridge.Consumer<Boolean>) {
        isAvailableByPingAsync("", consumer)
    }

    /**
     * 使用 ping 返回网络是否可用。
     *
     * 必须持有 `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param ip       The ip address.
     * @param consumer The consumer.
     * @return the task
     */
    @RequiresPermission(permission.INTERNET)
    fun isAvailableByPingAsync(
        ip: String?, consumer: DawnBridge.Consumer<Boolean>
    ): DawnBridge.Task<Boolean> {
        return doAsync(object : DawnBridge.Task<Boolean>(consumer) {
            @RequiresPermission(permission.INTERNET)
            override fun doInBackground(): Boolean {
                return isAvailableByPing(ip)
            }
        })
    }

    @get:RequiresPermission(permission.INTERNET)
    val isAvailableByPing: Boolean
        /**
         * 使用 ping 返回网络是否可用。
         *
         * 必须持有 `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * The default ping ip: 223.5.5.5
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = isAvailableByPing("")

    /**
     * 使用 ping 返回网络是否可用。
     *
     * 必须持有 `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param ip The ip address.
     * @return `true`: yes<br></br>`false`: no
     */
    @RequiresPermission(permission.INTERNET)
    fun isAvailableByPing(ip: String?): Boolean {
        val realIp = if (TextUtils.isEmpty(ip)) "223.5.5.5" else ip!!
        val result = ShellUtils.execCmd(String.format("ping -c 1 %s", realIp), false)
        return result.result == 0
    }

    /**
     * 使用域返回网络是否可用。
     *
     * 必须持有 `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param consumer The consumer.
     */
    @RequiresPermission(permission.INTERNET)
    fun isAvailableByDnsAsync(consumer: DawnBridge.Consumer<Boolean>) {
        isAvailableByDnsAsync("", consumer)
    }

    /**
     * 使用域返回网络是否可用。
     *
     * 必须持有 `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param domain   The name of domain.
     * @param consumer The consumer.
     * @return the task
     */
    @RequiresPermission(permission.INTERNET)
    fun isAvailableByDnsAsync(
        domain: String?, consumer: DawnBridge.Consumer<Boolean>
    ): DawnBridge.Task<*> {
        return doAsync<Boolean>(object : DawnBridge.Task<Boolean>(consumer) {
            @RequiresPermission(permission.INTERNET)
            override fun doInBackground(): Boolean {
                return isAvailableByDns(domain)
            }
        })
    }

    @get:RequiresPermission(permission.INTERNET)
    val isAvailableByDns: Boolean
        /**
         * 使用域返回网络是否可用。
         *
         * 必须持有 `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = isAvailableByDns("")

    /**
     * 使用域返回网络是否可用。
     *
     * 必须持有 `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param domain The name of domain.
     * @return `true`: yes<br></br>`false`: no
     */
    @RequiresPermission(permission.INTERNET)
    fun isAvailableByDns(domain: String?): Boolean {
        val realDomain = if (TextUtils.isEmpty(domain)) "www.baidu.com" else domain!!
        val inetAddress: InetAddress?
        return try {
            inetAddress = InetAddress.getByName(realDomain)
            inetAddress != null
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            false
        }
    }

    @get:RequiresPermission(permission.ACCESS_NETWORK_STATE)
    val mobileDataEnabled: Boolean
        /**
         * 返回是否启用移动数据。
         *
         * @return `true`: enabled<br></br>`false`: disabled
         */
        get() {
            try {
                val tm = app.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    return tm.isDataEnabled
                }
                @SuppressLint("PrivateApi") val getMobileDataEnabledMethod =
                    tm.javaClass.getDeclaredMethod("getDataEnabled")
                return getMobileDataEnabledMethod.invoke(tm) as Boolean
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }
    val isBehindProxy: Boolean
        /**
         * 如果设备通过代理连接到互联网，则返回 true，适用于 Wi-Fi 和移动数据。
         *
         * @return 如果使用代理连接到 Internet，则为 true。
         */
        get() = !(System.getProperty("http.proxyHost") == null || System.getProperty("http.proxyPort") == null)
    val isUsingVPN: Boolean
        /**
         * 如果设备通过 VPN 连接到互联网，则返回 true。
         *
         * @return 如果使用 VPN 连接到 Internet，则为 true。
         */
        get() {
            val cm = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                cm.getNetworkInfo(ConnectivityManager.TYPE_VPN)!!.isConnectedOrConnecting
            } else {
                cm.getNetworkInfo(NetworkCapabilities.TRANSPORT_VPN)!!.isConnectedOrConnecting
            }
        }

    @get:RequiresPermission(permission.ACCESS_NETWORK_STATE)
    val isMobileData: Boolean
        /**
         * 返回是否使用移动数据
         *
         * 必须持有 `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() {
            val info = activeNetworkInfo
            return null != info && info.isAvailable && info.type == ConnectivityManager.TYPE_MOBILE
        }

    /**
     * 返回是否使用4G。
     *
     * 必须持有 `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
     *
     * @return `true`: yes<br></br>`false`: no
     */
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun is4G(): Boolean {
        val info = activeNetworkInfo
        return info != null && info.isAvailable && info.subtype == TelephonyManager.NETWORK_TYPE_LTE
    }

    /**
     * 返回是否使用5G。
     *
     * 必须持有 `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
     *
     * @return `true`: yes<br></br>`false`: no
     */
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun is5G(): Boolean {
        val info = activeNetworkInfo
        return info != null && info.isAvailable && info.subtype == TelephonyManager.NETWORK_TYPE_NR
    }

    @get:RequiresPermission(permission.ACCESS_WIFI_STATE)
    @set:RequiresPermission(permission.CHANGE_WIFI_STATE)
    var wifiEnabled: Boolean
        /**
         * 返回是否启用 wifi。
         *
         * 必须持有 `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`
         *
         * @return `true`: enabled<br></br>`false`: disabled
         */
        get() {
            @SuppressLint("WifiManagerLeak") val manager =
                app.getSystemService(Context.WIFI_SERVICE) as WifiManager
            return manager.isWifiEnabled
        }
        /**
         * 启用或禁用 wifi。
         *
         * 必须持有 `<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />`
         *
         * @param enabled True to enabled, false otherwise.
         */
        set(enabled) {
            @SuppressLint("WifiManagerLeak") val manager =
                app.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (enabled == manager.isWifiEnabled) {
                return
            }
            manager.isWifiEnabled = enabled
        }

    @get:RequiresPermission(permission.ACCESS_NETWORK_STATE)
    val isWifiConnected: Boolean
        /**
         * 返回是否连接了wifi。
         *
         * 必须持有 `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
         *
         * @return `true`: connected<br></br>`false`: disconnected
         */
        get() {
            val cm = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val ni = cm.activeNetworkInfo
            return ni != null && ni.type == ConnectivityManager.TYPE_WIFI
        }

    @get:RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.INTERNET])
    val isWifiAvailable: Boolean
        /**
         * 返回wifi是否可用。
         *
         * 必须持有 `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
         * `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * @return `true`: available<br></br>`false`: unavailable
         */
        get() = wifiEnabled && isAvailable

    /**
     * 返回wifi是否可用。
     *
     * 必须持有 `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
     * `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param consumer The consumer.
     * @return the task
     */
    @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.INTERNET])
    fun isWifiAvailableAsync(consumer: DawnBridge.Consumer<Boolean>): DawnBridge.Task<Boolean> {
        return doAsync<Boolean>(object : DawnBridge.Task<Boolean>(consumer) {
            @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.INTERNET])
            override fun doInBackground(): Boolean {
                return isWifiAvailable
            }
        })
    }

    val networkOperatorName: String
        /**
         * 返回网络运营的名称。
         *
         * @return 网络运营名称
         */
        get() {
            val tm = app.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm.networkOperatorName
        }

    @JvmStatic
    @get:RequiresPermission(permission.ACCESS_NETWORK_STATE)
    val networkType: NetworkType
        /**
         * 返回网络类型。
         *
         * 必须持有 `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
         *
         * @return 网络类型。
         *
         *  * [NetworkType.NETWORK_ETHERNET]
         *  * [NetworkType.NETWORK_WIFI]
         *  * [NetworkType.NETWORK_4G]
         *  * [NetworkType.NETWORK_3G]
         *  * [NetworkType.NETWORK_2G]
         *  * [NetworkType.NETWORK_UNKNOWN]
         *  * [NetworkType.NETWORK_NO]
         *
         */
        get() {
            if (isEthernet) {
                return NetworkType.NETWORK_ETHERNET
            }
            val info = activeNetworkInfo
            return if (info != null && info.isAvailable) {
                if (info.type == ConnectivityManager.TYPE_WIFI) {
                    NetworkType.NETWORK_WIFI
                } else if (info.type == ConnectivityManager.TYPE_MOBILE) {
                    when (info.subtype) {
                        TelephonyManager.NETWORK_TYPE_GSM, TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> NetworkType.NETWORK_2G
                        TelephonyManager.NETWORK_TYPE_TD_SCDMA, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> NetworkType.NETWORK_3G
                        TelephonyManager.NETWORK_TYPE_IWLAN, TelephonyManager.NETWORK_TYPE_LTE -> NetworkType.NETWORK_4G
                        TelephonyManager.NETWORK_TYPE_NR -> NetworkType.NETWORK_5G
                        else -> {
                            val subtypeName = info.subtypeName
                            if (subtypeName.equals("TD-SCDMA", ignoreCase = true) || subtypeName.equals(
                                    "WCDMA", ignoreCase = true
                                ) || subtypeName.equals("CDMA2000", ignoreCase = true)
                            ) {
                                NetworkType.NETWORK_3G
                            } else {
                                NetworkType.NETWORK_UNKNOWN
                            }
                        }
                    }
                } else {
                    NetworkType.NETWORK_UNKNOWN
                }
            } else NetworkType.NETWORK_NO
        }

    @get:RequiresPermission(permission.ACCESS_NETWORK_STATE)
    private val isEthernet: Boolean
        /**
         * 返回是否使用以太网。
         *
         * 必须持有 `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() {
            val cm = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = cm.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET) ?: return false
            val state = info.state ?: return false
            return state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING
        }

    @get:RequiresPermission(permission.ACCESS_NETWORK_STATE)
    private val activeNetworkInfo: NetworkInfo?
        get() {
            val cm = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo
        }

    /**
     * 返回IP地址。
     *
     * 必须持有 `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param useIPv4  使用 ipv4 为真，否则为假。
     * @param consumer consumer.
     * @return the task
     */
    fun getIPAddressAsync(
        useIPv4: Boolean, consumer: DawnBridge.Consumer<String>
    ): DawnBridge.Task<String> {
        return doAsync(object : DawnBridge.Task<String>(consumer) {
            @RequiresPermission(permission.INTERNET)
            override fun doInBackground(): String {
                return getIPAddress(useIPv4)
            }
        })
    }

    /**
     * 返回IP地址
     *
     * 必须持有 `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param useIPv4 使用 ipv4 为真，否则为假。
     * @return IP地址
     */
    @RequiresPermission(permission.INTERNET)
    fun getIPAddress(useIPv4: Boolean): String {
        try {
            val nis = NetworkInterface.getNetworkInterfaces()
            val adds = LinkedList<InetAddress>()
            while (nis.hasMoreElements()) {
                val ni = nis.nextElement()
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp || ni.isLoopback) {
                    continue
                }
                val addresses = ni.inetAddresses
                while (addresses.hasMoreElements()) {
                    adds.addFirst(addresses.nextElement())
                }
            }
            for (add in adds) {
                if (!add.isLoopbackAddress) {
                    val hostAddress = add.hostAddress
                    val isIPv4 = (hostAddress?.indexOf(':') ?: 0) < 0
                    if (useIPv4) {
                        if (isIPv4) {
                            return hostAddress ?: ""
                        }
                    } else {
                        if (!isIPv4) {
                            val index = hostAddress?.indexOf('%') ?: 0
                            if (hostAddress != null) {
                                return if (index < 0) hostAddress.uppercase(Locale.getDefault()) else hostAddress.substring(
                                    0, index
                                ).uppercase(
                                    Locale.getDefault()
                                )
                            }
                        }
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return ""
    }

    val broadcastIpAddress: String?
        /**
         * 返回广播的ip地址。
         *
         * @return 广播的ip地址。
         */
        get() {
            try {
                val nis = NetworkInterface.getNetworkInterfaces()
                while (nis.hasMoreElements()) {
                    val ni = nis.nextElement()
                    if (!ni.isUp || ni.isLoopback) {
                        continue
                    }
                    val ias = ni.interfaceAddresses
                    var i = 0
                    val size = ias.size
                    while (i < size) {
                        val ia = ias[i]
                        val broadcast = ia.broadcast
                        if (broadcast != null) {
                            return broadcast.hostAddress
                        }
                        i++
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }
            return ""
        }

    /**
     * 返回域地址。
     *
     * 必须持有 `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param domain   域的名称
     * @param consumer consumer.
     * @return the task
     */
    @RequiresPermission(permission.INTERNET)
    fun getDomainAddressAsync(
        domain: String?, consumer: DawnBridge.Consumer<String>
    ): DawnBridge.Task<String> {
        return doAsync(object : DawnBridge.Task<String>(consumer) {
            @RequiresPermission(permission.INTERNET)
            override fun doInBackground(): String {
                return getDomainAddress(domain)
            }
        })
    }

    /**
     * 返回域地址。
     *
     * 必须持有 `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param domain 域的名称
     * @return 域地址
     */
    @RequiresPermission(permission.INTERNET)
    fun getDomainAddress(domain: String?): String {
        val inetAddress: InetAddress
        return try {
            inetAddress = InetAddress.getByName(domain)
            inetAddress.hostAddress?.toString() ?: ""
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            ""
        }
    }

    @get:RequiresPermission(permission.ACCESS_WIFI_STATE)
    val ipAddressByWifi: String
        /**
         * 通过wifi返回IP地址。
         *
         * @return wifi的ip地址
         */
        get() {
            @SuppressLint("WifiManagerLeak") val wm =
                app.getSystemService(Context.WIFI_SERVICE) as WifiManager
            return Formatter.formatIpAddress(wm.dhcpInfo.ipAddress)
        }

    @get:RequiresPermission(permission.ACCESS_WIFI_STATE)
    val gatewayByWifi: String
        /**
         * 返回wifi的网关地址
         *
         * @return wifi的网关地址
         */
        get() {
            @SuppressLint("WifiManagerLeak") val wm =
                app.getSystemService(Context.WIFI_SERVICE) as WifiManager
            return Formatter.formatIpAddress(wm.dhcpInfo.gateway)
        }

    @get:RequiresPermission(permission.ACCESS_WIFI_STATE)
    val netMaskByWifi: String
        /**
         * 通过wifi返回网络掩码。
         *
         * @return wifi网络掩码。
         */
        get() {
            @SuppressLint("WifiManagerLeak") val wm =
                app.getSystemService(Context.WIFI_SERVICE) as WifiManager
            return Formatter.formatIpAddress(wm.dhcpInfo.netmask)
        }

    @get:RequiresPermission(permission.ACCESS_WIFI_STATE)
    val serverAddressByWifi: String
        /**
         * 通过wifi返回服务器地址。
         *
         * @return wifi服务器地址。
         */
        get() {
            @SuppressLint("WifiManagerLeak") val wm =
                app.getSystemService(Context.WIFI_SERVICE) as WifiManager
            return Formatter.formatIpAddress(wm.dhcpInfo.serverAddress)
        }

    @get:RequiresPermission(permission.ACCESS_WIFI_STATE)
    val sSID: String
        /**
         * 返回 ssid。
         *
         * @return ssid.
         */
        get() {
            val wm = app.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wi = wm.connectionInfo ?: return ""
            val ssid = wi.ssid
            if (TextUtils.isEmpty(ssid)) {
                return ""
            }
            return if (ssid.length > 2 && ssid[0] == '"' && ssid[ssid.length - 1] == '"') {
                ssid.substring(1, ssid.length - 1)
            } else ssid
        }

    /**
     * 注册网络更改侦听器的状态。
     *
     * @param listener listener
     */
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun registerNetworkStatusChangedListener(listener: OnNetworkStatusChangedListener?) {
        NetworkChangedReceiver.instance.registerListener(listener)
    }

    /**
     * 返回网络改变监听器的状态是否已经注册。
     *
     * @param listener listener
     * @return true 是, false 否.
     */
    fun isRegisteredNetworkStatusChangedListener(listener: OnNetworkStatusChangedListener?): Boolean {
        return NetworkChangedReceiver.instance.isRegistered(listener)
    }

    /**
     * 取消注册网络更改侦听器的状态。
     *
     * @param listener listener.
     */
    fun unregisterNetworkStatusChangedListener(listener: OnNetworkStatusChangedListener?) {
        NetworkChangedReceiver.instance.unregisterListener(listener)
    }

    @get:RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.ACCESS_COARSE_LOCATION])
    val wifiScanResult: WifiScanResults?
        get() {
            val result = WifiScanResults()
            if (!wifiEnabled) {
                return result
            }
            @SuppressLint("WifiManagerLeak") val wm =
                app.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (ActivityCompat.checkSelfPermission(
                    app, permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return null
            }
            val results = wm.scanResults
            if (results != null) {
                result.allResults = results
            }
            return result
        }
    private const val SCAN_PERIOD_MILLIS: Long = 3000
    private val SCAN_RESULT_CONSUMERS: MutableSet<DawnBridge.Consumer<WifiScanResults?>> =
        CopyOnWriteArraySet()
    private var sScanWifiTimer: Timer? = null
    private var sPreWifiScanResults: WifiScanResults? = null

    @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.CHANGE_WIFI_STATE, permission.ACCESS_COARSE_LOCATION])
    fun addOnWifiChangedConsumer(consumer: DawnBridge.Consumer<WifiScanResults?>?) {
        if (consumer == null) {
            return
        }
        runOnUiThread(Runnable {
            if (SCAN_RESULT_CONSUMERS.isEmpty()) {
                SCAN_RESULT_CONSUMERS.add(consumer)
                startScanWifi()
                return@Runnable
            }
            consumer.accept(sPreWifiScanResults)
            SCAN_RESULT_CONSUMERS.add(consumer)
        })
    }

    private fun startScanWifi() {
        sPreWifiScanResults = WifiScanResults()
        sScanWifiTimer = Timer()
        sScanWifiTimer!!.schedule(object : TimerTask() {
            @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.CHANGE_WIFI_STATE, permission.ACCESS_COARSE_LOCATION])
            override fun run() {
                startScanWifiIfEnabled()
                val scanResults = wifiScanResult
                if (isSameScanResults(sPreWifiScanResults!!.allResults, scanResults!!.allResults)) {
                    return
                }
                sPreWifiScanResults = scanResults
                runOnUiThread {
                    for (consumer in SCAN_RESULT_CONSUMERS) {
                        consumer.accept(sPreWifiScanResults)
                    }
                }
            }
        }, 0, SCAN_PERIOD_MILLIS)
    }

    @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.CHANGE_WIFI_STATE])
    private fun startScanWifiIfEnabled() {
        if (!wifiEnabled) {
            return
        }
        @SuppressLint("WifiManagerLeak") val wm = app.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wm.startScan()
    }

    fun removeOnWifiChangedConsumer(consumer: DawnBridge.Consumer<WifiScanResults?>?) {
        if (consumer == null) {
            return
        }
        runOnUiThread {
            SCAN_RESULT_CONSUMERS.remove(consumer)
            if (SCAN_RESULT_CONSUMERS.isEmpty()) {
                stopScanWifi()
            }
        }
    }

    private fun stopScanWifi() {
        if (sScanWifiTimer != null) {
            sScanWifiTimer!!.cancel()
            sScanWifiTimer = null
        }
    }

    private fun isSameScanResults(l1: List<ScanResult>?, l2: List<ScanResult>?): Boolean {
        if (l1 == null && l2 == null) {
            return true
        }
        if (l1 == null || l2 == null) {
            return false
        }
        if (l1.size != l2.size) {
            return false
        }
        for (i in l1.indices) {
            val r1 = l1[i]
            val r2 = l2[i]
            if (!isSameScanResultContent(r1, r2)) {
                return false
            }
        }
        return true
    }

    private fun isSameScanResultContent(r1: ScanResult?, r2: ScanResult?): Boolean {
        return (r1 != null && r2 != null && equals(r1.BSSID, r2.BSSID) && equals(r1.SSID, r2.SSID) && equals(
            r1.capabilities, r2.capabilities
        ) && r1.level == r2.level)
    }
}