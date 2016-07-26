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

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import com.meidusa.amoeba.sqljep.ASTFunNode;
import com.meidusa.amoeba.sqljep.BaseJEP;
import com.meidusa.amoeba.sqljep.JepRuntime;
import com.meidusa.amoeba.sqljep.ParseException;

public class ToDate extends PostfixCommand {
	static final String PARAM_EXCEPTION = "Format shoud be string";
	private static final String FORMAT_EXCEPTION = "Wrong timestamp";
	
	final public int getNumberOfParameters() {
		return -1;
	}
	
	public Comparable<?>[] evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		int num = node.jjtGetNumChildren();
		if (num == 1) {
			Comparable<?>  param1 = runtime.stack.pop();
			return new  Comparable<?>[]{param1};
		}
		else if (num == 2) {
			Comparable<?>  param2 = runtime.stack.pop();
			Comparable<?>  param1 = runtime.stack.pop();
			return new  Comparable<?>[]{param1,param2};
		} else {
			// remove all parameters from stack and push null
			removeParams(runtime.stack, num);
			throw new ParseException("Wrong number of parameters for instr");
		}
	}
	
	public static java.util.Date to_date(Comparable<?>  param1) throws ParseException {
		if (param1 == null) {
			return null;
		}
		else if (param1 instanceof String) {
			return Timestamp.valueOf((String)param1);
		} else {
			throw new ParseException(FORMAT_EXCEPTION);
		}
	}

	public static java.util.Date to_date(Comparable<?>  param1, Comparable<?>  param2) throws ParseException {
		if (param1 == null || param2 == null) {
			return null;
		}
		if (param1 instanceof String && ((String)param1).length() == 0) {
			return null;
		}
		if (param1 instanceof java.util.Date) {
			return (java.util.Date)param1;
		}
		if (!(param1 instanceof String) || !(param2 instanceof String)) {
			throw new ParseException(WRONG_TYPE+"  to_date("+param1.getClass()+","+param2.getClass()+")");
		}
		try {
			OracleTimestampFormat format = new OracleTimestampFormat((String)param2);
			return (java.util.Date)format.parseObject((String)param1);
		} catch (java.text.ParseException e) {
			if (BaseJEP.debug) {
				e.printStackTrace();
			}
			throw new ParseException(e.getMessage());
		}
	}
	
	public static void main(String[] args) throws ParseException{
		System.out.println(to_date("99-00-00 00:00:0000"));
		
		Date.valueOf("1999-11-21").getTime();
		Calendar long2 = Calendar.getInstance();
		long2.setTime(Time.valueOf("00:01:0000"));
		Calendar cal = Calendar.getInstance();
		cal.setTime(Date.valueOf("1999-11-21"));
		
		cal.add(Calendar.HOUR, long2.get(Calendar.HOUR));
		cal.add(Calendar.MINUTE, long2.get(Calendar.MINUTE));
		cal.add(Calendar.SECOND, long2.get(Calendar.SECOND));
		cal.add(Calendar.MILLISECOND, long2.get(Calendar.MILLISECOND));
		
		System.out.println(cal.getTime());
		System.out.println(Date.valueOf("1999-11-21").getTime());
		System.out.println(Time.valueOf("00:00:0000").getTime());
	}

	public Comparable<?> getResult(Comparable<?>... comparables)
			throws ParseException {
		if(comparables.length == 1){
			if (comparables[0] instanceof String) {
				return (Timestamp.valueOf((String)comparables[0]));
			} else {
				throw new ParseException(FORMAT_EXCEPTION);
			}
		}else{
			return to_date(comparables[0],comparables[1]);
		}
	}
}
