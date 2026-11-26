import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import java.io.ByteArrayInputStream

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
    id("signing")
    id("com.google.devtools.ksp")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = ProjectConfigs.namespace
    resourcePrefix = ProjectConfigs.resourcePrefix
    compileSdk = ProjectConfigs.compileSdk

    defaultConfig {
        minSdk = ProjectConfigs.minSdk

        testInstrumentationRunner = ProjectConfigs.testRunner
        consumerProguardFiles(ProjectConfigs.consumerRules, ProjectConfigs.proguardRules)


    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile(ProjectConfigs.proguardName), ProjectConfigs.proguardRules)
        }

        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = ProjectConfigs.jvmTarget
    }

    buildFeatures {
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = ProjectConfigs.kotlinCompiler
    }

    sourceSets.named("main") {
        java.srcDirs(ProjectConfigs.javaSrc)
        kotlin.srcDirs(ProjectConfigs.ktSrc)
        jniLibs.srcDirs(ProjectConfigs.libs, ProjectConfigs.jniLibs)
    }

    lint {
        baseline = file(ProjectConfigs.lintName)
        enable.add("deprecation")
    }

    testOptions { unitTests.isIncludeAndroidResources = true }

    publishing { singleVariant("release") { withSourcesJar() } }

    libraryVariants.all {
        artifacts {
        }
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.14.1")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("com.squareup.okhttp3:mockwebserver:5.0.0-alpha.11")
    ksp(project(":auto-service-ksp"))
    // ---------------------------依赖---------------------------
    api(libs.androidx.appcompat)
    api(libs.androidx.core.ktx)
    api(libs.androidx.exifinterface)
    api(libs.androidx.multidex)

    api(libs.auto.service.annotations)

    api(libs.androidx.material)

    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.androidx.lifecycle.livedata.ktx)
    api(libs.androidx.lifecycle.runtime.ktx)

    api(libs.androidx.room.runtime)
    api(libs.androidx.room.ktx)

    api(libs.hilt.android)

    api(platform(libs.compose.bom))
    api(libs.androidx.activity)
    api(libs.androidx.material3)
    api(libs.androidx.material)
    api(libs.androidx.ui.tooling)
    api(libs.androidx.ui.util)
    api(libs.androidx.material.icons.extended)
    api(libs.accompanist.themeadapter)
    api(libs.accompanist.systemuicontroller)

//    api(libs.kotlin.stdlib)
    api(libs.kotlin.coroutines.core)
    api(libs.kotlinx.coroutines.android)

    api(libs.gson)
    api(libs.toaster)

    api(libs.eventbus)
    api(libs.reactivex.rxjava3)
    api(libs.reactivex.rxjava3.rxandroid)
    api(libs.retrofit)
    api(libs.retrofit.converter.gson)
    api(libs.okhttp.logging.interceptor)

    api(libs.androidx.core.ktx)
    api(libs.androidx.appcompat)
    api(libs.androidx.material)
    api(libs.androidx.activity)
    api(libs.androidx.constraintlayout)

    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    // ---------------------------依赖---------------------------

    // -------------------------注解依赖--------------------------

    ksp(libs.eventbus.annotation)
    ksp(libs.androidx.lifecycle.common.compiler)
    ksp(libs.androidx.room.compiler)
    //ksp(libs.hilt.android.compiler)

    // -------------------------注解依赖--------------------------
}


// 认证只从显式参数或环境读取，不把本机私密属性复制进发布元数据。
val publishVersion = providers.gradleProperty("PUBLISH_VERSION").get()
val mavenGroupId = providers.gradleProperty("PUBLISH_GROUP_ID").get()
val mavenArtifactId = providers.gradleProperty("PUBLISH_ARTIFACT_ID").get()
require(listOf(publishVersion, mavenGroupId, mavenArtifactId).all { it.isNotBlank() }) {
    "发布坐标不得为空"
}

afterEvaluate {
    publishing {
        repositories {
            maven {
                name = "Central"
                // 旧 OSSRH 发布入口已日落，改用 Central Portal 的 OSSRH Staging API 兼容端点。
                url = uri(CentralPortalStaging.deployUrl())
                credentials {
                    username = providers.gradleProperty("publishUsername").orElse(providers.environmentVariable("MAVEN_USERNAME")).orNull
                    password = providers.gradleProperty("publishPassword").orElse(providers.environmentVariable("MAVEN_PASSWORD")).orNull
                }
            }
            maven {
                name = "Candidate"
                url = uri(layout.buildDirectory.dir("candidate-repository"))
            }
        }
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = mavenGroupId
                artifactId = mavenArtifactId
                version = publishVersion
                pom {
                    name.set(mavenArtifactId)
                    description.set("An Android application development framework")
                    url.set("https://github.com/asasa868/DuskMVX")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers { developer { id.set("asasa868"); name.set("asasa868") } }
                    scm {
                        connection.set("scm:git:https://github.com/asasa868/DuskMVX.git")
                        developerConnection.set("scm:git:ssh://git@github.com/asasa868/DuskMVX.git")
                        url.set("https://github.com/asasa868/DuskMVX")
                    }
                }
            }
        }
    }
    // 本地候选产物不需要真实签名；远端发布仍由维护者显式启用。
    if (providers.gradleProperty("signRelease").orNull == "true") {
        signing { sign(publishing.publications["release"]) }
    }
}

