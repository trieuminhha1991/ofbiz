package com.olbius.basesales.shoppingcart;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ShippingWorker {
	public static final String module = ShippingWorker.class.getName();
	public static final String resource = "OrderUiLabels";
    public static final String resource_error = "OrderErrorUiLabels";
    
	public static Map<String, Object> getShipEstimate(Delegator delegator, LocalDispatcher dispatcher, ShoppingCart cart, boolean runPromo) {
        int shipGroups = cart.getShipGroupSize();
        for (int i = 0; i < shipGroups; i++) {
            String shipmentMethodTypeId = cart.getShipmentMethodTypeId(i);
            if (UtilValidate.isEmpty(shipmentMethodTypeId)) {
                continue;
            }
            Map<String, Object> result = getShipGroupEstimate(dispatcher, delegator, cart, i);
            if (ServiceUtil.isError(result)) {
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
            }

            BigDecimal shippingTotal = (BigDecimal) result.get("shippingTotal");
            if (shippingTotal == null) {
                shippingTotal = BigDecimal.ZERO;
            }
            cart.setItemShipGroupEstimate(shippingTotal, i);
        }

        if (runPromo) ProductPromoWorker.doPromotions(cart, dispatcher);
        
        // all done
        return ServiceUtil.returnSuccess();
    }
	
	public static Map<String, Object> getShipGroupEstimate(LocalDispatcher dispatcher, Delegator delegator, ShoppingCart cart, int groupNo) {
        // check for shippable items
        if (!cart.shippingApplies()) {
            Map<String, Object> responseResult = ServiceUtil.returnSuccess();
            responseResult.put("shippingTotal", BigDecimal.ZERO);
            return responseResult;
        }

        String shipmentMethodTypeId = cart.getShipmentMethodTypeId(groupNo);
        String carrierPartyId = cart.getCarrierPartyId(groupNo);
        String productStoreShipMethId = cart.getProductStoreShipMethId(groupNo);

        return getShipGroupEstimate(dispatcher, delegator, cart.getOrderType(), shipmentMethodTypeId, carrierPartyId, null,
                cart.getShippingContactMechId(groupNo), cart.getProductStoreId(), cart.getSupplierPartyId(groupNo), cart.getShippableItemInfo(groupNo),
                cart.getShippableWeight(groupNo), cart.getShippableQuantity(groupNo), cart.getShippableTotal(groupNo), cart.getPartyId(), productStoreShipMethId);
    }
	
	public static Map<String, Object> getShipGroupEstimate(LocalDispatcher dispatcher, Delegator delegator, String orderTypeId,
            String shipmentMethodTypeId, String carrierPartyId, String carrierRoleTypeId, String shippingContactMechId,
            String productStoreId, String supplierPartyId, List<Map<String, Object>> itemInfo, BigDecimal shippableWeight, BigDecimal shippableQuantity,
            BigDecimal shippableTotal, String partyId, String productStoreShipMethId) {
        String standardMessage = "A problem occurred calculating shipping. Fees will be calculated offline.";
        List<String> errorMessageList = FastList.newInstance();

        if ("NO_SHIPPING".equals(shipmentMethodTypeId)) {
            return ServiceUtil.returnSuccess();
        }

        if (shipmentMethodTypeId == null || carrierPartyId == null) {
            if ("SALES_ORDER".equals(orderTypeId)) {
                errorMessageList.add("Please Select Your Shipping Method.");
                return ServiceUtil.returnError(errorMessageList);
            } else {
                return ServiceUtil.returnSuccess();
            }
        }

        if (carrierRoleTypeId == null) {
            carrierRoleTypeId = "CARRIER";
        }

		//  ShipmentCostEstimate entity allows null value for geoIdTo field. So if geoIdTo is null we should be using orderFlatPrice for shipping cost.
		//  So now calcShipmentCostEstimate service requires shippingContactMechId only if geoIdTo field has not null value.
		//        if (shippingContactMechId == null) {
		//            errorMessageList.add("Please Select Your Shipping Address.");
		//            return ServiceUtil.returnError(errorMessageList);
		//        }

        // if as supplier is associated, then we have a drop shipment and should use the origin shipment address of it
        String shippingOriginContactMechId = null;
        if (supplierPartyId != null) {
            try {
                GenericValue originAddress = getShippingOriginContactMech(delegator, supplierPartyId);
                if (originAddress == null) {
                    return ServiceUtil.returnError("Cannot find the origin shipping address (SHIP_ORIG_LOCATION) for the supplier with ID ["+supplierPartyId+"].  Will not be able to calculate drop shipment estimate.");
                }
                shippingOriginContactMechId = originAddress.getString("contactMechId");
            } catch (GeneralException e) {
                return ServiceUtil.returnError(standardMessage);
            }
        }

        // no shippable items; we won't change any shipping at all
        if (shippableQuantity.compareTo(BigDecimal.ZERO) == 0) {
            Map<String, Object> result = ServiceUtil.returnSuccess();
            result.put("shippingTotal", BigDecimal.ZERO);
            return result;
        }

        // check for an external service call
        GenericValue storeShipMethod = ProductStoreWorker.getProductStoreShipmentMethod(delegator, productStoreId,
                shipmentMethodTypeId, carrierPartyId, carrierRoleTypeId);

        if (storeShipMethod == null) {
            errorMessageList.add("No applicable shipment method found.");
            return ServiceUtil.returnError(errorMessageList);
        }

        // the initial amount before manual estimates
        BigDecimal shippingTotal = BigDecimal.ZERO;

        // prepare the service invocation fields
        Map<String, Object> serviceFields = FastMap.newInstance();
        serviceFields.put("initialEstimateAmt", shippingTotal);
        serviceFields.put("shippableTotal", shippableTotal);
        serviceFields.put("shippableQuantity", shippableQuantity);
        serviceFields.put("shippableWeight", shippableWeight);
        serviceFields.put("shippableItemInfo", itemInfo);
        serviceFields.put("productStoreId", productStoreId);
        serviceFields.put("carrierRoleTypeId", "CARRIER");
        serviceFields.put("carrierPartyId", carrierPartyId);
        serviceFields.put("shipmentMethodTypeId", shipmentMethodTypeId);
        serviceFields.put("shippingContactMechId", shippingContactMechId);
        serviceFields.put("shippingOriginContactMechId", shippingOriginContactMechId);
        serviceFields.put("partyId", partyId);
        serviceFields.put("productStoreShipMethId", productStoreShipMethId);

        // call the external shipping service
        try {
            BigDecimal externalAmt = null;
            if (UtilValidate.isNotEmpty(shippingContactMechId)) {
                externalAmt = getExternalShipEstimate(dispatcher, storeShipMethod, serviceFields);
            }
            if (externalAmt != null) {
                shippingTotal = shippingTotal.add(externalAmt);
            }
        } catch (GeneralException e) {
            return ServiceUtil.returnError(standardMessage);
        }

        // update the initial amount
        serviceFields.put("initialEstimateAmt", shippingTotal);

        // call the generic estimate service
        try {
            BigDecimal genericAmt = getGenericShipEstimate(dispatcher, storeShipMethod, serviceFields);
            if (genericAmt != null) {
                shippingTotal = shippingTotal.add(genericAmt);
            }
        } catch (GeneralException e) {
            return ServiceUtil.returnError(standardMessage);
        }

        // return the totals
        Map<String, Object> responseResult = ServiceUtil.returnSuccess();
        responseResult.put("shippingTotal", shippingTotal);
        return responseResult;
    }

	/**
     * Attempts to get the supplier's shipping origin address and failing that, the general location.
     */
    public static GenericValue getShippingOriginContactMech(Delegator delegator, String supplierPartyId) throws GeneralException {
        List<EntityCondition> conditions = UtilMisc.toList(
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, supplierPartyId),
                EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "POSTAL_ADDRESS"),
                EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.IN, UtilMisc.toList("SHIP_ORIG_LOCATION", "GENERAL_LOCATION")),
                EntityUtil.getFilterByDateExpr("contactFromDate", "contactThruDate"),
                EntityUtil.getFilterByDateExpr("purposeFromDate", "purposeThruDate")
       );
        EntityConditionList<EntityCondition> ecl = EntityCondition.makeCondition(conditions, EntityOperator.AND);

        List<GenericValue> addresses = delegator.findList("PartyContactWithPurpose", ecl, null, UtilMisc.toList("contactMechPurposeTypeId DESC"), null, false);

        GenericValue generalAddress = null;
        GenericValue originAddress = null;
        for (GenericValue address : addresses) {
            if ("GENERAL_LOCATION".equals(address.get("contactMechPurposeTypeId")))
                generalAddress = address;
            else if ("SHIP_ORIG_LOCATION".equals(address.get("contactMechPurposeTypeId")))
                originAddress = address;
        }
        return originAddress != null ? originAddress : generalAddress;
    }
    
    public static BigDecimal getExternalShipEstimate(LocalDispatcher dispatcher, GenericValue storeShipMeth, Map<String, Object> context) throws GeneralException {
        String shipmentCustomMethodId = storeShipMeth.getString("shipmentCustomMethodId");
        String serviceName = "";
        if (UtilValidate.isNotEmpty(shipmentCustomMethodId)) {
            serviceName = getShipmentCustomMethod(dispatcher.getDelegator(), shipmentCustomMethodId);
        }
        if (UtilValidate.isEmpty(serviceName)) {
            serviceName = storeShipMeth.getString("serviceName");
        }
        // invoke the external shipping estimate service
        BigDecimal externalShipAmt = null;
        if (serviceName != null) {
            String doEstimates = UtilProperties.getPropertyValue("shipment.properties", "shipment.doratecheck", "true");
            //If all estimates are not turned off, check for the individual one
            if ("true".equals(doEstimates)) {
                String dothisEstimate = UtilProperties.getPropertyValue("shipment.properties", "shipment.doratecheck." + serviceName, "true");
                if ("false".equals(dothisEstimate))
                 serviceName = null;
            } else {
                //Rate checks inhibited
                serviceName = null;
            }
        }
        if (serviceName != null) {
            String shipmentGatewayConfigId = storeShipMeth.getString("shipmentGatewayConfigId");
            String configProps = storeShipMeth.getString("configProps");
            if (UtilValidate.isNotEmpty(serviceName)) {
                // prepare the external service context
                context.put("serviceConfigProps", configProps);
                context.put("shipmentCustomMethodId", shipmentCustomMethodId);
                context.put("shipmentGatewayConfigId", shipmentGatewayConfigId);
                
                // invoke the service
                Map<String, Object> serviceResp = null;
                try {
                    Debug.logInfo("Service : " + serviceName + " / shipmentGatewayConfigId : " + shipmentGatewayConfigId + " / configProps : " + configProps + " -- " + context, module);
                    // because we don't want to blow up too big or rollback the transaction when this happens, always have it run in its own transaction...
                    serviceResp = dispatcher.runSync(serviceName, context, 0, true);
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Shipment Service Error", module);
                    throw new GeneralException(e);
                }
                if (ServiceUtil.isError(serviceResp)) {
                    String errMsg = "Error getting external shipment cost estimate: " + ServiceUtil.getErrorMessage(serviceResp);
                    Debug.logError(errMsg, module);
                    throw new GeneralException(errMsg);
                } else if (ServiceUtil.isFailure(serviceResp)) {
                    String errMsg = "Failure getting external shipment cost estimate: " + ServiceUtil.getErrorMessage(serviceResp);
                    Debug.logError(errMsg, module);
                    // should not throw an Exception here, otherwise getShipGroupEstimate would return an error, causing all sorts of services like add or update order item to abort
                } else {
                    externalShipAmt = (BigDecimal) serviceResp.get("shippingEstimateAmount");
                }
            }
        }
        return externalShipAmt;
    }
    
    public static BigDecimal getGenericShipEstimate(LocalDispatcher dispatcher, GenericValue storeShipMeth, Map <String, ? extends Object>context) throws GeneralException {
        // invoke the generic estimate service next -- append to estimate amount
        Map<String, Object> genericEstimate = null;
        BigDecimal genericShipAmt = null;
        try {
            genericEstimate = dispatcher.runSync("calcShipmentCostEstimate", context);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Shipment Service Error", module);
            throw new GeneralException();
        }
        if (ServiceUtil.isError(genericEstimate) || ServiceUtil.isFailure(genericEstimate)) {
            Debug.logError(ServiceUtil.getErrorMessage(genericEstimate), module);
            throw new GeneralException();
        } else if (ServiceUtil.isFailure(genericEstimate)) {
            genericShipAmt = BigDecimal.ONE.negate();
        } else {
            genericShipAmt = (BigDecimal) genericEstimate.get("shippingEstimateAmount");
        }
        return genericShipAmt;
    }
    
    public static String getShipmentCustomMethod(Delegator delegator, String shipmentCustomMethodId) {
        String serviceName = null;
        GenericValue customMethod = null;
        try {
            customMethod = delegator.findOne("CustomMethod", UtilMisc.toMap("customMethodId", shipmentCustomMethodId), false);
            if (UtilValidate.isNotEmpty(customMethod)) {
                serviceName = customMethod.getString("customMethodName");
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return serviceName;
    }
}
