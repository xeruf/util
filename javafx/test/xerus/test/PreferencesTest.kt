package xerus.test

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import xerus.ktutil.preferences.SettingsNode
import java.time.DayOfWeek

class PreferencesTest {
	val settings = SettingsNode(SettingsNode.getPreferences(this::class))
	@Test
	fun settingsNodeTest() {
		Assertions.assertEquals("/xerus/test", settings.preferences.absolutePath())
		settings.clear()
		settings.preferences.put("dayOfWeek", "TROLOLOL")
		val enumSettingBackend = settings.create("dayOfWeek", DayOfWeek.WEDNESDAY)
		val enumSetting = settings.create("dayOfWeek", DayOfWeek.THURSDAY)
		Assertions.assertEquals(DayOfWeek.THURSDAY, enumSetting())
		enumSettingBackend.set(DayOfWeek.MONDAY)
		enumSetting.refresh()
		Assertions.assertEquals(DayOfWeek.MONDAY, enumSetting())
	}
}