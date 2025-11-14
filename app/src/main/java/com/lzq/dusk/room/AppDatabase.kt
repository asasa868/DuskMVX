package com.lzq.dusk.room

import androidx.room.Database
import androidx.room.TypeConverters
import com.lzq.dawn.room.DawnRoomDatabase

/**
 * @projectName com.lzq.dusk.room
 * @author Lzq
 * @date : Created by Lzq on 2024/6/26 09:37
 * @version
 * @description: 数据库
 * 继承 DawnRoomDatabase，以便后续在框架侧统一扩展数据库通用行为。
 */
@Database(
    entities = [BannerBean::class, HarmonyBean::class, Category::class, Article::class],
    version = 3,
    exportSchema = true
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : DawnRoomDatabase() {

    abstract fun bannerDao(): BannerDao

    abstract fun harmonyDao(): HarmonyDao

    abstract fun categoryDao(): CategoryDao

    abstract fun articleDao(): ArticleDao


}