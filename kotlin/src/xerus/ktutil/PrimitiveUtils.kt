@file:Suppress("NOTHING_TO_INLINE", "unused")

package xerus.ktutil

import java.util.*

// Booleans

/** @return 1 if true, 0 if false */
inline fun Boolean.toInt() = to(1, 0)

/** @return [ifTrue] when this is true, [ifFalse] when this is false */
inline fun <T> Boolean.to(ifTrue: T, ifFalse: T) = if (this) ifTrue else ifFalse

/** @return result of [ifTrue] when this is true, result of [ifFalse] when this is false */
inline fun <T> Boolean.to(ifTrue: () -> T, ifFalse: () -> T) = if (this) ifTrue() else ifFalse()

/** @return the [value] if true or else null */
inline fun <T> Boolean.ifTrue(value: T) = to(value, null)

/** @return result of [calc] if true or else null */
inline fun <T> Boolean.ifTrue(calc: () -> T) = to(calc, { null })

/** @return the [value] if false or else null */
inline fun <T> Boolean.ifFalse(value: T) = to(null, value)

/** @return result of [calc] if false or else null */
inline fun <T> Boolean.ifFalse(calc: () -> T) = to({ null }, calc)

// Numbers

inline val Double.square
	get() = this * this

private val powersOf10 = intArrayOf(1, 10, 100, 1000, 10000)
/** Rounds a Double to [digits] decimal places, by default 2. */
fun Double.round(digits: Int = 2): Double {
	if (digits < 5)
		return Math.rint(this * powersOf10[digits]) / powersOf10[digits]
	val c = Math.pow(10.0, digits.toDouble())
	return Math.rint(this * c) / c
}

/** Formats this Double to a String with [digits] decimal places using [String.format] */
fun Double.format(digits: Int, locale: Locale = Locale.ENGLISH) = "%.${digits}f".format(locale, this)

/** Turns this Long into a human-readable byte amount. */
fun Long.byteCountString(): String {
	val unit = 1024
	val exp = (Math.log(toDouble()) / Math.log(unit.toDouble())).toInt()
	val prefix = " KMGTPE"[Math.max(exp, 0)]
	return String.format("%.1f %sB", this / Math.pow(unit.toDouble(), exp.toDouble()), prefix)
}

// Statistics

/** @return The factorial of this Int */
tailrec fun Int.factorial(total: Double = 1.0): Double = if (this <= 1) total else (this - 1).factorial(total * this)

/** @return The factorial of this Int down to [downTo].
 * For example `5.factorial(3)` would return 60. */
tailrec fun Int.factorial(downTo: Int, total: Double = 1.0): Double = if (this <= downTo) total else (this - 1).factorial(downTo, total * this)

fun binominalCD(k: Int, n: Int, p: Double = 0.5): Double =
		(0..k).sumByDouble { binominalPD(it, n, p) }

fun binominalPD(k: Int, n: Int, p: Double = 0.5) =
		Math.pow(p, k.toDouble()) * Math.pow(1 - p, (n - k).toDouble()) * (n.factorial(k) / (n - k).factorial())