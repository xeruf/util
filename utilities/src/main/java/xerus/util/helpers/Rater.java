package xerus.util.helpers;

public class Rater<X> {
	
	public X obj;
	public double points;
	protected boolean inv;
	
	public Rater() {
		this(false);
	}
	
	public Rater(boolean invert) {
		this(null, invert ? Double.MAX_VALUE : Double.MIN_VALUE, invert);
	}
	
	public Rater(X object, double p) {
		this(object, p, false);
	}
	
	public Rater(X object, double p, boolean invert) {
		this.obj = object;
		points = p;
		inv = invert;
	}
	
	public boolean update(Rater<X> other) {
		return update(other.obj, other.points);
	}
	
	public boolean update(Rater<X> other, double multiplier) {
		return update(other.obj, other.points * multiplier);
	}
	
	/** replaces the objects if the given points are higher than the saved ones */
	public boolean update(X newobj, double newpoints) {
		if ((!inv && newpoints > points) || (inv && newpoints < points)) {
			obj = newobj;
			points = newpoints;
			return true;
		}
		return false;
	}
	
	public boolean update(X newobj, double newpoints, boolean bonus) {
		if (bonus) newpoints++;
		return update(newobj, newpoints);
	}
	
	public boolean hasobj() {
		return obj != null;
	}
	
	public String toString() {
		return obj.toString() + " - Punkte: " + points;
	}
	
	public boolean equals(Object obj) {
		if (obj.getClass() != this.getClass())
			return false;
		Rater m = (Rater) obj;
		return m.points == this.points && m.obj.equals(obj);
	}
	
	public int hashCode() {
		return obj.hashCode() * 9 + (int) points;
	}
	
}
