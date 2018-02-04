package xerus.ktutil.javafx

import javafx.scene.Scene
import xerus.ktutil.XerusLogger
import xerus.ktutil.getResource
import java.net.URL

/** either finds a skin of the standard skins or searches an external file for that
 * @param "dark", "silver" or "white" or a valid path to a stylesheet
 * @return applied Skin file as String */
fun Scene.applySkin(name: String): String {
	val url = Skins.get(name)
	stylesheets.clear()
	arrayOf(url, getResource("css/default.css"), getResource("css/style.css"))
			.forEach { it?.toExternalForm()?.let { stylesheets.add(it) } }
	XerusLogger.fine("Applied $name skin")
	//url.openStream()
	return url.toExternalForm()
}

object Skins {
	val availableSkins = arrayOf("white", "beige", "silver", "chocolate", "swamp", "black").map { it.capitalize() }.toTypedArray()
	
	fun get(name: String): URL = getResource("skins/${name.toLowerCase()}.css") ?: URL("file", null, name)
	
	//var fxbase = Color.hsb(0.0, 0.0, 0.93)
}