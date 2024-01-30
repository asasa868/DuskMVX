package com.lzq.dawn.network.core

import com.lzq.dawn.network.bean.DawnHttpResult
import com.lzq.dawn.network.error.IErrorHandler
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @projectName com.lzq.dawn.network
 * @author Lzq
 * @date : Created by Lzq on 2024/1/26 16:28
 * @version 0.0.21
 * @description: dawn自定义retrofit适配器
 */
class DawnCallAdapterFactory(private val exception: IErrorHandler? = null) : CallAdapter.Factory() {
    companion object {
        fun create(exception: IErrorHandler?): DawnCallAdapterFactory {
            return DawnCallAdapterFactory(exception)
        }
    }

    override fun get(
        returnType: Type, annotations: Array<out Annotation>, retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (Call::class.java != getRawType(returnType)) {
            return null
        }
        check(returnType is ParameterizedType) {
            "return type must be parameterized as Call<DawnHttpResult<<Foo>> or Call<DawnHttpResult<out Foo>>"
        }
        val responseType = getParameterUpperBound(0, returnType)

        if (getRawType(responseType) != DawnHttpResult::class.java) {
            return null
        }
        check(responseType is ParameterizedType) { "Response must be parameterized as DawnHttpResult<Foo> or DawnResponseCall<out Foo>" }

        return object : CallAdapter<Any, Call<*>?> {

            override fun responseType(): Type {
                return responseType
            }

            override fun adapt(call: Call<Any>): Call<*> {
                return DawnResponseCall(call, exception)
            }

        }
    }



}