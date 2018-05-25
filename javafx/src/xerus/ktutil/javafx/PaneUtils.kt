package xerus.ktutil.javafx

import javafx.event.ActionEvent
import javafx.scene.Node
import javafx.scene.layout.*

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

fun VBox.addLabeled(text: String, node: Node) = addRow(javafx.scene.control.Label(text), node)
fun VBox.addRow(vararg nodes: Node) = add(HBox(5.0, *nodes))
