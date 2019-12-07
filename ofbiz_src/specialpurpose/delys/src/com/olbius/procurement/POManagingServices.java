package com.olbius.procurement;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
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
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.CheckOutEvents;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceAuthException;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.ServiceValidationException;

import com.olbius.order.ShoppingCartHelper;
import com.olbius.util.SecurityUtil;


public class POManagingServices {
	public static final String module = POManagingServices.class.getName();
	public static final String resource = "DelysProcurementLabels";
    public static final String resourceError = "widgetErrorUiLabels";

    public static Map<String, Object> createPOProposal(DispatchContext ctx, Map<String, Object> context) throws GenericServiceException {
    	LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        List<Map<String, Object>> products = (List<Map<String, Object>>) context.get("products");
        ModelService createAccountService = ctx.getModelService("createRequirementPO");
        Map<String, Object> inputMap = createAccountService.makeValid(context, ModelService.IN_PARAM);
        inputMap.put("createdByUserLogin", userLogin.getString("userLoginId"));
        inputMap.put("createdDate", UtilDateTime.nowTimestamp());
        inputMap.put("statusId", "REQ_CREATED");
        inputMap.put("requirementTypeId", "PO_REQ");
        try{
        	Map<String,Object> output = dispatcher.runSync("createRequirementPO", inputMap);
        	String requirementId = (String) output.get("requirementId");
        	String tmp = "";
            for(Map<String, Object> p : products){
            	Map<String, Object> ri = FastMap.newInstance();
            	ri.put("requirementId", requirementId);
            	ri.put("productId", p.get("productId"));
            	tmp = (String) p.get("quantity");
            	if(!UtilValidate.isEmpty(tmp)){
            		ri.put("quantity", new BigDecimal(tmp));
            	}else{
            		ri.put("quantity", new BigDecimal(0));
            	}
            	ri.put("quantityUomId", p.get("quantityUomId"));
            	tmp = (String) p.get("unitCost");
            	if(!UtilValidate.isEmpty(tmp)){
            		ri.put("unitCost", new BigDecimal(tmp));
            	}else{
            		ri.put("unitCost", new BigDecimal(0));
            	}
            	ri.put("currencyUomId", p.get("currencyUomId"));
            	ri.put("userLogin", userLogin);
            	dispatcher.runSync("createRequirementItemPO", ri);
            }
        }catch(Exception e){
        	e.printStackTrace();
        	
        }
        return successResult;
	}
    public static Map<String, Object> deleteProposalPO(DispatchContext dctx, Map<String, Object> context){
    	Delegator delegator = dctx.getDelegator();
    	Map<String, Object> retMap = FastMap.newInstance();
		Locale locale = (Locale) context.get("locale");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		try {
			dispatcher.runSync("deleteRequirementPO", context);
			String requirementId = (String) context.get("requirementId");
			List<GenericValue> requirementItem = delegator.findList("RequirementItem", EntityCondition.makeCondition("requirementId", requirementId), UtilMisc.toSet("reqItemSeqId"), null, null, false);
			for(GenericValue item : requirementItem){
				context.put("reqItemSeqId", item.getString("reqItemSeqId"));
				dispatcher.runSync("deleteRequirementItemPO", context);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "deleteRequirementPOError", locale));
		}
		retMap.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("DelysProcurementLabels", "deleteRequirementPOSuccess", locale));
		return retMap;
	}
	public static Map<String, Object> createRequirementPO(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Timestamp requirementStartDate = (Timestamp) context.get("requirementStartDate");
		Timestamp requiredByDate = (Timestamp) context.get("requiredByDate");
		requirementStartDate = UtilDateTime.getDayStart(requirementStartDate);
		requiredByDate = UtilDateTime.getDayEnd(requiredByDate);
		String quantity = (String) context.get("quantity");
		if(quantity != null && !quantity.isEmpty()){
			context.put("quantity", new BigDecimal(quantity));
		}
		GenericValue Requirement = delegator.makeValidValue("Requirement", context);
		String requirementId = delegator.getNextSeqId("Requirement");
		Requirement.set("requirementId", requirementId);
		Requirement.set("requirementStartDate", requirementStartDate);
		Requirement.set("requiredByDate", requiredByDate);
		Map<String, Object> retMap = FastMap.newInstance();
		Locale locale = (Locale) context.get("locale");
		try {
			Requirement.create();
			retMap.put("requirementId", requirementId);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "createRequirementPOError", locale));
		}
		retMap.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("DelysProcurementLabels", "createRequirementPOSuccess", locale));
		return retMap;
	}
	public static Map<String, Object> deleteRequirementPO(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		Locale locale = (Locale) context.get("locale");
		try {
			String requirementId = (String) context.get("requirementId");
			GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
			if(requirement == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "NotFoundRecordToDelete", locale));
			}
			requirement.remove();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "deleteRequirementPOError", locale));
		}
		retMap.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("DelysProcurementLabels", "deleteRequirementPOSuccess", locale));
		return retMap;
	}
	public static Map<String, Object> updateRequirementPO(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String requirementId = (String) context.get("requirementId");
		try {
			GenericValue requirement = delegator.findOne("Requirement", UtilMisc.toMap("requirementId", requirementId), false);
			requirement.setNonPKFields(context);
			requirement.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> createRequirementItemPO(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue Requirement = delegator.makeValidValue("RequirementItem", context);
		String reqId = delegator.getNextSeqId("RequirementItem");
		Requirement.set("reqItemSeqId", reqId);
		
		Map<String, Object> retMap = FastMap.newInstance();
		Locale locale = (Locale) context.get("locale");
		try {
			delegator.create(Requirement);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "createRequirementPOError", locale));
		}
		retMap.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("DelysProcurementLabels", "createRequirementPOSuccess", locale));
		return retMap;
	}
	public static Map<String, Object> deleteRequirementItemPO(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		Locale locale = (Locale) context.get("locale");
		String reqItemSeqId = (String) context.get("reqItemSeqId");
		String requirementId = (String) context.get("requirementId");
		try {
			GenericValue requirement = delegator.findOne("RequirementItem", false, UtilMisc.toMap("requirementId", requirementId, "reqItemSeqId", reqItemSeqId));
			if(requirement == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "NotFoundRecordToDelete", locale));
			}
			requirement.remove();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "updateRequirementPOError", locale));
		}
		retMap.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("DelysProcurementLabels", "updateRequirementPOSuccess", locale));
		return retMap;
	}
	
	public static Map<String, Object> updateRequirementItemPO(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String reqItemSeqId = (String) context.get("reqItemSeqId");
		String requirementId = (String) context.get("requirementId");
		try {
			GenericValue requirement = delegator.findOne("RequirementItem", UtilMisc.toMap("requirementId", requirementId, "reqItemSeqId", reqItemSeqId), false);
			requirement.setNonPKFields(context);
			requirement.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListRequirementPurchaseOrderByParty(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String statusId = parameters.get("statusId")[0];
    	listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
    	List<GenericValue> listRequirement = delegator.findList("Requirement", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
    	List<GenericValue> listRequirementItem = delegator.findList("RequirementItem", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, null, false);
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	for (GenericValue requirement : listRequirement) {
    		String requirementIdTotal = requirement.getString("requirementId");
    		Map<String, Object> row = new HashMap<String, Object>();
    		List<GenericValue> listRowDetails = FastList.newInstance();
    		row.putAll(requirement);
			for (GenericValue requirementItem : listRequirementItem) {
				String requirementId = (String) requirementItem.get("requirementId");
				if (requirementIdTotal.equals(requirementId)) {
					listRowDetails.add(requirementItem);
				}
			}
			List<Map<String, Object>> listToResult = FastList.newInstance();
			
			for (GenericValue rowDetail : listRowDetails){
				Map<String, Object> mapTmp = FastMap.newInstance();
				mapTmp.put("requirementId", rowDetail.getString("requirementId"));
				mapTmp.put("reqItemSeqId", rowDetail.getString("reqItemSeqId"));
				mapTmp.put("productId", rowDetail.getString("productId"));
				mapTmp.put("quantity", rowDetail.getBigDecimal("quantity"));
				mapTmp.put("quantityUomId", rowDetail.get("quantityUomId"));
				mapTmp.put("statusId", rowDetail.getString("statusId"));
				listToResult.add(mapTmp);
			}
			row.put("rowDetail", listToResult);
			listIterator.add(row);
    	}
    	successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> rejectRequirementPurchaseOrderByPartyGroup(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = dctx.getDelegator();
		List<String> listRequirement = (List<String>) context.get("requirementData[]");
		if (!listRequirement.isEmpty()) {
			for (String requirementId : listRequirement) {
				GenericValue requirement = delegator.findOne("Requirement", UtilMisc.toMap("requirementId", requirementId), false);
				requirement.put("statusId", "REQ_PURCH_REJECTED");
				delegator.store(requirement);
				List<GenericValue> listRequirementItem = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
				if(!listRequirementItem.isEmpty()){
					for (GenericValue requirementItem : listRequirementItem) {
						requirementItem.put("statusId", "REQ_PURCH_REJECTED");
						delegator.store(requirementItem);
					}
				}
			}
		}
		return ServiceUtil.returnSuccess();
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListRequirementPurchaseOrderToPO(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	GenericValue createByUserGe = (GenericValue)context.get("userLogin");
		String createByUser = (String)createByUserGe.get("userLoginId");
    	listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "REQ_PURCH_CREATE"));
    	listAllConditions.add(EntityCondition.makeCondition("createdByUserLogin", EntityOperator.EQUALS, createByUser)); 
    	List<GenericValue> listRequirement = delegator.findList("Requirement", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
    	List<GenericValue> listRequirementItem = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("statusId", "REQ_PURCH_CREATE")), null, null, null, false);
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	for (GenericValue requirement : listRequirement) {
    		String requirementIdTotal = requirement.getString("requirementId");
    		Map<String, Object> row = new HashMap<String, Object>();
    		List<GenericValue> listRowDetails = FastList.newInstance();
    		row.putAll(requirement);
			for (GenericValue requirementItem : listRequirementItem) {
				String requirementId = (String) requirementItem.get("requirementId");
				if (requirementIdTotal.equals(requirementId)) {
					listRowDetails.add(requirementItem);
				}
			}
			List<Map<String, Object>> listToResult = FastList.newInstance();
			
			for (GenericValue rowDetail : listRowDetails){
				Map<String, Object> mapTmp = FastMap.newInstance();
				mapTmp.put("requirementId", rowDetail.getString("requirementId"));
				mapTmp.put("reqItemSeqId", rowDetail.getString("reqItemSeqId"));
				mapTmp.put("productId", rowDetail.getString("productId"));
				mapTmp.put("quantity", rowDetail.getBigDecimal("quantity"));
				mapTmp.put("quantityUomId", rowDetail.get("quantityUomId"));
				mapTmp.put("statusId", rowDetail.getString("statusId"));
				listToResult.add(mapTmp);
			}
			row.put("rowDetail", listToResult);
			listIterator.add(row);
    	}
    	successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductTotalByProduct(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	try {
    		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
	    	listIterator = delegator.find("ProductByInventoryItemTotal", cond, null, null, listSortFields, opts);
    	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductTotalByProduct service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	public static Map<String, Object> getConfigPackingUomIdByProductId(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> result = new FastMap<String, Object>();
    	String productId = (String) context.get("productId");
    	List<GenericValue> listConfigPacking = delegator.findList("ConfigPacking", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)), null, null, null, false);
    	List<String> listUomId = new ArrayList<String>();
    	List<GenericValue> listUomTranfer = new ArrayList<GenericValue>();
    	if(!listConfigPacking.isEmpty()){
        	for (GenericValue configPacking : listConfigPacking) {
    			String uomFromId = (String) configPacking.get("uomFromId");
    			String uomToId = (String) configPacking.get("uomToId");
    			listUomId.add(uomFromId);
    			listUomId.add(uomToId);
    		}
        	Set<String> listUomUnique = new HashSet<String>(listUomId);
        	for (String uomUnique : listUomUnique) {
    			GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", uomUnique), false);
    			listUomTranfer.add(uom);
    		}
    	}
    	result.put("listUomTranfer", listUomTranfer);
    	return result;
    }
	
	@SuppressWarnings({ "unused", "unchecked" })
	public static Map<String, Object> createRequirementPurchaseOrderToPO(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Security sec = ctx.getSecurity();
		String facilityId = (String)context.get("facilityId");
		String requirementTypeId = (String)context.get("requirementTypeId");
		String description = (String)context.get("description");
		Timestamp requirementByDate = (Timestamp)context.get("requirementByDate");
		Timestamp requirementStartDate = (Timestamp)context.get("requirementStartDate");
		List<Map<String, String>> listProducts = (List<Map<String, String>>)context.get("listProducts");
		String statusId = "REQ_PURCH_CREATE";
		GenericValue requirement = delegator.makeValue("Requirement");
		String requirementId = delegator.getNextSeqId("Requirement");
		Date date = new Date();
		long dateLong = date.getTime();
		String partyIdFrom = (String) userLogin.get("partyId");
		String createByUser = (String)userLogin.get("userLoginId");
		String partyId = null;
		List<GenericValue> 	listPartyRelationship = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyIdFrom)), null, null, null, false);
		for (GenericValue partyRelationship : listPartyRelationship) {
			String roleTypeIdTo = (String) partyRelationship.get("roleTypeIdTo");
			GenericValue roleType = delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", roleTypeIdTo), false);
			String parentTypeId = (String) roleType.get("parentTypeId");
			if(parentTypeId != null){
				if(parentTypeId.equals("DEPARTMENT")){
					partyId = (String) partyRelationship.get("partyIdTo");
				}
			}
		}
		Timestamp createDate = new Timestamp(dateLong);
		requirement.put("requirementId", requirementId);
		requirement.put("requirementTypeId", requirementTypeId);
		if(!facilityId.equals("")){
			requirement.put("facilityId", facilityId);
		}
		requirement.put("statusId", statusId);
		requirement.put("description", description);
		requirement.put("createdByUserLogin", createByUser);
		requirement.put("createdDate", createDate);
		requirement.put("requiredByDate", requirementByDate);
		requirement.put("requirementStartDate", requirementStartDate);
		requirement.put("partyId", partyId);
		try {
			delegator.create(requirement);
		} catch (GenericEntityException e) {
		    return ServiceUtil.returnError(e.getStackTrace().toString());
		}
		int nextSeqId = 1; 
		GenericValue requirementItem = delegator.makeValue("RequirementItem");
    	for(Map<String, String> item: listProducts){
    		String productId = item.get("productId");
    		String quantity = item.get("quantity");
    		String quantityUomIdToTransfer = item.get("quantityUomIdToTransfer");
    		BigDecimal quantityBig = new BigDecimal(quantity);
    		requirementItem.put("requirementId", requirementId);
    		requirementItem.put("reqItemSeqId", UtilFormatOut.formatPaddedNumber(nextSeqId++, 5));
    		requirementItem.put("productId", productId);
    		requirementItem.put("quantity", quantityBig);
    		requirementItem.put("quantityUomId", quantityUomIdToTransfer);
    		requirementItem.put("createDate", createDate);
    		requirementItem.put("statusId", statusId);
    		try {
    			delegator.create(requirementItem);
    		} catch (GenericEntityException e) {
    		    return ServiceUtil.returnError(e.getStackTrace().toString());
    		}
    	}
		return result;
	}
	
	@SuppressWarnings("unchecked")
   	public static Map<String, Object> sendRequirementPurchaseOrderToPO(DispatchContext ctx, Map<String, Object> context){
   		Map<String, Object> result = new FastMap<String, Object>();
   		Delegator delegator = ctx.getDelegator();
   		LocalDispatcher dispatcher = ctx.getDispatcher();
   		List<String> listRequirementId = (List<String>)context.get("requirementData[]");
   		String roleTypeId = (String)context.get("roleTypeId");
   		String sendMessage = (String)context.get("sendMessage");
   		String action = (String)context.get("action");
   		GenericValue userLogin = (GenericValue)context.get("userLogin");
   		try {
   			for (int i = 0; i < listRequirementId.size(); i++) {
   				String requirementId = listRequirementId.get(i);
   				GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
   				List<GenericValue> listRequirementItem = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
   				if (requirement != null){
   					requirement.put("statusId", "REQ_PURCH_SENT");
   					delegator.createOrStore(requirement);
   				}
   				if(!listRequirementItem.isEmpty()){
   					for (GenericValue requirementItem : listRequirementItem) {
   						requirementItem.put("statusId", "REQ_PURCH_SENT");
   						delegator.createOrStore(requirementItem);
   					}
   				}
   			}
   		} catch (GenericEntityException e) {
   			e.printStackTrace();
   		}
   		try {
   			List<String> listQaAdmin = new ArrayList<String>();
   			List<String> listPartyGroups = SecurityUtil.getPartiesByRoles(roleTypeId, delegator);
   			if (!listPartyGroups.isEmpty()){
   				for (String group : listPartyGroups){
   					try {
   						List<GenericValue> listManagers = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", group, "roleTypeIdFrom", "MANAGER", "roleTypeIdTo", roleTypeId)), null, null, null, false);
   						listManagers = EntityUtil.filterByDate(listManagers);
   						if (!listManagers.isEmpty()){
   							for (GenericValue manager : listManagers){
   								listQaAdmin.add(manager.getString("partyIdFrom"));
   							}
   						}
   					} catch (GenericEntityException e) {
   						ServiceUtil.returnError("get Party relationship error!");
   					}
   				}
   			}
   			if(!listQaAdmin.isEmpty()){
   				for (String managerParty : listQaAdmin){
   					String targetLink = "statusId=REQ_PURCH_SENT";
   					String sendToPartyId = managerParty;
   					Map<String, Object> mapContext = new HashMap<String, Object>();
   					mapContext.put("partyId", sendToPartyId);
   					mapContext.put("action", action);
   					mapContext.put("targetLink", targetLink);
   					mapContext.put("header", UtilProperties.getMessage(resource, sendMessage, (Locale)context.get("locale")));
   					mapContext.put("userLogin", userLogin);
   					dispatcher.runSync("createNotification", mapContext);
   				}
   			}
   		} catch (GenericServiceException e) {
   			e.printStackTrace();
   		}
   		return result;
   	}
	
	public static Map<String, Object> deleteRequirementPurchaseOrderToPO(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	String requirementId = (String) context.get("requirementId");
    	GenericValue requirement = delegator.findOne("Requirement", UtilMisc.toMap("requirementId", requirementId), false);
    	List<GenericValue> listRequirementItem = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
		if(!listRequirementItem.isEmpty()){
			for (GenericValue requirementItem : listRequirementItem) {
				delegator.removeValue(requirementItem);
			}
			delegator.removeValue(requirement);
		}
    	return successResult;
    }
	
	public static Map<String, Object> deleteRequirementItemPurchaseOrderToPO(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	String requirementId = (String) context.get("requirementId");
    	String reqItemSeqId = (String) context.get("reqItemSeqId");
    	GenericValue requirementItem = delegator.findOne("RequirementItem", UtilMisc.toMap("requirementId", requirementId, "reqItemSeqId", reqItemSeqId), false);
    	if(requirementItem != null){
    		delegator.removeValue(requirementItem);
    	}
    	return successResult;
    }
	
	public static Map<String, Object> loadProduct(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		List<GenericValue> listProduct = delegator.findList("ProductByInventoryItemTotal", null, null, null, null, false);
		result.put("listProduct", listProduct);   
		return result;
	}
	
	public static Map<String, Object> editRequirementItemPurchaseOrderToPo(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String requirementId = (String) context.get("requirementId");
		String reqItemSeqId = (String) context.get("reqItemSeqId");
		String productId = (String) context.get("productId");
		String quantity = (String) context.get("quantity");
		String quantityUomId = (String) context.get("quantityUomId");
		long quantityLog = Long.parseLong(quantity);
		BigDecimal quantityBig = new BigDecimal(quantityLog);
		GenericValue requirementItem = delegator.findOne("RequirementItem", UtilMisc.toMap("requirementId", requirementId, "reqItemSeqId", reqItemSeqId), false);
		if(requirementItem != null){
			String productIdData = (String) requirementItem.get("productId");
			BigDecimal quantityData = requirementItem.getBigDecimal("quantity");
			String quantityUomIdData = (String) requirementItem.get("quantityUomId");
			if(productId.equals(productIdData) && quantityBig.compareTo(quantityData) == 0 && quantityUomId.equals(quantityUomIdData)){
				result.put("value", "notEdit");
			}else{
				requirementItem.put("productId", productId);
				requirementItem.put("quantity", quantityBig);
				requirementItem.put("quantityUomId", quantityUomId);
				delegator.store(requirementItem);
				result.put("value", "success");
			}
		}else{
			result.put("value", "error");
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> addProductToRequirementItemByRequirementId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String requirementId = (String) context.get("requirementId");
		List<String> listProductIdData = (List<String>) context.get("productIdData[]");
		List<String> listQuantityData = (List<String>) context.get("quantityData[]");
		List<String> listQuantityUomData = (List<String>) context.get("quantityUomIdData[]");
		
		Date date = new Date();
		long dateLong = date.getTime();
		Timestamp createDate = new Timestamp(dateLong);
		for (int i = 0; i < listProductIdData.size(); i++) {
			String productId = listProductIdData.get(i);
    		String quantity = listQuantityData.get(i);
    		long quantityLog = Long.parseLong(quantity);
    		BigDecimal quantityBig = new BigDecimal(quantityLog);
    		int quantityBigInt = quantityBig.intValue();
    		String quantityUomId = listQuantityUomData.get(i);
    		List<GenericValue> listRequirementItem = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId, "productId", productId, "quantityUomId", quantityUomId)), null, null, null, false);
    		if(!listRequirementItem.isEmpty()){
    			for (GenericValue requirementItemData : listRequirementItem) {
    				BigDecimal quantityByData = requirementItemData.getBigDecimal("quantity");
    				int quantityByDataInt = quantityByData.intValue();
					int valueQuantityInt = quantityBigInt + quantityByDataInt;
					BigDecimal valueQuantityBig = new BigDecimal(valueQuantityInt);
					requirementItemData.put("quantity", valueQuantityBig);
					delegator.store(requirementItemData);
    			}
    		}else{
    			GenericValue requirementItem = delegator.makeValue("RequirementItem");
				delegator.setNextSubSeqId(requirementItem, "reqItemSeqId", 5, 1);
				requirementItem.put("requirementId", requirementId);
	    		requirementItem.put("productId", productId);
	    		requirementItem.put("quantity", quantityBig);
	    		requirementItem.put("quantityUomId", quantityUomId);
	    		requirementItem.put("createDate", createDate);
	    		requirementItem.put("statusId", "REQ_PURCH_CREATE");
				delegator.create(requirementItem);
    		}
		}
		return result;
	}
	
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListRequirementPurchaseOrderRejectByPO(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<EntityCondition> listAllConditionsItem = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	GenericValue createByUserGe = (GenericValue)context.get("userLogin");
		String createByUser = (String)createByUserGe.get("userLoginId");
    	listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "REQ_PURCH_REJECTED"));
    	listAllConditions.add(EntityCondition.makeCondition("createdByUserLogin", EntityOperator.EQUALS, createByUser)); 
    	listAllConditionsItem.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "REQ_PURCH_REJECTED"));
    	List<GenericValue> listRequirement = delegator.findList("Requirement", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
    	List<GenericValue> listRequirementItem = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("statusId", "REQ_PURCH_REJECTED")), null, null, null, false);
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	for (GenericValue requirement : listRequirement) {
    		String requirementIdTotal = requirement.getString("requirementId");
    		Map<String, Object> row = new HashMap<String, Object>();
    		List<GenericValue> listRowDetails = FastList.newInstance();
    		row.putAll(requirement);
			for (GenericValue requirementItem : listRequirementItem) {
				String requirementId = (String) requirementItem.get("requirementId");
				if (requirementIdTotal.equals(requirementId)) {
					listRowDetails.add(requirementItem);
				}
			}
			List<Map<String, Object>> listToResult = FastList.newInstance();
			
			for (GenericValue rowDetail : listRowDetails){
				Map<String, Object> mapTmp = FastMap.newInstance();
				mapTmp.put("requirementId", rowDetail.getString("requirementId"));
				mapTmp.put("reqItemSeqId", rowDetail.getString("reqItemSeqId"));
				mapTmp.put("productId", rowDetail.getString("productId"));
				mapTmp.put("quantity", rowDetail.getBigDecimal("quantity"));
				mapTmp.put("quantityUomId", rowDetail.get("quantityUomId"));
				mapTmp.put("statusId", rowDetail.getString("statusId"));
				listToResult.add(mapTmp);
			}
			row.put("rowDetail", listToResult);
			listIterator.add(row);
    	}
    	successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> loadRequirementItemByRequirementByCreateOrder(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		List<String> listRequirementId = (List<String>) context.get("requirementData[]");
		List<GenericValue> listRequirementItemData = new ArrayList<GenericValue>();
		for (String requirementId : listRequirementId) {
			List<GenericValue> listRequirementItem = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
			if(!listRequirementItem.isEmpty()){
				for (GenericValue requirementItem : listRequirementItem) {
					listRequirementItemData.add(requirementItem);
				}
			}
		}
		result.put("listRequirementItemData", listRequirementItemData);
		return result;
	}
	
	public static Map<String, Object> fetchProductBySupplier(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String supplier = (String) context.get("supplier");
		List<GenericValue> listSupplierProduct = delegator.findList("SupplierProduct", EntityCondition.makeCondition(UtilMisc.toMap("partyId", supplier)), null, null, null, false);
		List<String> listProductBySupplier = new ArrayList<String>();
		List<String> listLastPrice = new ArrayList<String>();
		
		if(!listSupplierProduct.isEmpty()){
			for (GenericValue supplierProduct: listSupplierProduct) {
				String productId = (String) supplierProduct.get("productId");
				BigDecimal lastPrice = supplierProduct.getBigDecimal("lastPrice");
				String lastPriceStr = lastPrice.toString();
				listLastPrice.add(lastPriceStr);
				listProductBySupplier.add(productId);
			}
		}
		result.put("listLastPrice", listLastPrice);
		result.put("listProductBySupplier", listProductBySupplier);
		return result;
	}
	
	public static Map<String, Object> fetchUomIdBasicByProduct(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String productId = (String) context.get("productId");
		GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
		String quantityUomId = (String) product.get("quantityUomId");
		result.put("quantityUomId", quantityUomId);
		return result;
	}
	
	public static Map<String, Object> tranferProductQuantityToQuantityUomIdBasic(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String productId = (String) context.get("productId");
		String uomFromId = (String) context.get("uomFromId");
		String uomToId = (String) context.get("uomToId");
		GenericValue configPacking = delegator.findOne("ConfigPacking", UtilMisc.toMap("productId", productId, "uomFromId", uomFromId, "uomToId", uomToId), false);
		BigDecimal quantityTranfer = new BigDecimal(1);
		String quantityConvert = "1";
		if(configPacking != null){
			quantityTranfer = configPacking.getBigDecimal("quantityConvert");
			quantityConvert = quantityTranfer.toString();
		}
		result.put("quantityTranfer", quantityConvert);
		return result;
	}
	
	public static Map<String, Object> loadCurrencyUomId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String supplier = (String) context.get("supplier");
		List<GenericValue> listCurrencyUomIdBySupplier = delegator.findList("SupplierProductGroupBy", EntityCondition.makeCondition(UtilMisc.toMap("partyId", supplier)), null, null, null, false);
		result.put("listCurrencyUomIdBySupplier", listCurrencyUomIdBySupplier);
		return result;
	}
	
	public static Map<String, Object> loadListFacilityByPO(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		List<GenericValue> listFacility = delegator.findList("Facility", null, null, null, null, false);
		result.put("listFacility", listFacility);   
		return result;
	}
	
	public static Map<String, Object> loadContactMechByFacilityId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String facilityId = (String) context.get("facilityId");
		List<GenericValue> listFacilityContactMech = delegator.findList("FacilityContactMech", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
		String address1 = "";
		String countryGeoId = "";
		String stateProvinceGeoId = "";
		String contactMechId = "";
		if(!listFacilityContactMech.isEmpty()){
			for(GenericValue facilityContactMech: listFacilityContactMech){
				contactMechId = (String) facilityContactMech.get("contactMechId");
				GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
				address1 = (String) postalAddress.get("address1");
				countryGeoId = (String) postalAddress.get("countryGeoId");
				stateProvinceGeoId = (String) postalAddress.get("stateProvinceGeoId");
			}
		}
		
		result.put("contactMechId", contactMechId);   
		result.put("address1", address1);   
		result.put("countryGeoId", countryGeoId);   
		result.put("stateProvinceGeoId", stateProvinceGeoId);   
		return result;
	}
	
	@SuppressWarnings("unused")
	public static Map<String, Object> createPurchaseOrderByRequirement(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException, ParseException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		LocalDispatcher dispatcher = dpx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		String currencyUomId = (String) context.get("currencyUomId");
		String orderName = (String) context.get("orderName");
		GenericValue userLogin = (GenericValue)context.get("userLogin"); 
		String partyIdFrom = (String)context.get("partyIdFrom");
		String partyIdTo = (String) userLogin.get("partyId");
		String contactMechId = (String)context.get("contactMechId");
		String listOrderItems = (String) context.get("orderItems");
		JSONArray arrOrderItems = JSONArray.fromObject(listOrderItems);
		ShoppingCart cart = new ShoppingCart(delegator, null,null, locale, currencyUomId, null, null);
		Security security = dpx.getSecurity();
		try {
			cart.setOrderType("PURCHASE_ORDER");
			cart.setCurrency(dispatcher, currencyUomId);
			cart.setUserLogin(userLogin, dispatcher);
			cart.setLocale(locale);
			cart.setPlacingCustomerPartyId(partyIdTo);
			cart.setBillToCustomerPartyId(partyIdTo);
			cart.setAllShippingContactMechId(contactMechId);
			if ((String)cart.getAttribute("supplierPartyId") == null){
				cart.setAttribute("supplierPartyId", partyIdFrom);
			}
			cart.setOrderPartyId(partyIdFrom);
		} catch (CartItemModifyException e) {
		}
		Timestamp shipBeforeDate = null;
		Timestamp shipAfterDate = null;
		ShoppingCartHelper helper = new ShoppingCartHelper(delegator, dispatcher, cart);
		Map<String, Object> contextMap = FastMap.newInstance();
		int size = arrOrderItems.size();
		for (int i = 0; i < size; i++) {
			JSONObject orderItem = arrOrderItems.getJSONObject(i);
			String productId = (String)orderItem.get("productId");
			Integer quantityInt = (Integer) orderItem.get("quantity");
			String quantityUomId = (String)orderItem.get("quantityUomId");
			String lastPrice = (String)orderItem.get("lastPrice");	
			double lastPriceDou = Double.parseDouble(lastPrice);
			
			double quantityDou = quantityInt.doubleValue();
			double amoundDou = quantityDou * lastPriceDou;
			
			BigDecimal price = new BigDecimal(0);
			BigDecimal amount = new BigDecimal(0);
			
			NumberFormat num = NumberFormat.getInstance(locale);
			double mynb = num.parse(lastPrice).doubleValue();
			
			price = new BigDecimal(mynb);
			price = price.setScale(2, BigDecimal.ROUND_HALF_UP);
			amount = new BigDecimal(amoundDou);
			amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal quantity = new BigDecimal(quantityInt);
			
			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			String productCategoryId = null;
			String catalogId = null;
			List<String> orderBy = new ArrayList<String>();
			orderBy.add("-fromDate");
			if ((String)product.get("primaryProductCategoryId") != null){
				productCategoryId = (String)product.get("primaryProductCategoryId");
			} else {
				List<GenericValue> listCategoryByProducts = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)), null, orderBy, null, false);
				listCategoryByProducts = EntityUtil.filterByDate(listCategoryByProducts);
				if (!listCategoryByProducts.isEmpty()){
					productCategoryId = (String)EntityUtil.getFirst(listCategoryByProducts).get("productCategoryId");
				} 
			}
			List<GenericValue> listCatalogCategorys = delegator.findList("ProdCatalogCategory", EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryId)), null, orderBy, null, false);
			listCatalogCategorys = EntityUtil.filterByDate(listCatalogCategorys);
			if (!listCatalogCategorys.isEmpty()){
				catalogId = (String)EntityUtil.getFirst(listCatalogCategorys).get("prodCatalogId");
			} else {
				String productCategoryIdTmp = productCategoryId;
				while (listCatalogCategorys.isEmpty()){
					listCatalogCategorys = new ArrayList<GenericValue>();
					List<GenericValue> listCategoryParents = delegator.findList("ProductCategoryRollup", EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryIdTmp)), null, orderBy, null, false);
					listCategoryParents = EntityUtil.filterByDate(listCategoryParents);
	    			if (!listCategoryParents.isEmpty()){
	    				productCategoryIdTmp = (String)EntityUtil.getFirst(listCategoryParents).get("parentProductCategoryId");
	    				listCatalogCategorys = delegator.findList("ProdCatalogCategory", EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryIdTmp)), null, orderBy, null, false);
	    				listCatalogCategorys = EntityUtil.filterByDate(listCatalogCategorys);
	    			} else {
	    				break;
	    			}
				}
			}
			if (!listCatalogCategorys.isEmpty()){
				catalogId = (String)EntityUtil.getFirst(listCatalogCategorys).get("prodCatalogId");
			}
			helper.addToCart(catalogId, null, null, productId, productCategoryId, "PRODUCT_ORDER_ITEM", null, null, amount, quantity, null, null, null, null, null, shipBeforeDate, shipAfterDate, null, null, context, null);
