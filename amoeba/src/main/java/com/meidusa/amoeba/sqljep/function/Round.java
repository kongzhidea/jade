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

import static com.meidusa.amoeba.sqljep.function.Trunc.DATE_EXCEPTION;
import static com.meidusa.amoeba.sqljep.function.Trunc.FORMAT_EXCEPTION;
import static com.meidusa.amoeba.sqljep.function.Trunc.TIME_EXCEPTION;
import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.YEAR;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;

import com.meidusa.amoeba.sqljep.ASTFunNode;
import com.meidusa.amoeba.sqljep.JepRuntime;
import com.meidusa.amoeba.sqljep.ParseException;

public class Round extends PostfixCommand {
	private static final String PARAM_EXCEPTION = "Scale in round shoud be integer";

	final public int getNumberOfParameters() {
		return -1;
	}
	
	public Comparable<?>[] evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		int num = node.jjtGetNumChildren();
		if (num == 1) {
			Comparable<?>  param1 = runtime.stack.pop();
			return new Comparable<?>[]{param1};
		}
		else if (num == 2) {
			Comparable<?>  param2 = runtime.stack.pop();
			Comparable<?>  param1 = runtime.stack.pop();
			return new Comparable<?>[]{param1,param2};
		} else {
			// remove all parameters from stack and push null
			removeParams(runtime.stack, num);
			throw new ParseException(PARAMS_NUMBER+" for trunc");
		}
	}

	public static Comparable<?>  round(Comparable<?>  param, Calendar cal) throws ParseException {
		if (param == null) {
			return null;
		}
		if (param instanceof String) {
			param = parse((String)param);
		}
		if (param instanceof BigDecimal) {		// BigInteger is not supported
			BigDecimal b = ((BigDecimal)param).setScale(0, BigDecimal.ROUND_HALF_UP);
			try {
				return b.longValueExact();
			} catch (ArithmeticException e) {
			}
			return b;
		}
		if (param instanceof Double || param instanceof Float) {
			return Math.round(((Number)param).doubleValue());
		}
		if (param instanceof Number) {		// Long, Integer, Short, Byte 
			return param;
		}
		if (param instanceof Timestamp) {
			Timestamp ts = (Timestamp)param;
			cal.setTimeInMillis(ts.getTime());
			int year = cal.get(YEAR);
			int month = cal.get(MONTH);
			int date = cal.get(DATE);
			int hour = cal.get(HOUR_OF_DAY);
			cal.clear();
			if (hour > 11) {
				date++;
			}
			cal.set(year, month, date);
			return new Timestamp(cal.getTimeInMillis());
		}
		throw new ParseException(WRONG_TYPE+" round("+param.getClass()+")");
	}

	public static Comparable<?>  round(Comparable<?>  param1, Comparable<?>  param2, Calendar cal) throws ParseException {
		if (param1 == null || param2 == null) {
			return null;
		}
		if (param1 instanceof String) {
			param1 = parse((String)param1);
		}
		if (param1 instanceof Number) {
			int scale;
			try {
				scale = getInteger(param2);
			} catch (ParseException e) {
				throw new ParseException(PARAM_EXCEPTION);
			}
			if (scale < 0) {
				return ZERO;
			}
			if (param1 instanceof BigDecimal) {		// BigInteger is not supported
				return ((BigDecimal)param1).setScale(scale, BigDecimal.ROUND_HALF_UP);
			}
			if (param1 instanceof Double || param1 instanceof Float) {
				double d = ((Number)param1).doubleValue();
				long mult = 1;
				for (int i = 0; i < scale; i++) {
					mult *= 10;
				}
				d *= mult;
				return (Math.round(d))/mult;
			}
			if (param1 instanceof Number) {		// Long, Integer, Short, Byte 
				return param1;
			}
			throw new ParseException(WRONG_TYPE+" round("+param1.getClass()+","+param2.getClass()+")");
		}
		else if (param1 instanceof java.util.Date) {
			if (param2 instanceof String) {
				String s = (String)param2;
				java.util.Date d = (java.util.Date)param1;
				cal.setTimeInMillis(d.getTime());
				if (s.equalsIgnoreCase("CC") || s.equalsIgnoreCase("SCC")) {
					if (d instanceof java.sql.Time) {
						throw new ParseException(TIME_EXCEPTION);
					}
					int year = cal.get(YEAR);
					int year1 = (year/100)*100;
					if (year-year1 > 50) {
						year1 += 100;
					}
					cal.clear();
					cal.set(year1+1, 0, 1);
					return new Timestamp(cal.getTimeInMillis());
				}
				else if (s.equalsIgnoreCase("SYYYY") || s.equalsIgnoreCase("YYYY") || s.equalsIgnoreCase("YYY") || 
							s.equalsIgnoreCase("Y") || s.equalsIgnoreCase("YEAR") || s.equalsIgnoreCase("SYEAR")) {
					if (d instanceof java.sql.Time) {
						throw new ParseException(TIME_EXCEPTION);
					}
					int year = cal.get(YEAR);
					int month = cal.get(MONTH);
					if (month > 6) {
						year++;
					}
					cal.clear();
					cal.set(year, 0, 1);
					return new Timestamp(cal.getTimeInMillis());
				}
				else if (s.equalsIgnoreCase("IYYY") || s.equalsIgnoreCase("IY") || s.equalsIgnoreCase("I")) {
					throw new ParseException(NOT_IMPLIMENTED_EXCEPTION);
				}
				else if (s.equalsIgnoreCase("Q")) {
					if (d instanceof java.sql.Time) {
						throw new ParseException(TIME_EXCEPTION);
					}
					int year = cal.get(YEAR);
					int month = cal.get(MONTH);
					int q = (month/3)*3;
					if (month > q+1) {
						q++;
					}
					else if (month > q) {
						int day = cal.get(DAY_OF_MONTH);
						if (day > 15) {
							q++;
						}
					}
					cal.clear();
					cal.set(year, q, 1);
					return new Timestamp(cal.getTimeInMillis());
				}
				else if (s.equalsIgnoreCase("MONTH") || s.equalsIgnoreCase("MON") || 
							s.equalsIgnoreCase("MM") || s.equalsIgnoreCase("RM")) {
					if (d instanceof java.sql.Time) {
						throw new ParseException(TIME_EXCEPTION);
					}
					int year = cal.get(YEAR);
					int month = cal.get(MONTH);
					int day = cal.get(DAY_OF_MONTH);
					if (day > 15) {
						month++;
					}
					cal.clear();
					cal.set(year, month, 1);
					return new Timestamp(cal.getTimeInMillis());
				}
				else if (s.equalsIgnoreCase("WW")) {
					if (d instanceof java.sql.Time) {
						throw new ParseException(TIME_EXCEPTION);
					}
					int year = cal.get(YEAR);
					int month = cal.get(MONTH);
					int day = cal.get(DAY_OF_MONTH);
					int dw = cal.get(DAY_OF_WEEK);
					cal.clear();
					cal.set(year, 0, 1);
					int dayOfWeek = cal.get(DAY_OF_WEEK);
					int delta = (dw<dayOfWeek ? 7-(dayOfWeek-dw) : dw-dayOfWeek);
					if (delta > 2) {
						delta = delta-7;
					}
					cal.set(year, month, day-delta);
					return new Timestamp(cal.getTimeInMillis());
				}
				else if (s.equalsIgnoreCase("W")) {
					if (d instanceof java.sql.Time) {
						throw new ParseException(TIME_EXCEPTION);
					}
					int year = cal.get(YEAR);
					int month = cal.get(MONTH);
					int day = cal.get(DAY_OF_MONTH);
					int dw = cal.get(DAY_OF_WEEK);
					cal.clear();
					cal.set(year, month, 1);
					int dayOfWeek = cal.get(DAY_OF_WEEK);
					int delta = (dw<dayOfWeek ? 7-(dayOfWeek-dw) : dw-dayOfWeek);
					if (delta > 2) {
						delta = delta-7;
					}
					cal.set(year, month, day-delta);
					return new Timestamp(cal.getTimeInMillis());
				}
				else if (s.equalsIgnoreCase("IW")) {
					throw new ParseException(NOT_IMPLIMENTED_EXCEPTION);
				}
				else if (s.equalsIgnoreCase("DAY") || s.equalsIgnoreCase("DY") || s.equalsIgnoreCase("D")) {
					if (d instanceof java.sql.Time) {
						throw new ParseException(TIME_EXCEPTION);
					}
					int year = cal.get(YEAR);
					int month = cal.get(MONTH);
					int day = cal.get(DAY_OF_MONTH);
					int dw = cal.get(DAY_OF_WEEK);
					int delta = dw-cal.getFirstDayOfWeek();
					if (delta > 2) {
						delta = delta-7;
					}
					cal.clear();
					cal.set(year, month, day-delta);
					return new Timestamp(cal.getTimeInMillis());
				}
				else if (s.equalsIgnoreCase("HH") || s.equalsIgnoreCase("HH12") || s.equalsIgnoreCase("HH24")) {
					if (d instanceof java.sql.Date) {
						throw new ParseException(DATE_EXCEPTION);
					}
					int minute = cal.get(MINUTE);
					if (minute >= 30) {
						cal.get(HOUR_OF_DAY);
						cal.add(HOUR_OF_DAY, 1);
					}
					cal.set(MINUTE, 0);
					cal.set(SECOND, 0);
					cal.set(MILLISECOND, 0);
					return new Timestamp(cal.getTimeInMillis());
				}
				else if (s.equalsIgnoreCase("MI")) {
					if (d instanceof java.sql.Date) {
						throw new ParseException(DATE_EXCEPTION);
					}
					int second = cal.get(SECOND);
					if (second >= 30) {
						cal.get(MINUTE);
						cal.add(MINUTE, 1);
					}
					cal.set(SECOND, 0);
					cal.set(MILLISECOND, 0);
					return new Timestamp(cal.getTimeInMillis());
				} else {
					throw new ParseException(FORMAT_EXCEPTION);
				}
			}
		}
		throw new ParseException(WRONG_TYPE+" trunc("+param1.getClass()+","+param2.getClass()+")");
	}

	public Comparable<?> getResult(Comparable<?>... comparables)
			throws ParseException {
		if(comparables.length ==1){
			return round(comparables[0],JepRuntime.getCalendar());
		}else{
			return round(comparables[0],comparables[2],JepRuntime.getCalendar());
		}
	}
}
