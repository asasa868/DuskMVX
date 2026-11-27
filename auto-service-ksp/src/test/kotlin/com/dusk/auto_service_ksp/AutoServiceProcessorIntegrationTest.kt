package com.dusk.auto_service_ksp

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.net.URLClassLoader
import java.util.ServiceLoader

/**
 * @projectName com.dusk.auto_service_ksp.AutoServiceProcessorIntegrationTest
 * @author Lzq
 * @date : Created by Lzq on 2026/11/25 13:54:06
 * @version 0.0.30
 * @description: 验证服务聚合处理器在多轮处理、增量变更和异常声明下的注册结果
 */
class AutoServiceProcessorIntegrationTest {
    private lateinit var fixture: File
    private lateinit var generatedFile: File

    @Before
    fun createFixture() {
        val outputRoot = File(System.getProperty("fixture.output"))
        outputRoot.mkdirs()
        fixture = Files.createTempDirectory(outputRoot.toPath(), "processor-test-").toFile()
        generatedFile = File(fixture, "build/generated/ksp/main/resources/META-INF/services/fixture.Service")
        File(fixture, "settings.gradle").writeText("rootProject.name = 'processor-fixture'\n")
        val buildScript = (
            "plugins {\n" +
                "  id 'org.jetbrains.kotlin.jvm' version '2.3.21'\n" +
                "  id 'com.google.devtools.ksp' version '2.3.14'\n" +
                "}\n" +
                "repositories { mavenCentral() }\n" +
                "dependencies {\n" +
                "  implementation files('ANNOTATIONS_JAR')\n" +
                "  ksp files('PROCESSOR_JAR', 'KSP_API_JAR', 'ANNOTATIONS_JAR', 'FIXTURE_PROCESSOR_JAR')\n" +
                "}\n" +
                "ksp { arg('fixture.generateLate', 'true') }\n"
            )
            .replace("FIXTURE_PROCESSOR_JAR", gradlePath("fixture.processor.jar"))
            .replace("PROCESSOR_JAR", gradlePath("processor.jar"))
            .replace("KSP_API_JAR", gradlePath("ksp.api.jar"))
            .replace("ANNOTATIONS_JAR", gradlePath("annotations.jar"))
        File(fixture, "build.gradle").writeText(buildScript)
        File(fixture, "gradle.properties").writeText("ksp.incremental=true\n")
        writeSources("FirstImplementation")
        File(fixture, "src/main/kotlin/fixture/FirstImplementation.kt").writeText(
            "package fixture\n" +
                "@com.google.auto.service.AutoService(Service::class)\n" +
                "class FirstImplementation : LateBase\n",
        )
    }

    @Test
    fun aggregatesLaterRoundsAndKeepsCleanAndIncrementalOutputsEquivalent() {
        assertEquals(listOf("fixture.FirstImplementation", "fixture.LateImplementation"), runFixture())

        writeSources("FirstImplementation", "SecondImplementation")
        assertEquals(
            listOf("fixture.FirstImplementation", "fixture.LateImplementation", "fixture.SecondImplementation"),
            runFixture(),
        )

        writeSources("SecondImplementation")
        val afterDeletion = runFixture()
        assertEquals(listOf("fixture.LateImplementation", "fixture.SecondImplementation"), afterDeletion)

        writeSources("RenamedImplementation")
        val incremental = runFixture()
        assertEquals(listOf("fixture.LateImplementation", "fixture.RenamedImplementation"), incremental)
        val incrementalBytes = generatedFile.readBytes()

        // 独立的新目录提供干净基线，不清除增量目录或已有构建缓存。
        val incrementalFixture = fixture
        fixture = Files.createTempDirectory(incrementalFixture.parentFile.toPath(), "clean-fixture-").toFile()
        listOf("settings.gradle", "build.gradle", "gradle.properties").forEach {
            File(incrementalFixture, it).copyTo(File(fixture, it))
        }
        File(incrementalFixture, "src").copyRecursively(File(fixture, "src"))
        generatedFile = File(fixture, "build/generated/ksp/main/resources/META-INF/services/fixture.Service")
        val clean = runFixture()
        assertEquals(incremental, clean)
        assertTrue(incrementalBytes.contentEquals(generatedFile.readBytes()))
    }

