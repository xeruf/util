package xerus.ktutil.javafx.properties

import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableListBase

class UnmodifiableObservableList<T>(vararg val content: T) : ObservableListBase<T>() {
	
	override val size: Int
		get() = content.size
	
	override fun get(index: Int) = content[index]
	
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