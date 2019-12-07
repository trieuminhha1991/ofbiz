package com.olbius.salesmtl.partycustomer;

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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.util.SalesUtil;
import com.olbius.crm.CallcenterServices;

import javolution.util.FastMap;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by user on 3/22/18.
 */
public class PartyCustomerServices {
    public static final String module = PartyCustomerServices.class.getName();
    public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";

    public static Map<String, Object> createPartyCustomer(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        String partyId =(String) context.get("partyId");
        String supervisorId = (String) context.get("supervisorId");
        String salesmanId = (String) context.get("salesmanId");
        String distributorId = (String) context.get("distributorId");
        String visitFrequencyTypeId = (String) context.get("visitFrequencyTypeId");
        if (visitFrequencyTypeId == null) visitFrequencyTypeId = "F0";
        String salesMethodChannelEnumId = (String) context.get("salesMethodChannelEnumId");
        if(UtilValidate.isNotEmpty(distributorId)) {
            GenericValue distributor = delegator.findOne("PartyDistributor", UtilMisc.toMap("partyId", distributorId), false);
            supervisorId = distributor.getString("supervisorId");
        }
        /*
        if(distributorId != null){
        	GenericValue d = delegator.findOne("PartyDistributor", UtilMisc.toMap("partyId",distributorId), false);
        	if(d != null) 
        		supervisorId = d.getString("supervisorId");
        }
        */
        GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
        GenericValue partyGroup = delegator.findOne("PartyGroup" , UtilMisc.toMap("partyId", partyId), false);
        GenericValue partyCustomer = delegator.makeValue("PartyCustomer");
        partyCustomer.set("partyId", partyId);
        partyCustomer.set("partyCode", party.get("partyCode"));
        partyCustomer.set("statusId", party.get("statusId"));
        partyCustomer.set("fullName", partyGroup.get("groupName"));
        partyCustomer.set("preferredCurrencyUomId", party.get("preferredCurrencyUomId"));
        partyCustomer.set("officeSiteName", partyGroup.get("officeSiteName"));
        partyCustomer.set("supervisorId", supervisorId);
        partyCustomer.set("distributorId", distributorId);
        partyCustomer.set("salesmanId", salesmanId);
        partyCustomer.set("partyTypeId", party.get("partyTypeId"));
        partyCustomer.set("createdDate", UtilDateTime.nowTimestamp());
        partyCustomer.set("visitFrequencyTypeId", visitFrequencyTypeId);
        partyCustomer.set("salesMethodChannelEnumId", salesMethodChannelEnumId);
        partyCustomer.create();
        successResult.put("partyId", partyId);
        return successResult;
    }
    public static Map<String, Object> createOrUpdatePartyCustomer(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
        Delegator delegator = ctx.getDelegator();
        String partyId = (String) context.get("partyId");
        GenericValue partyCustomer = delegator.findOne("PartyCustomer", UtilMisc.toMap("partyId", partyId), false);
        if(UtilValidate.isNotEmpty(partyCustomer)) {
            return updatePartyCustomer(ctx, context);
        }
        else return createPartyCustomer(ctx, context);
    }

