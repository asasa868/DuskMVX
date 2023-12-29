package com.lzq.dawn.base.app

import android.content.Context
import android.content.res.Configuration
import androidx.multidex.MultiDexApplication
import com.lzq.dawn.util.log.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

/**
 * @projectName com.lzq.dawn.base
 * @author Lzq
 * @description: 最初始的Application,仅用来实现多模块的Application代理
 * @date : Created by Lzq on 2023/12/21 13:47
 * @version 0.0.1
 * 用法：
 * 1.先依赖谷歌的AutoService库，@link[Google.autoService]
 * 2.在创建一个Application的实现类实现@link[BaseApplicationLifecycle]
 * 3.在实现类上面使用注解 @AutoService(BaseApplicationLifecycle::class)
 */


 open class Application internal constructor(): MultiDexApplication() {

    private val applicationProxy by lazy(mode = LazyThreadSafetyMode.NONE) { ModuleApplicationProxy() }

    private val mainScope by lazy(mode = LazyThreadSafetyMode.NONE) { MainScope() }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        applicationProxy.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        applicationProxy.onCreate(this)
        initData()
    }

    override fun onTerminate() {
        super.onTerminate()
        applicationProxy.onTerminate(this)
        mainScope.cancel()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        applicationProxy.onLowMemory(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        applicationProxy.onConfigurationChanged(newConfig)
    }

    private fun initData() {
        mainScope.launch(Dispatchers.Default) {
            applicationProxy.initBackground()
        }
        // 前台初始化
        val allTimeMillis = measureTimeMillis {
            val depends = applicationProxy.initForeground()
            var dependInfo: String
            depends.forEach {
                val dependTimeMillis = measureTimeMillis { dependInfo = it() }
                LogUtils.d("initDepends: $dependInfo : $dependTimeMillis ms")
            }
        }
        LogUtils.d("初始化完成 $allTimeMillis ms")
    }

}

