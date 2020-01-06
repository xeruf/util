package xerus.ktutil.javafx

import javafx.scene.Scene
import xerus.ktutil.SystemUtils
import xerus.ktutil.getResource
import xerus.ktutil.helpers.Named
import java.io.File

/** @return a String that, interpreted as CSS, will style the root node (and thus the whole application) in the given HSB color. */
fun rootHsbStyling(hue: Double, saturation: Double, brightness: Double) =
	".root { -fx-base: hsb($hue, ${saturation * 100}%, ${brightness * 100}%); }"

/** Clears the stylesheets of this [Scene] and applies the stylesheet from [file] together with the default stylesheets. */
fun Scene.applyStyles(file: File? = null): Scene {
	stylesheets.clear()
	arrayOf(file?.toURI()?.toURL(), getResource("css/default.css"), getResource("css/style.css"))
		.forEach { it?.toExternalForm()?.let { stylesheets.add(it) } }
	return this
}

/** Applies the given [theme]. Default: BEIGE */
fun Scene.applyTheme(theme: Theme = Themes.BEIGE): Scene {
	val file = SystemUtils.cacheDir.resolve("xerus-themes").resolve(theme.displayName + ".css")
	if(!file.exists()) {
		file.parentFile.mkdirs()
		file.writeText(theme.styling)
	}
	return applyStyles(file)
}

/** Finds a Theme called [theme] in [Themes] and applies it to [this] [Scene]. */
fun Scene.applyTheme(theme: String) =
	applyTheme(Themes.valueOf(theme.toUpperCase()))

/** A Theme is used to style the application.
 * The [styling] is to be used directly, as such it has to be valid CSS.
 * The [displayName] is used to identify this Theme to the system and the user. */
interface Theme : Named {
	val styling: String
}

/** Preset application themes based on HSB values. */
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

/** A Simple Theme with name and styling. */
open class SimpleTheme(override val displayName: String, override val styling: String) : Theme

/** A Theme that only defines the root color of the application. */
open class ColorTheme(displayName: String, hue: Double, saturation: Double, brightness: Double) : SimpleTheme(displayName, rootHsbStyling(hue, saturation, brightness))

