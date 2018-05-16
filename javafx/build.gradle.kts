import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.2.41"
}

dependencies {
    compile(project(":kotlin"))
}

java.sourceSets {
    "main" {
        java.srcDirs("src")
        resources.srcDirs("resources")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

kotlin.experimental.coroutines = Coroutines.ENABLE
