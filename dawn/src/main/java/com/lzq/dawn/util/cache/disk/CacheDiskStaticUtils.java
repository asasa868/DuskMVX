package com.lzq.dawn.util.cache.disk;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @Name :CacheDiskStaticUtils
 * @Time :2022/7/18 14:10
 * @Author :  Lzq
 * @Desc : 磁盘缓存实用类
 */
public final class CacheDiskStaticUtils {

    private static CacheDiskUtils sDefaultCacheDiskUtils;

    /**
     * 设置{@link CacheDiskUtils}的默认实例。
     *
     * @param cacheDiskUtils {@link CacheDiskUtils}的默认实例。
     */
    public static void setDefaultCacheDiskUtils(@Nullable final CacheDiskUtils cacheDiskUtils) {
        sDefaultCacheDiskUtils = cacheDiskUtils;
    }

    /**
     * 将 bytes 放入缓存
     *
     * @param key   key.
     * @param value value
     */
    public static void put(@NonNull final String key, @Nullable final byte[] value) {
        put(key, value, getDefaultCacheDiskUtils());
    }

    /**
     * 将 bytes 放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public static void put(@NonNull final String key, @Nullable final byte[] value, final int saveTime) {
        put(key, value, saveTime, getDefaultCacheDiskUtils());
    }

    /**
     * 返回缓存中字节
     *
     * @param key key
     * @return bytes 如果缓存存在，否则为null
     */
    public static byte[] getBytes(@NonNull final String key) {
        return getBytes(key, getDefaultCacheDiskUtils());
    }

    /**
     * 返回缓存中字节
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return bytes 如果缓存存在，否则为defaultValue
     */
    public static byte[] getBytes(@NonNull final String key, @Nullable final byte[] defaultValue) {
        return getBytes(key, defaultValue, getDefaultCacheDiskUtils());
    }

    ///////////////////////////////////////////////////////////////////////////
    // about String
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 string value 放入缓存
     *
     * @param key   key.
     * @param value value
     */
    public static void put(@NonNull final String key, @Nullable final String value) {
        put(key, value, getDefaultCacheDiskUtils());
    }

    /**
     * 将 string value 放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public static void put(@NonNull final String key, @Nullable final String value, final int saveTime) {
        put(key, value, saveTime, getDefaultCacheDiskUtils());
    }

    /**
     * 返回缓存中String
     *
     * @param key key
     * @return the string value如果缓存存在，否则为null
     */
    public static String getString(@NonNull final String key) {
        return getString(key, getDefaultCacheDiskUtils());
    }

    /**
     * 返回缓存中String
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return string value如果缓存存在，否则为defaultValue
     */
    public static String getString(@NonNull final String key, @Nullable final String defaultValue) {
        return getString(key, defaultValue, getDefaultCacheDiskUtils());
    }

    ///////////////////////////////////////////////////////////////////////////
    // about JSONObject
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 JSONObject 放入缓存
     *
     * @param key   key.
     * @param value value
     */
    public static void put(@NonNull final String key, @Nullable final JSONObject value) {
        put(key, value, getDefaultCacheDiskUtils());
    }

    /**
     * 将 JSONObject 放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public static void put(@NonNull final String key,
                           @Nullable final JSONObject value,
                           final int saveTime) {
        put(key, value, saveTime, getDefaultCacheDiskUtils());
    }

    /**
     * 返回缓存中JSONObject
     *
     * @param key key
     * @return the JSONObject如果缓存存在，否则为null
     */
    public static JSONObject getJSONObject(@NonNull final String key) {
        return getJSONObject(key, getDefaultCacheDiskUtils());
    }

    /**
     * 返回缓存中JSONObject
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return JSONObject如果缓存存在，否则为defaultValue
     */
    public static JSONObject getJSONObject(@NonNull final String key, @Nullable final JSONObject defaultValue) {
        return getJSONObject(key, defaultValue, getDefaultCacheDiskUtils());
    }


