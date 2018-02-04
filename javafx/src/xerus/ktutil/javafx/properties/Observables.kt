package xerus.ktutil.javafx.properties

import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableValue
import javafx.collections.ObservableListBase

class UnmodifiableObservableList<T>(vararg val content: T) : ObservableListBase<T>() {
	
	override val size: Int
		get() = content.size
	
	override fun get(index: Int) = content[index]
	
	fun getArray() = content
	
	override fun addAll(vararg elements: T) = throw UnsupportedOperationException()
	override fun setAll(vararg elements: T) = throw UnsupportedOperationException()
	override fun setAll(col: Collection<T>?) = throw UnsupportedOperationException()
	override fun removeAll(vararg elements: T) = throw UnsupportedOperationException()
	override fun retainAll(vararg elements: T) = throw UnsupportedOperationException()
	override fun remove(from: Int, to: Int) = throw UnsupportedOperationException()
}

class ConstantObservable<T>(private val constValue: T) : ObservableValue<T> {
	override fun getValue() = constValue
	override fun removeListener(listener: ChangeListener<in T>?) {}
	override fun removeListener(listener: InvalidationListener?) {}
	override fun addListener(listener: ChangeListener<in T>?) {}
	override fun addListener(listener: InvalidationListener?) {}
}

class SimpleObservable<T>(private var value: T) : ObservableValue<T>, WritableValue<T> {
	
	override fun setValue(value: T) {
		val old = this.value
		this.value = value
		listeners.notify(old, value)
	}
	
	override fun getValue() = value
	
	private val listeners = Listeners(this)
	override fun addListener(listener: InvalidationListener) = listeners.add(listener)
	override fun addListener(listener: ChangeListener<in T>) = listeners.add(listener)
	override fun removeListener(listener: InvalidationListener) = listeners.remove(listener)
	override fun removeListener(listener: ChangeListener<in T>) = listeners.remove(listener)
	
}