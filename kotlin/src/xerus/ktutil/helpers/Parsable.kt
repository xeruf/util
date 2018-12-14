package xerus.ktutil.helpers

import xerus.ktutil.joinEnumeration
import xerus.ktutil.reflectField

private val fieldParser = Parser('%')
private val bracketParser = Parser('{', '}')

/** This Interface denotes a class that can be stringified according to patterns.
 * It exposes a [toString] function that takes a pattern as argument
 * and then tries to fill in that pattern, gathering the necessary
 * information via reflection */
interface Parsable {
	
	/**
	 * parses this object to a String using the given format String
	 * @throws NoSuchFieldException if the format contains an unknown field
	 */
	fun toString(pattern: String) = parseRecursively(pattern).first
	
	private fun parseRecursively(pattern: String): Pair<String, Boolean> {
		var inserted = false
		val result = bracketParser.parse(pattern, {
			fieldParser.parse(it) {
				val value = insertField(it)
				inserted = inserted || value.isNotEmpty()
				value
			}
		}, {
			val parsed = parseRecursively(it)
			if(parsed.second)
				parsed.first
			else ""
		})
		return Pair(result, inserted)
	}
	
	private fun insertField(string: String): String {
		string.split('|').let {
			val value = reflectField(it[0])
			if(it.size > 1) {
				val separator = it[1]
				@Suppress("UNCHECKED_CAST")
				(value as? Array<Any> ?: (value as? Collection<Any>)?.toTypedArray())?.let {
					return if(separator == "enumeration") joinEnumeration(*it)
					else it.joinToString(separator)
				}
			}
			return value.toString()
		}
	}
	
}
