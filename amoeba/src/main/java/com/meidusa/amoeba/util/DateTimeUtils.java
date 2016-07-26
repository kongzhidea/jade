package com.meidusa.amoeba.util;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateTimeUtils {
	
	public abstract class Value {

	    /**
	     * The data type is unknown at this time.
	     */
	    public static final int UNKNOWN = -1;
	    public static final int NULL = 0, BOOLEAN = 1, BYTE = 2, SHORT = 3, INT = 4, LONG = 5, DECIMAL = 6;
	    public static final int DOUBLE = 7, FLOAT = 8, TIME = 9, DATE = 10, TIMESTAMP = 11, BYTES = 12;
	    public static final int STRING = 13, STRING_IGNORECASE = 14, BLOB = 15, CLOB = 16;
	    public static final int ARRAY = 17, RESULT_SET = 18, JAVA_OBJECT = 19, UUID = 20, STRING_FIXED = 21;

	    public static final int TYPE_COUNT = STRING_FIXED + 1;
	}
	
    public static java.util.Date parseDateTime(String original, int type, int errorCode) throws SQLException {
        String s = original;
        if (s == null) {
            return null;
        }
        try {
            int timeStart = 0;
            TimeZone tz = null;
            if (type == Value.TIME) {
                timeStart = 0;
            } else {
                timeStart = s.indexOf(' ') + 1;
                if (timeStart <= 0) {
                    // ISO 8601 compatibility
                    timeStart = s.indexOf('T') + 1;
                }
            }

            int year = 1970, month = 1, day = 1;
            if (type != Value.TIME) {
                if (s.startsWith("+")) {
                    // +year
                    s = s.substring(1);
                }
                // start at position 1 to support -year
                int s1 = s.indexOf('-', 1);
                int s2 = s.indexOf('-', s1 + 1);
                if (s1 <= 0 || s2 <= s1) {
                    throw new SQLException(s, "format yyyy-mm-dd" ,errorCode);
                }
                year = Integer.parseInt(s.substring(0, s1));
                month = Integer.parseInt(s.substring(s1 + 1, s2));
                int end = timeStart == 0 ? s.length() : timeStart - 1;
                day = Integer.parseInt(s.substring(s2 + 1, end));
            }
            int hour = 0, minute = 0, second = 0, nano = 0;
            if (type != Value.DATE) {
                int s1 = s.indexOf(':', timeStart);
                int s2 = s.indexOf(':', s1 + 1);
                int s3 = s.indexOf('.', s2 + 1);
                if (s1 <= 0 || s2 <= s1) {
                    throw new SQLException(s, "format hh:mm:ss" ,errorCode);
                }

                if (s.endsWith("Z")) {
                    s = s.substring(0, s.length() - 1);
                    tz = TimeZone.getTimeZone("UTC");
                } else {
                    int timeZoneStart = s.indexOf('+', s2 + 1);
                    if (timeZoneStart < 0) {
                        timeZoneStart = s.indexOf('-', s2 + 1);
                    }
                    if (timeZoneStart >= 0) {
                        String tzName = "GMT" + s.substring(timeZoneStart);
                        tz = TimeZone.getTimeZone(tzName);
                        if (!tz.getID().equals(tzName)) {
                        	throw new SQLException(s, tz.getID() + " <>" + tzName ,errorCode);
                        }
                        s = s.substring(0, timeZoneStart).trim();
                    }
                }

                hour = Integer.parseInt(s.substring(timeStart, s1));
                minute = Integer.parseInt(s.substring(s1 + 1, s2));
                if (s3 < 0) {
                    second = Integer.parseInt(s.substring(s2 + 1));
                } else {
                    second = Integer.parseInt(s.substring(s2 + 1, s3));
                    String n = (s + "000000000").substring(s3 + 1, s3 + 10);
                    nano = Integer.parseInt(n);
                }
            }
            if (hour < 0 || hour > 23) {
                throw new IllegalArgumentException("hour: " + hour);
            }
            long time;
            try {
                time = getTime(false, tz, year, month, day, hour, minute, second, type != Value.TIMESTAMP, nano);
            } catch (IllegalArgumentException e) {
                // special case: if the time simply doesn't exist, use the lenient version
                if (e.toString().indexOf("HOUR_OF_DAY") > 0) {
                    time = getTime(true, tz, year, month, day, hour, minute, second, type != Value.TIMESTAMP, nano);
                } else {
                    throw e;
                }
            }
            switch (type) {
            case Value.DATE:
                return new java.sql.Date(time);
            case Value.TIME:
                return new java.sql.Time(time);
            case Value.TIMESTAMP: {
                Timestamp ts = new Timestamp(time);
                ts.setNanos(nano);
                return ts;
            }
            default:
            	throw new SQLException("type:" + type);
            }
        } catch (IllegalArgumentException e) {
        	throw new SQLException(original, e.toString());
        }
    }
    
    private static long getTime(boolean lenient, TimeZone tz, int year, int month, int day, int hour, int minute, int second, boolean setMillis, int nano) {
        Calendar c;
        if (tz == null) {
            c = Calendar.getInstance();
        } else {
            c = Calendar.getInstance(tz);
        }
        c.setLenient(lenient);
        if (year <= 0) {
            c.set(Calendar.ERA, GregorianCalendar.BC);
            c.set(Calendar.YEAR, 1 - year);
        } else {
            c.set(Calendar.YEAR, year);
        }
        c.set(Calendar.MONTH, month - 1); // january is 0
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        if (setMillis) {
            c.set(Calendar.MILLISECOND, nano / 1000000);
        }
        return c.getTime().getTime();
    }
}
