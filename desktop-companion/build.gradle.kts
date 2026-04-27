import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    application
}

group = "com.slideremote"
version = "1.0.2"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

application {
    mainClass.set("com.slideremote.desktop.MainKt")
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.3.12")
    implementation("io.ktor:ktor-server-cio-jvm:2.3.12")
    implementation("io.ktor:ktor-server-websockets-jvm:2.3.12")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.google.zxing:javase:3.5.3")
}

tasks.jar {
    archiveBaseName.set("SlideRemoteCompanion")
    manifest {
        attributes["Main-Class"] = "com.slideremote.desktop.MainKt"
    }
}

tasks.register<Exec>("jpackageImage") {
    dependsOn(tasks.installDist)

    val installLibDir = layout.buildDirectory.dir("install/slide-remote-companion/lib")
    val destinationDir = layout.buildDirectory.dir("jpackage")

    doFirst {
        delete(destinationDir.get().asFile)
    }

    commandLine(
        "jpackage",
        "--type", "app-image",
        "--name", "SlideRemoteCompanion",
        "--app-version", project.version.toString(),
        "--vendor", "Slide Remote",
        "--input", installLibDir.get().asFile.absolutePath,
        "--main-jar", "SlideRemoteCompanion-${project.version}.jar",
        "--main-class", "com.slideremote.desktop.MainKt",
        "--dest", destinationDir.get().asFile.absolutePath,
        "--java-options", "-Dfile.encoding=UTF-8"
    )
}