val verifyCandidate by tasks.registering {
    group = "verification"
    description = "检查候选坐标、敏感映射与处理器依赖边界"
    dependsOn("publishReleasePublicationToCandidateRepository")
    doLast {
        val candidate = layout.buildDirectory.dir("candidate-repository").get().asFile
        val sentinel = providers.gradleProperty("credentialSentinel").orElse("SENTINEL_DUSKMVX_13_9f82c").get()
        val files = candidate.walkTopDown().filter { it.isFile }.toList()
        check(listOf(".aar", ".pom", ".module", "-sources.jar").all { suffix -> files.any { it.name.endsWith(suffix) } }) { "候选产物不完整" }
        fun checkBytes(bytes: ByteArray, label: String) {
            check(!bytes.toString(Charsets.ISO_8859_1).contains(sentinel)) { "候选产物包含敏感哨兵：$label" }
            if (label.endsWith(".jar") || label.endsWith(".aar") || label.endsWith(".zip")) {
                ZipInputStream(ByteArrayInputStream(bytes)).use { zip ->
                    while (true) {
                        val entry = zip.nextEntry ?: break
                        if (!entry.isDirectory) checkBytes(zip.readBytes(), label + ":" + entry.name)
                    }
                }
            }
        }
        files.forEach { file ->
            checkBytes(file.readBytes(), file.name)
        }
        val pom = files.single { it.extension == "pom" }.readText()
        check(pom.contains("<groupId>$mavenGroupId</groupId>") && pom.contains("<artifactId>$mavenArtifactId</artifactId>") && pom.contains("<version>$publishVersion</version>")) { "候选坐标不一致" }
        check(!pom.contains("auto-service-ksp") && !pom.contains("symbol-processing-api") && !pom.contains("<artifactId>auto-service</artifactId>")) { "处理器实现泄漏到运行依赖" }
        val report = layout.buildDirectory.file("reports/candidate-validation.txt").get().asFile
        report.parentFile.mkdirs()
        report.writeText("坐标=$mavenGroupId:$mavenArtifactId:$publishVersion\n文件数=" + files.size + "\n哨兵与依赖边界通过\n")
    }
}

tasks.withType<PublishToMavenRepository>().configureEach {
    doFirst {
        if (repository.name == "Central") {
            check(!publishVersion.endsWith("SNAPSHOT")) { "Central Portal staging 兼容端点不接受 SNAPSHOT 版本" }
            check(providers.gradleProperty("allowRemotePublish").orNull == "true") { "远端发布需要维护者单独授权" }
            check(providers.gradleProperty("signRelease").orNull == "true") { "远端发布必须显式启用签名" }
            check(!repository.credentials.username.isNullOrBlank() && !repository.credentials.password.isNullOrBlank()) { "远端发布认证参数缺失" }
        }
    }
}

// 显式人工动作：把已通过兼容端点上交的制品交接为 USER_MANAGED 部署。
// 不依赖任何上传、候选或发布任务，默认构建、CI 与 GitHub Release 路径都不会触发。
tasks.register("centralManualUpload") {
    group = "publishing"
    description = "显式创建 Central Portal USER_MANAGED 部署，不执行正式发布；需要 -PallowCentralStaging=true"
    doLast {
        check(providers.gradleProperty("allowCentralStaging").orNull == "true") {
            "Central Portal 部署交接需要维护者单独授权：-PallowCentralStaging=true"
        }
        check(!publishVersion.endsWith("SNAPSHOT")) {
            "Central Portal staging 兼容端点不接受 SNAPSHOT 版本，请先设置发布版本"
        }
        // 只复用既有凭据注入机制，凭据与认证头都不得出现在日志或异常信息里。
        val injectedUser = providers.gradleProperty("publishUsername")
            .orElse(providers.environmentVariable("MAVEN_USERNAME")).orNull
        val injectedPassword = providers.gradleProperty("publishPassword")
            .orElse(providers.environmentVariable("MAVEN_PASSWORD")).orNull
        val tokenUser = injectedUser?.takeIf { it.isNotBlank() } ?: error("缺少 Central Portal user token 用户名")
        val tokenPassword = injectedPassword?.takeIf { it.isNotBlank() } ?: error("缺少 Central Portal user token 密码")
        val namespace = mavenGroupId
        val status = CentralPortalStaging.manualUpload(
            CentralPortalStaging.BASE_URL,
            namespace,
            tokenUser,
            tokenPassword,
        )
        logger.lifecycle(
            "已创建 Central Portal USER_MANAGED 部署（HTTP $status，命名空间 $namespace）。" +
                "请到 central.sonatype.com/publishing 人工发布或丢弃；本任务不执行正式发布。",
        )
    }
}
