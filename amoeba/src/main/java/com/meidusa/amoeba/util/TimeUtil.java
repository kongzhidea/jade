/*
 * 	This program is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, 
 * or (at your option) any later version. 
 * 
 * 	This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details. 
 * 	You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.meidusa.amoeba.util;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TimeUtil {
	public static final int TIMESTAMP = 1;
	public static final int DATE = 2;
	public static final int TIME = 3;
	
	public static java.util.Date parseDateTime(String original, int type) throws SQLException {
	        String s = original;
	        if (s == null) {
	            return null;
	        }
	        try {
	            int timeStart = 0;
	            TimeZone tz = null;
	            if (type == TIME) {
	                timeStart = 0;
	            } else {
	                timeStart = s.indexOf(' ') + 1;
	                if (timeStart <= 0) {
	                    // ISO 8601 compatibility
	                    timeStart = s.indexOf('T') + 1;
	                }
	            }

	            int year = 1970, month = 1, day = 1;
	            if (type != TIME) {
	                if (s.startsWith("+")) {
	                    // +year
	                    s = s.substring(1);
	                }
	                // start at position 1 to support -year
	                int s1 = s.indexOf('-', 1);
	                int s2 = s.indexOf('-', s1 + 1);
	                if (s1 <= 0 || s2 <= s1) {
	                    throw new SQLException("error format:"+s+" must format yyyy-mm-dd");
	                }
	                year = Integer.parseInt(s.substring(0, s1));
	                month = Integer.parseInt(s.substring(s1 + 1, s2));
	                int end = timeStart == 0 ? s.length() : timeStart - 1;
	                day = Integer.parseInt(s.substring(s2 + 1, end));
	            }
	            int hour = 0, minute = 0, second = 0, nano = 0;
	            if (type != DATE) {
	                int s1 = s.indexOf(':', timeStart);
	                int s2 = s.indexOf(':', s1 + 1);
	                int s3 = s.indexOf('.', s2 + 1);
	                if (s1 <= 0 || s2 <= s1) {
	                    throw new SQLException("error format:"+s+" must format hh:mm:ss");
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
	                            throw new SQLException("time zone name error :"+tz.getID() + " <>" + tzName);
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
	                time = getTime(false, tz, year, month, day, hour, minute, second, type != TIMESTAMP, nano);
	            } catch (IllegalArgumentException e) {
	                // special case: if the time simply doesn't exist, use the lenient version
	                if (e.toString().indexOf("HOUR_OF_DAY") > 0) {
	                    time = getTime(true, tz, year, month, day, hour, minute, second, type != TIMESTAMP, nano);
	                } else {
	                    throw e;
	                }
	            }
	            switch (type) {
	            case DATE:
	                return new java.sql.Date(time);
	            case TIME:
	                return new java.sql.Time(time);
	            case TIMESTAMP: {
	                Timestamp ts = new Timestamp(time);
	                ts.setNanos(nano);
	                return ts;
	            }
	            default:
	            	throw new SQLException("error type:"+type);
	            }
	        } catch (IllegalArgumentException e) {
	            throw new SQLException("IllegalArgumentException");
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
    
    public static void main(String[] args){
    	try {
			System.out.println(parseDateTime("1998-02-03 22:23:00",TIME));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
    }

}
