package xerus.ktutil.javafx.properties

import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import java.util.*

open class Listeners<T>(private val observable: ObservableValue<T>) {

    private val listeners = ArrayDeque<Any>()

    fun add(listener: Any) {
        listeners.add(listener)
    }

    fun remove(listener: Any) {
        listeners.removeIf { it == listener }
    }

    /** If old != new, then all listeners are notified of the change
     * @return the new value */
    fun notify(old: T, new: T): T {
        if (old != new)
            listeners.forEach {
                (it as? InvalidationListener)?.invalidated(observable) ?:
                @Suppress("UNCHECKED_CAST")
                (it as? ChangeListener<in T>)?.changed(observable, old, new)
            }
        return new
    }

    /*
    fun addListener(listener: InvalidationListener) = add(listener)
    fun addListener(listener: ChangeListener<*>) = add(listener)
    fun removeListener(listener: InvalidationListener) = remove(listener)
    fun removeListener(listener: ChangeListener<*>) = remove(listener)
    */

}