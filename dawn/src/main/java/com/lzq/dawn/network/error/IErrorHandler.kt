package com.lzq.dawn.network.error

/**
 * @projectName com.lzq.dawn.network
 * @author Lzq
 * @date : Created by Lzq on 2024/1/26 16:32
 * @version 0.0.21
 * @description: 全局处理错误
 */
interface IErrorHandler {

    /**
     * 处理错误
     * @param dawnException DawnException
     */
    fun handleError(dawnException: DawnException)
}