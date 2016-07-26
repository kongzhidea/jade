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

public class Substring extends PostfixCommand {
	private static final String PARAM_EXCEPTION = "BeginIndex and CountChars in substr shoud be integers";
	
	final public int getNumberOfParameters() {
		return -1;
	}
	
	public Comparable<?>[] evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		int num = node.jjtGetNumChildren();
		if (num == 2) {
			Comparable<?>  param2 = runtime.stack.pop();
			Comparable<?>  param1 = runtime.stack.pop();
			return new Comparable<?>[]{param1,param2};
		}
		else if (num == 3) {
			Comparable<?>  param3 = runtime.stack.pop();
			Comparable<?>  param2 = runtime.stack.pop();
			Comparable<?>  param1 = runtime.stack.pop();
			return new Comparable<?>[]{param1,param2,param3};
		} else {
			// remove all parameters from stack and push null
			removeParams(runtime.stack, num);
			throw new ParseException(PARAMS_NUMBER+" for substr");
		}
	}

	public static String substr(Comparable<?>  param1, Comparable<?>  param2) throws ParseException {
		if (param1 == null) {
			return null;
		}
		try {
			int beginIndex = getInteger(param2)-1;
			return param1.toString().substring(beginIndex);
		} catch (ParseException e) {
			throw new ParseException(PARAM_EXCEPTION);
		} catch (StringIndexOutOfBoundsException e) {
			return null;
		}
	}

	public static String substr(Comparable<?>  param1, Comparable<?>  param2, Comparable<?>  param3) throws ParseException {
		if (param1 == null) {
			return null;
		}
		try {
			int beginIndex = getInteger(param2)-1;
			int count = getInteger(param3);
			String source = param1.toString();
			if (beginIndex < 0) {
				beginIndex += source.length()+1;
			}
			return source.substring(beginIndex,beginIndex+count);
		} catch (ParseException e) {
			throw new ParseException(PARAM_EXCEPTION);
		} catch (StringIndexOutOfBoundsException e) {
			return null;
		}
	}

	public Comparable<?> getResult(Comparable<?>... comparables)
			throws ParseException {
		if(comparables.length == 2){
			return substr(comparables[0],comparables[1]);
		}else{
			return substr(comparables[0],comparables[1],comparables[2]);
		}
	}
}

