package com.olbius.baselogistics.report;

import java.util.Date;
import java.util.Map;

import org.ofbiz.service.DispatchContext;

import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class AverageExecutedPurchasesOrder extends OlbiusOlapService {

	private OlbiusQuery query;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("dateType", (String) context.get("dateType"));
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
		query
			.select("pt.party_code as party_code")
			.select("pt.name as party_name")
			.select("COUNT(DISTINCT order_id) as order_num")
			.select("Sum(CASE WHEN osf.executed_time > 0 THEN osf.executed_time ELSE 0 END) as executed_time_total")
			.select(
				"Sum(CASE WHEN osf.executed_time > 0 THEN osf.executed_time ELSE 0 END) / COUNT(DISTINCT order_id) as average_executed_time")
			.select(
					"Sum(CASE WHEN osf.executed_time > 0 THEN osf.executed_time ELSE 0 END) / COUNT(DISTINCT order_id) / 60 as average_executed_time_hour")
				.select(dateType).from("order_status_fact", "osf")
				.join(Join.INNER_JOIN, "date_dimension", "dd", "osf.status_date_dim_id = dd.dimension_id")
				.join(Join.INNER_JOIN, "status_dimension", "stt", "osf.status_dim_id = stt.dimension_id")
				.join(Join.INNER_JOIN, "party_dimension", "pt", "osf.party_from_dim_id = pt.dimension_id")
				.where(Condition
						.makeBetween("osf.status_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
						.andEQ("osf.order_type_id", "PURCHASE_ORDER").andEQ("stt.status_id", "ORDER_COMPLETED"))
				.groupBy("osf.party_from_dim_id")
				.groupBy(dateType);
		return query;
	}

	@Override
	public void prepareResultGrid() {
		String dateType = getDateType((String) getParameter("dateType"));
		addDataField("dateTime", dateType);
		addDataField("party_code", "party_code");
		addDataField("party_name", "party_name");
		addDataField("order_num", "order_num");
		addDataField("executed_time_total", "executed_time_total");
		addDataField("average_executed_time", "average_executed_time");
		addDataField("average_executed_time_hour", "average_executed_time_hour");
	}
}