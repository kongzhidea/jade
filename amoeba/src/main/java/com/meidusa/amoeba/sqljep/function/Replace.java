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

public class Replace extends PostfixCommand {
	final public int getNumberOfParameters() {
		return -1;
	}
	
	public Comparable<?>[] evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		int num = node.jjtGetNumChildren();
		if (num == 2) {
			Comparable<?>  param2 = runtime.stack.pop();
			Comparable<?>  param1 = runtime.stack.pop();
			return new Comparable<?>[]{param1,param2,null};
		}
		else if (num == 3) {
			Comparable<?>  param3 = runtime.stack.pop();
			Comparable<?>  param2 = runtime.stack.pop();
			Comparable<?>  param1 = runtime.stack.pop();
			return new Comparable<?>[]{param1,param2,param3};
		} else {
			// remove all parameters from stack and push null
			removeParams(runtime.stack, num);
			throw new ParseException(PARAMS_NUMBER+" for replace");
		}
	}

	public static String replace(Comparable<?>  param1, Comparable<?>  param2, Comparable<?>  param3) throws ParseException {
		if (param1 == null || param2 == null) {
			return null;
		}
		String source = param1.toString();
		String from = param2.toString();
		int fromLen = from.length();
		String to = (param3 != null) ? param3.toString() : null;
		StringBuilder output = new StringBuilder();
		int beginIndex = 0;
		int k;
		boolean found = false;
		if (to != null) {
			while ((k = source.indexOf(from, beginIndex)) != -1) {
				output.append(source.substring(beginIndex, k));
				output.append(to);
				beginIndex = k+fromLen;
				found = true;
			}
		} else {
			while ((k = source.indexOf(from, beginIndex)) != -1) {
				output.append(source.substring(beginIndex, k));
				beginIndex = k+fromLen;
				found = true;
			}
		}
		if (found) {
			output.append(source.substring(beginIndex));
		}
		return output.toString();
	}

	public Comparable<?> getResult(Comparable<?>... comparables)
			throws ParseException {
		return replace(comparables[0],comparables[1],comparables[2]);
	}
}
