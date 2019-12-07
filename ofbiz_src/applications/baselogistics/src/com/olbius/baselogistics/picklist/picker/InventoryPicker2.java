package com.olbius.baselogistics.picklist.picker;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.uom.UomWorker;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.baselogistics.util.LogisticsUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class InventoryPicker2 {
	Delegator delegator;
	GenericValue userLogin;
	Locale locale;
	List<String> orderIds;
	String facilityId;
	String organizationId;

	List<Map<String, Object>> allItems = FastList.newInstance();
	Map<String, List<Map<String, Object>>> item = FastMap.newInstance();

	public InventoryPicker2(Delegator delegator, GenericValue userLogin, Locale locale, String facilityId) {
		this.delegator = delegator;
		this.userLogin = userLogin;
		this.locale = locale;
		this.facilityId = facilityId;
		organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		autoPick();
	}

	public InventoryPicker2(Delegator delegator, GenericValue userLogin, Locale locale, String facilityId,
			List<String> orderIds) {
		this.delegator = delegator;
		this.userLogin = userLogin;
		this.locale = locale;
		this.facilityId = facilityId;
		this.orderIds = orderIds;
		organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		autoPick();
	}

	public String getFacilityId() {
		return facilityId;
	}

	public List<Map<String, Object>> getItem(String contactMechId) {
		return item.get(contactMechId);
	}

	public List<Map<String, Object>> getAllItems() {
		return allItems;
	}

	private void pickTotalItem() {
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition
					.makeCondition(UtilMisc.toMap("facilityId", facilityId, "orderTypeId", "SALES_ORDER")));
			conditions.add(EntityCondition.makeCondition("orderStatusId", EntityJoinOperator.IN,
					UtilMisc.toList("ORDER_APPROVED")));
			conditions.add(EntityCondition.makeCondition("itemStatusId", EntityJoinOperator.IN,
					UtilMisc.toList("ITEM_APPROVED")));
			conditions.add(EntityCondition.makeCondition("orderId", EntityJoinOperator.IN, orderIds));

			List<GenericValue> dummy = delegator.findList("OrderItemByFacilityAndProduct",
					EntityCondition.makeCondition(conditions), null, UtilMisc.toList("contactMechId", "productCode"), null, false);
			List<String> productIds = EntityUtil.getFieldListFromEntityList(dummy, "productId", true);
			Map<String, BigDecimal> productQuantity = FastMap.newInstance();
			for (String s : productIds) {
				productQuantity.put(s, BigDecimal.ZERO);
			}
			Map<String, Map<String, Object>> items = FastMap.newInstance();
			for (GenericValue x : dummy) {
				Map<String, Object> item = FastMap.newInstance();
				String productId = x.getString("productId");

				item.put("location", productLocations(delegator, facilityId, productId));
				item.put("barcode", getBarcodePrimary(delegator, productId));
				item.put("productId", x.get("productId"));
				item.put("productCode", x.get("productCode"));
				item.put("productName", x.get("productName"));
				item.put("orderStatusId", x.get("orderStatusId"));
				item.put("requireAmount", x.get("requireAmount"));
				item.put("baseWeightUomId", x.get("baseWeightUomId"));
				item.put("baseQuantityUomId", x.get("baseQuantityUomId"));

				if ("Y".equals(x.get("requireAmount"))) {
					item.put("quantityUomId",
							LogisticsUtil.getUomDescription(delegator, locale, x.get("baseWeightUomId")));
					BigDecimal quantity = x.getBigDecimal("quantity").multiply(x.getBigDecimal("selectedAmount"))
							.add(productQuantity.get(productId));
					productQuantity.put(productId, quantity);
					item.put("quantity", quantity);
				} else {
					item.put("quantityUomId",
							LogisticsUtil.getUomDescription(delegator, locale, x.get("baseQuantityUomId")));
					BigDecimal quantity = x.getBigDecimal("quantity").add(productQuantity.get(productId));
					productQuantity.put(productId, quantity);
					item.put("quantity", quantity);
				}
				items.put(productId, item);
			}
			for (String s : items.keySet()) {
				GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", s), false);
				String quantityUomId = product.getString("quantityUomId");
				String purchaseUomId = product.getString("purchaseUomId");
				if (UtilValidate.isEmpty(purchaseUomId))
					purchaseUomId = quantityUomId;

				BigDecimal quantity = (BigDecimal) items.get(s).get("quantity");

				GenericValue productFacility = delegator.findOne("ProductFacility",
						UtilMisc.toMap("facilityId", facilityId, "productId", s), false);
				if (UtilValidate.isNotEmpty(productFacility)) {
					BigDecimal lastInventoryCount = productFacility.getBigDecimal("lastInventoryCount");
					if (UtilValidate.isEmpty(lastInventoryCount)) {
						lastInventoryCount = BigDecimal.ZERO;
					}
					if (lastInventoryCount.compareTo(quantity) < 0) {
						quantity = lastInventoryCount;
					}
					if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
						continue;
					}
					BigDecimal quantityConvert = BigDecimal.ONE;
					if ("Y".equals(product.get("requireAmount"))) {
						quantityConvert = UomWorker.customConvertUom(s, purchaseUomId,
								(String) items.get(s).get("baseWeightUomId"), BigDecimal.ONE, delegator);
						if (quantityConvert == null)
							quantityConvert = BigDecimal.ONE;
					} else {
						quantityConvert = UomWorker.customConvertUom(s, purchaseUomId,
								(String) items.get(s).get("baseQuantityUomId"), BigDecimal.ONE, delegator);
						if (quantityConvert == null)
							quantityConvert = BigDecimal.ONE;
					}
					Map<String, Object> item = FastMap.newInstance();
					item.putAll(items.get(s));
					int remainder = quantity.intValue() % quantityConvert.intValue();
					int divided = (quantity.intValue() - remainder) / quantityConvert.intValue();
					item.put("remainder", remainder);
					item.put("divided", divided);
					item.put("quantity", quantity.intValue());
					item.put("quantityConvert", quantityConvert.intValue());
					allItems.add(item);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void autoPick() {
		try {
			pickTotalItem();
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition
					.makeCondition(UtilMisc.toMap("facilityId", facilityId, "orderTypeId", "SALES_ORDER")));
			conditions.add(EntityCondition.makeCondition("orderStatusId", EntityJoinOperator.IN,
					UtilMisc.toList("ORDER_APPROVED")));
			conditions.add(EntityCondition.makeCondition("itemStatusId", EntityJoinOperator.IN,
					UtilMisc.toList("ITEM_APPROVED")));
			conditions.add(EntityCondition.makeCondition("orderId", EntityJoinOperator.IN, orderIds));

			List<GenericValue> dummy = delegator.findList("OrderItemByFacilityAndProduct",
					EntityCondition.makeCondition(conditions), null, UtilMisc.toList("contactMechId", "productCode"), null, false);
			List<String> contactMechIds = EntityUtil.getFieldListFromEntityList(dummy, "contactMechId", true);

			Map<String, BigDecimal> inventoryPicked = FastMap.newInstance();

			for (String s : contactMechIds) {
				conditions.clear();
				conditions.add(EntityCondition
						.makeCondition(UtilMisc.toMap("facilityId", facilityId, "orderTypeId", "SALES_ORDER")));
				conditions.add(EntityCondition.makeCondition("orderStatusId", EntityJoinOperator.IN,
						UtilMisc.toList("ORDER_APPROVED")));
				conditions.add(EntityCondition.makeCondition("itemStatusId", EntityJoinOperator.IN,
						UtilMisc.toList("ITEM_APPROVED")));
				conditions.add(EntityCondition.makeCondition("orderId", EntityJoinOperator.IN, orderIds));
				conditions.add(EntityCondition.makeCondition("contactMechId", EntityJoinOperator.EQUALS, s));

				dummy = delegator.findList("OrderItemByFacilityAndProduct", EntityCondition.makeCondition(conditions),
						null, UtilMisc.toList("contactMechId", "productCode"), null, false);
				List<Map<String, Object>> deliveryItems = FastList.newInstance();
				for (GenericValue x : dummy) {
					String productId = x.getString("productId");
					GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
					String quantityUomId = product.getString("quantityUomId");
					String purchaseUomId = product.getString("purchaseUomId");
					if (UtilValidate.isEmpty(purchaseUomId))
						purchaseUomId = quantityUomId;
					BigDecimal quantity = BigDecimal.ZERO;
					Object uomName = null;
					if ("Y".equals(x.get("requireAmount"))) {
						uomName = LogisticsUtil.getUomDescription(delegator, locale, x.get("baseWeightUomId"));
						quantity = x.getBigDecimal("quantity").multiply(x.getBigDecimal("selectedAmount"));
					} else {
						uomName = LogisticsUtil.getUomDescription(delegator, locale, x.get("baseQuantityUomId"));
						quantity = x.getBigDecimal("quantity");
					}
					if (quantity.compareTo(BigDecimal.ZERO) > 0) {

						GenericValue productFacility = delegator.findOne("ProductFacility",
								UtilMisc.toMap("facilityId", facilityId, "productId", productId), false);
						if (UtilValidate.isNotEmpty(productFacility)) {
							BigDecimal lastInventoryCount = productFacility.getBigDecimal("lastInventoryCount");
							if (UtilValidate.isEmpty(lastInventoryCount)) {
								lastInventoryCount = BigDecimal.ZERO;
							}
							BigDecimal picked = BigDecimal.ZERO;
							if (inventoryPicked.containsKey(productId)
									&& inventoryPicked.get(productId).compareTo(BigDecimal.ZERO) > 0) {
								picked = inventoryPicked.get(productId);
							}
							lastInventoryCount = lastInventoryCount.subtract(picked);
							if (lastInventoryCount.compareTo(quantity) < 0) {
								quantity = lastInventoryCount;
							}
							if (quantity.compareTo(BigDecimal.ZERO) > 0) {
								inventoryPicked.put(productId, picked.add(quantity));
								BigDecimal quantityConvert = BigDecimal.ONE;
								if ("Y".equals(x.get("requireAmount"))) {
									quantityConvert = UomWorker.customConvertUom(productId, purchaseUomId,
											(String) x.get("baseWeightUomId"), BigDecimal.ONE, delegator);
									if (quantityConvert == null)
										quantityConvert = BigDecimal.ONE;
								} else {
									quantityConvert = UomWorker.customConvertUom(productId, purchaseUomId,
											(String) x.get("baseQuantityUomId"), BigDecimal.ONE, delegator);
									if (quantityConvert == null)
										quantityConvert = BigDecimal.ONE;
								}

								Map<String, Object> item = FastMap.newInstance();
								item.putAll(x);
								item.put("location", productLocations(delegator, facilityId, productId));
								item.put("barcode", getBarcodePrimary(delegator, productId));
								item.put("quantityUomId", uomName);
								int remainder = quantity.intValue() % quantityConvert.intValue();
								int divided = (quantity.intValue() - remainder) / quantityConvert.intValue();
								item.put("remainder", remainder);
								item.put("divided", divided);
								item.put("quantity", quantity.intValue());
								item.put("quantityConvert", quantityConvert.intValue());
								deliveryItems.add(item);
							}
						}
					}
				}
				item.put(s, deliveryItems);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String productLocations(Delegator delegator, String facilityId, String productId) {
		String location = "";
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("facilityId", EntityJoinOperator.EQUALS, facilityId));
			conditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.EQUALS, productId));
			conditions.add(EntityCondition.makeCondition("quantity", EntityJoinOperator.GREATER_THAN, BigDecimal.ZERO));
			List<GenericValue> dummy = delegator.findList("LocationFacilityAndInventoryItemLocation",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			List<String> locationCode = EntityUtil.getFieldListFromEntityList(dummy, "locationCode", true);
			if (UtilValidate.isNotEmpty(locationCode)) {
				location = locationCode.toString().replaceAll(";", ", ").replace("[", "").replace("]", "");
			}
		} catch (Exception e) {
		}
		return location;
	}

	private static String getBarcodePrimary(Delegator delegator, String productId) {
		String barcode = "";
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("goodIdentificationTypeId", EntityJoinOperator.EQUALS, "SKU"));
			conditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.EQUALS, productId));
			List<GenericValue> dummy = delegator.findList("GoodIdentification",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			List<String> idValue = EntityUtil.getFieldListFromEntityList(dummy, "idValue", true);
			if (UtilValidate.isNotEmpty(idValue)) {
				barcode = idValue.toString().replaceAll(";", ", ").replace("[", "").replace("]", "");
			}
		} catch (Exception e) {
		}
		return barcode;
	}
}