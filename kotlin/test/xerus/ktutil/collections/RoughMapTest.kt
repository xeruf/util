package xerus.ktutil.collections

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

internal class RoughMapTest : StringSpec({
	"toString" {
		val map = RoughMap<String>()
		map.put("hi1", "hi")
		map.put("hi2", "hai")
		map.put("hi3", "")
		map.toString() shouldBe "RoughMap{hi1=hi, hi2=hai, hi3=}"
	}
})