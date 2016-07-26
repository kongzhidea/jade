package com.meidusa.amoeba.sqljep.function;

import java.util.Comparator;

public class ComparativeAND extends ComparativeBaseList{
	
	public ComparativeAND(int function, Comparable<?> value) {
		super(function, value);
	}
	
	public ComparativeAND(){
	}
	
	public ComparativeAND(Comparative item){
		super(item);
	}
	
	@SuppressWarnings("unchecked")
	public boolean intersect(int function,Comparable other,Comparator comparator){
		for(Comparative source :list){
			if(!source.intersect(function, other, comparator)){
				return false;
			}
		}
		return true;
	}

}
