package com.olbius.bi.olap.grid;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.olbius.bi.olap.AbstractOlapResult;
import com.olbius.bi.olap.OlapInterface;
import com.olbius.bi.olap.OlapResultQueryInterface;

public abstract class AbstractOlapGrid extends AbstractOlapResult implements OlapGridInterface{

	protected List<String> fields;
	protected List<Map<String, Object>> data;
	protected String id;
	protected ByteArrayOutputStream out;
	
	public AbstractOlapGrid(OlapInterface olap, OlapResultQueryInterface query) {
		super(olap, query);
		fields = new ArrayList<String>();
		data = new ArrayList<Map<String, Object>>();
	}
	
	@Override
	public List<String> getDataFields() {
		return this.fields;
	}

	@Override
	public List<Map<String, Object>> getData() {
		return this.data;
	}

	@Override
	public String getId() {
		return this.id;
	}
	
	@Override
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	protected Map<String, Object> putMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("datafields", fields);
		map.put("data", data);
		map.put("id", id);
		map.put("out", out);
		return map;
	}

}
