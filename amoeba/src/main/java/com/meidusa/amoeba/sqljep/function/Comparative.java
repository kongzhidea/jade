package com.meidusa.amoeba.sqljep.function;

import java.util.Comparator;

@SuppressWarnings("unchecked")
public class Comparative implements Comparable ,Cloneable{
    
    public static final int GreaterThan = 1;
    public static final int GreaterThanOrEqual = 2;
    public static final int Equivalent = 3;
    public static final int Like = 4;
    public static final int NotLike = 5;
    public static final int NotEquivalent = 6;
    public static final int LessThan = 7;
    public static final int LessThanOrEqual = 8;
    
    
    /**
     * 表达式取反
     * @param function
     * @return
     */
    public static int reverseComparison(int function){
        return 9-function;
    }
    
    /**
     * 表达式前后位置调换的时候
     * @param function
     * @return
     */
    public static int exchangeComparison(int function){
        if(function == GreaterThan){
            return LessThan;
        }else if(function == GreaterThanOrEqual){
            return LessThanOrEqual;
        }else if(function == LessThan){
            return GreaterThan;
        } if(function == LessThanOrEqual){
            return GreaterThanOrEqual;
        }else{
            return function;
        }
    }
    
    private Comparable value;
    private int comparison;
    protected Comparative(){
    }

    public Comparative(int function,Comparable value){
        this.comparison = function;
        this.value = value;
    }
    
    public Comparable getValue(){
        return value;
    }
    
    public void setComparison(int function){
        this.comparison = function;
    }
    
    public static String getComparisonName(int function){
        if(function == Equivalent){
            return "=";
        }else if(function == GreaterThan){
            return ">";
        }else if(function == GreaterThanOrEqual){
            return ">=";
        }else if(function == LessThanOrEqual){
            return "<=";
        }else if(function == LessThan){
            return "<";
        }else if(function == NotEquivalent){
            return "<>";
        }else if(function == Like){
            return "LIKE";
        }else if(function == NotLike){
            return "NOT LIKE";
        }else{
            return null;
        }
    }
    
    public static int getComparisonByIdent(String ident){
        if("=".equals(ident)){
            return Equivalent;
        }else if(">".equals(ident)){
            return GreaterThan;
        }else if(">=".equals(ident)){
            return GreaterThanOrEqual;
        }else if("<=".equals(ident)){
            return LessThanOrEqual;
        }else if("<".equals(ident)){
            return LessThan;
        }else if("!=".equals(ident)){
            return NotEquivalent;
        }else if("<>".equals(ident)){
            return NotEquivalent;
        }else if("like".equalsIgnoreCase(ident)){
            return Like;
        }else{
            return -1;
        }
    }
    
    public int getComparison(){
        return comparison;
    }
    
    public void setValue(Comparable value){
        this.value = value;
    }
    
    public int compareTo(Object o) {
        if(o instanceof Comparative){
            Comparative other = (Comparative)o;
            return this.getValue().compareTo(other.getValue());
        }else if(o instanceof Comparable){
            return this.getValue().compareTo(o);
        }
        return -1;
    }
    
    public boolean intersect(int function,Comparable other,Comparator comparator){
        return Intersector.intersect(function,other,this.comparison,this.getValue(),comparator);
    }
    public String toString(){
        return ""+this.value;
    }
    
    public Object clone(){
        return new Comparative(this.comparison,this.value);
    }
}
