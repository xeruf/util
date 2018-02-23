package xerus.ktutil.javafx.ui.controls

import javafx.beans.property.DoubleProperty
import javafx.scene.control.ProgressBar
import javafx.scene.control.Slider
import javafx.scene.layout.StackPane
import xerus.ktutil.javafx.setSize

class SlideBar(width: Int) : StackPane() {

    private val progressBar = ProgressBar(0.0).apply {
        setSize(width = width.toDouble())
    }

    private val slider = Slider(0.0, 1.0, 0.0).apply {
        setSize(width = width.toDouble())
        valueProperty().addListener({ _, _, new ->
            progressBar.progress = new.toDouble() * 0.96 + 0.04
        })
    }

    val value: DoubleProperty = slider.valueProperty()

    init {
        styleClass.add("slide-bar")
        children.addAll(progressBar, slider)
    }

}