package com.lzq.dusk.room

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lzq.dawn.DawnBridge

/**
 * @projectName com.lzq.dusk.room
 * @author Lzq
 * @date : Created by Lzq on 2024/6/26 09:35
 * @version
 * @description: 数据库管理类
 */
object DatabaseManager {
    private var INSTANCE: AppDatabase? = null

    fun getInstance(): AppDatabase {
        if (INSTANCE == null) {
            synchronized(AppDatabase::class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(DawnBridge.app, AppDatabase::class.java, "dusk_database")
                        .addMigrations(migration_1_2)
                        .addMigrations(migration_2_3)
                        .build()
                }
            }
        }
        return INSTANCE!!
    }

    private val migration_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // 1. 创建一个新的临时表格，结构与新的 BannerBean 数据类一致，但不包括 mainId 字段
            db.execSQL("CREATE TABLE IF NOT EXISTS bannerList_new " +
                    "(id INTEGER PRIMARY KEY NOT NULL, " +
                    "`desc` TEXT NOT NULL, " +
                    "imagePath TEXT NOT NULL, " +
                    "isVisible INTEGER NOT NULL, " +
                    "`order` INTEGER NOT NULL, " +
                    "title TEXT NOT NULL, " +
                    "type INTEGER NOT NULL, " +
                    "url TEXT NOT NULL, " +
                    "insertTime INTEGER NOT NULL)")

            // 2. 从旧表格复制数据到新表格，注意不包括 mainId 字段，将其复制到 id 字段
            db.execSQL("INSERT INTO bannerList_new " +
                    "(id, `desc`, imagePath, isVisible, `order`, title, type, url, insertTime) " +
                    "SELECT id, `desc`, imagePath, isVisible, `order`, title, type, url, insertTime FROM bannerList")

            // 3. 删除旧的表格
            db.execSQL("DROP TABLE bannerList")

            // 4. 将新表格重命名为旧表格的名称
            db.execSQL("ALTER TABLE bannerList_new RENAME TO bannerList")
        }
    }

    private val migration_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE harmony ADD COLUMN insertTime INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}")
        }
    }
}