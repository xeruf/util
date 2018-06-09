package xerus.ktutil

import java.lang.ref.WeakReference
import kotlin.reflect.KProperty


class WeakDelegate<T>(private val supplier: () -> T) {
	private var reference: WeakReference<T>? = null
	operator fun getValue(thisRef: Any, property: KProperty<*>): T =
			reference?.get() ?: supplier().also { reference = WeakReference(it) }
}