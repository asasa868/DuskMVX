package com.lzq.dawn

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.alibaba.android.arouter.launcher.ARouter
import com.google.auto.service.AutoService
import com.lzq.dawn.base.app.BaseApplicationLifecycle
import com.lzq.dawn.util.process.ProcessUtils

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
        if (ProcessUtils.isMainProcess()) {
            list.add { initARouter() }
        }
        return list
    }

    override fun initBackground() {

    }


    /**
     * 阿里路由 ARouter 初始化
     */
    private fun initARouter(): String {
        // 测试环境下打开ARouter的日志和调试模式 正式环境需要关闭
        if (BuildConfig.DEBUG) {
            ARouter.openLog()     // 打印日志
            ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(DawnBridge.getApp())
        return "ARouter -->> init complete"
    }

}