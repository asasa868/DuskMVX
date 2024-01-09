
plugins {
    id("com.android.library")
    id("maven-publish")
    id("kotlin-android")
    id("kotlin-kapt")
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

