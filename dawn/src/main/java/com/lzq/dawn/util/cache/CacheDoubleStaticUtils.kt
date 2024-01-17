package com.lzq.dawn.util.cache

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Parcelable
import com.lzq.dawn.util.cache.CacheDoubleUtils.Companion.instance
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable

/**
 * @Name :CacheDoubleStaticUtils
 * @Time :2022/7/18 16:01
 * @Author :  Lzq
 * @Desc : 双缓存
 */
object CacheDoubleStaticUtils {
    private var sDefaultCacheDoubleUtils: CacheDoubleUtils? = null

    /**
     * Return the bytes
     *
     * @param key key
     * @return 如果缓存存在，则为 bytes ，否则为默认值
     */
    fun getBytes(key: String): ByteArray? {
        return getBytes(key, defaultCacheDoubleUtils!!)
    }

    /**
     * Return the bytes
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为 bytes ，否则为默认值
     */
    fun getBytes(key: String, defaultValue: ByteArray?): ByteArray? {
        return getBytes(key, defaultValue, defaultCacheDoubleUtils!!)
    }

    /**
     * Return the string value
     *
     * @param key key
     * @return 如果缓存存在，则为 string ，否则为默认值
     */
    fun getString(key: String): String? {
        return getString(key, defaultCacheDoubleUtils!!)
    }

    /**
     * Return the string value
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为 string ，否则为默认值
     */
    fun getString(key: String, defaultValue: String?): String? {
        return getString(key, defaultValue, defaultCacheDoubleUtils!!)
    }

    /**
     * Return the JSONObject
     *
     * @param key key
     * @return 如果缓存存在，则为 JSONObject ，否则为默认值
     */
    fun getJSONObject(key: String): JSONObject? {
        return getJSONObject(key, defaultCacheDoubleUtils!!)
    }

    /**
     * Return the JSONObject
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为 JSONObject ，否则为默认值
     */
    fun getJSONObject(key: String, defaultValue: JSONObject?): JSONObject? {
        return getJSONObject(key, defaultValue, defaultCacheDoubleUtils!!)
    }

    /**
     * Return the JSONArray
     *
     * @param key key
     * @return 如果缓存存在，则为 JSONArray ，否则为默认值
     */
    fun getJSONArray(key: String): JSONArray? {
        return getJSONArray(key, defaultCacheDoubleUtils!!)
    }

    /**
     * Return the JSONArray
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为 JSONArray ，否则为默认值
     */
    fun getJSONArray(key: String, defaultValue: JSONArray?): JSONArray? {
        return getJSONArray(key, defaultValue, defaultCacheDoubleUtils!!)
    }

    /**
     * Return the bitmap
     *
     * @param key key
     * @return 如果缓存存在，则为 bitmap ，否则为默认值
     */
    fun getBitmap(key: String): Bitmap? {
        return getBitmap(key, defaultCacheDoubleUtils!!)
    }

    /**
     * Return the bitmap
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为 bitmap ，否则为默认值
     */
    fun getBitmap(key: String, defaultValue: Bitmap?): Bitmap? {
        return getBitmap(key, defaultValue, defaultCacheDoubleUtils!!)
    }

    /**
     * Return the drawable
     *
     * @param key key
     * @return 如果缓存存在，则为 drawable ，否则为默认值
     */
    fun getDrawable(key: String): Drawable? {
        return getDrawable(key, defaultCacheDoubleUtils!!)
    }

    /**
     * Return the drawable
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为 drawable ，否则为默认值
     */
    fun getDrawable(key: String, defaultValue: Drawable?): Drawable? {
        return getDrawable(key, defaultValue, defaultCacheDoubleUtils!!)
    }


    /**
     * Return the parcelable
     *
     * @param key          key
     * @param creator      The creator.
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @param <T>          值类型
     * @return 如果缓存存在，则为 parcelable ，否则为默认值
    </T> */
    fun <T> getParcelable(key: String, creator: Parcelable.Creator<T?>, defaultValue: T): T {
        return getParcelable(key, creator, defaultValue, defaultCacheDoubleUtils!!)
    }

