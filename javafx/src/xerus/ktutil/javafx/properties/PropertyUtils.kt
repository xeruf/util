package xerus.ktutil.javafx.properties

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.binding.Bindings
import javafx.beans.property.Property
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableValue
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import java.util.concurrent.Callable

/** Creates a [javafx.beans.binding.ObjectBinding] using the supplied parameters and binds this property to it.
 * @return [this] for a fluent style */
fun <T> Property<T>.bind(callable: () -> T, vararg dependencies: Observable): Property<T> {
	bind(Bindings.createObjectBinding(Callable(callable), *dependencies))
	return this
}

/** Binds this property bidirectionally to the other using [ChangeListener]s and converters.
 * Neither property will appear bound, instead both will simply update when the other is changed.
 * @return [this] for a fluent style */
fun <T, U> Property<T>.bindBidirectional(other: Property<U>, converterToOther: (T) -> U, converterFromOther: (U) -> T): Property<T> {
	var working = false
	addListener { _, _, new ->
		if(!working) {
			working = true
			other.value = converterToOther(new)
			working = false
		}
	}
	other.addListener { _, _, new ->
		if(!working) {
			working = true
			value = converterFromOther(new)
			working = false
		}
	}
	return this
}

/** Binds this Property via a simple InvalidationListener, the Property will NOT appear bound.
 * @return the [InvalidationListener] used to establish the connection */
fun <T> WritableValue<T>.bindSoft(callable: () -> T, vararg dependencies: Observable): InvalidationListener {
	value = callable()
	val listener = InvalidationListener { value = callable() }
	dependencies.forEach { it.addListener(listener) }
	return listener
}

/** Adds the given Listener to each Observable in the Array.
 * @param trigger whether the [runnable] should be run once after adding it as Listener
 * @return the [InvalidationListener] added to each [Observable] */
fun Array<out Observable>.addListener(trigger: Boolean = false, runnable: () -> Unit): InvalidationListener {
	val listener = InvalidationListener { runnable() }
	forEach { it.addListener(listener) }
	if(trigger)
		runnable()
	return listener
}

/** Calls the given function with the value of the dependency as argument whenever it notifies its ChangeListeners.
 * @return the [ChangeListener] attached to [dependency] */
inline fun <T, U> WritableValue<T>.dependOn(dependency: ObservableValue<U>, crossinline function: (U) -> T): ChangeListener<U> {
	val listener = ChangeListener<U> { _, _, new -> value = function(new) }
	listener.changed(dependency, dependency.value, dependency.value)
	dependency.addListener(listener)
	return listener
}

/** Creates a simple ObervableValue that updates its value according to the function whenever
 * this ObservableValue triggers its ChangeListeners. */
fun <T, U> ObservableValue<T>.dependentObservable(function: (T) -> U): ObservableValue<U> =
	SimpleObservable(function(value)).also { addListener { _, _, new -> it.value = function(new) } }

/** Adds a Listener that removes itself after being triggered once. */
fun Observable.addOneTimeListener(runnable: () -> Unit) = addListener(object : InvalidationListener {
	override fun invalidated(observable: Observable?) {
		this@addOneTimeListener.removeListener(this)
		runnable()
	}
})

/** Adds a ChangeListener to this ObservableValue that only receives the new value. */
fun <T> ObservableValue<T>.listen(listener: (T) -> Unit) =
	ChangeListener<T> { _, _, new -> listener(new) }.also { addListener(it) }

/** Adds a ChangeListener to this ObservableList that only receives this list. */
fun <T> ObservableList<T>.listen(listener: (ObservableList<T>) -> Unit) =
	ListChangeListener<T> { listener(this) }.also { addListener(it) }

/** Removes a listener, sets this Property to the [value] and then adds the listener back.
 * @param listenerToSilence The listener to temporarily remove, so it doesn't get triggered by this change */
fun <T> Property<T>.setWithoutListener(value: T, listenerToSilence: ChangeListener<T>) {
	removeListener(listenerToSilence)
	setValue(value)
	addListener(listenerToSilence)
}
