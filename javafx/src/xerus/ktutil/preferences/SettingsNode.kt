package xerus.ktutil.preferences

import xerus.ktutil.SystemUtils
import xerus.ktutil.collections.WeakCollection
import xerus.ktutil.preferences.ISetting.Companion.MULTIDELIMITER
import xerus.ktutil.printIt
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.prefs.Preferences
import kotlin.reflect.KClass

open class SettingsNode(val preferences: Preferences) {
	constructor(path: String) : this(getPreferences(path))
	
	/** Creates a new [PropertySetting] using the parameters and [preferences]
	 * and adds it to the [settings] so they can all be cleared at once */
	fun <T> create(key: String, default: T, parser: (String) -> T) =
		PropertySetting(key, default, preferences, parser).also { settings.add(it) }
	
	inline fun <reified T : Enum<T>> create(key: String, default: T) =
		create(key, default) { enumValueOf(it) }
	
	fun create(key: String, default: String = "") = create(key, default) { it }
	fun create(key: String, default: Boolean) = create(key, default) { it.toBoolean() }
	fun create(key: String, default: Int) = create(key, default) { it.toInt() }
	fun create(key: String, default: Long) = create(key, default) { it.toLong() }
	fun create(key: String, default: Double) = create(key, default) { it.toDouble() }
	
	fun create(key: String, default: File) = create(key, default) { File(it) }
	fun create(key: String, default: Path) = create(key, default) { Paths.get(it) }
	
	fun create(key: String, default: Array<*>) = create(key, default.joinToString(MULTIDELIMITER)) { it }
	
	val settings = WeakCollection<PropertySetting<*>>()
	
	/** Removes all data from [preferences], resets all settings created in this Node to their default and [flush]es */
	fun clear() {
		preferences.clear()
		settings.forEach { it.clear(); it.printIt() }
		flush()
	}
	
	/** Writes any pending changes to disk */
	fun flush() = preferences.flush()
	
	/** Reloads each Setting created by this [SettingsNode] from the [preferences] */
	fun refresh() {
		settings.forEach { it.refresh() }
	}
	
	companion object {
		fun getPreferences(clazz: KClass<*>): Preferences = getPreferences(clazz.java.`package`.name.replace('.', '/'))
		fun getPreferences(path: String): Preferences = SystemUtils.suppressErr { Preferences.userRoot().node(path) }
	}
}
