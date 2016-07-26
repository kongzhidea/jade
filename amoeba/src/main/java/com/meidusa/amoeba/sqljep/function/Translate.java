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

public class Translate extends PostfixCommand {
	final public int getNumberOfParameters() {
		return 3;
	}
	
	public Comparable<?>[] evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		Comparable<?>  param3 = runtime.stack.pop();
		Comparable<?>  param2 = runtime.stack.pop();
		Comparable<?>  param1 = runtime.stack.pop();
		return new Comparable<?>[]{param1,param2,param3};
	}

	public static String translate(Comparable<?>  param1, Comparable<?>  param2, Comparable<?>  param3) throws ParseException {
		if (param1 == null || param2 == null || param3 == null) {
			return null;
		}
		String inputStr = param1.toString();
		String chars1 = param2.toString();
		String chars2 = param3.toString();
		int len;
		StringBuilder output;
		if (chars2.length() < chars1.length()) {
			// delete characters
			output = new StringBuilder();
			final int n = inputStr.length();
			for (int i = 0; i < n; i++) {
				char c = inputStr.charAt(i);
				int k = chars1.indexOf(c, chars2.length());
				if (k == -1) {
					output.append(c);
				}
			}
			len = chars2.length();
		} else {
			len = chars1.length();
			output = new StringBuilder(inputStr);
		}
		// replace characters 
		for (int i = 0; i < len; i++) {
			String c = chars1.substring(i,i+1);
			int k;
			int fromIndex = 0;
			while ((k = output.indexOf(c, fromIndex)) != -1) {
				output.setCharAt(k, chars2.charAt(i));
				fromIndex = k+1;
			}
		}
		return output.toString();
	}

	public Comparable<?> getResult(Comparable<?>... comparables)
			throws ParseException {
		return translate(comparables[0],comparables[1],comparables[2]);
	}
}
