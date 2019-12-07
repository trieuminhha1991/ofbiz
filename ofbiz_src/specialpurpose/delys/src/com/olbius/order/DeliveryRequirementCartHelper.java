package com.olbius.order;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class DeliveryRequirementCartHelper {
	public static final String resource = "OrderUiLabels";
    public static String module = ShoppingCartHelper.class.getName();
    public static final String resource_error = "OrderErrorUiLabels";

    // The shopping cart to manipulate
    private DeliveryRequirementCart cart = null;

    // The entity engine delegator
    private Delegator delegator = null;

    // The service invoker
    private LocalDispatcher dispatcher = null;

    /**
     * Changes will be made to the cart directly, as opposed
     * to a copy of the cart provided.
     *
     * @param cart The cart to manipulate
     */
    public DeliveryRequirementCartHelper(Delegator delegator, LocalDispatcher dispatcher, DeliveryRequirementCart cart) {
        this.dispatcher = dispatcher;
        this.delegator = delegator;
        this.cart = cart;

        if (delegator == null) {
            this.delegator = dispatcher.getDelegator();
        }
        if (dispatcher == null) {
            throw new IllegalArgumentException("Dispatcher argument is null");
        }
        if (cart == null) {
            throw new IllegalArgumentException("Delivery requirement argument is null");
        }
    }
    
 // Create order event - uses createOrder service for processing
    public Map<String, Object> createOrder(GenericValue userLogin, String visitId) {
        if (this.cart == null) {
            return null;
        }
        String deliveryReqId = this.cart.getDeliveryReqId();
//        String deliveryReqName = this.cart.getDeliveryReqName();
//        String deliveryReqStatus  = this.cart.getDeliveryReqStatus();
//        String deliveryReqDescription = this.cart.getDeliveryReqDescription();
        
        // store the order - build the context
        Map<String, Object> context = this.cart.makeCartMap(this.dispatcher);

        context.put("requirementId", deliveryReqId);
        context.put("userLogin", userLogin);
//        context.put("visitId", visitId);
        
        if (UtilValidate.isEmpty(deliveryReqId)) {
            // for purchase orders or when other orderId generation fails, a product store id should not be required to make an order
            deliveryReqId = delegator.getNextSeqId("Requirement");
            this.cart.setDeliveryReqId(deliveryReqId);
        }
        
     // store the order - invoke the service
        Map<String, Object> storeResult = null;
        
        try {
			storeResult = dispatcher.runSync("storeDeliveryRequirement", context);
			deliveryReqId = (String) storeResult.get("requirementId");
			if (UtilValidate.isNotEmpty(deliveryReqId)) {
				this.cart.setDeliveryReqId(deliveryReqId);
			}
		} catch (GenericServiceException e) {
			String service = e.getMessage();
			Map<String, Object> messageMap = UtilMisc.<String, Object>toMap("service", service);
			String errMsg = UtilProperties.getMessage(resource_error, "DACouldNotCreateDeliveryRequirementInvokingService", messageMap, (cart != null ? cart.getLocale() : Locale.getDefault()));
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
        
        // check for error message(s)
        if (ServiceUtil.isError(storeResult)) {
            String errMsg = UtilProperties.getMessage(resource_error, "checkhelper.did_not_complete_order_following_occurred", (cart != null ? cart.getLocale() : Locale.getDefault()));
            List<String> resErrorMessages = new LinkedList<String>();
            resErrorMessages.add(errMsg);
            resErrorMessages.add(ServiceUtil.getErrorMessage(storeResult));
            return ServiceUtil.returnError(resErrorMessages);
        }

        // set the orderId for use by chained events
        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("requirementId", deliveryReqId);
        
        return result;
    }
}
