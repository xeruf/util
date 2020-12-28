package xerus.ktutil.helpers

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec

class ParserTest: StringSpec({
	val parseString = "test%ab% test %cd%"
	
	"split by Delimiters" {
		Parser.Delimiters('%').apply(parseString) shouldBe listOf("test", "ab", " test ", "cd", "")
	}
	
	val resultString = "testrun test go"
	val parser = Parser('%')
	"Parser parse" {
		parser.parse(parseString) {
			return@parse when(it) {
				"ab" -> "run"
				"cd" -> "go"
				else -> throw NoWhenBranchMatchedException()
			}
		} shouldBe resultString
	}
	
	"Matcher" {
		shouldThrow<ParserException> { parser.createMatcher(parseString) }
		shouldThrow<ArrayIndexOutOfBoundsException> { parser.createMatcher(parseString, "ab", "cd").apply("run") }
		parser.createMatcher(parseString, "ab", "cd").apply("run", "go") shouldBe resultString
		parser.createMatcher(parseString, "cd", "ab").apply("go", "run") shouldBe resultString
	}
})