    ///////////////////////////////////////////////////////////////////////////
    // about JSONArray
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 JSONArray 放入缓存
     *
     * @param key   key.
     * @param value value
     */
    public static void put(@NonNull final String key, @Nullable final JSONArray value) {
        put(key, value, getDefaultCacheDiskUtils());
    }

    /**
     * 将 JSONArray 放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public static void put(@NonNull final String key, @Nullable final JSONArray value, final int saveTime) {
        put(key, value, saveTime, getDefaultCacheDiskUtils());
    }

    /**
     * 返回缓存中JSONArray
     *
     * @param key key
     * @return the JSONArray如果缓存存在，否则为null
     */
    public static JSONArray getJSONArray(@NonNull final String key) {
        return getJSONArray(key, getDefaultCacheDiskUtils());
    }

    /**
     * 返回缓存中JSONArray
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return JSONArray如果缓存存在，否则为defaultValue
     */
    public static JSONArray getJSONArray(@NonNull final String key, @Nullable final JSONArray defaultValue) {
        return getJSONArray(key, defaultValue, getDefaultCacheDiskUtils());
    }


    ///////////////////////////////////////////////////////////////////////////
    // about Bitmap
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 bitmap 放入缓存
     *
     * @param key   key.
     * @param value value
     */
    public static void put(@NonNull final String key, @Nullable final Bitmap value) {
        put(key, value, getDefaultCacheDiskUtils());
    }

    /**
     * 将 bitmap 放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public static void put(@NonNull final String key, @Nullable final Bitmap value, final int saveTime) {
        put(key, value, saveTime, getDefaultCacheDiskUtils());
    }

    /**
     * 返回缓存中 bitmap
     *
     * @param key key
     * @return the bitmap如果缓存存在，否则为null
     */
    public static Bitmap getBitmap(@NonNull final String key) {
        return getBitmap(key, getDefaultCacheDiskUtils());
    }

    /**
     * 返回缓存中 bitmap
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return bitmap如果缓存存在，否则为defaultValue
     */
    public static Bitmap getBitmap(@NonNull final String key, @Nullable final Bitmap defaultValue) {
        return getBitmap(key, defaultValue, getDefaultCacheDiskUtils());
    }

    ///////////////////////////////////////////////////////////////////////////
    // about Drawable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 drawable 放入缓存
     *
     * @param key   key.
     * @param value value
     */
    public static void put(@NonNull final String key, @Nullable final Drawable value) {
        put(key, value, getDefaultCacheDiskUtils());
    }

    /**
     * 将 drawable 放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public static void put(@NonNull final String key, @Nullable final Drawable value, final int saveTime) {
        put(key, value, saveTime, getDefaultCacheDiskUtils());
    }

    /**
     * 返回缓存中 drawable
     *
     * @param key key
     * @return the drawable如果缓存存在，否则为null
     */
    public static Drawable getDrawable(@NonNull final String key) {
        return getDrawable(key, getDefaultCacheDiskUtils());
    }

    /**
     * 返回缓存中 drawable
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return the drawable如果缓存存在，否则为defaultValue
     */
    public static Drawable getDrawable(@NonNull final String key, final @Nullable Drawable defaultValue) {
        return getDrawable(key, defaultValue, getDefaultCacheDiskUtils());
    }

    ///////////////////////////////////////////////////////////////////////////
    // about Parcelable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 parcelable 放入缓存
     *
     * @param key   key.
     * @param value value
     */
    public static void put(@NonNull final String key, @Nullable final Parcelable value) {
        put(key, value, getDefaultCacheDiskUtils());
    }

