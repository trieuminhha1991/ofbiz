package com.olbius.baselogistics.report;

import java.util.List;

import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class ReceiveWarehouseOlapImp extends OlbiusBuilder {

	public ReceiveWarehouseOlapImp(Delegator delegator) {
		super(delegator);
	}

	public static final String PRODUCT_ID = "PRODUCT_ID";
	public static final String FACILITY_ID = "FACILITY_ID";
	public static final String CATEGORY_ID = "CATEGORY_ID";
	public static final String USER_LOGIN_ID = "USER_LOGIN_ID";
	private OlbiusQuery query;
	private String dateType = "date_value";

	@SuppressWarnings("unchecked")
	private void initQuery() {
		String userLoginId = (String) getParameter(USER_LOGIN_ID);
		List<Object> productId = (List<Object>) getParameter(PRODUCT_ID);
		List<Object> facilityId = (List<Object>) getParameter(FACILITY_ID);
		List<Object> categoryId = (List<Object>) getParameter(CATEGORY_ID);

		query = new OlbiusQuery(getSQLProcessor());

		OlbiusQuery tmpQuery = OlbiusQuery.make();
		Condition conditionTmp = new Condition();

		tmpQuery.from("inventory_item_fact").select("date_dimension.dimension_id")
				.select("product_dimension.product_code").select("product_dimension.internal_name")
				.select("inventory_item_fact.manufactured_date_dim_id").select("inventory_item_fact.expire_date_dim_id")
				.select("inventory_item_fact.lot_id")
				.select("sum(inventory_item_fact.quantity_on_hand_total)")
				.select("currency_dimension.currency_id", "uom_id").select("facility_dimension.facility_name")
				.select("category_dimension.category_name")
				.join(Join.INNER_JOIN, "date_dimension", null, "inventory_date_dim_id = date_dimension.dimension_id")
				.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id")
				.join(Join.LEFT_OUTER_JOIN, "product_category_relationship", null,
						"product_category_relationship.product_dim_id = product_dimension.dimension_id")
				.join(Join.LEFT_OUTER_JOIN, "category_dimension", null,
						"product_category_relationship.category_dim_id = category_dimension.dimension_id AND category_dimension.category_type = 'CATALOG_CATEGORY'")
				.join(Join.INNER_JOIN, "currency_dimension", null,
						"inventory_item_fact.uom_dim_id = currency_dimension.dimension_id")
				.join(Join.INNER_JOIN, "facility_dimension", null, "facility_dim_id = facility_dimension.dimension_id")
				.join(Join.INNER_JOIN, "party_dimension", "facility_party_id",
						"facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_id = "
								+ "'" + userLoginId + "'")
				.join(Join.INNER_JOIN, "party_dimension", "party_organization",
						"inventory_item_fact.owner_party_dim_id = party_organization.dimension_id AND party_organization.party_id = "
								+ "'" + userLoginId + "'");
		conditionTmp
				.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		if (categoryId != null) {
			conditionTmp.and(Condition.makeIn("category_dimension.category_id", categoryId, categoryId != null));
		}
		if (facilityId != null) {
			conditionTmp.and(Condition.makeIn("facility_dimension.facility_id", facilityId, facilityId != null));
		}
		if (productId != null) {
			conditionTmp.and(Condition.makeIn("product_dimension.product_code", productId, productId != null));
		}
		conditionTmp.and(Condition.makeEQ("inventory_item_fact.inventory_type", "RECEIVE"));
		tmpQuery.where(conditionTmp).groupBy("date_dimension.dimension_id").groupBy("product_dimension.product_code")
				.groupBy("product_dimension.internal_name").groupBy("inventory_item_fact.manufactured_date_dim_id")
				.groupBy("inventory_item_fact.expire_date_dim_id").groupBy("inventory_item_fact.lot_id").groupBy("uom_id")
				.groupBy("facility_dimension.facility_name").groupBy("category_dimension.category_name")
				.orderBy("date_dimension.".concat(dateType), OlbiusQuery.DESC);

		query.select("tmp.product_code").select("date_dimension.".concat(dateType)).select("tmp.internal_name")
				.select("tmp.manufactured_date_dim_id").select("tmp.expire_date_dim_id").select("tmp.lot_id").select("tmp.uom_id")
				.select("tmp.facility_name").select("tmp.quantityOnHandTotal")
				.select("array_to_string(array_agg(tmp.category_name), ',')", "category_name").from(tmpQuery, "tmp")
				.join(Join.INNER_JOIN, "date_dimension", "tmp.dimension_id = date_dimension.dimension_id")
				.groupBy("date_dimension.".concat(dateType)).groupBy("tmp.product_code").groupBy("tmp.internal_name")
				.groupBy("tmp.manufactured_date_dim_id").groupBy("tmp.expire_date_dim_id").groupBy("tmp.lot_id")
				.groupBy("tmp.uom_id").groupBy("tmp.facility_name").groupBy("tmp.quantityOnHandTotal")
				.orderBy("date_dimension.".concat(dateType), OlbiusQuery.DESC);
	}

	@Override
	protected OlbiusQuery getQuery() {
		if (query == null) {
			initQuery();
		}
		return query;
	}

	@Override
	public void prepareResultGrid() {
		addDataField("date", "date_value");
		addDataField("productId", "product_code");
		addDataField("productName", "internal_name");
		addDataField("datetimeManufactured", "manufactured_date_dim_id");
		addDataField("expireDate", "expire_date_dim_id");
		addDataField("lotId", "lot_id");
		addDataField("quantityOnHandTotal", "quantityOnHandTotal");
		addDataField("uomId", "uom_id");
		addDataField("facilityName", "facility_name");
		addDataField("categoryName", "category_name");
	}

}