//			contextMap.put("update_"+(size-i-1), quantity.toString());
//			contextMap.put("price_"+(size-i-1), lastPrice);
//			contextMap.put("itemType_"+(size-i-1), "PRODUCT_ORDER_ITEM");
		}
		
//		contextMap.put("finalizeReqAdditionalParty", false);
//		contextMap.put("finalizeReqOptions", false);
//		contextMap.put("removeSelected", false);
//		contextMap.put("finalizeReqPayInfo", false);
//		helper.modifyCart(security, userLogin, contextMap, false, null, locale);
		CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);
        checkOutHelper.finalizeOrderEntryShip(0, contactMechId, partyIdTo);
        checkOutHelper.finalizeOrderEntryOptions(0, "STANDARD@_NA_", null, "false", null, "false", null, null, null, null, null);
        try {
			checkOutHelper.calcAndAddTax();
		} catch (GeneralException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        boolean areOrderItemsExploded = CheckOutEvents.explodeOrderItems(delegator, cart);
        
        Map<String, Object> callResult = checkOutHelper.createOrderChangeSuppPrice(userLogin, null, null, null, areOrderItemsExploded, null, null, "false");
		cart.clear();
        return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> loadProductBySupplier(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		List<String> listProductId = (List<String>) context.get("productId[]");
		List<GenericValue> listSupplierProduct = new ArrayList<GenericValue>();
		Map<String, Object> mapSupplierProduct = new HashMap<String, Object>();
		for (String productId : listProductId) {
			listSupplierProduct = delegator.findList("SupplierProduct", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)), null, null, null, false);
			mapSupplierProduct.put(productId, listSupplierProduct);
		}
		result.put("mapSupplierProduct", mapSupplierProduct);   
		return result;
	}
	
	public static Map<String, Object> addNewSupplierForProductId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		LocalDispatcher dispatcher = dpx.getDispatcher();
		String productId = (String) context.get("productId"); 
		String availableFromDateStr = (String) context.get("availableFromDate");
		String availableThruDateStr = (String) context.get("availableThruDate");
		String partyId = (String) context.get("partyId");
		String currencyUomId = (String) context.get("currencyUomId");
		String lastPriceStr = (String) context.get("lastPrice");
		String minimumOrderQuantityStr = (String) context.get("minimumOrderQuantity");
		String shippingPriceStr = (String) context.get("shippingPrice");
		String comments = (String) context.get("comments");
		long availableFromDateLog = Long.parseLong(availableFromDateStr);
		Timestamp availableFromDate = new Timestamp(availableFromDateLog);
		BigDecimal minimumOrderQuantity = new BigDecimal(minimumOrderQuantityStr);	
		BigDecimal lastPrice = new BigDecimal(lastPriceStr);	
		BigDecimal shippingPrice = new BigDecimal(shippingPriceStr);	
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
		String quantityUomId = (String) product.get("quantityUomId");
		GenericValue supplierProduct = delegator.findOne("SupplierProduct", UtilMisc.toMap("productId", productId, "partyId", partyId, "availableFromDate", availableFromDate, "minimumOrderQuantity", minimumOrderQuantity, "currencyUomId", currencyUomId), false);
		if(supplierProduct == null){
			if(availableThruDateStr != null){
				long availableThruDateLog = Long.parseLong(availableThruDateStr);
				Timestamp availableThruDateTime = new Timestamp(availableThruDateLog);
				Map<String, Object> contextInput = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "currencyUomId", currencyUomId ,"productId", productId, "availableThruDate", availableThruDateTime, "availableFromDate", availableFromDate, "minimumOrderQuantity", minimumOrderQuantity, "lastPrice", lastPrice, "shippingPrice", shippingPrice, "comments", comments, "quantityUomId", quantityUomId);
				try {
					dispatcher.runAsync("createSupplierProduct", contextInput);
				} catch (ServiceAuthException e) {
					e.printStackTrace();
				} catch (ServiceValidationException e) {
					e.printStackTrace();
				} catch (GenericServiceException e) {
					e.printStackTrace();
				}
			}
			else{
				Map<String, Object> contextInput = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "currencyUomId", currencyUomId ,"productId", productId, "availableFromDate", availableFromDate, "minimumOrderQuantity", minimumOrderQuantity, "lastPrice", lastPrice, "shippingPrice", shippingPrice, "comments", comments, "quantityUomId", quantityUomId);
				try {
					dispatcher.runAsync("createSupplierProduct", contextInput);
				} catch (ServiceAuthException e) {
					e.printStackTrace();
				} catch (ServiceValidationException e) {
					e.printStackTrace();
				} catch (GenericServiceException e) {
					e.printStackTrace();
				}
			}
			result.put("value", "success");
		}else{
			result.put("value", "exits");
		}
		
		return result;
	}

}
