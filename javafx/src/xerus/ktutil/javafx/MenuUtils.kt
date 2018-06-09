package xerus.ktutil.javafx

import javafx.event.ActionEvent
import javafx.scene.control.*

fun MenuItem(text: String, onAction: ((ActionEvent) -> Unit)) = MenuItem(text).apply { setOnAction(onAction) }
fun CheckMenuItem(text: String, onAction: (Boolean) -> Unit, selected: Boolean = false) = CheckMenuItem(text).apply {
    isSelected = selected
    setOnAction { onAction.invoke(isSelected) }
}

fun Control.addMenuItem(item: MenuItem) {
    if(contextMenu == null)
        contextMenu = ContextMenu()
    contextMenu.items.add(item)
}