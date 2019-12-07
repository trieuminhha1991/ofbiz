package com.olbius.olap.grid;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.jdbc.SQLProcessor;

public interface OlapGridInterface {

void SQLProcessor(SQLProcessor processor);
	
	void setFromDate(Date date);
	
	void setThruDate(Date date);
	
	void close() throws GenericDataSourceException;
	
	SQLProcessor getSQLProcessor();
	
	List<Map<String, Object>> getDataFields();
	
	List<Map<String, Object>> getData();
	
	List<Map<String, Object>> getColumns();
	
	String getId();
	
	void setId(String id);
}
