package com.meidusa.amoeba.sqljep.function;

import java.math.BigDecimal;
import java.util.Comparator;

import com.meidusa.amoeba.sqljep.ParseException;

@SuppressWarnings("unchecked")
class ComparativeComparator implements Comparator{
	protected static ComparativeComparator comparator = new ComparativeComparator();
	public int compare(Object o1, Object o2) {
		try {
			return compareTo((Comparable)o1,(Comparable)o2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public static int compareTo(Comparable s1, Comparable s2) throws ParseException {
		if(s1 == null || s2 == null) return -1;
		if (s1.getClass() == s2.getClass() 
				|| s1.getClass().isAssignableFrom(s2.getClass()) 
				|| s2.getClass().isAssignableFrom(s1.getClass())) {
			return s1.compareTo(s2);
		} else {
			if (s2 instanceof Number && s1 instanceof String) {
				s1 = PostfixCommand.parse((String)s1);
			} 
			else if (s1 instanceof Number && s2 instanceof String) {
				s2 = PostfixCommand.parse((String)s2);
			}
			
			if (s1 instanceof Number && s2 instanceof Number) {
				Number n1 = (Number)s1;
				Number n2 = (Number)s2;
				if (n1 instanceof BigDecimal || n2 instanceof BigDecimal) {		// BigInteger is not supported
					BigDecimal d1 = PostfixCommand.getBigDecimal(n1);
					BigDecimal d2 = PostfixCommand.getBigDecimal(n2);
					return d1.compareTo(d2);
				}
				else if (n1 instanceof Double || n2 instanceof Double || n1 instanceof Float || n2 instanceof Float) {
					return Double.compare(n1.doubleValue(), n2.doubleValue());
				} else {		// Long, Integer, Short, Byte
					long thisVal = n1.longValue();
					long anotherVal = n2.longValue();
					return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
				}
			}
			throw new ParseException("Not comparable");
		}
	}

}