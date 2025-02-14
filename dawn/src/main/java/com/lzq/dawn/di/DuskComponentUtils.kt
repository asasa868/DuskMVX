package com.lzq.dawn.di


object DuskComponentUtils {

    lateinit var component: DuskComponent

    fun init(){
        component = DaggerDuskComponent.builder().build()
    }
}