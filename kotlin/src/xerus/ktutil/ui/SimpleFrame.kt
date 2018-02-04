package xerus.ktutil.ui

import java.awt.Component
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.WindowConstants

class SimpleFrame(parent: Component? = null, initGUI: JFrame.() -> Unit) : JFrame() {

    init {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        minimumSize = Dimension(400, 300)
        initGUI()
        pack()
        setLocationRelativeTo(parent)
        isVisible = true
    }

}
