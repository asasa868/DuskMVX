package com.example.dusk.consumer

import com.google.auto.service.AutoService
import com.lzq.dawn.DawnApplication
import com.lzq.dawn.base.app.BaseApplicationLifecycle
import com.lzq.dawn.network.bean.DawnHttpResult
import com.lzq.dawn.sseClient.SseClient
import com.lzq.dawn.sseClient.SseConfig
import org.junit.Assert.*
import org.junit.Test
import java.net.URLClassLoader
import java.nio.file.Files
import java.util.ServiceLoader
import java.util.zip.ZipFile

/**
 * @projectName com.example.dusk.consumer.CandidateArchiveTest
 * @author Lzq
 * @date : Created by Lzq on 2026/11/25 14:15:01
 * @version 0.0.30
 * @description: 验证候选归档中的公开接口与嵌套服务注册可被独立消费者加载
 */
class CandidateArchiveTest {
    @Test fun publicCoordinatesExposeResultStreamAndAnnotationWithoutProcessorDependency() {
        val result = DawnHttpResult.Success("candidate")
        val events = SseClient(SseConfig("https://example.invalid/events")).connect()

        assertEquals("candidate", result.data)
        assertNotNull(events)
        assertEquals("com.google.auto.service.AutoService", AutoService::class.java.name)
        assertEquals("com.lzq.dawn.DawnApplication", DawnApplication::class.java.name)
    }

    @Test fun archiveContainsNestedServiceRegistrationForPublicApplicationLifecycle() {
        ZipFile(System.getProperty("candidateAar")).use { aar ->
            val classes = aar.getEntry("classes.jar")
            assertNotNull("候选归档必须包含类文件包", classes)
            val temporaryJar = Files.createTempFile("dusk-consumer-classes-", ".jar").toFile()
            aar.getInputStream(classes).use { source -> temporaryJar.outputStream().use { destination -> source.copyTo(destination) } }

            ZipFile(temporaryJar).use { jar ->
                val service = jar.getEntry("META-INF/services/" + BaseApplicationLifecycle::class.java.name)
                assertNotNull("嵌套类文件包必须保留服务注册", service)
                val implementations = jar.getInputStream(service).bufferedReader().use { reader ->
                    reader.readLines().map { it.trim() }.filter { it.isNotEmpty() && !it.startsWith("#") }
                }
                assertEquals(listOf(DawnApplication::class.java.name), implementations)
                assertNotNull(jar.getEntry("com/lzq/dawn/DawnApplication.class"))
            }

            URLClassLoader(arrayOf(temporaryJar.toURI().toURL()), BaseApplicationLifecycle::class.java.classLoader).use { loader ->
                val services = ServiceLoader.load(BaseApplicationLifecycle::class.java, loader).toList()
                assertEquals(1, services.count { it is DawnApplication })
            }
        }
    }
}

