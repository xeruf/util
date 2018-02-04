package xerus.util.tools;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.function.Consumer;

public final class FileTools {
	
	public static File findExisting(File f) {
		while (f != null && (!f.exists() || !f.canRead() || !f.isDirectory()))
			f = f.getParentFile();
		if (f == null)
			f = new File(System.getProperty("user.dir"));
		return f;
	}
	
	public static Path getPath(Object o) {
		if (o.getClass() == Path.class)
			return (Path) o;
		return Paths.get(o.toString());
	}
	
	public static String removeExtension(String filename) {
		int ind = filename.lastIndexOf(".");
		return ind == -1 ? filename : filename.substring(0, ind);
	}
	
	public static String attachFilename(String path, String attachment) {
		int dot = path.lastIndexOf(".");
		return path.substring(0, dot) + attachment + path.substring(dot);
	}
	
	/**
	 * moves Files/Directories recursively to the destination
	 * @return true if all Files were move successfully, false if an Error occurred
	 */
	public static void move(Path source, Path destination) throws IOException {
		File s = source.toFile();
		if (s.isDirectory()) {
			for (File file : s.listFiles())
				move(file.toPath(), destination.resolve(file.getName()));
			Files.delete(source);
		} else {
			Files.createDirectories(destination.getParent());
			Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
	// == READ INDEPENDENTLY ==
	
	/**
	 * reads the whole file, trying 3 times
	 * @return Array of the file's lines
	 */
	public static String[] readall(String file) {
		List<String> lines = readall(file, 3, false);
		return lines == null ? null : lines.toArray(new String[0]);
	}
	
	/**
	 * reads the whole file, tries up to {@code maxattempts} times
	 * @param maxattempts maximum number of attempts - if i is set to 0 it will try until it succeeds
	 * @return Array of the file's lines
	 */
	public static List<String> readall(String file, int maxattempts, boolean warn) {
		int attempts = 0;
		while (attempts < maxattempts || maxattempts == 0) {
			try {
				return Files.readAllLines(Paths.get(file));
			} catch(IOException e) {
				attempts++;
				if (warn)
					System.out.println(String.format("Could not access %s at attempt %s", file, attempts));
				Tools.sleep(20 * attempts);
			}
		}
		System.out.println(String.format("Could not access %s after %s attempts", file, attempts));
		return null;
	}
	
	public static long lines(String file) {
		while (true)
			try {
				return Files.lines(Paths.get(file)).count();
			} catch(IOException e) {
			}
	}
	
	private static Consumer<Exception> handler = e -> e.printStackTrace();
	
	public static void handle(Consumer<Exception> newHandler) {
		handler = newHandler;
	}
	
	/**
	 * reads a specific line from a document
	 * @param line line number, counting starts at 0
	 * @return specified line
	 */
	public static String read(String file, int line) {
		String ausgabe = null;
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			for (int i = 0; i < line + 1; i++)
				ausgabe = reader.readLine();
		} catch(IOException e) {
			handler.accept(e);
		}
		return ausgabe;
	}
	
	// == READ STEP-BY-STEP ==
	
	private static BufferedReader br;
	
	/**
	 * starts the reader on the given file
	 * @return true if successful, false if the file is not found
	 */
	public static boolean openFile(String file) {
		try {
			br = new BufferedReader(new FileReader(file));
			return true;
		} catch(FileNotFoundException e) {
			handler.accept(e);
			return false;
		}
	}
	
	/**
	 * reads a line from the current reader position
	 * @return next line
	 */
	public static String readln() {
		try {
			return br.readLine();
		} catch(IOException e) {
			handler.accept(e);
		}
		return null;
	}
	
	/**
	 * advances the reader
	 * @param steps amount of lines to advance
	 */
	public static void advancepos(int steps) {
		try {
			for (int i = 0; i < steps; i++)
				br.readLine();
		} catch(IOException e) {
			handler.accept(e);
		}
	}
	
	// == WRITE ==
	
	/**
	 * write {@code text} and a linebreak after each element into the given file
	 * @param file name of the file
	 * @param text the text to append
	 * @param append if the writer should append on an existing file
	 */
	public static boolean write(String file, boolean append, String... text) {
		try (Writer w = new FileWriter(file, append)) {
			for (String line : text)
				w.write(line + "\n");
			return true;
		} catch(IOException e) {
			handler.accept(e);
			return false;
		}
	}
	
	public static boolean write(String file, int line, String text) {
		String[] cur = readall(file);
		if (line > cur.length - 1)
			return write(file, true, repeat("\n", line - cur.length) + text);
		try (Writer w = new FileWriter(file)) {
			for (int i = 0; i < cur.length; i++) {
				if (i == line)
					writeln(w, text);
				else
					writeln(w, cur[i]);
			}
			return true;
		} catch(IOException e) {
			handler.accept(e);
			return false;
		}
	}
	
	public static String repeat(String s, int times) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < times; i++)
			sb.append(s);
		return sb.toString();
	}
	
	public static void writeln(Writer w, String text) throws IOException {
		w.write(text + System.lineSeparator());
	}
	
	public static void writeln(Writer w, Object text) throws IOException {
		writeln(w, text.toString());
	}
	
}
