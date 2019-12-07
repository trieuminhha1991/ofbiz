package com.olbius.acc.report.incomestatement.query;

import java.util.Map;

import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusReturnResultCallback;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class CustGroupIncomeOlapImpl extends LoyalIncomeOlapImpl{
	public CustGroupIncomeOlapImpl(Delegator delegator) {
		super(delegator);
		// TODO Auto-generated constructor stub
	}

	private void initQuery(){
		Condition cond = new Condition();
		//FIXME check valid party is COPPER/SILVER/GOLD. Check in party_classification_fact
		query = new OlbiusQuery(getSQLProcessor());
		dateType = (String) getParameter(DATATYPE);
		groupId = (String) getParameter(GROUP_ID);
		dateType = getDateType(dateType);
		organizationPartyId = (String) getParameter(ORG_PARTY_ID);
		if(groupId != null) {
			cond.and(Condition.makeEQ("pcgd.party_classification_group_id", groupId));
		}
		cond.and(Condition.makeIn("gad.gl_account_id", getGlAccount()));
		cond.and(Condition.makeBetween("dd.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		cond.and(Condition.makeEQ("opg.party_id", organizationPartyId));
		query.from("acctg_trans_fact", "atf");
		if (hasTransTime.equals("Y")) {
			query.select("dd.".concat(dateType), "transTime");
		}
		
		query.select("gad.gl_account_id", "glAccountId")
		.select("pcgd.party_classification_group_id", "groupId")
		.select("sum(atf.amount)", "amount")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "atf.transaction_dim_date = dd.dimension_id")
		.join(Join.INNER_JOIN, "gl_account_dimension", "gad", "atf.gl_account_dim_id = gad.dimension_id")
		.join(Join.INNER_JOIN, "product_dimension", "pd", "atf.product_dim_id = pd.dimension_id" )
		.join(Join.INNER_JOIN, "product_category_relationship", "pcr", "pd.dimension_id = pcr.product_dim_id" )
		.join(Join.INNER_JOIN, "party_classification_fact", "pcf", "atf.party_dim_id = pcf.party_dim_id" )
		.join(Join.INNER_JOIN, "party_class_group_dimension", "pcgd", "pcgd.dimension_id = pcf.party_class_group_dim_id" )
		.join(Join.INNER_JOIN, "party_dimension", "opg", "opg.dimension_id = atf.organization_party_dim_id")
		.where(cond)
		.groupBy("glAccountId")
		.groupBy("groupId");
		if (hasTransTime.equals("Y")) {
			query.groupBy("transTime")
			.orderBy("transTime", OlbiusQuery.DESC);
		}else {
			query.orderBy("amount", OlbiusQuery.ASC);
		}
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
	
	@Override
	public void prepareResultGrid() {
		addDataField("groupId", "groupId");
		addDataField("glAccountId", "glAccountId");
		addDataField("amount","amount");
		if (hasTransTime.equals("Y")) {
			addDataField("transTime", new OlbiusReturnResultCallback<String>("transTime") {

				@Override
				public String get(Map<String, Object> map) {
					String transTime = "";
					if(map.get("transTime") != null && map.get("transTime") instanceof String){
						String[] tmp = ((String)map.get("transTime")).split("-");
						if(tmp != null) {
							for(int i = tmp.length -1; i >= 0; i--) {
								transTime += tmp[i];
								transTime += "/";
							}
							transTime = transTime.substring(0, transTime.length()-1);
						}
					}
					return transTime;
				}
			});
		}
	}
}
