package com.olbius.acc.report.incomestatement.query;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;


public class LoyalIncomeOlapImpl extends IncomeOlapImpl{
	
	public LoyalIncomeOlapImpl(Delegator delegator) {
		super(delegator);
		// TODO Auto-generated constructor stub
	}

	protected String groupId;
	private void initQuery(){
		Condition cond = new Condition();
		
		query = new OlbiusQuery(getSQLProcessor());
		dateType = (String) getParameter(DATATYPE);
		groupId = (String) getParameter(GROUP_ID);
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
		if(groupId != null) {
			cond.and(Condition.makeEQ("pcgd.party_classification_group_id", groupId));
		}
		cond.and(Condition.makeIn("gad.gl_account_id", getGlAccount()));
//		cond.and(Condition.makeEQ("cd.category_type", "CATALOG_CATEGORY"));
		cond.and(Condition.makeBetween("dd.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		cond.and(Condition.makeEQ("opg.party_id", organizationPartyId));
		query.from("acctg_trans_fact", "atf")
		.select("dd.".concat(dateType), "transTime")
		.select("gad.gl_account_id", "glAccountId")
		.select("pcgd.party_classification_group_id", "groupId")
		.select("pd.product_id", "productId")
		.select("cd.category_id", "categoryId")
		.select("sum(atf.amount)", "amount")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "atf.transaction_dim_date = dd.dimension_id")
		.join(Join.INNER_JOIN, "gl_account_dimension", "gad", "atf.gl_account_dim_id = gad.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "pgd", "atf.party_dim_id = pgd.dimension_id" )		
		.join(Join.INNER_JOIN, "product_dimension", "pd", "atf.product_dim_id = pd.dimension_id" )
		.join(Join.INNER_JOIN, "product_category_relationship", "pcr", "pd.dimension_id = pcr.product_dim_id" )
		.join(Join.INNER_JOIN, "date_dimension", "dpcrf", "pcr.from_dim_date = dpcrf.dimension_id" )
		.join(Join.INNER_JOIN, "date_dimension", "dpcrt", "pcr.thru_dim_date = dpcrt.dimension_id" )
		.join(Join.INNER_JOIN, "category_dimension", "cd", "cd.dimension_id = pcr.category_dim_id AND cd.category_type = 'CATALOG_CATEGORY' AND dd.date_value >= dpcrf.date_value AND (dpcrt.date_value IS NULL OR dpcrt.date_value >= dd.date_value) " )
		.join(Join.INNER_JOIN, "party_classification_fact", "pcf", "atf.party_dim_id = pcf.party_dim_id" )
		.join(Join.INNER_JOIN, "party_class_group_dimension", "pcgd", "pcgd.dimension_id = pcf.party_class_group_dim_id" )
		.join(Join.INNER_JOIN, "party_dimension", "opg", "opg.dimension_id = atf.organization_party_dim_id")
		.where(cond)
		.groupBy("transTime")
		.groupBy("glAccountId")
		.groupBy("groupId")
		.groupBy("productId")
		.groupBy("categoryId")
		.groupBy("transTime", false)
		.orderBy("transTime", OlbiusQuery.DESC);
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
	
	public class LoyalIncomeStatementResult extends ReturnResultGrid{
		
		public LoyalIncomeStatementResult() {
			addDataField("groupId");
			addDataField("productId");
			addDataField("categoryId");
			addDataField("glAccountId");
			addDataField("transTime");
			addDataField("amount");
		}
		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				map.put("groupId", result.getString("groupId"));
				map.put("productId", result.getString("productId"));
				map.put("categoryId", result.getString("categoryId"));
				String[] tmp = result.getString("transTime").split("-");
				String transTime = "";
				if(tmp != null) {
					for(int i = tmp.length -1; i >= 0; i--) {
						transTime += tmp[i];
						transTime += "/";
					}
					transTime = transTime.substring(0, transTime.length()-1);
				}
				map.put("transTime", transTime);
				map.put("amount", result.getString("amount"));
				map.put("glAccountId", result.getString("glAccountId"));
			} catch (Exception e) {
				Debug.logError(e.getMessage(), MODULE);
			}
			return map;
		}
		
	}
}
