package com.olbius.basepo.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.olap.AbstractOlap;
import com.olbius.olap.OlapInterface;

public class POOlapChart extends AbstractOlap implements OlapInterface {
	public void purchaseOrderChart(List<Object> productId, List<Object> statusId, String limitId, String filterTypeId,
			List<Object> categoryId, String ownerPartyId)
			throws GenericDataSourceException, GenericEntityException, SQLException {
		OlbiusQuery query2 = new OlbiusQuery(getSQLProcessor());
		query2.select("pof.party_to_dim_id", "pof.order_date_dim_id", "pof.product_dim_id", "pof.status_dim_id")
				.select("CASE WHEN prd.require_amount = 'Y' THEN (pof.quantity * pof.selected_amount) ELSE pof.quantity END AS quantity")
				.from("purchase_order_fact", "pof")
				.join(Join.INNER_JOIN, "product_dimension", "prd", "prd.dimension_id = pof.product_dim_id")
				.groupBy("quantity", "pof.party_to_dim_id", "pof.order_date_dim_id", "pof.product_dim_id",
						"pof.status_dim_id", "prd.require_amount", "pof.selected_amount");

		OlbiusQuery query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		query.from(query2, "purchase_order_fact").select("product_dimension.product_code")
				.select("category_dimension.category_id", categoryId != null)
				.select("sum(purchase_order_fact.quantity)", "quantity");
		query.join(Join.INNER_JOIN, "date_dimension", null, "order_date_dim_id = date_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "product_category_relationship", null,
				"product_category_relationship.product_dim_id = product_dimension.dimension_id", categoryId != null);
		query.join(Join.INNER_JOIN, "category_dimension", null,
				"product_category_relationship.category_dim_id = category_dimension.dimension_id AND category_dimension.category_type = 'CATALOG_CATEGORY'",
				categoryId != null);
		query.join(Join.INNER_JOIN, "party_dimension", "party_organization",
				"purchase_order_fact.party_to_dim_id = party_organization.dimension_id AND party_organization.party_id = "
						+ "'" + ownerPartyId + "'");
		condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		/*
		 * condition.and(Condition.make(
		 * "status_dimension.status_id != 'ITEM_CANCELLED'"));
		 */
		if (productId != null) {
			condition.and(Condition.makeIn("product_dimension.product_code", productId, productId != null));
		}
		if (categoryId != null) {
			condition.and(Condition.makeIn("category_dimension.category_id", categoryId, categoryId != null));
		}
		query.where(condition);
		query.groupBy("product_dimension.product_code").groupBy("category_dimension.category_id", categoryId != null);
		if (filterTypeId.equals("FILTER_MIN")) {
			query.orderBy("quantity", OlbiusQuery.ASC, true);
		}
		if (filterTypeId.equals("FILTER_MAX")) {
			query.orderBy("quantity", OlbiusQuery.DESC, true);
		}
		if (limitId != null) {
			int limitIdInt = Integer.parseInt(limitId);
			query.limit(limitIdInt);
		}

		ResultSet resultSet = query.getResultSet();

		while (resultSet.next()) {
			String _key = resultSet.getString("product_code");
			if (yAxis.get("test") == null) {
				yAxis.put("test", new ArrayList<Object>());
			}
			xAxis.add(_key);
			BigDecimal quantity = resultSet.getBigDecimal("quantity");
			yAxis.get("test").add(quantity);
		}

	}

