package com.olbius.bi.olap.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.olbius.bi.olap.AbstractOlapResult;
import com.olbius.bi.olap.OlapInterface;
import com.olbius.bi.olap.OlapResultQueryInterface;

public abstract class AbstractOlapChart extends AbstractOlapResult implements OlapChartInterface {

	protected Map<String, List<Object>> yAxis;
	protected List<String> xAxis;
	protected String dateType;

	public AbstractOlapChart(OlapInterface olap, OlapResultQueryInterface query) {
		super(olap, query);
		olap.setChart(isChart());
		xAxis = new ArrayList<String>();
		yAxis = new TreeMap<String, List<Object>>();
	}

	@Override
	public Map<String, List<Object>> getYAxis() {
		return yAxis;
	}

	@Override
	public List<Object> getYAxis(String key) {
		return yAxis.get(key);
	}

	@Override
	public List<String> getXAxis() {
		return xAxis;
	}
	
	@Override
	protected Map<String, Object> putMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("xAxis", xAxis);
		map.put("yAxis", yAxis);
		if(dateType != null) {
			map.put("dateType", dateType);
		}
		return map;
	}

	@Override
	public boolean isChart() {
		return true;
	}

	public String getDateType() {
		return dateType;
	}

	public void setDateType(String dateType) {
		this.dateType = dateType;
	}
	
}
