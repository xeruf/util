package xerus.ktutil.javafx

import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.property.Property
import javafx.concurrent.Task
import javafx.event.ActionEvent
import javafx.event.EventDispatcher
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.*
import javafx.stage.Stage
import javafx.stage.Window
import javafx.stage.WindowEvent
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import xerus.ktutil.javafx.properties.addListener
import xerus.ktutil.javafx.properties.addOneTimeListener
import xerus.ktutil.printNamed
import xerus.ktutil.printWith

inline fun checkJFX(crossinline run: () -> Unit) {
	if (Platform.isFxApplicationThread())
		run()
	else
		Platform.runLater { run() }
}

inline fun onJFX(crossinline run: () -> Unit) = Platform.runLater { run() }

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

fun <T : Control> T.tooltip(string: String) = apply { tooltip = Tooltip(string) }

fun <T : Labeled> T.text(text: String) = also { it.text = text }
fun <T : Labeled> T.centerText() = apply { textAlignment = TextAlignment.CENTER }
fun <T : Labeled> T.textWidth(text: String? = this.text) = text.textWidth(font)

inline fun <T : ButtonBase> T.onClick(crossinline runnable: T.() -> Unit) = apply {
	setOnAction { runnable(this) }
}

fun <T : TextInputControl> T.bindText(property: Property<String>) = also { it.textProperty().bindBidirectional(property) }
fun CheckBox.bind(property: Property<Boolean>) = apply { selectedProperty().bindBidirectional(property) }

fun <T> ComboBox<T>.select(item: T) = apply { selectionModel.select(item) }

fun <T : Node> Pane.add(node: T) = node.also { children.add(node) }
fun Pane.addButton(text: String = "", handler: (ActionEvent) -> Unit) = createButton(text, handler).also { children.add(it) }

fun GridPane.spacing(spacing: Int = 3) = also { it.hgap = spacing.toDouble(); it.vgap = spacing.toDouble() }

fun Node.grow(priority: Priority = Priority.ALWAYS) = also {
	HBox.setHgrow(it, priority)
	VBox.setVgrow(it, priority)
	GridPane.setVgrow(it, priority)
	GridPane.setHgrow(it, priority)
}

fun HBox.fill(node: Region = Region(), pos: Int = children.size) {
	children.add(pos, node)
	node.maxWidth = Double.MAX_VALUE
	HBox.setHgrow(node, Priority.ALWAYS)
}

fun VBox.fill(node: Region = Region(), pos: Int = children.size) {
	children.add(pos, node)
	node.maxHeight = Double.MAX_VALUE
	VBox.setVgrow(node, Priority.ALWAYS)
}

fun VBox.addLabeled(text: String, node: Node) = addRow(Label(text), node)
fun VBox.addRow(vararg nodes: Node) = add(HBox(5.0, *nodes))

fun Font.format(bold: Boolean = false, italic: Boolean = false): Font =
		Font.font(family, if (bold) FontWeight.BOLD else FontWeight.NORMAL, if (italic) FontPosture.ITALIC else FontPosture.REGULAR, size)

fun Font.italic() = format(italic = true)
fun Font.bold() = format(bold = true)

fun Text.format(bold: Boolean = false, italic: Boolean = false) = apply {
	style = arrayOf("weight: bold".takeIf { bold }, "style: italic".takeIf { italic }).filterNotNull().joinToString(separator = ";", prefix = "-fx-font-")
}

fun <T, U> TableView<T>.addColumn(title: String, function: (T) -> U) {
	columns.add(TableColumn<T, U>(title, { function(it.value) }))
}

fun hexToColor(hex: String) =
		Color.rgb(
				Integer.parseInt(hex.substring(1, 3), 16),
				Integer.parseInt(hex.substring(3, 5), 16),
				Integer.parseInt(hex.substring(5, 7), 16)
		)

fun <T> Task<T>.launch() =
		kotlinx.coroutines.experimental.launch { run() }

fun String?.textWidth(font: Font) =
		Text(this).let {
			it.font = font
			it.prefWidth(-1.0)
		}

// DEBUG

fun Scene.printSize() = printWith { it.height.toString() + " " + it.width }
fun Region.printSize() = printWith { it.height.toString() + " " + it.width }

fun TextField.placeholder(prompt: String) = apply { promptText = prompt }
