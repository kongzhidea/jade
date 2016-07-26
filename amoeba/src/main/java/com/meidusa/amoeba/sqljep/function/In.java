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

import com.meidusa.amoeba.sqljep.ASTArray;
import com.meidusa.amoeba.sqljep.ASTFunNode;
import com.meidusa.amoeba.sqljep.JepRuntime;
import com.meidusa.amoeba.sqljep.Node;
import com.meidusa.amoeba.sqljep.ParseException;

public final class In extends PostfixCommand {

	public boolean isAutoBox() {
		return false;
	}

	final public int getNumberOfParameters() {
		return 2;
	}

	public Comparable<?>[] evaluate(ASTFunNode node, JepRuntime runtime)
			throws ParseException {
		node.jjtGetChild(0).jjtAccept(runtime.ev, null);
		Comparable<?> source = runtime.stack.pop();

		if (source == null) {
			return new Comparable<?>[] { (Boolean.FALSE) };
		} else {
			Node arg = node.jjtGetChild(1);
			if (arg instanceof ASTArray) {
				arg.jjtAccept(runtime.ev, null);
				int childSize = arg.jjtGetNumChildren();

				for (int i = 0; i < childSize; i++) {
					Comparable<?> d = runtime.stack.pop();
					if (source instanceof Comparative) {
						Comparative other = (Comparative) source;
						boolean result = other.intersect(
								Comparative.Equivalent, d,
								ComparativeComparator.comparator);
						if (result) {
							runtime.stack.setSize(runtime.stack.size()
									- (childSize - i - 1));
							return new Comparable<?>[] { (Boolean.TRUE) };
						}
					} else if (d != null
							&& ComparativeComparator.compareTo(source, d) == 0) {
						// runtime.stack.setSize(0);
						runtime.stack.setSize(runtime.stack.size()
								- (childSize - i - 1));
						return new Comparable<?>[] { (Boolean.TRUE) };
					}
				}
				// runtime.stack.setSize(0);
				return new Comparable<?>[] { (Boolean.FALSE) };
			} else {
				throw new ParseException("Internal error in function IN");
			}
		}
	}

	public Comparable<?> getResult(Comparable<?>... comparables)
			throws ParseException {
		return comparables[0];
	}
}
