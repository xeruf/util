package xerus.ktutil.javafx.ui

import javafx.scene.control.TextArea
import xerus.ktutil.javafx.checkJFX
import xerus.ktutil.ui.Loggable

class LogTextArea : TextArea(), Loggable {

    init {
        isEditable = false
        isWrapText = true
    }

    override fun appendText(text: String) =
            checkJFX { super.appendText(text) }

}
