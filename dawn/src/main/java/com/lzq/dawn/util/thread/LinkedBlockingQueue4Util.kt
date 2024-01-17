package com.lzq.dawn.util.thread

import java.util.concurrent.LinkedBlockingQueue

/**
 * @Name :LinkedBlockingQueue4Util
 * @Time :2022/9/2 14:48
 * @Author :  Lzq
 * @Desc :
 */
class LinkedBlockingQueue4Util : LinkedBlockingQueue<Runnable> {
    @Volatile
    private var mPool: ThreadPoolExecutor4Util? = null
    private var mCapacity = Int.MAX_VALUE

    internal constructor() : super()
    internal constructor(isAddSubThreadFirstThenAddQueue: Boolean) : super() {
        if (isAddSubThreadFirstThenAddQueue) {
            mCapacity = 0
        }
    }

    internal constructor(capacity: Int) : super() {
        mCapacity = capacity
    }

    override fun offer(runnable: Runnable): Boolean {
        return if (mCapacity <= size && mPool != null && mPool!!.poolSize < mPool!!.maximumPoolSize) {
            // create a non-core thread
            false
        } else super.offer(
            runnable
        )
    }

    fun setmPool(mPool: ThreadPoolExecutor4Util?) {
        this.mPool = mPool
    }

    fun getmPool(): ThreadPoolExecutor4Util? {
        return mPool
    }
}