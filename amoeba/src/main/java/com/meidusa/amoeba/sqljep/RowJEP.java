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



/**
 * Example of usage
 * <blockquote><pre>
 * 	Comparable[] row = {1,100.00,new java.util.Date()};
 * 	HashMap<String,Integer> columnMapping = new HashMap<String,Integer>();
 * 	columnMapping.put("ID",0);
 * 	columnMapping.put("SUM",1);
 * 	columnMapping.put("SALE_DATE",2);
 * 	RowJEP sqljep = new RowJEP("ID in (1,2,3) and SUM>100 and SALE_DATE>to_date('2006-01-01','yyyy-mm-dd')");
 * 	try {
 * 		sqljep.parseExpression(columnMapping);
 * 		System.out.println(sqljep.getValue(row));
 * 	}
 * 	catch (ParseException e) {
 * 		e.printStackTrace();
 * 	}
 * </blockquote></pre>
 * @author Alexey Gaidukov
 * @see com.meidusa.amoeba.sqljep.BaseJEP
 */
public class RowJEP extends BaseJEP {

	public RowJEP(String exp) {
		super(exp);
	}

	public void clear() {
		super.clear();
	}
	
	public Comparable getColumnObject(int column) throws ParseException {
		JepRuntime runtime = getThreadJepRuntime(this);
		if(runtime.row != null){
			try {
				return runtime.row[column];
			} catch (Exception e) {
				throw new ParseException("Column index:"+column, e);
			}
		}else{
			throw new ParseException("Column index:"+column);
		}
	}
}
