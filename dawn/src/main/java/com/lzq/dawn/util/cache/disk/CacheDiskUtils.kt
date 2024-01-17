package com.lzq.dawn.util.cache.disk;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.lzq.dawn.DawnBridge;
import com.lzq.dawn.util.cache.CacheConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Name :CacheDiskUtils
 * @Time :2022/7/18 14:10
 * @Author :  Lzq
 * @Desc : 磁盘缓存
 */
public final class CacheDiskUtils {

    /**
     * 缓存对象的map
     */
    private static final Map<String, CacheDiskUtils> CACHE_MAP = new HashMap<>();

    private final String mCacheKey;
    private final File mCacheDir;
    private final long mMaxSize;
    private final int mMaxCount;
    private DiskCacheManager mDiskCacheManager;

    /**
     * 返回单个 {@link CacheDiskUtils} 实例
     * <p>cache directory: /data/data/package/cache/cacheUtils</p>
     * <p>cache size: unlimited</p>
     * <p>cache count: unlimited</p>
     *
     * @return 返回单个 {@link CacheDiskUtils} 实例
     */
    public static CacheDiskUtils getInstance() {
        return getInstance("", CacheConstants.DEFAULT_MAX_SIZE, CacheConstants.DISK_DEFAULT_MAX_COUNT);
    }

    /**
     * 返回单个 {@link CacheDiskUtils} 实例
     * <p>cache directory: /data/data/package/cache/cacheUtils</p>
     * <p>cache size: unlimited</p>
     * <p>cache count: unlimited</p>
     *
     * @param cacheName 缓存名称.
     * @return 返回单个 {@link CacheDiskUtils} 实例
     */
    public static CacheDiskUtils getInstance(final String cacheName) {
        return getInstance(cacheName, CacheConstants.DEFAULT_MAX_SIZE, CacheConstants.DISK_DEFAULT_MAX_COUNT);
    }

    /**
     * 返回单个 {@link CacheDiskUtils} 实例
     * <p>cache directory: /data/data/package/cache/cacheUtils</p>
     *
     * @param maxSize  缓存的最大大小，以字节为单位.
     * @param maxCount 最大缓存数.
     * @return 返回单个 {@link CacheDiskUtils} 实例
     */
    public static CacheDiskUtils getInstance(final long maxSize, final int maxCount) {
        return getInstance("", maxSize, maxCount);
    }

    /**
     * 返回单个 {@link CacheDiskUtils} 实例
     * <p>cache directory: /data/data/package/cache/cacheName</p>
     *
     * @param cacheName 缓存名称.
     * @param maxSize   缓存的最大大小，以字节为单位.
     * @param maxCount  最大缓存数.
     * @return 返回单个 {@link CacheDiskUtils} 实例
     */
    public static CacheDiskUtils getInstance(String cacheName, final long maxSize, final int maxCount) {
        if (DawnBridge.isSpace(cacheName)) {
            cacheName = "cacheUtils";
        }
        File file = new File(DawnBridge.getApp().getCacheDir(), cacheName);
        return getInstance(file, maxSize, maxCount);
    }

    /**
     * 返回单个 {@link CacheDiskUtils} 实例
     * <p>cache size: unlimited</p>
     * <p>cache count: unlimited</p>
     *
     * @param cacheDir 缓存目录。
     * @return 返回单个 {@link CacheDiskUtils} 实例
     */
    public static CacheDiskUtils getInstance(@NonNull final File cacheDir) {
        return getInstance(cacheDir, CacheConstants.DEFAULT_MAX_SIZE, CacheConstants.DISK_DEFAULT_MAX_COUNT);
    }

    /**
     * 返回单个 {@link CacheDiskUtils} 实例
     *
     * @param cacheDir 缓存目录。
     * @param maxSize  缓存的最大大小，以字节为单位.
     * @param maxCount 最大缓存数.
     * @return 返回单个 {@link CacheDiskUtils} 实例
     */
    public static CacheDiskUtils getInstance(@NonNull final File cacheDir,
                                             final long maxSize,
                                             final int maxCount) {
        final String cacheKey = cacheDir.getAbsoluteFile() + "_" + maxSize + "_" + maxCount;
        CacheDiskUtils cache = CACHE_MAP.get(cacheKey);
        if (cache == null) {
            synchronized (CacheDiskUtils.class) {
                cache = CACHE_MAP.get(cacheKey);
                if (cache == null) {
                    cache = new CacheDiskUtils(cacheKey, cacheDir, maxSize, maxCount);
                    CACHE_MAP.put(cacheKey, cache);
                }
            }
        }
        return cache;
    }

    private CacheDiskUtils(final String cacheKey,
                           final File cacheDir,
                           final long maxSize,
                           final int maxCount) {
        mCacheKey = cacheKey;
        mCacheDir = cacheDir;
        mMaxSize = maxSize;
        mMaxCount = maxCount;
    }

