/*****************************************************************************
      SQLJEP - Java SQL Expression Parser 0.2
      November 1 2006
         (c) Copyright 2006, Alexey Gaidukov
      SQLJEP Author: Alexey Gaidukov

      SQLJEP is based on JEP 2.24 (http://www.singularsys.com/jep/)
           (c) Copyright 2002, Nathan Funk
 
      See LICENSE.txt for license information.
 *****************************************************************************/

package com.meidusa.amoeba.sqljep.function;

import com.meidusa.amoeba.sqljep.ASTFunNode;
import com.meidusa.amoeba.sqljep.JepRuntime;
import com.meidusa.amoeba.sqljep.ParseException;

public final class Between extends PostfixCommand {
	final public int getNumberOfParameters() {
		return 3;
	}

	public boolean isAutoBox() {
		return false;
	}

	public Comparable<?>[] evaluate(ASTFunNode node, JepRuntime runtime)
			throws ParseException {
		node.childrenAccept(runtime.ev, null);
		Comparable<?> limit2 = runtime.stack.pop();
		Comparable<?> limit1 = runtime.stack.pop();
		Comparable<?> source = runtime.stack.pop();
		return new Comparable<?>[] { source, limit1, limit2 };
	}

	public Comparable<?> getResult(Comparable<?>... comparables)
			throws ParseException {
		Comparable<?> limit2 = comparables[2];
		Comparable<?> limit1 = comparables[1];
		Comparable<?> source = comparables[0];
		if (source == null || limit1 == null || limit2 == null) {
			return (Boolean.FALSE);
		} else {
			if (source instanceof Comparative) {
				Comparative other = (Comparative) source;
				boolean result = other.intersect(
						Comparative.GreaterThanOrEqual, limit1,
						ComparativeComparator.comparator);
				result = result
						&& other.intersect(Comparative.LessThanOrEqual, limit2,
								ComparativeComparator.comparator);
				return (result);
			} else {
				return (ComparativeComparator.compareTo(source, limit1) >= 0 && ComparativeComparator
						.compareTo(source, limit2) <= 0);
			}
		}
	}

}
