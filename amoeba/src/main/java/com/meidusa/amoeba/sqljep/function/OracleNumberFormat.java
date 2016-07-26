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
import java.math.BigInteger;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Hashtable;

public final class OracleNumberFormat extends Format {

	private static final long serialVersionUID = 1L;
	private static final String PATTERN_EXCEPTION = "Wrong pattern";
	public static final String NOT_IMPLIMENTED_EXCEPTION = "Not implimented";

	final static char DOT = '.';
	final static char GROUP = ' ';

	private static final Hashtable<String, FORMAT> formatsCache = new Hashtable<String, FORMAT>();
	private FORMAT format = null;
	
	public static enum SIGN {
		DEFAULT,
		MI,
		_S,
		S_,
		PR
	};
	public static enum CURRENCY {
		NON,
		DOLLARS,
		LOCAL,
		ISO
	};

	private static class FORMAT {
		boolean sci;
		boolean localGroups = true;		// for D and G  
		String numbers = "";
		int digits = 0; 				// number of digits in 'numbers' member
		int firstNine = -1;			// position in 'numbers' member the first 9 digit
		SIGN sign = SIGN.DEFAULT;
		boolean fm = false;
		boolean b = false;
		CURRENCY cur = CURRENCY.NON;
		int v = 0;
		int dot = 0;

		public void getPrefix(StringBuffer s, BigDecimal n) {
			if (sci && !fm) {
				s.append(' ');
			}
			if (n.signum() == 1) {
				if (sign == SIGN.S_) {
					s.append('+');
				}
				else if (!fm && sign != SIGN._S && sign != SIGN.MI) {
					s.append(' ');
				}
			}
			else if (n.signum() == -1) {
				if (sign == SIGN.PR) {
					s.append('<');
				}
				else if (sign != SIGN._S && sign != SIGN.MI) {
					s.append('-');
				}
			}
			else if (!fm) {
				s.append(' ');
			}
			if (cur == CURRENCY.DOLLARS) {
				s.append("$");
			}
	//			else if (cur == CURRENCY.LOCAL) {
	//				s.append("L");
	//			}
			else if (cur == CURRENCY.ISO) {
				s.append("RUR");
			}
		}
	
		public void getSuffix(StringBuffer s, BigDecimal n) {
			if (cur == CURRENCY.LOCAL) {
				s.append("痼?");
			}
			if (n.signum() == 1) {
				if (sign == SIGN._S) {
					s.append('+');
				}
				else if (sign == SIGN.MI) {
					s.append(' ');
				}
			}
			else if (n.signum() == -1) {
				if (sign == SIGN.PR) {
					s.append('>');
				}
				else if (sign == SIGN.MI || sign == SIGN._S) {
					s.append('-');
				}
			}
		}
		
		public String toString() {
			StringBuilder s = new StringBuilder();
			if (sign == SIGN.S_) {
				s.append('S');
			}
			if (fm) {
				s.append("FM");
			}
			if (b) {
				s.append('B');
			}
			if (cur == CURRENCY.DOLLARS) {
				s.append('$');
			}
			else if (cur == CURRENCY.LOCAL) {
				s.append('L');
			}
			else if (cur == CURRENCY.ISO) {
				s.append('C');
			}
			s.append(numbers);
			if (dot > 0) {
				s.append('D');
				for (int i = 0; i < dot; i++) {
					s.append('9');
				}
			}
			else if (v > 0) {
				s.append('V');
				for (int i = 0; i < v; i++) {
					s.append('9');
				}
			}
			if (sci) {
				s.append("EEEE");
			}
			if (sign == SIGN._S) {
				s.append('S');
			}
			else if (sign == SIGN.MI) {
				s.append("MI");
			}
			return s.toString();
		}
		
		public boolean equals(Comparable<?>  obj) {
			if (obj == null) {
				return false;
			}
			FORMAT other = (FORMAT)obj;
			return (sci == other.sci && 
					localGroups == other.localGroups && 
					numbers.equals(other.numbers) &&
					digits == other.digits &&
					firstNine == other.firstNine &&
					sign == other.sign &&
					fm == other.fm &&
					b == other.b &&
					cur == other.cur &&
					v == other.v &&
					dot == other.dot);
		}
	};
	
	public OracleNumberFormat(String pattern) throws java.text.ParseException {
		format = formatsCache.get(pattern);
		if (format == null) {
			format = compilePattern(pattern);
			formatsCache.put(pattern, format);
		}
	}
	
