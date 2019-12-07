package com.olbius.bi.olap.query;

public abstract class AbstractFunction implements Function {

	private String[] array;

	protected void set(String... s) {
		array = s;
	}
	
	protected abstract String func();
	
	@Override
	public String toString() {
		String tmp = func() + "(";
		for(int i = 0; i < array.length; i++) {
			tmp += array[i];
			if(i < array.length - 1) {
				tmp += ",";
			} else {
				tmp += ")";
			}
		}
		return tmp;
	}
}
