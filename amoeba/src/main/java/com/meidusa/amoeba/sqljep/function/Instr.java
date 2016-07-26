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

public class Instr extends PostfixCommand {
	private static final String PARAM_EXCEPTION = "BeginIndex and Number in instr shoud be integers";
	private static final String OUT_OF_RANGE_EXCEPTION = "The fourth argument of instr is out of range";
	
	final public int getNumberOfParameters() {
		return -1;
	}
	
	public Comparable<?>[] evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		int num = node.jjtGetNumChildren();
		if (num == 2 || num ==3 || num ==4) {
			Comparable<?>[] comparables= new Comparable<?>[num];
			for(int i= num-1;i<=0;i--){
				comparables[i] = runtime.stack.pop();
			}
			return comparables;
		} else {
			removeParams(runtime.stack, num);
			throw new ParseException(PARAMS_NUMBER+" for instr");
		}
	}
	
	public static Integer instr(Comparable<?>  param1, Comparable<?>  param2) {
		if (param1 == null || param2 == null) {
			return null;
		}
		return new Integer(param1.toString().indexOf(param2.toString())+1);
	}

	public static Integer instr(Comparable<?>  param1, Comparable<?>  param2, Comparable<?>  param3) throws ParseException {
		if (param1 == null || param2 == null || param3 == null) {
			return null;
		}
		int beginIndex;
		try {
			beginIndex = getInteger(param3)-1;
		} catch (ParseException e) {
			throw new ParseException(PARAM_EXCEPTION);
		}
		String source = param1.toString();
		if (beginIndex < 0) {
			beginIndex += source.length()+1;
			return new Integer(source.lastIndexOf(param2.toString(), beginIndex)+1);
		} else {
			return new Integer(source.indexOf(param2.toString(), beginIndex)+1);
		}
	}

	public static Integer instr(Comparable<?>  param1, Comparable<?>  param2, Comparable<?>  param3, Comparable<?>  param4) throws ParseException {
		if (param1 == null || param2 == null || param3 == null || param4 == null) {
			return null;
		}
		int beginIndex;
		int number;
		String source = param1.toString();
		try {
			beginIndex = getInteger(param3)-1;
			number = getInteger(param4);
		} catch (ParseException e) {
			throw new ParseException(PARAM_EXCEPTION);
		}
		if (number < 1) {
			throw new ParseException(OUT_OF_RANGE_EXCEPTION);
		}
		int i = 0;
		if (beginIndex < 0) {
			beginIndex += source.length()+1;
			for (; number > 0 && i >= 0; number--) {
				i = source.lastIndexOf(param2.toString(), beginIndex);
				beginIndex = i-1;
			}
		} else {
			for (; number > 0 && i >= 0; number--) {
				i = source.indexOf(param2.toString(), beginIndex);
				beginIndex = i+1;
			}
		}
		return new Integer(i+1);
	}

	public Comparable<?> getResult(Comparable<?>... comparables)
			throws ParseException {
		if(comparables.length == 2){
			return instr(comparables[0],comparables[1]);
		}else if(comparables.length == 3){
			return instr(comparables[0],comparables[1],comparables[2]);
		}else{
			return instr(comparables[0],comparables[1],comparables[2],comparables[3]);
		}
	}
}

