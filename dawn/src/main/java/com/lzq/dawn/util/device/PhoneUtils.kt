package com.lzq.dawn.util.device

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.text.TextUtils
import androidx.annotation.RequiresPermission
import com.lzq.dawn.DawnBridge

/**
 * @Name :PhoneUtils
 * @Time :2022/8/16 14:29
 * @Author :  Lzq
 * @Desc : 手机
 *
 * Android 8–9 的设备标识接口接受普通或特权电话状态权限。Android 10 起，
 * 原生接口还受特权、运营商或设备管理等系统授权限制；deviceId/getImeiOrMeid
 * 保留直接返回空值的约定，serial/iMSI 保留原有读取与权限异常降级流程。
 * 特权应用须在自身清单声明权限，库不向普通应用合并系统专用权限。
 */
object PhoneUtils {
    private const val PRIVILEGED_PHONE_STATE = "android.permission.READ_PRIVILEGED_PHONE_STATE"

    val isPhone: Boolean
        /**
         * Return 设备是否为手机。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() {
            val tm = telephonyManager
            return tm.phoneType != TelephonyManager.PHONE_TYPE_NONE
        }

    @get:RequiresPermission(anyOf = [permission.READ_PHONE_STATE, PRIVILEGED_PHONE_STATE])
    @get:SuppressLint("HardwareIds")
    val deviceId: String
        /**
         * Return 唯一的设备 ID。
         *
         * 如果SDK版本大于28，则返回空字符串。
         *
         *
         *
         * 必须持有 `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
         *
         * @return 唯一的设备 ID。
         */
        get() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return ""
            }
            val tm = telephonyManager
            val deviceId = tm.deviceId
            if (!deviceId.isNullOrEmpty()) {
                return deviceId
            }
            val imei = tm.imei
            if (!TextUtils.isEmpty(imei)) {
                return imei
            }
            val meid = tm.meid
            return if (TextUtils.isEmpty(meid)) "" else meid
        }

    @get:RequiresPermission(anyOf = [permission.READ_PHONE_STATE, PRIVILEGED_PHONE_STATE])
    @get:SuppressLint("HardwareIds")
    val serial: String
        /**
         * Return 设备的序列号。
         *
         * @return 设备的序列号。
         */
        get() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return try {
                    Build.getSerial()
                } catch (e: SecurityException) {
                    e.printStackTrace()
                    ""
                }
            }
            return Build.getSerial()
        }

    @get:RequiresPermission(anyOf = [permission.READ_PHONE_STATE, PRIVILEGED_PHONE_STATE])
    val iMEI: String?
        /**
         * Return IMEI
         *
         * 如果SDK版本大于28，则返回空字符串。
         *
         *
         *
         * 必须持有`<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
         *
         * @return IMEI
         */
        get() = getImeiOrMeid(true)

    @get:RequiresPermission(anyOf = [permission.READ_PHONE_STATE, PRIVILEGED_PHONE_STATE])
    val mEID: String?
        /**
         * Return  MEID.
         *
         * 如果SDK版本大于28，则返回空字符串。
         *
         * 必须持有 `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
         *
         * @return MEID
         */
        get() = getImeiOrMeid(false)

    /**
     * 返回双卡标识中字典序较小的非空值；受隐私限制的新系统继续返回空字符串。
     *
     * @param isImei 是否读取国际移动设备识别码，否则读取移动设备识别码。
     * @return 选中的设备标识，没有可用值时返回空字符串。
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(anyOf = [permission.READ_PHONE_STATE, PRIVILEGED_PHONE_STATE])
    fun getImeiOrMeid(isImei: Boolean): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return ""
        }
        val tm = telephonyManager
        return if (isImei) {
            getMinOne(tm.getImei(0), tm.getImei(1))
        } else {
            getMinOne(tm.getMeid(0), tm.getMeid(1))
        }
    }

    private fun getMinOne(s0: String?, s1: String?): String? {
        val empty0 = TextUtils.isEmpty(s0)
        val empty1 = TextUtils.isEmpty(s1)
        if (empty0 && empty1) {
            return ""
        }
        if (!empty0 && !empty1) {
            return if (s0!!.compareTo(s1!!) <= 0) {
                s0
            } else {
                s1
            }
        }
        return if (!empty0) {
            s0
        } else s1
    }

    @get:RequiresPermission(anyOf = [permission.READ_PHONE_STATE, PRIVILEGED_PHONE_STATE])
    @get:SuppressLint("HardwareIds")
    val iMSI: String
        /**
         * Return IMSI.
         *
         * 必须持有 `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
         *
         * @return the IMSI
         */
        get() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    telephonyManager.subscriberId
                } catch (e: SecurityException) {
                    e.printStackTrace()
                    return ""
                }
            }
            return telephonyManager.subscriberId
        }
    val phoneType: Int
        /**
         * Returns 当前电话类型。
         *
         * @return the current phone type
         *
         *  * [TelephonyManager.PHONE_TYPE_NONE]
         *  * [TelephonyManager.PHONE_TYPE_GSM]
         *  * [TelephonyManager.PHONE_TYPE_CDMA]
         *  * [TelephonyManager.PHONE_TYPE_SIP]
         *
         */
        get() {
            val tm = telephonyManager
            return tm.phoneType
        }
    val isSimCardReady: Boolean
        /**
         * Return sim卡状态是否就绪。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() {
            val tm = telephonyManager
            return tm.simState == TelephonyManager.SIM_STATE_READY
        }
    val simOperatorName: String
        /**
         * Return sim 操作员名称。
         *
         * @return sim 操作员名称。
         */
        get() {
            val tm = telephonyManager
            return tm.simOperatorName
        }
    val simOperatorByMnc: String
        /**
         * Return mnc 返回 sim 运算符。
         *
         * @return sim 运算符。
         */
        get() {
            val tm = telephonyManager
            val operator = tm.simOperator ?: return ""
            return when (operator) {
                "46000", "46002", "46007", "46020" -> "中国移动"
                "46001", "46006", "46009" -> "中国联通"
                "46003", "46005", "46011" -> "中国电信"
                else -> operator
            }
        }

    /**
     * 跳到拨号。
     *
     * @param phoneNumber 电话号码。
     */
    fun dial(phoneNumber: String) {
        DawnBridge.app.startActivity(DawnBridge.getDialIntent(phoneNumber))
    }

    /**
     * 直接拨号
     *
     * 必须持有`<uses-permission android:name="android.permission.CALL_PHONE" />`
     *
     * @param phoneNumber 电话号码。
     */
    @RequiresPermission(permission.CALL_PHONE)
    fun call(phoneNumber: String) {
        DawnBridge.app.startActivity(DawnBridge.getCallIntent(phoneNumber))
    }

    /**
     * 发短信
     *
     * @param phoneNumber 电话号码
     * @param content     content.
     */
    fun sendSms(phoneNumber: String, content: String?) {
        DawnBridge.app.startActivity(DawnBridge.getSendSmsIntent(phoneNumber, content))
    }

    private val telephonyManager: TelephonyManager
        get() = DawnBridge.app.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
}
