@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package xerus.ktutil

import java.time.LocalDate
import java.util.*

// Stringies

/** If this String is null or empty, other is returned, else this */
inline fun String?.or(other: String) = if (this.isNullOrEmpty()) other else this!!
inline fun String?.nullIfEmpty() = if (isNullOrEmpty()) null else this

fun String.containsAny(vararg sequences: CharSequence) = sequences.any { contains(it, true) }

fun List<Any>.joinEnumeration(): String =
		if (size == 1) get(0).toString()
		else "%s & %s".format(slice(0..size - 2).joinToString(), last())

fun joinEnumeration(vararg sequences: Any): String =
		if (sequences.size == 1) sequences[0].toString()
		else "%s & %s".format(sequences.slice(0..sequences.size - 2).joinToString(), sequences.last())

// Basics

inline fun Boolean.toInt() = if (this) 1 else 0

inline fun <T, U> T.pair(function: T.() -> U): Pair<T, U> = Pair(this, this.run(function))

inline fun <T> T?.ifNull(runnable: () -> Unit) = also { if(it==null) runnable() }
inline fun <T> T?.ifNotNull(runnable: (T) -> Unit) = also { if(it!=null) runnable(it) }

// DEBUG

inline fun <T> T?.printWith(function: (T) -> Any?) = println(if (this == null) "null" else "${this.testString()} - ${function(this).testString()}")
inline fun <T> T?.testString(): String = when (this) {
	is Array<*> -> Arrays.toString(this)
	null -> "null"
	else -> toString()
}

inline fun <T> T.printNamed(name: Any? = null) = apply { testString().let { println(if (name != null) "$name: $it" else it) } }

// COLLECTIONS

inline fun <C: Collection<T>, T> C?.nullIfEmpty() = this?.takeUnless { it.isEmpty() }

inline fun <T> List<T>.getReverse(index: Int): T =
	this[size - 1 - index]

inline fun <E> MutableList<E>.removeLast() = this.removeAt(this.size - 1)

fun ByteArray.toInt(): Int {
	return this[3].toInt() and 0xFF or (this[2].toInt() and 0xFF shl 8) or (this[1].toInt() and 0xFF shl 16) or (this[0].toInt() and 0xFF shl 24)
}

fun Int.toByteArray(): ByteArray {
	return byteArrayOf(this.ushr(24).toByte(), this.ushr(16).toByte(), this.ushr(8).toByte(), this.toByte())
}

fun <E> MutableCollection<E>.filterOut(predicate: (E) -> Boolean): MutableCollection<E> {
	for (element in this)
		if (!predicate(element))
			remove(element)
	return this
}

// Reflection

fun Any.getField(field: String): Any = javaClass.getField(field).get(this)

// Other

fun String.toLocalDate(): LocalDate? {
	val split = split("-").map { it.toIntOrNull() ?: return null }
	return LocalDate.of(split[0], split[1], split[2])
}
