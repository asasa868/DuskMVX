package com.lzq.dawn.network.core

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonParseException
import com.lzq.dawn.DawnConstants.NetWorkConstants.RESPONSE_BODY_NULL_CODE
import com.lzq.dawn.DawnConstants.NetWorkConstants.RESPONSE_EXCEPTION_NULL_CODE
import com.lzq.dawn.network.DawnNetWork
import com.lzq.dawn.network.bean.DawnHttpResult
import com.lzq.dawn.network.error.DawnException
import com.lzq.dawn.network.error.IErrorHandler
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/** 验证响应信封扩展经过真实转换器、调用适配器与网络基类后的公开行为。 */
class DawnResponseEnvelopeIntegrationTest {
    @Test fun nestedStringStatusUnwrapsGenericPayloadWithoutNestedResult() {
        withServer { server, client ->
            server.enqueue(json("""{"meta":{"status":"ok"},"payload":{"items":[{"name":"first"},{"name":"second"}]}}"""))
            val handler = Handler()
            val parser = IResponseEnvelopeParser { response ->
                val envelope = response.asJsonObject
                if (envelope.getAsJsonObject("meta").get("status").asString != "ok") {
                    throw DawnException(41, "请求失败")
                }
                envelope.getAsJsonObject("payload").get("items")
            }

            val response = await(api(server, client, parser, handler).items())

            assertEquals(listOf(Item("first"), Item("second")), (response.body() as DawnHttpResult.Success).data)
            assertEquals(200, response.body()!!.httpResponse!!.code())
            assertTrue(handler.errors.isEmpty())
        }
    }

    @Test fun booleanStatusUsesSameParserForSuspendRequests() {
        withServer { server, client ->
            server.enqueue(json("""{"ok":true,"payload":"suspended"}"""))
            val handler = Handler()
            val parser = IResponseEnvelopeParser { response ->
                val envelope = response.asJsonObject
                if (!envelope.get("ok").asBoolean) throw DawnException(42, "请求失败")
                envelope.get("payload")
            }

            val result = runBlocking {
                withTimeout(5000) { api(server, client, parser, handler).suspendValue() }
            }

            assertEquals("suspended", (result as DawnHttpResult.Success).data)
            assertTrue(handler.errors.isEmpty())
        }
    }

    @Test fun customBusinessFailureRetainsLegacyFailureMappingAndHandlesOnce() {
        withServer { server, client ->
            server.enqueue(json("""{"ok":false,"error":{"number":43,"reason":"权限不足"}}"""))
            val handler = Handler()
            val parser = IResponseEnvelopeParser { response ->
                val error = response.asJsonObject.getAsJsonObject("error")
                throw DawnException(error.get("number").asInt, error.get("reason").asString)
            }

            val response = await(api(server, client, parser, handler).value())
            val failure = response.body() as DawnHttpResult.Failure

            assertEquals(RESPONSE_EXCEPTION_NULL_CODE, failure.code)
            assertEquals("权限不足", failure.message)
            assertNull(failure.httpResponse)
            assertEquals(listOf(43), handler.errors)
        }
    }

    @Test fun invalidCustomEnvelopeBecomesParsingFailureWithoutBusinessError() {
        withServer { server, client ->
            server.enqueue(json("""{"unexpected":true}"""))
            val handler = Handler()
            val parser = IResponseEnvelopeParser { throw JsonParseException("缺少业务状态") }

            val failure = await(api(server, client, parser, handler).value()).body() as DawnHttpResult.Failure

            assertEquals(RESPONSE_EXCEPTION_NULL_CODE, failure.code)
            assertEquals("缺少业务状态", failure.message)
            assertTrue(handler.errors.isEmpty())
        }
    }

    @Test fun identityParserAcceptsArrayRootAndPreservesGenericElementType() {
        withServer { server, client ->
            server.enqueue(json("""[{"name":"direct"}]"""))
            val handler = Handler()

            val response = await(api(server, client, IResponseEnvelopeParser { it }, handler).items())

            assertEquals(listOf(Item("direct")), (response.body() as DawnHttpResult.Success).data)
            assertTrue(handler.errors.isEmpty())
        }
    }

    @Test fun nestedGenericMapPreservesListElementType() {
        withServer { server, client ->
            server.enqueue(json("""{"payload":{"group":[{"name":"nested"}]}}"""))
            val handler = Handler()
            val parser = IResponseEnvelopeParser { it.asJsonObject.get("payload") }

            val response = await(api(server, client, parser, handler).groups())

            assertEquals(mapOf("group" to listOf(Item("nested"))), (response.body() as DawnHttpResult.Success).data)
            assertTrue(handler.errors.isEmpty())
        }
    }

