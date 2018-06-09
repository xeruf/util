package xerus.util.helpers;

public abstract class Benchmark {
	
	public static void test(Runnable... torun) {
		for (Runnable method : torun) {
			System.gc();
			Timer.start();
			method.run();
			Timer.finish();
		}
	}
	
	public static void test(int param, Testable... torun) {
		for (Testable testable : torun) {
			System.gc();
			output(testable.test(param), param);
		}
	}
	
	public static int optimise(int param, Testable consumer) {
		int precision = 2;
		long time = consumer.test(param);
		output(time, param, precision);
		do {
			System.gc();
			long newTime = consumer.test(param + param / precision);
			if (newTime < time) {
				param += param / precision;
				time = newTime;
				continue;
			}
			newTime = consumer.test(param - param / precision);
			if (newTime < time) {
				param -= param / precision;
				time = newTime;
				continue;
			}
			precision *= 2;
			output(time, param, precision);
		} while (precision < 65);
		return param;
	}
	
	private static void output(long... values) {
		StringBuilder sb = new StringBuilder("Time: " + values[0]);
		if (values.length > 1) {
			sb.append("Parameter: ").append(values[1]);
			if (values.length > 2)
				sb.append("Precision: ").append(values[2]);
		}
		System.out.println(sb.toString());
	}
	
	public interface Testable {
		
		default long test(int param) {
			Timer.start();
			perform(param);
			return Timer.runtime();
		}
		
		void perform(int param);
		
	}
	
}
