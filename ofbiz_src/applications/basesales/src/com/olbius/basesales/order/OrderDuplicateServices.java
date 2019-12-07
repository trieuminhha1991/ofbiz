package com.olbius.basesales.order;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import javax.transaction.Transaction;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.GeneralRuntimeException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.DataModelConstants;
import org.ofbiz.common.uom.UomWorker;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ItemNotFoundException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.order.shoppingcart.shipping.ShippingEvents;
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.ibm.icu.util.Calendar;
import com.olbius.security.util.SecurityUtil;

public class OrderDuplicateServices {
	public static final String module = OrderDuplicateServices.class.getName();
	public static final String resource = "OrderUiLabels";
    public static final String resource_error = "OrderErrorUiLabels";
    public static final int taxDecimals = UtilNumber.getBigDecimalScale("salestax.calc.decimals");
    public static final int taxRounding = UtilNumber.getBigDecimalRoundingMode("salestax.rounding");
    public static final int orderDecimals = UtilNumber.getBigDecimalScale("order.decimals");
    public static final int orderRounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
    public static final BigDecimal ZERO = BigDecimal.ZERO.setScale(taxDecimals, taxRounding);
   
    public static boolean hasPermission(String orderId, GenericValue userLogin, String action, Security security, Delegator delegator) {
        OrderReadHelper orh = new OrderReadHelper(delegator, orderId);
        String orderTypeId = orh.getOrderTypeId();
        String partyId = null;
        GenericValue orderParty = orh.getEndUserParty();
        if (UtilValidate.isEmpty(orderParty)) {
            orderParty = orh.getPlacingParty();
        }
        if (UtilValidate.isNotEmpty(orderParty)) {
            partyId = orderParty.getString("partyId");
        }
        boolean hasPermission = hasPermission(orderTypeId, partyId, userLogin, action, security);
        if (!hasPermission) {
            GenericValue placingCustomer = null;
            try {
                Map<String, Object> placingCustomerFields = UtilMisc.<String, Object>toMap("orderId", orderId, "partyId", userLogin.getString("partyId"), "roleTypeId", "PLACING_CUSTOMER");
                placingCustomer = delegator.findOne("OrderRole", placingCustomerFields, false);
            } catch (GenericEntityException e) {
                Debug.logError("Could not select OrderRoles for order " + orderId + " due to " + e.getMessage(), module);
            }
            hasPermission = (placingCustomer != null);
        }
        return hasPermission;
    }

    private static boolean hasPermission(String orderTypeId, String partyId, GenericValue userLogin, String action, Security security) {
        //boolean hasPermission = security.hasEntityPermission("ORDERMGR", "_" + action, userLogin);
        
        // TODOCHANGE
        boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, action, "ENTITY", "SALESORDER");
        
