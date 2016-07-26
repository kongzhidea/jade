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
package com.meidusa.amoeba.parser.expression;

/**
 * And Expression
 * @author <a href=mailto:piratebase@sina.com>Struct chen</a>
 *
 */
public class AndExpression extends BaseExpressionList {

    public AndExpression(){
    }

    public AndExpression(Expression node){
        super(node);
    }

    @SuppressWarnings("unchecked")
    public Comparable evaluate(Object[] parameters) {
        for (Expression e : eList) {
            if (!((Boolean) e.evaluate(parameters))) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    protected BaseExpressionList getReverseObject() {
        return new OrExpression(null);
    }

    protected void toString(StringBuilder builder) {
        this.toString(builder, " AND ");
    }

}
