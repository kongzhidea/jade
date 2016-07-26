package com.meidusa.amoeba.parser.statment;


public abstract class AbstractStatment implements Statment {

	private int parameterCount;
	
	public int getParameterCount() {
		return parameterCount;
	}

	public void setParameterCount(int count){
		this.parameterCount = count;
	}

}
