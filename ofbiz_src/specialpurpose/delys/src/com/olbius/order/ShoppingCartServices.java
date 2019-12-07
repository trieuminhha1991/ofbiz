package com.olbius.order;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.ItemNotFoundException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.product.config.ProductConfigWorker;
import org.ofbiz.product.config.ProductConfigWrapper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public class ShoppingCartServices {
	public static final String module = ShoppingCartServices.class.getName();
	public static final String resourceError = "DelysAdminUiLabels";
	
	private static boolean isTaxAdjustment(GenericValue cartAdj) {
        String adjType = cartAdj.getString("orderAdjustmentTypeId");

        return "SALES_TAX".equals(adjType) || "VAT_TAX".equals(adjType) || "VAT_PRICE_CORRECT".equals(adjType);
    }
	
	// extend from loadCartFromOrder (org.ofbiz.order.shoppingcart.ShoppingCartServices.java)
	public static Map<String, Object> loadCartFromOrderItem(DispatchContext dctx, Map<String, Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        String orderItemSeqId = (String) context.get("orderItemSeqId");
        Boolean skipInventoryChecks = (Boolean) context.get("skipInventoryChecks");
        Boolean skipProductChecks = (Boolean) context.get("skipProductChecks");
        boolean includePromoItems = Boolean.TRUE.equals(context.get("includePromoItems"));
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> result = FastMap.newInstance();
        
        if (UtilValidate.isEmpty(skipInventoryChecks)) {
            skipInventoryChecks = Boolean.FALSE;
        }
        if (UtilValidate.isEmpty(skipProductChecks)) {
            skipProductChecks = Boolean.FALSE;
        }
        
        //get the cart
        ShoppingCart cart = (ShoppingCart) context.get("shoppingCart");
        
        ShoppingCartItem cartItemTemp = cart.findCartItem(orderId, orderItemSeqId);
        if (cartItemTemp != null) {
        	result.put(ModelService.RESPONSE_MESSAGE, UtilProperties.getMessage(resourceError, "DAThisItemIsAlreadyExistsInCart", locale));
        	result.put(ModelService.ERROR_MESSAGE, UtilProperties.getMessage(resourceError, "DAThisItemIsAlreadyExistsInCart", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId), locale));
            result.put("shoppingCart", cart);
            return result;
//        	return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "DAThisItemIsAlreadyExistsInCart", locale));
        }
        
        // get the order header
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        
        // initial require cart info
        OrderReadHelper orh = new OrderReadHelper(orderHeader); //productStoreId, prodCatalogId, website, currency
        String productStoreId = cart.getProductStoreId();
//        String orderTypeId = orh.getOrderTypeId();
        String currency = cart.getCurrency();
        String website = cart.getWebSiteId();
//        String currentStatusString = orh.getCurrentStatusString();

        
        // create the cart
        // set the order name
        // set the role information
        // load order attributes
        // load the payment infos
        
        GenericValue orderItem = null;
		try {
			orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId), false);
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        long nextItemSeq = 0;
        if (UtilValidate.isNotEmpty(orderItem)) {
        	String productId = orderItem.getString("productId");
        	GenericValue product = null;
        	 Map<String, Object> surveyResponseResult = null;
             try {
                 long seq = Long.parseLong(orderItemSeqId);
                 if (seq > nextItemSeq) {
                     nextItemSeq = seq;
                 }
             } catch (NumberFormatException e) {
                 Debug.logError(e, module);
                 return ServiceUtil.returnError(e.getMessage());
             }
        	if (!("ITEM_REJECTED".equals(orderItem.getString("statusId")) || "ITEM_CANCELLED".equals(orderItem.getString("statusId")))) {
        		try {
                    product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
                    if ("DIGITAL_GOOD".equals(product.getString("productTypeId"))) {
                        Map<String, Object> surveyResponseMap = FastMap.newInstance();
                        Map<String, Object> answers = FastMap.newInstance();
                        List<GenericValue> surveyResponseAndAnswers = delegator.findByAnd("SurveyResponseAndAnswer", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId), null, false);
                        if (UtilValidate.isNotEmpty(surveyResponseAndAnswers)) {
                            String surveyId = EntityUtil.getFirst(surveyResponseAndAnswers).getString("surveyId");
                            for (GenericValue surveyResponseAndAnswer : surveyResponseAndAnswers) {
                                answers.put((surveyResponseAndAnswer.get("surveyQuestionId").toString()), surveyResponseAndAnswer.get("textResponse"));
                            }
                            surveyResponseMap.put("answers", answers);
                            surveyResponseMap.put("surveyId", surveyId);
                            surveyResponseResult = dispatcher.runSync("createSurveyResponse", surveyResponseMap);
                        }
                    }
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(e.getMessage());
                } catch (GenericServiceException e) {
                    Debug.logError(e.toString(), module);
                    return ServiceUtil.returnError(e.toString());
                }
            	
            	// do not include PROMO items
                if (!(!includePromoItems && orderItem.get("isPromo") != null && "Y".equals(orderItem.getString("isPromo")))) {
                	// not a promo item; go ahead and add it in
                    BigDecimal amount = orderItem.getBigDecimal("selectedAmount");
                    if (amount == null) {
                        amount = BigDecimal.ZERO;
                    }
                    BigDecimal quantity = orderItem.getBigDecimal("quantity");
                    if (quantity == null) {
                        quantity = BigDecimal.ZERO;
                    }

                    BigDecimal unitPrice = null;
                    if ("Y".equals(orderItem.getString("isModifiedPrice"))) {
                        unitPrice = orderItem.getBigDecimal("unitPrice");
                    }
                	
                    int itemIndex = -1;
                    if (orderItem.get("productId") == null) {
                        // non-product item
                        String itemType = orderItem.getString("orderItemTypeId");
                        String desc = orderItem.getString("itemDescription");
                        try {
                            // TODO: passing in null now for itemGroupNumber, but should reproduce from OrderItemGroup records
                            itemIndex = cart.addNonProductItem(itemType, desc, null, unitPrice, quantity, null, null, null, dispatcher);
                        } catch (CartItemModifyException e) {
                            Debug.logError(e, module);
                            return ServiceUtil.returnError(e.getMessage());
                        }
                    } else {
                    	// product item
                        String prodCatalogId = orderItem.getString("prodCatalogId");

                        //prepare the rental data
                        Timestamp reservStart = null;
                        BigDecimal reservLength = null;
                        BigDecimal reservPersons = null;
                        String accommodationMapId = null;
                        String accommodationSpotId = null;

                        GenericValue workEffort = null;
                        String workEffortId = orh.getCurrentOrderItemWorkEffort(orderItem);
                        if (workEffortId != null) {
                            try {
                                workEffort = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId), false);
                            } catch (GenericEntityException e) {
                                Debug.logError(e, module);
                            }
                        }
                        if (workEffort != null && "ASSET_USAGE".equals(workEffort.getString("workEffortTypeId"))) {
                            reservStart = workEffort.getTimestamp("estimatedStartDate");
                            reservLength = OrderReadHelper.getWorkEffortRentalLength(workEffort);
                            reservPersons = workEffort.getBigDecimal("reservPersons");
                            accommodationMapId = workEffort.getString("accommodationMapId");
                            accommodationSpotId = workEffort.getString("accommodationSpotId");

                        }    //end of rental data

                        //check for AGGREGATED products
                        ProductConfigWrapper configWrapper = null;
                        String configId = null;
                        try {
                            product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
                            if (EntityTypeUtil.hasParentType(delegator, "ProductType", "productTypeId", product.getString("productTypeId"), "parentTypeId", "AGGREGATED")) {
                                List<GenericValue>productAssocs = delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productAssocTypeId", "PRODUCT_CONF", "productIdTo", product.getString("productId")), null, false);
                                productAssocs = EntityUtil.filterByDate(productAssocs);
                                if (UtilValidate.isNotEmpty(productAssocs)) {
                                    productId = EntityUtil.getFirst(productAssocs).getString("productId");
                                    configId = product.getString("configId");
                                }
                            }
                        } catch (GenericEntityException e) {
                            Debug.logError(e, module);
                        }

                        if (UtilValidate.isNotEmpty(configId)) {
                            configWrapper = ProductConfigWorker.loadProductConfigWrapper(delegator, dispatcher, configId, productId, productStoreId, prodCatalogId, website, currency, locale, userLogin);
                        }
                        try {
                            itemIndex = cart.addItemToEnd(productId, amount, quantity, unitPrice, reservStart, reservLength, reservPersons,accommodationMapId,accommodationSpotId, null, null, prodCatalogId, configWrapper, orderItem.getString("orderItemTypeId"), dispatcher, false, unitPrice == null ? null : false, skipInventoryChecks, skipProductChecks);
                        } catch (ItemNotFoundException e) {
                            Debug.logError(e, module);
                            return ServiceUtil.returnError(e.getMessage());
                        } catch (CartItemModifyException e) {
                            Debug.logError(e, module);
                            return ServiceUtil.returnError(e.getMessage());
                        }
                    }
                    
                    // flag the item w/ the orderItemSeqId so we can reference it
                    ShoppingCartItem cartItem = cart.findCartItem(itemIndex);
                    cartItem.setIsPromo(orderItem.get("isPromo") != null && "Y".equals(orderItem.getString("isPromo")));
