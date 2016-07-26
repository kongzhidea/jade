/*
 *  This program is free software; you can redistribute it and/or modify it under the terms of 
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
 * 表达式基类
 * 
 * @author <a href=mailto:piratebase@sina.com>Struct chen</a>
 *
 */
public abstract class Expression {

    public Expression(){
    }

    public boolean canEvaluate() {
        return true;
    }

    /**
     * 表达式取反
     */
    public abstract Expression reverse();

    protected abstract void toString(StringBuilder builder);

    /**
     * 表示该表达式是否需要实时计算
     */
    public abstract boolean isRealtime();

    /**
     * 表达式计算
     */
    @SuppressWarnings("unchecked")
    public abstract Comparable evaluate(Object[] parameters);

    public String toString() {
        StringBuilder builder = new StringBuilder();
        this.toString(builder);
        return builder.toString();
    }
}
