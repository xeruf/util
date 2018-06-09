package xerus.ktutil.javafx

import javafx.concurrent.Task
import javafx.event.ActionEvent
import javafx.geometry.HPos
import javafx.scene.control.Button
import javafx.scene.control.TableColumn
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import xerus.ktutil.javafx.properties.ConstantObservable

fun createButton(text: String, handler: (ActionEvent) -> Unit) = Button(text).apply { setOnAction { handler(it) } }

fun ColumnConstraints(minWidth: Double? = null, prefWidth: Double? = null, maxWidth: Double? = null, hgrow: Priority? = null, halignment: HPos? = null, fillWidth: Boolean = true): ColumnConstraints =
        ColumnConstraints().apply {
            if (minWidth != null) setMinWidth(minWidth)
            if (prefWidth != null) setPrefWidth(prefWidth)
            if (maxWidth != null) setMaxWidth(maxWidth)
            isFillWidth = fillWidth
            setHgrow(hgrow)
            setHalignment(halignment)
        }

fun gridPane(hgap: Double = 3.0, vgap: Double = 3.0) = GridPane().apply {
    this.hgap = hgap
    this.vgap = vgap
}

fun <T, U> TableColumn(title: String, function: (TableColumn.CellDataFeatures<T, U>) -> U): TableColumn<T, U> =
        TableColumn<T, U>(title).apply { setCellValueFactory { ConstantObservable(function(it)) } }

class SimpleTask(title: String = "", message: String = "", autostart: Boolean = true, private val runnable: suspend SimpleTask.() -> Unit) : Task<Unit>() {

    init {
        updateTitle(title)
        updateMessage(message)
        if (autostart)
            launch {
                run()
            }
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

}