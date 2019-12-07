package com.olbius.basepos.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import net.sf.json.JSONArray;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class ProductJQServices {
	public static final String module = ProductJQServices.class.getName();

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProduct(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String[] kewordSearchs = parameters.get("keywordSearch");
		List<EntityCondition> orConditionList = FastList.newInstance();
		if (UtilValidate.isNotEmpty(kewordSearchs)) {
			String keywordSearch = kewordSearchs[0];
			if (UtilValidate.isNotEmpty(keywordSearch)) {
				orConditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productId"),
						EntityOperator.LIKE, EntityFunction.UPPER("%" + keywordSearch + "%")));
				orConditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("idValue"),
						EntityOperator.LIKE, EntityFunction.UPPER("%" + keywordSearch + "%")));
				orConditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("internalName"),
						EntityOperator.LIKE, EntityFunction.UPPER("%" + keywordSearch + "%")));
			}
		}
		EntityCondition orCondition = EntityCondition.makeCondition(orConditionList, EntityOperator.OR);
		if (UtilValidate.isEmpty(listSortFields)) {
			listSortFields.add("productId ASC");
		}
		listAllConditions.add(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null));
		listAllConditions.add(EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS, "FINISHED_GOOD"));
		listAllConditions.add(orCondition);
		EntityCondition listCondSearch = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
		try {
			listIterator = delegator.find("ProductAndPriceAndGoodIdentificationSimple", listCondSearch, null, null,
					listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListProduct service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductSupplier(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String[] partyIds = parameters.get("partyId");
		String[] equalConditions = parameters.get("equalCondition");
		String partyId = null;
		String equalCondition = null;
		if (UtilValidate.isNotEmpty(partyIds)) {
			partyId = partyIds[0];
		}
		if (UtilValidate.isNotEmpty(equalConditions)) {
			equalCondition = equalConditions[0];
		}
		EntityCondition condition = null;
		if (UtilValidate.isNotEmpty(equalCondition) && equalCondition.equalsIgnoreCase("Y")) {
			if (UtilValidate.isNotEmpty(partyId)) {
				condition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
			}
		} else {
			if (UtilValidate.isNotEmpty(partyId)) {
				condition = EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, partyId);
			}
		}
		EntityCondition delCondition = EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS,
				null);
		EntityCondition productTypeCondition = EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS,
				"FINISHED_GOOD");
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(productTypeCondition);
		if (UtilValidate.isNotEmpty(condition)) {
			listAllConditions.add(condition);
		}
		listAllConditions.add(delCondition);
		listAllConditions.add(tmpConditon);
		listSortFields.add("-availableFromDate");
		try {
			listIterator = delegator.find("SupplierProductAndProduct",
					EntityCondition.makeCondition(listAllConditions), null, null,
					listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListProductSupplier service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListOrderCustomer(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String[] partyIds = parameters.get("partyId");
		String partyId = null;
		if (UtilValidate.isNotEmpty(partyIds)) {
			partyId = partyIds[0];
		}
		EntityCondition condition = null;
		if (UtilValidate.isNotEmpty(partyId)) {
			condition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
		}
		EntityCondition typeCondition = EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS,
				"SALES_ORDER");
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(condition);
		listAllConditions.add(tmpConditon);
		listAllConditions.add(typeCondition);
		try {
			listIterator = delegator.find("OrderHeaderAndRoleCusAndName",
					EntityCondition.makeCondition(listAllConditions), null, null,
					listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListOrderCustomer service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListRestoreProduct(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		EntityCondition delCondition = EntityCondition.makeCondition("salesDiscontinuationDate",
				EntityOperator.NOT_EQUAL, null);
		EntityCondition productTypeCondition = EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS,
				"FINISHED_GOOD");
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		EntityCondition listAllConditionsSearch = EntityCondition.makeCondition(listAllConditions,
				EntityJoinOperator.OR);
		EntityCondition listCondSearch = EntityCondition.makeCondition(EntityJoinOperator.AND, listAllConditionsSearch,
				delCondition, tmpConditon, productTypeCondition);
		try {
			listIterator = delegator.find("ProductAndPriceAndGoodIdentificationSimple", listCondSearch, null, null,
					listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListRestoreProduct service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListConfigPrintOrder(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			listIterator = delegator.find("ConfigPrintOrderAndFacilityAndAddress",
					EntityCondition.makeCondition(listAllConditions), null, null,
					listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListConfigPrintOrder service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductInCategory(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Map<String, String> mapCondition = new HashMap<String, String>();
		mapCondition.put("productCategoryId", parameters.get("productCategoryId")[0]);
		EntityCondition delCondition = EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS,
				null);
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		listAllConditions.add(delCondition);
		try {
			listIterator = delegator.find("ProductAndPriceAndGoodIdentificationSimple",
					EntityCondition.makeCondition(listAllConditions), null, null,
					listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling JQGetListCategoryProduct service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductToCheckPhysical(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Map<String, String> mapCondition = new HashMap<String, String>();
		mapCondition.put("facilityId", parameters.get("facilityId")[0]);
		EntityCondition delCondition = EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS,
				null);
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		listAllConditions.add(delCondition);
		if (UtilValidate.isEmpty(listSortFields)) {
			listSortFields.add("productId ASC");
		}
		try {
			listIterator = delegator.find("ProductToCheckPhysical",
					EntityCondition.makeCondition(listAllConditions),
					EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO),
					null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling JQGetListProductToCheckPhysical service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductSelectedToCheckPhysical(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Map<String, String> mapCondition = new HashMap<String, String>();
		mapCondition.put("facilityId", parameters.get("facilityId")[0]);
		String productIds = parameters.get("productIds")[0];
		String[] listProductId = productIds.split("_OLBIUS_");
		List<String> productList = new ArrayList<String>();
		for (int i = 0; i < listProductId.length; i++) {
			productList.add(listProductId[i]);
		}
		EntityCondition productCondition = EntityCondition.makeCondition("productId", EntityOperator.IN, productList);
		EntityCondition delCondition = EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS,
				null);
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		listAllConditions.add(delCondition);
		listAllConditions.add(productCondition);
		try {
			listIterator = delegator.find("ProductToCheckPhysical",
					EntityCondition.makeCondition(listAllConditions), null, null,
					listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling JQGetListProductSelectedToCheckPhysical service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListPhysicalInventoryHistory(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		listSortFields.add("physicalInventoryDate DESC");
		try {
			listIterator = delegator.find("PhysicalInventoryAndParty",
					EntityCondition.makeCondition(listAllConditions), null, null,
					listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling JQGetListPhysicalInventoryHistory service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListReceiveInventoryHistory(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			listIterator = delegator.find("ShipmentReceiptAndProduct",
					EntityCondition.makeCondition(listAllConditions), null, null,
					listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling JQGetListReceiveInventoryHistory service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetProductListForTranfer(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		if (UtilValidate.isEmpty(listSortFields)) {
			listSortFields.add("internalName");
		}
		try {
			listIterator = delegator.find("ProductAnConfigPackingAndFacilityCommon",
					EntityCondition.makeCondition(listAllConditions), null, null,
					listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetProductListForTranfer service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetProductListForTranferAdvance(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		if (UtilValidate.isEmpty(listSortFields)) {
			listSortFields.add("internalName");
		}
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String[] facilityIds = parameters.get("facilityId");
		String facilityId = null;
		if (facilityIds != null) {
			facilityId = facilityIds[0];
		}
		List<String> categoryIdList = FastList.newInstance();
		String[] categoryIdListParam = parameters.get("categoryIdList");
		if (UtilValidate.isNotEmpty(categoryIdListParam)) {
			String categoryIdListTmp = categoryIdListParam[0];
			JSONArray categoryIdListJson = JSONArray.fromObject(categoryIdListTmp);
			if (categoryIdListJson != null) {
				for (int index = 0; index < categoryIdListJson.size(); index++) {
					String categoryId = categoryIdListJson.getString(index);
					if (UtilValidate.isNotEmpty(categoryId)) {
						categoryIdList.add(categoryId);
					}
				}
			}
		}

		if (UtilValidate.isNotEmpty(facilityId)) {
			listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		}
		if (UtilValidate.isNotEmpty(categoryIdList)) {
			listAllConditions.add(EntityCondition.makeCondition("categoryId", EntityOperator.IN, categoryIdList));
		}

		try {
			listIterator = delegator.find("ProductAndProductFacility",
					EntityCondition.makeCondition(listAllConditions), null, null,
					listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetProductListForTranferAdvance service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetRowDetailForCalculate(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		if (UtilValidate.isEmpty(listSortFields)) {
			listSortFields.add("facilityId");
		}
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String[] productIds = parameters.get("productId");
		String productId = null;
		if (productIds != null) {
			productId = productIds[0];
		}
		if (UtilValidate.isNotEmpty(productId)) {
			listAllConditions.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		}
		try {
			listIterator = delegator.find("ProductFacilitySummaryAndFacility",
					EntityCondition.makeCondition(listAllConditions), null, null,
					listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetRowDetailForCalculate service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetPlanItem(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		if (UtilValidate.isEmpty(listSortFields)) {
			listSortFields.add("internalName");
		}
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String[] shipmentIds = parameters.get("shipmentId");
		String planId = null;
		String shipmentId = null;
		if (shipmentIds != null) {
			shipmentId = shipmentIds[0];
		}
		GenericValue shipment = null;
		try {
			shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentId), false);
		} catch (GenericEntityException e1) {
			String errMsg = "Fatal error calling jqGetPlanItem service: " + e1.toString();
			Debug.logError(e1, errMsg, module);
		}
		planId = shipment.getString("planId");
		if (UtilValidate.isNotEmpty(planId)) {
			listAllConditions.add(EntityCondition.makeCondition("planId", EntityOperator.EQUALS, planId));
		}
		try {
			listIterator = delegator.find("ProductAndPlanItemCommon",
					EntityCondition.makeCondition(listAllConditions), null, null,
					listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetPlanItem service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetRowDetailTransferPlanHistory(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String[] planIds = parameters.get("planId");
		String planId = null;
		String productId = null;
		if (planIds != null) {
			planId = planIds[0];
		}
		String[] productIds = parameters.get("productId");
		if (productIds != null) {
			productId = productIds[0];
		}
		if (UtilValidate.isNotEmpty(planId)) {
			listAllConditions.add(EntityCondition.makeCondition("planId", EntityOperator.EQUALS, planId));
		}
		if (UtilValidate.isNotEmpty(productId)) {
			listAllConditions.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		}
		try {
			listIterator = delegator.find("PlanItemHistoryAndFaciliy",
					EntityCondition.makeCondition(listAllConditions), null, null,
					listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetRowDetailTransferPlanHistory service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetProductListForPurchase(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			String[] supplierIds = parameters.get("supplierId");
			if (UtilValidate.isNotEmpty(supplierIds)) {
				List<String> supplierList = new ArrayList<>();
				String supplierTmp = supplierIds[0];
				JSONArray supplierArray = JSONArray.fromObject(supplierTmp);
				for (Object supplier : supplierArray) {
					supplierList.add((String) supplier);
				}
				listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, supplierList));
				listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr("availableFromDate", "availableThruDate")));
				if (UtilValidate.isEmpty(listSortFields)) {
					listSortFields.add("productId");
				}
				EntityListIterator listIterator = delegator.find("ProductSummaryAndSupplierProduct",
						EntityCondition.makeCondition(listAllConditions), null, null,
						listSortFields, opts);
				result.put("listIterator", listIterator);
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetProductListForPurchase service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetPlanPurchaseItem(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		if (UtilValidate.isEmpty(listSortFields)) {
			listSortFields.add("productId");
		}
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String[] orderIds = parameters.get("orderId");
		String planId = null;
		String orderId = null;
		if (orderIds != null) {
			orderId = orderIds[0];
		}
		GenericValue orderHeader = null;
		try {
			orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
		} catch (GenericEntityException e1) {
			String errMsg = "Fatal error calling jqGetPlanPurchaseItem service: " + e1.toString();
			Debug.logError(e1, errMsg, module);
		}
		planId = orderHeader.getString("planId");
		if (UtilValidate.isNotEmpty(planId)) {
			listAllConditions.add(EntityCondition.makeCondition("planId", EntityOperator.EQUALS, planId));
		}
		try {
			listIterator = delegator.find("PlanItemHistoryAndProduct",
					EntityCondition.makeCondition(listAllConditions), null, null,
					listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetPlanPurchaseItem service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		result.put("listIterator", listIterator);
		return result;
	}
}
