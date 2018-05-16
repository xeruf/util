@file:Suppress("NOTHING_TO_INLINE")

package xerus.ktutil

import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

inline fun File.appendln(line: String) = appendText(line + "\n")

inline fun Path.exists() = Files.exists(this)
inline fun Path.renameTo(new: Path) = Files.move(this, new)
inline fun Path.delete() = Files.deleteIfExists(this)

inline fun Path.createFile() = Files.createFile(this)
inline fun Path.createDir() = Files.createDirectory(this)
inline fun Path.createDirs() = Files.createDirectories(this)

fun Path.moveRecursively(destination: Path) {
	val s = this.toFile()
	if (s.isDirectory) {
		for (file in s.listFiles())
			file.toPath().moveRecursively(destination.resolve(file.name))
		Files.delete(this)
	} else {
		Files.createDirectories(destination.parent)
		Files.move(this, destination, StandardCopyOption.REPLACE_EXISTING)
	}
}

fun File?.findFolder(): File {
	var file = this
	while (file != null && !(file.exists() && file.canRead() && file.isDirectory))
		file = file.parentFile
	return file ?: File(System.getProperty("user.dir"))
}

fun String.replaceIllegalFileChars() =
		replace(":", " -").replace('|', '-').replace('/', '-').trim()

/** Replaces the line [line] with [text].
 * If the document is too short, empty lines will be appended to extend it */
fun File.write(line: Int, text: String) {
	val lines = readLines()
	if (line >= lines.size) {
		appendText("\n".repeat(line - lines.size) + text)
	} else {
		val old = File("$name.old")
		renameTo(old)
		bufferedWriter().use { new ->
			lines.forEachIndexed { index, s ->
				new.appendln(if (index == line) text else s)
			}
		}
		old.delete()
	}
}

fun InputStream.dump() = bufferedReader().forEachLine { println(it) }

/** copies the contents of the InputStream to the OutputStream.
 * @param progressHandler is invoked every time after the buffer has been transferred.
 * If it returns true, then the copying will cease immediately.
 * @param closeIn if this InputStream should be closed when this method returns
 * @param closeOut if the OutputStream should be closed when this method returns */
fun InputStream.copyTo(out: OutputStream, closeIn: Boolean = false, closeOut: Boolean = false, progressHandler: (Long) -> Boolean): Long {
	try {
		val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
		var bytesCopied = 0L
		var bytes = read(buffer)
		while (bytes >= 0) {
			out.write(buffer, 0, bytes)
			bytesCopied += bytes
			if (progressHandler(bytesCopied))
				break
			bytes = read(buffer)
		}
		return bytesCopied
	} finally {
		if (closeIn)
			this.close()
		if (closeOut)
			out.close()
	}
}

fun writeObject(file: File, obj: Any) {
	ObjectOutputStream(FileOutputStream(file)).use { oos ->
		oos.writeObject(obj)
		oos.flush()
	}
}

@Suppress("Unchecked_cast")
inline fun <reified T : Any> readObject(file: File): T =
		ObjectInputStream(FileInputStream(file)).use { ois -> return ois.readObject() as T }