    /**
     * Return the serializable
     *
     * @param key key
     * @return 如果缓存存在，则为 serializable ，否则为默认值
     */
    fun getSerializable(key: String): Any? {
        return getSerializable(key, defaultCacheDoubleUtils)
    }

    /**
     * Return the serializable
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为 serializable ，否则为默认值
     */
    fun getSerializable(key: String, defaultValue: Any?): Any? {
        return getSerializable(key, defaultValue, defaultCacheDoubleUtils!!)
    }

    val cacheDiskSize: Long
        /**
         * 返回磁盘中缓存的大小.
         *
         * @return 返回磁盘中缓存的大小
         */
        get() = getCacheDiskSize(defaultCacheDoubleUtils!!)
    val cacheDiskCount: Int
        /**
         * 返回磁盘中缓存的数量.
         *
         * @return 返回磁盘中缓存的数量
         */
        get() = getCacheDiskCount(defaultCacheDoubleUtils!!)
    val cacheMemoryCount: Int
        /**
         * 返回内存中缓存的数量
         *
         * @return 返回内存中缓存的数量
         */
        get() = getCacheMemoryCount(defaultCacheDoubleUtils!!)
    ///////////////////////////////////////////////////////////////////////////
    // dividing line
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 将 bytes   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String, value: ByteArray?, cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils!!
    ) {
        cacheDoubleUtils.put(key, value)
    }
    /**
     * 将 bytes   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param saveTime         缓存的保存时间，以秒为单位。
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String,
        value: ByteArray?,
        saveTime: Int,
        cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils!!
    ) {
        cacheDoubleUtils.put(key, value, saveTime)
    }

    /**
     * Return the bytes
     *
     * @param key              key
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     * @return 如果缓存存在，则为 bytes ，否则为默认值
     */
    fun getBytes(key: String, cacheDoubleUtils: CacheDoubleUtils): ByteArray? {
        return cacheDoubleUtils.getBytes(key)
    }

    /**
     * Return the bytes
     *
     * @param key              key
     * @param defaultValue    如果缓存不存在，则为默认值。
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     * @return 如果缓存存在，则为 bytes ，否则为默认值
     */
    fun getBytes(
        key: String, defaultValue: ByteArray?, cacheDoubleUtils: CacheDoubleUtils
    ): ByteArray? {
        return cacheDoubleUtils.getBytes(key, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about String
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 将 string value   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String, value: String?, cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils!!
    ) {
        cacheDoubleUtils.put(key, value)
    }
    /**
     * 将 string value   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param saveTime         缓存的保存时间，以秒为单位。
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String,
        value: String?,
        saveTime: Int,
        cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils!!
    ) {
        cacheDoubleUtils.put(key, value, saveTime)
    }

    /**
     * Return the string value
     *
     * @param key              key
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     * @return 如果缓存存在，则为 string ，否则为默认值
     */
    fun getString(key: String, cacheDoubleUtils: CacheDoubleUtils): String? {
        return cacheDoubleUtils.getString(key)
    }

    /**
     * Return the string value
     *
     * @param key              key
     * @param defaultValue    如果缓存不存在，则为默认值。
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     * @return 如果缓存存在，则为 string ，否则为默认值
     */
    fun getString(
        key: String, defaultValue: String?, cacheDoubleUtils: CacheDoubleUtils
    ): String? {
        return cacheDoubleUtils.getString(key, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about JSONObject
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 将 JSONObject   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String, value: JSONObject?, cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils!!
    ) {
        cacheDoubleUtils.put(key, value)
    }
    /**
     * 将 JSONObject   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param saveTime         缓存的保存时间，以秒为单位。
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String,
        value: JSONObject?,
        saveTime: Int,
        cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils!!
    ) {
        cacheDoubleUtils.put(key, value, saveTime)
    }

    /**
     * Return the JSONObject
     *
     * @param key              key
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     * @return  如果缓存存在，则为 JSONObject ，否则为默认值
     */
    fun getJSONObject(
        key: String, cacheDoubleUtils: CacheDoubleUtils
    ): JSONObject? {
        return cacheDoubleUtils.getJSONObject(key)
    }

    /**
     * Return the JSONObject
     *
     * @param key              key
     * @param defaultValue    如果缓存不存在，则为默认值。
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     * @return  如果缓存存在，则为 JSONObject ，否则为默认值
     */
    fun getJSONObject(
        key: String, defaultValue: JSONObject?, cacheDoubleUtils: CacheDoubleUtils
    ): JSONObject? {
        return cacheDoubleUtils.getJSONObject(key, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about JSONArray
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 将 JSONArray   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String, value: JSONArray?, cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils!!
    ) {
        cacheDoubleUtils.put(key, value)
    }
    /**
     * 将 JSONArray   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param saveTime         缓存的保存时间，以秒为单位。
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String,
        value: JSONArray?,
        saveTime: Int,
        cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils!!
    ) {
        cacheDoubleUtils.put(key, value, saveTime)
    }

    /**
     * Return the JSONArray
     *
     * @param key              key
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     * @return 如果缓存存在，则为 JSONArray ，否则为默认值
     */
    fun getJSONArray(key: String, cacheDoubleUtils: CacheDoubleUtils): JSONArray? {
        return cacheDoubleUtils.getJSONArray(key)
    }

    /**
     * Return the JSONArray
     *
     * @param key              key
     * @param defaultValue    如果缓存不存在，则为默认值。
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     * @return 如果缓存存在，则为 JSONArray ，否则为默认值
     */
    fun getJSONArray(
        key: String, defaultValue: JSONArray?, cacheDoubleUtils: CacheDoubleUtils
    ): JSONArray? {
        return cacheDoubleUtils.getJSONArray(key, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // Bitmap cache
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 将 bitmap   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String, value: Bitmap?, cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils!!
    ) {
        cacheDoubleUtils.put(key, value)
    }
    /**
     * 将 bitmap   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param saveTime         缓存的保存时间，以秒为单位。
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String,
        value: Bitmap?,
        saveTime: Int,
        cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils!!
    ) {
        cacheDoubleUtils.put(key, value, saveTime)
    }

    /**
     * Return the bitmap
     *
     * @param key              key
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     * @return 如果缓存存在，则为 bitmap ，否则为默认值
     */
    fun getBitmap(key: String, cacheDoubleUtils: CacheDoubleUtils): Bitmap? {
        return cacheDoubleUtils.getBitmap(key)
    }

    /**
     * Return the bitmap
     *
     * @param key              key
     * @param defaultValue    如果缓存不存在，则为默认值。
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     * @return 如果缓存存在，则为 bitmap ，否则为默认值
     */
    fun getBitmap(
        key: String, defaultValue: Bitmap?, cacheDoubleUtils: CacheDoubleUtils
    ): Bitmap? {
        return cacheDoubleUtils.getBitmap(key, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about Drawable
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 将 drawable   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String, value: Drawable?, cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils!!
    ) {
        cacheDoubleUtils.put(key, value)
    }
    /**
     * 将 drawable   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param saveTime         缓存的保存时间，以秒为单位。
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String,
        value: Drawable?,
        saveTime: Int,
        cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils!!
    ) {
        cacheDoubleUtils.put(key, value, saveTime)
    }

    /**
     * Return the drawable
     *
     * @param key              key
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     * @return 如果缓存存在，则为 drawable ，否则为默认值
     */
    fun getDrawable(key: String, cacheDoubleUtils: CacheDoubleUtils): Drawable? {
        return cacheDoubleUtils.getDrawable(key)
    }

    /**
     * Return the drawable
     *
     * @param key              key
     * @param defaultValue    如果缓存不存在，则为默认值。
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     * @return 如果缓存存在，则为 drawable ，否则为默认值
     */
    fun getDrawable(
        key: String, defaultValue: Drawable?, cacheDoubleUtils: CacheDoubleUtils
    ): Drawable? {
        return cacheDoubleUtils.getDrawable(key, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about Parcelable
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 将 parcelable   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String, value: Parcelable?, cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils!!
    ) {
        cacheDoubleUtils.put(key, value)
    }
    /**
     * 将 parcelable   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param saveTime         缓存的保存时间，以秒为单位。
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String,
        value: Parcelable?,
        saveTime: Int,
        cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils!!
    ) {
        cacheDoubleUtils.put(key, value, saveTime)
    }

    /**
     * Return the parcelable
     *
     * @param key              key
     * @param creator          The creator.
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     * @param <T>              The value type.
     * @return 如果缓存存在，则为 parcelable ，否则为默认值
    </T> */
    fun <T> getParcelable(
        key: String, creator: Parcelable.Creator<T?>, cacheDoubleUtils: CacheDoubleUtils
    ): T? {
        return cacheDoubleUtils.getParcelable<T>(key, creator)
    }

    /**
     * Return the parcelable
     *
     * @param key              key
     * @param creator          The creator.
     * @param defaultValue    如果缓存不存在，则为默认值。
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     * @param <T>              The value type.
     * @return 如果缓存存在，则为 parcelable ，否则为默认值
    </T> */
    fun <T> getParcelable(
        key: String, creator: Parcelable.Creator<T?>, defaultValue: T, cacheDoubleUtils: CacheDoubleUtils
    ): T {
        return cacheDoubleUtils.getParcelable(key, creator, defaultValue)
    }
    ///////////////////////////////////////////////////////////////////////////
    // about Serializable
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 将 serializable   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String, value: Serializable?, cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils!!
    ) {
        cacheDoubleUtils.put(key, value)
    }
    /**
     * 将 serializable   放入缓存
     *
     * @param key              key
     * @param value            value
     * @param saveTime         缓存的保存时间，以秒为单位。
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String,
        value: Serializable?,
        saveTime: Int,
        cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils!!
    ) {
        cacheDoubleUtils.put(key, value, saveTime)
    }

    /**
     * Return the serializable
     *
     * @param key              key
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     * @return 如果缓存存在，则为 serializable ，否则为默认值
     */
    fun getSerializable(key: String, cacheDoubleUtils: CacheDoubleUtils): Any? {
        return cacheDoubleUtils.getSerializable(key)
    }

    /**
     * Return the serializable
     *
     * @param key              key
     * @param defaultValue    如果缓存不存在，则为默认值。
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     * @return 如果缓存存在，则为 serializable ，否则为默认值
     */
    fun getSerializable(
        key: String, defaultValue: Any?, cacheDoubleUtils: CacheDoubleUtils
    ): Any? {
        return cacheDoubleUtils.getSerializable(key, defaultValue)
    }

    /**
     * 返回磁盘中缓存的大小.
     *
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     * @return 返回磁盘中缓存的大小
     */
    fun getCacheDiskSize(cacheDoubleUtils: CacheDoubleUtils): Long {
        return cacheDoubleUtils.cacheDiskSize
    }

    /**
     * 返回磁盘中缓存的数量.
     *
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     * @return 返回磁盘中缓存的数量
     */
    fun getCacheDiskCount(cacheDoubleUtils: CacheDoubleUtils): Int {
        return cacheDoubleUtils.cacheDiskCount
    }

    /**
     * 返回内存中缓存的数量
     *
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     * @return 返回内存中缓存的数量
     */
    fun getCacheMemoryCount(cacheDoubleUtils: CacheDoubleUtils): Int {
        return cacheDoubleUtils.cacheMemoryCount
    }
    /**
     * 按key删除缓存。
     *
     * @param key              key
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     */
    @JvmOverloads
    fun remove(key: String, cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils!!) {
        cacheDoubleUtils.remove(key)
    }
    /**
     * 清空缓存
     *
     * @param cacheDoubleUtils [CacheDoubleUtils]的实例。
     */
    @JvmOverloads
    fun clear(cacheDoubleUtils: CacheDoubleUtils = defaultCacheDoubleUtils!!) {
        cacheDoubleUtils.clear()
    }

    private var defaultCacheDoubleUtils: CacheDoubleUtils?
        get() = if (sDefaultCacheDoubleUtils != null) sDefaultCacheDoubleUtils else instance
        /**
         * 设置[CacheDoubleUtils]的默认实例。
         *
         * @param cacheDoubleUtils [CacheDoubleUtils]的默认实例。
         */
        set(cacheDoubleUtils) {
            sDefaultCacheDoubleUtils = cacheDoubleUtils
        }
}