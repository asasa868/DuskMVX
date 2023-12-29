package com.lzq.dawn.util.image;

/**
 * @Name :ImageType
 * @Time :2022/8/1 15:49
 * @Author :  Lzq
 * @Desc :  图片格式
 */
public enum ImageType {

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

    TYPE_UNKNOWN("unknown");

    final String value;

    ImageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
