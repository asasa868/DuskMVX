package com.lzq.dawn.base

/**
 * @projectName com.lzq.dawn.base
 * @author Lzq
 * @date : Created by Lzq on 2023/12/25 17:17
 * @version 0.0.1
 * @description: 注册EventBus
 * 使用方法
 * @RegisterEventBus
 * class xxxActivity:BaseXXXActivity()
 *
 */

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterEventBus
