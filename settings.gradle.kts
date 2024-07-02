import org.gradle.api.initialization.resolve.RepositoriesMode

rootProject.name = "Dusk"
include(":app")
include(":dawn")
include(":login")
include(":main_compose")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { setUrl("https://maven.google.com") }
        maven { setUrl("https://jitpack.io") }
    }
}

buildscript {
    repositories {
        google()
        mavenLocal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    versionCatalogs {
        create("app") { from(files("gradle/app.versions.toml")) }
        create("compose") { from(files("gradle/compose.versions.toml")) }
    }

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { setUrl("https://maven.google.com") }
        maven { setUrl("https://jitpack.io") }
    }
}