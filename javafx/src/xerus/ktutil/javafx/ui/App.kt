package xerus.ktutil.javafx.ui

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.TextArea
import javafx.stage.Stage
import xerus.ktutil.getStackTraceString
import xerus.ktutil.javaVersion
import java.lang.RuntimeException

class App : Application() {
	
	override fun start(primaryStage: Stage) {
		stage = primaryStage
		initialized = true
		stager.invoke(primaryStage)
		restart()
	}
	
	companion object {
		var initialized = false
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
				val stage = Stage()
				stage.scene = Scene(TextArea("A critical error occured while starting the application. " +
						"Please contact the developer, providing the information below!\n\n" +
						"Java version: " + javaVersion() + "\n" +
						error.getStackTraceString()))
				stage.show()
			}
		}
	}
	
}
