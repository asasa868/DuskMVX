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
 * @description: Room 封装统一入口：注册、获取、清理、预热与状态查询。
 *
 * 使用方式：
 * - 注册：DawnRoom.register<AppDatabase> { name = "db"; migrations(m1, m2) }
 * - 获取：DawnRoom.get<AppDatabase>() 或 DawnRoom.get<AppDatabase>("db")
 * - 清理：DawnRoom.clear(AppDatabase::class.java) / DawnRoom.clear(AppDatabase::class.java, "db")
 */
public object DawnRoom {
    val manager = DawnDatabaseManager
    val configs = mutableMapOf<Pair<Class<out RoomDatabase>, String>, DatabaseConfig>()
    val initialized = mutableSetOf<Pair<Class<out RoomDatabase>, String>>()

    /** 使用 DSL 注册数据库配置与迁移；首次构建后同键注册将被忽略。 */
    public inline fun <reified T : RoomDatabase> register(builder: DawnRoomBuilder.() -> Unit) {
        val cls = T::class.java
        val b = DawnRoomBuilder()
        builder(b)
        val key = cls to b.name
        if (initialized.contains(key)) return
        val prev = configs[key]
        val next = b.toConfig()
        configs[key] = merge(prev, next)
        if (b.migrations.isNotEmpty()) manager.registerMigrations(cls, *b.migrations.toTypedArray())
    }

    /** 按名称获取数据库实例。 */
    public inline fun <reified T : RoomDatabase> get(name: String): T {
        val cls = T::class.java
        val key = cls to name
        val cfg = configs[key] ?: throw IllegalStateException("Room not registered for ${cls.name} with name ${name}")
        val db = manager.get(cls, cfg)
        initialized.add(key)
        return db
    }

    /** 获取已注册的单一名称实例；如同类库注册了多个名称则抛出提示异常。 */
    public inline fun <reified T : RoomDatabase> get(): T {
        val cls = T::class.java
        val names = configs.keys.filter { it.first == cls }.map { it.second }
        if (names.isEmpty()) throw IllegalStateException("Room not registered for ${cls.name}")
        if (names.size > 1) throw IllegalStateException("Multiple names registered for ${cls.name}: ${names.joinToString(", ")}. Use get<T>(name)")
        val name = names.first()
        return get<T>(name)
    }

    /** 清理指定类的所有实例与配置。 */
    public fun clear(cls: Class<out RoomDatabase>) {
        manager.clear(cls)
        initialized.removeIf { it.first == cls }
        configs.keys.removeIf { it.first == cls }
    }

    /** 精确清理指定类+名称的实例与配置。 */
    public fun clear(cls: Class<out RoomDatabase>, name: String) {
        manager.clear(cls, name)
        initialized.remove(cls to name)
        configs.remove(cls to name)
    }

    fun merge(a: DatabaseConfig?, b: DatabaseConfig): DatabaseConfig {
        if (a == null) return b
        return DatabaseConfig(
            name = b.name.ifEmpty { a.name },
            allowMainThreadQueries = a.allowMainThreadQueries || b.allowMainThreadQueries,
            inMemory = a.inMemory || b.inMemory,
            destructiveMigration = a.destructiveMigration || b.destructiveMigration,
            migrations = (a.migrations + b.migrations).distinct(),
            callback = b.callback ?: a.callback,
            journalMode = b.journalMode ?: a.journalMode,
            queryExecutor = b.queryExecutor ?: a.queryExecutor,
            transactionExecutor = b.transactionExecutor ?: a.transactionExecutor,
            enableMultiInstanceInvalidation = a.enableMultiInstanceInvalidation || b.enableMultiInstanceInvalidation,
            autoCloseTimeoutMs = b.autoCloseTimeoutMs ?: a.autoCloseTimeoutMs,
            autoCloseTimeUnit = b.autoCloseTimeUnit ?: a.autoCloseTimeUnit
        )
    }
    /** 数据库状态信息。 */
    public data class DawnRoomStatus(
        val name: String,
        val initialized: Boolean,
        val migrationsCount: Int,
        val journalMode: RoomDatabase.JournalMode?,
        val version: Int
    )

    /** 查询数据库状态（名称、是否初始化、迁移数量、日志模式、版本号）。 */
    public fun status(cls: Class<out RoomDatabase>, name: String): DawnRoomStatus {
        val key = cls to name
        val cfg = configs[key] ?: throw IllegalStateException("Room not registered for ${cls.name} with name ${name}")
        val init = initialized.contains(key)
        val db = manager.get(cls, cfg)
        val version = db.openHelper.writableDatabase.version
        return DawnRoomStatus(cfg.name, init, cfg.migrations.size, cfg.journalMode, version)
    }

    /** 预热数据库（适用于应用启动阶段）。 */
    public inline fun <reified T : RoomDatabase> prewarm() {
        get<T>()
    }
}

/** 数据库构建配置的 DSL Builder。 */
public class DawnRoomBuilder {
    var name: String = ""
    var allowMainThreadQueries: Boolean = false
    var inMemory: Boolean = false
    var destructiveMigration: Boolean = false
    var callback: RoomDatabase.Callback? = null
    var journalMode: RoomDatabase.JournalMode? = null
    var queryExecutor: Executor? = null
    var transactionExecutor: Executor? = null
    var enableMultiInstanceInvalidation: Boolean = false
    var autoCloseTimeoutMs: Long? = null
    var autoCloseTimeUnit: TimeUnit? = null
    var enforceReleaseSafety: Boolean = true
    var queryCallback: RoomDatabase.QueryCallback? = null
    var queryCallbackExecutor: Executor? = null
    val migrations = mutableSetOf<Migration>()

    public fun migrations(vararg m: Migration) { migrations.addAll(m) }
    public fun toConfig() = DatabaseConfig(
        name,
        allowMainThreadQueries,
        inMemory,
        destructiveMigration,
        migrations.toList(),
        callback,
        journalMode,
        queryExecutor,
        transactionExecutor,
        enableMultiInstanceInvalidation,
        autoCloseTimeoutMs,
        autoCloseTimeUnit,
        enforceReleaseSafety,
        queryCallback,
        queryCallbackExecutor
    )
}