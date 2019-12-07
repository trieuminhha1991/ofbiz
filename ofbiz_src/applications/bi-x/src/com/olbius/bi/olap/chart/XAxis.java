package com.olbius.bi.olap.chart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XAxis {

	private List<String> xAxis;
	private Map<String, Map<String, Object>> data;

	public List<String> getXAxis() {
		if(xAxis == null) {
			xAxis = new ArrayList<String>();
		}
		return xAxis;
	}

	public void setXAxis(List<String> xAxis) {
		this.xAxis = xAxis;
	}

	public Map<String, Map<String, Object>> getData() {
		return data;
	}

	public void setData(Map<String, Map<String, Object>> data) {
		this.data = data;
	}
	
	public void add(String name) {
		if(!getXAxis().contains(name)) {
			getXAxis().add(name);
		}
	}

}
