package net.paoding.rose.jade.exql.impl;

import net.paoding.rose.jade.exql.ExprResolver;
import net.paoding.rose.jade.exql.ExqlContext;
import net.paoding.rose.jade.exql.ExqlUnit;
import net.paoding.rose.jade.exql.util.ExqlUtils;

/**
 * 直接输出表达式内容的语句单元, 例如: '##(:expr)' 形式的表达式，内容不经转义输出。
 * 
 * @author han.liao
 */
public class JoinExprUnit implements ExqlUnit {

    private final String expr;

    /**
     * 构造输出表达式内容的语句单元。
     * 
     * @param expr - 输出的表达式
     */
    public JoinExprUnit(String expr) {
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

        // 直接输出未经转义的对象内容
        exqlContext.fillText(String.valueOf(obj));
    }
}
