package xerus.ktutil.javafx

import javafx.beans.property.Property
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.text.TextAlignment
import xerus.ktutil.javafx.properties.listen

fun <T: Control> T.tooltip(string: String) = apply { tooltip = Tooltip(string) }

fun <T: Labeled> T.text(text: String) = also { it.text = text }
fun <T: Labeled> T.centerText() = apply { textAlignment = TextAlignment.CENTER }
fun <T: Labeled> T.textWidth(text: String? = this.text) = text.textWidth(font)

fun <T: TextInputControl> T.placeholder(prompt: String) = apply { promptText = prompt }
fun <T: TextInputControl> T.bindText(property: Property<String>) = also { it.textProperty().bindBidirectional(property) }

inline fun <T: ButtonBase> T.onClick(crossinline runnable: T.() -> Unit) = apply {
	setOnAction { runnable(this) }
}

/** Adjusts this Slider to react to scrolling.
 * @param step The amount to increase or decrease the value when scrolling.
 * 	By default will be calculated so there are 20 steps across the Slider */
fun Slider.scrollable(step: Double = (max - min) / 20) = this.apply {
	blockIncrement = step
	setOnScroll {
		if(it.touchCount > 0)
			return@setOnScroll
		if(it.deltaY > 0)
			increment()
		if(it.deltaY < 0)
			decrement()
	}
}


fun CheckBox.bind(property: Property<Boolean>) = apply { selectedProperty().bindBidirectional(property) }


fun <T> ComboBox<T>.select(item: T) = apply { selectionModel.select(item) }

/** Updates the Selection state of this [CheckBoxTreeItem] by flipping the isSelected state of the lowest first child twice */
fun CheckBoxTreeItem<*>.updateSelection() {
	(children.firstOrNull() as? CheckBoxTreeItem)?.updateSelection() ?: run {
		isSelected = !isSelected
		isSelected = !isSelected
	}
}

fun TreeView<*>.expandAll(expand: Boolean = true) =
	if(this.isShowRoot)
		root.expandRecursively(expand)
	else
		root.children.forEach { it.expandRecursively(expand) }

fun TreeItem<*>.expandRecursively(expand: Boolean = true) {
	isExpanded = expand
	children.forEach { it.expandRecursively(expand) }
}

fun <T, U> TableView<T>.addColumn(title: String, function: (T) -> U) {
	columns.add(TableColumn<T, U>(title) { function(it.value) })
}

/** Instantly calls the handler with the exception if [Image.isError] and attaches a Listener to the exceptionProperty
 * to call the handler on future exceptions. */
fun Image.onError(handler: (Exception) -> Unit) {
	if(isError)
		handler(exception)
	exceptionProperty().listen {
		if(it != null)
			handler(it)
	}
}