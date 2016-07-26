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

public final class ComparativeIsNotNull extends PostfixCommand {
	final public int getNumberOfParameters() {
		return 1;
	}

	public boolean isAutoBox() {
		return false;
	}

	public Comparable<?>[] evaluate(ASTFunNode node, JepRuntime runtime)
			throws ParseException {
		node.childrenAccept(runtime.ev, null);

		Comparable<?> param = runtime.stack.pop();
		return new Comparable<?>[] { param };
	}

	public Comparable<?> getResult(Comparable<?>... comparables)
			throws ParseException {
		if (comparables[0] instanceof Comparative) {
			return (((Comparative) comparables[0]).getValue() != null);
		} else {
			return (comparables[0] != null);
		}
	}
}