	public void purchaseOrderChartLine(String dateType, List<Object> productId, List<Object> statusId,
			String ownerPartyId, List<Object> categoryId, Integer filterTop, String filterSort)
			throws GenericDataSourceException, GenericEntityException, SQLException {

		dateType = getDateType(dateType);

		OlbiusQuery query2 = new OlbiusQuery(getSQLProcessor());
		query2.select("pof.party_to_dim_id", "pof.order_date_dim_id", "pof.product_dim_id", "pof.status_dim_id")
				.select("CASE WHEN prd.require_amount = 'Y' THEN (pof.quantity * pof.selected_amount) ELSE pof.quantity END AS quantity")
				.from("purchase_order_fact", "pof")
				.join(Join.INNER_JOIN, "product_dimension", "prd", "prd.dimension_id = pof.product_dim_id")
				.groupBy("quantity", "pof.party_to_dim_id", "pof.order_date_dim_id", "pof.product_dim_id",
						"pof.status_dim_id", "prd.require_amount", "pof.selected_amount");

		if ("DESC".equals(filterSort)) {
			query2.orderBy("quantity DESC").limit(filterTop);
		} else if ("ASC".equals(filterSort)) {
			query2.orderBy("quantity ASC").limit(filterTop);
		}

		OlbiusQuery query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		query.from(query2, "purchase_order_fact").select("date_dimension.".concat(dateType))
				.select("product_dimension.product_code").select("status_dimension.status_id")
				.select("category_dimension.category_id", categoryId != null)
				.select("sum(purchase_order_fact.quantity)", "quantity");
		query.join(Join.INNER_JOIN, "date_dimension", null, "order_date_dim_id = date_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "status_dimension", null, "status_dim_id = status_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "party_dimension", null,
				"purchase_order_fact.party_to_dim_id = party_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "product_category_relationship", null,
				"product_category_relationship.product_dim_id = product_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "category_dimension", null,
				"product_category_relationship.category_dim_id = category_dimension.dimension_id AND category_dimension.category_type = 'CATALOG_CATEGORY'");
		condition.and(Condition.makeEQ("party_dimension.party_id", ownerPartyId));
		condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		if (statusId != null) {
			condition.and(Condition.makeIn("status_dimension.status_id", statusId, statusId != null));
		}
		if (productId != null) {
			condition.and(Condition.makeIn("product_dimension.product_code", productId, productId != null));
		}
		if (categoryId != null) {
			condition.and(Condition.makeIn("category_dimension.category_id", categoryId, categoryId != null));
		}
		query.where(condition);
		query.groupBy("date_dimension.".concat(dateType), dateType != null).groupBy("product_dimension.product_code")
				.groupBy("category_dimension.category_id", categoryId != null).groupBy("status_dimension.status_id")
				.orderBy("date_dimension.".concat(dateType), dateType != null);

		ResultSet resultSet = query.getResultSet();

		Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();

		while (resultSet.next()) {
			String productIdOut = resultSet.getString("product_code");
			if (map.get(productIdOut) == null) {
				map.put(productIdOut, new HashMap<String, Object>());
			}
			map.get(productIdOut).put(resultSet.getString(dateType), resultSet.getInt("quantity"));
		}

		axis(map, dateType);
	}

	public void returnProductChartPO(List<Object> productId, List<Object> facilityId, String ownerPartyId,
			List<Object> categoryId, List<Object> returnReasonId, String limitId, String filterTypeId)
			throws GenericDataSourceException, GenericEntityException, SQLException {
		OlbiusQuery query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		query.from("return_item_fact").select("product_dimension.product_code")
				.select("return_reason_dimension.return_reason_id")
				.select("category_dimension.category_id", categoryId != null).select("facility_dimension.facility_id")
				.select("sum(return_item_fact.return_quantity)", "quantity");
		query.join(Join.INNER_JOIN, "date_dimension", null, "status_date_dim_id = date_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "facility_dimension", null, "facility_dim_id = facility_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "return_reason_dimension", null,
				"return_reason_dim_id = return_reason_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "product_category_relationship", null,
				"product_category_relationship.product_dim_id = product_dimension.dimension_id", categoryId != null);
		query.join(Join.INNER_JOIN, "category_dimension", null,
				"product_category_relationship.category_dim_id = category_dimension.dimension_id AND category_dimension.category_type = 'CATALOG_CATEGORY'",
				categoryId != null);
		query.join(Join.INNER_JOIN, "party_dimension", "facility_party_id",
				"facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_id = "
						+ "'" + ownerPartyId + "'");
		condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		if (returnReasonId != null) {
			condition.and(Condition.makeIn("return_reason_dimension.return_reason_id", returnReasonId,
					returnReasonId != null));
		}
		if (facilityId != null) {
			condition.and(Condition.makeIn("facility_dimension.facility_id", facilityId, facilityId != null));
		}
		if (productId != null) {
			condition.and(Condition.makeIn("product_dimension.product_code", productId, productId != null));
		}
		if (categoryId != null) {
			condition.and(Condition.makeIn("category_dimension.category_id", categoryId, categoryId != null));
		}
		condition.and(Condition.makeEQ("return_item_fact.return_header_type_id", "VENDOR_RETURN"));
		query.where(condition);
		query.groupBy("product_dimension.product_code").groupBy("facility_dimension.facility_id")
				.groupBy("category_dimension.category_id", categoryId != null)
				.groupBy("return_reason_dimension.return_reason_id");
		if (filterTypeId.equals("FILTER_MIN")) {
			query.orderBy("quantity", OlbiusQuery.ASC, true);
		}
		if (filterTypeId.equals("FILTER_MAX")) {
			query.orderBy("quantity", OlbiusQuery.DESC, true);
		}
		if (limitId != null) {
			int limitIdInt = Integer.parseInt(limitId);
			query.limit(limitIdInt);
		}
		query.orderBy("facility_dimension.facility_id");
		ResultSet resultSet = query.getResultSet();

		while (resultSet.next()) {
			String _key = resultSet.getString("product_code");
			if (yAxis.get("test") == null) {
				yAxis.put("test", new ArrayList<Object>());
			}
			xAxis.add(_key);
			BigDecimal quantity = resultSet.getBigDecimal("quantity");
			yAxis.get("test").add(quantity);
		}

	}
}
