package com.lzq.dawn.util.time;

import android.annotation.SuppressLint;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.lzq.dawn.DawnBridge;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @Name :TimeUtils
 * @Time :2022/8/31 15:34
 * @Author :  Lzq
 * @Desc : time
 */
public final class TimeUtils {

    private static final ThreadLocal<Map<String, SimpleDateFormat>> SDF_THREAD_LOCAL
            = new ThreadLocal<Map<String, SimpleDateFormat>>() {
        @Override
        protected Map<String, SimpleDateFormat> initialValue() {
            return new HashMap<>();
        }
    };

    private static SimpleDateFormat getDefaultFormat() {
        return getSafeDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 检查设备是否正在使用网络提供时间。
     * <p>
     * 在您想要验证设备是否设置了正确的时间、
     * 避免欺诈或想要防止用户弄乱时间并滥用您的“一次性”和“过期”功能的情况下很有用。
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isUsingNetworkProvidedTime() {
        return Settings.Global.getInt(DawnBridge.getApp().getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
    }


    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat getSafeDateFormat(String pattern) {
        Map<String, SimpleDateFormat> sdfMap = SDF_THREAD_LOCAL.get();
        //noinspection ConstantConditions
        SimpleDateFormat simpleDateFormat = sdfMap.get(pattern);
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat(pattern);
            sdfMap.put(pattern, simpleDateFormat);
        }
        return simpleDateFormat;
    }

    private TimeUtils() {

    }

    /**
     * 格式化时间字符串的毫秒数。
     * <p>模式是{@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param millis 毫秒。
     * @return 格式化的时间字符串
     */
    public static String millis2String(final long millis) {
        return millis2String(millis, getDefaultFormat());
    }

    /**
     * 格式化时间字符串的毫秒数
     *
     * @param millis  毫秒
     * @param pattern 日期格式的模式，如 yyyy/MM/dd HH:mm
     * @return 格式化的时间字符串
     */
    public static String millis2String(long millis, @NonNull final String pattern) {
        return millis2String(millis, getSafeDateFormat(pattern));
    }

    /**
     * 格式化时间字符串的毫秒数
     *
     * @param millis 毫秒
     * @param format 格式
     * @return 格式化的时间字符串
     */
    public static String millis2String(final long millis, @NonNull final DateFormat format) {
        return format.format(new Date(millis));
    }

    /**
     * 以毫秒为单位的格式化时间字符串。
     * <p>模式是 {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time 格式化的时间字符串。
     * @return 毫秒
     */
    public static long string2Millis(final String time) {
        return string2Millis(time, getDefaultFormat());
    }

    /**
     * 以毫秒为单位的格式化时间字符串。
     *
     * @param time    格式化的时间字符串。
     * @param pattern 日期格式的模式，如 yyyy/MM/dd HH:mm
     * @return 毫秒
     */
    public static long string2Millis(final String time, @NonNull final String pattern) {
        return string2Millis(time, getSafeDateFormat(pattern));
    }

    /**
     * 以毫秒为单位的格式化时间字符串。
     *
     * @param time   格式化的时间字符串。
     * @param format 格式
     * @return 毫秒
     */
    public static long string2Millis(final String time, @NonNull final DateFormat format) {
        try {
            return format.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 格式化的时间字符串到日期。
     * <p>模式是{@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time 格式化的时间字符串。
     * @return the date
     */
    public static Date string2Date(final String time) {
        return string2Date(time, getDefaultFormat());
    }

    /**
     * 格式化的时间字符串到日期。
     *
     * @param time    格式化的时间字符串。
     * @param pattern 日期格式的模式，如 yyyy/MM/dd HH:mm
     * @return the date
     */
    public static Date string2Date(final String time, @NonNull final String pattern) {
        return string2Date(time, getSafeDateFormat(pattern));
    }

    /**
     * 格式化的时间字符串到日期。
     *
     * @param time   格式化的时间字符串。
     * @param format 格式
     * @return date
     */
    public static Date string2Date(final String time, @NonNull final DateFormat format) {
        try {
            return format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 日期到格式化时间字符串。
     * <p>模式是 {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param date date.
     * @return 格式化时间字符串。
     */
    public static String date2String(final Date date) {
        return date2String(date, getDefaultFormat());
    }

    /**
     * 日期到格式化时间字符串
     *
     * @param date    date.
     * @param pattern 日期格式的模式，如 yyyy/MM/dd HH:mm
     * @return 格式化时间字符串。
     */
    public static String date2String(final Date date, @NonNull final String pattern) {
        return getSafeDateFormat(pattern).format(date);
    }

    /**
     * 日期到格式化时间字符串
     *
     * @param date   date.
     * @param format 格式
     * @return 格式化时间字符串。
     */
    public static String date2String(final Date date, @NonNull final DateFormat format) {
        return format.format(date);
    }

    /**
     * 以毫秒为单位的日期
     *
     * @param date date.
     * @return 毫秒
     */
    public static long date2Millis(final Date date) {
        return date.getTime();
    }

    /**
     * 毫秒数到日期
     *
     * @param millis 毫秒
     * @return date
     */
    public static Date millis2Date(final long millis) {
        return new Date(millis);
    }

    /**
     * Return the time span, in unit.
     * <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time1 第一个格式化的时间字符串。
     * @param time2 第二个格式化的时间字符串。
     * @param unit  单位
     *              <ul>
     *              <li>{@link TimeConstants#MSEC}</li>
     *              <li>{@link TimeConstants#SEC }</li>
     *              <li>{@link TimeConstants#MIN }</li>
     *              <li>{@link TimeConstants#HOUR}</li>
     *              <li>{@link TimeConstants#DAY }</li>
     *              </ul>
     * @return the time span, in unit
     */
    public static long getTimeSpan(final String time1,
                                   final String time2,
                                   @TimeConstants.Unit final int unit) {
        return getTimeSpan(time1, time2, getDefaultFormat(), unit);
    }

    /**
     * Return the time span, in unit.
     *
     * @param time1  第一个格式化的时间字符串。
     * @param time2  第二个格式化的时间字符串。
     * @param format 格式
     * @param unit   单位
     *               <ul>
     *               <li>{@link TimeConstants#MSEC}</li>
     *               <li>{@link TimeConstants#SEC }</li>
     *               <li>{@link TimeConstants#MIN }</li>
     *               <li>{@link TimeConstants#HOUR}</li>
     *               <li>{@link TimeConstants#DAY }</li>
     *               </ul>
     * @return the time span, in unit
     */
    public static long getTimeSpan(final String time1,
                                   final String time2,
                                   @NonNull final DateFormat format,
                                   @TimeConstants.Unit final int unit) {
        return millis2TimeSpan(string2Millis(time1, format) - string2Millis(time2, format), unit);
    }

    /**
     * Return the time span, in unit.
     *
     * @param date1 The first date.
     * @param date2 The second date.
     * @param unit  The unit of time span.
     *              <ul>
     *              <li>{@link TimeConstants#MSEC}</li>
     *              <li>{@link TimeConstants#SEC }</li>
     *              <li>{@link TimeConstants#MIN }</li>
     *              <li>{@link TimeConstants#HOUR}</li>
     *              <li>{@link TimeConstants#DAY }</li>
     *              </ul>
     * @return the time span, in unit
     */
    public static long getTimeSpan(final Date date1,
                                   final Date date2,
                                   @TimeConstants.Unit final int unit) {
        return millis2TimeSpan(date2Millis(date1) - date2Millis(date2), unit);
    }

    /**
     * Return the time span, in unit.
     *
     * @param millis1 The first milliseconds.
     * @param millis2 The second milliseconds.
     * @param unit    The unit of time span.
     *                <ul>
     *                <li>{@link TimeConstants#MSEC}</li>
     *                <li>{@link TimeConstants#SEC }</li>
     *                <li>{@link TimeConstants#MIN }</li>
     *                <li>{@link TimeConstants#HOUR}</li>
     *                <li>{@link TimeConstants#DAY }</li>
     *                </ul>
     * @return the time span, in unit
     */
    public static long getTimeSpan(final long millis1,
                                   final long millis2,
                                   @TimeConstants.Unit final int unit) {
        return millis2TimeSpan(millis1 - millis2, unit);
    }

    /**
     * Return the fit time span.
     * <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time1     The first formatted time string.
     * @param time2     The second formatted time string.
     * @param precision The precision of time span.
     *                  <ul>
     *                  <li>precision = 0, return null</li>
     *                  <li>precision = 1, return 天</li>
     *                  <li>precision = 2, return 天, 小时</li>
     *                  <li>precision = 3, return 天, 小时, 分钟</li>
     *                  <li>precision = 4, return 天, 小时, 分钟, 秒</li>
     *                  <li>precision &gt;= 5，return 天, 小时, 分钟, 秒, 毫秒</li>
     *                  </ul>
     * @return the fit time span
     */
    public static String getFitTimeSpan(final String time1,
                                        final String time2,
                                        final int precision) {
        long delta = string2Millis(time1, getDefaultFormat()) - string2Millis(time2, getDefaultFormat());
        return millis2FitTimeSpan(delta, precision);
    }

    /**
     * Return the fit time span.
     *
     * @param time1     The first formatted time string.
     * @param time2     The second formatted time string.
     * @param format    The format.
     * @param precision The precision of time span.
     *                  <ul>
     *                  <li>precision = 0, return null</li>
     *                  <li>precision = 1, return 天</li>
     *                  <li>precision = 2, return 天, 小时</li>
     *                  <li>precision = 3, return 天, 小时, 分钟</li>
     *                  <li>precision = 4, return 天, 小时, 分钟, 秒</li>
     *                  <li>precision &gt;= 5，return 天, 小时, 分钟, 秒, 毫秒</li>
     *                  </ul>
     * @return the fit time span
     */
    public static String getFitTimeSpan(final String time1,
                                        final String time2,
                                        @NonNull final DateFormat format,
                                        final int precision) {
        long delta = string2Millis(time1, format) - string2Millis(time2, format);
        return millis2FitTimeSpan(delta, precision);
    }

    /**
     * Return the fit time span.
     *
     * @param date1     The first date.
     * @param date2     The second date.
     * @param precision The precision of time span.
     *                  <ul>
     *                  <li>precision = 0, return null</li>
     *                  <li>precision = 1, return 天</li>
     *                  <li>precision = 2, return 天, 小时</li>
     *                  <li>precision = 3, return 天, 小时, 分钟</li>
     *                  <li>precision = 4, return 天, 小时, 分钟, 秒</li>
     *                  <li>precision &gt;= 5，return 天, 小时, 分钟, 秒, 毫秒</li>
     *                  </ul>
     * @return the fit time span
     */
    public static String getFitTimeSpan(final Date date1, final Date date2, final int precision) {
        return millis2FitTimeSpan(date2Millis(date1) - date2Millis(date2), precision);
    }

    /**
     * Return the fit time span.
     *
     * @param millis1   The first milliseconds.
     * @param millis2   The second milliseconds.
     * @param precision The precision of time span.
     *                  <ul>
     *                  <li>precision = 0, return null</li>
     *                  <li>precision = 1, return 天</li>
     *                  <li>precision = 2, return 天, 小时</li>
     *                  <li>precision = 3, return 天, 小时, 分钟</li>
     *                  <li>precision = 4, return 天, 小时, 分钟, 秒</li>
     *                  <li>precision &gt;= 5，return 天, 小时, 分钟, 秒, 毫秒</li>
     *                  </ul>
     * @return the fit time span
     */
    public static String getFitTimeSpan(final long millis1,
                                        final long millis2,
                                        final int precision) {
        return millis2FitTimeSpan(millis1 - millis2, precision);
    }

    /**
     * 以毫秒为单位返回当前时间。
     *
     * @return 当前时间（以毫秒为单位）
     */
    public static long getNowMills() {
        return System.currentTimeMillis();
    }

    /**
     * 返回当前格式化的时间字符串。
     * <p>模式是{@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @return 当前格式化的时间字符串。
     */
    public static String getNowString() {
        return millis2String(System.currentTimeMillis(), getDefaultFormat());
    }

    /**
     * 返回当前格式化的时间字符串。
     *
     * @param format 格式
     * @return 当前格式化的时间字符串。
     */
    public static String getNowString(@NonNull final DateFormat format) {
        return millis2String(System.currentTimeMillis(), format);
    }

    /**
     * 返回当前日期。
     *
     * @return 当前日期。
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * Return the time span by now, in unit.
     * <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time The formatted time string.
     * @param unit The unit of time span.
     *             <ul>
     *             <li>{@link TimeConstants#MSEC}</li>
     *             <li>{@link TimeConstants#SEC }</li>
     *             <li>{@link TimeConstants#MIN }</li>
     *             <li>{@link TimeConstants#HOUR}</li>
     *             <li>{@link TimeConstants#DAY }</li>
     *             </ul>
     * @return the time span by now, in unit
     */
    public static long getTimeSpanByNow(final String time, @TimeConstants.Unit final int unit) {
        return getTimeSpan(time, getNowString(), getDefaultFormat(), unit);
    }

    /**
     * Return the time span by now, in unit.
     *
     * @param time   The formatted time string.
     * @param format The format.
     * @param unit   The unit of time span.
     *               <ul>
     *               <li>{@link TimeConstants#MSEC}</li>
     *               <li>{@link TimeConstants#SEC }</li>
     *               <li>{@link TimeConstants#MIN }</li>
     *               <li>{@link TimeConstants#HOUR}</li>
     *               <li>{@link TimeConstants#DAY }</li>
     *               </ul>
     * @return the time span by now, in unit
     */
    public static long getTimeSpanByNow(final String time,
                                        @NonNull final DateFormat format,
                                        @TimeConstants.Unit final int unit) {
        return getTimeSpan(time, getNowString(format), format, unit);
    }

    /**
     * Return the time span by now, in unit.
     *
     * @param date The date.
     * @param unit The unit of time span.
     *             <ul>
     *             <li>{@link TimeConstants#MSEC}</li>
     *             <li>{@link TimeConstants#SEC }</li>
     *             <li>{@link TimeConstants#MIN }</li>
     *             <li>{@link TimeConstants#HOUR}</li>
     *             <li>{@link TimeConstants#DAY }</li>
     *             </ul>
     * @return the time span by now, in unit
     */
    public static long getTimeSpanByNow(final Date date, @TimeConstants.Unit final int unit) {
        return getTimeSpan(date, new Date(), unit);
    }

    /**
     * Return the time span by now, in unit.
     *
     * @param millis The milliseconds.
     * @param unit   The unit of time span.
     *               <ul>
     *               <li>{@link TimeConstants#MSEC}</li>
     *               <li>{@link TimeConstants#SEC }</li>
     *               <li>{@link TimeConstants#MIN }</li>
     *               <li>{@link TimeConstants#HOUR}</li>
     *               <li>{@link TimeConstants#DAY }</li>
     *               </ul>
     * @return the time span by now, in unit
     */
    public static long getTimeSpanByNow(final long millis, @TimeConstants.Unit final int unit) {
        return getTimeSpan(millis, System.currentTimeMillis(), unit);
    }

    /**
     * Return the fit time span by now.
     * <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time      The formatted time string.
     * @param precision The precision of time span.
     *                  <ul>
     *                  <li>precision = 0，返回 null</li>
     *                  <li>precision = 1，返回天</li>
     *                  <li>precision = 2，返回天和小时</li>
     *                  <li>precision = 3，返回天、小时和分钟</li>
     *                  <li>precision = 4，返回天、小时、分钟和秒</li>
     *                  <li>precision &gt;= 5，返回天、小时、分钟、秒和毫秒</li>
     *                  </ul>
     * @return the fit time span by now
     */
    public static String getFitTimeSpanByNow(final String time, final int precision) {
        return getFitTimeSpan(time, getNowString(), getDefaultFormat(), precision);
    }

    /**
     * Return the fit time span by now.
     *
     * @param time      The formatted time string.
     * @param format    The format.
     * @param precision The precision of time span.
     *                  <ul>
     *                  <li>precision = 0，返回 null</li>
     *                  <li>precision = 1，返回天</li>
     *                  <li>precision = 2，返回天和小时</li>
     *                  <li>precision = 3，返回天、小时和分钟</li>
     *                  <li>precision = 4，返回天、小时、分钟和秒</li>
     *                  <li>precision &gt;= 5，返回天、小时、分钟、秒和毫秒</li>
     *                  </ul>
     * @return the fit time span by now
     */
    public static String getFitTimeSpanByNow(final String time,
                                             @NonNull final DateFormat format,
                                             final int precision) {
        return getFitTimeSpan(time, getNowString(format), format, precision);
    }

    /**
     * Return the fit time span by now.
     *
     * @param date      The date.
     * @param precision The precision of time span.
     *                  <ul>
     *                  <li>precision = 0，返回 null</li>
     *                  <li>precision = 1，返回天</li>
     *                  <li>precision = 2，返回天和小时</li>
     *                  <li>precision = 3，返回天、小时和分钟</li>
     *                  <li>precision = 4，返回天、小时、分钟和秒</li>
     *                  <li>precision &gt;= 5，返回天、小时、分钟、秒和毫秒</li>
     *                  </ul>
     * @return the fit time span by now
     */
    public static String getFitTimeSpanByNow(final Date date, final int precision) {
        return getFitTimeSpan(date, getNowDate(), precision);
    }

    /**
     * Return the fit time span by now.
     *
     * @param millis    The milliseconds.
     * @param precision The precision of time span.
     *                  <ul>
     *                  <li>precision = 0，返回 null</li>
     *                  <li>precision = 1，返回天</li>
     *                  <li>precision = 2，返回天和小时</li>
     *                  <li>precision = 3，返回天、小时和分钟</li>
     *                  <li>precision = 4，返回天、小时、分钟和秒</li>
     *                  <li>precision &gt;= 5，返回天、小时、分钟、秒和毫秒</li>
     *                  </ul>
     * @return the fit time span by now
     */
    public static String getFitTimeSpanByNow(final long millis, final int precision) {
        return getFitTimeSpan(millis, System.currentTimeMillis(), precision);
    }

    /**
     * Return the friendly time span by now.
     * <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time The formatted time string.
     * @return the friendly time span by now
     * <ul>
     * <li>如果小于 1 秒钟内，显示刚刚</li>
     * <li>如果在 1 分钟内，显示 XXX秒前</li>
     * <li>如果在 1 小时内，显示 XXX分钟前</li>
     * <li>如果在 1 小时外的今天内，显示今天15:32</li>
     * <li>如果是昨天的，显示昨天15:32</li>
     * <li>其余显示，2016-10-15</li>
     * <li>时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007</li>
     * </ul>
     */
    public static String getFriendlyTimeSpanByNow(final String time) {
        return getFriendlyTimeSpanByNow(time, getDefaultFormat());
    }

    /**
     * Return the friendly time span by now.
     *
     * @param time   The formatted time string.
     * @param format The format.
     * @return the friendly time span by now
     * <ul>
     * <li>如果小于 1 秒钟内，显示刚刚</li>
     * <li>如果在 1 分钟内，显示 XXX秒前</li>
     * <li>如果在 1 小时内，显示 XXX分钟前</li>
     * <li>如果在 1 小时外的今天内，显示今天15:32</li>
     * <li>如果是昨天的，显示昨天15:32</li>
     * <li>其余显示，2016-10-15</li>
     * <li>时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007</li>
     * </ul>
     */
    public static String getFriendlyTimeSpanByNow(final String time,
                                                  @NonNull final DateFormat format) {
        return getFriendlyTimeSpanByNow(string2Millis(time, format));
    }

    /**
     * Return the friendly time span by now.
     *
     * @param date The date.
     * @return the friendly time span by now
     * <ul>
     * <li>如果小于 1 秒钟内，显示刚刚</li>
     * <li>如果在 1 分钟内，显示 XXX秒前</li>
     * <li>如果在 1 小时内，显示 XXX分钟前</li>
     * <li>如果在 1 小时外的今天内，显示今天15:32</li>
     * <li>如果是昨天的，显示昨天15:32</li>
     * <li>其余显示，2016-10-15</li>
     * <li>时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007</li>
     * </ul>
     */
    public static String getFriendlyTimeSpanByNow(final Date date) {
        return getFriendlyTimeSpanByNow(date.getTime());
    }

    /**
     * Return the friendly time span by now.
     *
     * @param millis The milliseconds.
     * @return the friendly time span by now
     * <ul>
     * <li>如果小于 1 秒钟内，显示刚刚</li>
     * <li>如果在 1 分钟内，显示 XXX秒前</li>
     * <li>如果在 1 小时内，显示 XXX分钟前</li>
     * <li>如果在 1 小时外的今天内，显示今天15:32</li>
     * <li>如果是昨天的，显示昨天15:32</li>
     * <li>其余显示，2016-10-15</li>
     * <li>时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007</li>
     * </ul>
     */
    public static String getFriendlyTimeSpanByNow(final long millis) {
        long now = System.currentTimeMillis();
        long span = now - millis;
        // U can read http://www.apihome.cn/api/java/Formatter.html to understand it.
        if (span < 0) {
            return String.format("%tc", millis);
        }
        if (span < 1000) {
            return "刚刚";
        } else if (span < TimeConstants.MIN) {
            return String.format(Locale.getDefault(), "%d秒前", span / TimeConstants.SEC);
        } else if (span < TimeConstants.HOUR) {
            return String.format(Locale.getDefault(), "%d分钟前", span / TimeConstants.MIN);
        }
        // 获取当天 00:00
        long wee = getWeeOfToday();
        if (millis >= wee) {
            return String.format("今天%tR", millis);
        } else if (millis >= wee - TimeConstants.DAY) {
            return String.format("昨天%tR", millis);
        } else {
            return String.format("%tF", millis);
        }
    }

    private static long getWeeOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * Return the milliseconds differ time span.
     *
     * @param millis   The milliseconds.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *                 <ul>
     *                 <li>{@link TimeConstants#MSEC}</li>
     *                 <li>{@link TimeConstants#SEC }</li>
     *                 <li>{@link TimeConstants#MIN }</li>
     *                 <li>{@link TimeConstants#HOUR}</li>
     *                 <li>{@link TimeConstants#DAY }</li>
     *                 </ul>
     * @return the milliseconds differ time span
     */
    public static long getMillis(final long millis,
                                 final long timeSpan,
                                 @TimeConstants.Unit final int unit) {
        return millis + timeSpan2Millis(timeSpan, unit);
    }

    /**
     * Return the milliseconds differ time span.
     * <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time     The formatted time string.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *                 <ul>
     *                 <li>{@link TimeConstants#MSEC}</li>
     *                 <li>{@link TimeConstants#SEC }</li>
     *                 <li>{@link TimeConstants#MIN }</li>
     *                 <li>{@link TimeConstants#HOUR}</li>
     *                 <li>{@link TimeConstants#DAY }</li>
     *                 </ul>
     * @return the milliseconds differ time span
     */
    public static long getMillis(final String time,
                                 final long timeSpan,
                                 @TimeConstants.Unit final int unit) {
        return getMillis(time, getDefaultFormat(), timeSpan, unit);
    }

    /**
     * Return the milliseconds differ time span.
     *
     * @param time     The formatted time string.
     * @param format   The format.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *                 <ul>
     *                 <li>{@link TimeConstants#MSEC}</li>
     *                 <li>{@link TimeConstants#SEC }</li>
     *                 <li>{@link TimeConstants#MIN }</li>
     *                 <li>{@link TimeConstants#HOUR}</li>
     *                 <li>{@link TimeConstants#DAY }</li>
     *                 </ul>
     * @return the milliseconds differ time span.
     */
    public static long getMillis(final String time,
                                 @NonNull final DateFormat format,
                                 final long timeSpan,
                                 @TimeConstants.Unit final int unit) {
        return string2Millis(time, format) + timeSpan2Millis(timeSpan, unit);
    }

    /**
     * Return the milliseconds differ time span.
     *
     * @param date     The date.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *                 <ul>
     *                 <li>{@link TimeConstants#MSEC}</li>
     *                 <li>{@link TimeConstants#SEC }</li>
     *                 <li>{@link TimeConstants#MIN }</li>
     *                 <li>{@link TimeConstants#HOUR}</li>
     *                 <li>{@link TimeConstants#DAY }</li>
     *                 </ul>
     * @return the milliseconds differ time span.
     */
    public static long getMillis(final Date date,
                                 final long timeSpan,
                                 @TimeConstants.Unit final int unit) {
        return date2Millis(date) + timeSpan2Millis(timeSpan, unit);
    }

    /**
     * Return the formatted time string differ time span.
     * <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param millis   The milliseconds.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *                 <ul>
     *                 <li>{@link TimeConstants#MSEC}</li>
     *                 <li>{@link TimeConstants#SEC }</li>
     *                 <li>{@link TimeConstants#MIN }</li>
     *                 <li>{@link TimeConstants#HOUR}</li>
     *                 <li>{@link TimeConstants#DAY }</li>
     *                 </ul>
     * @return the formatted time string differ time span
     */
    public static String getString(final long millis,
                                   final long timeSpan,
                                   @TimeConstants.Unit final int unit) {
        return getString(millis, getDefaultFormat(), timeSpan, unit);
    }

    /**
     * Return the formatted time string differ time span.
     *
     * @param millis   The milliseconds.
     * @param format   The format.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *                 <ul>
     *                 <li>{@link TimeConstants#MSEC}</li>
     *                 <li>{@link TimeConstants#SEC }</li>
     *                 <li>{@link TimeConstants#MIN }</li>
     *                 <li>{@link TimeConstants#HOUR}</li>
     *                 <li>{@link TimeConstants#DAY }</li>
     *                 </ul>
     * @return the formatted time string differ time span
     */
    public static String getString(final long millis,
                                   @NonNull final DateFormat format,
                                   final long timeSpan,
                                   @TimeConstants.Unit final int unit) {
        return millis2String(millis + timeSpan2Millis(timeSpan, unit), format);
    }

    /**
     * Return the formatted time string differ time span.
     * <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time     The formatted time string.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *                 <ul>
     *                 <li>{@link TimeConstants#MSEC}</li>
     *                 <li>{@link TimeConstants#SEC }</li>
     *                 <li>{@link TimeConstants#MIN }</li>
     *                 <li>{@link TimeConstants#HOUR}</li>
     *                 <li>{@link TimeConstants#DAY }</li>
     *                 </ul>
     * @return the formatted time string differ time span
     */
    public static String getString(final String time,
                                   final long timeSpan,
                                   @TimeConstants.Unit final int unit) {
        return getString(time, getDefaultFormat(), timeSpan, unit);
    }

    /**
     * Return the formatted time string differ time span.
     *
     * @param time     The formatted time string.
     * @param format   The format.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *                 <ul>
     *                 <li>{@link TimeConstants#MSEC}</li>
     *                 <li>{@link TimeConstants#SEC }</li>
     *                 <li>{@link TimeConstants#MIN }</li>
     *                 <li>{@link TimeConstants#HOUR}</li>
     *                 <li>{@link TimeConstants#DAY }</li>
     *                 </ul>
     * @return the formatted time string differ time span
     */
    public static String getString(final String time,
                                   @NonNull final DateFormat format,
                                   final long timeSpan,
                                   @TimeConstants.Unit final int unit) {
        return millis2String(string2Millis(time, format) + timeSpan2Millis(timeSpan, unit), format);
    }

    /**
     * Return the formatted time string differ time span.
     * <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param date     The date.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *                 <ul>
     *                 <li>{@link TimeConstants#MSEC}</li>
     *                 <li>{@link TimeConstants#SEC }</li>
     *                 <li>{@link TimeConstants#MIN }</li>
     *                 <li>{@link TimeConstants#HOUR}</li>
     *                 <li>{@link TimeConstants#DAY }</li>
     *                 </ul>
     * @return the formatted time string differ time span
     */
    public static String getString(final Date date,
                                   final long timeSpan,
                                   @TimeConstants.Unit final int unit) {
        return getString(date, getDefaultFormat(), timeSpan, unit);
    }

    /**
     * Return the formatted time string differ time span.
     *
     * @param date     The date.
     * @param format   The format.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *                 <ul>
     *                 <li>{@link TimeConstants#MSEC}</li>
     *                 <li>{@link TimeConstants#SEC }</li>
     *                 <li>{@link TimeConstants#MIN }</li>
     *                 <li>{@link TimeConstants#HOUR}</li>
     *                 <li>{@link TimeConstants#DAY }</li>
     *                 </ul>
     * @return the formatted time string differ time span
     */
    public static String getString(final Date date,
                                   @NonNull final DateFormat format,
                                   final long timeSpan,
                                   @TimeConstants.Unit final int unit) {
        return millis2String(date2Millis(date) + timeSpan2Millis(timeSpan, unit), format);
    }

    /**
     * Return the date differ time span.
     *
     * @param millis   The milliseconds.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *                 <ul>
     *                 <li>{@link TimeConstants#MSEC}</li>
     *                 <li>{@link TimeConstants#SEC }</li>
     *                 <li>{@link TimeConstants#MIN }</li>
     *                 <li>{@link TimeConstants#HOUR}</li>
     *                 <li>{@link TimeConstants#DAY }</li>
     *                 </ul>
     * @return the date differ time span
     */
    public static Date getDate(final long millis,
                               final long timeSpan,
                               @TimeConstants.Unit final int unit) {
        return millis2Date(millis + timeSpan2Millis(timeSpan, unit));
    }

    /**
     * Return the date differ time span.
     * <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time     The formatted time string.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *                 <ul>
     *                 <li>{@link TimeConstants#MSEC}</li>
     *                 <li>{@link TimeConstants#SEC }</li>
     *                 <li>{@link TimeConstants#MIN }</li>
     *                 <li>{@link TimeConstants#HOUR}</li>
     *                 <li>{@link TimeConstants#DAY }</li>
     *                 </ul>
     * @return the date differ time span
     */
    public static Date getDate(final String time,
                               final long timeSpan,
                               @TimeConstants.Unit final int unit) {
        return getDate(time, getDefaultFormat(), timeSpan, unit);
    }

    /**
     * Return the date differ time span.
     *
     * @param time     The formatted time string.
     * @param format   The format.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *                 <ul>
     *                 <li>{@link TimeConstants#MSEC}</li>
     *                 <li>{@link TimeConstants#SEC }</li>
     *                 <li>{@link TimeConstants#MIN }</li>
     *                 <li>{@link TimeConstants#HOUR}</li>
     *                 <li>{@link TimeConstants#DAY }</li>
     *                 </ul>
     * @return the date differ time span
     */
    public static Date getDate(final String time,
                               @NonNull final DateFormat format,
                               final long timeSpan,
                               @TimeConstants.Unit final int unit) {
        return millis2Date(string2Millis(time, format) + timeSpan2Millis(timeSpan, unit));
    }

    /**
     * Return the date differ time span.
     *
     * @param date     The date.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *                 <ul>
     *                 <li>{@link TimeConstants#MSEC}</li>
     *                 <li>{@link TimeConstants#SEC }</li>
     *                 <li>{@link TimeConstants#MIN }</li>
     *                 <li>{@link TimeConstants#HOUR}</li>
     *                 <li>{@link TimeConstants#DAY }</li>
     *                 </ul>
     * @return the date differ time span
     */
    public static Date getDate(final Date date,
                               final long timeSpan,
                               @TimeConstants.Unit final int unit) {
        return millis2Date(date2Millis(date) + timeSpan2Millis(timeSpan, unit));
    }

    /**
     * Return the milliseconds differ time span by now.
     *
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *                 <ul>
     *                 <li>{@link TimeConstants#MSEC}</li>
     *                 <li>{@link TimeConstants#SEC }</li>
     *                 <li>{@link TimeConstants#MIN }</li>
     *                 <li>{@link TimeConstants#HOUR}</li>
     *                 <li>{@link TimeConstants#DAY }</li>
     *                 </ul>
     * @return the milliseconds differ time span by now
     */
    public static long getMillisByNow(final long timeSpan, @TimeConstants.Unit final int unit) {
        return getMillis(getNowMills(), timeSpan, unit);
    }

    /**
     * Return the formatted time string differ time span by now.
     * <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *                 <ul>
     *                 <li>{@link TimeConstants#MSEC}</li>
     *                 <li>{@link TimeConstants#SEC }</li>
     *                 <li>{@link TimeConstants#MIN }</li>
     *                 <li>{@link TimeConstants#HOUR}</li>
     *                 <li>{@link TimeConstants#DAY }</li>
     *                 </ul>
     * @return the formatted time string differ time span by now
     */
    public static String getStringByNow(final long timeSpan, @TimeConstants.Unit final int unit) {
        return getStringByNow(timeSpan, getDefaultFormat(), unit);
    }

    /**
     * Return the formatted time string differ time span by now.
     *
     * @param timeSpan The time span.
     * @param format   The format.
     * @param unit     The unit of time span.
     *                 <ul>
     *                 <li>{@link TimeConstants#MSEC}</li>
     *                 <li>{@link TimeConstants#SEC }</li>
     *                 <li>{@link TimeConstants#MIN }</li>
     *                 <li>{@link TimeConstants#HOUR}</li>
     *                 <li>{@link TimeConstants#DAY }</li>
     *                 </ul>
     * @return the formatted time string differ time span by now
     */
    public static String getStringByNow(final long timeSpan,
                                        @NonNull final DateFormat format,
                                        @TimeConstants.Unit final int unit) {
        return getString(getNowMills(), format, timeSpan, unit);
    }

    /**
     * Return the date differ time span by now.
     *
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *                 <ul>
     *                 <li>{@link TimeConstants#MSEC}</li>
     *                 <li>{@link TimeConstants#SEC }</li>
     *                 <li>{@link TimeConstants#MIN }</li>
     *                 <li>{@link TimeConstants#HOUR}</li>
     *                 <li>{@link TimeConstants#DAY }</li>
     *                 </ul>
     * @return the date differ time span by now
     */
    public static Date getDateByNow(final long timeSpan, @TimeConstants.Unit final int unit) {
        return getDate(getNowMills(), timeSpan, unit);
    }

    /**
     * 返回是否是今天。
     * <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time 格式化的时间字符串。
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isToday(final String time) {
        return isToday(string2Millis(time, getDefaultFormat()));
    }

    /**
     * 返回是否是今天。
     *
     * @param time   格式化的时间字符串
     * @param format 格式
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isToday(final String time, @NonNull final DateFormat format) {
        return isToday(string2Millis(time, format));
    }

    /**
     * 返回是否是今天。
     *
     * @param date date.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isToday(final Date date) {
        return isToday(date.getTime());
    }

    /**
     * 返回是否是今天
     *
     * @param millis 毫秒
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isToday(final long millis) {
        long wee = getWeeOfToday();
        return millis >= wee && millis < wee + TimeConstants.DAY;
    }

    /**
     * 返回是否是闰年。
     * <p>模式是 {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time 格式化的时间字符串。
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isLeapYear(final String time) {
        return isLeapYear(string2Date(time, getDefaultFormat()));
    }

    /**
     * 返回是否是闰年
     *
     * @param time   格式化的时间字符串
     * @param format 格式
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isLeapYear(final String time, @NonNull final DateFormat format) {
        return isLeapYear(string2Date(time, format));
    }

    /**
     * 返回是否是闰年
     *
     * @param date date.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isLeapYear(final Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        return isLeapYear(year);
    }

    /**
     * 返回是否是闰年。
     *
     * @param millis 毫秒
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isLeapYear(final long millis) {
        return isLeapYear(millis2Date(millis));
    }

    /**
     * 返回是否是闰年。
     *
     * @param year 年
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isLeapYear(final int year) {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
    }

    /**
     * 以中文返回星期几。
     * <p>模式是 {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time 格式化的时间字符串
     * @return 中文返回星期几。
     */
    public static String getChineseWeek(final String time) {
        return getChineseWeek(string2Date(time, getDefaultFormat()));
    }

    /**
     * 以中文返回星期几。
     *
     * @param time   格式化的时间字符串
     * @param format 格式
     * @return 星期几。
     */
    public static String getChineseWeek(final String time, @NonNull final DateFormat format) {
        return getChineseWeek(string2Date(time, format));
    }

    /**
     * 以中文返回星期几。
     *
     * @param date date.
     * @return 星期几。
     */
    public static String getChineseWeek(final Date date) {
        return new SimpleDateFormat("E", Locale.CHINA).format(date);
    }

    /**
     * 以中文返回星期几
     *
     * @param millis 毫秒
     * @return 星期几
     */
    public static String getChineseWeek(final long millis) {
        return getChineseWeek(new Date(millis));
    }

    /**
     * 返回美国的星期几。
     * <p>模式是{@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time 格式化的时间字符串。
     * @return 美国的星期几。
     */
    public static String getUSWeek(final String time) {
        return getUSWeek(string2Date(time, getDefaultFormat()));
    }

    /**
     * 返回美国的星期几。
     *
     * @param time   格式化的时间字符串。
     * @param format 格式
     * @return 美国的星期几。
     */
    public static String getUSWeek(final String time, @NonNull final DateFormat format) {
        return getUSWeek(string2Date(time, format));
    }

    /**
     * 返回美国的星期几。
     *
     * @param date date.
     * @return 美国的星期几。
     */
    public static String getUSWeek(final Date date) {
        return new SimpleDateFormat("EEEE", Locale.US).format(date);
    }

    /**
     * 返回美国的星期几。
     *
     * @param millis 毫秒
     * @return 美国的星期几。
     */
    public static String getUSWeek(final long millis) {
        return getUSWeek(new Date(millis));
    }

    /**
     * 返回是否是 am。
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAm() {
        Calendar cal = Calendar.getInstance();
        return cal.get(GregorianCalendar.AM_PM) == 0;
    }

    /**
     * 返回是否是 am。
     * <p>模式是 {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time 格式化的时间字符串。
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAm(final String time) {
        return getValueByCalendarField(time, getDefaultFormat(), GregorianCalendar.AM_PM) == 0;
    }

    /**
     * 返回是否是 am。
     *
     * @param time   格式化的时间字符串。
     * @param format 格式
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAm(final String time,
                               @NonNull final DateFormat format) {
        return getValueByCalendarField(time, format, GregorianCalendar.AM_PM) == 0;
    }

    /**
     * 返回是否是 am。
     *
     * @param date date.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAm(final Date date) {
        return getValueByCalendarField(date, GregorianCalendar.AM_PM) == 0;
    }

    /**
     * 返回是否是 am。
     *
     * @param millis 毫秒
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAm(final long millis) {
        return getValueByCalendarField(millis, GregorianCalendar.AM_PM) == 0;
    }

    /**
     * 返回是否是 pm。
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isPm() {
        return !isAm();
    }

    /**
     * 返回是否是 pm。
     * <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time 格式化的时间字符串
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isPm(final String time) {
        return !isAm(time);
    }

    /**
     * 返回是否是 pm。
     *
     * @param time   格式化的时间字符串
     * @param format 格式
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isPm(final String time,
                               @NonNull final DateFormat format) {
        return !isAm(time, format);
    }

    /**
     * 返回是否是 pm。
     *
     * @param date date.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isPm(final Date date) {
        return !isAm(date);
    }

    /**
     * 返回是否是 pm。
     *
     * @param millis 毫秒
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isPm(final long millis) {
        return !isAm(millis);
    }

    /**
     * 返回给定日历字段的值。
     *
     * @param field 给定的日历字段。
     *              <ul>
     *              <li>{@link Calendar#ERA}</li>
     *              <li>{@link Calendar#YEAR}</li>
     *              <li>{@link Calendar#MONTH}</li>
     *              <li>...</li>
     *              <li>{@link Calendar#DST_OFFSET}</li>
     *              </ul>
     * @return 给定日历字段的值
     */
    public static int getValueByCalendarField(final int field) {
        Calendar cal = Calendar.getInstance();
        return cal.get(field);
    }

    /**
     * 返回给定日历字段的值。
     * <p>The pattern is {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time  格式化的时间字符串。
     * @param field 给定的日历字段。
     *              <ul>
     *              <li>{@link Calendar#ERA}</li>
     *              <li>{@link Calendar#YEAR}</li>
     *              <li>{@link Calendar#MONTH}</li>
     *              <li>...</li>
     *              <li>{@link Calendar#DST_OFFSET}</li>
     *              </ul>
     * @return 给定日历字段的值。
     */
    public static int getValueByCalendarField(final String time, final int field) {
        return getValueByCalendarField(string2Date(time, getDefaultFormat()), field);
    }

    /**
     * 返回给定日历字段的值。
     *
     * @param time   格式化的时间字符串。
     * @param format 格式
     * @param field  给定的日历字段。
     *               <ul>
     *               <li>{@link Calendar#ERA}</li>
     *               <li>{@link Calendar#YEAR}</li>
     *               <li>{@link Calendar#MONTH}</li>
     *               <li>...</li>
     *               <li>{@link Calendar#DST_OFFSET}</li>
     *               </ul>
     * @return 给定日历字段的值。
     */
    public static int getValueByCalendarField(final String time,
                                              @NonNull final DateFormat format,
                                              final int field) {
        return getValueByCalendarField(string2Date(time, format), field);
    }

    /**
     * 返回给定日历字段的值。
     *
     * @param date  date.
     * @param field 给定的日历字段。
     *              <ul>
     *              <li>{@link Calendar#ERA}</li>
     *              <li>{@link Calendar#YEAR}</li>
     *              <li>{@link Calendar#MONTH}</li>
     *              <li>...</li>
     *              <li>{@link Calendar#DST_OFFSET}</li>
     *              </ul>
     * @return 给定日历字段的值。
     */
    public static int getValueByCalendarField(final Date date, final int field) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(field);
    }

    /**
     * 返回给定日历字段的值。
     *
     * @param millis 毫秒
     * @param field  给定的日历字段。
     *               <ul>
     *               <li>{@link Calendar#ERA}</li>
     *               <li>{@link Calendar#YEAR}</li>
     *               <li>{@link Calendar#MONTH}</li>
     *               <li>...</li>
     *               <li>{@link Calendar#DST_OFFSET}</li>
     *               </ul>
     * @return 给定日历字段的值。
     */
    public static int getValueByCalendarField(final long millis, final int field) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return cal.get(field);
    }

    private static final String[] CHINESE_ZODIAC =
            {"猴", "鸡", "狗", "猪", "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊"};

    /**
     * 返回中国十二生肖。
     * <p>模式是 {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time 格式化的时间字符串。
     * @return 中国十二生肖。
     */
    public static String getChineseZodiac(final String time) {
        return getChineseZodiac(string2Date(time, getDefaultFormat()));
    }

    /**
     * 返回中国十二生肖。
     *
     * @param time   格式化的时间字符串。
     * @param format 格式
     * @return 中国十二生肖。
     */
    public static String getChineseZodiac(final String time, @NonNull final DateFormat format) {
        return getChineseZodiac(string2Date(time, format));
    }

    /**
     * 返回中国十二生肖。
     *
     * @param date date.
     * @return 中国十二生肖。
     */
    public static String getChineseZodiac(final Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return CHINESE_ZODIAC[cal.get(Calendar.YEAR) % 12];
    }

    /**
     * 返回中国十二生肖。
     *
     * @param millis 毫秒
     * @return 中国十二生肖。
     */
    public static String getChineseZodiac(final long millis) {
        return getChineseZodiac(millis2Date(millis));
    }

    /**
     * 返回中国十二生肖。
     *
     * @param year 年
     * @return 中国十二生肖。
     */
    public static String getChineseZodiac(final int year) {
        return CHINESE_ZODIAC[year % 12];
    }

    private static final int[] ZODIAC_FLAGS = {20, 19, 21, 21, 21, 22, 23, 23, 23, 24, 23, 22};
    private static final String[] ZODIAC = {
            "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座",
            "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座"
    };

    /**
     * 返回十二星座。
     * <p>模式是 {@code yyyy-MM-dd HH:mm:ss}.</p>
     *
     * @param time 格式化的时间字符串。
     * @return 十二星座。
     */
    public static String getZodiac(final String time) {
        return getZodiac(string2Date(time, getDefaultFormat()));
    }

    /**
     * 返回十二星座。
     *
     * @param time   格式化的时间字符串。
     * @param format 格式
     * @return 十二星座。
     */
    public static String getZodiac(final String time, @NonNull final DateFormat format) {
        return getZodiac(string2Date(time, format));
    }

    /**
     * 返回十二星座。
     *
     * @param date date.
     * @return 十二星座。
     */
    public static String getZodiac(final Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return getZodiac(month, day);
    }

    /**
     * 返回十二星座。
     *
     * @param millis 毫秒
     * @return 十二星座。
     */
    public static String getZodiac(final long millis) {
        return getZodiac(millis2Date(millis));
    }

    /**
     * 返回十二星座。
     *
     * @param month month.
     * @param day   day.
     * @return 十二星座。
     */
    public static String getZodiac(final int month, final int day) {
        return ZODIAC[day >= ZODIAC_FLAGS[month - 1]
                ? month - 1
                : (month + 10) % 12];
    }

    private static long timeSpan2Millis(final long timeSpan, @TimeConstants.Unit final int unit) {
        return timeSpan * unit;
    }

    private static long millis2TimeSpan(final long millis, @TimeConstants.Unit final int unit) {
        return millis / unit;
    }

   public static String millis2FitTimeSpan(long millis, int precision) {
        if (precision <= 0) {
            return null;
        }
        precision = Math.min(precision, 5);
        String[] units = {"天", "小时", "分钟", "秒", "毫秒"};
        if (millis == 0) {
            return 0 + units[precision - 1];
        }
        StringBuilder sb = new StringBuilder();
        if (millis < 0) {
            sb.append("-");
            millis = -millis;
        }
        int[] unitLen = {86400000, 3600000, 60000, 1000, 1};
        for (int i = 0; i < precision; i++) {
            if (millis >= unitLen[i]) {
                long mode = millis / unitLen[i];
                millis -= mode * unitLen[i];
                sb.append(mode).append(units[i]);
            }
        }
        return sb.toString();
    }

}
