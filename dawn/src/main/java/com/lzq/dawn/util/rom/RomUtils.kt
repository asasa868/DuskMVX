package com.lzq.dawn.util.rom

import android.annotation.SuppressLint
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.Locale
import java.util.Properties

/**
 * @Name :RomUtils
 * @Time :2022/8/29 15:14
 * @Author :  Lzq
 * @Desc : rom
 */
object RomUtils {
    private val ROM_HUAWEI = arrayOf("huawei")
    private val ROM_VIVO = arrayOf("vivo")
    private val ROM_XIAOMI = arrayOf("xiaomi")
    private val ROM_OPPO = arrayOf("oppo")
    private val ROM_LEECO = arrayOf("leeco", "letv")
    private val ROM_360 = arrayOf("360", "qiku")
    private val ROM_ZTE = arrayOf("zte")
    private val ROM_ONEPLUS = arrayOf("oneplus")
    private val ROM_NUBIA = arrayOf("nubia")
    private val ROM_COOLPAD = arrayOf("coolpad", "yulong")
    private val ROM_LG = arrayOf("lg", "lge")
    private val ROM_GOOGLE = arrayOf("google")
    private val ROM_SAMSUNG = arrayOf("samsung")
    private val ROM_MEIZU = arrayOf("meizu")
    private val ROM_LENOVO = arrayOf("lenovo")
    private val ROM_SMARTISAN = arrayOf("smartisan", "deltainno")
    private val ROM_HTC = arrayOf("htc")
    private val ROM_SONY = arrayOf("sony")
    private val ROM_GIONEE = arrayOf("gionee", "amigo")
    private val ROM_MOTOROLA = arrayOf("motorola")
    private const val VERSION_PROPERTY_HUAWEI = "ro.build.version.emui"
    private const val VERSION_PROPERTY_VIVO = "ro.vivo.os.build.display.id"
    private const val VERSION_PROPERTY_XIAOMI = "ro.build.version.incremental"
    private const val VERSION_PROPERTY_OPPO = "ro.build.version.opporom"
    private const val VERSION_PROPERTY_LEECO = "ro.letv.release.version"
    private const val VERSION_PROPERTY_360 = "ro.build.uiversion"
    private const val VERSION_PROPERTY_ZTE = "ro.build.MiFavor_version"
    private const val VERSION_PROPERTY_ONEPLUS = "ro.rom.version"
    private const val VERSION_PROPERTY_NUBIA = "ro.build.rom.id"
    private const val UNKNOWN = "unknown"
    private var bean: RomInfo? = null
    val isHuawei: Boolean
        /**
         * 返回rom是否为华为制造。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = ROM_HUAWEI[0] == romInfo!!.name
    val isVivo: Boolean
        /**
         * 返回rom是否为vivo制造。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = ROM_VIVO[0] == romInfo!!.name
    val isXiaomi: Boolean
        /**
         * 返回rom是否为小米制造。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = ROM_XIAOMI[0] == romInfo!!.name
    val isOppo: Boolean
        /**
         * 返回rom是否为oppo制造。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = ROM_OPPO[0] == romInfo!!.name
    val isLeeco: Boolean
        /**
         * 返回rom是否为leeco制造。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = ROM_LEECO[0] == romInfo!!.name

    /**
     * 返回rom是否为360制造。
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun is360(): Boolean {
        return ROM_360[0] == romInfo!!.name
    }

    val isZte: Boolean
        /**
         * 返回rom是否为zte制造。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = ROM_ZTE[0] == romInfo!!.name
    val isOneplus: Boolean
        /**
         * 返回rom是否为Oneplus制造。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = ROM_ONEPLUS[0] == romInfo!!.name
    val isNubia: Boolean
        /**
         * 返回rom是否为Nubia制造。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = ROM_NUBIA[0] == romInfo!!.name
    val isCoolpad: Boolean
        /**
         * 返回rom是否为Coolpad制造。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = ROM_COOLPAD[0] == romInfo!!.name
    val isLg: Boolean
        /**
         * 返回rom是否为Lg制造。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = ROM_LG[0] == romInfo!!.name
    val isGoogle: Boolean
        /**
         * 返回rom是否为Google制造。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = ROM_GOOGLE[0] == romInfo!!.name

    @JvmStatic
    val isSamsung: Boolean
        /**
         * 返回rom是否为Samsung制造。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = ROM_SAMSUNG[0] == romInfo!!.name
    val isMeizu: Boolean
        /**
         * 返回rom是否为Meizu制造。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = ROM_MEIZU[0] == romInfo!!.name
    val isLenovo: Boolean
        /**
         * 返回rom是否为Lenovo制造。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = ROM_LENOVO[0] == romInfo!!.name
    val isSmartisan: Boolean
        /**
         * 返回rom是否为Smartisan制造。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = ROM_SMARTISAN[0] == romInfo!!.name
    val isHtc: Boolean
        /**
         * 返回rom是否为Htc制造。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = ROM_HTC[0] == romInfo!!.name
    val isSony: Boolean
        /**
         * 返回rom是否为Sony制造。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = ROM_SONY[0] == romInfo!!.name
    val isGionee: Boolean
        /**
         * 返回rom是否为Gionee制造。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = ROM_GIONEE[0] == romInfo!!.name
    val isMotorola: Boolean
        /**
         * 返回rom是否为Motorola制造。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = ROM_MOTOROLA[0] == romInfo!!.name

    @JvmStatic
    val romInfo: RomInfo?
        /**
         * 返回rom的信息。
         *
         * @return rom的信息。
         */
        get() {
            if (bean != null) {
                return bean
            }
            bean = RomInfo()
            val brand = brand
            val manufacturer = manufacturer
            if (isRightRom(brand, manufacturer, *ROM_HUAWEI)) {
                bean!!.name = ROM_HUAWEI[0]
                val version = getRomVersion(VERSION_PROPERTY_HUAWEI)
                val temp = version.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (temp.size > 1) {
                    bean!!.version = temp[1]
                } else {
                    bean!!.version = version
                }
                return bean
            }
            if (isRightRom(brand, manufacturer, *ROM_VIVO)) {
                bean!!.name = ROM_VIVO[0]
                bean!!.version = getRomVersion(VERSION_PROPERTY_VIVO)
                return bean
            }
            if (isRightRom(brand, manufacturer, *ROM_XIAOMI)) {
                bean!!.name = ROM_XIAOMI[0]
                bean!!.version = getRomVersion(VERSION_PROPERTY_XIAOMI)
                return bean
            }
            if (isRightRom(brand, manufacturer, *ROM_OPPO)) {
                bean!!.name = ROM_OPPO[0]
                bean!!.version = getRomVersion(VERSION_PROPERTY_OPPO)
                return bean
            }
            if (isRightRom(brand, manufacturer, *ROM_LEECO)) {
                bean!!.name = ROM_LEECO[0]
                bean!!.version = getRomVersion(VERSION_PROPERTY_LEECO)
                return bean
            }
            if (isRightRom(brand, manufacturer, *ROM_360)) {
                bean!!.name = ROM_360[0]
                bean!!.version = getRomVersion(VERSION_PROPERTY_360)
                return bean
            }
            if (isRightRom(brand, manufacturer, *ROM_ZTE)) {
                bean!!.name = ROM_ZTE[0]
                bean!!.version = getRomVersion(VERSION_PROPERTY_ZTE)
                return bean
            }
            if (isRightRom(brand, manufacturer, *ROM_ONEPLUS)) {
                bean!!.name = ROM_ONEPLUS[0]
                bean!!.version = getRomVersion(VERSION_PROPERTY_ONEPLUS)
                return bean
            }
            if (isRightRom(brand, manufacturer, *ROM_NUBIA)) {
                bean!!.name = ROM_NUBIA[0]
                bean!!.version = getRomVersion(VERSION_PROPERTY_NUBIA)
                return bean
            }
            if (isRightRom(brand, manufacturer, *ROM_COOLPAD)) {
                bean!!.name = ROM_COOLPAD[0]
            } else if (isRightRom(brand, manufacturer, *ROM_LG)) {
                bean!!.name = ROM_LG[0]
            } else if (isRightRom(brand, manufacturer, *ROM_GOOGLE)) {
                bean!!.name = ROM_GOOGLE[0]
            } else if (isRightRom(brand, manufacturer, *ROM_SAMSUNG)) {
                bean!!.name = ROM_SAMSUNG[0]
            } else if (isRightRom(brand, manufacturer, *ROM_MEIZU)) {
                bean!!.name = ROM_MEIZU[0]
            } else if (isRightRom(brand, manufacturer, *ROM_LENOVO)) {
                bean!!.name = ROM_LENOVO[0]
            } else if (isRightRom(brand, manufacturer, *ROM_SMARTISAN)) {
                bean!!.name = ROM_SMARTISAN[0]
            } else if (isRightRom(brand, manufacturer, *ROM_HTC)) {
                bean!!.name = ROM_HTC[0]
            } else if (isRightRom(brand, manufacturer, *ROM_SONY)) {
                bean!!.name = ROM_SONY[0]
            } else if (isRightRom(brand, manufacturer, *ROM_GIONEE)) {
                bean!!.name = ROM_GIONEE[0]
            } else if (isRightRom(brand, manufacturer, *ROM_MOTOROLA)) {
                bean!!.name = ROM_MOTOROLA[0]
            } else {
                bean!!.name = manufacturer
            }
            bean!!.version = getRomVersion("")
            return bean
        }

