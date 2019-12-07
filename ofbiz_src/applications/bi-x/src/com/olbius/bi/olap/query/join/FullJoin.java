package com.olbius.bi.olap.query.join;

public class FullJoin extends AbstractJoin{

	@Override
	public String getJoinName() {
		return FULL_OUTER_JOIN;
	}

}
