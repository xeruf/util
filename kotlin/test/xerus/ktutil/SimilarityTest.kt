package xerus.ktutil

import io.kotlintest.forAll
import io.kotlintest.matchers.doubles.shouldBeGreaterThan
import io.kotlintest.matchers.doubles.shouldBeLessThan
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class SimilarityTest: StringSpec({
	val focus = listOf("Chipzel", "Focus")
	val focus03 = listOf("03", "Focus")
	val focusAxisDefied = listOf("Chipzel", "Focus", "Axis", "Defied")
	"basic similarity" {
		forAll(SimilarityAlgorithms.values) {alg ->
			with(alg) {
				calculateSimilarity(focusAxisDefied, listOf("Hand", "Shoe")) shouldBe 0.0
				calculateSimilarity(focus, focus) shouldBe 1.0
				calculateSimilarity(focus03, focus) shouldBeGreaterThan calculateSimilarity(focus03, focusAxisDefied)
			}
		}
	}
	"duplicates" {
		calculateSimilarity(focus, focus + "Focus", SimilarityAlgorithms.RatcliffObershelp) shouldBeLessThan 1.0
	}
	"ordering" {
		calculateSimilarity(focus, focus.reversed(), SimilarityAlgorithms.RatcliffObershelp) shouldBeLessThan 1.0
	}
	// TODO fix similarity algorithm
	"length".config(enabled = false) {
		calculateSimilarity(focus, focus03) shouldBeGreaterThan calculateSimilarity(focus, focusAxisDefied)
	}
	"substrings".config(enabled = false) {
		val protostar = listOf("Protostar")
		val init = protostar + "<init>"
		calculateSimilarity(init, protostar + "init") shouldBeGreaterThan calculateSimilarity(init, protostar + "Titan")
	}
	
	"longestCommonSequence" {
		longestCommonSequence(focus, focus03) shouldBe Seq(1, 1, 1)
		longestCommonSequence(focus, focusAxisDefied) shouldBe Seq(0, 0, 2)
	}
	"RatcliffOberShelp" {
		SimilarityAlgorithms.RatcliffObershelp(focus, focus03) shouldBe 0.5
		SimilarityAlgorithms.RatcliffObershelp(focus, focusAxisDefied) shouldBe 4.0 / 6
	}
})
