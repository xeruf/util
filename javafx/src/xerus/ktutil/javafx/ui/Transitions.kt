package xerus.ktutil.javafx.ui

import javafx.animation.Transition
import javafx.scene.layout.Region
import javafx.util.Duration
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import xerus.ktutil.javafx.*

interface Fadable {
    val fader: SimpleTransition<Region>

    fun show(new: () -> Unit) {
        launch {
            if (visible)
                fader.play()
            while (fading)
                delay(50)
            onJFX {
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

fun Region.verticalTransition(seconds: Double, targetHeight: Int): SimpleTransition<Region>
    = SimpleTransition(this, Duration.seconds(seconds), { frac -> setSize(height = targetHeight * frac); opacity = frac }, false, { isVisible = opacity > 0.1 })

open class SimpleTransition<out T>(internal val target: T, time: Duration, private val function: T.(pos: Double) -> Unit, instantPlay: Boolean = true, private val onComplete: (T.() -> Unit)? = null) : Transition() {
    var playing: Boolean = instantPlay

    init {
        cycleDuration = time
        if (instantPlay)
            onJFX { play() }
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
            onJFX {
                rate = -rate
                playing = false
            }
        }
    }
}