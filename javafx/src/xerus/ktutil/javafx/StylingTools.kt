package xerus.ktutil.javafx

import javafx.css.CssMetaData
import javafx.css.Styleable
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.TabPane
import xerus.ktutil.testString
import java.util.*

@Suppress("UNCHECKED_CAST")
object StylingTools {

    /** returns a recursively composed Collection with Nodes that match the given css StyleClass  */
    fun findByStyleClass(root: Styleable, className: String): Collection<Styleable> {
        return find(root, { node -> node.styleClass.contains(className) })
    }

    /** returns a recursively composed Collection with Nodes that are instances of the given Class  */
    fun <T> findByClass(root: Styleable, c: Class<T>): Collection<T> {
        return find(root, { c.isInstance(it) }) as Collection<T>
    }

    /** returns a recursively composed Collection with Nodes that match the predicate  */
    fun find(root: Styleable, p: (Styleable) -> Boolean): Collection<Styleable> {
        val found = HashSet<Styleable>()
        findRec(root, p, found)
        return found
    }

    fun dump(root: Styleable, f: (Styleable) -> String?) {
        findRec(root, { node ->
            val s = f(node) ?: false
            println(s)
            false
        }, null)
    }

    /** recursively fills the given Collection with Nodes that match the predicate  */
    fun findRec(node: Styleable?, p: (Styleable) -> Boolean, col: MutableCollection<Styleable>?) {
        if (node == null)
            return
        if (p(node))
            col?.add(node)
        when (node) {
            is TabPane -> for (tab in node.tabs)
                findRec(tab.content, p, col)
            is MenuBar -> for (child in node.menus)
                findRec(child, p, col)
            is Menu -> for (child in node.items)
                findRec(child, p, col)
            is Parent -> for (child in node.childrenUnmodifiable)
                findRec(child, p, col)
        }
    }

    // CSS Metadata

    fun printCSS(n: Node, filter: String = "") = printCSS(n.cssMetaData, filter)
    fun printCSS(styles: Collection<CssMetaData<out Styleable, *>>, filter: String) {
        styles.filter { it.property.contains(filter) }.forEach { println(it) }
    }

    fun toString(style: CssMetaData<out Styleable, *>, n: Node): String {
        val res = StringBuilder(String.format("%20s: %s", style.property, (style as CssMetaData<Styleable, *>).getInitialValue(n).testString()))
        val properties = style.subProperties
        if (properties != null && properties.size > 0) {
            res.append(" - sub: [")
            for (prop in properties) {
                res.append(toString(prop, n))
                res.append(", ")
            }
            res.setLength(res.length - 2)
            res.append(']')
        }
        return res.toString()
    }

}
