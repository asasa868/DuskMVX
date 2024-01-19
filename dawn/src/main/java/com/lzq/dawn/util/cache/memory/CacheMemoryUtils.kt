package com.lzq.dawn.util.cache.memory

import androidx.collection.LruCache
import com.lzq.dawn.util.cache.CacheConstants

/**
 * @Name :CacheMemoryUtils
 * @Time :2022/7/18 14:10
 * @Author :  Lzq
 * @Desc : 内存缓存
 */
class CacheMemoryUtils private constructor(
    private val mCacheKey: String,
    private val mMemoryCache: LruCache<String, CacheValue>
) {
    /**
     * 将字节放入缓存。
     *
     * @param key  key
     * @param value value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    @JvmOverloads
    fun put(key: String, value: Any?, saveTime: Int = -1) {
        if (value == null) {
            return
        }
        val dueTime = if (saveTime < 0) -1 else System.currentTimeMillis() + saveTime * 1000
        mMemoryCache.put(key, CacheValue(dueTime, value))
    }

    /**
     * 返回缓存中的值。
     *
     * @param key  key
     * @param <T> 值类型
     * @return 缓存存在时的值，否则为null
    </T> */
    operator fun <T> get(key: String): T? {
        return get<T?>(key, null)
    }

    /**
     * 返回缓存中的值。
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @param <T>         值类型
     * @return 缓存存在时的值，否则为defaultValue
    </T> */
    operator fun <T> get(key: String, defaultValue: T): T {
        val value = mMemoryCache[key] ?: return defaultValue
        if (value.dueTime == -1L || value.dueTime >= System.currentTimeMillis()) {
            return value.value as T
        }
        mMemoryCache.remove(key)
        return defaultValue
    }

    val cacheCount: Int
        /**
         * 返回缓存数量
         *
         * @return 返回缓存数量
         */
        get() = mMemoryCache.size()

    /**
     * 按key删除缓存。
     *
     * @param key key
     * @return `true`: success<br></br>`false`: fail
     */
    fun remove(key: String): Any? {
        val remove = mMemoryCache.remove(key) ?: return null
        return remove.value
    }

    /**
     * 清除所有缓存。
     */
    fun clear() {
        mMemoryCache.evictAll()
    }

    override fun toString(): String {
        return mCacheKey + "@" + Integer.toHexString(hashCode())
    }

    companion object {
        private val CACHE_MAP: MutableMap<String, CacheMemoryUtils> = HashMap()
        @JvmStatic
        val instance: CacheMemoryUtils?
            /**
             * 返回单个[CacheMemoryUtils]实例。
             *
             * @return 返回单个{@link CacheMemoryUtils}实例。
             */
            get() = getInstance(CacheConstants.MEMORY_DEFAULT_MAX_COUNT)

        /**
         * 返回单个[CacheMemoryUtils]实例。
         *
         * @param maxCount 缓存的最大计数。
         * @return 单个{@link CacheMemoryUtils}实例
         */
        fun getInstance(maxCount: Int): CacheMemoryUtils? {
            return getInstance(maxCount.toString(), maxCount)
        }

        /**
         * 返回单个[CacheMemoryUtils]实例。
         *
         * @param cacheKey The key of cache.
         * @param maxCount 缓存的最大计数。
         * @return 单个{@link CacheMemoryUtils}实例
         */
        fun getInstance(cacheKey: String, maxCount: Int): CacheMemoryUtils? {
            var cache = CACHE_MAP[cacheKey]
            if (cache == null) {
                synchronized(CacheMemoryUtils::class.java) {
                    cache = CACHE_MAP[cacheKey]
                    if (cache == null) {
                        cache = CacheMemoryUtils(cacheKey, LruCache(maxCount))
                        CACHE_MAP[cacheKey] = cache!!
                    }
                }
            }
            return cache
        }
    }
}