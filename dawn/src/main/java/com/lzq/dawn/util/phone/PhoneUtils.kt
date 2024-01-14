package com.lzq.dawn.util.phone

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.text.TextUtils
import androidx.annotation.RequiresPermission
import com.lzq.dawn.DawnBridge
import java.lang.reflect.InvocationTargetException

/**
 * @Name :PhoneUtils
 * @Time :2022/8/16 14:29
 * @Author :  Lzq
 * @Desc : 手机
 */
object PhoneUtils {
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

    @get:RequiresPermission(permission.READ_PHONE_STATE)
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
            if (!TextUtils.isEmpty(deviceId)) {
                return deviceId
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val imei = tm.imei
                if (!TextUtils.isEmpty(imei)) {
                    return imei
                }
                val meid = tm.meid
                return if (TextUtils.isEmpty(meid)) "" else meid
            }
            return ""
        }

    @get:RequiresPermission(permission.READ_PHONE_STATE)
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
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Build.getSerial() else Build.SERIAL
        }

    @get:RequiresPermission(permission.READ_PHONE_STATE)
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

    @get:RequiresPermission(permission.READ_PHONE_STATE)
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

    @SuppressLint("HardwareIds")
    @RequiresPermission(permission.READ_PHONE_STATE)
    fun getImeiOrMeid(isImei: Boolean): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return ""
        }
        val tm = telephonyManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (isImei) {
                getMinOne(tm.getImei(0), tm.getImei(1))
            } else {
                getMinOne(tm.getMeid(0), tm.getMeid(1))
            }
        } else {
            val ids = getSystemPropertyByReflect(if (isImei) "ril.gsm.imei" else "ril.cdma.meid")
            if (!TextUtils.isEmpty(ids)) {
                val idArr = ids.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                return if (idArr.size == 2) {
                    getMinOne(idArr[0], idArr[1])
                } else {
                    idArr[0]
                }
            }
            var id0 = tm.deviceId
            var id1 = ""
            try {
                val method = tm.javaClass.getMethod("getDeviceId", Int::class.javaPrimitiveType)
                id1 = method.invoke(
                    tm, if (isImei) TelephonyManager.PHONE_TYPE_GSM else TelephonyManager.PHONE_TYPE_CDMA
                ) as String
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
            if (isImei) {
                if (id0 != null && id0.length < 15) {
                    id0 = ""
                }
                if (id1 != null && id1.length < 15) {
                    id1 = ""
                }
            } else {
                if (id0 != null && id0.length == 14) {
                    id0 = ""
                }
                if (id1 != null && id1.length == 14) {
                    id1 = ""
                }
            }
            getMinOne(id0, id1)
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

    private fun getSystemPropertyByReflect(key: String): String {
        try {
            @SuppressLint("PrivateApi") val clz = Class.forName("android.os.SystemProperties")
            val getMethod = clz.getMethod("get", String::class.java, String::class.java)
            return getMethod.invoke(clz, key, "") as String
        } catch (e: Exception) { /**/
        }
        return ""
    }

    @get:RequiresPermission(permission.READ_PHONE_STATE)
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
        DawnBridge.getApp().startActivity(DawnBridge.getDialIntent(phoneNumber))
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
        DawnBridge.getApp().startActivity(DawnBridge.getCallIntent(phoneNumber))
    }

    /**
     * 发短信
     *
     * @param phoneNumber 电话号码
     * @param content     content.
     */
    fun sendSms(phoneNumber: String, content: String?) {
        DawnBridge.getApp().startActivity(DawnBridge.getSendSmsIntent(phoneNumber, content))
    }

    private val telephonyManager: TelephonyManager
        private get() = DawnBridge.getApp().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
}