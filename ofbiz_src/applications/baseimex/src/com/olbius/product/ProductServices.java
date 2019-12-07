package com.olbius.product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Iterator;
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
import com.olbius.util.JsonUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ProductServices {
	public static final String module = ProductServices.class.getName();
	public static final String resource = "BaseImExUiLabels";
	public static final String resourceError = "BaseImExErrorUiLabels";
	
	public static Map<String, Object> JQGetListProductRemainDeclaration(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		listAllConditions.add(EntityCondition.makeCondition("eventTypeId", "QUALITY_DECLARATION"));
		listAllConditions.add(EntityCondition.makeCondition("eventStatusId", "PRODUCT_EVENT_APPROVED"));
		listAllConditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.NOT_EQUAL, null));
		listAllConditions.add(EntityCondition.makeCondition("thruDate", EntityOperator.NOT_EQUAL, null));
		listAllConditions.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> listTemp = FastList.newInstance();
		List<GenericValue> listReturns = FastList.newInstance();
		if ( !UtilValidate.isNotEmpty(listAllConditions))
			listSortFields.add("thruDate");
		
		try {
			listTemp = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "ProductEventDeclarationItemAndProductAvailable", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetProductEventItems service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		// get minimum fromDate and maximum thruDate
		for( GenericValue item: listTemp){
			Boolean check = false;
			String productId = (String) item.get("productId");
			
			Timestamp fromDateTS = (Timestamp)item.get("fromDate");
			long fromDate = fromDateTS.getTime();
			
			
			Timestamp thruDateTS = (Timestamp)item.get("thruDate");
			long thruDate = thruDateTS.getTime();
			
			for (GenericValue temp: listReturns){
				String productIdTemp = (String) temp.get("productId");
				 
				Timestamp fromDateTSTemp = (Timestamp) temp.get("fromDate");
				long fromDateTemp = fromDateTSTemp.getTime();
				
				 
				Timestamp thruDateTSTemp = (Timestamp) temp.get("thruDate");
				long thruDateTemp = thruDateTSTemp.getTime();
				
				if (productId.equals(productIdTemp)){
					if (fromDate < fromDateTemp){
						temp.put("fromDate", fromDateTS);
					}
					if (thruDate > thruDateTemp){
						temp.put("thruDate", thruDateTS);
					}
					check = true;
					break;
				}
			}
			
			if (check == false){
				listReturns.add(item);
			}
			check = false;
		}
		String TotalRows =  String.valueOf(listReturns.size());
		successResult.put("TotalRows", TotalRows);
		successResult.put("listIterator", listReturns);
		return successResult;
	}
	
	public static Map<String, Object> JQGetListProductOutOfDeclaration(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<GenericValue> listTemp = FastList.newInstance();
		List<GenericValue> listReturns = FastList.newInstance();
		List<GenericValue> listRemain = FastList.newInstance();
		List<GenericValue> listProduct = FastList.newInstance();
		Map<String, Object> result = null;
		
		if ( !UtilValidate.isNotEmpty(listAllConditions))
			listSortFields.add("thruDate");
		
		try {
			listTemp = delegator.findList("ProductAndProductEventItemDeclarationAvailable", EntityCondition.makeCondition(listAllConditions), null, listSortFields , opts, false);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetProductEventItems service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		try {
			result = dispatcher.runSync("JQGetListProductRemainDeclaration", context);
			listRemain = (List<GenericValue>) result.get("listIterator");
		} catch (Exception e) {
			String errMsg = "Fatal error calling JQGetListProductRemainDeclaration service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		
		// remove product remain declaration
		Iterator<GenericValue> i = listTemp.iterator();
		while (i.hasNext()) {
			GenericValue GV = i.next();
			for(GenericValue temp: listRemain){
				if(GV.get("productId").equals(temp.get("productId"))){
					i.remove();
				}
			}
		}
		
		// get maximum thruDate && thruDate < now
		for( GenericValue temp: listTemp){
			Boolean check = false;
			String productId = (String) temp.get("productId");
			Timestamp thruDateTS = null;
			Timestamp fromDateTS = null;
			
			if ( UtilValidate.isNotEmpty(temp.get("thruDate")))
				thruDateTS = (Timestamp)temp.get("thruDate");
			if ( UtilValidate.isNotEmpty(temp.get("fromDate")))
				fromDateTS = (Timestamp)temp.get("fromDate");
			
			for (GenericValue item: listReturns){
				String productIdTemp = (String) item.get("productId");
				Timestamp thruDateTSTemp = null;
				
				if ( UtilValidate.isNotEmpty(item.get("thruDate")))
				thruDateTSTemp = (Timestamp) item.get("thruDate");
				
				Timestamp nowTS = UtilDateTime.nowTimestamp();
				long now = nowTS.getTime();
				
				if (productId.equals(productIdTemp)){
					if ( UtilValidate.isNotEmpty(thruDateTSTemp) && UtilValidate.isNotEmpty(thruDateTS)){
						long thruDate = thruDateTS.getTime();
						long thruDateTemp = thruDateTSTemp.getTime();
						if (thruDate > thruDateTemp && thruDate < now){
							item.put("thruDate", thruDateTS);
							item.put("fromDate", fromDateTS);
						}
					} 
					else {
						if (UtilValidate.isNotEmpty(thruDateTS)){
							long thruDate = thruDateTS.getTime();
							if ( thruDate < now){
								item.put("thruDate", thruDateTS);
								item.put("fromDate", fromDateTS);
							}
						}
					}
					check = true;
					break;
				}
			}
			
			if (check == false){
				listReturns.add(temp);
			}
		}
		
		String TotalRows =  String.valueOf(listReturns.size());
		successResult.put("TotalRows", TotalRows);
		successResult.put("listIterator", listReturns);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetProductEvents(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<GenericValue> listReturns = FastList.newInstance();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		listAllConditions.add(EntityCondition.makeCondition("organizationPartyId", company));
		
		String parentEventTypeId = null;
		if (parameters.containsKey("parentEventTypeId") && parameters.get("parentEventTypeId").length > 0) {
			parentEventTypeId = (String) parameters.get("parentEventTypeId")[0];
			if (UtilValidate.isNotEmpty(parentEventTypeId)) {
				listAllConditions.add(EntityCondition.makeCondition("parentTypeId", parentEventTypeId));
			}
		}
		String eventTypeId = null;
		if (parameters.containsKey("eventTypeId") && parameters.get("eventTypeId").length > 0) {
			eventTypeId = (String) parameters.get("eventTypeId")[0];
			if (UtilValidate.isNotEmpty(eventTypeId)) {
				listAllConditions.add(EntityCondition.makeCondition("eventTypeId", eventTypeId));
			}
		}
		if (listSortFields.isEmpty()){
			listSortFields.add("-eventId");
		}
		try {
			listReturns = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "ProductEventDetail", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetProductEvents service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listReturns);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetProductEventTypes(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<GenericValue> listReturns = FastList.newInstance();
		try {
			listReturns = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "ProductEventType", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetProductEventTypes service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listReturns);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetProductEventItems(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<GenericValue> listReturns = FastList.newInstance();
		
		String eventId = null;
		if (parameters.containsKey("eventId") && parameters.get("eventId").length > 0) {
			eventId = (String) parameters.get("eventId")[0];
			if (UtilValidate.isNotEmpty(eventId)) {
				listAllConditions.add(EntityCondition.makeCondition("eventId", eventId));
			}
		}
		try {
			listReturns = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "ProductEventItemAndProduct", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetProductEventItems service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listReturns);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> createProductEvent(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> success = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String productStr = (String)context.get("listProducts");
		String eventCode = null;
		if (UtilValidate.isNotEmpty(context.get("eventCode"))) {
			eventCode = (String)context.get("eventCode");
		}
		GenericValue map = delegator.makeValue("ProductEvent");
		String eventId = delegator.getNextSeqId("ProductEvent");
		if (UtilValidate.isEmpty(eventCode)) {
			eventCode = eventId;
		}
		map.put("eventId", eventId);
		map.put("eventCode", eventCode);
		map.put("statusId", "PRODUCT_EVENT_CREATED");
		map.put("createdByUserLogin", userLogin.getString("userLoginId"));
		map.put("createdDate", UtilDateTime.nowTimestamp());
		map.put("organizationPartyId", organizationPartyId);
		
		Timestamp executedDate = null;
		if (UtilValidate.isNotEmpty(context.get("executedDate"))) {
			String executedDateStr = (String)context.get("executedDate");
			if (UtilValidate.isNotEmpty(executedDateStr)) {
				executedDate = new Timestamp(new Long(executedDateStr));
			}
		}
		Timestamp completedDate = null;
		if (UtilValidate.isNotEmpty(context.get("completedDate"))) {
			String completedDateStr = (String)context.get("completedDate");
			if (UtilValidate.isNotEmpty(completedDateStr)) {
				completedDate = new Timestamp(new Long(completedDateStr));
			}
		}
		context.put("executedDate", executedDate);
		context.put("completedDate", completedDate);
		map.setNonPKFields(context);
		try {
			delegator.createOrStore(map);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: createProductEvent error! " + e.toString());
		}
		
		List<Map<String, Object>> listProducts = null;
		try {
			listProducts = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", productStr);
		} catch (ParseException e) {
			return ServiceUtil.returnError("OLBIUS: createProductEvent error when JqxWidgetSevices.convert ! " + e.toString());
		}
    	LocalDispatcher dispatcher = ctx.getDispatcher();
		for (Map<String, Object> pt : listProducts) {
			if (UtilValidate.isNotEmpty(pt.get("productId"))) {
				String productId = (String)pt.get("productId");
				String quantityStr = null;
				if (UtilValidate.isNotEmpty(pt.get("quantity"))){
					quantityStr = (String)pt.get("quantity");
				}
				if (UtilValidate.isNotEmpty(productId)) {
					BigDecimal quantity = null;
					if (UtilValidate.isNotEmpty(quantityStr)){
						quantity = new BigDecimal(quantityStr);
					}	
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
					BigDecimal registeredQuantity = BigDecimal.ZERO; 
					if (pt.containsKey("registeredQuantity")){
						if (UtilValidate.isNotEmpty(pt.get("registeredQuantity"))) {
							String registeredQuantityStr = (String)pt.get("registeredQuantity");
							registeredQuantity = new BigDecimal(registeredQuantityStr);
						}
					}
					Timestamp fromDate = null;
					Timestamp thruDate = null;
					if (pt.containsKey("fromDate")){
						String fromDateStr = (String)pt.get("fromDate");
						if (UtilValidate.isNotEmpty(fromDateStr)) {
							fromDate = new Timestamp(new Long(fromDateStr));
						}
					}
					if (pt.containsKey("fromDate")){
						String thruDateStr = (String)pt.get("thruDate");
						if (UtilValidate.isNotEmpty(thruDateStr)) {
							thruDate = new Timestamp(new Long(thruDateStr));
						}
					}
					Map<String, Object> mapTmp = FastMap.newInstance();
					mapTmp.put("eventId", eventId);
					mapTmp.put("quantity", quantity);
					mapTmp.put("registeredQuantity", registeredQuantity);
					mapTmp.put("productId", productId);
					mapTmp.put("fromDate", fromDate);
					mapTmp.put("thruDate", thruDate);
					mapTmp.put("uomId", uomId);
					mapTmp.put("userLogin", userLogin);
					try { 
						dispatcher.runSync("createProductEventItem", mapTmp);
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: createProductEventItem error! " + e.toString());
					}
				}
			}
		}
		success.put("eventId", eventId);
		return success;
	}
	
	public static Map<String,Object> changeProductEventStatus(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> success = ServiceUtil.returnSuccess();
		String eventId = (String)context.get("eventId");
		String statusId = (String)context.get("statusId");
		Delegator delegator = ctx.getDelegator();
		GenericValue objProductEvent = null;
		try {
			objProductEvent = delegator.findOne("ProductEvent", false, UtilMisc.toMap("eventId", eventId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne ProductEvent: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (UtilValidate.isEmpty(objProductEvent)) {
			String errMsg = "OLBIUS: Fatal error when changeProductEventStatus - ProductEvent not found!: " + eventId;
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		objProductEvent.put("statusId", statusId);
		try {
			delegator.store(objProductEvent);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when store ProductEvent: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		String itemStatusId = null;
		switch (statusId) {
		case "PRODUCT_EVENT_APPROVED":
			itemStatusId = "PRODUCT_EVENT_ITEM_APPROVED";
			break;
		case "PRODUCT_EVENT_COMPLETED":
			itemStatusId = "PRODUCT_EVENT_ITEM_COMPLETED";
			break;
		case "PRODUCT_EVENT_CANCELLED":
			itemStatusId = "PRODUCT_EVENT_ITEM_CANCELLED";
			break;
		case "PRODUCT_EVENT_PROCESSING":
			itemStatusId = "PRODUCT_EVENT_ITEM_PROCESSING";
			break;
		default:
			break;
		}
		if (UtilValidate.isNotEmpty(itemStatusId)) {
			try {
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("eventId", eventId));
				conds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, itemStatusId));
				delegator.storeByCondition("ProductEventItem", UtilMisc.toMap("statusId", itemStatusId), EntityCondition.makeCondition(conds));
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when storeByCondition ProductEventItem: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
		}
		success.put("eventId", eventId);
		success.put("statusId", statusId);
		return success;
	}
	
	public static Map<String,Object> changeProductEventItemStatus(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> success = ServiceUtil.returnSuccess();
		String eventId = (String)context.get("eventId");
		String eventItemSeqId = (String)context.get("eventItemSeqId");
		String statusId = (String)context.get("statusId");
		Delegator delegator = ctx.getDelegator();
		GenericValue objProductEventItem = null;
		try {
			objProductEventItem = delegator.findOne("ProductEventItem", false, UtilMisc.toMap("eventId", eventId, "eventItemSeqId", eventItemSeqId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne ProductEventItem: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (UtilValidate.isEmpty(objProductEventItem)) {
			String errMsg = "OLBIUS: Fatal error when changeProductEventItemStatus - ProductEventItem not found!: " + eventId;
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		objProductEventItem.put("statusId", statusId);
		try {
			delegator.store(objProductEventItem);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when store ProductEventItem: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		success.put("eventId", eventId);
		success.put("eventItemSeqId", eventId);
		success.put("statusId", statusId);
		return success;
	}
	
	public static Map<String,Object> checkProductEventStatus(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> success = ServiceUtil.returnSuccess();
		String eventId = (String)context.get("eventId");
		Delegator delegator = ctx.getDelegator();
		GenericValue objProductEvent = null;
		try {
			objProductEvent = delegator.findOne("ProductEvent", false, UtilMisc.toMap("eventId", eventId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne ProductEvent: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (UtilValidate.isEmpty(objProductEvent)) {
			return success;
		}
		
		List<GenericValue> ProductEventItems = null;
        try {
        	ProductEventItems = delegator.findByAnd("ProductEventItem", UtilMisc.toMap("eventId", eventId), null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get ProductEventItem records", module);
			return ServiceUtil.returnError(e.toString());
        }

        String currentStatusId = objProductEvent.getString("statusId");

        boolean allCanceled = true;
        boolean allCompleted = true;
        boolean allApproved = true;
        boolean allProcessing = true;
        for (GenericValue item : ProductEventItems) {
            String statusId = item.getString("statusId");
            if (!"PRODUCT_EVENT_ITEM_CANCELLED".equals(statusId)) {
                allCanceled = false;
                if (!"PRODUCT_EVENT_ITEM_COMPLETED".equals(statusId)) {
                    allCompleted = false;
                    if (!"PRODUCT_EVENT_ITEM_PROCESSING".equals(statusId)) {
                    	allProcessing = false;
	                	if (!"PRODUCT_EVENT_ITEM_APPROVED".equals(statusId)) {
	                		allApproved = false;
	                		break;
	                    }
                    }
                }
            }
        }

        String newStatus = null;
        if (allCanceled) {
            newStatus = "PRODUCT_EVENT_CANCELLED";
        } else if (allCompleted) {
            newStatus = "PRODUCT_EVENT_COMPLETED";
        } else if (allProcessing) {
        	newStatus = "PRODUCT_EVENT_PROCESSING";
		} else if (allApproved) {
			newStatus = "PRODUCT_EVENT_APPROVED";
		}  

        if (newStatus != null && !newStatus.equals(currentStatusId)) {
        	LocalDispatcher dispatcher = ctx.getDispatcher();
        	GenericValue userLogin = (GenericValue) context.get("userLogin");
        	try {
                Map<String, Object> serviceContext = UtilMisc.<String, Object>toMap("eventId", eventId, "statusId", newStatus, "userLogin", userLogin);
                Map<String, Object> newSttsResult = null;
                newSttsResult = dispatcher.runSync("changeProductEventStatus", serviceContext);
                if (ServiceUtil.isError(newSttsResult)) {
                    return ServiceUtil.returnError(ServiceUtil.getErrorMessage(newSttsResult));
                }
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problem calling the changeProductEventStatus service", module);
                return ServiceUtil.returnError(e.toString());
            }
        }
		return success;
	}
		
	@SuppressWarnings("unchecked")
	public static Map<String,Object> updateProductEvent(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> success = ServiceUtil.returnSuccess();
		String eventId = (String)context.get("eventId");
		Delegator delegator = ctx.getDelegator();
		GenericValue objProductEvent = null;
		try {
			objProductEvent = delegator.findOne("ProductEvent", false, UtilMisc.toMap("eventId", eventId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne ProductEvent: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (UtilValidate.isEmpty(objProductEvent)) {
			String errMsg = "OLBIUS: Fatal error when updateProductEvent - ProductEvent not found!: " + eventId;
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		Timestamp executedDate = null;
		if (UtilValidate.isNotEmpty(context.get("executedDate"))) {
			String executedDateStr = (String)context.get("executedDate");
			if (UtilValidate.isNotEmpty(executedDateStr)) {
				executedDate = new Timestamp(new Long(executedDateStr));
			}
		}
		Timestamp completedDate = null;
		if (UtilValidate.isNotEmpty(context.get("completedDate"))) {
			String completedDateStr = (String)context.get("completedDate");
			if (UtilValidate.isNotEmpty(completedDateStr)) {
				completedDate = new Timestamp(new Long(completedDateStr));
			}
		}
		context.put("executedDate", executedDate);
		context.put("completedDate", completedDate);
		
		objProductEvent.setNonPKFields(context);
		try {
			delegator.store(objProductEvent);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when Store ProductEvent: " + e.toString();
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
				return ServiceUtil.returnError("OLBIUS: updateProductEvent error when JqxWidgetSevices.convert ! " + e.toString());
			}
			GenericValue userLogin = (GenericValue) context.get("userLogin");
	    	LocalDispatcher dispatcher = ctx.getDispatcher();
			for (Map<String, Object> pt : listProducts) {
				if (UtilValidate.isNotEmpty(pt.get("productId")) && UtilValidate.isNotEmpty(pt.get("eventItemSeqId"))) {
					String eventItemSeqId = (String)pt.get("eventItemSeqId");
					Map<String, Object> mapTmp = FastMap.newInstance();
					mapTmp.put("eventId", eventId);
					mapTmp.put("eventItemSeqId", eventItemSeqId);
					
					if (UtilValidate.isNotEmpty(pt.get("uomId"))) {
						mapTmp.put("uomId", pt.get("uomId"));
					}
					if (UtilValidate.isNotEmpty(pt.get("quantity"))) {
						String quantityStr = (String)pt.get("quantity");
						if (UtilValidate.isNotEmpty(quantityStr)) {
							mapTmp.put("quantity", new BigDecimal(quantityStr));
						}
					}
					Timestamp fromDate = null;
					if (UtilValidate.isNotEmpty(pt.get("fromDate"))) {
						String fromDateStr = (String)pt.get("fromDate");
						if (UtilValidate.isNotEmpty(fromDateStr)) {
							fromDate = new Timestamp(new Long(fromDateStr));	
						}
					}
					Timestamp thruDate = null;
					if (UtilValidate.isNotEmpty(pt.get("thruDate"))) {
						String thruDateStr = (String)pt.get("thruDate");
						if (UtilValidate.isNotEmpty(thruDateStr)) {
							thruDate = new Timestamp(new Long(thruDateStr));	
						}
					}
					mapTmp.put("fromDate",fromDate);
					mapTmp.put("thruDate",thruDate);
					mapTmp.put("userLogin", userLogin);
					try {
						dispatcher.runSync("updateProductEventItem", mapTmp);
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: updateProductEventItem error! " + e.toString());
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
				return ServiceUtil.returnError("OLBIUS: updateProductEvent error when JqxWidgetSevices.convert ! " + e.toString());
			}
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			LocalDispatcher dispatcher = ctx.getDispatcher();
			for (Map<String, Object> pt : listProducts) {
				if ( UtilValidate.isNotEmpty(pt.get("productId"))) {
					String productId = (String)pt.get("productId");
					String quantityStr = null;
					if (UtilValidate.isNotEmpty(pt.get("quantity"))){
						quantityStr = (String)pt.get("quantity");
					}
					if (UtilValidate.isNotEmpty(productId)) {
						BigDecimal quantity = null;
						String uomId = null; 
						if (UtilValidate.isNotEmpty(quantityStr)){
							quantity = new BigDecimal(quantityStr);
						}
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
						Timestamp fromDate = null;
						if (UtilValidate.isNotEmpty(pt.get("fromDate"))) {
							String fromDateStr = (String)pt.get("fromDate");
							if (UtilValidate.isNotEmpty(fromDateStr)) {
								fromDate = new Timestamp(new Long(fromDateStr));
								
							}
						}
						Timestamp thruDate = null;
						if (UtilValidate.isNotEmpty(pt.get("thruDate"))) {
							String thruDateStr = (String)pt.get("thruDate");
							if (UtilValidate.isNotEmpty(thruDateStr)) {
								thruDate = new Timestamp(new Long(thruDateStr));
								
							}
						}
						BigDecimal registeredQuantity = BigDecimal.ZERO; 
						if (pt.containsKey("registeredQuantity")){
							if (UtilValidate.isNotEmpty(pt.get("registeredQuantity"))) {
								String registeredQuantityStr = (String)pt.get("registeredQuantity");
								registeredQuantity = new BigDecimal(registeredQuantityStr);
							}
						}
						Map<String, Object> mapTmp = FastMap.newInstance();
						mapTmp.put("eventId", eventId);
						mapTmp.put("quantity", quantity);
						mapTmp.put("registeredQuantity", registeredQuantity);
						mapTmp.put("productId", productId);
						mapTmp.put("uomId", uomId);
						mapTmp.put("userLogin", userLogin);
						mapTmp.put("fromDate",fromDate);
						mapTmp.put("thruDate",thruDate);
						try {
							dispatcher.runSync("createProductEventItem", mapTmp);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError("OLBIUS: createProductEventItem error! " + e.toString());
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
				return ServiceUtil.returnError("OLBIUS: updateProductEvent error when JqxWidgetSevices.convert ! " + e.toString());
			}
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			LocalDispatcher dispatcher = ctx.getDispatcher();
			for (Map<String, Object> pt : listProducts) {
				if (UtilValidate.isNotEmpty(pt.get("productId"))) {
					String eventItemSeqId = (String)pt.get("eventItemSeqId");
					Map<String, Object> mapTmp = FastMap.newInstance();
					mapTmp.put("eventId", eventId);
					mapTmp.put("eventItemSeqId", eventItemSeqId);
					mapTmp.put("statusId", "PRODUCT_EVENT_ITEM_CANCELLED");
					mapTmp.put("userLogin", userLogin);
					try {
						dispatcher.runSync("changeProductEventItemStatus", mapTmp);
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: changeProductEventItemStatus error! " + e.toString());
					}
				}
			}
		}
		
		success.put("eventId", eventId);
		return success;
	}
	
	public static Map<String,Object> createProductEventItem(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> success = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String eventId = (String)context.get("eventId");
		
		GenericValue ProductEventItem = delegator.makeValue("ProductEventItem");
		delegator.setNextSubSeqId(ProductEventItem, "eventItemSeqId", 5, 1);
		ProductEventItem.put("eventId", eventId);
		ProductEventItem.put("statusId", "PRODUCT_EVENT_ITEM_CREATED");
		ProductEventItem.setNonPKFields(context);
		try {
			delegator.create(ProductEventItem);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: createProductEventItem error! " + e.toString());
		}
		String eventItemSeqId = ProductEventItem.getString("eventItemSeqId");
		success.put("eventId", eventId);
		success.put("eventItemSeqId", eventItemSeqId);
		return success;
	}
	
	public static Map<String,Object> updateProductEventItem(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> success = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String eventId = (String)context.get("eventId");
		String eventItemSeqId = (String)context.get("eventItemSeqId");
		GenericValue objProductEventItem = null;
		try {
			objProductEventItem = delegator.findOne("ProductEventItem", false, UtilMisc.toMap("eventId", eventId, "eventItemSeqId", eventItemSeqId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne ProductEventItem: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		delegator.setNextSubSeqId(objProductEventItem, "eventItemSeqId", 5, 1);
		objProductEventItem.put("eventId", eventId);
		objProductEventItem.setNonPKFields(context);
		try {
			delegator.store(objProductEventItem);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: updateProductEventItem error! " + e.toString());
		}
		success.put("eventId", eventId);
		success.put("eventItemSeqId", eventItemSeqId);
		return success;
	}
	
	public static Map<String,Object> getProductByEvent(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> success = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String eventId = (String)context.get("eventId");
		List<GenericValue> listProducts = FastList.newInstance();
		GenericValue objProductEvent = null;
		try {
			objProductEvent = delegator.findOne("ProductEvent", false, UtilMisc.toMap("eventId", eventId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne ProductEvent: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (UtilValidate.isNotEmpty(objProductEvent)) {
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("eventId", eventId));
			if (!"PRODUCT_EVENT_CANCELLED".equals(objProductEvent.getString("statusId"))){
				conds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PRODUCT_EVENT_ITEM_CANCELLED"));
			} 
			try {
				listProducts = delegator.findList("ProductEventItemAndProduct", EntityCondition.makeCondition(conds), null, null, null,
						false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList ProductEventItemAndProduct: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
		}
		
		success.put("eventId", eventId);
		success.put("listProducts", listProducts);
		return success;
	}
	
	public static Map<String,Object> createRequirementFromProductEvent(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> success = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String eventId = (String)context.get("eventId");
		List<GenericValue> listProductEventItems = FastList.newInstance();
		GenericValue objProductEvent = null;
		try {
			objProductEvent = delegator.findOne("ProductEvent", false, UtilMisc.toMap("eventId", eventId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne ProductEvent: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if ("PRODUCT_EVENT_CANCELLED".equals(objProductEvent.getString("statusId"))){
			String errMsg = "OLBIUS: Fatal error when call service createRequirementFromProductEvent: Cannot create requirement from event with status PRODUCT_EVENT_CANCELLED";
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (UtilValidate.isNotEmpty(objProductEvent)) {
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("eventId", eventId));
			conds.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRODUCT_EVENT_ITEM_APPROVED"));
			try {
				listProductEventItems = delegator.findList("ProductEventItemAndProduct", EntityCondition.makeCondition(conds), null, null, null,
						false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList ProductEventItemAndProduct: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
		}
		List<Map<String, Object>> listProducts = FastList.newInstance();
		String requirementId = null;
		if (!listProductEventItems.isEmpty()){
			LocalDispatcher dispatcher = ctx.getDispatcher();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String company = MultiOrganizationUtil.getCurrentOrganization(delegator,
					userLogin.getString("userLoginId"));
			String facilityId = null;
			GenericValue packing = null;
			try {
				packing = objProductEvent.getRelatedOne("PackingListHeader", false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList ProductEventItemAndProduct: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			if (UtilValidate.isNotEmpty(packing)) {
				facilityId = packing.getString("destFacilityId");
			}
			if (UtilValidate.isEmpty(facilityId)) {
				
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("ownerPartyId", company));
				conds.add(EntityCondition.makeCondition("primaryFacilityGroupId", "FACILITY_INTERNAL"));
				conds.add(EntityCondition.makeCondition("facilityTypeId", "WAREHOUSE"));
				
				List<EntityCondition> cond2s = FastList.newInstance();
				cond2s.add(EntityCondition.makeCondition("closedDate", EntityOperator.EQUALS, null));
				cond2s.add(EntityCondition.makeCondition("closedDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp()));
				
				conds.add(EntityCondition.makeCondition(cond2s, EntityOperator.OR));
				List<GenericValue> listFacility = FastList.newInstance();
				try {
					listFacility = delegator.findList("Facility", EntityCondition.makeCondition(conds), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList Facility: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
				if (!listFacility.isEmpty()) facilityId = listFacility.get(0).getString("facilityId");
			}
			Locale locale = (Locale) context.get("locale");
			if (UtilValidate.isEmpty(facilityId)) {
				ServiceUtil.returnError(UtilProperties.getMessage(resource, "BIEFacilityExportNotFound", locale));
			}
			for (GenericValue item : listProductEventItems) {
				String productId = item.getString("productId");
				String uomId = item.getString("uomId");
				BigDecimal quantity = item.getBigDecimal("quantity");
				BigDecimal unitCost = BigDecimal.ZERO;
				try {
					Map<String, Object> result = dispatcher.runSync("getProductAverageCostBaseSimple",
							UtilMisc.toMap("ownerPartyId", company, "facilityId", facilityId,
									"productId", productId, "userLogin", userLogin));
					if (ServiceUtil.isError(result)){
						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
					}
					if (UtilValidate.isNotEmpty(result.get("unitCost"))) {
						unitCost = (BigDecimal)result.get("unitCost");
					}
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BaseImExCreateRequirementError", locale));
				}
				Map<String, Object> map = FastMap.newInstance();
				map.put("productId", productId);
				map.put("uomId", uomId);
				map.put("unitCost", unitCost);
				map.put("quantity", quantity);
				listProducts.add(map);
			}
			if (listProducts.isEmpty()){
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BIEProductNotFound", locale));
			}
			String product = JsonUtil.convertListMapToJSON(listProducts);
			Timestamp requirementStartDate = objProductEvent.getTimestamp("executedDate");
			Map<String, Object> map = FastMap.newInstance();
			map.put("facilityId", facilityId);
			map.put("requirementStartDate", requirementStartDate.getTime());
			map.put("listProducts", product);
			map.put("requirementTypeId", "EXPORT_REQUIREMENT");
			map.put("reasonEnumId", "EXPORT_TEST_CERTIFICATE");
			map.put("eventId", eventId);
			map.put("userLogin", userLogin);
			try {
				Map<String, Object> rs = dispatcher.runSync("createNewRequirement", map);
				if (ServiceUtil.isError(rs)){
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(rs));
				}
				if (rs.containsKey("requirementId")){
					requirementId = (String)rs.get("requirementId");
				}
			} catch (GenericServiceException e) {
				String errMsg = "OLBIUS: Fatal error when run service createRequirement: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			
			try {
                Map<String, Object> serviceContext = UtilMisc.<String, Object>toMap("eventId", eventId, "statusId", "PRODUCT_EVENT_PROCESSING", "userLogin", userLogin);
                Map<String, Object> newSttsResult = null;
                newSttsResult = dispatcher.runSync("changeProductEventStatus", serviceContext);
                if (ServiceUtil.isError(newSttsResult)) {
                    return ServiceUtil.returnError(ServiceUtil.getErrorMessage(newSttsResult));
                }
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problem calling the changeProductEventStatus service", module);
                return ServiceUtil.returnError(e.toString());
            }
		}
		
		success.put("requirementId", requirementId);
		return success;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetConfigCapacitys(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<GenericValue> listReturns = FastList.newInstance();
		
		listAllConditions.add(EntityCondition.makeCondition("uomTypeIdFrom", "SHIPMENT_PACKING"));
		listAllConditions.add(EntityUtil.getFilterByDateExpr());
		try {
			listReturns = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "ConfigPackingDetail", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetConfigCapacitys service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listReturns);
		return successResult;
	}
	
	public static Map<String, Object> createConfigPacking(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();

		String description = null;
		if (UtilValidate.isNotEmpty(context.get("description"))) {
			description = (String) context.get("description");
		}
		
		Timestamp fromDate = null;
		Timestamp thruDate = null;
		Long fromDateStr = null;
		if (UtilValidate.isNotEmpty(context.get("fromDate"))) {
			fromDateStr = (Long) context.get("fromDate");
			fromDate = new Timestamp(fromDateStr);
		} else {
			fromDate = UtilDateTime.nowTimestamp();
		}
		
		Long thruDateStr = null;
		if (UtilValidate.isNotEmpty(context.get("thruDate"))) {
			thruDateStr = (Long) context.get("thruDate");
			thruDate = new Timestamp(thruDateStr);
		}
		
		BigDecimal convert = BigDecimal.ONE;
		String quantityConvert = null;
		if (UtilValidate.isNotEmpty(context.get("quantityConvert"))) {
			quantityConvert = (String) context.get("quantityConvert");
			convert = new BigDecimal(quantityConvert);
		}
		
		context.put("fromDate", fromDate);
		context.put("thruDate", thruDate);
		context.put("quantityConvert", convert);
		context.put("description", description);
		GenericValue configPacking = delegator.makeValue("ConfigPacking");
		configPacking.setPKFields(context);
		configPacking.setNonPKFields(context);
		try {
			delegator.createOrStore(configPacking);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateConfigPacking(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		
		String description = null;
		if (UtilValidate.isNotEmpty(context.get("description"))) {
			description = (String) context.get("description");
		}
		
		Timestamp fromDate = null;
		Timestamp thruDate = null;
		Long fromDateStr = null;
		if (UtilValidate.isNotEmpty(context.get("fromDate"))) {
			fromDateStr = (Long) context.get("fromDate");
			fromDate = new Timestamp(fromDateStr);
		}
		
		String productId = null;
		if (UtilValidate.isNotEmpty(context.get("productId"))) {
			productId = (String) context.get("productId");
		}
		String uomFromId = null;
		if (UtilValidate.isNotEmpty(context.get("uomFromId"))) {
			uomFromId = (String) context.get("uomFromId");
		}
		String uomToId = null;
		if (UtilValidate.isNotEmpty(context.get("uomToId"))) {
			uomToId = (String) context.get("uomToId");
		}
		GenericValue objConfigPacking = null;
		try {
			objConfigPacking = delegator.findOne("ConfigPacking", false, UtilMisc.toMap("productId", productId, "uomFromId", uomFromId, "uomToId", uomToId, "fromDate", fromDate));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne ConfigPacking: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (UtilValidate.isNotEmpty(objConfigPacking)) {
			Long thruDateStr = null;
			if (UtilValidate.isNotEmpty(context.get("thruDate"))) {
				thruDateStr = (Long) context.get("thruDateStr");
				thruDate = new Timestamp(thruDateStr);
			}
			BigDecimal convert = objConfigPacking.getBigDecimal("quantityConvert");
			String quantityConvert = null;
			if (UtilValidate.isNotEmpty(context.get("quantityConvert"))) {
				quantityConvert = (String) context.get("quantityConvert");
				convert = new BigDecimal(quantityConvert);
			}
			context.put("thruDate", thruDate);
			context.put("quantityConvert", convert);
			context.put("description", description);
			
			objConfigPacking.setNonPKFields(context);
			try {
				delegator.store(objConfigPacking);
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError(e.getMessage());
			}
		}
		
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> deleteConfigPacking(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		
		Timestamp fromDate = null;
		Long fromDateStr = null;
		if (UtilValidate.isNotEmpty(context.get("fromDate"))) {
			fromDateStr = (Long) context.get("fromDate");
			fromDate = new Timestamp(fromDateStr);
		}
		
		String productId = null;
		if (UtilValidate.isNotEmpty(context.get("productId"))) {
			productId = (String) context.get("productId");
		}
		String uomFromId = null;
		if (UtilValidate.isNotEmpty(context.get("uomFromId"))) {
			uomFromId = (String) context.get("uomFromId");
		}
		String uomToId = null;
		if (UtilValidate.isNotEmpty(context.get("uomToId"))) {
			uomToId = (String) context.get("uomToId");
		}
		GenericValue objConfigPacking = null;
		try {
			objConfigPacking = delegator.findOne("ConfigPacking", false, UtilMisc.toMap("productId", productId, "uomFromId", uomFromId, "uomToId", uomToId, "fromDate", fromDate));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne ConfigPacking: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (UtilValidate.isNotEmpty(objConfigPacking)) {
			objConfigPacking.set("thruDate", UtilDateTime.nowTimestamp());
			try {
				delegator.store(objConfigPacking);
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError(e.getMessage());
			}
		}
		
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> getProductPackingUomWithConvertNumbers(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listUoms = FastList.newInstance();
		String productId = null;
		if (UtilValidate.isNotEmpty(context.get("productId"))) {
			productId = (String) context.get("productId");
		}
		try {
			listUoms = ProductUtil.getProductPackingUomWithConvertNumbers(delegator, productId);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getMessage());
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listUoms", listUoms);
		return result;
	}
	
	public static Map<String, Object> getProductFromImportPlanToCreatePO(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listProducts = FastList.newInstance();
		String productPlanId = null;
		if (UtilValidate.isNotEmpty(context.get("productPlanId"))) {
			productPlanId = (String) context.get("productPlanId");
		}
		String customTimePeriodId = null;
		if (UtilValidate.isNotEmpty(context.get("customTimePeriodId"))) {
			customTimePeriodId = (String) context.get("customTimePeriodId");
		}
		
		if (UtilValidate.isNotEmpty(productPlanId) && UtilValidate.isNotEmpty(customTimePeriodId)) {
			GenericValue objProductPlanHeader = null;
			try {
				objProductPlanHeader = delegator.findOne("ProductPlanHeader", false, UtilMisc.toMap("productPlanId", productPlanId));
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findOne ProductPlanHeader: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			if (UtilValidate.isNotEmpty(objProductPlanHeader)) {
				String supplierPartyId = objProductPlanHeader.getString("supplierPartyId");
				String currencyUomId = objProductPlanHeader.getString("currencyUomId");
				if (UtilValidate.isNotEmpty(supplierPartyId) && UtilValidate.isNotEmpty(currencyUomId)) {
					List<EntityCondition> conds = FastList.newInstance();
					conds.add(EntityCondition.makeCondition("parentProductPlanId", productPlanId));
					conds.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId));
					conds.add(EntityCondition.makeCondition("statusId", "IMPORT_PLAN_APPROVED"));
					List<GenericValue> listProductPlanHeader = FastList.newInstance();
					try {
						listProductPlanHeader = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(conds), null, null, null,
								false);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findList ProductPlanHeader: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					if (!listProductPlanHeader.isEmpty()){
						conds = FastList.newInstance();
						String productPlanIdTmp = EntityUtil.getFirst(listProductPlanHeader).getString("productPlanId");
						conds.add(EntityCondition.makeCondition("productPlanId", productPlanIdTmp));
						conds.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId));
						conds.add(EntityCondition.makeCondition("statusId", "PLANITEM_APPROVED"));
						List<GenericValue> listProductPlanItem = FastList.newInstance();
						try {
							listProductPlanItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(conds), null, null,
									null, false);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList ProductPlanItem: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
						if (!listProductPlanItem.isEmpty()){
							List<String> productIds = EntityUtil.getFieldListFromEntityList(listProductPlanItem, "productId", true);
							
							conds = FastList.newInstance();
							conds.add(EntityCondition.makeCondition("partyId", supplierPartyId));
							conds.add(EntityCondition.makeCondition("currencyUomId", currencyUomId));
							conds.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
							
							List<GenericValue> listProductTmps = FastList.newInstance();
							try {
								listProductTmps = delegator.findList("SupplierProductGroupAndProduct", EntityCondition.makeCondition(conds),
										null, null, null, false);
							} catch (GenericEntityException e) {
								String errMsg = "OLBIUS: Fatal error when findList SupplierProductGroupAndProduct: " + e.toString();
								Debug.logError(e, errMsg, module);
								return ServiceUtil.returnError(errMsg);
							}
							LocalDispatcher dispatcher = ctx.getDispatcher();
							for (GenericValue itemProd : listProductTmps) {
				            	Map<String, Object> proMap = FastMap.newInstance();
				            	proMap.putAll(itemProd);
				            	
				            	String productId = (String)itemProd.get("productId");
				            	GenericValue objProductPlanItem = null;
								try {
									objProductPlanItem = delegator.findOne("ProductPlanItem", false, UtilMisc.toMap("productId", productId, "productPlanId", productPlanIdTmp, "customTimePeriodId", customTimePeriodId));
								} catch (GenericEntityException e) {
									String errMsg = "OLBIUS: Fatal error when findOne ProductPlanItem: " + e.toString();
									Debug.logError(e, errMsg, module);
									return ServiceUtil.returnError(errMsg);
								}
								if (UtilValidate.isEmpty(objProductPlanItem)) continue;
								BigDecimal planQuantity = objProductPlanItem.getBigDecimal("planQuantity");
								if (UtilValidate.isNotEmpty(objProductPlanItem.get("orderedQuantity"))) {
									planQuantity = planQuantity.subtract(objProductPlanItem.getBigDecimal("orderedQuantity"));
								}
								if (planQuantity.compareTo(BigDecimal.ZERO) < 0) continue;
				            	String purchaseUomId = (String)itemProd.get("purchaseUomId");
				            	String quantityUomId = itemProd.getString("quantityUomId");
				            	BigDecimal minimumQty = BigDecimal.ONE;
				            	BigDecimal convert = BigDecimal.ONE;
				            	if (ProductUtil.isWeightProduct(delegator, productId)){
				            		String weightUomId = itemProd.getString("weightUomId");
				            		purchaseUomId = weightUomId;
				            	} else {
									if (UtilValidate.isEmpty(purchaseUomId)) {
										purchaseUomId = quantityUomId;
									} else {
										convert = ProductUtil.getConvertPackingNumber(delegator, productId, purchaseUomId, quantityUomId);
									}
				            	}
				            	BigDecimal quantity = planQuantity.divide(convert, RoundingMode.HALF_UP);
				            	
								BigDecimal minimumOrderQuantity = BigDecimal.ONE;
								if (UtilValidate.isNotEmpty(itemProd.get("minimumOrderQuantity"))) {
									minimumOrderQuantity = itemProd.getBigDecimal("minimumOrderQuantity");
									if (minimumOrderQuantity.compareTo(minimumQty) > 0){
										minimumQty = minimumOrderQuantity;
									}
								}
								BigDecimal price = null;
								Map<String, Object> calPriceCtx = UtilMisc.<String, Object>toMap("productId", productId, 
										"currencyUomId", currencyUomId, "partyId", supplierPartyId, "quantity", minimumQty, "amount", null, "quantityUomId", purchaseUomId);
								Map<String, Object> resultCalPrice;
								try {
									resultCalPrice = dispatcher.runSync("calculatePurchasePrice", calPriceCtx);
									if (!ServiceUtil.isError(resultCalPrice)) {
						        		price = (BigDecimal) resultCalPrice.get("price");
						        	}
								} catch (GenericServiceException e) {
									String mess = e.toString();
									Debug.logError(e, module);
									return ServiceUtil.returnError(mess);
								}
					        	
								proMap.put("lastPrice", price);
								proMap.put("quantity", quantity);
								proMap.put("quantityPurchase", quantity);
				            	listProducts.add(proMap);
				            }
						}
					}
				}
			}
		}
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listProducts", listProducts);
		return result;
	}
	
	public static Map<String, Object> getOrderItemByOrderToCreateAgreement(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String orderId = (String) context.get("orderId");
		List<GenericValue> listOrderItems = FastList.newInstance();
		List<Map<String, Object>> listProducts = FastList.newInstance();
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("orderId", orderId));
		conds.add(EntityCondition.makeCondition("statusId", "ITEM_APPROVED"));
		try {
			listOrderItems = delegator.findList("OrderItemAndProduct", EntityCondition.makeCondition(conds), null, null, null, true);
		} catch (GenericEntityException e) {
			String mess = e.toString();
			Debug.logError(e, module);
			return ServiceUtil.returnError(mess);
		}
		List<GenericValue> listAgreementAndOrder = FastList.newInstance();
		try {
			listAgreementAndOrder = delegator.findList("AgreementAndOrder", EntityCondition.makeCondition("orderId", orderId), null, null, null,
					false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList AgreementAndOrder: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (!listOrderItems.isEmpty()) {
			List<String> productIds = EntityUtil.getFieldListFromEntityList(listOrderItems, "productId", true);
			List<String> agreementIds = EntityUtil.getFieldListFromEntityList(listAgreementAndOrder, "agreementId", true);
			for (String productId : productIds) {
				Map<String, Object> map = FastMap.newInstance();
				BigDecimal quantity = BigDecimal.ZERO;
				boolean isKg = ProductUtil.isWeightProduct(delegator, productId);
				for (GenericValue orderItem : listOrderItems) {
					if (productId.equals(orderItem.getString("productId"))){
						if (isKg) {
							if (UtilValidate.isNotEmpty(orderItem.get("selectedAmount"))) {
								quantity = quantity.add(orderItem.getBigDecimal("selectedAmount").multiply(orderItem.getBigDecimal("quantity")));
							}
						} else {
							quantity = quantity.add(orderItem.getBigDecimal("quantity"));
						}
						map.putAll(orderItem);
					}
				}
				if (!agreementIds.isEmpty()){
					conds = FastList.newInstance();
					conds.add(EntityCondition.makeCondition("agreementId", EntityOperator.IN, agreementIds));
					conds.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
					List<GenericValue> listAgreementProductAppl = FastList.newInstance();
					try {
						listAgreementProductAppl = delegator.findList("AgreementProductAppl", EntityCondition.makeCondition(conds), null,
								null, null, false);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findList AgreementProductAppl: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					if (!listAgreementAndOrder.isEmpty()){
						for (GenericValue item : listAgreementProductAppl) {
							quantity = quantity.subtract(item.getBigDecimal("quantity"));
						}
					}
				}
				if (quantity.compareTo(BigDecimal.ZERO) > 0) {
					map.put("quantity", quantity);
					listProducts.add(map);
				}
			}
		}
		
		result.put("listProducts", listProducts);
		return result;
	}
}
