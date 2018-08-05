package xerus.ktutil.helpers

import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.withTimeoutOrNull
import kotlin.math.absoluteValue

typealias Testable = (Double) -> Unit

private fun Testable.test(param: Double, timeout: Long = Long.MAX_VALUE): Long =
		runBlocking {
			val job = launch {
				Timer.start()
				invoke(param)
			}
			withTimeoutOrNull(timeout) {
				job.join()
			} ?: return@runBlocking Long.MAX_VALUE
			Timer.runtime()
		}

object Benchmark {
	
	fun test(vararg torun: () -> Unit) {
		for (method in torun) {
			System.gc()
			Timer.start()
			method()
			Timer.finish()
		}
	}
	
	fun test(param: Double, vararg torun: Testable) {
		for (testable in torun) {
			System.gc()
			output(testable.test(param), param)
		}
	}
	
	fun optimise(initialParam: Double, initialPrecision: Double = 0.1, doGC: Boolean = true, testable: Testable): Double {
		print("Warming up..")
		do {
			print(".")
			val times = LongArray(5) { testable.test(initialParam) }
			val avg = times.average()
			val variance = times.map { (it - avg).toInt().absoluteValue }.average()
		} while (variance > avg / 20)
		println()
		
		var time = testable.test(initialParam)
		output(time, initialParam, initialPrecision)
		var param = initialParam
		var precision = initialPrecision
		do {
			if (doGC)
				System.gc()
			val plusTime = testable.test(param + param * precision, time)
			if (plusTime < time) {
				param += param / precision
				time = plusTime
				precision *= 2
				continue
			}
			if (doGC)
				System.gc()
			val minusTime = testable.test(param - param * precision, time)
			if (minusTime < time) {
				param -= param / precision
				time = minusTime
				precision *= 2
				continue
			}
			precision /= 2
			output(time, param, precision)
		} while (precision > 0.001)
		return param
	}
	
	private fun output(vararg values: Any) {
		val sb = StringBuilder("Time: " + values[0])
		if (values.size > 1) {
			sb.append(" - Parameter: ").append(values[1])
			if (values.size > 2)
				sb.append(" - Precision: ").append(values[2])
		}
		println(sb.toString())
	}
	
}
