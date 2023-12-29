package com.lzq.dawn.util.crash;

import androidx.annotation.NonNull;

import com.lzq.dawn.DawnBridge;

import java.util.Map;

/**
 * @Name :CrashInfo
 * @Time :2022/7/20 14:57
 * @Author :  Lzq
 * @Desc :
 */
public final class CrashInfo {
    private final DawnBridge.FileHead mFileHeadProvider;
    private final Throwable mThrowable;

    CrashInfo(String time, Throwable throwable) {
        mThrowable = throwable;
        mFileHeadProvider = new DawnBridge.FileHead("Crash");
        mFileHeadProvider.addFirst("Time Of Crash", time);
    }

    public final void addExtraHead(Map<String, String> extraHead) {
        mFileHeadProvider.append(extraHead);
    }

    public final void addExtraHead(String key, String value) {
        mFileHeadProvider.append(key, value);
    }

    public final Throwable getThrowable() {
        return mThrowable;
    }

    @NonNull
    @Override
    public String toString() {
        return mFileHeadProvider.toString() + DawnBridge.getFullStackTrace(mThrowable);
    }
}
