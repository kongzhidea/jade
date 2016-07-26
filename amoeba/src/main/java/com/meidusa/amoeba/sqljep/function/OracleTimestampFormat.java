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

import static java.util.Calendar.AM;
import static java.util.Calendar.AM_PM;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.ERA;
import static java.util.Calendar.HOUR;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.PM;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.WEEK_OF_YEAR;
import static java.util.Calendar.YEAR;

import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;

public class OracleTimestampFormat extends Format {	
    private static final long serialVersionUID = 1L;    
    private static final String PATTERN_EXCEPTION = "Wrong pattern";
	private static final String FORMAT_EXCEPTION = "Wrong pattern";
	private static final String NOT_IMPLIMENTED_EXCEPTION = "Not implimented";
	private static final String BAD_INPUT_PATTERN = "Format code cannot appear in date input format";
	private static final String BAD_HH12 = "Hour must be between 1 and 12";

	protected static final Hashtable<String, ArrayList<Object>> formatsCache = new Hashtable<String, ArrayList<Object>>();
	protected static final ArrayList<DATE> dateSymbols = new ArrayList<DATE>();
	protected static final ArrayList<DATE> timeSymbols = new ArrayList<DATE>();
	protected ArrayList<Object> format = null;
	
	protected Calendar cal;
	protected DateFormatSymbols symb;
	
