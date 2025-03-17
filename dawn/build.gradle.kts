import groovy.util.Node
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
    id("signing")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
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

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["AROUTER_MODULE_NAME"] = project.name
            }
        }
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

    libraryVariants.all {
        artifacts {
        }
    }
}

dependencies {
    ksp(project(":auto-service-ksp"))
    api(project(":auto-service-ksp"))
    // ---------------------------依赖---------------------------
    api(libs.androidx.appcompat)
    api(libs.androidx.core.ktx)
    api(libs.androidx.exifinterface)
    api(libs.androidx.multidex)

    api(libs.auto.service)

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
    api(libs.arouter.api){
        exclude("com.android.support")
    }
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

    androidTestApi(libs.androidx.test.junit)
    androidTestApi(libs.androidx.test.espresso.core)
    // ---------------------------依赖---------------------------

    // -------------------------注解依赖--------------------------
    ksp(libs.auto.service.annotations)
    ksp(libs.arouter.compiler)
    ksp(libs.eventbus.annotation)
    ksp(libs.androidx.lifecycle.common.compiler)
    ksp(libs.androidx.room.compiler)
    ksp(libs.hilt.android.compiler)

    // -------------------------注解依赖--------------------------
}

/**
 * 以下打包上传部分可以独立放在单独的.gradle.kts脚本文件中，但是遇到了问题：
 * 在kotlin dsl脚本中引入其他独立的 kotlin的dsl脚本不方便，会存在无法识别相关依赖对象的问题
 * 目前的解决办法是使用gradle脚本，不使用kotlin的dsl脚本
 * 或者写在当前模块中
 *
 * 此处不需修改，下面会读取 local.properties 中配置的信息
 * PUBLISH_VERSION            发布的版本
 * PUBLISH_GROUP_ID           分组ID
 * PUBLISH_ARTIFACT_ID
 * signing.keyId              签名的密钥后8位
 * signing.password           签名设置的密码
 * signing.secretKeyRingFile  生成的secring.gpg文件目录
 * username                   sonatype用户名
 * username                   sonatype密码
 * email                      自己的邮箱
 * isJitpack                  是否是jitpack仓库，仓库不同使用的签名不同,详情查看signing{}
 */
extra["PUBLISH_VERSION"] = ""
extra["PUBLISH_GROUP_ID"] = ""
extra["PUBLISH_ARTIFACT_ID"] = ""
extra["signing.keyId"] = ""
extra["signing.password"] = ""
extra["signing.secretKeyRingFile"] = ""
extra["username"] = ""
extra["password"] = ""
extra["email"] = ""
extra["isJitpack"] = ""
// 遍历赋值
val secretPropsFile: File = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    val p = Properties()
    p.load(FileInputStream(secretPropsFile))
    p.forEach { name, value ->
        extra[name.toString()] = value
    }
} else {
    println("No props file, loading env vars")
}

val versionFile: File = project.rootProject.file("gradle.properties")
if (versionFile.exists()) {
    val p = Properties()
    p.load(FileInputStream(versionFile))
    p.forEach { name, value ->
        extra[name.toString()] = value
    }
} else {
    println("No props file, loading env vars")
}

var publishVersion = extra["PUBLISH_VERSION"].toString()
var mavenGroupId = extra["PUBLISH_GROUP_ID"].toString()
var mavenArtifactId = extra["PUBLISH_ARTIFACT_ID"].toString()
var signingKeyId = extra["signing.keyId"].toString()
var signingPassword = extra["signing.password"].toString()
var secretKeyRingFile = extra["signing.secretKeyRingFile"].toString()
var ossUsername = extra["username"].toString()
var ossPassword = extra["password"].toString()
var ossEmil = extra["email"].toString()
var isJitpack = extra["isJitpack"].toString()
// 暂存库
val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
// 快照库（版本名以 SNAPSHOT 结尾，就推送至快照库）
val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"

