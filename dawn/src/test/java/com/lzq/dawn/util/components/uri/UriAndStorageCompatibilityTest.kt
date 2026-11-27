package com.lzq.dawn.util.components.uri

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.Process
import android.os.storage.StorageManager
import com.lzq.dawn.DawnBridge
import com.lzq.dawn.util.file.SDCardUtils
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.StorageVolumeBuilder
import java.io.File

/** 验证移除最低系统版本以下分支后，文件定位与存储信息入口保持可用。 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26, 34, 37], application = Application::class, manifest = Config.NONE)
class UriAndStorageCompatibilityTest {
    private val storageManager
        get() = RuntimeEnvironment.getApplication().getSystemService(Context.STORAGE_SERVICE) as StorageManager

    @Before fun initializeApplication() {
        DawnBridge.init(RuntimeEnvironment.getApplication())
        shadowOf(storageManager).resetStorageVolumeList()
    }

    @After fun resetStorageVolumes() {
        shadowOf(storageManager).resetStorageVolumeList()
    }

    @Test fun nullInputsRetainTheirEmptyResults() {
        assertNull(UriUtils.file2Uri(null))
        assertNull(UriUtils.uri2File(null))
        assertNull(UriUtils.uri2Bytes(null))
    }

    @Test fun fileSchemeRetainsTheOriginalPathWithoutCreatingAFile() {
        val file = File(RuntimeEnvironment.getApplication().filesDir, "uri-check.txt")

        assertEquals(file, UriUtils.uri2File(Uri.fromFile(file)))
    }

    @Test fun unavailableFileIsNotExposedAsAProviderUri() {
        val file = File(RuntimeEnvironment.getApplication().filesDir, "unavailable-uri-check.txt")

        assertNull(UriUtils.file2Uri(file))
    }

    @Test fun mountedStoragePathsRemainConsistentWithReportedVolumes() {
        val application = RuntimeEnvironment.getApplication()
        val mountedPath = application.filesDir
        val unmountedPath = application.cacheDir
        val storage = shadowOf(storageManager)
        storage.addStorageVolume(
            StorageVolumeBuilder(
                "mounted", mountedPath, "已挂载测试卷", Process.myUserHandle(), Environment.MEDIA_MOUNTED
            ).setIsPrimary(false).setIsRemovable(true).build()
        )
        storage.addStorageVolume(
            StorageVolumeBuilder(
                "unmounted", unmountedPath, "未挂载测试卷", Process.myUserHandle(), Environment.MEDIA_UNMOUNTED
            ).setIsPrimary(false).setIsRemovable(false).build()
        )

        val volumes = SDCardUtils.sDCardInfo

        assertEquals(2, volumes.size)
        assertEquals(listOf(mountedPath.absolutePath, unmountedPath.absolutePath), volumes.map { it.path })
        assertEquals(listOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_UNMOUNTED), volumes.map { it.state })
        // 记录未公开可移除属性，通过公开描述验证其保留配置值。
        assertTrue(volumes[0].toString().contains("isRemovable = true"))
        assertTrue(volumes[1].toString().contains("isRemovable = false"))
        assertEquals(listOf(mountedPath.absolutePath), SDCardUtils.mountedSDCardPath)
    }

    @Test fun noStorageVolumesProduceEmptyInfoAndMountedPaths() {
        assertTrue(SDCardUtils.sDCardInfo.isEmpty())
        assertTrue(SDCardUtils.mountedSDCardPath.isEmpty())
    }
}
