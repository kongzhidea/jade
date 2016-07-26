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
import com.meidusa.amoeba.sqljep.ASTFunNode;
import com.meidusa.amoeba.sqljep.JepRuntime;
import com.meidusa.amoeba.sqljep.ParseException;

/**
 * All function classes must implement this interface to ensure that the run()
 * method is implemented.
 */
public interface PostfixCommandI {
	/**
	 * Run the function on the stack. Pops the arguments from the stack, and
	 * pushes the result on the top of the stack.
	 */
	public Comparable<?>[] evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException;
	
	public boolean isAutoBox();
	
	public Comparable<?> getResult(Comparable<?> ...comparables) throws ParseException;
	
	/**
	 * Returns the number of required parameters, or -1 if any number of
	 * parameters is allowed.
	 */
	public int getNumberOfParameters();
}

