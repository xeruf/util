package xerus.util.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/** Implementation of ArrayList which prohibits double Elements */
public class ArraySet<E> extends ArrayList<E> implements Set<E> {
	
	public ArraySet() {
		super();
	}
	
	public ArraySet(Collection<E> list) {
		super(list);
	}
	
	@Override
	public boolean add(E e) {
		return !contains(e) && super.add(e);
	}
	
	@Override
	public void add(int index, E e) {
		if (!contains(e)) {
			super.add(index, e);
		}
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		return addAll(size(), c);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		Collection<E> copy = new ArrayList<E>(c);
		copy.removeAll(this);
		return super.addAll(index, copy);
	}
	
}