import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.main_compose"
    compileSdk = ProjectConfigs.compileSdk

    defaultConfig {
        minSdk = ProjectConfigs.minSdk

        testInstrumentationRunner = ProjectConfigs.testRunner
        consumerProguardFiles(ProjectConfigs.consumerRules, ProjectConfigs.proguardRules)
        vectorDrawables {
            useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = ProjectConfigs.jvmTarget
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = ProjectConfigs.kotlinCompiler
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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
            archivesName.set(project.name)
        }
    }
}

dependencies {

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    kapt(libs.auto.service.annotations)
    implementation(project(":dawn"))
}