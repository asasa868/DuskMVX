package com.lzq.dawn.network.core

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.lzq.dawn.network.bean.DawnHttpResult
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @projectName com.lzq.dawn.network.core
 * @author Lzq
 * @date : Created by Lzq on 2024/1/29 14:36
 * @version 0.0.21
 * @description: 自定义响应json解析器
 */
class DawnResponseJsonDeserializer<T>(
    private val responseCode: IResponseCode?
) : JsonDeserializer<DawnHttpResult<T>> {
    override fun deserialize(
        json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?
    ): DawnHttpResult<T> {
        val jsonObject = json?.asJsonObject

        /**
         *  服务端返回的数据
         *  结构为 code+msg+data
         */
        val data = jsonObject?.get(responseCode?.getDataName())

        val type = object : TypeToken<DawnHttpResult<T>>() {}.type
        val dataType = if (type is ParameterizedType) {
            type.actualTypeArguments.firstOrNull()
        } else {
            throw JsonParseException("Type must be parameterized")
        }

        /**
         * 将data解析成对应的类型
         */
        val responseData: T = context!!.deserialize(data, dataType)

        return DawnHttpResult.Success(responseData)
    }

}