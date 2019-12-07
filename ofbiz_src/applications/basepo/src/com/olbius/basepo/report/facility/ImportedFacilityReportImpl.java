package com.olbius.basepo.report.facility;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.olbius.basepo.report.PurchaseOrderReportImp;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class ImportedFacilityReportImpl extends PurchaseOrderReportImp {
	@SuppressWarnings("unchecked")
	private void initQuery() {
		dateType = (String) getParameter(DATE_TYPE);
		List<Object> productId = (List<Object>) getParameter(PRODUCT_ID);
		List<Object> categoryId = (List<Object>) getParameter(CATEGORY_ID);
		String partyFacilityId = (String) getParameter(USER_LOGIN_ID);
		dateType = getDateType(dateType);

		query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		query.from("purchase_order_fact").select("purchase_order_fact.order_id")
				.select("purchase_order_fact.order_item_seq_id").select("product_dimension.product_code")
				.select("product_dimension.internal_name").select("purchase_order_fact.facility_id")
				.select("purchase_order_fact.quantity_uom_id").select("purchase_order_fact.party_id_customer")
				.select("purchase_order_fact.quantity", "quantity")
				.select("purchase_order_fact.actual_arrival_date", "actual_arrival_date")
				.select("purchase_order_fact.actual_imported_quantity", "actual_imported_quantity")
				.select("category_dimension.category_name").select("purchase_order_fact.ship_by_date")
				.select("purchase_order_fact.contact_mech_id");
		query.join(Join.INNER_JOIN, "date_dimension", null, "order_date_dim_id = date_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "status_dimension", null, "status_dim_id = status_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "product_category_relationship", null,
				"product_category_relationship.product_dim_id = product_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "category_dimension", null,
				"product_category_relationship.category_dim_id = category_dimension.dimension_id AND category_dimension.category_type = 'CATALOG_CATEGORY'");
		query.join(Join.INNER_JOIN, "party_dimension", null,
				"purchase_order_fact.party_to_dim_id = party_dimension.dimension_id");
		condition.and(Condition.makeEQ("party_dimension.party_id", partyFacilityId));
		condition.and(Condition.make("actual_arrival_date is not null"));
		condition.and(Condition.makeBetween("actual_arrival_date", getSqlFromDate(fromDate), getSqlThruDate(thruDate)));
		if (productId != null) {
			condition.and(Condition.makeIn("product_dimension.product_code", productId, productId != null));
		}
		if (categoryId != null) {
			condition.and(Condition.makeIn("category_dimension.category_id", categoryId, categoryId != null));
		}
		query.where(condition);
		query.orderBy("product_code", OlbiusQuery.DESC);
	}

	@Override
	protected OlbiusQuery getQuery() {
		if (query == null) {
			initQuery();
		}
		return query;
	}

	public class ResultOutPOReport extends ReturnResultGrid {
		public ResultOutPOReport() {
			addDataField("productId");
			addDataField("productName");
			addDataField("quantityUomId");
			addDataField("categoryId");
			addDataField("quantity");
			addDataField("facilityId");
			addDataField("partyId");
			addDataField("partyCustomerSO");
			addDataField("contactMechId");
			addDataField("shipByDate");
			addDataField("actualArrivalDate");
			addDataField("actualImportedQuantity");
			addDataField("reasonsDifference");
			addDataField("ratio");
		}

		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = new HashMap<String, Object>();
			GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
			try {
				String facilityId = result.getString("facility_id");
				String orderId = result.getString("order_id");
				String orderItemSeqId = result.getString("order_item_seq_id");
				String address1 = "";
				String partyCustomerSO = "";

				Timestamp shipAfterDate = null;
				List<GenericValue> listOrderItemShipGroup = delegator.findList("OrderItemShipGroup",
						EntityCondition.makeCondition(UtilMisc.toMap("orderId", result.getString("order_id"))), null,
						null, null, false);
				if (!listOrderItemShipGroup.isEmpty()) {
					for (GenericValue orderItemShipGroup : listOrderItemShipGroup) {
						if (orderItemShipGroup != null) {
							shipAfterDate = orderItemShipGroup.getTimestamp("shipAfterDate");
						}
					}
				}

				GenericValue facilitySO = delegator.findOne("Facility", UtilMisc.toMap("facilityId", partyCustomerSO),
						false);
				if (facilitySO != null) {
					partyCustomerSO = facilitySO.getString("facilityName");
				}

				GenericValue postAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", address1),
						false);
				if (postAddress != null) {
					if (postAddress.getString("toName") != null) {
						if (postAddress.getString("attnName") != null) {
							address1 = postAddress.getString("toName") + "( " + postAddress.getString("attnName") + " )"
									+ " " + postAddress.getString("address1");
						} else {
							address1 = postAddress.getString("toName") + "," + postAddress.getString("address1");
						}
					} else {
						address1 = " " + postAddress.getString("address1");
					}
				}

				/* String reasonEnumId = ""; */
				String changeComments = "";
				/* BigDecimal quantityDiffen = null; */
				List<GenericValue> listOrderItemChange = delegator.findList("OrderItemChange",
						EntityCondition
								.makeCondition(UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId)),
						null, null, null, false);
				if (!listOrderItemChange.isEmpty()) {
					for (GenericValue orderItemChange : listOrderItemChange) {
						/*
						 * reasonEnumId =
						 * orderItemChange.getString("reasonEnumId");
						 */
						/*
						 * quantityDiffen =
						 * orderItemChange.getBigDecimal("quantity");
						 */
						changeComments = orderItemChange.getString("changeComments");
					}
				}

				String partyIdCutomer = result.getString("party_id_customer");

				String shipByDate = "";
				if (shipAfterDate != null) {
					shipByDate = formatDate(shipAfterDate, true) + " / "
							+ formatDate(result.getTimestamp("ship_by_date"), true);
				}

				GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
				if (UtilValidate.isNotEmpty(facility)) {
					facilityId = (String) facility.get("facilityName");
				}

				BigDecimal quantity = result.getBigDecimal("quantity");
				BigDecimal actualImportedQuantity = result.getBigDecimal("actual_imported_quantity");
				BigDecimal ratio = quantity.subtract(actualImportedQuantity);

				GenericValue person = delegator.findOne("PartyFullNameDetail",
						UtilMisc.toMap("partyId", partyIdCutomer), false);
				map.put("productId", result.getString("product_code"));
				map.put("productName", result.getString("internal_name"));
				map.put("quantityUomId", result.getString("quantity_uom_id"));
				map.put("quantity", quantity);
				map.put("actualImportedQuantity", actualImportedQuantity);
				map.put("ratio", ratio);
				map.put("reasonsDifference", changeComments);
				map.put("shipByDate", shipByDate);
				map.put("actualArrivalDate", formatDate(result.getTimestamp("actual_arrival_date"), true));
				map.put("facilityId", facilityId);
				map.put("partyCustomerSO", partyCustomerSO);
				map.put("contactMechId", address1);
				map.put("categoryId", result.getString("category_name"));
				if (person != null) {
					partyIdCutomer = person.getString("fullName");
				}
				map.put("partyId", partyIdCutomer);
			} catch (Exception e) {
				Debug.logError(e, module);
			}
			return map;
		}
	}
}
