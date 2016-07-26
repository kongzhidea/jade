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

import java.util.ArrayList;



/**
 * Finds all columns in parser tree and stores them in the array
 */
public class ParserDumpColumns implements ParserVisitor {
	
	ArrayList<Integer> columns;
	
	public ParserDumpColumns(ArrayList<Integer> columns) {
		this.columns = columns;
	}
	
	public Object visit(SimpleNode node, Object data) throws ParseException {
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTStart node, Object data) throws ParseException {
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTFunNode node, Object data) throws ParseException {
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTVarNode node, Object data) throws ParseException {
		if (node.index >= 0) {
			columns.add(node.index);
		}
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTConstant node, Object data) throws ParseException {
		data = node.childrenAccept(this, data);
		return data;
	}
	public Object visit(ASTArray node, Object data) throws ParseException {
		data = node.childrenAccept(this, data);
		return data;
	}
}
