package com.lzq.dawn.util.thread

import android.util.Log
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * @Name :UtilsThreadFactory
 * @Time :2022/9/2 14:46
 * @Author :  Lzq
 * @Desc :
 */
class UtilsThreadFactory @JvmOverloads internal constructor(
    prefix: String,
    private val priority: Int,
    private val isDaemon: Boolean = false
) : AtomicLong(), ThreadFactory {
    private val namePrefix: String

    init {
        namePrefix = prefix + "-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-"
    }

    override fun newThread(r: Runnable): Thread {
        val t: Thread = object : Thread(r, namePrefix + andIncrement) {
            override fun run() {
                try {
                    super.run()
                } catch (t: Throwable) {
                    Log.e("ThreadUtils", "Request threw uncaught throwable", t)
                }
            }
        }
        t.isDaemon = isDaemon
        t.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, e -> println(e) }
        t.priority = priority
        return t
    }

    companion object {
        private val POOL_NUMBER = AtomicInteger(1)
        private const val serialVersionUID = -9209200509960368598L
    }

    override fun toByte(): Byte {
        return super.get().toByte()
    }

    override fun toShort(): Short {
     return super.get().toShort()
    }
}