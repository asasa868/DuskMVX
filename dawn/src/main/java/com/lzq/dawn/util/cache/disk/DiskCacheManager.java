package com.lzq.dawn.util.cache.disk;

import com.lzq.dawn.util.cache.CacheConstants;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Name :DiskCacheManager
 * @Time :2022/7/18 14:25
 * @Author :  Lzq
 * @Desc : 磁盘缓存管理器
 */
public final class DiskCacheManager {

    private final AtomicLong cacheSize;
    private final AtomicInteger cacheCount;
    private final long sizeLimit;
    private final int countLimit;
    private final Map<File, Long> lastUsageDates
            = Collections.synchronizedMap(new HashMap<File, Long>());
    private final File cacheDir;
    private final Thread mThread;

    DiskCacheManager(final File cacheDir, final long sizeLimit, final int countLimit) {
        this.cacheDir = cacheDir;
        this.sizeLimit = sizeLimit;
        this.countLimit = countLimit;
        cacheSize = new AtomicLong();
        cacheCount = new AtomicInteger();
        mThread = new Thread(() -> {
            int size = 0;
            int count = 0;
            final File[] cachedFiles = cacheDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith(CacheConstants.CACHE_PREFIX);
                }
            });
            if (cachedFiles != null) {
                for (File cachedFile : cachedFiles) {
                    size += cachedFile.length();
                    count += 1;
                    lastUsageDates.put(cachedFile, cachedFile.lastModified());
                }
                cacheSize.getAndAdd(size);
                cacheCount.getAndAdd(count);
            }
        });
        mThread.start();
    }

    long getCacheSize() {
        wait2InitOk();
        return cacheSize.get();
    }

    int getCacheCount() {
        wait2InitOk();
        return cacheCount.get();
    }

    File getFileBeforePut(final String key) {
        wait2InitOk();
        File file = new File(cacheDir, getCacheNameByKey(key));
        if (file.exists()) {
            cacheCount.addAndGet(-1);
            cacheSize.addAndGet(-file.length());
        }
        return file;
    }

    private void wait2InitOk() {
        try {
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    File getFileIfExists(final String key) {
        File file = new File(cacheDir, getCacheNameByKey(key));
        if (!file.exists()) {
            return null;
        }
        return file;
    }

    private String getCacheNameByKey(final String key) {
        return CacheConstants.CACHE_PREFIX + key.substring(0, 3) + key.substring(3).hashCode();
    }

    void put(final File file) {
        cacheCount.addAndGet(1);
        cacheSize.addAndGet(file.length());
        while (cacheCount.get() > countLimit || cacheSize.get() > sizeLimit) {
            cacheSize.addAndGet(-removeOldest());
            cacheCount.addAndGet(-1);
        }
    }

    void updateModify(final File file) {
        long millis = System.currentTimeMillis();
        file.setLastModified(millis);
        lastUsageDates.put(file, millis);
    }

    boolean removeByKey(final String key) {
        File file = getFileIfExists(key);
        if (file == null) {
            return true;
        }
        if (!file.delete()) {
            return false;
        }
        cacheSize.addAndGet(-file.length());
        cacheCount.addAndGet(-1);
        lastUsageDates.remove(file);
        return true;
    }

     boolean clear() {
        File[] files = cacheDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(CacheConstants.CACHE_PREFIX);
            }
        });
        if (files == null || files.length <= 0) {
            return true;
        }
        boolean flag = true;
        for (File file : files) {
            if (!file.delete()) {
                flag = false;
                continue;
            }
            cacheSize.addAndGet(-file.length());
            cacheCount.addAndGet(-1);
            lastUsageDates.remove(file);
        }
        if (flag) {
            lastUsageDates.clear();
            cacheSize.set(0);
            cacheCount.set(0);
        }
        return flag;
    }

    /**
     * 删除最旧的文件。
     *
     * @return 最旧文件的大小，以字节为单位
     */
    private long removeOldest() {
        if (lastUsageDates.isEmpty()) {
            return 0;
        }
        Long oldestUsage = Long.MAX_VALUE;
        File oldestFile = null;
        Set<Map.Entry<File, Long>> entries = lastUsageDates.entrySet();
        synchronized (lastUsageDates) {
            for (Map.Entry<File, Long> entry : entries) {
                Long lastValueUsage = entry.getValue();
                if (lastValueUsage < oldestUsage) {
                    oldestUsage = lastValueUsage;
                    oldestFile = entry.getKey();
                }
            }
        }
        if (oldestFile == null) {
            return 0;
        }
        long fileSize = oldestFile.length();
        if (oldestFile.delete()) {
            lastUsageDates.remove(oldestFile);
            return fileSize;
        }
        return 0;
    }
}
