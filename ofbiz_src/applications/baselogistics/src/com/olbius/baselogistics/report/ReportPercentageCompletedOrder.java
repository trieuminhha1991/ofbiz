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

public class ReportPercentageCompletedOrder extends OlbiusOlapService {

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
		String dateType = getDateType((String) getParameter("dateType"));
		OlbiusQuery queryTemp = makeQuery();
		OlbiusQuery queryTotal = makeQuery();
		OlbiusQuery query = makeQuery();
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		// so order hoan thanh trong chu ki
		queryTemp
		.select("COUNT(DISTINCT order_id) as order_num_total")
		.select(dateType,"dateTime")
			.from("sales_order_new_fact", "so")
			.join(Join.INNER_JOIN, "date_dimension", "dd", "so.order_date_dim_id = dd.dimension_id")
			.join(Join.INNER_JOIN, "party_dimension", "cpn", "so.vendor_dim_id = cpn.dimension_id ")
			.where(Condition
				.makeBetween("so.order_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
				.andEQ("so.order_status_id", "ORDER_COMPLETED")
				.andEQ("cpn.party_id", company))
			.groupBy(dateType);
		
		// so order duoc tao trong chu ki
		queryTotal
		.select("COUNT(DISTINCT order_id) as order_num_total")
		.select(dateType,"dateTime")
			.from("sales_order_new_fact", "so")
			.join(Join.INNER_JOIN, "date_dimension", "dd", "so.order_date_dim_id = dd.dimension_id")
			.join(Join.INNER_JOIN, "party_dimension", "cpn", "so.vendor_dim_id = cpn.dimension_id ")
			.where(Condition
				.makeBetween("so.order_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
				.andEQ("cpn.party_id", company))
			.groupBy(dateType);
		
		query
		.select("temp.order_num_total/total.order_num_total * 100 as percentage_order")
		.select("temp.order_num_total as order_num")
		.select("total.order_num_total as order_num_total")
		.select("total.dateTime as dateTime")
			.from(queryTotal, "total")
			.join(Join.INNER_JOIN, queryTemp, "temp", "total.dateTime = temp.dateTime");
		return query;
	}

	@Override
	public void prepareResultGrid() {
		addDataField("dateTime", "dateTime");
		addDataField("order_num", "order_num");
		addDataField("order_num_total", "order_num_total");
		addDataField("percentage_order", "percentage_order");
		
	}
}