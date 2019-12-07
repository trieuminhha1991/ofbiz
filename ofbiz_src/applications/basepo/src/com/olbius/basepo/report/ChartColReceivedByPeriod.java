package com.olbius.basepo.report;

import java.util.Date;
import java.util.Map;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.DispatchContext;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.function.Sum;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class ChartColReceivedByPeriod extends OlbiusOlapService{
	private OlbiusQuery query;
	
	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("filterTop", (Integer) context.get("filterTop"));
		putParameter("filterSort", (String) context.get("filterSort"));
		putParameter("dateType", context.get("dateType"));
	}

	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			query = init();
		}
		return query;
	}

	private OlbiusQuery init() {

		OlbiusQuery query = makeQuery();
		String dateType = getDateType((String) getParameter("dateType"));
		Integer filterTop = (Integer) getParameter("filterTop");
		String filterSort = (String) getParameter("filterSort");
		if (UtilValidate.isEmpty(filterTop)) filterTop = 5;
		if (!"DESC".equals(filterSort) && !"ASC".equals(filterSort)) filterSort = "DESC";
		query.select(dateType)
		.select(new Sum("(CASE WHEN pr.require_amount = 'Y' THEN iif.amount_on_hand_total * iif.unit_cost ELSE iif.quantity_on_hand_total * iif.unit_cost END)"), "totalPrice")
		.from("inventory_item_fact", "iif")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "dd.dimension_id = iif.inventory_date_dim_id")
		.join(Join.INNER_JOIN, "product_dimension", "pr", "pr.dimension_id = iif.product_dim_id")
		.groupBy(dateType)
		.where(Condition.makeBetween("iif.inventory_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
					.andEQ("iif.inventory_change_type", "PURCHASE_ORDER")
					.andEQ("pr.product_type", "FINISHED_GOOD")
					.andEQ("iif.inventory_type", "RECEIVE"))
		.orderBy("totalPrice " + filterSort)
		.limit(filterTop);
		
		return query;
	}

	@Override
	public void prepareResultChart() {
		if (getOlapResult() instanceof OlapColumnChart) {
			String dateType = getDateType((String) getParameter("dateType"));
			addXAxis(dateType);
			addYAxis("totalPrice");
		}
	}
}
