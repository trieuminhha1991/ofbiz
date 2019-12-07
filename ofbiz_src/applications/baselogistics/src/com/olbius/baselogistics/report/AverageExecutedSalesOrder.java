package com.olbius.baselogistics.report;

import java.util.Date;
import java.util.Map;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class AverageExecutedSalesOrder extends OlbiusOlapService {

	private OlbiusQuery query;
	private GenericValue userLogin = null;
	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("dateType", (String) context.get("dateType"));
		userLogin = (GenericValue) context.get("userLogin");
	}

	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			query = init();
		}
		return query;
	}

	private OlbiusQuery init() {
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		OlbiusQuery queryTransit = makeQuery();
		OlbiusQuery queryComplete = makeQuery();
		OlbiusQuery queryTotal = makeQuery();
		String dateType = getDateType((String) getParameter("dateType"));
		
		// time from approved to transit
		queryTransit
		.select("COUNT(DISTINCT order_id) as order_num")
		.select("Sum(CASE WHEN osf.executed_time > 0 THEN osf.executed_time ELSE 0 END) as executed_time_total")
		.select(
			"Sum(CASE WHEN osf.executed_time > 0 THEN osf.executed_time ELSE 0 END) / COUNT(DISTINCT order_id) as average_executed_time")
		.select(
				"Sum(CASE WHEN osf.executed_time > 0 THEN osf.executed_time ELSE 0 END) / COUNT(DISTINCT order_id) / 60 as average_executed_time_hour")
			.select(dateType, "dateTime").from("order_status_fact", "osf")
			.join(Join.INNER_JOIN, "date_dimension", "dd", "osf.status_date_dim_id = dd.dimension_id")
			.join(Join.INNER_JOIN, "status_dimension", "stt", "osf.status_dim_id = stt.dimension_id")
			.join(Join.INNER_JOIN, "party_dimension", "cpn", "osf.party_from_dim_id = cpn.dimension_id ")
			.where(Condition
					.makeBetween("osf.status_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
					.andEQ("osf.order_type_id", "SALES_ORDER")
					.andEQ("stt.status_id", "ORDER_IN_TRANSIT")
					.andEQ("cpn.party_id", company))
			.groupBy(dateType);
		
		// time from transit to completed
		queryComplete
			.select("COUNT(DISTINCT order_id) as order_num")
			.select("Sum(CASE WHEN osf.executed_time > 0 THEN osf.executed_time ELSE 0 END) as executed_time_total")
			.select(
				"Sum(CASE WHEN osf.executed_time > 0 THEN osf.executed_time ELSE 0 END) / COUNT(DISTINCT order_id) as average_executed_time")
			.select(
					"Sum(CASE WHEN osf.executed_time > 0 THEN osf.executed_time ELSE 0 END) / COUNT(DISTINCT order_id) / 60 as average_executed_time_hour")
				.select(dateType, "dateTime").from("order_status_fact", "osf")
				.join(Join.INNER_JOIN, "date_dimension", "dd", "osf.status_date_dim_id = dd.dimension_id")
				.join(Join.INNER_JOIN, "status_dimension", "stt", "osf.status_dim_id = stt.dimension_id")
				.join(Join.INNER_JOIN, "party_dimension", "cpn", "osf.party_from_dim_id = cpn.dimension_id ")
				.where(Condition
						.makeBetween("osf.status_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
						.andEQ("osf.order_type_id", "SALES_ORDER")
						.andEQ("stt.status_id", "ORDER_COMPLETED")
						.andEQ("cpn.party_id", company))
				.groupBy(dateType);
		
		queryTotal
		.select("transit.order_num as order_num")
		.select("transit.executed_time_total + complete.executed_time_total as executed_time_total")
		.select("transit.average_executed_time + complete.average_executed_time as average_executed_time")
		.select("transit.average_executed_time_hour + complete.average_executed_time_hour as average_executed_time_hour")
		.select("transit.dateTime as dateTime")
			.from(queryTransit, "transit")
			.join(Join.INNER_JOIN, queryComplete, "complete", "transit.dateTime = complete.dateTime");
			
		return queryTotal;
	}

	@Override
	public void prepareResultGrid() {
		addDataField("dateTime", "dateTime");
		addDataField("order_num", "order_num");
		addDataField("executed_time_total", "executed_time_total");
		addDataField("average_executed_time", "average_executed_time");
		addDataField("average_executed_time_hour", "average_executed_time_hour");
	}
}