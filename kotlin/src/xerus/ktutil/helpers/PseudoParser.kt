package xerus.ktutil.helpers

import xerus.ktutil.toInt
import java.util.*

class PseudoParser(startDelimiter: Char, endDelimiter: Char) {

    constructor(delimiter: Char) : this(delimiter, delimiter)

    private val delimiters = Delimiters(startDelimiter, endDelimiter)

    /**
     * parses the given String by applying the function to every String within the delimiters
     *
     * @return the results of the unparsed sections concatenated with the results of the function
     * @throws ParserException wraps the function when it throws an Exception
     */
    fun parse(toParse: String, unparsed: (String) -> String = { it }, function: (String) -> String): String {
        val split = delimiters.apply(toParse)

        val out = StringBuilder()
        var apply = false
        for (cur in split) {
            if(cur.isNotEmpty())
                try {
                    if (apply)
                        out.append(function(cur))
                    else
                        out.append(unparsed(cur))
                } catch (e: Exception) {
                    throw ParserException(cur, e)
                }
            apply = !apply
        }
        return out.toString()
    }

    /** Creates a Matcher from this Parser with the given String
     * @throws ParserException when the Matcher encounters errors while reading in the String to parse */
    fun createMatcher(toParse: String, vararg keys: String): Matcher {
        return Matcher(delimiters, toParse, *keys)
    }

    class Matcher @Throws(ParserException::class)
    constructor(delimiters: Delimiters, toParse: String, vararg keys: String) {

        private val intersections: Array<String?>
        private val matchIndices: IntArray
        private val uneven: Boolean

        init {
            val split = delimiters.apply(toParse)

            val s = split.size / 2
            uneven = split.size % 2 == 1
            intersections = arrayOfNulls(s + uneven.toInt())
            matchIndices = IntArray(s)

            var i = 0
            while (i < s) {
                intersections[i] = split[i]
                val cur = split[i + 1]
                val index = keys.indexOf(cur)
                if (index == -1)
                    throw ParserException(cur)
                matchIndices[i] = index
                i += 2
            }
            intersections[s] = split.last()
        }

        /** tries to insert the given values into the parsed String
         *  @return the processed String
         *  @throws ArrayIndexOutOfBoundsException if less values are provided than needed */
        fun apply(vararg values: String): String {
            val out = StringBuilder()
            for (i in matchIndices.indices) {
                out.append(intersections[i])
                out.append(values[matchIndices[i]])
            }
            out.append(intersections[intersections.size - 1])
            return out.toString()
        }
    }

    class Delimiters(private val start: Char, private val end: Char) {

        /** splits the given String with these Delimiters
         * @return a List of the split results, which alternates between Strings enclosed by delimiters and Strings in between.
         * Its length is always uneven and starts and ends with characters which were not within delimiters */
        fun apply(toSplit: String): List<String> {
            val result = ArrayList<String>()
            for (s in toSplit.split(start)) {
                val ind = s.indexOf(end)
                if (ind != -1) {
                    result.add(s.substring(0, ind))
                    result.add(s.substring(ind + 1))
                } else
                    result.add(s)
            }
            if (result.size % 2 == 0)
                result.add("")
            return result
        }

        companion object {
            fun create(delimiter: Char): Delimiters {
                return Delimiters(delimiter, delimiter)
            }
        }

    }

    class ParserException : Exception {
        val match: String

        internal constructor(msg: String) : super("Error while parsing " + msg) {
            match = msg
        }

        internal constructor(msg: String, t: Throwable) : super("Error while parsing " + msg, t) {
            match = msg
        }
    }

}
