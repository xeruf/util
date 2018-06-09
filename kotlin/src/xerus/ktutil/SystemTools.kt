package xerus.ktutil

import java.io.OutputStream
import java.io.PrintStream

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

fun currentSeconds() = (System.currentTimeMillis() / 1000).toInt()