    private fun isRightRom(
        brand: String,
        manufacturer: String,
        vararg names: String,
    ): Boolean {
        for (name in names) {
            if (brand.contains(name) || manufacturer.contains(name)) {
                return true
            }
        }
        return false
    }

    private val manufacturer: String
        get() {
            try {
                val manufacturer = Build.MANUFACTURER
                if (!TextUtils.isEmpty(manufacturer)) {
                    return manufacturer.lowercase(Locale.getDefault())
                }
            } catch (ignore: Throwable) {
                //
            }
            return UNKNOWN
        }
    private val brand: String
        get() {
            try {
                val brand = Build.BRAND
                if (!TextUtils.isEmpty(brand)) {
                    return brand.lowercase(Locale.getDefault())
                }
            } catch (ignore: Throwable) {
                //
            }
            return UNKNOWN
        }

    private fun getRomVersion(propertyName: String): String {
        var ret = ""
        if (!TextUtils.isEmpty(propertyName)) {
            ret = getSystemProperty(propertyName)
        }
        if (TextUtils.isEmpty(ret) || ret == UNKNOWN) {
            try {
                val display = Build.DISPLAY
                if (!TextUtils.isEmpty(display)) {
                    ret = display.lowercase(Locale.getDefault())
                }
            } catch (ignore: Throwable) {
                //
            }
        }
        return if (TextUtils.isEmpty(ret)) {
            UNKNOWN
        } else {
            ret
        }
    }

