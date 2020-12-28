package xerus.ktutil.helpers

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import xerus.ktutil.FieldNotFoundException

data class TestClass(val string: String, val int: Int): Parsable

class ParsableTest: StringSpec({
	val testObject = TestClass("test", 17)
	
	"getField" {
		testObject.getField("string") shouldBe "test"
		testObject.getField("int") shouldBe 17
	}
	
	"toString" {
		testObject.toString("%string%run %int%") shouldBe "testrun 17"
		testObject.toString("number %int%") shouldBe "number 17"
	}
	
	"toString Exceptions" {
		val ex = shouldThrow<FieldNotFoundException> {
			testObject.toString("%hi%")
		}
		ex.fieldName shouldBe "hi"
		ex.clazz shouldBe TestClass::class.java
	}
})