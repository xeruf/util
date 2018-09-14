package xerus.ktutil.helpers

/** Times in milliseconds.
 *
 * Can be used statically, which internally uses a static Timer instance,
 * or instantiated and used*/
class Timer {
	
	private var time: Long = System.currentTimeMillis()
	
	fun restart() {
		time = System.currentTimeMillis()
	}
	
	/** elapsed time in milliseconds */
	fun time(): Long =
			System.currentTimeMillis() - time
	
	/** Prints the [time] using [parseTime]
	 * @param msg the message to prepend the time with or null for no message */
	fun printTime(msg: String?): Long {
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
		
		/** Prints the given [millis] with a message.
		 * If [millis] are 10000 or above, it will instead show seconds.
		 *
		 * @param msg the message to prepend the time with or null for no message
		 * @param millis the milliseconds - will be postfixed by the appropriate unit */
		fun parseTime(msg: String?, millis: Long): String {
			val res = if (msg != null) StringBuilder(msg).append(": ") else StringBuilder()
			if (millis < 10000)
				res.append(millis).append('m')
			else
				res.append(millis / 1000)
			if (millis < 100000)
				res.append('.').append((millis / 100) % 10)
			return res.append('s').toString()
		}
		
	}
	
}