    public static Map<String, Object> updatePartyCustomer(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        String partyId = (String) context.get("partyId");
        String partyCode = (String) context.get("partyCode");
        String fullName = (String) context.get("fullName");
        String salesmanId = (String) context.get("salesmanId");
        String visitFrequencyTypeId = (String) context.get("visitFrequencyTypeId");
        successResult.put("partyId", partyId);

        GenericValue partyCustomer = delegator.findOne("PartyCustomer", UtilMisc.toMap("partyId", partyId), false);
        //if(partyCustomer.getString("supervisorId").equals(supervisorId)) return successResult
		partyCustomer.set("partyCode", partyCode);
		partyCustomer.set("fullName", fullName);
        partyCustomer.set("salesmanId", salesmanId);
        partyCustomer.set("visitFrequencyTypeId", visitFrequencyTypeId);
        partyCustomer.store();
        return successResult;
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListCustomerRelContact(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		String partyId = SalesUtil.getParameter(parameters, "partyId");
    		if (UtilValidate.isNotEmpty(partyId)) {
    			listAllConditions.add(EntityCondition.makeCondition("partyIdFrom", partyId));
    			listAllConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "CONTACT_REL"));
    			
    			if (UtilValidate.isEmpty(listSortFields)) {
    				listSortFields.add("thruDate");
    				listSortFields.add("-fromDate");
    			}
    			listIterator = delegator.find("PartyToAndPartyNameAndTelephoneDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListCustomerRelContact service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    public static Map<String, Object> createCustomerRelContact(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String,Object> successResult= ServiceUtil.returnSuccess();
		
		try {
			String customerId = (String) context.get("customerId");
			String roleTypeId = (String) context.get("roleTypeId");
			String firstName = (String) context.get("firstName");
			String middleName = (String) context.get("middleName");
			String lastName = (String) context.get("lastName");
			String gender = (String) context.get("gender");
			String phoneNumber = (String) context.get("phoneNumber");
			Timestamp fromDate = UtilDateTime.nowTimestamp();
			
			GenericValue customer = delegator.findOne("Party", UtilMisc.toMap("partyId", customerId), true);
			if (customer == null) {
				return ServiceUtil.returnError("Khong tim thay khach hang co id = " + customerId);
			}
			
			// create person
			String partyContactId = "CUSTREL" + delegator.getNextSeqId("Party");
			Map<String, Object> partyContactCtx = FastMap.newInstance();
			partyContactCtx.put("partyId", partyContactId);
			partyContactCtx.put("statusId", "PARTY_ENABLED");
			partyContactCtx.put("firstName", firstName);
			partyContactCtx.put("middleName", middleName);
			partyContactCtx.put("lastName", lastName);
			partyContactCtx.put("gender", gender);
			partyContactCtx.put("userLogin", userLogin);
			Map<String, Object> resultCreatePartyContact = dispatcher.runSync("createPerson", partyContactCtx);
			if (ServiceUtil.isError(resultCreatePartyContact)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreatePartyContact));
			}
			
			// update party code
			delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", partyContactId), EntityCondition.makeCondition("partyId", partyContactId));
			
			// create party role
			Map<String, Object> partyContactRoleCtx = FastMap.newInstance();
			partyContactRoleCtx.put("partyId", partyContactId);
			partyContactRoleCtx.put("roleTypeId", roleTypeId);
			partyContactRoleCtx.put("userLogin", userLogin);
			Map<String, Object> resultCreatePartyContactRole = dispatcher.runSync("createPartyRole", partyContactRoleCtx);
			if (ServiceUtil.isError(resultCreatePartyContactRole)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreatePartyContactRole));
			}
			
			// create telephone number
			String askForName = (new StringBuilder()).append(lastName).append(" ").append(middleName).append(" ").append(firstName).toString();
			CallcenterServices.createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PRIMARY_PHONE", phoneNumber, askForName, partyContactId, userLogin);
			
			// create party relationship
			Map<String, Object> partyContactRelCtx = FastMap.newInstance();
			partyContactRelCtx.put("partyIdFrom", customerId);
			partyContactRelCtx.put("partyIdTo", partyContactId);
			partyContactRelCtx.put("roleTypeIdFrom", "CUSTOMER");
			partyContactRelCtx.put("roleTypeIdTo", roleTypeId);
			partyContactRelCtx.put("fromDate", fromDate);
			partyContactRelCtx.put("partyRelationshipTypeId", "CONTACT_REL");
			partyContactRelCtx.put("userLogin", userLogin);
			Map<String, Object> resultCreatePartyContactRel = dispatcher.runSync("createPartyRelationship", partyContactRelCtx);
			if (ServiceUtil.isError(resultCreatePartyContactRel)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreatePartyContactRel));
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling createCustomerRelContact service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		return successResult;
	}
    
    public static Map<String, Object> updateCustomerRelContact(DispatchContext dctx, Map<String, Object> context){
    	Delegator delegator = dctx.getDelegator();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Locale locale = (Locale)context.get("locale");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String,Object> successResult= ServiceUtil.returnSuccess();
    	
    	try {
    		String customerId = (String) context.get("customerId");
    		String partyContactId = (String) context.get("partyContactId");
    		String roleTypeId = (String) context.get("roleTypeId");
    		String firstName = (String) context.get("firstName");
    		String middleName = (String) context.get("middleName");
    		String lastName = (String) context.get("lastName");
    		String gender = (String) context.get("gender");
    		String phoneNumber = (String) context.get("phoneNumber");
    		
    		GenericValue customer = delegator.findOne("Party", UtilMisc.toMap("partyId", customerId), true);
    		if (customer == null) {
    			return ServiceUtil.returnError("Khong tim thay khach hang co id = " + customerId);
    		}
    		GenericValue partyContact = delegator.findOne("Party", UtilMisc.toMap("partyId", partyContactId), true);
    		if (partyContact == null) {
    			return ServiceUtil.returnError("Khong tim thay nguoi lien he co id = " + partyContactId);
    		}
    		
    		// update person
    		Map<String, Object> partyContactCtx = FastMap.newInstance();
    		partyContactCtx.put("partyId", partyContactId);
    		partyContactCtx.put("firstName", firstName);
    		partyContactCtx.put("middleName", middleName);
    		partyContactCtx.put("lastName", lastName);
    		partyContactCtx.put("gender", gender);
    		partyContactCtx.put("userLogin", userLogin);
    		Map<String, Object> resultUpdatePartyContact = dispatcher.runSync("updatePerson", partyContactCtx);
    		if (ServiceUtil.isError(resultUpdatePartyContact)) {
    			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultUpdatePartyContact));
    		}
    		
    		// check party role
    		GenericValue partyContactRole = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", partyContactId, "roleTypeId", roleTypeId), false);
    		if (partyContactRole == null) {
    			// remove party role is exist, because the roles do not belong to this party contact
    			delegator.removeByCondition("PartyRelationship", EntityCondition.makeCondition(EntityCondition.makeCondition("partyIdFrom", customerId), 
    					EntityOperator.AND, EntityCondition.makeCondition("partyIdTo", partyContactId)));
    			delegator.removeByCondition("PartyRole", EntityCondition.makeCondition("partyId", partyContactId));
    			
    			// create party role new
        		Map<String, Object> partyContactRoleCtx = FastMap.newInstance();
        		partyContactRoleCtx.put("partyId", partyContactId);
        		partyContactRoleCtx.put("roleTypeId", roleTypeId);
        		partyContactRoleCtx.put("userLogin", userLogin);
        		Map<String, Object> resultCreatePartyContactRole = dispatcher.runSync("createPartyRole", partyContactRoleCtx);
        		if (ServiceUtil.isError(resultCreatePartyContactRole)) {
        			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreatePartyContactRole));
        		}
        		// create party relationship
    			Map<String, Object> partyContactRelCtx = FastMap.newInstance();
    			partyContactRelCtx.put("partyIdFrom", customerId);
    			partyContactRelCtx.put("partyIdTo", partyContactId);
    			partyContactRelCtx.put("roleTypeIdFrom", "CUSTOMER");
    			partyContactRelCtx.put("roleTypeIdTo", roleTypeId);
    			partyContactRelCtx.put("fromDate", UtilDateTime.nowTimestamp());
    			partyContactRelCtx.put("partyRelationshipTypeId", "CONTACT_REL");
    			partyContactRelCtx.put("userLogin", userLogin);
    			Map<String, Object> resultCreatePartyContactRel = dispatcher.runSync("createPartyRelationship", partyContactRelCtx);
    			if (ServiceUtil.isError(resultCreatePartyContactRel)) {
    				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreatePartyContactRel));
    			}
    		}
    		
    		// check telephone number
    		List<EntityCondition> conds = new ArrayList<EntityCondition>();
    		conds.add(EntityCondition.makeCondition("partyId", partyContactId));
    		conds.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_PHONE"));
    		conds.add(EntityUtil.getFilterByDateExpr());
    		GenericValue partyContactContactMech = EntityUtil.getFirst(delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(conds), null, null, null, false));
    		if (partyContactContactMech != null) {
    			GenericValue telecomNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", partyContactContactMech.getString("contactMechId")), false);
    			if (telecomNumber != null) {
    				String askForName = (new StringBuilder()).append(lastName).append(" ").append(middleName).append(" ").append(firstName).toString();
    				telecomNumber.set("contactNumber", phoneNumber);
    				telecomNumber.set("askForName", askForName);
    				delegator.store(telecomNumber);
    			}
    		} else {
    			// create telephone number
        		String askForName = (new StringBuilder()).append(lastName).append(" ").append(middleName).append(" ").append(firstName).toString();
        		CallcenterServices.createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PRIMARY_PHONE", phoneNumber, askForName, partyContactId, userLogin);
    		}
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling updateCustomerRelContact service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    		return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
    	}
    	return successResult;
    }
    
    public static Map<String, Object> deleteCustomerRelContact(DispatchContext dctx, Map<String, Object> context){
    	Delegator delegator = dctx.getDelegator();
    	Locale locale = (Locale)context.get("locale");
    	Map<String,Object> successResult= ServiceUtil.returnSuccess();
    	
    	try {
    		String partyIdFrom = (String) context.get("partyIdFrom");
    		String partyIdTo = (String) context.get("partyId");
    		Timestamp thruDate = UtilDateTime.nowTimestamp();
    		
    		List<EntityCondition> conds = new ArrayList<EntityCondition>();
    		conds.add(EntityCondition.makeCondition("partyIdFrom", partyIdFrom));
    		conds.add(EntityCondition.makeCondition("partyIdTo", partyIdTo));
    		conds.add(EntityUtil.getFilterByDateExpr());
    		delegator.storeByCondition("PartyRelationship", UtilMisc.toMap("thruDate", thruDate), EntityCondition.makeCondition(conds));
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling deleteCustomerRelContact service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    		return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
    	}
    	return successResult;
    }
    
    public static Map<String, Object> reopenCustomerRelContact(DispatchContext dctx, Map<String, Object> context){
    	Delegator delegator = dctx.getDelegator();
    	Locale locale = (Locale)context.get("locale");
    	Map<String,Object> successResult= ServiceUtil.returnSuccess();
    	
    	try {
    		String partyIdFrom = (String) context.get("partyIdFrom");
    		String partyIdTo = (String) context.get("partyId");
    		String fromDateStr = (String) context.get("fromDate");
    		Timestamp fromDate = null;
    		try {
    			fromDate = new Timestamp(new Long(fromDateStr));
    		} catch (Exception e) {
    			Debug.logError("Warning format fromDate from Long to Timestamp", module);
    		}
    		
    		List<EntityCondition> conds = new ArrayList<EntityCondition>();
    		conds.add(EntityCondition.makeCondition("partyIdFrom", partyIdFrom));
    		conds.add(EntityCondition.makeCondition("partyIdTo", partyIdTo));
    		conds.add(EntityCondition.makeCondition("fromDate", fromDate));
    		delegator.storeByCondition("PartyRelationship", UtilMisc.toMap("thruDate", null), EntityCondition.makeCondition(conds));
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling reopenCustomerRelContact service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    		return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
    	}
    	return successResult;
    }
    
}
