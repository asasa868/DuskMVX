package com.lzq.dawn.util.cache

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Parcelable
import com.lzq.dawn.util.cache.disk.CacheDiskUtils
import com.lzq.dawn.util.cache.memory.CacheMemoryUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable

/**
 * @Name :CacheDoubleUtils
 * @Time :2022/7/18 16:01
 * @Author :  Lzq
 * @Desc : 双缓存
 */
class CacheDoubleUtils private constructor(
    private val mCacheMemoryUtils: CacheMemoryUtils, private val mCacheDiskUtils: CacheDiskUtils
) : CacheConstants {
    /**
     * 将 bytes  放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    @JvmOverloads
    fun put(key: String, value: ByteArray?, saveTime: Int = -1) {
        mCacheMemoryUtils.put(key, value, saveTime)
        mCacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the bytes
     *
     * @param key key
     * @return 如果缓存存在，则为字节，否则为空
     */
    fun getBytes(key: String): ByteArray? {
        return getBytes(key, null)
    }

    /**
     * Return the bytes
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为字节，否则为默认值。
     */
    fun getBytes(key: String, defaultValue: ByteArray?): ByteArray? {
        val obj = mCacheMemoryUtils.get<ByteArray>(key)
        if (obj != null) {
            return obj
        }
        val bytes = mCacheDiskUtils.getBytes(key)
        if (bytes != null) {
            mCacheMemoryUtils.put(key, bytes)
            return bytes
        }
        return defaultValue
    }

    /**
     * 将 string value  放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    @JvmOverloads
    fun put(key: String, value: String?, saveTime: Int = -1) {
        mCacheMemoryUtils.put(key, value, saveTime)
        mCacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the string value
     *
     * @param key key
     * @return 如果缓存存在，则为string，否则为空
     */
    fun getString(key: String): String? {
        return getString(key, null)
    }

    /**
     * Return the string value
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为string，否则为默认值。
     */
    fun getString(key: String, defaultValue: String?): String? {
        val obj = mCacheMemoryUtils.get<String>(key)
        if (obj != null) return obj
        val string = mCacheDiskUtils.getString(key)
        if (string != null) {
            mCacheMemoryUtils.put(key, string)
            return string
        }
        return defaultValue
    }

    /**
     * 将 JSONObject  放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    @JvmOverloads
    fun put(
        key: String, value: JSONObject?, saveTime: Int = -1
    ) {
        mCacheMemoryUtils.put(key, value, saveTime)
        mCacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the JSONObject
     *
     * @param key key
     * @return 如果缓存存在，则为JSONObject，否则为null。
     */
    fun getJSONObject(key: String): JSONObject? {
        return getJSONObject(key, null)
    }

    /**
     * Return the JSONObject
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为JSONObject，否则为默认值。
     */
    fun getJSONObject(key: String, defaultValue: JSONObject?): JSONObject? {
        val obj = mCacheMemoryUtils.get<JSONObject>(key)
        if (obj != null) {
            return obj
        }
        val jsonObject = mCacheDiskUtils.getJSONObject(key)
        if (jsonObject != null) {
            mCacheMemoryUtils.put(key, jsonObject)
            return jsonObject
        }
        return defaultValue
    }

    /**
     * 将 JSONArray  放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    @JvmOverloads
    fun put(key: String, value: JSONArray?, saveTime: Int = -1) {
        mCacheMemoryUtils.put(key, value, saveTime)
        mCacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the JSONArray
     *
     * @param key key
     * @return 如果缓存存在，则为JSONArray，否则为默认值。
     */
    fun getJSONArray(key: String): JSONArray? {
        return getJSONArray(key, null)
    }

    /**
     * Return the JSONArray
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为 JSONArray，否则为默认值。
     */
    fun getJSONArray(key: String, defaultValue: JSONArray?): JSONArray? {
        val obj = mCacheMemoryUtils.get<JSONArray>(key)
        if (obj != null) {
            return obj
        }
        val jsonArray = mCacheDiskUtils.getJSONArray(key)
        if (jsonArray != null) {
            mCacheMemoryUtils.put(key, jsonArray)
            return jsonArray
        }
        return defaultValue
    }

    /**
     * 将 bitmap  放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    @JvmOverloads
    fun put(key: String, value: Bitmap?, saveTime: Int = -1) {
        mCacheMemoryUtils.put(key, value, saveTime)
        mCacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the bitmap
     *
     * @param key key
     * @return 如果缓存存在，则为 bitmap，否则为默认值。
     */
    fun getBitmap(key: String): Bitmap? {
        return getBitmap(key, null)
    }

    /**
     * Return the bitmap
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为 bitmap，否则为默认值。
     */
    fun getBitmap(key: String, defaultValue: Bitmap?): Bitmap? {
        val obj = mCacheMemoryUtils.get<Bitmap>(key)
        if (obj != null) {
            return obj
        }
        val bitmap = mCacheDiskUtils.getBitmap(key)
        if (bitmap != null) {
            mCacheMemoryUtils.put(key, bitmap)
            return bitmap
        }
        return defaultValue
    }

    /**
     * 将 drawable  放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    @JvmOverloads
    fun put(key: String, value: Drawable?, saveTime: Int = -1) {
        mCacheMemoryUtils.put(key, value, saveTime)
        mCacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the drawable
     *
     * @param key key
     * @return 如果缓存存在，则为 drawable，否则为默认值。
     */
    fun getDrawable(key: String): Drawable? {
        return getDrawable(key, null)
    }

    /**
     * Return the drawable
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为 drawable，否则为默认值。
     */
    fun getDrawable(key: String, defaultValue: Drawable?): Drawable? {
        val obj = mCacheMemoryUtils.get<Drawable>(key)
        if (obj != null) {
            return obj
        }
        val drawable = mCacheDiskUtils.getDrawable(key)
        if (drawable != null) {
            mCacheMemoryUtils.put(key, drawable)
            return drawable
        }
        return defaultValue
    }

    /**
     * 将 parcelable  放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    @JvmOverloads
    fun put(key: String, value: Parcelable?, saveTime: Int = -1) {
        mCacheMemoryUtils.put(key, value, saveTime)
        mCacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * Return the parcelable
     *
     * @param key     key
     * @param creator The creator.
     * @param <T>    值类型
     * @return 如果缓存存在，则为 parcelable，否则为默认值。
    </T> */
    fun <T> getParcelable(
        key: String, creator: Parcelable.Creator<T?>
    ): T? {
        return getParcelable(key, creator, null)
    }

    /**
     * Return the parcelable
     *
     * @param key          key
     * @param creator      The creator.
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @param <T>          值类型
     * @return 如果缓存存在，则为 parcelable，否则为默认值。
    </T> */
    fun <T> getParcelable(
        key: String, creator: Parcelable.Creator<T?>, defaultValue: T
    ): T {
        val value = mCacheMemoryUtils.get<T>(key)
        if (value != null) {
            return value
        }
        val values = mCacheDiskUtils.getParcelable(key, creator)
        if (values != null) {
            mCacheMemoryUtils.put(key, values)
            return values
        }
        return defaultValue
    }

    /**
     * 将 serializable  放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    @JvmOverloads
    fun put(key: String, value: Serializable?, saveTime: Int = -1) {
        mCacheMemoryUtils.put(key, value, saveTime)
        mCacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * 返回缓存中的 serializable
     *
     * @param key key
     * @return serializable（如果缓存存在），否则为null
     */
    fun getSerializable(key: String): Any? {
        return getSerializable(key, null)
    }

    /**
     * 返回缓存中的 serializable
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return serializable（如果缓存存在）或默认值（否则）
     */
    fun getSerializable(key: String, defaultValue: Any?): Any? {
        val obj = mCacheMemoryUtils.get<Any>(key)
        if (obj != null) {
            return obj
        }
        val serializable = mCacheDiskUtils.getSerializable(key)
        if (serializable != null) {
            mCacheMemoryUtils.put(key, serializable)
            return serializable
        }
        return defaultValue
    }

    val cacheDiskSize: Long
        /**
         * 返回磁盘中缓存的大小。
         *
         * @return 返回磁盘中缓存的大小。
         */
        get() = mCacheDiskUtils.cacheSize
    val cacheDiskCount: Int
        /**
         * 返回磁盘中缓存的数量
         *
         * @return 返回磁盘中缓存的数量
         */
        get() = mCacheDiskUtils.cacheCount
    val cacheMemoryCount: Int
        /**
         * 返回内存中缓存的数量
         *
         * @return 返回内存中缓存的数量
         */
        get() = mCacheMemoryUtils.cacheCount

    /**
     * 删除缓存中指定的key
     *
     * @param key key
     */
    fun remove(key: String) {
        mCacheMemoryUtils.remove(key)
        mCacheDiskUtils.remove(key)
    }

    /**
     * 清空缓存
     */
    fun clear() {
        mCacheMemoryUtils.clear()
        mCacheDiskUtils.clear()
    }

    companion object {
        private val CACHE_MAP: MutableMap<String, CacheDoubleUtils> = HashMap()

        @JvmStatic
        val instance: CacheDoubleUtils?
            /**
             * 返回单个[CacheDoubleUtils]实例。
             *
             * @return 返回单个{@link CacheDoubleUtils}实例。
             */
            get() = getInstance(CacheMemoryUtils.instance!!, CacheDiskUtils.instance!!)

        /**
         * 返回单个[CacheDoubleUtils]实例。
         *
         * @param cacheMemoryUtils [CacheMemoryUtils]的实例。
         * @param cacheDiskUtils   [CacheDiskUtils]的实例。
         * @return 单个{@link CacheDoubleUtils}实例。
         */
        fun getInstance(
            cacheMemoryUtils: CacheMemoryUtils, cacheDiskUtils: CacheDiskUtils
        ): CacheDoubleUtils? {
            val cacheKey = cacheDiskUtils.toString() + "_" + cacheMemoryUtils.toString()
            var cache = CACHE_MAP[cacheKey]
            if (cache == null) {
                synchronized(CacheDoubleUtils::class.java) {
                    cache = CACHE_MAP[cacheKey]
                    if (cache == null) {
                        cache = CacheDoubleUtils(cacheMemoryUtils, cacheDiskUtils)
                        CACHE_MAP[cacheKey] = cache!!
                    }
                }
            }
            return cache
        }
    }
}