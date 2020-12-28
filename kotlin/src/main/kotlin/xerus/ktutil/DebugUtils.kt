@file:Suppress("NOTHING_TO_INLINE", "unused")

package xerus.ktutil

/** Prints this object together with the return value of [function], or "null" if this is null. */
inline fun <T> T?.printWith(function: (T) -> Any?) =
	println(if(this == null) "null" else "${this.testString()} - ${function(this).testString()}")

/** Prints the [testString] of this object with an optional [name] to stdout and returns the object back.
 * Useful for debugging within call chains. */
inline fun <T> T.printIt(name: Any? = null) =
	apply { testString().let { println(if(name != null) "$name: $it" else it) } }

/** Recursively stringifies this object. Especially useful for nested Arrays/Collections. */
fun Any?.testString(): String =
	when(this) {
		is Array<*> -> this.joinToString(", ", "${this.javaClass.simpleName}@${this.hashCode()}[", "]") { it.testString() }
		is Collection<*> -> this.joinToString(", ", "${this.javaClass.simpleName}@${this.hashCode()}[", "]") { it.testString() }
		null -> "null"
		else -> toString()
	}