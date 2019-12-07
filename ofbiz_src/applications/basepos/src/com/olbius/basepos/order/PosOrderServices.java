package com.olbius.basepos.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.DataModelConstants;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class PosOrderServices{
	private static String module = PosOrderServices.class.getName();
	private static String resource_pos = "BasePosErrorUiLabels";
	private static String resource_order = "OrderErrorUiLabels";
	public static Map<String, String> salesAttributeRoleMap = FastMap.newInstance();
    public static Map<String, String> purchaseAttributeRoleMap = FastMap.newInstance();
    static {
        salesAttributeRoleMap.put("placingCustomerPartyId", "PLACING_CUSTOMER");
        salesAttributeRoleMap.put("billToCustomerPartyId", "BILL_TO_CUSTOMER");
        salesAttributeRoleMap.put("billFromVendorPartyId", "BILL_FROM_VENDOR");
        salesAttributeRoleMap.put("shipToCustomerPartyId", "SHIP_TO_CUSTOMER");
        salesAttributeRoleMap.put("endUserCustomerPartyId", "END_USER_CUSTOMER");

        purchaseAttributeRoleMap.put("billToCustomerPartyId", "BILL_TO_CUSTOMER");
        purchaseAttributeRoleMap.put("billFromVendorPartyId", "BILL_FROM_VENDOR");
        purchaseAttributeRoleMap.put("shipFromVendorPartyId", "SHIP_FROM_VENDOR");
        purchaseAttributeRoleMap.put("supplierAgentPartyId", "SUPPLIER_AGENT");
    }
    public static final int taxDecimals = UtilNumber.getBigDecimalScale("salestax.calc.decimals");
    public static final int taxRounding = UtilNumber.getBigDecimalRoundingMode("salestax.rounding");
    public static final int orderDecimals = UtilNumber.getBigDecimalScale("order.decimals");
    public static final int orderRounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
    public static final BigDecimal ZERO = BigDecimal.ZERO.setScale(taxDecimals, taxRounding);

	 /** Service for creating a new order */
    public static Map<String, Object> createOrder(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        List<GenericValue> toBeStored = new LinkedList<GenericValue>();
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        // get the order type
        String orderTypeId = (String) context.get("orderTypeId");
        String billFromVendorPartyId = (String) context.get("billFromVendorPartyId");
        // get the product store for the order, but it is required only for sales orders
        String productStoreId = (String) context.get("productStoreId");
        GenericValue productStore = null;
        if ((orderTypeId.equals("SALES_ORDER")) && (UtilValidate.isNotEmpty(productStoreId))) {
            try {
                productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), true);
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_order,
                        "OrderErrorCouldNotFindProductStoreWithID",UtilMisc.toMap("productStoreId",productStoreId),locale)  + e.toString());
            }
        }

        successResult.put("orderTypeId", orderTypeId);

        // lookup the order type entity
        GenericValue orderType = null;
        try {
            orderType = delegator.findOne("OrderType", UtilMisc.toMap("orderTypeId", orderTypeId), true);
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_order,
                    "OrderErrorOrderTypeLookupFailed",locale) + e.toString());
        }

        // make sure we have a valid order type
        if (orderType == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_order,
                    "OrderErrorInvalidOrderTypeWithID", UtilMisc.toMap("orderTypeId",orderTypeId), locale));
        }

        // check to make sure we have something to order
        List<GenericValue> orderItems = UtilGenerics.checkList(context.get("orderItems"));
        if (orderItems.size() < 1) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_order, "items.none", locale));
        }

        List<GenericValue> orderAdjustments = UtilGenerics.checkList(context.get("orderAdjustments"));
        List<GenericValue> orderItemShipGroupInfo = UtilGenerics.checkList(context.get("orderItemShipGroupInfo"));
        List<GenericValue> orderItemPriceInfo = UtilGenerics.checkList(context.get("orderItemPriceInfos"));

        // check inventory and other things for each item
        List<String> errorMessages = FastList.newInstance();
        Map<String, BigDecimal> normalizedItemQuantities = FastMap.newInstance();
        Map<String, String> normalizedItemNames = FastMap.newInstance();
        Map<String, GenericValue> itemValuesBySeqId = FastMap.newInstance();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        
        // determine the sales channel
        String salesChannelEnumId = "POS_SALES_CHANNEL";
        // need to run through the items combining any cases where multiple lines refer to the
        // same product so the inventory check will work correctly
        // also count quantities ordered while going through the loop
        for (GenericValue orderItem : orderItems) {
            // start by putting it in the itemValuesById Map
            itemValuesBySeqId.put(orderItem.getString("orderItemSeqId"), orderItem);
            String currentProductId = orderItem.getString("productId");
            if (currentProductId != null) {
                // only normalize items with a product associated (ignore non-product items)
                if (normalizedItemQuantities.get(currentProductId) == null) {
                    normalizedItemQuantities.put(currentProductId, orderItem.getBigDecimal("quantity"));
                    normalizedItemNames.put(currentProductId, orderItem.getString("itemDescription"));
                } else {
                    BigDecimal currentQuantity = normalizedItemQuantities.get(currentProductId);
                    normalizedItemQuantities.put(currentProductId, currentQuantity.add(orderItem.getBigDecimal("quantity")));
                }
            }
        }

        if (!"PURCHASE_ORDER".equals(orderTypeId) && productStoreId == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_order,
                    "OrderErrorTheProductStoreIdCanOnlyBeNullForPurchaseOrders",locale));
        }

        Timestamp orderDate = (Timestamp) context.get("orderDate");

        for (String currentProductId : normalizedItemQuantities.keySet()) {
            // lookup the product entity for each normalized item; error on products not found
            GenericValue product = null;
            try {
                product = delegator.findOne("Product", UtilMisc.toMap("productId", currentProductId), true);
            } catch (GenericEntityException e) {
                String errMsg = UtilProperties.getMessage(resource_order, "product.not_found", new Object[] { currentProductId }, locale);
                Debug.logError(e, errMsg, module);
                errorMessages.add(errMsg);
                continue;
            }

            if (product == null) {
                String errMsg = UtilProperties.getMessage(resource_order, "product.not_found", new Object[] { currentProductId }, locale);
                Debug.logError(errMsg, module);
                errorMessages.add(errMsg);
                continue;
            }
        }

        if (errorMessages.size() > 0) {
            return ServiceUtil.returnError(errorMessages);
        }

        // the inital status for ALL order types
        String initialStatus = "ORDER_CREATED";
        successResult.put("statusId", initialStatus);

        // create the order object
        String orderId = (String) context.get("orderId");
        String orgPartyId = null;
        if (productStore != null) {
            orgPartyId = productStore.getString("payToPartyId");
        } else if (billFromVendorPartyId != null) {
            orgPartyId = billFromVendorPartyId;
        }
        if(UtilValidate.isEmpty(orderId)){
        	Map<String, Object> getNextOrderIdContext = FastMap.newInstance();
            getNextOrderIdContext.putAll(context);
            getNextOrderIdContext.put("partyId", orgPartyId);
            getNextOrderIdContext.put("userLogin", userLogin);

            if ((orderTypeId.equals("SALES_ORDER")) || (productStoreId != null)) {
                getNextOrderIdContext.put("productStoreId", productStoreId);
            }
            if (UtilValidate.isEmpty(orderId)) {
                try {
                    getNextOrderIdContext = ctx.makeValidContext("getNextOrderId", "IN", getNextOrderIdContext);
                    Map<String, Object> getNextOrderIdResult = dispatcher.runSync("getNextOrderId", getNextOrderIdContext);
                    if (ServiceUtil.isError(getNextOrderIdResult)) {
                        String errMsg = UtilProperties.getMessage(resource_order, 
                                "OrderErrorGettingNextOrderIdWhileCreatingOrder", locale);
                        return ServiceUtil.returnError(errMsg, null, null, getNextOrderIdResult);
                    }
                    orderId = (String) getNextOrderIdResult.get("orderId");
                } catch (GenericServiceException e) {
                    String errMsg = UtilProperties.getMessage(resource_order, 
                            "OrderCaughtGenericServiceExceptionWhileGettingOrderId", locale);
                    Debug.logError(e, errMsg, module);
                    return ServiceUtil.returnError(errMsg);
                }
            }
        }
        

        String billingAccountId = (String) context.get("billingAccountId");
        if (orderDate == null) {
            orderDate = nowTimestamp;
        }

        Map<String, Object> orderHeaderMap = UtilMisc.<String, Object>toMap("orderId", orderId, "orderTypeId", orderTypeId,
                "orderDate", orderDate, "entryDate", nowTimestamp,
                "statusId", initialStatus, "billingAccountId", billingAccountId);
        orderHeaderMap.put("orderName", context.get("orderName"));
        orderHeaderMap.put("needsInventoryIssuance", "Y");
        
        //TODOCHANGE add new attribute
        String salesMethodChannelEnumId = (String) context.get("salesMethodChannelEnumId");
        if (UtilValidate.isNotEmpty(salesMethodChannelEnumId)) {
        	orderHeaderMap.put("salesMethodChannelEnumId", salesMethodChannelEnumId);
        }
        String agreementId = (String) context.get("agreementId");
        if (UtilValidate.isNotEmpty(agreementId)) {
        	orderHeaderMap.put("agreementId", agreementId);
        }
        String isFavorDelivery = (String) context.get("isFavorDelivery");
        if (UtilValidate.isNotEmpty(isFavorDelivery)) {
        	orderHeaderMap.put("isFavorDelivery", isFavorDelivery);
        }
        
        GenericValue orderHeader = delegator.makeValue("OrderHeader", orderHeaderMap);

        orderHeader.set("salesChannelEnumId", salesChannelEnumId);

        if (context.get("currencyUom") != null) {
            orderHeader.set("currencyUom", context.get("currencyUom"));
        }
        
        if (context.get("grandTotal") != null) {
            orderHeader.set("grandTotal", context.get("grandTotal"));
        }

        if (UtilValidate.isNotEmpty(context.get("visitId"))) {
            orderHeader.set("visitId", context.get("visitId"));
        }

        if (UtilValidate.isNotEmpty(context.get("originFacilityId"))) {
            orderHeader.set("originFacilityId", context.get("originFacilityId"));
        }

        if (UtilValidate.isNotEmpty(context.get("productStoreId"))) {
            orderHeader.set("productStoreId", context.get("productStoreId"));
        }

        if (UtilValidate.isNotEmpty(context.get("transactionId"))) {
            orderHeader.set("transactionId", context.get("transactionId"));
        }

        if (UtilValidate.isNotEmpty(context.get("terminalId"))) {
            orderHeader.set("terminalId", context.get("terminalId"));
        }

        if (userLogin != null && userLogin.get("userLoginId") != null) {
            orderHeader.set("createdBy", userLogin.getString("userLoginId"));
        }

        String invoicePerShipment = UtilProperties.getPropertyValue("AccountingConfig","create.invoice.per.shipment");
        if (UtilValidate.isNotEmpty(invoicePerShipment)) {
            orderHeader.set("invoicePerShipment", invoicePerShipment);
        }

        // first try to create the OrderHeader; if this does not fail, continue.
        try {
            delegator.create(orderHeader);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot create OrderHeader entity; problems with insert", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_order,
                    "OrderOrderCreationFailedPleaseNotifyCustomerService",locale));
        }

        // create the order status record
        String orderStatusSeqId = delegator.getNextSeqId("OrderStatus");
        GenericValue orderStatus = delegator.makeValue("OrderStatus", UtilMisc.toMap("orderStatusId", orderStatusSeqId));
        orderStatus.set("orderId", orderId);
        orderStatus.set("statusId", orderHeader.getString("statusId"));
        orderStatus.set("statusDatetime", nowTimestamp);
        orderStatus.set("statusUserLogin", userLogin.getString("userLoginId"));
        toBeStored.add(orderStatus);
        for (GenericValue orderItem : orderItems) {
            orderItem.set("orderId", orderId);
            toBeStored.add(orderItem);

            // create the item status record
            String itemStatusId = delegator.getNextSeqId("OrderStatus");
            GenericValue itemStatus = delegator.makeValue("OrderStatus", UtilMisc.toMap("orderStatusId", itemStatusId));
            itemStatus.put("statusId", orderItem.get("statusId"));
            itemStatus.put("orderId", orderId);
            itemStatus.put("orderItemSeqId", orderItem.get("orderItemSeqId"));
            itemStatus.put("statusDatetime", nowTimestamp);
            itemStatus.set("statusUserLogin", userLogin.getString("userLoginId"));
            toBeStored.add(itemStatus);
        }
        
        // set the order item attributes
        List<GenericValue> orderItemAttributes = UtilGenerics.checkList(context.get("orderItemAttributes"));
        if (UtilValidate.isNotEmpty(orderItemAttributes)) {
            for (GenericValue oiatt : orderItemAttributes) {
                oiatt.set("orderId", orderId);
                toBeStored.add(oiatt);
            }
        }

        if (errorMessages.size() > 0) {
            return ServiceUtil.returnError(errorMessages);
        }

        // set the orderId on all adjustments; this list will include order and
        // item adjustments...
        if (UtilValidate.isNotEmpty(orderAdjustments)) {
            for (GenericValue orderAdjustment : orderAdjustments) {
                try {
                    orderAdjustment.set("orderAdjustmentId", delegator.getNextSeqId("OrderAdjustment"));
                } catch (IllegalArgumentException e) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_order,
                            "OrderErrorCouldNotGetNextSequenceIdForOrderAdjustmentCannotCreateOrder",locale));
                }

                orderAdjustment.set("orderId", orderId);
                orderAdjustment.set("createdDate", UtilDateTime.nowTimestamp());
                orderAdjustment.set("createdByUserLogin", userLogin.getString("userLoginId"));

                if (UtilValidate.isEmpty(orderAdjustment.get("orderItemSeqId"))) {
                    orderAdjustment.set("orderItemSeqId", DataModelConstants.SEQ_ID_NA);
                }
                if (UtilValidate.isEmpty(orderAdjustment.get("shipGroupSeqId"))) {
                    orderAdjustment.set("shipGroupSeqId", DataModelConstants.SEQ_ID_NA);
                }
                toBeStored.add(orderAdjustment);
            }
        }
     // set the order contact mechs
        List<GenericValue> orderContactMechs = UtilGenerics.checkList(context.get("orderContactMechs"));
        if (UtilValidate.isNotEmpty(orderContactMechs)) {
            for (GenericValue ocm : orderContactMechs) {
                ocm.set("orderId", orderId);
                toBeStored.add(ocm);
            }
        }

        // set the order item contact mechs
        List<GenericValue> orderItemContactMechs = UtilGenerics.checkList(context.get("orderItemContactMechs"));
        if (UtilValidate.isNotEmpty(orderItemContactMechs)) {
            for (GenericValue oicm : orderItemContactMechs) {
                oicm.set("orderId", orderId);
                toBeStored.add(oicm);
            }
        }

        // set the order item ship groups
        List<String> dropShipGroupIds = FastList.newInstance(); // this list will contain the ids of all the ship groups for drop shipments (no reservations)
        if (UtilValidate.isNotEmpty(orderItemShipGroupInfo)) {
            for (GenericValue valueObj : orderItemShipGroupInfo) {
                valueObj.set("orderId", orderId);
                if ("OrderItemShipGroup".equals(valueObj.getEntityName())) {
                    // ship group
                    if (valueObj.get("carrierRoleTypeId") == null) {
                        valueObj.set("carrierRoleTypeId", "CARRIER");
                    }
                    if (!UtilValidate.isEmpty(valueObj.getString("supplierPartyId"))) {
                        dropShipGroupIds.add(valueObj.getString("shipGroupSeqId"));
                    }
                } else if ("OrderAdjustment".equals(valueObj.getEntityName())) {
                    // shipping / tax adjustment(s)
                    if (UtilValidate.isEmpty(valueObj.get("orderItemSeqId"))) {
                        valueObj.set("orderItemSeqId", DataModelConstants.SEQ_ID_NA);
                    }
                    valueObj.set("orderAdjustmentId", delegator.getNextSeqId("OrderAdjustment"));
                    valueObj.set("createdDate", UtilDateTime.nowTimestamp());
                    valueObj.set("createdByUserLogin", userLogin.getString("userLoginId"));
                }
                toBeStored.add(valueObj);
            }
        }
        // set the item price info; NOTE: this must be after the orderItems are stored for referential integrity
        if (UtilValidate.isNotEmpty(orderItemPriceInfo)) {
            for (GenericValue oipi : orderItemPriceInfo) {
                try {
                    oipi.set("orderItemPriceInfoId", delegator.getNextSeqId("OrderItemPriceInfo"));
                } catch (IllegalArgumentException e) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_order,
                            "OrderErrorCouldNotGetNextSequenceIdForOrderItemPriceInfoCannotCreateOrder",locale));
                }

                oipi.set("orderId", orderId);
                toBeStored.add(oipi);
            }
        }

        // set the item associations
        List<GenericValue> orderItemAssociations = UtilGenerics.checkList(context.get("orderItemAssociations"));
        if (UtilValidate.isNotEmpty(orderItemAssociations)) {
            for (GenericValue orderItemAssociation : orderItemAssociations) {
                if (orderItemAssociation.get("toOrderId") == null) {
                    orderItemAssociation.set("toOrderId", orderId);
                } else if (orderItemAssociation.get("orderId") == null) {
                    orderItemAssociation.set("orderId", orderId);
                }
                toBeStored.add(orderItemAssociation);
            }
        }

        // set the additional party roles
        Map<String, List<String>> additionalPartyRole = UtilGenerics.checkMap(context.get("orderAdditionalPartyRoleMap"));
        if (additionalPartyRole != null) {
            for (Map.Entry<String, List<String>> entry : additionalPartyRole.entrySet()) {
                String additionalRoleTypeId = entry.getKey();
                List<String> parties = entry.getValue();
                if (parties != null) {
                    for (String additionalPartyId : parties) {
                        toBeStored.add(delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", additionalPartyId, "roleTypeId", additionalRoleTypeId)));
                        toBeStored.add(delegator.makeValue("OrderRole", UtilMisc.toMap("orderId", orderId, "partyId", additionalPartyId, "roleTypeId", additionalRoleTypeId)));
                    }
                }
            }
        }
       
        // store the orderProductPromoUseInfos
        List<GenericValue> orderProductPromoUses = UtilGenerics.checkList(context.get("orderProductPromoUses"));
        if (UtilValidate.isNotEmpty(orderProductPromoUses)) {
            for (GenericValue productPromoUse  : orderProductPromoUses) {
                productPromoUse.set("orderId", orderId);
                toBeStored.add(productPromoUse);
            }
        }

        // store the orderProductPromoCodes
        Set<String> orderProductPromoCodes = UtilGenerics.checkSet(context.get("orderProductPromoCodes"));
        if (UtilValidate.isNotEmpty(orderProductPromoCodes)) {
            for (String productPromoCodeId : orderProductPromoCodes) {
                GenericValue orderProductPromoCode = delegator.makeValue("OrderProductPromoCode");
                orderProductPromoCode.set("orderId", orderId);
                orderProductPromoCode.set("productPromoCodeId", productPromoCodeId);
                toBeStored.add(orderProductPromoCode);
            }
        }

        // see the attributeRoleMap definition near the top of this file for attribute-role mappings
        Map<String, String> attributeRoleMap = salesAttributeRoleMap;
        if ("PURCHASE_ORDER".equals(orderTypeId)) {
            attributeRoleMap = purchaseAttributeRoleMap;
        }
        for (Map.Entry<String, String> attributeRoleEntry : attributeRoleMap.entrySet()) {
            if (UtilValidate.isNotEmpty(context.get(attributeRoleEntry.getKey()))) {
                // make sure the party is in the role before adding
                toBeStored.add(delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", context.get(attributeRoleEntry.getKey()), "roleTypeId", attributeRoleEntry.getValue())));
                toBeStored.add(delegator.makeValue("OrderRole", UtilMisc.toMap("orderId", orderId, "partyId", context.get(attributeRoleEntry.getKey()), "roleTypeId", attributeRoleEntry.getValue())));
            }
        }
        
        // TODOCHANGE: add new role "CALLCENTER_EMPL", "SALES_EXECUTIVE"
        String salesExecutiveId = (String) context.get("salesExecutiveId");
        if (UtilValidate.isNotEmpty(salesExecutiveId)) {
        	toBeStored.add(delegator.makeValue("OrderRole", UtilMisc.toMap("orderId", orderId, "partyId", salesExecutiveId, "roleTypeId", "SALES_EXECUTIVE")));
        }
        
        // set the order payment info
        List<GenericValue> orderPaymentInfos = UtilGenerics.checkList(context.get("orderPaymentInfo"));
        if (UtilValidate.isNotEmpty(orderPaymentInfos)) {
            for (GenericValue valueObj : orderPaymentInfos) {
                valueObj.set("orderId", orderId);
                if ("OrderPaymentPreference".equals(valueObj.getEntityName())) {
                    if (valueObj.get("orderPaymentPreferenceId") == null) {
                        valueObj.set("orderPaymentPreferenceId", delegator.getNextSeqId("OrderPaymentPreference"));
                        valueObj.set("createdDate", UtilDateTime.nowTimestamp());
                        valueObj.set("createdByUserLogin", userLogin.getString("userLoginId"));
                    }
                    if (valueObj.get("statusId") == null) {
                        valueObj.set("statusId", "PAYMENT_NOT_RECEIVED");
                    }
                }
                toBeStored.add(valueObj);
            }
        }

        try {
            // store line items, etc so that they will be there for the foreign key checks
            delegator.storeAll(toBeStored);
            successResult.put("orderId", orderId);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem with order storage or reservations", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_order,
                    "OrderErrorCouldNotCreateOrderWriteError",locale) + e.getMessage() + ").");
        }

        return successResult;
    }
    
    /**
     * Service to create a payment using an order payment preference.
     * @return Map
     */
    public static Map<String, Object> createPaymentFromPreference(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderPaymentPreferenceId = (String) context.get("orderPaymentPreferenceId");
        String paymentRefNum = (String) context.get("paymentRefNum");
        String paymentFromId = (String) context.get("paymentFromId");
        String comments = (String) context.get("comments");
        Timestamp eventDate = (Timestamp) context.get("eventDate");
        Locale locale = (Locale) context.get("locale");
        if (UtilValidate.isEmpty(eventDate)) {
            eventDate = UtilDateTime.nowTimestamp();
        }
        Map<String, Object> createPaymentFromPreference = ServiceUtil.returnSuccess();
        try {
            // get the order payment preference
            GenericValue orderPaymentPreference = delegator.findOne("OrderPaymentPreference", UtilMisc.toMap("orderPaymentPreferenceId", orderPaymentPreferenceId), false);
            if (orderPaymentPreference == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_order,
                        "OrderOrderPaymentCannotBeCreated",
                        UtilMisc.toMap("orderPaymentPreferenceId", "orderPaymentPreferenceId"), locale));
            }

            // get the order header
            GenericValue orderHeader = orderPaymentPreference.getRelatedOne("OrderHeader", false);
            if (orderHeader == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_order,
                        "OrderOrderPaymentCannotBeCreatedWithRelatedOrderHeader", locale));
            }

            // get the store for the order.  It will be used to set the currency
            GenericValue productStore = orderHeader.getRelatedOne("ProductStore", false);
            if (productStore == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_order,
                        "OrderOrderPaymentCannotBeCreatedWithRelatedProductStore", locale));
            }
            
            // get the partyId billed to
            if (paymentFromId == null) {
                OrderReadHelper orh = new OrderReadHelper(orderHeader);
                GenericValue billToParty = orh.getBillToParty();
                if (billToParty != null) {
                    paymentFromId = billToParty.getString("partyId");
                } else {
                    paymentFromId = "_NA_";
                }
            }

            // set the payToPartyId
            String payToPartyId = productStore.getString("payToPartyId");
            if (payToPartyId == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_order,
                        "OrderOrderPaymentCannotBeCreatedPayToPartyIdNotSet", locale));
            }

            // create the payment
            Map<String, Object> paymentParams = new HashMap<String, Object>();
            BigDecimal maxAmount = orderPaymentPreference.getBigDecimal("maxAmount");
        	if(orderHeader.getString("orderTypeId").equalsIgnoreCase("PURCHASE_ORDER")){
        		paymentParams.put("paymentTypeId", "VENDOR_PAYMENT");
        		paymentParams.put("partyIdTo", paymentFromId);
        		paymentParams.put("partyIdFrom", payToPartyId);
        		paymentParams.put("statusId", "PMNT_SENT");
        	}else{
        		paymentParams.put("paymentTypeId", "CUSTOMER_PAYMENT");
        		paymentParams.put("partyIdTo", payToPartyId);
        		paymentParams.put("partyIdFrom", paymentFromId);
        		paymentParams.put("statusId", "PMNT_RECEIVED");
        	}
            paymentParams.put("paymentMethodTypeId", orderPaymentPreference.getString("paymentMethodTypeId"));
            paymentParams.put("paymentPreferenceId", orderPaymentPreference.getString("orderPaymentPreferenceId"));
            paymentParams.put("amount", maxAmount);
            
            paymentParams.put("effectiveDate", eventDate);
            
            paymentParams.put("currencyUomId", productStore.getString("defaultCurrencyUomId"));
            if (paymentRefNum != null) {
                paymentParams.put("paymentRefNum", paymentRefNum);
            }
            if (comments != null) {
                paymentParams.put("comments", comments);
            }
            paymentParams.put("userLogin", userLogin);

            Map<String, Object> createPayment =  dispatcher.runSync("createPaymentPos", paymentParams);
            if(ServiceUtil.isSuccess(createPayment)){
            	String paymentId = (String)createPayment.get("paymentId");
            	createPaymentFromPreference.put("paymentId", paymentId);
            }
        } catch (GenericEntityException ex) {
            Debug.logError(ex, "Unable to create payment using payment preference.", module);
            return(ServiceUtil.returnError(ex.getMessage()));
        } catch (GenericServiceException ex) {
            Debug.logError(ex, "Unable to create payment using payment preference.", module);
            return(ServiceUtil.returnError(ex.getMessage()));
        }
        return createPaymentFromPreference;
    }
    
    /** Service for changing the status on an order header */
    public static Map<String, Object> setOrderStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        String statusId = (String) context.get("statusId");
        String changeReason = (String) context.get("changeReason");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");
        try {
            GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
            if (orderHeader == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_order,
                        "OrderErrorCouldNotChangeOrderStatusOrderCannotBeFound", locale));
            }
            // first save off the old status
            successResult.put("oldStatusId", orderHeader.get("statusId"));
            successResult.put("orderTypeId", orderHeader.get("orderTypeId"));

            if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setOrderStatus] : From Status : " + orderHeader.getString("statusId"), module);
            if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setOrderStatus] : To Status : " + statusId, module);

            if (orderHeader.getString("statusId").equals(statusId)) {
                Debug.logWarning(UtilProperties.getMessage(resource_order,
                        "OrderTriedToSetOrderStatusWithTheSameStatusIdforOrderWithId", UtilMisc.toMap("statusId",statusId,"orderId",orderId),locale),module);
                return successResult;
            }
            try {
                Map<String, String> statusFields = UtilMisc.<String, String>toMap("statusId", orderHeader.getString("statusId"), "statusIdTo", statusId);
                GenericValue statusChange = delegator.findOne("StatusValidChange", statusFields, true);
                if (statusChange == null) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_order, 
                            "OrderErrorCouldNotChangeOrderStatusStatusIsNotAValidChange", locale) + ": [" + statusFields.get("statusId") + "] -> [" + statusFields.get("statusIdTo") + "]");
                }
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_order,
                        "OrderErrorCouldNotChangeOrderStatus",locale) + e.getMessage() + ").");
            }

            // update the current status
            orderHeader.set("statusId", statusId);

            // now create a status change
            GenericValue orderStatus = delegator.makeValue("OrderStatus");
            orderStatus.put("orderStatusId", delegator.getNextSeqId("OrderStatus"));
            orderStatus.put("statusId", statusId);
            orderStatus.put("orderId", orderId);
            orderStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
            orderStatus.put("statusUserLogin", userLogin.getString("userLoginId"));
            orderStatus.put("changeReason", changeReason);
            orderHeader.store();
            orderStatus.create();
            successResult.put("needsInventoryIssuance", orderHeader.get("needsInventoryIssuance"));
            successResult.put("grandTotal", orderHeader.get("grandTotal"));
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_order,
                    "OrderErrorCouldNotChangeOrderStatus",locale) + e.getMessage() + ").");
        }

        // release the inital hold if we are cancelled or approved
        if ("ORDER_CANCELLED".equals(statusId) || "ORDER_APPROVED".equals(statusId)) {
            // cancel any order processing if we are cancelled
            if ("ORDER_CANCELLED".equals(statusId)) {
                OrderChangeHelper.abortOrderProcessing(ctx.getDispatcher(), orderId);
            }
        }


        successResult.put("orderStatusId", statusId);
        return successResult;
    }
    
    /** Service for changing the status on order item(s) */
    public static Map<String, Object> setItemStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        String orderItemSeqId = (String) context.get("orderItemSeqId");
        String fromStatusId = (String) context.get("fromStatusId");
        String statusId = (String) context.get("statusId");
        Timestamp statusDateTime = (Timestamp) context.get("statusDateTime");
        Locale locale = (Locale) context.get("locale");

        Map<String, String> fields = UtilMisc.<String, String>toMap("orderId", orderId);
        if (orderItemSeqId != null)
            fields.put("orderItemSeqId", orderItemSeqId);
        if (fromStatusId != null)
            fields.put("statusId", fromStatusId);

        List<GenericValue> orderItems = null;
        try {
            orderItems = delegator.findByAnd("OrderItem", fields, null, false);
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_order,
                    "OrderErrorCannotGetOrderItemEntity",locale) + e.getMessage());
        }

        if (UtilValidate.isNotEmpty(orderItems)) {
            List<GenericValue> toBeStored = new ArrayList<GenericValue>();
            for (GenericValue orderItem : orderItems) {
                if (orderItem == null) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_order,
                            "OrderErrorCannotChangeItemStatusItemNotFound", locale));
                }
                if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setItemStatus] : Status Change: [" + orderId + "] (" + orderItem.getString("orderItemSeqId"), module);
                if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setItemStatus] : From Status : " + orderItem.getString("statusId"), module);
                if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setOrderStatus] : To Status : " + statusId, module);

                if (orderItem.getString("statusId").equals(statusId)) {
                    continue;
                }

                try {
                    Map<String, String> statusFields = UtilMisc.<String, String>toMap("statusId", orderItem.getString("statusId"), "statusIdTo", statusId);
                    GenericValue statusChange = delegator.findOne("StatusValidChange", statusFields, true);

                    if (statusChange == null) {
                        Debug.logWarning(UtilProperties.getMessage(resource_order,
                                "OrderItemStatusNotChangedIsNotAValidChange", UtilMisc.toMap("orderStatusId",orderItem.getString("statusId"),"statusId",statusId), locale), module);
                        continue;
                    }
                } catch (GenericEntityException e) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_order,
                            "OrderErrorCouldNotChangeItemStatus",locale) + e.getMessage());
                }

                orderItem.set("statusId", statusId);
                if (UtilValidate.isEmpty(orderItemSeqId) && "ITEM_CANCELLED".equals(statusId) ) {
                    //Complete order is cancelled via changeOrderStatus : update cancel order item quantity
                    BigDecimal itemQuantity = orderItem.getBigDecimal("quantity");
                    if (itemQuantity == null) itemQuantity = BigDecimal.ZERO;
                    orderItem.set("cancelQuantity", itemQuantity);
                }
                toBeStored.add(orderItem);
                if (statusDateTime == null) {
                    statusDateTime = UtilDateTime.nowTimestamp();
                }
                // now create a status change
                Map<String, Object> changeFields = new HashMap<String, Object>();
                changeFields.put("orderStatusId", delegator.getNextSeqId("OrderStatus"));
                changeFields.put("statusId", statusId);
                changeFields.put("orderId", orderId);
                changeFields.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
                changeFields.put("statusDatetime", statusDateTime);
                changeFields.put("statusUserLogin", userLogin.getString("userLoginId"));
                GenericValue orderStatus = delegator.makeValue("OrderStatus", changeFields);
                toBeStored.add(orderStatus);
            }

            // store the changes
            if (toBeStored.size() > 0) {
                try {
                    delegator.storeAll(toBeStored);
                } catch (GenericEntityException e) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_order,
                            "OrderErrorCannotStoreStatusChanges", locale) + e.getMessage());
                }
            }
        }

        return ServiceUtil.returnSuccess();
    }
    public static Map<String, Object> updateProductFacilitySaleOrderFromOrder(DispatchContext dctx, Map<String, ? extends Object> context){
		Map<String, Object> returnSuccess = ServiceUtil.returnSuccess();
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String orderId =  (String) context.get("orderId");
		int periodTime = UtilProperties.getPropertyAsInteger("general", "periodTime", 30);
		//convert orderDate into date to create product_facility_saleOrder entity
		BigDecimal quantityDay = BigDecimal.ONE;
		GenericValue orderHeader = null;
		if(UtilValidate.isNotEmpty(orderId)){
			try {
				orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			} catch (GenericEntityException e) {
				String errorMessage = UtilProperties.getMessage(resource_pos, "BPOSOrderDidNotFind", UtilMisc.toMap("orderId", orderId), locale);
				return ServiceUtil.returnError(errorMessage);
			}
			List<GenericValue> orderItems = FastList.newInstance();
			EntityCondition orderItemCond = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
			try {
				orderItems = delegator.findList("OrderItem", orderItemCond,  null, null, null, false);
			} catch (GenericEntityException e) {
				String errorMessage = UtilProperties.getMessage(resource_pos, "BPOSCanNotGetInformationOfOrder",UtilMisc.toMap("orderId", orderId), locale);
				return ServiceUtil.returnError(errorMessage);
			}
			String facilityId = orderHeader.getString("originFacilityId");
			String orderType = orderHeader.getString("orderTypeId");
			Timestamp orderDateOrigin = orderHeader.getTimestamp("orderDate");
			//convert orderDate into date to create product_facility_saleOrder entity
			Date orderDate = new Date(orderDateOrigin.getTime());
			//check create_product_facility_saleOrder
			if(orderType.equalsIgnoreCase("SALES_ORDER")){
				if(UtilValidate.isNotEmpty(orderItems) && UtilValidate.isNotEmpty(facilityId)){
					for (GenericValue orderItem : orderItems) {
						String productId = orderItem.getString("productId");
						BigDecimal quantityOrdered = orderItem.getBigDecimal("quantity");
						Map<String, Object> productFacilityConds = FastMap.newInstance();
						productFacilityConds.put("productId", productId);
						productFacilityConds.put("facilityId", facilityId);
						productFacilityConds.put("orderDate", orderDate);
						GenericValue productFacilitySaleOrder = null;
						try {
							productFacilitySaleOrder = delegator.findOne("ProductFacilitySaleHistory", productFacilityConds, false);
						} catch (GenericEntityException e) {
							String errorMessage = UtilProperties.getMessage(resource_pos, "BPOSCanNotGetInformationOfProductFacilitySaleOrder",  UtilMisc.toMap("productId",productId,"facilityId", facilityId), locale);
							return ServiceUtil.returnError(errorMessage);
						}
						if(UtilValidate.isNotEmpty(productFacilitySaleOrder)){
							BigDecimal quantity = productFacilitySaleOrder.getBigDecimal("quantityOrdered");
							quantity = quantity.add(quantityOrdered);
							productFacilitySaleOrder.put("quantityOrdered", quantity);
							try {
								productFacilitySaleOrder.store();
							} catch (GenericEntityException e) {
								String errorMessage = UtilProperties.getMessage(resource_pos, "BPOSCanNotUpdateInformationOfProductFacilitySaleOrder",UtilMisc.toMap("productId",productId,"facilityId", facilityId), locale);
								return ServiceUtil.returnError(errorMessage);
							}
						}else{
							//create new entity
							productFacilitySaleOrder = delegator.makeValue("ProductFacilitySaleHistory");
							productFacilitySaleOrder.put("productId",productId);
							productFacilitySaleOrder.put("facilityId",facilityId);
							productFacilitySaleOrder.put("orderDate",orderDate);
							productFacilitySaleOrder.put("quantityOrdered",quantityOrdered);
							try {
								productFacilitySaleOrder.create();
							} catch (GenericEntityException e) {
								String errorMessage = UtilProperties.getMessage(resource_pos, "BPOSCanNotCreateInformationOfProductFacilitySaleOrder",UtilMisc.toMap("productId",productId,"facilityId", facilityId), locale);
								return ServiceUtil.returnError(errorMessage);
							}
						}
						//get total quantityOrdered while 30 last day
	 					Timestamp startTimestamp = UtilDateTime.getDayStart(orderDateOrigin, -periodTime);
	 					Date startTime = new Date(startTimestamp.getTime());
	 					List<EntityCondition> productFacilitySaleHistoryConditions = FastList.newInstance();
	 					productFacilitySaleHistoryConditions.add(EntityCondition.makeCondition("orderDate",EntityOperator.GREATER_THAN_EQUAL_TO , startTime));
	 					productFacilitySaleHistoryConditions.add(EntityCondition.makeCondition("orderDate",EntityOperator.LESS_THAN_EQUAL_TO , orderDate));
	 					productFacilitySaleHistoryConditions.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS , productId));
	 					productFacilitySaleHistoryConditions.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS , facilityId));
	 					EntityCondition productFacilitySaleHistoryCondition = EntityCondition.makeCondition(productFacilityConds, EntityOperator.AND);
	 					List<GenericValue> productFacilitySaleHistorys = FastList.newInstance();
	 					try {
							productFacilitySaleHistorys = delegator.findList("ProductFacilitySaleHistory", productFacilitySaleHistoryCondition, null, null, null, false);
						} catch (GenericEntityException e1) {
							String errorMessage = UtilProperties.getMessage(resource_pos, "BPOSCanNotGetInformationOfProductFacilitySaleOrder",  UtilMisc.toMap("productId",productId,"facilityId", facilityId), locale);
							return ServiceUtil.returnError(errorMessage);
						}
	 					int countDay = productFacilitySaleHistorys.size();
	 					if(countDay !=0){
	 						if(countDay > periodTime){
	 							quantityDay = new BigDecimal(periodTime);
		 					}else{
		 						quantityDay = new BigDecimal(countDay);
		 					}
	 					}
	 					BigDecimal totalQuantityOrdered =  BigDecimal.ZERO;
	 					for (GenericValue productFacilitySaleHistory : productFacilitySaleHistorys) {
							BigDecimal quantityOrderedTmp = productFacilitySaleHistory.getBigDecimal("quantityOrdered");
							if(UtilValidate.isNotEmpty(quantityOrderedTmp)){
								totalQuantityOrdered = totalQuantityOrdered.add(quantityOrderedTmp);
							}
						}
	 					BigDecimal quantityPerDay = totalQuantityOrdered.divide(quantityDay, 2, RoundingMode.HALF_UP);
						//update product facility
						Map<String, String> productFacilityCond = FastMap.newInstance();
						productFacilityCond.put("productId", productId);
						productFacilityCond.put("facilityId", facilityId);
						GenericValue productFacility = null;
						try {
							productFacility = delegator.findOne("ProductFacility", productFacilityCond, false);
						} catch (GenericEntityException e) {
							String errorMessage = UtilProperties.getMessage(resource_pos, "BPOSCanNotGetInformationOfProductFacilitySaleOrder",  UtilMisc.toMap("productId",productId,"facilityId", facilityId), locale);
							return ServiceUtil.returnError(errorMessage);
						}
						if(UtilValidate.isNotEmpty(productFacility)){
							//update product facility
							productFacility.put("lastSold", orderDateOrigin);
							productFacility.put("qpd", quantityPerDay);
							try {
								productFacility.store();
							} catch (GenericEntityException e) {
								String errorMessage = UtilProperties.getMessage(resource_pos, "BPOSCanNotUpdateProductFacility",  UtilMisc.toMap("productId",productId,"facilityId", facilityId), locale);
								return ServiceUtil.returnError(errorMessage);
							}
						}else{
							//create product facility
							productFacility = delegator.makeValue("ProductFacility");
							productFacility.put("productId", productId);
							productFacility.put("facilityId", facilityId);
							productFacility.put("lastSold", orderDateOrigin);
							productFacility.put("qpd", quantityPerDay);
							try {
								productFacility.create();
							} catch (GenericEntityException e) {
								String errorMessage = UtilProperties.getMessage(resource_pos, "BPOSCanNotCreateProductFacility",  UtilMisc.toMap("productId",productId,"facilityId", facilityId), locale);
								return ServiceUtil.returnError(errorMessage);
							}
						}
						
					}
				}
			}else{
				if(UtilValidate.isNotEmpty(orderItems) && UtilValidate.isNotEmpty(facilityId)){
					for (GenericValue orderItem : orderItems) {
						String productId = orderItem.getString("productId");
						BigDecimal quantityOrdered = orderItem.getBigDecimal("quantity");
						//update product facility
						Map<String, String> productFacilityCond = FastMap.newInstance();
						productFacilityCond.put("productId", productId);
						productFacilityCond.put("facilityId", facilityId);
						GenericValue productFacility = null;
						try {
							productFacility = delegator.findOne("ProductFacility", productFacilityCond, false);
						} catch (GenericEntityException e) {
							String errorMessage = UtilProperties.getMessage(resource_pos, "BPOSCanNotGetInformationOfProductFacilitySaleOrder",  UtilMisc.toMap("productId",productId,"facilityId", facilityId), locale);
							return ServiceUtil.returnError(errorMessage);
						}
						if(UtilValidate.isNotEmpty(productFacility)){
							//update product facility
							BigDecimal onOrder = productFacility.getBigDecimal("qoo");
							if(UtilValidate.isNotEmpty(onOrder)){
								onOrder = onOrder.add(quantityOrdered);
							}else{
								onOrder = BigDecimal.ZERO;
							}
							productFacility.put("qoo", onOrder);
							try {
								productFacility.store();
							} catch (GenericEntityException e) {
								String errorMessage = UtilProperties.getMessage(resource_pos, "BPOSCanNotUpdateProductFacility",  UtilMisc.toMap("productId",productId,"facilityId", facilityId), locale);
								return ServiceUtil.returnError(errorMessage);
							}
						}else{
							//create product facility
							productFacility = delegator.makeValue("ProductFacility");
							productFacility.put("productId", productId);
							productFacility.put("facilityId", facilityId);
							productFacility.put("qoo", quantityOrdered);
							try {
								productFacility.create();
							} catch (GenericEntityException e) {
								String errorMessage = UtilProperties.getMessage(resource_pos, "BPOSCanNotCreateProductFacility",  UtilMisc.toMap("productId",productId,"facilityId", facilityId), locale);
								return ServiceUtil.returnError(errorMessage);
							}
						}
					}
				}
			}
		}
		return returnSuccess;
	}
}
