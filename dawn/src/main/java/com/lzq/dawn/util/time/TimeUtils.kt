package com.lzq.dawn.util.time

import android.annotation.SuppressLint
import android.provider.Settings
import com.lzq.dawn.DawnBridge
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale

/**
 * @Name :TimeUtils
 * @Time :2022/8/31 15:34
 * @Author :  Lzq
 * @Desc : time
 */
object TimeUtils {

    class TimeThreadLocal : ThreadLocal<Map<String, SimpleDateFormat>>() {
        override fun initialValue(): Map<String, SimpleDateFormat> {
            return mutableMapOf()
        }
    }

    private val SDF_THREAD_LOCAL: ThreadLocal<Map<String, SimpleDateFormat>> = TimeThreadLocal()

    private val defaultFormat: SimpleDateFormat
        get() = getSafeDateFormat("yyyy-MM-dd HH:mm:ss")
    val isUsingNetworkProvidedTime: Boolean
        /**
         * 检查设备是否正在使用网络提供时间。
         *
         *
         * 在您想要验证设备是否设置了正确的时间、
         * 避免欺诈或想要防止用户弄乱时间并滥用您的“一次性”和“过期”功能的情况下很有用。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = Settings.Global.getInt(DawnBridge.app.contentResolver, Settings.Global.AUTO_TIME, 0) == 1

    @SuppressLint("SimpleDateFormat")
    fun getSafeDateFormat(pattern: String): SimpleDateFormat {
        val sdfMap = SDF_THREAD_LOCAL.get()?.toMutableMap()
        var simpleDateFormat = sdfMap?.get(pattern)
        if (simpleDateFormat == null) {
            simpleDateFormat = SimpleDateFormat(pattern)
            sdfMap?.set(pattern, simpleDateFormat)
        }
        return simpleDateFormat
    }

    /**
     * 格式化时间字符串的毫秒数
     *
     * @param millis  毫秒
     * @param pattern 日期格式的模式，如 yyyy/MM/dd HH:mm
     * @return 格式化的时间字符串
     */
    fun millis2String(millis: Long, pattern: String): String {
        return millis2String(millis, getSafeDateFormat(pattern))
    }
    /**
     * 格式化时间字符串的毫秒数
     *
     * @param millis 毫秒
     * @param format 格式
     * @return 格式化的时间字符串
     */
    @JvmOverloads
    fun millis2String(millis: Long, format: DateFormat = defaultFormat): String {
        return format.format(Date(millis))
    }

