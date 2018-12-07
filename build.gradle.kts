import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	java
	maven
	kotlin("jvm") version "1.3.11"
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
	
	tasks {
		val sourcesJar by creating(Jar::class) {
			classifier = "sources"
			from(sourceSets.getByName("main").allSource)
		}
		val javadocJar by creating(Jar::class) {
			classifier = "javadoc"
			from(getByName<Javadoc>("javadoc").destinationDir)
		}
		getByName("install").dependsOn("javadocJar", "sourcesJar")
		artifacts {
			add("archives", sourcesJar.outputs.files.first()) { classifier = "sources" }
			add("archives", javadocJar.outputs.files.first()) { classifier = "javadoc" }
		}
		
		withType<KotlinCompile> {
			kotlinOptions.jvmTarget = "1.8"
		}
	}
	
}