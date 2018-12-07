@file: Suppress("UNUSED")

package xerus.ktutil.javafx

import javafx.application.Platform
import javafx.concurrent.Task
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.TableView
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.text.*
import kotlinx.coroutines.*
import xerus.ktutil.printWith
import kotlin.coroutines.CoroutineContext

/** runs [action] if already on Fx Application Thread, otherwise schedules it via [Platform.runLater] */
inline fun checkFx(crossinline action: () -> Unit) {
	if (Platform.isFxApplicationThread())
		action()
	else
		Platform.runLater { action() }
}

/** runs [action] via [Platform.runLater] */
inline fun onFx(crossinline action: () -> Unit) = Platform.runLater { action() }

// NODES

fun <T : Node> T.styleClass(styleClass: String): T = this.apply { getStyleClass().add(styleClass) }
fun <T : Node> T.id(id: String): T = this.apply { setId(id) }

fun <T : Region> T.allowExpand(horizontal: Boolean = true, vertical: Boolean = true) = also {
	if (horizontal)
		it.maxWidth = Double.MAX_VALUE
	if (vertical)
		it.maxHeight = Double.MAX_VALUE
}

fun Region.setSize(width: Double? = null, height: Double? = null) = apply {
	if (width != null) {
		minWidth = width.toDouble()
		maxWidth = width.toDouble()
	}
	if (height != null) {
		minHeight = height.toDouble()
		maxHeight = height.toDouble()
	}
}

val <T> TableView<T>.selectedItem: T?
	get() = selectionModel.selectedItem

// TEXT

/** Returns a new Font with the same family and size but adjusted weight and posture */
fun Font.format(bold: Boolean = false, italic: Boolean = false): Font =
		Font.font(family, if (bold) FontWeight.BOLD else FontWeight.NORMAL, if (italic) FontPosture.ITALIC else FontPosture.REGULAR, size)

/** Returns a new Font with the same family and size but in italics */
fun Font.italic() = format(italic = true)

/** Returns a new Font with the same family and size but in bold */
fun Font.bold() = format(bold = true)

fun Text.format(bold: Boolean = false, italic: Boolean = false) = apply {
	style = arrayOf("weight: bold".takeIf { bold }, "style: italic".takeIf { italic }).filterNotNull().joinToString(separator = ";", prefix = "-fx-font-")
}

fun String?.textWidth(font: Font) =
		Text(this).let {
			it.font = font
			it.prefWidth(-1.0)
		}

// MISC

/** Takes a hex String like `#aabbcc` and returns the respective [Color] */
fun hexToColor(hex: String): Color =
		Color.rgb(
				Integer.parseInt(hex.substring(1, 3), 16),
				Integer.parseInt(hex.substring(3, 5), 16),
				Integer.parseInt(hex.substring(5, 7), 16)
		)

/** Launches this Task with the given [CoroutineContext], which defaults to [DefaultDispatcher] */
fun <T> Task<T>.launch(context: CoroutineContext = Dispatchers.Default): Job =
		GlobalScope.launch(context) { run() }

// DEBUG

fun Scene.printSize() = printWith { it.height.toString() + " " + it.width }
fun Region.printSize() = printWith { it.height.toString() + " " + it.width }
