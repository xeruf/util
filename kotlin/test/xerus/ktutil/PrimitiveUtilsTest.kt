package xerus.ktutil

import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.kotlintest.tables.row
import java.util.*

class PrimitiveUtilsTest: StringSpec({
	Locale.setDefault(Locale.ENGLISH)
	
	"byteCountString" {
		1L.byteCountString() shouldBe "1 B"
		9423L.byteCountString() shouldBe "9.2 KiB"
		942942394239423L.byteCountString() shouldBe "857.6 TiB"
	}
	
	"factorial" {
		forall(
			row(3, 6.0),
			row(5, 120.0),
			row(8, 40320.0),
			row(10, 3628800.0)
		) { parameter, result ->
			parameter.factorial() shouldBe result
		}
	}
})