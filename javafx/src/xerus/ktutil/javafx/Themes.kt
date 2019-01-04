package xerus.ktutil.javafx

import javafx.scene.Scene
import xerus.ktutil.SystemUtils
import xerus.ktutil.getResource
import xerus.ktutil.helpers.Named
import java.io.File
import java.net.URL

fun rootHsbStyling(hue: Double, saturation: Double, brightness: Double) =
	".root { -fx-base: hsb($hue, ${saturation * 100}%, ${brightness * 100}%); }"

/** Clears the Stylesheets and applies the Stylesheet from [file] together with the default stylesheets. */
fun Scene.applySkin(file: File? = null): Scene {
	stylesheets.clear()
	arrayOf(file?.let { URL("file", null, it.absolutePath) }, getResource("css/default.css"), getResource("css/style.css"))
		.forEach { it?.toExternalForm()?.let { stylesheets.add(it) } }
	return this
}

/** Applies the given [theme]. */
fun Scene.applyTheme(theme: Theme = Themes.BEIGE): Scene {
	val file = SystemUtils.tempDir.resolve("xerus-themes").resolve(theme.displayName)
	if(!file.exists()) {
		file.parentFile.mkdirs()
		file.writeText(theme.styling)
	}
	return applySkin(file)
}

/** Finds a theme in [Themes] with the name [theme], upper-cased. */
fun Scene.applyTheme(theme: String) =
	applyTheme(Themes.valueOf(theme.toUpperCase()))

enum class Themes(hue: Double,  saturation: Double, brightness: Double) : Theme {
	WHITE(0.0, 0.0, 0.95),
	SILVER(0.0, 0.00, 0.75),
	BEIGE(60.0, 0.10, 0.92),
	CHOCOLATE(25.0, 0.80, 0.20),
	SWAMP(70.0, 0.30, 0.10),
	BLACK(240.0, 0.10, 0.05);
	
	override val displayName: String
		get() = name.toLowerCase().capitalize()
	
	override val styling = rootHsbStyling(hue, saturation, brightness)
}

open class ColorTheme(displayName: String, hue: Double, saturation: Double, brightness: Double) : SimpleTheme(displayName, rootHsbStyling(hue, saturation, brightness))

open class SimpleTheme(override val displayName: String, override val styling: String) : Theme

interface Theme : Named {
	val styling: String
}