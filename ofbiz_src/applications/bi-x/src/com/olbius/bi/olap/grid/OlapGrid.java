package com.olbius.bi.olap.grid;

import org.ofbiz.base.util.Debug;

import com.olbius.bi.olap.OlapInterface;
import com.olbius.bi.olap.OlapResultQueryInterface;

public class OlapGrid extends AbstractOlapGrid{

	public OlapGrid(OlapInterface olap, OlapResultQueryInterface query) {
		super(olap, query);
	}

	@Override
	public boolean isChart() {
		return false;
	}

	@Override
	protected void result(Object object) {
		try{
			ReturnResultGrid grid = (ReturnResultGrid) object;
			fields = grid.getDataFields();
			data = grid.getData();
			id = grid.getId();
			out = grid.getOut();
		}catch(Exception e) {
			Debug.logError(e.getMessage(), olap.getModule());
		}
	}
}
