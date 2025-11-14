package com.lzq.dawn.room

import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

/**
 * @projectName com.lzq.dawn.room
 * @author Lzq
 * @date : Created by Lzq on 2025/11/14 10:00
 * @version
 * @description: Room 数据库构建完整配置项
 * 
 * - name：数据库名称（用于区分多实例）
 * - allowMainThreadQueries：是否允许主线程查询（release 默认禁用）
 * - inMemory：是否使用内存库（测试/短生命周期场景）
 * - destructiveMigration：缺失迁移时是否破坏性迁移（release 默认禁用）
 * - migrations：显式迁移列表
 * - callback：RoomDatabase 回调（onCreate/onOpen）
 * - journalMode：日志模式（建议 WAL）
 * - queryExecutor/transactionExecutor：查询与事务执行器
 * - enableMultiInstanceInvalidation：多实例/跨进程失效联动
 * - autoCloseTimeoutMs/autoCloseTimeUnit：自动关闭配置
 * - enforceReleaseSafety：是否启用发布环境安全守卫
 * - queryCallback/queryCallbackExecutor：SQL 查询调试钩子
 */
public data class DatabaseConfig(
    val name: String,
    val allowMainThreadQueries: Boolean = false,
    val inMemory: Boolean = false,
    val destructiveMigration: Boolean = false,
    val migrations: List<Migration> = emptyList(),
    val callback: RoomDatabase.Callback? = null,
    val journalMode: RoomDatabase.JournalMode? = null,
    val queryExecutor: Executor? = null,
    val transactionExecutor: Executor? = null,
    val enableMultiInstanceInvalidation: Boolean = false,
    val autoCloseTimeoutMs: Long? = null,
    val autoCloseTimeUnit: TimeUnit? = null,
    val enforceReleaseSafety: Boolean = true,
    val queryCallback: RoomDatabase.QueryCallback? = null,
    val queryCallbackExecutor: Executor? = null
)