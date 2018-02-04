package xerus.ktutil.helpers

import java.util.*

/** Implementation of ArrayList which prohibits double Elements  */
class ArraySet<E>() : ArrayList<E>(), Set<E> {
	
	constructor(list: Collection<E>) : this() {
		addAll(list)
	}
	
	override fun add(element: E): Boolean {
		return if (contains(element)) false else super.add(element)
	}
	
	override fun add(index: Int, element: E) {
		if (!contains(element))
			super.add(index, element)
	}
	
	override fun addAll(elements: Collection<E>) = addAll(size, elements)
	
	override fun addAll(index: Int, elements: Collection<E>): Boolean {
		val copy = ArrayList(elements)
		copy.removeAll(this)
		return super.addAll(index, copy.distinct())
	}
	
	override fun spliterator() = super<ArrayList>.spliterator()
	
}