    /**
     * 将 parcelable 放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public static void put(@NonNull final String key, @Nullable final Parcelable value, final int saveTime) {
        put(key, value, saveTime, getDefaultCacheDiskUtils());
    }

    /**
     * 返回缓存中 parcelable
     *
     * @param key     key
     * @param creator The creator.
     * @param <T>     值类型
     * @return the parcelable如果缓存存在，否则为null
     */
    public static <T> T getParcelable(@NonNull final String key,
                                      @NonNull final Parcelable.Creator<T> creator) {
        return getParcelable(key, creator, getDefaultCacheDiskUtils());
    }

    /**
     * 返回缓存中 parcelable
     *
     * @param key          key
     * @param creator      The creator.
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @param <T>          值类型
     * @return the parcelable如果缓存存在，否则为defaultValue
     */
    public static <T> T getParcelable(@NonNull final String key,
                                      @NonNull final Parcelable.Creator<T> creator,
                                      @Nullable final T defaultValue) {
        return getParcelable(key, creator, defaultValue, getDefaultCacheDiskUtils());
    }

    ///////////////////////////////////////////////////////////////////////////
    // about Serializable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 serializable 放入缓存
     *
     * @param key   key.
     * @param value value
     */
    public static void put(@NonNull final String key, @Nullable final Serializable value) {
        put(key, value, getDefaultCacheDiskUtils());
    }

    /**
     * 将 serializable 放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public static void put(@NonNull final String key, @Nullable final Serializable value, final int saveTime) {
        put(key, value, saveTime, getDefaultCacheDiskUtils());
    }

    /**
     * 返回缓存中 serializable
     *
     * @param key key
     * @return the bitmap如果缓存存在，否则为null
     */
    public static Object getSerializable(@NonNull final String key) {
        return getSerializable(key, getDefaultCacheDiskUtils());
    }

    /**
     * 返回缓存中 serializable
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return the bitmap如果缓存存在，否则为defaultValue
     */
    public static Object getSerializable(@NonNull final String key, @Nullable final Object defaultValue) {
        return getSerializable(key, defaultValue, getDefaultCacheDiskUtils());
    }

    /**
     * 返回缓存的大小，以字节为单位。
     *
     * @return 返回缓存的大小，以字节为单位。
     */
    public static long getCacheSize() {
        return getCacheSize(getDefaultCacheDiskUtils());
    }

    /**
     * 返回缓存数量。
     *
     * @return 返回缓存数量。
     */
    public static int getCacheCount() {
        return getCacheCount(getDefaultCacheDiskUtils());
    }

    /**
     * 按key删除缓存。
     *
     * @param key key
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean remove(@NonNull final String key) {
        return remove(key, getDefaultCacheDiskUtils());
    }

    /**
     * 清除所有缓存
     *
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean clear() {
        return clear(getDefaultCacheDiskUtils());
    }

    ///////////////////////////////////////////////////////////////////////////
    // dividing line
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 bytes 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           @Nullable final byte[] value,
                           @NonNull final CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value);
    }

    /**
     * 将 bytes 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param saveTime       缓存的保存时间，以秒为单位。
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           @Nullable final byte[] value,
                           final int saveTime,
                           @NonNull final CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value, saveTime);
    }

    /**
     * Return the bytes
     *
     * @param key            key
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     * @return the bytes如果缓存存在，否则为null
     */
    public static byte[] getBytes(@NonNull final String key, @NonNull final CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getBytes(key);
    }

    /**
     * Return the bytes
     *
     * @param key            key
     * @param defaultValue   如果缓存不存在，则为默认值。
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     * @return the bytes如果缓存存在，否则为defaultValue
     */
    public static byte[] getBytes(@NonNull final String key,
                                  @Nullable final byte[] defaultValue,
                                  @NonNull final CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getBytes(key, defaultValue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // about String
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 string value 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           @Nullable final String value,
                           @NonNull final CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value);
    }

    /**
     * 将 string value 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param saveTime       缓存的保存时间，以秒为单位。
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           @Nullable final String value,
                           final int saveTime,
                           @NonNull final CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value, saveTime);
    }

    /**
     * Return the string value
     *
     * @param key            key
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     * @return the string value如果缓存存在，否则为null
     */
    public static String getString(@NonNull final String key, @NonNull final CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getString(key);
    }

