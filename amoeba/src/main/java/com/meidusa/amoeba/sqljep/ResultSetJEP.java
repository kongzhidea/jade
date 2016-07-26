/*****************************************************************************
      SQLJEP - Java SQL Expression Parser 0.2
      November 1 2006
         (c) Copyright 2006, Alexey Gaidukov
      SQLJEP Author: Alexey Gaidukov

      SQLJEP is based on JEP 2.24 (http://www.singularsys.com/jep/)
           (c) Copyright 2002, Nathan Funk
 
      See LICENSE.txt for license information.
*****************************************************************************/

package com.meidusa.amoeba.sqljep;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


/**
 * Example of usage
 * <blockquote><pre>
 * 	ResultSet rs = statement.excute("SELECT ID,SUM,SALE_DATE from test");
 * 	ResultSetJEP sqljep = new ResultSetJEP("ID in (1,2,3) and SUM>100 and SALE_DATE>trunc(sysdate)-7");
 * 	try {
 * 		sqljep.addConstant("sysdate", new java.util.Date());
 * 		sqljep.parseExpression(rs);
 * 		whille (rs.next()) {
 * 			System.out.println(sqljep.getValue());
 * 		}
 * 	}
 * 	catch (ParseException e) {
 * 		e.printStackTrace();
 *	}
 * </pre></blockquote>
 * @author Alexey Gaidukov
 * @see com.meidusa.amoeba.sqljep.BaseJEP
 */
public class ResultSetJEP extends BaseJEP {
	protected ResultSet rs = null;
	
	/** Symbol Table */
	final protected HashMap<String, Comparable> constTab = new HashMap<String,Comparable>();

	/**
	 * Creates a new ResultSetJEP instance.
	 */
	public ResultSetJEP(String exp) {
		super(exp);
	}

	public void clear() {
		super.clear();
		this.rs = null;
	}
	
	public int findColumn(String name){
		if (rs != null) {
			try {
				return rs.findColumn(name)-1;
			} catch (SQLException e) {
				return -1;
			}
		}
		return -1;
	}
	
	public Comparable getColumnObject(int column) throws ParseException {
		try {
			return (Comparable)rs.getObject(column+1);
		} catch (SQLException e) {
			throw new ParseException(e.getMessage());
		}
	}

    /**
	 * Add the constant into constants table.
     * If the table previously contained a constant for this name, the old
     * value is replaced.
	 *
     * @return previous value of constant with specified name, or <tt>null</tt>
     *	       if there was no constant.  A <tt>null</tt> return can
     *	       also indicate that the previously value of the constant with the specified name
     *	       was <tt>null</tt>.
     */
	public Comparable addConstant(String name, Comparable value) {
		return constTab.put(name, value);
	}
	
	/**
	 * TODO: Use constTab hash more effectively. 
	 * If it was possible to use HasMap.getEntry it will be oprimal 
	 * but HasMap.getEntry is not public
	 */
	public Map.Entry getVariable(String name) throws ParseException {
		for (Map.Entry e : constTab.entrySet()) {
			if (e.getKey().equals(name));
				return e;
		}
		return null;
	}

	/*public void parseExpression(ResultSet rs) throws ParseException {
		this.rs = rs;
		super.parseExpression();
	}*/
}
