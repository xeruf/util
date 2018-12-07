package xerus.ktutil.javafx

import javafx.scene.Scene
import xerus.ktutil.SystemUtils
import xerus.ktutil.getResource
import java.io.File
import java.net.URL

/** Applies the Stylesheet at the given File and the default stylesheets */
fun Scene.applySkin(file: File? = null): Scene {
	stylesheets.clear()
	arrayOf(file?.let { URL("file", null, it.absolutePath) }, getResource("css/default.css"), getResource("css/style.css"))
		.forEach { it?.toExternalForm()?.let { stylesheets.add(it) } }
	return this
}

fun Scene.applyTheme(theme: Theme = Themes.BEIGE): Scene {
	val file = SystemUtils.tempDir.resolve("xerus-themes").resolve(theme.name)
	if (!file.exists())
		file.writeText(".root { -fx-base: hsb(${theme.hue}, ${theme.saturation * 100}%, ${theme.brightness * 100}%); }")
	return applySkin(file)
}

fun Scene.applyTheme(theme: String): Scene {
	return applyTheme(Themes.valueOf(theme.toUpperCase()))
}

enum class Themes(override val hue: Double, override val saturation: Double, override val brightness: Double) : Theme {
	WHITE(0.0, 0.0, 0.95),
	BEIGE(60.0, 0.10, 0.92),
	BLACK(240.0, 0.10, 0.5),
	CHOCOLATE(25.0, 0.80, 0.20),
	SILVER(0.0, 0.00, 0.75),
	SWAMP(70.0, 0.30, 0.10);
}

class SimpleTheme(val name: String, val hue: Double, val saturation: Double, val brightness: Double)

interface Theme {
	val name: String
	val hue: Double
	val saturation: Double
	val brightness: Double
}