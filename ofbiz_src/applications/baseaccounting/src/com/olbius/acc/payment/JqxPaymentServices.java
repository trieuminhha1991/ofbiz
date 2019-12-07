package com.olbius.acc.payment;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.acc.utils.UtilServices;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.SecurityUtil;
import com.olbius.common.util.EntityMiscUtil;

public class JqxPaymentServices {
	
	public static final String MODULE = JqxPaymentServices.class.getName();
	public static final String RESOURCE_CORE_LOG = "baselogistics.properties";
    public static final String RSN_PRTROLE_CORE_LOG = "roleTypeId.receiveMsg.order.approved";
    public static final String logistics_resource = "BaseLogisticsUiLabels";
    
	public static Map<String, Object> getListPayments(DispatchContext ctx, Map<String, Object> context) {
    	@SuppressWarnings("unchecked")
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	String paymentType = ((String[])parameters.get("paymentType"))[0];
		PaymentList pList = PaymentListFactory.getPaymentList(paymentType);
    	Map<String, Object> result = FastMap.newInstance();
		try {
			result = pList.getListPayments(ctx, context);			
		} catch (Exception e) {
			Debug.log(e.getMessage(), MODULE);
			result = ServiceUtil.returnError(e.getMessage());
		}
    	return result;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListPaymentsNew(DispatchContext dctx, Map<String,Object> context){
    	Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	EntityListIterator listIterator = null;
    	
    	String[] listParentAR = {"RECEIPT"};
    	String[] listParentAP = {"DISBURSEMENT","TAX_PAYMENT"};
    	List<String> PAYMENT_TYPE_AR = FastList.newInstance();
    	List<String> PAYMENT_TYPE_AP = FastList.newInstance();
    	
    	String paymentType = parameters.get("paymentType") != null ? parameters.get("paymentType")[0] : null;
    	if (UtilValidate.isNotEmpty(paymentType)) {
    		if (paymentType.equals("AR")) {
    			for (String parentType : listParentAR) {
    	    		UtilServices.getAllPaymentType(delegator, PAYMENT_TYPE_AR, parentType);
    	    	}
    	    	EntityCondition paymemtTypeCon = EntityCondition.makeCondition("paymentTypeId", EntityJoinOperator.IN, PAYMENT_TYPE_AR);
    	    	listAllConditions.add(paymemtTypeCon);
    	    	if (organizationPartyId!= null) {
    	    		EntityCondition organizationPartyCon = EntityCondition.makeCondition("partyIdTo", EntityJoinOperator.EQUALS, organizationPartyId);
    	    		listAllConditions.add(organizationPartyCon);
    	    	}  
    		} else if (paymentType.equals("AP")) {
    			for (String parentType : listParentAP) {
    	    		UtilServices.getAllPaymentType(delegator, PAYMENT_TYPE_AP, parentType);
    	    	}
    	    	EntityCondition paymemtTypeCon = EntityCondition.makeCondition("paymentTypeId", EntityJoinOperator.IN, PAYMENT_TYPE_AP);
    	    	listAllConditions.add(paymemtTypeCon);
    	    	
    	    	if (organizationPartyId!= null) {
    	    		EntityCondition organizationPartyCon = EntityCondition.makeCondition("partyIdFrom", EntityJoinOperator.EQUALS, organizationPartyId);
    	    		listAllConditions.add(organizationPartyCon);
    	    	}  
    		}
    		try {
    			listAllConditions.add(EntityCondition.makeCondition("paymentCode", EntityJoinOperator.NOT_EQUAL, null));
    			if(UtilValidate.isEmpty(listSortFields)){
        			listSortFields.add("-effectiveDate");
        		}
        		listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "PaymentPartyDetail", 
        				EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    		} catch (GenericEntityException e) {
    			e.printStackTrace();
    			return ServiceUtil.returnError(e.getLocalizedMessage());
    		}
    	}
    	
    	successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	 /*send notification order paid form acc to log admin*/
	public static Map<String,Object> sendPaidOrderNotiLog(DispatchContext dpct, Map<String,Object> context) throws GenericEntityException{
    	Delegator delegator = (Delegator) dpct.getDelegator();
    	String orderId = (String) context.get("orderId");
    	Locale locale = (Locale) context.get("locale");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	try {
			if(UtilValidate.isNotEmpty(orderId)){
				String roleLog =  EntityUtilProperties.getPropertyValue(RESOURCE_CORE_LOG, RSN_PRTROLE_CORE_LOG	, delegator);
				 String[] roleLogVal = new String[2];
				if(!roleLog.isEmpty()) roleLogVal = roleLog.split(";");
				List<String> listLogADM = FastList.newInstance();
				if(roleLogVal != null){
					listLogADM = SecurityUtil.getPartiesByRolesWithCurrentOrg(userLogin,roleLogVal[1], delegator);
				}
				if(listLogADM.size() > 0){
					for(String log : listLogADM){
						if(UtilValidate.isNotEmpty(log)){
							sendNotiLog(dpct,userLogin,log,orderId,locale);
						}
					}
				}
			}
		} catch (Exception e) {
			Debug.log("error when send notification for log admin cause : " + e.getMessage(), MODULE);
		}
    	/* logistics edit 
    	*/
    	String facilityId = null;
    	String action = "";
    	GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
    	String messages = "";
    	if ("SALES_ORDER".equals(orderHeader.getString("orderTypeId"))){
    		facilityId = orderHeader.getString("originFacilityId");
    		messages = "NeedsToPrepareProductToExport";
    		action = "viewOrder?orderId="+orderId;
    	} else if ("PURCHASE_ORDER".equals(orderHeader.getString("orderTypeId"))){
    		List<GenericValue> listOrderShipGroups = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);    	
        	if (!listOrderShipGroups.isEmpty()){
        		facilityId = listOrderShipGroups.get(0).getString("facilityId");
        	}
        	messages = "NeedsToPrepareWarehouseToReceive";
        	action = "viewDetailPO?orderId="+orderId;
    	}
    	GenericValue orderType = delegator.findOne("OrderType", false, UtilMisc.toMap("orderTypeId", orderHeader.getString("orderTypeId")));
    	if (UtilValidate.isNotEmpty(facilityId)){
    		List<GenericValue> listStorekeepers = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "roleTypeId", UtilProperties.getPropertyValue(RESOURCE_CORE_LOG, "role.storekeeper"))), null, null, null, false);
    		listStorekeepers = EntityUtil.filterByDate(listStorekeepers);
    		if (!listStorekeepers.isEmpty()){
    			LocalDispatcher dispatcher = dpct.getDispatcher();
    			for (GenericValue party : listStorekeepers) {
    				Map<String, Object> mapContext = new HashMap<String, Object>();
    				String header = UtilProperties.getMessage(logistics_resource, "Has", (Locale)context.get("locale"))+ " " + StringUtil.wrapString((String)orderType.get("description", (Locale)context.get("locale"))).toString().toLowerCase() + " " + StringUtil.wrapString(UtilProperties.getMessage(logistics_resource, messages, (Locale)context.get("locale"))).toString().toLowerCase() + ", "+ UtilProperties.getMessage(logistics_resource, "OrderId", (Locale)context.get("locale")) +": [" +orderId+"]";
    				String target = "";
            		mapContext.put("partyId", party.getString("partyId"));
            		mapContext.put("action", action);
            		mapContext.put("targetLink", target);
            		mapContext.put("header", header);
            		mapContext.put("ntfType", "ONE");
            		mapContext.put("userLogin", (GenericValue)context.get("userLogin"));
            		mapContext.put("openTime", UtilDateTime.nowTimestamp());
            		try {
            			dispatcher.runSync("createNotification", mapContext);
            		} catch (GenericServiceException e) {
            			e.printStackTrace();
            		}
				}
    		}
    	}
    	return  ServiceUtil.returnSuccess();
    }
    
    public static void sendNotiLog(DispatchContext dpct,GenericValue userLogin,String partyId, String orderId,Locale locale){
    	try {
    		GenericValue order = dpct.getDelegator().findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
    		if(order == null) return;
    		Map<String, Object> mapContext = new HashMap<String, Object>();
			String header = "";
			mapContext.put("partyId", partyId);
			mapContext.put("targetLink", "");
			mapContext.put("ntfType", "ONE");
			mapContext.put("userLogin", userLogin);
    		if(order.getString("orderTypeId").equals("SALES_ORDER")){
    			header = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCHaveNewSalesOrder", locale) + " [" + orderId +"]";
    			mapContext.put("action", "viewOrder");
    			mapContext.put("header", header);
    		}else{
    			header = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCpurchaseOrder", locale) + " [" + orderId +"]";
    			mapContext.put("action", "viewDetailPO?orderId=" + orderId);
    			mapContext.put("header", header);
    		}
			try {
				dpct.getDispatcher().runSync("createNotification", mapContext);
			} catch (GenericServiceException e) {
				ServiceUtil.returnError("createNotification error!");
			}
    		
		} catch (Exception e) {
			Debug.logError("error sendNotiLog cause " + e.getMessage(), MODULE);
		}
    	
    }
    
    public static Map<String,Object> changeOrderStatusByAccountant(DispatchContext dpct,Map<String,Object> context){
    	LocalDispatcher dispatcher = dpct.getDispatcher();
    	Delegator delegator = dpct.getDelegator();
    	String orderId = (String) context.get("orderId");
    	BigDecimal amount = (BigDecimal) context.get("amount");
    	String statusId  = (String) context.get("statusId");
   		String setItemStatus  = (String) context.get("setItemStatus");
   		Timestamp shipAfterDate  = (Timestamp) context.get("shipAfterDate");
   		Locale locale = (Locale) context.get("locale");
   		Map<String,Object> result = FastMap.newInstance();
    	try {
			Map<String,Object> status = dispatcher.runSync("changeOrderStatus", UtilMisc.toMap("userLogin", (GenericValue) context.get("userLogin"),
				"orderId",orderId,"statusId",statusId,"setItemStatus",setItemStatus,"shipAfterDate",(shipAfterDate != null ? shipAfterDate.getTime() : UtilDateTime.nowTimestamp().getTime())));
			if(ServiceUtil.isSuccess(status)){
				//if order is sales_order then create payment
				GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
				String orderTypeId = orderHeader.getString("orderTypeId");
				if(!"PURCHASE_ORDER".equals(orderTypeId)){
					status = dispatcher.runSync("createPaymentFromOrderEXT", UtilMisc.toMap("userLogin", (GenericValue) context.get("userLogin"),
							"orderId",orderId,"isLastPayment","TRUE","amount",amount,"comments",UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCommentsPaymentOff", locale)));
				}
    			if(ServiceUtil.isSuccess(status)){
    				sendPaidOrderNotiLog(dpct, context);
    			}
    		}else {
    			Debug.logError("Error when approved orders purchase cause", MODULE);
    			result.put(ModelService.ERROR_MESSAGE,(String) status.get("reponseMessage"));
    		}
    		
		} catch (Exception e) {
			Debug.logError("Problems when change orders status by accounant cause" + e.getMessage(), MODULE);
			result.put(ModelService.ERROR_MESSAGE,(String) e.getMessage());
		}
    	return result;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getListInvoiceOfPaymentJQ(DispatchContext dctx, Map<String,Object> context){
    	Delegator delegator = dctx.getDelegator();
    	Map<String, Object> result = FastMap.newInstance();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	if(UtilValidate.isEmpty(listSortFields)){
    		listSortFields.add("-invoiceDate");
    	}
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String paymentId = parameters.get("paymentId") != null? parameters.get("paymentId")[0] : null; 
    	int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		try {
			if(paymentId != null){
				listAllConditions.add(EntityCondition.makeCondition("paymentId", paymentId));
				List<GenericValue> listInvoiceOfPayment = delegator.findList("PaymentApplicationAndInvAndParty", EntityCondition.makeCondition(listAllConditions), null, listSortFields, null, false);
				totalRows = listInvoiceOfPayment.size();
				if(end > totalRows){
					end = totalRows;
				}
				listInvoiceOfPayment = listInvoiceOfPayment.subList(start, end);
				for(GenericValue invoiceOfPayment: listInvoiceOfPayment){
					Map<String, Object> tempMap = invoiceOfPayment.getAllFields();
					String invoiceId = invoiceOfPayment.getString("invoiceId");
					tempMap.put("totalAmount", InvoiceWorker.getInvoiceTotal(delegator, invoiceId));
					listIterator.add(tempMap);
				}
			}
			result.put("listIterator", listIterator);
			result.put("TotalRows", String.valueOf(totalRows));
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	return result;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getListInvoiceNotCompleteApplyJQ(DispatchContext dctx, Map<String,Object> context){
    	Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = FastMap.newInstance();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String businessType = parameters.get("businessType") != null? parameters.get("businessType")[0] : null;
    	String paymentId = parameters.get("paymentId") != null? parameters.get("paymentId")[0] : null;
    	if(UtilValidate.isEmpty(listSortFields)){
    		listSortFields.add("-invoiceDate");
    	}
    	try {
	    	GenericValue payment = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentId), false);
	    	if(payment == null){
	    		return ServiceUtil.returnError("cannot find payment to apply");
	    	}
	    	String paymentUomId = payment.getString("currencyUomId");
	    	String partyIdFrom = payment.getString("partyIdFrom");
	    	String partyIdTo = payment.getString("partyIdTo");
	    	if ("AP".equals(businessType)) {
	    		listAllConditions.add(EntityCondition.makeCondition("partyIdFrom", partyIdTo));
	    	} else {
	    		listAllConditions.add(EntityCondition.makeCondition("partyId", partyIdFrom));
	    	}
	    		
	    	/** only get invoice have currency uom equal equal payment uom currency**/
	    	listAllConditions.add(EntityCondition.makeCondition("currencyUomId", paymentUomId));
	    	String parentTypeId = "AP".equals(businessType) ? "PURCHASE_INVOICE" : "SALES_INVOICE";
	    	List<String> invoiceTypeList = FastList.newInstance();
	    	UtilServices.getAllInvoiceType(delegator, invoiceTypeList, parentTypeId);
	    	listAllConditions.add(EntityCondition.makeCondition("amountNotApply", EntityJoinOperator.GREATER_THAN, BigDecimal.ZERO));
	    	listAllConditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.NOT_IN, UtilMisc.toList("INVOICE_CANCELLED", "INVOICE_PAID", "INVOICE_WRITEOFF")));
	    	listAllConditions.add(EntityCondition.makeCondition("invoiceTypeId", EntityJoinOperator.IN, invoiceTypeList));
			listIterator = delegator.find("InvoiceAndPaymentApplDetail", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }

    @SuppressWarnings("unchecked")
	public static Map<String, Object> getAllListPaymentJQ(DispatchContext dctx, Map<String,Object> context){
    	Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	EntityListIterator listIterator = null;
    	try {
    		if(UtilValidate.isEmpty(listSortFields)){
    			listSortFields.add("-effectiveDate");
    		}
    		listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "PaymentPartyDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	successResult.put("listIterator", listIterator);
		return successResult;
    }
}
