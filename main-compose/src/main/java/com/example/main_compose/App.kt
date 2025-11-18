package com.example.main_compose

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.google.auto.service.AutoService
import com.lzq.dawn.base.app.BaseApplicationLifecycle
import com.lzq.dawn.util.log.LogUtils

/**
 * @projectName com.example.main_compose
 * @author Lzq
 * @date : Created by Lzq on 2024/6/28 15:06
 * @version
 * @description: application
 */
@AutoService(BaseApplicationLifecycle::class)
class App : BaseApplicationLifecycle {
    override fun attachBaseContext(context: Context?) {

    }

    override fun onCreate(application: Application) {
        LogUtils.d("main compose onCreate")
    }

    override fun onTerminate(application: Application) {

    }

    override fun onLowMemory(application: Application) {

    }

    override fun onConfigurationChanged(newConfig: Configuration) {

    }

    override fun initForeground(): MutableList<() -> String> {
       return mutableListOf()
    }

    override fun initBackground() {

    }
}