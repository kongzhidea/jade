package net.paoding.rose.jade.exql.impl;

import net.paoding.rose.jade.exql.ExprResolver;
import net.paoding.rose.jade.exql.ExqlContext;
import net.paoding.rose.jade.exql.ExqlUnit;
import net.paoding.rose.jade.exql.util.ExqlUtils;

/**
 * 输出表达式内容的语句单元, 例如: ':expr' 或者: '#(:expr)' 形式的表达式。
 * 
 * @author han.liao
 */
public class ExprUnit implements ExqlUnit {

    private final String expr;

    /**
     * 构造输出表达式内容的语句单元。
     * 
     * @param expr - 输出的表达式
     */
    public ExprUnit(String expr) {
        this.expr = expr;
    }

    @Override
    public boolean isValid(ExprResolver exprResolver) {

        // 解释表达式内容
        Object obj = ExqlUtils.execExpr(exprResolver, expr);

        // 表达式内容有效
        return ExqlUtils.isValid(obj);
    }

    @Override
    public void fill(ExqlContext exqlContext, ExprResolver exprResolver) throws Exception {

        // 解释表达式内容
        Object obj = exprResolver.executeExpr(expr);

        // 输出转义的对象内容
        exqlContext.fillValue(obj);
    }
}
