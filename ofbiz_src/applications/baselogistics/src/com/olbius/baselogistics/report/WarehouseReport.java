package com.olbius.baselogistics.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.DispatchContext;

import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.cache.dimension.CategoryDimension;
import com.olbius.bi.olap.cache.dimension.FacilityDimension;
import com.olbius.bi.olap.cache.dimension.ProductDimension;
import com.olbius.bi.olap.grid.ReturnResultGridEx;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.function.Sum;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class WarehouseReport extends OlbiusOlapService {

	private OlbiusQuery query;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));

		putParameter("dateType", context.get("dateType"));

		putParameter("facility", context.get("facility[]"));

		putParameter("product", context.get("product[]"));

		putParameter("categories", context.get("categories[]"));

		putParameter("inventoryType", context.get("inventoryType"));

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

	private List<Object> getCategories(List<?> list) {
		List<Object> tmp = new ArrayList<Object>();
		if (list != null) {
			for (Object x : list) {
				Long y = CategoryDimension.D.getId(delegator, (String) x);
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

	private OlbiusQuery init() {

		OlbiusQuery query = makeQuery();

		OlbiusQuery tmp = makeQuery();

		List<Object> facilities = getFacilities((List<?>) getParameter("facility"));
		List<Object> products = getProducts((List<?>) getParameter("product"));
		List<Object> categories = getCategories((List<?>) getParameter("categories"));
		String dateType = getDateType((String) getParameter("dateType"));
		String inventoryType = (String) getParameter("inventoryType");

		query.select("iif.quantity_on_hand_total").select("iif.amount_on_hand_total").select("iif.inventory_type")
				.select("fd.facility_name").select("pd.product_code").select("pd.product_name")
				.select("pd.require_amount").select("pd.weight_uom_dim_id").select("dd1.date_value", "inventory_date")
				.select("dd1.".concat(dateType)).select("dd2.date_value", "expire_date")
				.select("dd3.date_value", "manufactured_date").select("cd.description", "quantity_uom_id")
				.select("iif.category_dim_id").from("inventory_item_fact", "iif")
				.join(Join.INNER_JOIN, "facility_dimension", "fd", "fd.dimension_id = iif.facility_dim_id")
				.join(Join.INNER_JOIN, "product_dimension", "pd", "pd.dimension_id = iif.product_dim_id")
				.join(Join.INNER_JOIN, "date_dimension", "dd1", "dd1.dimension_id = iif.inventory_date_dim_id")
				.join(Join.INNER_JOIN, "date_dimension", "dd2", "dd2.dimension_id = iif.expire_date_dim_id")
				.join(Join.INNER_JOIN, "date_dimension", "dd3", "dd3.dimension_id = iif.manufactured_date_dim_id")
				.join(Join.INNER_JOIN, "uom_dimension", "cd", "cd.dimension_id = iif.quantity_uom_dim_id").where(
						Condition
								.makeBetween("inventory_date_dim_id", getSqlTime(getFromDate()),
										getSqlTime(getThruDate()))
								.andIn("facility_dim_id", facilities).andIn("iif.product_dim_id", products)
								.andEQ("iif.inventory_type", inventoryType).and("iif.quantity_on_hand_total <> 0")
								.and("physical_inventory_id is null"));

		tmp.select(new Sum("tmp.inventory_type ='EXPORT'",
				"-(CASE WHEN tmp.require_amount = 'Y' THEN tmp.amount_on_hand_total ELSE tmp.quantity_on_hand_total END)",
				"0"), "quantity_on_hand_total_export")
				.select(new Sum("tmp.inventory_type ='RECEIVE'",
						"(CASE WHEN tmp.require_amount = 'Y' THEN tmp.amount_on_hand_total ELSE tmp.quantity_on_hand_total END)",
						"0"), "quantity_on_hand_total_receive")
				.select("tmp.facility_name").select("tmp.product_code").select("tmp.product_name")
				.select("tmp.inventory_date").select("tmp.".concat(dateType)).select("tmp.expire_date")
				.select("tmp.manufactured_date").select("catd.category_name", "category_name")
				.select("CASE WHEN tmp.require_amount = 'Y' THEN cdw.description ELSE tmp.quantity_uom_id END AS quantity_uom_id")
				.from(query, "tmp")
				.join(Join.INNER_JOIN, "category_dimension", "catd", "catd.dimension_id = tmp.category_dim_id")
				.join(Join.INNER_JOIN, "currency_dimension", "cdw", "cdw.dimension_id = tmp.weight_uom_dim_id")
				.where(Condition.makeEQ("catd.category_type", "CATALOG_CATEGORY").andIn("tmp.category_dim_id",
						categories, UtilValidate.isNotEmpty(categories)))
				.orderBy("tmp.".concat(dateType)).groupBy("tmp.facility_name").groupBy("tmp.product_code")
				.groupBy("tmp.product_name").groupBy("tmp.inventory_date").groupBy("tmp.".concat(dateType))
				.groupBy("tmp.expire_date").groupBy("tmp.manufactured_date").groupBy("tmp.quantity_uom_id")
				.groupBy("catd.category_name").groupBy("cdw.description").groupBy("tmp.require_amount");
		return tmp;
	}

	@Override
	public void prepareResultGrid() {
		String inventoryType = (String) getParameter("inventoryType");
		String dateType = getDateType((String) getParameter("dateType"));
		addDataField("dateTime", dateType);
		if ("EXPORT".equals(inventoryType))
			addDataField("quantity_on_hand_total", "quantity_on_hand_total_export");
		if ("RECEIVE".equals(inventoryType))
			addDataField("quantity_on_hand_total", "quantity_on_hand_total_receive");
		addDataField("facility_name", "facility_name");
		addDataField("product_code", "product_code");
		addDataField("product_name", "product_name");
		addDataField("inventory_date", "inventory_date");
		addDataField("expire_date", "expire_date");
		addDataField("manufactured_date", "manufactured_date");
		addDataField("quantity_uom_id", "quantity_uom_id");
		addDataField("category_name", "category_name");
	}

	@Override
	protected OlapResultQueryInterface returnResultGrid() {
		return new ReturnResultGridFacility();
	}

	private class ReturnResultGridFacility extends ReturnResultGridEx {

		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = super.getObject(result);
			try {
				String inventoryType = (String) getParameter("inventoryType");
				if ("EXPORT".equals(inventoryType))
					map.put("quantity_on_hand_total", result.getBigDecimal("quantity_on_hand_total_export"));
				if ("RECEIVE".equals(inventoryType))
					map.put("quantity_on_hand_total", result.getBigDecimal("quantity_on_hand_total_receive"));
				map.put("facility_name", result.getString("facility_name"));
				map.put("product_code", result.getString("product_code"));
				map.put("product_name", result.getString("product_name"));
				map.put("inventory_date", result.getDate("inventory_date"));
				map.put("expire_date", result.getDate("expire_date"));
				map.put("manufactured_date", result.getDate("manufactured_date"));
				map.put("quantity_uom_id", result.getString("quantity_uom_id"));
				map.put("category_name", result.getString("category_name"));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return map;
		}
	}
}
