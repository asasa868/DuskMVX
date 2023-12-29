package com.lzq.dawn.util.cache.memory;

/**
 * @Name :CacheValue
 * @Time :2022/7/18 16:01
 * @Author :  Lzq
 * @Desc : 内存缓存bean
 */
public final class CacheValue {
    long dueTime;
    Object value;

    CacheValue(long dueTime, Object value) {
        this.dueTime = dueTime;
        this.value = value;
    }

}
