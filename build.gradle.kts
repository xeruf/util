import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	`maven-publish`
	kotlin("jvm") version "1.8.0"
	id("org.jetbrains.dokka") version "1.7.20"
	id("org.openjfx.javafxplugin") version "0.0.13"

	id("com.github.ben-manes.versions") version "0.45.0"
	id("se.patrikerdes.use-latest-versions") version "0.2.18"
}

allprojects {
	group = "xerus.util"
	repositories {
		mavenCentral()
	}
}

subprojects {
	apply(plugin = "kotlin")
	apply(plugin = "maven-publish")
	apply(plugin = "org.jetbrains.dokka")
	
	dependencies {
		testImplementation("io.kotlintest", "kotlintest-runner-junit5", "3.4.2")
	}
	
	tasks {
		
		val docJar by creating(Jar::class) {
			dependsOn(dokkaJavadoc.get())
			archiveClassifier.set("javadoc")
			from(dokkaJavadoc.get().outputDirectory)
		}
		
		val sourcesJar by creating(Jar::class) {
			archiveClassifier.set("sources")
			from(sourceSets.main.get().allSource)
		}
		
		publishToMavenLocal.get().dependsOn(docJar, sourcesJar)
		artifacts {
			archives(sourcesJar.archiveFile) { classifier = "sources" }
			archives(docJar.archiveFile) { classifier = "javadoc" }
		}
		
		withType<KotlinCompile> {
			kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
		}

		test {
			useJUnitPlatform()
		}
	}
	
}
