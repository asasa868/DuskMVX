package com.lzq.dawn.network.core

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
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
 *
 * 根据可选业务响应契约读取实体，并在所有解析出口释放响应体。
 * 自定义信封解析器优先于旧字段契约，未配置时保留原有解析行为。
 *
 * @param envelopeParser 可选的响应信封解析器，只提取数据节点而不改变目标数据类型。
 */
class DawnResponseBodyConverter<T> @JvmOverloads constructor(
    private var gson: Gson,
    private var adapter: TypeAdapter<T>,
    private var responseCode: IResponseCode?,
    private val exception: IErrorHandler,
    private val envelopeParser: IResponseEnvelopeParser? = null
) : Converter<ResponseBody, T> {

    /**
     * 业务失败抛出原业务异常，解析和取消异常继续传播，响应体始终关闭。
     *
     * 如果code为不是成功的code，那么就抛出异常，统一处理
     */
    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T? = value.use {
        val jsonReader = gson.newJsonReader(it.charStream())
        val contract = responseCode
        if (envelopeParser != null) {
            val response = gson.fromJson<JsonElement>(jsonReader, JsonElement::class.java)
                ?: throw JsonParseException("响应体不包含有效的响应节点")
            if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                throw JsonParseException("响应体包含多余的响应节点")
            }
            val data = try {
                envelopeParser.parse(response)
            } catch (failure: DawnException) {
                failure.handleOnce(exception)
                throw failure
            }
            adapter.fromJsonTree(data ?: JsonNull.INSTANCE)
        } else if (contract == null) {
            val result = adapter.read(jsonReader)
            if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                throw DawnException(
                    DawnConstants.NetWorkConstants.JSON_IO_CODE,
                    "JSON document was not fully consumed."
                )
            }
            result
        } else {
            val jsonObject = gson.fromJson<JsonObject>(jsonReader, JsonObject::class.java)
            val code = jsonObject.get(contract.getCodeName())
            val message = jsonObject.get(contract.getMessageName())
            val data = jsonObject.get(contract.getDataName())
            if (contract.isSuccessCode() == code.asInt) {
                adapter.fromJsonTree(data)
            } else {
                val failure = DawnException(code.asInt, message.asString)
                failure.handleOnce(exception)
                throw failure
            }
        }
    }
}
