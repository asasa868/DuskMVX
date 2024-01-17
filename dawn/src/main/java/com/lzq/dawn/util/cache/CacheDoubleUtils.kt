package com.lzq.dawn.util.cache;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.lzq.dawn.util.cache.disk.CacheDiskUtils;
import com.lzq.dawn.util.cache.memory.CacheMemoryUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Name :CacheDoubleUtils
 * @Time :2022/7/18 16:01
 * @Author :  Lzq
 * @Desc : 双缓存
 */
public final class CacheDoubleUtils implements CacheConstants {

    private static final Map<String, CacheDoubleUtils> CACHE_MAP = new HashMap<>();

    private final CacheMemoryUtils mCacheMemoryUtils;
    private final CacheDiskUtils mCacheDiskUtils;

    private CacheDoubleUtils(CacheMemoryUtils cacheMemoryUtils, CacheDiskUtils cacheUtils) {
        mCacheMemoryUtils = cacheMemoryUtils;
        mCacheDiskUtils = cacheUtils;
    }

    /**
     * 返回单个{@link CacheDoubleUtils}实例。
     *
     * @return 返回单个{@link CacheDoubleUtils}实例。
     */
    public static CacheDoubleUtils getInstance() {
        return getInstance(CacheMemoryUtils.getInstance(), CacheDiskUtils.getInstance());
    }

    /**
     * 返回单个{@link CacheDoubleUtils}实例。
     *
     * @param cacheMemoryUtils {@link CacheMemoryUtils}的实例。
     * @param cacheDiskUtils   {@link CacheDiskUtils}的实例。
     * @return 单个{@link CacheDoubleUtils}实例。
     */
    public static CacheDoubleUtils getInstance(@NonNull final CacheMemoryUtils cacheMemoryUtils,
                                               @NonNull final CacheDiskUtils cacheDiskUtils) {
        final String cacheKey = cacheDiskUtils.toString() + "_" + cacheMemoryUtils.toString();
        CacheDoubleUtils cache = CACHE_MAP.get(cacheKey);
        if (cache == null) {
            synchronized (CacheDoubleUtils.class) {
                cache = CACHE_MAP.get(cacheKey);
                if (cache == null) {
                    cache = new CacheDoubleUtils(cacheMemoryUtils, cacheDiskUtils);
                    CACHE_MAP.put(cacheKey, cache);
                }
            }
        }
        return cache;
    }

    ///////////////////////////////////////////////////////////////////////////
    // about bytes
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 bytes  放入缓存
     *
     * @param key   key
     * @param value value
     */
    public void put(@NonNull final String key, final byte[] value) {
        put(key, value, -1);
    }

    /**
     * 将 bytes  放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public void put(@NonNull final String key, byte[] value, final int saveTime) {
        mCacheMemoryUtils.put(key, value, saveTime);
        mCacheDiskUtils.put(key, value, saveTime);
    }

    /**
     * Return the bytes  
     *
     * @param key key
     * @return 如果缓存存在，则为字节，否则为空
     */
    public byte[] getBytes(@NonNull final String key) {
        return getBytes(key, null);
    }

    /**
     * Return the bytes  
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为字节，否则为默认值。
     */
    public byte[] getBytes(@NonNull final String key, final byte[] defaultValue) {
        byte[] obj = mCacheMemoryUtils.get(key);
        if (obj != null) {
            return obj;
        }
        byte[] bytes = mCacheDiskUtils.getBytes(key);
        if (bytes != null) {
            mCacheMemoryUtils.put(key, bytes);
            return bytes;
        }
        return defaultValue;
    }

    ///////////////////////////////////////////////////////////////////////////
    // about String
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 string value  放入缓存
     *
     * @param key   key
     * @param value value
     */
    public void put(@NonNull final String key, final String value) {
        put(key, value, -1);
    }

    /**
     * 将 string value  放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public void put(@NonNull final String key, final String value, final int saveTime) {
        mCacheMemoryUtils.put(key, value, saveTime);
        mCacheDiskUtils.put(key, value, saveTime);
    }

    /**
     * Return the string value  
     *
     * @param key key
     * @return 如果缓存存在，则为string，否则为空
     */
    public String getString(@NonNull final String key) {
        return getString(key, null);
    }

    /**
     * Return the string value  
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为string，否则为默认值。
     */
    public String getString(@NonNull final String key, final String defaultValue) {
        String obj = mCacheMemoryUtils.get(key);
        if (obj != null) return obj;
        String string = mCacheDiskUtils.getString(key);
        if (string != null) {
            mCacheMemoryUtils.put(key, string);
            return string;
        }
        return defaultValue;
    }

