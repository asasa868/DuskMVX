package com.lzq.dawn.network.core

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.lzq.dawn.DawnConstants.NetWorkConstants.MEDIA_TYPE
import com.lzq.dawn.DawnConstants.NetWorkConstants.UTF_8
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import retrofit2.Converter
import java.io.OutputStreamWriter
import java.io.Writer

/**
 * @projectName com.lzq.dawn.network
 * @author Lzq
 * @date : Created by Lzq on 2024/1/26 15:28
 * @version 0.0.21
 * @description: gson请求头格式化
 */
class DawnRequestBodyConverter<T : Any>(private var gson: Gson?, private var adapter: TypeAdapter<T>?) :
    Converter<T, RequestBody> {

    override fun convert(value: T): RequestBody {
        val buffer = Buffer()
        val writer: Writer = OutputStreamWriter(buffer.outputStream(), UTF_8)
        val jsonWriter = gson!!.newJsonWriter(writer)
        adapter!!.write(jsonWriter, value)
        jsonWriter.close()

        return buffer.readByteString().toRequestBody(MEDIA_TYPE)
    }
}