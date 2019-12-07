package com.olbius.bi.olap.query.join;

public class LeftJoin extends AbstractJoin{

	@Override
	public String getJoinName() {
		return LEFT_OUTER_JOIN;
	}

}
