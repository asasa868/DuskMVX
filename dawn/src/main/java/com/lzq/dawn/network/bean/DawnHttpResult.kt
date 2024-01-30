package com.lzq.dawn.network.bean

/**
 * @projectName com.lzq.dawn.network
 * @author Lzq
 * @date : Created by Lzq on 2024/1/26 17:45
 * @version 0.0.21
 * @description: 网络请求结果
 */
sealed class DawnHttpResult<out T> {

    /**
     * 请求成功
     */
    data class Success<T>(val data: T) : DawnHttpResult<T>()

    /**
     * 请求失败
     */
    data class Failure<Nothing>(val code: Int, val message: String) : DawnHttpResult<Nothing>()


}