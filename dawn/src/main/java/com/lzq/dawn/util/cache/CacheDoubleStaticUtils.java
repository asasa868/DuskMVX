package com.lzq.dawn.util.cache;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @Name :CacheDoubleStaticUtils
 * @Time :2022/7/18 16:01
 * @Author :  Lzq
 * @Desc : 双缓存
 */
public final class CacheDoubleStaticUtils {

    private static CacheDoubleUtils sDefaultCacheDoubleUtils;

    /**
     * 设置{@link CacheDoubleUtils}的默认实例。
     *
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的默认实例。
     */
    public static void setDefaultCacheDoubleUtils(final CacheDoubleUtils cacheDoubleUtils) {
        sDefaultCacheDoubleUtils = cacheDoubleUtils;
    }

    /**
     * 将 bytes   放入缓存
     *
     * @param key   key
     * @param value value
     */
    public static void put(@NonNull final String key, final byte[] value) {
        put(key, value, getDefaultCacheDoubleUtils());
    }

    /**
     * 将 bytes   放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public static void put(@NonNull final String key, byte[] value, final int saveTime) {
        put(key, value, saveTime, getDefaultCacheDoubleUtils());
    }

    /**
     * Return the bytes
     *
     * @param key key
     * @return 如果缓存存在，则为 bytes ，否则为默认值
     */
    public static byte[] getBytes(@NonNull final String key) {
        return getBytes(key, getDefaultCacheDoubleUtils());
    }

    /**
     * Return the bytes
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为 bytes ，否则为默认值
     */
    public static byte[] getBytes(@NonNull final String key, final byte[] defaultValue) {
        return getBytes(key, defaultValue, getDefaultCacheDoubleUtils());
    }

    ///////////////////////////////////////////////////////////////////////////
    // about String
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 string value   放入缓存
     *
     * @param key   key
     * @param value value
     */
    public static void put(@NonNull final String key, final String value) {
        put(key, value, getDefaultCacheDoubleUtils());
    }

    /**
     * 将 string value   放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public static void put(@NonNull final String key, final String value, final int saveTime) {
        put(key, value, saveTime, getDefaultCacheDoubleUtils());
    }

    /**
     * Return the string value
     *
     * @param key key
     * @return 如果缓存存在，则为 string ，否则为默认值
     */
    public static String getString(@NonNull final String key) {
        return getString(key, getDefaultCacheDoubleUtils());
    }

    /**
     * Return the string value
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为 string ，否则为默认值
     */
    public static String getString(@NonNull final String key, final String defaultValue) {
        return getString(key, defaultValue, getDefaultCacheDoubleUtils());
    }

    ///////////////////////////////////////////////////////////////////////////
    // about JSONObject
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 JSONObject   放入缓存
     *
     * @param key   key
     * @param value value
     */
    public static void put(@NonNull final String key, final JSONObject value) {
        put(key, value, getDefaultCacheDoubleUtils());
    }

    /**
     * 将 JSONObject   放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public static void put(@NonNull final String key,
                           final JSONObject value,
                           final int saveTime) {
        put(key, value, saveTime, getDefaultCacheDoubleUtils());
    }

    /**
     * Return the JSONObject
     *
     * @param key key
     * @return 如果缓存存在，则为 JSONObject ，否则为默认值
     */
    public static JSONObject getJSONObject(@NonNull final String key) {
        return getJSONObject(key, getDefaultCacheDoubleUtils());
    }

    /**
     * Return the JSONObject
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为 JSONObject ，否则为默认值
     */
    public static JSONObject getJSONObject(@NonNull final String key, final JSONObject defaultValue) {
        return getJSONObject(key, defaultValue, getDefaultCacheDoubleUtils());
    }


    ///////////////////////////////////////////////////////////////////////////
    // about JSONArray
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 JSONArray   放入缓存
     *
     * @param key   key
     * @param value value
     */
    public static void put(@NonNull final String key, final JSONArray value) {
        put(key, value, getDefaultCacheDoubleUtils());
    }

    /**
     * 将 JSONArray   放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public static void put(@NonNull final String key, final JSONArray value, final int saveTime) {
        put(key, value, saveTime, getDefaultCacheDoubleUtils());
    }

    /**
     * Return the JSONArray
     *
     * @param key key
     * @return 如果缓存存在，则为 JSONArray ，否则为默认值
     */
    public static JSONArray getJSONArray(@NonNull final String key) {
        return getJSONArray(key, getDefaultCacheDoubleUtils());
    }

