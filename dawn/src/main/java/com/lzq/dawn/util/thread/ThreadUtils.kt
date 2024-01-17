package com.lzq.dawn.util.thread

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.IntRange
import com.lzq.dawn.util.thread.ThreadPoolExecutor4Util.Companion.createPool
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

/**
 * @Name :ThreadUtils
 * @Time :2022/9/1 15:17
 * @Author :  Lzq
 * @Desc : 线程池
 */
object ThreadUtils {
    /**
     * 获取主线程Handler
     *
     * @return Handler
     */
    private val mainHandler = Handler(Looper.getMainLooper())
    private val TYPE_PRIORITY_POOLS: MutableMap<Int, MutableMap<Int, ExecutorService>> = HashMap()
    val TASK_POOL_MAP: MutableMap<Task<*>, ExecutorService?> = ConcurrentHashMap()
    val CPU_COUNT = Runtime.getRuntime().availableProcessors()
    private val TIMER = Timer()
    const val TYPE_SINGLE: Byte = -1
    const val TYPE_CACHED: Byte = -2
    const val TYPE_IO: Byte = -4
    const val TYPE_CPU: Byte = -8
    private var sDeliver: Executor? = null
    val isMainThread: Boolean
        /**
         * 返回线程是否是主线程。
         *
         * @return `true`: yes<br></br>`false`: no
         */
        get() = Looper.myLooper() == Looper.getMainLooper()

