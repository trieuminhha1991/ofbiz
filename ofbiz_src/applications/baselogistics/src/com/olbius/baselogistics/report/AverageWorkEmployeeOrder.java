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

public class AverageWorkEmployeeOrder extends OlbiusOlapService {

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
		String dateType = getDateType((String) getParameter("dateType"));
		OlbiusQuery query = makeQuery();
		OlbiusQuery queryTotal = makeQuery();
		OlbiusQuery queryTemp = makeQuery();
		
		// tong so order da hoan thanh theo chu ki
		queryTotal
			.select("COUNT(DISTINCT order_id) as order_num_total")
			.select(dateType,"dateTime")
			.from("order_status_fact", "osf")
				.join(Join.INNER_JOIN, "date_dimension", "dd", "osf.status_date_dim_id = dd.dimension_id")
				.join(Join.INNER_JOIN, "status_dimension", "stt", "osf.status_dim_id = stt.dimension_id")
				.join(Join.INNER_JOIN, "party_dimension", "cpn", "osf.party_from_dim_id = cpn.dimension_id ")
				.where(Condition
						.makeBetween("osf.status_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
						.andEQ("osf.order_type_id", "SALES_ORDER")
						.andEQ("cpn.party_id", company)
						.andEQ("stt.status_id", "ORDER_COMPLETED"))
						.groupBy(dateType);
	
	
		// tong so order nhan vien da hoan thanh theo chu ki
		queryTemp
			.select("pt.party_code as party_code")
			.select("pt.name as party_name")
			.select("COUNT(DISTINCT order_id) as order_num")
			.select(dateType, "dateTime")
				.from("order_status_fact", "osf")
				.join(Join.INNER_JOIN, "date_dimension", "dd", "osf.status_date_dim_id = dd.dimension_id")
				.join(Join.INNER_JOIN, "status_dimension", "stt", "osf.status_dim_id = stt.dimension_id")
				.join(Join.INNER_JOIN, "party_dimension", "pt", "osf.party_update_dim_id = pt.dimension_id ")
				.join(Join.INNER_JOIN, "party_dimension", "cpn", "osf.party_from_dim_id = cpn.dimension_id ")
				.where(Condition
						.makeBetween("osf.status_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
						.andEQ("stt.status_id", "ORDER_COMPLETED")
						.andEQ("osf.order_type_id", "SALES_ORDER")
						.andEQ("cpn.party_id", company))
				.groupBy("osf.party_update_dim_id")
				.groupBy(dateType);
		
		//join 
		query
			.select("temp.party_code as party_code")
			.select("temp.party_name as party_name")
			.select("temp.order_num/total.order_num_total * 100 as percentage_order")
			.select("temp.order_num as order_num")
			.select("total.order_num_total as order_num_total")
			.select("total.dateTime as dateTime")
				.from(queryTotal, "total")
				.join(Join.INNER_JOIN, queryTemp, "temp", "total.dateTime = temp.dateTime");
		
		return query;
	}

	@Override
	public void prepareResultGrid() {
		String dateType = getDateType((String) getParameter("dateType"));
		addDataField("dateTime", "dateTime");
		addDataField("party_code", "party_code");
		addDataField("party_name", "party_name");
		addDataField("order_num", "order_num");
		addDataField("order_num_total", "order_num_total");
		addDataField("percentage_order", "percentage_order");
		
	}
}