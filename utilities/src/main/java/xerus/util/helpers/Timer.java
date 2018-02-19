package xerus.util.helpers;

/** times in milliseconds */
public class Timer {

	private static final boolean isNano = true;

	private static Timer t;
	private long time;
	
	public Timer() {
		restart();
	}
	
	public static void start() {
		t = new Timer();
	}

	public void restart() { time = System.currentTimeMillis(); }

	/** elapsed time in milliseconds */
	public static long runtime() {
		return t.time();
	}

	/** elapsed time in milliseconds */
	public long time() {
		return System.currentTimeMillis() - time;
	}
	
	public static long finish() {
		return finish("Time");
	}
	
	public static long finish(String msg) {
		if(t == null)
			return 0;
		long time = runtime();
		t = null;
		System.out.println(parseTime(msg, time));
		return t.printTime(msg);
	}

	public long printTime(String msg) {
		long time = time();
		System.out.println(parseTime(msg, time));
		return time;
	}
	
	public static String parseTime(String msg, long time) {
		StringBuilder res = new StringBuilder(msg).append(": ");
		if(isNano)
			time /= 1000000;
		if(time < 10000)
			res.append(time).append("m");
		else
			res.append((time/100) / 10.0);
		return res.append("s").toString();
	}

}
