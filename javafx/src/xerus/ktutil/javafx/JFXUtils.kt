package xerus.ktutil.javafx

import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.property.Property
import javafx.concurrent.Task
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.*
import javafx.stage.Stage
import javafx.stage.Window
import xerus.ktutil.javafx.properties.addOneTimeListener
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

fun Region.setSize(width: Double? = null, height: Double? = null) {
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
inline fun <T : ButtonBase> T.onClick(crossinline runnable: T.() -> Unit) = apply {
	setOnAction { runnable(this) }
}

fun <T : TextInputControl> T.bindText(property: Property<String>) = also { it.textProperty().bindBidirectional(property) }
fun CheckBox.bind(property: Property<Boolean>) = apply { selectedProperty().bindBidirectional(property) }

fun <T> ComboBox<T>.select(item: T) = apply { selectionModel.select(item) }

fun <T : Node> Pane.add(node: T) = node.also { children.add(node) }
fun Pane.addButton(text: String = "", handler: (ActionEvent) -> Unit) = createButton(text, handler).also { children.add(it) }

fun GridPane.spacing(spacing: Int = 3) = also { it.hgap = spacing.toDouble(); it.vgap = spacing.toDouble() }

fun Node.priority(priority: Priority = Priority.ALWAYS) = also {
	HBox.setHgrow(it, priority)
	VBox.setVgrow(it, priority)
	GridPane.setVgrow(it, priority)
	GridPane.setHgrow(it, priority)
}

fun HBox.fill(node: Region = Region(), pos: Int = children.size) {
	children.add(pos, node)
	HBox.setHgrow(node, Priority.ALWAYS)
}

fun VBox.fill(node: Node = Region(), pos: Int = children.size) {
	children.add(pos, node)
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

fun Stage.initWindowOwner(other: Window) {
	initOwner(other)
	setPositionRelativeTo(other)
	if (other is Stage)
		bindStylesheets(other)
}

fun Stage.setPositionRelativeTo(other: Window) {
	setOnShowing { hide() }
	setOnShown {
		x = other.x + other.width / 2 - width / 2
		y = other.y + other.height / 2 - height / 2
		show()
	}
}

fun Stage.bindStylesheets(other: Stage) {
	when {
		other.scene == null -> other.sceneProperty().addOneTimeListener { bindStylesheets(other) }
		scene == null -> sceneProperty().addOneTimeListener { bindStylesheets(other) }
		else -> {
			Bindings.bindContent(scene.stylesheets, other.scene.stylesheets)
		}
	}
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

fun Labeled.textWidth(text: String? = this.text) = text.textWidth(font)

fun String?.textWidth(font: Font) =
		Text(this).let {
			it.font = font
			it.prefWidth(-1.0)
		}

// DEBUG

fun Scene.printSize() = printWith { it.height.toString() + " " + it.width }
fun Region.printSize() = printWith { it.height.toString() + " " + it.width }

fun TextField.placeholder(prompt: String) = apply { promptText = prompt }