	public String toString() {
		return (format != null) ? format.toString() : "null";
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
		OracleNumberFormat other = (OracleNumberFormat)obj;
		return (format != null) ? format.equals(other.format) : false;
	}

	private static FORMAT compilePattern(String pattern) throws java.text.ParseException {
		if (pattern == null) {
			throw new java.text.ParseException(PATTERN_EXCEPTION, 0);
		}
		if (pattern.equals("RN") || pattern.equals("rn")) {
			throw new java.text.ParseException(NOT_IMPLIMENTED_EXCEPTION, 0);
		}
		FORMAT format = new FORMAT();
		if (pattern.length() == 0) {
			return format;
		}
		pattern = pattern.toUpperCase();

		String prefix;
		String suffix;
		char c = pattern.charAt(0);
		boolean n = (c == '0' || c == '9');
		int i;
		final int flen = pattern.length();
		if (!n) {
			for (i = 1; i < flen; i++) {
				c = pattern.charAt(i);
				if (c == '0' || c == '9')
					break;
			}
			prefix = pattern.substring(0, i);
		} else {
			prefix = "";
			i = 0;
		}
		StringBuilder numbers = new StringBuilder();
		boolean stop = false;
		boolean leadZero = true;
		boolean definedGroups = false;
		for (; i < flen; i++) {
			c = pattern.charAt(i);
			stop = true;
			if (c == 'G') {
				if (definedGroups && !format.localGroups) {
					throw new java.text.ParseException(PATTERN_EXCEPTION, 0);
				}
				definedGroups = true;
				format.localGroups = true;
				stop = false;
				numbers.append('G');
			}
			else if (c == ',') {
				if (definedGroups && format.localGroups) {
					throw new java.text.ParseException(PATTERN_EXCEPTION, i);
				}
				definedGroups = true;
				format.localGroups = true;
				stop = false;
				numbers.append('G');
			} else {
				if (leadZero) {
					if (c == '0') {
						numbers.append('0');
						format.digits++;
						stop = false;
					}
					else if (c == '9') {
						stop = false;
						leadZero = false;
						format.firstNine = numbers.length();
						numbers.append('9');
						format.digits++;
					}
				} else {
					if (c == '0' || c == '9') {
						stop = false;
						numbers.append('9');
						format.digits++;
					}
				}
			}
			if (stop) {
				break;
			}
		}
		if (leadZero) {
			format.firstNine = numbers.length();
		}
		if (c == 'V') {
			for (i++; i < flen; i++,format.v++) {
				c = pattern.charAt(i);
				if (c != '0' && c != '9')
					break;
			}
		}
		else if (c == '.' || c == 'D') {
			if (definedGroups) {
				if (format.localGroups && c == '.' || !format.localGroups && c == 'D') {
					throw new java.text.ParseException(PATTERN_EXCEPTION, i);
				}
			} else {
				format.localGroups = (c == 'D');
			}
			for (i++; i < flen; i++,format.dot++) {
				c = pattern.charAt(i);
				if (c != '0' && c != '9')
					break;
			}
		}
		format.numbers = numbers.toString();
		if (i == prefix.length()) {
			throw new java.text.ParseException(PATTERN_EXCEPTION, i);
		}
		suffix = pattern.substring(i);
		
		int suf_offset = 0;
		if (suffix.startsWith("EEEE")) {
			format.sci = true;
			suf_offset = 4;
		}
		if (suffix.startsWith("S", suf_offset)) {
			format.sign = SIGN._S;
			suf_offset += 1;
		}
		else if (suffix.startsWith("MI", suf_offset)) {
			format.sign = SIGN.MI;
			suf_offset += 2;
		}
		else if (suffix.startsWith("PR", suf_offset)) {
			format.sign = SIGN.PR;
			suf_offset += 2;
		}
		
		if (suf_offset < suffix.length()) {
			throw new java.text.ParseException(PATTERN_EXCEPTION, i);
		}
		
		int pref_offset = 0;
		if (format.sign == SIGN.DEFAULT && prefix.startsWith("S")) {
			format.sign = SIGN.S_;
			pref_offset += 1;
		}
		if (prefix.startsWith("FM", pref_offset)) {
			format.fm = true;
			pref_offset += 2;
		}
		if (prefix.startsWith("B", pref_offset)) {
			format.b = true;
			pref_offset += 1;
		}
		if (format.sign == SIGN.DEFAULT && prefix.startsWith("S", pref_offset)) {
			format.sign = SIGN.S_;
			suf_offset += 1;
		}

		if (prefix.startsWith("$", pref_offset)) {
			format.cur = CURRENCY.DOLLARS;
			pref_offset += 1;
		}
		else if (prefix.startsWith("L", pref_offset)) {
			format.cur = CURRENCY.LOCAL;
			pref_offset += 1;
		}
		else if (prefix.startsWith("C", pref_offset)) {
			format.cur = CURRENCY.ISO;
			pref_offset += 1;
		}

		if (pref_offset < prefix.length()) {
			throw new java.text.ParseException(PATTERN_EXCEPTION, 0);
		}
		
		return format;
	}

