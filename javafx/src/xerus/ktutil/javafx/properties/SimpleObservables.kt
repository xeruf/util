package xerus.ktutil.javafx.properties

import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableValue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class AbstractObservableValue<T> : ObservableValue<T> {
	protected val listeners = Listeners(this)
	override fun addListener(listener: InvalidationListener) = listeners.add(listener)
	override fun addListener(listener: ChangeListener<in T>) = listeners.add(listener)
	override fun removeListener(listener: InvalidationListener) = listeners.remove(listener)
	override fun removeListener(listener: ChangeListener<in T>) = listeners.remove(listener)
	
	override fun toString() = "${this.javaClass.simpleName}(value=$value)"
}

/** An ObservableValue that recalculates its value from a supplier according to a specified Interval
 * @param interval the amount of milliseconds to wait between value calculations
 */
class TimedObservable<T>(private val interval: Long, private val supplier: () -> T) : AbstractObservableValue<T>() {
	private var value = supplier()
	override fun getValue() = value
	
	val job = GlobalScope.launch {
		while(true) {
			delay(interval)
			val old = value
			value = supplier()
			listeners.notifyChange(old, value)
		}
	}
	
	/** Stops the job */
	fun finalize() {
		job.cancel()
	}
	
}

class SimpleObservable<T>(private var value: T) : AbstractObservableValue<T>(), WritableValue<T> {
	
	override fun setValue(newValue: T) {
		val old = this.value
		this.value = newValue
		listeners.notifyChange(old, newValue)
	}
	
	override fun getValue() = value
	
}
