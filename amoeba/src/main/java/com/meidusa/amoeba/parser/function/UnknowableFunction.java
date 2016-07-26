package com.meidusa.amoeba.parser.function;

import java.util.List;

import com.meidusa.amoeba.parser.expression.Expression;
import com.meidusa.amoeba.sqljep.ParseException;

/**
 * 未知的函数，如果想要让sqlparser正常工作，但是又不希望实现，则可以采用该函数
 * @author struct
 *
 */
public class UnknowableFunction extends AbstractFunction {

	@SuppressWarnings("unchecked")
	public Comparable evaluate(List<Expression> list, Object[] parameters)
			throws ParseException {
		return null;
	}

}
