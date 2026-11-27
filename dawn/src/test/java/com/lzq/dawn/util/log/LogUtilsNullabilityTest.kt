package com.lzq.dawn.util.log

import android.app.Application
import com.lzq.dawn.DawnBridge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File

/** 验证日志路径及换行处理在使用明确非空的平台值后保持一致。 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = Application::class, manifest = Config.NONE)
class LogUtilsNullabilityTest {
    @Before fun initializeApplication() {
        DawnBridge.init(RuntimeEnvironment.getApplication())
    }

    @Test fun configuredDirectoryUsesExactlyOnePlatformSeparator() {
        val path = RuntimeEnvironment.getApplication().filesDir.absolutePath

        assertEquals(path + File.separator, LogUtils.Config().setDir(path).dir)
        assertEquals(path + File.separator, LogUtils.Config().setDir(path + File.separator).dir)
    }

    @Test fun absentLogDirectoryHasNoLogFiles() {
        val directory = File(RuntimeEnvironment.getApplication().filesDir, "absent-log-directory")
        LogUtils.init(LogUtils.Config().setDir(directory))

        assertTrue(LogUtils.logFiles.isEmpty())
    }

    @Test fun multiTagConsoleOutputPreservesMultilineMessageBoundaries() {
        val messages = mutableListOf<String>()
        val config = LogUtils.Config().setGlobalTag("log-check").setLogHeadSwitch(false)
            .setSingleTagSwitch(false).setOnConsoleOutputListener(object : LogClass.OnConsoleOutputListener {
                override fun onConsoleOutput(type: Int, tag: String?, content: String?) {
                    content?.let(messages::add)
                }
            })
        LogUtils.init(config)

        LogUtils.d("first${LogUtils.LINE_SEP}second")

        assertEquals(listOf("│ first", "│ second"), messages.filter { it.startsWith("│ ") })
    }

    @Test fun singleTagConsoleOutputPreservesPlatformLineSeparator() {
        val messages = mutableListOf<String>()
        val config = LogUtils.Config().setGlobalTag("log-check").setLogHeadSwitch(false)
            .setSingleTagSwitch(true).setOnConsoleOutputListener(object : LogClass.OnConsoleOutputListener {
                override fun onConsoleOutput(type: Int, tag: String?, content: String?) {
                    content?.let(messages::add)
                }
            })
        LogUtils.init(config)

        LogUtils.d("first${LogUtils.LINE_SEP}second")

        assertEquals(System.lineSeparator(), LogUtils.LINE_SEP)
        assertEquals(1, messages.size)
        assertTrue(messages.single().contains("│ first${LogUtils.LINE_SEP}│ second"))
    }
}
