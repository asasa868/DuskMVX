package com.lzq.dawn.network.core

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.lzq.dawn.DawnConstants
import com.lzq.dawn.network.bean.DawnHttpResult
import com.lzq.dawn.network.error.DawnException
import com.lzq.dawn.network.error.IErrorHandler
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @projectName com.lzq.dawn.network
 * @author Lzq
 * @date : Created by Lzq on 2024/1/26 15:11
 * @version 0.0.21
 * @description: 自定义响应体解析方式
 *
 * @param envelopeParser 可选的响应信封解析器，非空时优先于旧字段契约。
 */
class DawnConverterFactory @JvmOverloads constructor(
    private var gson: Gson,
    private var responseCode: IResponseCode?,
    private val exception: IErrorHandler,
    private val envelopeParser: IResponseEnvelopeParser? = null
) : Converter.Factory() {

    companion object {
        /**
         * 使用自定义响应信封解析器，并保留默认的数据序列化配置。
         *
         * @param parser 提取数据节点并判断业务失败的解析器。
         * @param exception 接收业务异常的处理器。
         * @return 使用指定解析器的转换器工厂。
         */
        fun createWithEnvelopeParser(
            parser: IResponseEnvelopeParser,
            exception: IErrorHandler
        ): DawnConverterFactory {
            return createWithEnvelopeParser(defaultGson(), parser, exception)
        }

        /**
         * 使用自定义响应信封解析器和数据序列化配置。
         * 框架结果包装由调用适配器处理，无需另行注册包装类型的解析器。
         *
         * @param gson 数据序列化配置，数据节点仍使用其中注册的类型适配器。
         * @param parser 提取数据节点并判断业务失败的解析器。
         * @param exception 接收业务异常的处理器。
         * @return 使用指定解析器及序列化配置的转换器工厂。
         * @throws DawnException 序列化配置为空。
         */
        fun createWithEnvelopeParser(
            gson: Gson?,
            parser: IResponseEnvelopeParser,
            exception: IErrorHandler
        ): DawnConverterFactory {
            if (gson == null) {
                throw DawnException(DawnConstants.NetWorkConstants.GSON_NULL_CODE, "Gson is null")
            }
            return DawnConverterFactory(gson, null, exception, parser)
        }

        fun create(responseCode: IResponseCode?, exception: IErrorHandler): DawnConverterFactory {
            return create(
                defaultGson(),
                responseCode,
                exception
            )
        }

        fun create(
            gson: Gson?,
            responseCode: IResponseCode?,
            exception: IErrorHandler
        ): DawnConverterFactory {
            if (gson == null){
                throw DawnException(DawnConstants.NetWorkConstants.GSON_NULL_CODE, "Gson is null")
            }
            return DawnConverterFactory(gson, responseCode, exception)
        }

        private fun defaultGson(): Gson = GsonBuilder()
            .serializeNulls()
            .disableHtmlEscaping()
            .registerTypeAdapter(DawnHttpResult::class.java, DawnResponseJsonDeserializer<Any>())
            .create()
    }

    override fun responseBodyConverter(
        type: Type, annotations: Array<out Annotation>, retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        // 自定义信封只交付数据节点，框架结果包装仍由调用适配器创建。
        val dataType = if (envelopeParser != null && type is ParameterizedType &&
            type.rawType == DawnHttpResult::class.java
        ) {
            type.actualTypeArguments[0]
        } else {
            type
        }
        val adapter = gson.getAdapter(TypeToken.get(dataType))
        return DawnResponseBodyConverter(gson, adapter, responseCode, exception, envelopeParser)
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