    ///////////////////////////////////////////////////////////////////////////
    // about JSONObject
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 JSONObject  放入缓存
     *
     * @param key   key
     * @param value value
     */
    public void put(@NonNull final String key, final JSONObject value) {
        put(key, value, -1);
    }

    /**
     * 将 JSONObject  放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public void put(@NonNull final String key,
                    final JSONObject value,
                    final int saveTime) {
        mCacheMemoryUtils.put(key, value, saveTime);
        mCacheDiskUtils.put(key, value, saveTime);
    }

    /**
     * Return the JSONObject  
     *
     * @param key key
     * @return 如果缓存存在，则为JSONObject，否则为null。
     */
    public JSONObject getJSONObject(@NonNull final String key) {
        return getJSONObject(key, null);
    }

    /**
     * Return the JSONObject  
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为JSONObject，否则为默认值。
     */
    public JSONObject getJSONObject(@NonNull final String key, final JSONObject defaultValue) {
        JSONObject obj = mCacheMemoryUtils.get(key);
        if (obj != null) {
            return obj;
        }
        JSONObject jsonObject = mCacheDiskUtils.getJSONObject(key);
        if (jsonObject != null) {
            mCacheMemoryUtils.put(key, jsonObject);
            return jsonObject;
        }
        return defaultValue;
    }


    ///////////////////////////////////////////////////////////////////////////
    // about JSONArray
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 JSONArray  放入缓存
     *
     * @param key   key
     * @param value value
     */
    public void put(@NonNull final String key, final JSONArray value) {
        put(key, value, -1);
    }

    /**
     * 将 JSONArray  放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public void put(@NonNull final String key, final JSONArray value, final int saveTime) {
        mCacheMemoryUtils.put(key, value, saveTime);
        mCacheDiskUtils.put(key, value, saveTime);
    }

    /**
     * Return the JSONArray  
     *
     * @param key key
     * @return 如果缓存存在，则为JSONArray，否则为默认值。
     */
    public JSONArray getJSONArray(@NonNull final String key) {
        return getJSONArray(key, null);
    }

