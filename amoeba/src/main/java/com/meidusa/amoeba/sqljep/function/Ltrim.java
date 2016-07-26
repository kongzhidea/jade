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

public class Ltrim extends PostfixCommand {
	final public int getNumberOfParameters() {
		return -1;
	}
	
	public Comparable<?>[] evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		int num = node.jjtGetNumChildren();
		if (num == 1) {
			Comparable<?>  param1 = runtime.stack.pop();
			return new Comparable<?>[]{param1," "};
		}
		else if (num == 2) {
			Comparable<?>  param2 = runtime.stack.pop();
			Comparable<?>  param1 = runtime.stack.pop();
			return new Comparable<?>[]{param1,param2};
		} else {
			// remove all parameters from stack and push null
			removeParams(runtime.stack, num);
			throw new ParseException(PARAMS_NUMBER+" for ltrim");
		}
	}

	public static String ltrim(Comparable<?>  param1, Comparable<?>  param2) throws ParseException {
		if (param1 == null || param2 == null) {
			return null;
		}
		String inputStr = param1.toString();
		String chars = param2.toString();
		final int len = inputStr.length();
		int i;
		for (i = 0; i < len; i++) {
			char c = inputStr.charAt(i);
			if (chars.indexOf(c) == -1) {
				break;
			}
		}
		return inputStr.substring(i);
	}

	public Comparable<?> getResult(Comparable<?>... comparables)
			throws ParseException {
		return ltrim(comparables[0],comparables[1]);
	}
}

