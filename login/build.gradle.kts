
plugins {
    id("com.android.library")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.dusk.login"
    compileSdk {
        version = release(ProjectConfigs.compileSdk) {
            minorApiLevel = ProjectConfigs.compileSdkMinor
        }
    }
    buildToolsVersion = ProjectConfigs.buildToolsVersion

    defaultConfig {
        minSdk = ProjectConfigs.minSdk

        testInstrumentationRunner = ProjectConfigs.testRunner
        consumerProguardFiles(ProjectConfigs.consumerRules, ProjectConfigs.proguardRules)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile(ProjectConfigs.proguardName), ProjectConfigs.proguardRules)
        }

        debug {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    sourceSets.named("main") {
        java.srcDirs(ProjectConfigs.javaSrc)
        kotlin.directories += ProjectConfigs.ktSrc
        jniLibs.srcDirs(ProjectConfigs.libs, ProjectConfigs.jniLibs)
    }

}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {

    ksp(project(":auto-service-ksp"))
    implementation(project(":dawn"))

}

