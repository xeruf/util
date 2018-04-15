package xerus.ktutil.helpers

class ArrayMap<K, V> : MutableMap<K, V> {
	
	override val keys = ArraySet<K>()
	override val values = ArrayList<V>()
	
	override val size: Int
		get() = keys.size
	
	override fun containsKey(key: K) = key in keys
	
	override fun containsValue(value: V) = value in values
	
	override fun get(key: K) = values[keys.indexOf(key)]
	
	override fun isEmpty() = keys.isEmpty()
	
	/** highly inefficient! Avoid if at all possible! */
	override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
		get() = mutableSetOf(*(0 until keys.size).map { ArrayMapEntry(it) }.toTypedArray())
	
	override fun clear() {
		keys.clear()
		values.clear()
	}
	
	override fun put(key: K, value: V): V? {
		val i = indexOf(key)
		var old: V? = null
		if (i == -1) {
			keys.add(key)
			values.add(value)
		} else {
			old = values[i]
			values[i] = value
		}
		return old
	}
	
	override fun putAll(from: Map<out K, V>) {
		from.forEach { k, u -> put(k, u) }
	}
	
	override fun remove(key: K): V? {
		val i = indexOf(key)
		return if (i == -1)
			null
		else {
			keys.removeAt(i)
			values.removeAt(i)
		}
	}
	
	@Suppress("NOTHING_TO_INLINE")
	private inline fun indexOf(key: K) = keys.indexOf(key)
	
	inner class ArrayMapEntry(val index: Int) : MutableMap.MutableEntry<K, V> {
		override val key = keys[index]
		override val value: V
			get() = values[index]
		
		override fun setValue(newValue: V) = value.also {
			values[index] = newValue
		}
	}
	
}