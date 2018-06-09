package xerus.util;

import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public interface Setting {
	
	/** associates the given String with this field within the Preferences */
	default void put(String value) {
		getPrefs().put(getName(), value);
	}
	
	/** associates the given Objects toString() with this field within the Preferences */
	default void put(Object value) {
		getPrefs().put(getName(), value.toString());
	}
	
	/** returns the String associated with this field within the Preferences */
	default String get() {
		return getPrefs().get(getName(), getDefault());
	}
	
	/** returns the boolean associated with this field within the Preferences */
	default boolean getBool() {
		return Boolean.valueOf(get());
	}
	
	/** returns the int associated with this field within the Preferences */
	default int asInt() {
		try {
			return Integer.valueOf(get());
		} catch(NumberFormatException e) {
			return Integer.valueOf(getDefault());
		}
	}
	
	// MULTI MECHANICS!
	
	String multiSeparator = ";";
	
	default void putMulti(String key, boolean value) {
		String cur = get();
		if (value)
			if (cur.isEmpty())
				put(key);
			else
				put(cur + multiSeparator + key);
		else {
			ArrayList<String> result = new ArrayList<>();
			for (String s : cur.split(multiSeparator)) {
				if (!s.contains(key))
					result.add(s);
			}
			put(String.join(multiSeparator, result));
		}
	}
	
	default void putMulti(String... values) {
		put(String.join(multiSeparator, values));
	}
	
	default boolean getMulti(String key) {
		return get().contains(key);
	}
	
	default String[] getAll() {
		return get().split(multiSeparator);
	}
	
	default void clear() throws BackingStoreException {
		getPrefs().clear();
	}
	
	Preferences getPrefs();
	
	String getName();
	
	String getDefault();
	
	/* DEFAULT IMPLEMENTATION
	public enum Settings implements Setting

	static final Preferences PREFS;
	static {
		PREFS = Preferences.userNodeForPackage(Settings.class);
	}

	private final String name;
	private final String defaultVal;

	Settings(String name) {
		this(name, "");
	}

	Settings(String name, Object defaultValue) {
		this.name = name;
		defaultVal = defaultValue.toString();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDefault() {
		return defaultVal;
	}

	@Override
	public Preferences getPrefs() {
		return PREFS;
	}
	*/
	
}