//                    cartItem.setOrderItemSeqId(orderItem.getString("orderItemSeqId"));
                    cartItem.setAssociatedOrderId(orderId);
                    cartItem.setAssociatedOrderItemSeqId(orderItemSeqId);
                    
                    try {
                        cartItem.setItemGroup(cart.addItemGroup(orderItem.getRelatedOne("OrderItemGroup", true)));
                    } catch (GenericEntityException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError(e.getMessage());
                    }
                    // attach surveyResponseId for each item
                    if (UtilValidate.isNotEmpty(surveyResponseResult)){
                        cartItem.setAttribute("surveyResponseId",surveyResponseResult.get("surveyResponseId"));
                    }
                    // attach addition item information
                    cartItem.setStatusId(orderItem.getString("statusId"));
                    cartItem.setItemType(orderItem.getString("orderItemTypeId"));
                    cartItem.setItemComment(orderItem.getString("comments"));
                    cartItem.setQuoteId(orderItem.getString("quoteId"));
                    cartItem.setQuoteItemSeqId(orderItem.getString("quoteItemSeqId"));
                    cartItem.setProductCategoryId(orderItem.getString("productCategoryId"));
                    cartItem.setDesiredDeliveryDate(orderItem.getTimestamp("estimatedDeliveryDate"));
                    cartItem.setShipBeforeDate(orderItem.getTimestamp("shipBeforeDate"));
                    cartItem.setShipAfterDate(orderItem.getTimestamp("shipAfterDate"));
                    cartItem.setShoppingList(orderItem.getString("shoppingListId"), orderItem.getString("shoppingListItemSeqId"));
                    cartItem.setIsModifiedPrice("Y".equals(orderItem.getString("isModifiedPrice")));
                    cartItem.setName(orderItem.getString("itemDescription"));
                    cartItem.setExternalId(orderItem.getString("externalId"));

                    // load order item attributes
                    List<GenericValue> orderItemAttributesList = null;
                    try {
                        orderItemAttributesList = delegator.findByAnd("OrderItemAttribute", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId), null, false);
                        if (UtilValidate.isNotEmpty(orderItemAttributesList)) {
                            for (GenericValue orderItemAttr : orderItemAttributesList) {
                                String name = orderItemAttr.getString("attrName");
                                String value = orderItemAttr.getString("attrValue");
                                cartItem.setOrderItemAttribute(name, value);
                            }
                        }
                    } catch (GenericEntityException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError(e.getMessage());
                    }

                    // load order item contact mechs
                    List<GenericValue> orderItemContactMechList = null;
                    try {
                        orderItemContactMechList = delegator.findByAnd("OrderItemContactMech", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId), null, false);
                        if (UtilValidate.isNotEmpty(orderItemContactMechList)) {
                            for (GenericValue orderItemContactMech : orderItemContactMechList) {
                                String contactMechPurposeTypeId = orderItemContactMech.getString("contactMechPurposeTypeId");
                                String contactMechId = orderItemContactMech.getString("contactMechId");
                                cartItem.addContactMech(contactMechPurposeTypeId, contactMechId);
                            }
                        }
                    } catch (GenericEntityException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError(e.getMessage());
                    }

                    // set the PO number on the cart
                    cart.setPoNumber(orderItem.getString("correspondingPoId"));

                    // get all item adjustments EXCEPT tax adjustments
                    List<GenericValue> itemAdjustments = orh.getOrderItemAdjustments(orderItem);
                    if (itemAdjustments != null) {
                        for (GenericValue itemAdjustment : itemAdjustments) {
                            if (!isTaxAdjustment(itemAdjustment)) cartItem.addAdjustment(itemAdjustment);
                        }
                    }
                }
        	}
            
            // setup the OrderItemShipGroupAssoc records
        	// set the item seq in the cart
//            if (nextItemSeq > 0) {
//                try {
//                    cart.setNextItemSeq(nextItemSeq+1);
//                } catch (GeneralException e) {
//                    Debug.logError(e, module);
//                    return ServiceUtil.returnError(e.getMessage());
//                }
//            }
        }
//        if (includePromoItems) {
//            for (String productPromoCode: orh.getProductPromoCodesEntered()) {
//                cart.addProductPromoCode(productPromoCode, dispatcher);
//            }
//            for (GenericValue productPromoUse: orh.getProductPromoUse()) {
//                cart.addProductPromoUse(productPromoUse.getString("productPromoId"), productPromoUse.getString("productPromoCodeId"), productPromoUse.getBigDecimal("totalDiscountAmount"), productPromoUse.getBigDecimal("quantityLeftInActions"));
//            }
//        }

//        List<GenericValue> adjustments = orh.getOrderHeaderAdjustments();
        // If applyQuoteAdjustments is set to false then standard cart adjustments are used.
//        if (!adjustments.isEmpty()) {
//            // The cart adjustments are added to the cart
//            cart.getAdjustments().addAll(adjustments);
//        }

        result = ServiceUtil.returnSuccess();
        result.put("shoppingCart", cart);
        return result;
    }
}
