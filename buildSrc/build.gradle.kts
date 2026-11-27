plugins {
    kotlin("jvm") version "2.3.21"
}

// 这里只编译配置常量与发布工具，不声明或发布构建插件。
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
repositories {
    google()
    mavenCentral()
    maven("https://maven.aliyun.com/repository/public")
}

dependencies {
    // 发布入口逻辑只在本机 mock 服务上验证，不连接 Central。
    testImplementation("junit:junit:4.13.2")
}

