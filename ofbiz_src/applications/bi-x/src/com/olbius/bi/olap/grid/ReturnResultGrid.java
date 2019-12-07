package com.olbius.bi.olap.grid;

import java.io.ByteArrayOutputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.ofbiz.base.util.Debug;

import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.grid.export.OlapExport;
import com.olbius.bi.olap.query.OlapQuery;

/**
 * @author havip
 *
 */
public abstract class ReturnResultGrid implements OlapResultQueryInterface {

	protected List<String> dataFields;
	
	private List<Map<String, Object>> data;
	
	private String id;
	
	private int count;
	
	private boolean export;
	
	private OlapExport olapExport;
	
	public ReturnResultGrid() {
		dataFields = new ArrayList<String>();
		data = new ArrayList<Map<String, Object>>();
		count = 0;
		id = UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	@Override
	public Object resultQuery(OlapQuery query) {
		try {
			ResultSet result = query.getResultSet();
			while(result.next()) {
				Map<String, Object> map = getObject(result);
				if (!map.isEmpty()) {
					if(export && olapExport != null) {
						olapExport.addData(map);
					} else {
						addData(map);
					}
				}
				
			}
		} catch (Exception e) {
			Debug.logError(e, ReturnResultGrid.class.getName());
		}
		return this;
	}

	protected abstract Map<String, Object> getObject(ResultSet result);
	
	public List<Map<String, Object>> getData() {
		return data;
	}
	
	public List<String> getDataFields() {
		return dataFields;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void addDataField(String name) {
		dataFields.add(name);
	}
	
	public void addData(Map<String, Object> data) {
		data.put(id, count++);
		this.data.add(data);
	}

	public void setOlapExport(OlapExport olapExport) {
		this.export = true;
		this.olapExport = olapExport;
	}
	
	public ByteArrayOutputStream getOut() {
		if(export && olapExport != null) {
			return (ByteArrayOutputStream) olapExport.getOutputStream();
		}
		return null;
	}
}
