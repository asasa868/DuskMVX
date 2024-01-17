package com.lzq.dawn.util.cache.disk

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Parcelable
import com.lzq.dawn.util.cache.disk.CacheDiskUtils.Companion.instance
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable

/**
 * @Name :CacheDiskStaticUtils
 * @Time :2022/7/18 14:10
 * @Author :  Lzq
 * @Desc : 磁盘缓存实用类
 */
object CacheDiskStaticUtils {
    private var sDefaultCacheDiskUtils: CacheDiskUtils? = null

    /**
     * 返回缓存中字节
     *
     * @param key key
     * @return bytes 如果缓存存在，否则为null
     */
    fun getBytes(key: String): ByteArray? {
        return getBytes(key, defaultCacheDiskUtils!!)
    }

    /**
     * 返回缓存中字节
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return bytes 如果缓存存在，否则为defaultValue
     */
    fun getBytes(key: String, defaultValue: ByteArray?): ByteArray? {
        return getBytes(key, defaultValue, defaultCacheDiskUtils!!)
    }

    /**
     * 返回缓存中String
     *
     * @param key key
     * @return the string value如果缓存存在，否则为null
     */
    fun getString(key: String): String? {
        return getString(key, defaultCacheDiskUtils!!)
    }

    /**
     * 返回缓存中String
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return string value如果缓存存在，否则为defaultValue
     */
    fun getString(key: String, defaultValue: String?): String? {
        return getString(key, defaultValue, defaultCacheDiskUtils!!)
    }

    /**
     * 返回缓存中JSONObject
     *
     * @param key key
     * @return the JSONObject如果缓存存在，否则为null
     */
    fun getJSONObject(key: String): JSONObject? {
        return getJSONObject(key, defaultCacheDiskUtils!!)
    }

    /**
     * 返回缓存中JSONObject
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return JSONObject如果缓存存在，否则为defaultValue
     */
    fun getJSONObject(key: String, defaultValue: JSONObject?): JSONObject? {
        return getJSONObject(key, defaultValue, defaultCacheDiskUtils!!)
    }

    /**
     * 返回缓存中JSONArray
     *
     * @param key key
     * @return the JSONArray如果缓存存在，否则为null
     */
    fun getJSONArray(key: String): JSONArray? {
        return getJSONArray(key, defaultCacheDiskUtils!!)
    }

    /**
     * 返回缓存中JSONArray
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return JSONArray如果缓存存在，否则为defaultValue
     */
    fun getJSONArray(key: String, defaultValue: JSONArray?): JSONArray? {
        return getJSONArray(key, defaultValue, defaultCacheDiskUtils!!)
    }

    /**
     * 返回缓存中 bitmap
     *
     * @param key key
     * @return the bitmap如果缓存存在，否则为null
     */
    fun getBitmap(key: String): Bitmap? {
        return getBitmap(key, defaultCacheDiskUtils!!)
    }

    /**
     * 返回缓存中 bitmap
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return bitmap如果缓存存在，否则为defaultValue
     */
    fun getBitmap(key: String, defaultValue: Bitmap?): Bitmap? {
        return getBitmap(key, defaultValue, defaultCacheDiskUtils!!)
    }

    /**
     * 返回缓存中 drawable
     *
     * @param key key
     * @return the drawable如果缓存存在，否则为null
     */
    fun getDrawable(key: String): Drawable? {
        return getDrawable(key, defaultCacheDiskUtils!!)
    }

    /**
     * 返回缓存中 drawable
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return the drawable如果缓存存在，否则为defaultValue
     */
    fun getDrawable(key: String, defaultValue: Drawable?): Drawable? {
        return getDrawable(key, defaultValue, defaultCacheDiskUtils!!)
    }



    /**
     * 返回缓存中 parcelable
     *
     * @param key          key
     * @param creator      The creator.
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @param <T>          值类型
     * @return the parcelable如果缓存存在，否则为defaultValue
    </T> */
    fun <T> getParcelable(
        key: String, creator: Parcelable.Creator<T?>, defaultValue: T?
    ): T? {
        return getParcelable<T?>(key, creator, defaultValue, defaultCacheDiskUtils!!)
    }

    /**
     * 返回缓存中 serializable
     *
     * @param key key
     * @return the bitmap如果缓存存在，否则为null
     */
    fun getSerializable(key: String): Any? {
        return getSerializable(key, defaultCacheDiskUtils)
    }

    /**
     * 返回缓存中 serializable
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return the bitmap如果缓存存在，否则为defaultValue
     */
    fun getSerializable(key: String, defaultValue: Any?): Any? {
        return getSerializable(key, defaultValue, defaultCacheDiskUtils!!)
    }