    @Test
    fun writesSeparateServiceFilesAndJvmNamesForNestedImplementations() {
        File(fixture, "src/main/kotlin/fixture/Container.kt").writeText(
            "package fixture\n" +
                "interface AnotherService\n" +
                "class Container {\n" +
                " @com.google.auto.service.AutoService(Service::class, AnotherService::class)\n" +
                " class NestedImplementation : Service, AnotherService\n" +
                "}\n",
        )

        assertEquals(
            listOf("fixture.Container\$NestedImplementation", "fixture.FirstImplementation", "fixture.LateImplementation"),
            runFixture(),
        )
        val another = File(generatedFile.parentFile, "fixture.AnotherService")
        assertEquals(listOf("fixture.Container\$NestedImplementation"), another.readLines())
    }

    @Test
    fun unresolvedServiceSymbolsFailInsteadOfWritingAPartialRegistry() {
        File(fixture, "src/main/kotlin/fixture/InvalidImplementation.kt").writeText(
            "package fixture\n" +
                "@com.google.auto.service.AutoService(MissingService::class)\n" +
                "class InvalidImplementation\n",
        )

        val result = fixtureRunner().buildAndFail()

        assertTrue(result.output.contains("BUILD FAILED"))
        assertEquals(TaskOutcome.FAILED, result.task(":compileKotlin")?.outcome)
        assertTrue(result.output.contains("Unresolved reference 'MissingService'"))
    }

    @Test
    fun deletingEveryImplementationRemovesThePreviousServiceRegistry() {
        val buildFile = File(fixture, "build.gradle")
        buildFile.writeText(buildFile.readText().replace("fixture.generateLate', 'true", "fixture.generateLate', 'false"))
        writeSources("FirstImplementation")
        assertEquals(listOf("fixture.FirstImplementation"), runFixture())

        writeSources()
        val result = fixtureRunner().build()

        assertTrue(result.output.contains("BUILD SUCCESSFUL"))
        assertTrue("全部实现移除后不得残留服务注册", !generatedFile.exists())
    }

    private fun runFixture(): List<String> {
        val result = fixtureRunner().build()
        assertTrue(result.output.contains("BUILD SUCCESSFUL"))
        val entries = generatedFile.readLines().filter { it.isNotBlank() }
        val classpath = arrayOf(
            File(fixture, "build/classes/kotlin/main").toURI().toURL(),
            File(fixture, "build/resources/main").toURI().toURL(),
        )
        URLClassLoader(classpath, javaClass.classLoader).use { loader ->
            val serviceClass = loader.loadClass("fixture.Service")
            val discovered = ServiceLoader.load(serviceClass, loader).map { it.javaClass.name }.sorted()
            assertEquals(entries, discovered)
        }
        return entries
    }

    private fun fixtureRunner(): GradleRunner {
        val arguments = listOf(
            "classes", "--stacktrace", "--max-workers=1",
            "-Dorg.gradle.jvmargs=-Xmx1g",
        )
        return GradleRunner.create()
            .withProjectDir(fixture)
            .withGradleInstallation(File(System.getProperty("fixture.gradle.home")))
            .withArguments(arguments)
            .forwardOutput()
    }

    private fun writeSources(vararg implementations: String) {
        val sourceDir = File(fixture, "src/main/kotlin/fixture")
        sourceDir.mkdirs()
        File(sourceDir, "Service.kt").writeText("package fixture\ninterface Service\n")
        sourceDir.listFiles().orEmpty().filter { it.name != "Service.kt" }.forEach {
            Files.move(it.toPath(), File(fixture, "removed-${it.name}-${System.nanoTime()}").toPath())
        }
        implementations.forEach { implementation ->
            File(sourceDir, "$implementation.kt").writeText(
                "package fixture\n" +
                    "@com.google.auto.service.AutoService(Service::class)\n" +
                    "class $implementation : Service\n"
            )
        }
    }

    private fun gradlePath(property: String): String =
        System.getProperty(property).replace("\\", "\\\\").replace("'", "\\'")
}
