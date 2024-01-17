package com.lzq.dawn.util.thread;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Name :UtilsThreadFactory
 * @Time :2022/9/2 14:46
 * @Author :  Lzq
 * @Desc :
 */
public class UtilsThreadFactory extends AtomicLong
        implements ThreadFactory {
    private static final AtomicInteger POOL_NUMBER      = new AtomicInteger(1);
    private static final long          serialVersionUID = -9209200509960368598L;
    private final        String        namePrefix;
    private final        int           priority;
    private final        boolean       isDaemon;

    UtilsThreadFactory(String prefix, int priority) {
        this(prefix, priority, false);
    }

    UtilsThreadFactory(String prefix, int priority, boolean isDaemon) {
        namePrefix = prefix + "-pool-" +
                POOL_NUMBER.getAndIncrement() +
                "-thread-";
        this.priority = priority;
        this.isDaemon = isDaemon;
    }

    @Override
    public Thread newThread(@NonNull Runnable r) {
        Thread t = new Thread(r, namePrefix + getAndIncrement()) {
            @Override
            public void run() {
                try {
                    super.run();
                } catch (Throwable t) {
                    Log.e("ThreadUtils", "Request threw uncaught throwable", t);
                }
            }
        };
        t.setDaemon(isDaemon);
        t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println(e);
            }
        });
        t.setPriority(priority);
        return t;
    }
}