package xerus.ktutil.javafx.ui

import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import xerus.ktutil.ui.SimpleFrame
import java.io.OutputStream
import java.io.PrintWriter
import javax.swing.JTextArea
import javax.swing.WindowConstants

class App : Application() {
	
	override fun start(primaryStage: Stage) {
		stage = primaryStage
		stager.invoke(primaryStage)
		restart()
	}
	
	companion object {
		lateinit var stage: Stage
		
		private lateinit var stager: (Stage) -> Unit
		private lateinit var content: (() -> Scene)
		
		fun launch(title: String = "Test", stager: ((Stage) -> Unit)? = null, scene: () -> Scene) {
			this.content = scene
			this.stager = { it.title = title; stager?.invoke(stage) }
			Application.launch(App::class.java)
		}
		
		fun restart() {
			try {
				stage.hide()
				System.gc()
				stage.scene = content()
				stage.show()
			} catch (error: Throwable) {
				SimpleFrame {
					val field = JTextArea("A critical error occured. Please contact the developer, providing the information below!" + System.lineSeparator() + System.lineSeparator())
					field.append("Java version: " + System.getProperty("java.specification.version") + System.lineSeparator())
					error.printStackTrace(PrintWriter(object : OutputStream() {
						override fun write(b: Int) {
							field.append(b.toChar().toString())
						}
					}, true))
					add(field)
					defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
				}
			}
		}
	}
}
