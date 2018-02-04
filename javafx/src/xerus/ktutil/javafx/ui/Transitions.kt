package xerus.ktutil.javafx.ui

import javafx.animation.Transition
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.util.Duration
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import xerus.ktutil.javafx.*


open class FadingHBox(visible: Boolean, protected var targetHeight: Double = 30.0, spacing: Double = 3.0) : HBox(spacing) {
    private val fader = SimpleTransition(this, Duration.seconds(0.3), { frac -> setSize(height = targetHeight * frac); opacity = frac }, false)
    protected val closeButton = Button().onClick { fadeOut() }.id("close")

    init {
        id("controls")
        alignment = Pos.CENTER
        if (visible) {
            setSize(height = targetHeight)
        } else {
            setSize(height = 0.0)
            opacity = 0.0
        }
    }

    fun setChildren(vararg children: Node) {
        this.children.setAll(*children)
        fill(pos = 0)
        fill()
        add(closeButton)
    }

    fun show(new: () -> Unit) {
        launch {
            if (opacity == 1.0)
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
        if (opacity == 1.0 && !fading)
            fader.play()
    }

    fun ensureVisible() {
        if (opacity == 0.0 && !fading)
            fader.play()
    }

    val fading: Boolean
        get() = fader.playing
}

open class SimpleTransition<T>(private val target: T, time: Duration, private val function: T.(pos: Double) -> Unit, instantPlay: Boolean = true, private val onComplete: (T.() -> Unit)? = null) : Transition() {
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