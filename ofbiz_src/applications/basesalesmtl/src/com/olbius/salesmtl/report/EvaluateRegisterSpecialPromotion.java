package com.olbius.salesmtl.report;

import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class EvaluateRegisterSpecialPromotion extends OlbiusBuilder{
	public static final String MONTHH = "MONTHH";
	public static final String YEARR = "YEARR";
	public static final String DISTRIBUTOR = "DISTRIBUTOR";
	
	public EvaluateRegisterSpecialPromotion(Delegator delegator) {
		super(delegator);
	}
	
	@Override
	public void prepareResultGrid() {
		addDataField("stt");
		addDataField("special_promo_id", "special_promo_id");
		addDataField("special_promo_name", "special_promo_name");
		addDataField("customer_code", "customer_code");
		addDataField("customer_name", "customer_name");
	}
	
	private OlbiusQuery query;
	
	private void initQuery() {
		String getYear = (String) getParameter(YEARR);
		String getMonth = (String) getParameter(MONTHH);
		String distributorId = (String) getParameter(DISTRIBUTOR);
		int queryYear = Integer.parseInt(getYear);
		int queryMonth = Integer.parseInt(getMonth);
		Condition condition = new Condition();
		condition.and("(pped.product_promo_type_id = 'PROMO_EXHIBITION' OR pped.product_promo_type_id = 'PROMO_ACCUMULATION')").andEQ("dd.month_of_year", queryMonth).andEQ("dd.year_name", queryYear)
		.andEQ("pd2.party_id", distributorId);
		
		query = new OlbiusQuery(getSQLProcessor());
		
		query.select("pped.product_promo_id", "special_promo_id").select("pped.promo_name", "special_promo_name")
		.select("pd.party_code", "customer_code").select("pd.name", "customer_name")
		.from("product_promo_ext_dimension", "pped")
		.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = pped.party_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "dd.dimension_id = pped.from_date_dim")
		.join(Join.INNER_JOIN, "customer_relationship", "cr", "cr.person_dim_id = pped.party_id")
		.join(Join.INNER_JOIN, "party_dimension", "pd2", "pd2.dimension_id = cr.group_dim_id")
		.where(condition).orderBy("special_promo_id");
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
}

