package xerus.ktutil.javafx.ui

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.Window
import xerus.ktutil.findFolder
import xerus.ktutil.javafx.createButton
import java.io.File
import java.nio.file.Path

/** @param extension the extension for the filter
 * - when null, then it will only allow directories
 * - when it is empty, any file will be allowed */
class FileChooser(private val window: Window, initialDir: File, private val extension: String?, private val name: String) {
	
	val selectedFile = SimpleObjectProperty<File>(initialDir)
	
	val file: File
		get() = selectedFile.get()
	
	val path: Path
		get() = file.toPath()
	
	val button: Button by lazy {
		createButton("Select $name", { showFileChooser() })
	}
	
	val textField: TextField by lazy {
		TextField(selectedFile.get().toString()).apply {
			selectedFile.addListener { _, _, nv -> text = nv.toString() }
			focusedProperty().addListener { _, _, focus ->
				val newFile = File(text)
				if (!focus && selectedFile.get() != newFile)
					selectedFile.set(newFile)
			}
			maxWidth = Double.MAX_VALUE
			HBox.setHgrow(this, Priority.ALWAYS)
		}
	}
	
	fun createHBox() = HBox(5.0, textField, button)
	
	var title = "Select $name"
	fun showFileChooser() {
		val file = if (extension == null) {
			val chooser = DirectoryChooser()
			chooser.initialDirectory = findFolder(selectedFile.get())
			chooser.title = title
			chooser.showDialog(window)
		} else {
			val chooser = FileChooser()
			chooser.initialDirectory = findFolder(selectedFile.get())
			if (extension.isNotEmpty())
				chooser.extensionFilters.add(FileChooser.ExtensionFilter(extension.toUpperCase(), "*.$extension"))
			chooser.title = title
			chooser.showOpenDialog(window)
		}
		if (file != null)
			selectedFile.set(file)
	}
	
}