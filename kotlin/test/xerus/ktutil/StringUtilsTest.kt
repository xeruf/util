package xerus.ktutil

import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.matchers.doubles.shouldBeGreaterThan
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.time.LocalDate

class StringUtilsTest: StringSpec({
	"containsAny" {
		"hi".containsAny("HA", "HO") shouldBe false
		"ho".containsAny("HA", "HI", "Hollow", "Ham") shouldBe false
		"Hello".containsAny("HA", "HI", "he", "Ham") shouldBe true
	}
	"containsEach" {
		"hello".containsEach("hella") shouldBe false
		"hello".containsEach("Hello there!") shouldBe true
		"Great!".containsEach("at!") shouldBe true
	}
	"toLocalDate" {
		"2000-12-24".toLocalDate() shouldBe LocalDate.of(2000, 12, 24)
	}
	val focus = listOf("03", "Focus")
	val focusAxisDefied = listOf("Chipzel", "Focus", "Axis", "Defied")
	"splitTitleTrimmed" {
		"Chipzel - Focus (Axis Defied Remix)".splitTitleTrimmed() shouldContainExactly focusAxisDefied
		"01 Devotion".splitTitleTrimmed() shouldContainExactly listOf("01", "Devotion")
		"Song (ft Artist)".splitTitleTrimmed() shouldContainExactly listOf("Song", "Artist")
		"Artist Feat. featu - Song".splitTitleTrimmed() shouldContainExactly listOf("Artist", "featu", "Song")
	}
	"findSimilarity" {
		findSimilarity(focus, listOf("Chipzel", "Focus")) shouldBeGreaterThan findSimilarity(focus, focusAxisDefied)
		findSimilarity(focusAxisDefied, listOf("Hand", "Shoe")) shouldBe 0.0
		findSimilarity(focus, focus) shouldBe 1.0
	}
})