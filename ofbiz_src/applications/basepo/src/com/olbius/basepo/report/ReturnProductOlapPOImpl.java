package com.olbius.basepo.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.olap.bi.accounting.AccountingOlap;

public class ReturnProductOlapPOImpl extends AbstractOlap implements AccountingOlap {

	public static final String DATE_TYPE = "DATE_TYPE";
	public static final String PRODUCT_ID = "PRODUCT_ID";
	public static final String FACILITY_ID = "FACILITY_ID";
	public static final String CATEGORY_ID = "CATEGORY_ID";
	public static final String RETURN_REASON_ID = "RETURN_REASON_ID";
	public static final String USER_ID = "USER_ID";
	public static final String LOCALE = "LOCALE";
	private OlbiusQuery query;
	private String dateType;

	@SuppressWarnings("unchecked")
	private void initQuery() {
		dateType = (String) getParameter(DATE_TYPE);
		List<Object> productId = (List<Object>) getParameter(PRODUCT_ID);
		List<Object> facilityId = (List<Object>) getParameter(FACILITY_ID);
		String userLoginId = (String) getParameter(USER_ID);
		List<Object> categoryId = (List<Object>) getParameter(CATEGORY_ID);
		List<Object> returnReasonId = (List<Object>) getParameter(RETURN_REASON_ID);
		dateType = getDateType(dateType);

		query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		query = new OlbiusQuery(getSQLProcessor());
		query.from("return_item_fact").select("date_dimension.".concat(dateType))
				.select("product_dimension.product_code").select("product_dimension.internal_name")
				.select("return_item_fact.return_id").select("return_item_fact.return_quantity")
				.select("return_item_fact.return_price").select("return_item_fact.order_id")
				.select("return_reason_dimension.return_reason_id").select("party_organization.name", "partyToId")
				.select("party_from_dimension.name", "partyFromId").select("return_item_fact.return_item_type_id")
				.select("return_item_fact.quantity_uom_id").select("facility_dimension.facility_name")
				.select("return_item_fact.status_header_id").select("category_dimension.category_name")
				.join(Join.INNER_JOIN, "date_dimension", null, "status_date_dim_id = date_dimension.dimension_id")
				.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id")
				.join(Join.INNER_JOIN, "return_reason_dimension", null,
						"return_reason_dim_id = return_reason_dimension.dimension_id")
				.join(Join.INNER_JOIN, "facility_dimension", null, "facility_dim_id = facility_dimension.dimension_id")
				.join(Join.INNER_JOIN, "product_category_relationship", null,
						"product_category_relationship.product_dim_id = product_dimension.dimension_id")
				.join(Join.INNER_JOIN, "category_dimension", null,
						"product_category_relationship.category_dim_id = category_dimension.dimension_id AND category_dimension.category_type = 'CATALOG_CATEGORY'")
				.join(Join.INNER_JOIN, "party_dimension", "party_organization",
						"return_item_fact.to_party_dim_id = party_organization.dimension_id")
				.join(Join.INNER_JOIN, "party_dimension", "party_from_dimension",
						"return_item_fact.from_party_dim_id = party_from_dimension.dimension_id");
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
		if (userLoginId != null) {
			condition.and(Condition.makeEQ("party_from_dimension.party_id", userLoginId));
		}
		condition.and(Condition.makeEQ("return_item_fact.return_header_type_id", "VENDOR_RETURN"));
		query.where(condition);
		query.orderBy("date_dimension.".concat(dateType), OlbiusQuery.DESC);
	}

	@Override
	protected OlbiusQuery getQuery() {
		if (query == null) {
			initQuery();
		}
		return query;
	}

	public class ResultOutReport extends ReturnResultGrid {
		public ResultOutReport() {
			addDataField("date");
			addDataField("returnId");
			addDataField("productId");
			addDataField("productName");
			addDataField("returnQuantity");
			addDataField("quantityUomId");
			addDataField("returnPrice");
			addDataField("orderId");
			addDataField("returnReasonId");
			addDataField("partyFromId");
			addDataField("partyToId");
			addDataField("returnItemTypeId");
			addDataField("facilityName");
			addDataField("statusHeaderId");
			addDataField("categoryName");
		}

		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = new HashMap<String, Object>();
			GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
			Locale locale = (Locale) getParameter(LOCALE);
			try {
				String statusHeaderId = result.getString("status_header_id");
				GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusHeaderId),
						false);
				if (UtilValidate.isNotEmpty(statusItem)) {
					statusHeaderId = (String) statusItem.get("description", locale);
				}

				BigDecimal returnPrice = result.getBigDecimal("return_price");
				BigDecimal returnQuantity = result.getBigDecimal("return_quantity");
				int totalPrice = 0;
				if (UtilValidate.isNotEmpty(returnPrice)) {
					totalPrice = returnPrice.intValue() * returnQuantity.intValue();
					if (totalPrice < 0) {
						totalPrice = totalPrice * (-1);
					}
				}

				map.put("date", result.getString(dateType));
				map.put("productName", result.getString("internal_name"));
				map.put("orderId", result.getString("order_id"));
				map.put("returnId", result.getString("return_id"));
				map.put("productId", result.getString("product_code"));
				map.put("returnQuantity", result.getBigDecimal("return_quantity"));
				map.put("quantityUomId", result.getString("quantity_uom_id"));
				map.put("returnPrice", totalPrice);
				map.put("returnReasonId", result.getString("return_reason_id"));
				map.put("partyFromId", result.getString("partyFromId"));
				map.put("partyToId", result.getString("partyToId"));
				map.put("returnItemTypeId", result.getString("return_item_type_id"));
				map.put("facilityName", result.getString("facility_name"));
				map.put("statusHeaderId", statusHeaderId);
				map.put("categoryName", result.getString("category_name"));
			} catch (Exception e) {
				Debug.logError(e.getMessage(), ResultOutReport.class.getName());
			}
			return map;
		}

	}

}