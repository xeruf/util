import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

dependencies {
	compile(project(":kotlin"))
}

sourceSets {
	getByName("main") {
		java.srcDirs("src")
		resources.srcDirs("resources")
	}
}
