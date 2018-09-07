import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.2.61"
	maven
}

dependencies {
	compile(project(":kotlin"))
}

sourceSets {
	getByName("main") {
		java.srcDirs("src")
		resources.srcDirs("resources")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions.jvmTarget = "1.8"
}

kotlin.experimental.coroutines = Coroutines.ENABLE
