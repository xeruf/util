import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

dependencies {
	compile(kotlin("stdlib"))
	compile("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "0.30.0")
}

sourceSets {
	getByName("main") {
		java.srcDirs("src")
		resources.srcDirs("resources")
	}
}