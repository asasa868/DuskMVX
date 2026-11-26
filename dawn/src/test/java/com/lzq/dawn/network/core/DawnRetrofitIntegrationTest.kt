package com.lzq.dawn.network.core

import com.lzq.dawn.DawnConstants.NetWorkConstants.RESPONSE_BODY_NULL_CODE
import com.lzq.dawn.DawnConstants.NetWorkConstants.RESPONSE_EXCEPTION_NULL_CODE
import com.lzq.dawn.network.bean.DawnHttpResult
import com.lzq.dawn.network.error.DawnException
import com.lzq.dawn.network.error.IErrorHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * @projectName com.lzq.dawn.network.core.DawnRetrofitIntegrationTest
 * @author Lzq
 * @date : Created by Lzq on 2026/11/25 14:05:23
 * @version 0.0.30
 * @description: 通过本地测试服务验证真实网络适配链路的响应约定
 */
class DawnRetrofitIntegrationTest {
    @Test fun retrofitAsyncCallUnwrapsPayloadWithoutNestedResult() {
        val server = MockWebServer()
        server.enqueue(json("{\"code\":0,\"message\":\"ok\",\"data\":\"payload\"}"))
        server.start()
        try {
            val handler = Handler()
            val response = await(api(server, handler).value())

            assertEquals("payload", (response.body() as DawnHttpResult.Success).data)
            assertTrue(handler.errors.isEmpty())
        } finally { server.shutdown() }
    }

    @Test fun retrofitBusinessErrorKeepsLegacyFailureCodeAndHandlesBusinessCodeOnce() {
        val server = MockWebServer()
        server.enqueue(json("{\"code\":12,\"message\":\"denied\",\"data\":null}"))
        server.start()
        try {
            val handler = Handler()
            val response = await(api(server, handler).value())

            assertEquals(RESPONSE_EXCEPTION_NULL_CODE, (response.body() as DawnHttpResult.Failure).code)
            assertEquals(listOf(12), handler.errors)
        } finally { server.shutdown() }
    }

    @Test fun retrofitHttpErrorKeepsRawResponseErrorBodyAndHeader() {
        val server = MockWebServer()
        server.enqueue(MockResponse().setResponseCode(599).setHeader("Retry-After", "8").setBody("upstream error"))
        server.start()
        try {
            val handler = Handler()
            val response = await(api(server, handler).value())
            val result = response.body() as DawnHttpResult.Failure

            assertEquals(599, result.code)
            assertEquals(599, result.httpResponse!!.code())
            assertEquals("8", result.httpResponse!!.headers()["Retry-After"])
            assertEquals("upstream error", result.httpResponse!!.errorBody()!!.string())
            assertEquals(listOf(599), handler.errors)
        } finally { server.shutdown() }
    }

    @Test fun retrofitMalformedPayloadTerminatesWithLegacyFailureWithoutBusinessHandler() {
        val server = MockWebServer()
        server.enqueue(json("{"))
        server.start()
        try {
            val handler = Handler()
            val response = await(api(server, handler).value())

            assertEquals(RESPONSE_EXCEPTION_NULL_CODE, (response.body() as DawnHttpResult.Failure).code)
            assertTrue(handler.errors.isEmpty())
        } finally { server.shutdown() }
    }

    @Test fun retrofitNoContentIsAnExplicitEmptyBodyFailure() = runBlocking {
        val server = MockWebServer()
        server.enqueue(MockResponse().setResponseCode(204))
        server.start()
        try {
            val result = withTimeout(5000) { api(server, Handler()).suspendValue() }

            assertEquals(RESPONSE_BODY_NULL_CODE, (result as DawnHttpResult.Failure).code)
        } finally { server.shutdown() }
    }

    @Test fun retrofitSuspendCallUsesTheSameBusinessFailureMapping() = runBlocking {
        val server = MockWebServer()
        server.enqueue(json("{\"code\":13,\"message\":\"denied\",\"data\":null}"))
        server.start()
        try {
            val handler = Handler()
            val result = withTimeout(5000) { api(server, handler).suspendValue() }

            assertEquals(RESPONSE_EXCEPTION_NULL_CODE, (result as DawnHttpResult.Failure).code)
            assertEquals(listOf(13), handler.errors)
        } finally { server.shutdown() }
    }

    @Test fun cancellingRetrofitSuspendCallCancelsWithoutBusinessError() = runBlocking {
        val server = MockWebServer()
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE))
        server.start()
        try {
            val handler = Handler()
            val request = async { api(server, handler).suspendValue() }
            withTimeout(5000) {
                while (server.requestCount == 0) kotlinx.coroutines.delay(1)
            }
            request.cancelAndJoin()

            assertTrue(request.isCancelled)
            assertTrue(handler.errors.isEmpty())
        } finally { server.shutdown() }
    }

    private fun api(server: MockWebServer, handler: Handler): Api = Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(DawnConverterFactory.create(object : IResponseCode {
            override fun getCodeName() = "code"
            override fun getMessageName() = "message"
            override fun getDataName() = "data"
            override fun isSuccessCode() = 0
        }, handler))
        .addCallAdapterFactory(DawnCallAdapterFactory(handler))
        .build().create(Api::class.java)

    private fun json(body: String) = MockResponse().setHeader("Content-Type", "application/json").setBody(body)

    private fun await(call: Call<DawnHttpResult<String>>): Response<DawnHttpResult<String>> {
        val complete = CountDownLatch(1)
        var response: Response<DawnHttpResult<String>>? = null
        var failure: Throwable? = null
        call.enqueue(object : Callback<DawnHttpResult<String>> {
            override fun onResponse(call: Call<DawnHttpResult<String>>, value: Response<DawnHttpResult<String>>) {
                response = value; complete.countDown()
            }
            override fun onFailure(call: Call<DawnHttpResult<String>>, value: Throwable) {
                failure = value; complete.countDown()
            }
        })
        assertTrue("callback must terminate", complete.await(5, TimeUnit.SECONDS))
        failure?.let { throw AssertionError("unexpected onFailure", it) }
        return response!!
    }

    /** 记录网络集成测试中的业务错误处理结果。 */
    private class Handler : IErrorHandler {
        val errors = mutableListOf<Int>()
        override fun handleError(dawnException: DawnException) { errors.add(dawnException.code) }
    }

    /** 声明网络集成测试使用的普通与挂起请求入口。 */
    private interface Api {
        @GET("value") fun value(): Call<DawnHttpResult<String>>
        @GET("value") suspend fun suspendValue(): DawnHttpResult<String>
    }
}

