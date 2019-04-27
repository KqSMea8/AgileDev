package com.baidu.spark.util;

import java.util.AbstractSet;
import java.util.IdentityHashMap;
import java.util.Iterator;

public class IdentityHashSet<E> extends AbstractSet<E> {
	
	private static final long serialVersionUID = -6952572717595730869L;
	
	private final IdentityHashMap<E, Object> map = new IdentityHashMap<E, Object>();
	
	 private static final Object PRESENT = new Object();

	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}

	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	@Override
	public boolean add(E t) {
		int size = size();
		map.put(t, PRESENT);
		return size != size();
	}

	@Override
	public boolean remove(Object o) {
		return map.remove(o) == PRESENT;
	}

	@Override
	public void clear() {
		map.clear();
	}
	
	@Override
    public Object clone() {
		throw new UnsupportedOperationException("Clone operation is not supported yet.");
	}
}