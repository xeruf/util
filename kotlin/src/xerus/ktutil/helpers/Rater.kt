package xerus.ktutil.helpers

import xerus.ktutil.toInt

open class Rater<X> @JvmOverloads constructor(var obj: X?, var points: Double, protected val inverted: Boolean = false) {

    @JvmOverloads constructor(invert: Boolean = false) : this(null, if (invert) java.lang.Double.MAX_VALUE else java.lang.Double.MIN_VALUE, invert)

    fun hasObj() = obj != null

    /** replaces the objects if the given points are higher than the saved ones  */
    fun update(newObj: X?, newPoints: Double): Boolean {
        if (!inverted && newPoints > points || inverted && newPoints < points) {
            obj = newObj
            points = newPoints
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
