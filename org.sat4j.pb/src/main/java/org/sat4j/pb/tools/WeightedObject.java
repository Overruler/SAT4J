/*******************************************************************************
* SAT4J: a SATisfiability library for Java Copyright (C) 2004-2008 Daniel Le Berre
*
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Alternatively, the contents of this file may be used under the terms of
* either the GNU Lesser General Public License Version 2.1 or later (the
* "LGPL"), in which case the provisions of the LGPL are applicable instead
* of those above. If you wish to allow use of your version of this file only
* under the terms of the LGPL, and not to allow others to use your version of
* this file under the terms of the EPL, indicate your decision by deleting
* the provisions above and replace them with the notice and other provisions
* required by the LGPL. If you do not delete the provisions above, a recipient
* may use your version of this file under the terms of the EPL or the LGPL.
*******************************************************************************/

package org.sat4j.pb.tools;

import java.math.BigInteger;

public class WeightedObject<T> implements Comparable<WeightedObject<T>>{

	public final T thing;
	private BigInteger weight;
	
	private WeightedObject(T thing, BigInteger weight) {
		this.thing = thing;
		this.weight = weight;
	}

	public BigInteger getWeight() {
		return weight;
	}
	
	public void increaseWeight(BigInteger delta) {
		weight = weight.add(delta);
	}
	
	public int compareTo(WeightedObject<T> arg0) {
		return weight.compareTo(arg0.getWeight());
	}

	public static <E> WeightedObject<E> newWO(E e,int w) {
		return new WeightedObject<E>(e,BigInteger.valueOf(w));
	}
	
	public static <E> WeightedObject<E> newWO(E e,BigInteger w) {
		return new WeightedObject<E>(e,w);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((thing == null) ? 0 : thing.hashCode());
		result = prime * result + ((weight == null) ? 0 : weight.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WeightedObject<?> other = (WeightedObject<?>) obj;
		if (thing == null) {
			if (other.thing != null)
				return false;
		} else if (!thing.equals(other.thing))
			return false;
		if (weight == null) {
			if (other.weight != null)
				return false;
		} else if (!weight.equals(other.weight))
			return false;
		return true;
	}
}
