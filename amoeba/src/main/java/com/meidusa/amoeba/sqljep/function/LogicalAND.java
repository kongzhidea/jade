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

public final class LogicalAND extends PostfixCommand {
	final public int getNumberOfParameters() {
		return 2;
	}
	
	public Comparable<?>[] evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		Comparable<?>  param2 = runtime.stack.pop();
		Comparable<?>  param1 = runtime.stack.pop();
		return new Comparable<?>[]{param1,param2};
	}

	public static Boolean and(Comparable<?>  param1, Comparable<?>  param2) throws ParseException {
		if (param1 == null || param2 == null) {
			return null;
		}
		if (param1 instanceof Boolean && param2 instanceof Boolean) {
			return (((Boolean)param1).booleanValue()) && (((Boolean)param2).booleanValue());
		}
		throw new ParseException(WRONG_TYPE+ param1.getClass()+" and "+ param2.getClass());
	}

	public Comparable<?> getResult(Comparable<?>... comparables)
			throws ParseException {
		return and(comparables[0], comparables[1]);
	}
}

