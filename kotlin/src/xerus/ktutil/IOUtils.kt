@file:Suppress("NOTHING_TO_INLINE", "unused")

package xerus.ktutil

import java.io.*

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
fun Any.serializeToFile(file: File) {
	ObjectOutputStream(FileOutputStream(file)).use { oos ->
		oos.writeObject(this)
		oos.flush()
	}
}

/** Reads the content of this file to an object using an [ObjectInputStream]. The result will automatically be casted to the type parameter.
 * @throws ClassCastException if the read object does not fit the given type */
inline fun <reified T: Any> File.readSerializedObject(): T =
	ObjectInputStream(FileInputStream(this)).use { ois -> return ois.readObject() as T }
