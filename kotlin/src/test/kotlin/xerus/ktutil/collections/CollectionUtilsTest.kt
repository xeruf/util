package xerus.ktutil.collections

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class CollectionUtilsTest: StringSpec({
	"joinEnumeration" {
		joinEnumeration() shouldBe ""
		joinEnumeration("test") shouldBe "test"
		joinEnumeration("test1", "test2", "test3") shouldBe "test1, test2 & test3"
	}
})