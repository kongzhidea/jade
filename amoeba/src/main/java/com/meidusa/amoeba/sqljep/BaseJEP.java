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

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.meidusa.amoeba.sqljep.function.Comparative;
import com.meidusa.amoeba.sqljep.function.ComparativeBaseList;
import com.meidusa.amoeba.sqljep.function.PostfixCommand;
import com.meidusa.amoeba.sqljep.function.PostfixCommandI;
import com.meidusa.amoeba.sqljep.variable.Variable;
import com.meidusa.amoeba.util.StaticString;
import com.meidusa.amoeba.util.ThreadLocalMap;

/**
 * Base class for different SQLJEP classes. This class doesn't  know how to get source data.
 * <p/>
 * There are two types of variables in an expression. Columns of a abstarct table data and
 * external variables. Example of external variables are <CODE>TIMESTAMP,DATE,TIME,SYSDATE</CODE> variables.
 * <p/>
 * User should override methods {@link #findColumn(String name)} and {@link #getColumnObject(int column)} for defining
 * access to abstarct table data and the method {@link #getVariable(String name)} to access to external variables.
 * <p/>
 * To get a variable value at first {@link #getVariable(String name)} and after that {@link #findColumn(String name)} method is used.
 * <p/>
 * In expressions 'part_1.part2' names of variables are supported.
 * Parser is case sensitive.
 * <p/>
 * Visit <a href="http://sourceforge.net/projects/sqljep">http://sourceforge.net/projects/sqljep</a>
 * for the newest version of SQLJEP, and complete documentation.
 * @author Alexey Gaidukov
 */
public abstract class BaseJEP implements ParserVisitor {
	/** Debug flag for extra command line output */
	public static final boolean debug = false;
	
	/** Parse time error List */
	final protected List<String> errorList = new ArrayList<String>();

	/** Node at the top of the parse tree */
	protected Node topNode;

	protected String expression;
	
	/**
	 * Creates a new SQLJEP instance.
	 * Store String representation of the expression. <br/>
	 * To compile it use {@link #parseExpression} method.<br/>
	 * To evaluate use {@link #getValue} method.
	 */
	public BaseJEP(String exp) {
		if (exp == null) {
			throw new IllegalArgumentException("expression can be null");
		}
		expression = exp;
		topNode = null;
	}

	/**
	* Change SQLJEP's state to initial state.
	* 
	*/
	public void clear() {
		topNode = null;
		errorList.clear();
	}
	
	/**
	* @return true if expresson is in compiled state otherwise false
	*/
	public boolean isValid() {
		return (topNode != null);
	}

	/**
	 * Returns the top node of the expression tree. Because all nodes are
	 * pointed to either directly or indirectly, the entire expression tree
	 * can be accessed through this node. It may be used to manipulate the
	 * expression, and subsequently evaluate it manually.
	 * <p/>
	 * 	Sometimes it is necessary to change value of a SQLJEP expression
	 * 	by some external reasons. Then following code can be applied
	 * <blockquote><pre>
	 * 		Node topNode = sqljep.getTopNode();
	 * 		if (topNode instanceof ASTVarNode) {
	 * 			((ASTVarNode)topNode).variable.setValue(value);
	 * 		}
	 * 		else {
	 * 			throw new org.medfoster.sqljep.ParseException("Expressions are not permitted");
	 * 		}
	 * </pre></blockquote>
	 * @return The top node of the expression tree
	 */
	public Node getTopNode() {
		return topNode;
	}
	
	/**
	 * method is used to get the value of the column in the current row of
	 * abstract table data.
	 * @return the column value
	 * @param column Number in the abstract table data
	 * @throws com.meidusa.amoeba.sqljep.ParseException 
	 */
	@SuppressWarnings("unchecked")
	public abstract Comparable getColumnObject(int column) throws ParseException;

	/**
	
	/**
	 * Returns true if an error occured during the most recent
	 * action (parsing or evaluation).
	 * @return Returns <code>true</code> if an error occured during the most
	 * recent action (parsing or evaluation).
	 */
	boolean hasError() {
		return !errorList.isEmpty();
	}

