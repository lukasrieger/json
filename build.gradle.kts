plugins {
    kotlin("jvm") version "1.9.23"
    application
}

group = "com.lukas"
version = "1.0-SNAPSHOT"

object DepVersions {
    const val kotestVersion = "5.8.1"
    const val arrowVersion = "1.2.1"
}



repositories {
    mavenCentral()
}

dependencies {
    implementation("io.arrow-kt:arrow-core:${DepVersions.arrowVersion}")

    testImplementation("io.kotest:kotest-runner-junit5:${DepVersions.kotestVersion}")
    testImplementation("io.kotest:kotest-assertions-core:${DepVersions.kotestVersion}")
    testImplementation("io.kotest:kotest-property:${DepVersions.kotestVersion}")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}