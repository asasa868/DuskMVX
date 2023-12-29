package com.lzq.dawn.util.thread;

import android.util.Log;

import androidx.annotation.CallSuper;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Name :Task
 * @Time :2022/9/1 15:37
 * @Author :  Lzq
 * @Desc :
 */
public abstract class Task<T> implements Runnable {

    /**
     * 线程状态
     */
    private static final int NEW = 0;
    private static final int RUNNING = 1;
    private static final int EXCEPTIONAL = 2;
    private static final int COMPLETING = 3;
    private static final int CANCELLED = 4;
    private static final int INTERRUPTED = 5;
    private static final int TIMEOUT = 6;

    /**
     * 当前线程状态
     */
    private final AtomicInteger state = new AtomicInteger(NEW);

    private volatile boolean isSchedule;
    private volatile Thread runner;

    private Timer mTimer;
    private long mTimeoutMillis;
    private OnTimeoutListener mTimeoutListener;

    private Executor deliver;

    /**
     * 在后台
     *
     * @return T
     * @throws Throwable Throwable
     */
    public abstract T doInBackground() throws Throwable;

    /**
     * 执行成功
     *
     * @param result T
     */
    public abstract void onSuccess(T result);

    /**
     * 执行取消
     */
    public abstract void onCancel();

    /**
     * 执行出错
     *
     * @param t Throwable
     */
    public abstract void onFail(Throwable t);


    @Override
    public void run() {
        if (isSchedule) {
            if (runner == null) {
                //如果当前值 {@code ==} 是预期值，则自动将值设置为给定的更新值。
                if (!state.compareAndSet(NEW, RUNNING)) {
                    return;
                }
                runner = Thread.currentThread();
                if (mTimeoutListener != null) {
                    Log.w("ThreadUtils", "Scheduled task doesn't support timeout.");
                }
            } else {
                if (state.get() != RUNNING) {
                    return;
                }
            }
        } else {
            //如果当前值 {@code ==} 是预期值，则自动将值设置为给定的更新值。
            if (!state.compareAndSet(NEW, RUNNING)) {
                return;
            }
            runner = Thread.currentThread();
            if (mTimeoutListener != null) {
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!isDone() && mTimeoutListener != null) {
                            timeout();
                            mTimeoutListener.onTimeout();
                            onDone();
                        }
                    }
                }, mTimeoutMillis);
            }

        }

    }


    public void cancel() {
        cancel(true);
    }

    public void cancel(boolean mayInterruptIfRunning) {
        synchronized (state) {
            if (state.get() > RUNNING) {
                return;
            }
            state.set(CANCELLED);
        }
        if (mayInterruptIfRunning) {
            if (runner != null) {
                runner.interrupt();
            }
        }

        getDeliver().execute(new Runnable() {
            @Override
            public void run() {
                onCancel();
                onDone();
            }
        });
    }

    public boolean isCanceled() {
        return state.get() >= CANCELLED;
    }

    public boolean isDone() {
        return state.get() > RUNNING;
    }

    public Task<T> setDeliver(Executor deliver) {
        this.deliver = deliver;
        return this;
    }

    /**
     * Scheduled task doesn't support timeout.
     */
    public Task<T> setTimeout(final long timeoutMillis, final OnTimeoutListener listener) {
        mTimeoutMillis = timeoutMillis;
        mTimeoutListener = listener;
        return this;
    }

     void setSchedule(boolean isSchedule) {
        this.isSchedule = isSchedule;
    }


    private void timeout() {
        synchronized (state) {
            if (state.get() > RUNNING) {
                return;
            }
            state.set(TIMEOUT);
        }
        if (runner != null) {
            runner.interrupt();
        }
    }

    private Executor getDeliver() {
        if (deliver == null) {
            return ThreadUtils.getGlobalDeliver();
        }
        return deliver;
    }

    @CallSuper
    protected void onDone() {
        ThreadUtils.TASK_POOL_MAP.remove(this);
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
            mTimeoutListener = null;
        }
    }

    public interface OnTimeoutListener {
        void onTimeout();
    }
}
