package com.olbius.bi.olap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.ofbiz.base.util.Debug;

public abstract class OlbiusReturnResultCallback<T> implements ReturnResultCallback<T>{

	private Set<String> columns = new TreeSet<String>();
	private ResultSet result;
	private Map<String, Object> map = new HashMap<String, Object>();
	
	public OlbiusReturnResultCallback(String... columns) {
		for(String s: columns) {
			this.columns.add(s);
		}
	}
	
	public void setResult(ResultSet result) {
		this.result = result;
	}

	public void get() {
		map.clear();
		for(String s : columns) {
			try {
				map.put(s, result.getObject(s));
			} catch (SQLException e) {
				Debug.logError(e, OlbiusReturnResultCallback.class.getName());
			}
		}
	}

	@Override
	public T get(Object object) {
		get();
		return get(map);
	}
	
	public abstract T get(Map<String, Object> map);

}
