package com.lzq.dawn.base.app

import android.app.Application
import android.content.Context
import android.content.res.Configuration

/**
 * @projectName com.lzq.dawn.base.app
 * @author Lzq
 * @description: Application 生命周期 解决多模块开发，但是只能有一个Application的问题
 * @date : Created by Lzq on 2023/12/21 17:36
 * @version 0.0.1
 */
interface BaseApplicationLifecycle {

    /**
     * [Application.attachBaseContext]
     * @param context Context
     */
    fun attachBaseContext(context: Context?)

    /**
     * [Application.onCreate]
     */
    fun onCreate(application: Application)

    /**
     * [Application.onTerminate]
     */
    fun onTerminate(application: Application)

    /**
     * [Application.onLowMemory]
     */
    fun onLowMemory(application: Application)

    /**
     * [Application.onConfigurationChanged]
     */
    fun onConfigurationChanged(newConfig: Configuration)

    /**
     * 立即初始化，
     */
    fun initForeground(): MutableList<() -> String>

    /**
     * 非立即初始化的 可以放在这个方法中
     */
    fun initBackground()


}