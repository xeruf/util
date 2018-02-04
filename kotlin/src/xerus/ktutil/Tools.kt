package xerus.ktutil

import java.io.*
import java.net.URL
import java.util.*
import kotlin.reflect.KClass

/**
 * convenience function to get a String representation of the current localized time
 * @return localized time in hh:mm:ss format
 */
fun formattedTime(): String {
	val time = System.currentTimeMillis()
	return formatTime((time + TimeZone.getDefault().getOffset(time)) / 1000)
}

/**
 * provides a String representation of the given time
 * @return `seconds` in hh:mm:ss format
 */
fun formatTime(seconds: Long) = String.format("%02d:%02d:%02d", seconds % 86400 / 3600, seconds % 3600 / 60, seconds % 60)

fun Throwable.getStackTraceString(): String {
	val sw = StringWriter()
	val pw = PrintWriter(sw, true)
	printStackTrace(pw)
	return sw.buffer.toString()
}

fun getResource(path: String): URL? = XerusLogger::class.java.getResource("/$path")

fun javaVersion(): String = System.getProperty("java.version")