    @Test fun synchronousBusinessFailurePreservesOriginalExceptionAndHandlesOnce() {
        withServer { server, client ->
            server.enqueue(json("""{"ok":false}"""))
            val handler = Handler()
            val failure = DawnException(44, "请求被拒绝")
            val parser = IResponseEnvelopeParser { throw failure }

            try {
                api(server, client, parser, handler).value().execute()
                fail("同步调用必须传播业务异常")
            } catch (actual: DawnException) {
                assertSame(failure, actual)
            }

            assertEquals(listOf(44), handler.errors)
        }
    }

    @Test fun nullParserPayloadKeepsEmptyBodyFailureForAsyncAndSuspendRequests() {
        withServer { server, client ->
            for (payload in listOf<JsonElement?>(null, JsonNull.INSTANCE)) {
                server.enqueue(json("""{"payload":"ignored"}"""))
                server.enqueue(json("""{"payload":"ignored"}"""))
                val handler = Handler()
                val service = api(server, client, IResponseEnvelopeParser { payload }, handler)

                val asyncResult = await(service.value()).body() as DawnHttpResult.Failure
                val suspendResult = runBlocking {
                    withTimeout(5000) { service.suspendValue() }
                } as DawnHttpResult.Failure

                assertEquals(RESPONSE_BODY_NULL_CODE, asyncResult.code)
                assertEquals(RESPONSE_BODY_NULL_CODE, suspendResult.code)
                assertTrue(handler.errors.isEmpty())
            }
        }
    }

    @Test fun customGsonAdapterIsRetainedWhenParsingGenericEnvelopePayload() {
        withServer { server, client ->
            server.enqueue(json("""{"payload":["first","second"]}"""))
            val handler = Handler()
            val gson = GsonBuilder().registerTypeAdapter(
                Item::class.java,
                JsonDeserializer<Item> { response, _, _ -> Item("custom:" + response.asString) }
            ).create()
            val factory = DawnConverterFactory.createWithEnvelopeParser(
                gson, IResponseEnvelopeParser { it.asJsonObject.get("payload") }, handler
            )

            val response = await(api(server, client, factory, handler).items())

            assertEquals(listOf(Item("custom:first"), Item("custom:second")), (response.body() as DawnHttpResult.Success).data)
            assertTrue(handler.errors.isEmpty())
        }
    }

    @Test fun legacyNetworkWithoutResponseContractStillDecodesWholeResponse() {
        withServer { server, client ->
            server.enqueue(json("""[{"name":"legacy"}]"""))
            val handler = Handler()
            val network = LegacyNetwork(server, client, handler)

            val response = await(network.getApiService().items())

            assertEquals(listOf(Item("legacy")), (response.body() as DawnHttpResult.Success).data)
            assertTrue(handler.errors.isEmpty())
        }
    }

    @Test fun legacyNetworkRetainsRenamedTopLevelFieldsAndSuccessCode() {
        withServer { server, client ->
            server.enqueue(json("""{"status":200,"result":"legacy"}"""))
            val handler = Handler()
            val network = LegacyNetwork(server, client, handler, legacyContract())

            val response = await(network.getApiService().value())

            assertEquals("legacy", (response.body() as DawnHttpResult.Success).data)
            assertTrue(handler.errors.isEmpty())
        }
    }

    @Test fun defaultEnvelopeHookPreservesLegacyNetworkConfigurationOrder() {
        withServer { server, client ->
            val calls = mutableListOf<String>()
            val handler = Handler()
            val network = object : DawnNetWork<Api>(Api::class.java) {
                override fun getBaseUrl(): String {
                    calls.add("baseUrl")
                    return server.url("/").toString()
                }
                override fun createOkhttpClient(): OkHttpClient {
                    calls.add("client")
                    return client
                }
                override fun getHttpResponseCode(): IResponseCode {
                    calls.add("responseCode")
                    return legacyContract()
                }
                override fun isDebug() = false
                override fun createRequestHeader(builder: Request.Builder): Request.Builder? = null
                override fun getErrorExceptionHandler() = handler
            }

            network.getApiService()

            assertEquals(listOf("baseUrl", "client", "responseCode"), calls)
        }
    }

    @Test fun networkEnvelopeParserTakesPrecedenceOverLegacyFieldMapping() {
        withServer { server, client ->
            server.enqueue(json("""{"payload":{"value":"custom"}}"""))
            val handler = Handler()
            val network = ParserNetwork(
                server, client, handler,
                IResponseEnvelopeParser { it.asJsonObject.getAsJsonObject("payload").get("value") },
                legacyContract()
            )

            val response = await(network.getApiService().value())

            assertEquals("custom", (response.body() as DawnHttpResult.Success).data)
            assertTrue(handler.errors.isEmpty())
        }
    }

