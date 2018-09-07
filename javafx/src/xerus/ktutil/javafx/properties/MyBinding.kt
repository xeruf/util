package xerus.ktutil.javafx.properties

import javafx.beans.Observable
import javafx.beans.binding.ObjectBinding

class MyBinding<T>(private val func: () -> T, vararg dependencies: Observable) : ObjectBinding<T>() {
	
	init {
		bind(*dependencies)
	}
	
	private val dependencies = arrayListOf(*dependencies)
	
	/** Start observing the dependencies for changes and reacts accordingly */
	fun addDependencies(vararg dependencies: Observable) {
		bind(*dependencies)
		this.dependencies.addAll(dependencies)
		invalidate()
	}
	
	/** clears this Binding of all Dependencies */
	fun clearDependencies() {
		unbind(*dependencies.toTypedArray())
		dependencies.clear()
	}
	
	override fun computeValue(): T? {
		return try {
			func()
		} catch (e: Exception) {
			System.err.println("Exception while evaluating binding!")
			e.printStackTrace()
			null
		}
	}
	
	override fun dispose() {
		clearDependencies()
	}
	
}