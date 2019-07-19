import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	java
	maven
	kotlin("jvm") version "1.3.41"
	id("org.jetbrains.dokka") version "0.9.18"
	id("com.github.ben-manes.versions") version "0.21.0"
}

allprojects {
	repositories {
		jcenter()
	}
	group = "xerus.util"
}

subprojects {
	apply(plugin = "java")
	apply(plugin = "kotlin")
	apply(plugin = "maven")
	apply(plugin = "org.jetbrains.dokka")
	
	dependencies {
		val junitVersion = "5.5.0"
		testImplementation("org.junit.jupiter", "junit-jupiter-api", junitVersion)
		testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", junitVersion)
		testImplementation("io.kotlintest", "kotlintest-runner-junit5", "3.3.3")
		testImplementation("org.slf4j", "slf4j-simple", "1.7.26")
	}
	
	sourceSets {
		main {
			java.srcDir("src")
			resources.srcDir("resources")
		}
		test {
			java.srcDir("test")
		}
	}
	
	tasks {
		
		dokka {
			outputFormat = "javadoc"
			outputDirectory = "$buildDir/doc"
		}
		
		val docJar by creating(Jar::class) {
			dependsOn(dokka.get())
			archiveClassifier.set("javadoc")
			from(dokka.get().outputDirectory)
		}
		
		val sourcesJar by creating(Jar::class) {
			archiveClassifier.set("sources")
			from(sourceSets.main.get().allSource)
		}
		
		install.get().dependsOn(docJar, sourcesJar)
		artifacts {
			archives(sourcesJar.archiveFile) { classifier = "sources" }
			archives(docJar.archiveFile) { classifier = "javadoc" }
		}
		
		withType<KotlinCompile> {
			kotlinOptions.jvmTarget = "1.8"
		}
		
		test {
			useJUnitPlatform()
		}
	}
	
}