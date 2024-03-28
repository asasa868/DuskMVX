package com.lzq.dawn.util.thread

import android.util.Log
import androidx.annotation.CallSuper
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicInteger

/**
 * @Name :Task
 * @Time :2022/9/1 15:37
 * @Author :  Lzq
 * @Desc :
 */
abstract class Task<T>() : Runnable {
    /**
     * 当前线程状态
     */
    private val state = AtomicInteger(NEW)

    @Volatile
    private var isSchedule = false

    @Volatile
    private var runner: Thread? = null
    private var mTimer: Timer? = null
    private var mTimeoutMillis: Long = 0
    private var mTimeoutListener: OnTimeoutListener? = null
    private var deliver: Executor? = null

    /**
     * 在后台
     *
     * @return T
     * @throws Throwable Throwable
     */
    @Throws(Throwable::class)
    abstract fun doInBackground(): T

    /**
     * 执行成功
     *
     * @param result T
     */
    abstract fun onSuccess(result: T)

    /**
     * 执行取消
     */
    abstract fun onCancel()

    /**
     * 执行出错
     *
     * @param t Throwable
     */
    abstract fun onFail(t: Throwable)

    override fun run() {
        if (isSchedule) {
            if (runner == null) {
                // 如果当前值 {@code ==} 是预期值，则自动将值设置为给定的更新值。
                if (!state.compareAndSet(NEW, RUNNING)) {
                    return
                }
                runner = Thread.currentThread()
                if (mTimeoutListener != null) {
                    Log.w("ThreadUtils", "Scheduled task doesn't support timeout.")
                }
            } else {
                if (state.get() != RUNNING) {
                    return
                }
            }
        } else {
            // 如果当前值 {@code ==} 是预期值，则自动将值设置为给定的更新值。
            if (!state.compareAndSet(NEW, RUNNING)) {
                return
            }
            runner = Thread.currentThread()
            if (mTimeoutListener != null) {
                mTimer = Timer()
                mTimer!!.schedule(
                    object : TimerTask() {
                        override fun run() {
                            if (!isDone && mTimeoutListener != null) {
                                timeout()
                                mTimeoutListener!!.onTimeout()
                                onDone()
                            }
                        }
                    },
                    mTimeoutMillis,
                )
            }
        }
    }

    @JvmOverloads
    fun cancel(mayInterruptIfRunning: Boolean = true) {
        synchronized(state) {
            if (state.get() > RUNNING) {
                return
            }
            state.set(CANCELLED)
        }
        if (mayInterruptIfRunning) {
            if (runner != null) {
                runner!!.interrupt()
            }
        }
        getDeliver().execute {
            onCancel()
            onDone()
        }
    }

    val isCanceled: Boolean
        get() = state.get() >= CANCELLED
    val isDone: Boolean
        get() = state.get() > RUNNING

    fun setDeliver(deliver: Executor?): Task<T> {
        this.deliver = deliver
        return this
    }

    /**
     * Scheduled task doesn't support timeout.
     */
    fun setTimeout(
        timeoutMillis: Long,
        listener: OnTimeoutListener?,
    ): Task<T> {
        mTimeoutMillis = timeoutMillis
        mTimeoutListener = listener
        return this
    }

    fun setSchedule(isSchedule: Boolean) {
        this.isSchedule = isSchedule
    }

    private fun timeout() {
        synchronized(state) {
            if (state.get() > RUNNING) {
                return
            }
            state.set(TIMEOUT)
        }
        if (runner != null) {
            runner!!.interrupt()
        }
    }

    private fun getDeliver(): Executor {
        return deliver ?: ThreadUtils.globalDeliver
    }

    @CallSuper
    protected fun onDone() {
        ThreadUtils.TASK_POOL_MAP.remove(this)
        if (mTimer != null) {
            mTimer!!.cancel()
            mTimer = null
            mTimeoutListener = null
        }
    }

    interface OnTimeoutListener {
        fun onTimeout()
    }

    companion object {
        /**
         * 线程状态
         */
        private const val NEW = 0
        private const val RUNNING = 1
        private const val EXCEPTIONAL = 2
        private const val COMPLETING = 3
        private const val CANCELLED = 4
        private const val INTERRUPTED = 5
        private const val TIMEOUT = 6
    }
}
