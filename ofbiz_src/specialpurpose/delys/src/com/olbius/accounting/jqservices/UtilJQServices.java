package com.olbius.accounting.jqservices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class UtilJQServices {
	
	public static final String MODULE = UtilJQServices.class.getName();
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getListParty(DispatchContext ctx, Map<String, ? extends Object> context) {
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
    		listIterator = delegator.find("PartyNameView", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getListParty service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
   	public static Map<String, Object> getListProductPrice(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	EntityListIterator listIterator = null;
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
       	String productId = parameters.get("productId")[0];
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	mapCondition.put("productId", productId);
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
       	listAllConditions.add(tmpConditon);
       	try {
       		listIterator = delegator.find("ProductPrice", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
       	} catch (GenericEntityException e) {
   			String errMsg = "Fatal error calling getListProductPrice service: " + e.toString();
   			Debug.logError(e, errMsg, MODULE);
   		}
       	successResult.put("listIterator", listIterator);
       	return successResult;
       }
	@SuppressWarnings("unchecked")
   	public static Map<String, Object> listProductConfigPacking(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	EntityListIterator listIterator = null;
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
       	String productId = parameters.get("productId")[0];
       	listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)));
		listAllConditions.add(EntityUtil.getFilterByDateExpr());
       	try {
       		listIterator = delegator.find("ConfigPacking", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
       	} catch (GenericEntityException e) {
   			String errMsg = "Fatal error calling ListProductConfigPacking service: " + e.toString();
   			Debug.logError(e, errMsg, MODULE);
   		}
       	successResult.put("listIterator", listIterator);
       	return successResult;
       }
	@SuppressWarnings("unchecked")
   	public static Map<String, Object> listProductSupplier(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	EntityListIterator listIterator = null;
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
       	String productId = parameters.get("productId")[0];
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	mapCondition.put("productId", productId);
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
       	listAllConditions.add(tmpConditon);
       	try {
       		listIterator = delegator.find("SupplierProduct", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
       	} catch (GenericEntityException e) {
   			String errMsg = "Fatal error calling listProductSupplier service: " + e.toString();
   			Debug.logError(e, errMsg, MODULE);
   		}
       	successResult.put("listIterator", listIterator);
       	return successResult;
       }
	@SuppressWarnings("unchecked")
    public static Map<String, Object> listShipment(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	EntityListIterator listIterator = null;
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
       	String facilityId = parameters.get("facilityId")[0];
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	if (!facilityId.equals("")) {
       		mapCondition.put("originFacilityId", facilityId);
       		mapCondition.put("destinationFacilityId", facilityId);
       		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition, EntityOperator.OR);
           	listAllConditions.add(tmpConditon);
		}
       	try {
       		listIterator = delegator.find("Shipment", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
       	} catch (GenericEntityException e) {
   			String errMsg = "Fatal error calling listProductSupplier service: " + e.toString();
   			Debug.logError(e, errMsg, MODULE);
   		}
       	successResult.put("listIterator", listIterator);
       	return successResult;
    }
	@SuppressWarnings("unchecked")
    public static Map<String, Object> listVehicles(DispatchContext ctx, Map<String, ? extends Object> context) {
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
    		listIterator = delegator.find("Vehicle", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getListProduct service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
    	
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	@SuppressWarnings("unchecked")
    public static Map<String, Object> listPurchaseAgreements(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	EntityListIterator listIterator = null;
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	mapCondition.put("agreementTypeId", "PURCHASE_AGREEMENT");
       	mapCondition.put("attrName", "AGREEMENT_NAME");
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
       	listAllConditions.add(tmpConditon);
       	try {
       		listIterator = delegator.find("AgreementAndAgreementAttribute", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
       	} catch (GenericEntityException e) {
   			String errMsg = "Fatal error calling listProductSupplier service: " + e.toString();
   			Debug.logError(e, errMsg, MODULE);
   		}
       	successResult.put("listIterator", listIterator);
       	return successResult;
    }
	@SuppressWarnings("unchecked")
    public static Map<String, Object> listImportQuotas(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	EntityListIterator listIterator = null;
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	mapCondition.put("quotaTypeId", "IMPORT_QUOTA");
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
       	listAllConditions.add(tmpConditon);
       	try {
       		listIterator = delegator.find("QuotaHeader", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
       	} catch (GenericEntityException e) {
   			String errMsg = "Fatal error calling listProductSupplier service: " + e.toString();
   			Debug.logError(e, errMsg, MODULE);
   		}
       	successResult.put("listIterator", listIterator);
       	return successResult;
    }
	@SuppressWarnings("unchecked")
    public static Map<String, Object> listQuotaItems(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	EntityListIterator listIterator = null;
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
       	String quotaId = parameters.get("quotaId")[0];
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	mapCondition.put("quotaId", quotaId);
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
       	listAllConditions.add(tmpConditon);
       	try {
       		listIterator = delegator.find("QuotaItem", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
       	} catch (GenericEntityException e) {
   			String errMsg = "Fatal error calling listProductSupplier service: " + e.toString();
   			Debug.logError(e, errMsg, MODULE);
   		}
       	successResult.put("listIterator", listIterator);
       	return successResult;
    }
	@SuppressWarnings("unchecked")
    public static Map<String, Object> listProcessingAgreements(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	EntityListIterator listIterator = null;
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	EntityCondition tmpConditon = EntityCondition.makeCondition();
       	
       	mapCondition.put("agreementTypeId", "PURCHASE_AGREEMENT");
       	mapCondition.put("attrName", "AGREEMENT_NAME");
       	
       	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
       	String agreementId = parameters.get("agreementId")[0];
       	if (!agreementId.equals("")) {
       		mapCondition.put("agreementId", agreementId);
		}else {
			tmpConditon = EntityCondition.makeCondition("statusId", EntityComparisonOperator.IN, UtilMisc.toSet("AGREEMENT_SENT", "AGREEMENT_PROCESSING"));
//	        listAllConditions.add(tmpConditon);
		}
       	listSortFields.add("lastUpdatedStamp");
       	tmpConditon = EntityCondition.makeCondition(mapCondition);
       	listAllConditions.add(tmpConditon);
       	
       	try {
       		listIterator = delegator.find("AgreementAndAgreementAttribute", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
       	} catch (GenericEntityException e) {
   			String errMsg = "Fatal error calling listProcessingAgreements service: " + e.toString();
   			Debug.logError(e, errMsg, MODULE);
   		}
       	successResult.put("listIterator", listIterator);
       	return successResult;
    }
	@SuppressWarnings("unchecked")
    public static Map<String, Object> listPendingAgreements(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	EntityListIterator listIterator = null;
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	EntityCondition tmpConditon = EntityCondition.makeCondition();
       	
       	mapCondition.put("agreementTypeId", "PURCHASE_AGREEMENT");
       	mapCondition.put("attrName", "AGREEMENT_NAME");
       	tmpConditon = EntityCondition.makeCondition(mapCondition);
       	listAllConditions.add(tmpConditon);
		tmpConditon = EntityCondition.makeCondition("statusId", EntityComparisonOperator.IN, UtilMisc.toSet("AGREEMENT_APPROVED", "AGREEMENT_CREATED"));
	    listAllConditions.add(tmpConditon);
       	try {
       		listIterator = delegator.find("AgreementAndAgreementAttribute", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
       	} catch (GenericEntityException e) {
   			String errMsg = "Fatal error calling listPendingAgreements service: " + e.toString();
   			Debug.logError(e, errMsg, MODULE);
   		}
       	successResult.put("listIterator", listIterator);
       	return successResult;
    }
//	hoanmEnd 
}
