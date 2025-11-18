
plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.google.devtools.ksp")
    alias(libs.plugins.compose.compiler)
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
    }

    libraryVariants.all {
        artifacts {
        }
    }
}

dependencies {

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.activity)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.util)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.accompanist.themeadapter)
    implementation(libs.accompanist.systemuicontroller)

    ksp(project(":auto-service-ksp"))
    implementation(project(":auto-service-ksp"))
    implementation(project(":dawn"))
}

