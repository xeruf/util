import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	maven
	kotlin("jvm") version "1.4.21"
	id("org.jetbrains.dokka") version "0.10.1"
 
	id("com.github.ben-manes.versions") version "0.36.0"
	id("se.patrikerdes.use-latest-versions") version "0.2.15"
}

allprojects {
	group = "xerus.util"
	repositories {
		jcenter()
	}
}

subprojects {
	apply(plugin = "kotlin")
	apply(plugin = "maven")
	apply(plugin = "org.jetbrains.dokka")
	
	dependencies {
		testImplementation("io.kotlintest", "kotlintest-runner-junit5", "3.4.2")
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
			kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
		}
		test {
			useJUnitPlatform()
		}
	}
	
}
