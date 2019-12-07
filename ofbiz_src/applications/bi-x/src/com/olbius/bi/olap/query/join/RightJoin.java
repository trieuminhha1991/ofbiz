package com.olbius.bi.olap.query.join;

public class RightJoin extends AbstractJoin{

	@Override
	public String getJoinName() {
		return RIGHT_OUTER_JOIN;
	}

}
