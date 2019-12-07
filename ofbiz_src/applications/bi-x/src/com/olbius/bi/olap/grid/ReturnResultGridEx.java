package com.olbius.bi.olap.grid;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;

import com.olbius.bi.olap.OlbiusReturnResultCallback;
import com.olbius.bi.olap.ReturnResultCallback;

public class ReturnResultGridEx extends ReturnResultGrid implements OlapResultQueryEx {

	protected Map<String, String> columns; 
	
	protected Map<String, ReturnResultCallback<?>> callBacks; 
	
	public ReturnResultGridEx() {
		super();
		columns = new HashMap<String, String>();
		callBacks = new HashMap<String, ReturnResultCallback<?>>();
	}
	
	@Override
	public void addDataField(String name, String col) {
		addDataField(name, col, null);
	}
	
	@Override
	public void addDataField(String name) {
		addDataField(name, null, null);
	}
	
	@Override
	protected Map<String, Object> getObject(ResultSet result) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			
			for(String name : dataFields) {
				
				Object tmp = null;
				
				if(columns.get(name) != null) {
					tmp = result.getObject(columns.get(name));
				}
				
				if(callBacks.get(name) != null) {
					
					if(callBacks.get(name) instanceof OlbiusReturnResultCallback) {
						((OlbiusReturnResultCallback<?>)callBacks.get(name)).setResult(result);
					}
					
					tmp = callBacks.get(name).get(tmp);
				}
				
				if(tmp != null) {
					map.put(name, tmp);
				}
				
			}
			
		} catch (Exception e) {
			Debug.logError(e, ReturnResultGridEx.class.getName());
		}
		return map;
	}

	@Override
	public String toDataField(String col) {
		if(col== null) {
			return null;
		}
		for(String name : dataFields) {
			if(col.equals(toColumn(name))) {
				return name;
			}
		}
		return null;
	}

	@Override
	public String toColumn(String name) {
		return columns.get(name);
	}

	@Override
	public void addDataField(String name, String col, ReturnResultCallback<?> callBack) {
		dataFields.add(name);
		columns.put(name, col);
		callBacks.put(name, callBack);
	}
	
	@Override
	public void addDataField(String name, ReturnResultCallback<?> callBack) {
		dataFields.add(name);
		callBacks.put(name, callBack);
	}

}
