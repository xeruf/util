@file:Suppress("NOTHING_TO_INLINE")

package xerus.ktutil

inline fun <C : Collection<T>, T> C?.nullIfEmpty() =
		this?.takeUnless { it.isEmpty() }

inline fun <T> List<T>.getReverse(index: Int) =
		this[size - 1 - index]

fun <E> MutableCollection<E>.addAll(vararg elements: E) =
		this.addAll(elements)

fun ByteArray.toInt() =
		this[3].toInt() and 0xFF or (this[2].toInt() and 0xFF shl 8) or (this[1].toInt() and 0xFF shl 16) or (this[0].toInt() and 0xFF shl 24)

fun Int.toByteArray() =
		byteArrayOf(this.ushr(24).toByte(), this.ushr(16).toByte(), this.ushr(8).toByte(), this.toByte())

// join

fun List<Any>.joinEnumeration(): String =
		if (size == 1) get(0).toString()
		else "%s & %s".format(slice(0..size - 2).joinToString(), last())

fun joinEnumeration(vararg sequences: Any): String =
		if (sequences.size == 1) sequences[0].toString()
		else "%s & %s".format(sequences.slice(0..sequences.size - 2).joinToString(), sequences.last())
