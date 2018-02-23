package xerus.ktutil.helpers

import xerus.ktutil.toInt

open class Rater<X> @JvmOverloads constructor(
        /** the current object of this Rater */
        @JvmField var obj: X?,
        /** the points to the current [obj] of this Rater */
        @JvmField var points: Double,
        /** if true, then objects with less points will be preferred */
        protected val inverted: Boolean = false) {

    @JvmOverloads constructor(invert: Boolean = false) : this(null, if (invert) java.lang.Double.MAX_VALUE else -java.lang.Double.MAX_VALUE, invert)

    fun hasObj() = obj != null

    fun clear() {
        points = if (inverted) java.lang.Double.MAX_VALUE else -java.lang.Double.MAX_VALUE
        obj = null
    }

    /** replaces the objects if the given points are higher than the saved ones
     * @return if other became the current [obj] */
    fun update(other: X?, otherPoints: Double): Boolean {
        if (!inverted && otherPoints > points || inverted && otherPoints < points) {
            obj = other
            points = otherPoints
            return true
        }
        return false
    }

    fun update(newObj: X, newPoints: Double, bonus: Boolean) =
            update(newObj, newPoints + bonus.toInt())

    fun update(other: Rater<X>) = update(other.obj, other.points)
    fun update(other: Rater<X>, multiplier: Double) = update(other.obj, other.points * multiplier)

    override fun toString() = obj.toString() + " - Punkte: " + points

    override fun equals(other: Any?) =
            other is Rater<*> && other.points == this.points && other.obj == other
    override fun hashCode() = obj?.hashCode() ?: 0 * 9 + points.toInt()

}
