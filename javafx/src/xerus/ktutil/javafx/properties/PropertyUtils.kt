package xerus.ktutil.javafx.properties

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.binding.Bindings
import javafx.beans.property.Property
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableValue
import javafx.collections.ObservableList
import java.util.concurrent.Callable

fun <T> Property<T>.bind(callable: () -> T, vararg dependencies: Observable): Property<T> {
	bind(Bindings.createObjectBinding(Callable(callable), *dependencies))
	return this
}

fun <T, U> Property<T>.bindBidirectional(other: Property<U>, converterToOther: (T) -> U, converterFromOther: (U) -> T): Property<T> {
	var working = false
	addListener { _, _, new ->
		if (!working) {
			working = true
			other.value = converterToOther(new)
			working = false
		}
	}
	other.addListener { _, _, new ->
		if (!working) {
			working = true
			value = converterFromOther(new)
			working = false
		}
	}
	return this
}

/** binds this Property via a simple InvalidationListener, the Property will NOT appear bound
 * @return the [InvalidationListener] used to establish the connection */
fun <T> WritableValue<T>.bindSoft(callable: () -> T, vararg dependencies: Observable): InvalidationListener {
	value = callable()
	val listener = InvalidationListener { _ -> value = callable() }
	dependencies.forEach { it.addListener(listener) }
	return listener
}

/** Adds the given Listener to each Observable in the Array.
 * @param trigger whether the [runnable] should be run after adding it as Listener */
fun Array<out Observable>.addListener(trigger: Boolean = false, runnable: () -> Unit): InvalidationListener {
	val listener = InvalidationListener { _ -> runnable() }
	forEach { it.addListener(listener) }
	if (trigger)
		runnable()
	return listener
}

/** calls the given function with the value of the dependency as argument whenever it notifies its ChangeListeners */
inline fun <T, U> WritableValue<T>.dependOn(dependency: ObservableValue<U>, crossinline function: (U) -> T): ChangeListener<U> {
	val listener = ChangeListener<U> { _, _, new -> value = function(new) }
	listener.changed(dependency, dependency.value, dependency.value)
	dependency.addListener(listener)
	return listener
}

/** Creates a simple ObervableValue that updates it's value according to the function when ever
 * this ObservableValue triggers its ChangeListeners */
fun <T, U> ObservableValue<T>.dependentObservable(function: (T) -> U): ObservableValue<U> =
		SimpleObservable(function(value)).also { addListener { _, _, new -> it.value = function(new) } }

/** Adds a Listener that removes itself after being triggered once */
fun Observable.addOneTimeListener(runnable: () -> Unit) = addListener(object : InvalidationListener {
	override fun invalidated(observable: Observable?) {
		this@addOneTimeListener.removeListener(this)
		runnable()
	}
})

/** Adds a ChangeListener to this ObservableValue that only receives the new value */
fun <T> ObservableValue<T>.listen(listener: (T) -> Unit) =
		addListener { _, _, new -> listener(new) }

/** Adds a ChangeListener to this ObservableList that only receives this list */
fun <T> ObservableList<T>.listen(listener: (ObservableList<T>) -> Unit) =
		addListener { _: Observable -> listener(this) }

/** Removes a listener, sets this Property to the [value] and then adds the listener back
 * @param listenerToSilence The listener to temporarily remove, so it doesn't get fired by this change */
fun <T> Property<T>.setSilently(value: T, listenerToSilence: ChangeListener<T>) {
	removeListener(listenerToSilence)
	setValue(value)
	addListener(listenerToSilence)
}
