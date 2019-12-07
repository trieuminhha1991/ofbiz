package com.olbius.salesmtl.requirement;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.olbius.common.util.EntityMiscUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.order.OrderWorker;
import com.olbius.basesales.util.NotificationWorker;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.security.util.SecurityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;

public class RequirementServices {
	public static final String module = RequirementServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
	public static final String resource_error = "BaseSalesErrorUiLabels";
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createReqDeliveryOrder(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		Security security = ctx.getSecurity();
		if (!SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "REQ_DELIVERY_ORDER_NEW")) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotCreatePermission", locale));
		}
		
		List<String> orderListParam = (List<String>) context.get("orderList");
		String description = (String) context.get("description");
		String requirementStartDateStr = (String) context.get("requirementStartDate");
		//String requirementId = (String) context.get("requirementId");
        
        Timestamp requirementStartDate = null;
        try {
	        if (UtilValidate.isNotEmpty(requirementStartDateStr)) {
	        	Long requirementStartDateL = Long.parseLong(requirementStartDateStr);
	        	requirementStartDate = new Timestamp(requirementStartDateL);
	        }
        } catch (Exception e) {
        	Debug.logError(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenFormatDateTime", locale));
        }
        
        List<String> errorMessageList = FastList.newInstance();
        if (UtilValidate.isEmpty(requirementStartDateStr)) {
        	errorMessageList.add(UtilProperties.getMessage(resource_error, "BSFromDateMustNotBeEmpty", locale));
        }
        
        // report error messages if any
        if (errorMessageList.size() > 0) {
            return ServiceUtil.returnError(errorMessageList);
        }
        
        boolean isJsonRole = false;
    	if (UtilValidate.isNotEmpty(orderListParam) && orderListParam.size() > 0){
    		if (orderListParam.get(0) instanceof String) isJsonRole = true;
    	}
    	List<String> orderList = new ArrayList<String>();
    	if (isJsonRole){
			String orderListStr = "[" + (String) orderListParam.get(0) + "]";
			JSONArray jsonArray = new JSONArray();
			if (UtilValidate.isNotEmpty(orderListStr)) {
				jsonArray = JSONArray.fromObject(orderListStr);
			}
			if (jsonArray != null && jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					orderList.add(jsonArray.getString(i));
				}
			}
    	} else {
    		orderList = (List<String>) context.get("orderList");
    	}
    	
    	if (UtilValidate.isEmpty(orderList)) {
    		return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouNotYetChooseRow", locale));
    	}
		
    	// begin create requirement
    	String requirementId = "DELORD" + delegator.getNextSeqId("Requirement");
    	
    	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
	    GenericValue requirement = delegator.makeValue("Requirement", UtilMisc.<String, Object>toMap(
    			"requirementId", requirementId, 
	    		"requirementTypeId", "DELIVERY_ORDER_REQ", 
	    		"description", description, 
	    		"statusId", "REQ_CREATED", 
	    		"requiredByDate", nowTimestamp, 
	    		"requirementStartDate", requirementStartDate, 
	    		"createdByUserLogin", userLogin.get("userLoginId")));
		GenericValue requirementResult = null;
		// first try to create the requirement; if this does not fail, continue.
	    try {
			requirementResult = delegator.create(requirement);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot create Requirement entity; problems with insert", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing",locale));
        }

	    if (requirementResult != null){
			GenericValue requirementStatus = delegator.makeValue("RequirementStatus", UtilMisc.<String, Object>toMap(
					"requirementId", requirementResult.getString("requirementId"),
					"statusId", "REQ_CREATED",
					"statusDate", UtilDateTime.nowTimestamp(),
					"statusUserLogin", userLogin.get("userLoginId")));
			try {
				delegator.create(requirementStatus);
			} catch (GenericEntityException e) {
				Debug.logError(e, "Cannot create Requirement status entity; problems with insert", module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing",locale));
			}
		}
	    
	    try {
		    List<GenericValue> tobeStored = new LinkedList<GenericValue>();
		    // int nextItemSeq = 1;
		    List<EntityCondition> listCond = FastList.newInstance();
		    listCond.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"));
		    listCond.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_REJECTED"));
		    for (String orderId : orderList) {
		    	List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(EntityCondition.makeCondition(listCond), EntityOperator.AND, EntityCondition.makeCondition("orderId", orderId)), null, null, null, false);
		    	
		    	if (UtilValidate.isNotEmpty(orderItems)) {
		    		for (GenericValue orderItem : orderItems) {
		    			GenericValue item = delegator.makeValue("OrderRequirementCommitment");
				    	item.set("orderId", orderItem.get("orderId"));
				    	item.set("orderItemSeqId", orderItem.get("orderItemSeqId"));
				    	item.set("requirementId", requirementId);
				    	item.set("quantity", orderItem.get("quantity"));
				    	tobeStored.add(item);
		    		}
		    	}
		    	// String reqItemSeqId = UtilFormatOut.formatPaddedNumber(nextItemSeq, 5);
		    	// item.set("reqItemSeqId", reqItemSeqId);
		    	// item.set("description", description);
	            // nextItemSeq++;
		    }
		    
		    if (UtilValidate.isNotEmpty(tobeStored)) {
		    	delegator.storeAll(tobeStored);
		    }
	    } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot create OrderRequirementCommitment entity; problems with insert", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing",locale));
        }
	    
	    try {
	    	com.olbius.basesales.util.NotificationWorker.sendNotifyWhenCreateReqDeliveryOrder(delegator, dispatcher, locale, requirementId, userLogin);
	    } catch (GenericEntityException | GenericServiceException e) {
	    	Debug.logWarning(e, "Cannot send notification when create OrderRequirementCommitment; problems with insert", module);
		}
	    
		successResult.put("requirementId", requirementId);
		return successResult;
	}
	
	@SuppressWarnings({ "unchecked" })
    public static Map<String, Object> jqGetListOrderReqDelivery(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		// Locale locale = (Locale) context.get("locale");
		Security security = ctx.getSecurity();
		
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	//EntityListIterator listIterator = null;
    	List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	try {
    		//check permission for each order type
    		boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "REQ_DELIVERY_ORDER_VIEW");
			if (!hasPermission) {
				Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to run manual payment transaction!", module);
	            //return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSTransactionNotAuthorized", locale));
				return successResult;
			}
			
			String requirementId = null;
    		if (parameters.containsKey("requirementId") && parameters.get("requirementId").length > 0) {
    			requirementId = parameters.get("requirementId")[0];
			}
			
    		if (UtilValidate.isNotEmpty(requirementId)) {
    			listAllConditions.add(EntityCondition.makeCondition("requirementId", requirementId));
    			opts.setDistinct(true);
    			List<GenericValue> listOrderDelivery = delegator.findList("OrderReqCommitDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
    			if (UtilValidate.isNotEmpty(listOrderDelivery)) {
    				for (GenericValue orderDel : listOrderDelivery) {
    					Map<String, Object> orderMap = orderDel.getAllFields();
    					BigDecimal totalWeight = OrderWorker.getTotalWeightProduct(delegator, dispatcher, orderDel.getString("orderId"));
    					orderMap.put("totalWeight", totalWeight);
    					listIterator.add(orderMap);
    				}
    			}
    		}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListOrderReqDelivery service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	@SuppressWarnings({ "unchecked" })
    public static Map<String, Object> jqGetListOrderItemReqDelivery(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		// Locale locale = (Locale) context.get("locale");
		Security security = ctx.getSecurity();
		
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	//EntityListIterator listIterator = null;
    	List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	try {
    		//check permission for each order type
			boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "REQ_DELIVERY_ORDER_VIEW");
    		if (!hasPermission) {
				Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to run manual payment transaction!", module);
	            //return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSTransactionNotAuthorized", locale));
				return successResult;
			}
			
			String requirementId = null;
    		if (parameters.containsKey("requirementId") && parameters.get("requirementId").length > 0) {
    			requirementId = parameters.get("requirementId")[0];
			}
			
    		if (UtilValidate.isNotEmpty(requirementId)) {
    			listAllConditions.add(EntityCondition.makeCondition("requirementId", requirementId));
    			List<GenericValue> listOrderItemDelivery = delegator.findList("OrderReqCommitItemDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
    			if (UtilValidate.isNotEmpty(listOrderItemDelivery)) {
    				Map<String, Map<String, Object>> productMap = FastMap.newInstance();
    				for (GenericValue orderItemDel : listOrderItemDelivery) {
    					/*Map<String, Object> orderMap = orderItemDel.getAllFields();
    					BigDecimal totalWeight = OrderWorker.getTotalWeightProductOrderItem(delegator, dispatcher, orderItemDel.getString("orderId"), orderItemDel.getString("orderItemSeqId"));
    					orderMap.put("totalWeight", totalWeight);
    					listIterator.add(orderMap);*/
    					String productId = orderItemDel.getString("productId");
    					if (productMap.containsKey(productId)) {
    						// update
    						Map<String, Object> item = productMap.get(productId);
    						BigDecimal quantity = (BigDecimal) item.get("quantity");
    						if (orderItemDel.get("itemRemainQuantity") != null) {
    							quantity = quantity.add(orderItemDel.getBigDecimal("itemRemainQuantity"));
    						}
    						item.put("quantity", quantity);
    					} else {
    						// add
    						BigDecimal quantity = (BigDecimal) orderItemDel.get("itemRemainQuantity");
    						if (quantity == null) quantity = BigDecimal.ZERO;
    						Map<String, Object> item = FastMap.newInstance();
    						item.put("productId", orderItemDel.get("productId"));
    						item.put("productCode", orderItemDel.get("productCode"));
    						item.put("quantity", quantity);
    						item.put("primaryProductCategoryId", orderItemDel.get("primaryProductCategoryId"));
    						item.put("productName", orderItemDel.get("productName"));
    						productMap.put(productId, item);
    					}
    				}
    				
    				for (Map.Entry<String, Map<String, Object>> item : productMap.entrySet()) {
    					String productId = item.getKey();
    					Map<String, Object> productItem = item.getValue();
    					BigDecimal quantity = (BigDecimal) productItem.get("quantity");
    					
    					Map<String, Object> orderMap = productItem;
    					BigDecimal totalWeight = OrderWorker.getTotalWeightProduct(delegator, dispatcher, productId, quantity);
    					orderMap.put("totalWeight", totalWeight);
    					listIterator.add(orderMap);
    				}
    			}
    			
    			
    		}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListOrderItemReqDelivery service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> jqGetListReqDeliveryOrder(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		// LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		// Locale locale = (Locale) context.get("locale");
		Security security = ctx.getSecurity();
		
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		try {
			//check permission for each order type
			boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "REQ_DELIVERY_ORDER_VIEW");
			if (!hasPermission) {
				Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to run manual payment transaction!", module);
				//return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSTransactionNotAuthorized", locale));
				return successResult;
			}
			listAllConditions.add(EntityCondition.makeCondition("requirementTypeId", "DELIVERY_ORDER_REQ"));
//			listIterator = delegator.find("Requirement", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "Requirement", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListReqDeliveryOrder service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> jqGetListReqSalesTransfer(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		// LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		// Locale locale = (Locale) context.get("locale");
		Security security = ctx.getSecurity();
		
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		// Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		try {
			//check permission for each order type
			boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "REQSALES_TRANSFER_VIEW");
			if (!hasPermission) {
				Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to run manual payment transaction!", module);
				//return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSTransactionNotAuthorized", locale));
				return successResult;
			}
			List<EntityCondition> condOrs = FastList.newInstance();
			condOrs.add(EntityCondition.makeCondition(EntityCondition.makeCondition("requirementTypeId", "TRANSFER_REQUIREMENT"), 
					EntityOperator.AND, EntityCondition.makeCondition("reasonEnumId", "TRANS_DISTRIBUTOR")));
			condOrs.add(EntityCondition.makeCondition("requirementTypeId", "BORROW_REQUIREMENT"));
			condOrs.add(EntityCondition.makeCondition("requirementTypeId", "PAY_REQUIREMENT"));
			condOrs.add(EntityCondition.makeCondition("requirementTypeId", "CHANGEDATE_REQUIREMENT"));
			listAllConditions.add(EntityCondition.makeCondition(condOrs, EntityOperator.OR));
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("-requirementStartDate");
				listSortFields.add("-requiredByDate");
			}
			listIterator = delegator.find("Requirement", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListReqSalesTransfer service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> jqGetListFacilityByOwner(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		try {
			String ownerId = null;
    		if (parameters.containsKey("partyId") && parameters.get("partyId").length > 0) {
    			ownerId = parameters.get("partyId")[0];
			}
			if (UtilValidate.isNotEmpty(ownerId)) {
				listAllConditions.add(EntityCondition.makeCondition("ownerPartyId", ownerId));
				listIterator = delegator.find("Facility", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListFacilityByOwner service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> jqGetListFacilityByAdmin(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		try {
			String orgId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			String managerId = null;
			if (parameters.containsKey("partyId") && parameters.get("partyId").length > 0) {
				managerId = parameters.get("partyId")[0];
			}
			if (UtilValidate.isNotEmpty(orgId) && UtilValidate.isNotEmpty(managerId)) {
				listAllConditions.add(EntityCondition.makeCondition("ownerPartyId", orgId));
				listAllConditions.add(EntityCondition.makeCondition("partyId", managerId));
				listAllConditions.add(EntityCondition.makeCondition("roleTypeId", "FACILITY_ADMIN"));
				listAllConditions.add(EntityUtil.getFilterByDateExpr());
				listIterator = delegator.find("FacilityAndFacilityParty", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListFacilityByOwnerAndManager service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> sendNotifyWhenCreateReqSalesTransfer(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	
    	String requirementId = (String) context.get("requirementId");
    	try {
    		GenericValue requirement = delegator.findOne("Requirement", UtilMisc.toMap("requirementId", requirementId), false);
    		if (requirement == null) return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSRecordIsNotFound", locale));
    		
    		NotificationWorker.sendNotifyWhenCreateReqSalesTransfer(delegator, dispatcher, locale, requirementId, userLogin);
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling sendNotifyWhenCreateReqSalesTransfer service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	
    	return successResult;
    }
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> jqGetListReasonEnumReqForSales(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		try {
			String requirementTypeId = null;
    		if (parameters.containsKey("requirementTypeId") && parameters.get("requirementTypeId").length > 0) {
    			requirementTypeId = parameters.get("requirementTypeId")[0];
			}
			if (UtilValidate.isNotEmpty(requirementTypeId)) {
				// get list relationship between requirement and reason enum
				List<String> enumTypeIds = EntityUtil.getFieldListFromEntityList(
						delegator.findList("RequirementEnumType", EntityCondition.makeCondition(
									EntityCondition.makeCondition("requirementTypeId", requirementTypeId), EntityOperator.AND,
									EntityUtil.getFilterByDateExpr()
								), UtilMisc.toSet("enumTypeId"), null, null, false), "enumTypeId", true);
				if ("TRANSFER_REQUIREMENT".equals(requirementTypeId)) {
					listAllConditions.add(EntityCondition.makeCondition("enumId", "TRANS_DISTRIBUTOR"));
				}
				listAllConditions.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.IN, enumTypeIds));
				listIterator = delegator.find("Enumeration", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListReasonEnumReqForSales service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
}
