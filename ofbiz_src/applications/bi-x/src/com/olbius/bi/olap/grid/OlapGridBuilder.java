package com.olbius.bi.olap.grid;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.ReturnResultCallback;

public abstract class OlapGridBuilder extends AbstractOlap {

	public OlapGridBuilder() {
		setOlapResult(new OlapGrid(this, new ReturnResultGridEx()));
	}
	
	public void addDataField(String name, String col, ReturnResultCallback<?> callBack) {
		((OlapResultQueryEx)getOlapResult().getResultQuery()).addDataField(name, col, callBack);
	}
	
	public void addDataField(String name, String col) {
		addDataField(name, col, null);
	}
	
	public void addDataField(String name) {
		addDataField(name, null, null);
	}
	
}
