package com.olbius.bi.olap.query.option;

import com.olbius.bi.olap.OlapInterface;
import com.olbius.bi.olap.grid.OlapResultQueryEx;
import com.olbius.bi.olap.query.OlbiusQueryInterface;

public class Sort extends AbstractOptionResult {

	private String col;
	
	private String type;

	public Sort() {
		setParam(OlapInterface.SORT);
	}

	@Override
	public void addOption() {
		if (checkParam()) {
			if (query instanceof OlbiusQueryInterface) {
				if(col == null) {
					return;
				}
				((OlbiusQueryInterface) query).orderBy(col, type);
			}
		}
	}

	@Override
	public boolean checkParam() {
		Object col = this.parameters.get(this.param);
		Object type = this.parameters.get(OlapInterface.SORT_TYPE);
		if (col != null && col instanceof String && type != null && type instanceof String) {
			if(result != null && result instanceof OlapResultQueryEx) {
				this.col = ((OlapResultQueryEx) result).toColumn((String) col);
			} else {
				this.col = (String) col;
			}
			this.type = (String) type;
			return true;
		}
		return false;
	}

}
