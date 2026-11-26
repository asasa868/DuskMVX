import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.io.File
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets
import java.util.Base64
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * @projectName CentralPortalStagingTest
 * @author Lzq
 * @date : Created by Lzq on 2026/11/26 14:17:18
 * @version 0.0.30
 * @description: 发布入口回归测试
 *
 * 全部请求只打本地 mock HTTP 服务，不连接 Central。
 */
class CentralPortalStagingTest {

    /** 启动本地 mock 服务并在回调结束后关闭，全程不访问外网。 */
    private fun <T> withServer(status: Int, capture: MutableList<String>, body: (String) -> T): T {
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        val observed = AtomicReference<HttpExchange>()
        server.createContext("/") { exchange ->
            observed.set(exchange)
            capture.add("method=" + exchange.requestMethod)
            capture.add("path=" + exchange.requestURI.rawPath)
            capture.add("query=" + (exchange.requestURI.rawQuery ?: ""))
            capture.add("authorization=" + (exchange.requestHeaders.getFirst("Authorization") ?: ""))
            exchange.sendResponseHeaders(status, -1)
            exchange.close()
        }
        server.start()
        try {
            return body("http://127.0.0.1:" + server.address.port)
        } finally {
            server.stop(0)
        }
    }

    @Test
    fun `部署地址使用当前官方兼容端点`() {
        assertEquals("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/",
            CentralPortalStaging.deployUrl())
    }

    @Test
    fun `manual端点携带命名空间与user_managed`() {
        assertEquals(
            "https://ossrh-staging-api.central.sonatype.com/manual/upload/defaultRepository/io.github.asasa868" +
                "?publishing_type=user_managed",
            CentralPortalStaging.manualUploadEndpoint(CentralPortalStaging.BASE_URL, "io.github.asasa868"),
        )
        assertEquals("user_managed", CentralPortalStaging.PUBLISHING_TYPE)
    }

    @Test
    fun `命名空间作为单个路径段编码`() {
        val endpoint = CentralPortalStaging.manualUploadEndpoint("https://example.invalid/", "com.example/非法 名")
        assertTrue(endpoint, endpoint.contains("/defaultRepository/com.example%2F%E9%9D%9E%E6%B3%95%20%E5%90%8D?"))
    }

    @Test
    fun `认证头为Bearer加base64凭据`() {
        val expected = "Bearer " + Base64.getEncoder()
            .encodeToString("tokenuser:tokenpass".toByteArray(StandardCharsets.UTF_8))
        assertEquals(expected, CentralPortalStaging.authorizationHeader("tokenuser", "tokenpass"))
    }

    @Test
    fun `手动交接只向本地mock端点发送一次POST且参数正确`() {
        val capture = Collections.synchronizedList(mutableListOf<String>())
        val status = withServer(201, capture) { baseUrl ->
            CentralPortalStaging.manualUpload(baseUrl, "io.github.asasa868", "tokenuser", "tokenpass")
        }
        assertEquals(201, status)
        assertEquals("manual 交接只发送一次请求", 1, capture.count { it.startsWith("method=") })
        assertTrue(capture.contains("method=POST"))
        assertTrue(capture.contains("path=/manual/upload/defaultRepository/io.github.asasa868"))
        assertTrue(capture.contains("query=publishing_type=user_managed"))
        val expected = "Bearer " + Base64.getEncoder()
            .encodeToString("tokenuser:tokenpass".toByteArray(StandardCharsets.UTF_8))
        assertTrue(capture.contains("authorization=" + expected))
    }

    @Test
    fun `失败响应不回显任何凭据`() {
        val capture = Collections.synchronizedList(mutableListOf<String>())
        try {
            withServer(401, capture) { baseUrl ->
                CentralPortalStaging.manualUpload(baseUrl, "io.github.asasa868", "tokenuser", "tokenpass")
            }
            fail("401 应当抛出异常")
        } catch (error: IllegalStateException) {
            val message = error.message.orEmpty()
            val encoded = Base64.getEncoder().encodeToString("tokenuser:tokenpass".toByteArray(StandardCharsets.UTF_8))
            assertTrue(message, message.contains("HTTP 401"))
            assertFalse(message, message.contains(encoded))
            assertFalse(message, message.contains("tokenpass"))
            assertFalse(message, message.contains("tokenuser"))
        }
    }

    @Test
    fun `空凭据在建立任何请求前被拒绝`() {
        val requests = AtomicInteger()
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/") { exchange ->
            requests.incrementAndGet()
            exchange.sendResponseHeaders(201, -1)
            exchange.close()
        }
        server.start()
        try {
            val baseUrl = "http://127.0.0.1:" + server.address.port
            try {
                CentralPortalStaging.manualUpload(baseUrl, "io.github.asasa868", "", "tokenpass")
                fail("空用户名应被拒绝")
            } catch (error: IllegalArgumentException) {
                assertTrue(error.message.orEmpty().contains("不完整"))
            }
            try {
                CentralPortalStaging.manualUpload(baseUrl, "io.github.asasa868", "tokenuser", "   ")
                fail("空密码应被拒绝")
            } catch (error: IllegalArgumentException) {
                assertTrue(error.message.orEmpty().contains("不完整"))
            }
            assertEquals("空凭据不得产生任何请求", 0, requests.get())
        } finally {
            server.stop(0)
        }
    }

    @Test
    fun `空命名空间在联网前被拒绝`() {
        try {
            CentralPortalStaging.manualUploadEndpoint(CentralPortalStaging.BASE_URL, "   ")
            fail("空命名空间应被拒绝")
        } catch (error: IllegalArgumentException) {
            assertTrue(error.message.orEmpty().contains("不得为空"))
        }
    }

    @Test
    fun `发布任务保持显式且不接入默认或自动发布路径`() {
        val script = File("../dawn/build.gradle.kts")
        assertTrue("未找到 dawn 构建脚本：" + script.absolutePath, script.isFile)
        val text = script.readText()
        assertFalse("旧 OSSRH 地址必须移除", text.contains("s01.oss.sonatype.org"))
        assertTrue("Central 仓库应使用统一常量", text.contains("CentralPortalStaging.deployUrl()"))
        assertTrue("必须存在显式手动交接任务", text.contains("centralManualUpload"))
        assertTrue("手动交接必须要求独立授权参数", text.contains("allowCentralStaging"))
        assertFalse("不得把 Central 正式上传接入任何自动任务",
            text.contains("publishReleasePublicationToCentralRepository"))
        assertFalse("不得出现 automatic 发布类型", text.contains("publishing_type=automatic"))
    }
}
