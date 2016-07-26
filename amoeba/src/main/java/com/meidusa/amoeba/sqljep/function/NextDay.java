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
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.THURSDAY;
import static java.util.Calendar.TUESDAY;
import static java.util.Calendar.WEDNESDAY;

import java.sql.Timestamp;
import java.util.Calendar;

import com.meidusa.amoeba.sqljep.ASTFunNode;
import com.meidusa.amoeba.sqljep.JepRuntime;
import com.meidusa.amoeba.sqljep.ParseException;

public class NextDay extends PostfixCommand {
	final public int getNumberOfParameters() {
		return 2;
	}
	
	public Comparable<?>[] evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		Comparable<?>  param2 = runtime.stack.pop();
		Comparable<?>  param1 = runtime.stack.pop();
		return new Comparable<?>[]{param1,param2};
	}

	public static java.util.Date nextDay(Comparable<?>  param1, Comparable<?>  param2, Calendar cal) throws ParseException {
		if (param1 == null && param2 == null) {
			return null;
		}
		try {
			int dayOfWeek;
			if (param2 instanceof Number) {
				dayOfWeek = ((Number)param2).intValue();
			}
			else if (param2 instanceof String) {
				String d = (String)param2;
				if (d == "SUNDAY") {
					dayOfWeek = SUNDAY;
				}
				else if (d == "MONDAY") {
					dayOfWeek = MONDAY;
				}
				else if (d == "TUESDAY") {
					dayOfWeek = TUESDAY;
				}
				else if (d == "WEDNESDAY") {
					dayOfWeek = WEDNESDAY;
				}
				else if (d == "THURSDAY") {
					dayOfWeek = THURSDAY;
				}
				else if (d == "FRIDAY") {
					dayOfWeek = FRIDAY;
				}
				else if (d == "SATURDAY") {
					dayOfWeek = SATURDAY;
				} else {
					throw new ParseException();
				}
			} else {
				throw new ParseException();
			}
			if (param1 instanceof Timestamp || param1 instanceof java.sql.Date) {
				java.util.Date d = (java.util.Date)param1;
				cal.setTimeInMillis(d.getTime());
				int day = cal.get(DAY_OF_WEEK);
				cal.add(DATE, (day<dayOfWeek) ? dayOfWeek-day : 7+dayOfWeek-day);
				return new java.util.Date(cal.getTimeInMillis());
			} else {
				throw new ParseException();
			}
		} catch (ParseException e) {
			throw new ParseException(WRONG_TYPE+"  next_day("+param1.getClass()+","+param2.getClass()+")");
		}
	}

	public Comparable<?> getResult(Comparable<?>... comparables)
			throws ParseException {
		return nextDay(comparables[0],comparables[1],JepRuntime.getCalendar());
	}
}

