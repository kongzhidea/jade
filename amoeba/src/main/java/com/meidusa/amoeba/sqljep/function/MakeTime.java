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

import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;

import java.sql.Time;
import java.util.Calendar;

import com.meidusa.amoeba.sqljep.ASTFunNode;
import com.meidusa.amoeba.sqljep.JepRuntime;
import com.meidusa.amoeba.sqljep.ParseException;

public class MakeTime extends PostfixCommand {
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

	public static Time makeTime(Comparable<?>  param1, Comparable<?>  param2, Comparable<?>  param3, Calendar cal) throws ParseException {
		if (param1 == null && param2 == null && param3 == null) {
			return null;
		}
		if (param1 instanceof String) {
			param1 = parse((String)param1);
		}
		if (param2 instanceof String) {
			param2 = parse((String)param2);
		}
		if (param3 instanceof String) {
			param3= parse((String)param3);
		}
		if (param1 instanceof Number && param2 instanceof Number && param3 instanceof Number) {
			int h = ((Number)param1).intValue();
			int m = ((Number)param2).intValue();
			int s = ((Number)param3).intValue();
			cal.clear();
			cal.set(HOUR_OF_DAY, h);
			cal.set(MINUTE, m);
			cal.set(SECOND, s);
			return new Time(cal.getTimeInMillis());
		}
		throw new ParseException(WRONG_TYPE+" maketime("+param1.getClass()+","+param2.getClass()+")");
	}

	public Comparable<?> getResult(Comparable<?>... comparables)
			throws ParseException {
		return makeTime(comparables[0], comparables[1], comparables[2], JepRuntime.getCalendar());
	}
}
