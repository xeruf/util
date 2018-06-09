package xerus.ktutil

import java.io.*

fun InputStream.dump() = bufferedReader().forEachLine { println(it) }

// fixme this shouldn't be inlined, see https://youtrack.jetbrains.com/issue/KT-22222
/** copies the contents of the InputStream to the OutputStream.
 * @param progressHandler is invoked every time after the buffer has been transferred. If it returns true, then the copying will cease immediately
 * @param closeIn if this InputStream should be closed when this method returns
 * @param closeOut if the OutputStream should be closed when this method returns */
inline fun InputStream.copyTo(out: OutputStream, closeIn: Boolean = false, closeOut: Boolean = false, progressHandler: (Long) -> Boolean): Long {
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

