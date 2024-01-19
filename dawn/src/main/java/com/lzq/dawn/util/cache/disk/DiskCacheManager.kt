package com.lzq.dawn.util.cache.disk

import com.lzq.dawn.util.cache.CacheConstants
import java.io.File
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * @Name :DiskCacheManager
 * @Time :2022/7/18 14:25
 * @Author :  Lzq
 * @Desc : 磁盘缓存管理器
 */
class DiskCacheManager internal constructor(
    private val cacheDir: File,
    private val sizeLimit: Long,
    private val countLimit: Int
) {
    private val cacheSize: AtomicLong = AtomicLong()
    private val cacheCount: AtomicInteger = AtomicInteger()
    private val lastUsageDates = Collections.synchronizedMap(HashMap<File, Long>())
    private val mThread: Thread = Thread {
        var size = 0
        var count = 0
        val cachedFiles =
            cacheDir.listFiles { _, name -> return@listFiles name.startsWith(CacheConstants.CACHE_PREFIX) }
        if (cachedFiles != null) {
            for (cachedFile in cachedFiles) {
                size += cachedFile.length().toInt()
                count += 1
                lastUsageDates[cachedFile] = cachedFile.lastModified()
            }
            cacheSize.getAndAdd(size.toLong())
            cacheCount.getAndAdd(count)
        }
    }

    init {
        mThread.start()
    }

    fun getCacheSize(): Long {
        wait2InitOk()
        return cacheSize.get()
    }

    fun getCacheCount(): Int {
        wait2InitOk()
        return cacheCount.get()
    }

    fun getFileBeforePut(key: String): File {
        wait2InitOk()
        val file = File(cacheDir, getCacheNameByKey(key))
        if (file.exists()) {
            cacheCount.addAndGet(-1)
            cacheSize.addAndGet(-file.length())
        }
        return file
    }

    private fun wait2InitOk() {
        try {
            mThread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun getFileIfExists(key: String): File? {
        val file = File(cacheDir, getCacheNameByKey(key))
        return if (!file.exists()) {
            null
        } else file
    }

    private fun getCacheNameByKey(key: String): String {
        return CacheConstants.CACHE_PREFIX + key.substring(0, 3) + key.substring(3).hashCode()
    }

    fun put(file: File) {
        cacheCount.addAndGet(1)
        cacheSize.addAndGet(file.length())
        while (cacheCount.get() > countLimit || cacheSize.get() > sizeLimit) {
            cacheSize.addAndGet(-removeOldest())
            cacheCount.addAndGet(-1)
        }
    }

    fun updateModify(file: File) {
        val millis = System.currentTimeMillis()
        file.setLastModified(millis)
        lastUsageDates[file] = millis
    }

    fun removeByKey(key: String): Boolean {
        val file = getFileIfExists(key) ?: return true
        if (!file.delete()) {
            return false
        }
        cacheSize.addAndGet(-file.length())
        cacheCount.addAndGet(-1)
        lastUsageDates.remove(file)
        return true
    }

    fun clear(): Boolean {
        val files = cacheDir.listFiles { _, name -> name.startsWith(CacheConstants.CACHE_PREFIX) }
        if (files == null || files.isEmpty()) {
            return true
        }
        var flag = true
        for (file in files) {
            if (!file.delete()) {
                flag = false
                continue
            }
            cacheSize.addAndGet(-file.length())
            cacheCount.addAndGet(-1)
            lastUsageDates.remove(file)
        }
        if (flag) {
            lastUsageDates.clear()
            cacheSize.set(0)
            cacheCount.set(0)
        }
        return flag
    }

    /**
     * 删除最旧的文件。
     *
     * @return 最旧文件的大小，以字节为单位
     */
    private fun removeOldest(): Long {
        if (lastUsageDates.isEmpty()) {
            return 0
        }
        var oldestUsage = Long.MAX_VALUE
        var oldestFile: File? = null
        val entries: Set<Map.Entry<File, Long>> = lastUsageDates.entries
        synchronized(lastUsageDates) {
            for ((key, lastValueUsage) in entries) {
                if (lastValueUsage < oldestUsage) {
                    oldestUsage = lastValueUsage
                    oldestFile = key
                }
            }
        }
        if (oldestFile == null) {
            return 0
        }
        val fileSize = oldestFile!!.length()
        if (oldestFile!!.delete()) {
            lastUsageDates.remove(oldestFile)
            return fileSize
        }
        return 0
    }
}