package xerus.ktutil.javafx

import javafx.beans.property.Property
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.util.converter.LocalTimeStringConverter
import xerus.ktutil.javafx.properties.dependentObservable
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/** Creates an editale Spinner<Int> using the supplied or default values */
fun intSpinner(min: Int = -Int.MAX_VALUE, max: Int = Int.MAX_VALUE, initial: Int = 0) = Spinner<Int>(min, max, initial).editable()

/** Creates an editale Spinner<Double> using the supplied or default values */
fun doubleSpinner(min: Double = -Double.MAX_VALUE, max: Double = Double.MAX_VALUE, initial: Double = 0.0) = Spinner<Double>(min, max, initial).editable()

/** Enables editing of the field and embraces changes accordingly */
fun <T> Spinner<T>.editable(): Spinner<T> {
	isEditable = true
	editor.textProperty().addListener { _, _, nv ->
		// let the user clear the field without complaining
		if(nv.isNotEmpty())
			valueFactory.value = try {
				valueFactory.converter.fromString(nv)
			} catch(e: Exception) {
				// user typed an illegal character
				value
			} catch(t: Throwable) {
				// user typing stuff
				return@addListener
			}
	}
	return this
}

/** An ObervableValue that is null when the Spinner is cleared */
fun <T> Spinner<T>.optionalValue() = editor.textProperty().dependentObservable { if(it.isEmpty()) null else value }

/** Updates the given Property whenever the value of this Spinner changes */
infix fun <T> Spinner<T>.syncTo(observable: Property<T>): Spinner<T> {
	valueFactory.value = observable.value
	observable.bind(valueProperty())
	return this
}

class TimeSpinner : Spinner<LocalTime>() {
	
	init {
		valueFactory = object : SpinnerValueFactory<LocalTime>() {
			init {
				converter = LocalTimeStringConverter(DateTimeFormatter.ofPattern("HH:mm:ss"), DateTimeFormatter.ofPattern("HH:mm:ss"))
			}
			
			override fun increment(steps: Int) {
				if(value == null)
					value = LocalTime.MIN
				value = value.plusMinutes(steps.toLong())
			}
			
			override fun decrement(steps: Int) {
				value = if(value == null || value.minute == 0)
					LocalTime.MIN
				else
					value.minusMinutes(steps.toLong())
			}
			
		}
		editable()
	}
}