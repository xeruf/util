package xerus.ktutil.preferences

import javafx.beans.InvalidationListener
import javafx.beans.property.ObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import xerus.ktutil.javafx.properties.Listeners
import xerus.ktutil.javafx.properties.bindSoft
import java.util.prefs.Preferences

/**
 * Caches the Setting as an [ObjectProperty] to minimise I/O.
 *
 * It is recommended to use a [SettingsNode] to ease the creation, but it can also be used independently.
 */
open class PropertySetting<T>(private val key: String, private val default: T, val preferences: Preferences, private val parser: (String) -> T) : ObjectProperty<T>(), ISetting {
	
	override var value: String
		get() = get().toString()
		set(value) = set(parser(value))
	
	private var _value = loadValue()
	
	override fun get() = _value
	
	override fun set(value: T) {
		updateOwnValue(value)
		preferences.put(key, value.toString())
	}
	
	/** Reloads the [value] from the [preferences] */
	fun refresh() = set(loadValue())
	
	private fun loadValue() = preferences.get(key, null)?.let {
		try {
			parser(it)
		} catch(e: Exception) {
			null
		}
	} ?: default
	
	private fun updateOwnValue(value: T) {
		val old = _value
		_value = value
		listeners.notifyChange(old, value)
	}
	
	/** Clears the entry in [preferences] and resets the value to the default */
	fun clear() {
		preferences.remove(key)
		updateOwnValue(default)
	}
	
	// Listeners
	private val listeners = Listeners(this)
	
	override fun addListener(listener: InvalidationListener) = listeners.add(listener)
	override fun addListener(listener: ChangeListener<in T>) = listeners.add(listener)
	override fun removeListener(listener: InvalidationListener) = listeners.remove(listener)
	override fun removeListener(listener: ChangeListener<in T>) = listeners.remove(listener)
	
	override fun getName() = key
	override fun getBean() = preferences
	
	// Bindings
	private var observable: ObservableValue<out T>? = null
	private var listener: InvalidationListener? = null
	/** calls [unbind] and then softly binds this [PropertySetting] to the given [ObservableValue] via a listener */
	override fun bind(observable: ObservableValue<out T>) {
		unbind()
		listener = bindSoft({ observable.value }, observable)
		this.observable = observable
	}
	
	/** stops listening to changes of [observable]. No-op if not bound. */
	override fun unbind() {
		observable?.removeListener(listener)
		observable = null
	}
	
	override fun isBound() = observable != null
	
	operator fun invoke() = get()
}