afterEvaluate {
    publishing {
        repositories {
            maven {
                /**
                 * 推送至远端的中央仓库，一旦发布release中央仓库版本，旧版本无法修改
                 * 一般都在暂存库中进行测试，然后确认无误后再发布到 release中央仓库
                 */
                isAllowInsecureProtocol = false
                name = mavenArtifactId

                url = if (publishVersion.endsWith("SNAPSHOT")) {
                    uri(snapshotsRepoUrl)
                } else {
                    uri(releasesRepoUrl)
                }

                credentials {
                    username = ossUsername
                    password = ossPassword
                }
            }
            maven {
                // 推送至本地存储库，本机测试的时候可以用
                isAllowInsecureProtocol = false
                name = "Local"
                url = uri("../maven")
            }
        }
        publications {
            create<MavenPublication>("release") {
                println("publish-maven Log-------> "
                        + "PUBLISH_GROUP_ID: $mavenGroupId; "
                        + "PUBLISH_ARTIFACT_ID: $mavenArtifactId; "
                        + "PUBLISH_VERSION: $publishVersion"
                )

                from(components.getByName("release"))
                groupId = mavenGroupId
                artifactId = mavenArtifactId
                version = publishVersion

                // 生成的 aar 路径，修改成自己的aar地址名称
                artifact("${layout.buildDirectory}/outputs/aar/${project.name}-release.aar") {
                    classifier = "release"
                }

                pom {
                    name.set(mavenArtifactId)
                    // 项目描述
                    description.set("An Android application development framework")
                    // 项目github链接
                    url.set("https://github.com/asasa868/DuskMVX")
                    licenses {
                        license {
                            // 协议类型，一般默认Apache License2.0
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            // 修改对应的用户名、邮箱
                            // sonatype用户名
                            id.set(ossUsername)
                            // 你的sonatype用户名
                            name.set(ossPassword)
                            // 你的sonatype注册邮箱
                            email.set(ossEmil)
                        }
                    }
                    // 版本控制信息，如果使用的是GitHub，按照下面的格式
                    scm {
                        // 你的Git地址：
                        connection.set("scm:git@github.com:asasa868/DuskMVX.git")
                        developerConnection.set("scm:git@github.com:asasa868/DuskMVX.git")
                        // 分支地址：
                        url.set("https://github.com/asasa868/DuskMVX/tree/master")
                    }

                    withXml {
                        withXml {
                            // 检查是否已存在 <dependencies> 节点
                            val rootNode = asNode()
                            val existingDependenciesNode = rootNode.children().find {
                                (it as Node).name() == "dependencies"
                            }
                            val dependenciesNode: Node = if (existingDependenciesNode == null) {
                                // 如果不存在，则添加 <dependencies> 节点
                                rootNode.appendNode("dependencies")
                            } else {
                                existingDependenciesNode as Node
                            }
                            configurations["implementation"].dependencies.forEach { dependency ->
                                if (dependency.version != "unspecified" && dependency.name != "unspecified") {
                                    val dependencyNode = dependenciesNode.appendNode("dependency")
                                    dependencyNode.appendNode("groupId", dependency.group)
                                    dependencyNode.appendNode("artifactId", dependency.name)
                                    dependencyNode.appendNode("version", dependency.version)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    signing {
        if (isJitpack.isEmpty() || isJitpack == "null" || isJitpack == "true") {
            useGpgCmd()
        } else {
            sign(publishing.publications["release"])
        }
    }
    tasks.named("generateMetadataFileForReleasePublication") {
        inputs.files(tasks.named("androidSourcesJar"))
    }
}

// 生成文档注释
tasks.register("androidJavadocs", Javadoc::class) {
    description = "Generates the Javadoc documentation for this project."
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    title = "project docs"
    outputs.dirs().also {
        layout.buildDirectory.dir("docs")
    }
    // 设置源码所在的位置
    source(android.sourceSets["main"].java.srcDirs)
}

// 将文档打包成jar,生成javadoc.jar
tasks.register("androidJavadocsJar", Jar::class) {
    description = "Generates the JavadocsJar for this project."
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    outputs.dirs().also {
        layout.buildDirectory.dir("docs")
    }
    // 指定文档名称
    archiveClassifier.set("javadoc")
    from(tasks["androidJavadocs"].outputs)
}

// 将源码打包 ，生成sources.jar
tasks.register("androidSourcesJar", Jar::class) {
    description = "Generates the SourcesJar for this project."
    group = JavaBasePlugin.DOCUMENTATION_GROUP

    archiveClassifier.set("dawn-sources")
    from(android.sourceSets["main"].java.srcDirs)

    exclude("**/R.class", "**/BuildConfig.class")
}

if (project.hasProperty("kotlin")) {
    // 禁用创建javadocs
    project.tasks.withType<Javadoc> {
        enabled = false
    }
}

tasks.withType<Javadoc> {
    options {
        encoding = "UTF-8"
        charset("UTF-8")
        version = publishVersion
        title = "$mavenArtifactId $publishVersion"
    }
}

// 配置需要上传到maven仓库的文件
artifacts {
    archives(tasks["androidSourcesJar"]) // 将源码打包进aar,这样使用方可以看到方法注释.
    archives(tasks["androidJavadocsJar"]) // 将注释打包进aar
}