    /**
     * Return the JSONArray
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为 JSONArray ，否则为默认值
     */
    public static JSONArray getJSONArray(@NonNull final String key, final JSONArray defaultValue) {
        return getJSONArray(key, defaultValue, getDefaultCacheDoubleUtils());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Bitmap cache
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 bitmap   放入缓存
     *
     * @param key   key
     * @param value value
     */
    public static void put(@NonNull final String key, final Bitmap value) {
        put(key, value, getDefaultCacheDoubleUtils());
    }

    /**
     * 将 bitmap   放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public static void put(@NonNull final String key, final Bitmap value, final int saveTime) {
        put(key, value, saveTime, getDefaultCacheDoubleUtils());
    }

    /**
     * Return the bitmap
     *
     * @param key key
     * @return 如果缓存存在，则为 bitmap ，否则为默认值
     */
    public static Bitmap getBitmap(@NonNull final String key) {
        return getBitmap(key, getDefaultCacheDoubleUtils());
    }

    /**
     * Return the bitmap
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为 bitmap ，否则为默认值
     */
    public static Bitmap getBitmap(@NonNull final String key, final Bitmap defaultValue) {
        return getBitmap(key, defaultValue, getDefaultCacheDoubleUtils());
    }

    ///////////////////////////////////////////////////////////////////////////
    // about Drawable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 drawable   放入缓存
     *
     * @param key   key
     * @param value value
     */
    public static void put(@NonNull final String key, final Drawable value) {
        put(key, value, getDefaultCacheDoubleUtils());
    }

    /**
     * 将 drawable   放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public static void put(@NonNull final String key, final Drawable value, final int saveTime) {
        put(key, value, saveTime, getDefaultCacheDoubleUtils());
    }

    /**
     * Return the drawable
     *
     * @param key key
     * @return 如果缓存存在，则为 drawable ，否则为默认值
     */
    public static Drawable getDrawable(@NonNull final String key) {
        return getDrawable(key, getDefaultCacheDoubleUtils());
    }

    /**
     * Return the drawable
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为 drawable ，否则为默认值
     */
    public static Drawable getDrawable(@NonNull final String key, final Drawable defaultValue) {
        return getDrawable(key, defaultValue, getDefaultCacheDoubleUtils());
    }

    ///////////////////////////////////////////////////////////////////////////
    // about Parcelable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 parcelable   放入缓存
     *
     * @param key   key
     * @param value value
     */
    public static void put(@NonNull final String key, final Parcelable value) {
        put(key, value, getDefaultCacheDoubleUtils());
    }

    /**
     * 将 parcelable   放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public static void put(@NonNull final String key, final Parcelable value, final int saveTime) {
        put(key, value, saveTime, getDefaultCacheDoubleUtils());
    }

    /**
     * Return the parcelable
     *
     * @param key     key
     * @param creator The creator.
     * @param <T>     值类型
     * @return 如果缓存存在，则为 parcelable ，否则为默认值
     */
    public static <T> T getParcelable(@NonNull final String key,
                                      @NonNull final Parcelable.Creator<T> creator) {
        return getParcelable(key, creator, getDefaultCacheDoubleUtils());
    }

    /**
     * Return the parcelable
     *
     * @param key          key
     * @param creator      The creator.
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @param <T>          值类型
     * @return 如果缓存存在，则为 parcelable ，否则为默认值
     */
    public static <T> T getParcelable(@NonNull final String key,
                                      @NonNull final Parcelable.Creator<T> creator,
                                      final T defaultValue) {
        return getParcelable(key, creator, defaultValue, getDefaultCacheDoubleUtils());
    }

    ///////////////////////////////////////////////////////////////////////////
    // about Serializable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 serializable   放入缓存
     *
     * @param key   key
     * @param value value
     */
    public static void put(@NonNull final String key, final Serializable value) {
        put(key, value, getDefaultCacheDoubleUtils());
    }

    /**
     * 将 serializable   放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public static void put(@NonNull final String key, final Serializable value, final int saveTime) {
        put(key, value, saveTime, getDefaultCacheDoubleUtils());
    }

    /**
     * Return the serializable
     *
     * @param key key
     * @return 如果缓存存在，则为 serializable ，否则为默认值
     */
    public static Object getSerializable(@NonNull final String key) {
        return getSerializable(key, getDefaultCacheDoubleUtils());
    }

    /**
     * Return the serializable
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为 serializable ，否则为默认值
     */
    public static Object getSerializable(@NonNull final String key, final Object defaultValue) {
        return getSerializable(key, defaultValue, getDefaultCacheDoubleUtils());
    }

    /**
     * 返回磁盘中缓存的大小.
     *
     * @return 返回磁盘中缓存的大小
     */
    public static long getCacheDiskSize() {
        return getCacheDiskSize(getDefaultCacheDoubleUtils());
    }

    /**
     * 返回磁盘中缓存的数量.
     *
     * @return 返回磁盘中缓存的数量
     */
    public static int getCacheDiskCount() {
        return getCacheDiskCount(getDefaultCacheDoubleUtils());
    }

    /**
     * 返回内存中缓存的数量
     *
     * @return 返回内存中缓存的数量
     */
    public static int getCacheMemoryCount() {
        return getCacheMemoryCount(getDefaultCacheDoubleUtils());
    }

    /**
     * 按key删除缓存。
     *
     * @param key key
     */
    public static void remove(@NonNull String key) {
        remove(key, getDefaultCacheDoubleUtils());
    }

    /**
     * 清空缓存
     */
    public static void clear() {
        clear(getDefaultCacheDoubleUtils());
    }

    ///////////////////////////////////////////////////////////////////////////
    // dividing line
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 bytes   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           final byte[] value,
                           @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value);
    }

    /**
     * 将 bytes   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param saveTime         缓存的保存时间，以秒为单位。
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           final byte[] value,
                           final int saveTime,
                           @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value, saveTime);
    }

    /**
     * Return the bytes
     *
     * @param key              key
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     * @return 如果缓存存在，则为 bytes ，否则为默认值
     */
    public static byte[] getBytes(@NonNull final String key, @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getBytes(key);
    }

    /**
     * Return the bytes
     *
     * @param key              key
     * @param defaultValue    如果缓存不存在，则为默认值。
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     * @return 如果缓存存在，则为 bytes ，否则为默认值
     */
    public static byte[] getBytes(@NonNull final String key,
                                  final byte[] defaultValue,
                                  @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getBytes(key, defaultValue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // about String
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 string value   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           final String value,
                           @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value);
    }

    /**
     * 将 string value   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param saveTime         缓存的保存时间，以秒为单位。
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           final String value,
                           final int saveTime,
                           @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value, saveTime);
    }

    /**
     * Return the string value
     *
     * @param key              key
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     * @return 如果缓存存在，则为 string ，否则为默认值
     */
    public static String getString(@NonNull final String key, @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getString(key);
    }

    /**
     * Return the string value
     *
     * @param key              key
     * @param defaultValue    如果缓存不存在，则为默认值。
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     * @return 如果缓存存在，则为 string ，否则为默认值
     */
    public static String getString(@NonNull final String key,
                                   final String defaultValue,
                                   @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getString(key, defaultValue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // about JSONObject
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 JSONObject   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           final JSONObject value,
                           @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value);
    }

    /**
     * 将 JSONObject   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param saveTime         缓存的保存时间，以秒为单位。
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           final JSONObject value,
                           final int saveTime,
                           @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value, saveTime);
    }

    /**
     * Return the JSONObject
     *
     * @param key              key
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     * @return  如果缓存存在，则为 JSONObject ，否则为默认值
     */
    public static JSONObject getJSONObject(@NonNull final String key,
                                           @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getJSONObject(key);
    }

    /**
     * Return the JSONObject
     *
     * @param key              key
     * @param defaultValue    如果缓存不存在，则为默认值。
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     * @return  如果缓存存在，则为 JSONObject ，否则为默认值
     */
    public static JSONObject getJSONObject(@NonNull final String key,
                                           final JSONObject defaultValue,
                                           @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getJSONObject(key, defaultValue);
    }


    ///////////////////////////////////////////////////////////////////////////
    // about JSONArray
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 JSONArray   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           final JSONArray value,
                           @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value);
    }

    /**
     * 将 JSONArray   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param saveTime         缓存的保存时间，以秒为单位。
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           final JSONArray value,
                           final int saveTime,
                           @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value, saveTime);
    }

    /**
     * Return the JSONArray
     *
     * @param key              key
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     * @return 如果缓存存在，则为 JSONArray ，否则为默认值
     */
    public static JSONArray getJSONArray(@NonNull final String key, @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getJSONArray(key);
    }

    /**
     * Return the JSONArray
     *
     * @param key              key
     * @param defaultValue    如果缓存不存在，则为默认值。
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     * @return 如果缓存存在，则为 JSONArray ，否则为默认值
     */
    public static JSONArray getJSONArray(@NonNull final String key,
                                         final JSONArray defaultValue,
                                         @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getJSONArray(key, defaultValue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Bitmap cache
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 bitmap   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           final Bitmap value,
                           @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value);
    }

    /**
     * 将 bitmap   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param saveTime         缓存的保存时间，以秒为单位。
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           final Bitmap value,
                           final int saveTime,
                           @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value, saveTime);
    }

    /**
     * Return the bitmap
     *
     * @param key              key
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     * @return 如果缓存存在，则为 bitmap ，否则为默认值
     */
    public static Bitmap getBitmap(@NonNull final String key, @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getBitmap(key);
    }

    /**
     * Return the bitmap
     *
     * @param key              key
     * @param defaultValue    如果缓存不存在，则为默认值。
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     * @return 如果缓存存在，则为 bitmap ，否则为默认值
     */
    public static Bitmap getBitmap(@NonNull final String key,
                                   final Bitmap defaultValue,
                                   @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getBitmap(key, defaultValue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // about Drawable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 drawable   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           final Drawable value,
                           @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value);
    }

    /**
     * 将 drawable   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param saveTime         缓存的保存时间，以秒为单位。
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           final Drawable value,
                           final int saveTime,
                           @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value, saveTime);
    }

    /**
     * Return the drawable
     *
     * @param key              key
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     * @return 如果缓存存在，则为 drawable ，否则为默认值
     */
    public static Drawable getDrawable(@NonNull final String key, @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getDrawable(key);
    }

    /**
     * Return the drawable
     *
     * @param key              key
     * @param defaultValue    如果缓存不存在，则为默认值。
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     * @return 如果缓存存在，则为 drawable ，否则为默认值
     */
    public static Drawable getDrawable(@NonNull final String key,
                                       final Drawable defaultValue,
                                       @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getDrawable(key, defaultValue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // about Parcelable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 parcelable   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           final Parcelable value,
                           @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value);
    }

    /**
     * 将 parcelable   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param saveTime         缓存的保存时间，以秒为单位。
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           final Parcelable value,
                           final int saveTime,
                           @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value, saveTime);
    }

    /**
     * Return the parcelable
     *
     * @param key              key
     * @param creator          The creator.
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     * @param <T>              The value type.
     * @return 如果缓存存在，则为 parcelable ，否则为默认值
     */
    public static <T> T getParcelable(@NonNull final String key,
                                      @NonNull final Parcelable.Creator<T> creator,
                                      @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getParcelable(key, creator);
    }

    /**
     * Return the parcelable
     *
     * @param key              key
     * @param creator          The creator.
     * @param defaultValue    如果缓存不存在，则为默认值。
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     * @param <T>              The value type.
     * @return 如果缓存存在，则为 parcelable ，否则为默认值
     */
    public static <T> T getParcelable(@NonNull final String key,
                                      @NonNull final Parcelable.Creator<T> creator,
                                      final T defaultValue,
                                      @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getParcelable(key, creator, defaultValue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // about Serializable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 serializable   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           final Serializable value,
                           @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value);
    }

    /**
     * 将 serializable   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param saveTime         缓存的保存时间，以秒为单位。
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           final Serializable value,
                           final int saveTime,
                           @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value, saveTime);
    }

    /**
     * Return the serializable
     *
     * @param key              key
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     * @return 如果缓存存在，则为 serializable ，否则为默认值
     */
    public static Object getSerializable(@NonNull final String key, @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getSerializable(key);
    }

    /**
     * Return the serializable
     *
     * @param key              key
     * @param defaultValue    如果缓存不存在，则为默认值。
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     * @return 如果缓存存在，则为 serializable ，否则为默认值
     */
    public static Object getSerializable(@NonNull final String key,
                                         final Object defaultValue,
                                         @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getSerializable(key, defaultValue);
    }

    /**
     * 返回磁盘中缓存的大小.
     *
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     * @return 返回磁盘中缓存的大小
     */
    public static long getCacheDiskSize(@NonNull final CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getCacheDiskSize();
    }

    /**
     * 返回磁盘中缓存的数量.
     *
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     * @return 返回磁盘中缓存的数量
     */
    public static int getCacheDiskCount(@NonNull final CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getCacheDiskCount();
    }

    /**
     * 返回内存中缓存的数量
     *
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     * @return 返回内存中缓存的数量
     */
    public static int getCacheMemoryCount(@NonNull final CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getCacheMemoryCount();
    }

    /**
     * 按key删除缓存。
     *
     * @param key              key
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     */
    public static void remove(@NonNull String key, @NonNull final CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.remove(key);
    }

    /**
     * 清空缓存
     *
     * @param cacheDoubleUtils {@link CacheDoubleUtils}的实例。
     */
    public static void clear(@NonNull final CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.clear();
    }

    private static CacheDoubleUtils getDefaultCacheDoubleUtils() {
        return sDefaultCacheDoubleUtils != null ? sDefaultCacheDoubleUtils : CacheDoubleUtils.getInstance();
    }
}
