package com.lzq.dawn.util.language

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.Log
import com.lzq.dawn.DawnBridge
import java.util.Locale

/**
 * @Name :LanguageUtils
 * @Time :2022/8/2 11:36
 * @Author :  Lzq
 * @Desc : 语言
 */
object LanguageUtils {
    private const val KEY_LOCALE = "KEY_LOCALE"
    private const val VALUE_FOLLOW_SYSTEM = "VALUE_FOLLOW_SYSTEM"
    /**
     * 应用系统语言
     *
     * @param isRelaunchApp true 重新启动应用程序，false 重新创建所有活动。
     */
    /**
     * 应用系统语言。
     */
    @JvmOverloads
    fun applySystemLanguage(isRelaunchApp: Boolean = false) {
        applyLanguageReal(null, isRelaunchApp)
    }
    /**
     * 应用语言
     *
     * @param locale        语言环境的语言。
     * @param isRelaunchApp true 重新启动应用程序，false 重新创建所有活动。
     */
    /**
     * 应用语言
     *
     * @param locale 语言环境的语言。
     */
    @JvmOverloads
    fun applyLanguage(
        locale: Locale, isRelaunchApp: Boolean = false
    ) {
        applyLanguageReal(locale, isRelaunchApp)
    }

    private fun applyLanguageReal(
        locale: Locale?, isRelaunchApp: Boolean
    ) {
        val destLocal = locale ?: getLocal(Resources.getSystem().configuration)
        updateAppContextLanguage(destLocal, object : DawnBridge.Consumer<Boolean?> {
            override fun accept(t: Boolean?) {
                if (t == true) {
                    restart(isRelaunchApp)
                } else {
                    DawnBridge.relaunchApp()
                }
            }

        })
    }

    private fun restart(isRelaunchApp: Boolean) {
        if (isRelaunchApp) {
            DawnBridge.relaunchApp()
        } else {
            for (activity in DawnBridge.activityList) {
                activity.recreate()
            }
        }
    }

    /**
     * 返回上下文的语言环境。
     *
     * @return 返回上下文的语言环境。
     */
    fun getContextLanguage(context: Context): Locale {
        return getLocal(context.resources.configuration)
    }

    val appContextLanguage: Locale
        /**
         * 返回 applicationContext 的语言环境。
         *
         * @return 返回 applicationContext 的语言环境。
         */
        get() = getContextLanguage(DawnBridge.app!!)
    val systemLanguage: Locale
        /**
         * 返回系统的语言环境
         *
         * @return 返回系统的语言环境
         */
        get() = getLocal(Resources.getSystem().configuration)

    /**
     * 更新 applicationContext 的语言环境。
     *
     * @param destLocale dest 语言环境。
     * @param consumer   The consumer.
     */
    fun updateAppContextLanguage(destLocale: Locale, consumer: DawnBridge.Consumer<Boolean?>?) {
        pollCheckAppContextLocal(destLocale, 0, consumer)
    }

    fun pollCheckAppContextLocal(destLocale: Locale, index: Int, consumer: DawnBridge.Consumer<Boolean?>?) {
        val appResources = DawnBridge.app?.resources
        val appConfig = appResources!!.configuration
        val appLocal = getLocal(appConfig)
        setLocal(appConfig, destLocale)
        DawnBridge.app?.resources!!.updateConfiguration(appConfig, appResources.displayMetrics)
        if (consumer == null) {
            return
        }
        if (isSameLocale(appLocal, destLocale)) {
            consumer.accept(true)
        } else {
            if (index < 20) {
                DawnBridge.runOnUiThreadDelayed(
                    { pollCheckAppContextLocal(destLocale, index + 1, consumer) },
                    16
                )
                return
            }
            Log.e("LanguageUtils", "appLocal didn't update.")
            consumer.accept(false)
        }
    }

    private fun string2LocaleReal(str: String): Locale? {
        return if (!isRightFormatLocalStr(str)) {
            null
        } else try {
            val splitIndex = str.indexOf("$")
            Locale(str.substring(0, splitIndex), str.substring(splitIndex + 1))
        } catch (ignore: Exception) {
            null
        }
    }

    private fun isRightFormatLocalStr(localStr: String): Boolean {
        val chars = localStr.toCharArray()
        var count = 0
        for (c in chars) {
            if (c == '$') {
                if (count >= 1) {
                    return false
                }
                ++count
            }
        }
        return count == 1
    }

    private fun isSameLocale(l0: Locale, l1: Locale): Boolean {
        return (DawnBridge.equals(l1.language, l0.language) && DawnBridge.equals(l1.country, l0.country))
    }

    private fun getLocal(configuration: Configuration): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.locales[0]
        } else {
            configuration.locale
        }
    }

    private fun setLocal(configuration: Configuration, locale: Locale) {
        configuration.setLocale(locale)
    }
}