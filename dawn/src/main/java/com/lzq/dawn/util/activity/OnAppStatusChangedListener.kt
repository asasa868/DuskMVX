package com.lzq.dawn.util.activity;

import android.app.Activity;

/**
 * className :OnAppStatusChangedListener
 * createTime :2022/7/8 15:55
 *
 * @Author :  Lzq
 */
public interface OnAppStatusChangedListener {
    /**
     * 在前台的activity
     * @param activity activity
     */
    void onForeground(Activity activity);

    /**
     * 在后台的activity
     * @param activity activity
     */
    void onBackground(Activity activity);
}
