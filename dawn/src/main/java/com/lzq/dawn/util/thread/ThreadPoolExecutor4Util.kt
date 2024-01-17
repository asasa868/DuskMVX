package com.lzq.dawn.util.thread

import android.util.Log
import java.util.concurrent.ExecutorService
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * @Name :ThreadPoolExecutor4Util
 * @Time :2022/9/2 14:49
 * @Author :  Lzq
 * @Desc : 线程池创建类
 */
class ThreadPoolExecutor4Util internal constructor(
    corePoolSize: Int,
    maximumPoolSize: Int,
    keepAliveTime: Long,
    unit: TimeUnit?,
    workQueue: LinkedBlockingQueue4Util,
    threadFactory: ThreadFactory?
) : ThreadPoolExecutor(
    corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory
) {
    private val mSubmittedCount = AtomicInteger()
    private val mWorkQueue: LinkedBlockingQueue4Util

    init {
        workQueue.setmPool(this)
        mWorkQueue = workQueue
    }

    private val submittedCount: Int
        private get() = mSubmittedCount.get()

    override fun afterExecute(r: Runnable, t: Throwable) {
        mSubmittedCount.decrementAndGet()
        super.afterExecute(r, t)
    }

    override fun execute(command: Runnable) {
        if (this.isShutdown) {
            return
        }
        mSubmittedCount.incrementAndGet()
        try {
            super.execute(command)
        } catch (ignore: RejectedExecutionException) {
            Log.e("ThreadUtils", "This will not happen!")
            mWorkQueue.offer(command)
        } catch (t: Throwable) {
            mSubmittedCount.decrementAndGet()
        }
    }

    companion object {
        @JvmStatic
        fun createPool(type: Int, priority: Int): ExecutorService {
            return when (type) {
                ThreadUtils.TYPE_SINGLE.toInt() -> ThreadPoolExecutor4Util(
                    1,
                    1,
                    0L,
                    TimeUnit.MILLISECONDS,
                    LinkedBlockingQueue4Util(),
                    UtilsThreadFactory("single", priority)
                )

                ThreadUtils.TYPE_CACHED.toInt() -> ThreadPoolExecutor4Util(
                    0,
                    128,
                    60L,
                    TimeUnit.SECONDS,
                    LinkedBlockingQueue4Util(true),
                    UtilsThreadFactory("cached", priority)
                )

                ThreadUtils.TYPE_IO.toInt() -> ThreadPoolExecutor4Util(
                    2 * ThreadUtils.CPU_COUNT + 1,
                    2 * ThreadUtils.CPU_COUNT + 1,
                    30,
                    TimeUnit.SECONDS,
                    LinkedBlockingQueue4Util(),
                    UtilsThreadFactory("io", priority)
                )

                ThreadUtils.TYPE_CPU.toInt() -> ThreadPoolExecutor4Util(
                    ThreadUtils.CPU_COUNT + 1,
                    2 * ThreadUtils.CPU_COUNT + 1,
                    30,
                    TimeUnit.SECONDS,
                    LinkedBlockingQueue4Util(true),
                    UtilsThreadFactory("cpu", priority)
                )

                else -> ThreadPoolExecutor4Util(
                    type,
                    type,
                    0L,
                    TimeUnit.MILLISECONDS,
                    LinkedBlockingQueue4Util(),
                    UtilsThreadFactory("fixed($type)", priority)
                )
            }
        }
    }
}