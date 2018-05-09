package xerus.ktutil

import java.io.*
import java.net.URL
import java.util.*

object SystemUtils {
	
	val tempDir
		get() = File(System.getProperty("java.io.tmpdir"))
	
	private val systemErr = System.err
	fun <T> suppressErr(supplier: () -> T): T {
		suspendErr()
		val result = supplier()
		restoreErr()
		return result
	}
	
	fun suspendErr() {
		System.setErr(PrintStream(object : OutputStream() {
			override fun write(b: Int) {
			}
		}))
	}
	
	fun restoreErr() = System.setErr(systemErr)
	
}

fun currentSeconds() = (System.currentTimeMillis() / 1000).toInt()

/** convenience function to get a String representation of the current localized time
 * @return localized time in hh:mm:ss format */
fun formattedTime(): String {
	val time = System.currentTimeMillis()
	return formatTime(currentSeconds() + TimeZone.getDefault().getOffset(time) / 1000)
}

/** provides a String representation of the given time
 * @param shorten if true, adjusts the format to the size of the number, else it is always hh:mm:ss
 * @return `seconds` in hh:mm:ss format
 */
fun formatTime(seconds: Int, format: String = "%02d:%02d:%02d") =
		format.format(seconds % 86400 / 3600, seconds % 3600 / 60, seconds % 60)

fun formatTimeDynamic(seconds: Int, orientation: Int = seconds)  =
		when {
			orientation > 3600 -> formatTime(seconds)
			orientation > 60 -> formatTime(seconds, "%2$02d:%3$02d")
			else -> "%02ds".format(seconds)
		}

fun Throwable.getStackTraceString(): String {
	val sw = StringWriter()
	val pw = PrintWriter(sw, true)
	printStackTrace(pw)
	return sw.buffer.toString()
}

fun getResource(path: String): URL? = XerusLogger::class.java.getResource("/$path")

fun javaVersion(): String = System.getProperty("java.version")
