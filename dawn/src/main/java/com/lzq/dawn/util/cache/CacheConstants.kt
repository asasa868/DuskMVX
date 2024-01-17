package com.lzq.dawn.util.cache

/**
 * @Name :CacheConstants
 * @Time :2022/7/18 14:09
 * @Author :  Lzq
 * @Desc : 缓存常量
 */
interface CacheConstants {
    companion object {
        /**
         * 秒
         */
        const val SEC = 1

        /**
         * 分
         */
        const val MIN = 60

        /**
         * 时
         */
        const val HOUR = 3600

        /**
         * 天
         */
        const val DAY = 86400

        /**
         * 缓存前缀
         */
        const val CACHE_PREFIX = "cdu_"

        /**
         * 磁盘默认缓存大小
         */
        const val DEFAULT_MAX_SIZE = Long.MAX_VALUE
        const val DISK_DEFAULT_MAX_COUNT = Int.MAX_VALUE

        /**
         * 缓存类型
         */
        const val TYPE_BYTE = "by_"
        const val TYPE_STRING = "st_"
        const val TYPE_JSON_OBJECT = "jo_"
        const val TYPE_JSON_ARRAY = "ja_"
        const val TYPE_BITMAP = "bi_"
        const val TYPE_DRAWABLE = "dr_"
        const val TYPE_PARCELABLE = "pa_"
        const val TYPE_SERIALIZABLE = "se_"

        /**
         * 内存默认缓存大小
         */
        const val MEMORY_DEFAULT_MAX_COUNT = 256
    }
}