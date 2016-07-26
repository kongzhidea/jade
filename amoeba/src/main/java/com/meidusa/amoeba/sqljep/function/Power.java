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

import java.math.BigDecimal;

import com.meidusa.amoeba.sqljep.ASTFunNode;
import com.meidusa.amoeba.sqljep.JepRuntime;
import com.meidusa.amoeba.sqljep.ParseException;

public class Power extends PostfixCommand {
	final public int getNumberOfParameters() {
		return 2;
	}
	
	public Comparable<?>[] evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		Comparable<?>  param2 = runtime.stack.pop();
		Comparable<?>  param1 = runtime.stack.pop();
		return new Comparable<?>[]{param1,param2};
	}
	
	public static Comparable<?>  power(Comparable<?>  param1, Comparable<?>  param2) throws ParseException {
		if (param1 == null || param2 == null) {
			return null;
		}
		if (param1 instanceof String) {
			param1 = parse((String)param1);
		}
		if (param2 instanceof String) {
			param2 = parse((String)param2);
		}
		if (param1 instanceof Number && param2 instanceof Number) {
			int i = ((Number)param2).intValue();
			double d = ((Number)param2).doubleValue();
			if ((double)i == d) {
				int p2 = i;
				if (param1 instanceof BigDecimal) {
					try {
						return ((BigDecimal)param1).pow(p2);
					} catch (ArithmeticException e) {
						throw new ParseException(e.getMessage());
					}
				}
				if (param1 instanceof Double || param1 instanceof Float) {
					return Math.pow(((Number)param1).doubleValue(), p2);
				} else {
					long l1 = ((Number)param1).longValue();
					long res = l1;
					for (; p2 > 1; p2--) {
						long old = res;
						res *= l1;
						if (res < old) {
							return Math.pow(l1, p2);
						}
					}
					if (res >= 0) {
						return res;
					}
				}
			}
			throw new ParseException("Power should be integer value");
		} else {
			throw new ParseException(WRONG_TYPE+"  power("+param1.getClass()+","+param2.getClass()+")");
		}
	}

	public Comparable<?> getResult(Comparable<?>... comparables)
			throws ParseException {
		return power(comparables[0],comparables[1]);
	}
}
