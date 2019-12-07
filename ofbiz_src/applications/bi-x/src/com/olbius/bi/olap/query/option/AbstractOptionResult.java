package com.olbius.bi.olap.query.option;

import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.query.OptionQueryResult;

/**
 * @author Nguyen Ha
 */
public abstract class AbstractOptionResult extends AbstractOptionQuery implements OptionQueryResult {

	protected OlapResultQueryInterface result;
	
	@Override
	public void setResult(OlapResultQueryInterface result) {
		this.result = result;
	}
	
}
