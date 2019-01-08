package xerus.ktutil.helpers

import xerus.ktutil.collections.joinEnumeration
import xerus.ktutil.reflectField

private val fieldParser = Parser('%')
private val bracketParser = Parser('{', '}')

/** This Interface denotes a class that can be stringified according to patterns.
 * It exposes a [toString] function that takes a pattern as argument
 * and then tries to fill in that pattern by filling field references
 * (indicated by surrounding % signs) using [getField] */
interface Parsable {
	
	/**Parses this object to a String using the given format String.
	 * @throws NoSuchFieldException if the format contains an unknown field
	 */
	fun toString(pattern: String, vararg additionalFields: Pair<String, String>) = parseRecursively(pattern, additionalFields.associate { it }).first
	
	private fun parseRecursively(pattern: String, additionalFields: Map<String, String>): Pair<String, Boolean> {
		var inserted = false
		val result = bracketParser.parse(pattern, {
			try {
				fieldParser.parse(it) { field ->
					val value = parseField(field, additionalFields)
					inserted = inserted || value.isNotEmpty()
					value
				}
			} catch(e: ParserException) {
				if(e.cause is NoSuchFieldException)
					throw e.cause
				else
					throw e
			}
		}, {
			val parsed = parseRecursively(it, additionalFields)
			if(parsed.second)
				parsed.first
			else ""
		})
		return Pair(result, inserted)
	}
	
	/** Parses a field for insertion and joins it if it is an array or a collection, taking into account a potential given separator. */
	private fun parseField(field: String, additionalFields: Map<String, String>): String {
		field.split('|').let {
			val fieldName = it[0]
			val value = additionalFields[fieldName] ?: getField(fieldName)
			if(it.size > 1) {
				val separator = it.getOrNull(1) ?: ","
				@Suppress("UNCHECKED_CAST")
				(value as? Array<Any> ?: (value as? Collection<Any>)?.toTypedArray())?.let { array ->
					return when {
						array.size == 1 -> array.first().toString()
						separator == "enumeration" -> joinEnumeration(*array)
						else -> array.joinToString(separator)
					}
				}
			}
			return value.toString()
		}
	}
	
	/** Gets the value of the [field] for insertion. The default implementation uses [reflectField]. */
	fun getField(field: String) = reflectField(field)
	
}
