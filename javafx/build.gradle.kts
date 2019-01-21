import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

dependencies {
	compile(project(":kotlin"))

	val junitVersion = "5.3.2"
	testImplementation("org.junit.jupiter", "junit-jupiter-api", junitVersion)
	testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", junitVersion)
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