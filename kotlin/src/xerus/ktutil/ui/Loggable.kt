package xerus.ktutil.ui

interface Loggable {
	
	fun appendText(text: String)
	
	fun appendln(text: String) {
		appendText(text)
		appendln()
	}
	
	fun appendln(any: Any) = appendln(any.toString())
	
	fun appendln() =
			appendText("\n")
	
	fun appendAll(prefix: String = "", vararg strings: String) {
		for (arg in strings)
			appendln(prefix + arg)
	}
	
	fun log(format: String, vararg args: Any) =
			appendln(format.format(*args))
	
}
