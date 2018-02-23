package xerus.ktutil.javafx.ui.controls

import javafx.beans.property.DoubleProperty
import javafx.scene.control.ProgressBar
import javafx.scene.control.Slider
import javafx.scene.layout.StackPane
import xerus.ktutil.javafx.properties.bindSoft
import xerus.ktutil.javafx.setSize

class SlideBar(width: Int? = null) : StackPane() {

    val slider = Slider(0.0, 1.0, 0.0).apply {
        setSize(width = width?.toDouble())
    }

    private val progressBar = ProgressBar(0.0).apply {
        slider.widthProperty().addListener { _, _, new -> prefWidth = new.toDouble() }
    }

    val value: DoubleProperty = slider.valueProperty()

    init {
        styleClass.add("slide-bar")
        children.addAll(progressBar, slider)
        progressBar.progressProperty().bindSoft({
            val corrector = 10 / progressBar.width
            slider.value * (1 - corrector) + corrector
        }, slider.valueProperty(), progressBar.widthProperty())
    }

}