package com.olbius.olap;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.jdbc.SQLProcessor;

import com.olbius.bi.olap.OlapDate;
import com.olbius.bi.olap.TypeOlap;

public abstract class AbstractOlap extends TypeOlap implements OlapInterface {

	private SQLProcessor processor;

	protected Date fromDate;

	protected Date thruDate;
	
	protected Map<String, List<Object>> yAxis;
	protected List<String> xAxis;

	public AbstractOlap() {
		xAxis = new ArrayList<String>();
		yAxis = new TreeMap<String, List<Object>>();
	}
	
	@Override
	public void setFromDate(Date date) {
		this.fromDate = date;
	}

	@Override
	public void setThruDate(Date date) {
		this.thruDate = date;
	}

	@Override
	public void SQLProcessor(SQLProcessor processor) {
		this.processor = processor;
	}
	
	@Override
	public void close() throws GenericDataSourceException {
		processor.close();
	}
	
	@Override
	public org.ofbiz.entity.jdbc.SQLProcessor getSQLProcessor() {
		return this.processor;
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
	
	protected void axis(Map<String, Map<String, Object>> map, String dateType) throws GenericDataSourceException, GenericEntityException, SQLException {
		
		if(map.isEmpty()) {
			return;
		}
		
		OlapDate olapDate = new OlapDate();
		olapDate.SQLProcessor(getSQLProcessor());
		olapDate.setFromDate(fromDate);
		olapDate.setThruDate(thruDate);
		
		xAxis = olapDate.getValues(dateType);
		
		yAxis = new TreeMap<String, List<Object>>();
		
		for(String key : map.keySet()) {
			if(key != null && yAxis.get(key)==null) {
				yAxis.put(key, new ArrayList<Object>());
			}
		}
		
		
		for(String s : xAxis) {
			
			for(String key : map.keySet()) {
				if(key != null) {
					if(map.get(key).get(s)!= null) {
						yAxis.get(key).add(map.get(key).get(s));
					} else {
						yAxis.get(key).add(new Integer(0));
					}
				}
			}
		}
	}
	
}
