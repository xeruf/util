package xerus.ktutil.preferences

val multiSeparator = ";"

/** provides a general interface for persistently storing String values */
interface ISetting {

    var value: String

    fun put(value: String) {
        this.value = value
    }

    /** associates the given Objects toString() with this Setting  */
    fun put(value: Any) = put(value.toString())

    val bool: Boolean
        get() = value.toBoolean()

    val int: Int
        get() = value.toInt()

    // MULTI MECHANICS!

    val all: List<String>
        get() = value.split(multiSeparator)

    /** adds or removes the given key, depending on the Boolean add */
    fun putMulti(key: String, add: Boolean) {
        val cur: String = this.value
        if (add)
            put(if (cur.isEmpty()) key else cur + multiSeparator + key)
        else {
            val result = cur.split(multiSeparator).filterNot { it.contains(key) }.toTypedArray()
            putMulti(*result)
        }
    }

    fun putMulti(vararg values: String) = put(values.joinToString(multiSeparator))
    fun getMulti(key: String) = value.contains(key)

}
