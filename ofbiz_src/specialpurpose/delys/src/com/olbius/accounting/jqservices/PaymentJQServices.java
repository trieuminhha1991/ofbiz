package com.olbius.accounting.jqservices;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;


public class PaymentJQServices {
	public static final String module = PaymentJQServices.class.getName();
	public static final String resource = "AccountingUiLabels";
    public static final String resource_error = "AccountingErrorUiLabels";
    public static final String resourceDelys = "DelysAdminUiLabels";
    public static final String resourceDelys_error = "DelysAdminUiLabels";
    public static final String resourceProduct = "AccountingUiLabels";
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListInvoiceItemsInPayment(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	mapCondition.put("invoiceId", (String)parameters.get("invoiceId")[0]);
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("InvoiceItem", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListInvoiceItemsInPayment service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProduct(DispatchContext ctx, Map<String, ? extends Object> context) {
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
    		listIterator = delegator.find("Product", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListProduct service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListPaymentMethodTypes(DispatchContext ctx, Map<String, ? extends Object> context) {
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
    		if(UtilValidate.isNotEmpty(listAllConditions)){
    			for(int i  = 0; i < listAllConditions.size();i++){
    				String listCondStr = listAllConditions.get(i).toString().trim();
    				String fieldCond = listCondStr.split(" ")[0];
    				int index = listCondStr.indexOf("%");
    				if(index != -1){
    					String tmpValue = listCondStr.substring(index + 1, listCondStr.length() - 1);
    					if(fieldCond.equals("defaultGlAccountId")){
    	    				listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("accountName",EntityJoinOperator.LIKE,"%" + tmpValue + "%"),listAllConditions.get(i)),EntityJoinOperator.OR));
    	    				listAllConditions.remove(i);
    	    				break;
    					}else continue;
    				}
    			}
    		}
    		
    		listIterator = delegator.find("PaymentMethodTypeAndGlAccountDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListPaymentMethodTypes service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    public static Map<String,Object> jqGetListPaymentDetail(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = dpct.getDelegator();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Map<String,Object> result = FastMap.newInstance();
		List<GenericValue> listPaymentPartialList = FastList.newInstance();
		List<EntityCondition> listConditions = FastList.newInstance();
		try {
			int pagesize = (parameters.get("pagesize")[0] != null)?Integer.parseInt(parameters.get("pagesize")[0]) : null;
			int pagenum = (parameters.get("pagenum")[0] != null)?Integer.parseInt(parameters.get("pagenum")[0]):null;
			opts.setDistinct(true);
			if(UtilValidate.isNotEmpty(pagesize) && UtilValidate.isNotEmpty(pagenum)){
				int startIndex = pagesize*pagenum;
				int endIndex = startIndex + pagesize;
				listIterator = delegator.find("PaymentAndPartyNameView", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
				listPaymentPartialList  = listIterator.getPartialList(startIndex, endIndex);
			}
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			String erMsg = "error when call service jqGetListInvoice cause : " + e.getMessage();
			Debug.log(e,erMsg,module);
		}
		return result;
	}
 
    @SuppressWarnings("unused")
	public static Map<String,Object> jqGetListPaymentNotApplied(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Map<String,Object> result = FastMap.newInstance();
		String invoiceId = (String) parameters.get("invoiceId")[0];
		try {
			Map<String,Object> mapTmp = FastMap.newInstance();
			mapTmp.put("invoiceId",invoiceId);
			mapTmp.put("userLogin", (GenericValue) context.get("userLogin"));
			Map<String,Object> resultRunSync = dpct.getDispatcher().runSync("getListNotAppliedPayments", mapTmp);
			if(ServiceUtil.isSuccess(resultRunSync)){
				List<Map<String,Object>> listTmp = FastList.newInstance();
				if((resultRunSync.get("payments")  instanceof java.util.List)){
					@SuppressWarnings("unchecked")
					List<Map<String,Object>> listPayments = (List<Map<String,Object>>) resultRunSync.get("payments");
					if(listPayments != null){
							result.put("listIterator", listPayments);
							result.put("TotalRows", String.valueOf(listPayments.size()));
					}
				}else if(resultRunSync.get("payments") == null) {
					result.put("listIterator",listTmp);
					result.put("TotalRows",String.valueOf(0));
				}
			}
		} catch (Exception e) {
			String erMsg = "Fatal error when get List Payments when call servicer jqGetListPaymentNotApplied cause : " + e.getMessage();
			Debug.log(e,erMsg,module);
		}
		return result;
    }
    
    public static Map<String,Object> jqgetListEditPaymentApps(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = dpct.getDelegator();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Map<String,Object> result = FastMap.newInstance();
		String paymentId = (String) parameters.get("paymentId")[0];
		try {
			int pagesize = (parameters.get("pagesize")[0] != null)?Integer.parseInt(parameters.get("pagesize")[0]) : null;
			int pagenum = (parameters.get("pagenum")[0] != null)?Integer.parseInt(parameters.get("pagenum")[0]):null;
			int startIndex = pagesize*pagenum;
			int endIndex = startIndex + pagesize;
			listAllConditions.add(EntityCondition.makeCondition("paymentId",EntityJoinOperator.EQUALS,paymentId));
			listAllConditions.add(EntityCondition.makeCondition("invoiceId",EntityJoinOperator.NOT_EQUAL,null));
			listSortFields.add("invoiceId");
			listSortFields.add("invoiceItemSeqId");
			
			listIterator = delegator.find("PaymentApplication", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			List<GenericValue> listTmp  = listIterator.getPartialList(startIndex, endIndex);
			result.put("listIterator", listTmp);
			result.put("TotalRows", String.valueOf(listTmp.size()));
			
		} catch (Exception e) {
			String erMsg = "Fatal error when get List Payments when call servicer jqgetListEditPaymentApps cause : " + e.getMessage();
			Debug.log(e,erMsg,module);
		}
		return result;
    }
    
    
    public static Map<String,Object> updateApplicationPayment(DispatchContext dpct,Map<String,Object> context){
    	Delegator delegator = dpct.getDelegator();
		Map<String,Object> result = ServiceUtil.returnSuccess();
		String invoiceId = (String) context.get("invoiceId");
		String paymentId = (String) context.get("paymentId");
		BigDecimal amount = (BigDecimal) context.get("amount");
		Timestamp effectiveDate = (Timestamp) context.get("effectiveDate");
		BigDecimal amountApplied = (BigDecimal) context.get("amountApplied");
		BigDecimal amountToApply = (BigDecimal) context.get("amountToApply");
		
		try {
			Map<String,Object> mapTcx = FastMap.newInstance();
			mapTcx.put("invoiceId", invoiceId);
			mapTcx.put("paymentId", paymentId);
			mapTcx.put("amount", amount);
			mapTcx.put("effectiveDate", effectiveDate);
			mapTcx.put("amountApplied", amountToApply);
			mapTcx.put("dummy", amountApplied);
			mapTcx.put("userLogin", (GenericValue) context.get("userLogin"));
			 try {
				 dpct.getDispatcher().runSync("updatePaymentApplicationDeff", mapTcx);
			} catch (Exception e) {
				String erMsg = "Fatal error when call servicer updateApplicationPayment cause : " + e.getMessage();
				Debug.log(e,erMsg,module);
				return ServiceUtil.returnError("error");
			}
		} catch (Exception e) {
			String erMsg = "Fatal error when get List Payments when call servicer jqgetListEditPaymentApps cause : " + e.getMessage();
			Debug.log(e,erMsg,module);
		}
		return result;
    }
    
}
