
plugins {
    id("com.android.library")
    id("maven-publish")
    id("kotlin-android")
    id("kotlin-kapt")
    id("publishing")
}

android {
    namespace = "com.lzq.dawn"
    resourcePrefix = "dawn_"
    compileSdk = 33

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro", "proguard-rules.pro")

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
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
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
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    sourceSets.named("main") {
        java.srcDirs("src/main/java")
        kotlin.srcDirs("src/main/kotlin")
        jniLibs.srcDirs("libs", "jniLibs")
    }

    lint {
        baseline = file("lint-baseline.xml")
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

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.lzq.dawn"
            artifactId = "dawn"
            version = "1.0.0"

            afterEvaluate{
                from(components["release"])
            }
        }
    }

    repositories{
        maven{
            name = "dawnRepo"
            url =uri("${project.buildDir}/repo")
        }
    }

}


