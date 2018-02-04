package xerus.ktutil.javafx.ui

import javafx.beans.binding.Bindings
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.stage.Stage
import javafx.stage.Window
import xerus.ktutil.javafx.bindStylesheets
import xerus.ktutil.javafx.initWindowOwner
import xerus.ktutil.javafx.setPositionRelativeTo

class Version(val version: String, val title: String, vararg minorChanges: String) {
	internal val changes = ArrayList<Change>(minorChanges.map { Change(it) })
	internal val patches = ArrayList<List<String>>()
	
	fun change(main: String, vararg subChanges: String): Version {
		changes.add(Change(main, *subChanges))
		return this
	}
	
	fun patch(vararg changes: String): Version {
		patches.add(changes.toList())
		return this
	}
	
	override fun toString() = "%s - %s".format(version, title)
	
}

internal class Change(private val main: String, private vararg val subChanges: String) {
	override fun toString(): String {
		return " - " + arrayOf(main, *subChanges).joinToString(separator = System.lineSeparator() + "      - ")
	}
}

class Changelog(private vararg val notes: String) {
	
	private val versions = ArrayList<Version>()
	
	fun version(major: Int, minor: Int, title: String, vararg minorChanges: String) = Version("%s.%s".format(major, minor), title, *minorChanges).also { versions.add(it) }
	
	fun show(parent: Window) {
		val text = LogTextFlow()
		appendLog(text)
		
		val stage = Stage()
		val scroll = ScrollPane(text)
		scroll.isFitToWidth = true
		val scene = Scene(scroll)
		stage.scene = scene
		stage.title = "Changelog"
		stage.icons.add(Image(javaClass.getResourceAsStream("/paper.png")))
		stage.height = 400.0
		stage.width = 500.0
		stage.initWindowOwner(parent)
		stage.show()
	}
	
	private fun appendLog(text: LogTextFlow) {
		with(text) {
			if (notes.isNotEmpty()) {
				appendAll(strings = *notes)
				appendln()
			}
			for (version in versions) {
				for (patch in version.patches.withIndex().reversed()) {
					appendln(version.version + "." + (patch.index + 1))
					patch.value.forEach { appendln(" - " + it) }
					appendln()
				}
				
				appendFormatted(version.toString() + "\n", true)
				for (change in version.changes)
					appendln(change.toString())
				appendln()
			}
		}
	}
	
}

