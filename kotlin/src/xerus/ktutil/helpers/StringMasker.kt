package xerus.ktutil.helpers

class StringMasker(private val mask: String, private vararg val toMask: String) {
	
	fun mask(s: String): String {
		var res = s
		for (i in toMask.indices)
			res = res.replace(toMask[i], mask + i)
		return res
	}
	
	fun unmask(s: String): String {
		var res = s
		for (i in toMask.indices)
			res = res.replace(mask + i, toMask[i])
		return res
	}
	
}
