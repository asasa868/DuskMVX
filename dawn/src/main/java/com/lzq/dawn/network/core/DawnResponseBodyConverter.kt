package com.lzq.dawn.network.core

import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonObject
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonToken
import com.lzq.dawn.DawnConstants.NetWorkConstants.RESPONSE_ANALYZE_CODE
import com.lzq.dawn.network.error.DawnException
import com.lzq.dawn.network.error.IErrorHandler
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException

/**
 * @projectName com.lzq.dawn.network
 * @author Lzq
 * @date : Created by Lzq on 2024/1/26 15:49
 * @version 0.0.21
 * @description: 自定义响应体解析
 */
class DawnResponseBodyConverter<T>(
    private var gson: Gson,
    private var adapter: TypeAdapter<T>,
    private var responseCode: IResponseCode?,
    private val exception: IErrorHandler
) : Converter<ResponseBody, T> {

    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T? {
        if (responseCode == null) {
            val jsonReader = gson.newJsonReader(value.charStream())
            return value.use {
                val result = adapter.read(jsonReader)
                if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                    throw JsonIOException("JSON document was not fully consumed.")
                }
                result
            }
        } else {
            val jsonReader = gson.newJsonReader(value.charStream())
            val jsonObject = gson.fromJson<JsonObject>(jsonReader, JsonObject::class.java)

            /**
             * 服务端返回的数据
             * 使用[IResponseCode]其中的字段进行解析
             *
             */
            if (jsonObject.get(responseCode?.getCodeName()) == null) {
                exception.handleError(
                    DawnException(
                        RESPONSE_ANALYZE_CODE, "${responseCode?.getCodeName()} Field not present"
                    )
                )
            }
            if (jsonObject.get(responseCode?.getMessageName()) == null) {
                exception.handleError(
                    DawnException(
                        RESPONSE_ANALYZE_CODE, "${responseCode?.getMessageName()} Field not present"
                    )
                )
            }
            if (jsonObject.get(responseCode?.getDataName()) == null) {
                exception.handleError(
                    DawnException(
                        RESPONSE_ANALYZE_CODE, "${responseCode?.getDataName()} Field not present"
                    )
                )
            }
            val code = jsonObject.get(responseCode?.getCodeName())
            val message = jsonObject.get(responseCode?.getMessageName())


            /**
             * 如果code为不是成功的code，那么就抛出异常，统一处理
             */
            return if (responseCode?.isSuccessCode() == code.asInt) {
                adapter.fromJson(jsonObject.toString()) as T
            } else {
                exception.handleError(
                    DawnException(
                        code.asInt,
                        message.asString
                    )
                )
               null
            }
        }
    }

}