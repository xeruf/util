package xerus.ktutil

/** Tries to find out how similar these two collections are. */
fun <T: Any> calculateSimilarity(c1: Collection<T>, c2: Collection<T>, algorithm: SimilarityAlgorithm<out T> = SimilarityAlgorithms.SorensenDice): Double =
	algorithm(c1, c2)

typealias SimilarityAlgorithm<T> = (Collection<T>, Collection<T>) -> Double

enum class SimilarityAlgorithms(val compare: SimilarityAlgorithm<*>): SimilarityAlgorithm<Any> {
	/** Divides the count of elements that each collection contains of the other by the amount of total elements in both collections. */
	SorensenDice({ c1: Collection<*>, c2: Collection<*> -> (c1.count { c2.contains(it) } + c2.count { c1.contains(it) }).toDouble() / (c1.size + c2.size) });
	
	fun calculateSimilarity(c1: Collection<*>, c2: Collection<*>) =
		compare(c1, c2)
	
	override fun invoke(p1: Collection<Any>, p2: Collection<Any>) =
		compare(p1, p2)
}
