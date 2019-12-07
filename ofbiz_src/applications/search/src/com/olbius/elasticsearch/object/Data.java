package com.olbius.elasticsearch.object;

public class Data {
	
	private String index;
	private String type;
	private Index data;

	public Data(String index, String type, Index data) {
		this.index = index;
		this.type = type;
		this.data = data;
	}
	
	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Index getData() {
		return data;
	}

	public void setData(Index data) {
		this.data = data;
	}
	
	public static Data buildData(String index, String type, Index data) {
		return new Data(index, type, data);
	}

}
