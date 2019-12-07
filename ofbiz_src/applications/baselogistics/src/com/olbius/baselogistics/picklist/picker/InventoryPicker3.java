package com.olbius.baselogistics.picklist.picker;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.uom.UomWorker;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.baselogistics.util.LogisticsUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class InventoryPicker3 {
	Delegator delegator;
	GenericValue userLogin;
	Locale locale;
	String picklistId;
	List<String> orderIds;
	List<String> contactMechIds;

	Map<String, List<Map<String, Object>>> picklistBins = FastMap.newInstance();
	Map<String, Object> item = FastMap.newInstance();

	public InventoryPicker3(Delegator delegator, GenericValue userLogin, Locale locale, List<String> orderIds,
			List<String> contactMechIds, String picklistId) {
		this.delegator = delegator;
		this.userLogin = userLogin;
		this.locale = locale;
		this.picklistId = picklistId;
		this.orderIds = orderIds;
		this.contactMechIds = contactMechIds;
		try {
			autoPick();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getItem(String contactMechId) {
		return (List<Map<String, Object>>) item.get(contactMechId);
	}

	@SuppressWarnings("unchecked")
	public List<String> getOrders(String contactMechId) {
		return (List<String>) item.get(contactMechId + "orderIds");
	}

	public Map<String, List<Map<String, Object>>> getAllItems() {
		return picklistBins;
	}

	private void pickTotalItem() {
		EntityListIterator iterator = null;
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("picklistId", picklistId)));
			List<GenericValue> dummy = delegator.findList("PicklistBin", EntityCondition.makeCondition(conditions),
					null, UtilMisc.toList("picklistBinId"), null, false);
			List<String> picklistBinIds = EntityUtil.getFieldListFromEntityList(dummy, "picklistBinId", true);
			for (String picklistBinId : picklistBinIds) {
				List<Map<String, Object>> items = FastList.newInstance();
				try {
					conditions.clear();
					conditions.add(EntityCondition.makeCondition("picklistId", EntityJoinOperator.EQUALS, picklistId));
					conditions.add(
							EntityCondition.makeCondition("picklistBinId", EntityJoinOperator.EQUALS, picklistBinId));
					conditions.add(EntityCondition.makeCondition("itemStatusId", EntityJoinOperator.NOT_EQUAL,
							"PICKITEM_CANCELLED"));
					iterator = delegator.find("PicklistItemSum", EntityCondition.makeCondition(conditions), null, null,
							UtilMisc.toList("productCode"), null);
					GenericValue value = null;
					while ((value = iterator.next()) != null) {
						Map<String, Object> item = FastMap.newInstance();

						Object facilityId = value.get("facilityId");
						String productId = value.getString("productId");
						item.put("location", productLocations(delegator, facilityId, productId));
						item.put("barcode", getBarcodePrimary(delegator, productId));
						item.put("productId", productId);
						item.put("productCode", value.get("productCode"));
						item.put("productName", value.get("productName"));
						item.put("primaryProductCategoryId", value.get("primaryProductCategoryId"));

						BigDecimal quantityConvert = BigDecimal.ONE;
						if ("Y".equals(value.get("requireAmount"))) {
							quantityConvert = UomWorker.customConvertUom(productId, value.getString("purchaseUomId"),
									value.getString("weightUomId"), BigDecimal.ONE, delegator);
							if (quantityConvert == null)
								quantityConvert = BigDecimal.ONE;
							item.put("quantityUomId",
									LogisticsUtil.getUomDescription(delegator, locale, value.get("weightUomId")));
						} else {
							quantityConvert = UomWorker.customConvertUom(productId, value.getString("purchaseUomId"),
									value.getString("quantityUomId"), BigDecimal.ONE, delegator);
							if (quantityConvert == null)
								quantityConvert = BigDecimal.ONE;
							item.put("quantityUomId",
									LogisticsUtil.getUomDescription(delegator, locale, value.get("quantityUomId")));
						}
						BigDecimal quantity = value.getBigDecimal("quantity");
						int remainder = quantity.intValue() % quantityConvert.intValue();
						int divided = (quantity.intValue() - remainder) / quantityConvert.intValue();
						item.put("remainder", remainder);
						item.put("divided", divided);
						item.put("quantity", quantity.intValue());
						item.put("quantityConvert", quantityConvert.intValue());
						items.add(item);
					}
					picklistBins.put(picklistBinId, items);
				} catch (Exception e) {
					throw e;
				} finally {
					if (iterator != null) {
						try {
							iterator.close();
						} catch (GenericEntityException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void autoPick() throws GenericTransactionException {
		boolean beganTx = TransactionUtil.begin(7200);
		EntityListIterator iterator = null;
		try {
			pickTotalItem();
			for (String contactMechId : contactMechIds) {
				List<Map<String, Object>> items = FastList.newInstance();
				List<EntityCondition> conditions = FastList.newInstance();
				conditions
						.add(EntityCondition.makeCondition("contactMechId", EntityJoinOperator.EQUALS, contactMechId));
				conditions.add(EntityCondition.makeCondition("orderId", EntityJoinOperator.IN, orderIds));

				List<GenericValue> dummy = delegator.findList("OrderAndShippingLocation",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				List<String> orderIds = EntityUtil.getFieldListFromEntityList(dummy, "orderId", true);

				conditions.clear();
				conditions.add(EntityCondition.makeCondition("orderId", EntityJoinOperator.IN, orderIds));
				conditions.add(EntityCondition.makeCondition("picklistId", EntityJoinOperator.EQUALS, picklistId));
				conditions.add(EntityCondition.makeCondition("itemStatusId", EntityJoinOperator.NOT_EQUAL,
						"PICKITEM_CANCELLED"));
				if (iterator != null) {
					iterator.close();
				}
				iterator = delegator.find("PicklistItemSum2", EntityCondition.makeCondition(conditions), null, null,
						UtilMisc.toList("productCode"), null);
				GenericValue value = null;
				while ((value = iterator.next()) != null) {
					Map<String, Object> item = FastMap.newInstance();

					Object facilityId = value.get("facilityId");
					String productId = value.getString("productId");
					item.put("location", productLocations(delegator, facilityId, productId));
					item.put("barcode", getBarcodePrimary(delegator, productId));
					item.put("productId", productId);
					item.put("productCode", value.get("productCode"));
					item.put("productName", value.get("productName"));
					item.put("primaryProductCategoryId", value.get("primaryProductCategoryId"));

					BigDecimal quantityConvert = BigDecimal.ONE;
					if ("Y".equals(value.get("requireAmount"))) {
						quantityConvert = UomWorker.customConvertUom(productId, value.getString("purchaseUomId"),
								value.getString("weightUomId"), BigDecimal.ONE, delegator);
						if (quantityConvert == null)
							quantityConvert = BigDecimal.ONE;
						item.put("quantityUomId",
								LogisticsUtil.getUomDescription(delegator, locale, value.get("weightUomId")));
					} else {
						quantityConvert = UomWorker.customConvertUom(productId, value.getString("purchaseUomId"),
								value.getString("quantityUomId"), BigDecimal.ONE, delegator);
						if (quantityConvert == null)
							quantityConvert = BigDecimal.ONE;
						item.put("quantityUomId",
								LogisticsUtil.getUomDescription(delegator, locale, value.get("quantityUomId")));
					}
					BigDecimal quantity = value.getBigDecimal("quantity");
					int remainder = quantity.intValue() % quantityConvert.intValue();
					int divided = (quantity.intValue() - remainder) / quantityConvert.intValue();
					item.put("remainder", remainder);
					item.put("divided", divided);
					item.put("quantity", quantity.intValue());
					item.put("quantityConvert", quantityConvert.intValue());
					items.add(item);
				}
				item.put(contactMechId, items);
				item.put(contactMechId + "orderIds", orderIds);
			}
		} catch (Exception e) {
			e.printStackTrace();
			TransactionUtil.rollback(beganTx, e.getMessage(), e);
		} finally {
			if (iterator != null) {
				try {
					iterator.close();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
		}
		TransactionUtil.commit(beganTx);
	}

	private static String productLocations(Delegator delegator, Object facilityId, Object productId) {
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
				location = locationCode.toString().replaceAll(";", ", ");
			}
		} catch (Exception e) {
		}
		return location;
	}

	private static String getBarcodePrimary(Delegator delegator, Object productId) {
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