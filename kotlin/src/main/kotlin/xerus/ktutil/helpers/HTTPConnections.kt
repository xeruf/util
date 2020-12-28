package xerus.ktutil.helpers

import xerus.ktutil.dump
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

fun HttpURLConnection.dumpResponse() = try {
	if(responseCode % 100 != 5) {
		for(e in headerFields.entries) {
			println(e)
		}
	}
	inputStream.dump()
} catch(e: IOException) {
	println(e)
	errorStream.dump()
}

fun URL.createConnection(): HttpURLConnection {
	val connection = openConnection() as HttpURLConnection
	connection.setRequestProperty("Accept-Charset", "UTF-8")
	connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
	return connection
}

fun URL.createPostConnection(vararg params: String) = createConnection().post(*params)

fun HttpURLConnection.post(vararg params: String): HttpURLConnection {
	doOutput = true // Triggers POST
	requestMethod = "POST"
	outputStream.use { output -> output.write(params.joinToString("&").toByteArray(charset("UTF-8"))) }
	return this
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
	
	fun replaceQuery(key: String, value: String): T {
		params[key] = mutableListOf(value)
		return this as T
	}
	
	fun addQuery(key: String, vararg values: String): T {
		params[key]?.addAll(values) ?: params.put(key, values.toMutableList())
		return this as T
	}
	
	fun addQueries(vararg queries: String): T {
		for(s in queries) {
			val ind = s.indexOf('=')
			addQuery(s.substring(0, ind), s.substring(ind + 1))
		}
		return this as T
	}
	
	fun joinQuery(other: HTTPQuery<*>): T {
		other.params.forEach { key, value -> addQuery(key, *value.toTypedArray()) }
		return this as T
	}
	
	fun getQuery(): String? = if(params.isEmpty()) null else params.map { e -> e.key + "=" + e.value.joinToString(",") }.joinToString("&")
	
	override fun toString() = getQuery().orEmpty()
	
}
