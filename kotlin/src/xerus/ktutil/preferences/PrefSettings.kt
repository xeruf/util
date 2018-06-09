package xerus.ktutil.preferences

import java.util.prefs.Preferences

open class PlainSetting(override val key: String, override val default: String, override val prefs: Preferences) : PrefSetting

interface PrefSetting : ISetting {
	val prefs: Preferences
	
	val key: String
	val default: String
	
	override var value: String
		get() = prefs.get(key, default)
		set(value) = prefs.put(key, value)
	
	/** Removes ALL Settings in the used Preferences Node */
	fun clear() = prefs.clear()
}