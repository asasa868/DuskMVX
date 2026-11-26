import java.net.HttpURLConnection
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64

/**
 * @projectName CentralPortalStaging
 * @author Lzq
 * @date : Created by Lzq on 2026/11/26 14:17:18
 * @version 0.0.30
 * @description: Central Portal 发布入口的纯本地构件
 *
 * 只负责拼出 Sonatype 官方 compatibility endpoint 与 manual upload 请求，
 * 不挂到任何默认任务上；只有维护者显式调用对应 Gradle 任务时才会建立网络连接。
 */
object CentralPortalStaging {
    /** OSSRH Staging API 兼容端点根地址，原 s01.oss.sonatype.org 已日落。 */
    const val BASE_URL = "https://ossrh-staging-api.central.sonatype.com"

    /** Gradle 内置 maven-publish 上传制品的部署路径。 */
    const val DEPLOY_PATH = "/service/local/staging/deploy/maven2/"

    /** manual upload 固定携带 user_managed，避免上传即触发正式发布。 */
    const val PUBLISHING_TYPE = "user_managed"

    /** maven-publish 仓库应使用的部署地址。 */
    fun deployUrl(): String = BASE_URL + DEPLOY_PATH

    /** 构造 manual upload 端点，显式声明 publishing_type=user_managed。 */
    fun manualUploadEndpoint(baseUrl: String, namespace: String): String {
        require(namespace.isNotBlank()) { "Central Portal 命名空间不得为空" }
        val encoded = URLEncoder.encode(namespace, StandardCharsets.UTF_8.name()).replace("+", "%20")
        return baseUrl.trimEnd('/') + "/manual/upload/defaultRepository/" + encoded +
            "?publishing_type=" + PUBLISHING_TYPE
    }

    /** manual endpoint 使用 Bearer + base64(username:password)；返回值只能进请求头，不得进日志。 */
    fun authorizationHeader(username: String, password: String): String {
        require(username.isNotBlank() && password.isNotBlank()) { "Central Portal user token 不完整" }
        val token = Base64.getEncoder().encodeToString("$username:$password".toByteArray(StandardCharsets.UTF_8))
        return "Bearer " + token
    }

    /**
     * 把已上传的制品交接为 USER_MANAGED 部署。
     *
     * 成功返回 HTTP 状态码；失败只抛出不含认证信息、不含编码凭据的异常，
     * 部署是否正式发布由维护者在 Central Portal 人工决定。
     */
    fun manualUpload(baseUrl: String, namespace: String, username: String, password: String): Int {
        val endpoint = manualUploadEndpoint(baseUrl, namespace)
        val connection = URI.create(endpoint).toURL().openConnection() as HttpURLConnection
        return try {
            connection.requestMethod = "POST"
            connection.connectTimeout = 30_000
            connection.readTimeout = 60_000
            connection.doOutput = true
            connection.setFixedLengthStreamingMode(0)
            connection.setRequestProperty("Authorization", authorizationHeader(username, password))
            connection.setRequestProperty("Accept", "application/json")
            connection.outputStream.close()
            val status = connection.responseCode
            if (status !in 200..299) {
                throw IllegalStateException("Central Portal USER_MANAGED 部署交接失败：HTTP $status @ $endpoint")
            }
            status
        } finally {
            connection.disconnect()
        }
    }
}