        if (!hasPermission) {
            if (orderTypeId.equals("SALES_ORDER")) {
                if (security.hasEntityPermission("ORDERMGR", "_SALES_" + action, userLogin) || security.hasPermission("ORDER_HOLD_UPDATE", userLogin)) {
                    hasPermission = true;
                } else {
                    // check sales agent/customer relationship
                    List<GenericValue> repsCustomers = new LinkedList<GenericValue>();
                    try {
                        repsCustomers = EntityUtil.filterByDate(userLogin.getRelatedOne("Party", false).getRelated("FromPartyRelationship", UtilMisc.toMap("roleTypeIdFrom", "AGENT", "roleTypeIdTo", "CUSTOMER", "partyIdTo", partyId), null, false));
                    } catch (GenericEntityException ex) {
                        Debug.logError("Could not determine if " + partyId + " is a customer of user " + userLogin.getString("userLoginId") + " due to " + ex.getMessage(), module);
                    }
                    if ((repsCustomers != null) && (repsCustomers.size() > 0) && (security.hasEntityPermission("ORDERMGR", "_ROLE_" + action, userLogin))) {
                        hasPermission = true;
                    }
                    if (!hasPermission) {
                        // check sales sales rep/customer relationship
                        try {
                            repsCustomers = EntityUtil.filterByDate(userLogin.getRelatedOne("Party", false).getRelated("FromPartyRelationship", UtilMisc.toMap("roleTypeIdFrom", "SALES_REP", "roleTypeIdTo", "CUSTOMER", "partyIdTo", partyId), null, false));
                        } catch (GenericEntityException ex) {
                            Debug.logError("Could not determine if " + partyId + " is a customer of user " + userLogin.getString("userLoginId") + " due to " + ex.getMessage(), module);
                        }
                        if ((repsCustomers != null) && (repsCustomers.size() > 0) && (security.hasEntityPermission("ORDERMGR", "_ROLE_" + action, userLogin))) {
                            hasPermission = true;
                        }
                    }
                }
            } else if ((orderTypeId.equals("PURCHASE_ORDER") && (security.hasEntityPermission("ORDERMGR", "_PURCHASE_" + action, userLogin)))) {
                hasPermission = true;
            }
        }
        return hasPermission;
    }
    
    public static Map<String, Object> loadCartForUpdate(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();

        String orderId = (String) context.get("orderId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        ShoppingCart cart = null;
        Map<String, Object> result = null;
        try {
        	Boolean isRemoveOrderAdjustment = (Boolean) context.get("isRemoveOrderAdjustment"); // TODOCHANGE process order adjustment billing invoice
        	if (isRemoveOrderAdjustment == null) isRemoveOrderAdjustment = true;
        	
            cart = loadCartForUpdate(dispatcher, delegator, userLogin, orderId, isRemoveOrderAdjustment);
            result = ServiceUtil.returnSuccess();
            result.put("shoppingCart", cart);
        } catch (GeneralException e) {
            Debug.logError(e, module);
            result = ServiceUtil.returnError(e.getMessage());
        }

        result.put("orderId", orderId);
        return result;
    }
    
    public static ShoppingCart loadCartForUpdate(LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin, String orderId) throws GeneralException {
    	return loadCartForUpdate(dispatcher, delegator, userLogin, orderId, true);
    }
    
    /*
     * TODOCHANGE add new parameter isRemoveOrderAdjustment, process order adjustment billing invoice
     */
	/*
     *  Warning: loadCartForUpdate(...) and saveUpdatedCartToOrder(...) must always
     *           be used together in this sequence.
     *           In fact loadCartForUpdate(...) will remove or cancel data associated to the order,
     *           before returning the ShoppingCart object; for this reason, the cart
     *           must be stored back using the method saveUpdatedCartToOrder(...),
     *           because that method will recreate the data.
     */
    public static ShoppingCart loadCartForUpdate(LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin, String orderId, Boolean isRemoveOrderAdjustment) throws GeneralException {
        // load the order into a shopping cart
        Map<String, Object> loadCartResp = null;
        try {
            loadCartResp = dispatcher.runSync("loadCartFromOrder", UtilMisc.<String, Object>toMap("orderId", orderId,
                                                                                  "skipInventoryChecks", Boolean.TRUE, // the items are already reserved, no need to check again
                                                                                  "skipProductChecks", Boolean.TRUE, // the products are already in the order, no need to check their validity now
                                                                                  "userLogin", userLogin));
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }
        if (ServiceUtil.isError(loadCartResp)) {
            throw new GeneralException(ServiceUtil.getErrorMessage(loadCartResp));
        }

        ShoppingCart cart = (ShoppingCart) loadCartResp.get("shoppingCart");
        if (cart == null) {
            throw new GeneralException("Error loading shopping cart from order [" + orderId + "]");
        } else {
            cart.setOrderId(orderId);
        }

        // Now that the cart is loaded, all the data that will be re-created
        // when the method saveUpdatedCartToOrder(...) will be called, are
        // removed and cancelled:
        // - inventory reservations are cancelled
        // - promotional items are cancelled
        // - order payments are released (cancelled)
        // - offline non received payments are cancelled
        // - promotional, shipping and tax adjustments are removed

        // Inventory reservations
        // find ship group associations
        List<GenericValue> shipGroupAssocs = null;
        try {
            shipGroupAssocs = delegator.findByAnd("OrderItemShipGroupAssoc", UtilMisc.toMap("orderId", orderId), null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }
        // cancel existing inventory reservations
        if (shipGroupAssocs != null) {
            for (GenericValue shipGroupAssoc : shipGroupAssocs) {
                String orderItemSeqId = shipGroupAssoc.getString("orderItemSeqId");
                String shipGroupSeqId = shipGroupAssoc.getString("shipGroupSeqId");

                Map<String, Object> cancelCtx = UtilMisc.<String, Object>toMap("userLogin", userLogin, "orderId", orderId);
                cancelCtx.put("orderItemSeqId", orderItemSeqId);
                cancelCtx.put("shipGroupSeqId", shipGroupSeqId);

                Map<String, Object> cancelResp = null;
                try {
                    cancelResp = dispatcher.runSync("cancelOrderInventoryReservation", cancelCtx);
                } catch (GenericServiceException e) {
                    Debug.logError(e, module);
                    throw new GeneralException(e.getMessage());
                }
                if (ServiceUtil.isError(cancelResp)) {
                    throw new GeneralException(ServiceUtil.getErrorMessage(cancelResp));
                }
            }
        }

        // cancel promo items -- if the promo still qualifies it will be added by the cart
        List<GenericValue> promoItems = null;
        try {
        	// TODOCHANGE new orderItemType PRODPROMO_ORDER_ITEM
        	List<EntityCondition> condsAll = FastList.newInstance();
        	condsAll.add(EntityCondition.makeCondition("orderId", orderId));
        	condsAll.add(EntityCondition.makeCondition("isPromo", "Y"));
        	condsAll.add(EntityCondition.makeCondition("orderItemTypeId", EntityOperator.NOT_EQUAL, "PRODPROMO_ORDER_ITEM"));
            promoItems = delegator.findList("OrderItem", EntityCondition.makeCondition(condsAll, EntityOperator.AND), null, null, null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }
        if (promoItems != null) {
            for (GenericValue promoItem : promoItems) {
                // Skip if the promo is already cancelled
                if ("ITEM_CANCELLED".equals(promoItem.get("statusId"))) {
                    continue;
                }
                Map<String, Object> cancelPromoCtx = UtilMisc.<String, Object>toMap("orderId", orderId);
                cancelPromoCtx.put("orderItemSeqId", promoItem.getString("orderItemSeqId"));
                cancelPromoCtx.put("userLogin", userLogin);
                Map<String, Object> cancelResp = null;
                try {
                    cancelResp = dispatcher.runSync("cancelOrderItemNoActions", cancelPromoCtx);
                } catch (GenericServiceException e) {
                    Debug.logError(e, module);
                    throw new GeneralException(e.getMessage());
                }
                if (ServiceUtil.isError(cancelResp)) {
                    throw new GeneralException(ServiceUtil.getErrorMessage(cancelResp));
                }
            }
        }

        // cancel exiting authorizations
        Map<String, Object> releaseResp = null;
        try {
            releaseResp = dispatcher.runSync("releaseOrderPayments", UtilMisc.<String, Object>toMap("orderId", orderId, "userLogin", userLogin));
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }
        if (ServiceUtil.isError(releaseResp)) {
            throw new GeneralException(ServiceUtil.getErrorMessage(releaseResp));
        }

        // cancel other (non-completed and non-cancelled) payments
        List<GenericValue> paymentPrefsToCancel = null;
        try {
            List<EntityExpr> exprs = UtilMisc.toList(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
            exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_RECEIVED"));
            exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_CANCELLED"));
            exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_DECLINED"));
            exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_SETTLED"));
            exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_REFUNDED"));
            EntityCondition cond = EntityCondition.makeCondition(exprs, EntityOperator.AND);
            paymentPrefsToCancel = delegator.findList("OrderPaymentPreference", cond, null, null, null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }
        if (paymentPrefsToCancel != null) {
            for (GenericValue opp : paymentPrefsToCancel) {
                try {
                    opp.set("statusId", "PAYMENT_CANCELLED");
                    opp.store();
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    throw new GeneralException(e.getMessage());
                }
            }
        }

        if (isRemoveOrderAdjustment) {
        	// remove the adjustments
            try {
                List<EntityCondition> adjExprs = new LinkedList<EntityCondition>();
                adjExprs.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
                List<EntityCondition> exprs = new LinkedList<EntityCondition>();
                exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "PROMOTION_ADJUSTMENT"));
                exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "SHIPPING_CHARGES"));
                exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "SALES_TAX"));
                exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "VAT_TAX"));
                exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "VAT_PRICE_CORRECT"));
                adjExprs.add(EntityCondition.makeCondition(exprs, EntityOperator.OR));
                EntityCondition cond = EntityCondition.makeCondition(adjExprs, EntityOperator.AND);
                delegator.removeByCondition("OrderAdjustment", cond);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                throw new GeneralException(e.getMessage());
            }
        }

        return cart;
    }

    public static Map<String, Object> saveUpdatedCartToOrder(DispatchContext dctx, Map<String, ? extends Object> context) throws GeneralException {

        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();

        String orderId = (String) context.get("orderId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        ShoppingCart cart = (ShoppingCart) context.get("shoppingCart");
        Map<String, Object> changeMap = UtilGenerics.checkMap(context.get("changeMap"));
        Locale locale = (Locale) context.get("locale");
        Boolean deleteItems = (Boolean) context.get("deleteItems");
        Boolean calcTax = (Boolean) context.get("calcTax");
        if (calcTax == null) {
            calcTax = Boolean.TRUE;
        }

        Map<String, Object> result = null;
        try {
            saveUpdatedCartToOrder(dispatcher, delegator, cart, locale, userLogin, orderId, changeMap, calcTax, deleteItems);
            result = ServiceUtil.returnSuccess();
            //result.put("shoppingCart", cart);
        } catch (GeneralException e) {
            Debug.logError(e, module);
            result = ServiceUtil.returnError(e.getMessage());
        }

        result.put("orderId", orderId);
        return result;
    }
    
    public static void saveUpdatedCartToOrder(LocalDispatcher dispatcher, Delegator delegator, ShoppingCart cart,
            Locale locale, GenericValue userLogin, String orderId, Map<String, Object> changeMap, boolean calcTax,
            boolean deleteItems) throws GeneralException {
    	saveUpdatedCartToOrder(dispatcher, delegator, cart, locale, userLogin, orderId, changeMap, calcTax, deleteItems, true);
    }

    /*
     * TODOCHANGE add new parameter isRemoveOrderAdjustment, process order adjustment billing invoice
     */
    public static void saveUpdatedCartToOrder(LocalDispatcher dispatcher, Delegator delegator, ShoppingCart cart,
            Locale locale, GenericValue userLogin, String orderId, Map<String, Object> changeMap, boolean calcTax,
            boolean deleteItems, boolean isRemoveOrderAdjustment) throws GeneralException {
        // get/set the shipping estimates.  if it's a SALES ORDER, then return an error if there are no ship estimates
        int shipGroups = cart.getShipGroupSize();
        for (int gi = 0; gi < shipGroups; gi++) {
            String shipmentMethodTypeId = cart.getShipmentMethodTypeId(gi);
            String carrierPartyId = cart.getCarrierPartyId(gi);
            Debug.logInfo("Getting ship estimate for group #" + gi + " [" + shipmentMethodTypeId + " / " + carrierPartyId + "]", module);
            Map<String, Object> result = ShippingEvents.getShipGroupEstimate(dispatcher, delegator, cart, gi);
            if (("SALES_ORDER".equals(cart.getOrderType())) && (ServiceUtil.isError(result))) {
                Debug.logError(ServiceUtil.getErrorMessage(result), module);
                throw new GeneralException(ServiceUtil.getErrorMessage(result));
            }

            BigDecimal shippingTotal = (BigDecimal) result.get("shippingTotal");
            if (shippingTotal == null) {
                shippingTotal = BigDecimal.ZERO;
            }
            cart.setItemShipGroupEstimate(shippingTotal, gi);
        }

        // calc the sales tax        
        CheckOutHelper coh = new CheckOutHelper(dispatcher, delegator, cart);
        if (calcTax) {
            try {
                coh.calcAndAddTax();
            } catch (GeneralException e) {
                Debug.logError(e, module);
                throw new GeneralException(e.getMessage());
            }
        }

        // get the new orderItems, adjustments, shipping info, payments and order item attributes from the cart
        List<Map<String, Object>> modifiedItems = FastList.newInstance();
        List<Map<String, Object>> newItems = FastList.newInstance(); // TODOCHANGE make order item status
        List<GenericValue> toStore = new LinkedList<GenericValue>();
        List<GenericValue> toAddList = new ArrayList<GenericValue>();
        toAddList.addAll(cart.makeAllAdjustments());
        cart.clearAllPromotionAdjustments();
        ProductPromoWorker.doPromotions(cart, dispatcher);

        // validate the payment methods
        Map<String, Object> validateResp = coh.validatePaymentMethods();
        if (ServiceUtil.isError(validateResp)) {
            throw new GeneralException(ServiceUtil.getErrorMessage(validateResp));
        }

        // handle OrderHeader fields
        String billingAccountId = cart.getBillingAccountId();
        if (UtilValidate.isNotEmpty(billingAccountId)) {
            try {
                GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
                orderHeader.set("billingAccountId", billingAccountId);
                toStore.add(orderHeader);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                throw new GeneralException(e.getMessage());
            }
        }

        toStore.addAll(cart.makeOrderItems());
        toStore.addAll(cart.makeAllAdjustments());

        String shipGroupSeqId = null;
        long groupIndex = cart.getShipInfoSize();
        if (!deleteItems) {
            for (long itr = 1; itr <= groupIndex; itr++) {
                shipGroupSeqId = UtilFormatOut.formatPaddedNumber(itr, 5);
                List<GenericValue> removeList = new ArrayList<GenericValue>();
                for (GenericValue stored: toStore) {
                    if ("OrderAdjustment".equals(stored.getEntityName())) {
                        if (("SHIPPING_CHARGES".equals(stored.get("orderAdjustmentTypeId")) ||
                                "SALES_TAX".equals(stored.get("orderAdjustmentTypeId"))) &&
                                stored.get("orderId").equals(orderId) &&
                                stored.get("shipGroupSeqId").equals(shipGroupSeqId)) {
                            // Removing objects from toStore list for old Shipping and Handling Charges Adjustment and Sales Tax Adjustment.
                            removeList.add(stored);
                        }
                        if (stored.get("comments") != null && ((String)stored.get("comments")).startsWith("Added manually by")) {
                            // Removing objects from toStore list for Manually added Adjustment.
                            removeList.add(stored);
                        }
                    }
                }
                toStore.removeAll(removeList);
            }
            for (GenericValue toAdd: toAddList) {
                if ("OrderAdjustment".equals(toAdd.getEntityName())) {
                    if (toAdd.get("comments") != null && ((String)toAdd.get("comments")).startsWith("Added manually by") && (("PROMOTION_ADJUSTMENT".equals(toAdd.get("orderAdjustmentTypeId"))) ||
                            ("SHIPPING_CHARGES".equals(toAdd.get("orderAdjustmentTypeId"))) || ("SALES_TAX".equals(toAdd.get("orderAdjustmentTypeId"))))) {
                        toStore.add(toAdd);
                    }
                }
            }
        } else {                      
            // add all the cart adjustments
            toStore.addAll(toAddList);
        }
        
        // TODOCHANGE new process order adjustment billing invoice
        List<GenericValue> listOrderAdjExists = FastList.newInstance();
    	if (!isRemoveOrderAdjustment) {
        	// remove the adjustments
            try {
                List<EntityCondition> adjExprs = new LinkedList<EntityCondition>();
                adjExprs.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
                List<EntityCondition> exprs = new LinkedList<EntityCondition>();
                exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "PROMOTION_ADJUSTMENT"));
                exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "SHIPPING_CHARGES"));
                exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "SALES_TAX"));
                exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "VAT_TAX"));
                exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "VAT_PRICE_CORRECT"));
                adjExprs.add(EntityCondition.makeCondition(exprs, EntityOperator.OR));
                EntityCondition cond = EntityCondition.makeCondition(adjExprs, EntityOperator.AND);
                //delegator.removeByCondition("OrderAdjustment", cond);
                
                listOrderAdjExists = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(cond), null, null, null, false);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                throw new GeneralException(e.getMessage());
            }
        }
        
        // Creating objects for New Shipping and Handling Charges Adjustment and Sales Tax Adjustment
    	if (!isRemoveOrderAdjustment) {
    		// new code
    		// TODOCHANGE new process order adjustment billing invoice
    		List<GenericValue> orderItemShipGroupInfo = cart.makeAllShipGroupInfos();
        	// set the order item ship groups
            // List<String> dropShipGroupIds = FastList.newInstance(); // this list will contain the ids of all the ship groups for drop shipments (no reservations)
            if (UtilValidate.isNotEmpty(orderItemShipGroupInfo)) {
                for (GenericValue valueObj : orderItemShipGroupInfo) {
                    valueObj.set("orderId", orderId);
                    if ("OrderAdjustment".equals(valueObj.getEntityName())) {
                        //TODOCHANGE check adjustment exists, update this adjustment
                        List<GenericValue> ordAdjEquals = EntityUtil.filterByAnd(listOrderAdjExists, 
                        			UtilMisc.toMap("orderId", valueObj.get("orderId"), "orderItemSeqId", valueObj.get("orderItemSeqId"), 
                        				"orderAdjustmentTypeId", valueObj.get("orderAdjustmentTypeId"), "shipGroupSeqId", valueObj.get("shipGroupSeqId")));
                        if (UtilValidate.isNotEmpty(ordAdjEquals)) {
                        	// new code
                        	listOrderAdjExists.removeAll(ordAdjEquals);
                        	/*for (GenericValue item : ordAdjEquals) {
                        		item.setNonPKFields(valueObj);
                        	}
                        	toStore.addAll(ordAdjEquals);*/
                        	
                        	String orderAdjustmentId = ordAdjEquals.get(0).getString("orderAdjustmentId");
                        	valueObj.put("orderAdjustmentId", orderAdjustmentId);
                        	toStore.add(valueObj);
                        } else {
                        	// old code
                        	/*// shipping / tax adjustment(s)
                            if (UtilValidate.isEmpty(valueObj.get("orderItemSeqId"))) {
                                valueObj.set("orderItemSeqId", DataModelConstants.SEQ_ID_NA);
                            }
                            valueObj.set("orderAdjustmentId", delegator.getNextSeqId("OrderAdjustment"));
                            valueObj.set("createdDate", UtilDateTime.nowTimestamp());
                            valueObj.set("createdByUserLogin", userLogin.getString("userLoginId"));*/
                            
                            toStore.add(valueObj);
                        }
                    } else {
                    	toStore.add(valueObj);
                    }
                    // toBeStored.add(valueObj);
                }
            }
        	// end new
    	} else {
    		// old code
    		toStore.addAll(cart.makeAllShipGroupInfos());
    	}
        toStore.addAll(cart.makeAllOrderPaymentInfos(dispatcher));
        toStore.addAll(cart.makeAllOrderItemAttributes(orderId, ShoppingCart.FILLED_ONLY));        

        
        List<GenericValue> toRemove = FastList.newInstance();
        if (deleteItems) {
            // flag to delete existing order items and adjustments           
            try {
                toRemove.addAll(delegator.findByAnd("OrderItemShipGroupAssoc", UtilMisc.toMap("orderId", orderId), null, false));
                toRemove.addAll(delegator.findByAnd("OrderItemContactMech", UtilMisc.toMap("orderId", orderId), null, false));
                toRemove.addAll(delegator.findByAnd("OrderItemPriceInfo", UtilMisc.toMap("orderId", orderId), null, false));
                toRemove.addAll(delegator.findByAnd("OrderItemAttribute", UtilMisc.toMap("orderId", orderId), null, false));
                toRemove.addAll(delegator.findByAnd("OrderItemBilling", UtilMisc.toMap("orderId", orderId), null, false));
                toRemove.addAll(delegator.findByAnd("OrderItemRole", UtilMisc.toMap("orderId", orderId), null, false));
                toRemove.addAll(delegator.findByAnd("OrderItemChange", UtilMisc.toMap("orderId", orderId), null, false));
                toRemove.addAll(delegator.findByAnd("OrderAdjustment", UtilMisc.toMap("orderId", orderId), null, false));
                toRemove.addAll(delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId), null, false));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        } else {
            // get the empty order item atrributes from the cart and remove them
            toRemove.addAll(cart.makeAllOrderItemAttributes(orderId, ShoppingCart.EMPTY_ONLY));
        }
        
        // TODOCHANGE new process order adjustment billing invoice
        if (!isRemoveOrderAdjustment && UtilValidate.isNotEmpty(listOrderAdjExists)) {
        	toRemove.addAll(listOrderAdjExists);
        }

        // get the promo uses and codes
        for (String promoCodeEntered : cart.getProductPromoCodesEntered()) {
            GenericValue orderProductPromoCode = delegator.makeValue("OrderProductPromoCode");                                   
            orderProductPromoCode.set("orderId", orderId);
            orderProductPromoCode.set("productPromoCodeId", promoCodeEntered);
            toStore.add(orderProductPromoCode);                                    
        }
        for (GenericValue promoUse : cart.makeProductPromoUses()) {
            promoUse.set("orderId", orderId);
            toStore.add(promoUse);
        }        
        
        List<GenericValue> existingPromoCodes = null;
        List<GenericValue> existingPromoUses = null;
        try {
            existingPromoCodes = delegator.findByAnd("OrderProductPromoCode", UtilMisc.toMap("orderId", orderId), null, false);
            existingPromoUses = delegator.findByAnd("ProductPromoUse", UtilMisc.toMap("orderId", orderId), null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        toRemove.addAll(existingPromoCodes);
        toRemove.addAll(existingPromoUses);
                        
        // set the orderId & other information on all new value objects
        List<String> dropShipGroupIds = FastList.newInstance(); // this list will contain the ids of all the ship groups for drop shipments (no reservations)
        for (GenericValue valueObj : toStore) {
            valueObj.set("orderId", orderId);
            if ("OrderItemShipGroup".equals(valueObj.getEntityName())) {
                // ship group
                if (valueObj.get("carrierRoleTypeId") == null) {
                    valueObj.set("carrierRoleTypeId", "CARRIER");
                }
                if (!UtilValidate.isEmpty(valueObj.get("supplierPartyId"))) {
                    dropShipGroupIds.add(valueObj.getString("shipGroupSeqId"));
                }
            } else if ("OrderAdjustment".equals(valueObj.getEntityName())) {
                // shipping / tax adjustment(s)
                if (UtilValidate.isEmpty(valueObj.get("orderItemSeqId"))) {
                    valueObj.set("orderItemSeqId", DataModelConstants.SEQ_ID_NA);
                }
                // in order to avoid duplicate adjustments don't set orderAdjustmentId (which is the pk) if there is already one
                if (UtilValidate.isEmpty(valueObj.getString("orderAdjustmentId"))) {
                    valueObj.set("orderAdjustmentId", delegator.getNextSeqId("OrderAdjustment"));
                }
                valueObj.set("createdDate", UtilDateTime.nowTimestamp());
                valueObj.set("createdByUserLogin", userLogin.getString("userLoginId"));
            } else if ("OrderPaymentPreference".equals(valueObj.getEntityName())) {
                if (valueObj.get("orderPaymentPreferenceId") == null) {
                    valueObj.set("orderPaymentPreferenceId", delegator.getNextSeqId("OrderPaymentPreference"));
                    valueObj.set("createdDate", UtilDateTime.nowTimestamp());
                    valueObj.set("createdByUserLogin", userLogin.getString("userLoginId"));
                }
                if (valueObj.get("statusId") == null) {
                    valueObj.set("statusId", "PAYMENT_NOT_RECEIVED");
                }
            } else if ("OrderItem".equals(valueObj.getEntityName()) && !deleteItems) {

                //  ignore promotion items. They are added/canceled automatically
                //  ignore promotion items. They are added/canceled automatically
            	// TODOCHANGE new orderItemType PRODPROMO_ORDER_ITEM
                if ("Y".equals(valueObj.getString("isPromo")) && !"PRODPROMO_ORDER_ITEM".equals(valueObj.getString("orderItemTypeId"))) {
                    continue;
                }
                GenericValue oldOrderItem = null;
                try {
                    oldOrderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", valueObj.getString("orderId"), "orderItemSeqId", valueObj.getString("orderItemSeqId")), false);
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    throw new GeneralException(e.getMessage());
                }
                if (UtilValidate.isNotEmpty(oldOrderItem)) {

                    //  Existing order item found. Check for modifications and store if any
                    String oldItemDescription = oldOrderItem.getString("itemDescription") != null ? oldOrderItem.getString("itemDescription") : "";
                    BigDecimal oldQuantity = oldOrderItem.getBigDecimal("quantity") != null ? oldOrderItem.getBigDecimal("quantity") : BigDecimal.ZERO;
                    BigDecimal oldUnitPrice = oldOrderItem.getBigDecimal("unitPrice") != null ? oldOrderItem.getBigDecimal("unitPrice") : BigDecimal.ZERO;

                    boolean changeFound = false;
                    Map<String, Object> modifiedItem = FastMap.newInstance();
                    if (!oldItemDescription.equals(valueObj.getString("itemDescription"))) {
                        modifiedItem.put("itemDescription", oldItemDescription);
                        changeFound = true;
                    }

                    BigDecimal quantityDif = valueObj.getBigDecimal("quantity").subtract(oldQuantity);
                    BigDecimal unitPriceDif = valueObj.getBigDecimal("unitPrice").subtract(oldUnitPrice);
                    if (quantityDif.compareTo(BigDecimal.ZERO) != 0) {
                        modifiedItem.put("quantity", quantityDif);
                        changeFound = true;
                    }
                    if (unitPriceDif.compareTo(BigDecimal.ZERO) != 0) {
                        modifiedItem.put("unitPrice", unitPriceDif);
                        changeFound = true;
                    }
                    if (changeFound) {

                        //  found changes to store
                        Map<String, String> itemReasonMap = UtilGenerics.checkMap(changeMap.get("itemReasonMap"));
                        Map<String, String> itemCommentMap = UtilGenerics.checkMap(changeMap.get("itemCommentMap"));
                        if (UtilValidate.isNotEmpty(itemReasonMap)) {
                            String changeReasonId = itemReasonMap.get(valueObj.getString("orderItemSeqId"));
                            modifiedItem.put("reasonEnumId", changeReasonId);
                        }
                        if (UtilValidate.isNotEmpty(itemCommentMap)) {
                            String changeComments = itemCommentMap.get(valueObj.getString("orderItemSeqId"));
                            modifiedItem.put("changeComments", changeComments);
                        }

                        modifiedItem.put("orderId", valueObj.getString("orderId"));
                        modifiedItem.put("orderItemSeqId", valueObj.getString("orderItemSeqId"));
                        modifiedItem.put("changeTypeEnumId", "ODR_ITM_UPDATE");
                        modifiedItems.add(modifiedItem);
                    }
                } else {

                    //  this is a new item appended to the order
                    Map<String, String> itemReasonMap = UtilGenerics.checkMap(changeMap.get("itemReasonMap"));
                    Map<String, String> itemCommentMap = UtilGenerics.checkMap(changeMap.get("itemCommentMap"));
                    Map<String, Object> appendedItem = FastMap.newInstance();
                    if (UtilValidate.isNotEmpty(itemReasonMap)) {
                        String changeReasonId = itemReasonMap.get("reasonEnumId");
                        appendedItem.put("reasonEnumId", changeReasonId);
                    }
                    if (UtilValidate.isNotEmpty(itemCommentMap)) {
                        String changeComments = itemCommentMap.get("changeComments");
                        appendedItem.put("changeComments", changeComments);
                    }

                    appendedItem.put("orderId", valueObj.getString("orderId"));
                    appendedItem.put("orderItemSeqId", valueObj.getString("orderItemSeqId"));
                    appendedItem.put("quantity", valueObj.getBigDecimal("quantity"));
                    appendedItem.put("changeTypeEnumId", "ODR_ITM_APPEND");
                    modifiedItems.add(appendedItem);
                    
                    //TODOCHANGE make order item status
                    Map<String, Object> appendedItemNew = new HashMap<String, Object>(appendedItem);
                    appendedItemNew.put("statusId", valueObj.getString("statusId"));
                    newItems.add(appendedItemNew);
                }
            }
        }
        
        if (Debug.verboseOn())
            Debug.logVerbose("To Store Contains: " + toStore, module);

        // remove any order item attributes that were set to empty
        try {
            delegator.removeAll(toRemove,true);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }

        // store the new items/adjustments/order item attributes
        try {
            delegator.storeAll(toStore);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }

        //  store the OrderItemChange
        if (UtilValidate.isNotEmpty(modifiedItems)) {
            for (Map<String, Object> modifiendItem: modifiedItems) {
                Map<String, Object> serviceCtx = FastMap.newInstance();
                serviceCtx.put("orderId", modifiendItem.get("orderId"));
                serviceCtx.put("orderItemSeqId", modifiendItem.get("orderItemSeqId"));
                serviceCtx.put("itemDescription", modifiendItem.get("itemDescription"));
                serviceCtx.put("quantity", modifiendItem.get("quantity"));
                serviceCtx.put("unitPrice", modifiendItem.get("unitPrice"));
                serviceCtx.put("changeTypeEnumId", modifiendItem.get("changeTypeEnumId"));
                serviceCtx.put("reasonEnumId", modifiendItem.get("reasonEnumId"));
                serviceCtx.put("changeComments", modifiendItem.get("changeComments"));
                serviceCtx.put("userLogin", userLogin);
                Map<String, Object> resp = null;
                try {
                    resp = dispatcher.runSync("createOrderItemChange", serviceCtx);
                } catch (GenericServiceException e) {
                    Debug.logError(e, module);
                    throw new GeneralException(e.getMessage());
                }
                if (ServiceUtil.isError(resp)) {
                    throw new GeneralException((String) resp.get(ModelService.ERROR_MESSAGE));
                }
            }
        }
        
        // make the order item status
        if (UtilValidate.isNotEmpty(newItems)) {
        	List<GenericValue> tobeStored = new LinkedList<GenericValue>();
        	for (Map<String, Object> newItem : newItems) {
        		// create the item status record
        		String newStatusId = (String) newItem.get("statusId");
        		if (!"ITEM_CREATED".equals(newStatusId)) {
        			String itemStatusId = delegator.getNextSeqId("OrderStatus");
                    GenericValue itemStatus = delegator.makeValue("OrderStatus", UtilMisc.toMap("orderStatusId", itemStatusId));
                    itemStatus.put("statusId", "ITEM_CREATED");
                    itemStatus.put("orderId", orderId);
                    itemStatus.put("orderItemSeqId", newItem.get("orderItemSeqId"));
                    itemStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
                    itemStatus.set("statusUserLogin", userLogin.getString("userLoginId"));
                    tobeStored.add(itemStatus);
        		}
        		
        		String itemStatusId = delegator.getNextSeqId("OrderStatus");
                GenericValue itemStatus = delegator.makeValue("OrderStatus", UtilMisc.toMap("orderStatusId", itemStatusId));
                itemStatus.put("statusId", newStatusId);
                itemStatus.put("orderId", orderId);
                itemStatus.put("orderItemSeqId", newItem.get("orderItemSeqId"));
                itemStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
                itemStatus.set("statusUserLogin", userLogin.getString("userLoginId"));
                tobeStored.add(itemStatus);
        	}
        	delegator.storeAll(tobeStored);
        }

        // make the order item object map & the ship group assoc list
        List<GenericValue> orderItemShipGroupAssoc = new LinkedList<GenericValue>();
        Map<String, GenericValue> itemValuesBySeqId = new HashMap<String, GenericValue>();
        for (GenericValue v : toStore) {
            if ("OrderItem".equals(v.getEntityName())) {
                itemValuesBySeqId.put(v.getString("orderItemSeqId"), v);
            } else if ("OrderItemShipGroupAssoc".equals(v.getEntityName())) {
                orderItemShipGroupAssoc.add(v);
            }
        }

        // reserve the inventory
        String productStoreId = cart.getProductStoreId();
        String orderTypeId = cart.getOrderType();
        List<String> resErrorMessages = new LinkedList<String>();
        try {
            Debug.logInfo("Calling reserve inventory...", module);
            org.ofbiz.order.order.OrderServices.reserveInventory(delegator, dispatcher, userLogin, locale, orderItemShipGroupAssoc, dropShipGroupIds, itemValuesBySeqId,
                    orderTypeId, productStoreId, resErrorMessages);
        } catch (GeneralException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }

        if (resErrorMessages.size() > 0) {
            throw new GeneralException(ServiceUtil.getErrorMessage(ServiceUtil.returnError(resErrorMessages)));
        }
    }

    public static Map<String, Object> processOrderPayments(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        Locale locale = (Locale) context.get("locale");

        OrderReadHelper orh = new OrderReadHelper(delegator, orderId);
        String productStoreId = orh.getProductStoreId();

        // check if order was already cancelled / rejected
        GenericValue orderHeader = orh.getOrderHeader();
        String orderStatus = orderHeader.getString("statusId");
        if ("ORDER_CANCELLED".equals(orderStatus) || "ORDER_REJECTED".equals(orderStatus)) {
            return ServiceUtil.returnFailure(UtilProperties.getMessage(resource,
                    "OrderProcessOrderPaymentsStatusInvalid", locale) + orderStatus);
        }

        // process the payments
        if (!"PURCHASE_ORDER".equals(orh.getOrderTypeId())) {
            GenericValue productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
            Map<String, Object> paymentResp = null;
            try {
                Debug.logInfo("Calling process payments...", module);
                //Debug.set(Debug.VERBOSE, true);
                paymentResp = CheckOutHelper.processPayment(orderId, orh.getOrderGrandTotal(), orh.getCurrency(), productStore, userLogin, false, false, dispatcher, delegator);
                //Debug.set(Debug.VERBOSE, false);
            } catch (GeneralException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            } catch (GeneralRuntimeException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }

            if (ServiceUtil.isError(paymentResp)) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                        "OrderProcessOrderPayments", locale), null, null, paymentResp);
            }
        }
        return ServiceUtil.returnSuccess();
    }

    // sample test services
    public static Map<String, Object> shoppingCartTest(DispatchContext dctx, Map<String, ? extends Object> context) {
        Locale locale = (Locale) context.get("locale");
        ShoppingCart cart = new ShoppingCart(dctx.getDelegator(), "9000", "webStore", locale, "USD");
        try {
            cart.addOrIncreaseItem("GZ-1005", null, BigDecimal.ONE, null, null, null, null, null, null, null, "DemoCatalog", null, null, null, null, dctx.getDispatcher());
        } catch (CartItemModifyException e) {
            Debug.logError(e, module);
        } catch (ItemNotFoundException e) {
            Debug.logError(e, module);
        }

        try {
            dctx.getDispatcher().runAsync("shoppingCartRemoteTest", UtilMisc.toMap("cart", cart), true);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> shoppingCartRemoteTest(DispatchContext dctx, Map<String, ? extends Object> context) {
        ShoppingCart cart = (ShoppingCart) context.get("cart");
        Debug.logInfo("Product ID : " + cart.findCartItem(0).getProductId(), module);
        return ServiceUtil.returnSuccess();
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
        try {
            // get the order payment preference
            GenericValue orderPaymentPreference = delegator.findOne("OrderPaymentPreference", UtilMisc.toMap("orderPaymentPreferenceId", orderPaymentPreferenceId), false);
            if (orderPaymentPreference == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                        "OrderOrderPaymentCannotBeCreated",
                        UtilMisc.toMap("orderPaymentPreferenceId", "orderPaymentPreferenceId"), locale));
            }

            // get the order header
            GenericValue orderHeader = orderPaymentPreference.getRelatedOne("OrderHeader", false);
            if (orderHeader == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                        "OrderOrderPaymentCannotBeCreatedWithRelatedOrderHeader", locale));
            }

            // get the store for the order.  It will be used to set the currency
            GenericValue productStore = orderHeader.getRelatedOne("ProductStore", false);
            if (productStore == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource,
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
                return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                        "OrderOrderPaymentCannotBeCreatedPayToPartyIdNotSet", locale));
            }

            // create the payment
            Map<String, Object> paymentParams = new HashMap<String, Object>();
            BigDecimal maxAmount = orderPaymentPreference.getBigDecimal("maxAmount");
            //if (maxAmount > 0.0) {
                paymentParams.put("paymentTypeId", "CUSTOMER_PAYMENT");
                paymentParams.put("paymentMethodTypeId", orderPaymentPreference.getString("paymentMethodTypeId"));
                paymentParams.put("paymentPreferenceId", orderPaymentPreference.getString("orderPaymentPreferenceId"));
                paymentParams.put("amount", maxAmount);
                paymentParams.put("statusId", "PMNT_RECEIVED");
                paymentParams.put("effectiveDate", eventDate);
                paymentParams.put("partyIdFrom", paymentFromId);
                paymentParams.put("currencyUomId", productStore.getString("defaultCurrencyUomId"));
                paymentParams.put("partyIdTo", payToPartyId);
            /*}
            else {
                paymentParams.put("paymentTypeId", "CUSTOMER_REFUND"); // JLR 17/7/4 from a suggestion of Si cf. https://issues.apache.org/jira/browse/OFBIZ-828#action_12483045
                paymentParams.put("paymentMethodTypeId", orderPaymentPreference.getString("paymentMethodTypeId")); // JLR 20/7/4 Finally reverted for now, I prefer to see an amount in payment, even negative
                paymentParams.put("paymentPreferenceId", orderPaymentPreference.getString("orderPaymentPreferenceId"));
                paymentParams.put("amount", Double.valueOf(Math.abs(maxAmount)));
                paymentParams.put("statusId", "PMNT_RECEIVED");
                paymentParams.put("effectiveDate", UtilDateTime.nowTimestamp());
                paymentParams.put("partyIdFrom", payToPartyId);
                paymentParams.put("currencyUomId", productStore.getString("defaultCurrencyUomId"));
                paymentParams.put("partyIdTo", billToParty.getString("partyId"));
            }*/
            if (paymentRefNum != null) {
                paymentParams.put("paymentRefNum", paymentRefNum);
            }
            if (comments != null) {
                paymentParams.put("comments", comments);
            }
            paymentParams.put("userLogin", userLogin);

            return dispatcher.runSync("createPayment", paymentParams);

        } catch (GenericEntityException ex) {
            Debug.logError(ex, "Unable to create payment using payment preference.", module);
            return(ServiceUtil.returnError(ex.getMessage()));
        } catch (GenericServiceException ex) {
            Debug.logError(ex, "Unable to create payment using payment preference.", module);
            return(ServiceUtil.returnError(ex.getMessage()));
        }
    }

    public static Map<String, Object> massChangeApproved(DispatchContext dctx, Map<String, ? extends Object> context) {
        return massChangeOrderStatus(dctx, context, "ORDER_APPROVED");
    }

    public static Map<String, Object> massCancelOrders(DispatchContext dctx, Map<String, ? extends Object> context) {
        return massChangeItemStatus(dctx, context, "ITEM_CANCELLED");
    }

    public static Map<String, Object> massRejectOrders(DispatchContext dctx, Map<String, ? extends Object> context) {
        return massChangeItemStatus(dctx, context, "ITEM_REJECTED");
    }

    public static Map<String, Object> massHoldOrders(DispatchContext dctx, Map<String, ? extends Object> context) {
        return massChangeOrderStatus(dctx, context, "ORDER_HOLD");
    }

    public static Map<String, Object> massProcessOrders(DispatchContext dctx, Map<String, ? extends Object> context) {
        return massChangeOrderStatus(dctx, context, "ORDER_PROCESSING");
    }

    public static Map<String, Object> massChangeOrderStatus(DispatchContext dctx, Map<String, ? extends Object> context, String statusId) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List<String> orderIds = UtilGenerics.checkList(context.get("orderIdList"));
        Locale locale = (Locale) context.get("locale");
        for (String orderId : orderIds) {
            if (UtilValidate.isEmpty(orderId)) {
                continue;
            }
            GenericValue orderHeader = null;
            try {
                orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            if (orderHeader == null) {
                return ServiceUtil.returnFailure(UtilProperties.getMessage(resource, 
                        "OrderOrderNotFound", UtilMisc.toMap("orderId", orderId), locale));
            }

            Map<String, Object> ctx = FastMap.newInstance();
            ctx.put("statusId", statusId);
            ctx.put("orderId", orderId);
            ctx.put("setItemStatus", "Y");
            ctx.put("userLogin", userLogin);
            Map<String, Object> resp = null;
            try {
                resp = dispatcher.runSync("changeOrderStatus", ctx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            if (ServiceUtil.isError(resp)) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                        "OrderErrorCouldNotChangeOrderStatus", locale), null, null, resp);
            }
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> massChangeItemStatus(DispatchContext dctx, Map<String, ? extends Object> context, String statusId) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List<String> orderIds = UtilGenerics.checkList(context.get("orderIdList"));
        Locale locale = (Locale) context.get("locale");
        for (String orderId : orderIds) {
            if (UtilValidate.isEmpty(orderId)) {
                continue;
            }
            GenericValue orderHeader = null;
            try {
                orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            if (orderHeader == null) {
                return ServiceUtil.returnFailure(UtilProperties.getMessage(resource, 
                        "OrderOrderNotFound", UtilMisc.toMap("orderId", orderId), locale));
            }

            Map<String, Object> ctx = FastMap.newInstance();
            ctx.put("statusId", statusId);
            ctx.put("orderId", orderId);
            ctx.put("userLogin", userLogin);
            Map<String, Object> resp = null;
            try {
                resp = dispatcher.runSync("changeOrderItemStatus", ctx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            if (ServiceUtil.isError(resp)) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                        "OrderErrorCouldNotChangeItemStatus", locale), null, null, resp);
            }
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> massQuickShipOrders(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List<String> orderIds = UtilGenerics.checkList(context.get("orderIdList"));
        Locale locale = (Locale) context.get("locale");
        for (Object orderId : orderIds) {
            if (UtilValidate.isEmpty(orderId)) {
                continue;
            }
            Map<String, Object> ctx = FastMap.newInstance();
            ctx.put("userLogin", userLogin);
            ctx.put("orderId", orderId);

            Map<String, Object> resp = null;
            try {
                resp = dispatcher.runSync("quickShipEntireOrder", ctx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            if (ServiceUtil.isError(resp)) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                        "OrderOrderQuickShipEntireOrderError", locale), null, null, resp);
            }
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> massPickOrders(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        // grouped by facility
        Map<String, List<String>> facilityOrdersMap = FastMap.newInstance();

        // make the list per facility
        List<String> orderIds = UtilGenerics.checkList(context.get("orderIdList"));
        for (String orderId : orderIds) {
            if (UtilValidate.isEmpty(orderId)) {
                continue;
            }
            List<GenericValue> invInfo = null;
            try {
                invInfo = delegator.findByAnd("OrderItemAndShipGrpInvResAndItem",
                        UtilMisc.toMap("orderId", orderId, "statusId", "ITEM_APPROVED"), null, false);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            if (invInfo != null) {
                for (GenericValue inv : invInfo) {
                    String facilityId = inv.getString("facilityId");
                    List<String> orderIdsByFacility = facilityOrdersMap.get(facilityId);
                    if (orderIdsByFacility == null) {
                        orderIdsByFacility = new ArrayList<String>();
                    }
                    orderIdsByFacility.add(orderId);
                    facilityOrdersMap.put(facilityId, orderIdsByFacility);
                }
            }
        }

        // now create the pick lists for each facility
        for (String facilityId : facilityOrdersMap.keySet()) {
            List<String> orderIdList = facilityOrdersMap.get(facilityId);

            Map<String, Object> ctx = FastMap.newInstance();
            ctx.put("userLogin", userLogin);
            ctx.put("orderIdList", orderIdList);
            ctx.put("facilityId", facilityId);

            Map<String, Object> resp = null;
            try {
                resp = dispatcher.runSync("createPicklistFromOrders", ctx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            if (ServiceUtil.isError(resp)) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                        "OrderOrderPickingListCreationError", locale), null, null, resp);
            }
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> massPrintOrders(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String screenLocation = (String) context.get("screenLocation");
        String printerName = (String) context.get("printerName");

        // make the list per facility
        List<String> orderIds = UtilGenerics.checkList(context.get("orderIdList"));
        for (String orderId : orderIds) {
            if (UtilValidate.isEmpty(orderId)) {
                continue;
            }
            Map<String, Object> ctx = FastMap.newInstance();
            ctx.put("userLogin", userLogin);
            ctx.put("screenLocation", screenLocation);
            //ctx.put("contentType", "application/postscript");
            if (UtilValidate.isNotEmpty(printerName)) {
                ctx.put("printerName", printerName);
            }
            ctx.put("screenContext", UtilMisc.toMap("orderId", orderId));

            try {
                dispatcher.runAsync("sendPrintFromScreen", ctx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
            }
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> massCreateFileForOrders(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String screenLocation = (String) context.get("screenLocation");

        // make the list per facility
        List<String> orderIds = UtilGenerics.checkList(context.get("orderIdList"));
        for (String orderId : orderIds) {
            if (UtilValidate.isEmpty(orderId)) {
                continue;
            }
            Map<String, Object> ctx = FastMap.newInstance();
            ctx.put("userLogin", userLogin);
            ctx.put("screenLocation", screenLocation);
            //ctx.put("contentType", "application/postscript");
            ctx.put("fileName", "order_" + orderId + "_");
            ctx.put("screenContext", UtilMisc.toMap("orderId", orderId));

            try {
                dispatcher.runAsync("createFileFromScreen", ctx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
            }
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> massCancelRemainingPurchaseOrderItems(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List<String> orderIds = UtilGenerics.checkList(context.get("orderIdList"));
        Locale locale = (Locale) context.get("locale");

        for (Object orderId : orderIds) {
            if (UtilValidate.isEmpty(orderId)) {
                continue;
            }
            Map<String, Object> ctx = FastMap.newInstance();
            ctx.put("orderId", orderId);
            ctx.put("userLogin", userLogin);

            Map<String, Object> resp = null;
            try {
                resp = dispatcher.runSync("cancelRemainingPurchaseOrderItems", ctx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            if (ServiceUtil.isError(resp)) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                        "OrderOrderCancelRemainingPurchaseOrderItemsError", locale), null, null, resp);
            }
            try {
                resp = dispatcher.runSync("checkOrderItemStatus", ctx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            if (ServiceUtil.isError(resp)) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                        "OrderOrderCheckOrderItemStatusError", locale), null, null, resp);
            }
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> checkCreateDropShipPurchaseOrders(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        // TODO (use the "system" user)
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        Locale locale = (Locale) context.get("locale");
        OrderReadHelper orh = new OrderReadHelper(delegator, orderId);
        // TODO: skip this if there is already a purchase order associated with the sales order (ship group)

        try {
            // if sales order
            if ("SALES_ORDER".equals(orh.getOrderTypeId())) {
                // get the order's ship groups
                for (GenericValue shipGroup : orh.getOrderItemShipGroups()) {
                    if (!UtilValidate.isEmpty(shipGroup.getString("supplierPartyId"))) {
                        // This ship group is a drop shipment: we create a purchase order for it
                        String supplierPartyId = shipGroup.getString("supplierPartyId");
                        // create the cart
                        ShoppingCart cart = new ShoppingCart(delegator, orh.getProductStoreId(), null, orh.getCurrency());
                        cart.setOrderType("PURCHASE_ORDER");
                        cart.setBillToCustomerPartyId(cart.getBillFromVendorPartyId()); //Company
                        cart.setBillFromVendorPartyId(supplierPartyId);
                        cart.setOrderPartyId(supplierPartyId);
                        // Get the items associated to it and create po
                        List<GenericValue> items = orh.getValidOrderItems(shipGroup.getString("shipGroupSeqId"));
                        if (!UtilValidate.isEmpty(items)) {
                            for (GenericValue item : items) {
                                try {
                                    int itemIndex = cart.addOrIncreaseItem(item.getString("productId"),
                                                                           null, // amount
                                                                           item.getBigDecimal("quantity"),
                                                                           null, null, null, // reserv
                                                                           item.getTimestamp("shipBeforeDate"),
                                                                           item.getTimestamp("shipAfterDate"),
                                                                           null, null, null,
                                                                           null, null, null,
                                                                           null, dispatcher);
                                    ShoppingCartItem sci = cart.findCartItem(itemIndex);
                                    sci.setAssociatedOrderId(orderId);
                                    sci.setAssociatedOrderItemSeqId(item.getString("orderItemSeqId"));
                                    sci.setOrderItemAssocTypeId("DROP_SHIPMENT");
                                } catch (Exception e) {
                                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                                            "OrderOrderCreatingDropShipmentsError", 
                                            UtilMisc.toMap("orderId", orderId, "errorString", e.getMessage()),
                                            locale));
                                }
                            }
                        }

                        // If there are indeed items to drop ship, then create the purchase order
                        if (!UtilValidate.isEmpty(cart.items())) {
                            // set checkout options
                            cart.setDefaultCheckoutOptions(dispatcher);
                            // the shipping address is the one of the customer
                            cart.setAllShippingContactMechId(shipGroup.getString("contactMechId"));
                            // associate ship groups of sales and purchase orders
                            ShoppingCart.CartShipInfo cartShipInfo = cart.getShipGroups().get(0);
                            cartShipInfo.setAssociatedShipGroupSeqId(shipGroup.getString("shipGroupSeqId"));
                            // create the order
                            CheckOutHelper coh = new CheckOutHelper(dispatcher, delegator, cart);
                            coh.createOrder(userLogin);
                        } else {
                            // if there are no items to drop ship, then clear out the supplier partyId
                            Debug.logWarning("No drop ship items found for order [" + shipGroup.getString("orderId") + "] and ship group [" + shipGroup.getString("shipGroupSeqId") + "] and supplier party [" + shipGroup.getString("supplierPartyId") + "].  Supplier party information will be cleared for this ship group", module);
                            shipGroup.set("supplierPartyId", null);
                            shipGroup.store();

                        }
                    }
                }
            }
        } catch (Exception exc) {
            // TODO: imporve error handling
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                    "OrderOrderCreatingDropShipmentsError", 
                    UtilMisc.toMap("orderId", orderId, "errorString", exc.getMessage()),
                    locale));
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> updateOrderPaymentPreference(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        String orderPaymentPreferenceId = (String) context.get("orderPaymentPreferenceId");
        String checkOutPaymentId = (String) context.get("checkOutPaymentId");
        String statusId = (String) context.get("statusId");
        
        try {
            GenericValue opp = delegator.findOne("OrderPaymentPreference", UtilMisc.toMap("orderPaymentPreferenceId", orderPaymentPreferenceId), false);
            String paymentMethodId = null;
            String paymentMethodTypeId = null;

            // The checkOutPaymentId is either a paymentMethodId or paymentMethodTypeId
            // the original method did a "\d+" regexp to decide which is the case, this version is more explicit with its lookup of PaymentMethodType
            if (checkOutPaymentId != null) {
                List<GenericValue> paymentMethodTypes = delegator.findList("PaymentMethodType", null, null, null, null, true);
                for (GenericValue type : paymentMethodTypes) {
                    if (type.get("paymentMethodTypeId").equals(checkOutPaymentId)) {
                        paymentMethodTypeId = (String) type.get("paymentMethodTypeId");
                        break;
                    }
                }
                if (paymentMethodTypeId == null) {
                    GenericValue method = delegator.findOne("PaymentMethod", UtilMisc.toMap("paymentMethodTypeId", paymentMethodTypeId), false);
                    paymentMethodId = checkOutPaymentId;
                    paymentMethodTypeId = (String) method.get("paymentMethodTypeId");
                }
            }

            Map<String, Object> results = ServiceUtil.returnSuccess();
            if (UtilValidate.isNotEmpty(statusId) && statusId.equalsIgnoreCase("PAYMENT_CANCELLED")) {
                opp.set("statusId", "PAYMENT_CANCELLED");
                opp.store();
                results.put("orderPaymentPreferenceId", opp.get("orderPaymentPreferenceId"));
            } else {
                GenericValue newOpp = (GenericValue) opp.clone();
                opp.set("statusId", "PAYMENT_CANCELLED");
                opp.store();

                newOpp.set("orderPaymentPreferenceId", delegator.getNextSeqId("OrderPaymentPreference"));
                newOpp.set("paymentMethodId", paymentMethodId);
                newOpp.set("paymentMethodTypeId", paymentMethodTypeId);
                newOpp.setNonPKFields(context);
                newOpp.create();
                results.put("orderPaymentPreferenceId", newOpp.get("orderPaymentPreferenceId"));
            }

            return results;
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
    }

    /**
     * Generates a product requirement for the total cancelled quantity over all order items for each product
     * @param dctx the dispatch context
     * @param context the context
     * @return the result of the service execution
     */
    public static Map<String, Object> generateReqsFromCancelledPOItems(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String orderId = (String) context.get("orderId");
        String facilityId = (String) context.get("facilityId");

        try {

            GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);

            if (UtilValidate.isEmpty(orderHeader)) {
                String errorMessage = UtilProperties.getMessage(resource_error, 
                        "OrderErrorOrderIdNotFound", UtilMisc.toMap("orderId", orderId), locale);
                Debug.logError(errorMessage, module);
                return ServiceUtil.returnError(errorMessage);
            }

            if (! "PURCHASE_ORDER".equals(orderHeader.getString("orderTypeId"))) {
                String errorMessage = UtilProperties.getMessage(resource_error,
                        "ProductErrorOrderNotPurchaseOrder", UtilMisc.toMap("orderId", orderId), locale);
                Debug.logError(errorMessage, module);
                return ServiceUtil.returnError(errorMessage);
            }

            // Build a map of productId -> quantity cancelled over all order items
            Map<String, Object> productRequirementQuantities = new HashMap<String, Object>();
            List<GenericValue> orderItems = orderHeader.getRelated("OrderItem", null, null, false);
            for (GenericValue orderItem : orderItems) {
            	String itemTypeId = orderItem.getString("orderItemTypeId"); // TODOCHANGE new orderItemType PRODPROMO_ORDER_ITEM
            	String itemTypeParentId = null; // TODOCHANGE new orderItemType PRODPROMO_ORDER_ITEM
                if (!"PRODUCT_ORDER_ITEM".equals(itemTypeId)) {
                    try {
						GenericValue orderItemTypeParentGV = delegator.findOne("OrderItemType", UtilMisc.toMap("orderItemTypeId", itemTypeId), true);
						if (orderItemTypeParentGV != null) itemTypeParentId = orderItemTypeParentGV.getString("parentTypeId");
					} catch (GenericEntityException e) {
						Debug.logError("Problems with find parent order item type : " + itemTypeId, module);
					}
                }
                if (!"PRODUCT_ORDER_ITEM".equals(itemTypeId) && !(itemTypeParentId != null && "PRODUCT_ORDER_ITEM".equals(itemTypeParentId))) continue;

                // Get the cancelled quantity for the item
                BigDecimal orderItemCancelQuantity = BigDecimal.ZERO;
                if (! UtilValidate.isEmpty(orderItem.get("cancelQuantity"))) {
                    orderItemCancelQuantity = orderItem.getBigDecimal("cancelQuantity");
                }

                if (orderItemCancelQuantity.compareTo(BigDecimal.ZERO) <= 0) continue;

                String productId = orderItem.getString("productId");
                if (productRequirementQuantities.containsKey(productId)) {
                    orderItemCancelQuantity = orderItemCancelQuantity.add((BigDecimal) productRequirementQuantities.get(productId));
                }
                productRequirementQuantities.put(productId, orderItemCancelQuantity);

            }

            // Generate requirements for each of the product quantities
            for (String productId : productRequirementQuantities.keySet()) {
                BigDecimal requiredQuantity = (BigDecimal) productRequirementQuantities.get(productId);
                Map<String, Object> createRequirementResult = dispatcher.runSync("createRequirement", UtilMisc.<String, Object>toMap("requirementTypeId", "PRODUCT_REQUIREMENT", "facilityId", facilityId, "productId", productId, "quantity", requiredQuantity, "userLogin", userLogin));
                if (ServiceUtil.isError(createRequirementResult)) return createRequirementResult;
            }

        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } catch (GenericServiceException se) {
            Debug.logError(se, module);
            return ServiceUtil.returnError(se.getMessage());
        }

        return ServiceUtil.returnSuccess();
    }

    /**
     * Cancels remaining (unreceived) quantities for items of an order. Does not consider received-but-rejected quantities.
     * @param dctx the dispatch context
     * @param context the context
     * @return cancels remaining (unreceived) quantities for items of an order
     */
    public static Map<String, Object> cancelRemainingPurchaseOrderItems(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String orderId = (String) context.get("orderId");

        try {

            GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);

            if (UtilValidate.isEmpty(orderHeader)) {
                String errorMessage = UtilProperties.getMessage(resource_error,
                        "OrderErrorOrderIdNotFound", UtilMisc.toMap("orderId", orderId), locale);
                Debug.logError(errorMessage, module);
                return ServiceUtil.returnError(errorMessage);
            }

            if (! "PURCHASE_ORDER".equals(orderHeader.getString("orderTypeId"))) {
                String errorMessage = UtilProperties.getMessage(resource_error, 
                        "OrderErrorOrderNotPurchaseOrder", UtilMisc.toMap("orderId", orderId), locale);
                Debug.logError(errorMessage, module);
                return ServiceUtil.returnError(errorMessage);
            }

            List<GenericValue> orderItems = orderHeader.getRelated("OrderItem", null, null, false);
            for (GenericValue orderItem : orderItems) {
            	String itemTypeId = orderItem.getString("orderItemTypeId"); // TODOCHANGE new orderItemType PRODPROMO_ORDER_ITEM
            	String itemTypeParentId = null; // TODOCHANGE new orderItemType PRODPROMO_ORDER_ITEM
                if (!"PRODUCT_ORDER_ITEM".equals(itemTypeId)) {
                    try {
						GenericValue orderItemTypeParentGV = delegator.findOne("OrderItemType", UtilMisc.toMap("orderItemTypeId", itemTypeId), true);
						if (orderItemTypeParentGV != null) itemTypeParentId = orderItemTypeParentGV.getString("parentTypeId");
					} catch (GenericEntityException e) {
						Debug.logError("Problems with find parent order item type : " + itemTypeId, module);
					}
                }
                if (!"PRODUCT_ORDER_ITEM".equals(itemTypeId) && !(itemTypeParentId != null && "PRODUCT_ORDER_ITEM".equals(itemTypeParentId))) continue;

                // Get the ordered quantity for the item
                BigDecimal orderItemQuantity = BigDecimal.ZERO;
                if (! UtilValidate.isEmpty(orderItem.get("quantity"))) {
                    orderItemQuantity = orderItem.getBigDecimal("quantity");
                }
                BigDecimal orderItemCancelQuantity = BigDecimal.ZERO;
                if (! UtilValidate.isEmpty(orderItem.get("cancelQuantity"))) {
                    orderItemCancelQuantity = orderItem.getBigDecimal("cancelQuantity");
                }

                // Get the received quantity for the order item - ignore the quantityRejected, since rejected items should be reordered
                List<GenericValue> shipmentReceipts = orderItem.getRelated("ShipmentReceipt", null, null, false);
                BigDecimal receivedQuantity = BigDecimal.ZERO;
                for (GenericValue shipmentReceipt : shipmentReceipts) {
                    if (! UtilValidate.isEmpty(shipmentReceipt.get("quantityAccepted"))) {
                        receivedQuantity = receivedQuantity.add(shipmentReceipt.getBigDecimal("quantityAccepted"));
                    }
                }

                BigDecimal quantityToCancel = orderItemQuantity.subtract(orderItemCancelQuantity).subtract(receivedQuantity);
                if (quantityToCancel.compareTo(BigDecimal.ZERO) > 0) {
                Map<String, Object> cancelOrderItemResult = dispatcher.runSync("cancelOrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItem.get("orderItemSeqId"), "cancelQuantity", quantityToCancel, "userLogin", userLogin));
                if (ServiceUtil.isError(cancelOrderItemResult)) return cancelOrderItemResult;
                }

                // If there's nothing to cancel, the item should be set to completed, if it isn't already
                orderItem.refresh();
                if ("ITEM_APPROVED".equals(orderItem.getString("statusId"))) {
                    Map<String, Object> changeOrderItemStatusResult = dispatcher.runSync("changeOrderItemStatus", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItem.get("orderItemSeqId"), "statusId", "ITEM_COMPLETED", "userLogin", userLogin));
                    if (ServiceUtil.isError(changeOrderItemStatusResult)) return changeOrderItemStatusResult;
                }
            }

        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } catch (GenericServiceException se) {
            Debug.logError(se, module);
            return ServiceUtil.returnError(se.getMessage());
        }

        return ServiceUtil.returnSuccess();
    }

    // create simple non-product order
    public static Map<String, Object> createSimpleNonProductSalesOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String paymentMethodId = (String) context.get("paymentMethodId");
        String productStoreId = (String) context.get("productStoreId");
        String currency = (String) context.get("currency");
        String partyId = (String) context.get("partyId");
        Map<String, BigDecimal> itemMap = UtilGenerics.checkMap(context.get("itemMap"));

        ShoppingCart cart = new ShoppingCart(delegator, productStoreId, null, locale, currency);
        try {
            cart.setUserLogin(userLogin, dispatcher);
        } catch (CartItemModifyException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        cart.setOrderType("SALES_ORDER");
        cart.setOrderPartyId(partyId);

        for (String item : itemMap.keySet()) {
            BigDecimal price = itemMap.get(item);
            try {
                cart.addNonProductItem("BULK_ORDER_ITEM", item, null, price, BigDecimal.ONE, null, null, null, dispatcher);
            } catch (CartItemModifyException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
        }

        // set the payment method
        try {
            cart.addPayment(paymentMethodId);
        } catch (IllegalArgumentException e) {
            return ServiceUtil.returnError(e.getMessage());
        }

        // save the order (new tx)
        Map<String, Object> createResp;
        try {
            createResp = dispatcher.runSync("createOrderFromShoppingCart", UtilMisc.toMap("shoppingCart", cart), 90, true);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        if (ServiceUtil.isError(createResp)) {
            return createResp;
        }

        // auth the order (new tx)
        Map<String, Object> authResp;
        try {
            authResp = dispatcher.runSync("callProcessOrderPayments", UtilMisc.toMap("shoppingCart", cart), 180, true);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        if (ServiceUtil.isError(authResp)) {
            return authResp;
        }

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("orderId", createResp.get("orderId"));
        return result;
    }

    // generic method for creating an order from a shopping cart
    public static Map<String, Object> createOrderFromShoppingCart(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();

        ShoppingCart cart = (ShoppingCart) context.get("shoppingCart");
        GenericValue userLogin = cart.getUserLogin();

        CheckOutHelper coh = new CheckOutHelper(dispatcher, delegator, cart);
        Map<String, Object> createOrder = coh.createOrder(userLogin);
        if (ServiceUtil.isError(createOrder)) {
            return createOrder;
        }
        String orderId = (String) createOrder.get("orderId");

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("shoppingCart", cart);
        result.put("orderId", orderId);
        return result;
    }

    // generic method for processing an order's payment(s)
    public static Map<String, Object> callProcessOrderPayments(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");

        Transaction trans = null;
        try {
            // disable transaction procesing
            trans = TransactionUtil.suspend();

            // get the cart
            ShoppingCart cart = (ShoppingCart) context.get("shoppingCart");
            GenericValue userLogin = cart.getUserLogin();
            Boolean manualHold = (Boolean) context.get("manualHold");
            if (manualHold == null) {
                manualHold = Boolean.FALSE;
            }

            if (!"PURCHASE_ORDER".equals(cart.getOrderType())) {
                String productStoreId = cart.getProductStoreId();
                GenericValue productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
                CheckOutHelper coh = new CheckOutHelper(dispatcher, delegator, cart);

                // process payment
                Map<String, Object> payResp;
                try {
                    payResp = coh.processPayment(productStore, userLogin, false, manualHold.booleanValue());
                } catch (GeneralException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(e.getMessage());
                }
                if (ServiceUtil.isError(payResp)) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                            "OrderProcessOrderPayments", locale), null, null, payResp);
                }
            }

            return ServiceUtil.returnSuccess();
        } catch (GenericTransactionException e) {
            return ServiceUtil.returnError(e.getMessage());
        } finally {
            // resume transaction
            try {
                TransactionUtil.resume(trans);
            } catch (GenericTransactionException e) {
                Debug.logWarning(e, e.getMessage(), module);
            }
        }
    }

    /**
     * Determines the total amount invoiced for a given order item over all invoices by totalling the item subtotal (via OrderItemBilling),
     *  any adjustments for that item (via OrderAdjustmentBilling), and the item's share of any order-level adjustments (that calculated
     *  by applying the percentage of the items total that the item represents to the order-level adjustments total (also via
     *  OrderAdjustmentBilling). Also returns the quantity invoiced for the item over all invoices, to aid in prorating.
     * @param dctx DispatchContext
     * @param context Map
     * @return Map
     */
    public static Map<String, Object> getOrderItemInvoicedAmountAndQuantity(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");

        String orderId = (String) context.get("orderId");
        String orderItemSeqId = (String) context.get("orderItemSeqId");

        GenericValue orderHeader = null;
        GenericValue orderItemToCheck = null;
        BigDecimal orderItemTotalValue = ZERO;
        BigDecimal invoicedQuantity = ZERO; // Quantity invoiced for the target order item
        try {

            orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
            if (UtilValidate.isEmpty(orderHeader)) {
                String errorMessage = UtilProperties.getMessage(resource_error, 
                        "OrderErrorOrderIdNotFound", context, locale);
                Debug.logError(errorMessage, module);
                return ServiceUtil.returnError(errorMessage);
            }
            orderItemToCheck = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId), false);
            if (UtilValidate.isEmpty(orderItemToCheck)) {
                String errorMessage = UtilProperties.getMessage(resource_error,
                        "OrderErrorOrderItemNotFound", context, locale);
                Debug.logError(errorMessage, module);
                return ServiceUtil.returnError(errorMessage);
            }

            BigDecimal orderItemsSubtotal = ZERO; // Aggregated value of order items, non-tax and non-shipping item-level adjustments
            BigDecimal invoicedTotal = ZERO; // Amount invoiced for the target order item
            BigDecimal itemAdjustments = ZERO; // Item-level tax- and shipping-adjustments

            // Aggregate the order items subtotal
            List<GenericValue> orderItems = orderHeader.getRelated("OrderItem", null, UtilMisc.toList("orderItemSeqId"), false);
            for (GenericValue orderItem : orderItems) {
                // Look at the orderItemBillings to discover the amount and quantity ever invoiced for this order item
                List<GenericValue> orderItemBillings = delegator.findByAnd("OrderItemBilling", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItem.get("orderItemSeqId")), null, false);
                for (GenericValue orderItemBilling : orderItemBillings) {
                    BigDecimal quantity = orderItemBilling.getBigDecimal("quantity");
                    BigDecimal amount = orderItemBilling.getBigDecimal("amount").setScale(orderDecimals, orderRounding);
                    if (UtilValidate.isEmpty(invoicedQuantity) || UtilValidate.isEmpty(amount)) continue;

                    // Add the item base amount to the subtotal
                    orderItemsSubtotal = orderItemsSubtotal.add(quantity.multiply(amount));

                    // If the item is the target order item, add the invoiced quantity and amount to their respective totals
                    if (orderItemSeqId.equals(orderItem.get("orderItemSeqId"))) {
                        invoicedQuantity = invoicedQuantity.add(quantity);
                        invoicedTotal = invoicedTotal.add(quantity.multiply(amount));
                    }
                }

                // Retrieve the adjustments for this item
                List<GenericValue> orderAdjustments = delegator.findByAnd("OrderAdjustment", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItem.get("orderItemSeqId")), null, false);
                for (GenericValue orderAdjustment : orderAdjustments) {
                    String orderAdjustmentTypeId = orderAdjustment.getString("orderAdjustmentTypeId");

                    // Look at the orderAdjustmentBillings to discove the amount ever invoiced for this order adjustment
                    List<GenericValue> orderAdjustmentBillings = delegator.findByAnd("OrderAdjustmentBilling", UtilMisc.toMap("orderAdjustmentId", orderAdjustment.get("orderAdjustmentId")), null, false);
                    for (GenericValue orderAjustmentBilling : orderAdjustmentBillings) {
                        BigDecimal amount = orderAjustmentBilling.getBigDecimal("amount").setScale(orderDecimals, orderRounding);
                        if (UtilValidate.isEmpty(amount)) continue;

                        if ("SALES_TAX".equals(orderAdjustmentTypeId) || "SHIPPING_CHARGES".equals(orderAdjustmentTypeId)) {
                            if (orderItemSeqId.equals(orderItem.get("orderItemSeqId"))) {

                                // Add tax- and shipping-adjustment amounts to the total adjustments for the target order item
                                itemAdjustments = itemAdjustments.add(amount);
                            }
                        } else {

                            // Add non-tax and non-shipping adjustment amounts to the order items subtotal
                            orderItemsSubtotal = orderItemsSubtotal.add(amount);
                            if (orderItemSeqId.equals(orderItem.get("orderItemSeqId"))) {

                                // If the item is the target order item, add non-tax and non-shipping adjustment amounts to the invoiced total
                                invoicedTotal = invoicedTotal.add(amount);
                            }
                        }
                    }
                }
            }

            // Total the order-header-level adjustments for the order
            BigDecimal orderHeaderAdjustmentsTotalValue = ZERO;
            List<GenericValue> orderHeaderAdjustments = delegator.findByAnd("OrderAdjustment", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", "_NA_"), null, false);
            for (GenericValue orderHeaderAdjustment : orderHeaderAdjustments) {
                List<GenericValue> orderHeaderAdjustmentBillings = delegator.findByAnd("OrderAdjustmentBilling", UtilMisc.toMap("orderAdjustmentId", orderHeaderAdjustment.get("orderAdjustmentId")), null, false);
                for (GenericValue orderHeaderAdjustmentBilling : orderHeaderAdjustmentBillings) {
                    BigDecimal amount = orderHeaderAdjustmentBilling.getBigDecimal("amount").setScale(orderDecimals, orderRounding);
                    if (UtilValidate.isEmpty(amount)) continue;
                    orderHeaderAdjustmentsTotalValue = orderHeaderAdjustmentsTotalValue.add(amount);
                }
            }

            // How much of the order-level adjustments total does the target order item represent? The assumption is: the same
            //  proportion of the adjustments as of the invoiced total for the item to the invoiced total for all items. These
            //  figures don't take tax- and shipping- adjustments into account, so as to be in accordance with the code in InvoiceServices
            BigDecimal invoicedAmountProportion = ZERO;
            if (orderItemsSubtotal.signum() != 0) {
                invoicedAmountProportion = invoicedTotal.divide(orderItemsSubtotal, 5, orderRounding);
            }
            BigDecimal orderItemHeaderAjustmentAmount = orderHeaderAdjustmentsTotalValue.multiply(invoicedAmountProportion);
            orderItemTotalValue = invoicedTotal.add(orderItemHeaderAjustmentAmount);

            // Add back the tax- and shipping- item-level adjustments for the order item
            orderItemTotalValue = orderItemTotalValue.add(itemAdjustments);

        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("invoicedAmount", orderItemTotalValue.setScale(orderDecimals, orderRounding));
        result.put("invoicedQuantity", invoicedQuantity.setScale(orderDecimals, orderRounding));
        return result;
    }

    public static Map<String, Object> setOrderPaymentStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        String orderPaymentPreferenceId = (String) context.get("orderPaymentPreferenceId");
        String changeReason = (String) context.get("changeReason");
        Locale locale = (Locale) context.get("locale");
        try {
            GenericValue orderPaymentPreference = delegator.findOne("OrderPaymentPreference", UtilMisc.toMap("orderPaymentPreferenceId", orderPaymentPreferenceId), false);
            String orderId = orderPaymentPreference.getString("orderId");
            String statusUserLogin = orderPaymentPreference.getString("createdByUserLogin");
            GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
            if (orderHeader == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                        "OrderErrorCouldNotChangeOrderStatusOrderCannotBeFound", locale));
            }
            String statusId = orderPaymentPreference.getString("statusId");
            if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setOrderPaymentStatus] : Setting Order Payment Status to : " + statusId, module);
            // create a order payment status
            GenericValue orderStatus = delegator.makeValue("OrderStatus");
            orderStatus.put("statusId", statusId);
            orderStatus.put("orderId", orderId);
            orderStatus.put("orderPaymentPreferenceId", orderPaymentPreferenceId);
            orderStatus.put("statusUserLogin", statusUserLogin);
            orderStatus.put("changeReason", changeReason);

            // Check that the status has actually changed before creating a new record
            List<GenericValue> previousStatusList = delegator.findByAnd("OrderStatus", UtilMisc.toMap("orderId", orderId, "orderPaymentPreferenceId", orderPaymentPreferenceId), UtilMisc.toList("-statusDatetime"), false);
            GenericValue previousStatus = EntityUtil.getFirst(previousStatusList);
            if (previousStatus != null) {
                // Temporarily set some values on the new status so that we can do an equals() check
                orderStatus.put("orderStatusId", previousStatus.get("orderStatusId"));
                orderStatus.put("statusDatetime", previousStatus.get("statusDatetime"));
                if (orderStatus.equals(previousStatus)) {
                    // Status is the same, return without creating
                    return ServiceUtil.returnSuccess();
                }
            }
            orderStatus.put("orderStatusId", delegator.getNextSeqId("OrderStatus"));
            orderStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
            orderStatus.create();

        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorCouldNotChangeOrderStatus", locale) + e.getMessage() + ").");
        }

        return ServiceUtil.returnSuccess();
    }
    public static Map<String, Object> runSubscriptionAutoReorders(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        int count = 0;
        Map<String, Object> result = null;

        boolean beganTransaction = false;
        try {
            beganTransaction = TransactionUtil.begin();

            List<EntityExpr> exprs = UtilMisc.toList(EntityCondition.makeCondition("automaticExtend", EntityOperator.EQUALS, "Y"),
                    EntityCondition.makeCondition("orderId", EntityOperator.NOT_EQUAL, null),
                    EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, null));
            EntityCondition cond = EntityCondition.makeCondition(exprs, EntityOperator.AND);
            EntityListIterator eli = null;
            eli = delegator.find("Subscription", cond, null, null, null, null);

            if (eli != null) {
                GenericValue subscription;
                while (((subscription = eli.next()) != null)) {

                    Calendar endDate = Calendar.getInstance();
                    endDate.setTime(UtilDateTime.nowTimestamp());
                    //check if the thruedate - cancel period (if provided) is earlier than todays date
                    int field = Calendar.MONTH;
                    if (subscription.get("canclAutmExtTime") != null && subscription.get("canclAutmExtTimeUomId") != null) {
                        if ("TF_day".equals(subscription.getString("canclAutmExtTimeUomId"))) {
                            field = Calendar.DAY_OF_YEAR;
                        } else if ("TF_wk".equals(subscription.getString("canclAutmExtTimeUomId"))) {
                            field = Calendar.WEEK_OF_YEAR;
                        } else if ("TF_mon".equals(subscription.getString("canclAutmExtTimeUomId"))) {
                            field = Calendar.MONTH;
                        } else if ("TF_yr".equals(subscription.getString("canclAutmExtTimeUomId"))) {
                            field = Calendar.YEAR;
                        } else {
                            Debug.logWarning("Don't know anything about useTimeUomId [" + subscription.getString("canclAutmExtTimeUomId") + "], defaulting to month", module);
                        }

                        endDate.add(field, Integer.valueOf(subscription.getString("canclAutmExtTime")).intValue());
                    }

                    Calendar endDateSubscription = Calendar.getInstance();
                    endDateSubscription.setTime(subscription.getTimestamp("thruDate"));

                    if (endDate.before(endDateSubscription)) {
                        // nor expired yet.....
                        continue;
                    }

                    result = dispatcher.runSync("loadCartFromOrder", UtilMisc.toMap("orderId", subscription.get("orderId"), "userLogin", userLogin));
                    ShoppingCart cart = (ShoppingCart) result.get("shoppingCart");
                                        
                    // remove former orderId from cart (would cause duplicate entry).
                    // orderId is set by order-creation services (including store-specific prefixes, e.g.)
                    //cart.setOrderId(null);

                    // only keep the orderitem with the related product.
                    List<ShoppingCartItem> cartItems = cart.items();
                    for (ShoppingCartItem shoppingCartItem : cartItems) {
                        if (!subscription.get("productId").equals(shoppingCartItem.getProductId())) {
                            cart.removeCartItem(shoppingCartItem, dispatcher);
                        }
                    }
                    
                    //cart.getSubTotal()
                    // TODO TRUNGNT Create a payment, not order
                    // Get total and pass to new Invoice
                    GenericValue party = PartyWorker.findParty(delegator, cart.getOrderPartyId());
                    
					result = dispatcher.runSync("createInvoice", UtilMisc
							.toMap("party", party, "invoiceTypeId",
									"SALES_INVOICE", "partyIdFrom",
									cart.getBillFromVendorPartyId(),
									"partyId", cart.getOrderPartyId(),
									"userLogin", userLogin));
					
					String invoiceId = (String) result.get("invoiceId");
                    
					result = dispatcher.runSync("createInvoiceItem", UtilMisc
							.toMap("invoiceId", result.get("invoiceId"),
									"amount", cart.getSubTotal(), "description", "Payment for Subscription in Order " + cart.getOrderId(),
									"quantity", 1,
									"productId", subscription.get("productId"),
									"invoiceItemTypeId", "INV_FDPROD_ITEM",
									"userLogin", userLogin));                    

		            // Should all be in place now. Depending on the ProductStore.autoApproveInvoice setting, set status to INVOICE_READY (unless it's a purchase invoice, which we set to INVOICE_IN_PROCESS)
					Map<String, Object> setInvoiceStatusResult = dispatcher
							.runSync("setInvoiceStatus", UtilMisc
									.<String, Object> toMap("invoiceId",
											invoiceId, "statusId",
											"INVOICE_READY", "userLogin",
											userLogin));
					if (ServiceUtil.isError(setInvoiceStatusResult)) {
						return ServiceUtil
								.returnError(
										UtilProperties
												.getMessage(
														resource,
														"AccountingErrorCreatingInvoiceFromOrder",
														locale), null, null,
										setInvoiceStatusResult);
					}
					
					// Extend thruDate
					
			        Timestamp thruDate = subscription != null ? (Timestamp) subscription.get("thruDate") : null;

			        Integer useTime = Integer.valueOf(subscription.getString("useTime"));
			        String useTimeUomId = (String) subscription.get("useTimeUomId");

			        Calendar calendar = Calendar.getInstance();
			        calendar.setTime(thruDate);
			        int[] times = UomWorker.uomTimeToCalTime(useTimeUomId);
			        if (times != null) {
			            calendar.add(times[0], (useTime.intValue() * times[1]));
			        } else {
			            Debug.logWarning("Don't know anything about useTimeUomId [" + useTimeUomId + "], defaulting to month", module);
			            calendar.add(Calendar.MONTH, useTime);
			        }

			        thruDate = new Timestamp(calendar.getTimeInMillis());
			        subscription.set("thruDate", thruDate);					
                    subscription.store();
                    count++;
					
                    //CheckOutHelper helper = new CheckOutHelper(dispatcher, delegator, cart);

                    /*
                    // store the order
                    Map<String, Object> createResp = helper.createOrder(userLogin);
                    if (createResp != null && ServiceUtil.isError(createResp)) {
                        Debug.logError("Cannot create order for shopping list - " + subscription, module);
                    } else {
                        String orderId = (String) createResp.get("orderId");

                        // authorize the payments
                        Map<String, Object> payRes = null;
                        try {
                            payRes = helper.processPayment(ProductStoreWorker.getProductStore(cart.getProductStoreId(), delegator), userLogin);
                        } catch (GeneralException e) {
                            Debug.logError(e, module);
                        }

                        if (payRes != null && ServiceUtil.isError(payRes)) {
                            Debug.logError("Payment processing problems with shopping list - " + subscription, module);
                        }

                        // remove the automatic extension flag
                        subscription.put("automaticExtend", "N");
                        subscription.store();

                        // send notification
                        dispatcher.runAsync("sendOrderPayRetryNotification", UtilMisc.toMap("orderId", orderId));
                        count++;
                    }
                    */
                }
                eli.close();
            }

        } catch (GenericServiceException e) {
            Debug.logError("Could call service to create cart", module);
            return ServiceUtil.returnError(e.toString());
        } catch (CartItemModifyException e) {
            Debug.logError("Could not modify cart: " + e.toString(), module);
            return ServiceUtil.returnError(e.toString());
        } catch (GenericEntityException e) {
            try {
                // only rollback the transaction if we started one...
                TransactionUtil.rollback(beganTransaction, "Error creating subscription auto-reorders", e);
            } catch (GenericEntityException e2) {
                Debug.logError(e2, "[Delegator] Could not rollback transaction: " + e2.toString(), module);
            }
            Debug.logError(e, "Error while creating new shopping list based automatic reorder" + e.toString(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                    "OrderShoppingListCreationError", UtilMisc.toMap("errorString", e.toString()), locale));
        } finally {
            try {
                // only commit the transaction if we started one... this will throw an exception if it fails
                TransactionUtil.commit(beganTransaction);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Could not commit transaction for creating new shopping list based automatic reorder", module);
            }
        }
        return ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, 
                "OrderRunSubscriptionAutoReorders", UtilMisc.toMap("count", count), locale));
    }

    public static Map<String, Object> setShippingInstructions(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        String orderId = (String) context.get("orderId");
        String shipGroupSeqId = (String) context.get("shipGroupSeqId");
        String shippingInstructions = (String) context.get("shippingInstructions");
        try {
            GenericValue orderItemShipGroup = EntityUtil.getFirst(delegator.findByAnd("OrderItemShipGroup", UtilMisc.toMap("orderId", orderId,"shipGroupSeqId",shipGroupSeqId), null, false));
            orderItemShipGroup.set("shippingInstructions", shippingInstructions);
            orderItemShipGroup.store();
            result.put("shippingInstructions", shippingInstructions);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return result;
    }

    public static Map<String, Object> setGiftMessage(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        String orderId = (String) context.get("orderId");
        String shipGroupSeqId = (String) context.get("shipGroupSeqId");
        String giftMessage = (String) context.get("giftMessage");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        try {
            GenericValue orderItemShipGroup = EntityUtil.getFirst(delegator.findByAnd("OrderItemShipGroup", UtilMisc.toMap("orderId", orderId,"shipGroupSeqId",shipGroupSeqId), null, false));
            orderItemShipGroup.set("giftMessage", giftMessage);
            orderItemShipGroup.set("isGift", "Y");
            orderItemShipGroup.store();
            result.put("giftMessage", giftMessage);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return result;
    }

    public static Map<String, Object> createAlsoBoughtProductAssocs(DispatchContext dctx, Map<String, ? extends Object> context) {
        final Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        // All orders with an entryDate > orderEntryFromDateTime will be processed
        Timestamp orderEntryFromDateTime = (Timestamp) context.get("orderEntryFromDateTime");
        // If true all orders ever created will be processed and any pre-existing ALSO_BOUGHT ProductAssocs will be expired
        boolean processAllOrders = context.get("processAllOrders") == null ? false : (Boolean) context.get("processAllOrders");
        if (orderEntryFromDateTime == null && !processAllOrders) {
            // No from date supplied, check to see when this service last ran and use the startDateTime
            // FIXME: This code is unreliable - the JobSandbox value might have been purged. Use another mechanism to persist orderEntryFromDateTime.
            EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toMap("statusId", "SERVICE_FINISHED", "serviceName", "createAlsoBoughtProductAssocs"));
            EntityFindOptions efo = new EntityFindOptions();
            efo.setMaxRows(1);
            try {
                GenericValue lastRunJobSandbox = EntityUtil.getFirst(delegator.findList("JobSandbox", cond, null, UtilMisc.toList("startDateTime DESC"), efo, false));
                if (lastRunJobSandbox != null) {
                    orderEntryFromDateTime = lastRunJobSandbox.getTimestamp("startDateTime");
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
            if (orderEntryFromDateTime == null) {
                // Still null, process all orders
                processAllOrders = true;
            }
        }
        if (processAllOrders) {
            // Expire any pre-existing ALSO_BOUGHT ProductAssocs in preparation for reprocessing
            EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toList(
                    EntityCondition.makeCondition("productAssocTypeId", "ALSO_BOUGHT"),
                    EntityCondition.makeConditionDate("fromDate", "thruDate")
           ));
            try {
                delegator.storeByCondition("ProductAssoc", UtilMisc.toMap("thruDate", UtilDateTime.nowTimestamp()), cond);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        }
        List<EntityExpr> orderCondList = UtilMisc.toList(EntityCondition.makeCondition("orderTypeId", "SALES_ORDER"));
        if (!processAllOrders && orderEntryFromDateTime != null) {
            orderCondList.add(EntityCondition.makeCondition("entryDate", EntityOperator.GREATER_THAN, orderEntryFromDateTime));
        }
        final EntityCondition cond = EntityCondition.makeCondition(orderCondList);
        List<String> orderIds;
        try {
            orderIds = TransactionUtil.doNewTransaction(new Callable<List<String>>() {
                public List<String> call() throws Exception {
                    List<String> orderIds = new LinkedList<String>();
                    EntityListIterator eli = null;
                    try {
                        eli = delegator.find("OrderHeader", cond, null, UtilMisc.toSet("orderId"), UtilMisc.toList("entryDate ASC"), null);
                        GenericValue orderHeader;
                        while ((orderHeader = eli.next()) != null) {
                            orderIds.add(orderHeader.getString("orderId"));
                        }
                    } finally {
                        if (eli != null) {
                            eli.close();
                        }
                    }
                    return orderIds;
                }
            }, "getSalesOrderIds", 0, true);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        for (String orderId: orderIds) {
            Map<String, Object> svcIn = FastMap.newInstance();
            svcIn.put("userLogin", context.get("userLogin"));
            svcIn.put("orderId", orderId);
            try {
                dispatcher.runSync("createAlsoBoughtProductAssocsForOrder", svcIn);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
            }
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> createAlsoBoughtProductAssocsForOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        String orderId = (String) context.get("orderId");
        OrderReadHelper orh = new OrderReadHelper(delegator, orderId);
        List<GenericValue> orderItems = orh.getOrderItems();
        // In order to improve efficiency a little bit, we will always create the ProductAssoc records
        // with productId < productIdTo when the two are compared.  This way when checking for an existing
        // record we don't have to check both possible combinations of productIds
        TreeSet<String> productIdSet = new TreeSet<String>();
        if (orderItems != null) {
            for (GenericValue orderItem : orderItems) {
                String productId = orderItem.getString("productId");
                if (productId != null) {
                    GenericValue parentProduct = ProductWorker.getParentProduct(productId, delegator);
                    if (parentProduct != null) productId = parentProduct.getString("productId");
                    productIdSet.add(productId);
                }
            }
        }
        TreeSet<String> productIdToSet = new TreeSet<String>(productIdSet);
        for (String productId : productIdSet) {
            productIdToSet.remove(productId);
            for (String productIdTo : productIdToSet) {
                EntityCondition cond = EntityCondition.makeCondition(
                        UtilMisc.toList(
                                EntityCondition.makeCondition("productId", productId),
                                EntityCondition.makeCondition("productIdTo", productIdTo),
                                EntityCondition.makeCondition("productAssocTypeId", "ALSO_BOUGHT"),
                                EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()),
                                EntityCondition.makeCondition("thruDate", null)
                       )
               );
                GenericValue existingProductAssoc = null;
                try {
                    // No point in using the cache because of the filterByDateExpr
                    existingProductAssoc = EntityUtil.getFirst(delegator.findList("ProductAssoc", cond, null, UtilMisc.toList("fromDate DESC"), null, false));
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                }
                try {
                    if (existingProductAssoc != null) {
                        BigDecimal newQuantity = existingProductAssoc.getBigDecimal("quantity");
                        if (newQuantity == null || newQuantity.compareTo(BigDecimal.ZERO) < 0) {
                            newQuantity = BigDecimal.ZERO;
                        }
                        newQuantity = newQuantity.add(BigDecimal.ONE);
                        ModelService updateProductAssoc = dctx.getModelService("updateProductAssoc");
                        Map<String, Object> updateCtx = updateProductAssoc.makeValid(context, ModelService.IN_PARAM, true, null);
                        updateCtx.putAll(updateProductAssoc.makeValid(existingProductAssoc, ModelService.IN_PARAM));
                        updateCtx.put("quantity", newQuantity);
                        dispatcher.runSync("updateProductAssoc", updateCtx);
                    } else {
                        Map<String, Object> createCtx = FastMap.newInstance();
                        createCtx.put("userLogin", context.get("userLogin"));
                        createCtx.put("productId", productId);
                        createCtx.put("productIdTo", productIdTo);
                        createCtx.put("productAssocTypeId", "ALSO_BOUGHT");
                        createCtx.put("fromDate", UtilDateTime.nowTimestamp());
                        createCtx.put("quantity", BigDecimal.ONE);
                        dispatcher.runSync("createProductAssoc", createCtx);
                    }
                } catch (GenericServiceException e) {
                    Debug.logError(e, module);
                }
            }
        }

        return ServiceUtil.returnSuccess();
    }
}
