package xerus.ktutil.javafx.ui

import javafx.animation.Transition
import javafx.scene.layout.Region
import javafx.util.Duration
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import xerus.ktutil.javafx.onFx
import xerus.ktutil.javafx.setSize

interface Fadable {
	val fader: SimpleTransition<Region>
	
	fun show(new: () -> Unit) {
		launch {
			if (visible)
				fader.play()
			while (fading)
				delay(50)
			onFx {
				new()
				fader.play()
			}
		}
	}
	
	fun fadeOut() {
		if (visible && !fading)
			fader.play()
	}
	
	fun ensureVisible() {
		if (!visible && !fading)
			fader.play()
	}
	
	val fading: Boolean
		get() = fader.playing
	
	val visible: Boolean
		get() = fader.target.opacity > 0.0
	
}

/**@param translate a negative value will make the content fade upwards, a positive downwards. 0 (default) simple collapses this Region*/
fun Region.verticalFade(targetHeight: Int, translate: Double = 0.0, seconds: Double = 0.4): SimpleTransition<Region> =
		SimpleTransition(this, Duration.seconds(seconds), { frac -> setSize(height = targetHeight * frac); translateY = translate * targetHeight * (0.5 - frac / 2); opacity = frac; isVisible = opacity > 0.0 }, false)

fun Region.transitionToHeight(targetHeight: Double, seconds: Double = 0.4): SimpleTransition<Region> {
	val initialHeight = height
	return SimpleTransition(this, Duration.seconds(seconds), { frac -> setSize(height = initialHeight + (targetHeight - initialHeight) * frac) }, true)
}

open class SimpleTransition<out T>(internal val target: T, length: Duration, private val function: T.(pos: Double) -> Unit, instantPlay: Boolean = true, private val onComplete: (T.() -> Unit)? = null) : Transition() {
	var playing: Boolean = instantPlay
	
	init {
		cycleDuration = length
		if (instantPlay)
			onFx { play() }
	}
	
	override fun play() {
		playing = true
		super.play()
	}
	
	private var lastPos: Double = 0.0
	override fun interpolate(pos: Double) {
		function(target, pos)
		if (pos != lastPos && (pos == 0.0 || pos == 1.0)) {
			lastPos = pos
			onComplete?.invoke(target)
			onFx {
				rate = -rate
				playing = false
			}
		}
	}
}