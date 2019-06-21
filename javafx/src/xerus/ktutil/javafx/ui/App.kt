package xerus.ktutil.javafx.ui

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.TextArea
import javafx.stage.Stage
import xerus.ktutil.SystemUtils
import xerus.ktutil.getStackTraceString
import xerus.ktutil.javafx.Theme
import xerus.ktutil.javafx.Themes
import xerus.ktutil.javafx.applyTheme

class App : Application() {
	
	init {
		application = this
	}
	
	override fun start(primaryStage: Stage) {
		stage = primaryStage
		initialized = true
		stager.invoke(primaryStage)
		restart()
	}
	
	companion object {
		var initialized = false
		lateinit var application: Application
		lateinit var stage: Stage
		
		private lateinit var stager: (Stage) -> Unit
		private lateinit var content: (() -> Scene)
		private var theme: Theme? = null
		
		/** Launches the JavaFX Application in the current Thread. This method will only return once JavaFX is terminated. */
		fun launch(title: String = "Test", theme: Theme? = Themes.BEIGE, stager: ((Stage) -> Unit)? = null, scene: () -> Scene) {
			this.content = scene
			this.theme = theme
			this.stager = { it.title = title; stager?.invoke(stage) }
			Application.launch(App::class.java)
		}
		
		/** Hides the [stage], invokes [System.gc] to ensure all old bindings are gone and creates a new Scene
		 * from the parameters set at [launch] and shows the [stage] again.
		 * If an error occurs during startup, a crash report is shown instead. */
		fun restart() {
			try {
				stage.hide()
				stage.scene = null
				System.gc()
				stage.scene = content().apply { theme?.let { applyTheme(it) } }
				stage.show()
			} catch(error: Throwable) {
				val stage = Stage()
				stage.scene = Scene(TextArea("A critical error occurred while starting the application. " +
					"Please contact the developer, providing the information below!\n\n" +
					"Java version: " + SystemUtils.javaVersion + "\n" +
					error.getStackTraceString()))
				stage.show()
			}
		}
	}
	
}
