package com.lzq.dawn.network.core

import com.lzq.dawn.DawnConstants.NetWorkConstants.RESPONSE_BODY_NULL_CODE
import com.lzq.dawn.DawnConstants.NetWorkConstants.RESPONSE_EXCEPTION_NULL_CODE
import com.lzq.dawn.network.bean.DawnHttpResult
import com.lzq.dawn.network.error.DawnException
import com.lzq.dawn.network.error.IErrorHandler
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicReference

/**
 * @projectName com.lzq.dawn.network
 * @author Lzq
 * @date : Created by Lzq on 2024/1/26 17:14
 * @version 0.0.21
 * @description: 响应体解析
 *
 * 将底层 Retrofit 调用映射为框架结果，并保留原始响应和取消语义。
 */
internal class DawnResponseCall<T : Any>(
    private val call: Call<T>,
    private val errorHandler: IErrorHandler?
) : Call<DawnHttpResult<T>> {

    override fun enqueue(callback: Callback<DawnHttpResult<T>>) {
        val terminal = AtomicReference<Any?>()
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (!terminal.compareAndSet(null, response)) {
                    discardResponse(response, terminal.get() as? Response<*>)
                    return
                }
                if (call.isCanceled) {
                    discardResponse(response)
                    callback.onFailure(this@DawnResponseCall, IOException("Canceled"))
                    return
                }
                val mapped = try {
                    mapResponse(response)
                } catch (failure: Throwable) {
                    discardResponse(response)
                    callback.onFailure(this@DawnResponseCall, failure)
                    return
                }
                callback.onResponse(this@DawnResponseCall, mapped)
            }

            override fun onFailure(call: Call<T>, failure: Throwable) {
                if (!terminal.compareAndSet(null, failure)) return
                if (call.isCanceled || failure is CancellationException) {
                    callback.onFailure(this@DawnResponseCall, failure)
                    return
                }
                val mapped = try {
                    if (failure is DawnException) failure.handleOnce(errorHandler)
                    Response.success<DawnHttpResult<T>>(
                        DawnHttpResult.Failure(
                            RESPONSE_EXCEPTION_NULL_CODE,
                            failure.message ?: "Throwable message is null"
                        )
                    )
                } catch (handlerFailure: Throwable) {
                    callback.onFailure(this@DawnResponseCall, handlerFailure)
                    return
                }
                callback.onResponse(this@DawnResponseCall, mapped)
            }
        })
    }

    /**
     * 这里的code，是http状态码，不是业务状态码
     * 具体查看http状态码(网络查找)或者
     * [NetWorkConstants]
     */
    private fun mapResponse(response: Response<T>): Response<DawnHttpResult<T>> {
        val result: DawnHttpResult<T> = when {
            // 保留既有异步适配器将列出的重定向码按实体映射的消费者行为。
            response.isSuccessful || response.code() in 300..308 -> response.body()?.let {
                DawnHttpResult.Success(it)
            } ?: DawnHttpResult.Failure(RESPONSE_BODY_NULL_CODE, "response body is null")
            else -> {
                DawnException(response.code(), response.message()).handleOnce(errorHandler)
                DawnHttpResult.Failure(response.code(), response.message())
            }
        }
        result.httpResponse = response
        // HTTP 错误继续通过 Failure 交付；原始 HTTP 响应保存在结果上。
        return if (response.isSuccessful) Response.success(result, response.raw())
        else Response.success(result, response.headers())
    }

    override fun execute(): Response<DawnHttpResult<T>> {
        val response = call.execute()
        if (call.isCanceled) {
            discardResponse(response)
            throw IOException("Canceled")
        }
        if (!response.isSuccessful) {
            try {
                DawnException(response.code(), response.message()).handleOnce(errorHandler)
            } catch (failure: Throwable) {
                discardResponse(response)
                throw failure
            }
            return Response.error(response.errorBody() ?: "".toResponseBody(null), response.raw())
        }
        val result: DawnHttpResult<T> = response.body()?.let { DawnHttpResult.Success(it) }
            ?: DawnHttpResult.Failure(RESPONSE_BODY_NULL_CODE, "response body is null")
        result.httpResponse = response
        return Response.success(result, response.raw())
    }

    private fun discardResponse(response: Response<T>, retained: Response<*>? = null) {
        val errorBody = response.errorBody()
        if (errorBody !== retained?.errorBody()) errorBody?.close()
        val body = response.body() as? ResponseBody
        if (body !== retained?.body()) body?.close()
    }

    override fun isExecuted() = call.isExecuted
    override fun clone() = DawnResponseCall(call.clone(), errorHandler)
    override fun isCanceled() = call.isCanceled
    override fun cancel() = call.cancel()
    override fun request(): Request = call.request()
    override fun timeout(): Timeout = call.timeout()
}
