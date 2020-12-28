package xerus.ktutil.preferences

import java.util.prefs.Preferences

/** Simple implementation of [PrefSetting]. */
open class PlainSetting(override val key: String, override val default: String, override val prefs: Preferences): PrefSetting

/** An [ISetting] that automatically syncs its value to the given [prefs] - [key]. */
interface PrefSetting: ISetting {
	/** [Preferences] node to sync to. */
	val prefs: Preferences
	/** Key within [prefs] to sync to. */
	val key: String
	/** Default value if there is no stored value yet. */
	val default: String
	
	/** The current value - there is no backing field, simply a pass-through to [prefs]. */
	override var value: String
		get() = prefs.get(key, default)
		set(value) = prefs.put(key, value)
	
	/** Clears out this [key] from [prefs]. */
	fun clear() = prefs.remove(key)
}