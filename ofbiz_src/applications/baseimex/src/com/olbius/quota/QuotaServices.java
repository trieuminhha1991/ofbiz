package com.olbius.quota;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.common.util.EntityMiscUtil;
import com.olbius.product.util.ProductUtil;
import com.olbius.services.JqxWidgetSevices;

import javolution.util.FastList;
import javolution.util.FastMap;

public class QuotaServices {
	
	public static final String module = QuotaServices.class.getName();
	public static final String resource = "BaseImExUiLabels";
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetQuotaHeaders(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<GenericValue> listContainers = FastList.newInstance();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		listAllConditions.add(EntityCondition.makeCondition("organizationPartyId", company));
		
		String quotaTypeId = null;
		if (parameters.containsKey("quotaTypeId") && parameters.get("quotaTypeId").length > 0) {
			quotaTypeId = (String) parameters.get("quotaTypeId")[0];
			if (UtilValidate.isNotEmpty(quotaTypeId)) {
				listAllConditions.add(EntityCondition.makeCondition("quotaTypeId", quotaTypeId));
			}
		}
		if (listSortFields.isEmpty()){
			listSortFields.add("-quotaId");
		}
		try {
			listContainers = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "QuotaHeader", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetQuotaHeaders service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listContainers);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetQuotaTypes(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<GenericValue> listContainers = FastList.newInstance();
		try {
			listContainers = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "QuotaType", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetQuotaTypes service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listContainers);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetQuotaItems(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<GenericValue> listContainers = FastList.newInstance();
		
		String quotaId = null;
		if (parameters.containsKey("quotaId") && parameters.get("quotaId").length > 0) {
			quotaId = (String) parameters.get("quotaId")[0];
			if (UtilValidate.isNotEmpty(quotaId)) {
				listAllConditions.add(EntityCondition.makeCondition("quotaId", quotaId));
			}
		}
		try {
			listContainers = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "QuotaItemAndProduct", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetQuotaItems service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listContainers);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> createQuotaHeader(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> success = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String productStr = (String)context.get("listProducts");
		String quotaCode = null;
		if (UtilValidate.isNotEmpty(context.get("quotaCode"))) {
			quotaCode = (String)context.get("quotaCode");
		}
		GenericValue map = delegator.makeValue("QuotaHeader");
		String quotaId = delegator.getNextSeqId("QuotaHeader");
		if (UtilValidate.isEmpty(quotaCode)) {
			quotaCode = quotaId;
		}
		map.put("quotaId", quotaId);
		map.put("quotaCode", quotaCode);
		map.put("statusId", "QUOTA_CREATED");
		map.put("quotaTypeId", "IMPORT_QUOTA");
		map.put("createdByUserLogin", userLogin.getString("userLoginId"));
		map.put("createdDate", UtilDateTime.nowTimestamp());
		map.put("organizationPartyId", organizationPartyId);
		map.setNonPKFields(context);
		try {
			delegator.createOrStore(map);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: createQuotaHeader error! " + e.toString());
		}
		
		List<Map<String, Object>> listProducts = null;
		try {
			listProducts = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", productStr);
		} catch (ParseException e) {
			return ServiceUtil.returnError("OLBIUS: createQuotaHeader error when JqxWidgetSevices.convert ! " + e.toString());
		}
    	LocalDispatcher dispatcher = ctx.getDispatcher();
		for (Map<String, Object> pt : listProducts) {
			if (UtilValidate.isNotEmpty(pt.get("quotaQuantity")) && UtilValidate.isNotEmpty(pt.get("productId"))) {
				String productId = (String)pt.get("productId");
				String quotaQuantityStr = (String)pt.get("quotaQuantity");
				if (UtilValidate.isNotEmpty(quotaQuantityStr) && UtilValidate.isNotEmpty(productId)) {
					BigDecimal quotaQuantity = new BigDecimal(quotaQuantityStr);
					String uomId = null; 
					if (UtilValidate.isNotEmpty(pt.get("uomId"))) {
						uomId = (String)pt.get("uomId");
					} else {
						GenericValue objProduct = null;
						try {
							objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findOne Product: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
						if (ProductUtil.isWeightProduct(delegator, productId)){
							uomId = objProduct.getString("weightUomId");
						} else {
							uomId = objProduct.getString("quantityUomId");
						}
					}
					Map<String, Object> mapTmp = FastMap.newInstance();
					mapTmp.put("quotaId", quotaId);
					mapTmp.put("quotaQuantity", quotaQuantity);
					mapTmp.put("availableQuantity", BigDecimal.ZERO);
					mapTmp.put("productId", productId);
					mapTmp.put("uomId", uomId);
					if (UtilValidate.isNotEmpty(pt.get("fromDate"))) {
						mapTmp.put("fromDate", new Timestamp(new Long((String)pt.get("fromDate"))));
					}
					if (UtilValidate.isNotEmpty(pt.get("thruDate"))) {
						mapTmp.put("thruDate", new Timestamp(new Long((String)pt.get("thruDate"))));
					}
					mapTmp.put("userLogin", userLogin);
					try {
						dispatcher.runSync("createQuotaItem", mapTmp);
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: createQuotaItem error! " + e.toString());
					}
				}
			}
		}
		success.put("quotaId", quotaId);
		return success;
	}
	
	public static Map<String,Object> changeQuotaStatus(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> success = ServiceUtil.returnSuccess();
		String quotaId = (String)context.get("quotaId");
		String statusId = (String)context.get("statusId");
		Delegator delegator = ctx.getDelegator();
		GenericValue objQuota = null;
		try {
			objQuota = delegator.findOne("QuotaHeader", false, UtilMisc.toMap("quotaId", quotaId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne QuotaHeader: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (UtilValidate.isEmpty(objQuota)) {
			String errMsg = "OLBIUS: Fatal error when changeQuotaStatus - QuotaHeader not found!: " + quotaId;
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		objQuota.put("statusId", statusId);
		try {
			delegator.store(objQuota);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when store QuotaHeader: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		String itemStatusId = null;
		switch (statusId) {
		case "QUOTA_APPROVED":
			itemStatusId = "QUOTA_ITEM_APPROVED";
			break;
		case "QUOTA_COMPLETED":
			itemStatusId = "QUOTA_ITEM_COMPLETED";
			break;
		case "QUOTA_CANCELLED":
			itemStatusId = "QUOTA_ITEM_CANCELLED";
			break;
		default:
			break;
		}
		if (UtilValidate.isNotEmpty(itemStatusId)) {
			
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("quotaId", quotaId));
			conds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, itemStatusId));
			conds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "QUOTA_ITEM_CANCELLED"));
			
			List<GenericValue> listQuotaItem = FastList.newInstance();
			try {
				listQuotaItem = delegator.findList("QuotaItem", EntityCondition.makeCondition(conds), null,
						null, null, false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList QuotaItem: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			for (GenericValue item : listQuotaItem) {
				if ("QUOTA_ITEM_APPROVED".equals(itemStatusId)){
					item.put("availableQuantity", item.getBigDecimal("quotaQuantity"));
				}
				item.put("statusId", itemStatusId);
				try {
					delegator.store(item);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when storeByCondition QuotaItem: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
			}
			
		}
		success.put("quotaId", quotaId);
		success.put("statusId", statusId);
		return success;
	}
	
	public static Map<String,Object> changeQuotaItemStatus(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> success = ServiceUtil.returnSuccess();
		String quotaId = (String)context.get("quotaId");
		String quotaItemSeqId = (String)context.get("quotaItemSeqId");
		String statusId = (String)context.get("statusId");
		Delegator delegator = ctx.getDelegator();
		GenericValue objQuotaItem = null;
		try {
			objQuotaItem = delegator.findOne("QuotaItem", false, UtilMisc.toMap("quotaId", quotaId, "quotaItemSeqId", quotaItemSeqId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne QuotaItem: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (UtilValidate.isEmpty(objQuotaItem)) {
			String errMsg = "OLBIUS: Fatal error when changeQuotaItemStatus - QuotaItem not found!: " + quotaId;
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		String curStatusId = objQuotaItem.getString("statusId");
		if (UtilValidate.isNotEmpty(curStatusId) && UtilValidate.isNotEmpty(statusId)) {
			if (!statusId.equals(curStatusId)){
				objQuotaItem.put("statusId", statusId);
				try {
					delegator.store(objQuotaItem);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when store QuotaItem: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
			}
		}
		success.put("quotaId", quotaId);
		success.put("quotaItemSeqId", quotaId);
		success.put("statusId", statusId);
		return success;
	}
	
	public static Map<String,Object> checkQuotaStatus(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> success = ServiceUtil.returnSuccess();
		String quotaId = (String)context.get("quotaId");
		Delegator delegator = ctx.getDelegator();
		GenericValue objQuotaHeader = null;
		try {
			objQuotaHeader = delegator.findOne("QuotaHeader", false, UtilMisc.toMap("quotaId", quotaId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne QuotaHeader: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (UtilValidate.isEmpty(objQuotaHeader)) {
			return success;
		}
		
		List<GenericValue> quotaItems = null;
        try {
        	quotaItems = delegator.findByAnd("QuotaItem", UtilMisc.toMap("quotaId", quotaId), null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get QuotaItem records", module);
			return ServiceUtil.returnError(e.toString());
        }

        String currentStatusId = objQuotaHeader.getString("statusId");

        boolean allCanceled = true;
        boolean allCompleted = true;
        boolean allApproved = true;
        for (GenericValue item : quotaItems) {
            String statusId = item.getString("statusId");
            if (!"QUOTA_ITEM_CANCELLED".equals(statusId)) {
                allCanceled = false;
                if (!"QUOTA_ITEM_COMPLETED".equals(statusId)) {
                    allCompleted = false;
                	if (!"QUOTA_ITEM_APPROVED".equals(statusId)) {
                		allApproved = false;
                		break;
                    }
                }
            }
        }

        String newStatus = null;
        if (allCanceled) {
            newStatus = "QUOTA_CANCELLED";
        } else if (allCompleted) {
            newStatus = "QUOTA_COMPLETED";
        } else if (allApproved) {
        	newStatus = "QUOTA_APPROVED";
        }  

        if (newStatus != null && !newStatus.equals(currentStatusId)) {
        	LocalDispatcher dispatcher = ctx.getDispatcher();
        	GenericValue userLogin = (GenericValue) context.get("userLogin");
        	try {
                Map<String, Object> serviceContext = UtilMisc.<String, Object>toMap("quotaId", quotaId, "statusId", newStatus, "userLogin", userLogin);
                Map<String, Object> newSttsResult = null;
                newSttsResult = dispatcher.runSync("changeQuotaStatus", serviceContext);
                if (ServiceUtil.isError(newSttsResult)) {
                    return ServiceUtil.returnError(ServiceUtil.getErrorMessage(newSttsResult));
                }
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problem calling the changeQuotaStatus service", module);
                return ServiceUtil.returnError(e.toString());
            }
        }
		return success;
	}
		
	@SuppressWarnings("unchecked")
	public static Map<String,Object> updateQuotaHeader(DispatchContext ctx, Map<String, Object> context){

		Map<String, Object> success = ServiceUtil.returnSuccess();
		String quotaId = (String)context.get("quotaId");
		Delegator delegator = ctx.getDelegator();
		GenericValue objQuotaHeader = null;
		try {
			objQuotaHeader = delegator.findOne("QuotaHeader", false, UtilMisc.toMap("quotaId", quotaId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne QuotaHeader: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (UtilValidate.isEmpty(objQuotaHeader)) {
			String errMsg = "OLBIUS: Fatal error when updateQuotaHeader - QuotaHeader not found!: " + quotaId;
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		objQuotaHeader.setNonPKFields(context);
		try {
			delegator.store(objQuotaHeader);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when Store QuotaHeader: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		// update
		if (UtilValidate.isNotEmpty(context.get("listProductUpdates"))) {
			String productStr = (String)context.get("listProductUpdates");
			List<Map<String, Object>> listProducts = null;
			try {
				listProducts = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", productStr);
			} catch (ParseException e) {
				return ServiceUtil.returnError("OLBIUS: updateQuotaHeader error when JqxWidgetSevices.convert ! " + e.toString());
			}
			GenericValue userLogin = (GenericValue) context.get("userLogin");
	    	LocalDispatcher dispatcher = ctx.getDispatcher();
			for (Map<String, Object> pt : listProducts) {
				if (UtilValidate.isNotEmpty(pt.get("productId")) && UtilValidate.isNotEmpty(pt.get("quotaItemSeqId"))) {
					String quotaItemSeqId = (String)pt.get("quotaItemSeqId");
					Map<String, Object> mapTmp = FastMap.newInstance();
					mapTmp.put("quotaId", quotaId);
					mapTmp.put("quotaItemSeqId", quotaItemSeqId);
					
					if (UtilValidate.isNotEmpty(pt.get("uomId"))) {
						mapTmp.put("uomId", pt.get("uomId"));
					}
					if (UtilValidate.isNotEmpty(pt.get("quotaQuantity"))) {
						String quantityStr = (String)pt.get("quotaQuantity");
						if (UtilValidate.isNotEmpty(quantityStr)) {
							mapTmp.put("quotaQuantity", new BigDecimal(quantityStr));
						}
					}
					
					if (UtilValidate.isNotEmpty(pt.get("fromDate"))) {
						mapTmp.put("fromDate", new Timestamp(new Long((String)pt.get("fromDate"))));
					}
					if (UtilValidate.isNotEmpty(pt.get("thruDate"))) {
						mapTmp.put("thruDate", new Timestamp(new Long((String)pt.get("thruDate"))));
					}
					
					mapTmp.put("userLogin", userLogin);
					try {
						dispatcher.runSync("updateQuotaItem", mapTmp);
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: updateQuotaHeaderItem error! " + e.toString());
					}
				}
			}
		}
		// add
		if (UtilValidate.isNotEmpty(context.get("listProductNews"))) {
			String productStr = (String)context.get("listProductNews");
			List<Map<String, Object>> listProducts = null;
			try {
				listProducts = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", productStr);
			} catch (ParseException e) {
				return ServiceUtil.returnError("OLBIUS: updateQuotaHeader error when JqxWidgetSevices.convert ! " + e.toString());
			}
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			LocalDispatcher dispatcher = ctx.getDispatcher();
			for (Map<String, Object> pt : listProducts) {
				if (UtilValidate.isNotEmpty(pt.get("quotaQuantity")) && UtilValidate.isNotEmpty(pt.get("productId"))) {
					String productId = (String)pt.get("productId");
					String quantityStr = (String)pt.get("quotaQuantity");
					if (UtilValidate.isNotEmpty(quantityStr) && UtilValidate.isNotEmpty(productId)) {
						BigDecimal quantity = new BigDecimal(quantityStr);
						String uomId = null; 
						if (UtilValidate.isNotEmpty(pt.get("uomId"))) {
							uomId = (String)pt.get("uomId");
						} else {
							GenericValue objProduct = null;
							try {
								objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
							} catch (GenericEntityException e) {
								String errMsg = "OLBIUS: Fatal error when findOne Product: " + e.toString();
								Debug.logError(e, errMsg, module);
								return ServiceUtil.returnError(errMsg);
							}
							if (ProductUtil.isWeightProduct(delegator, productId)){
								uomId = objProduct.getString("weightUomId");
							} else {
								uomId = objProduct.getString("quantityUomId");
							}
						}
						Map<String, Object> mapTmp = FastMap.newInstance();
						mapTmp.put("quotaId", quotaId);
						mapTmp.put("quotaQuantity", quantity);
						mapTmp.put("productId", productId);
						mapTmp.put("uomId", uomId);
						if (UtilValidate.isNotEmpty(pt.get("fromDate"))) {
							mapTmp.put("fromDate", new Timestamp(new Long((String)pt.get("fromDate"))));
						}
						if (UtilValidate.isNotEmpty(pt.get("thruDate"))) {
							mapTmp.put("thruDate", new Timestamp(new Long((String)pt.get("thruDate"))));
						}
						mapTmp.put("userLogin", userLogin);
						try {
							dispatcher.runSync("createQuotaItem", mapTmp);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError("OLBIUS: createQuotaHeaderItem error! " + e.toString());
						}
					}
				}
			}
		}
		
		// cancel
		if (UtilValidate.isNotEmpty(context.get("listProductCancels"))) {
			String productStr = (String)context.get("listProductCancels");
			List<Map<String, Object>> listProducts = null;
			try {
				listProducts = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", productStr);
			} catch (ParseException e) {
				return ServiceUtil.returnError("OLBIUS: updateQuotaHeader error when JqxWidgetSevices.convert ! " + e.toString());
			}
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			LocalDispatcher dispatcher = ctx.getDispatcher();
			for (Map<String, Object> pt : listProducts) {
				if (UtilValidate.isNotEmpty(pt.get("productId"))) {
					String quotaItemSeqId = (String)pt.get("quotaItemSeqId");
					Map<String, Object> mapTmp = FastMap.newInstance();
					mapTmp.put("quotaId", quotaId);
					mapTmp.put("quotaItemSeqId", quotaItemSeqId);
					mapTmp.put("statusId", "QUOTA_ITEM_CANCELLED");
					mapTmp.put("userLogin", userLogin);
					try {
						dispatcher.runSync("changeQuotaItemStatus", mapTmp);
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: changeQuotaHeaderItemStatus error! " + e.toString());
					}
				}
			}
		}
		
		success.put("quotaId", quotaId);
		return success;
	}
	
	public static Map<String,Object> createQuotaItem(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> success = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String quotaId = (String)context.get("quotaId");
		
		GenericValue quotaItem = delegator.makeValue("QuotaItem");
		delegator.setNextSubSeqId(quotaItem, "quotaItemSeqId", 5, 1);
		quotaItem.put("quotaId", quotaId);
		quotaItem.put("statusId", "QUOTA_ITEM_CREATED");
		quotaItem.setNonPKFields(context);
		try {
			delegator.create(quotaItem);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: createQuotaItem error! " + e.toString());
		}
		String quotaItemSeqId = quotaItem.getString("quotaItemSeqId");
		success.put("quotaId", quotaId);
		success.put("quotaItemSeqId", quotaItemSeqId);
		return success;
	}
	
	public static Map<String,Object> updateQuotaItem(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> success = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String quotaId = (String)context.get("quotaId");
		String quotaItemSeqId = (String)context.get("quotaItemSeqId");
		GenericValue objQuotaItem = null;
		try {
			objQuotaItem = delegator.findOne("QuotaItem", false, UtilMisc.toMap("quotaId", quotaId, "quotaItemSeqId", quotaItemSeqId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne QuotaItem: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		objQuotaItem.setNonPKFields(context);
		try {
			delegator.store(objQuotaItem);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: updateQuotaItem error! " + e.toString());
		}
		BigDecimal availableQuantity = objQuotaItem.getBigDecimal("availableQuantity");
		if (UtilValidate.isNotEmpty(availableQuantity) && availableQuantity.compareTo(BigDecimal.ZERO) <= 0 && "QUOTA_ITEM_APPROVED".equals(objQuotaItem.getString("statusId"))) {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			LocalDispatcher dispatcher = ctx.getDispatcher();
			Map<String, Object> map = FastMap.newInstance();
			map.put("quotaId", quotaId);
			map.put("quotaItemSeqId", quotaItemSeqId);
			map.put("statusId", "QUOTA_ITEM_COMPLETED");
			map.put("userLogin", userLogin);
			try {
				Map<String, Object> rs = dispatcher.runSync("changeQuotaItemStatus", map);
				if (ServiceUtil.isError(rs)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(rs));
				}
			} catch (GenericServiceException e) {
				String errMsg = "OLBIUS: Fatal error when run service changeQuotaItemStatus: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
		}
		success.put("quotaId", quotaId);
		success.put("quotaItemSeqId", quotaItemSeqId);
		return success;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetProductInQuota(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<GenericValue> listContainers = FastList.newInstance();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		listAllConditions.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, company));
		try {
			listContainers = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "QuotaItemAvailableGroupByProductAlias", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetProductInQuota service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listContainers);
		return successResult;
	}
	
/*	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetProductOutOfQuota(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<GenericValue> listContainers = FastList.newInstance();
		
		listAllConditions.add(EntityCondition.makeCondition("availableQuantity", EntityOperator.LESS_THAN_EQUAL_TO, BigDecimal.ZERO));
		try {
			listContainers = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "QuotaItemGroupByProduct", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetProductOutOfQuota service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listContainers);
		return successResult;
	}*/
	
	public static Map<String,Object> getProductQuota(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> success = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String quotaId = (String)context.get("quotaId");
		List<GenericValue> listProducts = FastList.newInstance();
		GenericValue objQuota = null;
		try {
			objQuota = delegator.findOne("QuotaHeader", false, UtilMisc.toMap("quotaId", quotaId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne QuotaHeader: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (UtilValidate.isNotEmpty(objQuota)) {
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("quotaId", quotaId));
			if (!"QUOTA_CANCELLED".equals(objQuota.getString("statusId"))){
				conds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "QUOTA_ITEM_CANCELLED"));
			} 
			try {
				listProducts = delegator.findList("QuotaItemAndProduct", EntityCondition.makeCondition(conds), null, null, null,
						false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList QuotaItemAndProduct: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
		}
		
		success.put("quotaId", quotaId);
		success.put("listProducts", listProducts);
		return success;
	}
	
	public static Map<String,Object> updateQuotaFromAgreement(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> success = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String agreementId = (String)context.get("agreementId");
		GenericValue objAgreement = null;
		try {
			objAgreement = delegator.findOne("Agreement", false, UtilMisc.toMap("agreementId", agreementId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne QuotaHeader: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (UtilValidate.isNotEmpty(objAgreement) && "AGREEMENT_PROCESSING".equals(objAgreement.getString("statusId")) && "PURCHASE_AGREEMENT".equals(objAgreement.getString("agreementTypeId"))) {
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("agreementId", agreementId));
			List<GenericValue> listProducts = FastList.newInstance();
			try {
				listProducts = delegator.findList("AgreementProductAppl", EntityCondition.makeCondition(conds), null, null, null, false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList AgreementProductAppl: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			if (listProducts.isEmpty()) return success;

			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			List<EntityCondition> cond2s = FastList.newInstance();
			cond2s.add(EntityCondition.makeCondition("statusId", "QUOTA_APPROVED"));
			cond2s.add(EntityCondition.makeCondition("organizationPartyId", company));
			cond2s.add(EntityCondition.makeCondition("quotaTypeId", "IMPORT_QUOTA"));
			List<GenericValue> listQuotaHeader = FastList.newInstance();
			try {
				listQuotaHeader = delegator.findList("QuotaHeader", EntityCondition.makeCondition(cond2s), null, null,
						null, false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList QuotaHeader: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			if (!listQuotaHeader.isEmpty()){
				List<String> quotaIds = EntityUtil.getFieldListFromEntityList(listQuotaHeader, "quotaId", true);
				List<Map<String, Object>> listUpdates = FastList.newInstance();
				for (GenericValue item : listProducts) {
					String productId = item.getString("productId");
					BigDecimal quantity = item.getBigDecimal("quantity");
					if (UtilValidate.isNotEmpty(quantity) && quantity.compareTo(BigDecimal.ZERO) > 0) {
						BigDecimal remain = quantity;
						
						List<EntityCondition> condTmps = FastList.newInstance();
						condTmps.add(EntityCondition.makeCondition("productId", productId));
						condTmps.add(EntityCondition.makeCondition("statusId", "QUOTA_ITEM_APPROVED"));
						condTmps.add(EntityCondition.makeCondition("quotaId", EntityOperator.IN, quotaIds));
						condTmps.add(EntityCondition.makeCondition("availableQuantity", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
						List<GenericValue> listQuotaItem = FastList.newInstance();
						try {
							listQuotaItem = delegator.findList("QuotaItem", EntityCondition.makeCondition(condTmps),
									null, null, null, false);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList QuotaItem: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
						if (listQuotaItem.isEmpty()) continue;
						
						for (GenericValue quota : listQuotaItem) {
							BigDecimal availableQuantity = quota.getBigDecimal("availableQuantity");
							if (UtilValidate.isEmpty(availableQuantity)) continue;
							if (remain.compareTo(BigDecimal.ZERO) <= 0) break;
							
							if (remain.compareTo(availableQuantity) >= 0) {
								Map<String, Object> map = FastMap.newInstance();
								map.put("quotaId", quota.getString("quotaId"));
								map.put("quotaItemSeqId", quota.getString("quotaItemSeqId"));
								map.put("availableQuantity", BigDecimal.ZERO);
								listUpdates.add(map);
								remain = remain.subtract(availableQuantity);
							} else {
								availableQuantity = availableQuantity.subtract(remain);
								Map<String, Object> map = FastMap.newInstance();
								map.put("quotaId", quota.getString("quotaId"));
								map.put("quotaItemSeqId", quota.getString("quotaItemSeqId"));
								map.put("availableQuantity", availableQuantity);
								listUpdates.add(map);
								remain = BigDecimal.ZERO;
							}
						}
					}
				}
				if (!listUpdates.isEmpty()){
					LocalDispatcher dispatcher = ctx.getDispatcher();
					for (Map<String, Object> map : listUpdates) {
						map.put("userLogin", userLogin);
						try {
							Map<String, Object> rs = dispatcher.runSync("updateQuotaItem", map);
							if (ServiceUtil.isError(rs)) {
								return ServiceUtil.returnError(ServiceUtil.getErrorMessage(rs));
							}
						} catch (GenericServiceException e) {
							String errMsg = "OLBIUS: Fatal error when run service updateQuotaItem: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
					}
				}
			}
		}
		return success;
	}
	
}