    private DiskCacheManager getDiskCacheManager() {
        if (mCacheDir.exists()) {
            if (mDiskCacheManager == null) {
                mDiskCacheManager = new DiskCacheManager(mCacheDir, mMaxSize, mMaxCount);
            }
        } else {
            if (mCacheDir.mkdirs()) {
                mDiskCacheManager = new DiskCacheManager(mCacheDir, mMaxSize, mMaxCount);
            } else {
                Log.e("CacheDiskUtils", "无法创建目录" + mCacheDir.getAbsolutePath());
            }
        }
        return mDiskCacheManager;
    }

    /**
     * 将字节放入缓存中。
     *
     * @param key   缓存的key
     * @param value 缓存的value
     */
    public void put(@NonNull final String key, final byte[] value) {
        put(key, value, -1);
    }

    /**
     * 将字节放入缓存中。
     *
     * @param key      缓存的key
     * @param value    缓存的value
     * @param saveTime 缓存的保存时间，以秒为单位
     */
    public void put(@NonNull final String key, final byte[] value, final int saveTime) {
        realPutBytes(CacheConstants.TYPE_BYTE + key, value, saveTime);
    }

    /**
     * 返回缓存中的字节。
     *
     * @param key 缓存的key
     * @return 如果缓存存在则为字节，否则为 null
     */
    public byte[] getBytes(@NonNull final String key) {
        return getBytes(key, null);
    }

    /**
     * 返回缓存中的字节。
     *
     * @param key          缓存的key
     * @param defaultValue 缓存不存在时的默认值。
     * @return 如果缓存存在则为字节，否则为默认值
     */
    public byte[] getBytes(@NonNull final String key, final byte[] defaultValue) {
        return realGetBytes(CacheConstants.TYPE_BYTE + key, defaultValue);
    }


    /**
     * 将字符串放入缓存中。
     *
     * @param key   key
     * @param value value
     */
    public void put(@NonNull final String key, final String value) {
        put(key, value, -1);
    }

    /**
     * 将字符串放入缓存中。
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public void put(@NonNull final String key, final String value, final int saveTime) {
        realPutBytes(CacheConstants.TYPE_STRING + key, DawnBridge.string2Bytes(value), saveTime);
    }

    /**
     * 返回缓存中的字符串值。
     *
     * @param key key
     * @return 如果缓存存在则为字符串，否则为 null
     */
    public String getString(@NonNull final String key) {
        return getString(key, null);
    }

    /**
     * 返回缓存中的字符串值。
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return 如果缓存存在则为字符串，否则为默认值
     */
    public String getString(@NonNull final String key, final String defaultValue) {
        byte[] bytes = realGetBytes(CacheConstants.TYPE_STRING + key);
        if (bytes == null) {
            return defaultValue;
        }
        return DawnBridge.bytes2String(bytes);
    }

    /**
     * 将JSONObject放入缓存中。
     *
     * @param key   key
     * @param value value
     */
    public void put(@NonNull final String key, final JSONObject value) {
        put(key, value, -1);
    }

    /**
     * 将JSONObject放入缓存中。
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public void put(@NonNull final String key,
                    final JSONObject value,
                    final int saveTime) {
        realPutBytes(CacheConstants.TYPE_JSON_OBJECT + key, DawnBridge.jsonObject2Bytes(value), saveTime);
    }

    /**
     * 返回缓存中的JSONObject。
     *
     * @param key key
     * @return 如果缓存存在则为 JSONObject，否则为 null
     */
    public JSONObject getJSONObject(@NonNull final String key) {
        return getJSONObject(key, null);
    }

    /**
     * 返回缓存中的JSONObject。
     *
     * @param key          key
     * @param defaultValue 缓存不存在时的默认值。
     * @return 如果缓存存在则为 JSONObject，否则为 null
     */
    public JSONObject getJSONObject(@NonNull final String key, final JSONObject defaultValue) {
        byte[] bytes = realGetBytes(CacheConstants.TYPE_JSON_OBJECT + key);
        if (bytes == null) {
            return defaultValue;
        }
        return DawnBridge.bytes2JSONObject(bytes);
    }

    /**
     * 将 JSONArray 放入缓存中。
     *
     * @param key   key
     * @param value value
     */
    public void put(@NonNull final String key, final JSONArray value) {
        put(key, value, -1);
    }

    /**
     * 将 JSONArray 放入缓存中。
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public void put(@NonNull final String key, final JSONArray value, final int saveTime) {
        realPutBytes(CacheConstants.TYPE_JSON_ARRAY + key, DawnBridge.jsonArray2Bytes(value), saveTime);
    }

    /**
     * 返回缓存中的 JSONArray。
     *
     * @param key key
     * @return 如果缓存存在，则返回JSONArray，否则返回null
     */
    public JSONArray getJSONArray(@NonNull final String key) {
        return getJSONArray(key, null);
    }

