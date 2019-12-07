package com.olbius.basesales.order;

import java.util.List;

import javolution.util.FastList;

public class DataStrings {
	private List<String> data;

	public DataStrings(){
		data = FastList.newInstance();
	}
	public List<String> getData() {
		return data;
	}

	public void setData(List<String> data) {
		this.data = data;
	}
	public String get(int i){
		if(data == null) return null;
		return data.get(i);
	}
	public int size(){
		return data.size();
	}
	public void add(String d){
		data.add(d);
	}
	public String composeCode(){
		String s = "";
		for(int i = 0; i < data.size(); i++){
			s = s + data.get(i);
			if(i < data.size() - 1) s = s + "@";
		}
		return s;
	}
	public String toString(){
		String s = "";
		if(data != null){
			for(int i = 0; i < data.size(); i++) s = s + data.get(i) + ",";
		}
		return s;
	}
}
