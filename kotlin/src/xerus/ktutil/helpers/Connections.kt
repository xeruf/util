package xerus.ktutil.helpers

import xerus.ktutil.dump
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

fun HttpURLConnection.dumpResponse() = try {
	if (responseCode % 100 != 5) {
		for (e in headerFields.entries) {
			println(e)
		}
	}
	inputStream.dump()
} catch (e: IOException) {
	println(e)
	errorStream.dump()
}

object Connections {
	@Throws(MalformedURLException::class, IOException::class)
	fun createConnection(url: String): HttpURLConnection {
		val connection = URL(url).openConnection() as HttpURLConnection
		connection.setRequestProperty("Accept-Charset", "UTF-8")
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
		return connection
	}
	
	@Throws(IOException::class)
	fun post(connection: HttpURLConnection, vararg params: String): HttpURLConnection {
		connection.doOutput = true // Triggers POST
		connection.outputStream.use { output -> output.write(params.joinToString("&").toByteArray(charset("UTF-8"))) }
		return connection
	}
	
	@Throws(MalformedURLException::class, IOException::class)
	fun createPostConnection(url: String, vararg params: String) = post(createConnection(url), *params)
}

@Suppress("UNCHECKED_CAST")
open class HTTPQuery<out T>(vararg queries: String) {
	
	private val params: MutableMap<String, MutableList<String>>
	
	init {
		params = HashMap()
		addQueries(*queries)
	}
	
	fun removeQuery(key: String): T {
		params.remove(key)
		return this as T
	}
	
	fun replaceQuery(key: String, `val`: String): T {
		params.put(key, ArrayList(listOf(`val`)))
		return this as T
	}
	
	fun addQuery(key: String, vararg vals: String): T {
		params[key]?.addAll(vals) ?: params.put(key, vals.toMutableList())
		return this as T
	}
	
	fun addQueries(vararg queries: String): T {
		for (s in queries) {
			val ind = s.indexOf('=')
			addQuery(s.substring(0, ind), s.substring(ind + 1))
		}
		return this as T
	}
	
	fun joinQuery(other: HTTPQuery<*>): T {
		other.params.forEach { key, value -> addQuery(key, *value.toTypedArray()) }
		return this as T
	}
	
	fun getQuery(): String? = if (params.isEmpty()) null else params.map { e -> e.key + "=" + e.value.joinToString(",") }.joinToString("&")
	
	override fun toString() = getQuery().orEmpty()
	
}
