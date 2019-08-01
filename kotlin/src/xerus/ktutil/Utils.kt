@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package xerus.ktutil

import java.io.PrintWriter
import java.io.StringWriter
import java.time.LocalDate

// Strings

/** If this String is null or empty, return [other], otherwise returns itself. */
inline fun String?.or(other: String) =
	if(this.isNullOrEmpty()) other else this

/** If this String is empty, return null, otherwise returns itself. */
inline fun String?.nullIfEmpty() =
	if(isNullOrEmpty()) null else this

/** Checks if this String contains any of the given [sequences], ignoring case. */
fun String.containsAny(vararg sequences: CharSequence) =
	sequences.any { contains(it, true) }

/** Checks if this String contains [other] or the other way around, ignoring case. */
fun String.containsEach(other: String) = contains(other, true) || other.contains(this, true)

/** Converts a String in `yyyy-mm-dd` format to a LocalDate */
fun String.toLocalDate(): LocalDate? {
	val split = split("-").map { it.toIntOrNull() ?: return null }
	return LocalDate.of(split[0], split[1], split[2])
}

// Generics

/** Creates a Pair of this object and a value calculated from it. */
inline fun <T, U> T.pair(function: T.() -> U): Pair<T, U> =
	Pair(this, this.run(function))

/** Runs the given runnable if [this] is null and returns [this]. */
inline fun <T> T?.ifNull(runnable: () -> Unit) =
	also { if(it == null) runnable() }

// Other

/** A short String representation of a Throwable including the name of the exception and the message. */
fun Throwable.str() = "${javaClass.simpleName}: $message"

/** Returns the StackTrace of this [Throwable] as a String as written by [printStackTrace]. */
fun Throwable.getStackTraceString(): String {
	val sw = StringWriter()
	val pw = PrintWriter(sw, true)
	printStackTrace(pw)
	return sw.buffer.toString()
}

/** Calls [action] with all values from [start], inclusive, to [end], exclusive.
 * Dedicated for performance-critical algorithms, usually you should use `(start until end).forEach { }` instead */
inline fun forRange(start: Int, end: Int, action: (Int) -> Unit) {
	var i = start
	while(i < end) {
		action(i)
		i++
	}
}

/** Gets the value of the field with the given name using java reflection.
 * If the class has no accessible field with that name, it also tries to find a getter.
 * @return value of the Field
 * @throws FieldNotFoundException if neither an accessible field nor a getter with the corresponding name could be found in this class */
fun Any.reflectField(fieldName: String): Any =
	try {
		javaClass.getField(fieldName).get(this)
	} catch(e: NoSuchFieldException) {
		try {
			javaClass.getMethod("get" + fieldName.capitalize()).invoke(this)
		} catch(e: NoSuchMethodException) {
			throw FieldNotFoundException(fieldName, this.javaClass)
		}
	}

/** An exception indicating that no field or getter for [fieldName] could be found in class [clazz]. */
class FieldNotFoundException(val fieldName: String, val clazz: Class<*>): ReflectiveOperationException("Field not found: $fieldName")
