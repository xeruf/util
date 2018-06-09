package xerus.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class SysoutListener {
	
	private static ArrayList<SysoutObserver> observers = new ArrayList<>();
	
	static {
		coverSysout();
	}
	
	public static void addObserver(SysoutObserver observer) {
		if (observers == null) {
			observers = new ArrayList<>();
			coverSysout();
		}
		observers.add(observer);
	}
	
	public static void removeObserver(SysoutObserver observer) {
		if (observers == null)
			return;
		observers.remove(observer);
	}
	
	private static void fireSysout(String message) {
		for (SysoutObserver observer : observers)
			observer.handle(message + "\n");
	}
	
	private static void coverSysout() {
		System.setOut(new PrintStream(System.out) {
			private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			@Override
			public void write(int b) {
				super.write(b);
				buffer.write(b);
				if (b == '\n') {
					fire();
				}
			}
			@Override
			public void println(String s) {
				super.println(s);
				fireSysout(buffer.toString() + s);
				buffer.reset();
			}
			@Override
			public void println() {
				super.println();
				fire();
			}
			private void fire() {
				fireSysout(buffer.toString());
				buffer.reset();
			}
		});
	}
	
	public interface SysoutObserver {
		void handle(String message);
	}
	
}