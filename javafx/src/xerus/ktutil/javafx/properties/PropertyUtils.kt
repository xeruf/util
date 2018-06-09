package xerus.ktutil.javafx.properties

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.binding.Bindings
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.Property
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableValue
import javafx.collections.FXCollections
import jdk.nashorn.internal.objects.NativeFunction.function
import sun.misc.ExtensionDependency
import java.util.*
import java.util.concurrent.Callable

fun <T> Property<T>.bind(callable: () -> T, vararg dependencies: Observable): Property<T> {
	bind(Bindings.createObjectBinding(Callable(callable), *dependencies))
	return this
}

fun <T, U> Property<T>.bindBidirectional(other: Property<U> , converterToOther: (T) -> U, converterFromOther: (U) -> T): Property<T> {
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

/** binds this Property via a simple InvalidationListener, the Property will NOT appear bound
 * @return the [InvalidationListener] used to establish the connection */
fun <T> WritableValue<T>.bindSoft(callable: () -> T, vararg dependencies: Observable): InvalidationListener {
	value = callable()
	val listener = InvalidationListener { _ -> value = callable() }
	dependencies.forEach { it.addListener(listener) }
	return listener
}

fun Array<out Observable>.addListener(runnable: () -> Unit): InvalidationListener {
	val listener = InvalidationListener { _ -> runnable() }
	forEach { it.addListener(listener) }
	return listener
}

/** calls the given function with the value of the dependency as argument whenever it notifies its ChangeListeners */
inline fun <T, U> WritableValue<T>.dependOn(dependency: ObservableValue<U>, crossinline function: (U) -> T): ChangeListener<U> {
	val listener = ChangeListener<U> { _, _, new -> value = function(new) }
	dependency.addListener(listener)
	return listener
}

fun <T, U> ObservableValue<T>.dependentObservable(function: (T) -> U): ObservableValue<U> =
		SimpleObservable(function(value)).also { addListener { _, _, new -> it.value = function(new) } }

fun Observable.addOneTimeListener(runnable: () -> Unit) = addListener(object : InvalidationListener {
	override fun invalidated(observable: Observable?) {
		runnable()
		this@addOneTimeListener.removeListener(this)
	}
})

fun <T> ObservableValue<T>.listen(listener: (T) -> Unit) =
	addListener { _, _, new -> listener(new) }

