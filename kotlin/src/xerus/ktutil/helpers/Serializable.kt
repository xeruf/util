package xerus.ktutil.helpers

interface Serializable<T> {
	fun deserialize(serialized: T)
	fun serialize(): T
}