    /**
     * Return the string value
     *
     * @param key            key
     * @param defaultValue   如果缓存不存在，则为默认值。
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     * @return the string value如果缓存存在，否则为defaultValue
     */
    public static String getString(@NonNull final String key,
                                   @Nullable final String defaultValue,
                                   @NonNull final CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getString(key, defaultValue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // about JSONObject
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 JSONObject 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           @Nullable final JSONObject value,
                           @NonNull final CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value);
    }

    /**
     * 将 JSONObject 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param saveTime       缓存的保存时间，以秒为单位。
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           @Nullable final JSONObject value,
                           final int saveTime,
                           @NonNull final CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value, saveTime);
    }

    /**
     * Return the JSONObject
     *
     * @param key            key
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     * @return the JSONObject如果缓存存在，否则为null
     */
    public static JSONObject getJSONObject(@NonNull final String key, @NonNull final CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getJSONObject(key);
    }

    /**
     * Return the JSONObject
     *
     * @param key            key
     * @param defaultValue   如果缓存不存在，则为默认值。
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     * @return the JSONObject如果缓存存在，否则为defaultValue
     */
    public static JSONObject getJSONObject(@NonNull final String key,
                                           @Nullable final JSONObject defaultValue,
                                           @NonNull final CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getJSONObject(key, defaultValue);
    }


