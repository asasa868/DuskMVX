package com.lzq.dawn.network.core

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.lzq.dawn.DawnConstants
import com.lzq.dawn.network.bean.DawnHttpResult
import com.lzq.dawn.network.error.DawnException
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
) : JsonDeserializer<T> {
    override fun deserialize(
        json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?
    ): T {
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
            throw DawnException(DawnConstants.NetWorkConstants.JSON_PARSE_CODE, "Type must be parameterized")
        }

        return context!!.deserialize(data, dataType)
    }

}