    /**
     * 以毫秒为单位的格式化时间字符串。
     *
     * @param time    格式化的时间字符串。
     * @param pattern 日期格式的模式，如 yyyy/MM/dd HH:mm
     * @return 毫秒
     */
    fun string2Millis(time: String?, pattern: String): Long {
        return string2Millis(time, getSafeDateFormat(pattern))
    }
    /**
     * 以毫秒为单位的格式化时间字符串。
     *
     * @param time   格式化的时间字符串。
     * @param format 格式
     * @return 毫秒
     */
    @JvmOverloads
    fun string2Millis(time: String? , format: DateFormat = defaultFormat): Long {
        try {
            return time?.let { format.parse(it)?.time } ?: 0L
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return -1
    }

    /**
     * 格式化的时间字符串到日期。
     *
     * @param time    格式化的时间字符串。
     * @param pattern 日期格式的模式，如 yyyy/MM/dd HH:mm
     * @return the date
     */
    fun string2Date(time: String?, pattern: String): Date? {
        return string2Date(time, getSafeDateFormat(pattern))
    }
    /**
     * 格式化的时间字符串到日期。
     *
     * @param time   格式化的时间字符串。
     * @param format 格式
     * @return date
     */
    @JvmOverloads
    fun string2Date(time: String?, format: DateFormat = defaultFormat): Date? {
        try {
            return format.parse(time?:"")
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 日期到格式化时间字符串
     *
     * @param date    date.
     * @param pattern 日期格式的模式，如 yyyy/MM/dd HH:mm
     * @return 格式化时间字符串。
     */
    fun date2String(date: Date?, pattern: String): String {
        return getSafeDateFormat(pattern).format(date?:Date())
    }
    /**
     * 日期到格式化时间字符串
     *
     * @param date   date.
     * @param format 格式
     * @return 格式化时间字符串。
     */
    @JvmOverloads
    fun date2String(date: Date?, format: DateFormat = defaultFormat): String {
        return format.format(date?:Date())
    }

    /**
     * 以毫秒为单位的日期
     *
     * @param date date.
     * @return 毫秒
     */
    private fun date2Millis(date: Date): Long {
        return date.time
    }

    /**
     * 毫秒数到日期
     *
     * @param millis 毫秒
     * @return date
     */
    private fun millis2Date(millis: Long): Date {
        return Date(millis)
    }

    /**
     * Return the time span, in unit.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time1 第一个格式化的时间字符串。
     * @param time2 第二个格式化的时间字符串。
     * @param unit  单位
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the time span, in unit
     */
    fun getTimeSpan(
        time1: String?, time2: String?, @TimeConstants.Unit unit: Int
    ): Long {
        return getTimeSpan(time1, time2, defaultFormat, unit)
    }

    /**
     * Return the time span, in unit.
     *
     * @param time1  第一个格式化的时间字符串。
     * @param time2  第二个格式化的时间字符串。
     * @param format 格式
     * @param unit   单位
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the time span, in unit
     */
     fun getTimeSpan(
        time1: String?, time2: String?, format: DateFormat, @TimeConstants.Unit unit: Int
    ): Long {
        return millis2TimeSpan(string2Millis(time1, format) - string2Millis(time2, format), unit)
    }

    /**
     * Return the time span, in unit.
     *
     * @param date1 The first date.
     * @param date2 The second date.
     * @param unit  The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the time span, in unit
     */
     fun getTimeSpan(
        date1: Date, date2: Date, @TimeConstants.Unit unit: Int
    ): Long {
        return millis2TimeSpan(date2Millis(date1) - date2Millis(date2), unit)
    }

    /**
     * Return the time span, in unit.
     *
     * @param millis1 The first milliseconds.
     * @param millis2 The second milliseconds.
     * @param unit    The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the time span, in unit
     */
    fun getTimeSpan(
        millis1: Long, millis2: Long, @TimeConstants.Unit unit: Int
    ): Long {
        return millis2TimeSpan(millis1 - millis2, unit)
    }

    /**
     * Return the fit time span.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time1     The first formatted time string.
     * @param time2     The second formatted time string.
     * @param precision The precision of time span.
     *
     *  * precision = 0, return null
     *  * precision = 1, return 天
     *  * precision = 2, return 天, 小时
     *  * precision = 3, return 天, 小时, 分钟
     *  * precision = 4, return 天, 小时, 分钟, 秒
     *  * precision &gt;= 5，return 天, 小时, 分钟, 秒, 毫秒
     *
     * @return the fit time span
     */
    fun getFitTimeSpan(
        time1: String?, time2: String?, precision: Int
    ): String? {
        val delta = string2Millis(time1, defaultFormat) - string2Millis(time2, defaultFormat)
        return millis2FitTimeSpan(delta, precision)
    }

    /**
     * Return the fit time span.
     *
     * @param time1     The first formatted time string.
     * @param time2     The second formatted time string.
     * @param format    The format.
     * @param precision The precision of time span.
     *
     *  * precision = 0, return null
     *  * precision = 1, return 天
     *  * precision = 2, return 天, 小时
     *  * precision = 3, return 天, 小时, 分钟
     *  * precision = 4, return 天, 小时, 分钟, 秒
     *  * precision &gt;= 5，return 天, 小时, 分钟, 秒, 毫秒
     *
     * @return the fit time span
     */
    fun getFitTimeSpan(
        time1: String?, time2: String?, format: DateFormat, precision: Int
    ): String? {
        val delta = string2Millis(time1, format) - string2Millis(time2, format)
        return millis2FitTimeSpan(delta, precision)
    }

    /**
     * Return the fit time span.
     *
     * @param date1     The first date.
     * @param date2     The second date.
     * @param precision The precision of time span.
     *
     *  * precision = 0, return null
     *  * precision = 1, return 天
     *  * precision = 2, return 天, 小时
     *  * precision = 3, return 天, 小时, 分钟
     *  * precision = 4, return 天, 小时, 分钟, 秒
     *  * precision &gt;= 5，return 天, 小时, 分钟, 秒, 毫秒
     *
     * @return the fit time span
     */
    fun getFitTimeSpan(date1: Date, date2: Date, precision: Int): String? {
        return millis2FitTimeSpan(date2Millis(date1) - date2Millis(date2), precision)
    }

    /**
     * Return the fit time span.
     *
     * @param millis1   The first milliseconds.
     * @param millis2   The second milliseconds.
     * @param precision The precision of time span.
     *
     *  * precision = 0, return null
     *  * precision = 1, return 天
     *  * precision = 2, return 天, 小时
     *  * precision = 3, return 天, 小时, 分钟
     *  * precision = 4, return 天, 小时, 分钟, 秒
     *  * precision &gt;= 5，return 天, 小时, 分钟, 秒, 毫秒
     *
     * @return the fit time span
     */
    fun getFitTimeSpan(
        millis1: Long, millis2: Long, precision: Int
    ): String? {
        return millis2FitTimeSpan(millis1 - millis2, precision)
    }

    val nowMills: Long
        /**
         * 以毫秒为单位返回当前时间。
         *
         * @return 当前时间（以毫秒为单位）
         */
        get() = System.currentTimeMillis()
    val nowString: String
        /**
         * 返回当前格式化的时间字符串。
         *
         * 模式是`yyyy-MM-dd HH:mm:ss`.
         *
         * @return 当前格式化的时间字符串。
         */
        get() = millis2String(System.currentTimeMillis(), defaultFormat)

    /**
     * 返回当前格式化的时间字符串。
     *
     * @param format 格式
     * @return 当前格式化的时间字符串。
     */
    fun getNowString(format: DateFormat): String {
        return millis2String(System.currentTimeMillis(), format)
    }

    val nowDate: Date
        /**
         * 返回当前日期。
         *
         * @return 当前日期。
         */
        get() = Date()

    /**
     * Return the time span by now, in unit.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time The formatted time string.
     * @param unit The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the time span by now, in unit
     */
    fun getTimeSpanByNow(time: String?, @TimeConstants.Unit unit: Int): Long {
        return getTimeSpan(time, nowString, defaultFormat, unit)
    }

    /**
     * Return the time span by now, in unit.
     *
     * @param time   The formatted time string.
     * @param format The format.
     * @param unit   The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the time span by now, in unit
     */
    fun getTimeSpanByNow(
        time: String?, format: DateFormat, @TimeConstants.Unit unit: Int
    ): Long {
        return getTimeSpan(time, getNowString(format), format, unit)
    }

    /**
     * Return the time span by now, in unit.
     *
     * @param date The date.
     * @param unit The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the time span by now, in unit
     */
    fun getTimeSpanByNow(date: Date, @TimeConstants.Unit unit: Int): Long {
        return getTimeSpan(date, Date(), unit)
    }

    /**
     * Return the time span by now, in unit.
     *
     * @param millis The milliseconds.
     * @param unit   The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the time span by now, in unit
     */
    fun getTimeSpanByNow(millis: Long, @TimeConstants.Unit unit: Int): Long {
        return getTimeSpan(millis, System.currentTimeMillis(), unit)
    }

    /**
     * Return the fit time span by now.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time      The formatted time string.
     * @param precision The precision of time span.
     *
     *  * precision = 0，返回 null
     *  * precision = 1，返回天
     *  * precision = 2，返回天和小时
     *  * precision = 3，返回天、小时和分钟
     *  * precision = 4，返回天、小时、分钟和秒
     *  * precision &gt;= 5，返回天、小时、分钟、秒和毫秒
     *
     * @return the fit time span by now
     */
    fun getFitTimeSpanByNow(time: String?, precision: Int): String? {
        return getFitTimeSpan(time, nowString, defaultFormat, precision)
    }

    /**
     * Return the fit time span by now.
     *
     * @param time      The formatted time string.
     * @param format    The format.
     * @param precision The precision of time span.
     *
     *  * precision = 0，返回 null
     *  * precision = 1，返回天
     *  * precision = 2，返回天和小时
     *  * precision = 3，返回天、小时和分钟
     *  * precision = 4，返回天、小时、分钟和秒
     *  * precision &gt;= 5，返回天、小时、分钟、秒和毫秒
     *
     * @return the fit time span by now
     */
    fun getFitTimeSpanByNow(
        time: String?, format: DateFormat, precision: Int
    ): String? {
        return getFitTimeSpan(time, getNowString(format), format, precision)
    }

    /**
     * Return the fit time span by now.
     *
     * @param date      The date.
     * @param precision The precision of time span.
     *
     *  * precision = 0，返回 null
     *  * precision = 1，返回天
     *  * precision = 2，返回天和小时
     *  * precision = 3，返回天、小时和分钟
     *  * precision = 4，返回天、小时、分钟和秒
     *  * precision &gt;= 5，返回天、小时、分钟、秒和毫秒
     *
     * @return the fit time span by now
     */
    fun getFitTimeSpanByNow(date: Date, precision: Int): String? {
        return getFitTimeSpan(date, nowDate, precision)
    }

    /**
     * Return the fit time span by now.
     *
     * @param millis    The milliseconds.
     * @param precision The precision of time span.
     *
     *  * precision = 0，返回 null
     *  * precision = 1，返回天
     *  * precision = 2，返回天和小时
     *  * precision = 3，返回天、小时和分钟
     *  * precision = 4，返回天、小时、分钟和秒
     *  * precision &gt;= 5，返回天、小时、分钟、秒和毫秒
     *
     * @return the fit time span by now
     */
    fun getFitTimeSpanByNow(millis: Long, precision: Int): String? {
        return getFitTimeSpan(millis, System.currentTimeMillis(), precision)
    }

    /**
     * Return the friendly time span by now.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time The formatted time string.
     * @return the friendly time span by now
     *
     *  * 如果小于 1 秒钟内，显示刚刚
     *  * 如果在 1 分钟内，显示 XXX秒前
     *  * 如果在 1 小时内，显示 XXX分钟前
     *  * 如果在 1 小时外的今天内，显示今天15:32
     *  * 如果是昨天的，显示昨天15:32
     *  * 其余显示，2016-10-15
     *  * 时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007
     *
     */
    fun getFriendlyTimeSpanByNow(time: String?): String {
        return getFriendlyTimeSpanByNow(time, defaultFormat)
    }

    /**
     * Return the friendly time span by now.
     *
     * @param time   The formatted time string.
     * @param format The format.
     * @return the friendly time span by now
     *
     *  * 如果小于 1 秒钟内，显示刚刚
     *  * 如果在 1 分钟内，显示 XXX秒前
     *  * 如果在 1 小时内，显示 XXX分钟前
     *  * 如果在 1 小时外的今天内，显示今天15:32
     *  * 如果是昨天的，显示昨天15:32
     *  * 其余显示，2016-10-15
     *  * 时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007
     *
     */
    fun getFriendlyTimeSpanByNow(
        time: String?, format: DateFormat
    ): String {
        return getFriendlyTimeSpanByNow(string2Millis(time, format))
    }

    /**
     * Return the friendly time span by now.
     *
     * @param date The date.
     * @return the friendly time span by now
     *
     *  * 如果小于 1 秒钟内，显示刚刚
     *  * 如果在 1 分钟内，显示 XXX秒前
     *  * 如果在 1 小时内，显示 XXX分钟前
     *  * 如果在 1 小时外的今天内，显示今天15:32
     *  * 如果是昨天的，显示昨天15:32
     *  * 其余显示，2016-10-15
     *  * 时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007
     *
     */
    fun getFriendlyTimeSpanByNow(date: Date): String {
        return getFriendlyTimeSpanByNow(date.time)
    }

    /**
     * Return the friendly time span by now.
     *
     * @param millis The milliseconds.
     * @return the friendly time span by now
     *
     *  * 如果小于 1 秒钟内，显示刚刚
     *  * 如果在 1 分钟内，显示 XXX秒前
     *  * 如果在 1 小时内，显示 XXX分钟前
     *  * 如果在 1 小时外的今天内，显示今天15:32
     *  * 如果是昨天的，显示昨天15:32
     *  * 其余显示，2016-10-15
     *  * 时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007
     *
     */
    fun getFriendlyTimeSpanByNow(millis: Long): String {
        val now = System.currentTimeMillis()
        val span = now - millis
        // U can read http://www.apihome.cn/api/java/Formatter.html to understand it.
        if (span < 0) {
            return String.format("%tc", millis)
        }
        if (span < 1000) {
            return "刚刚"
        } else if (span < TimeConstants.MIN) {
            return String.format(Locale.getDefault(), "%d秒前", span / TimeConstants.SEC)
        } else if (span < TimeConstants.HOUR) {
            return String.format(Locale.getDefault(), "%d分钟前", span / TimeConstants.MIN)
        }
        // 获取当天 00:00
        val wee = weeOfToday
        return if (millis >= wee) {
            String.format("今天%tR", millis)
        } else if (millis >= wee - TimeConstants.DAY) {
            String.format("昨天%tR", millis)
        } else {
            String.format("%tF", millis)
        }
    }

    private val weeOfToday: Long
        get() {
            val cal = Calendar.getInstance()
            cal[Calendar.HOUR_OF_DAY] = 0
            cal[Calendar.SECOND] = 0
            cal[Calendar.MINUTE] = 0
            cal[Calendar.MILLISECOND] = 0
            return cal.timeInMillis
        }

    /**
     * Return the milliseconds differ time span.
     *
     * @param millis   The milliseconds.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the milliseconds differ time span
     */
    fun getMillis(
        millis: Long, timeSpan: Long, @TimeConstants.Unit unit: Int
    ): Long {
        return millis + timeSpan2Millis(timeSpan, unit)
    }

    /**
     * Return the milliseconds differ time span.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time     The formatted time string.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the milliseconds differ time span
     */
    fun getMillis(
        time: String?, timeSpan: Long, @TimeConstants.Unit unit: Int
    ): Long {
        return getMillis(time, defaultFormat, timeSpan, unit)
    }

    /**
     * Return the milliseconds differ time span.
     *
     * @param time     The formatted time string.
     * @param format   The format.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the milliseconds differ time span.
     */
    fun getMillis(
        time: String?, format: DateFormat, timeSpan: Long, @TimeConstants.Unit unit: Int
    ): Long {
        return string2Millis(time, format) + timeSpan2Millis(timeSpan, unit)
    }

    /**
     * Return the milliseconds differ time span.
     *
     * @param date     The date.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the milliseconds differ time span.
     */
    fun getMillis(
        date: Date, timeSpan: Long, @TimeConstants.Unit unit: Int
    ): Long {
        return date2Millis(date) + timeSpan2Millis(timeSpan, unit)
    }

    /**
     * Return the formatted time string differ time span.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param millis   The milliseconds.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the formatted time string differ time span
     */
    fun getString(
        millis: Long, timeSpan: Long, @TimeConstants.Unit unit: Int
    ): String {
        return getString(millis, defaultFormat, timeSpan, unit)
    }

    /**
     * Return the formatted time string differ time span.
     *
     * @param millis   The milliseconds.
     * @param format   The format.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the formatted time string differ time span
     */
    fun getString(
        millis: Long, format: DateFormat, timeSpan: Long, @TimeConstants.Unit unit: Int
    ): String {
        return millis2String(millis + timeSpan2Millis(timeSpan, unit), format)
    }

    /**
     * Return the formatted time string differ time span.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time     The formatted time string.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the formatted time string differ time span
     */
    fun getString(
        time: String?, timeSpan: Long, @TimeConstants.Unit unit: Int
    ): String {
        return getString(time, defaultFormat, timeSpan, unit)
    }

    /**
     * Return the formatted time string differ time span.
     *
     * @param time     The formatted time string.
     * @param format   The format.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the formatted time string differ time span
     */
    fun getString(
        time: String?, format: DateFormat, timeSpan: Long, @TimeConstants.Unit unit: Int
    ): String {
        return millis2String(string2Millis(time, format) + timeSpan2Millis(timeSpan, unit), format)
    }

    /**
     * Return the formatted time string differ time span.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param date     The date.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the formatted time string differ time span
     */
    fun getString(
        date: Date, timeSpan: Long, @TimeConstants.Unit unit: Int
    ): String {
        return getString(date, defaultFormat, timeSpan, unit)
    }

    /**
     * Return the formatted time string differ time span.
     *
     * @param date     The date.
     * @param format   The format.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the formatted time string differ time span
     */
    fun getString(
        date: Date, format: DateFormat, timeSpan: Long, @TimeConstants.Unit unit: Int
    ): String {
        return millis2String(date2Millis(date) + timeSpan2Millis(timeSpan, unit), format)
    }

    /**
     * Return the date differ time span.
     *
     * @param millis   The milliseconds.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the date differ time span
     */
    fun getDate(
        millis: Long, timeSpan: Long, @TimeConstants.Unit unit: Int
    ): Date {
        return millis2Date(millis + timeSpan2Millis(timeSpan, unit))
    }

    /**
     * Return the date differ time span.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time     The formatted time string.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the date differ time span
     */
    fun getDate(
        time: String?, timeSpan: Long, @TimeConstants.Unit unit: Int
    ): Date {
        return getDate(time, defaultFormat, timeSpan, unit)
    }

    /**
     * Return the date differ time span.
     *
     * @param time     The formatted time string.
     * @param format   The format.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the date differ time span
     */
    fun getDate(
        time: String?, format: DateFormat, timeSpan: Long, @TimeConstants.Unit unit: Int
    ): Date {
        return millis2Date(string2Millis(time, format) + timeSpan2Millis(timeSpan, unit))
    }

    /**
     * Return the date differ time span.
     *
     * @param date     The date.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the date differ time span
     */
    fun getDate(
        date: Date, timeSpan: Long, @TimeConstants.Unit unit: Int
    ): Date {
        return millis2Date(date2Millis(date) + timeSpan2Millis(timeSpan, unit))
    }

    /**
     * Return the milliseconds differ time span by now.
     *
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the milliseconds differ time span by now
     */
    fun getMillisByNow(timeSpan: Long, @TimeConstants.Unit unit: Int): Long {
        return getMillis(nowMills, timeSpan, unit)
    }

    /**
     * Return the formatted time string differ time span by now.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the formatted time string differ time span by now
     */
    fun getStringByNow(timeSpan: Long, @TimeConstants.Unit unit: Int): String {
        return getStringByNow(timeSpan, defaultFormat, unit)
    }

    /**
     * Return the formatted time string differ time span by now.
     *
     * @param timeSpan The time span.
     * @param format   The format.
     * @param unit     The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the formatted time string differ time span by now
     */
    fun getStringByNow(
        timeSpan: Long, format: DateFormat, @TimeConstants.Unit unit: Int
    ): String {
        return getString(nowMills, format, timeSpan, unit)
    }

    /**
     * Return the date differ time span by now.
     *
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return the date differ time span by now
     */
    fun getDateByNow(timeSpan: Long, @TimeConstants.Unit unit: Int): Date {
        return getDate(nowMills, timeSpan, unit)
    }

    /**
     * 返回是否是今天。
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time 格式化的时间字符串。
     * @return `true`: yes<br></br>`false`: no
     */
    fun isToday(time: String?): Boolean {
        return isToday(string2Millis(time, defaultFormat))
    }

    /**
     * 返回是否是今天。
     *
     * @param time   格式化的时间字符串
     * @param format 格式
     * @return `true`: yes<br></br>`false`: no
     */
    fun isToday(time: String?, format: DateFormat): Boolean {
        return isToday(string2Millis(time, format))
    }

    /**
     * 返回是否是今天。
     *
     * @param date date.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isToday(date: Date): Boolean {
        return isToday(date.time)
    }

    /**
     * 返回是否是今天
     *
     * @param millis 毫秒
     * @return `true`: yes<br></br>`false`: no
     */
    fun isToday(millis: Long): Boolean {
        val wee = weeOfToday
        return millis >= wee && millis < wee + TimeConstants.DAY
    }

    /**
     * 返回是否是闰年。
     *
     * 模式是 `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time 格式化的时间字符串。
     * @return `true`: yes<br></br>`false`: no
     */
    fun isLeapYear(time: String?): Boolean {
        return isLeapYear(string2Date(time, defaultFormat))
    }

    /**
     * 返回是否是闰年
     *
     * @param time   格式化的时间字符串
     * @param format 格式
     * @return `true`: yes<br></br>`false`: no
     */
    fun isLeapYear(time: String?, format: DateFormat): Boolean {
        return isLeapYear(string2Date(time, format))
    }

    /**
     * 返回是否是闰年
     *
     * @param date date.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isLeapYear(date: Date?): Boolean {
        val cal = Calendar.getInstance()
        if (date != null) {
            cal.time = date
        }
        val year = cal[Calendar.YEAR]
        return isLeapYear(year)
    }

    /**
     * 返回是否是闰年。
     *
     * @param millis 毫秒
     * @return `true`: yes<br></br>`false`: no
     */
    fun isLeapYear(millis: Long): Boolean {
        return isLeapYear(millis2Date(millis))
    }

    /**
     * 返回是否是闰年。
     *
     * @param year 年
     * @return `true`: yes<br></br>`false`: no
     */
    fun isLeapYear(year: Int): Boolean {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0
    }

    /**
     * 以中文返回星期几。
     *
     * 模式是 `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time 格式化的时间字符串
     * @return 中文返回星期几。
     */
    fun getChineseWeek(time: String?): String {
        return getChineseWeek(string2Date(time, defaultFormat))
    }

    /**
     * 以中文返回星期几。
     *
     * @param time   格式化的时间字符串
     * @param format 格式
     * @return 星期几。
     */
    fun getChineseWeek(time: String?, format: DateFormat): String {
        return getChineseWeek(string2Date(time, format))
    }

    /**
     * 以中文返回星期几。
     *
     * @param date date.
     * @return 星期几。
     */
    fun getChineseWeek(date: Date?): String {
        return date?.let { SimpleDateFormat("E", Locale.CHINA).format(it) } ?: ""
    }

    /**
     * 以中文返回星期几
     *
     * @param millis 毫秒
     * @return 星期几
     */
    fun getChineseWeek(millis: Long): String {
        return getChineseWeek(Date(millis))
    }

    /**
     * 返回美国的星期几。
     *
     * 模式是`yyyy-MM-dd HH:mm:ss`.
     *
     * @param time 格式化的时间字符串。
     * @return 美国的星期几。
     */
    fun getUSWeek(time: String?): String {
        return getUSWeek(string2Date(time, defaultFormat))
    }

    /**
     * 返回美国的星期几。
     *
     * @param time   格式化的时间字符串。
     * @param format 格式
     * @return 美国的星期几。
     */
    fun getUSWeek(time: String?, format: DateFormat): String {
        return getUSWeek(string2Date(time, format))
    }

    /**
     * 返回美国的星期几。
     *
     * @param date date.
     * @return 美国的星期几。
     */
    fun getUSWeek(date: Date?): String {
        return date?.let { SimpleDateFormat("EEEE", Locale.US).format(it) } ?:""
    }

    /**
     * 返回美国的星期几。
     *
     * @param millis 毫秒
     * @return 美国的星期几。
     */
    fun getUSWeek(millis: Long): String {
        return getUSWeek(Date(millis))
    }

    private val isAm: Boolean
        /**
         * 返回是否是 am。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() {
            val cal = Calendar.getInstance()
            return cal[GregorianCalendar.AM_PM] == 0
        }

    /**
     * 返回是否是 am。
     *
     * 模式是 `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time 格式化的时间字符串。
     * @return `true`: yes<br></br>`false`: no
     */
    fun isAm(time: String?): Boolean {
        return getValueByCalendarField(time, defaultFormat, GregorianCalendar.AM_PM) == 0
    }

    /**
     * 返回是否是 am。
     *
     * @param time   格式化的时间字符串。
     * @param format 格式
     * @return `true`: yes<br></br>`false`: no
     */
    fun isAm(
        time: String?, format: DateFormat
    ): Boolean {
        return getValueByCalendarField(time, format, GregorianCalendar.AM_PM) == 0
    }

    /**
     * 返回是否是 am。
     *
     * @param date date.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isAm(date: Date?): Boolean {
        return getValueByCalendarField(date, GregorianCalendar.AM_PM) == 0
    }

    /**
     * 返回是否是 am。
     *
     * @param millis 毫秒
     * @return `true`: yes<br></br>`false`: no
     */
    fun isAm(millis: Long): Boolean {
        return getValueByCalendarField(millis, GregorianCalendar.AM_PM) == 0
    }

    val isPm: Boolean
        /**
         * 返回是否是 pm。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = !isAm

    /**
     * 返回是否是 pm。
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time 格式化的时间字符串
     * @return `true`: yes<br></br>`false`: no
     */
    fun isPm(time: String?): Boolean {
        return !isAm(time)
    }

    /**
     * 返回是否是 pm。
     *
     * @param time   格式化的时间字符串
     * @param format 格式
     * @return `true`: yes<br></br>`false`: no
     */
    fun isPm(
        time: String?, format: DateFormat
    ): Boolean {
        return !isAm(time, format)
    }

    /**
     * 返回是否是 pm。
     *
     * @param date date.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isPm(date: Date?): Boolean {
        return !isAm(date)
    }

    /**
     * 返回是否是 pm。
     *
     * @param millis 毫秒
     * @return `true`: yes<br></br>`false`: no
     */
    fun isPm(millis: Long): Boolean {
        return !isAm(millis)
    }

    /**
     * 返回给定日历字段的值。
     *
     * @param field 给定的日历字段。
     *
     *  * [Calendar.ERA]
     *  * [Calendar.YEAR]
     *  * [Calendar.MONTH]
     *  * ...
     *  * [Calendar.DST_OFFSET]
     *
     * @return 给定日历字段的值
     */
    fun getValueByCalendarField(field: Int): Int {
        val cal = Calendar.getInstance()
        return cal[field]
    }

    /**
     * 返回给定日历字段的值。
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time  格式化的时间字符串。
     * @param field 给定的日历字段。
     *
     *  * [Calendar.ERA]
     *  * [Calendar.YEAR]
     *  * [Calendar.MONTH]
     *  * ...
     *  * [Calendar.DST_OFFSET]
     *
     * @return 给定日历字段的值。
     */
    fun getValueByCalendarField(time: String?, field: Int): Int {
        return getValueByCalendarField(string2Date(time, defaultFormat), field)
    }

    /**
     * 返回给定日历字段的值。
     *
     * @param time   格式化的时间字符串。
     * @param format 格式
     * @param field  给定的日历字段。
     *
     *  * [Calendar.ERA]
     *  * [Calendar.YEAR]
     *  * [Calendar.MONTH]
     *  * ...
     *  * [Calendar.DST_OFFSET]
     *
     * @return 给定日历字段的值。
     */
    fun getValueByCalendarField(
        time: String?, format: DateFormat, field: Int
    ): Int {
        return getValueByCalendarField(string2Date(time, format), field)
    }

    /**
     * 返回给定日历字段的值。
     *
     * @param date  date.
     * @param field 给定的日历字段。
     *
     *  * [Calendar.ERA]
     *  * [Calendar.YEAR]
     *  * [Calendar.MONTH]
     *  * ...
     *  * [Calendar.DST_OFFSET]
     *
     * @return 给定日历字段的值。
     */
    fun getValueByCalendarField(date: Date?, field: Int): Int {
        val cal = Calendar.getInstance()
        if (date != null) {
            cal.time = date
        }
        return cal[field]
    }

    /**
     * 返回给定日历字段的值。
     *
     * @param millis 毫秒
     * @param field  给定的日历字段。
     *
     *  * [Calendar.ERA]
     *  * [Calendar.YEAR]
     *  * [Calendar.MONTH]
     *  * ...
     *  * [Calendar.DST_OFFSET]
     *
     * @return 给定日历字段的值。
     */
    fun getValueByCalendarField(millis: Long, field: Int): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        return cal[field]
    }

    private val CHINESE_ZODIAC =
        arrayOf("猴", "鸡", "狗", "猪", "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊")

    /**
     * 返回中国十二生肖。
     *
     * 模式是 `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time 格式化的时间字符串。
     * @return 中国十二生肖。
     */
    fun getChineseZodiac(time: String?): String {
        return getChineseZodiac(string2Date(time, defaultFormat))
    }

    /**
     * 返回中国十二生肖。
     *
     * @param time   格式化的时间字符串。
     * @param format 格式
     * @return 中国十二生肖。
     */
    fun getChineseZodiac(time: String?, format: DateFormat): String {
        return getChineseZodiac(string2Date(time, format))
    }

    /**
     * 返回中国十二生肖。
     *
     * @param date date.
     * @return 中国十二生肖。
     */
    fun getChineseZodiac(date: Date?): String {
        val cal = Calendar.getInstance()
        if (date != null) {
            cal.time = date
        }
        return CHINESE_ZODIAC[cal[Calendar.YEAR] % 12]
    }

    /**
     * 返回中国十二生肖。
     *
     * @param millis 毫秒
     * @return 中国十二生肖。
     */
    fun getChineseZodiac(millis: Long): String {
        return getChineseZodiac(millis2Date(millis))
    }

    /**
     * 返回中国十二生肖。
     *
     * @param year 年
     * @return 中国十二生肖。
     */
    fun getChineseZodiac(year: Int): String {
        return CHINESE_ZODIAC[year % 12]
    }

    private val ZODIAC_FLAGS = intArrayOf(20, 19, 21, 21, 21, 22, 23, 23, 23, 24, 23, 22)
    private val ZODIAC = arrayOf(
        "水瓶座",
        "双鱼座",
        "白羊座",
        "金牛座",
        "双子座",
        "巨蟹座",
        "狮子座",
        "处女座",
        "天秤座",
        "天蝎座",
        "射手座",
        "摩羯座"
    )

    /**
     * 返回十二星座。
     *
     * 模式是 `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time 格式化的时间字符串。
     * @return 十二星座。
     */
    fun getZodiac(time: String?): String {
        return getZodiac(string2Date(time, defaultFormat))
    }

    /**
     * 返回十二星座。
     *
     * @param time   格式化的时间字符串。
     * @param format 格式
     * @return 十二星座。
     */
    fun getZodiac(time: String?, format: DateFormat): String {
        return getZodiac(string2Date(time, format))
    }

    /**
     * 返回十二星座。
     *
     * @param date date.
     * @return 十二星座。
     */
    fun getZodiac(date: Date?): String {
        val cal = Calendar.getInstance()
        if (date != null) {
            cal.time = date
        }
        val month = cal[Calendar.MONTH] + 1
        val day = cal[Calendar.DAY_OF_MONTH]
        return getZodiac(month, day)
    }

    /**
     * 返回十二星座。
     *
     * @param millis 毫秒
     * @return 十二星座。
     */
    fun getZodiac(millis: Long): String {
        return getZodiac(millis2Date(millis))
    }

    /**
     * 返回十二星座。
     *
     * @param month month.
     * @param day   day.
     * @return 十二星座。
     */
    fun getZodiac(month: Int, day: Int): String {
        return ZODIAC[if (day >= ZODIAC_FLAGS[month - 1]) month - 1 else (month + 10) % 12]
    }

    private fun timeSpan2Millis(timeSpan: Long, @TimeConstants.Unit unit: Int): Long {
        return timeSpan * unit
    }

    private fun millis2TimeSpan(millis: Long, @TimeConstants.Unit unit: Int): Long {
        return millis / unit
    }

    @JvmStatic
    fun millis2FitTimeSpan(milli: Long, precisionInt: Int): String? {
        var millis = milli
        var precision = precisionInt
        if (precision <= 0) {
            return null
        }
        precision = precision.coerceAtMost(5)
        val units = arrayOf("天", "小时", "分钟", "秒", "毫秒")
        if (millis == 0L) {
            return 0.toString() + units[precision - 1]
        }
        val sb = StringBuilder()
        if (millis < 0) {
            sb.append("-")
            millis = -millis
        }
        val unitLen = intArrayOf(86400000, 3600000, 60000, 1000, 1)
        for (i in 0 until precision) {
            if (millis >= unitLen[i]) {
                val mode = millis / unitLen[i]
                millis -= mode * unitLen[i]
                sb.append(mode).append(units[i])
            }
        }
        return sb.toString()
    }
}