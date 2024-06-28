package com.lzq.dusk.room

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.lzq.dawn.util.gson.GsonUtils

/**
 * @projectName com.lzq.dusk.room
 * @author Lzq
 * @date : Created by Lzq on 2024/6/26 17:26
 * @version
 * @description: room类型转换
 */
object RoomConverters {


    @TypeConverter
    @JvmStatic
    fun formArticle(article: List<Article>): String {
        return GsonUtils.toJson(article)
    }

    @TypeConverter
    @JvmStatic
    fun toArticle(article: String): List<Article> {
        return GsonUtils.fromJson(article, object : TypeToken<List<Article>>() {}.type)
    }

    @TypeConverter
    @JvmStatic
    fun formCategory(category: Category): String {
        return GsonUtils.toJson(category)
    }

    @TypeConverter
    @JvmStatic
    fun toCategory(category: String): Category {
        return GsonUtils.fromJson(category, Category::class.java)
    }

    @TypeConverter
    @JvmStatic
    fun formListString(list: List<String>): String {
        return GsonUtils.toJson(list)
    }

    @TypeConverter
    @JvmStatic
    fun toListString(list: String): List<String> {
        return GsonUtils.fromJson(list, object : TypeToken<List<String>>() {}.type)
    }


}