package com.lzq.dawn.network.error

import com.lzq.dawn.util.log.LogUtils

/**
 * @projectName com.lzq.dawn.network
 * @author Lzq
 * @date : Created by Lzq on 2024/1/26 16:31
 * @version 0.0.21
 * @description: 全局处理错误
 */
class DawnErrorHandler : IErrorHandler {

    /**
     * 实现这个方法来处理错误
     * 这里只是简单打印日志
     * 可以根据自己的需求来处理错误
     * 如:弹出toast,弹出dialog,跳转到登录页面等等
     */
    override fun handleError(dawnException: DawnException) {
        when (dawnException.code) {
            else -> {
                LogUtils.d("DawnErrorHandler---handleError:code:${dawnException.code} message:${dawnException.message}")
            }
        }
    }


}