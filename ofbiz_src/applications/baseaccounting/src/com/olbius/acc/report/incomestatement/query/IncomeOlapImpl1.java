package com.olbius.acc.report.incomestatement.query;

import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.query.OlapQuery;

public class IncomeOlapImpl1 extends QueryIncomeStatement{
	public IncomeOlapImpl1(Delegator delegator,String dimension) {
		super(delegator);
		super.set_dimension(dimension);
		this.delegator = delegator;
	}
	
	public Delegator getDelegator() {
		return delegator;
	}

	public void setDelegator(Delegator delegator) {
		this.delegator = delegator;
	}

	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
	
	
}
