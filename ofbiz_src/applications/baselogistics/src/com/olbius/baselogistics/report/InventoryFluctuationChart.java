package com.olbius.baselogistics.report;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.DispatchContext;

import com.olbius.bi.olap.chart.OlapLineChart;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.function.Sum;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class InventoryFluctuationChart extends OlbiusOlapService {


	private OlbiusQuery query;
	
	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
       
		Timestamp t = UtilDateTime.nowTimestamp();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(t.getTime());
		int curYear = cal.get(Calendar.YEAR);
		int _num = 3;
		if (UtilValidate.isNotEmpty(context.get("numYear"))) {
			_num = new Integer((String)context.get("numYear"));
		}
		String startDate = String.valueOf(curYear - _num + 1) + "-01-01";
        try {
            Date date = formatter.parse(startDate);
            setFromDate(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
		setThruDate(new Date());

		putParameter("group", context.get("group[]"));

		putParameter("facility", context.get("facilityId"));

		putParameter("product", context.get("productId"));
		
		putParameter("inventoryType", context.get("inventoryType"));
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
		
		String inventoryType = (String) getParameter("inventoryType");

		if (UtilValidate.isNotEmpty(getParameter("facility")) && UtilValidate.isNotEmpty(getParameter("product"))){
			String facility = (String) getParameter("facility");
			String product = (String) getParameter("product");
			
			query.select("dd.month_of_year")
			.select("dd.year_name");
			if ("EXPORT".equals(inventoryType)){
				query.select(new Sum("CASE WHEN pr.require_amount = 'Y' THEN ABS(iif.amount_on_hand_total) ELSE ABS(iif.quantity_on_hand_total) END"), "quantity");
			} else {
				query.select(new Sum("CASE WHEN pr.require_amount = 'Y' THEN iif.amount_on_hand_total ELSE iif.quantity_on_hand_total END"), "quantity");
			}
			query.from("inventory_item_fact", "iif")
			.join(Join.INNER_JOIN, "product_dimension", "pr", "iif.product_dim_id = pr.dimension_id")
			.join(Join.INNER_JOIN, "facility_dimension", "fa", "iif.facility_dim_id = fa.dimension_id")
			.join(Join.INNER_JOIN, "date_dimension", "dd", "iif.inventory_date_dim_id = dd.dimension_id")
			.groupBy("dd.year_name")
			.groupBy("dd.month_of_year")
			.where(Condition.makeBetween("iif.inventory_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
						.andEQ("fa.facility_id", facility)
						.andEQ("pr.product_id", product)
						.andEQ("pr.product_type", "FINISHED_GOOD"));
			if (!"TOTAL".equals(inventoryType)){
				query.where(Condition.makeBetween("iif.inventory_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
						.andEQ("fa.facility_id", facility)
						.andEQ("pr.product_id", product)
						.andEQ("pr.product_type", "FINISHED_GOOD")
						.andEQ("iif.inventory_type", inventoryType));
			}
		}
		return query;
	}
	

	@Override
	public void prepareResultBuilder() {
		if(getOlapResult() instanceof OlapLineChart) {
			getOlapResult().putParameter(DATE_TYPE, "month_of_year");
		}
	};
	
	@Override
	public void prepareResultChart() {
		if (getOlapResult() instanceof OlapLineChart) {
			addSeries("year_name");
			addXAxis("month_of_year");
			addYAxis("quantity");
		}
	}

}