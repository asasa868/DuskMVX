package com.lzq.dawn.util.metaData

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import com.lzq.dawn.DawnBridge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

/** 验证类型读取迁移后，清单值与七个公开入口保留原有结果和异常约定。 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26, 33, 34, 37], application = Application::class, manifest = Config.NONE)
class MetaDataUtilsCompatibilityTest {
    private val application get() = RuntimeEnvironment.getApplication()

    @Before
    fun initializeApplication() {
        DawnBridge.init(application)
    }

    @Test
    fun manifestValueTypesKeepTheirExistingStringRepresentations() {
        installMetadata(Bundle().apply {
            putString("text", "渠道发布")
            putInt("integer", 42)
            putBoolean("boolean", true)
            putFloat("float", 1.25f)
            putInt("resource", android.R.string.ok)
        })

        val expected = linkedMapOf(
            "text" to "渠道发布",
            "integer" to "42",
            "boolean" to "true",
            "float" to "1.25",
            "resource" to android.R.string.ok.toString()
        )

        expected.forEach { (key, value) -> assertAllReaders(value, key) }
    }

    @Test
    fun emptyStringsAndNumericBoundariesKeepTheirExistingValues() {
        installMetadata(Bundle().apply {
            putString("", "空键的值")
            putString("empty", "")
            putInt("minimum", Int.MIN_VALUE)
            putInt("maximum", Int.MAX_VALUE)
            putBoolean("disabled", false)
            putFloat("negativeZero", -0.0f)
        })

        val expected = linkedMapOf(
            "" to "空键的值",
            "empty" to "",
            "minimum" to Int.MIN_VALUE.toString(),
            "maximum" to Int.MAX_VALUE.toString(),
            "disabled" to "false"
        )

        expected.forEach { (key, value) -> assertAllReaders(value, key) }
        readers().forEach { reader ->
            // 比较系统交付的清单值，避免把包管理器复制前的浮点位模式当作读取契约。
            val metadata = metadataForScope(reader.scope)
            assertTrue("${reader.name}：negativeZero 必须存在", metadata.containsKey("negativeZero"))
            val expectedNegativeZero = metadata.getFloat("negativeZero", Float.NaN).toString()
            assertEquals("${reader.name}：negativeZero", expectedNegativeZero, reader.read("negativeZero"))
        }
    }

    @Test
    fun absentKeysAndExplicitNullValuesKeepTheLiteralNullResult() {
        installMetadata(Bundle().apply { putString("explicitNull", null) })

        assertAllReaders("null", "missing")
        assertAllReaders("null", "")
        assertAllReaders("null", "explicitNull")
    }

    @Test
    fun eachPublicEntryReadsItsOwnManifestComponent() {
        installMetadata(Bundle().apply { putString("scope", "app") })
        val manager = shadowOf(application.packageManager)
        manager.addActivityIfNotPresent(ComponentName(application, MetadataActivity::class.java))
            .metaData = Bundle().apply { putString("scope", "activity") }
        manager.addServiceIfNotPresent(ComponentName(application, MetadataService::class.java))
            .metaData = Bundle().apply { putString("scope", "service") }
        manager.addReceiverIfNotPresent(ComponentName(application, MetadataReceiver::class.java))
            .metaData = Bundle().apply { putString("scope", "receiver") }

        readers().forEach { reader ->
            assertEquals(reader.name, reader.scope, reader.read("scope"))
        }
    }

    @Test
    fun absentMetadataKeepsTheNullPointerFailure() {
        installMetadata(null)

        readers().forEach { reader ->
            assertThrows(reader.name, NullPointerException::class.java) {
                reader.read("missing")
            }
        }
    }

    @Test
    fun missingPackagesAndComponentsKeepTheEmptyStringFallback() {
        installMetadata(Bundle())
        shadowOf(application.packageManager).removePackage(application.packageName)

        assertAllReaders("", "missing")
    }

    private fun installMetadata(metadata: Bundle?) {
        val manager = shadowOf(application.packageManager)
        val packageInfo = manager.getInternalMutablePackageInfo(application.packageName)
        requireNotNull(packageInfo.applicationInfo).metaData = metadata
        manager.addActivityIfNotPresent(ComponentName(application, MetadataActivity::class.java))
            .metaData = metadata
        manager.addServiceIfNotPresent(ComponentName(application, MetadataService::class.java))
            .metaData = metadata
        manager.addReceiverIfNotPresent(ComponentName(application, MetadataReceiver::class.java))
            .metaData = metadata
    }

    private fun assertAllReaders(expected: String, key: String) {
        readers().forEach { reader ->
            assertEquals("${reader.name}：$key", expected, reader.read(key))
        }
    }

    private fun metadataForScope(scope: String): Bundle {
        val manager = application.packageManager
        val metadata = when (scope) {
            "app" -> manager.getApplicationInfo(application.packageName, PackageManager.GET_META_DATA).metaData
            "activity" -> manager.getActivityInfo(
                ComponentName(application, MetadataActivity::class.java), PackageManager.GET_META_DATA
            ).metaData
            "service" -> manager.getServiceInfo(
                ComponentName(application, MetadataService::class.java), PackageManager.GET_META_DATA
            ).metaData
            "receiver" -> manager.getReceiverInfo(
                ComponentName(application, MetadataReceiver::class.java), PackageManager.GET_META_DATA
            ).metaData
            else -> error("未知清单范围：$scope")
        }
        return requireNotNull(metadata)
    }

    private fun readers(): List<MetadataReader> = listOf(
        MetadataReader("应用", "app") { MetaDataUtils.getMetaDataInApp(it) },
        MetadataReader("活动类", "activity") {
            MetaDataUtils.getMetaDataInActivity(MetadataActivity::class.java, it)
        },
        MetadataReader("活动实例", "activity") {
            MetaDataUtils.getMetaDataInActivity(MetadataActivity(), it)
        },
        MetadataReader("服务类", "service") {
            MetaDataUtils.getMetaDataInService(MetadataService::class.java, it)
        },
        MetadataReader("服务实例", "service") {
            MetaDataUtils.getMetaDataInService(MetadataService(), it)
        },
        MetadataReader("广播类", "receiver") {
            MetaDataUtils.getMetaDataInReceiver(MetadataReceiver::class.java, it)
        },
        MetadataReader("广播实例", "receiver") {
            MetaDataUtils.getMetaDataInReceiver(MetadataReceiver(), it)
        }
    )

    private data class MetadataReader(
        val name: String,
        val scope: String,
        val read: (String) -> String
    )

    /** 提供活动类入口及实例转发入口，不启动活动生命周期。 */
    class MetadataActivity : Activity()

    /** 提供服务类入口及实例转发入口，不创建后台任务。 */
    class MetadataService : Service() {
        override fun onBind(intent: Intent?): IBinder? = null
    }

    /** 提供接收器类入口及实例转发入口，不注册广播。 */
    class MetadataReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) = Unit
    }
}
