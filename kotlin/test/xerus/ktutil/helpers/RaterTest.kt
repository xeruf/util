package xerus.ktutil.helpers

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec

class RaterTest: StringSpec({
	fun newRater() = Rater("Karlson vom Dach", 1.337)
	val rater = newRater()
	val slightlyHigherRater = newRater()
	slightlyHigherRater.points = 1.338
	
	"equals equivalent objects" {
		rater shouldBe newRater()
	}
	"hashCode equivalent objects" {
		rater.hashCode() shouldBe newRater().hashCode()
	}
	
	"equals slightly different points" {
		rater shouldNotBe slightlyHigherRater
	}
	"hashCode slightly different points" {
		rater.hashCode() shouldNotBe slightlyHigherRater.hashCode()
	}
	
	"toString"{
		rater.toString() shouldBe "Karlson vom Dach - Points: 1.34"
	}
	
	"update function: donut update when other points lower" {
		rater.update("Ajsoeim", 0.69) shouldBe false
		rater.obj shouldBe "Karlson vom Dach"
		rater shouldBe newRater()
	}
	"update function: update when other points higher" {
		rater.update("TOO", 4.20) shouldBe true
		rater.obj shouldBe "TOO"
		rater shouldNotBe newRater()
	}
	
	"update with other Rater" {
		val raterToUpdate = newRater()
		raterToUpdate.update(slightlyHigherRater) shouldBe true
		raterToUpdate shouldBe slightlyHigherRater
	}
	
	
	val invertedRater = Rater<String>(true)
	
	"inverted update" {
		invertedRater.update(slightlyHigherRater) shouldBe true
		invertedRater shouldBe slightlyHigherRater
		invertedRater.update(newRater()) shouldBe true
		invertedRater shouldBe newRater()
	}
	"inverted do not update" {
		invertedRater.update(slightlyHigherRater) shouldBe false
		invertedRater shouldBe newRater()
	}
})