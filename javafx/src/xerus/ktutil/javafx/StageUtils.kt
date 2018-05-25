package xerus.ktutil.javafx

import javafx.beans.binding.Bindings
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.Window
import xerus.ktutil.javafx.properties.addOneTimeListener

fun Window.createStage(title: String, content: Parent) =
		Stage().also {
			it.title = title
			it.scene = Scene(content)
			it.initWindowOwner(this)
		}

fun Stage.initWindowOwner(other: Window) {
	initOwner(other)
	setPositionRelativeTo(other)
	if (other is Stage)
		bindStylesheets(other)
}

fun Stage.setPositionRelativeTo(other: Window) {
	var disabled = false
	setOnShowing { if (!disabled) opacity = 0.0 }
	setOnShown {
		if (disabled) return@setOnShown
		val newx = (other.x + other.width / 2 - width / 2).toInt().toDouble()
		val newy = (other.y + other.height / 2 - height / 2).toInt().toDouble()
		onFx {
			disabled = true
			hide()
			x = newx; y = newy
			opacity = 1.0
			show()
			disabled = false
		}
	}
}

fun Stage.bindStylesheets(other: Stage) {
	when {
		other.scene == null -> other.sceneProperty().addOneTimeListener { bindStylesheets(other) }
		scene == null -> sceneProperty().addOneTimeListener { bindStylesheets(other) }
		else -> {
			Bindings.bindContent(scene.stylesheets, other.scene.stylesheets)
		}
	}
}