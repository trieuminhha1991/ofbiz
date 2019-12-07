package com.olbius.basepo.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.olbius.basepo.utils.ErrorUtils;
import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.olap.bi.accounting.AccountingOlap;

@SuppressWarnings("unused")
public class PurchaseOrderReportImp extends AbstractOlap implements AccountingOlap {
	public static final String MODULE = PurchaseOrderPieImpl.class.getName();

	public static final String DATE_TYPE = "DATE_TYPE";
	public static final String PRODUCT_ID = "PRODUCT_ID";
	public static final String STATUS_ID = "STATUS_ID";
	public static final String PRODUCT_STORE_ID = "PRODUCT_STORE_ID";
	public static final String CATEGORY_ID = "CATEGORY_ID";
	public static final String USER_LOGIN_ID = "USER_LOGIN_ID";
	public static final String FILTER_SALE_ORDER = "FILTER_SALE_ORDER";
	public static final String resource = "BasePOUiLabels";
	public static final String LOCALE = "LOCALE";
	public OlbiusQuery query;
	public String dateType;

	@SuppressWarnings("unchecked")
	private void initQuery() {
		dateType = (String) getParameter(DATE_TYPE);
		List<Object> productId = (List<Object>) getParameter(PRODUCT_ID);
		List<Object> statusId = (List<Object>) getParameter(STATUS_ID);
		List<Object> categoryId = (List<Object>) getParameter(CATEGORY_ID);
		String partyFacilityId = (String) getParameter(USER_LOGIN_ID);
		String filterSaleOrder = (String) getParameter(FILTER_SALE_ORDER);
		dateType = getDateType(dateType);

		query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		query.from("purchase_order_fact").select("date_dimension.".concat(dateType))
				.select("product_dimension.product_code").select("product_dimension.internal_name")
				.select("purchase_order_fact.order_id").select("purchase_order_fact.order_item_seq_id")
				.select("purchase_order_fact.order_id_so").select("purchase_order_fact.facility_id")
				.select("purchase_order_fact.order_item_seq_id_so").select("purchase_order_fact.quantity_uom_id")
				.select("purchase_order_fact.party_id_customer").select("purchase_order_fact.quantity", "quantity")
				.select("status_dimension.status_id").select("category_dimension.category_name")
				.select("purchase_order_fact.ship_by_date").select("purchase_order_fact.contact_mech_id");
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
		if (UtilValidate.isNotEmpty(filterSaleOrder)) {
			if (filterSaleOrder.equals("YES")) {
				condition.and(Condition.make("purchase_order_fact.order_id_so IS NOT NULL"));
			}
			if (filterSaleOrder.equals("NO")) {
				condition.and(Condition.make("purchase_order_fact.order_id_so IS NULL"));
			}
		}
		query.where(condition);
		query.orderBy("order_id", OlbiusQuery.DESC);
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
			addDataField("orderId");
			addDataField("orderIdSo");
			addDataField("date");
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
			addDataField("statusId");
			/* addDataField("actualArrivalDate"); */
			/* addDataField("actualExportedQuantity"); */
			/* addDataField("quantityOnHandDiff"); */
			/*
			 * addDataField("ratio"); addDataField("reasonsDifference");
			 */
		}

		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = new HashMap<String, Object>();
			GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
			Date currentDate = new Date();
			Timestamp currentDateBig = new Timestamp(currentDate.getTime());
			try {
				String facilityId = result.getString("facility_id");
				String orderId = result.getString("order_id");
				String orderItemSeqId = result.getString("order_item_seq_id");
				String address1 = "";
				String partyCustomerSO = "";
				boolean checkAddressSO = true;
				String statusId = result.getString("status_id");
				List<GenericValue> listOrderItemAssoc = delegator.findList("OrderItemAssoc",
						EntityCondition.makeCondition(
								UtilMisc.toMap("toOrderId", orderId, "toOrderItemSeqId", orderItemSeqId)),
						null, null, null, false);
				if (!listOrderItemAssoc.isEmpty()) {
					for (GenericValue orderItemAssoc : listOrderItemAssoc) {
						if (orderItemAssoc != null) {
							String orderIdSO = orderItemAssoc.getString("orderId");
							List<GenericValue> listOrderRole = delegator.findList("OrderRole",
									EntityCondition.makeCondition(
											UtilMisc.toMap("orderId", orderIdSO, "roleTypeId", "SHIP_TO_CUSTOMER")),
									null, null, null, false);
							List<GenericValue> listOrderContactMech = delegator.findList(
									"OrderContactMech", EntityCondition.makeCondition(UtilMisc.toMap("orderId",
											orderIdSO, "contactMechPurposeTypeId", "SHIPPING_LOCATION")),
									null, null, null, false);
							if (!listOrderRole.isEmpty()) {
								for (GenericValue orderRole : listOrderRole) {
									if (orderRole != null) {
										partyCustomerSO = orderRole.getString("partyId");
										GenericValue personSO = delegator.findOne("PartyFullNameDetail",
												UtilMisc.toMap("partyId", partyCustomerSO), false);
										if (personSO != null) {
											partyCustomerSO = personSO.getString("fullName");
										}
										/*
										 * List<GenericValue>
										 * listPartyContactMech =
										 * delegator.findList(
										 * "PartyContactMech",
										 * EntityCondition.makeCondition(
										 * UtilMisc.toMap("partyId",
										 * orderRole.getString("partyId"))),
										 * null, null, null, false);
										 * if(!listPartyContactMech.isEmpty()){
										 * for (GenericValue partyContactMech :
										 * listPartyContactMech) { Timestamp
										 * thruDatePartyContactMech =
										 * partyContactMech.getTimestamp(
										 * "thruDate");
										 * if(thruDatePartyContactMech != null){
										 * if(thruDatePartyContactMech.compareTo
										 * (currentDateBig) > 0){ address1 =
										 * partyContactMech.getString(
										 * "contactMechId"); checkAddressSO =
										 * false; } }else{ address1 =
										 * partyContactMech.getString(
										 * "contactMechId"); checkAddressSO =
										 * false; } } }
										 */
									}
								}
							}
							if (!listOrderContactMech.isEmpty()) {
								for (GenericValue orderContactMech : listOrderContactMech) {
									address1 = orderContactMech.getString("contactMechId");
									checkAddressSO = false;
								}
							}
						}
					}
				}

				if (address1.equals("")) {
					List<GenericValue> listFacilityContactMech = delegator.findList("FacilityContactMech",
							EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null,
							false);
					if (!listFacilityContactMech.isEmpty()) {
						for (GenericValue facilityContactMech : listFacilityContactMech) {
							Timestamp thruDate = facilityContactMech.getTimestamp("thruDate");
							if (thruDate != null) {
								if (thruDate.compareTo(currentDateBig) > 0) {
									address1 = facilityContactMech.getString("contactMechId");
								}
							} else {
								address1 = facilityContactMech.getString("contactMechId");
							}
						}
					}
				}

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

				GenericValue orderItem = delegator.findOne("OrderItem",
						UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId), false);
				String itemPromotion = "N";
				BigDecimal unitPrice = null;
				if (orderItem != null) {
					unitPrice = orderItem.getBigDecimal("unitPrice");
					if (unitPrice != null) {
						int unitPriceint = unitPrice.intValue();
						if (unitPriceint == 0) {
							itemPromotion = "Y";
						}
					}
				}

