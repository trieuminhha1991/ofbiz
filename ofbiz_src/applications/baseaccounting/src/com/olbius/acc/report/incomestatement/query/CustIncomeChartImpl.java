package com.olbius.acc.report.incomestatement.query;

import java.util.Date;
import java.util.Map;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.chart.OlapPieChart;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class CustIncomeChartImpl extends OlbiusOlapService{
	private OlbiusQuery query;
	
	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter(IncomeOlapConstant.ORG_PARTY_ID, MultiOrganizationUtil.getCurrentOrganization(delegator, ((GenericValue)context.get("userLogin")).getString("userLoginId")));
	}
	
	private void initQuery() {
		Condition cond = new Condition();
		query = new OlbiusQuery(getSQLProcessor());
		String organizationPartyId = (String) getParameter(IncomeOlapConstant.ORG_PARTY_ID);
		cond.and(Condition.makeEQ("gad.account_code","511"));
		cond.and(Condition.makeBetween("dd.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		cond.and(Condition.makeEQ("opg.party_id", organizationPartyId));
		
		query.from("acctg_trans_fact", "atf");
		query.select("pgd.party_id", "partyId")
		.select("pgd.party_code", "partyCode")
		.select("pgd.name", "customerName");
		query.select("-sum(atf.amount)", "amount")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "atf.transaction_dim_date = dd.dimension_id")
		.join(Join.INNER_JOIN, "gl_account_relationship", "gar", "atf.gl_account_dim_id = gar.dimension_id")
		.join(Join.INNER_JOIN, "gl_account_dimension", "gad", "gad.dimension_id = gar.parent_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", "opg", "opg.dimension_id = atf.organization_party_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", "pgd", "pgd.dimension_id = atf.party_dim_id");
		
		query.where(cond);
		
		query.groupBy("partyId")
		.groupBy("partyCode")
		.groupBy("customerName")
		.orderBy("customerName", OlbiusQuery.DESC);
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}

	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapPieChart){
			addXAxis("customerName");
			addYAxis("amount");
		}
	}

}
