package xerus.ktutil.javafx

import javafx.event.ActionEvent
import javafx.scene.control.*

/** Creates a [MenuItem] with the specified [text] and [onAction] Listener. */
fun MenuItem(text: String, onAction: ((ActionEvent) -> Unit)) = MenuItem(text).apply { setOnAction(onAction) }

/** Creates a [CheckMenuItem] with the specified [text] and [onAction] Listener.
 * @param selected whether the CheckMenuItem should be selected upon initialization. Defaults to false. */
fun CheckMenuItem(text: String, onAction: (Boolean) -> Unit, selected: Boolean = false) = CheckMenuItem(text).apply {
	isSelected = selected
	setOnAction { onAction.invoke(isSelected) }
}

/** Adds a [MenuItem] to this [Control], creating a [ContextMenu] if necessary. */
fun Control.addMenuItem(item: MenuItem) {
	if (contextMenu == null)
		contextMenu = ContextMenu()
	contextMenu.items.add(item)
}