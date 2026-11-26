package com.lzq.dawn.network.bean

import retrofit2.Response

/**
 * @projectName com.lzq.dawn.network
 * @author Lzq
 * @date : Created by Lzq on 2024/1/26 17:45
 * @version 0.0.21
 * @description: 网络请求结果
 *
 * 网络请求结果，保留既有数据类的构造入口与错误码约定。
 */
sealed class DawnHttpResult<out T> {

    /**
     * 原始响应，可读取状态码、响应头和错误体；传输或解析失败时为空。
     * 错误体是单次读取资源，调用方使用后应关闭。
     */
    var httpResponse: Response<*>? = null
        internal set

    /** 请求成功 */
    data class Success<T>(val data: T) : DawnHttpResult<T>()

    /** 请求失败，保留原有错误码与消息。 */
    data class Failure<Nothing>(val code: Int, val message: String) : DawnHttpResult<Nothing>()


}
