package xerus.ktutil.javafx

import javafx.event.ActionEvent
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.*

/** Adds the Node to the children of this container and returns it.*/
fun <T : Node> Pane.add(node: T) = node.also { children.add(node) }

/** Creates a [Button] with the specified [text] and [onAction] handler, adds it to this [Pane] and returns it.
 * @return The created Button. */
fun Pane.addButton(text: String = "", onAction: (ActionEvent) -> Unit) = add(createButton(text, onAction))

/** Sets the [GridPane.hgap] and [GridPane.vgap] for this [GridPane]*/
fun GridPane.spacing(spacing: Int = 3) = also { it.hgap = spacing.toDouble(); it.vgap = spacing.toDouble() }

/** Sets the Priority of this Node for growing in an HBox, VBox and GridPane to the given value.
 * @param priority the desired [Priority]. Defaults to [Priority.ALWAYS]. */
fun Node.grow(priority: Priority = Priority.ALWAYS) = also {
	HBox.setHgrow(it, priority)
	VBox.setVgrow(it, priority)
	GridPane.setVgrow(it, priority)
	GridPane.setHgrow(it, priority)
}

/** Adds a [Region], by default an empty one, to this [HBox] and enables it to grow as much as possible */
fun HBox.fill(node: Region = Region(), pos: Int = children.size) {
	children.add(pos, node)
	node.maxWidth = Double.MAX_VALUE
	HBox.setHgrow(node, Priority.ALWAYS)
}

/** Adds a [Region], by default an empty one, to this [VBox] and enables it to grow as much as possible */
fun VBox.fill(node: Region = Region(), pos: Int = children.size) {
	children.add(pos, node)
	node.maxHeight = Double.MAX_VALUE
	VBox.setVgrow(node, Priority.ALWAYS)
}

/** Adds the [node] preceded by a Label with the given [text] as a [HBox] to this [VBox] */
fun VBox.addLabeled(text: String, node: Node) = addRow(Label(text), node)

/** Adds the given [nodes] in an [HBox] to this [VBox]*/
fun VBox.addRow(vararg nodes: Node) = add(HBox(5.0, *nodes))
