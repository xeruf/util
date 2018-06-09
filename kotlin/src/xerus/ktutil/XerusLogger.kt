package xerus.ktutil

import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.logging.*
import java.util.prefs.Preferences

object XerusLogger : Logger("xerus.xeruslogger", null) {
	
	init {
		level = Level.CONFIG
		addHandler(object : Handler() {
			init {
				formatter = ShortFormatter()
			}
			
			override fun flush() = System.out.flush()
			override fun close() {}
			
			override fun publish(record: LogRecord) {
				val msg = formatter.format(record)
				if (record.thrown != null)
					System.err.print(msg)
				else
					System.out.print(msg)
				flush()
			}
		})
		useParentHandlers = false
		LogManager.getLogManager().addLogger(this)
	}
	
	var debugPrefix: String = "DEBUG"
	fun debug(msg: Any?) {
		log(Level.SEVERE, debugPrefix + " " + msg.toString())
	}
	
	fun logLines(level: Level, vararg lines: String) {
		log(level, lines.joinToString("\t" + System.lineSeparator()))
	}
	
	override fun throwing(sourceClass: String?, sourceMethod: String, thrown: Throwable) {
		if (sourceClass != null)
			format(Level.WARNING, "Method %s in Class %s threw %s", sourceMethod, sourceClass, thrown.toString())
		else
			format(Level.WARNING, "%s threw %s", sourceMethod, thrown.toString())
		log(Level.CONFIG, "THROW", thrown)
	}
	
	fun format(loglevel: Level, string: String, vararg args: Any) {
		log(loglevel, String.format(string, *args))
	}
	
	private class ShortFormatter : Formatter() {
		
		override fun format(record: LogRecord): String {
			return if (record.thrown != null) {
				String.format("%s EXCEPTION %s%s", formattedTime(), record.thrown.getStackTraceString(), System.getProperty("line.separator"))
			} else String.format("%s %-7s %s%s", formattedTime(), record.level.toString(), formatMessage(record), System.getProperty("line.separator"))
		}
		
	}
	
	// Logger creation
	
	fun parseArgs(vararg args: String, defaultLevel: String = "config"): XerusLogger {
		val prefs = suppressErr { Preferences.userRoot().node("/xerus") }
		var default = prefs.get("loglevel", defaultLevel)
		
		if (args.size > 1 && args[1] == "save") {
			prefs.put("loglevel", args[0].toLowerCase())
			default = args[0]
		}
		invoke(if (args.isNotEmpty() && args[0].isNotBlank()) args[0] else default)
		logLines(Level.CONFIG,
				"This application can be launched from console using \"java -jar %jarname%.jar %LogLevel%\" (wrapped in % signs are placeholders that should be replaced by their appropriate value)",
				"LogLevel can be one of: OFF, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST. Appending the argument \"save\" will result in the given LogLevel becoming the default, which is currently ${default.toUpperCase()}")
		return this
	}
	
	operator fun invoke(level: String) {
		try {
			val logLevel = Level.parse(level.toUpperCase())
			setLevel(logLevel)
			config("LogLevel set to $logLevel")
		} catch (e: IllegalArgumentException) {
			System.err.println(level + " is not a valid Logger Level!")
		}
	}
	
	// Formatting
	
	val logFiles = HashMap<File, Handler>(1)
	fun logToFile(f: File, log: Boolean = true, append: Boolean = true) {
		val file = f.absoluteFile
		if (log) {
			if (!logFiles.contains(file)) {
				logFiles.put(file, addOutputStream(FileOutputStream(file, append)))
			}
		} else {
			removeHandler(logFiles[file])
		}
	}
	
	fun addOutputStream(out: OutputStream) =
		createHandler(out).also { addHandler(it) }
	
	private fun createHandler(out: OutputStream): StreamHandler {
		val handler = object : StreamHandler(out, ShortFormatter()) {
			override fun publish(record: LogRecord) {
				super.publish(record)
				flush()
			}
		}
		handler.level = Level.ALL
		return handler
	}
	
}