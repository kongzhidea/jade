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

import java.text.DateFormatSymbols;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Calendar;

public final class OracleTimeFormat extends OracleTimestampFormat {	
	
    private static final long serialVersionUID = 1L;

    protected OracleTimeFormat(ArrayList<Object> format, Calendar calendar, DateFormatSymbols dateSymb) {
		this.format = format;
		this.cal = calendar;
		this.symb = dateSymb;
	}
	
	public OracleTimeFormat(String pattern) throws java.text.ParseException {
		cal = Calendar.getInstance();
		symb = new DateFormatSymbols();
		format = formatsCache.get(pattern);
		if (format == null) {
			format = new ArrayList<Object>();
			compilePattern(format, timeSymbols, pattern);
			formatsCache.put(pattern, format);
		}
	}

	public OracleTimeFormat(String pattern, Calendar calendar, DateFormatSymbols dateSymb) throws java.text.ParseException {
		cal = calendar;
		symb = dateSymb;
		format = formatsCache.get(pattern);
		if (format == null) {
			format = new ArrayList<Object>();
			compilePattern(format, timeSymbols, pattern);
			formatsCache.put(pattern, format);
		}
	}

	public Object parseObject(String source, ParsePosition pos) {
		return new java.sql.Time(parseInMillis(source, pos));
	}
}

