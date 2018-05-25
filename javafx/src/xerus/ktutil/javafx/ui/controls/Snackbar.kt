package xerus.ktutil.javafx.ui.controls

import javafx.beans.property.StringProperty
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.Labeled

class Snackbar(fromTop: Boolean = false) : FadingHBox(false, if (fromTop) -1.0 else 1.0, 25) {
	
	val child: Node
		get() = children[1]
	
	val text: StringProperty
		get() {
			return if (child is Labeled) {
				(child as Labeled).textProperty()
			} else {
				throw IllegalStateException("The currently displayed element $child is not Labeled!")
			}
		}
	
	init {
		setChildren(Label())
	}
	
	fun hide() = fadeOut()
	
	fun show(node: Control) {
		show {
			setChildren(node)
			if (node is Labeled)
				node.alignment = Pos.CENTER
		}
	}
	
	fun showText(text: String, reopen: Boolean = false) {
		if (reopen || child !is Label)
			show(Label(text))
		else {
			this.text.set(text)
			ensureVisible()
		}
	}
	
}
