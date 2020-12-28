package xerus.ktutil.preferences

/** Provides a general interface for persistently storing String values. */
interface ISetting {
	
	var value: String
	
	/** Saves [value] to this Setting. */
	fun put(value: String) {
		this.value = value
	}
	
	/** Saves the toString() of value to this Setting. */
	fun put(value: Any) = put(value.toString())
	
	/** @return the [value] as Boolean. */
	val bool: Boolean
		get() = value.toBoolean()
	
	/** @return the [value] as Int. */
	val int: Int
		get() = value.toInt()
	
	/** @return all multi-values. */
	val all: List<String>
		get() = value.split(MULTIDELIMITER)
	
	/** Adds or removes the given key, depending on the Boolean [add]. */
	fun putMulti(key: String, add: Boolean) {
		val cur: String = this.value
		if(add) {
			put(if(cur.isEmpty()) key else cur + MULTIDELIMITER + key)
		} else {
			val result = cur.split(MULTIDELIMITER).filterNot { it.contains(key) }.toTypedArray()
			putMulti(*result)
		}
	}
	
	/** Replaces the current value with the joined [values]. */
	fun putMulti(vararg values: Any?) = put(values.joinToString(MULTIDELIMITER))
	
	/** Replaces the current value with the joined [values]. */
	fun putMulti(values: List<*>) = put(values.joinToString(MULTIDELIMITER))
	
	/** Checks if the given [value] is contained in this multi-value. */
	fun getMulti(value: String) = this.value.contains(value)
	
	companion object {
		const val MULTIDELIMITER = ";"
	}
}
