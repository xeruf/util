package xerus.ktutil.javafx

import javafx.beans.value.ObservableValue
import javafx.concurrent.Task
import javafx.event.ActionEvent
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import kotlinx.coroutines.runBlocking
import xerus.ktutil.javafx.properties.ImmutableObservable
import xerus.ktutil.javafx.properties.dependOn

fun createButton(text: String, handler: (ActionEvent) -> Unit) = Button(text).apply { setOnAction(handler) }

fun buttonWithId(id: String, handler: (ActionEvent) -> Unit) = Button().apply {
	setId(id)
	setOnAction(handler)
}

fun <T> Label(property: ObservableValue<T>, converter: (T) -> String = { it.toString() }) = Label().apply {
	textProperty().dependOn(property, converter)
}

fun ColumnConstraints(minWidth: Double? = null, prefWidth: Double? = null, maxWidth: Double? = null, hgrow: Priority? = null, halignment: HPos? = null, fillWidth: Boolean = true): ColumnConstraints =
		ColumnConstraints().apply {
			if (minWidth != null) setMinWidth(minWidth)
			if (prefWidth != null) setPrefWidth(prefWidth)
			if (maxWidth != null) setMaxWidth(maxWidth)
			isFillWidth = fillWidth
			setHgrow(hgrow)
			setHalignment(halignment)
		}

fun gridPane(hgap: Double = 3.0, vgap: Double = 3.0, padding: Int = 0) = GridPane().apply {
	this.hgap = hgap
	this.vgap = vgap
	if (padding != 0)
		this.padding = Insets(padding.toDouble())
}

fun <T, U> TableColumn(title: String, function: (TableColumn.CellDataFeatures<T, U>) -> U) =
		TableColumn<T, U>(title).apply { setCellValueFactory { ImmutableObservable(function(it)) } }

fun <T, U> TreeTableColumn(title: String, function: (TreeTableColumn.CellDataFeatures<T, U>) -> U) =
		TreeTableColumn<T, U>(title).apply { setCellValueFactory { ImmutableObservable(function(it)) } }

open class SimpleTask(title: String = "", message: String = "", autostart: Boolean = true, private val runnable: suspend SimpleTask.() -> Unit) : Task<Unit>() {
	
	init {
		this.updateTitle(title)
		this.updateMessage(message)
		if (autostart)
			launch()
	}
	
	override fun call() {
		val t = this
		runBlocking { runnable(t) }
	}
	
	fun updateProgress(workDone: Int, max: Int) =
			super.updateProgress(workDone.toLong(), max.toLong())
	
	public override fun updateProgress(workDone: Long, max: Long) =
			super.updateProgress(workDone, max)
	
	public override fun updateProgress(workDone: Double, max: Double) =
			super.updateProgress(workDone, max)
	
	public override fun updateMessage(message: String?) =
			super.updateMessage(message)
	
	override fun toString() = "Simpletask(title='$title', message='$message', progress=$progress)"
	
}