    val cacheSize: Long
        /**
         * 返回缓存的大小，以字节为单位。
         *
         * @return 返回缓存的大小，以字节为单位。
         */
        get() = getCacheSize(defaultCacheDiskUtils!!)
    val cacheCount: Int
        /**
         * 返回缓存数量。
         *
         * @return 返回缓存数量。
         */
        get() = getCacheCount(defaultCacheDiskUtils!!)
    ///////////////////////////////////////////////////////////////////////////
    // dividing line
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 将 bytes 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String, value: ByteArray?, cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils!!
    ) {
        cacheDiskUtils.put(key, value)
    }
    /**
     * 将 bytes 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param saveTime       缓存的保存时间，以秒为单位。
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String,
        value: ByteArray?,
        saveTime: Int,
        cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils!!
    ) {
        cacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the bytes
     *
     * @param key            key
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     * @return the bytes如果缓存存在，否则为null
     */
    fun getBytes(key: String, cacheDiskUtils: CacheDiskUtils): ByteArray? {
        return cacheDiskUtils.getBytes(key)
    }

    /**
     * Return the bytes
     *
     * @param key            key
     * @param defaultValue   如果缓存不存在，则为默认值。
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     * @return the bytes如果缓存存在，否则为defaultValue
     */
    fun getBytes(
        key: String, defaultValue: ByteArray?, cacheDiskUtils: CacheDiskUtils
    ): ByteArray? {
        return cacheDiskUtils.getBytes(key, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about String
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 将 string value 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String, value: String?, cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils!!
    ) {
        cacheDiskUtils.put(key, value)
    }
    /**
     * 将 string value 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param saveTime       缓存的保存时间，以秒为单位。
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String, value: String?, saveTime: Int, cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils!!
    ) {
        cacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the string value
     *
     * @param key            key
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     * @return the string value如果缓存存在，否则为null
     */
    fun getString(key: String, cacheDiskUtils: CacheDiskUtils): String? {
        return cacheDiskUtils.getString(key)
    }

    /**
     * Return the string value
     *
     * @param key            key
     * @param defaultValue   如果缓存不存在，则为默认值。
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     * @return the string value如果缓存存在，否则为defaultValue
     */
    fun getString(
        key: String, defaultValue: String?, cacheDiskUtils: CacheDiskUtils
    ): String? {
        return cacheDiskUtils.getString(key, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about JSONObject
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 将 JSONObject 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String, value: JSONObject?, cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils!!
    ) {
        cacheDiskUtils.put(key, value)
    }
    /**
     * 将 JSONObject 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param saveTime       缓存的保存时间，以秒为单位。
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String,
        value: JSONObject?,
        saveTime: Int,
        cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils!!
    ) {
        cacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the JSONObject
     *
     * @param key            key
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     * @return the JSONObject如果缓存存在，否则为null
     */
    fun getJSONObject(key: String, cacheDiskUtils: CacheDiskUtils): JSONObject? {
        return cacheDiskUtils.getJSONObject(key)
    }

    /**
     * Return the JSONObject
     *
     * @param key            key
     * @param defaultValue   如果缓存不存在，则为默认值。
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     * @return the JSONObject如果缓存存在，否则为defaultValue
     */
    fun getJSONObject(
        key: String, defaultValue: JSONObject?, cacheDiskUtils: CacheDiskUtils
    ): JSONObject? {
        return cacheDiskUtils.getJSONObject(key, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about JSONArray
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 将 JSONArray 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String, value: JSONArray?, cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils!!
    ) {
        cacheDiskUtils.put(key, value)
    }
    /**
     * 将 JSONArray 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param saveTime       缓存的保存时间，以秒为单位。
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String,
        value: JSONArray?,
        saveTime: Int,
        cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils!!
    ) {
        cacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the JSONArray
     *
     * @param key            key
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     * @return the JSONArray如果缓存存在，否则为null
     */
    fun getJSONArray(key: String, cacheDiskUtils: CacheDiskUtils): JSONArray? {
        return cacheDiskUtils.getJSONArray(key)
    }

    /**
     * Return the JSONArray
     *
     * @param key            key
     * @param defaultValue   如果缓存不存在，则为默认值。
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     * @return the JSONArray如果缓存存在，否则为defaultValue
     */
    fun getJSONArray(
        key: String, defaultValue: JSONArray?, cacheDiskUtils: CacheDiskUtils
    ): JSONArray? {
        return cacheDiskUtils.getJSONArray(key, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about Bitmap
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 将 bitmap 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String, value: Bitmap?, cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils!!
    ) {
        cacheDiskUtils.put(key, value)
    }
    /**
     * 将 bitmap 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param saveTime       缓存的保存时间，以秒为单位。
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String, value: Bitmap?, saveTime: Int, cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils!!
    ) {
        cacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the bitmap
     *
     * @param key            key
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     * @return the bitmap如果缓存存在，否则为null
     */
    fun getBitmap(key: String, cacheDiskUtils: CacheDiskUtils): Bitmap? {
        return cacheDiskUtils.getBitmap(key)
    }

    /**
     * Return the bitmap
     *
     * @param key            key
     * @param defaultValue   如果缓存不存在，则为默认值。
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     * @return the bitmap如果缓存存在，否则为defaultValue
     */
    fun getBitmap(
        key: String, defaultValue: Bitmap?, cacheDiskUtils: CacheDiskUtils
    ): Bitmap? {
        return cacheDiskUtils.getBitmap(key, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about Drawable
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 将 drawable 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String, value: Drawable?, cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils!!
    ) {
        cacheDiskUtils.put(key, value)
    }
    /**
     * 将 drawable 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param saveTime       缓存的保存时间，以秒为单位。
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String, value: Drawable?, saveTime: Int, cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils!!
    ) {
        cacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the drawable
     *
     * @param key            key
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     * @return the drawable如果缓存存在，否则为null
     */
    fun getDrawable(key: String, cacheDiskUtils: CacheDiskUtils): Drawable? {
        return cacheDiskUtils.getDrawable(key)
    }

    /**
     * Return the drawable
     *
     * @param key            key
     * @param defaultValue   如果缓存不存在，则为默认值。
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     * @return the drawable如果缓存存在，否则为defaultValue
     */
    fun getDrawable(
        key: String, defaultValue: Drawable?, cacheDiskUtils: CacheDiskUtils
    ): Drawable? {
        return cacheDiskUtils.getDrawable(key, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about Parcelable
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 将 parcelable 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String, value: Parcelable?, cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils!!
    ) {
        cacheDiskUtils.put(key, value)
    }
    /**
     * 将 parcelable 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param saveTime       缓存的保存时间，以秒为单位。
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String,
        value: Parcelable?,
        saveTime: Int,
        cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils!!
    ) {
        cacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the parcelable
     *
     * @param key            key
     * @param creator        The creator.
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     * @param <T>            The value type.
     * @return the parcelable如果缓存存在，否则为null
    </T> */
    fun <T> getParcelable(
        key: String, creator: Parcelable.Creator<T?>, cacheDiskUtils: CacheDiskUtils
    ): T? {
        return cacheDiskUtils.getParcelable<T>(key, creator)
    }

    /**
     * Return the parcelable
     *
     * @param key            key
     * @param creator        The creator.
     * @param defaultValue   如果缓存不存在，则为默认值。
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     * @param <T>            The value type.
     * @return the parcelable如果缓存存在，否则为defaultValue
    </T> */
    fun <T> getParcelable(
        key: String, creator: Parcelable.Creator<T?>, defaultValue: T?, cacheDiskUtils: CacheDiskUtils
    ): T? {
        return cacheDiskUtils.getParcelable(key, creator, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about Serializable
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 将 serializable 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String, value: Serializable?, cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils!!
    ) {
        cacheDiskUtils.put(key, value)
    }
    /**
     * 将 serializable 放入缓存
     *
     * @param key            key
     * @param value          value
     * @param saveTime       缓存的保存时间，以秒为单位。
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String,
        value: Serializable?,
        saveTime: Int,
        cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils!!
    ) {
        cacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the serializable
     *
     * @param key            key
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     * @return the Serializable 如果缓存存在，否则为null
     */
    fun getSerializable(key: String, cacheDiskUtils: CacheDiskUtils): Any? {
        return cacheDiskUtils.getSerializable(key)
    }

    /**
     * Return the serializable
     *
     * @param key            key
     * @param defaultValue   如果缓存不存在，则为默认值。
     * @param cacheDiskUtils [CacheDiskUtils]的实例。
     * @return the Serializable 如果缓存存在，否则为defaultValue
     */
    fun getSerializable(
        key: String, defaultValue: Any?, cacheDiskUtils: CacheDiskUtils
    ): Any? {
        return cacheDiskUtils.getSerializable(key, defaultValue)
    }

    /**
     * 返回缓存的大小，以字节为单位。
     *
     * @param cacheDiskUtils [CacheDiskUtils].的实例。
     * @return 返回缓存的大小，以字节为单位。
     */
    fun getCacheSize(cacheDiskUtils: CacheDiskUtils): Long {
        return cacheDiskUtils.cacheSize
    }

    /**
     * 返回缓存数量。
     *
     * @param cacheDiskUtils [CacheDiskUtils]. 的实例。
     * @return 返回缓存数量。
     */
    fun getCacheCount(cacheDiskUtils: CacheDiskUtils): Int {
        return cacheDiskUtils.cacheCount
    }
    /**
     * 按key删除缓存。
     *
     * @param key            key
     * @param cacheDiskUtils [CacheDiskUtils].的实例。
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmOverloads
    fun remove(key: String, cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils!!): Boolean {
        return cacheDiskUtils.remove(key)
    }
    /**
     * 清除所有缓存
     *
     * @param cacheDiskUtils [CacheDiskUtils].的实例。
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmOverloads
    fun clear(cacheDiskUtils: CacheDiskUtils = defaultCacheDiskUtils!!): Boolean {
        return cacheDiskUtils.clear()
    }

    private var defaultCacheDiskUtils: CacheDiskUtils?
        get() = if (sDefaultCacheDiskUtils != null) sDefaultCacheDiskUtils else instance
        /**
         * 设置[CacheDiskUtils]的默认实例。
         *
         * @param cacheDiskUtils [CacheDiskUtils]的默认实例。
         */
        set(cacheDiskUtils) {
            sDefaultCacheDiskUtils = cacheDiskUtils
        }
}