	public static abstract class DATE {
		abstract public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) 
				throws java.text.ParseException;
		abstract public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) 
				throws java.text.ParseException;
		public boolean equals(Comparable<?>  obj) {
			if (obj == null) {
				return false;
			}
			return (obj.getClass() == this.getClass());
		}
	};
	
	public static abstract class TIME extends DATE {
	};

	// ERA
	public static final class fBC extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			String[] eras = symb.getEras();
			int era = cal.get(ERA);
			str.append(eras[era]);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			String[] eras = symb.getEras();
			int idx = pos.getIndex();
			for (int i = 0; i < eras.length; i++) {
				int len = eras[i].length();
				if (source.regionMatches(false, idx, eras[i], 0, len)) {
					pos.setIndex(idx+len);
					cal.set(ERA, i);
					return;
				}
			}
			throw new java.text.ParseException("", 0);
		}
		public String toString() {
			return "BC";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new fBC());
	}

	public static final class fAD extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			String[] eras = symb.getEras();
			int era = cal.get(ERA);
			str.append(eras[era]);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			String[] eras = symb.getEras();
			int idx = pos.getIndex();
			for (int i = 0; i < eras.length; i++) {
				int len = eras[i].length();
				if (source.regionMatches(true, idx, eras[i], 0, len)) {
					pos.setIndex(idx+len);
					cal.set(ERA, i);
					return;
				}
			}
			throw new java.text.ParseException("", 0);
		}
		public String toString() {
			return "AD";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new fAD());
	}

	public static final class BdCd extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int era = cal.get(ERA);
			str.append(era == GregorianCalendar.BC ? "b.c." : "a.d.");
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			int i = pos.getIndex();
			String t = source.substring(i, i+4);
			if (toString().equals(t.toUpperCase())) {
				pos.setIndex(i+4);
				cal.set(ERA, GregorianCalendar.BC);
			}
			else if (t.toUpperCase().equals("A.D.")) {
				pos.setIndex(i+4);
				cal.set(ERA, GregorianCalendar.AD);
			} else {
				throw new java.text.ParseException("", 0);
			}
		}
		public String toString() {
			return "B.C.";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new BdCd());
	}

	public static final class AdDd extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int era = cal.get(ERA);
			str.append(era == GregorianCalendar.AD ? "a.d." : "b.c.");
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			int i = pos.getIndex();
			String t = source.substring(i, i+4);
			if (toString().equals(t.toUpperCase())) {
				pos.setIndex(i+4);
				cal.set(ERA, GregorianCalendar.AD);
			}
			else if (t.toUpperCase().equals("B.C.")) {
				pos.setIndex(i+4);
				cal.set(ERA, GregorianCalendar.BC);
			} else {
				throw new java.text.ParseException("", 0);
			}
		}
		public String toString() {
			return "A.D.";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new AdDd());
	}
	
	// CENTURE

	public static final class SCC extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int year = cal.get(YEAR);
			String cc = Integer.toString((year/100)*100);
			if (cc.length() == 1) {
				str.append('0');
			}
			str.append(cc);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			throw new java.text.ParseException(BAD_INPUT_PATTERN, 0);
		}
		public String toString() {
			return "SCC";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new SCC());
	}

	public static final class CC extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int year = cal.get(YEAR);
			String cc = Integer.toString(Math.abs((year/100)*100));
			if (cc.length() == 1) {
				str.append('0');
			}
			str.append(cc);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			throw new java.text.ParseException(BAD_INPUT_PATTERN, 0);
		}
		public String toString() {
			return "CC";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new CC());
	}
	
	// YEAR

	public static final class SYYYY extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int year = cal.get(YEAR);
			String y = Integer.toString(year);
			for (int i = y.length(); i < 4; i++) {
				str.append('0');
			}
			str.append(y);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			boolean negate = false;
			if (getSign(source, pos) == '-') {
				negate = true;
			}
			int year = getNumber(source, pos, 4);
			if (negate) {
				year = -year;
			}
			cal.set(YEAR, year);
		}
		public String toString() {
			return "SYYYY";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new SYYYY());
	}

	public static final class YYYY extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int year = cal.get(YEAR);
			String y = Integer.toString(Math.abs(year));
			for (int i = y.length(); i < 4; i++) {
				str.append('0');
			}
			str.append(y);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			int year = getNumber(source, pos, 4);
			cal.set(YEAR, year);
		}
		public String toString() {
			return "YYYY";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new YYYY());
	}

	public static final class YcYYY extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int year = cal.get(YEAR);
			String y = Integer.toString(year/1000);
			str.append(y+',');
			String yyy = Integer.toString(year-(year/1000)*1000);
			for (int i = y.length(); i < 3; i++) {
				str.append('0');
			}
			str.append(yyy);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			getNumber(source, pos, 1);
			int i = pos.getIndex();
			String d = source.charAt(i-1)+"";
			int year;
			if (source.charAt(i) == ',') {
				++i;
				pos.setIndex(i);
				getNumber(source, pos, 3);
				d += source.substring(i, pos.getIndex());
				year = Integer.valueOf(d);
			} else {
				pos.setIndex(i-1);
				year = getNumber(source, pos, 4);
			}
			cal.set(YEAR, year);
		}
		public String toString() {
			return "Y,YYY";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new YcYYY());
	}

	public static final class YYY extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int year = cal.get(YEAR);
			String y = Integer.toString(year - (year/1000)*1000);
			for (int i = y.length(); i < 3; i++) {
				str.append('0');
			}
			str.append(y);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			int year = getNumber(source, pos, 3);
			int y = (cal.get(YEAR)/1000)*1000;
			cal.set(YEAR, y+year);
		}
		public String toString() {
			return "YYY";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new YYY());
	}

	public static final class YY extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int year = cal.get(YEAR);
			String y = Integer.toString(year - (year/100)*100);
			for (int i = y.length(); i < 2; i++) {
				str.append('0');
			}
			str.append(y);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			int year = getNumber(source, pos, 2);
			int y = (cal.get(YEAR)/100)*100;
			cal.set(YEAR, y+year);
		}
		public String toString() {
			return "YY";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new YY());
	}

	public static final class Y extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int year = cal.get(YEAR);
			String y = Integer.toString(year - (year/10)*10);
			str.append(y);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			int year = getNumber(source, pos, 1);
			int y = (cal.get(YEAR)/10)*10;
			cal.set(YEAR, y+year);
		}
		public String toString() {
			return "Y";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new Y());
	}
	
	public static final class SYEAR extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			throw new java.text.ParseException(NOT_IMPLIMENTED_EXCEPTION, 0);
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			throw new java.text.ParseException(BAD_INPUT_PATTERN, 0);
		}
		public String toString() {
			return "SYEAR";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new SYEAR());
	}

	public static final class YEAR extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			throw new java.text.ParseException(NOT_IMPLIMENTED_EXCEPTION, 0);
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			throw new java.text.ParseException(BAD_INPUT_PATTERN, 0);
		}
		public String toString() {
			return "YEAR";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new YEAR());
	}

	public static final class IYYY extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			throw new java.text.ParseException(NOT_IMPLIMENTED_EXCEPTION, 0);
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			throw new java.text.ParseException(NOT_IMPLIMENTED_EXCEPTION, 0);
		}
		public String toString() {
			return "IYYY";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new IYYY());
	}

	public static final class IY extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			throw new java.text.ParseException(NOT_IMPLIMENTED_EXCEPTION, 0);
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			throw new java.text.ParseException(NOT_IMPLIMENTED_EXCEPTION, 0);
		}
		public String toString() {
			return "IY";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new IY());
	}

	public static final class I extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			throw new java.text.ParseException(NOT_IMPLIMENTED_EXCEPTION, 0);
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			throw new java.text.ParseException(NOT_IMPLIMENTED_EXCEPTION, 0);
		}
		public String toString() {
			return "I";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new I());
	}

	public static final class RRRR extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			throw new java.text.ParseException(NOT_IMPLIMENTED_EXCEPTION, 0);
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			throw new java.text.ParseException(NOT_IMPLIMENTED_EXCEPTION, 0);
		}
		public String toString() {
			return "RRRR";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new RRRR());
	}

	public static final class RR extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			throw new java.text.ParseException(NOT_IMPLIMENTED_EXCEPTION, 0);
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			throw new java.text.ParseException(NOT_IMPLIMENTED_EXCEPTION, 0);
		}
		public String toString() {
			return "RR";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new RR());
	}

	public static final class Q extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int month = cal.get(MONTH);
			String y = Integer.toString((month/3)*3);
			str.append(y);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			throw new java.text.ParseException(BAD_INPUT_PATTERN, 0);
		}
		public String toString() {
			return "Q";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new Q());
	}

	public static final class MONTH extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int month = cal.get(MONTH);
			String[] months = symb.getMonths();
			str.append(months[month]);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			String[] months = symb.getMonths();
			int idx = pos.getIndex();
			for (int i = 0; i < months.length; i++) {
				int len = months[i].length();
				if (source.regionMatches(true, idx, months[i], 0, len)) {
					pos.setIndex(idx+len);
					cal.set(MONTH, i);
					return;
				}
			}
			throw new java.text.ParseException("", 0);
		}
		public String toString() {
			return "MONTH";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new MONTH());
	}

	public static final class MON extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int month = cal.get(MONTH);
			String[] months = symb.getShortMonths();
			str.append(months[month]);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			String[] months = symb.getShortMonths();
			int idx = pos.getIndex();
			for (int i = 0; i < months.length; i++) {
				int len = months[i].length();
				if (source.regionMatches(true, idx, months[i], 0, len)) {
					pos.setIndex(idx+len);
					cal.set(MONTH, i);
					return;
				}
			}
			throw new java.text.ParseException("", 0);
		}
		public String toString() {
			return "MON";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new MON());
	}

	public static final class MM extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int month = cal.get(MONTH)+1;
			String m = Integer.toString(month);
			if (m.length() == 1) {
				str.append('0');
			}
			str.append(m);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			int month = getNumber(source, pos, 2)-1;
			cal.set(MONTH, month);
		}
		public String toString() {
			return "MM";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new MM());
	}

	public static final class RM extends DATE {
		private final static String rmonths[] = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII", "XIII"};
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int month = cal.get(MONTH);
			str.append(rmonths[month]);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			int idx = pos.getIndex();
			for (int i = 0; i < rmonths.length; i++) {
				int len = rmonths[i].length();
				if (source.regionMatches(true, idx, rmonths[i], 0, len)) {
					pos.setIndex(idx+len);
					cal.set(MONTH, i);
					return;
				}
			}
			throw new java.text.ParseException("", 0);
		}
		public String toString() {
			return "RM";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new RM());
	}

	public static final class WW extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int month = cal.get(WEEK_OF_YEAR);
			String m = Integer.toString(month);
			if (m.length() == 1) {
				str.append('0');
			}
			str.append(m);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			throw new java.text.ParseException(BAD_INPUT_PATTERN, 0);
		}
		public String toString() {
			return "WW";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new WW());
	}

	public static final class IW extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			throw new java.text.ParseException(NOT_IMPLIMENTED_EXCEPTION, 0);
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			throw new java.text.ParseException(BAD_INPUT_PATTERN, 0);
		}
		public String toString() {
			return "IW";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new IW());
	}

	public static final class DAY extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int day = cal.get(DAY_OF_WEEK);
			String[] week = symb.getWeekdays();
			str.append(week[day]);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			String[] months = symb.getWeekdays();
			int idx = pos.getIndex();
			for (int i = 0; i < months.length; i++) {
				int len = months[i].length();
				if (source.regionMatches(true, idx, months[i], 0, len)) {
					pos.setIndex(idx+len);
					return;
				}
			}
			throw new java.text.ParseException("", 0);
		}
		public String toString() {
			return "DAY";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new DAY());
	}

	public static final class DY extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int day = cal.get(DAY_OF_WEEK);
			String[] week = symb.getShortWeekdays();
			str.append(week[day]);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			String[] months = symb.getShortWeekdays();
			int idx = pos.getIndex();
			for (int i = 0; i < months.length; i++) {
				int len = months[i].length();
				if (source.regionMatches(true, idx, months[i], 0, len)) {
					pos.setIndex(idx+len);
					return;
				}
			}
			throw new java.text.ParseException("", 0);
		}
		public String toString() {
			return "DY";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new DY());
	}

	public static final class DDD extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int day = cal.get(DAY_OF_YEAR);
			String d = Integer.toString(day);
			for (int i = d.length(); i < 3; i++) {
				str.append('0');
			}
			str.append(d);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			int month = getNumber(source, pos, 3);
			cal.set(DAY_OF_YEAR, month);
		}
		public String toString() {
			return "DDD";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new DDD());
	}

	public static final class DD extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int day = cal.get(DAY_OF_MONTH);
			String d = Integer.toString(day);
			if (d.length() == 1) {
				str.append('0');
			}
			str.append(d);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			int day = getNumber(source, pos, 2);
			cal.set(DAY_OF_MONTH, day);
		}
		public String toString() {
			return "DD";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new DD());
	}

	public static final class D extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int day = cal.get(DAY_OF_WEEK);
			int t = day-cal.getFirstDayOfWeek();
			String d = Integer.toString((t < 0) ? 7 : t+1);
			str.append(d);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			getNumber(source, pos, 1);
			return;
		}
		public String toString() {
			return "D";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new D());
	}

	public static final class J extends DATE {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			throw new java.text.ParseException(NOT_IMPLIMENTED_EXCEPTION, 0);
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			throw new java.text.ParseException(NOT_IMPLIMENTED_EXCEPTION, 0);
		}
		public String toString() {
			return "J";
		}
	};
	static {
		OracleTimestampFormat.dateSymbols.add(new J());
	}

	public static final class HH12 extends TIME {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int hour = cal.get(HOUR);
			String h = Integer.toString(hour);
			if (h.length() == 1) {
				str.append('0');
			}
			str.append(h);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			int hour = getNumber(source, pos, 2);
			if (hour > 0 && hour < 13) {
				cal.set(HOUR, hour);
			} else {
				throw new java.text.ParseException(BAD_HH12, 0);
			}
		}
		public String toString() {
			return "HH12";
		}
	};
	static {
		OracleTimestampFormat.timeSymbols.add(new HH12());
	}
	
	public static final class HH24 extends TIME {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int hour = cal.get(HOUR_OF_DAY);
			String h = Integer.toString(hour);
			if (h.length() == 1) {
				str.append('0');
			}
			str.append(h);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			int hour = getNumber(source, pos, 2);
			cal.set(HOUR_OF_DAY, hour);
		}
		public String toString() {
			return "HH24";
		}
	};
	static {
		OracleTimestampFormat.timeSymbols.add(new HH24());
	}
	
	public static final class HH extends TIME {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int hour = cal.get(HOUR);
			String h = Integer.toString(hour);
			if (h.length() == 1) {
				str.append('0');
			}
			str.append(h);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			int hour = getNumber(source, pos, 2);
			if (hour > 0 && hour < 13) {
				cal.set(HOUR, hour);
			} else {
				throw new java.text.ParseException(BAD_HH12, 0);
			}
		}
		public String toString() {
			return "HH";
		}
	};
	static {
		OracleTimestampFormat.timeSymbols.add(new HH());
	}

	public static final class fAM extends TIME {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			String[] ampm = symb.getAmPmStrings();
			int am = cal.get(AM_PM);
			str.append(ampm[am]);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			String[] ampm = symb.getAmPmStrings();
			int idx = pos.getIndex();
			for (int i = 0; i < ampm.length; i++) {
				int len = ampm[i].length();
				if (source.regionMatches(true, idx, ampm[i], 0, len)) {
					pos.setIndex(idx+len);
					cal.set(ERA, i);
					return;
				}
			}
			throw new java.text.ParseException("", 0);
		}
		public String toString() {
			return "AM";
		}
	};
	static {
		OracleTimestampFormat.timeSymbols.add(new fAM());
	}

	public static final class fPM extends TIME {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			String[] ampm = symb.getAmPmStrings();
			int am = cal.get(AM_PM);
			str.append(ampm[am]);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			String[] ampm = symb.getAmPmStrings();
			int idx = pos.getIndex();
			for (int i = 0; i < ampm.length; i++) {
				int len = ampm[i].length();
				if (source.regionMatches(true, idx, ampm[i], 0, len)) {
					pos.setIndex(idx+len);
					cal.set(ERA, i);
					return;
				}
			}
			throw new java.text.ParseException("", 0);
		}
		public String toString() {
			return "PM";
		}
	};
	static {
		OracleTimestampFormat.timeSymbols.add(new fPM());
	}

	public static final class PdMd extends TIME {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int am = cal.get(AM_PM);
			str.append((am == AM) ? "a.m." : "p.m.");
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			int i = pos.getIndex();
			String t = source.substring(i, i+4);
			if (toString().equals(t.toUpperCase())) {
				pos.setIndex(i+4);
				cal.set(AM_PM, PM);
			}
			else if (t.toUpperCase().equals("A.M.")) {
				pos.setIndex(i+4);
				cal.set(AM_PM, AM);
			} else {
				throw new java.text.ParseException("", 0);
			}
		}
		public String toString() {
			return "P.M.";
		}
	};
	static {
		OracleTimestampFormat.timeSymbols.add(new PdMd());
	}

	public static final class AdMd extends TIME {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int am = cal.get(AM_PM);
			str.append((am == AM) ? "a.m." : "p.m.");
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			int i = pos.getIndex();
			String t = source.substring(i, i+4);
			if (toString().equals(t.toUpperCase())) {
				pos.setIndex(i+4);
				cal.set(AM_PM, AM);
			}
			else if (t.toUpperCase().equals("P.M.")) {
				pos.setIndex(i+4);
				cal.set(AM_PM, PM);
			} else {
				throw new java.text.ParseException("", 0);
			}
		}
		public String toString() {
			return "A.M.";
		}
	};
	static {
		OracleTimestampFormat.timeSymbols.add(new AdMd());
	}

	public static final class MI extends TIME {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int minute = cal.get(MINUTE);
			String m = Integer.toString(minute);
			if (m.length() == 1) {
				str.append('0');
			}
			str.append(m);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			int minute = getNumber(source, pos, 2);
			cal.set(MINUTE, minute);
		}
		public String toString() {
			return "MI";
		}
	};
	static {
		OracleTimestampFormat.timeSymbols.add(new MI());
	}

	public static final class SSSSS extends TIME {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int hour = cal.get(HOUR);
			int minute = cal.get(MINUTE);
			int second = cal.get(SECOND);
			String s = Integer.toString(hour*60*60+minute*60+second);
			for (int i = s.length(); i < 5; i++) {
				str.append('0');
			}
			str.append(s);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			int sssss = getNumber(source, pos, 5);
			int hour = sssss/3600;
			sssss -= hour;
			int minute = sssss/60;
			int second = sssss-minute;
			cal.set(HOUR, hour);
			cal.set(MINUTE, minute);
			cal.set(SECOND, second);
		}
		public String toString() {
			return "SSSSS";
		}
	};
	static {
		OracleTimestampFormat.timeSymbols.add(new SSSSS());
	}

	public static final class SS extends TIME {
		public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
			int second = cal.get(SECOND);
			String s = Integer.toString(second);
			if (s.length() == 1) {
				str.append('0');
			}
			str.append(s);
			return str;
		}
		public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
			int second = getNumber(source, pos, 2);
			cal.set(SECOND, second);
		}
		public String toString() {
			return "SS";
		}
	};
	static {
		OracleTimestampFormat.timeSymbols.add(new SS());
	}

	protected OracleTimestampFormat() {
	}
	
	@SuppressWarnings("unchecked")
    public OracleTimestampFormat(String pattern) throws java.text.ParseException {
		cal = Calendar.getInstance();
		symb = new DateFormatSymbols();
		format = formatsCache.get(pattern);
		if (format == null) {
			format = new ArrayList<Object>();
			ArrayList<DATE> symb = (ArrayList<DATE>)dateSymbols.clone();
			symb.addAll(timeSymbols);
			compilePattern(format, symb, pattern);
			formatsCache.put(pattern, format);
		}
	}

	@SuppressWarnings("unchecked")
    public OracleTimestampFormat(String pattern, Calendar calendar, DateFormatSymbols dateSymb) throws java.text.ParseException {
		cal = calendar;
		symb = dateSymb;
		format = formatsCache.get(pattern);
		if (format == null) {
			format = new ArrayList<Object>();
			ArrayList<DATE> symb = (ArrayList<DATE>)dateSymbols.clone();
			symb.addAll(timeSymbols);
			compilePattern(format, symb, pattern);
			formatsCache.put(pattern, format);
		}
	}
	
	public OracleTimeFormat getTimeFormat() {
		ArrayList<Object> f = new ArrayList<Object>();
		int first = 0;
		int last = f.size();
		for (int i = 0; i < f.size(); i++) {
			Object obj = format.get(i);
			if (obj instanceof TIME) {
				if (i > first) {
					first = i;
				}
				else if (i < last) {
					last = i;
				}
			}
		}
		for (int i = first; i < last; i++) {
			f.add(format.get(i));
		}
		return new OracleTimeFormat(f, cal, symb);
	}
	
	public OracleDateFormat getDateFormat() {
		ArrayList<Object> f = new ArrayList<Object>();
		int first = 0;
		int last = f.size();
		for (int i = 0; i < f.size(); i++) {
			Object obj = format.get(i);
			if (obj instanceof DATE) {
				if (i > first) {
					first = i;
				}
				else if (i < last) {
					last = i;
				}
			}
		}
		for (int i = first; i < last; i++) {
			f.add(format.get(i));
		}
		return new OracleDateFormat(f, cal, symb);
	}

	public String toString() {
		if (format != null) {
			StringBuilder str = new StringBuilder();
			for (Object obj : format) {
				str.append(obj.toString());
			}
			return str.toString();
		} else {
			return "null";
		}
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		OracleTimestampFormat other = (OracleTimestampFormat)obj;
		return (format != null) ? format.equals(other.format) : false;
	}

	protected static void compilePattern(ArrayList<Object> format, ArrayList<DATE> symbols, String pattern) throws java.text.ParseException {
		if (pattern == null) {
			throw new java.text.ParseException(PATTERN_EXCEPTION, 0);
		}
		StringBuilder fill = new StringBuilder();
		final int plen = pattern.length();
		for (int i = 0; i < plen; ) {
			boolean f = false;
			for (DATE d : symbols) {
				String symb = d.toString();
				if (i+symb.length() <= plen && pattern.regionMatches(true, i, symb, 0, symb.length())) {
					if (fill.length() > 0) {
						format.add(fill.toString());
						fill.setLength(0);
					}
					format.add(d);
					i += symb.length();
					f = true;
					break;
				}
			}
			if (!f) {
				char c = pattern.charAt(i);
				if (!Character.isLetterOrDigit(c)) {
					fill.append(c);
					i++;
				} else {
					throw new java.text.ParseException(PATTERN_EXCEPTION, i);
				}
			}
		}
		if (fill.length() > 0) {
			format.add(fill.toString());
		}
	}

	public StringBuffer format(Object obj, StringBuffer str, FieldPosition fieldPosition) {
		if (obj instanceof java.util.Date) {
			java.util.Date date = (java.util.Date)obj;
			cal.setTime(date);
			if (format != null) {
				for (Object f : format) {
					if (f instanceof String) {
						str.append((String)f);
					} else {
						DATE d = (DATE)f;
						try {
							d.toString(str, cal, symb);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return str;
	}
	
	public Object parseObject(String source, ParsePosition pos) {
		return new java.sql.Timestamp(parseInMillis(source, pos));
	}
	
	protected long parseInMillis(String source, ParsePosition pos) {
		int year = cal.get(YEAR);
		int month = cal.get(MONTH);
		cal.clear();
		cal.set(YEAR, year);
		cal.set(MONTH, month);
		final int slen = source.length();
		try {
			if (format != null) {
				for (Object obj : format) {
					if (obj instanceof DATE) {
						DATE d = (DATE)obj;
						d.parse(cal, symb, source, pos);
					}
				}
				for (int i = pos.getIndex(); i < slen; i++) {
					final char c = source.charAt(i);
					if (Character.isLetterOrDigit(c)) {
						throw new java.text.ParseException(FORMAT_EXCEPTION, 0);
					}
				}
				pos.setIndex(slen);
			}
		} catch (Exception e) {
//			e.printStackTrace();
			pos.setErrorIndex(pos.getIndex());
			pos.setIndex(0);
			return 0;
		}
		return cal.getTimeInMillis();
	}
	
	private static char getSign(String source, ParsePosition pos) throws java.text.ParseException {
		final int len = source.length();
		int i;
		for (i = pos.getIndex(); i < len; i++) {
			final char c = source.charAt(i);
			if (c == '-' || c == '+') {
				pos.setIndex(i);
				return c;
			}
			if (Character.isDigit(c)) {
				pos.setIndex(i);
				return '+';
			}
			if (Character.isLetter(c)) {
				pos.setIndex(i);
				throw new java.text.ParseException("", i);
			}
		}
		throw new java.text.ParseException("", i);
	}
	
	private static int getNumber(String source, ParsePosition pos, int digits) 
				throws java.text.ParseException, NumberFormatException {
		final int len = source.length();
		String d = "";
		int i;
		for (i = pos.getIndex(); i < len; i++) {
			final char c = source.charAt(i);
			if (Character.isDigit(c)) {
				break;
			}
			else if (Character.isLetter(c)) {
				pos.setIndex(i);
				throw new java.text.ParseException("", i);
			}
		}
		for (; i < len; i++) {
			final char c = source.charAt(i);
			if (Character.isDigit(c)) {
				d += c;
				if (d.length() == digits) {
					pos.setIndex(i+1);
					return Integer.valueOf(d);
				}
			}
			else if (Character.isLetter(c)) {
				pos.setIndex(i);
				throw new java.text.ParseException("", i);
			} else {
				break;
			}
		}
		pos.setIndex(i);
		return Integer.valueOf(d);
	}
}

