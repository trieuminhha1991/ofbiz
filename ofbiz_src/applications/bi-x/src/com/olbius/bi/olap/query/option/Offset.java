package com.olbius.bi.olap.query.option;

import java.util.Map;

import com.olbius.bi.olap.OlapInterface;

public class Offset extends AbstractOptionResult {

	private long offset;

	public Offset() {
		setParam(OlapInterface.OFFSET);
	}

	@Override
	public void addOption() {
		if(checkParam()) {
			query.offset(offset);
		}
	}

	@Deprecated
	public void addOption(Map<String, Object> map) {
	}

	@Override
	public boolean checkParam() {
		Object offset = this.parameters.get(this.param);
		if (offset != null && offset instanceof Long) {
			this.offset = (Long) offset;
			return true;
		}
		return false;
	}
	
}