	/**
	 * Reports information on the errors that occured during the most recent
	 * action.
	 * @return A string containing information on the errors, each separated
	 * by a newline character; null if no error has occured
	 */
	String getErrorInfo() {
		if (hasError()) {
			String str = "";
			for (String err : errorList) {
				str += err + "\n";
			}
			return str;
		} else {
			return null;
		}
	}

	/**
	 * Parses the expression. 
	 * The root of the expression tree is returned by {@link #getTopNode()} method
	 * @throws com.meidusa.amoeba.sqljep.ParseException If there are errors in the expression then it fires the exception
	 */
	final public void parseExpression(Map<String,Integer> columnMapping,Map<String,Variable> variableMapping,final Map<String,PostfixCommand> funMap) throws ParseException {
		Reader reader = new StringReader(expression);
		Parser parser = new Parser(reader){

			@Override
			public boolean containsKey(String key) {
				return funMap.containsKey(key);
			}

			@Override
			public PostfixCommandI getFunction(String name) {
				return funMap.get(name);
			}
			
		};
		parser.setColumnMapping(columnMapping);
		parser.setVariableMapping(variableMapping);
		try {
			// try parsing
			topNode = parser.parseStream(reader);
			errorList.clear();
			errorList.addAll(parser.getErrorList());
		} catch (ParseException e) {
			// an exception was thrown, so there is no parse tree
			topNode = null;
			errorList.add(e.getMessage());
		} catch (Throwable e) {
			throw new com.meidusa.amoeba.sqljep.ParseException(toString(), e);
		}
		
		if (hasError()) {
			throw new com.meidusa.amoeba.sqljep.ParseException(getErrorInfo());
		}
		
		// If debug is enabled, print a dump of the tree to
		// standard output
		if (debug) {
			ParserVisitor v = new ParserDumpVisitor();
			try {
				topNode.jjtAccept(v, null);
			} catch (ParseException e) {
				errorList.add(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns the value of the expression as an object. The expression
	 * tree is specified with its top node. The algorithm uses a stack 
	 * {@link com.meidusa.amoeba.sqljep.JepRuntime#stack} for evaluation.
	 * <p>
	 * An exception is thrown, if an error occurs during evaluation.
	 * @return The value of the expression as an object.
	 */
	public Comparable<?> getValue(Comparable<?>[] row) throws ParseException {
		JepRuntime runtime = getThreadJepRuntime(this);
		runtime.stack.setSize(0);
		runtime.row = row;
		
		if (!isValid()) {
			throw new ParseException("Parser is not prepared");
		}
		if (!hasError()) {
			runtime.stack.setSize(0);
			// evaluate by letting the top node accept the visitor
			topNode.jjtAccept(this, null);
			
			// something is wrong if not exactly one item remains on the stack
			// or if the error flag has been set
			if (runtime.stack.size() != 1) {
				throw new ParseException("Wrong stack state. Stack size: "+runtime.stack.size());
			}
	
			// return the value of the expression
			return runtime.stack.pop();
		} else {
			throw new ParseException(getErrorInfo());
		}
	}

	/**
	 * Compares two objects for equality.
	 * Two SQLJEPs are equals only when their String representations are equals.
	 */
	public boolean equals(Object obj) {
		if (obj instanceof BaseJEP) {
			BaseJEP f = (BaseJEP)obj;
			return expression.equals(f.expression);
		}
		return false;
	}

    /**
	 * Returns the expression string representation {@link #BaseJEP(String exp)}
	 */
	public String toString() {
		return expression;
	}

	/* ParserVisitor interface */
	
	/**
	 * This method should never be called when evaluation a normal
	 * expression.
	 */
	final public Object visit(SimpleNode node, Object data) throws ParseException {
		return null;
	}
	
	/**
	 * This method should never be called when evaluating a normal
	 * expression.
	 */
	final public Object visit(ASTStart node, Object data) throws ParseException {
		return null;
	}
	
	/**
	 * Visit a function node. The values of the child nodes
	 * are first pushed onto the stack. Then the function class associated
	 * with the node is used to evaluate the function.
	 */
	final public Object visit(ASTFunNode node, Object data) throws ParseException {
		JepRuntime runtime = getThreadJepRuntime(this);
		PostfixCommandI pfmc = node.getPFMC();

		// check if the function class is set
		if (pfmc == null) {
			throw new ParseException("No function class associated with " + node.getName());
		}

		// evaluate all children (each leaves their result on the stack)

		if (debug) {
			System.out.println("Stack size after childrenAccept: " + runtime.stack.size());
		}
		
		Comparable<?>[] parameters = pfmc.evaluate(node, runtime);
		
		if(pfmc.isAutoBox()){
			ComparativeBaseList list = null;
			int index = -1;
			for(int i=0;i<parameters.length;i++){
				if(parameters[i] instanceof ComparativeBaseList){
					index = i;
					list = (ComparativeBaseList)parameters[i];
					break;
				}else{
					
				}
			}
			
			if(index >=0){
				for(int i=0;i<parameters.length;i++){
					if(i != index){
						if(parameters[i] instanceof Comparative){
							parameters[i] =((Comparative) parameters[i]).getValue(); 
						}
					}
				}
				
				for(Comparative comp:list.getList()){
					parameters[index] = comp.getValue();
					Comparable<?> value = pfmc.getResult(parameters);
					if(value instanceof Comparative){
						comp.setComparison(((Comparative) value).getComparison());
						comp.setValue(((Comparative) value).getValue());
					}else{
						comp.setValue(value);
					}
				}
				runtime.stack.push(list);
			}else{
				//分析每个参数是否是 Comparative 类型
				Comparative lastComparative = null;
				for(int i=0;i<parameters.length;i++){
					if(parameters[i] instanceof Comparative){
						lastComparative = ((Comparative) parameters[i]);
						parameters[i] =((Comparative) parameters[i]).getValue(); 
					}
				}
				
				Comparable<?> result = pfmc.getResult(parameters);
				if(lastComparative != null){
					lastComparative.setValue(result);
					result = lastComparative;
				}
				runtime.stack.push(result);
			}
		}else{
			runtime.stack.push(pfmc.getResult(parameters));
		}
		
		
		if (debug) {
			System.out.println("Stack size after run: " + runtime.stack.size());
		}
		return null;
	}

	/**
	 * Visit a variable node. The value of the variable is obtained from the
	 * model and pushed onto the stack.
	 */
	@SuppressWarnings("unchecked")
	final public Object visit(ASTVarNode node, Object data) throws ParseException {
		JepRuntime runtime = getThreadJepRuntime(this);
		if (node.index >= 0) {
			Comparable value = getColumnObject(node.index);
			if(value instanceof Comparative){
				value = (Comparable)((Comparative)value).clone();
			}
			runtime.stack.push(value);
		} else {
			runtime.stack.push((Comparable)node.variable.getValue());
		}
		return null;
	}

	/**
	 * Visit a constant node. The value of the constant is pushed onto the
	 * stack.
	 */
	final public Object visit(ASTConstant node, Object data) throws ParseException {
		JepRuntime runtime = getThreadJepRuntime(this);
		runtime.stack.push((Comparable<?>)node.value);
		return null;
	}

	final public Object visit(ASTArray node, Object data) throws ParseException {
		node.childrenAccept(this, null);
		return null;
	}
	
	public static JepRuntime getThreadJepRuntime(BaseJEP baseJep){
		JepRuntime runtime = (JepRuntime)ThreadLocalMap.get(StaticString.JEP_RUNTIME);
		if(runtime == null){
			runtime = new JepRuntime(baseJep);
			ThreadLocalMap.put(StaticString.JEP_RUNTIME, runtime);
		}else{
			runtime.ev = baseJep;
		}
		return runtime;
	}
}
