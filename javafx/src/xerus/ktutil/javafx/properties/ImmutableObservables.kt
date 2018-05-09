package xerus.ktutil.javafx.properties

import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableListBase

class ImmutableObservableList<T>(vararg val content: T) : ObservableListBase<T>() {
	
	override val size: Int
		get() = content.size
	
	override fun get(index: Int) = content[index]
	
}

class ImmutableObservable<T>(private val constValue: T) : ObservableValue<T> {
	override fun getValue() = constValue
	override fun removeListener(listener: ChangeListener<in T>?) {}
	override fun removeListener(listener: InvalidationListener?) {}
	override fun addListener(listener: ChangeListener<in T>?) {}
	override fun addListener(listener: InvalidationListener?) {}
}
