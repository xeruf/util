@file:Suppress("NOTHING_TO_INLINE", "unused")

package xerus.ktutil.collections

inline fun <C : Collection<T>, T> C?.nullIfEmpty() =
	this?.takeUnless { it.isEmpty() }

inline fun <T> List<T>.getReverse(index: Int) =
	this[size - 1 - index]

inline fun <E> MutableCollection<E>.addAll(vararg elements: E) =
	this.addAll(elements)

fun ByteArray.toInt() =
	this[3].toInt() and 0xFF or (this[2].toInt() and 0xFF shl 8) or (this[1].toInt() and 0xFF shl 16) or (this[0].toInt() and 0xFF shl 24)

fun Int.toByteArray() =
	byteArrayOf(this.ushr(24).toByte(), this.ushr(16).toByte(), this.ushr(8).toByte(), this.toByte())

// JOIN

/** Joins this list in a way that is natural to read for humans, using commas and `&` instead of the last comma.
 * @return - an empty string if the list is empty
 * - the first element [toString] if there is only one
 * - otherwise a string following the pattern "str(0), str(1), ... str(n-1) & str(n)" where n is the length of this List. */
fun List<Any>.joinEnumeration(): String = when(size) {
	0 -> ""
	1 -> get(0).toString()
	else -> "%s & %s".format(slice(0..size - 2).joinToString(", "), last())
}

/** Joins the given [sequences] in a human way, using commas and `&` instead of the last comma. */
inline fun joinEnumeration(vararg sequences: Any): String = sequences.asList().joinEnumeration()
