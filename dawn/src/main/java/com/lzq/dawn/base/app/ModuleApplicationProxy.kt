package com.lzq.dawn.base.app

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import java.util.ServiceLoader

/**
 * @projectName com.lzq.dawn.base.app
 * @author Lzq
 * @description: 多模块开发 子模块application的代理类
 * @date : Created by Lzq on 2023/12/21 17:44
 * @version 0.0.1
 */
internal class ModuleApplicationProxy : BaseApplicationLifecycle {

    companion object {
        private var instance: ModuleApplicationProxy? = null

        @Synchronized
        fun getInstance(): ModuleApplicationProxy {
            if (instance == null) {
                instance = ModuleApplicationProxy()
            }
            return instance!!
        }
    }

    private var moduleApplications: ServiceLoader<BaseApplicationLifecycle> =
        ServiceLoader.load(BaseApplicationLifecycle::class.java)


    override fun attachBaseContext(context: Context?) {

        moduleApplications.forEach {
            it.attachBaseContext(context)
        }
    }

    override fun onCreate(application: Application) {
        moduleApplications.forEach {
            it.onCreate(application)
        }
    }

    override fun onTerminate(application: Application) {
        moduleApplications.forEach {
            it.onTerminate(application)
        }
    }

    override fun onLowMemory(application: Application) {
        moduleApplications.forEach {
            it.onLowMemory(application)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        moduleApplications.forEach {
            it.onConfigurationChanged(newConfig)
        }
    }

    override fun initForeground(): MutableList<() -> String> {
        val list: MutableList<() -> String> = mutableListOf()
        moduleApplications.forEach { list.addAll(it.initForeground()) }
        return list
    }

    override fun initBackground() {
        moduleApplications.forEach {
            it.initBackground()
        }
    }


}