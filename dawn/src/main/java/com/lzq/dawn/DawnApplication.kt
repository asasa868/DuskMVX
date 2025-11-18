package com.lzq.dawn

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.google.auto.service.AutoService
import com.lzq.dawn.base.app.BaseApplicationLifecycle
import com.lzq.dawn.util.execution.process.ProcessUtils

/**
 * @projectName com.lzq.dawn
 * @author Lzq
 * @date : Created by Lzq on 2023/12/28 11:52
 * @version 0.0.1
 * @description: Dawn框架的Application
 */

@AutoService(BaseApplicationLifecycle::class)
class DawnApplication : BaseApplicationLifecycle {
    override fun attachBaseContext(context: Context?) {

    }

    override fun onCreate(application: Application) {
        DawnBridge.init(application)
    }

    override fun onTerminate(application: Application) {

    }

    override fun onLowMemory(application: Application) {

    }

    override fun onConfigurationChanged(newConfig: Configuration) {

    }

    override fun initForeground(): MutableList<() -> String> {
        val list = mutableListOf<() -> String>()
        if (ProcessUtils.isMainProcess) {
            list.add { initRouter() }
        }
        return list
    }

    override fun initBackground() {

    }


    private fun initRouter(): String {

        return "Router -->> init complete"
    }

}