    public StringBuffer format(Object number, StringBuffer str, FieldPosition fieldPosition) {
		if (number instanceof Number) {
			BigDecimal d;
			if (number instanceof BigDecimal) {
				d = (BigDecimal)number;
			}
			else if (number instanceof Double || number instanceof Float) {
				d = new BigDecimal(((Number)number).doubleValue());
			} else {
				d = new BigDecimal(((Number)number).longValue());
			}
			if (format.sci) {
				int len = d.unscaledValue().abs().toString().toCharArray().length;
				int scale = d.scale();
				format.getPrefix(str, d);
				d = new BigDecimal(d.unscaledValue(), len-1);
				d = d.setScale(format.dot+format.v, BigDecimal.ROUND_HALF_EVEN);
				char coeff[] = d.unscaledValue().abs().toString().toCharArray();
				if (coeff.length > 1+format.v) {
					str.append(coeff, 0, 1+format.v);
					if (coeff.length > 1) {
						str.append(DOT);
						str.append(coeff, 1+format.v, coeff.length-1);
					}
				} else {
					str.append(coeff);
					for (int i = 0; i < format.v+1-coeff.length; i++) {
						str.append('0');
					}
				}
				int adjusted = len-scale-1;
				str.append('E');
				str.append(adjusted >= 0 ? '+' : '-');
				String m = Integer.toString(Math.abs(adjusted));
				if (m.length() == 1) {
					str.append('0');
				}
				str.append(m);
				format.getSuffix(str, d);
			} else {
				if (format.b && d.signum() == 0) {
					if (!format.fm) {
						for (int i = 0; i < format.numbers.length()+format.v+1; i++) {
							str.append(' ');
						}
					}
				}
				else if (d.precision() - d.scale() > format.digits) {
					for (int i = 0; i < format.numbers.length()+format.v+1; i++) {
						str.append('#');
					}
				} else {
					format.getPrefix(str, d);
					int scale = d.scale();
					int digits = format.digits;
					if (format.v > 0 && scale > 0) {
						int v;
						if (format.v > scale) {
							v = 0;
							format.v -= scale;
						} else {
							v = scale-format.v;
							format.v = 0;
						}
						digits += scale;
						d = new BigDecimal(d.unscaledValue(), v);
					}
					char coeff[] = d.unscaledValue().abs().toString().toCharArray();
					int len = coeff.length;
					if (format.dot < d.scale()) {
						d = d.setScale(format.dot, BigDecimal.ROUND_HALF_EVEN);
						coeff = d.unscaledValue().abs().toString().toCharArray();
						len = coeff.length;
					}
					int j = 0;
					int prec = len-d.scale();
//					boolean canGroup = false;
					int i1 = 0;
					final int n = format.numbers.length();
					for (int i = 0; i < n; i++) {
						if (format.numbers.charAt(i) == 'G') {
							str.append(GROUP);
						}
						else if (i1 < digits-prec) {
							if (format.firstNine != 0) {
								str.append('0');
//								canGroup = true;
							}
							else if (!format.fm) {
								str.append(' ');
							}
							i1++;
						} else {
							str.append(coeff[j++]);
//							canGroup = true;
							i1++;
						}
					}
					for (; j < coeff.length; j++) {
						str.append(coeff[j]);
					}
					for (int i = 0; i < format.v; i++) {
						str.append('0');
					}
					if (format.dot > 0) {
						str.append(DOT);
					}
					for (int i = 0; i < format.dot; i++) {
						if (i < d.scale()) {
							str.append(coeff[prec+i]);
						} else {
							str.append('0');
						}
					}
					format.getSuffix(str, d);
				}
			}
		}
		return str;
	}
	
