package com.lzq.dawn.util.activity

import android.app.Activity

/**
 * className :OnAppStatusChangedListener
 * createTime :2022/7/8 15:55
 *
 * @Author :  Lzq
 */
interface OnAppStatusChangedListener {
    /**
     * 在前台的activity
     * @param activity activity
     */
    fun onForeground(activity: Activity?)

    /**
     * 在后台的activity
     * @param activity activity
     */
    fun onBackground(activity: Activity?)
}