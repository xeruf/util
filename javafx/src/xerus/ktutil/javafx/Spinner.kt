package xerus.ktutil.javafx

import javafx.beans.property.Property
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.util.converter.LocalTimeStringConverter
import xerus.ktutil.javafx.properties.dependentObservable
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun intSpinner(min: Int = -Int.MAX_VALUE, max: Int = Int.MAX_VALUE, initial: Int = 0) = Spinner<Int>(min, max, initial).editable()
fun doubleSpinner(min: Double = -Double.MAX_VALUE, max: Double = Double.MAX_VALUE, initial: Double = 0.0) = Spinner<Double>(min, max, initial).editable()
fun <T> Spinner<T>.editable(): Spinner<T> {
	isEditable = true
	editor.textProperty().addListener { _, _, nv ->
		// let the user clear the field without complaining
		if (nv.isNotEmpty())
			valueFactory.value = try {
				valueFactory.converter.fromString(nv)
			} catch (e: Exception) {
				// user typed an illegal character
				value
			} catch (t: Throwable) {
				// user typing stuff
				return@addListener
			}
	}
	return this
}

fun <T> Spinner<T>.optionalProperty() = editor.textProperty().dependentObservable { if (it.isEmpty()) null else value }

class TimeSpinner : Spinner<LocalTime>() {
	
	init {
		valueFactory = object : SpinnerValueFactory<LocalTime>() {
			init {
				converter = LocalTimeStringConverter(DateTimeFormatter.ofPattern("HH:mm:ss"), DateTimeFormatter.ofPattern("HH:mm:ss"))
			}
			
			override fun increment(steps: Int) {
				if (value == null)
					value = LocalTime.MIN
				value = value.plusMinutes(steps.toLong())
			}
			
			override fun decrement(steps: Int) {
				value = if (value == null || value.minute == 0)
					LocalTime.MIN
				else
					value.minusMinutes(steps.toLong())
			}
			
		}
		editable()
	}
}