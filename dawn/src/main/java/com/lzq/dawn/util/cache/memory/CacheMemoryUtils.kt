package com.lzq.dawn.util.cache.memory;

import androidx.annotation.NonNull;
import androidx.collection.LruCache;


import com.lzq.dawn.util.cache.CacheConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * @Name :CacheMemoryUtils
 * @Time :2022/7/18 14:10
 * @Author :  Lzq
 * @Desc : 内存缓存
 */
public final class CacheMemoryUtils {



    private static final Map<String, CacheMemoryUtils> CACHE_MAP = new HashMap<>();

    private final String mCacheKey;
    private final LruCache<String, CacheValue> mMemoryCache;


    private CacheMemoryUtils(String cacheKey, LruCache<String, CacheValue> memoryCache) {
        mCacheKey = cacheKey;
        mMemoryCache = memoryCache;
    }


    /**
     * 返回单个{@link CacheMemoryUtils}实例。
     *
     * @return 返回单个{@link CacheMemoryUtils}实例。
     */
    public static CacheMemoryUtils getInstance() {
        return getInstance(CacheConstants.MEMORY_DEFAULT_MAX_COUNT);
    }

    /**
     * 返回单个{@link CacheMemoryUtils}实例。
     *
     * @param maxCount 缓存的最大计数。
     * @return 单个{@link CacheMemoryUtils}实例
     */
    public static CacheMemoryUtils getInstance(final int maxCount) {
        return getInstance(String.valueOf(maxCount), maxCount);
    }

    /**
     * 返回单个{@link CacheMemoryUtils}实例。
     *
     * @param cacheKey The key of cache.
     * @param maxCount 缓存的最大计数。
     * @return 单个{@link CacheMemoryUtils}实例
     */
    public static CacheMemoryUtils getInstance(final String cacheKey, final int maxCount) {
        CacheMemoryUtils cache = CACHE_MAP.get(cacheKey);
        if (cache == null) {
            synchronized (CacheMemoryUtils.class) {
                cache = CACHE_MAP.get(cacheKey);
                if (cache == null) {
                    cache = new CacheMemoryUtils(cacheKey, new LruCache<String, CacheValue>(maxCount));
                    CACHE_MAP.put(cacheKey, cache);
                }
            }
        }
        return cache;
    }


    /**
     * 将字节放入缓存。
     *
     * @param key  key
     * @param value value
     */
    public void put(@NonNull final String key, final Object value) {
        put(key, value, -1);
    }

    /**
     * 将字节放入缓存。
     *
     * @param key  key
     * @param value value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public void put(@NonNull final String key, final Object value, int saveTime) {
        if (value == null) {
            return;
        }
        long dueTime = saveTime < 0 ? -1 : System.currentTimeMillis() + saveTime * 1000;
        mMemoryCache.put(key, new CacheValue(dueTime, value));
    }

    /**
     * 返回缓存中的值。
     *
     * @param key  key
     * @param <T> 值类型
     * @return 缓存存在时的值，否则为null
     */
    public <T> T get(@NonNull final String key) {
        return get(key, null);
    }

    /**
     * 返回缓存中的值。
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @param <T>         值类型
     * @return 缓存存在时的值，否则为defaultValue
     */
    public <T> T get(@NonNull final String key, final T defaultValue) {
        CacheValue val = mMemoryCache.get(key);
        if (val == null) {
            return defaultValue;
        }
        if (val.dueTime == -1 || val.dueTime >= System.currentTimeMillis()) {
            //noinspection unchecked
            return (T) val.value;
        }
        mMemoryCache.remove(key);
        return defaultValue;
    }

    /**
     * 返回缓存数量
     *
     * @return 返回缓存数量
     */
    public int getCacheCount() {
        return mMemoryCache.size();
    }

    /**
     * 按key删除缓存。
     *
     * @param key key
     * @return {@code true}: success<br>{@code false}: fail
     */
    public Object remove(@NonNull final String key) {
        CacheValue remove = mMemoryCache.remove(key);
        if (remove == null) {
            return null;
        }
        return remove.value;
    }

    /**
     * 清除所有缓存。
     */
    public void clear() {
        mMemoryCache.evictAll();
    }


    @Override
    public String toString() {
        return mCacheKey + "@" + Integer.toHexString(hashCode());
    }

}