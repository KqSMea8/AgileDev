package com.baidu.spark.util;

import java.io.Serializable;

/**
 * 对容器.
 * 
 * @author GuoLin
 * 
 */
public class Pair<F, S> implements Serializable {

	private static final long serialVersionUID = 2611317586983831325L;

	/** 第一个成员. */
	private F first;

	/** 第二个成员. */
	private S second;

	/**
	 * 构造器.
	 * @param first 第一个成员.
	 * @param second 第二个成员.
	 */
	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * 获取第一个成员.
	 * @return 第一个成员.
	 */
	public F getFirst() {
		return first;
	}

	/**
	 * 获取第二个成员.
	 * @return 第二个成员.
	 */
	public S getSecond() {
		return second;
	}

	public void setFirst(F first) {
		this.first = first;
	}

	public void setSecond(S second) {
		this.second = second;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Pair)) {
			return false;
		}
		Pair o = (Pair) other;
		return first.equals(o.getFirst()) && second.equals(o.getSecond());
	}

	@Override
	public int hashCode() {
		return first.hashCode() + second.hashCode();
	}

	@Override
	public String toString() {
		return "[" + first + ", " + second + "]";
	}
}
