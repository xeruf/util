package xerus.ktutil.helpers

/** Keeps only the top [size] elements, judging by the [comparator] */
class TopList<I, T>(val top: Array<T?>, val ratings: Array<I>, val comparator: (I, I) -> Boolean) {
	
	/*companion object {
		inline fun <reified I: Comparable<I>, reified T> byComparable(size: Int) = TopList<I, T>(arrayOfNulls(size), { o1, o2 -> o1 > o2 })
		inline fun <reified I: Comparable<I>, reified T> byComparator(size: Int, noinline comparator: (I, I) -> Boolean) = TopList<I, T>(arrayOfNulls(size), comparator)
	}*/
}