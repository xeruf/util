package xerus.ktutil.javafx.ui.controls

import javafx.scene.control.TextArea
import xerus.ktutil.javafx.checkFx
import xerus.ktutil.ui.Loggable

class LogTextArea : TextArea(), Loggable {
	
	init {
		isEditable = false
		isWrapText = true
	}
	
	override fun appendText(text: String) =
			checkFx { super.appendText(text) }
	
}
