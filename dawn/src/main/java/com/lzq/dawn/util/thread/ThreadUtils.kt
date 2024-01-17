package com.lzq.dawn.util.thread;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Name :ThreadUtils
 * @Time :2022/9/1 15:17
 * @Author :  Lzq
 * @Desc : 线程池
 */
public final class ThreadUtils {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    private static final Map<Integer, Map<Integer, ExecutorService>> TYPE_PRIORITY_POOLS = new HashMap<>();

    public static final Map<Task, ExecutorService> TASK_POOL_MAP = new ConcurrentHashMap<>();

    public static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final Timer TIMER = new Timer();

    public static final byte TYPE_SINGLE = -1;
    public static final byte TYPE_CACHED = -2;
    public static final byte TYPE_IO = -4;
    public static final byte TYPE_CPU = -8;

    private static Executor sDeliver;

    /**
     * 返回线程是否是主线程。
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * 获取主线程Handler
     *
     * @return Handler
     */
    public static Handler getMainHandler() {
        return HANDLER;
    }

    /**
     * 主线程执行任务
     *
     * @param runnable runnable
     */
    public static void runOnUiThread(final Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            HANDLER.post(runnable);
        }
    }

    /**
     * 主线程延迟执行任务
     *
     * @param runnable    runnable
     * @param delayMillis 延迟时间
     */
    public static void runOnUiThreadDelayed(final Runnable runnable, long delayMillis) {
        HANDLER.postDelayed(runnable, delayMillis);
    }

    /**
     * 返回一个线程池，该线程池重用在共享的无界队列中运行的固定数量的线程，并在需要时使用提供的 ThreadFactory 创建新线程。
     *
     * @param size 线程池大小
     * @return 固定线程池
     */
    public static ExecutorService getFixedPool(@IntRange(from = 1) final int size) {
        return getPoolByTypeAndPriority(size);
    }

    /**
     * 返回一个线程池，该线程池重用在共享的无界队列中运行的固定数量的线程，并在需要时使用提供的 ThreadFactory 创建新线程。
     *
     * @param size     线程池大小
     * @param priority 轮询中线程的优先级。
     * @return 固定线程池
     */
    public static ExecutorService getFixedPool(@IntRange(from = 1) final int size,
                                               @IntRange(from = 1, to = 10) final int priority) {
        return getPoolByTypeAndPriority(size, priority);
    }

    /**
     * 返回一个线程池，该线程池使用在无限队列中运行的单个工作线程，并在需要时使用提供的 ThreadFactory 创建新线程
     *
     * @return 单线程池
     */
    public static ExecutorService getSinglePool() {
        return getPoolByTypeAndPriority(TYPE_SINGLE);
    }

    /**
     * 返回一个线程池，该线程池使用在无限队列中运行的单个工作线程，并在需要时使用提供的 ThreadFactory 创建新线程
     *
     * @param priority 轮询中线程的优先级。
     * @return 单线程池
     */
    public static ExecutorService getSinglePool(@IntRange(from = 1, to = 10) final int priority) {
        return getPoolByTypeAndPriority(TYPE_SINGLE, priority);
    }

    /**
     * 返回一个线程池，该线程池根据需要创建新线程，但在以前构造的线程可用时将重用它们。
     *
     * @return 缓存的线程池
     */
    public static ExecutorService getCachedPool() {
        return getPoolByTypeAndPriority(TYPE_CACHED);
    }

    /**
     * 返回一个线程池，该线程池根据需要创建新线程，但在以前构造的线程可用时将重用它们。
     *
     * @param priority 轮询中线程的优先级。
     * @return 缓存的线程池
     */
    public static ExecutorService getCachedPool(@IntRange(from = 1, to = 10) final int priority) {
        return getPoolByTypeAndPriority(TYPE_CACHED, priority);
    }


    /**
     * 返回一个线程池，该线程池创建 （2 * CPU_COUNT + 1） 个线程，这些线程在大小为 128 的队列中运行。
     *
     * @return 一个 IO 线程池
     */
    public static ExecutorService getIoPool() {
        return getPoolByTypeAndPriority(TYPE_IO);
    }

    /**
     * 返回一个线程池，该线程池创建 （2 * CPU_COUNT + 1） 个线程，这些线程在大小为 128 的队列中运行。
     *
     * @param priority 轮询中线程的优先级
     * @return 一个 IO 线程池
     */
    public static ExecutorService getIoPool(@IntRange(from = 1, to = 10) final int priority) {
        return getPoolByTypeAndPriority(TYPE_IO, priority);
    }

    /**
     * 返回一个线程池，该线程池创建 （CPU_COUNT + 1） 个线程，这些线程在大小为 128 且最大线程数等于 （2 * CPU_COUNT + 1） 的队列中运行。
     *
     * @return 一个 CPU 线程池
     */
    public static ExecutorService getCpuPool() {
        return getPoolByTypeAndPriority(TYPE_CPU);
    }

    /**
     * 返回一个线程池，该线程池创建 （CPU_COUNT + 1） 个线程，这些线程在大小为 128 且最大线程数等于 （2 * CPU_COUNT + 1） 的队列中运行。
     *
     * @param priority 轮询中线程的优先级
     * @return 一个 CPU 线程池
     */
    public static ExecutorService getCpuPool(@IntRange(from = 1, to = 10) final int priority) {
        return getPoolByTypeAndPriority(TYPE_CPU, priority);
    }

    /**
     * 在固定线程池中执行给定任务。
     *
     * @param size 固定线程池中的线程大小。
     * @param task 要执行的任务。
     * @param <T>  任务结果的类型。
     */
    public static <T> void executeByFixed(@IntRange(from = 1) final int size, final Task<T> task) {
        execute(getPoolByTypeAndPriority(size), task);
    }

    /**
     * 在固定线程池中执行给定任务。
     *
     * @param size     固定线程池中的线程大小。
     * @param task     要执行的任务。
     * @param priority 轮询中线程的优先级
     * @param <T>      任务结果的类型。
     */
    public static <T> void executeByFixed(@IntRange(from = 1) final int size,
                                          final Task<T> task,
                                          @IntRange(from = 1, to = 10) final int priority) {
        execute(getPoolByTypeAndPriority(size, priority), task);
    }

    /**
     * 在给定延迟后在固定线程池中执行给定任务。
     *
     * @param size  固定线程池中的线程大小。
     * @param task  要执行的任务。
     * @param delay 从现在开始延迟执行的时间。
     * @param unit  延迟参数的时间单位。
     * @param <T>   任务结果的类型。
     */
    public static <T> void executeByFixedWithDelay(@IntRange(from = 1) final int size,
                                                   final Task<T> task,
                                                   final long delay,
                                                   final TimeUnit unit) {
        executeWithDelay(getPoolByTypeAndPriority(size), task, delay, unit);
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
     */
    public static <T> void executeByFixedWithDelay(@IntRange(from = 1) final int size,
                                                   final Task<T> task,
                                                   final long delay,
                                                   final TimeUnit unit,
                                                   @IntRange(from = 1, to = 10) final int priority) {
        executeWithDelay(getPoolByTypeAndPriority(size, priority), task, delay, unit);
    }

    /**
     * 以固定速率在固定线程池中执行给定任务。
     *
     * @param size   固定线程池中的线程大小。
     * @param task   要执行的任务。
     * @param period 连续执行之间的时间段。
     * @param unit   周期参数的时间单位。
     * @param <T>    任务结果的类型。
     */
    public static <T> void executeByFixedAtFixRate(@IntRange(from = 1) final int size,
                                                   final Task<T> task,
                                                   final long period,
                                                   final TimeUnit unit) {
        executeAtFixedRate(getPoolByTypeAndPriority(size), task, 0, period, unit);
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
     */
    public static <T> void executeByFixedAtFixRate(@IntRange(from = 1) final int size,
                                                   final Task<T> task,
                                                   final long period,
                                                   final TimeUnit unit,
                                                   @IntRange(from = 1, to = 10) final int priority) {
        executeAtFixedRate(getPoolByTypeAndPriority(size, priority), task, 0, period, unit);
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
     */
    public static <T> void executeByFixedAtFixRate(@IntRange(from = 1) final int size,
                                                   final Task<T> task,
                                                   long initialDelay,
                                                   final long period,
                                                   final TimeUnit unit) {
        executeAtFixedRate(getPoolByTypeAndPriority(size), task, initialDelay, period, unit);
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
     */
    public static <T> void executeByFixedAtFixRate(@IntRange(from = 1) final int size,
                                                   final Task<T> task,
                                                   long initialDelay,
                                                   final long period,
                                                   final TimeUnit unit,
                                                   @IntRange(from = 1, to = 10) final int priority) {
        executeAtFixedRate(getPoolByTypeAndPriority(size, priority), task, initialDelay, period, unit);
    }


    /**
     * 在单个线程池中执行给定的任务。
     *
     * @param task 要执行的任务
     * @param <T>  任务结果的类型。
     */
    public static <T> void executeBySingle(final Task<T> task) {
        execute(getPoolByTypeAndPriority(TYPE_SINGLE), task);
    }

    /**
     * 在单个线程池中执行给定的任务。
     *
     * @param task     要执行的任务
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
     */
    public static <T> void executeBySingle(final Task<T> task,
                                           @IntRange(from = 1, to = 10) final int priority) {
        execute(getPoolByTypeAndPriority(TYPE_SINGLE, priority), task);
    }

    /**
     * 在给定延迟后在单个线程池中执行给定任务。
     *
     * @param task  要执行的任务
     * @param delay 从现在开始延迟执行的时间。
     * @param unit  延迟参数的时间单位。
     * @param <T>   任务结果的类型。
     */
    public static <T> void executeBySingleWithDelay(final Task<T> task,
                                                    final long delay,
                                                    final TimeUnit unit) {
        executeWithDelay(getPoolByTypeAndPriority(TYPE_SINGLE), task, delay, unit);
    }

    /**
     * 在给定延迟后在单个线程池中执行给定任务。
     *
     * @param task     要执行的任务
     * @param delay    从现在开始延迟执行的时间。
     * @param unit     延迟参数的时间单位。
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
     */
    public static <T> void executeBySingleWithDelay(final Task<T> task,
                                                    final long delay,
                                                    final TimeUnit unit,
                                                    @IntRange(from = 1, to = 10) final int priority) {
        executeWithDelay(getPoolByTypeAndPriority(TYPE_SINGLE, priority), task, delay, unit);
    }

    /**
     * 以固定速率在单个线程池中执行给定任务。
     *
     * @param task   要执行的任务
     * @param period 连续执行之间的时间段。
     * @param unit   周期参数的时间单位。
     * @param <T>    任务结果的类型。
     */
    public static <T> void executeBySingleAtFixRate(final Task<T> task,
                                                    final long period,
                                                    final TimeUnit unit) {
        executeAtFixedRate(getPoolByTypeAndPriority(TYPE_SINGLE), task, 0, period, unit);
    }

    /**
     * 以固定速率在单个线程池中执行给定任务。
     *
     * @param task     要执行的任务
     * @param period   连续执行之间的时间段。
     * @param unit     周期参数的时间单位。
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
     */
    public static <T> void executeBySingleAtFixRate(final Task<T> task,
                                                    final long period,
                                                    final TimeUnit unit,
                                                    @IntRange(from = 1, to = 10) final int priority) {
        executeAtFixedRate(getPoolByTypeAndPriority(TYPE_SINGLE, priority), task, 0, period, unit);
    }

    /**
     * 以固定速率在单个线程池中执行给定任务。
     *
     * @param task         要执行的任务
     * @param initialDelay 延迟首次执行的时间。
     * @param period       连续执行之间的时间段。
     * @param unit         初始延迟和周期参数的时间单位。
     * @param <T>          任务结果的类型。
     */
    public static <T> void executeBySingleAtFixRate(final Task<T> task,
                                                    long initialDelay,
                                                    final long period,
                                                    final TimeUnit unit) {
        executeAtFixedRate(getPoolByTypeAndPriority(TYPE_SINGLE), task, initialDelay, period, unit);
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
     */
    public static <T> void executeBySingleAtFixRate(final Task<T> task,
                                                    long initialDelay,
                                                    final long period,
                                                    final TimeUnit unit,
                                                    @IntRange(from = 1, to = 10) final int priority) {
        executeAtFixedRate(
                getPoolByTypeAndPriority(TYPE_SINGLE, priority), task, initialDelay, period, unit
        );
    }


    /**
     * 在缓存的线程池中执行给定的任务。
     *
     * @param task 要执行的任务。
     * @param <T>  任务结果的类型。
     */
    public static <T> void executeByCached(final Task<T> task) {
        execute(getPoolByTypeAndPriority(TYPE_CACHED), task);
    }

    /**
     * 在缓存的线程池中执行给定的任务。
     *
     * @param task     要执行的任务。
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
     */
    public static <T> void executeByCached(final Task<T> task,
                                           @IntRange(from = 1, to = 10) final int priority) {
        execute(getPoolByTypeAndPriority(TYPE_CACHED, priority), task);
    }

    /**
     * 在给定延迟后在缓存线程池中执行给定任务。
     *
     * @param task  要执行的任务。
     * @param delay 从现在开始延迟执行的时间。
     * @param unit  延迟参数的时间单位。
     * @param <T>   任务结果的类型。
     */
    public static <T> void executeByCachedWithDelay(final Task<T> task,
                                                    final long delay,
                                                    final TimeUnit unit) {
        executeWithDelay(getPoolByTypeAndPriority(TYPE_CACHED), task, delay, unit);
    }

    /**
     * 在给定延迟后在缓存线程池中执行给定任务。
     *
     * @param task     要执行的任务。
     * @param delay    轮询中线程的优先级。
     * @param unit     轮询中线程的优先级。
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
     */
    public static <T> void executeByCachedWithDelay(final Task<T> task,
                                                    final long delay,
                                                    final TimeUnit unit,
                                                    @IntRange(from = 1, to = 10) final int priority) {
        executeWithDelay(getPoolByTypeAndPriority(TYPE_CACHED, priority), task, delay, unit);
    }

    /**
     * 以固定速率在缓存线程池中执行给定任务。
     *
     * @param task   要执行的任务。
     * @param period 连续执行之间的时间段。
     * @param unit   周期参数的时间单位。
     * @param <T>    任务结果的类型。
     */
    public static <T> void executeByCachedAtFixRate(final Task<T> task,
                                                    final long period,
                                                    final TimeUnit unit) {
        executeAtFixedRate(getPoolByTypeAndPriority(TYPE_CACHED), task, 0, period, unit);
    }

    /**
     * 以固定速率在缓存线程池中执行给定任务。
     *
     * @param task     要执行的任务。
     * @param period   连续执行之间的时间段。
     * @param unit     周期参数的时间单位。
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
     */
    public static <T> void executeByCachedAtFixRate(final Task<T> task,
                                                    final long period,
                                                    final TimeUnit unit,
                                                    @IntRange(from = 1, to = 10) final int priority) {
        executeAtFixedRate(getPoolByTypeAndPriority(TYPE_CACHED, priority), task, 0, period, unit);
    }

    /**
     * 以固定速率在缓存线程池中执行给定任务。
     *
     * @param task         要执行的任务。
     * @param initialDelay 延迟首次执行的时间。
     * @param period       连续执行之间的时间段。
     * @param unit         初始延迟和周期参数的时间单位。
     * @param <T>          任务结果的类型。
     */
    public static <T> void executeByCachedAtFixRate(final Task<T> task,
                                                    long initialDelay,
                                                    final long period,
                                                    final TimeUnit unit) {
        executeAtFixedRate(getPoolByTypeAndPriority(TYPE_CACHED), task, initialDelay, period, unit);
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
     */
    public static <T> void executeByCachedAtFixRate(final Task<T> task,
                                                    long initialDelay,
                                                    final long period,
                                                    final TimeUnit unit,
                                                    @IntRange(from = 1, to = 10) final int priority) {
        executeAtFixedRate(
                getPoolByTypeAndPriority(TYPE_CACHED, priority), task, initialDelay, period, unit
        );
    }

    /**
     * 在 IO 线程池中执行给定任务。
     *
     * @param task 要执行的任务。
     * @param <T>  任务结果的类型。
     */
    public static <T> void executeByIo(final Task<T> task) {
        execute(getPoolByTypeAndPriority(TYPE_IO), task);
    }

    /**
     * 在 IO 线程池中执行给定任务。
     *
     * @param task     要执行的任务。
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
     */
    public static <T> void executeByIo(final Task<T> task,
                                       @IntRange(from = 1, to = 10) final int priority) {
        execute(getPoolByTypeAndPriority(TYPE_IO, priority), task);
    }

    /**
     * 在给定延迟后在 IO 线程池中执行给定任务。
     *
     * @param task  要执行的任务。
     * @param delay 轮询中线程的优先级。
     * @param unit  轮询中线程的优先级。
     * @param <T>   任务结果的类型。
     */
    public static <T> void executeByIoWithDelay(final Task<T> task,
                                                final long delay,
                                                final TimeUnit unit) {
        executeWithDelay(getPoolByTypeAndPriority(TYPE_IO), task, delay, unit);
    }

    /**
     * 在给定延迟后在 IO 线程池中执行给定任务。
     *
     * @param task     要执行的任务。
     * @param delay    轮询中线程的优先级。
     * @param unit     轮询中线程的优先级。
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
     */
    public static <T> void executeByIoWithDelay(final Task<T> task,
                                                final long delay,
                                                final TimeUnit unit,
                                                @IntRange(from = 1, to = 10) final int priority) {
        executeWithDelay(getPoolByTypeAndPriority(TYPE_IO, priority), task, delay, unit);
    }

    /**
     * 以固定速率在 IO 线程池中执行给定任务。
     *
     * @param task   要执行的任务。
     * @param period 连续执行之间的时间段。
     * @param unit   周期参数的时间单位。
     * @param <T>    任务结果的类型。
     */
    public static <T> void executeByIoAtFixRate(final Task<T> task,
                                                final long period,
                                                final TimeUnit unit) {
        executeAtFixedRate(getPoolByTypeAndPriority(TYPE_IO), task, 0, period, unit);
    }

    /**
     * 以固定速率在 IO 线程池中执行给定任务。
     *
     * @param task     要执行的任务。
     * @param period   连续执行之间的时间段。
     * @param unit     周期参数的时间单位。
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
     */
    public static <T> void executeByIoAtFixRate(final Task<T> task,
                                                final long period,
                                                final TimeUnit unit,
                                                @IntRange(from = 1, to = 10) final int priority) {
        executeAtFixedRate(getPoolByTypeAndPriority(TYPE_IO, priority), task, 0, period, unit);
    }

    /**
     * 以固定速率在 IO 线程池中执行给定任务。
     *
     * @param task         要执行的任务。
     * @param initialDelay 延迟首次执行的时间。
     * @param period       连续执行之间的时间段。
     * @param unit         初始延迟和周期参数的时间单位。
     * @param <T>          任务结果的类型。
     */
    public static <T> void executeByIoAtFixRate(final Task<T> task,
                                                long initialDelay,
                                                final long period,
                                                final TimeUnit unit) {
        executeAtFixedRate(getPoolByTypeAndPriority(TYPE_IO), task, initialDelay, period, unit);
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
     */
    public static <T> void executeByIoAtFixRate(final Task<T> task,
                                                long initialDelay,
                                                final long period,
                                                final TimeUnit unit,
                                                @IntRange(from = 1, to = 10) final int priority) {
        executeAtFixedRate(
                getPoolByTypeAndPriority(TYPE_IO, priority), task, initialDelay, period, unit
        );
    }

    /**
     * 在 CPU 线程池中执行给定的任务。
     *
     * @param task 要执行的任务。
     * @param <T>  任务结果的类型。
     */
    public static <T> void executeByCpu(final Task<T> task) {
        execute(getPoolByTypeAndPriority(TYPE_CPU), task);
    }

    /**
     * 在 CPU 线程池中执行给定的任务。
     *
     * @param task     要执行的任务。
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
     */
    public static <T> void executeByCpu(final Task<T> task,
                                        @IntRange(from = 1, to = 10) final int priority) {
        execute(getPoolByTypeAndPriority(TYPE_CPU, priority), task);
    }

    /**
     * 在给定延迟后在 CPU 线程池中执行给定任务。
     *
     * @param task  要执行的任务。
     * @param delay 轮询中线程的优先级。
     * @param unit  轮询中线程的优先级。
     * @param <T>   任务结果的类型。
     */
    public static <T> void executeByCpuWithDelay(final Task<T> task,
                                                 final long delay,
                                                 final TimeUnit unit) {
        executeWithDelay(getPoolByTypeAndPriority(TYPE_CPU), task, delay, unit);
    }

    /**
     * 在给定延迟后在 CPU 线程池中执行给定任务。
     *
     * @param task     要执行的任务。
     * @param delay    轮询中线程的优先级。
     * @param unit     轮询中线程的优先级。
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
     */
    public static <T> void executeByCpuWithDelay(final Task<T> task,
                                                 final long delay,
                                                 final TimeUnit unit,
                                                 @IntRange(from = 1, to = 10) final int priority) {
        executeWithDelay(getPoolByTypeAndPriority(TYPE_CPU, priority), task, delay, unit);
    }

    /**
     * 以固定速率在 CPU 线程池中执行给定任务。
     *
     * @param task   要执行的任务。
     * @param period 连续执行之间的时间段。
     * @param unit   周期参数的时间单位。
     * @param <T>    任务结果的类型。
     */
    public static <T> void executeByCpuAtFixRate(final Task<T> task,
                                                 final long period,
                                                 final TimeUnit unit) {
        executeAtFixedRate(getPoolByTypeAndPriority(TYPE_CPU), task, 0, period, unit);
    }

    /**
     * 以固定速率在 CPU 线程池中执行给定任务。
     *
     * @param task     要执行的任务。
     * @param period   连续执行之间的时间段。
     * @param unit     周期参数的时间单位。
     * @param priority 轮询中线程的优先级。
     * @param <T>      任务结果的类型。
     */
    public static <T> void executeByCpuAtFixRate(final Task<T> task,
                                                 final long period,
                                                 final TimeUnit unit,
                                                 @IntRange(from = 1, to = 10) final int priority) {
        executeAtFixedRate(getPoolByTypeAndPriority(TYPE_CPU, priority), task, 0, period, unit);
    }

    /**
     * 以固定速率在 CPU 线程池中执行给定任务。
     *
     * @param task         要执行的任务。
     * @param initialDelay 延迟首次执行的时间。
     * @param period       连续执行之间的时间段。
     * @param unit         初始延迟和周期参数的时间单位。
     * @param <T>          任务结果的类型。
     */
    public static <T> void executeByCpuAtFixRate(final Task<T> task,
                                                 long initialDelay,
                                                 final long period,
                                                 final TimeUnit unit) {
        executeAtFixedRate(getPoolByTypeAndPriority(TYPE_CPU), task, initialDelay, period, unit);
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
     */
    public static <T> void executeByCpuAtFixRate(final Task<T> task,
                                                 long initialDelay,
                                                 final long period,
                                                 final TimeUnit unit,
                                                 @IntRange(from = 1, to = 10) final int priority) {
        executeAtFixedRate(
                getPoolByTypeAndPriority(TYPE_CPU, priority), task, initialDelay, period, unit
        );
    }

    /**
     * 在自定义线程池中执行给定任务。
     *
     * @param pool 自定义线程池。
     * @param task 要执行的任务。
     * @param <T>  任务结果的类型。
     */
    public static <T> void executeByCustom(final ExecutorService pool, final Task<T> task) {
        execute(pool, task);
    }

    /**
     * 在给定延迟后在自定义线程池中执行给定任务。
     *
     * @param pool  自定义线程池。
     * @param task  要执行的任务。
     * @param delay 从现在开始延迟执行的时间。
     * @param unit  延迟参数的时间单位。
     * @param <T>   任务结果的类型。
     */
    public static <T> void executeByCustomWithDelay(final ExecutorService pool,
                                                    final Task<T> task,
                                                    final long delay,
                                                    final TimeUnit unit) {
        executeWithDelay(pool, task, delay, unit);
    }

    /**
     * 以固定速率在自定义线程池中执行给定任务。
     *
     * @param pool   自定义线程池。
     * @param task   要执行的任务。
     * @param period 连续执行之间的时间段。
     * @param unit   延迟参数的时间单位。
     * @param <T>    任务结果的类型。
     */
    public static <T> void executeByCustomAtFixRate(final ExecutorService pool,
                                                    final Task<T> task,
                                                    final long period,
                                                    final TimeUnit unit) {
        executeAtFixedRate(pool, task, 0, period, unit);
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
     */
    public static <T> void executeByCustomAtFixRate(final ExecutorService pool,
                                                    final Task<T> task,
                                                    long initialDelay,
                                                    final long period,
                                                    final TimeUnit unit) {
        executeAtFixedRate(pool, task, initialDelay, period, unit);
    }

    /**
     * 取消给定的任务。
     *
     * @param task 要取消的任务。
     */
    public static void cancel(final Task task) {
        if (task == null) {
            return;
        }
        task.cancel();
    }

    /**
     * 取消给定的任务。
     *
     * @param tasks 要取消的任务。
     */
    public static void cancel(final Task... tasks) {
        if (tasks == null || tasks.length == 0) {
            return;
        }
        for (Task task : tasks) {
            if (task == null) {
                continue;
            }
            task.cancel();
        }
    }

    /**
     * 取消给定的任务。
     *
     * @param tasks 要取消的任务。
     */
    public static void cancel(final List<Task> tasks) {
        if (tasks == null || tasks.size() == 0) {
            return;
        }
        for (Task task : tasks) {
            if (task == null) {
                continue;
            }
            task.cancel();
        }
    }

    /**
     * 取消池中的任务。
     *
     * @param executorService 线程池
     */
    public static void cancel(ExecutorService executorService) {
        if (executorService instanceof ThreadPoolExecutor4Util) {
            for (Map.Entry<Task, ExecutorService> taskTaskInfoEntry : TASK_POOL_MAP.entrySet()) {
                if (taskTaskInfoEntry.getValue() == executorService) {
                    cancel(taskTaskInfoEntry.getKey());
                }
            }
        } else {
            Log.e("ThreadUtils", "The executorService is not ThreadUtils's pool.");
        }
    }

    /**
     * 设置 deliver.
     *
     * @param deliver The deliver.
     */
    public static void setDeliver(final Executor deliver) {
        sDeliver = deliver;
    }

    private static <T> void execute(final ExecutorService pool, final Task<T> task) {
        execute(pool, task, 0, 0, null);
    }

    private static <T> void executeWithDelay(final ExecutorService pool,
                                             final Task<T> task,
                                             final long delay,
                                             final TimeUnit unit) {
        execute(pool, task, delay, 0, unit);
    }

    private static <T> void executeAtFixedRate(final ExecutorService pool,
                                               final Task<T> task,
                                               long delay,
                                               final long period,
                                               final TimeUnit unit) {
        execute(pool, task, delay, period, unit);
    }

    private static <T> void execute(final ExecutorService pool, final Task<T> task,
                                    long delay, final long period, final TimeUnit unit) {
        synchronized (TASK_POOL_MAP) {
            if (TASK_POOL_MAP.get(task) != null) {
                Log.e("ThreadUtils", "Task can only be executed once.");
                return;
            }
            TASK_POOL_MAP.put(task, pool);
        }
        if (period == 0) {
            if (delay == 0) {
                pool.execute(task);
            } else {
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        pool.execute(task);
                    }
                };
                TIMER.schedule(timerTask, unit.toMillis(delay));
            }
        } else {
            task.setSchedule(true);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    pool.execute(task);
                }
            };
            TIMER.scheduleAtFixedRate(timerTask, unit.toMillis(delay), unit.toMillis(period));
        }
    }

    private static ExecutorService getPoolByTypeAndPriority(final int type) {
        return getPoolByTypeAndPriority(type, Thread.NORM_PRIORITY);
    }

    private static ExecutorService getPoolByTypeAndPriority(final int type, final int priority) {
        synchronized (TYPE_PRIORITY_POOLS) {
            ExecutorService pool;
            Map<Integer, ExecutorService> priorityPools = TYPE_PRIORITY_POOLS.get(type);
            if (priorityPools == null) {
                priorityPools = new ConcurrentHashMap<>();
                pool = ThreadPoolExecutor4Util.createPool(type, priority);
                priorityPools.put(priority, pool);
                TYPE_PRIORITY_POOLS.put(type, priorityPools);
            } else {
                pool = priorityPools.get(priority);
                if (pool == null) {
                    pool = ThreadPoolExecutor4Util.createPool(type, priority);
                    priorityPools.put(priority, pool);
                }
            }
            return pool;
        }
    }

    public static Executor getGlobalDeliver() {
        if (sDeliver == null) {
            sDeliver = new Executor() {
                @Override
                public void execute(@NonNull Runnable command) {
                    runOnUiThread(command);
                }
            };
        }
        return sDeliver;
    }
}