    /**
     * 返回缓存中的 JSONArray。
     *
     * @param key          key
     * @param defaultValue 缓存不存在时的默认值。
     * @return 如果缓存存在，则返回JSONArray，否则返回默认值
     */
    public JSONArray getJSONArray(@NonNull final String key, final JSONArray defaultValue) {
        byte[] bytes = realGetBytes(CacheConstants.TYPE_JSON_ARRAY + key);
        if (bytes == null) {
            return defaultValue;
        }
        return DawnBridge.bytes2JSONArray(bytes);
    }


    /**
     * 将Bitmap放入缓存。
     *
     * @param key   key
     * @param value value
     */
    public void put(@NonNull final String key, final Bitmap value) {
        put(key, value, -1);
    }

    /**
     * 将Bitmap放入缓存。
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public void put(@NonNull final String key, final Bitmap value, final int saveTime) {
        realPutBytes(CacheConstants.TYPE_BITMAP + key, DawnBridge.bitmap2Bytes(value), saveTime);
    }

    /**
     * 返回缓存中的Bitmap。
     *
     * @param key key
     * @return Bitmap（如果缓存存在），否则为null
     */
    public Bitmap getBitmap(@NonNull final String key) {
        return getBitmap(key, null);
    }

    /**
     * 返回缓存中的Bitmap。
     *
     * @param key          key
     * @param defaultValue 缓存不存在时的默认值。
     * @return 如果缓存存在，则返回Bitmap，否则返回默认值
     */
    public Bitmap getBitmap(@NonNull final String key, final Bitmap defaultValue) {
        byte[] bytes = realGetBytes(CacheConstants.TYPE_BITMAP + key);
        if (bytes == null) {
            return defaultValue;
        }
        return DawnBridge.bytes2Bitmap(bytes);
    }


    /**
     * 将drawable放入缓存。
     *
     * @param key   key
     * @param value value
     */
    public void put(@NonNull final String key, final Drawable value) {
        put(key, value, -1);
    }

    /**
     * 将drawable放入缓存。
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public void put(@NonNull final String key, final Drawable value, final int saveTime) {
        realPutBytes(CacheConstants.TYPE_DRAWABLE + key, DawnBridge.drawable2Bytes(value), saveTime);
    }

    /**
     * 返回缓存中的drawable。
     *
     * @param key key
     * @return 如果缓存存在，则为Drawable，否则为null
     */
    public Drawable getDrawable(@NonNull final String key) {
        return getDrawable(key, null);
    }

    /**
     * 返回缓存中的drawable。
     *
     * @param key          key
     * @param defaultValue 缓存不存在时的默认值。
     * @return 如果缓存存在，则为Drawable，否则为默认值
     */
    public Drawable getDrawable(@NonNull final String key, final Drawable defaultValue) {
        byte[] bytes = realGetBytes(CacheConstants.TYPE_DRAWABLE + key);
        if (bytes == null) {
            return defaultValue;
        }
        return DawnBridge.bytes2Drawable(bytes);
    }

    /**
     * 将Parcelable放入缓存。
     *
     * @param key   key
     * @param value value
     */
    public void put(@NonNull final String key, final Parcelable value) {
        put(key, value, -1);
    }

    /**
     * 将Parcelable放入缓存。
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public void put(@NonNull final String key, final Parcelable value, final int saveTime) {
        realPutBytes(CacheConstants.TYPE_PARCELABLE + key, DawnBridge.parcelable2Bytes(value), saveTime);
    }

    /**
     * 返回缓存中的Parcelable。
     *
     * @param key     key
     * @param creator 解析Parcelable的对象.
     * @param <T>     值类型.
     * @return 如果缓存存在，则为Parcelable，否则为null
     */
    public <T> T getParcelable(@NonNull final String key,
                               @NonNull final Parcelable.Creator<T> creator) {
        return getParcelable(key, creator, null);
    }

    /**
     * 返回缓存中的Parcelable。
     *
     * @param key          key
     * @param creator      解析Parcelable的对象.
     * @param defaultValue 缓存不存在时的默认值。
     * @param <T>          值类型.
     * @return 如果缓存存在，则为Parcelable，否则为默认值
     */
    public <T> T getParcelable(@NonNull final String key,
                               @NonNull final Parcelable.Creator<T> creator,
                               final T defaultValue) {
        byte[] bytes = realGetBytes(CacheConstants.TYPE_PARCELABLE + key);
        if (bytes == null) {
            return defaultValue;
        }
        return DawnBridge.bytes2Parcelable(bytes, creator);
    }


    /**
     * 将Serializable放入缓存。
     *
     * @param key   key
     * @param value value
     */
    public void put(@NonNull final String key, final Serializable value) {
        put(key, value, -1);
    }

