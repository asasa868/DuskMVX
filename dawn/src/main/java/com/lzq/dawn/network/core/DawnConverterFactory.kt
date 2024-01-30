package com.lzq.dawn.network.core

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.lzq.dawn.network.bean.DawnHttpResult
import com.lzq.dawn.network.error.IErrorHandler
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * @projectName com.lzq.dawn.network
 * @author Lzq
 * @date : Created by Lzq on 2024/1/26 15:11
 * @version 0.0.21
 * @description: 自定义响应体解析方式
 */
class DawnConverterFactory(
    private var gson: Gson,
    private var responseCode: IResponseCode?,
    private val exception: IErrorHandler
) : Converter.Factory() {

    companion object {
        fun create(responseCode: IResponseCode?, exception: IErrorHandler): DawnConverterFactory {
            return create(
                GsonBuilder()
                    .serializeNulls()
                    .disableHtmlEscaping()
                    .registerTypeAdapter(DawnHttpResult::class.java, DawnResponseJsonDeserializer<Any>(responseCode))
                    .create(),
                responseCode,
                exception
            )
        }

        fun create(
            gson: Gson?,
            responseCode: IResponseCode?,
            exception: IErrorHandler
        ): DawnConverterFactory {
            if (gson == null) throw NullPointerException("gson == null")
            return DawnConverterFactory(gson, responseCode, exception)
        }
    }

    override fun responseBodyConverter(
        type: Type, annotations: Array<out Annotation>, retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return DawnResponseBodyConverter(gson, adapter, responseCode,exception)
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return DawnRequestBodyConverter(gson, adapter)
    }

}