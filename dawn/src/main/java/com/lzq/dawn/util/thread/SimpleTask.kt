package com.lzq.dawn.util.thread

import android.util.Log

/**
 * @Name :SimpleTask
 * @Time :2022/9/2 14:46
 * @Author :  Lzq
 * @Desc :
 */
abstract class SimpleTask<T> : Task<T>() {
    override fun onCancel() {
        Log.e("ThreadUtils", "onCancel: " + Thread.currentThread())
    }

    override fun onFail(t: Throwable) {
        Log.e("ThreadUtils", "onFail: ", t)
    }
}