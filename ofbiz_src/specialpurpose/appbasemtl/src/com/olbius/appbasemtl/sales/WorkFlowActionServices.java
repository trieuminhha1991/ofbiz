package com.olbius.appbasemtl.sales;

import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.util.SalesUtil;

public class WorkFlowActionServices {
	public static final String module = WorkFlowActionServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
    
	public static Map<String, Object> sendNotiChangeOrderStatus(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	
    	String orderId = (String) context.get("orderId");
    	try {
    		GenericValue order = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
    		if (order == null) return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSNotFoundOrderHasOrderIdIs", UtilMisc.toList(orderId), locale));
    		
    		String organizationId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    		GenericValue orderOwnerRole = delegator.findOne("OrderRole", UtilMisc.<String, Object>toMap("orderId", orderId, "partyId", organizationId, "roleTypeId", "BILL_FROM_VENDOR"), false);
    		if (orderOwnerRole != null) {
    			// TODOCHANGE send notify to partyIds
    			NotificationWorker.sendNotiChangeOrderStatus(delegator, dispatcher, locale, orderId, userLogin);
    		} else {
    			// send notify to owner of this order
    			com.olbius.basesales.util.NotificationWorker.sendNotifyWhenCreateOrderToOwner(delegator, dispatcher, locale, orderId, userLogin);
    		}
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling sendNotifyWhenCreateOrder service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	
    	return successResult;
    }
}
