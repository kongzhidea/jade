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

public final class LogicalNOT extends PostfixCommand {
	final public int getNumberOfParameters() {
		return 1;
	}
	
	public Comparable<?>[] evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		Comparable<?>  param = runtime.stack.pop();
		return new Comparable<?>[]{param};
	}

	public static Boolean not(Comparable<?>  param) throws ParseException {
		if (param == null) {
			return null;
		}
		if (param instanceof Boolean) {
			return ((Boolean)param).booleanValue() ? Boolean.TRUE : Boolean.FALSE;
		}
		throw new ParseException(WRONG_TYPE+" not "+param.getClass());
	}

	public Comparable<?> getResult(Comparable<?>... comparables)
			throws ParseException {
		return not(comparables[0]);
	}
}

