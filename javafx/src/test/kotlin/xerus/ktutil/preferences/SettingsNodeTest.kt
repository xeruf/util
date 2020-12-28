package xerus.ktutil.preferences

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.time.DayOfWeek

class SettingsNodeTest : StringSpec({
	val settings = SettingsNode(SettingsNode.getPreferences(this::class))
	"/xerus/ktutil/preferences" shouldBe settings.preferences.absolutePath()
	settings.clear()
	
	settings.preferences.put("dayOfWeek", "TROLOLOL")
	val enumSetting1 = settings.create("dayOfWeek", DayOfWeek.WEDNESDAY)
	val enumSetting2 = settings.create("dayOfWeek", DayOfWeek.THURSDAY)
	
	"construct settings successfully" {
		settings.settings.elementCount shouldBe 2
	}
	
	"enumSettings have default value because loaded value is invalid" {
		enumSetting1() shouldBe DayOfWeek.WEDNESDAY
		enumSetting2() shouldBe DayOfWeek.THURSDAY
	}
	
	"enumSetting set value" {
		enumSetting1.set(DayOfWeek.MONDAY)
		enumSetting1() shouldBe DayOfWeek.MONDAY
	}
	"enumSetting reload value from store" {
		enumSetting2() shouldBe DayOfWeek.THURSDAY
		enumSetting2.refresh()
		enumSetting2() shouldBe DayOfWeek.MONDAY
	}
	
	"enumSetting reset to default after clearing settings" {
		settings.clear()
		enumSetting1() shouldBe DayOfWeek.WEDNESDAY
		enumSetting2() shouldBe DayOfWeek.THURSDAY
	}
})