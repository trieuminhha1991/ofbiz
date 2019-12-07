package com.olbius.acc.trans;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.common.util.EntityMiscUtil;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JqxTransServices {
	public static final String MODULE = JqxTransServices.class.getName();
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListAcctgTrans(DispatchContext ctx, Map<String, Object> context) {
		//Variables
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();
    	EntityListIterator listIterator = null;
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	
    	//Get parameters
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	/*Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");*/
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	//Get Current Organization
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	//Set distinct
    	opts.setDistinct(true);
    	//Sort by transactionDate
    	listSortFields.add("-transactionDate");
    	try {
    		//Create condition
    		EntityCondition invoiceCond = EntityCondition.makeCondition(mapCondition);
    		listAllConditions.add(invoiceCond);
    		if (organizationPartyId!= null){
        		EntityCondition organizationPartyCon = EntityCondition.makeCondition("organizationPartyId", EntityJoinOperator.EQUALS, organizationPartyId);
        		listAllConditions.add(organizationPartyCon);
        	}
    		//Get data
    		listIterator = EntityMiscUtil.processIterator(parameters, result, delegator, "AcctgTrans", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);

    		result.put("listIterator", listIterator);
    	} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
			result = ServiceUtil.returnError(e.getMessage());
		}
    	return result;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListAcctgTransAndEntries(DispatchContext ctx, Map<String, Object> context) {
		//Get delegator
		Delegator delegator = ctx.getDelegator();
		
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		
    	//Get parameters
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	listSortFields.add("reciprocalSeqId");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	try {
    		if(parameters.containsKey("invoiceId")) {
    			String invoiceId = parameters.get("invoiceId")[0];
    			mapCondition.put("invoiceId", invoiceId);
    		}
    		if(parameters.containsKey("paymentId")) {
    			String paymentId = parameters.get("paymentId")[0];
    			mapCondition.put("paymentId", paymentId);
    		}
    		String acctgTransId = parameters.get("acctgTransId")[0];
    		mapCondition.put("acctgTransId", acctgTransId);
    		EntityCondition invoiceCond = EntityCondition.makeCondition(mapCondition);
    		listAllConditions.add(invoiceCond);
    		if (organizationPartyId!= null)
        	{
        		EntityCondition organizationPartyCon = EntityCondition.makeCondition("organizationPartyId", EntityJoinOperator.EQUALS, organizationPartyId);
        		listAllConditions.add(organizationPartyCon);
        	}    		
    		listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "AcctgTransAndEntries", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
    	} catch (Exception e) {
			String errMsg = "Fatal error calling getListAcctgTransAndEntries service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListShipmentJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	EntityListIterator listIterator = null;
    	try {
    		if(UtilValidate.isEmpty(listSortFields)){
    			listSortFields.add("-shipmentId");
    		}
    		listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "Shipment", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListAcctgTransHistoryJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String acctgTransId = parameters.get("acctgTransId") != null? parameters.get("acctgTransId")[0] : null;
		EntityListIterator listIterator = null;
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("-changeDate");
			}
			if(acctgTransId != null){
				listAllConditions.add(EntityCondition.makeCondition("acctgTransId", acctgTransId));
				listIterator = delegator.find("AcctgTransHistoryAndParty", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListAcctgTransEntryHistoryJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String acctgTransId = parameters.get("acctgTransId") != null? parameters.get("acctgTransId")[0] : null;
		String changeDateStr = parameters.get("changeDate") != null? parameters.get("changeDate")[0] : null;
		EntityListIterator listIterator = null;
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("-changeDate");
			}
			if(acctgTransId != null && changeDateStr != null){
				Timestamp changeDate = new Timestamp(Long.parseLong(changeDateStr));
				listAllConditions.add(EntityCondition.makeCondition("acctgTransId", acctgTransId));
				listAllConditions.add(EntityCondition.makeCondition("changeDate", changeDate));
				listIterator = delegator.find("AcctgTransEntryHistory", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
}
