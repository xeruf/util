package xerus.util.helpers;

import java.util.AbstractList;
import java.util.Arrays;

public class Row extends AbstractList<String> {
	
	private String[] data;
	private int size;
	
	public Row(int size, String... data) {
		this.data = data;
		this.size = size;
	}
	
	public Row(String... data) {
		this(data.length, data);
	}
	
	public String get(int col) {
		if (col >= data.length)
			return "";
		return data[col];
	}
	
	public String set(int col, String o) {
		String old = null;
		if (data.length < col)
			data = Arrays.copyOf(data, col + 1);
		else
			old = data[col];
		data[col] = o;
		return old;
	}
	
	@Override
	public int size() {
		return size;
	}
	
	@Override
	public String toString() {
		return "Row" + Arrays.toString(data);
	}
}