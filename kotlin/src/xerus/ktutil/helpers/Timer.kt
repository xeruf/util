package xerus.ktutil.helpers

/** times in milliseconds  */
class Timer {
	
	private var time: Long = System.currentTimeMillis()
	
	fun restart() {
		time = System.currentTimeMillis()
	}
	
	/** elapsed time in milliseconds  */
	fun time(): Long =
			System.currentTimeMillis() - time
	
	fun printTime(msg: String): Long {
		val time = time()
		println(parseTime(msg, time))
		return time
	}
	
	companion object {
		
		private var t: Timer? = null
		
		fun start() {
			t = Timer()
		}
		
		/** elapsed time in milliseconds */
		fun runtime(): Long = t!!.time()
		
		fun finish(msg: String = "Time"): Long {
			if (t == null)
				return 0
			return t!!.printTime(msg).also { t = null }
		}
		
		fun parseTime(msg: String, millis: Long): String {
			val res = StringBuilder(msg).append(": ")
			if (millis < 10000)
				res.append(millis).append("m")
			else
				res.append(millis / 1000)
			return res.append("s").toString()
		}
	}
	
}
