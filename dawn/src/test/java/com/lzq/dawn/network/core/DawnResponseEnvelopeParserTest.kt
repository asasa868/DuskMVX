package com.lzq.dawn.network.core

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonNull
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.google.gson.TypeAdapter
import com.lzq.dawn.DawnConstants.NetWorkConstants.GSON_NULL_CODE
import com.lzq.dawn.network.error.DawnException
import com.lzq.dawn.network.error.IErrorHandler
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.buffer
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.concurrent.CancellationException

/** 验证自定义响应解析、资源释放及原有公开入口的兼容性。 */
class DawnResponseEnvelopeParserTest {
    @Test fun defaultContractStillExtractsDataWithoutRequiringSuccessMessage() {
        val handler = RecordingHandler()
        val body = TrackedBody("{\"code\":0,\"data\":\"payload\"}")

        val result = converter(handler = handler, contract = contract()).convert(body)

        assertEquals("payload", result)
        assertTrue(handler.errors.isEmpty())
        assertTrue(body.closed)
    }

    @Test fun oldNullContractStillReadsTheWholeResponse() {
        val body = TrackedBody("\"whole response\"")

        val result = converter().convert(body)

        assertEquals("whole response", result)
        assertTrue(body.closed)
    }

    @Test fun parserCanExtractNestedDataWithoutAnIntegerStatus() {
        val body = TrackedBody("{\"meta\":{\"status\":\"ok\"},\"result\":{\"value\":\"payload\"}}")
        val parser = IResponseEnvelopeParser { response ->
            val root = response.asJsonObject
            assertEquals("ok", root.getAsJsonObject("meta").get("status").asString)
            root.getAsJsonObject("result").get("value")
        }

        val result = converter(parser).convert(body)

        assertEquals("payload", result)
        assertTrue(body.closed)
    }

    @Test fun customParserTakesPrecedenceOverLegacyFieldContract() {
        val body = TrackedBody("{\"code\":9,\"msg\":\"legacy failure\",\"payload\":\"custom\"}")
        val handler = RecordingHandler()

        val result = converter(
            IResponseEnvelopeParser { it.asJsonObject.get("payload") }, handler, contract()
        ).convert(body)

        assertEquals("custom", result)
        assertTrue(handler.errors.isEmpty())
    }

    @Test fun parserReceivesPrimitiveAndArrayRoots() {
        for ((json, expected) in listOf("true" to "true", "[\"first\"]" to "first")) {
            val body = TrackedBody(json)
            val parser = IResponseEnvelopeParser { if (it.isJsonArray) it.asJsonArray[0] else it }

            val result = converter(parser).convert(body)

            assertEquals(expected, result)
            assertTrue(body.closed)
        }
    }

    @Test fun parserCanReturnNullOrAnExplicitNullNode() {
        for (data in listOf(null, JsonNull.INSTANCE)) {
            val body = TrackedBody("{}")

            val result = converter(IResponseEnvelopeParser { data }).convert(body)

            assertNull(result)
            assertTrue(body.closed)
        }
    }

    @Test fun explicitNullRootIsPassedToTheParser() {
        var called = false
        val body = TrackedBody("null")

        val result = converter(IResponseEnvelopeParser {
            called = true
            assertTrue(it.isJsonNull)
            it
        }).convert(body)

        assertTrue(called)
        assertNull(result)
        assertTrue(body.closed)
    }

    @Test fun businessFailureRetainsTheOriginalExceptionAndIsHandledOnce() {
        val expected = DawnException(401, "denied")
        val handler = RecordingHandler()
        val body = TrackedBody("{}")

        val actual = assertThrows(DawnException::class.java) {
            converter(IResponseEnvelopeParser { throw expected }, handler).convert(body)
        }
        actual.handleOnce(handler)

        assertSame(expected, actual)
        assertEquals(listOf(expected), handler.errors)
        assertTrue(body.closed)
    }

    @Test fun parseAndCancellationFailuresPropagateWithoutBusinessHandling() {
        for (expected in listOf(JsonParseException("invalid envelope"), CancellationException("cancelled"))) {
            val handler = RecordingHandler()
            val body = TrackedBody("{}")

            val actual = assertThrows(RuntimeException::class.java) {
                converter(IResponseEnvelopeParser { throw expected }, handler).convert(body)
            }

            assertSame(expected, actual)
            assertTrue(handler.errors.isEmpty())
            assertTrue(body.closed)
        }
    }

