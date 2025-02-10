import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    namespace = "com.dusk.login"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = ProjectConfigs.jvmTarget
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
            archivesName.set(project.name)
        }
    }
}

dependencies {

    kapt(libs.auto.service.annotations)
    implementation(project(":dawn"))
}