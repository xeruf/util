@file:Suppress("NOTHING_TO_INLINE")

package xerus.ktutil

import java.time.LocalDate

/** If this String is null or empty, return [other], otherwise return itself. */
inline fun String?.or(other: String) =
	if(this.isNullOrEmpty()) other else this

/** If this String is empty, return null, otherwise return itself. */
inline fun String?.nullIfEmpty() =
	if(isNullOrEmpty()) null else this

/** Checks if this String contains any of the given [sequences], ignoring case. */
fun String.containsAny(vararg sequences: CharSequence) =
	sequences.any { contains(it, true) }

/** Checks if this String contains [other] or the other way around, ignoring case. */
fun String.containsEach(other: String) =
	contains(other, true) || other.contains(this, true)

/** Converts a String in `yyyy-mm-dd` format to a LocalDate.
 * @return a [LocalDate] or null in case any part is not a valid Integer.
 * @throws IndexOutOfBoundsException if there are less than 3 `-`-delimited numbers. */
fun String.toLocalDate(): LocalDate? {
	val split = split("-").map { it.toIntOrNull() ?: return null }
	return LocalDate.of(split[0], split[1], split[2])
}

val titleDelimiters = charArrayOf(' ', ',', '[', ']', '(', ')', '&')
val titleFluff = arrayOf("", "-", "Remix")

/** Splits a String, usually a music title, into its parts using [titleDelimiters] and filters out [titleFluff]. */
fun String.splitTitleTrimmed() =
	split(*titleDelimiters).filterNot { it in titleFluff || it.matches(Regex("(ft|feat)\\.?", RegexOption.IGNORE_CASE)) }