    @Test fun handlerFailurePropagatesAndClosesTheResponse() {
        val expected = IllegalStateException("handler failed")
        val handler = object : IErrorHandler {
            override fun handleError(dawnException: DawnException) { throw expected }
        }
        val body = TrackedBody("{}")

        val actual = assertThrows(IllegalStateException::class.java) {
            converter(IResponseEnvelopeParser { throw DawnException(7, "denied") }, handler).convert(body)
        }

        assertSame(expected, actual)
        assertTrue(body.closed)
    }

    @Test fun emptyOrMalformedJsonFailsBeforeInvokingTheParser() {
        for (json in listOf("", " ", "{", "[1,")) {
            var called = false
            val handler = RecordingHandler()
            val body = TrackedBody(json)

            assertThrows(JsonParseException::class.java) {
                converter(IResponseEnvelopeParser { called = true; it }, handler).convert(body)
            }

            assertFalse(called)
            assertTrue(handler.errors.isEmpty())
            assertTrue(body.closed)
        }
    }

    @Test fun trailingJsonIsRejectedBeforeInvokingTheParser() {
        var called = false
        val body = TrackedBody("{} {}")

        assertThrows(Exception::class.java) {
            converter(IResponseEnvelopeParser { called = true; JsonPrimitive("payload") }).convert(body)
        }

        assertFalse(called)
        assertTrue(body.closed)
    }

    @Test fun payloadTypeMismatchClosesTheResponseWithoutBusinessHandling() {
        val handler = RecordingHandler()
        val body = TrackedBody("{}")

        assertThrows(IllegalStateException::class.java) {
            converter(IResponseEnvelopeParser { it }, handler).convert(body)
        }

        assertTrue(handler.errors.isEmpty())
        assertTrue(body.closed)
    }

    @Test fun namedFactoryRejectsNullGsonWithTheExistingErrorCode() {
        val failure = assertThrows(DawnException::class.java) {
            DawnConverterFactory.createWithEnvelopeParser(null, IResponseEnvelopeParser { it }, RecordingHandler())
        }

        assertEquals(GSON_NULL_CODE, failure.code)
    }

    @Test fun originalConstructorsAndNullFactoryCallsRemainAvailable() {
        val handler = RecordingHandler()

        DawnConverterFactory.create(null, handler)
        DawnConverterFactory.create(Gson(), null, handler)
        DawnConverterFactory::class.java.getConstructor(
            Gson::class.java, IResponseCode::class.java, IErrorHandler::class.java
        )
        DawnResponseBodyConverter::class.java.getConstructor(
            Gson::class.java, TypeAdapter::class.java, IResponseCode::class.java, IErrorHandler::class.java
        )
    }

    @Test fun customEnvelopeDoesNotChangeRequestSerialization() {
        val gson = GsonBuilder().registerTypeAdapter(
            Payload::class.java, JsonSerializer<Payload> { _, _, _ -> JsonPrimitive("encoded") }
        ).create()
        val parser = IResponseEnvelopeParser { error("请求序列化不应调用响应解析器") }
        val factory = DawnConverterFactory.createWithEnvelopeParser(gson, parser, RecordingHandler())
        @Suppress("UNCHECKED_CAST")
        val converter = factory.requestBodyConverter(
            Payload::class.java, emptyArray(), emptyArray(), Retrofit.Builder().baseUrl("https://example.test/").build()
        ) as Converter<Payload, RequestBody>
        val buffer = Buffer()

        converter.convert(Payload("value"))!!.writeTo(buffer)

        assertEquals("\"encoded\"", buffer.readUtf8())
    }

    private fun converter(
        parser: IResponseEnvelopeParser? = null,
        handler: IErrorHandler = RecordingHandler(),
        contract: IResponseCode? = null
    ) = DawnResponseBodyConverter(Gson(), Gson().getAdapter(String::class.java), contract, handler, parser)

    private fun contract() = object : IResponseCode {
        override fun getCodeName() = "code"
        override fun getMessageName() = "msg"
        override fun getDataName() = "data"
        override fun isSuccessCode() = 0
    }

    private data class Payload(val value: String)

    private class RecordingHandler : IErrorHandler {
        val errors = mutableListOf<DawnException>()
        override fun handleError(dawnException: DawnException) { errors.add(dawnException) }
    }

    private class TrackedBody(json: String) : ResponseBody() {
        var closed = false
        private val source = object : ForwardingSource(Buffer().writeUtf8(json)) {
            override fun close() {
                closed = true
                super.close()
            }
        }.buffer()

        override fun contentType() = "application/json".toMediaType()
        override fun contentLength() = -1L
        override fun source(): BufferedSource = source
    }
}
