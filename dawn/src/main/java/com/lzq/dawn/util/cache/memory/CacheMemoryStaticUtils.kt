package com.lzq.dawn.util.cache.memory;

import androidx.annotation.NonNull;

/**
 * @Name :CacheMemoryStaticUtils
 * @Time :2022/7/18 14:10
 * @Author :  Lzq
 * @Desc : 内存缓存
 */
public final class CacheMemoryStaticUtils {

    private static CacheMemoryUtils sDefaultCacheMemoryUtils;

    /**
     * 设置{@link CacheMemoryUtils}的默认实例。
     *
     * @param cacheMemoryUtils {@link CacheMemoryUtils}的默认实例。
     */
    public static void setDefaultCacheMemoryUtils(final CacheMemoryUtils cacheMemoryUtils) {
        sDefaultCacheMemoryUtils = cacheMemoryUtils;
    }

    /**
     * 将字节放入缓存。
     *
     * @param key   key
     * @param value value
     */
    public static void put(@NonNull final String key, final Object value) {
        put(key, value, getDefaultCacheMemoryUtils());
    }

    /**
     * 将字节放入缓存。
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位
     */
    public static void put(@NonNull final String key, final Object value, int saveTime) {
        put(key, value, saveTime, getDefaultCacheMemoryUtils());
    }

    /**
     * 返回缓存中的值。
     *
     * @param key key
     * @param <T> The value type.
     * @return 缓存存在时的值，否则为null
     */
    public static <T> T get(@NonNull final String key) {
        return get(key, getDefaultCacheMemoryUtils());
    }

    /**
     * 返回缓存中的值。
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @param <T>          The value type.
     * @return 缓存存在时的值，否则为defaultValue
     */
    public static <T> T get(@NonNull final String key, final T defaultValue) {
        return get(key, defaultValue, getDefaultCacheMemoryUtils());
    }

    /**
     * 返回缓存数量
     *
     * @return 返回缓存数量
     */
    public static int getCacheCount() {
        return getCacheCount(getDefaultCacheMemoryUtils());
    }

    /**
     * 按key删除缓存。
     *
     * @param key key
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static Object remove(@NonNull final String key) {
        return remove(key, getDefaultCacheMemoryUtils());
    }

    /**
     * 清除所有缓存。
     */
    public static void clear() {
        clear(getDefaultCacheMemoryUtils());
    }

    ///////////////////////////////////////////////////////////////////////////
    // dividing line
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将字节放入缓存。
     *
     * @param key              The key of cache.
     * @param value            The value of cache.
     * @param cacheMemoryUtils {@link CacheMemoryUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           final Object value,
                           @NonNull final CacheMemoryUtils cacheMemoryUtils) {
        cacheMemoryUtils.put(key, value);
    }

    /**
     * 将字节放入缓存。
     *
     * @param key              The key of cache.
     * @param value            The value of cache.
     * @param saveTime         The save time of cache, in seconds.
     * @param cacheMemoryUtils {@link CacheMemoryUtils}的实例。
     */
    public static void put(@NonNull final String key,
                           final Object value,
                           int saveTime,
                           @NonNull final CacheMemoryUtils cacheMemoryUtils) {
        cacheMemoryUtils.put(key, value, saveTime);
    }

    /**
     * 返回缓存中的值。
     *
     * @param key              The key of cache.
     * @param cacheMemoryUtils {@link CacheMemoryUtils}的实例。
     * @param <T>              The value type.
     * @return the value if cache exists or null otherwise
     */
    public static <T> T get(@NonNull final String key, @NonNull final CacheMemoryUtils cacheMemoryUtils) {
        return cacheMemoryUtils.get(key);
    }

    /**
     * 返回缓存中的值。
     *
     * @param key              The key of cache.
     * @param defaultValue     The default value if the cache doesn't exist.
     * @param cacheMemoryUtils {@link CacheMemoryUtils}的实例。
     * @param <T>              The value type.
     * @return the value if cache exists or defaultValue otherwise
     */
    public static <T> T get(@NonNull final String key,
                            final T defaultValue,
                            @NonNull final CacheMemoryUtils cacheMemoryUtils) {
        return cacheMemoryUtils.get(key, defaultValue);
    }

    /**
     * 返回缓存数量
     *
     * @param cacheMemoryUtils {@link CacheMemoryUtils}的实例。
     * @return 返回缓存数量
     */
    public static int getCacheCount(@NonNull final CacheMemoryUtils cacheMemoryUtils) {
        return cacheMemoryUtils.getCacheCount();
    }

    /**
     * 按key删除缓存。
     *
     * @param key              key
     * @param cacheMemoryUtils {@link CacheMemoryUtils}的实例。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static Object remove(@NonNull final String key, @NonNull final CacheMemoryUtils cacheMemoryUtils) {
        return cacheMemoryUtils.remove(key);
    }

    /**
     * 清除所有缓存。
     *
     * @param cacheMemoryUtils {@link CacheMemoryUtils}的实例。
     */
    public static void clear(@NonNull final CacheMemoryUtils cacheMemoryUtils) {
        cacheMemoryUtils.clear();
    }

    private static CacheMemoryUtils getDefaultCacheMemoryUtils() {
        return sDefaultCacheMemoryUtils != null ? sDefaultCacheMemoryUtils : CacheMemoryUtils.getInstance();
    }
}