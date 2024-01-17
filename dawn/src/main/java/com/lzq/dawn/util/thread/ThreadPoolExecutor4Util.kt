package com.lzq.dawn.util.thread;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Name :ThreadPoolExecutor4Util
 * @Time :2022/9/2 14:49
 * @Author :  Lzq
 * @Desc : 线程池创建类
 */
public class ThreadPoolExecutor4Util extends ThreadPoolExecutor {

    static ExecutorService createPool(final int type, final int priority) {
        switch (type) {
            case ThreadUtils.TYPE_SINGLE:
                return new ThreadPoolExecutor4Util(1, 1,
                        0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue4Util(),
                        new UtilsThreadFactory("single", priority)
                );
            case ThreadUtils.TYPE_CACHED:
                return new ThreadPoolExecutor4Util(0, 128,
                        60L, TimeUnit.SECONDS,
                        new LinkedBlockingQueue4Util(true),
                        new UtilsThreadFactory("cached", priority)
                );
            case ThreadUtils.TYPE_IO:
                return new ThreadPoolExecutor4Util(2 * ThreadUtils.CPU_COUNT + 1, 2 * ThreadUtils.CPU_COUNT + 1,
                        30, TimeUnit.SECONDS,
                        new LinkedBlockingQueue4Util(),
                        new UtilsThreadFactory("io", priority)
                );
            case ThreadUtils.TYPE_CPU:
                return new ThreadPoolExecutor4Util(ThreadUtils.CPU_COUNT + 1, 2 * ThreadUtils.CPU_COUNT + 1,
                        30, TimeUnit.SECONDS,
                        new LinkedBlockingQueue4Util(true),
                        new UtilsThreadFactory("cpu", priority)
                );
            default:
                return new ThreadPoolExecutor4Util(type, type,
                        0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue4Util(),
                        new UtilsThreadFactory("fixed(" + type + ")", priority)
                );
        }
    }

    private final AtomicInteger mSubmittedCount = new AtomicInteger();

    private LinkedBlockingQueue4Util mWorkQueue;

    ThreadPoolExecutor4Util(int corePoolSize, int maximumPoolSize,
                            long keepAliveTime, TimeUnit unit,
                            LinkedBlockingQueue4Util workQueue,
                            ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize,
                keepAliveTime, unit,
                workQueue,
                threadFactory
        );
        workQueue.setmPool(this);
        mWorkQueue = workQueue;
    }

    private int getSubmittedCount() {
        return mSubmittedCount.get();
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        mSubmittedCount.decrementAndGet();
        super.afterExecute(r, t);
    }

    @Override
    public void execute(@NonNull Runnable command) {
        if (this.isShutdown()) {
            return;
        }
        mSubmittedCount.incrementAndGet();
        try {
            super.execute(command);
        } catch (RejectedExecutionException ignore) {
            Log.e("ThreadUtils", "This will not happen!");
            mWorkQueue.offer(command);
        } catch (Throwable t) {
            mSubmittedCount.decrementAndGet();
        }
    }
}