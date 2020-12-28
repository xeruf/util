@file:Suppress("NOTHING_TO_INLINE", "unused")

package xerus.ktutil.collections

/** @return this collection or null if it is empty */
inline fun <C : Collection<T>, T> C?.nullIfEmpty() =
	this?.takeUnless { it.isEmpty() }

/** Gets the element at [index] counted from the end.
 * @return element at [lastIndex] - [index] */
inline fun <T> List<T>.getReverse(index: Int) =
	this[lastIndex - index]

/** Adds the given [elements] to this [MutableCollection]. */
inline fun <E> MutableCollection<E>.addAll(vararg elements: E) =
	this.addAll(elements)

// JOIN

/** Joins this list in a way that is natural to read for humans, using commas and `&` instead of the last comma.
 * @return - an empty string if the list is empty
 *   - the first element [toString] if there is only one
 *   - otherwise a string following the pattern "str(0), str(1), ... str(n-1) & str(n)" where n is the length of this List. */
fun List<Any>.joinEnumeration(): String = when(size) {
	0 -> ""
	1 -> get(0).toString()
	else -> "%s & %s".format(slice(0..size - 2).joinToString(", "), last())
}

/** Joins the given [sequences] in a way that is natural to read for humans, using commas and `&` instead of the last comma.
 * @return - an empty string if the list is empty
 *   - the first element [toString] if there is only one
 *   - otherwise a string following the pattern "str(0), str(1), ... str(n-1) & str(n)" where n is the length of this List.  */
inline fun joinEnumeration(vararg sequences: Any): String = sequences.asList().joinEnumeration()
