package com.lzq.dawn.util.cache.disk

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.Log
import com.lzq.dawn.DawnBridge
import com.lzq.dawn.util.cache.CacheConstants
import com.lzq.dawn.util.cache.disk.DiskCacheHelper.getDataWithoutDueTime
import com.lzq.dawn.util.cache.disk.DiskCacheHelper.isDue
import com.lzq.dawn.util.cache.disk.DiskCacheHelper.newByteArrayWithTime
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.Serializable

/**
 * @Name :CacheDiskUtils
 * @Time :2022/7/18 14:10
 * @Author :  Lzq
 * @Desc : 磁盘缓存
 */
class CacheDiskUtils private constructor(
    private val mCacheKey: String,
    private val mCacheDir: File,
    private val mMaxSize: Long,
    private val mMaxCount: Int
) {
    private var mDiskCacheManager: DiskCacheManager? = null
    private val diskCacheManager: DiskCacheManager?
        get() {
            if (mCacheDir.exists()) {
                if (mDiskCacheManager == null) {
                    mDiskCacheManager = DiskCacheManager(mCacheDir, mMaxSize, mMaxCount)
                }
            } else {
                if (mCacheDir.mkdirs()) {
                    mDiskCacheManager = DiskCacheManager(mCacheDir, mMaxSize, mMaxCount)
                } else {
                    Log.e("CacheDiskUtils", "无法创建目录" + mCacheDir.absolutePath)
                }
            }
            return mDiskCacheManager
        }
    /**
     * 将字节放入缓存中。
     *
     * @param key      缓存的key
     * @param value    缓存的value
     * @param saveTime 缓存的保存时间，以秒为单位
     */
    @JvmOverloads
    fun put(key: String, value: ByteArray, saveTime: Int = -1) {
        realPutBytes(CacheConstants.TYPE_BYTE + key, value, saveTime)
    }

    /**
     * 返回缓存中的字节。
     *
     * @param key 缓存的key
     * @return 如果缓存存在则为字节，否则为 null
     */
    fun getBytes(key: String): ByteArray? {
        return getBytes(key, null)
    }

    /**
     * 返回缓存中的字节。
     *
     * @param key          缓存的key
     * @param defaultValue 缓存不存在时的默认值。
     * @return 如果缓存存在则为字节，否则为默认值
     */
    fun getBytes(key: String, defaultValue: ByteArray?): ByteArray? {
        return realGetBytes(CacheConstants.TYPE_BYTE + key, defaultValue)
    }
    /**
     * 将字符串放入缓存中。
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    @JvmOverloads
    fun put(key: String, value: String?, saveTime: Int = -1) {
        realPutBytes(CacheConstants.TYPE_STRING + key, DawnBridge.string2Bytes(value), saveTime)
    }

    /**
     * 返回缓存中的字符串值。
     *
     * @param key key
     * @return 如果缓存存在则为字符串，否则为 null
     */
    fun getString(key: String): String? {
        return getString(key, null)
    }

    /**
     * 返回缓存中的字符串值。
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return 如果缓存存在则为字符串，否则为默认值
     */
    fun getString(key: String, defaultValue: String?): String? {
        val bytes = realGetBytes(CacheConstants.TYPE_STRING + key) ?: return defaultValue
        return DawnBridge.bytes2String(bytes)
    }
    /**
     * 将JSONObject放入缓存中。
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    @JvmOverloads
    fun put(
        key: String, value: JSONObject?, saveTime: Int = -1
    ) {
        realPutBytes(CacheConstants.TYPE_JSON_OBJECT + key, DawnBridge.jsonObject2Bytes(value), saveTime)
    }

    /**
     * 返回缓存中的JSONObject。
     *
     * @param key key
     * @return 如果缓存存在则为 JSONObject，否则为 null
     */
    fun getJSONObject(key: String): JSONObject? {
        return getJSONObject(key, null)
    }

    /**
     * 返回缓存中的JSONObject。
     *
     * @param key          key
     * @param defaultValue 缓存不存在时的默认值。
     * @return 如果缓存存在则为 JSONObject，否则为 null
     */
    fun getJSONObject(key: String, defaultValue: JSONObject?): JSONObject? {
        val bytes = realGetBytes(CacheConstants.TYPE_JSON_OBJECT + key) ?: return defaultValue
        return DawnBridge.bytes2JSONObject(bytes)
    }
    /**
     * 将 JSONArray 放入缓存中。
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    @JvmOverloads
    fun put(key: String, value: JSONArray?, saveTime: Int = -1) {
        realPutBytes(CacheConstants.TYPE_JSON_ARRAY + key, DawnBridge.jsonArray2Bytes(value), saveTime)
    }

    /**
     * 返回缓存中的 JSONArray。
     *
     * @param key key
     * @return 如果缓存存在，则返回JSONArray，否则返回null
     */
    fun getJSONArray(key: String): JSONArray? {
        return getJSONArray(key, null)
    }

    /**
     * 返回缓存中的 JSONArray。
     *
     * @param key          key
     * @param defaultValue 缓存不存在时的默认值。
     * @return 如果缓存存在，则返回JSONArray，否则返回默认值
     */
    fun getJSONArray(key: String, defaultValue: JSONArray?): JSONArray? {
        val bytes = realGetBytes(CacheConstants.TYPE_JSON_ARRAY + key) ?: return defaultValue
        return DawnBridge.bytes2JSONArray(bytes)
    }
    /**
     * 将Bitmap放入缓存。
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    @JvmOverloads
    fun put(key: String, value: Bitmap?, saveTime: Int = -1) {
        realPutBytes(CacheConstants.TYPE_BITMAP + key, DawnBridge.bitmap2Bytes(value), saveTime)
    }

    /**
     * 返回缓存中的Bitmap。
     *
     * @param key key
     * @return Bitmap（如果缓存存在），否则为null
     */
    fun getBitmap(key: String): Bitmap? {
        return getBitmap(key, null)
    }

    /**
     * 返回缓存中的Bitmap。
     *
     * @param key          key
     * @param defaultValue 缓存不存在时的默认值。
     * @return 如果缓存存在，则返回Bitmap，否则返回默认值
     */
    fun getBitmap(key: String, defaultValue: Bitmap?): Bitmap? {
        val bytes = realGetBytes(CacheConstants.TYPE_BITMAP + key) ?: return defaultValue
        return DawnBridge.bytes2Bitmap(bytes)
    }
    /**
     * 将drawable放入缓存。
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    @JvmOverloads
    fun put(key: String, value: Drawable?, saveTime: Int = -1) {
        realPutBytes(CacheConstants.TYPE_DRAWABLE + key, DawnBridge.drawable2Bytes(value), saveTime)
    }

    /**
     * 返回缓存中的drawable。
     *
     * @param key key
     * @return 如果缓存存在，则为Drawable，否则为null
     */
    fun getDrawable(key: String): Drawable? {
        return getDrawable(key, null)
    }

    /**
     * 返回缓存中的drawable。
     *
     * @param key          key
     * @param defaultValue 缓存不存在时的默认值。
     * @return 如果缓存存在，则为Drawable，否则为默认值
     */
    fun getDrawable(key: String, defaultValue: Drawable?): Drawable? {
        val bytes = realGetBytes(CacheConstants.TYPE_DRAWABLE + key) ?: return defaultValue
        return DawnBridge.bytes2Drawable(bytes)
    }
    /**
     * 将Parcelable放入缓存。
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    @JvmOverloads
    fun put(key: String, value: Parcelable?, saveTime: Int = -1) {
        realPutBytes(CacheConstants.TYPE_PARCELABLE + key, DawnBridge.parcelable2Bytes(value), saveTime)
    }

    /**
     * 返回缓存中的Parcelable。
     *
     * @param key     key
     * @param creator 解析Parcelable的对象.
     * @param <T>     值类型.
     * @return 如果缓存存在，则为Parcelable，否则为null
    </T> */
    fun <T> getParcelable(
        key: String, creator: Parcelable.Creator<T?>
    ): T? {
        return getParcelable(key, creator, null)
    }

    /**
     * 返回缓存中的Parcelable。
     *
     * @param key          key
     * @param creator      解析Parcelable的对象.
     * @param defaultValue 缓存不存在时的默认值。
     * @param <T>          值类型.
     * @return 如果缓存存在，则为Parcelable，否则为默认值
    </T> */
    fun <T> getParcelable(
        key: String, creator: Parcelable.Creator<T?>, defaultValue: T
    ): T? {
        val bytes = realGetBytes(CacheConstants.TYPE_PARCELABLE + key) ?: return defaultValue
        return DawnBridge.bytes2Parcelable(bytes, creator)
    }
    /**
     * 将Serializable放入缓存。
     *
     * @param key      key
     * @param value    value
     * @param saveTime 缓存的保存时间，以秒为单位。
     */
    @JvmOverloads
    fun put(key: String, value: Serializable?, saveTime: Int = -1) {
        realPutBytes(CacheConstants.TYPE_SERIALIZABLE + key, DawnBridge.serializable2Bytes(value), saveTime)
    }

    /**
     * 返回缓存中的serializable。
     *
     * @param key key
     * @return 如果缓存存在，则为serializable，否则为null
     */
    fun getSerializable(key: String): Any? {
        return getSerializable(key, null)
    }

    /**
     * 返回缓存中的serializable。
     *
     * @param key          key
     * @param defaultValue 缓存不存在时的默认值。
     * @return 如果缓存存在，则为serializable，否则为默认值
     */
    fun getSerializable(key: String, defaultValue: Any?): Any? {
        val bytes = realGetBytes(CacheConstants.TYPE_SERIALIZABLE + key) ?: return defaultValue
        return DawnBridge.bytes2Object(bytes)
    }

    val cacheSize: Long
        /**
         * 返回缓存的大小，以字节为单位。
         *
         * @return 返回缓存的大小，以字节为单位。
         */
        get() {
            val diskCacheManager = diskCacheManager ?: return 0
            return diskCacheManager.getCacheSize()
        }
    val cacheCount: Int
        /**
         * 返回缓存数。
         *
         * @return 缓存数
         */
        get() {
            val diskCacheManager = diskCacheManager ?: return 0
            return diskCacheManager.getCacheCount()
        }

    /**
     * 按key删除缓存。
     *
     * @param key 缓存的key。
     * @return `true`: success<br></br>`false`: fail
     */
    fun remove(key: String): Boolean {
        val diskCacheManager = diskCacheManager ?: return true
        return (diskCacheManager.removeByKey(CacheConstants.TYPE_BYTE + key) && diskCacheManager.removeByKey(
            CacheConstants.TYPE_STRING + key
        ) && diskCacheManager.removeByKey(CacheConstants.TYPE_JSON_OBJECT + key) && diskCacheManager.removeByKey(
            CacheConstants.TYPE_JSON_ARRAY + key
        ) && diskCacheManager.removeByKey(CacheConstants.TYPE_BITMAP + key) && diskCacheManager.removeByKey(
            CacheConstants.TYPE_DRAWABLE + key
        ) && diskCacheManager.removeByKey(CacheConstants.TYPE_PARCELABLE + key) && diskCacheManager.removeByKey(
            CacheConstants.TYPE_SERIALIZABLE + key
        ))
    }

    /**
     * 清除所有缓存。
     *
     * @return `true`: success<br></br>`false`: fail
     */
    fun clear(): Boolean {
        val diskCacheManager = diskCacheManager ?: return true
        return diskCacheManager.clear()
    }

    /**
     * 把要缓存的内容转换成字节存入缓存文件
     *
     * @param key      key
     * @param byteValue    value
     * @param saveTime 缓存时间
     */
    private fun realPutBytes(key: String, byteValue: ByteArray?, saveTime: Int) {
        var value = byteValue
        val diskCacheManager = diskCacheManager ?: return
        if (saveTime >= 0) {
            value = newByteArrayWithTime(saveTime, value)
        }
        val file = diskCacheManager.getFileBeforePut(key)
        DawnBridge.writeFileFromBytes(file, value)
        diskCacheManager.updateModify(file)
        diskCacheManager.put(file)
    }
    /**
     * 读取缓存文件指定key的内容
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return value
     */
    private fun realGetBytes(key: String, defaultValue: ByteArray? = null): ByteArray? {
        val diskCacheManager = diskCacheManager ?: return defaultValue
        val file = diskCacheManager.getFileIfExists(key) ?: return defaultValue
        val data = DawnBridge.readFile2Bytes(file)
        if (isDue(data)) {
            diskCacheManager.removeByKey(key)
            return defaultValue
        }
        diskCacheManager.updateModify(file)
        return getDataWithoutDueTime(data)
    }

    override fun toString(): String {
        return mCacheKey + "@" + Integer.toHexString(hashCode())
    }

    companion object {
        /**
         * 缓存对象的map
         */
        private val CACHE_MAP: MutableMap<String, CacheDiskUtils> = HashMap()
        @JvmStatic
        val instance: CacheDiskUtils?
            /**
             * 返回单个 [CacheDiskUtils] 实例
             *
             * cache directory: /data/data/package/cache/cacheUtils
             *
             * cache size: unlimited
             *
             * cache count: unlimited
             *
             * @return 返回单个 [CacheDiskUtils] 实例
             */
            get() = getInstance("", CacheConstants.DEFAULT_MAX_SIZE, CacheConstants.DISK_DEFAULT_MAX_COUNT)

        /**
         * 返回单个 [CacheDiskUtils] 实例
         *
         * cache directory: /data/data/package/cache/cacheUtils
         *
         * cache size: unlimited
         *
         * cache count: unlimited
         *
         * @param cacheName 缓存名称.
         * @return 返回单个 [CacheDiskUtils] 实例
         */
        fun getInstance(cacheName: String?): CacheDiskUtils? {
            return getInstance(
                cacheName,
                CacheConstants.DEFAULT_MAX_SIZE,
                CacheConstants.DISK_DEFAULT_MAX_COUNT
            )
        }

        /**
         * 返回单个 [CacheDiskUtils] 实例
         *
         * cache directory: /data/data/package/cache/cacheUtils
         *
         * @param maxSize  缓存的最大大小，以字节为单位.
         * @param maxCount 最大缓存数.
         * @return 返回单个 [CacheDiskUtils] 实例
         */
        fun getInstance(maxSize: Long, maxCount: Int): CacheDiskUtils? {
            return getInstance("", maxSize, maxCount)
        }

        /**
         * 返回单个 [CacheDiskUtils] 实例
         *
         * cache directory: /data/data/package/cache/cacheName
         *
         * @param cacheNameStr 缓存名称.
         * @param maxSize   缓存的最大大小，以字节为单位.
         * @param maxCount  最大缓存数.
         * @return 返回单个 [CacheDiskUtils] 实例
         */
        fun getInstance(cacheNameStr: String?, maxSize: Long, maxCount: Int): CacheDiskUtils? {
            var cacheName = cacheNameStr
            if (DawnBridge.isSpace(cacheName)) {
                cacheName = "cacheUtils"
            }
            val file = File(DawnBridge.app.cacheDir, cacheName!!)
            return getInstance(file, maxSize, maxCount)
        }

        /**
         * 返回单个 [CacheDiskUtils] 实例
         *
         * cache size: unlimited
         *
         * cache count: unlimited
         *
         * @param cacheDir 缓存目录。
         * @return 返回单个 [CacheDiskUtils] 实例
         */
        fun getInstance(cacheDir: File): CacheDiskUtils? {
            return getInstance(
                cacheDir,
                CacheConstants.DEFAULT_MAX_SIZE,
                CacheConstants.DISK_DEFAULT_MAX_COUNT
            )
        }

        /**
         * 返回单个 [CacheDiskUtils] 实例
         *
         * @param cacheDir 缓存目录。
         * @param maxSize  缓存的最大大小，以字节为单位.
         * @param maxCount 最大缓存数.
         * @return 返回单个 [CacheDiskUtils] 实例
         */
        fun getInstance(
            cacheDir: File, maxSize: Long, maxCount: Int
        ): CacheDiskUtils? {
            val cacheKey = cacheDir.absoluteFile.toString() + "_" + maxSize + "_" + maxCount
            var cache = CACHE_MAP[cacheKey]
            if (cache == null) {
                synchronized(CacheDiskUtils::class.java) {
                    cache = CACHE_MAP[cacheKey]
                    if (cache == null) {
                        cache = CacheDiskUtils(cacheKey, cacheDir, maxSize, maxCount)
                        CACHE_MAP[cacheKey] = cache!!
                    }
                }
            }
            return cache
        }
    }
}