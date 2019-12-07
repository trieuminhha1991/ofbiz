package com.olbius.promo;

import java.math.BigDecimal;

import org.ofbiz.security.Security;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
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
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.SalesPartyUtil;

public class PromoServices {
	public static final String module = PromoServices.class.getName();
	
	public static Map<String, Object> getListRoleTypeByGroup(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	List<Map<String, Object>> listRoleType = new ArrayList<Map<String,Object>>();
       	EntityFindOptions opts = new EntityFindOptions();
       	String channelId = (String) context.get("channelId");
       	opts.setDistinct(true);
   		try {
   			if (UtilValidate.isNotEmpty(channelId)) {
   				List<GenericValue> listProductStoreGV = SalesPartyUtil.getListGVRoleMemberDescendantInGroup(channelId, delegator);
   				if (listProductStoreGV != null) {
   					for (GenericValue item : listProductStoreGV) {
   						Map<String, Object> itemMap = FastMap.newInstance();
   						itemMap.put("roleTypeId", item.get("roleTypeId"));
   						itemMap.put("description", item.get("description", locale));
   						listRoleType.add(itemMap);
   					}
   				}
   			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListRoleTypeByGroup service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listRoleType", listRoleType);
    	return successResult;
	}
	public static Map<String, Object> getListProductStore(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	List<Map<String, Object>> listProductStore = new ArrayList<Map<String,Object>>();
   		List<EntityCondition> listAllConditions = FastList.newInstance();
       	EntityFindOptions opts = new EntityFindOptions();
       	String channelId = (String) context.get("channelId");
       	opts.setDistinct(true);
   		try {
   			if (UtilValidate.isNotEmpty(channelId)) {
   				listAllConditions.add(EntityCondition.makeCondition("salesMethodChannelEnumId", channelId));
   				List<GenericValue> listProductStoreGV = delegator.findList("ProductStore", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, opts, false);
   				if (listProductStoreGV != null) {
   					for (GenericValue item : listProductStoreGV) {
   						Map<String, Object> itemMap = FastMap.newInstance();
   						itemMap.put("productStoreId", item.get("productStoreId"));
   						itemMap.put("storeName", item.get("storeName"));
   						listProductStore.add(itemMap);
   					}
   				}
   			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListProductStore service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listProductStore", listProductStore);
    	return successResult;
	}
	public static Map<String, Object> getListProductPromoType(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	List<Map<String, Object>> listPromoType = new ArrayList<Map<String,Object>>();
   		List<EntityCondition> listAllConditions = FastList.newInstance();
       	EntityFindOptions opts = new EntityFindOptions();
       	String channelId = (String) context.get("channelId");
       	opts.setDistinct(true);
   		try {
   			if (UtilValidate.isNotEmpty(channelId)) {
   				listAllConditions.add(EntityCondition.makeCondition("productPromoTypeGroupId", channelId));
   				List<GenericValue> listPromoTypeGV = delegator.findList("ProductPromoType", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, opts, false);
   				if (listPromoTypeGV != null) {
   					for (GenericValue item : listPromoTypeGV) {
   						Map<String, Object> itemMap = FastMap.newInstance();
   						itemMap.put("productPromoTypeId", item.get("productPromoTypeId"));
   						itemMap.put("description", item.get("description", locale));
   						listPromoType.add(itemMap);
   					}
   				}
   			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListProductPromoType service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listPromoType", listPromoType);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListPromoSettlementRecord(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	/*Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	mapCondition.put("organizationPartyId", (String)parameters.get("organizationPartyId")[0]);
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);*/
    	try {
			EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("PromoSettleRecord", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductByCategoryCatalog service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListOrderItem(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	mapCondition.put("sellerRoleTypeId", "DELYS_DISTRIBUTOR");
    	mapCondition.put("promoSettleRecordId", null);
    	mapCondition.put("orderStatusId", "ORDER_COMPLETED");
    	mapCondition.put("itemStatusId", "ITEM_COMPLETED");
    	EntityCondition tmpConditonPre = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditonPre);
    	if (UtilValidate.isNotEmpty(parameters) && parameters.containsKey("fromDate") && parameters.containsKey("toDate")) {
    		String fromDateStr = parameters.get("fromDate")[0];
    		String toDateStr = parameters.get("toDate")[0];
    		if (UtilValidate.isNotEmpty(fromDateStr) && UtilValidate.isNotEmpty(toDateStr)) {
    			try {
    				Timestamp fromDate = Timestamp.valueOf(fromDateStr);
                	Timestamp toDate = Timestamp.valueOf(toDateStr);
                	if (UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(toDate)) {
                		listAllConditions.add(EntityCondition.makeCondition("orderDate", EntityOperator.BETWEEN, UtilMisc.toList(fromDate, toDate)));
                	}
    			} catch (Exception e) {
    				
    			}
    		}
    	}
    	try {
			EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("OrderHeaderItemsPromosAndRoleAndSettle", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductByCategoryCatalog service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListPromoExhibitedSettle(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	//Map<String, String> mapCondition = new HashMap<String, String>();
    	//mapCondition.put("organizationPartyId", (String)parameters.get("organizationPartyId")[0]);
    	//EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	if (UtilValidate.isNotEmpty(parameters) && parameters.containsKey("fromDate") && parameters.containsKey("toDate")) {
    		String fromDateStr = parameters.get("fromDate")[0];
    		String toDateStr = parameters.get("toDate")[0];
    		if (UtilValidate.isNotEmpty(fromDateStr) && UtilValidate.isNotEmpty(toDateStr)) {
    			try {
    				Timestamp fromDate = Timestamp.valueOf(fromDateStr);
                	Timestamp toDate = Timestamp.valueOf(toDateStr);
                	if (UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(toDate)) {
                		listAllConditions.add(EntityCondition.makeCondition("orderDate", EntityOperator.BETWEEN, UtilMisc.toList(fromDate, toDate)));
                	}
    			} catch (Exception e) {
    				
    			}
    		}
    	}
    	try {
			EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("ProductPromo", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductByCategoryCatalog service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListOrderItemSettleAccept(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	mapCondition.put("sellerRoleTypeId", "DELYS_DISTRIBUTOR");
    	mapCondition.put("promoSettleRecordId", null);
    	mapCondition.put("orderStatusId", "ORDER_COMPLETED");
    	mapCondition.put("itemStatusId", "ITEM_COMPLETED");
    	EntityCondition tmpConditonPre = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditonPre);
    	if (UtilValidate.isNotEmpty(parameters)) {
    		if (parameters.containsKey("fromDate") && parameters.containsKey("toDate")) {
    			String fromDateStr = parameters.get("fromDate")[0];
        		String toDateStr = parameters.get("toDate")[0];
        		if (UtilValidate.isNotEmpty(fromDateStr) && UtilValidate.isNotEmpty(toDateStr)) {
        			try {
        				Timestamp fromDate = Timestamp.valueOf(fromDateStr);
                    	Timestamp toDate = Timestamp.valueOf(toDateStr);
                    	if (UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(toDate)) {
                    		listAllConditions.add(EntityCondition.makeCondition("orderDate", EntityOperator.BETWEEN, UtilMisc.toList(fromDate, toDate)));
                    	}
        			} catch (Exception e) {
        				
        			}
        		}
    		}
    		if (parameters.containsKey("dataSelectedOrderId[]") && parameters.containsKey("dataSelectedOrderItemSeqId[]")) {
    			String[] dataSelectedOrderId = (String[]) parameters.get("dataSelectedOrderId[]");
    			String[] dataSelectedOrderItemSeqId = (String[]) parameters.get("dataSelectedOrderItemSeqId[]");
    			//listAllConditions.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, java.util.Arrays.asList(dataSelectedOrderId)));
    			//listAllConditions.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.IN, java.util.Arrays.asList(dataSelectedOrderItemSeqId)));
    			String str = "(OH.ORDER_ID, OI.ORDER_ITEM_SEQ_ID) IN (";
    			for (int i=0; i < dataSelectedOrderId.length; i++) {
					if (i==0) {
						str += "('" + dataSelectedOrderId[i] + "', '" + dataSelectedOrderItemSeqId[i] + "')";
					} else {
						str += ", ('" + dataSelectedOrderId[i] + "', '" + dataSelectedOrderItemSeqId[i] + "')";
					}
    			}
    			str += ")";
    			listAllConditions.add(EntityCondition.makeConditionWhere(str));
    			try {
    				EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    				listIterator = delegator.find("OrderHeaderItemsPromosAndRoleAndSettle", tmpConditon, null, null, listSortFields, opts);
    			} catch (Exception e) {
    				String errMsg = "Fatal error calling jqGetListProductByCategoryCatalog service: " + e.toString();
    				Debug.logError(e, errMsg, module);
    			}
    		}
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListOrderItemSettleReject(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	mapCondition.put("sellerRoleTypeId", "DELYS_DISTRIBUTOR");
    	mapCondition.put("promoSettleRecordId", null);
    	mapCondition.put("orderStatusId", "ORDER_COMPLETED");
    	mapCondition.put("itemStatusId", "ITEM_COMPLETED");
    	EntityCondition tmpConditonPre = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditonPre);
    	if (UtilValidate.isNotEmpty(parameters)) {
    		if (parameters.containsKey("fromDate") && parameters.containsKey("toDate")) {
    			String fromDateStr = parameters.get("fromDate")[0];
        		String toDateStr = parameters.get("toDate")[0];
        		if (UtilValidate.isNotEmpty(fromDateStr) && UtilValidate.isNotEmpty(toDateStr)) {
        			try {
        				Timestamp fromDate = Timestamp.valueOf(fromDateStr);
                    	Timestamp toDate = Timestamp.valueOf(toDateStr);
                    	if (UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(toDate)) {
                    		listAllConditions.add(EntityCondition.makeCondition("orderDate", EntityOperator.BETWEEN, UtilMisc.toList(fromDate, toDate)));
                    	}
        			} catch (Exception e) {
        				
        			}
        		}
    		}
    		if (parameters.containsKey("dataSelectedOrderId[]") && parameters.containsKey("dataSelectedOrderItemSeqId[]")) {
    			String[] dataSelectedOrderId = (String[]) parameters.get("dataSelectedOrderId[]");
    			String[] dataSelectedOrderItemSeqId = (String[]) parameters.get("dataSelectedOrderItemSeqId[]");
    			//listAllConditions.add(EntityCondition.makeCondition("orderId", EntityOperator.NOT_IN, java.util.Arrays.asList(dataSelectedOrderId)));
    			//listAllConditions.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.NOT_IN, java.util.Arrays.asList(dataSelectedOrderItemSeqId)));
    			String str = "(OH.ORDER_ID, OI.ORDER_ITEM_SEQ_ID) NOT IN (";
    			for (int i=0; i < dataSelectedOrderId.length; i++) {
					if (i==0) {
						str += "('" + dataSelectedOrderId[i] + "', '" + dataSelectedOrderItemSeqId[i] + "')";
					} else {
						str += ", ('" + dataSelectedOrderId[i] + "', '" + dataSelectedOrderItemSeqId[i] + "')";
					}
    			}
    			str += ")";
    			listAllConditions.add(EntityCondition.makeConditionWhere(str));
    			try {
    				EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    				listIterator = delegator.find("OrderHeaderItemsPromosAndRoleAndSettle", tmpConditon, null, null, listSortFields, opts);
    			} catch (Exception e) {
    				String errMsg = "Fatal error calling jqGetListProductByCategoryCatalog service: " + e.toString();
    				Debug.logError(e, errMsg, module);
    			}
    		}
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListPromoSettleItem(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	mapCondition.put("promoSettleRecordId", (String)parameters.get("promoSettleRecordId")[0]);
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("PromoSettleItem", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListPromoSettleItem service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListPromoSettleGroup(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	mapCondition.put("promoSettleRecordId", (String)parameters.get("promoSettleRecordId")[0]);
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("PromoSettleGroup", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListPromoSettleGroup service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListPromoSettleGroupItem(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	mapCondition.put("promoSettleGroupId", (String)parameters.get("promoSettleGroupId")[0]);
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("PromoSettleGroupItem", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListPromoSettleGroupItem service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListPromoSettleGroupItemChild(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	if (parameters.containsKey("promoSettleGroupItemId") && UtilValidate.isNotEmpty(parameters.get("promoSettleGroupItemId")[0])) {
    		mapCondition.put("promoSettleGroupItemParentId", (String)parameters.get("promoSettleGroupItemId")[0]);
    		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
        	listAllConditions.add(tmpConditon);
        	try {
        		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    			listIterator = delegator.find("PromoSettleGroupItem", tmpConditon, null, null, listSortFields, opts);
    		} catch (Exception e) {
    			String errMsg = "Fatal error calling jqGetListPromoSettleGroupItem service: " + e.toString();
    			Debug.logError(e, errMsg, module);
    		}
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> updateSettlementGroupItem(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		String promoSettleGroupItemId = (String) context.get("promoSettleGroupItemId");
		String quantityAcceptedStr = (String) context.get("quantityAccepted");
		String amountAcceptedStr = (String) context.get("amountAccepted");
		String newStatusId = (String) context.get("newStatusId");
		
		if (UtilValidate.isEmpty(promoSettleGroupItemId)) {
			return ServiceUtil.returnError("Settlement group item is null!");
		}
		GenericValue settleGroupItem = null;
		try {
			settleGroupItem = delegator.findOne("PromoSettleGroupItem", UtilMisc.toMap("promoSettleGroupItemId", promoSettleGroupItemId), false);
			if (settleGroupItem == null) {
				return ServiceUtil.returnError("Settlement group item not avaiable");
			}
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Error when get data");
		}
		if (UtilValidate.isNotEmpty(newStatusId) && !("STLE_GRPIM_ACCEPTED".equals(newStatusId) || "STLE_GRPIM_REJECTED".equals(newStatusId))) {
			return ServiceUtil.returnError("Status not valid!");
		}
		BigDecimal quantityAccepted = null;
		BigDecimal amountAccepted = null;
		try {
			if (UtilValidate.isNotEmpty(quantityAcceptedStr)) quantityAccepted = new BigDecimal(quantityAcceptedStr);
			if (UtilValidate.isNotEmpty(amountAcceptedStr)) amountAccepted = new BigDecimal(amountAcceptedStr);
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Error when convert number");
		}
		try {
			settleGroupItem.put("quantityAccepted", quantityAccepted);
			settleGroupItem.put("amountAccepted", amountAccepted);
			if (UtilValidate.isNotEmpty(newStatusId)) settleGroupItem.put("statusId", newStatusId);
			delegator.store(settleGroupItem);
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Error when store data");
		}
		
		return successResult;
	}
	
	public static String splitSettleGroupItem(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String promoSettleGroupIdP = request.getParameter("promoSettleGroupId");
		String listData = request.getParameter("listData");
		
		JSONArray json = new JSONArray();
		if(UtilValidate.isNotEmpty(listData)){
			 json = JSONArray.fromObject(listData);
		}
		if (json != null && json.size() > 0) {
			List<GenericValue> toBeStored = new LinkedList<GenericValue>();
			for (int i = 0; i < json.size(); i++) {
				JSONObject groupItem = json.getJSONObject(i);
				String promoSettleGroupItemId = groupItem.getString("promoSettleGroupItemId");
				String promoSettleGroupItemParentId = groupItem.getString("promoSettleGroupItemParentId");
				String promoSettleGroupId = groupItem.getString("promoSettleGroupId");
				String promoSettleGroupType = groupItem.getString("promoSettleGroupType");
				String productId = groupItem.getString("productId");
				String quantityRequiredStr = groupItem.getString("quantityRequired");
				String amountRequiredStr = groupItem.getString("amountRequired");
				String quantityAcceptedStr = groupItem.getString("quantityAccepted");
				String amountAcceptedStr = groupItem.getString("amountAccepted");
				String statusId = groupItem.getString("statusId");
				
				BigDecimal quantityRequired = null;
				BigDecimal amountRequired = null;
				BigDecimal quantityAccepted = null;
				BigDecimal amountAccepted = null;
				try {
					if (UtilValidate.isNotEmpty(quantityRequiredStr)) quantityRequired = new BigDecimal(quantityRequiredStr);
					if (UtilValidate.isNotEmpty(amountRequiredStr)) amountRequired = new BigDecimal(amountRequiredStr);
					if (UtilValidate.isNotEmpty(quantityAcceptedStr)) quantityAccepted = new BigDecimal(quantityAcceptedStr);
					if (UtilValidate.isNotEmpty(amountAcceptedStr)) amountAccepted = new BigDecimal(amountAcceptedStr);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				boolean isExisted = false;
				if (UtilValidate.isNotEmpty(promoSettleGroupItemId)) {
					try {
						GenericValue itemData = delegator.findOne("PromoSettleGroupItem", UtilMisc.toMap("promoSettleGroupItemId", promoSettleGroupItemId), false);
						if (itemData != null) {
							itemData.put("productId", productId);
							itemData.put("quantityRequired", quantityRequired);
							itemData.put("amountRequired", amountRequired);
							itemData.put("quantityAccepted", quantityAccepted);
							itemData.put("amountAccepted", amountAccepted);
							toBeStored.add(itemData);
							isExisted = true;
						}
					} catch (GenericEntityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				if (!isExisted) {
					Map<String, Object> itemDataMap = FastMap.newInstance();
					itemDataMap.put("promoSettleGroupItemId", delegator.getNextSeqId("PromoSettleGroupItem"));
					itemDataMap.put("promoSettleGroupItemParentId", promoSettleGroupItemParentId);
					itemDataMap.put("promoSettleGroupId", promoSettleGroupId);
					itemDataMap.put("promoSettleGroupType", promoSettleGroupType);
					itemDataMap.put("productId", productId);
					itemDataMap.put("quantityRequired", quantityRequired);
					itemDataMap.put("amountRequired", amountRequired);
					itemDataMap.put("quantityAccepted", quantityAccepted);
					itemDataMap.put("amountAccepted", amountAccepted);
					itemDataMap.put("statusId", statusId);
					GenericValue itemData = delegator.makeValue("PromoSettleGroupItem", itemDataMap);
					toBeStored.add(itemData);
				}
			}
			try {
	            // store line items, etc so that they will be there for the foreign key checks
	            delegator.storeAll(toBeStored);
	        } catch (GenericEntityException e) {
	            Debug.logError(e, "Problem with store all data splitSettleGroupItem event", module);
	            return "error";
	        }
		}
		request.setAttribute("promoSettleGroupId", promoSettleGroupIdP);
		return "success";
	}
	
	
	public static Map<String, Object> changeStatusPromoSettleGroup(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successReturn = ServiceUtil.returnSuccess();
		String promoSettleGroupId = (String) context.get("promoSettleGroupId");
		String statusId = (String) context.get("statusId");
		
		GenericValue settleGroup = null;
		try {
			settleGroup = delegator.findOne("PromoSettleGroup", UtilMisc.toMap("promoSettleGroupId", promoSettleGroupId), false);
			if (settleGroup == null) {
				return ServiceUtil.returnError("Settlement group not avaiable");
			}
			if (!("STLE_GRP_PROCESSING".equals(statusId) || "STLE_GRP_CANCELED".equals(statusId) || "STLE_GRP_COMPLETED".equals(statusId))) {
				return ServiceUtil.returnError("Status not valid!");
			}
			settleGroup.put("statusId", statusId);
			delegator.store(settleGroup);
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Error when get data");
		}
		
		return successReturn;
	}
	
	public static Map<String, Object> deletePromoSettleGroupItem(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successReturn = ServiceUtil.returnSuccess();
		String promoSettleGroupItemId = (String) context.get("promoSettleGroupItemId");
		
		GenericValue settleGroupItem = null;
		try {
			settleGroupItem = delegator.findOne("PromoSettleGroupItem", UtilMisc.toMap("promoSettleGroupItemId", promoSettleGroupItemId), false);
			if (settleGroupItem == null) {
				return ServiceUtil.returnError("Settlement group not avaiable");
			}
			if (UtilValidate.isEmpty(settleGroupItem.get("promoSettleGroupItemParentId"))) {
				return ServiceUtil.returnError(UtilProperties.getMessage("DelysAdminErrorUiLabels", "DACannotRemoveOriginalElement", locale));
			}
			List<GenericValue> listSettleGroupItemChild = delegator.findByAnd("PromoSettleGroupItem", UtilMisc.toMap("promoSettleGroupItemParentId", promoSettleGroupItemId), null, false);
			if (UtilValidate.isNotEmpty(listSettleGroupItemChild)) {
				return ServiceUtil.returnError(UtilProperties.getMessage("DelysAdminErrorUiLabels", "DACannotRemoveThisElementThisElementHasChild", locale));
			}
			delegator.removeValue(settleGroupItem);
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Error when get data");
		}
		
		return successReturn;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> jqGetListProductPromo(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Security security = ctx.getSecurity();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			boolean isSearch = false;
			if (security.hasEntityPermission("DELYS_PROMOS", "_VIEW", userLogin)) {
				isSearch = true;
				listAllConditions.add(EntityCondition.makeCondition("productPromoTypeId", EntityOperator.NOT_EQUAL, null));
			} else if (security.hasEntityPermission("PROMOS_ROLE", "_VIEW", userLogin)) {
				if (SalesPartyUtil.isSalesAdminEmployee(userLogin, delegator)) {
					Map<String, Object> tmpResultProductStore = dispatcher.runSync("getListStoreCompanyViewedByUserLogin", UtilMisc.toMap("userLogin", userLogin));
		    		if (ServiceUtil.isError(tmpResultProductStore)) return tmpResultProductStore;
		    		List<GenericValue> listStore = (List<GenericValue>) tmpResultProductStore.get("listProductStore");
		    		if (UtilValidate.isNotEmpty(listStore)) {
		    			List<String> salesMethodChannel = EntityUtil.getFieldListFromEntityList(listStore, "salesMethodChannelEnumId", true);
		    			if (UtilValidate.isNotEmpty(salesMethodChannel)) {
		    				List<String> listPromoTypeId = FastList.newInstance();
		    				for (String salesMethodChannelId : salesMethodChannel) {
		    					Map<String, Object> resultService = dispatcher.runSync("getProductPromoTypesByChannel", UtilMisc.toMap("salesMethodChannel", salesMethodChannelId));
			    				if (ServiceUtil.isSuccess(resultService)) {
			    					List<GenericValue> listPromoType = (List<GenericValue>) resultService.get("listProductPromoType");
			    					if (listPromoType != null) {
			    						List<String> tmp = EntityUtil.getFieldListFromEntityList(listPromoType, "productPromoTypeId", true);
			    						if (tmp != null) listPromoTypeId.addAll(tmp);
			    					}
			    				}
		    				}
		    				if (UtilValidate.isNotEmpty(listPromoTypeId)) {
		    					listAllConditions.add(EntityCondition.makeCondition("productPromoTypeId", EntityOperator.IN, listPromoTypeId));
		    					isSearch = true;
		    				}
		    			}
		    		}
				}
			}
			
			if (isSearch) {
				boolean isAcceptView = false;
				if (SalesPartyUtil.isDistributor(userLogin, delegator)) {
					Map<String, Object> tmpResult = dispatcher.runSync("getListStoreCompanyViewedByUserLogin", UtilMisc.toMap("userLogin", userLogin));
		    		if (ServiceUtil.isError(tmpResult)) return tmpResult;
		    		List<GenericValue> listStore = (List<GenericValue>) tmpResult.get("listProductStore");
		    		
		    		if (UtilValidate.isNotEmpty(listStore)) {
		    			List<EntityCondition> listCond = FastList.newInstance();
		    			listCond.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(listStore, "productStoreId", true)));
		    			listCond.add(EntityCondition.makeCondition("productPromoStatusId", EntityOperator.EQUALS, "PROMO_ACCEPTED"));
		    			listCond.add(EntityCondition.makeCondition("productPromoTypeId", EntityOperator.NOT_EQUAL, null));
		    			EntityFindOptions findOpts = new EntityFindOptions();
		    			findOpts.setDistinct(true);
		    			
		    			List<String> listProductPromoId = new ArrayList<String>();
		    			List<String> listRoleTypeId = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", userLogin.getString("partyId")), null, false), "roleTypeId", true);
		    			List<GenericValue> listProductStorePromoAppl = delegator.findList("ProductStorePromoApplDetail", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, findOpts, false);
		    			if (UtilValidate.isNotEmpty(listProductStorePromoAppl)) {
		    				for (GenericValue productStorePromoAppl : listProductStorePromoAppl) {
		    					boolean isAdded = false;
		    					List<GenericValue> listProductPromoCond = delegator.findByAnd("ProductPromoCond", UtilMisc.toMap("inputParamEnumId", "PPIP_ROLE_TYPE", "operatorEnumId","PPC_EQ", "productPromoId", productStorePromoAppl.getString("productPromoId")), null, false);
		    					if (UtilValidate.isNotEmpty(listProductPromoCond)) {
		    						List<String> listRoleTypeIdAppl = EntityUtil.getFieldListFromEntityList(listProductPromoCond, "condValue", true);
		    						if (UtilValidate.isNotEmpty(listRoleTypeId) && UtilValidate.isNotEmpty(listRoleTypeIdAppl)) {
		    							if (SalesPartyUtil.hasContain(listRoleTypeIdAppl, listRoleTypeId)) {
		    								listProductPromoId.add(productStorePromoAppl.getString("productPromoId"));
		    								isAdded = true;
		    							}
		    						}
		    					}
		    					
		    					if (!isAdded) {
		    						// check in list roleTypeAppl
		    						List<GenericValue> listProductPromoRoleTypeAppl = delegator.findByAnd("ProductPromoRoleTypeAppl", UtilMisc.toMap("productPromoId", productStorePromoAppl.getString("productPromoId")), null, false);
		    						List<String> listRoleTypeIdAppl = EntityUtil.getFieldListFromEntityList(listProductPromoRoleTypeAppl, "roleTypeId", true);
		    						if (UtilValidate.isNotEmpty(listRoleTypeId) && UtilValidate.isNotEmpty(listRoleTypeIdAppl)) {
		    							if (SalesPartyUtil.hasContain(listRoleTypeIdAppl, listRoleTypeId)) {
		    								listProductPromoId.add(productStorePromoAppl.getString("productPromoId"));
		    								isAdded = true;
		    							}
		    						}
		    					}
		    				}
		    			}
		    			listAllConditions.add(EntityCondition.makeCondition("productPromoId", EntityOperator.IN, listProductPromoId));
		    			isAcceptView = true;
		    		}
				} else if (SalesPartyUtil.isSalesAdminEmployee(userLogin, delegator) || SalesPartyUtil.isSalesAdminManagerEmployee(userLogin, delegator)) {
					isAcceptView = true;
				}
				EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
				if (isAcceptView) {
					listIterator = delegator.find("ProductPromo", tmpConditon, null, null, listSortFields, opts);
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductPromo service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	public static Map<String, Object> getProductPromoTypesByChannel(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	String salesMethodChannel = (String) context.get("salesMethodChannel");
    	List<GenericValue> listIterator = new ArrayList<GenericValue>();
		try {
			/*List<EntityCondition> condsPT = FastList.newInstance();
			condsPT.add(EntityCondition.makeCondition("productPromoTypeId", "EXHIBITED"));
			condsPT.add(EntityCondition.makeCondition("productPromoTypeId", "ACCUMULATE"));
			condsPT.add(EntityCondition.makeCondition("productPromoTypeId", "PROMOTION"));
			condsPT.add(EntityCondition.makeCondition("productPromoTypeId", "OTHER"));
			List<EntityCondition> condsMain = FastList.newInstance();
			condsMain.add(EntityCondition.makeCondition(condsPT, EntityOperator.OR));*/
			List<EntityCondition> condsMain = FastList.newInstance();
			condsMain.add(EntityCondition.makeCondition("productPromoTypeId", EntityOperator.NOT_EQUAL, null));
			condsMain.add(EntityCondition.makeCondition("productPromoTypeGroupId", salesMethodChannel));
			listIterator = delegator.findList("ProductPromoType", EntityCondition.makeCondition(condsMain, EntityOperator.AND), null, null, null, false);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductPromo service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listProductPromoType", listIterator);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListExhibitedRegister(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			if (parameters.containsKey("productPromoTypeId") && UtilValidate.isNotEmpty(parameters.get("productPromoTypeId"))) {
				String productPromoTypeId = parameters.get("productPromoTypeId")[0];
				if (UtilValidate.isNotEmpty(productPromoTypeId)) {
					listAllConditions.add(EntityCondition.makeCondition("productPromoTypeId", productPromoTypeId));
				}
			}
			if (parameters.containsKey("statusId") && UtilValidate.isNotEmpty(parameters.get("statusId"))) {
				String[] statusIdArr = parameters.get("statusId");
				Set<String> statusIds = FastSet.newInstance();
				for (String item : statusIdArr) {
					statusIds.add(item);
				}
				if (UtilValidate.isNotEmpty(statusIds)) {
					listAllConditions.add(EntityCondition.makeCondition("registerStatus", EntityOperator.IN, statusIds));
				}
			}
			if (parameters.containsKey("promoMarkValue") && UtilValidate.isNotEmpty(parameters.get("promoMarkValue"))) {
				String promoMarkValue = parameters.get("promoMarkValue")[0];
				if (UtilValidate.isNotEmpty(promoMarkValue) && "N".equals(promoMarkValue)) {
					listAllConditions.add(EntityCondition.makeCondition("promoMarkValue", EntityOperator.EQUALS, null));
				}
			}
			EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("ProductPromoRegisterDetail", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListExhibitedRegister service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	public static String changeStatusExhibitedRegisters(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String listData = request.getParameter("listData");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		
		JSONArray json = new JSONArray();
		if(UtilValidate.isNotEmpty(listData)){
			 json = JSONArray.fromObject(listData);
		}
		if (json != null && json.size() > 0) {
			List<GenericValue> toBeStored = new LinkedList<GenericValue>();
			for (int i = 0; i < json.size(); i++) {
				JSONObject groupItem = json.getJSONObject(i);
				String productPromoRegisterId = groupItem.getString("productPromoRegisterId");
				String isApprove = groupItem.getString("isApprove");
				String newStatus = "REG_PROMO_CANCELED";
				if ("Y".equals(isApprove)) {
					/*if (SecurityUtil.hasRole("DELYS_SALESADMIN_GT", (String) userLogin.get("partyId"), delegator)) {
						newStatus = "REG_PROMO_ACCEPTED";
					} else if (SecurityUtil.hasRole("DELYS_ASM_GT", (String) userLogin.get("partyId"), delegator)) {
						newStatus = "REGPR_ASM_ACCEPTED";
					} else if (SecurityUtil.hasRole("DELYS_SALESSUP_GT", (String) userLogin.get("partyId"), delegator)) {
						newStatus = "REGPR_SUP_ACCEPTED";
					}*/
					//List<GenericValue> userLoginRole = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", userLogin.get("partyId"), "roleTypeId", "DELYS_SALESSUP_GT"), null, false);
					if (UtilValidate.isNotEmpty(SalesPartyUtil.isSupervisorEmployee(userLogin, delegator))) {
						newStatus = "REGPR_SUP_ACCEPTED";
					}
					//List<GenericValue> userLoginRole2 = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", userLogin.get("partyId"), "roleTypeId", "DELYS_ASM_GT"), null, false);
					if (UtilValidate.isNotEmpty(SalesPartyUtil.isAsmEmployee(userLogin, delegator))) {
						newStatus = "REGPR_ASM_ACCEPTED";
					}
					//List<GenericValue> userLoginRole3 = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", userLogin.get("partyId"), "roleTypeId", "DELYS_SALESADMIN_GT"), null, false);
					if (UtilValidate.isNotEmpty(SalesPartyUtil.isSalesAdminEmployee(userLogin, delegator))) {
						newStatus = "REG_PROMO_ACCEPTED";
					}
				}
				
				if (UtilValidate.isNotEmpty(productPromoRegisterId)) {
					try {
						GenericValue itemData = delegator.findOne("ProductPromoRegister", UtilMisc.toMap("productPromoRegisterId", productPromoRegisterId), false);
						if (itemData != null) {
							itemData.put("registerStatus", newStatus);
							toBeStored.add(itemData);
						}
					} catch (GenericEntityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			try {
	            // store line items, etc so that they will be there for the foreign key checks
	            delegator.storeAll(toBeStored);
	        } catch (GenericEntityException e) {
	            Debug.logError(e, "Problem with store all data changeStatusExhibitedRegisters event", module);
	            return "error";
	        }
		}
		return "success";
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListExhibitedMark(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			if (parameters.containsKey("productPromoRegisterId") && UtilValidate.isNotEmpty(parameters.get("productPromoRegisterId"))) {
				String productPromoRegisterId = parameters.get("productPromoRegisterId")[0];
				if (UtilValidate.isNotEmpty(productPromoRegisterId)) {
					listAllConditions.add(EntityCondition.makeCondition("productPromoRegisterId", productPromoRegisterId));
				}
			}
			EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("ProductPromoMarking", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListExhibitedMark service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	public static String markingExhibitedRegisters(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String listData = request.getParameter("listData");
		
		JSONArray json = new JSONArray();
		if(UtilValidate.isNotEmpty(listData)){
			 json = JSONArray.fromObject(listData);
		}
		if (json != null && json.size() > 0) {
			List<GenericValue> toBeStored = new LinkedList<GenericValue>();
			for (int i = 0; i < json.size(); i++) {
				JSONObject groupItem = json.getJSONObject(i);
				String productPromoRegisterId = groupItem.getString("productPromoRegisterId");
				String promoMarkValue = groupItem.getString("promoMarkValue");
				if (UtilValidate.isNotEmpty(productPromoRegisterId)) {
					try {
						GenericValue itemData = delegator.findOne("ProductPromoRegister", UtilMisc.toMap("productPromoRegisterId", productPromoRegisterId), false);
						if (itemData != null) {
							List<GenericValue> promoMarkValueSI = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusId", promoMarkValue, "statusTypeId", "EXH_MARKING_STTS"), null, false);
							if (UtilValidate.isNotEmpty(promoMarkValueSI)) {
								itemData.put("promoMarkValue", promoMarkValue);
								toBeStored.add(itemData);
							}
						}
					} catch (GenericEntityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			try {
	            // store line items, etc so that they will be there for the foreign key checks
	            delegator.storeAll(toBeStored);
	        } catch (GenericEntityException e) {
	            Debug.logError(e, "Problem with store all data markingExhibitedRegisters event", module);
	            return "error";
	        }
		}
		return "success";
	}
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListExhibitedAgreement(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("agreementTypeId", "PROMO_EXHIBITED_AGRE"));
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		if (UtilValidate.isEmpty(listSortFields)) {
    			listSortFields = FastList.newInstance();
    			listSortFields.add("-fromDate");
    			listSortFields.add("-agreementId");
    		}
    		listIterator = delegator.find("AgreementAndProductPromoRegister", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListExhibitedAgreement service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
}
