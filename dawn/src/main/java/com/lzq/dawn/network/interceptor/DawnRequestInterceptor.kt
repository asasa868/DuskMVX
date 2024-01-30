package com.lzq.dawn.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * @projectName com.lzq.dawn.network.interceptor
 * @author Lzq
 * @date : Created by Lzq on 2024/1/29 10:26
 * @version 0.0.21
 * @description: 通用的请求拦截器
 */
class DawnRequestInterceptor :Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        builder.addHeader("Cache-Control", "max-age=0")
        builder.addHeader("Upgrade-Insecure-Requests", "1")
        builder.addHeader("X-Platform", "Android")
        builder.addHeader("Connection", "Keep-Alive")

        return chain.proceed(builder.build())
    }

}