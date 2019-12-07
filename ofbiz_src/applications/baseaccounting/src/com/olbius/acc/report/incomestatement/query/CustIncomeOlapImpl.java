package com.olbius.acc.report.incomestatement.query;


import java.util.Date;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import com.olbius.acc.report.incomestatement.entity.IncomeConst;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.chart.AbstractOlapChart;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class CustIncomeOlapImpl extends OlbiusOlapService{
	
	private OlbiusQuery query;
	protected String dateType;
	protected String customerId;
	protected String categoryId;
	protected String organizationPartyId;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("dateType", (String) context.get(IncomeOlapConstant.DATATYPE));
		putParameter("categoryId", (String) context.get(IncomeOlapConstant.CATEGORY_ID));
		putParameter("customerId", (String) context.get(IncomeOlapConstant.CUSTOMER_ID));
		putParameter("organizationPartyId", MultiOrganizationUtil.getCurrentOrganization(delegator, ((GenericValue)context.get("userLogin")).getString("userLoginId")));
		
	}
	
	protected void initQuery(){
		Condition cond = new Condition();
		
		query = new OlbiusQuery(getSQLProcessor());
		OlbiusQuery subQuery = new OlbiusQuery();
		
		dateType = (String) getParameter(IncomeOlapConstant.DATATYPE);
		customerId = (String) getParameter(IncomeOlapConstant.CUSTOMER_ID);
		categoryId = (String) getParameter(IncomeOlapConstant.CATEGORY_ID);
		organizationPartyId = (String) getParameter(IncomeOlapConstant.ORG_PARTY_ID);
		dateType = getDateType(dateType);
		if(categoryId != null) {
			cond.and(Condition.makeEQ("cd.category_id", categoryId));
		}
		if(customerId != null) {
			cond.and(Condition.makeEQ("pgd.party_id", customerId));
		}
		
		boolean isFilterDateType = true;
		if (getOlapResult() instanceof AbstractOlapChart) isFilterDateType = false;
		
		String columDateType = "dd.".concat(dateType);
		
		cond.and(Condition.makeIn("gad.gl_account_id", getGlAccount()));
		cond.and(Condition.makeBetween("dd.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		cond.and(Condition.makeEQ("opg.party_id", organizationPartyId));
		subQuery.from("acctg_trans_fact", "atf");
		subQuery.select("pgd.party_id", "customerId")
		.select("pgd.party_code", "customerCode")
		.select("pgd.name", "customerName")
		.select(columDateType, isFilterDateType);
		
		subQuery.select("sum(case when gad.account_code = '511' then (-atf.amount) else 0 end)", IncomeConst.SALE_INCOME)
		.select("sum(case when gad.account_code = '5211' then (atf.amount) else 0 end)", IncomeConst.SALE_DISCOUNT)
		.select("sum(case when gad.account_code = '5212' then (atf.amount) else 0 end)", IncomeConst.SALE_RETURN)
		.select("sum(case when gad.account_code = '5213' then (atf.amount) else 0 end)", IncomeConst.PROMOTION)
		.select("sum(case when gad.account_code = '632' then (atf.amount) else 0 end)", IncomeConst.COGS)
		.join(Join.INNER_JOIN, "date_dimension", "dd", "atf.transaction_dim_date = dd.dimension_id")
		.join(Join.INNER_JOIN, "gl_account_relationship", "gar", "atf.gl_account_dim_id = gar.dimension_id")
		.join(Join.INNER_JOIN, "gl_account_dimension", "gad", "gad.dimension_id = gar.parent_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", "pgd", "atf.party_dim_id = pgd.dimension_id" )		
		.join(Join.INNER_JOIN, "product_dimension", "pd", "atf.product_dim_id = pd.dimension_id" )
		.join(Join.INNER_JOIN, "product_category_relationship", "pcr", "pd.dimension_id = pcr.product_dim_id" )
		.join(Join.INNER_JOIN, "date_dimension", "dpcrf", "pcr.from_dim_date = dpcrf.dimension_id" )
		.join(Join.INNER_JOIN, "date_dimension", "dpcrt", "pcr.thru_dim_date = dpcrt.dimension_id" )
		.join(Join.INNER_JOIN, "category_dimension", "cd", "cd.dimension_id = pcr.category_dim_id AND cd.category_type = 'CATALOG_CATEGORY' AND dd.date_value >= dpcrf.date_value AND (dpcrt.date_value IS NULL OR dpcrt.date_value >= dd.date_value) " )
		.join(Join.INNER_JOIN, "party_dimension", "opg", "opg.dimension_id = atf.organization_party_dim_id")
		.where(cond);
		
		subQuery.groupBy("customerId")
		.groupBy("customerCode")
		.groupBy("customerName")
		.groupBy(columDateType, isFilterDateType);
		
		String aliasSubQuery = "tmp";
		
		String netValueCol = aliasSubQuery + "." + IncomeConst.SALE_INCOME + " - " + aliasSubQuery + "." 
				+ IncomeConst.PROMOTION + " - " + aliasSubQuery + "." + IncomeConst.SALE_RETURN 
				+ " - " + aliasSubQuery + "." + IncomeConst.SALE_DISCOUNT;
		String grossProfitCol = netValueCol + " - " + aliasSubQuery + "." + IncomeConst.COGS;
		query.select("*")
		.select(netValueCol, IncomeConst.NET_REVENUE)
		.select(grossProfitCol, IncomeConst.GROSS_PROFIT)
		.from(subQuery, aliasSubQuery);
	}
	
	@Override
	public void prepareResultGrid() {
		String dateType = getDateType((String) getParameter("dateType"));
		addDataField("dateTime", dateType);
		addDataField("customerId", "customerId");
		addDataField("customerCode","customerCode");
		addDataField("customerName","customerName");
		addDataField(IncomeConst.SALE_INCOME, IncomeConst.SALE_INCOME);
		addDataField(IncomeConst.SALE_DISCOUNT, IncomeConst.SALE_DISCOUNT);
		addDataField(IncomeConst.SALE_RETURN, IncomeConst.SALE_RETURN);
		addDataField(IncomeConst.PROMOTION, IncomeConst.PROMOTION);
		addDataField(IncomeConst.COGS, IncomeConst.COGS);
		addDataField(IncomeConst.NET_REVENUE, IncomeConst.NET_REVENUE);
		addDataField(IncomeConst.GROSS_PROFIT, IncomeConst.GROSS_PROFIT);
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
	
	//TODO this function is also used in com.olbius.acc.report.incomestatement.query.QueryIncomeStatement,
	//so need to optimize 
	protected List<Object> getGlAccount(){
		List<Object> listAccountCode = FastList.newInstance();
		List<String> listGl = UtilMisc.toList("511","5211","5212","5213","632");
		listAccountCode.addAll(listGl);
		return listAccountCode;
	}
}
