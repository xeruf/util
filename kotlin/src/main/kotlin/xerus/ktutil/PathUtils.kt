@file:Suppress("NOTHING_TO_INLINE", "unused")

package xerus.ktutil

import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileAttribute

/** Tests whether this file exists.
 * @see Files.exists */
inline fun Path.exists(vararg options: LinkOption) = Files.exists(this, *options)

/** Move or rename this file to a target file.
 * @return the path to the [target] file
 * @see Files.move */
inline fun Path.renameTo(target: Path, vararg options: CopyOption): Path = Files.move(this, target, *options)

/** Deletes this file if it exists.
 * @return true if the file was deleted by this method
 * @see Files.deleteIfExists */
inline fun Path.delete() = Files.deleteIfExists(this)

/** Creates a new and empty file, failing if the file already exists.
 * @return the created file
 * @see Files.createFile */
inline fun Path.createFile(vararg attrs: FileAttribute<*>) = Files.createFile(this, *attrs)

/** Creates a new directory, failing if it already exists.
 * @return the created directory
 * @see Files.createDirectory */
inline fun Path.createDir(vararg attrs: FileAttribute<*>) = Files.createDirectory(this, *attrs)

/** Creates a directory by creating all nonexistent parent directories first. An exception is only thrown if the target already exists but is a file.
 * @return the directory
 * @see Files.createDirectories */
inline fun Path.createDirs(vararg attrs: FileAttribute<*>) = Files.createDirectories(this, *attrs)

/** Moves this [Path] and all of its content recursively into [target]. Empty directories are not copied and files in the [target] directory will by default be overridden.
 * @param target the [Path] to copy all content into
 * @param options options for copying the file - defaults to [StandardCopyOption.REPLACE_EXISTING]*/
fun Path.moveRecursively(target: Path, vararg options: CopyOption = arrayOf(StandardCopyOption.REPLACE_EXISTING)) {
	if(Files.isDirectory(this)) {
		Files.list(this).forEach { it.moveRecursively(target.resolve(it.fileName)) }
		Files.delete(this)
	} else {
		Files.createDirectories(target.parent)
		Files.move(this, target, *options)
	}
}

/** Deletes the contents of this [Path] recursively using [Files.walkFileTree]. */
fun Path.deleteRecursively() {
	Files.walkFileTree(this, object: SimpleFileVisitor<Path>() {
		override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
			file.delete()
			return FileVisitResult.CONTINUE
		}
		
		override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
			dir.delete()
			if(exc != null)
				throw exc
			return FileVisitResult.CONTINUE
		}
	})
}