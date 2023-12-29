import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    id("maven-publish")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    namespace = "com.lzq.dawn"
    resourcePrefix = "dawn_"

    compileSdk = 33

    defaultConfig {
        minSdk = 23
        targetSdk = 33


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
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }

        debug{

        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    viewBinding {
        enable = true
    }






    lint {
        baseline = file("lint-baseline.xml")
    }

    publishing {
        multipleVariants("${project.name}--${project.version}.aar") {
            allVariants()
            withJavadocJar()
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

//publishing {
//    publications {
//        register<MavenPublication>("release") {
//            groupId = "com.lzq.dawn"
//            artifactId = "dawn"
//            version = "1.0.0"
//
//            afterEvaluate{
//                from(components["release"])
//            }
//        }
//    }
//
//    repositories{
//        maven{
//            name = "dawnRepo"
//            url =uri("${project.buildDir}/repo")
//        }
//    }
//}


