package xerus.ktutil.javafx.ui

import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import xerus.ktutil.javafx.add
import xerus.ktutil.javafx.format
import xerus.ktutil.ui.Loggable

class LogTextFlow : TextFlow(), Loggable {
	
	init {
		style = "-fx-background-color: -fx-control-inner-background"
	}
	
	override fun appendText(text: String) {
		add(Text(text))
	}
	
	override fun appendln(text: String) {
		appendText(text + "\n")
	}
	
	fun appendFormatted(text: String, bold: Boolean = false, italic: Boolean = false) {
		add(Text(text).format(bold, italic))
	}
	
}