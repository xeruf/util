package xerus.ktutil.collections

import java.lang.ref.WeakReference
import java.util.*


fun WeakReference<*>?.isEmpty() = this?.get() == null

/** A collection that only stores WeakReferences to its elements and automatically reuses garbage-collected spaces.
 *
 * @param size initial size of the container
 * @param growFactor the factor to grow by when the container becomes too small */
class WeakCollection<E>(size: Int = 4, private val growFactor: Float = 1.6f) : AbstractMutableCollection<E>() {
	
	/** The Array of WeakReferences pointing to the stored objects*/
	private var elements = arrayOfNulls<WeakReference<E>>(size)
	
	/** The position at which the next added element should be inserted */
	private var nextIndex = 0
	
	/** Total current space in this WeakCollection */
	override val size: Int
		get() = elements.size
	
	/** Amount of non-empty elements */
	val elementCount: Int
		get() = elements.count { !it.isEmpty() }
	
	override fun add(element: E): Boolean {
		if(elements.size > nextIndex) {
			insert(element)
			return true
		}
		elements.forEachIndexed { index, reference ->
			if(reference.isEmpty()) {
				insert(element, index)
				return true
			}
		}
		grow()
		insert(element)
		return true
	}
	
	/** Grows the size of the internal array by [growFactor] */
	private fun grow() = ensureSize((size * growFactor).toInt() + 1)
	
	/** If [newSize] is greater than [size], cleans out the collection and sets its size to [newSize]. */
	fun ensureSize(newSize: Int) {
		if(size >= newSize)
			return
		fitSize(newSize)
	}
	
	/** Invokes [System.gc] and then resizes the collection to fit the remaining elements.
	 * @param extra How many free spaces to keep in the container after resizing. */
	fun clean(extra: Int = 1) {
		System.gc()
		fitSize(elementCount + extra)
	}
	
	/** Cleans out the Collection and resizes it to [newSize].
	 *
	 * **Warning** This will throw an [ArrayIndexOutOfBoundsException] if the amount of remaining elements is greater
	 * than [newSize]. */
	fun fitSize(newSize: Int) {
		val new = arrayOfNulls<WeakReference<E>>(newSize)
		var i = -1
		for(element in elements) {
			if(!element.isEmpty()) {
				i++
				new[i] = element
			}
		}
		nextIndex = i
		elements = new
	}
	
	/** Inserts a [WeakReference] to the given element at [index], which defaults to [nextIndex] and coerces [nextIndex]
	 * to at least index + 1. */
	private fun insert(element: E, index: Int = nextIndex) {
		elements[index] = WeakReference(element)
		nextIndex = nextIndex.coerceAtLeast(index + 1)
	}
	
	override fun iterator(): MutableIterator<E> {
		return object : MutableIterator<E> {
			var cursor = -1
			
			private var hasAdvanced = false
			private var next: E? = null
			override fun hasNext(): Boolean {
				if(!hasAdvanced) {
					cursor++
					if(cursor == nextIndex)
						return false
					next = elements[cursor]?.get()
					hasAdvanced = true
				}
				return next != null
			}
			
			override fun next(): E {
				if(!hasNext())
					throw NoSuchElementException()
				hasAdvanced = false
				return next!!
			}
			
			override fun remove() {
				elements[cursor] = null
			}
			
		}
	}
	
}
