package xerus.ktutil.javafx.ui.controls

import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import xerus.ktutil.javafx.*
import xerus.ktutil.javafx.ui.Fadable
import xerus.ktutil.javafx.ui.SimpleTransition
import xerus.ktutil.javafx.ui.verticalFade

open class FadingHBox(visible: Boolean, translate: Double = 0.0, targetHeight: Int = 30, spacing: Int = 3) : HBox(spacing.toDouble()), Fadable {
    override val fader: SimpleTransition<Region> = this.verticalFade(targetHeight, translate)
    protected val closeButton = Button().onClick { fadeOut() }.id("close")

    init {
        id("controls")
        alignment = Pos.CENTER
        if (visible) {
            setSize(height = targetHeight.toDouble())
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

}