    /**
     * 主线程执行任务
     *
     * @param runnable runnable
     */
    fun runOnUiThread(runnable: Runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run()
        } else {
            mainHandler.post(runnable)
        }
    }

    /**
     * 主线程延迟执行任务
     *
     * @param runnable    runnable
     * @param delayMillis 延迟时间
     */
    fun runOnUiThreadDelayed(runnable: Runnable?, delayMillis: Long) {
        mainHandler.postDelayed(runnable!!, delayMillis)
    }

    /**
     * 返回一个线程池，该线程池重用在共享的无界队列中运行的固定数量的线程，并在需要时使用提供的 ThreadFactory 创建新线程。
     *
     * @param size 线程池大小
     * @return 固定线程池
     */
    fun getFixedPool(@IntRange(from = 1) size: Int): ExecutorService {
        return getPoolByTypeAndPriority(size)
    }

    /**
     * 返回一个线程池，该线程池重用在共享的无界队列中运行的固定数量的线程，并在需要时使用提供的 ThreadFactory 创建新线程。
     *
     * @param size     线程池大小
     * @param priority 轮询中线程的优先级。
     * @return 固定线程池
     */
    fun getFixedPool(
        @IntRange(from = 1) size: Int, @IntRange(from = 1, to = 10) priority: Int
    ): ExecutorService {
        return getPoolByTypeAndPriority(size, priority)
    }

    val singlePool: ExecutorService
        /**
         * 返回一个线程池，该线程池使用在无限队列中运行的单个工作线程，并在需要时使用提供的 ThreadFactory 创建新线程
         *
         * @return 单线程池
         */
        get() = getPoolByTypeAndPriority(TYPE_SINGLE.toInt())

    /**
     * 返回一个线程池，该线程池使用在无限队列中运行的单个工作线程，并在需要时使用提供的 ThreadFactory 创建新线程
     *
     * @param priority 轮询中线程的优先级。
     * @return 单线程池
     */
    fun getSinglePool(@IntRange(from = 1, to = 10) priority: Int): ExecutorService {
        return getPoolByTypeAndPriority(TYPE_SINGLE.toInt(), priority)
    }

    val cachedPool: ExecutorService
        /**
         * 返回一个线程池，该线程池根据需要创建新线程，但在以前构造的线程可用时将重用它们。
         *
         * @return 缓存的线程池
         */
        get() = getPoolByTypeAndPriority(TYPE_CACHED.toInt())

    /**
     * 返回一个线程池，该线程池根据需要创建新线程，但在以前构造的线程可用时将重用它们。
     *
     * @param priority 轮询中线程的优先级。
     * @return 缓存的线程池
     */
    fun getCachedPool(@IntRange(from = 1, to = 10) priority: Int): ExecutorService {
        return getPoolByTypeAndPriority(TYPE_CACHED.toInt(), priority)
    }

    val ioPool: ExecutorService
        /**
         * 返回一个线程池，该线程池创建 （2 * CPU_COUNT + 1） 个线程，这些线程在大小为 128 的队列中运行。
         *
         * @return 一个 IO 线程池
         */
        get() = getPoolByTypeAndPriority(TYPE_IO.toInt())

    /**
     * 返回一个线程池，该线程池创建 （2 * CPU_COUNT + 1） 个线程，这些线程在大小为 128 的队列中运行。
     *
     * @param priority 轮询中线程的优先级
     * @return 一个 IO 线程池
     */
    fun getIoPool(@IntRange(from = 1, to = 10) priority: Int): ExecutorService {
        return getPoolByTypeAndPriority(TYPE_IO.toInt(), priority)
    }

    val cpuPool: ExecutorService
        /**
         * 返回一个线程池，该线程池创建 （CPU_COUNT + 1） 个线程，这些线程在大小为 128 且最大线程数等于 （2 * CPU_COUNT + 1） 的队列中运行。
         *
         * @return 一个 CPU 线程池
         */
        get() = getPoolByTypeAndPriority(TYPE_CPU.toInt())

    /**
     * 返回一个线程池，该线程池创建 （CPU_COUNT + 1） 个线程，这些线程在大小为 128 且最大线程数等于 （2 * CPU_COUNT + 1） 的队列中运行。
     *
     * @param priority 轮询中线程的优先级
     * @return 一个 CPU 线程池
     */
    fun getCpuPool(@IntRange(from = 1, to = 10) priority: Int): ExecutorService {
        return getPoolByTypeAndPriority(TYPE_CPU.toInt(), priority)
    }

    /**
     * 在固定线程池中执行给定任务。
     *
     * @param size 固定线程池中的线程大小。
     * @param task 要执行的任务。
     * @param <T>  任务结果的类型。
    </T> */
    fun <T> executeByFixed(@IntRange(from = 1) size: Int, task: Task<T>) {
        execute(getPoolByTypeAndPriority(size), task)
    }

    /**
     * 在固定线程池中执行给定任务。
     *
     * @param size     固定线程池中的线程大小。
     * @param task     要执行的任务。
     * @param priority 轮询中线程的优先级
     * @param <T>      任务结果的类型。
    </T> */
    fun <T> executeByFixed(
        @IntRange(from = 1) size: Int, task: Task<T>, @IntRange(from = 1, to = 10) priority: Int
    ) {
        execute(getPoolByTypeAndPriority(size, priority), task)
    }

    /**
     * 在给定延迟后在固定线程池中执行给定任务。
     *
     * @param size  固定线程池中的线程大小。
     * @param task  要执行的任务。
     * @param delay 从现在开始延迟执行的时间。
     * @param unit  延迟参数的时间单位。
     * @param <T>   任务结果的类型。
    </T> */
    fun <T> executeByFixedWithDelay(
        @IntRange(from = 1) size: Int, task: Task<T>, delay: Long, unit: TimeUnit
    ) {
        executeWithDelay(getPoolByTypeAndPriority(size), task, delay, unit)
    }

    /**
     * 在给定延迟后在固定线程池中执行给定任务。
     *
     * @param size     固定线程池中的线程大小。
     * @param task     要执行的任务。
     * @param delay    从现在开始延迟执行的时间。
     * @param unit     延迟参数的时间单位。
     * @param priority 轮询中线程的优先级
     * @param <T>      任务结果的类型。
    </T> */
    fun <T> executeByFixedWithDelay(
        @IntRange(from = 1) size: Int,
        task: Task<T>,
        delay: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeWithDelay(getPoolByTypeAndPriority(size, priority), task, delay, unit)
    }

    /**
     * 以固定速率在固定线程池中执行给定任务。
     *
     * @param size   固定线程池中的线程大小。
     * @param task   要执行的任务。
     * @param period 连续执行之间的时间段。
     * @param unit   周期参数的时间单位。
     * @param <T>    任务结果的类型。
    </T> */
    fun <T> executeByFixedAtFixRate(
        @IntRange(from = 1) size: Int, task: Task<T>, period: Long, unit: TimeUnit
    ) {
        executeAtFixedRate(getPoolByTypeAndPriority(size), task, 0, period, unit)
    }

    /**
     * 以固定速率在固定线程池中执行给定任务。
     *
     * @param size     固定线程池中的线程大小。
     * @param task     要执行的任务。
     * @param period   连续执行之间的时间段。
     * @param unit     周期参数的时间单位。
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
    </T> */
    fun <T> executeByFixedAtFixRate(
        @IntRange(from = 1) size: Int,
        task: Task<T>,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeAtFixedRate(getPoolByTypeAndPriority(size, priority), task, 0, period, unit)
    }

    /**
     * 以固定速率在固定线程池中执行给定任务。
     *
     * @param size         固定线程池中的线程大小。
     * @param task         要执行的任务。
     * @param initialDelay 延迟首次执行的时间。
     * @param period       连续执行之间的时间段。
     * @param unit         周期参数的时间单位。
     * @param <T>          任务结果的类型。
    </T> */
    fun <T> executeByFixedAtFixRate(
        @IntRange(from = 1) size: Int, task: Task<T>, initialDelay: Long, period: Long, unit: TimeUnit
    ) {
        executeAtFixedRate(getPoolByTypeAndPriority(size), task, initialDelay, period, unit)
    }

    /**
     * 以固定速率在固定线程池中执行给定任务。
     *
     * @param size         固定线程池中的线程大小。
     * @param task         要执行的任务。
     * @param initialDelay 延迟首次执行的时间。
     * @param period       连续执行之间的时间段。
     * @param unit         周期参数的时间单位。
     * @param priority     轮询中线程的优先级。
     * @param <T>          任务结果的类型。
    </T> */
    fun <T> executeByFixedAtFixRate(
        @IntRange(from = 1) size: Int,
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeAtFixedRate(getPoolByTypeAndPriority(size, priority), task, initialDelay, period, unit)
    }

    /**
     * 在单个线程池中执行给定的任务。
     *
     * @param task 要执行的任务
     * @param <T>  任务结果的类型。
    </T> */
    fun <T> executeBySingle(task: Task<T>) {
        execute(getPoolByTypeAndPriority(TYPE_SINGLE.toInt()), task)
    }

    /**
     * 在单个线程池中执行给定的任务。
     *
     * @param task     要执行的任务
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
    </T> */
    fun <T> executeBySingle(
        task: Task<T>, @IntRange(from = 1, to = 10) priority: Int
    ) {
        execute(getPoolByTypeAndPriority(TYPE_SINGLE.toInt(), priority), task)
    }

    /**
     * 在给定延迟后在单个线程池中执行给定任务。
     *
     * @param task  要执行的任务
     * @param delay 从现在开始延迟执行的时间。
     * @param unit  延迟参数的时间单位。
     * @param <T>   任务结果的类型。
    </T> */
    fun <T> executeBySingleWithDelay(
        task: Task<T>, delay: Long, unit: TimeUnit
    ) {
        executeWithDelay(getPoolByTypeAndPriority(TYPE_SINGLE.toInt()), task, delay, unit)
    }

    /**
     * 在给定延迟后在单个线程池中执行给定任务。
     *
     * @param task     要执行的任务
     * @param delay    从现在开始延迟执行的时间。
     * @param unit     延迟参数的时间单位。
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
    </T> */
    fun <T> executeBySingleWithDelay(
        task: Task<T>, delay: Long, unit: TimeUnit, @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeWithDelay(getPoolByTypeAndPriority(TYPE_SINGLE.toInt(), priority), task, delay, unit)
    }

    /**
     * 以固定速率在单个线程池中执行给定任务。
     *
     * @param task   要执行的任务
     * @param period 连续执行之间的时间段。
     * @param unit   周期参数的时间单位。
     * @param <T>    任务结果的类型。
    </T> */
    fun <T> executeBySingleAtFixRate(
        task: Task<T>, period: Long, unit: TimeUnit
    ) {
        executeAtFixedRate(getPoolByTypeAndPriority(TYPE_SINGLE.toInt()), task, 0, period, unit)
    }

    /**
     * 以固定速率在单个线程池中执行给定任务。
     *
     * @param task     要执行的任务
     * @param period   连续执行之间的时间段。
     * @param unit     周期参数的时间单位。
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
    </T> */
    fun <T> executeBySingleAtFixRate(
        task: Task<T>, period: Long, unit: TimeUnit, @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeAtFixedRate(getPoolByTypeAndPriority(TYPE_SINGLE.toInt(), priority), task, 0, period, unit)
    }

    /**
     * 以固定速率在单个线程池中执行给定任务。
     *
     * @param task         要执行的任务
     * @param initialDelay 延迟首次执行的时间。
     * @param period       连续执行之间的时间段。
     * @param unit         初始延迟和周期参数的时间单位。
     * @param <T>          任务结果的类型。
    </T> */
    fun <T> executeBySingleAtFixRate(
        task: Task<T>, initialDelay: Long, period: Long, unit: TimeUnit
    ) {
        executeAtFixedRate(getPoolByTypeAndPriority(TYPE_SINGLE.toInt()), task, initialDelay, period, unit)
    }

    /**
     * 以固定速率在单个线程池中执行给定任务。
     *
     * @param task         要执行的任务
     * @param initialDelay 延迟首次执行的时间。
     * @param period       连续执行之间的时间段。
     * @param unit         初始延迟和周期参数的时间单位。
     * @param priority     轮询中线程的优先级。
     * @param <T>          任务结果的类型。
    </T> */
    fun <T> executeBySingleAtFixRate(
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(TYPE_SINGLE.toInt(), priority), task, initialDelay, period, unit
        )
    }

    /**
     * 在缓存的线程池中执行给定的任务。
     *
     * @param task 要执行的任务。
     * @param <T>  任务结果的类型。
    </T> */
    fun <T> executeByCached(task: Task<T>) {
        execute(getPoolByTypeAndPriority(TYPE_CACHED.toInt()), task)
    }

    /**
     * 在缓存的线程池中执行给定的任务。
     *
     * @param task     要执行的任务。
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
    </T> */
    fun <T> executeByCached(
        task: Task<T>, @IntRange(from = 1, to = 10) priority: Int
    ) {
        execute(getPoolByTypeAndPriority(TYPE_CACHED.toInt(), priority), task)
    }

    /**
     * 在给定延迟后在缓存线程池中执行给定任务。
     *
     * @param task  要执行的任务。
     * @param delay 从现在开始延迟执行的时间。
     * @param unit  延迟参数的时间单位。
     * @param <T>   任务结果的类型。
    </T> */
    fun <T> executeByCachedWithDelay(
        task: Task<T>, delay: Long, unit: TimeUnit
    ) {
        executeWithDelay(getPoolByTypeAndPriority(TYPE_CACHED.toInt()), task, delay, unit)
    }

    /**
     * 在给定延迟后在缓存线程池中执行给定任务。
     *
     * @param task     要执行的任务。
     * @param delay    轮询中线程的优先级。
     * @param unit     轮询中线程的优先级。
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
    </T> */
    fun <T> executeByCachedWithDelay(
        task: Task<T>, delay: Long, unit: TimeUnit, @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeWithDelay(getPoolByTypeAndPriority(TYPE_CACHED.toInt(), priority), task, delay, unit)
    }

    /**
     * 以固定速率在缓存线程池中执行给定任务。
     *
     * @param task   要执行的任务。
     * @param period 连续执行之间的时间段。
     * @param unit   周期参数的时间单位。
     * @param <T>    任务结果的类型。
    </T> */
    fun <T> executeByCachedAtFixRate(
        task: Task<T>, period: Long, unit: TimeUnit
    ) {
        executeAtFixedRate(getPoolByTypeAndPriority(TYPE_CACHED.toInt()), task, 0, period, unit)
    }

    /**
     * 以固定速率在缓存线程池中执行给定任务。
     *
     * @param task     要执行的任务。
     * @param period   连续执行之间的时间段。
     * @param unit     周期参数的时间单位。
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
    </T> */
    fun <T> executeByCachedAtFixRate(
        task: Task<T>, period: Long, unit: TimeUnit, @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeAtFixedRate(getPoolByTypeAndPriority(TYPE_CACHED.toInt(), priority), task, 0, period, unit)
    }

    /**
     * 以固定速率在缓存线程池中执行给定任务。
     *
     * @param task         要执行的任务。
     * @param initialDelay 延迟首次执行的时间。
     * @param period       连续执行之间的时间段。
     * @param unit         初始延迟和周期参数的时间单位。
     * @param <T>          任务结果的类型。
    </T> */
    fun <T> executeByCachedAtFixRate(
        task: Task<T>, initialDelay: Long, period: Long, unit: TimeUnit
    ) {
        executeAtFixedRate(getPoolByTypeAndPriority(TYPE_CACHED.toInt()), task, initialDelay, period, unit)
    }

    /**
     * 以固定速率在缓存线程池中执行给定任务。
     *
     * @param task         要执行的任务。
     * @param initialDelay 延迟首次执行的时间。
     * @param period       连续执行之间的时间段。
     * @param unit         初始延迟和周期参数的时间单位。
     * @param priority     轮询中线程的优先级。
     * @param <T>          任务结果的类型。
    </T> */
    fun <T> executeByCachedAtFixRate(
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(TYPE_CACHED.toInt(), priority), task, initialDelay, period, unit
        )
    }

    /**
     * 在 IO 线程池中执行给定任务。
     *
     * @param task 要执行的任务。
     * @param <T>  任务结果的类型。
    </T> */
    fun <T> executeByIo(task: Task<T>) {
        execute(getPoolByTypeAndPriority(TYPE_IO.toInt()), task)
    }

    /**
     * 在 IO 线程池中执行给定任务。
     *
     * @param task     要执行的任务。
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
    </T> */
    fun <T> executeByIo(
        task: Task<T>, @IntRange(from = 1, to = 10) priority: Int
    ) {
        execute(getPoolByTypeAndPriority(TYPE_IO.toInt(), priority), task)
    }

    /**
     * 在给定延迟后在 IO 线程池中执行给定任务。
     *
     * @param task  要执行的任务。
     * @param delay 轮询中线程的优先级。
     * @param unit  轮询中线程的优先级。
     * @param <T>   任务结果的类型。
    </T> */
    fun <T> executeByIoWithDelay(
        task: Task<T>, delay: Long, unit: TimeUnit
    ) {
        executeWithDelay(getPoolByTypeAndPriority(TYPE_IO.toInt()), task, delay, unit)
    }

    /**
     * 在给定延迟后在 IO 线程池中执行给定任务。
     *
     * @param task     要执行的任务。
     * @param delay    轮询中线程的优先级。
     * @param unit     轮询中线程的优先级。
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
    </T> */
    fun <T> executeByIoWithDelay(
        task: Task<T>, delay: Long, unit: TimeUnit, @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeWithDelay(getPoolByTypeAndPriority(TYPE_IO.toInt(), priority), task, delay, unit)
    }

    /**
     * 以固定速率在 IO 线程池中执行给定任务。
     *
     * @param task   要执行的任务。
     * @param period 连续执行之间的时间段。
     * @param unit   周期参数的时间单位。
     * @param <T>    任务结果的类型。
    </T> */
    fun <T> executeByIoAtFixRate(
        task: Task<T>, period: Long, unit: TimeUnit
    ) {
        executeAtFixedRate(getPoolByTypeAndPriority(TYPE_IO.toInt()), task, 0, period, unit)
    }

    /**
     * 以固定速率在 IO 线程池中执行给定任务。
     *
     * @param task     要执行的任务。
     * @param period   连续执行之间的时间段。
     * @param unit     周期参数的时间单位。
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
    </T> */
    fun <T> executeByIoAtFixRate(
        task: Task<T>, period: Long, unit: TimeUnit, @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeAtFixedRate(getPoolByTypeAndPriority(TYPE_IO.toInt(), priority), task, 0, period, unit)
    }

    /**
     * 以固定速率在 IO 线程池中执行给定任务。
     *
     * @param task         要执行的任务。
     * @param initialDelay 延迟首次执行的时间。
     * @param period       连续执行之间的时间段。
     * @param unit         初始延迟和周期参数的时间单位。
     * @param <T>          任务结果的类型。
    </T> */
    fun <T> executeByIoAtFixRate(
        task: Task<T>, initialDelay: Long, period: Long, unit: TimeUnit
    ) {
        executeAtFixedRate(getPoolByTypeAndPriority(TYPE_IO.toInt()), task, initialDelay, period, unit)
    }

    /**
     * 以固定速率在 IO 线程池中执行给定任务。
     *
     * @param task         要执行的任务。
     * @param initialDelay 延迟首次执行的时间。
     * @param period       连续执行之间的时间段。
     * @param unit         初始延迟和周期参数的时间单位。
     * @param priority     轮询中线程的优先级。
     * @param <T>          任务结果的类型。
    </T> */
    fun <T> executeByIoAtFixRate(
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(TYPE_IO.toInt(), priority), task, initialDelay, period, unit
        )
    }

    /**
     * 在 CPU 线程池中执行给定的任务。
     *
     * @param task 要执行的任务。
     * @param <T>  任务结果的类型。
    </T> */
    fun <T> executeByCpu(task: Task<T>) {
        execute(getPoolByTypeAndPriority(TYPE_CPU.toInt()), task)
    }

    /**
     * 在 CPU 线程池中执行给定的任务。
     *
     * @param task     要执行的任务。
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
    </T> */
    fun <T> executeByCpu(
        task: Task<T>, @IntRange(from = 1, to = 10) priority: Int
    ) {
        execute(getPoolByTypeAndPriority(TYPE_CPU.toInt(), priority), task)
    }

    /**
     * 在给定延迟后在 CPU 线程池中执行给定任务。
     *
     * @param task  要执行的任务。
     * @param delay 轮询中线程的优先级。
     * @param unit  轮询中线程的优先级。
     * @param <T>   任务结果的类型。
    </T> */
    fun <T> executeByCpuWithDelay(
        task: Task<T>, delay: Long, unit: TimeUnit
    ) {
        executeWithDelay(getPoolByTypeAndPriority(TYPE_CPU.toInt()), task, delay, unit)
    }

    /**
     * 在给定延迟后在 CPU 线程池中执行给定任务。
     *
     * @param task     要执行的任务。
     * @param delay    轮询中线程的优先级。
     * @param unit     轮询中线程的优先级。
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
    </T> */
    fun <T> executeByCpuWithDelay(
        task: Task<T>, delay: Long, unit: TimeUnit, @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeWithDelay(getPoolByTypeAndPriority(TYPE_CPU.toInt(), priority), task, delay, unit)
    }

    /**
     * 以固定速率在 CPU 线程池中执行给定任务。
     *
     * @param task   要执行的任务。
     * @param period 连续执行之间的时间段。
     * @param unit   周期参数的时间单位。
     * @param <T>    任务结果的类型。
    </T> */
    fun <T> executeByCpuAtFixRate(
        task: Task<T>, period: Long, unit: TimeUnit
    ) {
        executeAtFixedRate(getPoolByTypeAndPriority(TYPE_CPU.toInt()), task, 0, period, unit)
    }

    /**
     * 以固定速率在 CPU 线程池中执行给定任务。
     *
     * @param task     要执行的任务。
     * @param period   连续执行之间的时间段。
     * @param unit     周期参数的时间单位。
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
    </T> */
    fun <T> executeByCpuAtFixRate(
        task: Task<T>, period: Long, unit: TimeUnit, @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeAtFixedRate(getPoolByTypeAndPriority(TYPE_CPU.toInt(), priority), task, 0, period, unit)
    }

    /**
     * 以固定速率在 CPU 线程池中执行给定任务。
     *
     * @param task         要执行的任务。
     * @param initialDelay 延迟首次执行的时间。
     * @param period       连续执行之间的时间段。
     * @param unit         初始延迟和周期参数的时间单位。
     * @param <T>          任务结果的类型。
    </T> */
    fun <T> executeByCpuAtFixRate(
        task: Task<T>, initialDelay: Long, period: Long, unit: TimeUnit
    ) {
        executeAtFixedRate(getPoolByTypeAndPriority(TYPE_CPU.toInt()), task, initialDelay, period, unit)
    }

    /**
     * 以固定速率在 CPU 线程池中执行给定任务。
     *
     * @param task         要执行的任务。
     * @param initialDelay 延迟首次执行的时间。
     * @param period       连续执行之间的时间段。
     * @param unit         初始延迟和周期参数的时间单位。
     * @param priority     轮询中线程的优先级。
     * @param <T>          任务结果的类型。
    </T> */
    fun <T> executeByCpuAtFixRate(
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(TYPE_CPU.toInt(), priority), task, initialDelay, period, unit
        )
    }

    /**
     * 在自定义线程池中执行给定任务。
     *
     * @param pool 自定义线程池。
     * @param task 要执行的任务。
     * @param <T>  任务结果的类型。
    </T> */
    fun <T> executeByCustom(pool: ExecutorService, task: Task<T>) {
        execute(pool, task)
    }

    /**
     * 在给定延迟后在自定义线程池中执行给定任务。
     *
     * @param pool  自定义线程池。
     * @param task  要执行的任务。
     * @param delay 从现在开始延迟执行的时间。
     * @param unit  延迟参数的时间单位。
     * @param <T>   任务结果的类型。
    </T> */
    fun <T> executeByCustomWithDelay(
        pool: ExecutorService, task: Task<T>, delay: Long, unit: TimeUnit
    ) {
        executeWithDelay(pool, task, delay, unit)
    }

    /**
     * 以固定速率在自定义线程池中执行给定任务。
     *
     * @param pool   自定义线程池。
     * @param task   要执行的任务。
     * @param period 连续执行之间的时间段。
     * @param unit   延迟参数的时间单位。
     * @param <T>    任务结果的类型。
    </T> */
    fun <T> executeByCustomAtFixRate(
        pool: ExecutorService, task: Task<T>, period: Long, unit: TimeUnit
    ) {
        executeAtFixedRate(pool, task, 0, period, unit)
    }

    /**
     * 以固定速率在自定义线程池中执行给定任务。
     *
     * @param pool         自定义线程池。
     * @param task         要执行的任务。
     * @param initialDelay 延迟首次执行的时间。
     * @param period       连续执行之间的时间段。
     * @param unit         初始延迟和周期参数的时间单位。
     * @param <T>          任务结果的类型。
    </T> */
    fun <T> executeByCustomAtFixRate(
        pool: ExecutorService, task: Task<T>, initialDelay: Long, period: Long, unit: TimeUnit
    ) {
        executeAtFixedRate(pool, task, initialDelay, period, unit)
    }

    /**
     * 取消给定的任务。
     *
     * @param task 要取消的任务。
     */
    fun cancel(task: Task<*>?) {
        if (task == null) {
            return
        }
        task.cancel()
    }

    /**
     * 取消给定的任务。
     *
     * @param tasks 要取消的任务。
     */
    fun cancel(vararg tasks: Task<*>?) {
        if (tasks.isEmpty()) {
            return
        }
        for (task in tasks) {
            if (task == null) {
                continue
            }
            task.cancel()
        }
    }

    /**
     * 取消给定的任务。
     *
     * @param tasks 要取消的任务。
     */
    fun cancel(tasks: List<Task<*>?>?) {
        if (tasks.isNullOrEmpty()) {
            return
        }
        for (task in tasks) {
            if (task == null) {
                continue
            }
            task.cancel()
        }
    }

    /**
     * 取消池中的任务。
     *
     * @param executorService 线程池
     */
    fun cancel(executorService: ExecutorService) {
        if (executorService is ThreadPoolExecutor4Util) {
            for ((key, value) in TASK_POOL_MAP) {
                if (value === executorService) {
                    cancel(key)
                }
            }
        } else {
            Log.e("ThreadUtils", "The executorService is not ThreadUtils's pool.")
        }
    }

    /**
     * 设置 deliver.
     *
     * @param deliver The deliver.
     */
    fun setDeliver(deliver: Executor?) {
        sDeliver = deliver
    }

    private fun <T> execute(pool: ExecutorService, task: Task<T>) {
        execute(pool, task, 0, 0, null)
    }

    private fun <T> executeWithDelay(
        pool: ExecutorService, task: Task<T>, delay: Long, unit: TimeUnit
    ) {
        execute(pool, task, delay, 0, unit)
    }

    private fun <T> executeAtFixedRate(
        pool: ExecutorService, task: Task<T>, delay: Long, period: Long, unit: TimeUnit
    ) {
        execute(pool, task, delay, period, unit)
    }

    private fun <T> execute(
        pool: ExecutorService, task: Task<T>, delay: Long, period: Long, unit: TimeUnit?
    ) {
        synchronized(TASK_POOL_MAP) {
            if (TASK_POOL_MAP[task] != null) {
                Log.e("ThreadUtils", "Task can only be executed once.")
                return
            }
            TASK_POOL_MAP.put(task, pool)
        }
        if (period == 0L) {
            if (delay == 0L) {
                pool.execute(task)
            } else {
                val timerTask: TimerTask = object : TimerTask() {
                    override fun run() {
                        pool.execute(task)
                    }
                }
                TIMER.schedule(timerTask, unit!!.toMillis(delay))
            }
        } else {
            task.setSchedule(true)
            val timerTask: TimerTask = object : TimerTask() {
                override fun run() {
                    pool.execute(task)
                }
            }
            TIMER.scheduleAtFixedRate(timerTask, unit!!.toMillis(delay), unit.toMillis(period))
        }
    }

    private fun getPoolByTypeAndPriority(type: Int): ExecutorService {
        return getPoolByTypeAndPriority(type, Thread.NORM_PRIORITY)
    }

    private fun getPoolByTypeAndPriority(type: Int, priority: Int): ExecutorService {
        synchronized(TYPE_PRIORITY_POOLS) {
            var pool: ExecutorService?
            var priorityPools = TYPE_PRIORITY_POOLS[type]
            if (priorityPools == null) {
                priorityPools = ConcurrentHashMap()
                pool = createPool(type, priority)
                priorityPools[priority] = pool
                TYPE_PRIORITY_POOLS[type] = priorityPools
            } else {
                pool = priorityPools[priority]
                if (pool == null) {
                    pool = createPool(type, priority)
                    priorityPools[priority] = pool
                }
            }
            return pool
        }
    }

    val globalDeliver: Executor
        get() {
            if (sDeliver == null) {
                sDeliver = Executor { command -> runOnUiThread(command) }
            }
            return sDeliver!!
        }
}