    @Test fun parserConfigurationIsIsolatedBetweenNetworkInstances() {
        withServer { server, client ->
            val handler = Handler()
            val left = ParserNetwork(server, client, handler, IResponseEnvelopeParser { it.asJsonObject.get("left") })
            val right = ParserNetwork(server, client, handler, IResponseEnvelopeParser { it.asJsonObject.get("right") })

            server.enqueue(json("""{"left":"first","right":"unused"}"""))
            val first = await(left.getApiService().value()).body() as DawnHttpResult.Success
            server.enqueue(json("""{"left":"unused","right":"second"}"""))
            val second = await(right.getApiService().value()).body() as DawnHttpResult.Success
            server.enqueue(json("""{"left":"third","right":"unused"}"""))
            val third = await(left.getApiService().value()).body() as DawnHttpResult.Success

            assertEquals(listOf("first", "second", "third"), listOf(first.data, second.data, third.data))
            assertTrue(handler.errors.isEmpty())
        }
    }

    private fun api(
        server: MockWebServer, client: OkHttpClient, parser: IResponseEnvelopeParser, handler: Handler
    ): Api = api(server, client, DawnConverterFactory.createWithEnvelopeParser(parser, handler), handler)

    private fun api(
        server: MockWebServer, client: OkHttpClient, factory: DawnConverterFactory, handler: Handler
    ): Api = Retrofit.Builder()
        .baseUrl(server.url("/"))
        .client(client)
        .addConverterFactory(factory)
        .addCallAdapterFactory(DawnCallAdapterFactory.create(handler))
        .build().create(Api::class.java)

    private fun legacyContract() = object : IResponseCode {
        override fun getCodeName() = "status"
        override fun getMessageName() = "message"
        override fun getDataName() = "result"
        override fun isSuccessCode() = 200
    }

    private fun json(body: String) = MockResponse().setHeader("Content-Type", "application/json").setBody(body)

    private fun withServer(test: (MockWebServer, OkHttpClient) -> Unit) {
        val server = MockWebServer()
        val client = OkHttpClient()
        server.start()
        try {
            test(server, client)
        } finally {
            client.dispatcher.executorService.shutdownNow()
            client.connectionPool.evictAll()
            server.shutdown()
        }
    }

    private fun <T> await(call: Call<DawnHttpResult<T>>): Response<DawnHttpResult<T>> {
        val complete = CountDownLatch(1)
        var response: Response<DawnHttpResult<T>>? = null
        var failure: Throwable? = null
        call.enqueue(object : Callback<DawnHttpResult<T>> {
            override fun onResponse(call: Call<DawnHttpResult<T>>, value: Response<DawnHttpResult<T>>) {
                response = value
                complete.countDown()
            }

            override fun onFailure(call: Call<DawnHttpResult<T>>, value: Throwable) {
                failure = value
                complete.countDown()
            }
        })
        assertTrue("网络调用必须结束", complete.await(5, TimeUnit.SECONDS))
        failure?.let { throw AssertionError("网络调用异常终止", it) }
        return response!!
    }

    /** 记录每次业务错误通知，以验证转换器和适配器不会重复处理。 */
    private class Handler : IErrorHandler {
        val errors = mutableListOf<Int>()
        override fun handleError(dawnException: DawnException) { errors.add(dawnException.code) }
    }

    /** 保留既有子类仅覆盖顶层字段契约的接入方式。 */
    private open class LegacyNetwork(
        private val server: MockWebServer,
        private val client: OkHttpClient,
        private val handler: IErrorHandler,
        private val contract: IResponseCode? = null
    ) : DawnNetWork<Api>(Api::class.java) {
        override fun isDebug() = false
        override fun getBaseUrl() = server.url("/").toString()
        override fun createRequestHeader(builder: Request.Builder): Request.Builder? = null
        override fun createOkhttpClient() = client
        override fun getErrorExceptionHandler() = handler
        override fun getHttpResponseCode() = contract
    }

    /** 模拟新旧响应配置同时存在的应用子类。 */
    private class ParserNetwork(
        server: MockWebServer,
        client: OkHttpClient,
        handler: IErrorHandler,
        private val parser: IResponseEnvelopeParser,
        contract: IResponseCode? = null
    ) : LegacyNetwork(server, client, handler, contract) {
        override fun getResponseEnvelopeParser() = parser
    }

    /** 验证集合元素保持业务类型而非退化为通用映射。 */
    private data class Item(val name: String)

    /** 覆盖普通调用、泛型集合和挂起调用的公开声明。 */
    private interface Api {
        @GET("value") fun value(): Call<DawnHttpResult<String>>
        @GET("items") fun items(): Call<DawnHttpResult<List<Item>>>
        @GET("groups") fun groups(): Call<DawnHttpResult<Map<String, List<Item>>>>
        @GET("value") suspend fun suspendValue(): DawnHttpResult<String>
    }
}
