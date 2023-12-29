/**
 * className :VersionControlPlugin
 * packageName :
 * createTime :2022/5/6 15:33
 * @Author :  Lzq
 */
object Versions {
    //Androidx------------------------------------------------------------------
    const val appcompat       = "1.3.1"
    const val coreKtx         = "1.7.0"
    const val kotlin          = "1.5.31"
    const val multiDex        = "2.0.1"
    const val constraint      = "2.1.0"
    const val legacy          = "1.0.0"
    const val exifInterface   = "1.3.6"
    const val activityKtx     = "1.5.1"
    const val fragmentKtx     = "1.5.2"

    //Google--------------------------------------------------------------------
    const val material        = "1.4.0"
    const val autoService     = "1.1.1"

    //Test----------------------------------------------------------------------
    const val junit           = "4.13.2"
    const val junitExt        = "1.1.3"
    const val espressoCore    = "3.4.0"

    //JitPack-------------------------------------------------------------------
    const val navigationCode  = "2.4.2"
    const val coroutines      = "1.6.1"
    const val lifecycle       = "2.4.1"
    const val hilt            = "2.44"

    //Github--------------------------------------------------------------------
    const val commonsIo       = "2.6"
    const val gson            = "2.8.5"
    const val okHttp          = "4.9.0"
    const val okHttpLogging   = "4.9.1"
    const val retrofit        = "2.9.0"
    const val reConverterGson = "2.9.0"
    const val MMKV            = "1.2.9"
    const val leakCanary      = "2.7"
    const val autoSize        = "1.2.1"
    const val aRouter         = "1.5.1"
    const val aRouterCompiler = "1.5.1"
    const val toastUtil       = "12.6"
    const val eventBus        = "3.2.0"
    const val rxjava          = "3.1.5"
    const val rxAndroid       = "3.0.2"

    //项目中使用的第三方库---------------------------------------------------------
    const val tencentBugly    = "3.3.9"
    const val buglyNative = "3.8.0"

    //Project-------------------------------------------------------------------
    const val minSdk          = 23
    const val compileSdk      = 33
    const val targetSdk       = 33
    const val versionCode     = 1
    const val versionName     = "1.0.0"
    const val applicationId   = "com.lzq.dusk"

}

object AndroidX {
    const val appcompat       = "androidx.appcompat:appcompat:${Versions.appcompat}"
    const val coreKtx         = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val consLayout      = "androidx.constraintlayout:constraintlayout:${Versions.constraint}"
    const val legacy          = "androidx.legacy:legacy-support-v4:${Versions.legacy}"
    const val exifInterface   = "androidx.exifinterface:exifinterface:${Versions.exifInterface}"
    const val multiDex        = "androidx.multidex:multidex:${Versions.multiDex}"
}

object Kotlin {
    const val stdlibJdk       = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    const val CoroutinesCore  = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val CoroutinesAnd   = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
}

object Google {
    const val material        = "com.google.android.material:material:${Versions.material}"
    const val autoService     = "com.google.auto.service:auto-service:${Versions.autoService}"
    const val autoAnnotations = "com.google.auto.service:auto-service-annotations:${Versions.autoService}"
}

object Depend {
    const val junit           = "junit:junit${Versions.junit}"
    const val junitExt        = "androidx.test.ext:junit${Versions.junitExt}"
    const val espressoCore    = "androidx.test.espresso:espresso-core:${Versions.espressoCore}"
}

object JetPack {

    const val navigation      = "androidx.navigation:navigation-fragment-ktx:${Versions.navigationCode}"
    const val navigation_ui   = "androidx.navigation:navigation-ui-ktx:${Versions.navigationCode}"
    const val viewModel       = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
    const val vmSavedState    = "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.lifecycle}"
    const val liveData        = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"
    const val lifecycle       = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"
    const val lifecycleAPT    = "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycle}"
    const val hiltCore        = "com.google.dagger:hilt-android:${Versions.hilt}"
    const val hiltApt         = "com.google.dagger:hilt-compiler:${Versions.hilt}"

}

object Classpath {
    const val navigation_safe = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navigationCode}"
    const val kotlin          = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
}

object ThirdParty {
    const val commons_io      = "commons-io:commons-io:${Versions.commonsIo}"
    const val gson            = "com.google.code.gson:gson:${Versions.gson}"
    const val okHttp          = "com.squareup.okhttp3:okhttp:${Versions.okHttp}"
    const val okHttpLogging   = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttpLogging}"
    const val retrofit        = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val reConverterGson = "com.squareup.retrofit2:converter-gson:${Versions.reConverterGson}"
    const val MMKV            = "com.tencent:mmkv-static:${Versions.MMKV}"
    const val autoSize        = "me.jessyan:autosize:${Versions.autoSize}"
    const val leakCanary      = "com.squareup.leakcanary:leakcanary-android:${Versions.leakCanary}"
    const val aRouter         = "com.alibaba:arouter-api:${Versions.aRouter}"
    const val aRouterCompiler = "com.alibaba:arouter-compiler:${Versions.aRouterCompiler}"
    const val tencentBugly    = "com.tencent.bugly:crashreport:${Versions.tencentBugly}"
    const val buglyNative     = "com.tencent.bugly:nativecrashreport:${Versions.buglyNative}"
    const val toaster         = "com.github.getActivity:Toaster:${Versions.toastUtil}"
    const val eventbusAPT     = "org.greenrobot:eventbus-annotation-processor:${Versions.eventBus}"
    const val eventbus        = "org.greenrobot:eventbus:${Versions.eventBus}"
    const val rxandroid       = "io.reactivex.rxjava3:rxandroid:${Versions.rxAndroid}"
    const val rxjava3         = "io.reactivex.rxjava3:rxjava:${Versions.rxjava}"

}


