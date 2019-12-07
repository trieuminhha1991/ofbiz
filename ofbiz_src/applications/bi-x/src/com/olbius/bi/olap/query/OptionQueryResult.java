package com.olbius.bi.olap.query;

import com.olbius.bi.olap.OlapResultQueryInterface;

/**
 * @author Nguyen Ha
 */
public interface OptionQueryResult extends OptionQuery {

	void setResult(OlapResultQueryInterface result);
	
	void addOption();
	
}