				/*
				 * boolean checkPartyCustomerSO = true;
				 * if(partyCustomerSO.equals("")){ partyCustomerSO = facilityId;
				 * checkPartyCustomerSO = false; }
				 */

				Locale locale = (Locale) getParameter(LOCALE);
				String titleNotReceive = UtilProperties.getMessage(resource, "PONotReceive", locale);
				if (statusId.equals("ITEM_CREATED")) {
					facilityId = titleNotReceive;
				}

				/*
				 * List<GenericValue> listDelivery =
				 * delegator.findList("Delivery",
				 * EntityCondition.makeCondition(UtilMisc.toMap("orderId",
				 * orderId)), null, null, null, false); Timestamp
				 * actualArrivalDate = null; if(!listDelivery.isEmpty()){ for
				 * (GenericValue delivery : listDelivery) { if (delivery !=
				 * null) { actualArrivalDate =
				 * delivery.getTimestamp("actualArrivalDate"); } } }
				 */

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
				/*
				 * if(actualExportedQuantity.intValue() == 0){ if(quantityDiffen
				 * != null){ if(unitPrice != null){ int unitPriceint =
				 * unitPrice.intValue(); if(unitPriceint != 0){ ratio =
				 * quantityDiffen; actualExportedQuantity = quantity; quantity =
				 * actualExportedQuantity.subtract(quantityDiffen); } } } }
				 */

				String shipByDate = "";
				if (shipAfterDate != null) {
					shipByDate = formatDate(shipAfterDate, true) + " / "
							+ formatDate(result.getTimestamp("ship_by_date"), true);
				}

				GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
				if (UtilValidate.isNotEmpty(statusItem)) {
					statusId = (String) statusItem.get("description", locale);
				}

				GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
				if (UtilValidate.isNotEmpty(facility)) {
					facilityId = (String) facility.get("facilityName", locale);
				}

				BigDecimal quantity = result.getBigDecimal("quantity");
				/*
				 * BigDecimal actualExportedQuantity =
				 * result.getBigDecimal("actualExportedQuantity");
				 */
				/*
				 * BigDecimal quantityOnHandDiff =
				 * result.getBigDecimal("quantityOnHandDiff");
				 */
				/* BigDecimal ratio = quantity.subtract(quantityOnHandDiff); */

				GenericValue person = delegator.findOne("PartyFullNameDetail",
						UtilMisc.toMap("partyId", partyIdCutomer), false);
				map.put("date", formatDate(result.getString(dateType), false));
				map.put("orderId", orderId);
				map.put("orderIdSo", result.getString("order_id_so"));
				map.put("productId", result.getString("product_code"));
				map.put("productName", result.getString("internal_name"));
				map.put("quantityUomId", result.getString("quantity_uom_id"));
				map.put("quantity", quantity);
				/* map.put("actualExportedQuantity", actualExportedQuantity); */
				/*
				 * map.put("quantityOnHandDiff", quantityOnHandDiff);
				 * map.put("ratio", ratio); map.put("reasonsDifference",
				 * changeComments);
				 */
				map.put("shipByDate", shipByDate);
				/*
				 * map.put("actualArrivalDate", formatDate(actualArrivalDate,
				 * true));
				 */
				map.put("facilityId", facilityId);
				map.put("partyCustomerSO", partyCustomerSO);
				map.put("contactMechId", address1);
				map.put("statusId", statusId);
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

	public String formatDate(Object date, boolean isDateTime) {
		// init formatter
		SimpleDateFormat format = null;
		if (isDateTime) {
			format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		} else {
			format = new SimpleDateFormat("dd/MM/yyyy");
		}
		// Result
		String parsedDateStr = "";
		try {
			if (date != null) {
				Date parsedDate = null;
				if (date instanceof String) {
					parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse((String) date);
				} else if (date instanceof Date) {
					parsedDate = (Date) date;
				} else if (date instanceof Timestamp) {
					parsedDate = (Date) date;
				}
				parsedDateStr = format.format(parsedDate);
			}
		} catch (ParseException e) {
			parsedDateStr = "";
		}
		return parsedDateStr;
	}
}
