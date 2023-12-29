package com.lzq.dawn.util.cache;

/**
 * @Name :CacheConstants
 * @Time :2022/7/18 14:09
 * @Author :  Lzq
 * @Desc : 缓存常量
 */
public interface CacheConstants {
    /**
     * 秒
     */
    int SEC = 1;
    /**
     * 分
     */
    int MIN = 60;
    /**
     * 时
     */
    int HOUR = 3600;
    /**
     * 天
     */
    int DAY = 86400;
    /**
     * 缓存前缀
     */
    String CACHE_PREFIX = "cdu_";

    /**
     * 磁盘默认缓存大小
     */
    long DEFAULT_MAX_SIZE = Long.MAX_VALUE;
    int DISK_DEFAULT_MAX_COUNT = Integer.MAX_VALUE;
    /**
     * 缓存类型
     */
    String TYPE_BYTE = "by_";
    String TYPE_STRING = "st_";
    String TYPE_JSON_OBJECT = "jo_";
    String TYPE_JSON_ARRAY = "ja_";
    String TYPE_BITMAP = "bi_";
    String TYPE_DRAWABLE = "dr_";
    String TYPE_PARCELABLE = "pa_";
    String TYPE_SERIALIZABLE = "se_";
    /**
     * 内存默认缓存大小
     */
    int MEMORY_DEFAULT_MAX_COUNT = 256;
}
