package com.olbius.baselogistics.report;

import java.util.List;

import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.chart.OlapLineChart;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class ReceiveChartImpl extends OlbiusBuilder{
	public static final String FILTER_TOP = "FILTER_TOP"; 
	public static final String FILTER_SORT = "FILTER_SORT"; 
	public static final String DATE_TYPE = "DATE_TYPE";
	public static final String FACILITY_ID = "FACILITY_ID";
	public static final String OWNER_PARTY_ID = "OWNER_PARTY_ID";
	public static final String CATEGORY_ID = "CATEGORY_ID";
	
	private OlbiusQuery queryReceive;
	
	public ReceiveChartImpl(Delegator delegator) {
		super(delegator);
	}
	
	@Override
	 public void prepareResultBuilder() {
		if(getOlapResult() instanceof OlapLineChart) {
			String dateType = getDateType((String) getParameter("dateType"));
			getOlapResult().putParameter(DATE_TYPE, dateType);
		}
	 };
	 
	@Override
	public void prepareResultChart() {
		String dateType = (String) getParameter(DATE_TYPE);
		dateType = getDateType(dateType);
		if(getOlapResult() instanceof OlapLineChart) {		
			getOlapResult().putParameter(DATE_TYPE, dateType);
			addSeries("product_code");
			addXAxis(dateType);
			addYAxis("quantityOnHandTotal");
		}
	}

	@SuppressWarnings("unchecked")
	private void initQuery() {
		String dateType = (String) getParameter(DATE_TYPE);
		List<Object> facilityId = (List<Object>) getParameter(FACILITY_ID);
		String ownerPartyId = (String) getParameter(OWNER_PARTY_ID);
		List<Object> categoryId = (List<Object>) getParameter(CATEGORY_ID);
		
		Integer filterTop = (Integer) getParameter(FILTER_TOP);
		String filterSort = (String) getParameter(FILTER_SORT);
		
		dateType = getDateType(dateType);
		queryReceive = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		
		queryReceive.from("inventory_item_fact", "iif")
		.select("dd.".concat(dateType))
		.select("prd.product_code")
		.select("CASE WHEN prd.require_amount = 'Y' THEN sum(iif.amount_on_hand_total) ELSE sum(iif.quantity_on_hand_total) END AS quantityOnHandTotal");
		queryReceive.join(Join.INNER_JOIN, "date_dimension", "dd", "iif.inventory_date_dim_id = dd.dimension_id");
		queryReceive.join(Join.INNER_JOIN, "product_dimension", "prd", "iif.product_dim_id = prd.dimension_id");
		queryReceive.join(Join.INNER_JOIN, "facility_dimension", "fd", "iif.facility_dim_id = fd.dimension_id");
		queryReceive.join(Join.INNER_JOIN, "party_dimension", "pd", "fd.owner_party_dim_id = pd.dimension_id AND pd.party_id = "+"'"+ownerPartyId+"'");
		queryReceive.join(Join.INNER_JOIN, "party_dimension", "pto", "iif.owner_party_dim_id = pto.dimension_id AND pto.party_id = "+"'"+ownerPartyId+"'");
		condition.and(Condition.makeBetween("dd.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		if (facilityId != null){
			condition.and(Condition.makeIn("fd.facility_id", facilityId, !facilityId.isEmpty()));
		}
		if (categoryId != null){
			condition.and(Condition.makeIn("iif.category_dim_id", categoryId, !categoryId.isEmpty()));
		}
		condition.and(Condition.makeEQ("iif.inventory_type", "RECEIVE"))
		.and("iif.physical_inventory_id is null");
		
		queryReceive.groupBy("dd.".concat(dateType))
		.groupBy("prd.product_code")
		.orderBy("dd.".concat(dateType));
		
		if ("RECEIVE_DESC".equals(filterSort)){
			condition.and(Condition.make("iif.inventory_type = 'RECEIVE'"))
			.and(Condition.make("iif.quantity_on_hand_total > 0"));
			queryReceive.where(condition);
			queryReceive.groupBy("dd.".concat(dateType))
			.groupBy("prd.product_code")
			.groupBy("prd.require_amount")
			.groupBy("inventory_type")
			.groupBy("quantity_on_hand_total")
			.orderBy("quantity_on_hand_total DESC")
			.limit(filterTop);
		} else if ("RECEIVE_ASC".equals(filterSort)){
			condition.and(Condition.make("iif.inventory_type = 'RECEIVE'"))
			.and(Condition.make("iif.quantity_on_hand_total > 0"));
			queryReceive.where(condition);
			queryReceive.groupBy("dd.".concat(dateType))
			.groupBy("prd.product_code")
			.groupBy("prd.require_amount")
			.groupBy("inventory_type")
			.groupBy("quantity_on_hand_total")
			.orderBy("quantity_on_hand_total ASC")
			.limit(filterTop);
		}
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(queryReceive == null) {
			initQuery();
		}
		return queryReceive;
	}

}
