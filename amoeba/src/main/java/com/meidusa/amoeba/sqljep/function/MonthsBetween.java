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

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Calendar;

import com.meidusa.amoeba.sqljep.ASTFunNode;
import com.meidusa.amoeba.sqljep.JepRuntime;
import com.meidusa.amoeba.sqljep.ParseException;

public class MonthsBetween extends PostfixCommand {
	private final static BigDecimal DAYS_IN_MONTH = new BigDecimal(31);
	
	final public int getNumberOfParameters() {
		return 2;
	}
	
	public Comparable<?>[] evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		Comparable<?>  param2 = runtime.stack.pop();
		Comparable<?>  param1 = runtime.stack.pop();
		return new Comparable<?>[]{param1,param2};
	}
	
	public static BigDecimal monthsBetween(Comparable<?>  param1, Comparable<?>  param2, Calendar cal) throws ParseException {
		if (param1 == null && param2 == null) {
			return null;
		}
		try {
			if (param1 instanceof java.util.Date && param2 instanceof java.util.Date) {
				if (param1 instanceof Time || param2 instanceof Time) {
					throw new ParseException();
				}
				java.util.Date d1 = (java.util.Date)param1;
				java.util.Date d2 = (java.util.Date)param2;
				cal.setTimeInMillis(d1.getTime());
				int y1 = cal.get(YEAR);
				int m1 = cal.get(MONTH);
				int dt1 = cal.get(DATE);
				cal.setTimeInMillis(d2.getTime());
				int y2 = cal.get(YEAR);
				int m2 = cal.get(MONTH);
				int dt2 = cal.get(DATE);
				BigDecimal m = new BigDecimal((y1-y2)*12+(m1-m2));
				BigDecimal d = new BigDecimal(dt1-dt2);
				d = d.divide(DAYS_IN_MONTH, 40, BigDecimal.ROUND_HALF_UP);
				return m.add(d);
			} else {
				throw new ParseException();
			}
		} catch (ParseException e) {
			throw new ParseException(WRONG_TYPE+"  month_between("+param1.getClass()+","+param2.getClass()+")");
		}
	}

	public Comparable<?> getResult(Comparable<?>... comparables)
			throws ParseException {
		return monthsBetween(comparables[0], comparables[1], JepRuntime.getCalendar());
	}
}
