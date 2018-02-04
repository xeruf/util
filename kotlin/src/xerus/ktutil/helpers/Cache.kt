package xerus.ktutil.helpers

import java.lang.ref.SoftReference

class Cache<T, V> {
	
	private val map = HashMap<T, SoftReference<V>>()
	
	fun put(key: T, value: V) = map.put(key, SoftReference(value))
	fun get(key: T) = map[key]?.get()
	fun get(key: T, alternative: (T) -> V) = get(key) ?: alternative(key)

}