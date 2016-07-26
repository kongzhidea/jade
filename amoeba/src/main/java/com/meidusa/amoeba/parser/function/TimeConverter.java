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
package com.meidusa.amoeba.parser.function;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import com.meidusa.amoeba.parser.expression.Expression;
import com.meidusa.amoeba.sqljep.ParseException;

public class TimeConverter extends AbstractFunction {

	private int defaultField = -1 ;
	
	public int getDefaultField() {
		return defaultField;
	}

	public void setDefaultField(int defaultfield) {
		this.defaultField = defaultfield;
	}

	@SuppressWarnings("unchecked")
	public Comparable evaluate(List<Expression> list, Object[] parameters)
			throws ParseException {
		Comparable param2 = null;
		Comparable param1 = null;
		if(list.size() == 0){
			return 0;
		}else if(list.size() == 1){
			param1 = list.get(0).evaluate(parameters);
			param2 = defaultField;
		}else if(list.size() == 2){
			param1 = list.get(0).evaluate(parameters);
			param2 = list.get(1).evaluate(parameters);;
		}
		
		if(param1 == null || param2 == null){
			return null;
		}
		
		Calendar cal = Calendar.getInstance();
		
		
		if(defaultField == -1){
			return converter((String)param1);
		}else{
			if(param1 instanceof String){
				param1 = Integer.valueOf((String)param1);
			}
			
			if(param2 instanceof String){
				param2 = Integer.valueOf((String)param1);
			}
			cal.set(0, 0, 0, 0, 0, 0);
			cal.set(((Integer)param2).intValue(), ((Integer)param1).intValue());
			return cal.getTime();
		}
	}
	
	public static Date converter(String param1){
		param1 = param1.trim();
		StringTokenizer tokenizer = new StringTokenizer(param1," ");
		String token1 = tokenizer.nextToken();
		String token2 = null;
		if(tokenizer.hasMoreTokens()){
			token2 = tokenizer.nextToken();
		}
		
		if(token1.indexOf(":")>0){
			
			return parser(token1);
		}else{
			if(token2 == null){
				return java.sql.Date.valueOf(token1);
			}else{
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(parser(token2));
				Calendar cal = Calendar.getInstance();
				cal.setTime(java.sql.Date.valueOf(token1));
				
				cal.add(Calendar.HOUR, cal2.get(Calendar.HOUR));
				cal.add(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
				cal.add(Calendar.SECOND, cal2.get(Calendar.SECOND));
				cal.add(Calendar.MILLISECOND, cal2.get(Calendar.MILLISECOND));
				return cal.getTime();
			}
		}
	}
	
	private static Date parser(String time){
		Calendar cal = Calendar.getInstance();
		int index = 0;
		if((index = time.indexOf("."))>0){
			String micro = time.substring(index+1);
			time = time.substring(0,index-1);
			Time thisTime = Time.valueOf(time);
			cal.setTime(thisTime);
			cal.set(Calendar.MILLISECOND, Integer.parseInt(micro) / 1000);
			return cal.getTime();
		}else{
			return Time.valueOf(time);
		}
		
	}

}
