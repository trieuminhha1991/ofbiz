package com.olbius.acc.report.incomestatement.query;

import org.ofbiz.entity.Delegator;

import com.olbius.acc.report.incomestatement.entity.IncomeConst;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class CatIncomeOlapImpl extends QueryIncomeStatement{
	public CatIncomeOlapImpl(Delegator delegator,String dimension) {
		super(delegator);
		super.set_dimension(dimension);
	}
	
	@Override
	protected void initQuery(){
		Condition cond = new Condition();
		
		query = new OlbiusQuery(getSQLProcessor());
		dateType = (String) getParameter(DATATYPE);
		customerId = (String) getParameter(CUSTOMER_ID);
		categoryId = (String) getParameter(CATEGORY_ID);
		productId = (String) getParameter(PRODUCT_ID);
		organizationPartyId = (String) getParameter(ORG_PARTY_ID);
		dateType = getDateType(dateType);
		if(productId != null) {
			cond.and(Condition.makeEQ("pd.product_id", productId));
		}
		if(categoryId != null) {
			cond.and(Condition.makeEQ("cd.category_id", categoryId));
		}
		if(customerId != null) {
			cond.and(Condition.makeEQ("pgd.party_id", customerId));
		}
		cond.and(Condition.makeIn("gad.gl_account_id",getGlAccount()));
		//cond.and(Condition.makeEQ("cd.category_type", "CATALOG_CATEGORY"));
		cond.and(Condition.makeBetween("dd.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		cond.and(Condition.makeEQ("opg.party_id", organizationPartyId));
		
		OlbiusQuery subQuery = new OlbiusQuery();
		subQuery.from("acctg_trans_fact", "atf").select("dd.".concat(dateType), "transTime");
		switch (QueryIncomeStatement.get_dimension()) {
		case "general":
			subQuery.select("pgd.party_id", "partyId")
			.select("pd.product_id", "productId")
			.select("cd.category_id", "categoryId");
			break;
		case "product":
			subQuery.select("pd.product_id", "productId");
			break;
		case "party":
			subQuery.select("pgd.party_id", "partyId");
			break;
		case "category":
			subQuery.select("cd.category_id", "categoryId");
			break;	
		default:
			break;
		}
		
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
		.where(cond).groupBy("transTime").orderBy("transTime", OlbiusQuery.DESC);
		
		switch (QueryIncomeStatement.get_dimension()) {
			case "general":
				subQuery.groupBy("partyId")
				.groupBy("productId")
				.groupBy("categoryId");
				break;
			case "product":
				subQuery.groupBy("productId");
				break;
			case "party":
				subQuery.groupBy("partyId");
				break;
			case "category":
				subQuery.groupBy("categoryId");
				break;	
			default:
				break;
		}
		
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
	
	/*private void initQuery(){
		Condition cond = new Condition();
		
		query = new OlbiusQuery(getSQLProcessor());
		dateType = (String) getParameter(DATATYPE);
		categoryId = (String) getParameter(CATEGORY_ID);
		organizationPartyId = (String) getParameter(ORG_PARTY_ID);
		dateType = getDateType(dateType);
		if(categoryId != null) {
			cond.and(Condition.makeEQ("cd.category_id", categoryId));
		}
		cond.and(Condition.makeIn("gad.gl_account_id", getGlAccount()));
		cond.and(Condition.makeBetween("dd.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		//cond.and(Condition.makeEQ("cd.category_type", "CATALOG_CATEGORY"));
		cond.and(Condition.makeEQ("opg.party_id", organizationPartyId));
		query.from("acctg_trans_fact", "atf");
		if (hasTransTime.equals("Y")) {
			query.select("dd.".concat(dateType), "transTime");
		}
		query.select("gad.gl_account_id", "glAccountId")
		.select("cd.category_id", "categoryId")
		.select("sum(atf.amount)", "amount")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "atf.transaction_dim_date = dd.dimension_id")
		.join(Join.INNER_JOIN, "gl_account_dimension", "gad", "atf.gl_account_dim_id = gad.dimension_id")
		.join(Join.INNER_JOIN, "product_dimension", "pd", "atf.product_dim_id = pd.dimension_id" )
		.join(Join.INNER_JOIN, "product_category_relationship", "pcr", "pd.dimension_id = pcr.product_dim_id" )
		.join(Join.INNER_JOIN, "date_dimension", "dpcrf", "pcr.from_dim_date = dpcrf.dimension_id" )
		.join(Join.INNER_JOIN, "date_dimension", "dpcrt", "pcr.thru_dim_date = dpcrt.dimension_id" )
		.join(Join.INNER_JOIN, "category_dimension", "cd", "cd.dimension_id = pcr.category_dim_id AND cd.category_type = 'CATALOG_CATEGORY' AND dd.date_value >= dpcrf.date_value AND (dpcrt.date_value ISNULL OR dpcrt.date_value >= dd.date_value) " )
		.join(Join.INNER_JOIN, "party_dimension", "opg", "opg.dimension_id = atf.organization_party_dim_id")
		.where(cond)
		.groupBy("glAccountId")
		.groupBy("categoryId");
		if (hasTransTime.equals("Y")) {
			query.groupBy("transTime")
			.orderBy("transTime", OlbiusQuery.DESC);
		}else {
			query.orderBy("amount", OlbiusQuery.ASC);
		}
	}*/
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
	
	/*@Override
	public void prepareResultGrid() {
		addDataField("categoryId", "categoryId");
		addDataField("glAccountId","glAccountId");
		addDataField("amount","amount");
		if (hasTransTime.equals("Y")) {
			addDataField("transTime", new OlbiusReturnResultCallback<String>("transTime") {

				@Override
				public String get(Map<String, Object> map) {
					String[] tmp = ((String)map.get("transTime")).split("-");
					String transTime = "";
					if(tmp != null) {
						for(int i = tmp.length -1; i >= 0; i--) {
							transTime += tmp[i];
							transTime += "/";
						}
						transTime = transTime.substring(0, transTime.length()-1);
					}
					return transTime;
				}
			});
		}
	}*/
}
