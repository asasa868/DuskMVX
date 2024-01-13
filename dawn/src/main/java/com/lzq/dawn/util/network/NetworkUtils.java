package com.lzq.dawn.util.network;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.CHANGE_WIFI_STATE;
import static android.Manifest.permission.INTERNET;
import static android.content.Context.WIFI_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

import com.lzq.dawn.DawnBridge;
import com.lzq.dawn.util.shell.CommandResult;
import com.lzq.dawn.util.shell.ShellUtils;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Name :NetworkUtils
 * @Time :2022/8/15 16:43
 * @Author :  Lzq
 * @Desc :网络
 */
public final class NetworkUtils {

    private NetworkUtils() {
    }

    /**
     * 打开无线设置。
     */
    public static void openWirelessSettings() {
        DawnBridge.getApp().startActivity(
                new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        );
    }

    /**
     * 返回网络是否连接。
     * <p>必须持有 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: connected<br>{@code false}: disconnected
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isConnected() {
        NetworkInfo info = getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    /**
     * 返回网络是否可用。
     * <p>必须持有 {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param consumer The consumer.
     * @return the task
     */
    @RequiresPermission(INTERNET)
    public static DawnBridge.Task<Boolean> isAvailableAsync(@NonNull final DawnBridge.Consumer<Boolean> consumer) {
        return DawnBridge.doAsync(new DawnBridge.Task<Boolean>(consumer) {
            @RequiresPermission(INTERNET)
            @Override
            public Boolean doInBackground() {
                return isAvailable();
            }
        });
    }

    /**
     * 返回网络是否可用。
     * <p>必须持有 {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(INTERNET)
    public static boolean isAvailable() {
        return isAvailableByDns() || isAvailableByPing(null);
    }

    /**
     * 使用 ping 返回网络是否可用。
     * <p>必须持有 {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     * <p>The default ping ip: 223.5.5.5</p>
     *
     * @param consumer The consumer.
     */
    @RequiresPermission(INTERNET)
    public static void isAvailableByPingAsync(final DawnBridge.Consumer<Boolean> consumer) {
        isAvailableByPingAsync("", consumer);
    }

    /**
     * 使用 ping 返回网络是否可用。
     * <p>必须持有 {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param ip       The ip address.
     * @param consumer The consumer.
     * @return the task
     */
    @RequiresPermission(INTERNET)
    public static DawnBridge.Task<Boolean> isAvailableByPingAsync(final String ip,
                                                                  @NonNull final DawnBridge.Consumer<Boolean> consumer) {
        return DawnBridge.doAsync(new DawnBridge.Task<Boolean>(consumer) {
            @RequiresPermission(INTERNET)
            @Override
            public Boolean doInBackground() {
                return isAvailableByPing(ip);
            }
        });
    }

    /**
     * 使用 ping 返回网络是否可用。
     * <p>必须持有 {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     * <p>The default ping ip: 223.5.5.5</p>
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(INTERNET)
    public static boolean isAvailableByPing() {
        return isAvailableByPing("");
    }

    /**
     * 使用 ping 返回网络是否可用。
     * <p>必须持有 {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param ip The ip address.
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(INTERNET)
    public static boolean isAvailableByPing(final String ip) {
        final String realIp = TextUtils.isEmpty(ip) ? "223.5.5.5" : ip;
        CommandResult result = ShellUtils.execCmd(String.format("ping -c 1 %s", realIp), false);
        return result.result == 0;
    }

    /**
     * 使用域返回网络是否可用。
     * <p>必须持有 {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param consumer The consumer.
     */
    @RequiresPermission(INTERNET)
    public static void isAvailableByDnsAsync(final DawnBridge.Consumer<Boolean> consumer) {
        isAvailableByDnsAsync("", consumer);
    }

    /**
     * 使用域返回网络是否可用。
     * <p>必须持有 {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param domain   The name of domain.
     * @param consumer The consumer.
     * @return the task
     */
    @RequiresPermission(INTERNET)
    public static DawnBridge.Task isAvailableByDnsAsync(final String domain,
                                                        @NonNull final DawnBridge.Consumer<Boolean> consumer) {
        return DawnBridge.doAsync(new DawnBridge.Task<Boolean>(consumer) {
            @RequiresPermission(INTERNET)
            @Override
            public Boolean doInBackground() {
                return isAvailableByDns(domain);
            }
        });
    }

