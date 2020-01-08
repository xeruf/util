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
		forAll(SimilarityAlgorithms.values()) {alg ->
			with(alg) {
				calculateSimilarity(focusAxisDefied, listOf("Hand", "Shoe")) shouldBe 0.0
				calculateSimilarity(focus, focus) shouldBe 1.0
				calculateSimilarity(focus03, focus) shouldBeGreaterThan calculateSimilarity(focus03, focusAxisDefied)
			}
		}
	}
	"duplicates" {
		calculateSimilarity(focus, focus + "Focus") shouldBeLessThan 1.0
	}
	"ordering" {
		calculateSimilarity(focus, focus.reversed()) shouldBeLessThan 1.0
	}
	"length" {
		calculateSimilarity(focus, focus03) shouldBeGreaterThan calculateSimilarity(focus, focusAxisDefied)
	}
})