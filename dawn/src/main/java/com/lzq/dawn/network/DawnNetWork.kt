package com.lzq.dawn.network

import com.lzq.dawn.DawnConstants
import com.lzq.dawn.network.core.DawnCallAdapterFactory
import com.lzq.dawn.network.core.DawnConverterFactory
import com.lzq.dawn.network.core.IResponseCode
import com.lzq.dawn.network.error.DawnErrorHandler
import com.lzq.dawn.network.error.IErrorHandler
import com.lzq.dawn.network.interceptor.DawnRequestInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * @projectName com.lzq.dawn.network
 * @author Lzq
 * @date : Created by Lzq on 2024/1/25 14:12
 * @version 0.0.21
 * @description: dawn网络框架
 */
abstract class DawnNetWork<T>(private val clazz: Class<T>) {

    /**
     * 通过懒加载的方式创建Retrofit
     */
    private val mRetrofit: Retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl(getBaseUrl())
            .client(createOkhttpClient())
            .addConverterFactory(DawnConverterFactory.create(getHttpResponseCode(),getErrorExceptionHandler()))
            .addCallAdapterFactory(DawnCallAdapterFactory.create(getErrorExceptionHandler()))
            .build()
    }


    /**
     * 创建OkhttpClient
     * 子类可重写，自己实现指定的OkhttpClient
     */
    protected open fun createOkhttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (isDebug()) {
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
                builder.addInterceptor(this)
            }
        }
        if (addInterceptor() != null) {
            for (interceptor in addInterceptor()!!) {
                builder.addInterceptor(interceptor)
            }
        }
        builder.addInterceptor { chain ->
            if (createRequestHeader(chain.request().newBuilder()) == null) {
                return@addInterceptor chain.proceed(chain.request())
            }
            val build = createRequestHeader(chain.request().newBuilder())!!.build()
            chain.proceed(build)
        }
        builder.addInterceptor(DawnRequestInterceptor())
        builder.connectTimeout(DawnConstants.NetWorkConstants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
        builder.readTimeout(DawnConstants.NetWorkConstants.READ_TIMEOUT, TimeUnit.SECONDS)
        builder.writeTimeout(DawnConstants.NetWorkConstants.WRITE_TIMEOUT, TimeUnit.SECONDS)
        builder.callTimeout(DawnConstants.NetWorkConstants.CALL_TIMEOUT, TimeUnit.MINUTES)

        return builder.build()
    }

    /**
     * 添加拦截器
     * 子类可重写，自己实现指定的拦截器
     */
    protected open fun addInterceptor(): MutableList<Interceptor>? {
        return null
    }

    /**
     * 获取服务器返回的数据格式的名字 一般都是code+msg+data组合
     * 子类可以重写，指定服务器返回的数据格式的名字
     */
    protected open fun getHttpResponseCode(): IResponseCode? {
        return null
    }

    /**
     * 获取错误处理器
     * 子类建议重写，指定错误处理器
     */
    protected open fun getErrorExceptionHandler(): IErrorHandler {
        return DawnErrorHandler()
    }

    /**
     * 获取ApiService
     */
    fun getApiService(): T {
        return mRetrofit.create(clazz)
    }

    /**
     * 是否调试模式
     * 子类必须重写，指定是否调试模式
     */
    protected abstract fun isDebug(): Boolean

    /**
     * 获取BaseUrl
     * 子类必须重写，指定BaseUrl
     */
    protected abstract fun getBaseUrl(): String

    /**
     * 创建请求头
     * 子类必须重写，指定请求头
     */
    protected abstract fun createRequestHeader(builder: Request.Builder): Request.Builder?


}