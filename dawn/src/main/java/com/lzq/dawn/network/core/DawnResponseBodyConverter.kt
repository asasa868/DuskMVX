package com.lzq.dawn.network.core

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonToken
import com.lzq.dawn.DawnConstants
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
                    throw DawnException(
                        DawnConstants.NetWorkConstants.JSON_IO_CODE,
                        "JSON document was not fully consumed."
                    )
                }
                result
            }
        } else {
            val jsonReader = gson.newJsonReader(value.charStream())
            val jsonObject = gson.fromJson<JsonObject>(jsonReader, JsonObject::class.java)

            val code = jsonObject.get(responseCode?.getCodeName())
            val message = jsonObject.get(responseCode?.getMessageName())
            val data = jsonObject.get(responseCode?.getDataName())


            /**
             * 如果code为不是成功的code，那么就抛出异常，统一处理
             */
            return try {
                if (responseCode?.isSuccessCode() == code.asInt) {
                    adapter.fromJsonTree(data)
                } else {
                    throw DawnException(code.asInt, message.asString)
                }
            } catch (e: DawnException) {
                exception.handleError(DawnException(e.code, e.message))
                throw e
            } finally {
                value.close()
            }
        }
    }

}