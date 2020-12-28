package xerus.ktutil.javafx.ui.controls

import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.GridPane
import xerus.ktutil.javafx.onClick
import xerus.ktutil.javafx.styleClass

@Suppress("UNCHECKED_CAST")
open class ExpandableView<T>(
	val toNodes: (T) -> Array<Node> = {
		when(it) {
			is Node -> arrayOf(it)
			is Array<*> -> it as Array<Node>
			is Collection<*> -> (it as Collection<Node>).toTypedArray()
			else -> throw IllegalArgumentException("Type parameter is not automatically convertible to Node. " +
				"Please specify the toNodes Parameter")
		}
	},
	val rowCreator: () -> T) : GridPane() {
	
	val rows = FXCollections.observableArrayList<T>().apply {
		addListener(ListChangeListener {
			children.clear()
			forEachIndexed { index, row -> addRow(index, *(toNodes(row) + rowButtons(index))) }
		})
	}
	
	init {
		createRow(0)
	}
	
	fun createRow(index: Int = rows.size, row: T = rowCreator()) = rows.add(index, row)
	
	fun removeRow(index: Int) {
		rows.removeAt(index)
	}
	
	fun rowButtons(index: Int): Array<Button> {
		val plusButton = Button().styleClass("plus").onClick {
			createRow(index + 1)
		}
		val removeButton = Button().styleClass("x").onClick {
			removeRow(index)
		}
		return arrayOf(plusButton, removeButton)
	}
	
}
