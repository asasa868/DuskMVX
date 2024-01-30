package com.lzq.dawn.network.core

import com.lzq.dawn.DawnConstants.NetWorkConstants
import com.lzq.dawn.DawnConstants.NetWorkConstants.RESPONSE_BODY_NULL_CODE
import com.lzq.dawn.DawnConstants.NetWorkConstants.RESPONSE_EXCEPTION_NULL_CODE
import com.lzq.dawn.network.bean.DawnHttpResult
import com.lzq.dawn.network.error.DawnException
import com.lzq.dawn.network.error.IErrorHandler
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @projectName com.lzq.dawn.network
 * @author Lzq
 * @date : Created by Lzq on 2024/1/26 17:14
 * @version 0.0.21
 * @description: 响应体解析
 */
internal class DawnResponseCall<T : Any>(
    private val call: Call<T>, private val errorHandler: IErrorHandler?
) : Call<DawnHttpResult<T>> {


    override fun enqueue(callback: Callback<DawnHttpResult<T>>) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val code = response.code()
                val message = response.message()
                val data = response.body()

                /**
                 * 这里的code，是http状态码，不是业务状态码
                 * 具体查看http状态码(网络查找)或者
                 * [NetWorkConstants]
                 */
                when (code) {
                    //100-103,400-451,500-511
                    NetWorkConstants.HTTP_CONTINUE_CODE,
                    NetWorkConstants.HTTP_SWITCHING_PROTOCOLS_CODE,
                    NetWorkConstants.HTTP_PROCESSING_CODE,
                    NetWorkConstants.HTTP_EARLY_HINTS_CODE,
                    NetWorkConstants.HTTP_BAD_REQUEST_CODE,
                    NetWorkConstants.HTTP_UNAUTHORIZED_CODE,
                    NetWorkConstants.HTTP_PAYMENT_REQUIRED_CODE,
                    NetWorkConstants.HTTP_FORBIDDEN_CODE,
                    NetWorkConstants.HTTP_NOT_FOUND_CODE,
                    NetWorkConstants.HTTP_METHOD_NOT_ALLOWED_CODE,
                    NetWorkConstants.HTTP_NOT_ACCEPTABLE_CODE,
                    NetWorkConstants.HTTP_PROXY_AUTHENTICATION_REQUIRED_CODE,
                    NetWorkConstants.HTTP_REQUEST_TIMEOUT_CODE,
                    NetWorkConstants.HTTP_CONFLICT_CODE,
                    NetWorkConstants.HTTP_GONE_CODE,
                    NetWorkConstants.HTTP_LENGTH_REQUIRED_CODE,
                    NetWorkConstants.HTTP_PRECONDITION_FAILED_CODE,
                    NetWorkConstants.HTTP_PAYLOAD_TOO_LARGE_CODE,
                    NetWorkConstants.HTTP_URI_TOO_LONG_CODE,
                    NetWorkConstants.HTTP_UNSUPPORTED_MEDIA_TYPE_CODE,
                    NetWorkConstants.HTTP_RANGE_NOT_SATISFIABLE_CODE,
                    NetWorkConstants.HTTP_EXPECTATION_FAILED_CODE,
                    NetWorkConstants.HTTP_TEAPOT_CODE,
                    NetWorkConstants.HTTP_MISDIRECTED_REQUEST_CODE,
                    NetWorkConstants.HTTP_UN_PROCESSABLE_ENTITY_CODE,
                    NetWorkConstants.HTTP_LOCKED_CODE,
                    NetWorkConstants.HTTP_FAILED_DEPENDENCY_CODE,
                    NetWorkConstants.HTTP_TOO_EARLY_CODE,
                    NetWorkConstants.HTTP_UPGRADE_REQUIRED_CODE,
                    NetWorkConstants.HTTP_PRECONDITION_REQUIRED_CODE,
                    NetWorkConstants.HTTP_TOO_MANY_REQUESTS_CODE,
                    NetWorkConstants.HTTP_REQUEST_HEADER_FIELDS_TOO_LARGE_CODE,
                    NetWorkConstants.HTTP_UNAVAILABLE_FOR_LEGAL_REASONS_CODE,
                    NetWorkConstants.HTTP_INTERNAL_SERVER_ERROR_CODE,
                    NetWorkConstants.HTTP_NOT_IMPLEMENTED_CODE,
                    NetWorkConstants.HTTP_BAD_GATEWAY_CODE,
                    NetWorkConstants.HTTP_SERVICE_UNAVAILABLE_CODE,
                    NetWorkConstants.HTTP_GATEWAY_TIMEOUT_CODE,
                    NetWorkConstants.HTTP_HTTP_VERSION_NOT_SUPPORTED_CODE,
                    NetWorkConstants.HTTP_VARIANT_ALSO_NEGOTIATES_CODE,
                    NetWorkConstants.HTTP_INSUFFICIENT_STORAGE_CODE,
                    NetWorkConstants.HTTP_LOOP_DETECTED_CODE,
                    NetWorkConstants.HTTP_NOT_EXTENDED_CODE,
                    NetWorkConstants.HTTP_NETWORK_AUTHENTICATION_REQUIRED_CODE,
                    -> {
                        errorHandler?.handleError(DawnException(code, message))
                        callback.onResponse(
                            this@DawnResponseCall, Response.success(
                                DawnHttpResult.Failure(
                                    code, message
                                )
                            )
                        )
                    }

                    // 200-226 , 300-308
                    NetWorkConstants.HTTP_OK_CODE,
                    NetWorkConstants.HTTP_CREATED_CODE,
                    NetWorkConstants.HTTP_ACCEPTED_CODE,
                    NetWorkConstants.HTTP_NON_AUTHORITATIVE_INFORMATION_CODE,
                    NetWorkConstants.HTTP_NO_CONTENT_CODE,
                    NetWorkConstants.HTTP_RESET_CONTENT_CODE,
                    NetWorkConstants.HTTP_PARTIAL_CONTENT_CODE,
                    NetWorkConstants.HTTP_MULTI_STATUS_CODE,
                    NetWorkConstants.HTTP_ALREADY_REPORTED_CODE,
                    NetWorkConstants.HTTP_IM_USED_CODE,
                    NetWorkConstants.HTTP_MULTIPLE_CHOICES_CODE,
                    NetWorkConstants.HTTP_MOVED_PERMANENTLY_CODE,
                    NetWorkConstants.HTTP_FOUND_CODE,
                    NetWorkConstants.HTTP_SEE_OTHER_CODE,
                    NetWorkConstants.HTTP_NOT_MODIFIED_CODE,
                    NetWorkConstants.HTTP_USE_PROXY_CODE,
                    NetWorkConstants.HTTP_SWITCH_PROXY_UNUSED_CODE,
                    NetWorkConstants.HTTP_TEMPORARY_REDIRECT_CODE,
                    NetWorkConstants.HTTP_PERMANENT_REDIRECT_CODE,
                    -> {
                        if (data != null) {
                            callback.onResponse(
                                this@DawnResponseCall, Response.success(DawnHttpResult.Success(data))
                            )
                        } else {
                            callback.onResponse(
                                this@DawnResponseCall, Response.success(
                                    DawnHttpResult.Failure(
                                        RESPONSE_BODY_NULL_CODE, "response body is null"
                                    )
                                )
                            )
                        }

                    }
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                when (t) {
                    is DawnException -> {
                        errorHandler?.handleError(t)
                    }

                    else -> {
                        DawnHttpResult.Failure<Nothing>(
                            RESPONSE_EXCEPTION_NULL_CODE, t.message ?: "Throwable message is null"
                        )
                    }
                }
            }
        })

    }

    override fun execute(): Response<DawnHttpResult<T>> {
        val response = call.execute()
        return if (response.isSuccessful) {
            val data = response.body()
            if (data != null) {
                Response.success(DawnHttpResult.Success(data))
            } else {
                Response.success(
                    DawnHttpResult.Failure(
                        RESPONSE_BODY_NULL_CODE, "response body is null"
                    )
                )
            }
        } else {
            Response.error(
                response.code(), DawnHttpResult.Failure<Nothing>(
                    response.code(), response.message()
                ).toString().toResponseBody(null)
            )
        }
    }

    override fun isExecuted() = call.isExecuted

    override fun clone() = DawnResponseCall(call.clone(), errorHandler)

    override fun isCanceled() = call.isCanceled

    override fun cancel() = call.cancel()

    override fun request(): Request = call.request()

    override fun timeout(): Timeout = call.timeout()
}