package xerus.util.tools;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public final class ConnectionTools {
	
	public static HttpURLConnection createConnection(String url) throws MalformedURLException, IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestProperty("Accept-Charset", "UTF-8");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		return connection;
	}
	
	public static HttpURLConnection Post(HttpURLConnection connection, String... params) throws IOException {
		connection.setDoOutput(true); // Triggers POST.
		try (OutputStream output = connection.getOutputStream()) {
			output.write(String.join("&", params).getBytes("UTF-8"));
		}
		return connection;
	}
	
	public static HttpURLConnection createPostConnection(String url, String... params) throws MalformedURLException, IOException {
		return Post(createConnection(url), params);
	}
	
	public static void dumpResponse(HttpURLConnection connection) {
		try {
			if (connection.getResponseCode() % 100 != 5) {
				for (Entry<String, List<String>> e : connection.getHeaderFields().entrySet()) {
					System.out.println(e);
				}
			}
			Tools.dumpStream(connection.getInputStream());
		} catch (IOException e1) {
			Tools.dumpStream(connection.getErrorStream());
		}
	}
	
	public static class HTTPQuery<T extends HTTPQuery> {
		
		private Map<String, List<String>> query;
		
		public HTTPQuery(String... queries) {
			query = new HashMap<>();
			addQueries(queries);
		}
		
		public T removeQuery(String key) {
			query.remove(key);
			return (T) this;
		}
		
		public T replaceQuery(String key, String val) {
			query.put(key, new ArrayList<>(Collections.singletonList(val)));
			return (T) this;
		}
		
		public T addQuery(String key, String... vals) {
			List<String> val = Arrays.asList(vals);
			if (query.containsKey(key))
				query.get(key).addAll(val);
			else
				query.put(key, new ArrayList<>(val));
			return (T) this;
		}
		
		public T addQueries(String... queries) {
			for (String s : queries) {
				int ind = s.indexOf('=');
				addQuery(s.substring(0, ind), s.substring(ind + 1));
			}
			return (T) this;
		}
		
		public T joinQuery(HTTPQuery<? extends HTTPQuery> other) {
			other.query.forEach((key, value) -> addQuery(key, value.toArray(new String[0])));
			return (T) this;
		}
		
		public String getQuery() {
			if (query.isEmpty())
				return "";
			return query.entrySet().stream().
					map(e -> e.getKey() + "=" + String.join(",", e.getValue())).collect(Collectors.joining("&"));
		}
		
		public String toString() {
			return getQuery();
		}
		
	}
	
}
