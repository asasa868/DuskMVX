package com.lzq.dawn.util.cache.memory

/**
 * @Name :CacheValue
 * @Time :2022/7/18 16:01
 * @Author :  Lzq
 * @Desc : 内存缓存bean
 */
class CacheValue internal constructor(@JvmField var dueTime: Long, @JvmField var value: Any)