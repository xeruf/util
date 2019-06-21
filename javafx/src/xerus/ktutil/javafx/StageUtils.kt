package xerus.ktutil.javafx

import javafx.beans.binding.Bindings
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.Window
import xerus.ktutil.javafx.properties.addOneTimeListener

/** Creates a new Stage from this Window given the [title] and [content], that has this [Window] as its owner */
fun Window.createStage(title: String, content: Parent) =
	Stage().also {
		it.title = title
		it.scene = Scene(content)
		it.initWindowOwner(this)
	}

/** Sets the [other] [Window] as owner of this Stage and adjusts the position.
 * Also binds Stylesheets if [other] is a Stage. */
fun Stage.initWindowOwner(other: Window) {
	initOwner(other)
	setPositionRelativeTo(other)
	if(other is Stage)
		bindStylesheets(other)
}

/** Use some hacks to adjust the Position of this [Stage] to the position of the [Window].
 * Only works if called before showing this Stage. */
fun Stage.setPositionRelativeTo(other: Window) {
	var disabled = false
	setOnShowing { if(!disabled) opacity = 0.0 }
	setOnShown {
		if(disabled) return@setOnShown
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

/** Bins the Stylesheets of this Stage to the ones of [other].
 * If the Scene is not yet initialized, it waits until that happens.
 * After calling this function, the stylesheets of this object must not be modified directly anymore. */
fun Stage.bindStylesheets(other: Stage) {
	when {
		other.scene == null -> other.sceneProperty().addOneTimeListener { bindStylesheets(other) }
		scene == null -> sceneProperty().addOneTimeListener { bindStylesheets(other) }
		else -> {
			Bindings.bindContent(scene.stylesheets, other.scene.stylesheets)
		}
	}
}