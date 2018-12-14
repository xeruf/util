package xerus.ktutil.javafx.ui

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.Window
import xerus.ktutil.findFolder
import xerus.ktutil.javafx.createButton
import xerus.ktutil.javafx.properties.dependOn
import xerus.ktutil.javafx.properties.listen
import xerus.ktutil.javafx.properties.setWithoutListener
import java.io.File
import java.nio.file.Path

/** @param extension the extension for the filter
 * - when null, then it will only allow directories
 * - when it is empty, any file will be allowed */
class FileChooser(private val window: Window, val selectedFile: ObjectProperty<File>, private val extension: String?, private val name: String) {
	
	constructor(window: Window, initialDir: File, extension: String?, name: String) :
		this(window, SimpleObjectProperty<File>(initialDir), extension, name)
	
	val file: File
		get() = selectedFile.get()
	
	val path: Path
		get() = file.toPath()
	
	fun button() = createButton("Select $name") { showFileChooser() }
	
	fun textField() = TextField().apply {
		val listener = textProperty().dependOn(selectedFile) { it.toString() }
		textProperty().listen {
			val newFile = File(it)
			if(selectedFile.get() != newFile)
				selectedFile.setWithoutListener(newFile, listener)
		}
		maxWidth = Double.MAX_VALUE
		HBox.setHgrow(this, Priority.ALWAYS)
	}
	
	fun createHBox() = HBox(5.0, textField(), button())
	
	var title = "Select $name"
	fun showFileChooser() {
		val file = if(extension == null) {
			val chooser = DirectoryChooser()
			chooser.initialDirectory = selectedFile.get().findFolder()
			chooser.title = title
			chooser.showDialog(window)
		} else {
			val chooser = FileChooser()
			chooser.initialDirectory = selectedFile.get().findFolder()
			if(extension.isNotEmpty())
				chooser.extensionFilters.add(FileChooser.ExtensionFilter(extension.toUpperCase(), "*.$extension"))
			chooser.title = title
			chooser.showOpenDialog(window)
		}
		if(file != null)
			selectedFile.set(file)
	}
	
}
