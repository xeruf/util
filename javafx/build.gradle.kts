import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

dependencies {
	compile(project(":kotlin"))
	
	testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.3.2")
	testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.3.2")
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
	withType<Test> {
		useJUnitPlatform()
	}
}