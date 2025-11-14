package com.lzq.dawn.room

import androidx.room.RoomDatabase

/**
 * @projectName com.lzq.dawn.room
 * @author Lzq
 * @date : Created by Lzq on 2025/11/14 10:00
 * @version
 * @description: RoomDatabase 的封装基类
 *
 * RoomDatabase 的封装基类，统一提供常用能力：
 * - version()：获取当前数据库版本号
 * - clear()：清空所有表数据
 * - inTransaction{}：以泛型方式在事务中执行代码块
 */
abstract class DawnRoomDatabase : RoomDatabase() {
    /** 当前数据库版本号 */
    fun version(): Int = openHelper.writableDatabase.version

    /** 清空所有表数据 */
    fun clear() = clearAllTables()

    /** 在事务中执行代码块 */
    inline fun <reified T : DawnRoomDatabase> inTransaction(crossinline block: (T) -> Unit) {
        val self = this as T
        runInTransaction { block(self) }
    }
}
