package com.lzq.dawn.util.image

/**
 * @Name :ImageType
 * @Time :2022/8/1 15:49
 * @Author :  Lzq
 * @Desc :  图片格式
 */
enum class ImageType(val value: String) {
    /**
     * 图片格式
     */
    TYPE_JPG("jpg"),
    TYPE_PNG("png"),
    TYPE_GIF("gif"),
    TYPE_TIFF("tiff"),
    TYPE_BMP("bmp"),
    TYPE_WEBP("webp"),
    TYPE_ICO("ico"),
    TYPE_UNKNOWN("unknown")

}