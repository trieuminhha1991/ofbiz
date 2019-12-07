package com.olbius.olap.grid;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.jdbc.SQLProcessor;

import com.olbius.bi.olap.TypeOlap;

public abstract class AbstractOlapGrid extends TypeOlap implements OlapGridInterface {

	private SQLProcessor processor;

	protected Date fromDate;

	protected Date thruDate;
	
	protected List<Map<String, Object>> columns;
	
	protected List<Map<String, Object>> dataFields;
	
	protected List<Map<String, Object>> data;
	
	protected String id;
	
	private int count;
	
	public AbstractOlapGrid() {
		columns = new ArrayList<Map<String, Object>>();
		dataFields = new ArrayList<Map<String, Object>>();
		data = new ArrayList<Map<String, Object>>();
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
	public List<Map<String, Object>> getData() {
		return data;
	}
	
	@Override
	public List<Map<String, Object>> getColumns() {
		return columns;
	}
	
	@Override
	public List<Map<String, Object>> getDataFields() {
		return dataFields;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public void setId(String id) {
		this.id = id;
	}
	
	public void addDataField(String name, String type) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("type", type);
		dataFields.add(map);
	}
	
	public void addColumn(String text, String dataField) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("text", text);
		map.put("datafield", dataField);
		columns.add(map);
	}
	
	public void addData(Map<String, Object> data) {
		data.put(id, count++);
		this.data.add(data);
	}
}
