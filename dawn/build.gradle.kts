import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("maven-publish")
    id("signing")
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

    testFixtures {
        enable = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile(ProjectConfigs.proguardName), ProjectConfigs.proguardRules)
        }

        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = ProjectConfigs.jvmTarget
    }

    buildFeatures {
        viewBinding = true
        compose = true
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
    }

    libraryVariants.all {
        artifacts {
            archivesName.set("${project.name}")
        }
    }
}

dependencies {

    //---------------------------依赖---------------------------
    api(AndroidX.appcompat)
    api(AndroidX.coreKtx)
    api(AndroidX.exifInterface)
    api(AndroidX.multiDex)

    api(Google.autoService)
    api(Google.material)

    api(JetPack.viewModel)
    api(JetPack.liveData)
    api(JetPack.lifecycle)
    api(platform(compose.bom))
    api(compose.androidx.activity)
    api(compose.androidx.material3)
    api(compose.androidx.material)
    api(compose.androidx.ui.tooling)
    api(compose.androidx.ui.util)
    api(compose.androidx.material.icons.extended)
    api(compose.accompanist.themeadapter)
    api(compose.accompanist.systemuicontroller)


    api(Kotlin.stdlibJdk)
    api(Kotlin.CoroutinesCore)
    api(Kotlin.CoroutinesAnd)

    api(ThirdParty.gson)
    api(ThirdParty.toaster)
    api(ThirdParty.aRouter)
    api(ThirdParty.eventbus)
    api(ThirdParty.rxjava3)
    api(ThirdParty.rxandroid)
    //---------------------------依赖---------------------------

    //-------------------------注解依赖--------------------------
    kapt(Google.autoAnnotations)
    kapt(ThirdParty.aRouterCompiler)
    kapt(ThirdParty.eventbusAPT)
    kapt(JetPack.lifecycleAPT)


    //-------------------------注解依赖--------------------------
}

//-------------------------注解依赖--------------------------

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
 */
extra["PUBLISH_VERSION"]           = ""
extra["PUBLISH_GROUP_ID"]          = ""
extra["PUBLISH_ARTIFACT_ID"]       = ""
extra["signing.keyId"]             = ""
extra["signing.password"]          = ""
extra["signing.secretKeyRingFile"] = ""
extra["username"]                  = ""
extra["password"]                  = ""
extra["email"]                     = ""
// 遍历赋值
val secretPropsFile = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    val p = Properties()
    p.load(FileInputStream(secretPropsFile))
    p.forEach { name, value ->
        extra[name.toString()] = value
    }
} else {
    println("No props file, loading env vars")
}

var publishVersion    = extra["PUBLISH_VERSION"].toString()
var mavenGroupId      = extra["PUBLISH_GROUP_ID"].toString()
var mavenArtifactId   = extra["PUBLISH_ARTIFACT_ID"].toString()
var signingKeyId      = extra["signing.keyId"].toString()
var signingPassword   = extra["signing.password"].toString()
var secretKeyRingFile = extra["signing.secretKeyRingFile"].toString()
var ossUsername       = extra["username"].toString()
var ossPassword       = extra["password"].toString()
var ossEmil           = extra["email"].toString()
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
                println("publish-maven Log-------> PUBLISH_GROUP_ID: $mavenGroupId; PUBLISH_ARTIFACT_ID: $mavenArtifactId; PUBLISH_VERSION: $publishVersion")

                groupId = mavenGroupId
                artifactId = mavenArtifactId
                version = publishVersion

                //生成的 aar 路径，修改成自己的aar地址名称
                artifact("$buildDir/outputs/aar/${project.name}-release.aar")
                //将源代码一起打包进aar
                artifact(tasks["androidSourcesJar"]) //将源码打包进aar,这样使用方可以看到方法注释.
                artifact(tasks["androidJavadocsJar"]) //将注释打包进aar

                pom {
                    name.set(mavenArtifactId)
                    //项目描述
                    description.set("An Android application development framework")
                    //项目github链接
                    url.set("https://github.com/asasa868/DuskMVX")
                    licenses {
                        license {
                            //协议类型，一般默认Apache License2.0
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            //修改对应的用户名、邮箱
                            //sonatype用户名
                            id.set(ossUsername)
                            //你的sonatype用户名
                            name.set(ossPassword)
                            //你的sonatype注册邮箱
                            email.set(ossEmil)
                        }
                    }
                    // 版本控制信息，如果使用的是GitHub，按照下面的格式
                    scm {
                        //你的Git地址：
                        connection.set("scm:git@github.com:asasa868/DuskMVX.git")
                        developerConnection.set("scm:git@github.com:asasa868/DuskMVX.git")
                        //分支地址：
                        url.set("https://github.com/asasa868/DuskMVX/tree/master")
                    }

                    withXml {
                        val dependenciesNode = asNode().appendNode("dependencies")
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
    signing {
        useGpgCmd()
        //sign(publishing.publications)
    }
}


// 生成文档注释
tasks.register("androidJavadocs",Javadoc::class) {
    // 设置源码所在的位置
    source(android.sourceSets["main"].java.srcDirs)

}

// 将文档打包成jar,生成javadoc.jar
tasks.register("androidJavadocsJar",Jar::class) {
    // 指定文档名称
    archiveClassifier.set("javadoc")
    from(tasks["androidJavadocs"].outputs)
}

// 将源码打包 ，生成sources.jar
tasks.register("androidSourcesJar",Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets["main"].java.srcDirs)

    exclude("**/R.class","**/BuildConfig.class")
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


//配置需要上传到maven仓库的文件
artifacts {
    archives(tasks["androidSourcesJar"]) //将源码打包进aar,这样使用方可以看到方法注释.
    archives(tasks["androidJavadocsJar"]) //将注释打包进aar
}

