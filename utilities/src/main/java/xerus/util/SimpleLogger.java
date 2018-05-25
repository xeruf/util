package xerus.util;

import xerus.util.tools.Tools;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.OutputStream;
import java.util.logging.*;
import java.util.prefs.Preferences;

@SuppressWarnings("unused")
public class SimpleLogger extends Logger {
	
	private SimpleLogger() {
		super("sysout", null);
	}
	
	public static String debugPrefix;
	
	public void debug(String msg) {
		log(Level.SEVERE, debugPrefix + " " + msg);
	}
	
	@Override
	public void throwing(String sourceClass, String sourceMethod, Throwable thrown) {
		format(Level.WARNING, "Method %s in Class %s threw", sourceMethod, sourceClass, thrown.toString());
		super.throwing(sourceClass, sourceMethod, thrown);
	}
	
	public void format(Level loglevel, String string, Object... args) {
		log(loglevel, String.format(string, args));
	}
	
	// Logger creation
	
	private static SimpleLogger logger;
	
	public static SimpleLogger parseArgs(String... args) {
		final Preferences prefs = Tools.suppressErr(() -> Preferences.userRoot().node("/xerus"));
		String defaultLevel = prefs.get("loglevel", "CONFIG");
		
		if (args.length > 1 && args[1].equals("save")) {
			prefs.put("loglevel", args[0].toLowerCase());
			defaultLevel = args[0];
		}
		logger = getLogger(args.length > 0 ? args[0] : defaultLevel);
		logger.config(String.join(System.lineSeparator()
				, "This application can be launched from console using \"java -jar %jarname%.jar %LogLevel%\" (wrapped in % signs are placeholders that should be replaced by their appropriate value)"
				, "LogLevel can be one of: OFF, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST. The additional argument \"save\" will result in the given LogLevel becoming the new default."
				, "The default LogLevel is currently " + defaultLevel));
		return logger;
	}
	
	/** gets the existing Logger if already created or else creates a new one at Level.CONFIG */
	public static SimpleLogger getLogger() {
		return logger != null ? logger : getLogger(Level.CONFIG, true);
	}
	
	/** parses the given String value to a Logger Level (ignoring case) and returns the SimpleLogger with the given Level */
	public static SimpleLogger getLogger(String level) {
		Level logLevel;
		try {
			logLevel = Level.parse(level.toUpperCase());
		} catch(IllegalArgumentException e) {
			System.err.println(level + " is not a valid Logger Level. Assuming CONFIG.");
			logLevel = Level.CONFIG;
		}
		return getLogger(logLevel, true);
	}
	
	/**
	 * creates or returns the SimpleLogger
	 * @param loglevel if the Logger exists, it will be set to this level and returned, else a new one will be created at this Level
	 * @param toSysout if a Handler to System.out should be added to a newly created logger
	 */
	public static SimpleLogger getLogger(Level loglevel, boolean toSysout) {
		if (logger != null) {
			logger.setLevel(loglevel);
			logger.config("Logger set to level " + loglevel);
			return logger;
		}
		logger = new SimpleLogger();
		logger.setLevel(loglevel);
		if (toSysout)
			logger.addHandler(createHandler(System.out));
		logger.setUseParentHandlers(false);
		LogManager.getLogManager().addLogger(logger);
		logger.config("Logger created at level " + loglevel);
		return logger;
	}
	
	// Formatting
	
	public static void addOutputstream(OutputStream out) {
		logger.addHandler(createHandler(out));
	}
	
	private static StreamHandler createHandler(OutputStream out) {
		StreamHandler handler = new StreamHandler(out, new ShortFormatter()) {
			@Override
			public synchronized void publish(final LogRecord record) {
				super.publish(record);
				flush();
			}
		};
		handler.setLevel(Level.ALL);
		return handler;
	}
	
	private static class ShortFormatter extends Formatter {
		
		//Color curColor;
		
		@Override
		public String format(LogRecord record) {
			if (record.getThrown() != null) {
				return String.format("%s EXCEPTION %s", Tools.time(), ExceptionUtils.getStackTrace(record.getThrown()));
			}
			String res = String.format("%s %-7s %s%s", Tools.time(), record.getLevel().toString(), formatMessage(record), System.getProperty("line.separator"));
			/*int level = record.getLevel().intValue();
			Color color = null;
			if (level > 800)
				color = level > 900 ? Color.RED : Color.YELLOW;
			if (level < 500)
				color = Color.BLACK;
			if (color != curColor) {
				curColor = color;
				if (color == null)
					res = ansi().reset().a(res).toString();
				else
					res = ansi().fgBright(color).a(res).toString();
			}*/
			return res;
		}
		
	}
	
}
