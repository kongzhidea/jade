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

public class Lpad extends PostfixCommand {
	private static final String PARAM_EXCEPTION = "Length in lpad shoud be integer";

	final public int getNumberOfParameters() {
		return -1;
	}
	
	public Comparable<?>[] evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		int num = node.jjtGetNumChildren();
		if (num == 2) {
			Comparable<?>  param2 = runtime.stack.pop();
			Comparable<?>  param1 = runtime.stack.pop();
			return new Comparable<?>[]{param1,param2," "};
		}
		else if (num == 3) {
			Comparable<?>  param3 = runtime.stack.pop();
			Comparable<?>  param2 = runtime.stack.pop();
			Comparable<?>  param1 = runtime.stack.pop();
			return new Comparable<?>[]{param1,param2,param3};
		} else {
			// remove all parameters from stack and push null
			removeParams(runtime.stack, num);
			throw new ParseException(PARAMS_NUMBER+" for lpad");
		}
	}

	public static String lpad(Comparable<?>  param1, Comparable<?>  param2, Comparable<?>  param3) throws ParseException {
		if (param1 == null || param2 == null || param3 == null) {
			return null;
		}
		String inputStr = param1.toString();
		String chars = param3.toString();
		int length;
		try {
			length = getInteger(param2);
		} catch (ParseException e) {
			throw new ParseException(PARAM_EXCEPTION);
		}
		if (length == inputStr.length()) {
			return inputStr;
		}
		else if (length < inputStr.length()) {
			return inputStr.substring(0, length);
		} else {
			length -= inputStr.length();
			int count = length / chars.length();
			int remainder = length % chars.length();
			StringBuilder output = new StringBuilder();
			for (int i = 0; i < count; i++) {
				output.append(chars);
			}
			output.append(chars.substring(0, remainder));
			output.append(inputStr);
			return output.toString();
		}
	}

	public Comparable<?> getResult(Comparable<?>... comparables)
			throws ParseException {
		return lpad(comparables[0],comparables[1],comparables[2]);
	}
}

