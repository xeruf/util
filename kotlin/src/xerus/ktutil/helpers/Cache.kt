package xerus.ktutil.helpers

import java.lang.ref.SoftReference
import java.lang.ref.WeakReference
import java.util.ArrayList
import java.util.NoSuchElementException
import kotlin.collections.AbstractMutableCollection
import kotlin.collections.HashMap
import kotlin.collections.MutableIterator
import kotlin.collections.addAll
import kotlin.collections.filterTo
import kotlin.collections.forEachIndexed
import kotlin.collections.toTypedArray

class Cache<T, V> {
	
	private val map = HashMap<T, SoftReference<V>>()
	
	fun put(key: T, value: V) = map.put(key, SoftReference(value))
	fun get(key: T) = map[key]?.get()
	fun getOrPut(key: T, alternative: (T) -> V) = get(key) ?: alternative(key).also { put(key, it) }
	
}

fun WeakReference<*>?.isEmpty() = this?.get() == null

/** This collection only stores WeakReferences to its elements
 * @param size initial size of the container
 * @param growFactor the factor to grow by when the container becomes too small */
class WeakCollection<E>(size: Int = 4, val growFactor: Float = 1.6f) : AbstractMutableCollection<E>() {
	
	var elements = arrayOfNulls<WeakReference<E>>(size)
	var lastindex = -1
	
	/** Currently filled spots */
	override val size: Int
		get() = lastindex + 1
	
	override fun add(element: E): Boolean {
		if(elements.size > size) {
			insertAt(element, size)
			return true
		}
		elements.forEachIndexed { index, reference ->
			if (reference.isEmpty()) {
				insertAt(element, index)
				return true
			}
		}
		val ind = elements.size
		grow(1)
		insertAt(element, ind)
		return true
	}
	
	private fun grow(additional: Int) = ensureSize(size + additional)
	
	fun ensureSize(newSize: Int) {
		val new = arrayOfNulls<WeakReference<E>>((newSize + 2).coerceAtLeast((size * growFactor).toInt()))
		var i = -1
		for(element in elements) {
			if(!element.isEmpty()) {
				i++
				new[i] = element
			}
		}
		lastindex = i
		elements = new
	}
	
	/** After running the Garbage collector, all obsolete Elements will be removed and the internal container will be
	 * shrinked to fit the remaining elements
	 * @param extra how many additional free spaces to keep in the container until it has to grow again */
	fun clean(extra: Int = 1) {
		System.gc()
		val list = elements.filterTo(ArrayList(size + extra)) { !it.isEmpty() }
		list.addAll(arrayOfNulls(extra))
		elements = list.toTypedArray()
		lastindex = elements.size - 2
		/*var counter = 0
		elements.forEachIndexed { index, reference ->
			if (reference.isEmpty()) {
				elements[index] = null
			} else {
				counter++
			}
		}
		val new = arrayOfNulls<E>(counter + 1)
		elements.forEach {
		
		}
		elements = new*/
	}
	
	private fun insertAt(element: E, index: Int) {
		elements[index] = WeakReference(element)
		lastindex = lastindex.coerceAtLeast(index)
	}
	
	override fun iterator(): MutableIterator<E> {
		return object : MutableIterator<E> {
			var cursor = 0
			
			private var next: E? = null
			override fun hasNext(): Boolean {
				while(next == null) {
					cursor++
					if(cursor > lastindex)
						return false
					next = elements[cursor]?.get()
				}
				return next != null
			}
			
			override fun next(): E {
				if (!hasNext())
					throw NoSuchElementException()
				return next.also { next = null } ?: throw NoSuchElementException()
			}
			
			override fun remove() {
				elements[cursor] = null
			}
			
		}
	}
	
}
