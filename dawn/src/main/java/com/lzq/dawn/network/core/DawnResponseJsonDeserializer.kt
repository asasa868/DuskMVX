package com.lzq.dawn.network.core

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.lzq.dawn.DawnConstants
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
class DawnResponseJsonDeserializer<T> : JsonDeserializer<T> {
    override fun deserialize(
        json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?
    ): T {

        val resultBean = if (typeOfT is ParameterizedType) {
            typeOfT.actualTypeArguments.firstOrNull()
        } else {
            throw DawnException(DawnConstants.NetWorkConstants.JSON_PARSE_CODE, "Type must be parameterized")
        }

        return context!!.deserialize(json, resultBean)
    }

}