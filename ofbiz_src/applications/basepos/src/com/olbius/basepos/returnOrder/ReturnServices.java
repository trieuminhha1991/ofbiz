package com.olbius.basepos.returnOrder;



import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.order.OrderReturnServices;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ReturnServices {
	public static String module = ReturnServices.class.getName();
	public static String resource_error = "OrderErrorUiLabels";
//  set some BigDecimal properties
    private static BigDecimal ZERO = BigDecimal.ZERO;
    private static int decimals = -1;
    private static int rounding = -1;
    static {
        decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
        rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");

        // set zero to the proper scale
        if (decimals != -1) ZERO = ZERO.setScale(decimals);
    }
	public static Map<String, Object> processRefundReturn(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String returnId = (String) context.get("returnId");
        String returnTypeId = (String) context.get("returnTypeId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        GenericValue returnHeader = null;
        List<GenericValue> returnItems = null;
        try {
            returnHeader = delegator.findOne("ReturnHeader", UtilMisc.toMap("returnId", returnId), false);
            if (returnHeader != null) {
                returnItems = returnHeader.getRelated("ReturnItem", UtilMisc.toMap("returnTypeId", returnTypeId), null, false);
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems looking up return information", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorGettingReturnHeaderItemInformation", locale));
        }
        
        BigDecimal adjustments = OrderReturnServices.getReturnAdjustmentTotal(delegator, UtilMisc.toMap("returnId", returnId, "returnTypeId", returnTypeId));

        if (returnHeader != null && (UtilValidate.isNotEmpty(returnItems) || adjustments.compareTo(ZERO) > 0)) {
            // make sure total refunds on a return don't exceed amount of returned orders
            Map<String, Object> serviceResult = null;
            BigDecimal returnAmount = BigDecimal.ZERO;
            if(UtilValidate.isNotEmpty(returnItems)){
            	for (GenericValue returnItem : returnItems) {
					BigDecimal returnQuantity = returnItem.getBigDecimal("returnQuantity");
					BigDecimal returnPrice = returnItem.getBigDecimal("returnPrice");
					BigDecimal itemAmount = returnQuantity.multiply(returnPrice);
					returnAmount = returnAmount.add(itemAmount);
				}
            }
            
            returnAmount = returnAmount.add(adjustments);
            
            // handle manual refunds
            String paymentId = null;
            try {
            	Map<String, Object> input = UtilMisc.<String, Object>toMap("userLogin", userLogin, "amount", returnAmount, "statusId", "PMNT_SENT");
                input.put("partyIdTo", returnHeader.get("fromPartyId"));
                input.put("partyIdFrom", returnHeader.get("toPartyId"));
                input.put("paymentTypeId", "CUSTOMER_REFUND");
                input.put("paymentMethodTypeId", "CASH");
                
                serviceResult = dispatcher.runSync("createPayment", input);

                if (ServiceUtil.isError(serviceResult) || ServiceUtil.isFailure(serviceResult)) {
                    Debug.logError("Error in refund payment: " + ServiceUtil.getErrorMessage(serviceResult), module);
                    return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceResult));
                }
                paymentId = (String) serviceResult.get("paymentId");
            } catch (GenericServiceException e) {
                return ServiceUtil.returnError(e.getMessage());
            }
            Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
            // Fill out the data for the new ReturnItemResponse
            Map<String, Object> response = FastMap.newInstance();
         
            response.put("responseAmount", returnAmount.setScale(decimals, rounding));
            response.put("responseDate", nowTimestamp);
            response.put("userLogin", userLogin);
            response.put("paymentId", paymentId);
          
            Map<String, Object> serviceResults = null;
            try {
                serviceResults = dispatcher.runSync("createReturnItemResponse", response);
                if (ServiceUtil.isError(serviceResults)) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                            "OrderProblemsCreatingReturnItemResponseEntity", locale), null, null, serviceResults);
                }
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problems creating new ReturnItemResponse entity", module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                        "OrderProblemsCreatingReturnItemResponseEntity", locale));
            }
            String responseId = (String) serviceResults.get("returnItemResponseId");

            // Set the response on each item
            String returnItemStatusId = "RETURN_MAN_REFUND";
            for (GenericValue item : returnItems) {
                Map<String, Object> returnItemMap = UtilMisc.<String, Object>toMap("returnItemResponseId", responseId, "returnId", item.get("returnId"), "returnItemSeqId", item.get("returnItemSeqId"), "statusId", returnItemStatusId, "userLogin", userLogin);
                //Debug.logInfo("Updating item status", module);
                try {
                    serviceResults = dispatcher.runSync("updateReturnItem", returnItemMap);
                    if (ServiceUtil.isError(serviceResults)) {
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                                "OrderProblemUpdatingReturnItemReturnItemResponseId", locale), null, null, serviceResults);
                    }
                } catch (GenericServiceException e) {
                    Debug.logError("Problem updating the ReturnItem entity", module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                            "OrderProblemUpdatingReturnItemReturnItemResponseId", locale));
                }

                //Debug.logInfo("Item status and return status history created", module);
            }

            // Create the payment applications for the return invoice
            try {
                serviceResults = dispatcher.runSync("createPaymentApplicationsFromReturnItemResponse",
                        UtilMisc.<String, Object>toMap("returnItemResponseId", responseId, "userLogin", userLogin));
                if (ServiceUtil.isError(serviceResults)) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                            "OrderProblemUpdatingReturnItemReturnItemResponseId", locale), null, null, serviceResults);
                }
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problem creating PaymentApplication records for return invoice", module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                        "OrderProblemUpdatingReturnItemReturnItemResponseId", locale));
            }

        }

        return ServiceUtil.returnSuccess();
    }
}
