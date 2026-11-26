plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}
dependencies {
    implementation(libs.ksp.api)
    implementation(libs.auto.service.annotations)
    testImplementation("junit:junit:4.13.2")
    testImplementation(gradleTestKit())
}

val fixtureProcessorJar by tasks.registering(Jar::class) {
    archiveClassifier.set("test-fixture-processor")
    from(sourceSets.test.get().output)
}

tasks.test {
    dependsOn(tasks.jar, fixtureProcessorJar)
    maxParallelForks = 1
    systemProperty("processor.jar", tasks.jar.get().archiveFile.get().asFile.absolutePath)
    systemProperty("fixture.processor.jar", fixtureProcessorJar.get().archiveFile.get().asFile.absolutePath)
    systemProperty("fixture.gradle.home", gradle.gradleHomeDir!!.absolutePath)
    systemProperty("fixture.output", layout.buildDirectory.dir("ksp-fixtures").get().asFile.absolutePath)
    doFirst {
        val runtimeJars = configurations.runtimeClasspath.get().files
        systemProperty("annotations.jar", runtimeJars.single { it.name.startsWith("auto-service-annotations-") }.absolutePath)
        systemProperty("ksp.api.jar", runtimeJars.single { it.name.startsWith("symbol-processing-api-") }.absolutePath)
    }
}
