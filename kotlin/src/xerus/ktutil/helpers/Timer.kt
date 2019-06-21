package xerus.ktutil.helpers

import xerus.ktutil.formatTimeDynamic

/**
 * Records times in milliseconds.
 *
 * Can be used statically, which internally uses a static Timer instance,
 * or instantiated and then used.
 */
class Timer {
	
	private var time: Long = System.currentTimeMillis()
	
	/** resets the time to [System.currentTimeMillis] */
	fun restart() {
		time = System.currentTimeMillis()
	}
	
	/** elapsed time in milliseconds */
	fun time(): Long =
		System.currentTimeMillis() - time
	
	/**
	 * Pretty-prints the [time] using [parseTime] and returns it
	 * @param msg the message to prepend the time with or null for no message
	 * @return [time] */
	fun printTime(msg: String?): Long {
		val time = time()
		println(parseTime(msg, time))
		return time
	}
	
	companion object {
		
		private var t: Timer? = null
		
		/** Starts a new internal [Timer] */
		fun start() {
			t = Timer()
		}
		
		/** Elapsed time in milliseconds */
		fun runtime() = t!!.time()
		
		/** Pretty-prints the elapsed time using [parseTime] and resets the internal timer */
		fun finish(msg: String = "Time") =
			t?.run { printTime(msg).also { t = null } } ?: 0
		
		/**
		 * Returns the given [millis] with a message.
		 * If [millis] exceeds 10 seconds, it will show seconds with tens of seconds.
		 * If [millis] exceeds 300 seconds, it will show a digital clock-like representation.
		 *
		 * @param msg the message to prepend the time with or null for no message
		 * @param millis the milliseconds - will be postfixed by the appropriate unit */
		fun parseTime(msg: String?, millis: Long): String {
			val res = if(msg != null) StringBuilder(msg).append(": ") else StringBuilder()
			if(millis < 300_000) {
				if(millis < 10_000) {
					res.append(millis).append('m')
				} else {
					res.append(millis / 1000).append('.').append((millis / 100) % 10)
				}.append('s')
			} else {
				res.append(formatTimeDynamic(millis / 1000))
			}
			return res.toString()
		}
		
	}
	
}