    /**
     * 使用域返回网络是否可用。
     * <p>必须持有 {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(INTERNET)
    public static boolean isAvailableByDns() {
        return isAvailableByDns("");
    }

    /**
     * 使用域返回网络是否可用。
     * <p>必须持有 {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param domain The name of domain.
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(INTERNET)
    public static boolean isAvailableByDns(final String domain) {
        final String realDomain = TextUtils.isEmpty(domain) ? "www.baidu.com" : domain;
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(realDomain);
            return inetAddress != null;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 返回是否启用移动数据。
     *
     * @return {@code true}: enabled<br>{@code false}: disabled
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean getMobileDataEnabled() {
        try {
            TelephonyManager tm =
                    (TelephonyManager) DawnBridge.getApp().getSystemService(Context.TELEPHONY_SERVICE);
            if (tm == null) {
                return false;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return tm.isDataEnabled();
            }
            @SuppressLint("PrivateApi")
            Method getMobileDataEnabledMethod =
                    tm.getClass().getDeclaredMethod("getDataEnabled");
            if (null != getMobileDataEnabledMethod) {
                return (boolean) getMobileDataEnabledMethod.invoke(tm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 如果设备通过代理连接到互联网，则返回 true，适用于 Wi-Fi 和移动数据。
     *
     * @return 如果使用代理连接到 Internet，则为 true。
     */
    public static boolean isBehindProxy() {
        return !(System.getProperty("http.proxyHost") == null || System.getProperty("http.proxyPort") == null);
    }

