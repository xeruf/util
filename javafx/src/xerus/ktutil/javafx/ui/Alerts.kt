package xerus.ktutil.javafx.ui

import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonType
import javafx.scene.layout.Region
import javafx.stage.Stage
import javafx.stage.Window
import xerus.ktutil.javafx.bindStylesheets
import xerus.ktutil.javafx.checkFx
import xerus.ktutil.javafx.properties.listen
import xerus.ktutil.javafx.setPositionRelativeTo

interface MessageDisplay {
	
	fun showError(error: Throwable, title: String = "Error") {
		showMessage(error.toString(), title, AlertType.ERROR)
		error.printStackTrace()
	}
	
	fun showMessage(message: Any, title: String? = null, type: AlertType = AlertType.INFORMATION)
	
}

interface JFXMessageDisplay: MessageDisplay {
	
	val window: Window
	
	override fun showMessage(message: Any, title: String?, type: AlertType) {
		checkFx {
			showAlert(type, title = title, content = message.toString())
		}
	}
	
	fun showAlert(type: AlertType, title: String? = null, header: String? = null, content: String, vararg buttons: ButtonType) =
		window.createAlert(type, title, header, content, *buttons).apply { show() }
	
}

fun Window?.createAlert(type: AlertType, title: String? = null, header: String? = null, content: String, vararg buttons: ButtonType) =
	Alert(type, content, *buttons).also {
		it.title = title
		it.headerText = header
		if(this != null)
			it.initWindowOwner(this)
	}

val Alert.stage
	get() = dialogPane.scene.window as Stage

/** Inits the owner of this Alert to the given [window], which will additionally set up the Position and Stylesheets. */
fun Alert.initWindowOwner(window: Window) {
	initOwner(window)
	stage.setPositionRelativeTo(window)
	if(window is Stage)
		stage.bindStylesheets(window)
}

/** Tells the internal DialogPane to resize, either to its preferred size or given values. */
fun Alert.resize(width: Double? = null, height: Double? = null) {
	dialogPane.apply {
		minWidth = width ?: Region.USE_PREF_SIZE
		minHeight = height ?: Region.USE_PREF_SIZE
	}
}

/** Shows this Alert and listens for its Result, calling the given [function] once its done. */
fun Alert.useResultAsync(function: (ButtonType) -> Unit) {
	show()
	resultProperty().listen(function)
}