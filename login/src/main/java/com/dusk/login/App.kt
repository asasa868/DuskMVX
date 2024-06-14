package com.dusk.login

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.alibaba.android.arouter.launcher.ARouter
import com.google.auto.service.AutoService
import com.lzq.dawn.BuildConfig
import com.lzq.dawn.DawnBridge
import com.lzq.dawn.base.app.BaseApplicationLifecycle
import com.lzq.dawn.util.log.LogUtils
import com.lzq.dawn.util.process.ProcessUtils

/**
 * @projectName com.dusk.login
 * @author Lzq
 * @date : Created by Lzq on 2024/6/4 15:03
 * @version
 * @description:
 */
@AutoService(BaseApplicationLifecycle::class)
class App : BaseApplicationLifecycle {
    override fun attachBaseContext(context: Context?) {

    }

    override fun onCreate(application: Application) {
        LogUtils.d("login onCreate")
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