    /**
     * Return the JSONArray  
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为 JSONArray，否则为默认值。
     */
    public JSONArray getJSONArray(@NonNull final String key, final JSONArray defaultValue) {
        JSONArray obj = mCacheMemoryUtils.get(key);
        if (obj != null) {
            return obj;
        }
        JSONArray jsonArray = mCacheDiskUtils.getJSONArray(key);
        if (jsonArray != null) {
            mCacheMemoryUtils.put(key, jsonArray);
            return jsonArray;
        }
        return defaultValue;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Bitmap cache
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 bitmap  放入缓存
     *
     * @param key   key
     * @param value value
     */
    public void put(@NonNull final String key, final Bitmap value) {
        put(key, value, -1);
    }

    /**
     * 将 bitmap  放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public void put(@NonNull final String key, final Bitmap value, final int saveTime) {
        mCacheMemoryUtils.put(key, value, saveTime);
        mCacheDiskUtils.put(key, value, saveTime);
    }

    /**
     * Return the bitmap  
     *
     * @param key key
     * @return 如果缓存存在，则为 bitmap，否则为默认值。
     */
    public Bitmap getBitmap(@NonNull final String key) {
        return getBitmap(key, null);
    }

    /**
     * Return the bitmap  
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为 bitmap，否则为默认值。
     */
    public Bitmap getBitmap(@NonNull final String key, final Bitmap defaultValue) {
        Bitmap obj = mCacheMemoryUtils.get(key);
        if (obj != null) {
            return obj;
        }
        Bitmap bitmap = mCacheDiskUtils.getBitmap(key);
        if (bitmap != null) {
            mCacheMemoryUtils.put(key, bitmap);
            return bitmap;
        }
        return defaultValue;
    }

    ///////////////////////////////////////////////////////////////////////////
    // about Drawable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 drawable  放入缓存
     *
     * @param key   key
     * @param value value
     */
    public void put(@NonNull final String key, final Drawable value) {
        put(key, value, -1);
    }

    /**
     * 将 drawable  放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public void put(@NonNull final String key, final Drawable value, final int saveTime) {
        mCacheMemoryUtils.put(key, value, saveTime);
        mCacheDiskUtils.put(key, value, saveTime);
    }

    /**
     * Return the drawable  
     *
     * @param key key
     * @return 如果缓存存在，则为 drawable，否则为默认值。
     */
    public Drawable getDrawable(@NonNull final String key) {
        return getDrawable(key, null);
    }

    /**
     * Return the drawable  
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return 如果缓存存在，则为 drawable，否则为默认值。
     */
    public Drawable getDrawable(@NonNull final String key, final Drawable defaultValue) {
        Drawable obj = mCacheMemoryUtils.get(key);
        if (obj != null) {
            return obj;
        }
        Drawable drawable = mCacheDiskUtils.getDrawable(key);
        if (drawable != null) {
            mCacheMemoryUtils.put(key, drawable);
            return drawable;
        }
        return defaultValue;
    }

    ///////////////////////////////////////////////////////////////////////////
    // about Parcelable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 parcelable  放入缓存
     *
     * @param key   key
     * @param value value
     */
    public void put(@NonNull final String key, final Parcelable value) {
        put(key, value, -1);
    }

    /**
     * 将 parcelable  放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public void put(@NonNull final String key, final Parcelable value, final int saveTime) {
        mCacheMemoryUtils.put(key, value, saveTime);
        mCacheDiskUtils.put(key, value, saveTime);
    }

    /**
     * Return the parcelable  
     *
     * @param key     key
     * @param creator The creator.
     * @param <T>    值类型
     * @return 如果缓存存在，则为 parcelable，否则为默认值。
     */
    public <T> T getParcelable(@NonNull final String key,
                               @NonNull final Parcelable.Creator<T> creator) {
        return getParcelable(key, creator, null);
    }

    /**
     * Return the parcelable  
     *
     * @param key          key
     * @param creator      The creator.
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @param <T>          值类型
     * @return 如果缓存存在，则为 parcelable，否则为默认值。
     */
    public <T> T getParcelable(@NonNull final String key,
                               @NonNull final Parcelable.Creator<T> creator,
                               final T defaultValue) {
        T value = mCacheMemoryUtils.get(key);
        if (value != null) {
            return value;
        }
        T val = mCacheDiskUtils.getParcelable(key, creator);
        if (val != null) {
            mCacheMemoryUtils.put(key, val);
            return val;
        }
        return defaultValue;
    }

    ///////////////////////////////////////////////////////////////////////////
    // about Serializable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将 serializable  放入缓存
     *
     * @param key   key
     * @param value value
     */
    public void put(@NonNull final String key, final Serializable value) {
        put(key, value, -1);
    }

    /**
     * 将 serializable  放入缓存
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public void put(@NonNull final String key, final Serializable value, final int saveTime) {
        mCacheMemoryUtils.put(key, value, saveTime);
        mCacheDiskUtils.put(key, value, saveTime);
    }

    /**
     * 返回缓存中的 serializable
     *
     * @param key key
     * @return serializable（如果缓存存在），否则为null
     */
    public Object getSerializable(@NonNull final String key) {
        return getSerializable(key, null);
    }

    /**
     * 返回缓存中的 serializable
     *
     * @param key          key
     * @param defaultValue 如果缓存不存在，则为默认值。
     * @return serializable（如果缓存存在）或默认值（否则）
     */
    public Object getSerializable(@NonNull final String key, final Object defaultValue) {
        Object obj = mCacheMemoryUtils.get(key);
        if (obj != null) {
            return obj;
        }
        Object serializable = mCacheDiskUtils.getSerializable(key);
        if (serializable != null) {
            mCacheMemoryUtils.put(key, serializable);
            return serializable;
        }
        return defaultValue;
    }

    /**
     * 返回磁盘中缓存的大小。
     *
     * @return 返回磁盘中缓存的大小。
     */
    public long getCacheDiskSize() {
        return mCacheDiskUtils.getCacheSize();
    }

    /**
     * 返回磁盘中缓存的数量
     *
     * @return 返回磁盘中缓存的数量
     */
    public int getCacheDiskCount() {
        return mCacheDiskUtils.getCacheCount();
    }

    /**
     * 返回内存中缓存的数量
     *
     * @return 返回内存中缓存的数量
     */
    public int getCacheMemoryCount() {
        return mCacheMemoryUtils.getCacheCount();
    }

    /**
     * 删除缓存中指定的key
     *
     * @param key key
     */
    public void remove(@NonNull String key) {
        mCacheMemoryUtils.remove(key);
        mCacheDiskUtils.remove(key);
    }

    /**
     * 清空缓存
     */
    public void clear() {
        mCacheMemoryUtils.clear();
        mCacheDiskUtils.clear();
    }
}
