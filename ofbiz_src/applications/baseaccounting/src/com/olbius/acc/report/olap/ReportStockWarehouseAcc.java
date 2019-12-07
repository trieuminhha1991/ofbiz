package com.olbius.acc.report.olap;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.DispatchContext;

import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.cache.dimension.FacilityDimension;
import com.olbius.bi.olap.cache.dimension.ProductDimension;
import com.olbius.bi.olap.grid.ReturnResultGridEx;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class ReportStockWarehouseAcc extends OlbiusOlapService {

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
	
	private List<Object> getFacilities(List<?> list) {
		List<Object> tmp = new ArrayList<Object>();
		if (list != null) {
			for (Object x : list) {
				Long y = FacilityDimension.D.getId(delegator, (String) x);
				if (y != -1) {
					tmp.add(y);
				}
			}
		}
		return tmp;
	}

	private List<Object> getProducts(List<?> list) {
		List<Object> tmp = new ArrayList<Object>();
		if (list != null) {
			for (Object x : list) {
				Long y = ProductDimension.D.getId(delegator, (String) x);
				if (y != -1) {
					tmp.add(y);
				}
			}
		}
		return tmp;
	}

	@SuppressWarnings("unchecked")
	private OlbiusQuery init() {
		List<Object> facilityDimIds = getFacilities((List<?>) getParameter("facilityId"));
		List<Object> productDimIds = getProducts((List<?>) getParameter("product"));
		List<Object> facilityIds = (List<Object>) getParameter("facilityId");
		List<Object> productIds = (List<Object>) getParameter("product");
		
		OlbiusQuery query = makeQuery();
		
		OlbiusQuery queryEnding = new OlbiusQuery();// ton kho cuoi ky
		queryEnding.from("inventory_item_fact","itf");
		queryEnding.select("itf.product_dim_id");
		queryEnding.select("itf.facility_dim_id");
		queryEnding.select("itf.quantity_uom_dim_id", "quantity_uom_dim_id");
		queryEnding.select("CASE WHEN prd.require_amount = 'Y' THEN sum(itf.amount_on_hand_total) ELSE sum(itf.quantity_on_hand_total) END", "endingQuantity");
		queryEnding.join(Join.INNER_JOIN, "product_dimension", "prd", "prd.dimension_id = itf.product_dim_id");
		Condition condEndingStock = Condition.make("itf.inventory_date_dim_id <= " + getSqlTime(getThruDate()));
		if (UtilValidate.isNotEmpty(facilityDimIds)) {
			condEndingStock.and(Condition.makeIn("itf.facility_dim_id", facilityDimIds));
		}
		if (UtilValidate.isNotEmpty(productDimIds)) {
			condEndingStock.and(Condition.makeIn("itf.product_dim_id", productDimIds));
		}
		queryEnding.where(condEndingStock);
		queryEnding.groupBy("itf.product_dim_id, itf.facility_dim_id, itf.quantity_uom_dim_id, prd.require_amount");
		
		OlbiusQuery queryTmp = new OlbiusQuery();
		queryTmp.select("endingQuantity")
		.select("uom_dimension.uom_id", "quantity_uom_id")
		.select("facility_dimension.facility_id", "facility_id")
		.select("facility_dimension.facility_name", "facility_name")
		.select("product_dimension.product_id", "product_id")
		.select("product_dimension.product_code", "product_code")
		.select("product_dimension.product_name", "product_name");
		queryTmp.from(queryEnding, "IMEP");
		queryTmp.join(Join.INNER_JOIN, "product_dimension","IMEP.product_dim_id = product_dimension.dimension_id");
		queryTmp.join(Join.INNER_JOIN, "facility_dimension","IMEP.facility_dim_id = facility_dimension.dimension_id");
		queryTmp.join(Join.INNER_JOIN, "uom_dimension", "IMEP.quantity_uom_dim_id = uom_dimension.dimension_id");
		
		OlbiusQuery queryAmountEnding = new OlbiusQuery();
		queryAmountEnding.select("facility_id").select("product_id").select("(sum(dr_amount) - sum(cr_amount))", "endingAmount")
			.from("inventory_product_cost_fact");
		Condition condAmountEnding = Condition.make("date_value",Condition.LESS_EQ, getThruDate());
		if (UtilValidate.isNotEmpty(facilityDimIds)) {
			condAmountEnding.and(Condition.makeIn("facility_id", facilityIds));
		}
		if (UtilValidate.isNotEmpty(productDimIds)) {
			condAmountEnding.and(Condition.makeIn("product_id", productIds));
		}
		queryAmountEnding.where(condAmountEnding);
		queryAmountEnding.groupBy("facility_id").groupBy("product_id");
		
		query.select("tmp.facility_id").select("facility_name").select("tmp.product_id").select("product_code")
			.select("product_name").select("endingQuantity").select("quantity_uom_id")
			.select("CASE WHEN v1.endingAmount IS NULL THEN 0 ELSE v1.endingAmount END AS endingAmount");
		query.from(queryTmp, "tmp");
		query.join(Join.LEFT_OUTER_JOIN, queryAmountEnding, "v1", "v1.product_id = tmp.product_id AND v1.facility_id = tmp.facility_id");
		
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
		addDataField("endingQuantity", "endingQuantity");
		addDataField("endingAmount", "endingAmount");
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
				if (UtilValidate.isNotEmpty(result.getBigDecimal("endingQuantity"))) {
					map.put("endingQuantity", result.getBigDecimal("endingQuantity"));
				} else {
					map.put("endingQuantity", BigDecimal.ZERO);
				}
				if (UtilValidate.isNotEmpty(result.getBigDecimal("endingAmount"))) {
					map.put("endingAmount", result.getBigDecimal("endingAmount"));
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