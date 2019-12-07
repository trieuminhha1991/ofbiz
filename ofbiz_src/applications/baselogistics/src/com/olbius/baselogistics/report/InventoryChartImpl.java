package com.olbius.baselogistics.report;

import java.util.List;
import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.chart.OlapLineChart;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class InventoryChartImpl extends OlbiusBuilder{
	public static final String PRODUCT_ID = "PRODUCT_ID"; 
	public static final String DATE_TYPE = "DATE_TYPE";
	public static final String FACILITY_ID = "FACILITY_ID";
	public static final String OWNER_PARTY_ID = "OWNER_PARTY_ID";
	
	public InventoryChartImpl(Delegator delegator) {
		super(delegator);
	}

	private OlbiusQuery query;
	
	@Override
	public void prepareResultChart() {
		String dateType = (String) getParameter(DATE_TYPE);
		dateType = getDateType(dateType);
		if(getOlapResult() instanceof OlapLineChart) {		
			getOlapResult().putParameter(DATE_TYPE, dateType);
			addSeries("product_code");
			addXAxis(dateType);
			addYAxis("total");
		}
	}
	
	@SuppressWarnings("unchecked")
	private void initQuery() {
		String dateType = (String) getParameter(DATE_TYPE);
		List<Object> facilityId = (List<Object>) getParameter(FACILITY_ID);
		List<Object> productId = (List<Object>) getParameter(PRODUCT_ID);
//		String ownerPartyId = (String) getParameter(OWNER_PARTY_ID);
		dateType = getDateType(dateType);
		
		Condition condition = new Condition();
		OlbiusQuery tmpQuery = OlbiusQuery.make();
		query = new OlbiusQuery(getSQLProcessor());
		tmpQuery.distinctOn("facility_dim_id", "product_dim_id", "date_dim_id")
				.distinctOn(dateType, dateType != null)
				.distinctOn("date_value", dateType == null)
				.select("*").from("facility_fact")
				.join(Join.INNER_JOIN, "date_dimension", "date_dim_id = date_dimension.dimension_id")
				.where(Condition.makeBetween("date_value", getSqlDate(fromDate), getSqlDate(thruDate)))
				.orderBy("date_value", OlbiusQuery.DESC, dateType == null)
				.orderBy(dateType, OlbiusQuery.DESC, dateType != null);
		
		query.select("SUM(inventory_total)", "total")
		.select("product_dimension.product_code")
		.select("date_dimension.".concat(dateType))
		.from(tmpQuery, "tmp")
		.join(Join.INNER_JOIN, "date_dimension", "tmp.date_dim_id = date_dimension.dimension_id")
		.join(Join.INNER_JOIN, "product_dimension", "tmp.product_dim_id = product_dimension.dimension_id")
		.join(Join.INNER_JOIN, "facility_dimension", "tmp.facility_dim_id = facility_dimension.dimension_id");
		condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		if (facilityId != null){
			condition.and(Condition.makeIn("facility_dimension.facility_id", facilityId, facilityId != null));
		}
		if(productId != null){
			condition.and(Condition.makeIn("product_dimension.product_code", productId, productId!=null));
		}
		condition.and(Condition.make("available_to_promise_total != 0 OR inventory_total != 0"));
		query.where(condition)
		.groupBy("product_dimension.product_code")
		.groupBy("date_dimension.".concat(dateType))
		.orderBy("date_dimension.".concat(dateType), OlbiusQuery.DESC);
	}
	
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}

}
