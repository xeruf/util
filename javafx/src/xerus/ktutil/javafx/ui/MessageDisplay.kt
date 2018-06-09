package xerus.ktutil.javafx.ui

import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonType
import javafx.stage.Stage
import javafx.stage.Window
import xerus.ktutil.javafx.bindStylesheets
import xerus.ktutil.javafx.checkJFX
import xerus.ktutil.javafx.setPositionRelativeTo

interface MessageDisplay {
	
	fun showError(error: Throwable, title: String = "Error") {
		showMessage(error.toString(), title, AlertType.ERROR)
		error.printStackTrace()
	}
	
	fun showMessage(message: Any, title: String? = null, type: AlertType = AlertType.INFORMATION)
	
}

interface JFXMessageDisplay : MessageDisplay {
	
	val window: Window
	
	override fun showMessage(message: Any, title: String?, type: AlertType) {
		checkJFX {
			showAlert(type, title = title, content = message.toString())
		}
	}
	
	fun showAlert(type: Alert.AlertType, title: String?, header: String? = null, content: String, vararg buttons: ButtonType) =
			window.createAlert(type, title, header, content, *buttons).apply { show() }
	
}

fun Window?.createAlert(type: Alert.AlertType, title: String? = null, header: String? = null, content: String, vararg buttons: ButtonType) =
		Alert(type, content, *buttons).also {
			it.title = title
			it.headerText = header
			if (this != null)
				it.initWindowOwner(this)
		}

val Alert.stage
	get() = dialogPane.scene.window as Stage

fun Alert.initWindowOwner(window: Window) {
	initOwner(window)
	stage.setPositionRelativeTo(window)
	if (window is Stage)
		stage.bindStylesheets(window)
}

