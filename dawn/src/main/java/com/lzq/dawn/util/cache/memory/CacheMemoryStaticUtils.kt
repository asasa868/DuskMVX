package com.lzq.dawn.util.cache.memory

import com.lzq.dawn.util.cache.memory.CacheMemoryUtils.Companion.instance

/**
 * @Name :CacheMemoryStaticUtils
 * @Time :2022/7/18 14:10
 * @Author :  Lzq
 * @Desc : 内存缓存
 */
object CacheMemoryStaticUtils {
    private var sDefaultCacheMemoryUtils: CacheMemoryUtils? = null



    /**
     * 返回缓存中的值。
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @param <T>          The value type.
     * @return 缓存存在时的值，否则为defaultValue
    </T> */
    operator fun <T> get(key: String, defaultValue: T): T {
        return get(key, defaultValue, defaultCacheMemoryUtils!!)
    }

    val cacheCount: Int
        /**
         * 返回缓存数量
         *
         * @return 返回缓存数量
         */
        get() = getCacheCount(defaultCacheMemoryUtils!!)
    /**
     * 将字节放入缓存。
     *
     * @param key              The key of cache.
     * @param value            The value of cache.
     * @param cacheMemoryUtils [CacheMemoryUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String, value: Any?, cacheMemoryUtils: CacheMemoryUtils = defaultCacheMemoryUtils!!
    ) {
        cacheMemoryUtils.put(key, value)
    }
    /**
     * 将字节放入缓存。
     *
     * @param key              The key of cache.
     * @param value            The value of cache.
     * @param saveTime         The save time of cache, in seconds.
     * @param cacheMemoryUtils [CacheMemoryUtils]的实例。
     */
    @JvmOverloads
    fun put(
        key: String,
        value: Any?,
        saveTime: Int,
        cacheMemoryUtils: CacheMemoryUtils = defaultCacheMemoryUtils!!
    ) {
        cacheMemoryUtils.put(key, value, saveTime)
    }

    /**
     * 返回缓存中的值。
     *
     * @param key              The key of cache.
     * @param cacheMemoryUtils [CacheMemoryUtils]的实例。
     * @param <T>              The value type.
     * @return the value if cache exists or null otherwise
    </T> */
    operator fun <T> get(key: String, cacheMemoryUtils: CacheMemoryUtils): T? {
        return cacheMemoryUtils.get<T>(key)
    }

    /**
     * 返回缓存中的值。
     *
     * @param key              The key of cache.
     * @param defaultValue     The default value if the cache doesn't exist.
     * @param cacheMemoryUtils [CacheMemoryUtils]的实例。
     * @param <T>              The value type.
     * @return the value if cache exists or defaultValue otherwise
    </T> */
    operator fun <T> get(
        key: String, defaultValue: T, cacheMemoryUtils: CacheMemoryUtils
    ): T {
        return cacheMemoryUtils.get(key, defaultValue)
    }

    /**
     * 返回缓存数量
     *
     * @param cacheMemoryUtils [CacheMemoryUtils]的实例。
     * @return 返回缓存数量
     */
    fun getCacheCount(cacheMemoryUtils: CacheMemoryUtils): Int {
        return cacheMemoryUtils.cacheCount
    }
    /**
     * 按key删除缓存。
     *
     * @param key              key
     * @param cacheMemoryUtils [CacheMemoryUtils]的实例。
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmOverloads
    fun remove(key: String, cacheMemoryUtils: CacheMemoryUtils = defaultCacheMemoryUtils!!): Any? {
        return cacheMemoryUtils.remove(key)
    }
    /**
     * 清除所有缓存。
     *
     * @param cacheMemoryUtils [CacheMemoryUtils]的实例。
     */
    @JvmOverloads
    fun clear(cacheMemoryUtils: CacheMemoryUtils = defaultCacheMemoryUtils!!) {
        cacheMemoryUtils.clear()
    }

    private var defaultCacheMemoryUtils: CacheMemoryUtils?
        get() = if (sDefaultCacheMemoryUtils != null) sDefaultCacheMemoryUtils else instance
        /**
         * 设置[CacheMemoryUtils]的默认实例。
         *
         * @param cacheMemoryUtils [CacheMemoryUtils]的默认实例。
         */
        set(cacheMemoryUtils) {
            sDefaultCacheMemoryUtils = cacheMemoryUtils
        }
}