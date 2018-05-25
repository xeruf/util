package xerus.util.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.apache.commons.lang3.StringUtils.split;

public class PseudoParser {
	
	private Delimiters delimiters;
	
	public PseudoParser(char delimiter) {
		this(delimiter, delimiter);
	}
	
	public PseudoParser(char startDelimiter, char endDelimiter) {
		delimiters = new Delimiters(startDelimiter, endDelimiter);
	}
	
	public String parse(String toParse, Function<String,String> function) throws ParserException {
		List<String> split = delimiters.apply(toParse);
		
		StringBuilder out = new StringBuilder();
		boolean apply = false;
		for (String cur : split) {
			if (apply)
				try {
					cur = function.apply(cur);
				} catch(Exception e) {
					throw new ParserException(cur, e);
				}
			apply = !apply;
			out.append(cur);
		}
		return out.toString();
	}
	
	public Matcher createMatcher(String toParse, String... keys) throws ParserException {
		return new Matcher(delimiters, toParse, keys);
	}
	
	public static class Matcher {
		
		private final String[] intersections;
		private final int[] matchIndices;
		private final boolean uneven;
		
		public Matcher(Delimiters delimiters, String toParse, String... keys) throws ParserException {
			List<String> split = delimiters.apply(toParse);
			
			int size = split.size();
			uneven = size % 2 == 1;
			int s = size / 2;
			intersections = new String[s + size % 2];
			matchIndices = new int[s];
			
			List<String> keyList = Arrays.asList(keys);
			for (int i = 0; i < s; i += 2) {
				intersections[i] = split.get(i);
				final String cur = split.get(i + 1);
				int index = keyList.indexOf(cur);
				if (index == -1)
					throw new ParserException(cur);
				matchIndices[i] = index;
			}
			if (uneven)
				intersections[s] = split.get(size - 1);
		}
		
		public String apply(String... values) {
			final StringBuilder out = new StringBuilder();
			for (int i = 0; i < matchIndices.length; i++) {
				out.append(intersections[i]);
				out.append(values[matchIndices[i]]);
			}
			if (uneven)
				out.append(intersections[intersections.length - 1]);
			return out.toString();
		}
	}
	
	public static class Delimiters {
		
		private final char start;
		private final char end;
		
		public Delimiters(char start, char end) {
			this.start = start;
			this.end = end;
		}
		
		public static Delimiters create(char delimiter) {
			return new Delimiters(delimiter, delimiter);
		}
		
		public List<String> apply(String toSplit) {
			List<String> result = new ArrayList<>();
			if (toSplit.charAt(0) == start)
				result.add("");
			
			for (String s : split(toSplit, start)) {
				int ind = s.indexOf(end);
				if (ind != -1) {
					result.add(s.substring(0, ind));
					result.add(s.substring(ind + 1));
				} else
					result.add(s);
			}
			return result;
		}
		
	}
	
	public static class ParserException extends Exception {
		public final String match;
		ParserException(String msg) {
			super("Error while parsing " + msg);
			match = msg;
		}
		ParserException(String msg, Throwable t) {
			super("Error while parsing " + msg, t);
			match = msg;
		}
	}
	
}
