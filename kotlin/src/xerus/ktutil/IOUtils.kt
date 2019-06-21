@file:Suppress("NOTHING_TO_INLINE", "unused")

package xerus.ktutil

import java.io.*

// FILES

/**
 * Replaces characters in this String which are not permitted in filenames on the current OS and trims it.
 *
 * On all operating systems, it will replace '/' with '-' and trim,
 * on Mac it will additionally replace ':' with ' -'.
 *
 * On Windows, these actions will additionally be performed:
 * - Replace ':' with ' -'
 * - Replace '|', '\', '*' with '-'
 * - Remove '?', '"', '<', '>'
 *
 * See also: [Wikipedia: Filenames - Reserved_characters_and_words](https://www.wikiwand.com/en/Filename#/Reserved_characters_and_words)
 */
fun String.replaceIllegalFileChars() =
	when {
		SystemUtils.isWindows -> replace(":", " -")
			.replace('|', '-').replace('*', '-').replace('\\', '-')
			.filterNot { it in arrayOf('?', '<', '>', '"') }
		SystemUtils.isMac -> replace(":", " -")
		else -> this
	}.replace('/', '-').trim()

/** Appends the given [text] plus a newline to this [File]. */
inline fun File.appendLn(text: String) = appendText(text + "\n")

/** Finds the closest directory to this file that still exists by traversing the path upwards until an existing directory is found. If the file is null or reaches the root, the directory from which the JVM has been launched obtained from the system property "user.dir" is returned instead.
 * @return an existing directory as close as possible to the given one */
fun File?.findExistingDirectory(): File {
	var file = this
	while(file != null && !(file.exists() && file.canRead() && file.isDirectory))
		file = file.parentFile
	return file ?: File(System.getProperty("user.dir")).findExistingDirectory()
}

/** Makes writing on this file thread-safe by atomically checking and creating a backup file. Note that this only works if all accessors of this file use this method. After the operation is done, the backup file will be deleted.
 *
 * @param operation the action to perform on the file while it is locked */
fun <T> File.safe(operation: File.() -> T): T {
	val backup = resolveSibling("$name~")
	while(!backup.createNewFile())
		Thread.yield()
	if(this.exists())
		backup.writeText(readText())
	val result = operation(this)
	backup.delete()
	return result
}

/** Replaces the line [line] with [text].
 * If the document is too short, empty lines will be appended to extend it.
 *
 * @param line the line number where the text should be put
 * @param text the text to put on that line */
fun File.write(line: Int, text: String) {
	val lines = readLines()
	if(line >= lines.size) {
		appendText("\n".repeat(line - lines.size + hasNewline.to(0, 1)) + text)
	} else {
		val backup = File("$name.bak")
		renameTo(backup)
		bufferedWriter().use { new ->
			lines.forEachIndexed { index, s ->
				new.appendln(if(index == line) text else s)
			}
		}
		backup.delete()
	}
}

/** Checks whether this file has a newline character at the end. */
val File.hasNewline: Boolean
	get() {
		RandomAccessFile(this, "r").use {
			val fileLength = it.length() - 1
			if(fileLength < 0)
				return true
			it.seek(fileLength)
			val lastByte = it.readByte().toInt()
			return lastByte == 0xA || lastByte == 0xD
		}
	}

// STREAMS

/** Dumps the content of this InputStream to the console. Please note that this method blocks until the Stream is closed. */
fun InputStream.dump() = bufferedReader().forEachLine { println(it) }

/** Copies the contents of this InputStream to the OutputStream [out].
 * @param progressHandler is invoked every time after the buffer has been transferred.
 * If it returns true, then the copying will cease immediately.
 * @param closeIn if this InputStream should be closed when this method returns
 * @param closeOut if the OutputStream should be closed when this method returns */
fun InputStream.copyTo(out: OutputStream, closeIn: Boolean = false, closeOut: Boolean = false, progressHandler: (Long) -> Boolean): Long {
	try {
		val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
		var bytesCopied = 0L
		var bytes = read(buffer)
		while(bytes >= 0) {
			out.write(buffer, 0, bytes)
			bytesCopied += bytes
			if(progressHandler(bytesCopied))
				break
			bytes = read(buffer)
		}
		return bytesCopied
	} finally {
		if(closeIn)
			this.close()
		if(closeOut)
			out.close()
	}
}

// SERIALIZATION

/** Serializes this object to the given [file] via an [ObjectOutputStream]. */
fun Any.writeToFile(file: File) {
	ObjectOutputStream(FileOutputStream(file)).use { oos ->
		oos.writeObject(this)
		oos.flush()
	}
}

/** Reads the content of this file to an object using an [ObjectInputStream]. The result will automatically be casted to the type parameter.
 * @throws ClassCastException if the read object does not fit the given type */
inline fun <reified T: Any> File.readToObject(): T =
	ObjectInputStream(FileInputStream(this)).use { ois -> return ois.readObject() as T }
