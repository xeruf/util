package xerus.ktutil.javafx.properties

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import java.util.*

/** A helper class that will store [ChangeListener]s and [InvalidationListener]s and notify them appropriately
 * @param observable will be passed to the listeners
 * @param alwaysNotify whether to notify listeners on change even if the new value equals the old one */
open class Listeners<T>(private val observable: ObservableValue<T>? = null, private val alwaysNotify: Boolean = false) : Observable {
	
	private val listeners = ArrayDeque<Any>()
	
	fun add(listener: Any) {
		listeners.add(listener)
	}
	
	fun remove(listener: Any) {
		listeners.removeIf { it == listener }
	}
	
	override fun addListener(listener: InvalidationListener) = add(listener)
	
	override fun removeListener(listener: InvalidationListener) = remove(listener)
	
	/** If old != new or [alwaysNotify], then all listeners are notified of the change
	 * @return the new value */
	fun notifyChange(old: T?, new: T?): T? {
		if(alwaysNotify || old != new)
			listeners.forEach {
				(it as? InvalidationListener)?.invalidated(observable) ?: @Suppress("UNCHECKED_CAST")
				(it as? ChangeListener<in T>)?.changed(observable, old, new)
			}
		return new
	}
	
	/** Notifies only [InvalidationListener]s */
	fun notifyInvalidation() {
		listeners.forEach {
			(it as? InvalidationListener)?.invalidated(observable)
		}
	}
	
	override fun toString() = "Listeners for $observable with ${listeners.size} listeners"
	
}
