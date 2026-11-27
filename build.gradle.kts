// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.kotlin) apply false
    alias(libs.plugins.android.ksp) apply false
    alias(libs.plugins.compose.compiler) apply false
    id("androidx.room") version "2.8.5" apply false
//    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    alias(libs.plugins.kotlin.jvm) apply false
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.layout.buildDirectory)
}


