/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ofbiz.order;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

/**
 * Order Manager Events
 */
public class OrderManagerEvents {

    public static final String module = OrderManagerEvents.class.getName();
    public static final String resource_error = "OrderErrorUiLabels";

    // FIXME: this event doesn't seem to be used; we may want to remove it
    public static String processOfflinePayments(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);

        if (session.getAttribute("OFFLINE_PAYMENTS") != null) {
            String orderId = (String) request.getAttribute("orderId");
            List<GenericValue> toBeStored = FastList.newInstance();
            List<GenericValue> paymentPrefs = null;
            GenericValue placingCustomer = null;
            try {
                paymentPrefs = delegator.findByAnd("OrderPaymentPreference", UtilMisc.toMap("orderId", orderId), null, false);
                List<GenericValue> pRoles = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "PLACING_CUSTOMER"), null, false);
                if (UtilValidate.isNotEmpty(pRoles))
                    placingCustomer = EntityUtil.getFirst(pRoles);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problems looking up order payment preferences", module);
                request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderErrorProcessingOfflinePayments", locale));
                return "error";
            }
            if (paymentPrefs != null) {
                for (GenericValue ppref : paymentPrefs) {
                    // update the preference to received
                    // TODO: updating payment preferences should be done as a service
                    ppref.set("statusId", "PAYMENT_RECEIVED");
                    ppref.set("authDate", UtilDateTime.nowTimestamp());
                    toBeStored.add(ppref);

                    // create a payment record
                    Map<String, Object> results = null;
                    try {
                        results = dispatcher.runSync("createPaymentFromPreference", UtilMisc.toMap("orderPaymentPreferenceId", ppref.get("orderPaymentPreferenceId"),
                                "paymentFromId", placingCustomer.getString("partyId"), "comments", "Payment received offline and manually entered."));
                    } catch (GenericServiceException e) {
                        Debug.logError(e, "Failed to execute service createPaymentFromPreference", module);
                        request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
                        return "error";
                    }

                    if ((results == null) || (results.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR))) {
                        Debug.logError((String) results.get(ModelService.ERROR_MESSAGE), module);
                        request.setAttribute("_ERROR_MESSAGE_", results.get(ModelService.ERROR_MESSAGE));
                        return "error";
                    }
                }

                // store the updated preferences
                try {
                    delegator.storeAll(toBeStored);
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Problems storing payment information", module);
                    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderProblemStoringReceivedPaymentInformation", locale));
                    return "error";
                }

                // set the status of the order to approved
                OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
            }
        }
        return "success";
    }

    public static String getRootPaymentTypeId(Delegator delegator, String paymentTypeId) throws GenericEntityException{
        String parentTypeId = paymentTypeId;
        GenericValue parentType = null;
        while(true){
            parentType = delegator.findOne("PaymentType", UtilMisc.toMap("paymentTypeId", parentTypeId), false);
            if(parentType == null || parentType.getString("parentTypeId") == null){
                break;
            }
            parentTypeId = parentType.getString("parentTypeId");
        }
        return parentTypeId;
    }

    public static String getPaymentCode(Delegator delegator, String paymentTypeId, String paymentMethodId,Timestamp effectiveDate) throws GenericEntityException{
        if(paymentMethodId == null || paymentTypeId == null){
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 1);
        if(effectiveDate != null){
            cal.setTime(effectiveDate);
        }
        int year = cal.get(Calendar.YEAR);
        Long nextSeqLong = 0l;
        List<GenericValue> paymentCodeSeqValueList = delegator.findByAnd("PaymentCodeSeqValue",
                UtilMisc.toMap("paymentTypeId", paymentTypeId, "paymentMethodId", paymentMethodId, "year", year), null, false);
        GenericValue paymentCodeSeqValue = EntityUtil.getFirst(paymentCodeSeqValueList);
        if(paymentCodeSeqValue == null){
            String sequenceId = delegator.getNextSeqId("PaymentCodeSeqValue");
            paymentCodeSeqValue = delegator.makeValue("PaymentCodeSeqValue");
            paymentCodeSeqValue.set("sequenceId", sequenceId);
            paymentCodeSeqValue.set("paymentTypeId", paymentTypeId);
            paymentCodeSeqValue.set("paymentMethodId", paymentMethodId);
            paymentCodeSeqValue.set("year", year);
            paymentCodeSeqValue.set("sequenceValue", 0l);
        }
        nextSeqLong = paymentCodeSeqValue.getLong("sequenceValue") + 1;
        String nextSeqId = String.valueOf(year) + UtilFormatOut.formatPaddedNumber(nextSeqLong, 6);
        paymentCodeSeqValue.set("sequenceValue", nextSeqLong);
        delegator.createOrStore(paymentCodeSeqValue);
        return nextSeqId;
    }

    public static String receiveOfflinePayment(HttpServletRequest request, HttpServletResponse response) throws GeneralException{
        HttpSession session = request.getSession();
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);
        String isAcc = request.getParameter("isAcc");
        String ntfId = request.getParameter("ntfId");
        
        String orderId = request.getParameter("orderId");
        if (UtilValidate.isEmpty(orderId)){
        	orderId = (String) request.getAttribute("orderId");
        }
        String partyId = request.getParameter("partyId");
        if (UtilValidate.isEmpty(partyId)){
        	partyId = (String) request.getAttribute("partyId");
        }

        String conversionFactorStr = request.getParameter("conversionFactor");

        // get the order header & payment preferences
        GenericValue orderHeader = null;
        List<GenericValue> orderRoles = null;
        try {
            orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
            orderRoles = delegator.findList("OrderRole", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems reading order header from datasource.", module);
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderProblemsReadingOrderHeaderInformation", locale));
            return "error";
        }

        BigDecimal grandTotal = BigDecimal.ZERO;
        if (orderHeader != null) {
            grandTotal = orderHeader.getBigDecimal("grandTotal");
        }

        // get the payment types to receive
        List<GenericValue> paymentMethodTypes = null;

        try {
            EntityExpr ee = EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.NOT_EQUAL, "EXT_OFFLINE");
            paymentMethodTypes = delegator.findList("PaymentMethodType", ee, null, null, null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems getting payment types", module);
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderProblemsWithPaymentTypeLookup", locale));
            return "error";
        }

        if (paymentMethodTypes == null) {
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderProblemsWithPaymentTypeLookup", locale));
            return "error";
        }

        // get the payment methods to receive
        List<GenericValue> paymentMethods = null;
        try {
            EntityExpr ee = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
            paymentMethods = delegator.findList("PaymentMethod", ee, null, null, null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems getting payment methods", module);
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderProblemsWithPaymentMethodLookup", locale));
            return "error";
        }

        GenericValue placingCustomer = null;
        try {
            List<GenericValue> pRoles = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "PLACING_CUSTOMER"), null, false);
            if (UtilValidate.isNotEmpty(pRoles))
                placingCustomer = EntityUtil.getFirst(pRoles);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems looking up order payment preferences", module);
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderErrorProcessingOfflinePayments", locale));
            return "error";
        }

        for (GenericValue paymentMethod : paymentMethods) {
            String paymentMethodId = paymentMethod.getString("paymentMethodId");
            String paymentMethodAmountStr = request.getParameter(paymentMethodId + "_amount");
            String paymentMethodReference = request.getParameter(paymentMethodId + "_reference");
            if (!UtilValidate.isEmpty(paymentMethodAmountStr)) {
            	String _amountTemp = (String) ObjectType.simpleTypeConvert(new BigDecimal(paymentMethodAmountStr), "String", null, locale);
     	    	BigDecimal _amount = (BigDecimal) ObjectType.simpleTypeConvert(_amountTemp, "BigDecimal", null, locale);
            	BigDecimal paymentMethodAmount = BigDecimal.ZERO;
                try {
                	paymentMethodAmount = (BigDecimal) ObjectType.simpleTypeConvert(paymentMethodAmountStr, "BigDecimal", null, locale);
                } catch (GeneralException e) {
                    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderProblemsPaymentParsingAmount", locale));
                    return "error";
                }
                
                if (paymentMethodAmount.compareTo(BigDecimal.ZERO) > 0) {
                    // create a payment, payment reference and payment appl record, when not exist yet.
                    Map<String, Object> results = null;
                    try {
                        String orderTypeId = orderHeader.getString("orderTypeId");
                        String paymentTypeId = "";
                        if("PURCHASE_ORDER".equals(orderTypeId))
                            paymentTypeId = "VENDOR_PAYMENT";
                        else if("SALES_ORDER".equals(orderTypeId))
                            paymentTypeId = "CUSTOMER_PAYMENT";
                        String rootPaymentTypeId = getRootPaymentTypeId(delegator, paymentTypeId);
                        Timestamp effectiveDate = UtilDateTime.nowTimestamp();
                        String paymentCode = getPaymentCode(delegator, rootPaymentTypeId, paymentMethodId, effectiveDate);
                        Map<String, Object> context = FastMap.newInstance();
                        context.put("orderId", orderId);
                        context.put("paymentCode", paymentCode);
                        context.put("paymentMethodId", paymentMethodId);
                        context.put("paymentRefNum", paymentMethodReference);
                        context.put("amount", _amount);
                        context.put("comments", UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCommentsPaymentOff",locale));
                        context.put("userLogin", userLogin);
                        context.put("conversionFactor", new BigDecimal(conversionFactorStr));
                        context.put("orderType", request.getParameter("orderType"));
                    	// Modify by VietTB
                        results = dispatcher.runSync("createPaymentFromOrderEXT", context);
                    } catch (GenericServiceException e) {
                        Debug.logError(e, "Failed to execute service createPaymentFromOrder", module);
                        request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
                        return "error";
                    }

                    if ((results == null) || (results.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR))) {
                        Debug.logError((String) results.get(ModelService.ERROR_MESSAGE), module);
                        request.setAttribute("_ERROR_MESSAGE_", results.get(ModelService.ERROR_MESSAGE));
                        return "error";
                    }
                    
                    if(results.containsKey("originalValue")){
                    	request.setAttribute("originalValue", (BigDecimal) results.get("originalValue"));
                    }
                	
                }
                // Modify by VietTB
//                OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
//                return "success";
            }
        }
        
        
        List<GenericValue> toBeStored = FastList.newInstance();
        for (GenericValue paymentMethodType : paymentMethodTypes) {
            String paymentMethodTypeId = paymentMethodType.getString("paymentMethodTypeId");
            String amountStr = request.getParameter(paymentMethodTypeId + "_amount");
            String paymentReference = request.getParameter(paymentMethodTypeId + "_reference");
            if (!UtilValidate.isEmpty(amountStr)) {
                BigDecimal paymentTypeAmount = BigDecimal.ZERO;
                try {
                    paymentTypeAmount = (BigDecimal) ObjectType.simpleTypeConvert(amountStr, "BigDecimal", null, locale);
                } catch (GeneralException e) {
                    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderProblemsPaymentParsingAmount", locale));
                    return "error";
                }
                if (paymentTypeAmount.compareTo(BigDecimal.ZERO) > 0) {
                    // create the OrderPaymentPreference
                    // TODO: this should be done with a service
                    Map<String, String> prefFields = UtilMisc.<String, String>toMap("orderPaymentPreferenceId", delegator.getNextSeqId("OrderPaymentPreference"));
                    GenericValue paymentPreference = delegator.makeValue("OrderPaymentPreference", prefFields);
                    paymentPreference.set("paymentMethodTypeId", paymentMethodType.getString("paymentMethodTypeId"));
                    paymentPreference.set("maxAmount", paymentTypeAmount);
                    paymentPreference.set("statusId", "PAYMENT_RECEIVED");
                    paymentPreference.set("orderId", orderId);
                    paymentPreference.set("createdDate", UtilDateTime.nowTimestamp());
                    if (userLogin != null) {
                        paymentPreference.set("createdByUserLogin", userLogin.getString("userLoginId"));
                    }

                    try {
                        delegator.create(paymentPreference);
                    } catch (GenericEntityException ex) {
                        Debug.logError(ex, "Cannot create a new OrderPaymentPreference", module);
                        request.setAttribute("_ERROR_MESSAGE_", ex.getMessage());
                        return "error";
                    }

                    // create a payment record
                    Map<String, Object> results = null;
                    try {
                        results = dispatcher.runSync("createPaymentFromPreference", UtilMisc.toMap("userLogin", userLogin,
                                "orderPaymentPreferenceId", paymentPreference.get("orderPaymentPreferenceId"), "paymentRefNum", paymentReference,
                                "paymentFromId", placingCustomer.getString("partyId"), "comments", "Payment received offline and manually entered."));
                    } catch (GenericServiceException e) {
                        Debug.logError(e, "Failed to execute service createPaymentFromPreference", module);
                        request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
                        return "error";
                    }

                    if ((results == null) || (results.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR))) {
                        Debug.logError((String) results.get(ModelService.ERROR_MESSAGE), module);
                        request.setAttribute("_ERROR_MESSAGE_", results.get(ModelService.ERROR_MESSAGE));
                        return "error";
                    }
                }
            }
        }

        // get the current payment prefs
        GenericValue offlineValue = null;
        List<GenericValue> currentPrefs = null;
        BigDecimal paymentTally = BigDecimal.ZERO;
        boolean isCOD = false;
        try {
            EntityConditionList<EntityExpr> ecl = EntityCondition.makeCondition(UtilMisc.toList(
                    EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId),
                    EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_CANCELLED")),
                    EntityOperator.AND);
            currentPrefs = delegator.findList("OrderPaymentPreference", ecl, null, null, null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, "ERROR: Unable to get existing payment preferences from order", module);
        }
        if (UtilValidate.isNotEmpty(currentPrefs)) {
            for (GenericValue cp : currentPrefs) {
                String paymentMethodType = cp.getString("paymentMethodTypeId");
                if("EXT_COD".equals(paymentMethodType)){
                	isCOD = true;
                }
                if ("EXT_OFFLINE".equals(paymentMethodType)) {
                    offlineValue = cp;
                } else {
                    BigDecimal cpAmt = cp.getBigDecimal("maxAmount");
                    if (cpAmt != null) {
                        paymentTally = paymentTally.add(cpAmt);
                    }
                }
            }
        }

        // now finish up
        boolean okayToApprove = false;
        if (paymentTally.compareTo(grandTotal) >= 0) {
            // cancel the offline preference
            okayToApprove = true;
            if (offlineValue != null) {
                offlineValue.set("statusId", "PAYMENT_CANCELLED");
                toBeStored.add(offlineValue);
            }
        }

        // store the status changes and the newly created payment preferences and payments
        // TODO: updating order payment preference should be done with a service
        try {
            delegator.storeAll(toBeStored);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems storing payment information", module);
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderProblemStoringReceivedPaymentInformation", locale));
            return "error";
        }
        // update the status of the order and items
        if (okayToApprove) {
        	if(request.getParameter("orderType") != null && request.getParameter("orderType").equals("Purchase")){
        		try {
        			Map<String,Object> changeResults = dispatcher.runSync("changeOrderStatus", UtilMisc.toMap("userLogin", userLogin,"orderId",orderId,"statusId","ORDER_APPROVED","setItemStatus","Y"));	
        			if(ServiceUtil.isError(changeResults)){
        				  Debug.logError("Problems change order status to approved", module);
        				  request.setAttribute("_ERROR_MESSAGE_", "Problems change order status to approved");
        				return "error";
        			}
        		} catch (Exception e) {
	        	       Debug.logError(e, "Problems change order status to approved", module);
	                   request.setAttribute("_ERROR_MESSAGE_", "Problems change order status to approved");
	                   return "error";
				}
            
        	}else{
        		 OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
        	}
        }
        
        try {
        	List<EntityCondition> listConds = FastList.newInstance();
        	listConds.add(EntityCondition.makeCondition("orderId",orderId));
        	listConds.add(EntityCondition.makeCondition("statusId","ORDER_APPROVED"));
        	List<GenericValue> orders = delegator.findList("OrderStatus", EntityCondition.makeCondition(listConds,EntityJoinOperator.AND), null, null, null, false);
        	if(UtilValidate.isNotEmpty(orders) && orders!= null && orders.size() > 0 && !isCOD){
        		if((isAcc != null && UtilValidate.isNotEmpty(isAcc)) || (request.getParameter("orderType") != null && request.getParameter("orderType").equals("Purchase"))){
    				Map<String,Object> mapTx = FastMap.newInstance();
    				mapTx.put("userLogin", request.getSession().getAttribute("userLogin"));
    				mapTx.put("orderId",orderId);
    				mapTx.put("locale",locale);
    				try {
    					Map<String,Object> resultSend = dispatcher.runSync("sendPaidOrderNotiLog",mapTx);
					} catch (Exception e) {
						Debug.logError("error when call services : sendPaidOrderNotiLog cause: "  +e.getMessage(), module);
					}
    				
    				/*update Notification when payment recevei full*/
    				try {
    					mapTx.remove("orderId");
    					if (ntfId != null)
    					{
    						mapTx.put("ntfId", ntfId);
    						dispatcher.runSync("updateNotification", mapTx);
    					}
					} catch (Exception e) {
						Debug.logError("error when call services : updateNotification cause : "  +e.getMessage(), module);
					}
    			}
        	}
		} catch (Exception e) {
			Debug.logError("Error when send notification cause " + e.getMessage(), module);
		}

        return "success";
    }

}
