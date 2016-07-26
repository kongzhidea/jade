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

import java.sql.Timestamp;
import java.util.Calendar;

import com.meidusa.amoeba.sqljep.ASTFunNode;
import com.meidusa.amoeba.sqljep.JepRuntime;
import com.meidusa.amoeba.sqljep.ParseException;
import com.meidusa.amoeba.util.StaticString;
import com.meidusa.amoeba.util.ThreadLocalMap;

public class AddMonths extends PostfixCommand {
	final public int getNumberOfParameters() {
		return 2;
	}
	
	public Comparable<?>[] evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		Comparable<?>  param2 = runtime.stack.pop();
		Comparable<?>  param1 = runtime.stack.pop();
		return new Comparable<?>[]{param1,param2};
		//runtime.stack.push(addMonths(param1, param2, runtime.calendar)); // push the result on the inStack
	}

	public static java.util.Date addMonths(Comparable<?>  param1, Comparable<?>  param2, Calendar cal) throws ParseException {
		if (param1 == null && param2 == null) {
			return null;
		}
		try {
			int months = getInteger(param2);
			if (param1 instanceof Timestamp) {
				Timestamp d = (Timestamp)param1;
				cal.setTimeInMillis(d.getTime());
				cal.add(Calendar.MONTH, months);
				return new Timestamp(cal.getTimeInMillis());
			}
			else if (param1 instanceof java.sql.Date) {
				java.util.Date d = (java.util.Date)param1;
				cal.setTimeInMillis(d.getTime());
				cal.add(Calendar.MONTH, months);
				return new java.util.Date(cal.getTimeInMillis());
			} else {
				throw new ParseException();
			}
		} catch (ParseException e) {
			throw new ParseException(WRONG_TYPE+"  month_between("+param1.getClass()+","+param2.getClass()+")");
		}
	}

	public Comparable<?> getResult(Comparable<?>... comparables)
			throws ParseException {
		Calendar calendar = (Calendar)ThreadLocalMap.get(StaticString.CALENDAR);
		if (calendar == null) {
			calendar = Calendar.getInstance();
			ThreadLocalMap.put(StaticString.CALENDAR,calendar);
		}
		return addMonths(comparables[0], comparables[1], calendar);
	}
}

