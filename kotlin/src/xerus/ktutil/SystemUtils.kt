package xerus.ktutil

import java.io.File
import java.io.OutputStream
import java.io.PrintStream
import java.util.*

object SystemUtils {
	
	val tempDir
		get() = File(System.getProperty("java.io.tmpdir"))
	
	val javaVersion
		get() = System.getProperty("java.version")
	
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

/** The current UTC seconds */
fun currentSeconds() = (System.currentTimeMillis() / 1000).toInt()

/** convenience function to get a String representation of the current localized time
 * @return localized time in hh:mm:ss format */
fun formattedTime(): String {
	val time = System.currentTimeMillis()
	return formatTime(System.currentTimeMillis().plus(TimeZone.getDefault().getOffset(time)).div(1000))
}

/** provides a String representation of the given time
 * @return `seconds` in hh:mm:ss format */
fun formatTime(seconds: Long, format: String = "%02d:%02d:%02d") =
	format.format(seconds % 86400 / 3600, seconds % 3600 / 60, seconds % 60)

/** provides a dynamic String representation of the given time
 * @return `seconds` in hh:mm:ss format, omitting hours or even minutes if not necessary */
fun formatTimeDynamic(seconds: Long, orientation: Long = seconds) =
	when {
		orientation >= 3600 -> formatTime(seconds)
		orientation >= 60 -> "%02d:%02d".format(seconds / 60, seconds % 60)
		else -> "%02ds".format(seconds)
	}

/** Gets a resource from the classpath by its absolute path. */
fun getResource(path: String) = SystemUtils::class.java.getResource("/$path")

/** Gets a resource from the classpath by its absolute path as a File or null if it doesn't exist */
fun getResourceAsFile(path: String) = getResource(path)?.file?.let { File(it) }

