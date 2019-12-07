package com.olbius.basesales.reports;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import org.ofbiz.service.DispatchContext;

import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.grid.ReturnResultGridEx;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.function.Sum;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class TemplateReport extends OlbiusOlapService {

	private OlbiusQuery query;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
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

		OlbiusQuery tmp = makeQuery();

		String dateType = getDateType((String) getParameter("dateType"));

		query.select("iif.quantity_on_hand_total").select("iif.inventory_type").select("fd.facility_name")
				.select("pd.product_code").select("pd.product_name").select("dd1.date_value", "inventory_date")
				.select("dd1.".concat(dateType)).select("dd2.date_value", "expire_date")
				.select("dd3.date_value", "manufactured_date").select("cd.description", "uom_id")
				.select("pcr.category_dim_id", "category_dim_id").from("inventory_item_fact", "iif")
				.join(Join.INNER_JOIN, "facility_dimension", "fd", "fd.dimension_id = iif.facility_dim_id")
				.join(Join.INNER_JOIN, "product_dimension", "pd", "pd.dimension_id = iif.product_dim_id")
				.join(Join.INNER_JOIN, "date_dimension", "dd1", "dd1.dimension_id = iif.inventory_date_dim_id")
				.join(Join.INNER_JOIN, "date_dimension", "dd2", "dd2.dimension_id = iif.expire_date_dim_id")
				.join(Join.INNER_JOIN, "date_dimension", "dd3", "dd3.dimension_id = iif.manufactured_date_dim_id")
				.join(Join.INNER_JOIN, "currency_dimension", "cd", "cd.dimension_id = iif.uom_dim_id")
				.join(Join.LEFT_OUTER_JOIN, "product_category_relationship", "pcr",
						"pcr.product_dim_id = iif.product_dim_id")
				.where(Condition.makeBetween("inventory_date_dim_id", getSqlTime(getFromDate()),
						getSqlTime(getThruDate())));

		tmp.select(new Sum("inventory_type ='EXPORT'", "-quantity_on_hand_total", "0"), "quantity_on_hand_total_export")
				.select(new Sum("inventory_type ='RECEIVE'", "quantity_on_hand_total", "0"),
						"quantity_on_hand_total_receive")
				.select("tmp.facility_name").select("tmp.product_code").select("tmp.product_name")
				.select("tmp.inventory_date").select("tmp.".concat(dateType)).select("tmp.expire_date")
				.select("tmp.manufactured_date").select("tmp.uom_id").select("catd.category_name", "category_name")
				.from(query, "tmp")
				.join(Join.INNER_JOIN, "category_dimension", "catd", "catd.dimension_id = tmp.category_dim_id")
				.where(Condition.makeEQ("catd.category_type", "CATALOG_CATEGORY")).orderBy("tmp.".concat(dateType))
				.groupBy("tmp.facility_name").groupBy("tmp.product_code").groupBy("tmp.product_name")
				.groupBy("tmp.inventory_date").groupBy("tmp.".concat(dateType)).groupBy("tmp.expire_date")
				.groupBy("tmp.manufactured_date").groupBy("tmp.uom_id").groupBy("catd.category_name");
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
		addDataField("uom_id", "uom_id");
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
				map.put("uom_id", result.getString("uom_id"));
				map.put("category_name", result.getString("category_name"));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return map;
		}
	}
}
