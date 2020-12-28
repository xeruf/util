package xerus.ktutil.collections

import java.lang.ref.SoftReference

class CacheMap<T, V> {
	
	private val map = HashMap<T, SoftReference<V>>()
	
	fun put(key: T, value: V) = map.put(key, SoftReference(value))
	fun get(key: T) = map[key]?.get()
	fun getOrPut(key: T, alternative: (T) -> V) = get(key) ?: alternative(key).also { put(key, it) }
	
}