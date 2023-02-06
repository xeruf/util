import kotlin.reflect.full.functions

apply(plugin = "org.openjfx.javafxplugin")

dependencies {
	api(project(":kotlin"))
}

val jfx = project.extensions.get("javafx")
jfx::class.functions.first { it.name.endsWith("modules") }.call(jfx, arrayOf("javafx.base", "javafx.controls"))