    private fun getSystemProperty(name: String): String {
        var prop = getSystemPropertyByShell(name)
        if (!TextUtils.isEmpty(prop)) {
            return prop
        }
        prop = getSystemPropertyByStream(name)
        if (!TextUtils.isEmpty(prop)) {
            return prop
        }
        return if (Build.VERSION.SDK_INT < 28) {
            getSystemPropertyByReflect(name)
        } else {
            prop
        }
    }

    private fun getSystemPropertyByShell(propName: String): String {
        var line: String
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop $propName")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            val ret = input.readLine()
            if (ret != null) {
                return ret
            }
        } catch (ignore: IOException) {
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (ignore: IOException) {
                    //
                }
            }
        }
        return ""
    }

    private fun getSystemPropertyByStream(key: String): String {
        try {
            val prop = Properties()
            val `is` =
                FileInputStream(
                    File(Environment.getRootDirectory(), "build.prop"),
                )
            prop.load(`is`)
            return prop.getProperty(key, "")
        } catch (ignore: Exception) {
            //
        }
        return ""
    }

    private fun getSystemPropertyByReflect(key: String): String {
        try {
            @SuppressLint("PrivateApi")
            val clz = Class.forName("android.os.SystemProperties")
            val getMethod = clz.getMethod("get", String::class.java, String::class.java)
            return getMethod.invoke(clz, key, "") as String
        } catch (e: Exception) {
            //
        }
        return ""
    }
}