    /**
     * 将Serializable放入缓存。
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    public void put(@NonNull final String key, final Serializable value, final int saveTime) {
        realPutBytes(CacheConstants.TYPE_SERIALIZABLE + key, DawnBridge.serializable2Bytes(value), saveTime);
    }

    /**
     * 返回缓存中的serializable。
     *
     * @param key key
     * @return 如果缓存存在，则为serializable，否则为null
     */
    public Object getSerializable(@NonNull final String key) {
        return getSerializable(key, null);
    }

    /**
     * 返回缓存中的serializable。
     *
     * @param key          key
     * @param defaultValue 缓存不存在时的默认值。
     * @return 如果缓存存在，则为serializable，否则为默认值
     */
    public Object getSerializable(@NonNull final String key, final Object defaultValue) {
        byte[] bytes = realGetBytes(CacheConstants.TYPE_SERIALIZABLE + key);
        if (bytes == null) {
            return defaultValue;
        }
        return DawnBridge.bytes2Object(bytes);
    }


    /**
     * 返回缓存的大小，以字节为单位。
     *
     * @return 返回缓存的大小，以字节为单位。
     */
    public long getCacheSize() {
        DiskCacheManager diskCacheManager = getDiskCacheManager();
        if (diskCacheManager == null) {
            return 0;
        }
        return diskCacheManager.getCacheSize();
    }

    /**
     * 返回缓存数。
     *
     * @return 缓存数
     */
    public int getCacheCount() {
        DiskCacheManager diskCacheManager = getDiskCacheManager();
        if (diskCacheManager == null) {
            return 0;
        }
        return diskCacheManager.getCacheCount();
    }

    /**
     * 按key删除缓存。
     *
     * @param key 缓存的key。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public boolean remove(@NonNull final String key) {
        DiskCacheManager diskCacheManager = getDiskCacheManager();
        if (diskCacheManager == null) {
            return true;
        }
        return diskCacheManager.removeByKey(CacheConstants.TYPE_BYTE + key)
                && diskCacheManager.removeByKey(CacheConstants.TYPE_STRING + key)
                && diskCacheManager.removeByKey(CacheConstants.TYPE_JSON_OBJECT + key)
                && diskCacheManager.removeByKey(CacheConstants.TYPE_JSON_ARRAY + key)
                && diskCacheManager.removeByKey(CacheConstants.TYPE_BITMAP + key)
                && diskCacheManager.removeByKey(CacheConstants.TYPE_DRAWABLE + key)
                && diskCacheManager.removeByKey(CacheConstants.TYPE_PARCELABLE + key)
                && diskCacheManager.removeByKey(CacheConstants.TYPE_SERIALIZABLE + key);
    }

    /**
     * 清除所有缓存。
     *
     * @return {@code true}: success<br>{@code false}: fail
     */
    public boolean clear() {
        DiskCacheManager diskCacheManager = getDiskCacheManager();
        if (diskCacheManager == null) {
            return true;
        }
        return diskCacheManager.clear();
    }

    /**
     * 把要缓存的内容转换成字节存入缓存文件
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存时间
     */
    private void realPutBytes(final String key, byte[] value, int saveTime) {
        if (value == null) {
            return;
        }
        DiskCacheManager diskCacheManager = getDiskCacheManager();
        if (diskCacheManager == null) {
            return;
        }
        if (saveTime >= 0) {
            value = DiskCacheHelper.newByteArrayWithTime(saveTime, value);
        }
        File file = diskCacheManager.getFileBeforePut(key);
        DawnBridge.writeFileFromBytes(file, value);
        diskCacheManager.updateModify(file);
        diskCacheManager.put(file);
    }

    /**
     * 读取缓存文件指定key的内容
     *
     * @param key key
     * @return value
     */
    private byte[] realGetBytes(@NonNull final String key) {
        return realGetBytes(key, null);
    }

    /**
     * 读取缓存文件指定key的内容
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return value
     */
    private byte[] realGetBytes(@NonNull final String key, final byte[] defaultValue) {
        DiskCacheManager diskCacheManager = getDiskCacheManager();
        if (diskCacheManager == null) {
            return defaultValue;
        }
        final File file = diskCacheManager.getFileIfExists(key);
        if (file == null) {
            return defaultValue;
        }
        byte[] data = DawnBridge.readFile2Bytes(file);
        if (DiskCacheHelper.isDue(data)) {
            diskCacheManager.removeByKey(key);
            return defaultValue;
        }
        diskCacheManager.updateModify(file);
        return DiskCacheHelper.getDataWithoutDueTime(data);
    }

    @NonNull
    @Override
    public String toString() {
        return mCacheKey + "@" + Integer.toHexString(hashCode());
    }

}
