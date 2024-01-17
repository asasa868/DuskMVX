import java.io.FileInputStream
import java.util.Properties

plugins {
    //一定要一摸一样 双引号不会报错 但是依赖后 会提示找不到依赖包的类
    `kotlin-dsl`
    groovy
    `java-gradle-plugin`
    `maven-publish`
}
repositories {
    google()
    mavenCentral()
    maven("https://maven.aliyun.com/repository/public")
}
gradlePlugin {
    plugins {

    }
}

dependencies {

    implementation(gradleApi())
    implementation(localGroovy())

}

sourceSets {
    main {
        groovy {
            setSrcDirs(srcDirs + "src/main/java")
        }
    }
}

extra["PUBLISH_VERSION"]           = ""
extra["PUBLISH_GROUP_ID"]          = ""
extra["PUBLISH_ARTIFACT_ID"]       = ""

// 遍历赋值
val secretPropsFile = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    val p =  Properties()
    p.load(FileInputStream(secretPropsFile))
    p.forEach { name, value ->
        extra[name.toString()] = value
    }
} else {
    println("No props file, loading env vars")
}

var publishVersion    = extra["PUBLISH_VERSION"].toString()
var mavenGroupId      = extra["PUBLISH_GROUP_ID"].toString()
var mavenArtifactId   = extra["PUBLISH_ARTIFACT_ID"].toString()

publishing{
    publications{
        create<MavenPublication>("release") {
            from(components["java"])

            groupId = mavenGroupId
            artifactId = mavenArtifactId
            version = publishVersion

        }
    }
}


