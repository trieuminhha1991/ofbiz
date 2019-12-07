package com.olbius.baselogistics.report;

import java.util.List;

import org.ofbiz.entity.Delegator;
import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.chart.OlapLineChart;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class ExportChartImpl extends OlbiusBuilder {
	public static final String PRODUCT_ID = "PRODUCT_ID";
	public static final String DATE_TYPE = "DATE_TYPE";
	public static final String FACILITY_ID = "FACILITY_ID";
	public static final String OWNER_PARTY_ID = "OWNER_PARTY_ID";
	public static final String CATEGORY_ID = "CATEGORY_ID";

	private OlbiusQuery queryExport;

	public ExportChartImpl(Delegator delegator) {
		super(delegator);
	}

	@Override
	public void prepareResultChart() {
		String dateType = (String) getParameter(DATE_TYPE);
		dateType = getDateType(dateType);
		if (getOlapResult() instanceof OlapLineChart) {
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
		List<Object> productId = (List<Object>) getParameter(PRODUCT_ID);
		String ownerPartyId = (String) getParameter(OWNER_PARTY_ID);
		List<Object> categoryId = (List<Object>) getParameter(CATEGORY_ID);
		dateType = getDateType(dateType);
		queryExport = new OlbiusQuery(getSQLProcessor());

		Condition condition = new Condition();
		queryExport.from("inventory_item_fact", "iif").select("dd.".concat(dateType)).select("prd.product_code")
				.select("CASE WHEN prd.require_amount = 'Y' THEN sum(iif.amount_on_hand_total) * (-1) ELSE sum(iif.quantity_on_hand_total) * (-1) END AS quantityOnHandTotal")
				.join(Join.INNER_JOIN, "date_dimension", "dd", "iif.inventory_date_dim_id = dd.dimension_id")
				.join(Join.INNER_JOIN, "product_dimension", "prd", "iif.product_dim_id = prd.dimension_id")
				.join(Join.INNER_JOIN, "facility_dimension", "fd", "iif.facility_dim_id = fd.dimension_id")
				.join(Join.INNER_JOIN, "party_dimension", "pd",
						"fd.owner_party_dim_id = pd.dimension_id AND pd.party_id = " + "'" + ownerPartyId + "'");
		condition.and(Condition.makeBetween("dd.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		condition.and(Condition.make("iif.quantity_on_hand_total != 0 OR iif.available_to_promise_total >= 0"));
		if (facilityId != null) {
			condition.and(Condition.makeIn("fd.facility_id", facilityId, !facilityId.isEmpty()));
		}
		if (productId != null) {
			condition.and(Condition.makeIn("prd.product_code", productId, !productId.isEmpty()));
		}
		if (categoryId != null) {
			condition.and(Condition.makeIn("category_dim_id", categoryId, !categoryId.isEmpty()));
		}
		condition.and(Condition.makeEQ("iif.inventory_type", "EXPORT"));
		queryExport.where(condition);
		queryExport.groupBy("dd.".concat(dateType)).groupBy("prd.product_code, prd.require_amount")
				.orderBy("dd.".concat(dateType));
	}

	@Override
	protected OlapQuery getQuery() {
		if (queryExport == null) {
			initQuery();
		}
		return queryExport;
	}

}
