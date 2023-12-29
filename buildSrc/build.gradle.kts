plugins {
    //一定要一摸一样 双引号不会报错 但是依赖后 会提示找不到依赖包的类
    `kotlin-dsl`
    groovy
    `java-gradle-plugin`
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


