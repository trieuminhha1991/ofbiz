package com.olbius.accounting.jqservices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class APPaymentJQServices {
	public static final String module = APPaymentJQServices.class.getName();
	public static final String resource = "widgetUiLabels";
    public static final String resourceError = "widgetErrorUiLabels";
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListAPPayment(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	mapCondition.put("parentTypeId", "DISBURSEMENT");
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("PaymentAndTypeAndCreditCardDetail", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListAPPayment service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    
    public static Map<String, Object> jqGetListApplicationsEdit(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
    	String paymentId = (String) parameters.get("paymentId")[0];
    	try {
    		if(UtilValidate.isNotEmpty(paymentId)){
    			listAllConditions.add(EntityCondition.makeCondition("paymentId",EntityJoinOperator.EQUALS,paymentId));
    		}else {
    			Debug.log("Not find field paymentId !");
    			return ServiceUtil.returnError("Required paymentId to get list PaymentApplication!");
    		} 
    		listAllConditions.add(EntityCondition.makeCondition("toPaymentId",EntityJoinOperator.NOT_EQUAL,null));
    		listSortFields.add("toPaymentId");
    		listIterator = delegator.find("PaymentApplication", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
    	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListApplicationsEdit service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    public static Map<String, Object> JqGetListApplicationsTax(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
    	String paymentId = (String) parameters.get("paymentId")[0];
    	try {
    		
    		
    		if(UtilValidate.isNotEmpty(paymentId)){
    			listAllConditions.add(EntityCondition.makeCondition("paymentId",EntityJoinOperator.EQUALS,paymentId));
    		}else {
    			Debug.log("Not find field paymentId !");
    			return ServiceUtil.returnError("Required paymentId to get list PaymentApplication!");
    		} 
    		listAllConditions.add(EntityCondition.makeCondition("taxAuthGeoId",EntityJoinOperator.NOT_EQUAL,null));
    		listSortFields.add("taxAuthGeoId");
    		listIterator = delegator.find("PaymentApplication", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
    	} catch (Exception e) {
			String errMsg = "Fatal error calling JqGetListApplicationsTax service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
	
    
    
}
