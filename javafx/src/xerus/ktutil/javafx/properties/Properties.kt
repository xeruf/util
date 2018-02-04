package xerus.ktutil.javafx.properties

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.binding.Bindings
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.Property
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableValue
import sun.misc.ExtensionDependency
import java.util.*
import java.util.concurrent.Callable

fun <T> Property<T>.bind(callable: () -> T, vararg dependencies: Observable): Property<T> {
	bind(Bindings.createObjectBinding(Callable(callable), *dependencies))
	return this
}

/** binds this Property via a simple InvalidationListener, the Property will NOT appear bound
 * @return the [InvalidationListener] used to establish the connection */
fun <T> Property<T>.bindSoft(callable: () -> T, vararg dependencies: Observable): InvalidationListener {
	value = callable()
	val listener = InvalidationListener { _ -> value = callable() }
	dependencies.forEach { it.addListener(listener) }
	return listener
}

/** calls the given function with the value of the dependency as argument whenever it notifies its ChangeListeners */
inline fun <T, U> Property<T>.dependOn(dependency: ObservableValue<U>, crossinline function: (U) -> T) {
	dependency.addListener { _, _, new -> value = function(new) }
}

fun <T, U> ObservableValue<T>.dependentObservable(function: (T) -> U): ObservableValue<U> =
		SimpleObservable(function(value)).also { addListener { _, _, new -> it.value = function(new) } }

fun Observable.addOneTimeListener(runnable: () -> Unit) = addListener(object : InvalidationListener {
	override fun invalidated(observable: Observable?) {
		runnable()
		this@addOneTimeListener.removeListener(this)
	}
})

class Listeners<in T>(private val observable: ObservableValue<T>) {

	private val listeners = ArrayDeque<Any>()
	fun add(listener: Any) {
		listeners.add(listener)
	}

	fun remove(listener: Any) {
		listeners.removeIf { it == listener }
	}

	/** If old != new, then all listeners are notified of the change */
	fun notify(old: T, new: T) {
		if (old != new)
			listeners.forEach {
				(it as? InvalidationListener)?.invalidated(observable) ?:
						@Suppress("UNCHECKED_CAST")
						(it as? ChangeListener<in T>)?.changed(observable, old, new)
			}
	}

}
