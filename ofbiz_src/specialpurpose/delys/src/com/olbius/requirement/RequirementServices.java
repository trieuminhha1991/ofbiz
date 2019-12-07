package com.olbius.requirement;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.ObjectType;
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
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.services.SalesServices;
import com.olbius.util.SalesPartyUtil;

public class RequirementServices {
	public static final String module = SalesServices.class.getName();
    public static final String resource = "DelysAdminUiLabels";
    public static final String resource_error = "DelysAdminErrorUiLabels";
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListRequirementSentToCompany(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			if (parameters.containsKey("requirementTypeId") && UtilValidate.isNotEmpty(parameters.get("requirementTypeId"))) {
				String requirementTypeId = parameters.get("requirementTypeId")[0];
				if (UtilValidate.isNotEmpty(requirementTypeId)) {
					listAllConditions.add(EntityCondition.makeCondition("requirementTypeId", requirementTypeId));
				}
			}
			/*if (SecurityUtil.hasRole("DELYS_SALESADMIN_GT", (String) userLogin.get("partyId"), delegator)) {
				newStatus = "REG_PROMO_ACCEPTED";
			} else if (SecurityUtil.hasRole("DELYS_ASM_GT", (String) userLogin.get("partyId"), delegator)) {
				newStatus = "REGPR_ASM_ACCEPTED";
			} else if (SecurityUtil.hasRole("DELYS_SALESSUP_GT", (String) userLogin.get("partyId"), delegator)) {
				newStatus = "REGPR_SUP_ACCEPTED";
			}*/
			if (UtilValidate.isNotEmpty(userLogin)) {
				if (SalesPartyUtil.isDistributor(userLogin, delegator)) {
					listAllConditions.add(EntityCondition.makeCondition("partyId", userLogin.get("partyId")));
					listAllConditions.add(EntityCondition.makeCondition("roleTypeId", "DELYS_DISTRIBUTOR"));
				} else {
					String companyId = EntityUtilProperties.getPropertyValue("general.properties", "ORGANIZATION_PARTY", "company", delegator);
					listAllConditions.add(EntityCondition.makeCondition("partyId", companyId));
					listAllConditions.add(EntityCondition.makeCondition("roleTypeId", "INTERNAL_ORGANIZATIO"));
					if (SalesPartyUtil.isSupervisorEmployee(userLogin, delegator)) {
						listAllConditions.add(EntityCondition.makeCondition("createdByUserLogin", userLogin.get("userLoginId")));
					}
				}
				EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
				listIterator = delegator.find("RequirementAndRole", tmpConditon, null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListRequirementSentToCompany service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListRequirementChangeDate(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		EntityListIterator listIterator = null;
		Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "DACreateSuccessfully", locale));
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		listAllConditions.add(EntityCondition.makeCondition("requirementTypeId", "CHANGE_DATE_REQ"));
		try {
			listIterator = delegator.find("Requirement", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListRequirementChangeDate services: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createRequirementChangeDate(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String companyId = (String) context.get("companyId");
		String customerId = (String) context.get("customerId");
		String contactMechId = (String) context.get("contactMechId");
		String description = (String) context.get("description");
		Timestamp requirementStartDate = (Timestamp) context.get("requirementStartDate");
		Timestamp requiredByDate = (Timestamp) context.get("requiredByDate");
		String requirementTypeId = "CHANGE_DATE_REQ"; //(String) context.get("requirementTypeId");
		String reason = (String) context.get("reason");
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String currencyUomId = (String) context.get("currencyUomId");
		if (UtilValidate.isNotEmpty(currencyUomId)) {
			currencyUomId = SalesPartyUtil.getCurrencyInProperties(delegator);
		}
		String customerRoleId = "";
		List<String> roleCustomerIds = SalesPartyUtil.getListDescendantRoleInclude("DELYS_CUSTOMER", delegator);
		EntityCondition condOne = EntityCondition.makeCondition("partyId", customerId);
		EntityCondition condTwo = EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roleCustomerIds);
		List<String> roleCustomerHereIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyRole", EntityCondition.makeCondition(condOne, EntityOperator.AND, condTwo), null, null, null, false), 
										"roleTypeId", true);
		if (UtilValidate.isEmpty(roleCustomerHereIds)) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAThisCustomerNotHaveRoleValid", locale));
		}
		customerRoleId = roleCustomerHereIds.get(0);
		
		String requirementId = null;
		try {
			requirementId = delegator.getNextSeqId("Requirement");
			Map<String, Object> requirementCtxMap = UtilMisc.<String, Object>toMap("requirementId", requirementId, 
					"requirementTypeId", requirementTypeId, "requirementStartDate", requirementStartDate, 
					"requiredByDate", requiredByDate, "currencyUomId", currencyUomId, 
					"description", description, "reason", reason, 
					"contactMechId", contactMechId, "statusId", "CHANGEDAT_CREATED", 
					"createdDate", nowTimestamp, "createdByUserLogin", userLogin.get("userLoginId"));
			
			GenericValue requirement = delegator.makeValue("Requirement", requirementCtxMap);
			delegator.create(requirement);
			
			Map<String, Object> contextMap2 = UtilMisc.<String, Object>toMap("requirementId", requirement.get("requirementId"), 
					"statusId", requirement.get("statusId"), "statusDate", nowTimestamp, "statusUserLogin", userLogin.get("userLoginId"));
			delegator.create("RequirementStatus", contextMap2);
			
			// create roles in requirement
			List<GenericValue> toBeStored = new LinkedList<GenericValue>();
			Map<String, Object> contextRoleMap = UtilMisc.<String, Object>toMap("requirementId", requirementId, "partyId", companyId, "roleTypeId", "OWNER", "fromDate", nowTimestamp);
			GenericValue requirementRoleOwner = delegator.makeValidValue("RequirementRole", contextRoleMap);
			toBeStored.add(requirementRoleOwner);
			contextRoleMap = UtilMisc.<String, Object>toMap("requirementId", requirementId, "partyId", companyId, "roleTypeId", "INTERNAL_ORGANIZATIO", "fromDate", nowTimestamp);
			GenericValue requirementRoleOwner2 = delegator.makeValidValue("RequirementRole", contextRoleMap);
			toBeStored.add(requirementRoleOwner2);
			contextRoleMap = UtilMisc.<String, Object>toMap("requirementId", requirementId, "partyId", customerId, "roleTypeId", customerRoleId, "fromDate", nowTimestamp);
			GenericValue requirementRoleOwner3 = delegator.makeValidValue("RequirementRole", contextRoleMap);
			toBeStored.add(requirementRoleOwner3);
			
			delegator.storeAll(toBeStored);
			
			// create requirement item
			List<Map<String, String>> listProducts = (List<Map<String, String>>)context.get("listProducts");
	    	for(Map<String, String> item: listProducts){
	    		String productId = item.get("productId");
	    		String quantityTmp = item.get("quantity");
	    		String quantityUomId = item.get("quantityUomIdRequire");
	    		String expDateTmp = item.get("expireDate");
	    		String orderId = item.get("orderId");
	    		String orderItemSeqId = item.get("orderItemSeqId");
	    		Timestamp expireDate = null;
	    		BigDecimal quantity = null;
	    		if (UtilValidate.isNotEmpty(expDateTmp) && !"null".equals(expDateTmp)){
	    			expireDate = new Timestamp(Long.parseLong(expDateTmp, 10));
	    		}
	    		try {
		            quantity = (BigDecimal) ObjectType.simpleTypeConvert(quantityTmp, "BigDecimal", null, locale);
		        } catch (Exception e) {
		            Debug.logWarning(e, "Problems parsing quantity string: " + quantityTmp, module);
		        }
	    		try {
	    			Map<String, Object> contextItemMap = UtilMisc.<String, Object>toMap("requirementId", requirementId, "requirementTypeId", requirementTypeId, "productId", productId,
		    				"quantity", quantityTmp, "quantityUomId", quantityUomId, "currencyUomId", currencyUomId, "expireDate", expireDate, "statusId", "REQ_ITEM_CREATED", "userLogin", userLogin);
					dispatcher.runSync("addProductToRequirement", contextItemMap);
					
					if (UtilValidate.isNotEmpty(orderId) && UtilValidate.isNotEmpty(orderItemSeqId)) {
						GenericValue orderRequirementCommitment = delegator.makeValue("OrderRequirementCommitment");
						orderRequirementCommitment.put("requirementId", requirement.get("requirementId"));
						orderRequirementCommitment.put("orderId", orderId);
						orderRequirementCommitment.put("orderItemSeqId", orderItemSeqId);
						orderRequirementCommitment.put("quantity", quantity);
						delegator.create(orderRequirementCommitment);
					}
				} catch (GenericServiceException e) {
					e.printStackTrace();
				}
	    	}
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "createError", new Object[]{e.getMessage()}, locale));
		}
		if (UtilValidate.isEmpty(requirementId)) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DACreateNotSuccessful", locale));
		}
		result.put("requirementId", requirementId);
		return result;
	}
}
