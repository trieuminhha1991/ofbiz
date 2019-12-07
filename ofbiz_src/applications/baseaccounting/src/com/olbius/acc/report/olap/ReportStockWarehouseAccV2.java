package com.olbius.acc.report.olap;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.DispatchContext;

import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.grid.ReturnResultGridEx;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class ReportStockWarehouseAccV2 extends OlbiusOlapService {

	private OlbiusQuery query;
	private ReturnResultGrid result = new ReturnResultGrid();

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("facilityId", context.get("facilityId[]"));
		putParameter("product", context.get("product[]"));
	}

	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			query = init();
		}
		return query;
	}
	
	@SuppressWarnings("unchecked")
	private OlbiusQuery init() {
		List<Object> facilityIds = (List<Object>) getParameter("facilityId");
		List<Object> productIds = (List<Object>) getParameter("product");
		
		OlbiusQuery query = makeQuery();
		
		query.select("facility_id").select("facility_name").select("product_id").select("product_code")
			.select("product_name").select("quantity_ending").select("quantity_uom_id").select("amount_ending");
		query.from("inventory_product_cost_sum_fact");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(getThruDate());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Timestamp sqlDate = new Timestamp(calendar.getTimeInMillis());
		
		Condition cond = Condition.make("date_value = '" + sqlDate + "'");
		if (UtilValidate.isNotEmpty(facilityIds)) {
			cond.and(Condition.makeIn("facility_id", facilityIds));
		}
		if (UtilValidate.isNotEmpty(productIds)) {
			cond.and(Condition.makeIn("product_id", productIds));
		}
		
		query.where(cond);
		
		return query;
	}

	@Override
	public void prepareResultGrid() {
		addDataField("productId", "product_id");
		addDataField("productCode", "product_code");
		addDataField("productName", "product_name");
		addDataField("facilityId", "facility_id");
		addDataField("facilityName", "facility_name");
		addDataField("quantityUomId", "quantity_uom_id");
		addDataField("endingQuantity", "quantity_ending");
		addDataField("endingAmount", "amount_ending");
	}

	@Override
	protected OlapResultQueryInterface returnResultGrid() {
		return result;
	}

	private class ReturnResultGrid extends ReturnResultGridEx {
		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = super.getObject(result);
			try {
				map.put("facility_id", result.getString("facility_id"));
				map.put("facility_name", result.getString("facility_name"));
				map.put("product_id", result.getString("product_id"));
				map.put("product_code", result.getString("product_code"));
				map.put("product_name", result.getString("product_name"));
				map.put("quantity_uom_id", result.getString("quantity_uom_id"));
				if (UtilValidate.isNotEmpty(result.getBigDecimal("quantity_ending"))) {
					map.put("endingQuantity", result.getBigDecimal("quantity_ending"));
				} else {
					map.put("endingQuantity", BigDecimal.ZERO);
				}
				if (UtilValidate.isNotEmpty(result.getBigDecimal("amount_ending"))) {
					map.put("endingAmount", result.getBigDecimal("amount_ending"));
				} else {
					map.put("endingAmount", BigDecimal.ZERO);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return map;
		}
	}
}