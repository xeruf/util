package xerus.ktutil.helpers

class RoughMap<V : Any> {
	
	val keys = ArrayList<String>()
	val values = ArrayList<V>()
	
	fun clear() {
		keys.clear()
		values.clear()
	}
	
	fun put(key: String, value: V) {
		keys.add(key)
		values.add(value)
	}
	
	fun get(key: String): V? {
		val ind = keys.indexOf(key)
		return if (ind != -1) values[ind] else null
	}
	
	fun find(key: String): V? {
		get(key)?.let { return it }
		keys.forEachIndexed { i, it -> if (it.contains(key, true)) return values[i] }
		return null
	}
	
	fun findUnsafe(key: String): V {
		get(key)?.let { return it }
		keys.forEachIndexed { i, it -> if (it.contains(key, true)) return values[i] }
		throw KeyNotFoundException(key)
	}
	
	fun findAll(key: String): List<V> =
			keys.mapIndexedNotNull { i, s -> if(s.contains(key, true)) values[i] else null }
		
}

class KeyNotFoundException(key: String) : Exception("Key not found: $key")