    public Object parseObject(String source, ParsePosition pos) {
		StringBuilder d = new StringBuilder(pos != null ? source.substring(pos.getIndex()) : source);
		int start = 0;				// first not white space symbol
		try {
			boolean negate = false;
			int len = d.length();		// length of parsed string
			for (; start < len; start++) {
				if (d.charAt(start) != ' ')
					break;
			}
			if (format.sign == SIGN.PR) {
				if (d.charAt(start) == '<' && d.charAt(len-1) == '>') {
					d.setCharAt(start++, ' ');
					d.setLength(--len);
					negate = true;
				}
			}
			else if (format.sign == SIGN.MI) {
				if (d.charAt(len-1) == '-') {
					d.setLength(--len);
					negate = true;
				}
			}
			else if (format.sign == SIGN._S) {
				char s = d.charAt(len-1);
				if (s == '-') {
					d.setLength(--len);
					negate = true;
				}
				else if (s == '+') {
					d.setLength(--len);
				} else {
					pos.setErrorIndex(start);
					return null;
				}
			}
			else if (format.sign == SIGN.S_) {
				char s = d.charAt(start);
				if (s == '-') {
					d.setCharAt(start++, ' ');
					negate = true;
				}
				else if (s == '+') {
					d.setCharAt(start++, ' ');
				} else {
					pos.setErrorIndex(start);
					return null;
				}
			}
			else if (format.sign == SIGN.DEFAULT) {
				char s = d.charAt(start);
				if (s == '-') {
					d.setCharAt(start++, ' ');
					negate = true;
				}
				else if (s == '+') {
					d.setCharAt(start++, ' ');
				}
			}
			for (; start < len; start++) {
				if (d.charAt(start) != ' ')
					break;
			}
			
			int e = d.indexOf("E");
			if (e == -1) {
				e = d.indexOf("e");
			}
			int dot = source.indexOf(DOT);
			int coefflen = len-start;
			if (negate) {
				coefflen++;
			}
			char coeff[] = new char[coefflen];
			int scale = 0;
			int precision = 0;
			if (negate) {
				precision++;
				coeff[0] = '-';
			}
			if (format.sci) {
				if (format.numbers.length() == 0 || format.numbers.indexOf('G') != -1) {
					pos.setErrorIndex(start);
					return null;
				}
				if (e == -1) {
					pos.setErrorIndex(start);
					return null;
				}
				coeff[precision++] = d.charAt(start);
				scale = -Integer.valueOf(d.substring(e+1));
				if (dot == -1) {
					if (start+1 != e) {
						pos.setErrorIndex(start);
						return null;
					}
				}
				else if (format.dot < e-dot-1) {
					pos.setErrorIndex(dot);
					return null;
				} else {
					if (start+1 != dot) {
						pos.setErrorIndex(start);
						return null;
					}
					scale += e-dot-1;
					for (int i = dot+1; i < e; i++) {
						coeff[precision++] = d.charAt(i);
					}
				}
			} else {
				if (e >= 0) {
					pos.setErrorIndex(e);
					return null;
				}
				if (dot != -1) {
					coefflen--;
					scale = len-dot-1;
					if (format.dot < scale) {
						pos.setErrorIndex(dot);
						return null;
					}
				}
				try {
					int end = (dot < 0 ? len : dot);
					int j = format.numbers.length()-1;
					for (int i = start; i < end; i++, j--) {
						char c = d.charAt(i);
						if (format.numbers.charAt(j) == 'G') {
							if (c != GROUP) {
								pos.setErrorIndex(i);
								return null;
							}
						} else {
							if (Character.isDigit(c)) {
								coeff[precision++] = c;
							} else {
								pos.setErrorIndex(i);
								return null;
							}
						}
					}
				} catch (Exception ex) {
					pos.setErrorIndex(start);
					ex.printStackTrace();
					return null;
				}
				if (dot != -1) {
					for (int i = dot+1; i < len; i++) {
						char c = d.charAt(i);
						if (Character.isDigit(c)) {
							coeff[precision++] = c;
						} else {
							pos.setErrorIndex(i);
							return null;
						}
					}
				}
			}
			String str = new String(coeff, 0, precision);
			Number ret;
			if (scale == 0 && precision < 10) {
				ret = new Integer(Integer.valueOf(str));
			} else {
				ret = new BigDecimal(new BigInteger(str), scale);
			}
			pos.setIndex(len);
			return ret;
		} catch (Exception e) {
			pos.setErrorIndex(start);
			e.printStackTrace();
			return null;
		}
	}

}
