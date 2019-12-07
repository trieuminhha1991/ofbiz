package com.olbius.basepos.report;

import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.chart.ReturnResultChartInterface;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class ReturnProduct extends OlbiusBuilder{
	public ReturnProduct(Delegator delegator) {
		super(delegator);
	}

	public static String resource = "BasePosUiLabels";
	public static final String LIMITT = "LIMITT";
	public static final String ORG = "ORG";
	public static final String PARTY_ID = "PARTY_ID";
	public static final String FACILITY = "FACILITY";
	
	private OlbiusQuery query;
	
	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapColumnChart) {
			((ReturnResultChartInterface) getOlapResult().getResultQuery()).setSeriesDefaultName("-");
			addXAxis("product_code");
			addYAxis("_total");
		}
	}
	
	private void initQuery() {
		query = OlbiusQuery.make(getSQLProcessor());
		Long limitt = (Long) getParameter(LIMITT);
		String partyId = (String) getParameter(PARTY_ID);
		String org = (String) getParameter(ORG);
		String facilityId = (String) getParameter(FACILITY);

		Condition condition = new Condition();
		query.from("return_order_fact");
		query.select("currency_dimension.currency_id");
		query.select("product_dimension.product_code");
		query.select("product_dimension.internal_name");
		query.select("sum(quantity)", "_quantity");
		query.select("sum(total)", "_total");
		
		query.join(Join.INNER_JOIN, "date_dimension", null, "return_date_dim_id = date_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "currency_dimension", null, "currency_dim_id = currency_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "party_dimension", "party", "party_dim_id = party.dimension_id");
		query.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "facility_dimension", "facility", "facility_dim_id = facility.dimension_id");
		query.join(Join.INNER_JOIN, "party_dimension", "owner_party", "facility.owner_party_dim_id = owner_party.dimension_id");
		
		condition.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		condition.andEQ("facility.facility_id", facilityId, facilityId != null);
		condition.andEQ("party.party_id", partyId, partyId != null);
		condition.andEQ("owner_party.party_id", org, org != null);
		query.where(condition);
		
		query.groupBy("currency_dimension.currency_id");
		query.groupBy("product_dimension.product_code");
		query.groupBy("product_dimension.internal_name");
		
		query.orderBy("_total", "DESC");
		query.limit(limitt);
	}
	
	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			initQuery();
		}
		return query;
	}
}
