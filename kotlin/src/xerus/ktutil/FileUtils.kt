@file:Suppress("NOTHING_TO_INLINE", "unused")

package xerus.ktutil

import java.io.File
import java.io.RandomAccessFile

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
 * @return A String that should be a valid filename on the current operating system.
 * @see <a href="https://www.wikiwand.com/en/Filename#/Reserved_characters_and_words">Wikipedia: Filenames - Reserved characters and words</a>
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