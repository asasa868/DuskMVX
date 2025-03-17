plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}



dependencies{
    api(libs.ksp.api)
    api(libs.auto.service)
}





