package com.lzq.dawn.room

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

/**
 * @projectName com.lzq.dawn.room
 * @author Lzq
 * @date : Created by Lzq on 2025/11/14 10:00
 * @version
 * @description: 通用 DAO 接口抽象
 * 
 * 通用 DAO 接口，抽象增删改与批量插入，减少重复声明。
 * 默认冲突策略为 ABORT，业务可在具体 DAO 中覆盖更严格/更宽松的策略。
 */
interface BaseDao<T> {
    /** 插入单项或多项（默认 ABORT 冲突策略）。 */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg entity: T)

    /** 批量插入（默认 ABORT 冲突策略）。 */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(entities: List<T>)

    /** 删除单项或多项。 */
    @Delete
    suspend fun delete(vararg entity: T)

    /** 更新单项或多项。 */
    @Update
    suspend fun update(vararg entity: T)
}