package com.olbius.basepo.report;

import java.util.Date;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.DispatchContext;

import com.olbius.bi.olap.chart.OlapLineChart;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class AverageCostChart extends OlbiusOlapService {


	private OlbiusQuery query;
	
	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		
		putParameter("group", context.get("group[]"));

		putParameter("facility", context.get("facilityId"));

		putParameter("product", context.get("productId"));
		
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
		
		if (UtilValidate.isNotEmpty(getParameter("facility")) && UtilValidate.isNotEmpty(getParameter("product"))){
			String facility = (String) getParameter("facility");
			String product = (String) getParameter("product");
			
			query.select("pf.average_cost")
			.select("pr.product_code")
			.select("dd.date_value")
			.from("product_facility_fact", "pf")
			.join(Join.INNER_JOIN, "product_dimension", "pr", "pf.product_dim_id = pr.dimension_id")
			.join(Join.INNER_JOIN, "facility_dimension", "fa", "pf.facility_dim_id = fa.dimension_id")
			.join(Join.INNER_JOIN, "date_dimension", "dd", "pf.date_dim_id = dd.dimension_id")
			.where(Condition.makeBetween("pf.date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
						.andEQ("fa.facility_id", facility)
						.andEQ("pr.product_id", product)
						.andEQ("pr.product_type", "FINISHED_GOOD"))
			.groupBy("pf.date_dim_id").groupBy("pf.average_cost").orderBy("pf.date_dim_id")
			.groupBy("pr.product_id").groupBy("fa.facility_id");
		}
		return query;
	}
	

	@Override
	public void prepareResultBuilder() {
		if(getOlapResult() instanceof OlapLineChart) {
			getOlapResult().putParameter(DATE_TYPE, "date_value");
		}
	};
	
	@Override
	public void prepareResultChart() {
		if (getOlapResult() instanceof OlapLineChart) {
			addSeries("product_code");
			addXAxis("date_value");
			addYAxis("average_cost");
		}
	}

}
