package com.olbius.baselogistics.picklist;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.baselogistics.util.LogisticsUtil;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.common.util.EntityMiscUtil;
import com.olbius.product.util.InventoryUtil;
import com.olbius.product.util.ProductUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class PicklistServices {

	public static final String module = PicklistServices.class.getName();
	public static final String resource = "BaseLogisticsUiLabels";
	public static final String PICKING_CREATOR = "PICKING_CREATOR";
	public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listPartiesInOrganization(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			listSortFields.add("partyName");
			
			EntityListIterator listIterator = EntityMiscUtil.processIterator(parameters, result, 
		   			 delegator, "Employee", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), 
		   			 null, null, listSortFields, opts);
			
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listPickingItemTempData(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		
		List<Map<String, Object>> listPicklistItems = FastList.newInstance();
		
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		if (parameters.containsKey("facilityId") && UtilValidate.isNotEmpty(parameters.get("facilityId"))) {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityJoinOperator.EQUALS, parameters.get("facilityId")[0]));
			listAllConditions.add(EntityCondition.makeCondition("userLoginId", EntityJoinOperator.EQUALS, userLogin.get("userLoginId")));
			if (listSortFields.isEmpty()){
				listSortFields.add("orderId");
				listSortFields.add("productCode");
			}
			List<GenericValue> listPicklistItemTmps = FastList.newInstance();
			try {
				listPicklistItemTmps = EntityMiscUtil.processIteratorToList(parameters, result, delegator, "PickingItemTempData", 
					EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
			} catch (Exception e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: listPickingItemTempData error! "+ e.toString());
			}
			if (UtilValidate.isNotEmpty(listPicklistItemTmps)) {
				for (GenericValue item : listPicklistItemTmps) {
					String productId = item.getString("productId");
					String facilityId = item.getString("facilityId");
					String picklistBinId = item.getString("picklistBinId");
					Map<String, Object> map = FastMap.newInstance();
					map.putAll(item);
					EntityCondition condpr = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
					EntityCondition condfa = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId);
					EntityCondition condbin = EntityCondition.makeCondition("picklistBinId", EntityOperator.NOT_EQUAL, picklistBinId);
					EntityCondition condQty = EntityCondition.makeCondition("quantity", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
					EntityCondition condStt = EntityCondition.makeCondition("itemStatusId", EntityOperator.IN, UtilMisc.toList("PICKITEM_PENDING", "PICKITEM_APPROVED", "PICKITEM_PICKED", "PICKITEM_CHECKED", "PICKITEM_DLV_CREATED"));
					List<EntityCondition> conds = FastList.newInstance();
					conds.add(condpr);
					conds.add(condfa);
					conds.add(condbin);
					conds.add(condQty);
					conds.add(condStt);
					List<GenericValue> listPicklistItemOthers = FastList.newInstance();
					try {
						listPicklistItemOthers = delegator.findList("PicklistItemAndInventoryItem", EntityCondition.makeCondition(conds), null, null, null, false);
					} catch (GenericEntityException e) {
						return ServiceUtil.returnError("OLBIUS: findList PicklistItemAndInventoryItem error! " + e.toString());
					}
					if (!listPicklistItemOthers.isEmpty()){
						List<String> listPickbinOther = FastList.newInstance();
						listPickbinOther = EntityUtil.getFieldListFromEntityList(listPicklistItemOthers, "picklistBinId", true);
						String otherId = null;
						for (String binId : listPickbinOther) {
							if (otherId == null){
								otherId = binId;
							} else {
								otherId = otherId +","+ binId;
							}
						}
						if (UtilValidate.isNotEmpty(otherId)) {
							map.put("otherPicklistBinId", otherId);
						}
					}
					listPicklistItems.add(map);
				}
			}
		}
		
		result.put("listIterator", listPicklistItems);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listFacilities(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator,
					userLogin.getString("userLoginId"));
			listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("facilityTypeId", "WAREHOUSE",
					"ownerPartyId", ownerPartyId)));
			listSortFields.add("facilityId");
			
			EntityListIterator listIterator = EntityMiscUtil.processIterator(parameters, result, 
		   			 delegator, "Facility", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), 
		   			 null, null, listSortFields, opts);
			
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listPicklist(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			listSortFields.add("-picklistDate");
			
			EntityListIterator listIterator = EntityMiscUtil.processIterator(parameters, result, 
		   			 delegator, "PicklistDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), 
		   			 null, null, listSortFields, opts);
			
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listPicklistBin(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Delegator delegator = ctx.getDelegator();
		EntityListIterator listIterator = null;
		String primaryPicklistId = null;
		if (parameters.containsKey("primaryPicklistId") && UtilValidate.isNotEmpty(parameters.get("primaryPicklistId"))) {
			primaryPicklistId = parameters.get("primaryPicklistId")[0];
		}
		List<String> productIds = FastList.newInstance();
		String productIdsStr = SalesUtil.getParameter(parameters, "productIds");
		if (UtilValidate.isNotEmpty(productIdsStr)) {
			JSONArray jsonArray = new JSONArray();
			if (UtilValidate.isNotEmpty(productIdsStr)) jsonArray = JSONArray.fromObject(productIdsStr);
			if (jsonArray != null && jsonArray.size() > 0) {
				for (int k = 0; k < jsonArray.size(); k++) {
					String productId = jsonArray.getString(k);
					if (UtilValidate.isNotEmpty(productId)) productIds.add(productId);
				}
			}
		}
		
		
		if (UtilValidate.isNotEmpty(productIds)){
			List<String> picklistBinIds = FastList.newInstance();
			List<GenericValue> picklistBinItems = null;
			try {
				picklistBinItems = delegator.findList("PicklistBinItem", 
						EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (UtilValidate.isNotEmpty(picklistBinItems)) {
				for (GenericValue item : picklistBinItems) {
					picklistBinIds.add(item.getString("picklistBinId"));
				}
			}
			listAllConditions.add(EntityCondition.makeCondition("picklistBinId", EntityJoinOperator.IN, picklistBinIds));
		}
		
		try {
			if (UtilValidate.isNotEmpty(primaryPicklistId)) {
				listAllConditions.add(EntityCondition.makeCondition("picklistId", EntityJoinOperator.EQUALS, primaryPicklistId));
			}
			if (listSortFields.isEmpty()) {
				listSortFields.add("-picklistBinId");
			}
			
			listIterator = EntityMiscUtil.processIterator(parameters, result, 
		   			 delegator, "PicklistBinAndRoleAndDate", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), 
		   			 null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listPicklistItem(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("picklistBinId") && UtilValidate.isNotEmpty(parameters.get("picklistBinId"))) {
				List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
				List<String> listSortFields = (List<String>) context.get("listSortFields");
				EntityFindOptions opts = (EntityFindOptions) context.get("opts");
				listAllConditions.add(EntityCondition.makeCondition("picklistBinId", EntityJoinOperator.EQUALS,
						parameters.get("picklistBinId")[0]));
				listSortFields.add("productCode");
				
				EntityListIterator listIterator = EntityMiscUtil.processIterator(parameters, result, 
			   			 delegator, "PicklistItemSum4", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), 
			   			 null, null, listSortFields, opts);
				
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listPicklistItemLocation(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("picklistBinId") && UtilValidate.isNotEmpty(parameters.get("picklistBinId")) 
					&& parameters.containsKey("orderId") && UtilValidate.isNotEmpty(parameters.get("orderId"))
					&& parameters.containsKey("orderItemSeqId") && UtilValidate.isNotEmpty(parameters.get("orderItemSeqId"))
					&& parameters.containsKey("shipGroupSeqId") && UtilValidate.isNotEmpty(parameters.get("shipGroupSeqId"))) {
				List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
				List<String> listSortFields = (List<String>) context.get("listSortFields");
				EntityFindOptions opts = (EntityFindOptions) context.get("opts");
				listAllConditions.add(EntityCondition.makeCondition("picklistBinId", EntityJoinOperator.EQUALS,
						parameters.get("picklistBinId")[0]));
				listAllConditions.add(EntityCondition.makeCondition("orderId", EntityJoinOperator.EQUALS,
						parameters.get("orderId")[0]));
				listAllConditions.add(EntityCondition.makeCondition("orderItemSeqId", EntityJoinOperator.EQUALS,
						parameters.get("orderItemSeqId")[0]));
				listAllConditions.add(EntityCondition.makeCondition("shipGroupSeqId", EntityJoinOperator.EQUALS,
						parameters.get("shipGroupSeqId")[0]));
				EntityListIterator listIterator = delegator.find("PicklistItemLocationCode",
						EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// picklistBin and picklistItem for mobile services
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listPicklistBinForMobile(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		
		try {
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("statusId") && UtilValidate.isNotEmpty(parameters.get("statusId"))) {
				String statusId = (String) parameters.get("statusId")[0];
				if (statusId != null && ("PICKBIN_INPUT".equals(statusId) || "PICKBIN_PICKED".equals(statusId))) {
					List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
					listAllConditions.add(EntityCondition.makeCondition("binStatusId", EntityOperator.EQUALS, statusId));
					List<String> listSortFields = (List<String>) context.get("listSortFields");
					EntityFindOptions opts = (EntityFindOptions) context.get("opts");
					EntityListIterator listIterator = delegator.find("PicklistBin",
							EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
					result.put("listIterator", listIterator);
				} else {
					
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, Object> listPicklistItemByPicklistBinId(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			String picklistBinId = (String) context.get("picklistBinId");
			if (picklistBinId != null && !"".equals(picklistBinId)) {
				List<EntityCondition> listAllConditions = FastList.newInstance();
				listAllConditions.add(EntityCondition.makeCondition("picklistBinId", EntityJoinOperator.EQUALS,
						picklistBinId));
				EntityListIterator listIterator = delegator.find("PicklistItemById",
						EntityCondition.makeCondition(listAllConditions), null, null, null, null);
				GenericValue item = null;
				List<Map<String, Object>> listPicklistItemOutput = FastList.newInstance();
				while ((item = listIterator.next()) != null) {
					String orderId = (String) item.get("orderId");
					String orderItemSeqId = (String) item.get("orderItemSeqId");
					String shipGroupSeqId = (String) item.get("shipGroupSeqId");
					String inventoryItemId = (String) item.get("inventoryItemId");
					String locationCode = "";
					String locationId = "";
					String productId = (String) item.get("productId");
					String productCode = (String) item.get("productCode");
					String productName = (String) item.get("productName");
					BigDecimal quantity = (BigDecimal) item.get("quantity");
					List<GenericValue> itemLocationList = delegator.findList("PicklistItemLocationCode", 
							EntityCondition.makeCondition(UtilMisc.toMap("picklistBinId", picklistBinId, 
									"orderId", orderId, "orderItemSeqId", orderItemSeqId, 
									"shipGroupSeqId", shipGroupSeqId, "inventoryItemId", inventoryItemId)), 
									null, null, null, false);
					if (UtilValidate.isNotEmpty(itemLocationList)) {
						for(GenericValue itemLocation : itemLocationList) {
							locationCode = (String) itemLocation.get("locationCode");
							locationId = (String) itemLocation.get("locationId");
							quantity = (BigDecimal) itemLocation.get("quantity");
							Map<String, Object> outTmp = FastMap.newInstance();
							outTmp.put("productId", productId);
							outTmp.put("productCode", productCode);
							outTmp.put("productName", productName);
							outTmp.put("quantity", quantity);
							outTmp.put("locationCode", locationCode);
							outTmp.put("locationId", locationId);
							listPicklistItemOutput.add(outTmp);
						}
					} else {
						Map<String, Object> outTmp = FastMap.newInstance();
						outTmp.put("productId", productId);
						outTmp.put("productCode", productCode);
						outTmp.put("productName", productName);
						outTmp.put("quantity", quantity);
						outTmp.put("locationCode", locationCode);
						outTmp.put("locationId", locationId);
						listPicklistItemOutput.add(outTmp);
					}
				}
				result.put("listIterator", listPicklistItemOutput);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listSalesOrderHeaderApproved(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("facilityId") && UtilValidate.isNotEmpty(parameters.get("facilityId"))) {
				List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
				List<String> listSortFields = (List<String>) context.get("listSortFields");
				EntityFindOptions opts = (EntityFindOptions) context.get("opts");
				String facilityId = null;
				if (parameters.get("facilityId") != null && parameters.get("facilityId").length > 0){
					facilityId = (String)parameters.get("facilityId")[0];
	        	}
				if (UtilValidate.isNotEmpty(facilityId)) {
					listAllConditions.add(EntityCondition.makeCondition("originFacilityId", EntityJoinOperator.EQUALS, facilityId));
				}
				String contactMechId = null;
				if (parameters.get("contactMechId") != null && parameters.get("contactMechId").length > 0){
					contactMechId = (String)parameters.get("contactMechId")[0];
				}
				if (UtilValidate.isNotEmpty(contactMechId)) {
					listAllConditions.add(EntityCondition.makeCondition("contactMechId", EntityJoinOperator.EQUALS, contactMechId));
				}
				
				listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityJoinOperator.EQUALS,
						"SALES_ORDER"));
				listAllConditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.EQUALS,
						"ORDER_APPROVED"));
				listSortFields.add("-orderDate");
				
				EntityListIterator listIterator = EntityMiscUtil.processIterator(parameters, result, 
			   			 delegator, "OrderHeaderFullView", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), 
			   			 null, null, listSortFields, opts);
				
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listSalesOrderHeaderApproved2(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("facilityId") && UtilValidate.isNotEmpty(parameters.get("facilityId"))) {
				List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
				List<String> listSortFields = (List<String>) context.get("listSortFields");
				EntityFindOptions opts = (EntityFindOptions) context.get("opts");
				String facilityId = null;
				if (parameters.get("facilityId") != null && parameters.get("facilityId").length > 0){
					facilityId = (String)parameters.get("facilityId")[0];
	        	}
				if (UtilValidate.isNotEmpty(facilityId)) {
					listAllConditions.add(EntityCondition.makeCondition("originFacilityId", EntityJoinOperator.EQUALS, facilityId));
				}
				String contactMechId = null;
				if (parameters.get("contactMechId") != null && parameters.get("contactMechId").length > 0){
					contactMechId = (String)parameters.get("contactMechId")[0];
				}
				if (UtilValidate.isNotEmpty(contactMechId)) {
					listAllConditions.add(EntityCondition.makeCondition("contactMechId", EntityJoinOperator.EQUALS, contactMechId));
				}
				String partyId = null;
				if(parameters.get("partyId") != null && parameters.get("partyId").length >0){
					partyId = (String)parameters.get("partyId")[0];
				}
				if(UtilValidate.isNotEmpty(partyId)){
					listAllConditions.add(EntityCondition.makeCondition("customerId", EntityJoinOperator.EQUALS, partyId));
				}
				
				listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityJoinOperator.EQUALS,
						"SALES_ORDER"));
				listAllConditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.EQUALS,
						"ORDER_APPROVED"));
				listSortFields.add("-orderDate");
				
				EntityListIterator listIterator = EntityMiscUtil.processIterator(parameters, result, 
			   			 delegator, "OrderHeaderFullView2", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), 
			   			 null, null, listSortFields, opts);
				
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, Object> createPickingItemTempData(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			String pickingItemId = delegator.getNextSeqId("PickingItemTempData");
			GenericValue pickingItemTempData = delegator.makeValidValue("PickingItemTempData", context);
			pickingItemTempData.set("pickingItemId", pickingItemId);
			pickingItemTempData.create();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> updatePickingItemTempData(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String userLoginId = userLogin.getString("userLoginId");
			String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
			BigDecimal quantityPicked = (BigDecimal) context.get("quantityPicked");
			GenericValue pickingItemTempData = delegator.findOne("PickingItemTempData",
					UtilMisc.toMap("pickingItemId", context.get("pickingItemId")), false);
			if (quantityPicked.compareTo(pickingItemTempData.getBigDecimal("quantity")) > 0) {
				throw new Exception(UtilProperties.getMessage(resource, "DmsKhongLonHonSoLuongTrenDon", locale));
			}
			String productId = pickingItemTempData.getString("productId");
			String facilityId = pickingItemTempData.getString("facilityId");
			GenericValue objProduct = null;
			try {
				objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
			} catch (GenericEntityException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: findOne Product error!");
			}
			
			BigDecimal qoh = quantityOnHandTotal(delegator, ownerPartyId, facilityId, productId, objProduct.get("requireAmount"));
			List<GenericValue> listProductInOtherOrder = FastList.newInstance();
			EntityCondition cond1 = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
			EntityCondition cond2 = EntityCondition.makeCondition("orderId", EntityOperator.NOT_EQUAL, pickingItemTempData.getString("orderId"));
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(cond1);
			conds.add(cond2);
			try {
				listProductInOtherOrder = delegator.findList("PickingItemTempData", EntityCondition.makeCondition(conds), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: findList PickingItemTempData error!");
			}
			BigDecimal qtyOtherPicked = BigDecimal.ZERO;
			if (!listProductInOtherOrder.isEmpty()){
				for (GenericValue item : listProductInOtherOrder) {
					if (UtilValidate.isNotEmpty(item.get("quantityPicked"))) {
						qtyOtherPicked = qtyOtherPicked.add(item.getBigDecimal("quantityPicked"));
					}
				}
			}
			if (UtilValidate.isNotEmpty(qoh)) {
				qoh = qoh.subtract(qtyOtherPicked);
			}
			if (quantityPicked.compareTo(qoh) > 0){
				throw new Exception(UtilProperties.getMessage(resource, "BLNotEnoughInventory", locale));
			}
			pickingItemTempData.set("quantityPicked", quantityPicked);
			pickingItemTempData.store();
			result.put("pickingItemId", pickingItemTempData.get("pickingItemId"));
			makePickingItemAccurately(delegator, locale, pickingItemTempData.get("facilityId"),
					pickingItemTempData.get("productId"), ownerPartyId, pickingItemTempData.get("picklistBinId"));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	private static void makePickingItemAccurately(Delegator delegator, Locale locale, Object facilityId,
			Object productId, Object ownerPartyId, Object picklistBinId) throws Exception {
		GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
		EntityListIterator iterator = delegator.find("PickingItemTempData",
				EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "productId", productId)), null,
				null, null, null);
		GenericValue value = null;
		while ((value = iterator.next()) != null) {
			BigDecimal quantity = value.getBigDecimal("quantity");
			GenericValue objFacility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityId));
			String requireLocation = "N";
			if (UtilValidate.isNotEmpty(objFacility.get("requireLocation"))) {
				requireLocation = objFacility.getString("requireLocation");
			}
			quantity = quantity.subtract(quantityPickedOnOrder(delegator, value.get("orderId"),
					value.get("orderItemSeqId"), picklistBinId));

			BigDecimal quantityOnHandTotal = quantityOnHandTotal(delegator, ownerPartyId, facilityId, productId,
					product.get("requireAmount"));
			BigDecimal quantityOnHandTotalInPickFace = quantityOnHandTotalInPickFace(delegator, ownerPartyId, facilityId, productId,
					product.get("requireAmount"));
			BigDecimal quantityPickable = BigDecimal.ZERO;
			if ("Y".equals(requireLocation)){
				quantityPickable = quantityOnHandTotalInPickFace.subtract(quantityPicked(delegator, facilityId, productId,
						value.get("orderId"), value.get("orderItemSeqId"), picklistBinId));
			} else {
				quantityPickable = quantityOnHandTotal.subtract(quantityPicked(delegator, facilityId, productId,
						value.get("orderId"), value.get("orderItemSeqId"), picklistBinId));
			}
			
			if (quantityPickable.compareTo(BigDecimal.ZERO) < 0) {
				throw new Exception(UtilProperties.getMessage(resource, "SGCKhongLonHonSoLuongCoTheSoan", locale));
			}

			if (quantityPickable.compareTo(quantity) > 0) {
				quantityPickable = quantity;
			}
			BigDecimal quantityPicked = value.getBigDecimal("quantityPicked");
			quantityPickable = quantityPickable.subtract(quantityPicked);
			if (quantityPickable.compareTo(BigDecimal.ZERO) < 0) {
				quantityPickable = BigDecimal.ZERO;
			}
			value.set("quantityOnHandTotal", quantityOnHandTotal);
			value.set("quantityPickable", quantityPickable);
			value.store();
		}
		if (iterator != null) {
			iterator.close();
		}
	}

	private static BigDecimal quantityPicked(Delegator delegator, Object facilityId, Object productId, Object orderId,
			Object orderItemSeqId, Object picklistBinId) throws Exception {
		BigDecimal quantity = BigDecimal.ZERO;

		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "productId", productId)));
		conditions.add(EntityCondition.makeCondition("orderId", EntityJoinOperator.NOT_EQUAL, orderId));
		conditions.add(EntityCondition.makeCondition("orderItemSeqId", EntityJoinOperator.NOT_EQUAL, orderItemSeqId));
		List<GenericValue> pickingItems = delegator.findList("PickingItemTempData",
				EntityCondition.makeCondition(conditions), null, null, null, false);
		for (GenericValue x : pickingItems) {
			BigDecimal quantityPicked = x.getBigDecimal("quantityPicked");
			if (UtilValidate.isNotEmpty(quantityPicked)) {
				quantity = quantity.add(quantityPicked);
			}
		}

		conditions.clear();
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "productId", productId)));
		if (UtilValidate.isNotEmpty(picklistBinId)) {
			conditions.add(EntityCondition.makeCondition("picklistBinId", EntityJoinOperator.NOT_EQUAL, picklistBinId));
		}
		conditions.add(EntityCondition.makeCondition("itemStatusId", EntityJoinOperator.IN,
				UtilMisc.toList("PICKITEM_PENDING", "PICKITEM_APPROVED", "PICKITEM_PICKED", "PICKITEM_CHECKED", "PICKITEM_DLV_CREATED")));
		List<GenericValue> picklistItems = delegator.findList("PicklistItemAndInventoryItem",
				EntityCondition.makeCondition(conditions), null, null, null, false);
		for (GenericValue x : picklistItems) {
			if (UtilValidate.isNotEmpty(x.getBigDecimal("quantity"))) {
				quantity = quantity.add(x.getBigDecimal("quantity"));
			}
		}

		return quantity;
	}

	private static BigDecimal quantityPickedOnOrder(Delegator delegator, Object orderId, Object orderItemSeqId,
			Object picklistBinId) throws Exception {
		BigDecimal quantity = BigDecimal.ZERO;
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "orderItemSeqId",
				orderItemSeqId)));
		if (UtilValidate.isNotEmpty(picklistBinId)) {
			conditions.add(EntityCondition.makeCondition("picklistBinId", EntityJoinOperator.NOT_EQUAL, picklistBinId));
		}
		conditions.add(EntityCondition
				.makeCondition("itemStatusId", EntityJoinOperator.NOT_IN, UtilMisc.toList("PICKITEM_CANCELLED", "PICKITEM_COMPLETED")));
		List<GenericValue> picklistItems = delegator.findList("PicklistItemAndInventoryItem",
				EntityCondition.makeCondition(conditions), null, null, null, false);
		for (GenericValue x : picklistItems) {
			if (UtilValidate.isNotEmpty(x.getBigDecimal("quantity"))) {
				quantity = quantity.add(x.getBigDecimal("quantity"));
			}
		}
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition(UtilMisc.toMap("fromOrderId", orderId, "fromOrderItemSeqId", orderItemSeqId)));
		EntityCondition condDiff = EntityCondition.makeCondition("actualExportedQuantity", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
		EntityCondition condStt = EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("DELI_ITEM_EXPORTED", "DELI_ITEM_DELIVERED"));
		conds.add(condStt);
		conds.add(condDiff);
		List<GenericValue> listDetail = FastList.newInstance();
		listDetail = delegator.findList("DeliveryItem", EntityCondition.makeCondition(conds), null, null, null, false);
		if (!listDetail.isEmpty()){
			for (GenericValue item : listDetail) {
				quantity = quantity.add(item.getBigDecimal("actualExportedQuantity"));
			}
		}
		return quantity;
	}

	public static Map<String, Object> deleteAllPickingItemTempData(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator iterator = null;
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String userLoginId = userLogin.getString("userLoginId");
			iterator = delegator.find(
					"PickingItemTempData",
					EntityCondition.makeCondition(UtilMisc.toMap("userLoginId", userLoginId, "facilityId",
							context.get("facilityId"))), null, null, null, null);
			GenericValue value = null;
			while ((value = iterator.next()) != null) {
				value.remove();
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> bufferIntoPickingItem(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> mapProductQuantity = FastMap.newInstance();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String userLoginId = userLogin.getString("userLoginId");
			String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
			List<String> orderIds = (List<String>) context.get("orderIds[]");
			Object facilityId = context.get("facilityId");
			if (UtilValidate.isNotEmpty(orderIds)) {
				
				dispatcher.runSync("deleteAllPickingItemTempData", UtilMisc.toMap("facilityId", facilityId, "userLogin", userLogin));

				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition("orderId", EntityJoinOperator.IN, orderIds));
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("orderItemTypeId", "PRODUCT_ORDER_ITEM", "statusId", "ITEM_APPROVED")));
				List<GenericValue> orderItems = delegator.findList("OrderItemAndProduct", EntityCondition.makeCondition(conditions), null, null, null, false);
				for (GenericValue x : orderItems) {
					String productId = x.getString("productId");
					BigDecimal quantity = x.getBigDecimal("quantity");
					if ("Y".equals(x.get("requireAmount"))) {
						BigDecimal selectedAmount = x.getBigDecimal("selectedAmount");
						if (selectedAmount.compareTo(BigDecimal.ZERO) > 0) {
							quantity = quantity.multiply(selectedAmount);
						}
					}
					GenericValue objProduct = null;
					try {
						objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
					} catch (GenericEntityException e) {
						return ServiceUtil.returnError("OLBIUS: findOne Product error! " + e.toString());
					}
					
					GenericValue objFacility = null;
					try {
						objFacility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityId));
					} catch (GenericEntityException e) {
						return ServiceUtil.returnError("OLBIUS: findOne Facility error! " + e.toString());
					}
					BigDecimal quantityPicked = quantityPickedOnOrder(delegator, x.get("orderId"), x.get("orderItemSeqId"), null);
					quantity = quantity.subtract(quantityPicked);
					BigDecimal quantityOnHandTotal = quantityOnHandTotal(delegator, ownerPartyId, facilityId, productId, x.get("requireAmount"));
					BigDecimal quantityOnHandTotalInPickFace = quantityOnHandTotalInPickFace(delegator, ownerPartyId, facilityId, productId, objProduct.get("requireAmount"));
					
					String useLocation = "N";
					if (UtilValidate.isNotEmpty(objFacility.get("requireLocation"))) {
						useLocation = objFacility.getString("requireLocation");
					}
					
					BigDecimal picked = BigDecimal.ZERO;
					if (mapProductQuantity.containsKey(productId)){
						picked = (BigDecimal)mapProductQuantity.get(productId);
						if (UtilValidate.isNotEmpty(picked)) {
							if (picked.compareTo(BigDecimal.ZERO) > 0){
								quantityOnHandTotal = quantityOnHandTotal.subtract(picked);
							}
						}
					} 
					
					BigDecimal quantityPickable = BigDecimal.ZERO; 
					if ("Y".equals(useLocation)){
						quantityPickable = quantityOnHandTotalInPickFace.subtract(quantityPicked(delegator, facilityId, productId, x.get("orderId"), x.get("orderItemSeqId"), null));
					} else {
						quantityPickable = quantityOnHandTotal.subtract(quantityPicked(delegator, facilityId, productId, x.get("orderId"), x.get("orderItemSeqId"), null));
					}

					if (quantityPickable.compareTo(quantity) > 0) {
						quantityPickable = quantity;
					}
					if (picked.compareTo(BigDecimal.ZERO) > 0){
						mapProductQuantity.put(productId, quantityPickable.add(picked));
					} else {
						mapProductQuantity.put(productId, quantityPickable);
					}
					
					if (quantity.compareTo(BigDecimal.ZERO) <= 0 || quantityPickable.compareTo(BigDecimal.ZERO) <= 0) {
						dispatcher.runSync("createPickingItemTempData", UtilMisc.toMap("facilityId", facilityId,
								"userLoginId", userLoginId, "orderId", x.get("orderId"), "orderItemSeqId",
								x.get("orderItemSeqId"), "productId", x.get("productId"), "productCode",
								x.get("productCode"), "productName", x.get("productName"), "primaryProductCategoryId",
								x.get("primaryProductCategoryId"), "quantity", quantity, "quantityOnHandTotal",
								quantityOnHandTotal, "quantityPickable", BigDecimal.ZERO, "quantityPicked",
								BigDecimal.ZERO, "quantityInPickFace", quantityOnHandTotalInPickFace, "userLogin", userLogin));
					} else {
					dispatcher.runSync("createPickingItemTempData", UtilMisc.toMap("facilityId", facilityId,
							"userLoginId", userLoginId, "orderId", x.get("orderId"), "orderItemSeqId",
							x.get("orderItemSeqId"), "productId", x.get("productId"), "productCode",
							x.get("productCode"), "productName", x.get("productName"), "primaryProductCategoryId",
							x.get("primaryProductCategoryId"), "quantity", quantity, "quantityOnHandTotal",
							quantityOnHandTotal, "quantityPickable", quantityPickable, "quantityPicked",
							quantityPickable, "quantityInPickFace", quantityOnHandTotalInPickFace, "userLogin", userLogin));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, Object> reBufferIntoPickingItem(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String userLoginId = userLogin.getString("userLoginId");
			String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
			Object picklistId = context.get("picklistId");

			GenericValue picklist = delegator.findOne("Picklist", UtilMisc.toMap("picklistId", picklistId), false);

			dispatcher.runSync("deleteAllPickingItemTempData",
					UtilMisc.toMap("facilityId", picklist.get("facilityId"), "userLogin", userLogin));

			List<GenericValue> picklistItems = delegator.findList("PicklistItemSum3", EntityCondition
					.makeCondition(UtilMisc.toMap("picklistId", picklistId, "itemStatusId", "PICKITEM_PENDING")), null,
					null, null, false);
			String facilityId = null;
			String productId = null;
			for (GenericValue x : picklistItems) {
				facilityId = x.getString("facilityId");
				productId = x.getString("productId");
				GenericValue orderItem = delegator.findOne("OrderItem",
						UtilMisc.toMap("orderId", x.get("orderId"), "orderItemSeqId", x.get("orderItemSeqId")), false);
				BigDecimal quantity = orderItem.getBigDecimal("quantity");
				if ("Y".equals(x.get("requireAmount"))) {
					BigDecimal selectedAmount = orderItem.getBigDecimal("selectedAmount");
					if (selectedAmount.compareTo(BigDecimal.ZERO) > 0) {
						quantity = quantity.multiply(selectedAmount);
					}
				}
				dispatcher.runSync("createPickingItemTempData", UtilMisc.toMap("facilityId", facilityId, "userLoginId",
						userLoginId, "orderId", x.get("orderId"), "orderItemSeqId", x.get("orderItemSeqId"),
						"productId", productId, "productCode", x.get("productCode"), "productName",
						x.get("productName"), "primaryProductCategoryId", x.get("primaryProductCategoryId"),
						"quantity", quantity, "quantityPicked", x.get("quantity"), "picklistBinId",
						x.get("picklistBinId"), "userLogin", userLogin));
				makePickingItemAccurately(delegator, locale, facilityId, productId, ownerPartyId,
						x.get("picklistBinId"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static BigDecimal quantityOnHandTotalInPickFace(Delegator delegator, Object ownerPartyId, Object facilityId, Object productId, Object requireAmount) {
		BigDecimal quantity = BigDecimal.ZERO;
		try {
			List<GenericValue> inventoryItems = delegator.findList("InventoryItemLocationAndInventoryItem", EntityCondition
					.makeCondition(UtilMisc.toMap("ownerPartyId", ownerPartyId, "facilityId", facilityId, "productId", productId, "locationFacilityTypeId", "PICK_FACE")), null, null, null, false);
			for (GenericValue x : inventoryItems) {
				BigDecimal quantityInLoc = x.getBigDecimal("quantity");
				String uomId = x.getString("uomId");
				String baseUomId = x.getString("quantityUomId");
				if (UtilValidate.isNotEmpty(uomId) && UtilValidate.isNotEmpty(baseUomId)) {
					BigDecimal convert = ProductUtil.getConvertPackingNumber(delegator, (String)productId, uomId, baseUomId);
					quantityInLoc = quantityInLoc.multiply(convert);
				}
				quantity = quantity.add(quantityInLoc);
			}
		} catch (Exception e) {
		}
		return quantity;
	}
	
	private static BigDecimal quantityOnHandTotal(Delegator delegator, Object ownerPartyId, Object facilityId,
			Object productId, Object requireAmount) {
		BigDecimal quantity = BigDecimal.ZERO;
		try {
			List<GenericValue> inventoryItems = delegator.findList("InventoryItemTotalDetail", EntityCondition
					.makeCondition(UtilMisc.toMap("ownerPartyId", ownerPartyId, "facilityId", facilityId, "productId",
							productId, "requireAmount", requireAmount)), null, null, null, false);
			for (GenericValue x : inventoryItems) {
				BigDecimal quantityOnHandTotal = x.getBigDecimal("quantityOnHandTotal");
				if ("Y".equals(x.get("requireAmount"))) {
					quantityOnHandTotal = x.getBigDecimal("amountOnHandTotal");
				}
				quantity = quantity.add(quantityOnHandTotal);
			}
		} catch (Exception e) {
		}
		return quantity;
	}

	public static Map<String, Object> transferToPicklistItem(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		EntityListIterator iterator = null;
		String useLocation = "N";
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Object facilityId = context.get("facilityId");
			GenericValue objFacility = null;
			try {
				objFacility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityId));
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError("OLBIUS: findOne Facility error! " + e.toString());
			}
			if (UtilValidate.isNotEmpty(objFacility.get("requireLocation"))) {
				useLocation = objFacility.getString("requireLocation");
			}
					
			Object userLoginId = userLogin.get("userLoginId");
			String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator,
					userLogin.getString("userLoginId"));

			Object picklistId = context.get("picklistId");

			Object partyCreate = context.get("partyCreate");

			// createPicklist
			if (UtilValidate.isEmpty(picklistId)) {
				result = dispatcher.runSync("createPicklist",
						UtilMisc.toMap("facilityId", facilityId, "userLogin", userLogin));
				picklistId = result.get("picklistId");
			} else {
				dispatcher.runSync("deletePicklistItemByPicklist",
						UtilMisc.toMap("picklistId", picklistId, "userLogin", userLogin));
			}
			List<EntityCondition> conditions = FastList.newInstance();
			// createPicklistRole PICKING_CREATOR
			if (UtilValidate.isEmpty(partyCreate)) {
				partyCreate = userLogin.getString("partyId");
			}
			if (UtilValidate.isNotEmpty(partyCreate)) {
				delegator.createOrStore(delegator.makeValidValue("PartyRole",
						UtilMisc.toMap("partyId", partyCreate, "roleTypeId", PICKING_CREATOR)));
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.NOT_EQUAL, partyCreate));
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeId", PICKING_CREATOR,
						"picklistId", picklistId)));
				delegator.storeByCondition("PicklistRole", UtilMisc.toMap("thruDate", UtilDateTime.nowTimestamp()),
						EntityCondition.makeCondition(conditions));
				conditions.clear();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeId", PICKING_CREATOR,
						"picklistId", picklistId, "partyId", partyCreate)));
				List<GenericValue> picklistRoles = delegator.findList("PicklistRole",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				if (UtilValidate.isEmpty(picklistRoles)) {
					dispatcher.runSync("createPicklistRole", UtilMisc.toMap("picklistId", picklistId, "partyId",
							partyCreate, "roleTypeId", PICKING_CREATOR, "userLogin", userLogin));
				}
			}

			conditions.clear();
			conditions.add(EntityCondition.makeCondition("userLoginId", EntityJoinOperator.EQUALS, userLoginId));
			conditions.add(EntityCondition.makeCondition("facilityId", EntityJoinOperator.EQUALS, facilityId));

			List<GenericValue> dummy = delegator.findList("PickingItemTempDataGrouped",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			List<String> orderIds = EntityUtil.getFieldListFromEntityList(dummy, "orderId", true);
			for (String orderId : orderIds) {

				List<GenericValue> orderItemShipGroups = delegator.findList("OrderItemShipGroup",
						EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
				Object shipGroupSeqId = null;
				for (GenericValue x : orderItemShipGroups) {
					if (UtilValidate.isNotEmpty(x.get("shipGroupSeqId"))) {
						shipGroupSeqId = x.get("shipGroupSeqId");
						break;
					}
				}

				conditions.clear();
				conditions.add(EntityCondition.makeCondition("userLoginId", EntityJoinOperator.EQUALS, userLoginId));
				conditions.add(EntityCondition.makeCondition("facilityId", EntityJoinOperator.EQUALS, facilityId));
				conditions.add(EntityCondition.makeCondition("orderId", EntityJoinOperator.EQUALS, orderId));
				dummy = delegator.findList("PickingItemTempDataGrouped", EntityCondition.makeCondition(conditions), null, null, null, false);
				List<String> primaryProductCategoryIds = EntityUtil.getFieldListFromEntityList(dummy, "primaryProductCategoryId", true);
				
				for (String primaryProductCategoryId : primaryProductCategoryIds) {
					try {
						conditions.clear();
						conditions.add(EntityCondition.makeCondition("userLoginId", EntityJoinOperator.EQUALS, userLoginId));
						conditions.add(EntityCondition.makeCondition("facilityId", EntityJoinOperator.EQUALS, facilityId));
						conditions.add(EntityCondition.makeCondition("orderId", EntityJoinOperator.EQUALS, orderId));
						conditions.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityJoinOperator.EQUALS, primaryProductCategoryId));
						conditions.add(EntityCondition.makeCondition("quantityPicked", EntityJoinOperator.GREATER_THAN, BigDecimal.ZERO));
						iterator = delegator.find("PickingItemTempData", EntityCondition.makeCondition(conditions), null, null, null, null);
						GenericValue value = null;
						Object picklistBinId = null;
						int maxOfBin = 15;
						while ((value = iterator.next()) != null) {
							if (UtilValidate.isNotEmpty(value.get("picklistBinId"))) {
								picklistBinId = value.get("picklistBinId");
							}
							if (maxOfBin == 0) {
								picklistBinId = null;
								maxOfBin = 15;
							}
							if (UtilValidate.isEmpty(picklistBinId)) {
								result = dispatcher.runSync("createPicklistBin", UtilMisc.toMap("picklistId",
										picklistId, "binLocationNumber", Long.valueOf(15), "primaryOrderId", orderId,
										"primaryShipGroupSeqId", shipGroupSeqId, "binStatusId", "PICKBIN_INPUT", "holding", "N","holdUserLoginId", userLoginId,
										"userLogin", userLogin));
								picklistBinId = result.get("picklistBinId");
							}

							BigDecimal quantityPicked = value.getBigDecimal("quantityPicked");
							if (UtilValidate.isNotEmpty(useLocation) && "Y".equals(useLocation)) {
								List<Map<String, Object>> listLocations = FastList.newInstance();
								listLocations = InventoryUtil.getInventoryItemLocationToExport(delegator, (String)value.get("productId"), (String)facilityId, quantityPicked, null, (String)picklistBinId);
								if (!listLocations.isEmpty()){
									for (Map<String, Object> x : listLocations) {
										if (quantityPicked.compareTo(BigDecimal.ZERO) <= 0) {
											break;
										}
		
										BigDecimal quantity = (BigDecimal)x.get("quantity");
										if (quantity.compareTo(BigDecimal.ZERO) > 0) {
											String inventoryItemId = (String)x.get("inventoryItemId");
											String locationId = (String)x.get("locationId");
											String orderItemSeqId = value.getString("orderItemSeqId");
											dispatcher.runSync("createPicklistItem", UtilMisc.toMap("picklistBinId", picklistBinId,
													"orderId", orderId, "orderItemSeqId", orderItemSeqId, "shipGroupSeqId",
													shipGroupSeqId, "inventoryItemId", inventoryItemId, "quantity", quantity,
													"userLogin", userLogin));
											Debug.logWarning("OLBIUS: service: transferToPicklistItem - createPicklistItem for picklistBinId= " + picklistBinId + " inventoryItemId= " + inventoryItemId, module);
											
											Map<String, Object> createMap = FastMap.newInstance();
											createMap.put("orderId", orderId);
											createMap.put("orderItemSeqId", orderItemSeqId);
											createMap.put("shipGroupSeqId", shipGroupSeqId);
											createMap.put("inventoryItemId", inventoryItemId);
											createMap.put("quantity", quantity);
											createMap.put("locationId", locationId);
											createMap.put("picklistBinId", picklistBinId);
											createMap.put("userLogin", userLogin);
											try {
												dispatcher.runSync("createPicklistItemLocation", createMap);
											} catch (GenericServiceException e) {
												return ServiceUtil.returnError("OLBIUS: createPicklistItemLocation error! " + e.toString());
											}
										}
										quantityPicked = quantityPicked.subtract(quantity);
									}
								}
							} else {
								String productId = (String)value.get("productId");
								GenericValue objProduct = null;
								try {
									objProduct = delegator.findOne("Product", false,
											UtilMisc.toMap("productId", productId));
								} catch (GenericEntityException e) {
									return ServiceUtil.returnError("OLBIUS: findOne Product error! " + e.toString());
								}
								String requireAmount = objProduct.getString("requireAmount");
								conditions.clear();
								conditions.add(EntityCondition.makeCondition("quantityOnHandTotal",
										EntityJoinOperator.GREATER_THAN, BigDecimal.ZERO));
								conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId,
										"ownerPartyId", ownerPartyId, "productId", productId, "requireAmount", requireAmount)));
								List<String> orderBy = FastList.newInstance();
								
								if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
									orderBy.add("-amountOnHandTotal");
									orderBy.add("expireDate");
								} else {
									orderBy.add("-quantityOnHandTotal");
									orderBy.add("expireDate");
								}
								List<GenericValue> inventoryItems = delegator.findList("InventoryItemAndProduct",
										EntityCondition.makeCondition(conditions), null, orderBy, null, false);
								for (GenericValue x : inventoryItems) {
									if (quantityPicked.compareTo(BigDecimal.ZERO) <= 0) {
										break;
									}

									BigDecimal quantityOnHandTotal = x.getBigDecimal("quantityOnHandTotal");
									if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
										quantityOnHandTotal = x.getBigDecimal("amountOnHandTotal");
									}
									conditions.clear();
									conditions.add(EntityCondition.makeCondition("inventoryItemId",
											EntityJoinOperator.EQUALS, x.get("inventoryItemId")));
									conditions.add(EntityCondition.makeCondition("itemStatusId", EntityJoinOperator.IN,
											UtilMisc.toList("PICKITEM_PENDING", "PICKITEM_APPROVED", "PICKITEM_PICKED", "PICKITEM_CHECKED", "PICKITEM_DLV_CREATED")));
									List<GenericValue> pickingItems = delegator.findList("PicklistItem",
											EntityCondition.makeCondition(conditions), null, null, null, false);
									for (GenericValue p : pickingItems) {
										if (UtilValidate.isNotEmpty(p.get("quantity"))) {
											quantityOnHandTotal = quantityOnHandTotal.subtract(p.getBigDecimal("quantity"));
										}
									}

									BigDecimal quantity = null;
									if (quantityPicked.compareTo(quantityOnHandTotal) > 0) {
										quantity = quantityOnHandTotal;
									} else {
										quantity = quantityPicked;
									}
									if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
										continue;
									}
									String inventoryItemId = x.getString("inventoryItemId");
									String orderItemSeqId = value.getString("orderItemSeqId");
									dispatcher.runSync("createPicklistItem", UtilMisc.toMap("picklistBinId", picklistBinId,
											"orderId", orderId, "orderItemSeqId", orderItemSeqId, "shipGroupSeqId",
											shipGroupSeqId, "inventoryItemId", inventoryItemId, "quantity", quantity,
											"userLogin", userLogin));
									
									Debug.logWarning("OLBIUS: service: transferToPicklistItem - createPicklistItem for picklistBinId= " + picklistBinId + " inventoryItemId= " + inventoryItemId, module);
									quantityPicked = quantityPicked.subtract(quantity);
								}
							}
							maxOfBin--;
						}
					} catch (Exception e) {
						throw e;
					} finally {
						if (iterator != null) {
							iterator.close();
						}
					}
				}
			}
			dispatcher.runSync("deleteAllPickingItemTempData",
					UtilMisc.toMap("facilityId", facilityId, "userLogin", userLogin));
			result.clear();
			result.put("picklistId", picklistId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> loadPicklistInfo(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> info = FastMap.newInstance();
		try {
			Object picklistId = context.get("picklistId");
			GenericValue picklist = delegator.findOne("Picklist", UtilMisc.toMap("picklistId", picklistId), false);
			info.put("facility",
					delegator.findOne("Facility", UtilMisc.toMap("facilityId", picklist.get("facilityId")), false));
			List<GenericValue> picklistItems = delegator.findList("PicklistItemSum2", EntityCondition
					.makeCondition(UtilMisc.toMap("picklistId", picklistId, "itemStatusId", "PICKITEM_PENDING")), null,
					null, null, false);
			info.put("orderIds", EntityUtil.getFieldListFromEntityList(picklistItems, "orderId", true));
			dispatcher.runSync("reBufferIntoPickingItem", context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("info", info);
		return result;
	}

	public static Map<String, Object> cancelPicklist(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			Object picklistId = context.get("picklistId");
			GenericValue picklist = delegator.findOne("Picklist", UtilMisc.toMap("picklistId", picklistId), false);
			if (picklist.get("statusId").equals("PICKLIST_INPUT")) {
				picklist.set("statusId", "PICKLIST_CANCELLED");
				picklist.store();

				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition("picklistId", EntityJoinOperator.EQUALS, picklistId));
				conditions
						.add(EntityCondition.makeCondition("binStatusId", EntityJoinOperator.EQUALS, "PICKBIN_INPUT"));
				delegator.storeByCondition("PicklistBin", UtilMisc.toMap("binStatusId", "PICKBIN_CANCELLED"),
						EntityCondition.makeCondition(conditions));

				dispatcher.runSync("cancelPicklistAndItems", context);
			} else {
				throw new Exception("picklist statusId not equal PICKLIST_INPUT");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, Object> approvePicklist(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Object picklistId = context.get("picklistId");
			GenericValue picklist = delegator.findOne("Picklist", UtilMisc.toMap("picklistId", picklistId), false);
			if (picklist.get("statusId").equals("PICKLIST_INPUT")) {
				picklist.set("statusId", "PICKLIST_PICKED");
				picklist.store();

				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition("picklistId", EntityJoinOperator.EQUALS, picklistId));
				conditions
						.add(EntityCondition.makeCondition("binStatusId", EntityJoinOperator.EQUALS, "PICKBIN_INPUT"));

				List<GenericValue> dummy = delegator.findList("PicklistBin", EntityCondition.makeCondition(conditions),
						null, null, null, false);
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				LocalDispatcher dispatcher = ctx.getDispatcher();
				for (GenericValue bin : dummy) {
					Map<String, Object> map = FastMap.newInstance();
					map.put("userLogin", userLogin);
					map.put("picklistBinId", bin.getString("picklistBinId"));
					map.put("statusId", "PICKBIN_APPROVED");
					try {
						dispatcher.runSync("changePicklistBinStatus", map);
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: changePicklistBinStatus error! " + e.toString());
					}
				}
			} else {
				throw new Exception("picklist statusId not equal PICKLIST_INPUT");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, Object> completePicklist(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Object picklistId = context.get("picklistId");
			GenericValue picklist = delegator.findOne("Picklist", UtilMisc.toMap("picklistId", picklistId), false);
			if (picklist.get("statusId").equals("PICKLIST_PICKED")) {
				picklist.set("statusId", "PICKLIST_COMPLETED");
				picklist.store();

				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition("picklistId", EntityJoinOperator.EQUALS, picklistId));
				conditions.add(EntityCondition.makeCondition("binStatusId", EntityJoinOperator.EQUALS,
						"PICKBIN_DLV_CREATED"));
				List<GenericValue> dummy = delegator.findList("PicklistBin", EntityCondition.makeCondition(conditions),
						null, null, null, false);
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				LocalDispatcher dispatcher = ctx.getDispatcher();
				for (GenericValue bin : dummy) {
					Map<String, Object> map = FastMap.newInstance();
					map.put("userLogin", userLogin);
					map.put("picklistBinId", bin.getString("picklistBinId"));
					map.put("statusId", "PICKBIN_COMPLETED");
					try {
						dispatcher.runSync("changePicklistBinStatus", map);
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: changePicklistBinStatus error! " + e.toString());
					}
				}
			} else {
				throw new Exception("picklist statusId not equal PICKLIST_PICKED");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, Object> deletePicklistItemByPicklist(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Object picklistId = context.get("picklistId");
			List<GenericValue> picklistBins = delegator.findList("PicklistBin",
					EntityCondition.makeCondition("picklistId", EntityJoinOperator.EQUALS, picklistId), null, null,
					null, false);
			for (GenericValue x : picklistBins) {
				delegator.removeByCondition(
						"PicklistItem",
						EntityCondition.makeCondition("picklistBinId", EntityJoinOperator.EQUALS,
								x.get("picklistBinId")));
//				x.remove();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, Object> changePicklistBinStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		String picklistBinId = (String) context.get("picklistBinId");
		String statusId = (String) context.get("statusId");
		String oldStatusId = null;
		try {
			GenericValue picklistBin = delegator.findOne("PicklistBin", false,
					UtilMisc.toMap("picklistBinId", picklistBinId));
			if (UtilValidate.isNotEmpty(picklistBin)) {
				oldStatusId = picklistBin.getString("statusId");
				picklistBin.put("binStatusId", statusId);
				picklistBin.put("holding", "N");
				delegator.store(picklistBin);
			}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: findOne picklistBin error! " + e.toString());
		}
		if (UtilValidate.isNotEmpty(context.get("setItemStatus"))) {
			String setItemStatus = (String) context.get("setItemStatus");
			if (UtilValidate.isNotEmpty(setItemStatus) && "Y".equals(setItemStatus)) {
				EntityCondition cond1 = EntityCondition.makeCondition("picklistBinId", EntityOperator.EQUALS,
						picklistBinId);
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(cond1);
				String itemStatusId = null;
				if ("PICKBIN_PICKED".equals(statusId)) {
					itemStatusId = "PICKITEM_PICKED";
					EntityCondition cond2 = EntityCondition.makeCondition("itemStatusId", EntityOperator.EQUALS, "PICKITEM_PENDING");
					conds.add(cond2);
				} else if ("PICKBIN_CHECKED".equals(statusId)) {
					itemStatusId = "PICKITEM_CHECKED";
					EntityCondition cond2 = EntityCondition.makeCondition("itemStatusId", EntityOperator.EQUALS, "PICKITEM_PICKED");
					conds.add(cond2);
				} else if ("PICKBIN_APPROVED".equals(statusId)) {
					itemStatusId = "PICKITEM_APPROVED";
					// allow approved when do not update to pick, check
					EntityCondition cond2 = EntityCondition.makeCondition("itemStatusId", EntityOperator.IN, UtilMisc.toList("PICKITEM_PENDING", "PICKITEM_PICKED", "PICKITEM_CHECKED"));
					conds.add(cond2);
				} else if ("PICKBIN_DLV_CREATED".equals(statusId)) {
					itemStatusId = "PICKITEM_DLV_CREATED";
					// allow approved when do not update to pick, check
					EntityCondition cond2 = EntityCondition.makeCondition("itemStatusId", EntityOperator.EQUALS, "PICKITEM_APPROVED");
					conds.add(cond2);
				} else if ("PICKBIN_CANCELLED".equals(statusId)) {
					itemStatusId = "PICKITEM_CANCELLED";
					EntityCondition cond2 = EntityCondition.makeCondition("itemStatusId", EntityOperator.IN, UtilMisc.toList("PICKITEM_PENDING", "PICKITEM_APPROVED", "PICKITEM_PICKED", "PICKITEM_CHECKED", "PICKITEM_DLV_CREATED"));
					conds.add(cond2);
				} else if ("PICKBIN_COMPLETED".equals(statusId)) {
					itemStatusId = "PICKITEM_COMPLETED";
					EntityCondition cond2 = EntityCondition.makeCondition("itemStatusId", EntityOperator.EQUALS, "PICKITEM_DLV_CREATED");
					conds.add(cond2);
				}

				List<GenericValue> listPicklistItems = FastList.newInstance();
				try {
					listPicklistItems = delegator.findList("PicklistItem", EntityCondition.makeCondition(conds), null,
							null, null, false);
				} catch (GenericEntityException e) {
					e.printStackTrace();
					return ServiceUtil.returnError("OLBIUS: findList PicklistItem error! " + e.toString());
				}

				if (!listPicklistItems.isEmpty() && UtilValidate.isNotEmpty(itemStatusId)) {
					LocalDispatcher dispatcher = ctx.getDispatcher();
					GenericValue userLogin = (GenericValue) context.get("userLogin");
					for (GenericValue item : listPicklistItems) {
						Map<String, Object> map = FastMap.newInstance();
						map.put("userLogin", userLogin);
						map.put("picklistBinId", picklistBinId);
						map.put("orderId", item.getString("orderId"));
						map.put("orderItemSeqId", item.getString("orderItemSeqId"));
						map.put("shipGroupSeqId", item.getString("shipGroupSeqId"));
						map.put("inventoryItemId", item.getString("inventoryItemId"));
						map.put("statusId", itemStatusId);
						try {
							dispatcher.runSync("changePicklistItemStatus", map);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError("OLBIUS: changePicklistItemStatus error! " + e.toString());
						}
					}
				}
			}
		}

		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		successResult.put("oldStatusId", oldStatusId);
		successResult.put("picklistBinId", picklistBinId);
		successResult.put("statusId", statusId);
		return successResult;
	}
	
	public static Map<String, Object> deleteListPicklistBinStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String listPicklistBinStr = (String) context.get("listPicklistBin");
		String statusId = "PICKBIN_CANCELLED";
		
		if (listPicklistBinStr != null) {
			JSONArray jsonArray = new JSONArray();
			if (UtilValidate.isNotEmpty(listPicklistBinStr)) {
				jsonArray = JSONArray.fromObject(listPicklistBinStr);
			}
			if (jsonArray != null && jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonObj = JSONObject.fromObject(jsonArray.get(i));
					String picklistBinId = jsonObj.getString("picklistBinId");
					String binStatusId = jsonObj.getString("binStatusId");
					
					if (!binStatusId.equals("PICKBIN_COMPLETED") && !binStatusId.equals("PICKBIN_CANCELLED")) {
						Map<String, Object> map = FastMap.newInstance();
						map.put("picklistBinId", picklistBinId);
						map.put("statusId", statusId);
						map.put("userLogin", userLogin);
						
						try {
							dispatcher.runSync("changePicklistBinStatus", map);
						} catch (GenericServiceException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		return successResult;
	}
	
	public static Map<String, Object> approveListPicklistBinStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String listPicklistBinStr = (String) context.get("listPicklistBin");
		String statusId = "PICKBIN_APPROVED";
		
		if (listPicklistBinStr != null) {
			JSONArray jsonArray = new JSONArray();
			if (UtilValidate.isNotEmpty(listPicklistBinStr)) {
				jsonArray = JSONArray.fromObject(listPicklistBinStr);
			}
			if (jsonArray != null && jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonObj = JSONObject.fromObject(jsonArray.get(i));
					String picklistBinId = jsonObj.getString("picklistBinId");
					String binStatusId = jsonObj.getString("binStatusId");
					String partyPickId = jsonObj.getString("partyPickId");
					String partyCheckId = jsonObj.getString("partyCheckId");
					
					if (binStatusId.equals("PICKBIN_INPUT") || binStatusId.equals("PICKBIN_PICKED") || binStatusId.equals("PICKBIN_CHECKED")) {
						if (!partyPickId.equals("null") && !partyCheckId.equals("null")) {
							Map<String, Object> map = FastMap.newInstance();
							map.put("picklistBinId", picklistBinId);
							map.put("statusId", statusId);
							map.put("userLogin", userLogin);
							
							try {
								dispatcher.runSync("changePicklistBinStatus", map);
							} catch (GenericServiceException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		
		return successResult;
	}

	public static Map<String, Object> changePicklistItemStatus(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		String picklistBinId = (String) context.get("picklistBinId");
		String orderId = (String) context.get("orderId");
		String orderItemSeqId = (String) context.get("orderItemSeqId");
		String shipGroupSeqId = (String) context.get("shipGroupSeqId");
		String inventoryItemId = (String) context.get("inventoryItemId");

		String statusId = (String) context.get("statusId");
		String oldStatusId = null;
		try {
			GenericValue picklistItem = delegator.findOne("PicklistItem", false, UtilMisc.toMap("picklistBinId",
					picklistBinId, "orderId", orderId, "orderItemSeqId", orderItemSeqId, "shipGroupSeqId",
					shipGroupSeqId, "inventoryItemId", inventoryItemId));
			if (UtilValidate.isNotEmpty(inventoryItemId)) {
				oldStatusId = picklistItem.getString("itemStatusId");
				picklistItem.put("itemStatusId", statusId);
				delegator.store(picklistItem);
			}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: findOne picklistItem error! " + e.toString());
		}

		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		successResult.put("oldStatusId", oldStatusId);
		successResult.put("picklistBinId", picklistBinId);
		successResult.put("orderId", orderId);
		successResult.put("orderItemSeqId", orderItemSeqId);
		successResult.put("shipGroupSeqId", shipGroupSeqId);
		successResult.put("inventoryItemId", inventoryItemId);
		successResult.put("statusId", statusId);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jQGetListPicklistBins(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		List<GenericValue> list = new ArrayList<GenericValue>();
		try {
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("-picklistBinId");
			}
			listIterator = delegator.find("PicklistBin",
					EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null,
					listSortFields, opts);
			list = LogisticsUtil.getIteratorPartialList(listIterator, parameters, successResult);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jQGetListFormulaProducts service: " + e.toString();
			return ServiceUtil.returnError("OLBIUS: " + errMsg);
		}
		successResult.put("listIterator", list);
		return successResult;
	}

	public static Map<String, Object> checkPicklistStatus(DispatchContext ctx, Map<String, ?> context)
			throws GenericEntityException, GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> result = new FastMap<String, Object>();
		String picklistBinId = (String) context.get("picklistBinId");
		GenericValue picklistBin = delegator.findOne("PicklistBin", false,
				UtilMisc.toMap("picklistBinId", picklistBinId));
		String picklistId = picklistBin.getString("picklistId");

		Boolean allPicked = true;
		Boolean allChecked = true;
		Boolean allApproved = true;
		Boolean allDlvCreated = true;
		Boolean allCompleted = true;
		Boolean allCancelled = true;

		Boolean hasChecked = false;
		Boolean hasPicked = false;
		Boolean hasApproved = false;
		Boolean hasDlvCreated = false;

		String newPicklistStatusId = null;
		if (picklistId != null) {
			List<GenericValue> listPicklistBins = delegator.findList("PicklistBin",
					EntityCondition.makeCondition("picklistId", picklistId), null, null, null, false);
			if (!listPicklistBins.isEmpty()) {
				for (GenericValue bin : listPicklistBins) {
					if (!"PICKBIN_PICKED".equals(bin.getString("binStatusId"))) {
						allPicked = false;
						if (!"PICKBIN_CHECKED".equals(bin.getString("binStatusId"))) {
							allChecked = false;
							if (!"PICKBIN_APPROVED".equals(bin.getString("binStatusId"))) {
								allApproved = false;
								if (!"PICKBIN_DLV_CREATED".equals(bin.getString("binStatusId"))) {
									allDlvCreated = false;
									if (!"PICKBIN_COMPLETED".equals(bin.getString("binStatusId"))) {
										allCompleted = false;
										if (!"PICKBIN_CANCELLED".equals(bin.getString("binStatusId"))) {
											allCancelled = false;
											break;
										}
									}
								} else {
									hasDlvCreated = true;
								}
							} else {
								hasApproved = true;
							}
						} else {
							hasChecked = true;
						}
					} else {
						hasPicked = true;
					}
				}
			}
			if (allPicked || hasPicked) {
				newPicklistStatusId = "PICKLIST_PICKED";
			} else if (allChecked || hasChecked) {
				newPicklistStatusId = "PICKLIST_CHECKED";
			} else if (allApproved || hasApproved) {
				newPicklistStatusId = "PICKLIST_APPROVED";
			} else if (allDlvCreated || hasDlvCreated) {
				newPicklistStatusId = "PICKLIST_DLV_CREATED";
			} else if (allCompleted) {
				newPicklistStatusId = "PICKLIST_COMPLETED";
			} else if (allCancelled) {
				newPicklistStatusId = "PICKLIST_CANCELLED";
			}

			if (newPicklistStatusId != null) {
				GenericValue picklist = delegator.findOne("Picklist", false, UtilMisc.toMap("picklistId", picklistId));
				String statusId = picklist.getString("statusId");
				if (!statusId.equals(newPicklistStatusId)) {
					Map<String, Object> map = FastMap.newInstance();
					map.put("userLogin", (GenericValue) context.get("userLogin"));
					map.put("picklistId", picklistId);
					map.put("statusId", newPicklistStatusId);
					map.put("setBinStatus", "N");
					dispatcher.runSync("changePicklistStatus", map);
				}
			}
		}
		return result;
	}

	public static Map<String, Object> changePicklistStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		String picklistId = (String) context.get("picklistId");
		String statusId = (String) context.get("statusId");
		String oldStatusId = null;
		try {
			GenericValue picklist = delegator.findOne("Picklist", false, UtilMisc.toMap("picklistId", picklistId));
			if (UtilValidate.isNotEmpty(picklist)) {
				oldStatusId = picklist.getString("statusId");
				picklist.put("statusId", statusId);
				delegator.store(picklist);
			}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: findOne picklist error! " + e.toString());
		}
		if (UtilValidate.isNotEmpty(context.get("setBinStatus"))) {
			String setBinStatus = (String) context.get("setBinStatus");
			if ("Y".equals(setBinStatus)) {
				EntityCondition cond1 = EntityCondition.makeCondition("picklistId", EntityOperator.EQUALS, picklistId);
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(cond1);
				String itemStatusId = null;
				if ("PICKLIST_PICKED".equals(statusId)) {
					itemStatusId = "PICKBIN_PICKED";
					EntityCondition cond2 = EntityCondition.makeCondition("binStatusId", EntityOperator.EQUALS, "PICKBIN_INPUT");
					conds.add(cond2);
				} else if ("PICKLIST_CHECKED".equals(statusId)) {
					itemStatusId = "PICKBIN_CHECKED";
					EntityCondition cond2 = EntityCondition.makeCondition("binStatusId", EntityOperator.EQUALS, "PICKBIN_PICKED");
					conds.add(cond2);
				} else if ("PICKLIST_APPROVED".equals(statusId)) {
					itemStatusId = "PICKBIN_APPROVED";
					EntityCondition cond2 = EntityCondition.makeCondition("binStatusId", EntityOperator.EQUALS, "PICKBIN_CHECKED");
					conds.add(cond2);
				} else if ("PICKLIST_DLV_CREATED".equals(statusId)) {
					itemStatusId = "PICKBIN_DLV_CREATED";
					EntityCondition cond2 = EntityCondition.makeCondition("binStatusId", EntityOperator.EQUALS, "PICKBIN_APROVED");
					conds.add(cond2);
				} else if ("PICKLIST_CANCELLED".equals(statusId)) {
					itemStatusId = "PICKBIN_CANCELLED";
					EntityCondition cond2 = EntityCondition.makeCondition("binStatusId", EntityOperator.IN,
							UtilMisc.toList("PICKITEM_INPUT", "PICKITEM_APPROVED"));
					conds.add(cond2);
				} else if ("PICKLIST_COMPLETED".equals(statusId)) {
					itemStatusId = "PICKBIN_COMPLETED";
					EntityCondition cond2 = EntityCondition.makeCondition("binStatusId", EntityOperator.EQUALS,
							"PICKITEM_APPROVED");
					conds.add(cond2);
				}

				List<GenericValue> listPicklistBins = FastList.newInstance();
				try {
					listPicklistBins = delegator.findList("PicklistBin", EntityCondition.makeCondition(conds), null,
							null, null, false);
				} catch (GenericEntityException e) {
					e.printStackTrace();
					return ServiceUtil.returnError("OLBIUS: findList PicklistBin error! " + e.toString());
				}

				if (!listPicklistBins.isEmpty() && UtilValidate.isNotEmpty(itemStatusId)) {
					LocalDispatcher dispatcher = ctx.getDispatcher();
					GenericValue userLogin = (GenericValue) context.get("userLogin");
					for (GenericValue bin : listPicklistBins) {
						Map<String, Object> map = FastMap.newInstance();
						map.put("userLogin", userLogin);
						map.put("picklistBinId", bin.getString("pickListBinId"));
						map.put("statusId", itemStatusId);
						try {
							dispatcher.runSync("changePicklistBinStatus", map);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError("OLBIUS: changePicklistItemStatus error! " + e.toString());
						}
					}
				}
			}
		}

		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		successResult.put("oldStatusId", oldStatusId);
		successResult.put("picklistId", picklistId);
		successResult.put("statusId", statusId);
		return successResult;
	}

	public static Map<String, Object> assignPartyToPicklistBin(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Object partyId = context.get("partyId");
			Object picklistBinId = context.get("picklistBinId");
			Object roleTypeId = context.get("roleTypeId");

			delegator.createOrStore(delegator.makeValidValue("PartyRole",
					UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId)));

			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.NOT_EQUAL, partyId));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeId", roleTypeId, "picklistBinId",
					picklistBinId)));
			delegator.storeByCondition(
					"PicklistBinRole",
					UtilMisc.toMap("thruDate", UtilDateTime.nowTimestamp(), "lastModifiedByUserLogin",
							userLogin.get("userLoginId")), EntityCondition.makeCondition(conditions));

			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeId", roleTypeId, "picklistBinId",
					picklistBinId, "partyId", partyId)));
			List<GenericValue> picklistRoles = delegator.findList("PicklistBinRole",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			if (UtilValidate.isEmpty(picklistRoles)) {
				delegator.create("PicklistBinRole", UtilMisc.toMap("picklistBinId", picklistBinId, "partyId", partyId,
						"roleTypeId", roleTypeId, "fromDate", UtilDateTime.nowTimestamp(), "createdByUserLogin",
						userLogin.get("userLoginId")));
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	public static Map<String, Object> updatePicklistBinTotal(DispatchContext dpx, Map<String, ? extends Object> context) throws GenericEntityException {
		Delegator delegator = dpx.getDelegator();
		String picklistBinId = (String) context.get("picklistBinId");
		String statusId = null;
		if (UtilValidate.isNotEmpty(context.get("binStatusId"))) {
			statusId = (String) context.get("binStatusId");
		}
		
		Map<String, Object> result = FastMap.newInstance();
		
		GenericValue objPicklistBin = delegator.findOne("PicklistBin", false, UtilMisc.toMap("picklistBinId", picklistBinId));
		if (UtilValidate.isNotEmpty(objPicklistBin)) {
			String orderId = objPicklistBin.getString("primaryOrderId");
			String binStatusId = objPicklistBin.getString("binStatusId");
			
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String partyId = userLogin.getString("partyId");
			LocalDispatcher dispatcher = dpx.getDispatcher();
			if (UtilValidate.isNotEmpty(orderId)) {
				String listProductStrs = (String) context.get("listProducts");
				JSONArray listProducts = JSONArray.fromObject(listProductStrs);
				String picklistId = objPicklistBin.getString("picklistId");
				GenericValue objPicklist = delegator.findOne("Picklist", false, UtilMisc.toMap("picklistId", picklistId));
				if (UtilValidate.isNotEmpty(objPicklist)) {
					EntityCondition cond1 = EntityCondition.makeCondition("orderId", orderId);
					EntityCondition cond2 = EntityCondition.makeCondition("picklistBinId", EntityOperator.EQUALS, picklistBinId);
					EntityCondition cond4 = EntityCondition.makeCondition("itemStatusId", EntityOperator.IN, UtilMisc.toList("PICKITEM_PENDING", "PICKITEM_APPROVED", "PICKITEM_PICKED", "PICKITEM_CHECKED", "PICKITEM_DLV_CREATED"));
					List<EntityCondition> conds = FastList.newInstance();
					conds.add(cond1);
					conds.add(cond2);
					conds.add(cond4);
					List<String> listOrderItemSeqs = FastList.newInstance();
					String itemStatusId = null;
					List<GenericValue> listPicklistItems = delegator.findList("PicklistItemAndOrderItem", EntityCondition.makeCondition(conds), null, null, null, false);
					if (!listPicklistItems.isEmpty()){
						itemStatusId = listPicklistItems.get(0).getString("itemStatusId");
						listOrderItemSeqs = EntityUtil.getFieldListFromEntityList(listPicklistItems, "orderItemSeqId", true);
					}
					
					List<EntityCondition> conditions = FastList.newInstance();
					conditions.add(EntityCondition.makeCondition("picklistBinId", EntityJoinOperator.EQUALS, picklistBinId));
					conditions.add(EntityCondition.makeCondition("orderId", EntityJoinOperator.EQUALS, orderId));
					delegator.removeByCondition("PicklistItemLocation", EntityCondition.makeCondition(conditions));
					delegator.removeByCondition("PicklistItem", EntityCondition.makeCondition(conditions));
					
					for (int i = 0; i < listProducts.size(); i++) {
						JSONObject item = listProducts.getJSONObject(i);
						String productId = (String) item.get("productId");
						String quantityStr = (String) item.get("quantity");
						String locationId = null;
						if (UtilValidate.isNotEmpty(item.get("locationId"))) {
							locationId = (String) item.get("locationId");
						}
						BigDecimal quantity = BigDecimal.ZERO;
						if (UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(quantityStr)) {
							quantity =  new BigDecimal(quantityStr); 
							if (quantity.compareTo(BigDecimal.ZERO) >= 0){
								if (!listOrderItemSeqs.isEmpty()){
									for (String orderItemSeqId : listOrderItemSeqs) {
										GenericValue objOrderItem = null;
										try {
											objOrderItem = delegator.findOne("OrderItem", false,
													UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
										} catch (GenericEntityException e) {
											return ServiceUtil.returnError("OLBIUS: findOne OrderItem error! " + e.toString());
										}
										if (UtilValidate.isNotEmpty(objOrderItem)) {
											if (productId.equals(objOrderItem.getString("productId"))){
												Map<String, Object> map = FastMap.newInstance();
												map.put("userLogin", userLogin);
												map.put("picklistBinId", picklistBinId);
												map.put("orderId", orderId);
												map.put("orderItemSeqId", orderItemSeqId);
												map.put("picklistId", picklistId);
												map.put("facilityId", objPicklist.getString("facilityId"));
												map.put("productId", productId);
												map.put("quantity", quantity);
												map.put("itemStatusId", itemStatusId);
												map.put("locationId", locationId);
												map.put("removeOld", "N");
												try {
													dispatcher.runSync("updatePicklistItemSum", map); 
												} catch (GenericServiceException e) {
													return ServiceUtil.returnError("OLBIUS: updatePicklistItemSum error! " + e.toString());
												}
											}
										}
									}
								}
							}
						}
					}
					if (UtilValidate.isNotEmpty(statusId) && !binStatusId.equals(statusId)) {
						Map<String, Object> map = FastMap.newInstance();
						map.put("userLogin", userLogin);
						map.put("picklistBinId", picklistBinId);
						map.put("statusId", statusId);
						try {
							dispatcher.runSync("changePicklistBinStatus", map);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError("OLBIUS: changePicklistBinStatus error! " + e.toString());
						}
						if ("PICKBIN_PICKED".equals(statusId) || "PICKBIN_CHECKED".equals(statusId)){
							Map<String, Object> mapTmp = FastMap.newInstance();
							mapTmp.put("picklistBinId", picklistBinId);
							mapTmp.put("userLogin", userLogin);
							mapTmp.put("partyId", partyId);
							if ("PICKBIN_PICKED".equals(statusId)){
								mapTmp.put("roleTypeId", "PICKING_PICKER");
							} else if ("PICKBIN_CHECKED".equals(statusId)){
								mapTmp.put("roleTypeId", "PICKING_CHECKER");
							}
							try {
								dispatcher.runSync("assignPartyToPicklistBin", mapTmp);
							} catch (GenericServiceException e) {
								return ServiceUtil.returnError("OLBIUS: assignPartyToPicklistBin error! " + e.toString());
							}
						}
					}
				}
			}
		}
		return result;
	}

	public static Map<String, Object> updatePicklistItemSum(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String useLocation = "N";
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Locale locale = (Locale) context.get("locale");
			Object picklistBinId = context.get("picklistBinId");
			Object facilityId = context.get("facilityId");
			Object orderId = context.get("orderId");
			Object orderItemSeqId = context.get("orderItemSeqId");
			Object productId = context.get("productId");
			Object itemStatusId = context.get("itemStatusId");
			BigDecimal quantityPicked = (BigDecimal) context.get("quantity");
			GenericValue objFacility = null;
			try {
				objFacility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityId));
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError("OLBIUS: findOne Facility error! " + e.toString());
			}
			
			if (UtilValidate.isNotEmpty(objFacility.get("requireLocation"))) {
				useLocation = objFacility.getString("requireLocation");
			}
			
			GenericValue objProduct = null;
			try {
				objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError("OLBIUS: findOne Product error! " + e.toString());
			}
			String requireAmount = objProduct.getString("requireAmount");
			String locationIdTmp = null;
			if (UtilValidate.isNotEmpty(context.get("locationId"))) {
				locationIdTmp = (String)context.get("locationId");
			}
			
			GenericValue orderItem = delegator.findOne("OrderItem",
					UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId), false);
			BigDecimal quantityOrdered = orderItem.getBigDecimal("quantity");
			if (UtilValidate.isNotEmpty(orderItem.get("cancelQuantity"))) {
				quantityOrdered = quantityOrdered.subtract(orderItem.getBigDecimal("cancelQuantity"));
			}
			if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount) && UtilValidate.isNotEmpty(orderItem.get("selectedAmount"))) {
				quantityOrdered = quantityOrdered.multiply(orderItem.getBigDecimal("selectedAmount"));
			}
			if (quantityPicked.compareTo(quantityOrdered) > 0) {
				throw new Exception(UtilProperties.getMessage(resource, "DmsLuongSoanLonHonLuongDatHang", locale));
			}
			
			List<EntityCondition> conditions = FastList.newInstance();
			
			String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator,
					userLogin.getString("userLoginId"));
			
			List<GenericValue> orderItemShipGroups = delegator.findList("OrderItemShipGroup",
					EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
			Object shipGroupSeqId = null;
			for (GenericValue x : orderItemShipGroups) {
				if (UtilValidate.isNotEmpty(x.get("shipGroupSeqId"))) {
					shipGroupSeqId = x.get("shipGroupSeqId");
					break;
				}
			}
			Boolean remove = false;
			if (UtilValidate.isNotEmpty(context.get("removeOld"))) {
				String removeOld = (String)context.get("removeOld");
				if ("Y".equals(removeOld)) remove = true;
			}
			if (remove){
				conditions.clear();
				conditions.add(EntityCondition.makeCondition("picklistBinId", EntityJoinOperator.EQUALS, picklistBinId));
				conditions.add(EntityCondition.makeCondition("orderId", EntityJoinOperator.EQUALS, orderId));
				conditions.add(EntityCondition.makeCondition("orderItemSeqId", EntityJoinOperator.EQUALS, orderItemSeqId));
				delegator.removeByCondition("PicklistItemLocation", EntityCondition.makeCondition(conditions));
				delegator.removeByCondition("PicklistItem", EntityCondition.makeCondition(conditions));
			}
			
			if (UtilValidate.isNotEmpty(useLocation) && "Y".equals(useLocation)) {
				List<Map<String, Object>> listLocations = FastList.newInstance();
				if (UtilValidate.isNotEmpty(locationIdTmp)) {
					listLocations = InventoryUtil.getInventoryItemLocationToExport(delegator, (String)productId, (String)facilityId, quantityPicked, locationIdTmp, (String)picklistBinId);
					if (listLocations.isEmpty()){ 
						throw new Exception(UtilProperties.getMessage(resource, "LocationNotTrue", locale));
					}
				} else {
					listLocations = InventoryUtil.getInventoryItemLocationToExport(delegator, (String)productId, (String)facilityId, quantityPicked, null, (String)picklistBinId);
				}
				if (!listLocations.isEmpty()){
					for (Map<String, Object> x : listLocations) {
						if (quantityPicked.compareTo(BigDecimal.ZERO) <= 0) {
							break;
						}

						BigDecimal quantity = (BigDecimal)x.get("quantity");
						if (quantity.compareTo(BigDecimal.ZERO) > 0) {
							BigDecimal quantityToPick = BigDecimal.ZERO;
									
							if (quantity.compareTo(quantityPicked) >= 0){
								quantityToPick = quantityPicked;
								quantityPicked = BigDecimal.ZERO;
							} else {
								quantityToPick = quantity;
								quantityPicked = quantityPicked.subtract(quantity);
							}
							String inventoryItemId = (String)x.get("inventoryItemId");
							String locationId = (String)x.get("locationId");
							dispatcher.runSync("createPicklistItem", UtilMisc.toMap("picklistBinId", picklistBinId,
									"orderId", orderId, "orderItemSeqId", orderItemSeqId, "shipGroupSeqId", 
									shipGroupSeqId, "inventoryItemId", inventoryItemId, "quantity", quantityToPick, "itemStatusId", itemStatusId,
									"userLogin", userLogin));
							
							Debug.logWarning("OLBIUS: service: updatePicklistItemSum - createPicklistItem for picklistBinId= " + picklistBinId + " inventoryItemId= " + inventoryItemId, module);
							
							Map<String, Object> createMap = FastMap.newInstance();
							createMap.put("orderId", orderId);
							createMap.put("orderItemSeqId", orderItemSeqId);
							createMap.put("shipGroupSeqId", shipGroupSeqId);
							createMap.put("inventoryItemId", inventoryItemId);
							createMap.put("quantity", quantityToPick);
							createMap.put("locationId", locationId);
							createMap.put("picklistBinId", picklistBinId);
							createMap.put("userLogin", userLogin);
							try {
								dispatcher.runSync("createPicklistItemLocation", createMap);
							} catch (GenericServiceException e) {
								return ServiceUtil.returnError("OLBIUS: createPicklistItemLocation error! " + e.toString());
							}
						}
					}
				}
			} else {
				conditions.clear();
				conditions.add(EntityCondition.makeCondition("picklistBinId", EntityJoinOperator.EQUALS, picklistBinId));
				conditions.add(EntityCondition.makeCondition("orderId", EntityJoinOperator.EQUALS, orderId));
				conditions.add(EntityCondition.makeCondition("orderItemSeqId", EntityJoinOperator.EQUALS, orderItemSeqId));
				List<GenericValue> picklistItems = delegator.findList("PicklistItem", EntityCondition.makeCondition(conditions), null, null, null, false);
				for (GenericValue x : picklistItems) {
					if ("PICKITEM_PENDING".equals(x.get("itemStatusId")) || "PICKITEM_APPROVED".equals(x.get("itemStatusId")) || "PICKITEM_PICKED".equals(x.get("itemStatusId")) || "PICKITEM_CHECKED".equals(x.get("itemStatusId")) || "PICKITEM_DLV_CREATED".equals(x.get("itemStatusId"))) {
						continue;
					}
					throw new Exception(UtilProperties.getMessage("WebtoolsUiLabels", "WebtoolsStatusInvalid", locale));
				}
				delegator.removeByCondition("PicklistItem", EntityCondition.makeCondition(conditions));
				
				conditions.clear();
				conditions.add(EntityCondition.makeCondition("quantityOnHandTotal",
						EntityJoinOperator.GREATER_THAN, BigDecimal.ZERO));
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "ownerPartyId", ownerPartyId, "productId", (String)productId, "requireAmount", requireAmount)));
				List<GenericValue> inventoryItems = delegator.findList("InventoryItemAndProduct",
						EntityCondition.makeCondition(conditions), null, UtilMisc.toList("expireDate"), null, false);
				for (GenericValue x : inventoryItems) {
					if (quantityPicked.compareTo(BigDecimal.ZERO) <= 0) {
						break;
					}
					BigDecimal quantityOnHandTotal = x.getBigDecimal("quantityOnHandTotal");
					
					if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
						quantityOnHandTotal = x.getBigDecimal("amountOnHandTotal");
					}
					conditions.clear();
					conditions.add(EntityCondition.makeCondition("picklistBinId", EntityJoinOperator.NOT_EQUAL, picklistBinId));
					conditions.add(EntityCondition.makeCondition("inventoryItemId", EntityJoinOperator.EQUALS, x.get("inventoryItemId")));
					conditions.add(EntityCondition.makeCondition("itemStatusId", EntityJoinOperator.IN, UtilMisc.toList("PICKITEM_PENDING", "PICKITEM_APPROVED", "PICKITEM_PICKED", "PICKITEM_CHECKED", "PICKITEM_DLV_CREATED")));
					List<GenericValue> pickingItems = delegator.findList("PicklistItem", EntityCondition.makeCondition(conditions), null, null, null, false);
					for (GenericValue p : pickingItems) {
						if (UtilValidate.isNotEmpty(p.get("quantity"))) {
							quantityOnHandTotal = quantityOnHandTotal.subtract(p.getBigDecimal("quantity"));
						}
					}

					BigDecimal quantity = null;
					if (quantityPicked.compareTo(quantityOnHandTotal) > 0) {
						quantity = quantityOnHandTotal;
					} else {
						quantity = quantityPicked;
					}
					if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
						continue;
					}
					String inventoryItemId = x.getString("inventoryItemId");
					GenericValue objPicklistItem = null;
					try {
						objPicklistItem = delegator.findOne("PicklistItem", false,
								UtilMisc.toMap("picklistBinId", picklistBinId, "orderId", orderId, "orderItemSeqId", orderItemSeqId, "shipGroupSeqId", shipGroupSeqId, "inventoryItemId", inventoryItemId));
					} catch (GenericEntityException e) {
						return ServiceUtil.returnError("OLBIUS: findOne PicklistItem error! " + e.toString());
					}
					if (UtilValidate.isEmpty(objPicklistItem)) {
						dispatcher.runSync("createPicklistItem", UtilMisc.toMap("picklistBinId", picklistBinId,
								"orderId", orderId, "orderItemSeqId", orderItemSeqId, "shipGroupSeqId", 
								shipGroupSeqId, "inventoryItemId", inventoryItemId, "quantity", quantity, "itemStatusId", itemStatusId,
								"userLogin", userLogin));

						Debug.logWarning("OLBIUS: service: updatePicklistItemSum - createPicklistItem for picklistBinId= " + picklistBinId + " inventoryItemId= " + inventoryItemId, module);
						quantityPicked = quantityPicked.subtract(quantity);
					}
				}
			}
			if (quantityPicked.compareTo(BigDecimal.ZERO) > 0) {
				throw new Exception(UtilProperties.getMessage(resource, "DmsLuongTonKhoKhongDu", locale));
			}
			conditions.clear();
			conditions.add(EntityCondition.makeCondition("picklistBinId", EntityJoinOperator.EQUALS, picklistBinId));
			List<GenericValue> picklistItems = delegator.findList("PicklistItem", EntityCondition.makeCondition(conditions), null, null, null, false);
			if (picklistItems.isEmpty()){
				Map<String, Object> map = FastMap.newInstance();
				map.put("userLogin", userLogin);
				map.put("picklistBinId", picklistBinId);
				map.put("statusId", "PICKBIN_CANCELLED");
				try {
					dispatcher.runSync("changePicklistBinStatus", map);
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError("OLBIUS: changePicklistBinStatus error! " + e.toString());
				}
			}
		} catch (Exception e) {
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	public static Map<String, Object> updateHoldingPicklistBin(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		
		String picklistBinId = null;
		if (UtilValidate.isNotEmpty(context.get("picklistBinId"))) {
			picklistBinId = (String)context.get("picklistBinId");
			GenericValue objPicklistBin = null;
			try {
				objPicklistBin = delegator.findOne("PicklistBin", false, UtilMisc.toMap("picklistBinId", picklistBinId));
			} catch (GenericEntityException e1) {
				result = ServiceUtil.returnError(e1.getMessage());
			}
			if (UtilValidate.isNotEmpty(objPicklistBin)) {
				String holding = null;
				String curHolding = objPicklistBin.getString("holding");
				if (UtilValidate.isNotEmpty(context.get("holding"))) {
					holding = (String)context.get("holding");
					if (UtilValidate.isNotEmpty(holding)) {
						if ("Y".equals(holding)){
							GenericValue userLogin = (GenericValue) context.get("userLogin");
							Locale locale = (Locale) context.get("locale");
							if (UtilValidate.isNotEmpty(curHolding) && "Y".equals(curHolding) && !userLogin.getString("userLoginId").equals(objPicklistBin.getString("holdUserLoginId"))) {
								return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BLPickBinHasBeenHoldByAnotherOne", locale));
							}
							objPicklistBin.set("holdUserLoginId", userLogin.getString("userLoginId"));
						} else {
							objPicklistBin.set("holdUserLoginId", null);
						}
						objPicklistBin.set("holding", holding);
						try {
							delegator.store(objPicklistBin);
						} catch (Exception e) {
							result = ServiceUtil.returnError(e.getMessage());
						}
					}
				}
			}
		}
		
		return result;
	}
	
	public static Map<String, Object> updatePicklistBinItemFromPicklist(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		
		String picklistId = (String)context.get("picklistId");
		EntityCondition cond1 = EntityCondition.makeCondition("picklistId", EntityOperator.EQUALS, picklistId);
		EntityCondition cond2 = EntityCondition.makeCondition("binStatusId", EntityOperator.EQUALS, "PICKBIN_INPUT");
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(cond1);
		conds.add(cond2);
		List<GenericValue> listPicklistBin = FastList.newInstance();
		try {
			listPicklistBin = delegator.findList("PicklistBin", EntityCondition.makeCondition(conds), null, null, null, false);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: findList PicklistBin error! " + e.toString());
		}
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		if (!listPicklistBin.isEmpty()){
			for (GenericValue bin : listPicklistBin) {
				String picklistBinId = bin.getString("picklistBinId");
				EntityCondition cond3 = EntityCondition.makeCondition("picklistBinId", EntityOperator.EQUALS, picklistBinId);
				EntityCondition cond4 = EntityCondition.makeCondition("itemStatusId", EntityOperator.EQUALS, "PICKITEM_PENDING");
				List<EntityCondition> cond2s = FastList.newInstance();
				cond2s.add(cond3);
				cond2s.add(cond4);
				List<GenericValue> listPicklistItems = FastList.newInstance();
				try {
					listPicklistItems = delegator.findList("PicklistItem", EntityCondition.makeCondition(cond2s), null, null, null, false);
				} catch (GenericEntityException e) {
					return ServiceUtil.returnError("OLBIUS: findList PicklistItem error! " + e.toString());
				}
				if (!listPicklistItems.isEmpty()){
					for (GenericValue item : listPicklistItems) {
						Map<String, Object> map = FastMap.newInstance();
						map.put("userLogin", userLogin);
						map.put("picklistBinId",picklistBinId );
						map.put("orderId", item.getString("orderId"));
						map.put("orderItemSeqId", item.getString("orderItemSeqId"));
						map.put("shipGroupSeqId", item.getString("shipGroupSeqId"));
						map.put("inventoryItemId", item.getString("inventoryItemId"));
						map.put("itemStatusId", item.getString("itemStatusId"));
						map.put("quantity", item.getBigDecimal("quantity"));
						try {
							dispatcher.runSync("updatePicklistBinItemFromPicklistItem", map);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError("OLBIUS: updatePicklistBinItemFromPicklistItem error! " + e.toString());
						}
					}
				}
			}
		}
		return result;
	}
	
	public static Map<String, Object> updatePicklistBinItemFromPicklistItem(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		
		String picklistBinId = (String)context.get("picklistBinId");
		String orderId = (String)context.get("orderId");
		String orderItemSeqId = (String)context.get("orderItemSeqId");
		
		GenericValue objPicklistBin = null;
		try {
			objPicklistBin = delegator.findOne("PicklistBin", false, UtilMisc.toMap("picklistBinId", picklistBinId));
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: findOne PicklistBin error! " + e.toString());
		}
		if (UtilValidate.isNotEmpty(objPicklistBin)) {
			String statusId = objPicklistBin.getString("binStatusId");
			if ("PICKBIN_INPUT".equals(statusId)){
				GenericValue objOrderItem = null;
				try {
					objOrderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
				} catch (GenericEntityException e) {
					return ServiceUtil.returnError("OLBIUS: findOne OrderItem error! " + e.toString());
				}
				if (UtilValidate.isNotEmpty(objOrderItem)) {
					String productId = objOrderItem.getString("productId");
					List<GenericValue> listBinItemByProducts = FastList.newInstance();
					EntityCondition cond1 = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
					EntityCondition cond2 = EntityCondition.makeCondition("picklistBinId", EntityOperator.EQUALS, picklistBinId);
					List<EntityCondition> conds = FastList.newInstance();
					conds.add(cond1);
					conds.add(cond2);
					try {
						listBinItemByProducts = delegator.findList("PicklistBinItem", EntityCondition.makeCondition(conds), null, null, null, false);
					} catch (GenericEntityException e) {
						return ServiceUtil.returnError("OLBIUS: findList PicklistBinItem error! " + e.toString());
					}
					
					BigDecimal newQuantity = BigDecimal.ZERO;
					List<GenericValue> listPickListItems = FastList.newInstance();
					EntityCondition cond3 = EntityCondition.makeCondition("itemStatusId", EntityOperator.EQUALS, "PICKITEM_PENDING");
					List<EntityCondition> cond2s = FastList.newInstance();
					cond2s.add(cond1);
					cond2s.add(cond2);
					cond2s.add(cond3);
					try {
						listPickListItems = delegator.findList("PicklistItemDetail", EntityCondition.makeCondition(cond2s), null, null, null, false);
					} catch (GenericEntityException e) {
						return ServiceUtil.returnError("OLBIUS: findList PicklistItemDetail error! " + e.toString());
					}
					if (!listPickListItems.isEmpty()){
						for (GenericValue item : listPickListItems) {
							newQuantity = newQuantity.add(item.getBigDecimal("quantity"));
						}
					}
					if (!listBinItemByProducts.isEmpty()){
						// update
						GenericValue binItem = listBinItemByProducts.get(0);
						binItem.put("quantity", newQuantity);
						try {
							delegator.store(binItem);
						} catch (GenericEntityException e) {
							e.printStackTrace();
							return ServiceUtil.returnError("OLBIUS: store PicklistBinItem error! " + e.toString());
						}
					} else {
						// create new
						GenericValue pickBinItem = delegator.makeValue("PicklistBinItem");
						delegator.setNextSubSeqId(pickBinItem, "picklistBinItemSeqId", 5, 1);
						pickBinItem.put("picklistBinId", picklistBinId);
						pickBinItem.put("productId", productId);
						pickBinItem.put("quantity", newQuantity);
						try {
							delegator.create(pickBinItem);
						} catch (GenericEntityException e) {
							e.printStackTrace();
							return ServiceUtil.returnError("OLBIUS: store PicklistBinItem error! " + e.toString());
						}
					}
				}
			}
		}
		
		return result;
	}
	
	public static Map<String, Object> createPicklistBinStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		String picklistBinId = (String) context.get("picklistBinId");
		String statusId = (String) context.get("statusId");
		Timestamp statusDatetime = UtilDateTime.nowTimestamp();
		String statusUserLogin = userLogin.getString("userLoginId");
		
		GenericValue picklistBinStatus = delegator.makeValue("PicklistBinStatus");
		String picklistBinStatusId = delegator.getNextSeqId("PicklistBinStatus");
		picklistBinStatus.set("picklistBinStatusId", picklistBinStatusId);
		picklistBinStatus.set("picklistBinId", picklistBinId);
		picklistBinStatus.set("statusId", statusId);
		picklistBinStatus.set("statusDatetime", statusDatetime);
		picklistBinStatus.set("statusUserLogin", statusUserLogin);
		
		try {
			picklistBinStatus.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return successResult;
	}
	
	public static Map<String, Object> updatePicklistBinByDelivery(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		String deliveryId = (String) context.get("deliveryId");
		GenericValue objDelivery = null;
		try {
			objDelivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: findOne Delivery error! " + e.toString());
		}
		String picklistBinId = null;
		if (UtilValidate.isNotEmpty(objDelivery.get("picklistBinId"))) {
			picklistBinId = objDelivery.getString("picklistBinId");
		}
		if (UtilValidate.isNotEmpty(picklistBinId)) {
			String statusId = objDelivery.getString("statusId");
			LocalDispatcher dispatcher = ctx.getDispatcher();
			if ("DLV_EXPORTED".equals(statusId)){
				Map<String, Object> map = FastMap.newInstance();
				map.put("userLogin", userLogin);
				map.put("picklistBinId", picklistBinId);
				map.put("statusId", "PICKBIN_COMPLETED");
				try {
					dispatcher.runSync("changePicklistBinStatus", map);
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError("OLBIUS: changePicklistBinStatus error! " + e.toString());
				}
			} else if ("DLV_CANCELLED".equals(statusId)){
				Map<String, Object> map = FastMap.newInstance();
				map.put("userLogin", userLogin);
				map.put("picklistBinId", picklistBinId);
				map.put("statusId", "PICKBIN_CANCELLED");
				try {
					dispatcher.runSync("changePicklistBinStatus", map);
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError("OLBIUS: changePicklistBinStatus error! " + e.toString());
				}
			}
		}
		return successResult;
	}
	
	public static Map<String, Object> checkQuantityOnHandEnoughToPick(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		String picklistBinId = (String) context.get("picklistBinId");
		List<EntityCondition> conds = FastList.newInstance();
		EntityCondition cond1 = EntityCondition.makeCondition("picklistBinId", EntityOperator.EQUALS, picklistBinId);
		EntityCondition cond2 = EntityCondition.makeCondition("itemStatusId", EntityOperator.NOT_IN, UtilMisc.toList("PICKITEM_CANCELLED", "PICKITEM_COMPLETED"));
		List<GenericValue> listPicklistItem = FastList.newInstance();
		conds.add(cond1);
		conds.add(cond2);
		try {
			listPicklistItem = delegator.findList("PicklistItemSum", EntityCondition.makeCondition(conds), null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError("OLBIUS: findList PicklistItem error!");
		}
		Boolean checkEnough = true;
		if (!listPicklistItem.isEmpty()){
			for (GenericValue item : listPicklistItem) {
				BigDecimal quantity = item.getBigDecimal("quantity");
				String productId = item.getString("productId");
				String facilityId = item.getString("facilityId");
				
				EntityCondition condFa = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS,
						facilityId);
				EntityCondition condPr = EntityCondition.makeCondition("productId", EntityOperator.EQUALS,
						productId);
				List<EntityCondition> condInvs = FastList.newInstance();
				condInvs.add(condFa);
				condInvs.add(condPr);
				List<GenericValue> listInventoryItems = FastList.newInstance();
				
				try {
					listInventoryItems = delegator.findList("InventoryItemTotal", EntityCondition.makeCondition(condInvs), null, null, null, false);
				} catch (GenericEntityException e) {
					Debug.logError(e.toString(), module);
					return ServiceUtil.returnError("OLBIUS: findList InventoryItem error!");
				}
				
				GenericValue objProduct = null;
				try {
					objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
				} catch (GenericEntityException e) {
					Debug.logError(e.toString(), module);
					return ServiceUtil.returnError("OLBIUS: findOne Product error!");
				}
				BigDecimal qoh = BigDecimal.ZERO;
				Boolean reqAmount = false;
				if (UtilValidate.isNotEmpty(objProduct.get("requireAmount")) && "Y".equals(objProduct.getString("requireAmount")) && "WEIGHT_MEASURE".equals(objProduct.getString("amountUomTypeId"))) {
					reqAmount = true;
				} 
				for (GenericValue inv : listInventoryItems) {
					if (reqAmount){
						qoh = qoh.add(inv.getBigDecimal("amountOnHandTotal"));
					} else {
						qoh = qoh.add(inv.getBigDecimal("quantityOnHandTotal"));
					}
				}
				// subtract quantity in other picklistItem
				List<EntityCondition> condTemp = FastList.newInstance();
				EntityCondition condStt = EntityCondition.makeCondition("itemStatusId", EntityOperator.EQUALS, "PICKITEM_DLV_CREATED");
				EntityCondition condPB = EntityCondition.makeCondition("picklistBinId", EntityOperator.NOT_EQUAL, picklistBinId);
				condTemp.add(condStt);
				condTemp.add(condPB);
				condTemp.add(condFa);
				condTemp.add(condPr);
				List<GenericValue> listPickItemByInvs = FastList.newInstance();
				try {
					listPickItemByInvs = delegator.findList("PicklistItemSum", EntityCondition.makeCondition(condTemp), null, null, null,
							false);
				} catch (GenericEntityException e) {
					Debug.logError(e.toString(), module);
					return ServiceUtil.returnError("OLBIUS: findList PicklistItem error!");
				}
				if (!listPickItemByInvs.isEmpty()){
					for (GenericValue inv : listPickItemByInvs) {
						qoh = qoh.subtract(inv.getBigDecimal("quantity"));
					}
				}
				
				if (qoh.compareTo(quantity) < 0){
					checkEnough = false;
					break;
				}
			}
		}
		successResult.put("checkEnough", checkEnough);
		return successResult;
	}
	
	public static Map<String, Object> refreshInventoryItemForPicklistItem(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		String picklistBinId = (String) context.get("picklistBinId");
		String curItemStatusId= null;
		GenericValue objPicklistBin = null;
		try {
			objPicklistBin = delegator.findOne("PicklistBin", false, UtilMisc.toMap("picklistBinId", picklistBinId));
		} catch (GenericEntityException e) {
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError("OLBIUS: findOne PicklistBin error!");
		}
		if (UtilValidate.isNotEmpty(objPicklistBin)) {
			String picklistId = objPicklistBin.getString("picklistId");
			GenericValue objPicklist = null;
			try {
				objPicklist = delegator.findOne("Picklist", false, UtilMisc.toMap("picklistId", picklistId));
			} catch (GenericEntityException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: findOne Picklist error!");
			}
			String facilityId = objPicklist.getString("facilityId");
			
			List<EntityCondition> conds = FastList.newInstance();
			EntityCondition cond1 = EntityCondition.makeCondition("picklistBinId", EntityOperator.EQUALS, picklistBinId);
			EntityCondition cond2 = EntityCondition.makeCondition("itemStatusId", EntityOperator.NOT_IN, UtilMisc.toList("PICKITEM_CANCELLED", "PICKITEM_COMPLETED"));
			List<GenericValue> listPicklistItem = FastList.newInstance();
			conds.add(cond1);
			conds.add(cond2);
			try {
				listPicklistItem = delegator.findList("PicklistItem", EntityCondition.makeCondition(conds), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: findList PicklistItem error!");
			}
			List<Map<String, Object>> listOINeedRefresh = FastList.newInstance();
			if (!listPicklistItem.isEmpty()){
				for (GenericValue item : listPicklistItem) {
					String orderItemSeqId = item.getString("orderItemSeqId");
					String orderId = item.getString("orderId");
					String shipGroupSeqId = item.getString("shipGroupSeqId");
					curItemStatusId = item.getString("itemStatusId");
					Boolean check = false;
					if (!listOINeedRefresh.isEmpty()){
						for (Map<String, Object> x : listOINeedRefresh) {
							if (orderId.equals((String)x.get("orderId")) && orderItemSeqId.equals((String)x.get("orderItemSeqId")) && shipGroupSeqId.equals((String)x.get("shipGroupSeqId"))){
								check = true;
							}
						}
					}
					if (check) continue;
					BigDecimal quantity = item.getBigDecimal("quantity");
					String inventoryItemId = item.getString("inventoryItemId");
					GenericValue objInventoryItem = null;
					try {
						objInventoryItem = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", inventoryItemId));
					} catch (GenericEntityException e) {
						Debug.logError(e.toString(), module);
						return ServiceUtil.returnError("OLBIUS: findOne InventoryItem error!");
					}
					String productId = objInventoryItem.getString("productId");
					GenericValue objProduct = null;
					try {
						objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
					} catch (GenericEntityException e) {
						Debug.logError(e.toString(), module);
						return ServiceUtil.returnError("OLBIUS: findOne Product error!");
					}
					BigDecimal qoh = objInventoryItem.getBigDecimal("quantityOnHandTotal");
					if (UtilValidate.isNotEmpty(objProduct.get("requireAmount")) && "Y".equals(objProduct.getString("requireAmount")) && "WEIGHT_MEASURE".equals(objProduct.getString("amountUomTypeId"))) {
						qoh = objInventoryItem.getBigDecimal("amountOnHandTotal");
					} 
					// subtract quantity in other picklistItem
					List<EntityCondition> condTemp = FastList.newInstance();
					EntityCondition condInvId = EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS, inventoryItemId);
					EntityCondition condStt = EntityCondition.makeCondition("itemStatusId", EntityOperator.NOT_IN,
							UtilMisc.toList("PICKITEM_CANCELLED", "PICKITEM_COMPLETED"));
					EntityCondition condPB = EntityCondition.makeCondition("picklistBinId", EntityOperator.NOT_EQUAL,
							picklistBinId);
					condTemp.add(condInvId);
					condTemp.add(condStt);
					condTemp.add(condPB);
					List<GenericValue> listPickItemByInvs = FastList.newInstance();
					try {
						listPickItemByInvs = delegator.findList("PicklistItem", EntityCondition.makeCondition(condTemp), null, null, null,
								false);
					} catch (GenericEntityException e) {
						Debug.logError(e.toString(), module);
						return ServiceUtil.returnError("OLBIUS: findList PicklistItem error!");
					}
					if (!listPickItemByInvs.isEmpty()){
						for (GenericValue inv : listPickItemByInvs) {
							qoh = qoh.subtract(inv.getBigDecimal("quantity"));
						}
					}
					if (quantity.compareTo(qoh) > 0){
						Map<String, Object> mapRf = FastMap.newInstance();
						mapRf.put("orderItemSeqId", orderItemSeqId);
						mapRf.put("orderId", orderId);
						mapRf.put("shipGroupSeqId", shipGroupSeqId);
						listOINeedRefresh.add(mapRf);
					}
				}
			}
			if (!listOINeedRefresh.isEmpty()){
				LocalDispatcher dispatcher = ctx.getDispatcher();
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				for (Map<String, Object> map : listOINeedRefresh) {
					String orderItemSeqId = (String)map.get("orderItemSeqId");
					String orderId = (String)map.get("orderId");
					String shipGroupSeqId = (String)map.get("shipGroupSeqId");
					BigDecimal quantity = BigDecimal.ZERO;
					List<GenericValue> listPicklistItemByOrderItem = FastList.newInstance();
					EntityCondition condOrd = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,
							orderId);
					EntityCondition condOrdItem = EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS,
							orderItemSeqId);
					EntityCondition condOrdSGp = EntityCondition.makeCondition("shipGroupSeqId", EntityOperator.EQUALS,
							shipGroupSeqId);
					List<EntityCondition> condByOrders = FastList.newInstance();
					condByOrders.add(condOrdItem);
					condByOrders.add(condOrdSGp);
					condByOrders.add(condOrd);
					try {
						listPicklistItemByOrderItem = delegator.findList("PicklistItem", EntityCondition.makeCondition(condByOrders), null, null, null,
								false);
					} catch (GenericEntityException e) {
						Debug.logError(e.toString(), module);
						return ServiceUtil.returnError("OLBIUS: findList PicklistItem error!");
					}
					for (GenericValue pit : listPicklistItemByOrderItem) {
						quantity = quantity.add(pit.getBigDecimal("quantity"));
					}
					
					GenericValue objOrderItem = null;
					try {
						objOrderItem = delegator.findOne("OrderItem", false,
								UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
					} catch (GenericEntityException e) {
						Debug.logError(e.toString(), module);
						return ServiceUtil.returnError("OLBIUS: findOne OrderItem error!");
					}
					if (UtilValidate.isEmpty(objOrderItem)) continue;
					String productId = objOrderItem.getString("productId");
					EntityCondition condFa = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId);
					EntityCondition condPr = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
					EntityCondition condQoh = EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
					List<EntityCondition> condInvs = FastList.newInstance();
					condInvs.add(condFa);
					condInvs.add(condPr);
					condInvs.add(condQoh);
					List<GenericValue> listInventoryItems = FastList.newInstance();
					
					GenericValue objProduct = null;
					try {
						objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
					} catch (GenericEntityException e) {
						Debug.logError(e.toString(), module);
						return ServiceUtil.returnError("OLBIUS: findOne Product error!");
					}
					List<String> orderBy = FastList.newInstance();
					Boolean reAmount = false;
					if (UtilValidate.isNotEmpty(objProduct.get("requireAmount")) && "Y".equals(objProduct.getString("requireAmount")) && "WEIGHT_MEASURE".equals(objProduct.getString("amountUomTypeId"))) {
						orderBy.add("-amountOnHandTotal");
						reAmount = true;
					} else {
						orderBy.add("-quantityOnHandTotal");
					}
					
					try {
						listInventoryItems = delegator.findList("InventoryItem", EntityCondition.makeCondition(condInvs), null, orderBy, null, false);
					} catch (GenericEntityException e) {
						Debug.logError(e.toString(), module);
						return ServiceUtil.returnError("OLBIUS: findList InventoryItem error!");
					}
					BigDecimal quantityRemain = quantity;
					if (!listInventoryItems.isEmpty()){
						
						try {
							delegator.removeByAnd("PicklistItem", UtilMisc.toMap("picklistBinId", picklistBinId, "orderId", orderId, "orderItemSeqId", orderItemSeqId, "shipGroupSeqId", shipGroupSeqId));
						} catch (GenericEntityException e1) {
							Debug.logError(e1.toString(), module);
							return ServiceUtil.returnError("OLBIUS: removeByAnd PicklistItem error!");
						}
							
						for (GenericValue inv : listInventoryItems) {
							BigDecimal qohTmp = inv.getBigDecimal("quantityOnHandTotal");
							if (reAmount){
								qohTmp = inv.getBigDecimal("amountOnHandTotal");
							}
							// subtract quantity in other picklistItem
							List<EntityCondition> condTemp = FastList.newInstance();
							EntityCondition condInvId = EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS, inv.getString("inventoryItemId"));
							EntityCondition condPB = EntityCondition.makeCondition("picklistBinId", EntityOperator.NOT_EQUAL,
									picklistBinId);
							EntityCondition condStt = EntityCondition.makeCondition("itemStatusId", EntityOperator.NOT_IN,
									UtilMisc.toList("PICKITEM_CANCELLED", "PICKITEM_COMPLETED"));
							condTemp.add(condInvId);
							condTemp.add(condPB);
							condTemp.add(condStt);
							List<GenericValue> listPickItemByInvs = FastList.newInstance();
							try {
								listPickItemByInvs = delegator.findList("PicklistItem", EntityCondition.makeCondition(condTemp), null, null, null,
										false);
							} catch (GenericEntityException e) {
								Debug.logError(e.toString(), module);
								return ServiceUtil.returnError("OLBIUS: findList PicklistItem error!");
							}
							if (!listPickItemByInvs.isEmpty()){
								for (GenericValue inv2 : listPickItemByInvs) {
									qohTmp = qohTmp.subtract(inv2.getBigDecimal("quantity"));
								}
							}
							
							if (qohTmp.compareTo(BigDecimal.ZERO) > 0){
								Map<String, Object> addMap = FastMap.newInstance();
								if (qohTmp.compareTo(quantityRemain) > 0) {
									addMap.put("quantity", quantityRemain);
									quantityRemain = BigDecimal.ZERO;
								} else {
									quantityRemain = quantityRemain.subtract(qohTmp);
									addMap.put("quantity", qohTmp);
								}
								String inventoryItemId = inv.getString("inventoryItemId");
								addMap.put("picklistBinId", picklistBinId);
								addMap.put("orderId", orderId);
								addMap.put("orderItemSeqId", orderItemSeqId);
								addMap.put("shipGroupSeqId", shipGroupSeqId);
								addMap.put("itemStatusId", curItemStatusId);
								addMap.put("inventoryItemId", inventoryItemId);
								addMap.put("userLogin", userLogin);
								
								try {
									dispatcher.runSync("createPicklistItem", addMap);
									Debug.logWarning("OLBIUS: service: refreshInventoryItemForPicklistItem - createPicklistItem for picklistBinId= " + picklistBinId + " inventoryItemId= " + inventoryItemId, module);
								} catch (GenericServiceException e) {
									Debug.logError(e.toString(), module);
									return ServiceUtil.returnError("OLBIUS: createPicklistItem error!");
								}
							}
							if (quantityRemain.compareTo(BigDecimal.ZERO) <= 0) {
								break;
							}
						}
					}
				}
			}
		}
		return successResult;
	}
	
	public static Map<String, Object> updateQuantityEAInventoryItemLocation(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			String productId = (String) context.get("productId");
			String inventoryItemId = (String) context.get("inventoryItemId");
			String locationId = (String) context.get("locationId");
			String uomId = (String) context.get("uomId");
			String uomEAIdIn = (String) context.get("uomEAId");
			BigDecimal quantityEAIn = (BigDecimal) context.get("quantityEA");
			BigDecimal quantity = (BigDecimal) context.get("quantity");
			BigDecimal quantityEA = BigDecimal.ZERO;
			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			if (UtilValidate.isNotEmpty(product)) {
				String quantityUomId = product.getString("quantityUomId");
				if (uomId.equals(quantityUomId)) {
					quantityEA = quantity;
				} else {
					List<EntityCondition> listCond = FastList.newInstance();
					listCond.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
					listCond.add(EntityCondition.makeCondition("uomFromId", EntityOperator.EQUALS, uomId));
					listCond.add(EntityCondition.makeCondition("uomToId", EntityOperator.EQUALS, quantityUomId));
					listCond.add(EntityUtil.getFilterByDateExpr());
					
					BigDecimal quantityConvert = BigDecimal.ONE;
					
					List<GenericValue> configPacking = delegator.findList("ConfigPacking", EntityCondition.makeCondition(listCond), null, null, null, false);
					if (UtilValidate.isNotEmpty(configPacking)) {
						quantityConvert = configPacking.get(0).getBigDecimal("quantityConvert");
					}
					quantityEA = quantity.multiply(quantityConvert);
				}
				
				if (!quantityEA.equals(quantityEAIn) || !quantityUomId.equals(uomEAIdIn)) {
					GenericValue invLoc = delegator.findOne("InventoryItemLocation", UtilMisc.toMap("inventoryItemId", inventoryItemId, "locationId", locationId), false);
					if (UtilValidate.isNotEmpty(invLoc)) {
						invLoc.set("quantityEA", quantityEA);
						invLoc.set("uomEAId", quantityUomId);
						invLoc.store();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
}
