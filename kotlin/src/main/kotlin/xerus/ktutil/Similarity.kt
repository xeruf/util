package xerus.ktutil

import java.util.*

/** Tries to find out how similar these two collections are. */
fun <C: Collection<*>> calculateSimilarity(c1: C, c2: C, algorithm: SimilarityAlgorithm<C> = SimilarityAlgorithms.SorensenDice): Double =
	algorithm(c1, c2)

typealias SimilarityAlgorithm<C> = (C, C) -> Double

class SimilarityAlg<in C: Collection<*>>(val compare: SimilarityAlgorithm<C>): SimilarityAlgorithm<C> {
	
	fun calculateSimilarity(c1: C, c2: C) =
		compare(c1, c2)
	
	override fun invoke(p1: C, p2: C): Double =
		compare(p1, p2)
}

object SimilarityAlgorithms {
	/** Divides the count of elements that each collection contains of the other by the amount of total elements in both collections. */
	val SorensenDice = SimilarityAlg { c1: Collection<*>, c2: Collection<*> -> (c1.count { c2.contains(it) } + c2.count { c1.contains(it) }).toDouble() / (c1.size + c2.size) }
	val RatcliffObershelp = SimilarityAlg { c1: List<*>, c2: List<*> ->
		val seqs = ArrayDeque<Pair<List<*>, List<*>>>()
		seqs.add(Pair(c1, c2))
		var matches = 0
		while(seqs.size > 0) {
			val seqPair = seqs.pop()
			val commonSeq = longestCommonSequence(seqPair.first, seqPair.second) ?: continue
			matches += commonSeq.len
			seqs.add(Pair(c1.subList(0, commonSeq.ind1), c2.subList(0, commonSeq.ind2)))
			seqs.add(Pair(c1.subList(commonSeq.ind1 + commonSeq.len, c1.size), c2.subList(commonSeq.ind2 + commonSeq.len, c2.size)))
		}
		matches * 2.0 / (c1.size + c2.size)
	}
	val values = arrayListOf(SorensenDice, RatcliffObershelp)
}

data class Seq(val ind1: Int, val ind2: Int, val len: Int)
fun <T> longestCommonSequence(c1: List<T>, c2: List<T>): Seq? {
	var longestSequence = Seq(-1, -1, 0)
	c1.forEachIndexed { ind1, elem1 ->
		c2.forEachIndexed { ind2, elem2 ->
			if(elem1 == elem2) {
				var len = 1
				val maxLen = kotlin.math.min(c1.size - ind1, c2.size - ind2)
				while(len < maxLen && c1[ind1 + len] == c2[ind2 + len])
					len++
				if(len > longestSequence.len)
					longestSequence = Seq(ind1, ind2, len)
			}
		}
	}
	return if(longestSequence.len > 0) longestSequence else null
}