    ///////////////////////////////////////////////////////////////////////////
    // about JSONArray
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 JSONArray 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           @Nullable final JSONArray value,
                           @NonNull final CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value);
    }

    /**
     * 将 JSONArray 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param saveTime       缓存的保存时间，以秒为单位。
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           @Nullable final JSONArray value,
                           final int saveTime,
                           @NonNull final CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value, saveTime);
    }

    /**
     * Return the JSONArray
     *
     * @param key            key
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     * @return the JSONArray如果缓存存在，否则为null
     */
    public static JSONArray getJSONArray(@NonNull final String key, @NonNull final CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getJSONArray(key);
    }

    /**
     * Return the JSONArray
     *
     * @param key            key
     * @param defaultValue   如果缓存不存在，则为默认值。
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     * @return the JSONArray如果缓存存在，否则为defaultValue
     */
    public static JSONArray getJSONArray(@NonNull final String key,
                                         @Nullable final JSONArray defaultValue,
                                         @NonNull final CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getJSONArray(key, defaultValue);
    }


    ///////////////////////////////////////////////////////////////////////////
    // about Bitmap
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 bitmap 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           @Nullable final Bitmap value,
                           @NonNull final CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value);
    }

    /**
     * 将 bitmap 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param saveTime       缓存的保存时间，以秒为单位。
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           @Nullable final Bitmap value,
                           final int saveTime,
                           @NonNull final CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value, saveTime);
    }

    /**
     * Return the bitmap
     *
     * @param key            key
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     * @return the bitmap如果缓存存在，否则为null
     */
    public static Bitmap getBitmap(@NonNull final String key, @NonNull final CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getBitmap(key);
    }

    /**
     * Return the bitmap
     *
     * @param key            key
     * @param defaultValue   如果缓存不存在，则为默认值。
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     * @return the bitmap如果缓存存在，否则为defaultValue
     */
    public static Bitmap getBitmap(@NonNull final String key,
                                   @Nullable final Bitmap defaultValue,
                                   @NonNull final CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getBitmap(key, defaultValue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // about Drawable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 drawable 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           @Nullable final Drawable value,
                           @NonNull final CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value);
    }

    /**
     * 将 drawable 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param saveTime       缓存的保存时间，以秒为单位。
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           @Nullable final Drawable value,
                           final int saveTime,
                           @NonNull final CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value, saveTime);
    }

    /**
     * Return the drawable
     *
     * @param key            key
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     * @return the drawable如果缓存存在，否则为null
     */
    public static Drawable getDrawable(@NonNull final String key, @NonNull final CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getDrawable(key);
    }

    /**
     * Return the drawable
     *
     * @param key            key
     * @param defaultValue   如果缓存不存在，则为默认值。
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     * @return the drawable如果缓存存在，否则为defaultValue
     */
    public static Drawable getDrawable(@NonNull final String key,
                                       @Nullable final Drawable defaultValue,
                                       @NonNull final CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getDrawable(key, defaultValue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // about Parcelable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 parcelable 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           @Nullable final Parcelable value,
                           @NonNull final CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value);
    }

    /**
     * 将 parcelable 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param saveTime       缓存的保存时间，以秒为单位。
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           @Nullable final Parcelable value,
                           final int saveTime,
                           @NonNull final CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value, saveTime);
    }

    /**
     * Return the parcelable
     *
     * @param key            key
     * @param creator        The creator.
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     * @param <T>            The value type.
     * @return the parcelable如果缓存存在，否则为null
     */
    public static <T> T getParcelable(@NonNull final String key,
                                      @NonNull final Parcelable.Creator<T> creator,
                                      @NonNull final CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getParcelable(key, creator);
    }

    /**
     * Return the parcelable
     *
     * @param key            key
     * @param creator        The creator.
     * @param defaultValue   如果缓存不存在，则为默认值。
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     * @param <T>            The value type.
     * @return the parcelable如果缓存存在，否则为defaultValue
     */
    public static <T> T getParcelable(@NonNull final String key,
                                      @NonNull final Parcelable.Creator<T> creator,
                                      @Nullable final T defaultValue,
                                      @NonNull final CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getParcelable(key, creator, defaultValue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // about Serializable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 serializable 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           @Nullable final Serializable value,
                           @NonNull final CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value);
    }

    /**
     * 将 serializable 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param saveTime       缓存的保存时间，以秒为单位。
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           @Nullable final Serializable value,
                           final int saveTime,
                           @NonNull final CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value, saveTime);
    }

    /**
     * Return the serializable
     *
     * @param key            key
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     * @return the Serializable 如果缓存存在，否则为null
     */
    public static Object getSerializable(@NonNull final String key, @NonNull final CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getSerializable(key);
    }

    /**
     * Return the serializable
     *
     * @param key            key
     * @param defaultValue   如果缓存不存在，则为默认值。
     * @param cacheDiskUtils {@link CacheDiskUtils}的实例。
     * @return the Serializable 如果缓存存在，否则为defaultValue
     */
    public static Object getSerializable(@NonNull final String key,
                                         @Nullable final Object defaultValue,
                                         @NonNull final CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getSerializable(key, defaultValue);
    }

    /**
     * 返回缓存的大小，以字节为单位。
     *
     * @param cacheDiskUtils {@link CacheDiskUtils}.的实例。
     * @return 返回缓存的大小，以字节为单位。
     */
    public static long getCacheSize(@NonNull final CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getCacheSize();
    }

    /**
     * 返回缓存数量。
     *
     * @param cacheDiskUtils {@link CacheDiskUtils}. 的实例。
     * @return 返回缓存数量。
     */
    public static int getCacheCount(@NonNull final CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getCacheCount();
    }

    /**
     * 按key删除缓存。
     *
     * @param key            key
     * @param cacheDiskUtils {@link CacheDiskUtils}.的实例。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean remove(@NonNull final String key, @NonNull final CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.remove(key);
    }

    /**
     * 清除所有缓存
     *
     * @param cacheDiskUtils {@link CacheDiskUtils}.的实例。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean clear(@NonNull final CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.clear();
    }

    @NonNull
    private static CacheDiskUtils getDefaultCacheDiskUtils() {
        return sDefaultCacheDiskUtils != null ? sDefaultCacheDiskUtils : CacheDiskUtils.getInstance();
    }
}