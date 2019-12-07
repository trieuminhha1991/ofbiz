package com.olbius.acc.report.dashboard.query;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.ReturnResultCallback;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import java.math.BigDecimal;

public class IndexAccountingOlapImplv2 extends OlbiusBuilder {
	public static final String ORG = "ORG";
	
	public IndexAccountingOlapImplv2(Delegator delegator) {
		super(delegator);
	}

	protected OlbiusQuery query;
	
	@Override
	public void prepareResultGrid() {
		addDataField("x511",  "x511", new ReturnResultCallback<BigDecimal>() {
			@Override
			public BigDecimal get(Object object) {
				if(UtilValidate.isNotEmpty(object)){ return (BigDecimal) object; }else{ return new BigDecimal(0); }
			}
		});
		addDataField("x521", "x521", new ReturnResultCallback<BigDecimal>() {
			@Override
			public BigDecimal get(Object object) {
				if(UtilValidate.isNotEmpty(object)){ return (BigDecimal) object; }else{ return new BigDecimal(0); }
			}
		});
		addDataField("x632", "x632", new ReturnResultCallback<BigDecimal>() {
			@Override
			public BigDecimal get(Object object) {
				if(UtilValidate.isNotEmpty(object)){ return (BigDecimal) object; }else{ return new BigDecimal(0); }
			}
		});		
	}
	
	@SuppressWarnings("unchecked")
	private void initQuery() {
		String organizationPartyId = (String) getParameter(ORG);
		Condition cond = new Condition();

		query = new OlbiusQuery(getSQLProcessor());
	
		cond.and(Condition.makeBetween("transaction_date", getSqlDate(fromDate), getSqlDate(thruDate)));
		cond.and(Condition.makeEQ("organization_party_id", organizationPartyId));
		query.from("acctg_document_list_fact", "adlf")
		.select("SUM( CASE WHEN account_code like '632%' THEN (dr_amount - cr_amount*-1 ) ELSE 0 END)", "x632")
		.select("SUM( CASE WHEN account_code like '511%' THEN (cr_amount - dr_amount * -1) ELSE 0 END)", "x511")
		.select("SUM( CASE WHEN account_code like '521%' THEN (dr_amount + cr_amount * -1) ELSE 0 END)", "x521")
		.where(cond);
	}
	
	@Override
	protected OlbiusQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
}
