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

import java.util.Iterator;

import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IConstr;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;

/**
 * That class is used to represent a conjunction of literals in the RHS of an
 * implication.
 * 
 * @author daniel
 * 
 * @param <T>
 * @param <C>
 */
public class ImplicationAnd<T, C> {
	private final DependencyHelper<T, C> helper;
	private final IVecInt clause;
	private final IVec<IConstr> toName = new Vec<IConstr>();

	public ImplicationAnd(DependencyHelper<T, C> helper, IVecInt clause) {
		this.clause = clause;
		this.helper = helper;
	}

	/**
	 * Add a new positive literal to the conjunction of literals.
	 * 
	 * @param thing
	 *            a domain object
	 * @return a RHS conjunction of literals.
	 * @throws ContradictionException
	 */
	public ImplicationAnd<T, C> and(T thing) throws ContradictionException {
		IVecInt tmpClause = new VecInt();
		clause.copyTo(tmpClause);
		tmpClause.push(helper.getIntValue(thing));
		IConstr constr = helper.solver.addClause(tmpClause);
		if (constr == null) {
			throw new IllegalStateException("Should not have empty constraint "
					+ thing);
		}
		toName.push(constr);
		return this;
	}

	/**
	 * Add a new negative literal to the conjunction of literals.
	 * 
	 * @param thing
	 *            a domain object
	 * @return a RHS conjunction of literals.
	 * @throws ContradictionException
	 */
	public ImplicationAnd<T, C> andNot(T thing) throws ContradictionException {
		IVecInt tmpClause = new VecInt();
		clause.copyTo(tmpClause);
		tmpClause.push(-helper.getIntValue(thing));
		toName.push(helper.solver.addClause(tmpClause));
		return this;
	}

	/**
	 * "name" the constraint for the explanation.
	 * 
	 * IT IS MANDATORY TO NAME ALL THE CONSTRAINTS!
	 * 
	 * @param name
	 *            an object to link to the constraint.
	 */
	public void named(C name) {
		for (Iterator<IConstr> it = toName.iterator(); it.hasNext();) {
			helper.descs.put(it.next(), name);
		}
	}
}