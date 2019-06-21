@file:Suppress("NOTHING_TO_INLINE", "unused")

package xerus.ktutil

inline fun <T> T?.printWith(function: (T) -> Any?) =
	println(if(this == null) "null" else "${this.testString()} - ${function(this).testString()}")

inline fun <T> T.printIt(name: Any? = null) =
	apply { testString().let { println(if(name != null) "$name: $it" else it) } }

fun Any?.testString(): String =
	when(this) {
		is Array<*> -> this.joinToString(", ", "${this.javaClass.simpleName}@${this.hashCode()}[", "]") { it.testString() }
		is Collection<*> -> this.joinToString(", ", "${this.javaClass.simpleName}@${this.hashCode()}[", "]") { it.testString() }
		null -> "null"
		else -> toString()
	}