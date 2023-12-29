package com.lzq.dawn.util.messenger;

import android.os.Bundle;

/**
 * @Name :MessageCallback
 * @Time :2022/8/15 16:07
 * @Author :  Lzq
 * @Desc :
 */
public interface MessageCallback {
    /**
     * message监听
     *
     * @param data data
     */
    void messageCall(Bundle data);
}
