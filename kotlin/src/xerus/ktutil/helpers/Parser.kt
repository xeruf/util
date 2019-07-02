package xerus.ktutil.helpers

import xerus.ktutil.toInt
import java.util.*

class Parser(startDelimiter: Char, endDelimiter: Char) {
	
	constructor(delimiter: Char): this(delimiter, delimiter)
	
	private val delimiters = Delimiters(startDelimiter, endDelimiter)
	
	/**
	 * parses the given String by applying the function to every String within the delimiters
	 *
	 * @return the results of the unparsed sections concatenated with the results of the function
	 * @throws ParserException wraps any Exceptions thrown by the function
	 */
	fun parse(toParse: String, unparsed: (String) -> String = { it }, function: (String) -> String): String {
		val split = delimiters.apply(toParse)
		
		val out = StringBuilder()
		var apply = false
		for(cur in split) {
			if(cur.isNotEmpty())
				try {
					if(apply)
						out.append(function(cur))
					else
						out.append(unparsed(cur))
				} catch(e: Exception) {
					throw ParserException(cur, e)
				}
			apply = !apply
		}
		return out.toString()
	}
	
	/** Creates a Matcher from this Parser with the given String
	 * @throws ParserException when the Matcher encounters errors while reading in the String to parse */
	fun createMatcher(toParse: String, vararg keys: String): Matcher =
		Matcher(delimiters, toParse, *keys)
	
	/** A Matcher is useful for string templates that are reused.
	 * On creation, it requires [Delimiters], a String and keys within the string that should be replaced.
	 * Then the string will be read into an abstraction and the template can quickly be filled using [apply].
	 * @throws ParserException if there is a delimited string which matches no provided key */
	class Matcher(delimiters: Delimiters, toParse: String, vararg keys: String) {
		
		private val intersections: Array<String?>
		private val matchIndices: IntArray
		
		init {
			val split = delimiters.apply(toParse)
			
			val s = split.size / 2
			intersections = arrayOfNulls(s + 1)
			matchIndices = IntArray(s)
			
			var i = 0
			while(i < s) {
				intersections[i] = split[i * 2]
				val cur = split[i * 2 + 1]
				val index = keys.indexOf(cur)
				if(index == -1)
					throw ParserException("No matching key for $cur in ${Arrays.toString(keys)}")
				matchIndices[i] = index
				i++
			}
			intersections[s] = split.last()
		}
		
		/** Tries to insert the given values into the template.
		 *  @return the processed String
		 *  @throws ArrayIndexOutOfBoundsException if less values are provided than needed */
		fun apply(vararg values: String): String {
			val out = StringBuilder()
			for(i in matchIndices.indices) {
				out.append(intersections[i])
				out.append(values[matchIndices[i]])
			}
			out.append(intersections[intersections.size - 1])
			return out.toString()
		}
	}
	
	class Delimiters(private val start: Char, private val end: Char) {
		
		constructor(delimiter: Char): this(delimiter, delimiter)
		
		/** Splits the given String with these Delimiters.
		 * @return A List of the split results, which alternates between Strings enclosed by delimiters and Strings in between.
		 * Its length is always uneven and starts and ends with characters which were not within delimiters. */
		fun apply(toSplit: String): List<String> {
			val result = ArrayList<String>()
			for(s in toSplit.split(start)) {
				val ind = s.indexOf(end)
				if(ind != -1) {
					result.add(s.substring(0, ind))
					result.add(s.substring(ind + 1))
				} else
					result.add(s)
			}
			if(result.size % 2 == 0)
				result.add("")
			return result
		}
		
	}
	
}


class ParserException(msg: String, cause: Throwable? = null): Exception("Error while parsing $msg", cause)