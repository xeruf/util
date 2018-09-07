package xerus.ktutil.javafx.properties

import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableValue
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

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
	
	val job = launch {
		while (true) {
			delay(interval)
			value = listeners.notify(value, supplier())
		}
	}
	
	/** Stops the calculation job */
	fun finalize() {
		job.cancel()
	}
}

class SimpleObservable<T>(private var value: T) : AbstractObservableValue<T>(), WritableValue<T> {
	
	override fun setValue(value: T) {
		val old = this.value
		this.value = value
		listeners.notify(old, value)
	}
	
	override fun getValue() = value
	
}
