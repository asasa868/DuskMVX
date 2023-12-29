package com.lzq.dawn.util.thread;

import android.util.Log;

/**
 * @Name :SimpleTask
 * @Time :2022/9/2 14:46
 * @Author :  Lzq
 * @Desc :
 */
public abstract class SimpleTask<T> extends Task<T> {

    @Override
    public void onCancel() {
        Log.e("ThreadUtils", "onCancel: " + Thread.currentThread());
    }

    @Override
    public void onFail(Throwable t) {
        Log.e("ThreadUtils", "onFail: ", t);
    }

}