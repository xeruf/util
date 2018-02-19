package xerus.ktutil

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

fun File.appendln(line: String) = appendText(line + "\n")

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

fun Path.exists() = Files.exists(this)
fun Path.create(): Path = Files.createDirectories(this)

fun findFolder(start: File?): File {
	var file = start
	while (file != null && !(file.exists() && file.canRead() && file.isDirectory))
		file = file.parentFile
	return file ?: File(System.getProperty("user.dir"))
}

fun String.replaceIllegalFileChars() =
		replace(":", " -").replace('|', '-').replace('/', '-').trim()