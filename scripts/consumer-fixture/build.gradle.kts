import java.util.zip.ZipFile
import java.security.MessageDigest

plugins {
    id("com.android.library") version "9.5.3"
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.21")
    }
}

android {
    namespace = "com.example.dusk.consumer"
    compileSdk { version = release(37) { minorApiLevel = 2 } }
    buildToolsVersion = "37.0.0"
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    testOptions.unitTests.all {
        it.systemProperty("candidateAar", providers.gradleProperty("candidateAar").get())
    }
}

dependencies {
    implementation("io.github.asasa868:Dusk:0.0.30")
    testImplementation("junit:junit:4.13.2")
}

val candidateArchive by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
    isTransitive = false
}
dependencies { add(candidateArchive.name, "io.github.asasa868:Dusk:0.0.30@aar") }

// 从实际解析结果取证，不依赖本仓库项目坐标或手工补充处理器依赖。
tasks.register("verifyConsumerDependencies") {
    group = "verification"
    doLast {
        val report = layout.buildDirectory.file("reports/consumer-dependencies.txt").get().asFile
        report.parentFile.mkdirs()
        val lines = mutableListOf<String>()
        for (name in listOf("debugCompileClasspath", "debugRuntimeClasspath")) {
            val components = configurations.getByName(name).incoming.resolutionResult.allComponents
            val coordinates = components.mapNotNull { it.moduleVersion?.toString() }.sorted()
            lines += name
            lines += coordinates
            check(coordinates.any { it == "io.github.asasa868:Dusk:0.0.30" }) { "未解析到候选坐标" }
            check(coordinates.any { it.startsWith("com.google.auto.service:auto-service-annotations:") }) { "注解消费契约缺失" }
            check(coordinates.none { it.contains("auto-service-ksp") || it.startsWith("com.google.devtools.ksp:") || it.startsWith("com.google.auto.service:auto-service:") }) {
                "业务类路径不应携带处理器实现或处理器接口依赖"
            }
        }
        val aar = File(providers.gradleProperty("candidateAar").get())
        check(aar.isFile) { "候选文件不存在" }
        fun digest(file: File) = MessageDigest.getInstance("SHA-256").digest(file.readBytes()).joinToString("") { "%02x".format(it) }
        val resolvedAar = candidateArchive.singleFile
        check(digest(aar) == digest(resolvedAar)) { "实际消费的归档与本轮候选文件不同" }
        lines += "candidateSha256=" + digest(aar)
        ZipFile(aar).use { zip -> check(zip.getEntry("classes.jar") != null) { "候选归档缺少类文件包" } }
        report.writeText(lines.joinToString("\n"))
        println("消费者依赖报告：" + report.absolutePath)
    }
}

