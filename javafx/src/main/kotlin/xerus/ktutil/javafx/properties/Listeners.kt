package xerus.ktutil.javafx.properties

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import java.util.*

/** A helper class that will store [ChangeListener]s and [InvalidationListener]s and notify them appropriately.
 * Please note: To avoid performance overhead, if a Listener is added while a change is propagated, a concurrent modification,
 *   the change will be sent to all Listeners again, so they might in rare circumstances receive the same change twice.
 *
 * @param observable will be passed to the listeners
 * @param alwaysNotify whether to notify listeners on change even if the new value equals the old one */
open class Listeners<T>(private val observable: ObservableValue<T>? = null, private val alwaysNotify: Boolean = false): Observable {
	
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
		if(alwaysNotify || old != new) {
			try {
				listeners.forEach {
					(it as? InvalidationListener)?.invalidated(observable) ?: @Suppress("UNCHECKED_CAST")
					(it as? ChangeListener<in T>)?.changed(observable, old, new)
				}
			} catch(e: ConcurrentModificationException) {
				notifyChange(old, new)
			}
		}
		return new
	}
	
	/** Notifies only [InvalidationListener]s */
	fun notifyInvalidation() {
		try {
			listeners.forEach {
				(it as? InvalidationListener)?.invalidated(observable)
			}
		} catch(e: ConcurrentModificationException) {
			notifyInvalidation()
		}
	}
	
	override fun toString() = "Listeners for $observable with ${listeners.size} listeners"
	
}
