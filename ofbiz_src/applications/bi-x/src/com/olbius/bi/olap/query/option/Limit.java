package com.olbius.bi.olap.query.option;

import com.olbius.bi.olap.OlapInterface;

public class Limit extends AbstractOptionResult {

	private long limit;

	public Limit() {
		setParam(OlapInterface.LIMIT);
	}

	@Override
	public void addOption() {
		if (checkParam()) {
			query.limit(limit);
		}
	}

	@Override
	public boolean checkParam() {
		Object limit = this.parameters.get(this.param);
		if (limit != null && limit instanceof Long) {
			this.limit = (Long) limit;
			return true;
		}
		return false;
	}

}
