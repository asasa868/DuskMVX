package com.lzq.dawn.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.lzq.dawn.DawnBridge
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * @projectName com.lzq.dawn.room
 * @author Lzq
 * @date : Created by Lzq on 2025/11/14 10:00
 * @version
 * @description: Room 数据库实例的统一管理器
 *
 * 特性：
 * - 线程安全：双检锁与全局锁，避免并发重复构建与迁移注册竞态
 * - 多实例：按 `(dbClass, name)` 作为键，支持同类库不同名称并存
 * - 迁移冻结：首个实例构建成功后，阻止后续对该 `dbClass` 的迁移注册
 * - 安全守卫：在非 debug 环境强制关闭主线程查询与破坏性迁移（由配置控制）
 * - 可观测：支持配置 SQL 查询回调与执行器
 */
public object DawnDatabaseManager {
    private val lock = ReentrantLock()
    private val instances = mutableMapOf<Pair<Class<out RoomDatabase>, String>, RoomDatabase>()
    private val migrations = mutableMapOf<Class<out RoomDatabase>, MutableSet<Migration>>()
    private val initializedClasses = mutableSetOf<Class<out RoomDatabase>>()

    /**
     * 注册数据库迁移。已初始化的数据库类会被忽略（冻结策略）。
     * 使用 Set 去重，避免重复添加相同迁移。
     */
    public fun registerMigrations(dbClass: Class<out RoomDatabase>, vararg items: Migration) {
        if (initializedClasses.contains(dbClass)) return
        lock.withLock {
            if (initializedClasses.contains(dbClass)) return
            val set = migrations.getOrPut(dbClass) { mutableSetOf() }
            set.addAll(items)
        }
    }

    @Suppress("UNCHECKED_CAST")
    /** 获取指定名称的数据库实例（简单配置）。 */
    public fun <T : RoomDatabase> get(dbClass: Class<T>, name: String): T {
        val config = DatabaseConfig(name = name)
        return get(dbClass, config)
    }

    @Suppress("UNCHECKED_CAST")
    /** 获取数据库实例（完整配置）。采用双检锁避免并发构建。 */
    public fun <T : RoomDatabase> get(dbClass: Class<T>, config: DatabaseConfig): T {
        val key = dbClass to config.name
        val first = instances[key]
        if (first != null) return first as T
        lock.withLock {
            val cached = instances[key]
            if (cached != null) return cached as T
            val context: Context = DawnBridge.app
            val builder = if (config.inMemory) {
                Room.inMemoryDatabaseBuilder(context, dbClass)
            } else {
                Room.databaseBuilder(context, dbClass, config.name)
            }
            migrations[dbClass]?.let { if (it.isNotEmpty()) builder.addMigrations(*it.toTypedArray()) }
            if (config.migrations.isNotEmpty()) builder.addMigrations(*config.migrations.toTypedArray())
            // 环境化安全守卫（release 默认禁用危险选项）
            val allowMainThread = if (config.enforceReleaseSafety) false else config.allowMainThreadQueries
            val destructive = if (config.enforceReleaseSafety) false else config.destructiveMigration
            if (allowMainThread) builder.allowMainThreadQueries()
            if (destructive) builder.fallbackToDestructiveMigration()
            config.journalMode?.let { builder.setJournalMode(it) }
            config.queryExecutor?.let { builder.setQueryExecutor(it) }
            config.transactionExecutor?.let { builder.setTransactionExecutor(it) }
            if (config.queryCallback != null && config.queryCallbackExecutor != null) {
                builder.setQueryCallback(config.queryCallback, config.queryCallbackExecutor!!)
            }
            if (config.enableMultiInstanceInvalidation) builder.enableMultiInstanceInvalidation()
            if (config.autoCloseTimeoutMs != null && config.autoCloseTimeUnit != null) {
                builder.setAutoCloseTimeout(config.autoCloseTimeoutMs, config.autoCloseTimeUnit)
            }
            config.callback?.let { builder.addCallback(it) }
            val db = builder.build()
            instances[key] = db
            initializedClasses.add(dbClass)
            return db
        }
    }

    /** 清理并关闭某数据库类的所有实例，同时移除已注册迁移与初始化标记。 */
    public fun clear(dbClass: Class<out RoomDatabase>) {
        val keys = instances.keys.filter { it.first == dbClass }
        keys.forEach { instances.remove(it)?.close() }
        migrations.remove(dbClass)
        initializedClasses.remove(dbClass)
    }

    /** 精确清理指定名称的数据库实例。 */
    public fun clear(dbClass: Class<out RoomDatabase>, name: String) {
        val key = dbClass to name
        instances.remove(key)?.close()
    }
}