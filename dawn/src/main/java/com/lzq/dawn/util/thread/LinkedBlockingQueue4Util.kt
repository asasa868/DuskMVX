package com.lzq.dawn.util.thread;

import androidx.annotation.NonNull;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Name :LinkedBlockingQueue4Util
 * @Time :2022/9/2 14:48
 * @Author :  Lzq
 * @Desc :
 */
public class LinkedBlockingQueue4Util extends LinkedBlockingQueue<Runnable> {

    private volatile ThreadPoolExecutor4Util mPool;

    private int mCapacity = Integer.MAX_VALUE;

    LinkedBlockingQueue4Util() {
        super();
    }

    LinkedBlockingQueue4Util(boolean isAddSubThreadFirstThenAddQueue) {
        super();
        if (isAddSubThreadFirstThenAddQueue) {
            mCapacity = 0;
        }
    }

    LinkedBlockingQueue4Util(int capacity) {
        super();
        mCapacity = capacity;
    }

    @Override
    public boolean offer(@NonNull Runnable runnable) {
        if (mCapacity <= size() &&
                mPool != null && mPool.getPoolSize() < mPool.getMaximumPoolSize()) {
            // create a non-core thread
            return false;
        }
        return super.offer(runnable);
    }

    public void setmPool(ThreadPoolExecutor4Util mPool) {
        this.mPool = mPool;
    }

    public ThreadPoolExecutor4Util getmPool() {
        return mPool;
    }
}