    /**
     * 如果设备通过 VPN 连接到互联网，则返回 true。
     *
     * @return 如果使用 VPN 连接到 Internet，则为 true。
     */
    public static boolean isUsingVPN() {
        ConnectivityManager cm = (ConnectivityManager) DawnBridge.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return cm.getNetworkInfo(ConnectivityManager.TYPE_VPN).isConnectedOrConnecting();
        } else {
            return cm.getNetworkInfo(NetworkCapabilities.TRANSPORT_VPN).isConnectedOrConnecting();
        }
    }


    /**
     * 返回是否使用移动数据
     * <p>必须持有 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isMobileData() {
        NetworkInfo info = getActiveNetworkInfo();
        return null != info
                && info.isAvailable()
                && info.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * 返回是否使用4G。
     * <p>必须持有 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean is4G() {
        NetworkInfo info = getActiveNetworkInfo();
        return info != null
                && info.isAvailable()
                && info.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE;
    }

    /**
     * 返回是否使用5G。
     * <p>必须持有 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean is5G() {
        NetworkInfo info = getActiveNetworkInfo();
        return info != null
                && info.isAvailable()
                && info.getSubtype() == TelephonyManager.NETWORK_TYPE_NR;
    }

    /**
     * 返回是否启用 wifi。
     * <p>必须持有 {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />}</p>
     *
     * @return {@code true}: enabled<br>{@code false}: disabled
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static boolean getWifiEnabled() {
        @SuppressLint("WifiManagerLeak")
        WifiManager manager = (WifiManager) DawnBridge.getApp().getSystemService(WIFI_SERVICE);
        if (manager == null) {
            return false;
        }
        return manager.isWifiEnabled();
    }

    /**
     * 启用或禁用 wifi。
     * <p>必须持有 {@code <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />}</p>
     *
     * @param enabled True to enabled, false otherwise.
     */
    @RequiresPermission(CHANGE_WIFI_STATE)
    public static void setWifiEnabled(final boolean enabled) {
        @SuppressLint("WifiManagerLeak")
        WifiManager manager = (WifiManager) DawnBridge.getApp().getSystemService(WIFI_SERVICE);
        if (manager == null) {
            return;
        }
        if (enabled == manager.isWifiEnabled()) {
            return;
        }
        manager.setWifiEnabled(enabled);
    }

    /**
     * 返回是否连接了wifi。
     * <p>必须持有 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: connected<br>{@code false}: disconnected
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isWifiConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) DawnBridge.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 返回wifi是否可用。
     * <p>必须持有 {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />},
     * {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @return {@code true}: available<br>{@code false}: unavailable
     */
    @RequiresPermission(allOf = {ACCESS_WIFI_STATE, INTERNET})
    public static boolean isWifiAvailable() {
        return getWifiEnabled() && isAvailable();
    }

    /**
     * 返回wifi是否可用。
     * <p>必须持有 {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />},
     * {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param consumer The consumer.
     * @return the task
     */
    @RequiresPermission(allOf = {ACCESS_WIFI_STATE, INTERNET})
    public static DawnBridge.Task<Boolean> isWifiAvailableAsync(@NonNull final DawnBridge.Consumer<Boolean> consumer) {
        return DawnBridge.doAsync(new DawnBridge.Task<Boolean>(consumer) {
            @RequiresPermission(allOf = {ACCESS_WIFI_STATE, INTERNET})
            @Override
            public Boolean doInBackground() {
                return isWifiAvailable();
            }
        });
    }

    /**
     * 返回网络运营的名称。
     *
     * @return 网络运营名称
     */
    public static String getNetworkOperatorName() {
        TelephonyManager tm =
                (TelephonyManager) DawnBridge.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return "";
        }
        return tm.getNetworkOperatorName();
    }

    /**
     * 返回网络类型。
     * <p>必须持有 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return 网络类型。
     * <ul>
     * <li>{@link  NetworkType#NETWORK_ETHERNET} </li>
     * <li>{@link  NetworkType#NETWORK_WIFI    } </li>
     * <li>{@link  NetworkType#NETWORK_4G      } </li>
     * <li>{@link  NetworkType#NETWORK_3G      } </li>
     * <li>{@link  NetworkType#NETWORK_2G      } </li>
     * <li>{@link  NetworkType#NETWORK_UNKNOWN } </li>
     * <li>{@link  NetworkType#NETWORK_NO      } </li>
     * </ul>
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static NetworkType getNetworkType() {
        if (isEthernet()) {
            return NetworkType.NETWORK_ETHERNET;
        }
        NetworkInfo info = getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return NetworkType.NETWORK_WIFI;
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_GSM:
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return NetworkType.NETWORK_2G;

                    case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return NetworkType.NETWORK_3G;

                    case TelephonyManager.NETWORK_TYPE_IWLAN:
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return NetworkType.NETWORK_4G;

                    case TelephonyManager.NETWORK_TYPE_NR:
                        return NetworkType.NETWORK_5G;
                    default:
                        String subtypeName = info.getSubtypeName();
                        if (subtypeName.equalsIgnoreCase("TD-SCDMA")
                                || subtypeName.equalsIgnoreCase("WCDMA")
                                || subtypeName.equalsIgnoreCase("CDMA2000")) {
                            return NetworkType.NETWORK_3G;
                        } else {
                            return NetworkType.NETWORK_UNKNOWN;
                        }
                }
            } else {
                return NetworkType.NETWORK_UNKNOWN;
            }
        }
        return NetworkType.NETWORK_NO;
    }

    /**
     * 返回是否使用以太网。
     * <p>必须持有 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    private static boolean isEthernet() {
        final ConnectivityManager cm =
                (ConnectivityManager) DawnBridge.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        final NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if (info == null) {
            return false;
        }
        NetworkInfo.State state = info.getState();
        if (null == state) {
            return false;
        }
        return state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING;
    }

    @RequiresPermission(ACCESS_NETWORK_STATE)
    private static NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager cm =
                (ConnectivityManager) DawnBridge.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return null;
        }
        return cm.getActiveNetworkInfo();
    }

    /**
     * 返回IP地址。
     * <p>必须持有 {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param useIPv4  使用 ipv4 为真，否则为假。
     * @param consumer consumer.
     * @return the task
     */
    public static DawnBridge.Task<String> getIPAddressAsync(final boolean useIPv4,
                                                            @NonNull final DawnBridge.Consumer<String> consumer) {
        return DawnBridge.doAsync(new DawnBridge.Task<String>(consumer) {
            @RequiresPermission(INTERNET)
            @Override
            public String doInBackground() {
                return getIPAddress(useIPv4);
            }
        });
    }

    /**
     * 返回IP地址
     * <p>必须持有 {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param useIPv4 使用 ipv4 为真，否则为假。
     * @return IP地址
     */
    @RequiresPermission(INTERNET)
    public static String getIPAddress(final boolean useIPv4) {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            LinkedList<InetAddress> adds = new LinkedList<>();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp() || ni.isLoopback()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    adds.addFirst(addresses.nextElement());
                }
            }
            for (InetAddress add : adds) {
                if (!add.isLoopbackAddress()) {
                    String hostAddress = add.getHostAddress();
                    boolean isIPv4 = hostAddress.indexOf(':') < 0;
                    if (useIPv4) {
                        if (isIPv4) {
                            return hostAddress;
                        }
                    } else {
                        if (!isIPv4) {
                            int index = hostAddress.indexOf('%');
                            return index < 0
                                    ? hostAddress.toUpperCase()
                                    : hostAddress.substring(0, index).toUpperCase();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 返回广播的ip地址。
     *
     * @return 广播的ip地址。
     */
    public static String getBroadcastIpAddress() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            LinkedList<InetAddress> adds = new LinkedList<>();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                if (!ni.isUp() || ni.isLoopback()) {
                    continue;
                }
                List<InterfaceAddress> ias = ni.getInterfaceAddresses();
                for (int i = 0, size = ias.size(); i < size; i++) {
                    InterfaceAddress ia = ias.get(i);
                    InetAddress broadcast = ia.getBroadcast();
                    if (broadcast != null) {
                        return broadcast.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 返回域地址。
     * <p>必须持有 {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param domain   域的名称
     * @param consumer consumer.
     * @return the task
     */
    @RequiresPermission(INTERNET)
    public static DawnBridge.Task<String> getDomainAddressAsync(final String domain,
                                                                @NonNull final DawnBridge.Consumer<String> consumer) {
        return DawnBridge.doAsync(new DawnBridge.Task<String>(consumer) {
            @RequiresPermission(INTERNET)
            @Override
            public String doInBackground() {
                return getDomainAddress(domain);
            }
        });
    }

    /**
     * 返回域地址。
     * <p>必须持有 {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param domain 域的名称
     * @return 域地址
     */
    @RequiresPermission(INTERNET)
    public static String getDomainAddress(final String domain) {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(domain);
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 通过wifi返回IP地址。
     *
     * @return wifi的ip地址
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getIpAddressByWifi() {
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) DawnBridge.getApp().getSystemService(Context.WIFI_SERVICE);
        if (wm == null) {
            return "";
        }
        return Formatter.formatIpAddress(wm.getDhcpInfo().ipAddress);
    }

    /**
     * 返回wifi的网关地址
     *
     * @return wifi的网关地址
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getGatewayByWifi() {
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) DawnBridge.getApp().getSystemService(Context.WIFI_SERVICE);
        if (wm == null) {
            return "";
        }
        return Formatter.formatIpAddress(wm.getDhcpInfo().gateway);
    }

    /**
     * 通过wifi返回网络掩码。
     *
     * @return wifi网络掩码。
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getNetMaskByWifi() {
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) DawnBridge.getApp().getSystemService(Context.WIFI_SERVICE);
        if (wm == null) {
            return "";
        }
        return Formatter.formatIpAddress(wm.getDhcpInfo().netmask);
    }

    /**
     * 通过wifi返回服务器地址。
     *
     * @return wifi服务器地址。
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getServerAddressByWifi() {
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) DawnBridge.getApp().getSystemService(Context.WIFI_SERVICE);
        if (wm == null) {
            return "";
        }
        return Formatter.formatIpAddress(wm.getDhcpInfo().serverAddress);
    }

    /**
     * 返回 ssid。
     *
     * @return ssid.
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getSSID() {
        WifiManager wm = (WifiManager) DawnBridge.getApp().getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wm == null) {
            return "";
        }
        WifiInfo wi = wm.getConnectionInfo();
        if (wi == null) {
            return "";
        }
        String ssid = wi.getSSID();
        if (TextUtils.isEmpty(ssid)) {
            return "";
        }
        if (ssid.length() > 2 && ssid.charAt(0) == '"' && ssid.charAt(ssid.length() - 1) == '"') {
            return ssid.substring(1, ssid.length() - 1);
        }
        return ssid;
    }

    /**
     * 注册网络更改侦听器的状态。
     *
     * @param listener listener
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static void registerNetworkStatusChangedListener(final OnNetworkStatusChangedListener listener) {
        NetworkChangedReceiver.getInstance().registerListener(listener);
    }

    /**
     * 返回网络改变监听器的状态是否已经注册。
     *
     * @param listener listener
     * @return true 是, false 否.
     */
    public static boolean isRegisteredNetworkStatusChangedListener(final OnNetworkStatusChangedListener listener) {
        return NetworkChangedReceiver.getInstance().isRegistered(listener);
    }

    /**
     * 取消注册网络更改侦听器的状态。
     *
     * @param listener listener.
     */
    public static void unregisterNetworkStatusChangedListener(final OnNetworkStatusChangedListener listener) {
        NetworkChangedReceiver.getInstance().unregisterListener(listener);
    }

    @RequiresPermission(allOf = {ACCESS_WIFI_STATE, ACCESS_COARSE_LOCATION})
    public static WifiScanResults getWifiScanResult() {
        WifiScanResults result = new WifiScanResults();
        if (!getWifiEnabled()) {
            return result;
        }
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) DawnBridge.getApp().getSystemService(WIFI_SERVICE);
        //noinspection ConstantConditions
        if (ActivityCompat.checkSelfPermission(DawnBridge.getApp(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        List<ScanResult> results = wm.getScanResults();
        if (results != null) {
            result.setAllResults(results);
        }
        return result;
    }

    private static final long SCAN_PERIOD_MILLIS = 3000;
    private static final Set<DawnBridge.Consumer<WifiScanResults>> SCAN_RESULT_CONSUMERS = new CopyOnWriteArraySet<>();
    private static Timer sScanWifiTimer;
    private static WifiScanResults sPreWifiScanResults;

    @RequiresPermission(allOf = {ACCESS_WIFI_STATE, CHANGE_WIFI_STATE, ACCESS_COARSE_LOCATION})
    public static void addOnWifiChangedConsumer(final DawnBridge.Consumer<WifiScanResults> consumer) {
        if (consumer == null) {
            return;
        }
        DawnBridge.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (SCAN_RESULT_CONSUMERS.isEmpty()) {
                    SCAN_RESULT_CONSUMERS.add(consumer);
                    startScanWifi();
                    return;
                }
                consumer.accept(sPreWifiScanResults);
                SCAN_RESULT_CONSUMERS.add(consumer);
            }
        });
    }

    private static void startScanWifi() {
        sPreWifiScanResults = new WifiScanResults();
        sScanWifiTimer = new Timer();
        sScanWifiTimer.schedule(new TimerTask() {
            @RequiresPermission(allOf = {ACCESS_WIFI_STATE, CHANGE_WIFI_STATE, ACCESS_COARSE_LOCATION})
            @Override
            public void run() {
                startScanWifiIfEnabled();
                WifiScanResults scanResults = getWifiScanResult();
                if (isSameScanResults(sPreWifiScanResults.getAllResults(), scanResults.getAllResults())) {
                    return;
                }
                sPreWifiScanResults = scanResults;
                DawnBridge.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (DawnBridge.Consumer<WifiScanResults> consumer : SCAN_RESULT_CONSUMERS) {
                            consumer.accept(sPreWifiScanResults);
                        }
                    }
                });
            }
        }, 0, SCAN_PERIOD_MILLIS);
    }

    @RequiresPermission(allOf = {ACCESS_WIFI_STATE, CHANGE_WIFI_STATE})
    private static void startScanWifiIfEnabled() {
        if (!getWifiEnabled()) {
            return;
        }
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) DawnBridge.getApp().getSystemService(WIFI_SERVICE);
        //noinspection ConstantConditions
        wm.startScan();
    }

    public static void removeOnWifiChangedConsumer(final DawnBridge.Consumer<WifiScanResults> consumer) {
        if (consumer == null) {
            return;
        }
        DawnBridge.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SCAN_RESULT_CONSUMERS.remove(consumer);
                if (SCAN_RESULT_CONSUMERS.isEmpty()) {
                    stopScanWifi();
                }
            }
        });
    }

    private static void stopScanWifi() {
        if (sScanWifiTimer != null) {
            sScanWifiTimer.cancel();
            sScanWifiTimer = null;
        }
    }

    private static boolean isSameScanResults(List<ScanResult> l1, List<ScanResult> l2) {
        if (l1 == null && l2 == null) {
            return true;
        }
        if (l1 == null || l2 == null) {
            return false;
        }
        if (l1.size() != l2.size()) {
            return false;
        }
        for (int i = 0; i < l1.size(); i++) {
            ScanResult r1 = l1.get(i);
            ScanResult r2 = l2.get(i);
            if (!isSameScanResultContent(r1, r2)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSameScanResultContent(ScanResult r1, ScanResult r2) {
        return r1 != null && r2 != null && DawnBridge.equals(r1.BSSID, r2.BSSID)
                && DawnBridge.equals(r1.SSID, r2.SSID)
                && DawnBridge.equals(r1.capabilities, r2.capabilities)
                && r1.level == r2.level;
    }


}
