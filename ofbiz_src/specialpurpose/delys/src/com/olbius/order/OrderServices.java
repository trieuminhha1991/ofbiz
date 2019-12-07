package com.olbius.order;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
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
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.order.shoppingcart.shipping.ShippingEvents;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.ProductUtil;
import com.olbius.util.SalesPartyUtil;

public class OrderServices {
	
	public static final String module = OrderServices.class.getName();
	public static final String resource = "OrderUiLabels";
    public static final String resource_error = "OrderErrorUiLabels";
    public static final String resource_module = "DelysAdminUiLabels";
    public static final String resource_module_error = "DelysAdminErrorUiLabels";
    public static final String resource_product = "ProductUiLabels";
    
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

    
	/** Service for creating a new delivery requirement */
	public static Map<String, Object> createDeliveryRequirement(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
//		LocalDispatcher dispatcher = ctx.getDispatcher();
//		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> toBeStored = new LinkedList<GenericValue>();
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		// get the requirement type
		String requirementTypeId = (String) context.get("requirementTypeId");
		String description = (String) context.get("description");
		Timestamp requirementStartDate = (Timestamp) context.get("requirementStartDate");
		Timestamp requiredByDate = (Timestamp) context.get("requiredByDate");
		String statusId = (String) context.get("statusId");
		String requirementId = (String) context.get("requirementId");
		
		//deliveryReqItems
		
		// check security permissions for requirement
		// ...
		
		successResult.put("requirementTypeId", requirementTypeId);
		// lookup the requirement type entity
		GenericValue requirementType = null;
		try {
			requirementType = delegator.findOne("RequirementType", UtilMisc.toMap("requirementTypeId", requirementTypeId), true);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_module_error, "DAErrorRequirementTypeLookupFailed", locale) + e.toString());
		}
		
		// make sure we have a valid requirement type
		if (requirementType == null) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_module_error, 
					"DAErrorInvalidRequirementTypeWithID", UtilMisc.toMap("requirementTypeId", requirementTypeId), locale));
		}
		
		// check to make sure we have something to requirement
		List<GenericValue> deliveryReqItems = UtilGenerics.checkList(context.get("deliveryReqItems"));
		if (deliveryReqItems.size() < 1) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_module_error, "DAItemsNone", locale));
		}
		
		List<String> errorMessages = FastList.newInstance();
		for (GenericValue deliveryReqItem : deliveryReqItems) {
			String currentOrderId = deliveryReqItem.getString("orderId");
			
			if (currentOrderId != null) {
				try {
					GenericValue currentOrder = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", currentOrderId), true);
					if (UtilValidate.isEmpty(currentOrder)) {
						String errMsg = UtilProperties.getMessage(resource_module_error, "DAProductNotFound", new Object[] { currentOrderId }, locale);
			            errorMessages.add(errMsg);
			            continue;
					}
		        } catch (GenericEntityException e) {
		            String errMsg = UtilProperties.getMessage(resource_module_error, "DAProductNotFound", new Object[] { currentOrderId }, locale);
		            Debug.logError(e, errMsg, module);
		            errorMessages.add(errMsg);
		            continue;
		        }
			}
		}
		
		if (errorMessages.size() > 0) {
	        return ServiceUtil.returnError(errorMessages);
	    }
		
		// the inital status for ALL order types
	    String initialStatus = "DVER_REQ_CREATED";
	    successResult.put("statusId", initialStatus);
		
	    if (UtilValidate.isEmpty(requirementId)) {
	    	requirementId = delegator.getNextSeqId("Requirement");
	    }
	    
	    if (requiredByDate == null) {
	    	requiredByDate = UtilDateTime.nowTimestamp();
	    }
	    
	    Map<String, Object> requirementMap = UtilMisc.<String, Object>toMap("requirementId", requirementId, 
	    		"requirementTypeId", requirementTypeId, "description", description, "statusId", statusId, 
	    		"requirementStartDate", requirementStartDate, "requiredByDate", requiredByDate, "createdByUserLogin", userLogin.get("userLoginId"));
	    GenericValue requirement = delegator.makeValue("Requirement", requirementMap);
	    
	    try {
			delegator.create(requirement);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_module_error, "DADeliveryRequirementCreationFailedPleaseNotifyCustomerService", locale));
		}
	    
	    // set the order items
	    for (GenericValue deliveryReqItem : deliveryReqItems) {
	    	deliveryReqItem.set("requirementId", requirementId);
	    	toBeStored.add(deliveryReqItem);
	    }
	    
	    try {
	    	// store line items, etc so that they will be there for the foreign key checks
			delegator.storeAll(toBeStored);
//			List<String> resErrorMessages = new LinkedList<String>();
//			if (UtilValidate.isNotEmpty(deliveryReqItems)) {
//				for (GenericValue deliveryItem : deliveryReqItems) {
//					String orderId = (String) deliveryItem.get("orderId");
//					GenericValue order = delegator.getRelatedOne("OrderHeader", deliveryItem, false);
//					
//					if (order != null) {
//						
//					}
//				}
//			}
			
			successResult.put("requirementId", requirementId);
			
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_module_error,
	                "DAErrorCouldNotCreateRequirementWriteError",locale) + e.getMessage() + ").");
		}
	    
		return successResult;
	}

    private static boolean hasPermission(String orderTypeId, String partyId, GenericValue userLogin, String action, Security security) {
        boolean hasPermission = security.hasEntityPermission("ORDERMGR", "_" + action, userLogin);
        if (!hasPermission && orderTypeId.equals("SALES_ORDER")) hasPermission = security.hasEntityPermission("ORDERSAL", "_DIS_" + action, userLogin);
        if (!hasPermission && orderTypeId.equals("PURCHASE_ORDER")) hasPermission = security.hasEntityPermission("ORDERPUR", "_DIS_" + action, userLogin);
        if (!hasPermission) {
            if (orderTypeId.equals("SALES_ORDER")) {
                if (security.hasEntityPermission("ORDERMGR", "_SALES_" + action, userLogin)) {
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
    
    private static boolean hasPermission(String orderTypeId, String partyId, GenericValue userLogin, String action, Security security, boolean reserveInventory) {
        boolean hasPermission = security.hasEntityPermission("ORDERMGR", "_" + action, userLogin);
        if (!hasPermission && orderTypeId.equals("SALES_ORDER")) hasPermission = security.hasEntityPermission("ORDERSAL", "_DIS_" + action, userLogin);
        if (!hasPermission && (orderTypeId.equals("PURCHASE_ORDER") || !reserveInventory)) hasPermission = security.hasEntityPermission("ORDERPUR", "_DIS_" + action, userLogin);
        if (!hasPermission) {
            if (orderTypeId.equals("SALES_ORDER")) {
                if (security.hasEntityPermission("ORDERMGR", "_SALES_" + action, userLogin)) {
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
	
	// TODOCHANGE change createOrder for order promotion
	/** Service for creating a new order */
	public static Map<String, Object> createOrder(DispatchContext ctx, Map<String, ? extends Object> context) {
        return createOrderCore(ctx, context, true);
    }
	
	// TODOCHANGE createOrder for distributor
 	/** Service for creating a new order */
	public static Map<String, Object> createOrderDis(DispatchContext ctx, Map<String, ? extends Object> context) {
		String notCreateInventoryItem = (String) context.get("notCreateInventoryItem");
		boolean reserveInventory = true;
		if ("Y".equals(notCreateInventoryItem)) {
			reserveInventory = false;
		}
		return createOrderCore(ctx, context, reserveInventory);
	}
    
    public static void reserveInventory(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, Locale locale, List<GenericValue> orderItemShipGroupInfo, List<String> dropShipGroupIds, Map<String, GenericValue> itemValuesBySeqId, String orderTypeId, String productStoreId, List<String> resErrorMessages) throws GeneralException {
        boolean isImmediatelyFulfilled = false;
        GenericValue productStore = null;
        if (UtilValidate.isNotEmpty(productStoreId)) {
            try {
                productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), true);
            } catch (GenericEntityException e) {
                throw new GeneralException(UtilProperties.getMessage(resource_error, 
                        "OrderErrorCouldNotFindProductStoreWithID", 
                        UtilMisc.toMap("productStoreId", productStoreId), locale) + e.toString());
            }
        }
        if (productStore != null) {
            isImmediatelyFulfilled = "Y".equals(productStore.getString("isImmediatelyFulfilled"));
        }

        boolean reserveInventory = ("SALES_ORDER".equals(orderTypeId));
        if (reserveInventory && isImmediatelyFulfilled) {
            // don't reserve inventory if the product store has isImmediatelyFulfilled set, ie don't if in this store things are immediately fulfilled
            reserveInventory = false;
        }

        // START inventory reservation
        // decrement inventory available for each OrderItemShipGroupAssoc, within the same transaction
        if (UtilValidate.isNotEmpty(orderItemShipGroupInfo)) {
            for (GenericValue orderItemShipGroupAssoc : orderItemShipGroupInfo) {
                if ("OrderItemShipGroupAssoc".equals(orderItemShipGroupAssoc.getEntityName())) {
                    if (dropShipGroupIds != null && dropShipGroupIds.contains(orderItemShipGroupAssoc.getString("shipGroupSeqId"))) {
                        // the items in the drop ship groups are not reserved
                        continue;
                    }
                    GenericValue orderItem = itemValuesBySeqId.get(orderItemShipGroupAssoc.get("orderItemSeqId"));
                    GenericValue orderItemShipGroup = orderItemShipGroupAssoc.getRelatedOne("OrderItemShipGroup", false);
                    String shipGroupFacilityId = orderItemShipGroup.getString("facilityId");
                    String itemStatus = orderItem.getString("statusId");
                    if ("ITEM_REJECTED".equals(itemStatus) || "ITEM_CANCELLED".equals(itemStatus) || "ITEM_COMPLETED".equals(itemStatus)) {
                        Debug.logInfo("Order item [" + orderItem.getString("orderId") + " / " + orderItem.getString("orderItemSeqId") + "] is not in a proper status for reservation", module);
                        continue;
                    }
                    if (UtilValidate.isNotEmpty(orderItem.getString("productId")) &&   // only reserve product items, ignore non-product items
                            !"RENTAL_ORDER_ITEM".equals(orderItem.getString("orderItemTypeId"))) {  // ignore for rental
                        try {
                            // get the product of the order item
                            GenericValue product = orderItem.getRelatedOne("Product", false);
                            if (product == null) {
                                Debug.logError("Error when looking up product in reserveInventory service", module);
                                resErrorMessages.add("Error when looking up product in reserveInventory service");
                                continue;
                            }
                            if (reserveInventory) {
                                // for MARKETING_PKG_PICK reserve the components
                                if (EntityTypeUtil.hasParentType(delegator, "ProductType", "productTypeId", product.getString("productTypeId"), "parentTypeId", "MARKETING_PKG_PICK")) {
                                    Map<String, Object> componentsRes = dispatcher.runSync("getAssociatedProducts", UtilMisc.toMap("productId", orderItem.getString("productId"), "type", "PRODUCT_COMPONENT"));
                                    if (ServiceUtil.isError(componentsRes)) {
                                        resErrorMessages.add((String)componentsRes.get(ModelService.ERROR_MESSAGE));
                                        continue;
                                    } else {
                                        List<GenericValue> assocProducts = UtilGenerics.checkList(componentsRes.get("assocProducts"));
                                        for (GenericValue productAssoc : assocProducts) {
                                            BigDecimal quantityOrd = productAssoc.getBigDecimal("quantity");
                                            BigDecimal quantityKit = orderItemShipGroupAssoc.getBigDecimal("quantity");
                                            BigDecimal quantity = quantityOrd.multiply(quantityKit);
                                            Map<String, Object> reserveInput = new HashMap<String, Object>();
                                            reserveInput.put("productStoreId", productStoreId);
                                            reserveInput.put("productId", productAssoc.getString("productIdTo"));
                                            reserveInput.put("orderId", orderItem.getString("orderId"));
                                            reserveInput.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
                                            reserveInput.put("shipGroupSeqId", orderItemShipGroupAssoc.getString("shipGroupSeqId"));
                                            reserveInput.put("quantity", quantity);
                                            reserveInput.put("userLogin", userLogin);
                                            reserveInput.put("facilityId", shipGroupFacilityId);
                                            Map<String, Object> reserveResult = dispatcher.runSync("reserveStoreInventory", reserveInput);

                                            if (ServiceUtil.isError(reserveResult)) {
                                                String invErrMsg = "The product ";
                                                if (product != null) {
                                                    invErrMsg += getProductName(product, orderItem);
                                                }
                                                invErrMsg += " with ID " + orderItem.getString("productId") + " is no longer in stock. Please try reducing the quantity or removing the product from this order.";
                                                resErrorMessages.add(invErrMsg);
                                            }
                                        }
                                    }
                                } else {
                                    // reserve the product
                                    Map<String, Object> reserveInput = new HashMap<String, Object>();
                                    reserveInput.put("productStoreId", productStoreId);
                                    reserveInput.put("productId", orderItem.getString("productId"));
                                    reserveInput.put("orderId", orderItem.getString("orderId"));
                                    reserveInput.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
                                    reserveInput.put("shipGroupSeqId", orderItemShipGroupAssoc.getString("shipGroupSeqId"));
                                    reserveInput.put("facilityId", shipGroupFacilityId);
                                    // use the quantity from the orderItemShipGroupAssoc, NOT the orderItem, these are reserved by item-group assoc
                                    reserveInput.put("quantity", orderItemShipGroupAssoc.getBigDecimal("quantity"));
                                    reserveInput.put("userLogin", userLogin);
                                    reserveInput.put("expireDate", orderItem.getTimestamp("expireDate")); //TODOCHANGE expireDate
                                    Map<String, Object> reserveResult = dispatcher.runSync("reserveStoreInventory", reserveInput);

                                    if (ServiceUtil.isError(reserveResult)) {
                                        String invErrMsg = "The product ";
                                        if (product != null) {
                                            invErrMsg += getProductName(product, orderItem);
                                        }
                                        invErrMsg += " with ID " + orderItem.getString("productId") + " is no longer in stock. Please try reducing the quantity or removing the product from this order.";
                                        resErrorMessages.add(invErrMsg);
                                    }
                                }
                            }
                            // Reserving inventory or not we still need to create a marketing package
                            // If the product is a marketing package auto, attempt to create enough packages to bring ATP back to 0, won't necessarily create enough to cover this order.
                            if (EntityTypeUtil.hasParentType(delegator, "ProductType", "productTypeId", product.getString("productTypeId"), "parentTypeId", "MARKETING_PKG_AUTO")) {
                                // do something tricky here: run as the "system" user
                                // that can actually create and run a production run
                                GenericValue permUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), true);
                                Map<String, Object> inputMap = new HashMap<String, Object>();
                                if (UtilValidate.isNotEmpty(shipGroupFacilityId)) {
                                    inputMap.put("facilityId", shipGroupFacilityId);
                                } else {
                                    inputMap.put("facilityId", productStore.getString("inventoryFacilityId"));
                                }
                                inputMap.put("orderId", orderItem.getString("orderId"));
                                inputMap.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
                                inputMap.put("userLogin", permUserLogin);
                                Map<String, Object> prunResult = dispatcher.runSync("createProductionRunForMktgPkg", inputMap);
                                if (ServiceUtil.isError(prunResult)) {
                                    Debug.logError(ServiceUtil.getErrorMessage(prunResult) + " for input:" + inputMap, module);
                                }
                            }
                        } catch (GenericServiceException e) {
                            String errMsg = "Fatal error calling reserveStoreInventory service: " + e.toString();
                            Debug.logError(e, errMsg, module);
                            resErrorMessages.add(errMsg);
                        }
                    }
                    
                    // rent item
                    if (UtilValidate.isNotEmpty(orderItem.getString("productId")) && "RENTAL_ORDER_ITEM".equals(orderItem.getString("orderItemTypeId"))) {
                        try {
                            // get the product of the order item
                            GenericValue product = orderItem.getRelatedOne("Product", false);
                            if (product == null) {
                                Debug.logError("Error when looking up product in reserveInventory service", module);
                                resErrorMessages.add("Error when looking up product in reserveInventory service");
                                continue;
                            }
                            
                            // check product type for rent
                            String productType = (String) product.get("productTypeId");
                            if ("ASSET_USAGE_OUT_IN".equals(productType)) {
                                if (reserveInventory) {
                                    // for MARKETING_PKG_PICK reserve the components
                                    if (EntityTypeUtil.hasParentType(delegator, "ProductType", "productTypeId", product.getString("productTypeId"), "parentTypeId", "MARKETING_PKG_PICK")) {
                                        Map<String, Object> componentsRes = dispatcher.runSync("getAssociatedProducts", UtilMisc.toMap("productId", orderItem.getString("productId"), "type", "PRODUCT_COMPONENT"));
                                        if (ServiceUtil.isError(componentsRes)) {
                                            resErrorMessages.add((String)componentsRes.get(ModelService.ERROR_MESSAGE));
                                            continue;
                                        } else {
                                            List<GenericValue> assocProducts = UtilGenerics.checkList(componentsRes.get("assocProducts"));
                                            for (GenericValue productAssoc : assocProducts) {
                                                BigDecimal quantityOrd = productAssoc.getBigDecimal("quantity");
                                                BigDecimal quantityKit = orderItemShipGroupAssoc.getBigDecimal("quantity");
                                                BigDecimal quantity = quantityOrd.multiply(quantityKit);
                                                Map<String, Object> reserveInput = new HashMap<String, Object>();
                                                reserveInput.put("productStoreId", productStoreId);
                                                reserveInput.put("productId", productAssoc.getString("productIdTo"));
                                                reserveInput.put("orderId", orderItem.getString("orderId"));
                                                reserveInput.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
                                                reserveInput.put("shipGroupSeqId", orderItemShipGroupAssoc.getString("shipGroupSeqId"));
                                                reserveInput.put("quantity", quantity);
                                                reserveInput.put("userLogin", userLogin);
                                                reserveInput.put("facilityId", shipGroupFacilityId);
                                                Map<String, Object> reserveResult = dispatcher.runSync("reserveStoreInventory", reserveInput);
    
                                                if (ServiceUtil.isError(reserveResult)) {
                                                    String invErrMsg = "The product ";
                                                    if (product != null) {
                                                        invErrMsg += getProductName(product, orderItem);
                                                    }
                                                    invErrMsg += " with ID " + orderItem.getString("productId") + " is no longer in stock. Please try reducing the quantity or removing the product from this order.";
                                                    resErrorMessages.add(invErrMsg);
                                                }
                                            }
                                        }
                                    } else {
                                        // reserve the product
                                        Map<String, Object> reserveInput = new HashMap<String, Object>();
                                        reserveInput.put("productStoreId", productStoreId);
                                        reserveInput.put("productId", orderItem.getString("productId"));
                                        reserveInput.put("orderId", orderItem.getString("orderId"));
                                        reserveInput.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
                                        reserveInput.put("shipGroupSeqId", orderItemShipGroupAssoc.getString("shipGroupSeqId"));
                                        reserveInput.put("facilityId", shipGroupFacilityId);
                                        // use the quantity from the orderItemShipGroupAssoc, NOT the orderItem, these are reserved by item-group assoc
                                        reserveInput.put("quantity", orderItemShipGroupAssoc.getBigDecimal("quantity"));
                                        reserveInput.put("userLogin", userLogin);
                                        Map<String, Object> reserveResult = dispatcher.runSync("reserveStoreInventory", reserveInput);
    
                                        if (ServiceUtil.isError(reserveResult)) {
                                            String invErrMsg = "The product ";
                                            if (product != null) {
                                                invErrMsg += getProductName(product, orderItem);
                                            }
                                            invErrMsg += " with ID " + orderItem.getString("productId") + " is no longer in stock. Please try reducing the quantity or removing the product from this order.";
                                            resErrorMessages.add(invErrMsg);
                                        }
                                    }
                                }
                                
                                if (EntityTypeUtil.hasParentType(delegator, "ProductType", "productTypeId", product.getString("productTypeId"), "parentTypeId", "MARKETING_PKG_AUTO")) {
                                    GenericValue permUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), true);
                                    Map<String, Object> inputMap = new HashMap<String, Object>();
                                    if (UtilValidate.isNotEmpty(shipGroupFacilityId)) {
                                        inputMap.put("facilityId", shipGroupFacilityId);
                                    } else {
                                        inputMap.put("facilityId", productStore.getString("inventoryFacilityId"));
                                    }
                                    inputMap.put("orderId", orderItem.getString("orderId"));
                                    inputMap.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
                                    inputMap.put("userLogin", permUserLogin);
                                    Map<String, Object> prunResult = dispatcher.runSync("createProductionRunForMktgPkg", inputMap);
                                    if (ServiceUtil.isError(prunResult)) {
                                        Debug.logError(ServiceUtil.getErrorMessage(prunResult) + " for input:" + inputMap, module);
                                    }
                                }
                            }
                        } catch (GenericServiceException e) {
                            String errMsg = "Fatal error calling reserveStoreInventory service: " + e.toString();
                            Debug.logError(e, errMsg, module);
                            resErrorMessages.add(errMsg);
                        }
                    }
                }
            }
        }
    }
    
    public static String getProductName(GenericValue product, GenericValue orderItem) {
        if (UtilValidate.isNotEmpty(product.getString("productName"))) {
            return product.getString("productName");
        } else {
            return orderItem.getString("itemDescription");
        }
    }
    
    public static String getProductName(GenericValue product, String orderItemName) {
        if (UtilValidate.isNotEmpty(product.getString("productName"))) {
            return product.getString("productName");
        } else {
            return orderItemName;
        }
    }
    
    private static boolean hasPermission(String orderId, GenericValue userLogin, String action, Security security, Delegator delegator) {
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
    
    /** TODOCHANGE new method extends from checkItemStatus method: 
     * Service for checking to see if an orderItemAssoc is fully completed or approved */
    public static Map<String, Object> checkOrderItemAssocStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        String orderItemId = (String) context.get("orderItemSeqId");
        String statusId = (String) context.get("statusId");

        // check and make sure we have permission to change the order
        Security security = ctx.getSecurity();
        boolean hasPermission = OrderServices.hasPermission(orderId, userLogin, "UPDATE", security, delegator);
        if (!hasPermission) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderYouDoNotHavePermissionToChangeThisOrdersStatus",locale));
        }
        
        // get the order item
        GenericValue orderItem = null;
        try {
			orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemId), false);
		} catch (GenericEntityException e2) {
			Debug.logError(e2, "Cannot get OrderItem record", module);
		}
        
        if (orderItem == null) {
        	Debug.logError("OrderItem came back as null", module);
        	return ServiceUtil.returnError(UtilProperties.getMessage(resource_module_error, "DACannotUpdateNullOrderHeader",UtilMisc.toMap("orderId",orderId),locale));
        }
        
        List<GenericValue> listOrderItemAssoc = null;
        try {
        	listOrderItemAssoc = delegator.findByAnd("OrderItemAssoc", UtilMisc.toMap("toOrderId", orderItem.getString("orderId"), "toOrderItemSeqId", orderItem.getString("orderItemSeqId"), "orderItemAssocTypeId", "PAY_PROMO"), null, false);
            if (UtilValidate.isNotEmpty(listOrderItemAssoc)) {
            	for (GenericValue itemAssoc : listOrderItemAssoc) {
            		// TODOCHANGE add code: check if "quantity shipped" greater than or equal "quantity order" then change status item of order item is ITEM_COMPLETE
                    GenericValue orderItemFrom = null;
                    GenericValue orderHeaderFrom = null;
                    try {
    					orderItemFrom = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", itemAssoc.get("orderId"), "orderItemSeqId", itemAssoc.get("orderItemSeqId")), false);
    					orderHeaderFrom = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", itemAssoc.get("orderId")), true);  
                    } catch (GenericEntityException e) {
    					Debug.logWarning(e, "Trouble get the item associations from pay promo", module);
    				}
                    if (orderItemFrom != null && orderHeaderFrom != null) {
                    	com.olbius.order.OrderReadHelper orderOrderReadHelper = new com.olbius.order.OrderReadHelper(orderHeaderFrom);
                    	BigDecimal itemShippedQuantity = orderOrderReadHelper.getItemShippedQuantity(orderItemFrom).add(itemAssoc.getBigDecimal("quantity"));
                    	BigDecimal itemOrderedQuantity = orderItemFrom.getBigDecimal("quantity");
                    	if (itemShippedQuantity.compareTo(itemOrderedQuantity) == 0 || itemShippedQuantity.compareTo(itemOrderedQuantity) == 1) {
                    		// mark the item as completed
                    		String newItemStatusId = "";
                    		if (statusId.equals("ITEM_COMPLETED")) {
                    			newItemStatusId = "ITEM_COMPLETED";
                    		} else {
                    			newItemStatusId = "ITEM_PROCESSING";
                    		}
                    		
                            Map<String, Object> statusCtx = UtilMisc.<String, Object>toMap("orderId", itemAssoc.getString("orderId"), "orderItemSeqId", itemAssoc.getString("orderItemSeqId"), "statusId", newItemStatusId, "userLogin", userLogin);
                            try {
                            	Map<String, Object> resp = dispatcher.runSync("changeOrderItemStatus", statusCtx);
                            	if (ServiceUtil.isError(resp)) {
                                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                                            "OrderErrorCouldNotChangeItemStatus", locale) + newItemStatusId, null, null, resp);
                                }
                            } catch (GenericServiceException e) {
                            	 Debug.logError(e, "Error changing item status to " + newItemStatusId + ": " + e.toString(), module);
                                 return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                                         "OrderErrorCouldNotChangeItemStatus", locale) + newItemStatusId + ": " + e.toString());
                            }
                    	}
                    }
    			}
            }
        } catch (GenericEntityException e2) {
			Debug.logError(e2, "Cannot get order item associate records", module);
		}

        return ServiceUtil.returnSuccess();
    }
    
    /** Service for changing the status on an order header */
    public static Map<String, Object> setOrderStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        String statusId = (String) context.get("statusId");
        String changeReason = (String) context.get("changeReason");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");

        // check and make sure we have permission to change the order
        Security security = ctx.getSecurity();
        boolean hasPermission = OrderServices.hasPermission(orderId, userLogin, "UPDATE", security, delegator);
        if (!hasPermission) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderYouDoNotHavePermissionToChangeThisOrdersStatus",locale));
        }
        try {
            GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);

            if (orderHeader == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                        "OrderErrorCouldNotChangeOrderStatusOrderCannotBeFound", locale));
            }
            // first save off the old status
            successResult.put("oldStatusId", orderHeader.get("statusId"));
            successResult.put("orderTypeId", orderHeader.get("orderTypeId"));

            if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setOrderStatus] : From Status : " + orderHeader.getString("statusId"), module);
            if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setOrderStatus] : To Status : " + statusId, module);

            if (orderHeader.getString("statusId").equals(statusId)) {
                Debug.logWarning(UtilProperties.getMessage(resource_error,
                        "OrderTriedToSetOrderStatusWithTheSameStatusIdforOrderWithId", UtilMisc.toMap("statusId",statusId,"orderId",orderId),locale),module);
                return successResult;
            }
            try {
                Map<String, String> statusFields = UtilMisc.<String, String>toMap("statusId", orderHeader.getString("statusId"), "statusIdTo", statusId);
                GenericValue statusChange = delegator.findOne("StatusValidChange", statusFields, true);
                if (statusChange == null) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, 
                            "OrderErrorCouldNotChangeOrderStatusStatusIsNotAValidChange", locale) + ": [" + statusFields.get("statusId") + "] -> [" + statusFields.get("statusIdTo") + "]");
                }
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
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
            //Debug.logInfo("For setOrderStatus orderHeader is " + orderHeader, module);
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorCouldNotChangeOrderStatus",locale) + e.getMessage() + ").");
        }

        // release the inital hold if we are cancelled or approved
        if ("ORDER_CANCELLED".equals(statusId) || "ORDER_APPROVED".equals(statusId)) {
            OrderChangeHelper.releaseInitialOrderHold(ctx.getDispatcher(), orderId);

            // cancel any order processing if we are cancelled
            if ("ORDER_CANCELLED".equals(statusId)) {
                OrderChangeHelper.abortOrderProcessing(ctx.getDispatcher(), orderId);
            }
        }

        if ("Y".equals(context.get("setItemStatus"))) {
            String newItemStatusId = null;
            if ("ORDER_APPROVED".equals(statusId)) {
                newItemStatusId = "ITEM_APPROVED";
            } else if ("ORDER_COMPLETED".equals(statusId)) {
                newItemStatusId = "ITEM_COMPLETED";
            } else if ("ORDER_CANCELLED".equals(statusId)) {
                newItemStatusId = "ITEM_CANCELLED";
            }

            if (newItemStatusId != null) {
                try {
                    Map<String, Object> resp = dispatcher.runSync("changeOrderItemStatus", UtilMisc.<String, Object>toMap("orderId", orderId, "statusId", newItemStatusId, "userLogin", userLogin));
                    if (ServiceUtil.isError(resp)) {
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                                "OrderErrorCouldNotChangeItemStatus", locale) + newItemStatusId, null, null, resp);
                    }
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Error changing item status to " + newItemStatusId + ": " + e.toString(), module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                            "OrderErrorCouldNotChangeItemStatus", locale) + newItemStatusId + ": " + e.toString());
                }
            }
        }
        String ntfId = (String) context.get("ntfId");
        if (UtilValidate.isNotEmpty(ntfId)) {
        	try {
        		dispatcher.runSync("updateNotification", UtilMisc.toMap("ntfId", ntfId, "userLogin", userLogin));
	        } catch (Exception e) {
	     		Debug.logError(e, "Error when close notify", module);
	     	}
        }
        // TODOCHANGE: check new status of order, create notify to ...
        if ("ORDER_SUPAPPROVED".equals(statusId) || "ORDER_SADAPPROVED".equals(statusId) || 
        		"ORDER_APPROVED".equals(statusId) || "ORDER_COMPLETED".equals(statusId) || 
        		"ORDER_CANCELLED".equals(statusId) || "ORDER_NPPAPPROVED".equals(statusId)) {
        	 List<String> partiesList = new ArrayList<String>();
         	String header = "";
         	String state = "open";
         	String action = "";
         	String targetLink = "";
         	String ntfType = "MANY";
         	String sendToGroup = "N";
         	String sendrecursive = "Y";
         	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
         	try {
         		GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
         		if (orderHeader != null) {
         			String productStoreId = orderHeader.getString("productStoreId");
         			if ("ORDER_SUPAPPROVED".equals(statusId)) {
             			// send to sales admin approve
             			if (productStoreId != null) {
             				List<GenericValue> listSadGe = EntityUtil.filterByDate(delegator.findByAnd("ProductStoreRoleDetail", UtilMisc.toMap("productStoreId", productStoreId, "roleTypeId", "DELYS_SALESADMIN_GT"), null, false), false);
         					List<String> listSad = EntityUtil.getFieldListFromEntityList(listSadGe, "partyId", true);
         					if (listSad != null) {
         						partiesList.addAll(listSad);
         					}
             			}
             			header = UtilProperties.getMessage(resource_module, "DAApproveOrder",locale) + " [" + orderId +"]";
             		} else if ("ORDER_SADAPPROVED".equals(statusId)) {
                     	// send to customer (distributor)
             			List<GenericValue> listPlacingCustomerGe = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "PLACING_CUSTOMER"), null, false);
             			if (listPlacingCustomerGe != null) {
             				List<String> listPlacingCustomer = EntityUtil.getFieldListFromEntityList(listPlacingCustomerGe, "partyId", true);
             				if (listPlacingCustomer != null) {
             					partiesList.addAll(listPlacingCustomer);
             					sendToGroup = "Y";
             					sendrecursive = "N";
             				}
             			}
             			header = UtilProperties.getMessage(resource_module, "DAApproveOrderAndAttachPaymentOrder",locale) + " [" + orderId +"]";
                    } else if ("ORDER_APPROVED".equals(statusId)) {
                    	// send to logistic
                    	/*List<String> listLogistic = SalesPartyUtil.getLogsSpecialist(delegator);
            			if (UtilValidate.isNotEmpty(listLogistic)) {
            				partiesList.addAll(listLogistic);
            			}*/ 
            			header = UtilProperties.getMessage(resource, "DAAccoutantWasApprovedOrder", locale) + " [" + orderId +"]";
            			action = "orderView";
                 		targetLink = "orderId="+orderId;
            			Map<String, Object> createNotification = new HashMap<String, Object>();
						createNotification.put("userLogin", userLogin);
						createNotification.put("state", state);
						createNotification.put("dateTime", nowTimestamp);
						createNotification.put("header", header);
						createNotification.put("ntfType", ntfType);
						createNotification.put("action", action);
						createNotification.put("targetLink", targetLink);
						createNotification.put("roleTypeId", "LOG_SPECIALIST");
						try {
			         		Map<String, Object> tmpResult = dispatcher.runSync("createNotification", createNotification);
			         		if (ServiceUtil.isError(tmpResult)) {
			         			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(tmpResult));
			         		}
			         	} catch (Exception e) {
			     			Debug.logError(e, "Error when create notify", module);
			     			return ServiceUtil.returnError(UtilProperties.getMessage("DelysAdminErrorUiLabels", "DAErrorWhenCreateNotify", locale));
			     		}
                    } else if ("ORDER_COMPLETED".equals(statusId)) {
                    	// send to customer (distributor)
                    	List<GenericValue> listPlacingCustomerGe = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "PLACING_CUSTOMER"), null, false);
             			if (listPlacingCustomerGe != null) {
             				List<String> listPlacingCustomer = EntityUtil.getFieldListFromEntityList(listPlacingCustomerGe, "partyId", true);
             				if (listPlacingCustomer != null) {
             					partiesList.addAll(listPlacingCustomer);
             					sendrecursive = "N";
             				}
             			}
             			ntfType = "ONE";
             			header = UtilProperties.getMessage(resource_module, "DAOrderComplete",locale) + " [" + orderId +"]";
                    } else if ("ORDER_CANCELLED".equals(statusId)) {
                    	// send to customer (distributor)
                    	List<GenericValue> listPlacingCustomerGe = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "PLACING_CUSTOMER"), null, false);
             			if (listPlacingCustomerGe != null) {
             				List<String> listPlacingCustomer = EntityUtil.getFieldListFromEntityList(listPlacingCustomerGe, "partyId", true);
             				if (listPlacingCustomer != null) {
             					partiesList.addAll(listPlacingCustomer);
             				}
             			}
             			ntfType = "ONE";
             			header = UtilProperties.getMessage(resource_module, "DAOrderIsCanceled",locale) + " [" + orderId +"]";
                    } else if ("ORDER_NPPAPPROVED".equals(statusId)) {
                    	// send to Accountant
                    	List<String> listAcc = SalesPartyUtil.getAccoutants(delegator);
            			if (listAcc != null) {
            				partiesList.addAll(listAcc);
            			}
            			header = UtilProperties.getMessage(resource, "DAApproveOrder",locale) + " [" + orderId +"]";
                    } else if ("ORDER_HOLD".equals(statusId)) {
                    	// notify to logistic
                    	header = UtilProperties.getMessage(resource, "DAAccoutantWasHeldOrder", locale) + " [" + orderId +"]";
            			action = "orderView";
                 		targetLink = "orderId="+orderId;
                 		ntfType = "ONE";
            			Map<String, Object> createNotification = new HashMap<String, Object>();
						createNotification.put("userLogin", userLogin);
						createNotification.put("state", state);
						createNotification.put("dateTime", nowTimestamp);
						createNotification.put("header", header);
						createNotification.put("ntfType", ntfType);
						createNotification.put("action", action);
						createNotification.put("targetLink", targetLink);
						createNotification.put("roleTypeId", "LOG_SPECIALIST");
						try {
			         		Map<String, Object> tmpResult = dispatcher.runSync("createNotification", createNotification);
			         		if (ServiceUtil.isError(tmpResult)) {
			         			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(tmpResult));
			         		}
			         	} catch (Exception e) {
			     			Debug.logError(e, "Error when create notify", module);
			     			return ServiceUtil.returnError(UtilProperties.getMessage("DelysAdminErrorUiLabels", "DAErrorWhenCreateNotify", locale));
			     		}
                    }
         		}
         		action = "orderView";
         		targetLink = "orderId="+orderId;
         	} catch (Exception e) {
         		Debug.logError(e, "Error when set value for notify", module);
         	}
         	try {
         		Map<String, Object> tmpResult = dispatcher.runSync("createNotification", UtilMisc.<String, Object>toMap("partiesList", partiesList, "header", header, "state", state, "action", action, "targetLink", targetLink, "dateTime", nowTimestamp, "ntfType", ntfType, "sendToGroup", sendToGroup, "sendrecursive", sendrecursive, "userLogin", userLogin));
         		if (ServiceUtil.isError(tmpResult)) {
         			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(tmpResult));
                 }
         	} catch (Exception e) {
     			Debug.logError(e, "Error when create notify", module);
     		}
        }

		successResult.put("orderStatusId", statusId);
		//Debug.logInfo("For setOrderStatus successResult is " + successResult, module);
		return successResult;
    }
    
    // TODOCHANGE common code
    /** Service for creating a new order */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createOrderCore(DispatchContext ctx, Map<String, ? extends Object> context, boolean reserveInventory) {
		Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Security security = ctx.getSecurity();
        List<GenericValue> toBeStored = new LinkedList<GenericValue>();
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        // get the order type
        String orderTypeId = (String) context.get("orderTypeId");
        String partyId = (String) context.get("partyId");
        String billFromVendorPartyId = (String) context.get("billFromVendorPartyId");

        // check security permissions for order:
        //  SALES ORDERS - if userLogin has ORDERMGR_SALES_CREATE or ORDERMGR_CREATE permission, or if it is same party as the partyId, or
        //                 if it is an AGENT (sales rep) creating an order for his customer
        //  PURCHASE ORDERS - if there is a PURCHASE_ORDER permission
        Map<String, Object> resultSecurity = new HashMap<String, Object>();
        boolean hasPermission = reserveInventory ? OrderServices.hasPermission(orderTypeId, partyId, userLogin, "CREATE", security) : OrderServices.hasPermission(orderTypeId, partyId, userLogin, "CREATE", security, reserveInventory);
        // final check - will pass if userLogin's partyId = partyId for order or if userLogin has ORDERMGR_CREATE permission
        // jacopoc: what is the meaning of this code block? FIXME
        if (!hasPermission) {
            partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, resultSecurity, "ORDERMGR", "_CREATE");
            if (resultSecurity.size() > 0) {
                return resultSecurity;
            }
        }

        // get the product store for the order, but it is required only for sales orders
        String productStoreId = (String) context.get("productStoreId");
        GenericValue productStore = null;
        if ((orderTypeId.equals("SALES_ORDER")) && (UtilValidate.isNotEmpty(productStoreId))) {
            try {
                productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), true);
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                        "OrderErrorCouldNotFindProductStoreWithID",UtilMisc.toMap("productStoreId",productStoreId),locale)  + e.toString());
            }
        }

        // figure out if the order is immediately fulfilled based on product store settings
        boolean isImmediatelyFulfilled = false;
        if (productStore != null) {
            isImmediatelyFulfilled = "Y".equals(productStore.getString("isImmediatelyFulfilled"));
        }

        successResult.put("orderTypeId", orderTypeId);

        // lookup the order type entity
        GenericValue orderType = null;
        try {
            orderType = delegator.findOne("OrderType", UtilMisc.toMap("orderTypeId", orderTypeId), true);
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorOrderTypeLookupFailed",locale) + e.toString());
        }

        // make sure we have a valid order type
        if (orderType == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorInvalidOrderTypeWithID", UtilMisc.toMap("orderTypeId",orderTypeId), locale));
        }
        
        // check to make sure we have something to order
        List<GenericValue> orderItems = UtilGenerics.checkList(context.get("orderItems"));
        if (orderItems.size() < 1) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "items.none", locale));
        }

        // all this marketing pkg auto stuff is deprecated in favor of MARKETING_PKG_AUTO productTypeId and a BOM of MANUF_COMPONENT assocs
        // these need to be retrieved now because they might be needed for exploding MARKETING_PKG_AUTO
        List<GenericValue> orderAdjustments = UtilGenerics.checkList(context.get("orderAdjustments"));
        List<GenericValue> orderItemShipGroupInfo = UtilGenerics.checkList(context.get("orderItemShipGroupInfo"));
        List<GenericValue> orderItemPriceInfo = UtilGenerics.checkList(context.get("orderItemPriceInfos"));

        // check inventory and other things for each item
        List<String> errorMessages = FastList.newInstance();
        Map<String, BigDecimal> normalizedItemQuantities = FastMap.newInstance();
        Map<String, String> normalizedItemNames = FastMap.newInstance();
        Map<String, GenericValue> itemValuesBySeqId = FastMap.newInstance();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

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

                try {
                    // count product ordered quantities
                    // run this synchronously so it will run in the same transaction
                    dispatcher.runSync("countProductQuantityOrdered", UtilMisc.<String, Object>toMap("productId", currentProductId, "quantity", orderItem.getBigDecimal("quantity"), "userLogin", userLogin));
                } catch (GenericServiceException e1) {
                    Debug.logError(e1, "Error calling countProductQuantityOrdered service", module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                            "OrderErrorCallingCountProductQuantityOrderedService",locale) + e1.toString());
                }
            }
        }

        if (!"PURCHASE_ORDER".equals(orderTypeId) && productStoreId == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorTheProductStoreIdCanOnlyBeNullForPurchaseOrders",locale));
        }

        Timestamp orderDate = (Timestamp) context.get("orderDate");

        for (String currentProductId : normalizedItemQuantities.keySet()) {
            // lookup the product entity for each normalized item; error on products not found
            BigDecimal currentQuantity = normalizedItemQuantities.get(currentProductId);
            String itemName = normalizedItemNames.get(currentProductId);
            GenericValue product = null;

            try {
                product = delegator.findOne("Product", UtilMisc.toMap("productId", currentProductId), true);
            } catch (GenericEntityException e) {
                String errMsg = UtilProperties.getMessage(resource_error, "product.not_found", new Object[] { currentProductId }, locale);
                Debug.logError(e, errMsg, module);
                errorMessages.add(errMsg);
                continue;
            }

            if (product == null) {
                String errMsg = UtilProperties.getMessage(resource_error, "product.not_found", new Object[] { currentProductId }, locale);
                Debug.logError(errMsg, module);
                errorMessages.add(errMsg);
                continue;
            }

            if ("SALES_ORDER".equals(orderTypeId)) {
                // check to see if introductionDate hasn't passed yet
                if (product.get("introductionDate") != null && nowTimestamp.before(product.getTimestamp("introductionDate"))) {
                    String excMsg = UtilProperties.getMessage(resource_error, "product.not_yet_for_sale",
                            new Object[] { getProductName(product, itemName), product.getString("productId") }, locale);
                    Debug.logWarning(excMsg, module);
                    errorMessages.add(excMsg);
                    continue;
                }
            }

            if ("SALES_ORDER".equals(orderTypeId)) {
                boolean salesDiscontinuationFlag = false;
                // When past orders are imported, they should be imported even if sales discontinuation date is in the past but if the order date was before it
                if (orderDate != null && product.get("salesDiscontinuationDate") != null) {
                    salesDiscontinuationFlag = orderDate.after(product.getTimestamp("salesDiscontinuationDate")) && nowTimestamp.after(product.getTimestamp("salesDiscontinuationDate"));
                } else if (product.get("salesDiscontinuationDate") != null) {
                    salesDiscontinuationFlag = nowTimestamp.after(product.getTimestamp("salesDiscontinuationDate"));    
                }
                // check to see if salesDiscontinuationDate has passed
                if (salesDiscontinuationFlag) {
                    String excMsg = UtilProperties.getMessage(resource_error, "product.no_longer_for_sale",
                            new Object[] { getProductName(product, itemName), product.getString("productId") }, locale);
                    Debug.logWarning(excMsg, module);
                    errorMessages.add(excMsg);
                    continue;
                }
            }

            if ("SALES_ORDER".equals(orderTypeId)) {
                // check to see if we have inventory available
                try {
                    Map<String, Object> invReqResult = dispatcher.runSync("isStoreInventoryAvailableOrNotRequired", UtilMisc.toMap("productStoreId", productStoreId, "productId", product.get("productId"), "product", product, "quantity", currentQuantity));
                    if (ServiceUtil.isError(invReqResult)) {
                        errorMessages.add((String) invReqResult.get(ModelService.ERROR_MESSAGE));
                        List<String> errMsgList = UtilGenerics.checkList(invReqResult.get(ModelService.ERROR_MESSAGE_LIST));
                        errorMessages.addAll(errMsgList);
                    } else if (!"Y".equals(invReqResult.get("availableOrNotRequired"))) {
                        String invErrMsg = UtilProperties.getMessage(resource_error, "product.out_of_stock",
                                new Object[] { getProductName(product, itemName), currentProductId }, locale);
                        Debug.logWarning(invErrMsg, module);
                        errorMessages.add(invErrMsg);
                        continue;
                    }
                } catch (GenericServiceException e) {
                    String errMsg = "Fatal error calling inventory checking services: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    errorMessages.add(errMsg);
                }
            }
        }

        // add the fixedAsset id to the workefforts map by obtaining the fixed Asset number from the FixedAssetProduct table
        List<GenericValue> workEfforts = UtilGenerics.checkList(context.get("workEfforts")); // is an optional parameter from this service but mandatory for rental items
        for (GenericValue orderItem : orderItems) {
            if ("RENTAL_ORDER_ITEM".equals(orderItem.getString("orderItemTypeId"))) {
                // check to see if workefforts are available for this order type.
                if (UtilValidate.isEmpty(workEfforts))    {
                    String errMsg = "Work Efforts missing for ordertype RENTAL_ORDER_ITEM " + "Product: "  + orderItem.getString("productId");
                    Debug.logError(errMsg, module);
                    errorMessages.add(errMsg);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                            "OrderRentalOrderItems",locale));
                }
                for (GenericValue workEffort : workEfforts) {
                    // find the related workEffortItem (workEffortId = orderSeqId)
                    // create the entity maps required.
                    if (workEffort.getString("workEffortId").equals(orderItem.getString("orderItemSeqId")))    {
                        List<GenericValue> selFixedAssetProduct = null;
                        try {
                            List<GenericValue> allFixedAssetProduct = delegator.findByAnd("FixedAssetProduct",UtilMisc.toMap("productId",orderItem.getString("productId"),"fixedAssetProductTypeId", "FAPT_USE"), null, false);
                            selFixedAssetProduct = EntityUtil.filterByDate(allFixedAssetProduct, nowTimestamp, "fromDate", "thruDate", true);
                        } catch (GenericEntityException e) {
                            String excMsg = "Could not find related Fixed Asset for the product: " + orderItem.getString("productId");
                            Debug.logError(excMsg, module);
                            errorMessages.add(excMsg);
                            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                                    "OrderCouldNotFindRelatedFixedAssetForTheProduct",UtilMisc.toMap("productId",orderItem.getString("productId")), locale));
                        }

                        if (UtilValidate.isNotEmpty(selFixedAssetProduct)) {
                            Iterator<GenericValue> firstOne = selFixedAssetProduct.iterator();
                            if (firstOne.hasNext())        {
                                GenericValue fixedAssetProduct = delegator.makeValue("FixedAssetProduct");
                                fixedAssetProduct = firstOne.next();
                                workEffort.set("fixedAssetId",fixedAssetProduct.get("fixedAssetId"));
                                workEffort.set("quantityToProduce",orderItem.get("quantity")); // have quantity easy available later...
                                workEffort.set("createdByUserLogin", userLogin.get("userLoginId"));
                            }
                        }
                        break;  // item found, so go to next orderitem.
                    }
                }
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

        if (UtilValidate.isNotEmpty(orgPartyId)) {
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
                        String errMsg = UtilProperties.getMessage(resource_error, 
                                "OrderErrorGettingNextOrderIdWhileCreatingOrder", locale);
                        return ServiceUtil.returnError(errMsg, null, null, getNextOrderIdResult);
                    }
                    orderId = (String) getNextOrderIdResult.get("orderId");
                } catch (GenericServiceException e) {
                    String errMsg = UtilProperties.getMessage(resource_error, 
                            "OrderCaughtGenericServiceExceptionWhileGettingOrderId", locale);
                    Debug.logError(e, errMsg, module);
                    return ServiceUtil.returnError(errMsg);
                }
            }
        }

        if (UtilValidate.isEmpty(orderId)) {
            // for purchase orders or when other orderId generation fails, a product store id should not be required to make an order
            orderId = delegator.getNextSeqId("OrderHeader");
        }

        String billingAccountId = (String) context.get("billingAccountId");
        if (orderDate == null) {
            orderDate = nowTimestamp;
        }

        Map<String, Object> orderHeaderMap = UtilMisc.<String, Object>toMap("orderId", orderId, "orderTypeId", orderTypeId,
                "orderDate", orderDate, "entryDate", nowTimestamp,
                "statusId", initialStatus, "billingAccountId", billingAccountId);
        orderHeaderMap.put("orderName", context.get("orderName"));
        if (isImmediatelyFulfilled) {
            // also flag this order as needing inventory issuance so that when it is set to complete it will be issued immediately (needsInventoryIssuance = Y)
            orderHeaderMap.put("needsInventoryIssuance", "Y");
        }
        
        //TODOCHANGE add new attribute
        String salesMethodChannelEnumId = (String) context.get("salesMethodChannelEnumId");
        if (UtilValidate.isNotEmpty(salesMethodChannelEnumId)) {
        	orderHeaderMap.put("salesMethodChannelEnumId", salesMethodChannelEnumId);
        }
        GenericValue orderHeader = delegator.makeValue("OrderHeader", orderHeaderMap);

        // determine the sales channel
        String salesChannelEnumId = (String) context.get("salesChannelEnumId");
        if ((salesChannelEnumId == null) || salesChannelEnumId.equals("UNKNWN_SALES_CHANNEL")) {
            // try the default store sales channel
            if (orderTypeId.equals("SALES_ORDER") && (productStore != null)) {
                salesChannelEnumId = productStore.getString("defaultSalesChannelEnumId");
            }
            // if there's still no channel, set to unknown channel
            if (salesChannelEnumId == null) {
                salesChannelEnumId = "UNKNWN_SALES_CHANNEL";
            }
        }
        orderHeader.set("salesChannelEnumId", salesChannelEnumId);

        if (context.get("currencyUom") != null) {
            orderHeader.set("currencyUom", context.get("currencyUom"));
        }

        if (context.get("firstAttemptOrderId") != null) {
            orderHeader.set("firstAttemptOrderId", context.get("firstAttemptOrderId"));
        }

        if (context.get("grandTotal") != null) {
            orderHeader.set("grandTotal", context.get("grandTotal"));
        }

        if (UtilValidate.isNotEmpty(context.get("visitId"))) {
            orderHeader.set("visitId", context.get("visitId"));
        }

        if (UtilValidate.isNotEmpty(context.get("internalCode"))) {
            orderHeader.set("internalCode", context.get("internalCode"));
        }

        if (UtilValidate.isNotEmpty(context.get("externalId"))) {
            orderHeader.set("externalId", context.get("externalId"));
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

        if (UtilValidate.isNotEmpty(context.get("autoOrderShoppingListId"))) {
            orderHeader.set("autoOrderShoppingListId", context.get("autoOrderShoppingListId"));
        }

        if (UtilValidate.isNotEmpty(context.get("webSiteId"))) {
            orderHeader.set("webSiteId", context.get("webSiteId"));
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
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
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

        // before processing orderItems process orderItemGroups so that they'll be in place for the foreign keys and what not
        List<GenericValue> orderItemGroups = UtilGenerics.checkList(context.get("orderItemGroups"));
        if (UtilValidate.isNotEmpty(orderItemGroups)) {
            for (GenericValue orderItemGroup : orderItemGroups){
                orderItemGroup.set("orderId", orderId);
                toBeStored.add(orderItemGroup);
            }
        }

        // set the order items
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

        // set the order attributes
        List<GenericValue> orderAttributes = UtilGenerics.checkList(context.get("orderAttributes"));
        if (UtilValidate.isNotEmpty(orderAttributes)) {
            for (GenericValue oatt : orderAttributes) {
                oatt.set("orderId", orderId);
                toBeStored.add(oatt);
            }
        }

        // set the order item attributes
        List<GenericValue> orderItemAttributes = UtilGenerics.checkList(context.get("orderItemAttributes"));
        if (UtilValidate.isNotEmpty(orderItemAttributes)) {
            for (GenericValue oiatt : orderItemAttributes) {
                oiatt.set("orderId", orderId);
                toBeStored.add(oiatt);
            }
        }

        // create the order internal notes
        List<String> orderInternalNotes = UtilGenerics.checkList(context.get("orderInternalNotes"));
        if (UtilValidate.isNotEmpty(orderInternalNotes)) {
            for (String orderInternalNote : orderInternalNotes) {
                try {
                    Map<String, Object> noteOutputMap = dispatcher.runSync("createOrderNote", UtilMisc.<String, Object>toMap("orderId", orderId,
                                                                                             "internalNote", "Y",
                                                                                             "note", orderInternalNote,
                                                                                             "userLogin", userLogin));
                    if (ServiceUtil.isError(noteOutputMap)) {
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                                "OrderOrderNoteCannotBeCreated", UtilMisc.toMap("errorString", ""), locale),
                                null, null, noteOutputMap);
                    }
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Error creating internal notes while creating order: " + e.toString(), module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                            "OrderOrderNoteCannotBeCreated", UtilMisc.toMap("errorString", e.toString()), locale));
                }
            }
        }

        // create the order public notes
        List<String> orderNotes = UtilGenerics.checkList(context.get("orderNotes"));
        if (UtilValidate.isNotEmpty(orderNotes)) {
            for (String orderNote : orderNotes) {
                try {
                    Map<String, Object> noteOutputMap = dispatcher.runSync("createOrderNote", UtilMisc.<String, Object>toMap("orderId", orderId,
                                                                                             "internalNote", "N",
                                                                                             "note", orderNote,
                                                                                             "userLogin", userLogin));
                    if (ServiceUtil.isError(noteOutputMap)) {
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                            "OrderOrderNoteCannotBeCreated", UtilMisc.toMap("errorString", ""), locale),
                            null, null, noteOutputMap);
                    }
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Error creating notes while creating order: " + e.toString(), module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                            "OrderOrderNoteCannotBeCreated", UtilMisc.toMap("errorString", e.toString()), locale));
                }
            }
        }

        // create the workeffort records
        // and connect them with the orderitem over the WorkOrderItemFulfillment
        // create also the techData calendars to keep track of availability of the fixed asset.
        if (UtilValidate.isNotEmpty(workEfforts)) {
            List<GenericValue> tempList = new LinkedList<GenericValue>();
            for (GenericValue workEffort : workEfforts) {
                // create the entity maps required.
                GenericValue workOrderItemFulfillment = delegator.makeValue("WorkOrderItemFulfillment");
                // find fixed asset supplied on the workeffort map
                GenericValue fixedAsset = null;
                Debug.logInfo("find the fixedAsset",module);
                try { fixedAsset = delegator.findOne("FixedAsset",
                        UtilMisc.toMap("fixedAssetId", workEffort.get("fixedAssetId")), false);
                }
                catch (GenericEntityException e) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                            "OrderFixedAssetNotFoundFixedAssetId", 
                            UtilMisc.toMap("fixedAssetId",workEffort.get("fixedAssetId")), locale));
                }
                if (fixedAsset == null) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                            "OrderFixedAssetNotFoundFixedAssetId", 
                            UtilMisc.toMap("fixedAssetId",workEffort.get("fixedAssetId")), locale));
                }
                // see if this fixed asset has a calendar, when no create one and attach to fixed asset
                Debug.logInfo("find the techdatacalendar",module);
                GenericValue techDataCalendar = null;
                try { techDataCalendar = fixedAsset.getRelatedOne("TechDataCalendar", false);
                }
                catch (GenericEntityException e) {
                    Debug.logInfo("TechData calendar does not exist yet so create for fixedAsset: " + fixedAsset.get("fixedAssetId") ,module);
                }
                if (techDataCalendar == null) {
                    for (GenericValue currentValue : tempList) {
                        if ("FixedAsset".equals(currentValue.getEntityName()) && currentValue.getString("fixedAssetId").equals(workEffort.getString("fixedAssetId"))) {
                            fixedAsset = currentValue;
                            break;
                        }
                    }
                    for (GenericValue currentValue : tempList) {
                        if ("TechDataCalendar".equals(currentValue.getEntityName()) && currentValue.getString("calendarId").equals(fixedAsset.getString("calendarId"))) {
                            techDataCalendar = currentValue;
                            break;
                        }
                    }
                }
                if (techDataCalendar == null) {
                    techDataCalendar = delegator.makeValue("TechDataCalendar");
                    Debug.logInfo("create techdata calendar because it does not exist",module);
                    String calendarId = delegator.getNextSeqId("TechDataCalendar");
                    techDataCalendar.set("calendarId", calendarId);
                    tempList.add(techDataCalendar);
                    Debug.logInfo("update fixed Asset",module);
                    fixedAsset.set("calendarId",calendarId);
                    tempList.add(fixedAsset);
                }
                // then create the workEffort and the workOrderItemFulfillment to connect to the order and orderItem
                workOrderItemFulfillment.set("orderItemSeqId", workEffort.get("workEffortId").toString()); // orderItemSeqNo is stored here so save first
                // workeffort
                String workEffortId = delegator.getNextSeqId("WorkEffort"); // find next available workEffortId
                workEffort.set("workEffortId", workEffortId);
                workEffort.set("workEffortTypeId", "ASSET_USAGE");
                workEffort.set("currentStatusId", "_NA_"); // a lot of workefforts selection services expect a value here....
                toBeStored.add(workEffort);  // store workeffort before workOrderItemFulfillment because of workEffortId key constraint
                // workOrderItemFulfillment
                workOrderItemFulfillment.set("workEffortId", workEffortId);
                workOrderItemFulfillment.set("orderId", orderId);
                toBeStored.add(workOrderItemFulfillment);
//                Debug.logInfo("Workeffort "+ workEffortId + " created for asset " + workEffort.get("fixedAssetId") + " and order "+ workOrderItemFulfillment.get("orderId") + "/" + workOrderItemFulfillment.get("orderItemSeqId") + " created", module);
//
                // now create the TechDataExcDay, when they do not exist, create otherwise update the capacity values
                // please note that calendarId is the same for (TechData)Calendar, CalendarExcDay and CalendarExWeek
                Timestamp estimatedStartDate = workEffort.getTimestamp("estimatedStartDate");
                Timestamp estimatedCompletionDate = workEffort.getTimestamp("estimatedCompletionDate");
                long dayCount = (estimatedCompletionDate.getTime() - estimatedStartDate.getTime())/86400000;
                while (--dayCount >= 0)    {
                    GenericValue techDataCalendarExcDay = null;
                    // find an existing Day exception record
                    Timestamp exceptionDateStartTime = UtilDateTime.getDayStart(new Timestamp(estimatedStartDate.getTime()),(int)dayCount);
                    try {
                        techDataCalendarExcDay = delegator.findOne("TechDataCalendarExcDay",
                            UtilMisc.toMap("calendarId", fixedAsset.get("calendarId"), "exceptionDateStartTime", exceptionDateStartTime), false);
                    }
                    catch (GenericEntityException e) {
                        Debug.logInfo(" techData excday record not found so creating........", module);
                    }
                    if (techDataCalendarExcDay == null) {
                        for (GenericValue currentValue : tempList) {
                            if ("TechDataCalendarExcDay".equals(currentValue.getEntityName()) && currentValue.getString("calendarId").equals(fixedAsset.getString("calendarId"))
                                    && currentValue.getTimestamp("exceptionDateStartTime").equals(exceptionDateStartTime)) {
                                techDataCalendarExcDay = currentValue;
                                break;
                            }
                        }
                    }
                    if (techDataCalendarExcDay == null)    {
                        techDataCalendarExcDay = delegator.makeValue("TechDataCalendarExcDay");
                        techDataCalendarExcDay.set("calendarId", fixedAsset.get("calendarId"));
                        techDataCalendarExcDay.set("exceptionDateStartTime", exceptionDateStartTime);
                        techDataCalendarExcDay.set("usedCapacity", BigDecimal.ZERO);  // initialise to zero
                        techDataCalendarExcDay.set("exceptionCapacity", fixedAsset.getBigDecimal("productionCapacity"));
//                       Debug.logInfo(" techData excday record not found creating for calendarId: " + techDataCalendarExcDay.getString("calendarId") +
//                               " and date: " + exceptionDateStartTime.toString(), module);
                    }
                    // add the quantity to the quantity on the date record
                    BigDecimal newUsedCapacity = techDataCalendarExcDay.getBigDecimal("usedCapacity").add(workEffort.getBigDecimal("quantityToProduce"));
                    // check to see if the requested quantity is available on the requested day but only when the maximum capacity is set on the fixed asset
                    if (fixedAsset.get("productionCapacity") != null)    {
//                       Debug.logInfo("see if maximum not reached, available:  " + techDataCalendarExcDay.getString("exceptionCapacity") +
//                               " already allocated: " + techDataCalendarExcDay.getString("usedCapacity") +
//                                " Requested: " + workEffort.getString("quantityToProduce"), module);
                       if (newUsedCapacity.compareTo(techDataCalendarExcDay.getBigDecimal("exceptionCapacity")) > 0)    {
                            String errMsg = "ERROR: fixed_Asset_sold_out AssetId: " + workEffort.get("fixedAssetId") + " on date: " + techDataCalendarExcDay.getString("exceptionDateStartTime");
                            Debug.logError(errMsg, module);
                            errorMessages.add(errMsg);
                            continue;
                        }
                    }
                    techDataCalendarExcDay.set("usedCapacity", newUsedCapacity);
                    tempList.add(techDataCalendarExcDay);
//                  Debug.logInfo("Update success CalendarID: " + techDataCalendarExcDay.get("calendarId").toString() +
//                            " and for date: " + techDataCalendarExcDay.get("exceptionDateStartTime").toString() +
//                            " and for quantity: " + techDataCalendarExcDay.getDouble("usedCapacity").toString(), module);
                }
            }
            if (tempList.size() > 0) {
                toBeStored.addAll(tempList);
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
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
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

        // set the item survey responses
        List<GenericValue> surveyResponses = UtilGenerics.checkList(context.get("orderItemSurveyResponses"));
        if (UtilValidate.isNotEmpty(surveyResponses)) {
            for (GenericValue surveyResponse : surveyResponses) {
                surveyResponse.set("orderId", orderId);
                toBeStored.add(surveyResponse);
            }
        }

        // set the item price info; NOTE: this must be after the orderItems are stored for referential integrity
        if (UtilValidate.isNotEmpty(orderItemPriceInfo)) {
            for (GenericValue oipi : orderItemPriceInfo) {
                try {
                    oipi.set("orderItemPriceInfoId", delegator.getNextSeqId("OrderItemPriceInfo"));
                } catch (IllegalArgumentException e) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
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

        /* DEJ20050529 the OLD way, where a single party had all roles... no longer doing things this way...
        // define the roles for the order
        List userOrderRoleTypes = null;
        if ("SALES_ORDER".equals(orderTypeId)) {
            userOrderRoleTypes = UtilMisc.toList("END_USER_CUSTOMER", "SHIP_TO_CUSTOMER", "BILL_TO_CUSTOMER", "PLACING_CUSTOMER");
        } else if ("PURCHASE_ORDER".equals(orderTypeId)) {
            userOrderRoleTypes = UtilMisc.toList("SHIP_FROM_VENDOR", "BILL_FROM_VENDOR", "SUPPLIER_AGENT");
        } else {
            // TODO: some default behavior
        }

        // now add the roles
        if (userOrderRoleTypes != null) {
            Iterator i = userOrderRoleTypes.iterator();
            while (i.hasNext()) {
                String roleType = (String) i.next();
                String thisParty = partyId;
                if (thisParty == null) {
                    thisParty = "_NA_";  // will always set these roles so we can query
                }
                // make sure the party is in the role before adding
                toBeStored.add(delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleType)));
                toBeStored.add(delegator.makeValue("OrderRole", UtilMisc.toMap("orderId", orderId, "partyId", partyId, "roleTypeId", roleType)));
            }
        }
        */

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
        
        // TODOCHANGE add new role Salesman and Sup by customer
        String placingCustomerId = (String) context.get("placingCustomerPartyId");
        if (UtilValidate.isNotEmpty(placingCustomerId)) {
        	if ("SALES_GT_CHANNEL".equals(orderHeader.getString("salesMethodChannelEnumId"))) {
        		String salesmanId = SalesPartyUtil.getSalesmanPersonIdByCustomer(delegator, placingCustomerId);
        		if (UtilValidate.isNotEmpty(salesmanId)) {
    				String roleIdSalesmanGt = EntityUtilProperties.getPropertyValue(SalesPartyUtil.RESOURCE_DL, SalesPartyUtil.RSN_PRTROLE_SALESMAN_GT_DL, delegator);
    				toBeStored.add(delegator.makeValue("OrderRole", UtilMisc.toMap("orderId", orderId, "partyId", salesmanId, "roleTypeId", roleIdSalesmanGt)));
         		 		
    				String supId = SalesPartyUtil.getSupPersonIdBySalesman(delegator, salesmanId);
    				if (UtilValidate.isNotEmpty(supId)) {
    					String roleIdSupGt = EntityUtilProperties.getPropertyValue(SalesPartyUtil.RESOURCE_DL, SalesPartyUtil.RSN_PRTROLE_SUP_GT_DL, delegator);
    					toBeStored.add(delegator.makeValue("OrderRole", UtilMisc.toMap("orderId", orderId, "partyId", supId, "roleTypeId", roleIdSupGt)));
    				}
        		}
        	} else {
        		GenericValue salesmanGV = SalesPartyUtil.getSalesmanOrPgPersonIdByCustomerMT(delegator, placingCustomerId);
        		if (UtilValidate.isNotEmpty(salesmanGV)) {
    				toBeStored.add(delegator.makeValue("OrderRole", UtilMisc.toMap("orderId", orderId, "partyId", salesmanGV.get("partyIdTo"), "roleTypeId", salesmanGV.get("roleTypeIdTo"))));
        		}
        		GenericValue supGV = SalesPartyUtil.getSupPersonIdByCustomerMT(delegator, placingCustomerId);
				if (UtilValidate.isNotEmpty(supGV)) {
					toBeStored.add(delegator.makeValue("OrderRole", UtilMisc.toMap("orderId", orderId, "partyId", supGV.get("partyIdFrom"), "roleTypeId", supGV.get("roleTypeIdFrom"))));
				}
        	}
        }
        
        // set the affiliate -- This is going to be removed...
        String affiliateId = (String) context.get("affiliateId");
        if (UtilValidate.isNotEmpty(affiliateId)) {
            toBeStored.add(delegator.makeValue("OrderRole",
                    UtilMisc.toMap("orderId", orderId, "partyId", affiliateId, "roleTypeId", "AFFILIATE")));
        }

        // set the distributor
        String distributorId = (String) context.get("distributorId");
        if (UtilValidate.isNotEmpty(distributorId)) {
            toBeStored.add(delegator.makeValue("OrderRole",
                    UtilMisc.toMap("orderId", orderId, "partyId", distributorId, "roleTypeId", "DISTRIBUTOR")));
        }

        // find all parties in role VENDOR associated with WebSite OR ProductStore (where WebSite overrides, if specified), associated first valid with the Order
        if (UtilValidate.isNotEmpty(context.get("productStoreId"))) {
            try {
                List<GenericValue> productStoreRoles = delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("roleTypeId", "VENDOR", "productStoreId", context.get("productStoreId")), UtilMisc.toList("-fromDate"), false);
                productStoreRoles = EntityUtil.filterByDate(productStoreRoles, true);
                GenericValue productStoreRole = EntityUtil.getFirst(productStoreRoles);
                if (productStoreRole != null) {
                    toBeStored.add(delegator.makeValue("OrderRole",
                            UtilMisc.toMap("orderId", orderId, "partyId", productStoreRole.get("partyId"), "roleTypeId", "VENDOR")));
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error looking up Vendor for the current Product Store", module);
            }

        }
        if (UtilValidate.isNotEmpty(context.get("webSiteId"))) {
            try {
                List<GenericValue> webSiteRoles = delegator.findByAnd("WebSiteRole", UtilMisc.toMap("roleTypeId", "VENDOR", "webSiteId", context.get("webSiteId")), UtilMisc.toList("-fromDate"), false);
                webSiteRoles = EntityUtil.filterByDate(webSiteRoles, true);
                GenericValue webSiteRole = EntityUtil.getFirst(webSiteRoles);
                if (webSiteRole != null) {
                    toBeStored.add(delegator.makeValue("OrderRole",
                            UtilMisc.toMap("orderId", orderId, "partyId", webSiteRole.get("partyId"), "roleTypeId", "VENDOR")));
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error looking up Vendor for the current Web Site", module);
            }

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

        // store the trackingCodeOrder entities
        List<GenericValue> trackingCodeOrders = UtilGenerics.checkList(context.get("trackingCodeOrders"));
        if (UtilValidate.isNotEmpty(trackingCodeOrders)) {
            for (GenericValue trackingCodeOrder : trackingCodeOrders) {
                trackingCodeOrder.set("orderId", orderId);
                toBeStored.add(trackingCodeOrder);
            }
        }

       // store the OrderTerm entities

       List<GenericValue> orderTerms = UtilGenerics.checkList(context.get("orderTerms"));
       if (UtilValidate.isNotEmpty(orderTerms)) {
           for (GenericValue orderTerm : orderTerms) {
               orderTerm.set("orderId", orderId);
               if (orderTerm.get("orderItemSeqId") == null) {
                   orderTerm.set("orderItemSeqId", "_NA_");
               }
               toBeStored.add(orderTerm);
           }
       }

       // if a workEffortId is passed, then prepare a OrderHeaderWorkEffort value
       String workEffortId = (String) context.get("workEffortId");
       if (UtilValidate.isNotEmpty(workEffortId)) {
           GenericValue orderHeaderWorkEffort = delegator.makeValue("OrderHeaderWorkEffort");
           orderHeaderWorkEffort.set("orderId", orderId);
           orderHeaderWorkEffort.set("workEffortId", workEffortId);
           toBeStored.add(orderHeaderWorkEffort);
       }

        try {
            // store line items, etc so that they will be there for the foreign key checks
            delegator.storeAll(toBeStored);

            List<String> resErrorMessages = new LinkedList<String>();

            // add a product service to inventory 
            if (UtilValidate.isNotEmpty(orderItems)) {
                for (GenericValue orderItem: orderItems) {
                    String productId = (String) orderItem.get("productId");
                    GenericValue product = delegator.getRelatedOne("Product", orderItem, false);
                    
                    if (product != null && ("SERVICE_PRODUCT".equals(product.get("productTypeId")) || "AGGREGATEDSERV_CONF".equals(product.get("productTypeId")) || "AGGR_DIGSERV_CONF".equals(product.get("productTypeId")))) {
                        String inventoryFacilityId = null;
                        if ("Y".equals(productStore.getString("oneInventoryFacility"))) {
                            inventoryFacilityId = productStore.getString("inventoryFacilityId");

                            if (UtilValidate.isEmpty(inventoryFacilityId)) {
                                Debug.logWarning("ProductStore with id " + productStoreId + " has Y for oneInventoryFacility but inventoryFacilityId is empty, returning false for inventory check", module);
                            }
                        } else {
                            List<GenericValue> productFacilities = null;
                            GenericValue productFacility = null;

                            try {
                                productFacilities = product.getRelated("ProductFacility", product, null, true);
                            } catch (GenericEntityException e) {
                                Debug.logWarning(e, "Error invoking getRelated in isCatalogInventoryAvailable", module);
                            }

                            if (UtilValidate.isNotEmpty(productFacilities)) {
                                productFacility = EntityUtil.getFirst(productFacilities);
                                inventoryFacilityId = (String) productFacility.get("facilityId");
                            }
                        }

                        Map<String, Object> ripCtx = FastMap.newInstance();
                        if (UtilValidate.isNotEmpty(inventoryFacilityId) && UtilValidate.isNotEmpty(productId) && orderItem.getBigDecimal("quantity").compareTo(BigDecimal.ZERO) > 0) {
                            // do something tricky here: run as the "system" user
                            GenericValue permUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), true);
                            ripCtx.put("productId", productId);
                            ripCtx.put("facilityId", inventoryFacilityId);
                            ripCtx.put("inventoryItemTypeId", "SERIALIZED_INV_ITEM");
                            ripCtx.put("statusId","INV_AVAILABLE");
                            ripCtx.put("quantityAccepted", orderItem.getBigDecimal("quantity"));
                            ripCtx.put("quantityRejected", 0.0);
                            ripCtx.put("userLogin", permUserLogin);
                            try {
                                Map<String, Object> ripResult = dispatcher.runSync("receiveInventoryProduct", ripCtx);
                                if (ServiceUtil.isError(ripResult)) {
                                    String errMsg = ServiceUtil.getErrorMessage(ripResult);
                                    resErrorMessages.addAll((Collection<? extends String>) UtilMisc.<String, String>toMap("reasonCode", "ReceiveInventoryServiceError", "description", errMsg));
                                }
                            } catch (GenericServiceException e) {
                                Debug.logWarning(e, "Error invoking receiveInventoryProduct service in createOrder", module);
                            }
                        }
                    }
                }
            }

            // START inventory reservation
            if (reserveInventory) {
            	try {
                    reserveInventory(delegator, dispatcher, userLogin, locale, orderItemShipGroupInfo, dropShipGroupIds, itemValuesBySeqId,
                            orderTypeId, productStoreId, resErrorMessages);
                } catch (GeneralException e) {
                    return ServiceUtil.returnError(e.getMessage());
                }

                if (resErrorMessages.size() > 0) {
                    return ServiceUtil.returnError(resErrorMessages);
                }
            }
            // END inventory reservation
            
            successResult.put("orderId", orderId);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem with order storage or reservations", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorCouldNotCreateOrderWriteError",locale) + e.getMessage() + ").");
        }
        
        // TODOCHANGE add code: check if "quantity shipped" greater than or equal "quantity order" then change status item of order item is ITEM_COMPLETE
        if (UtilValidate.isNotEmpty(orderItemAssociations)) {
            for (GenericValue orderItemAssociation : orderItemAssociations) {
                GenericValue orderItemFrom = null;
                GenericValue orderHeaderFrom = null;
                try {
					orderItemFrom = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", orderItemAssociation.get("orderId"), "orderItemSeqId", orderItemAssociation.get("orderItemSeqId")), false);
					orderHeaderFrom = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderItemAssociation.get("orderId")), true);  
                } catch (GenericEntityException e) {
					Debug.logWarning(e, "Trouble get the item associations from pay promo", module);
				}
                if (orderItemFrom != null && orderHeaderFrom != null) {
                	com.olbius.order.OrderReadHelper orderOrderReadHelper = new com.olbius.order.OrderReadHelper(orderHeaderFrom);
                	BigDecimal itemShippedQuantity = orderOrderReadHelper.getItemShippedQuantity(orderItemFrom).add(orderItemAssociation.getBigDecimal("quantity"));
                	BigDecimal itemOrderedQuantity = orderItemFrom.getBigDecimal("quantity");
                	if (itemShippedQuantity.compareTo(itemOrderedQuantity) == 0 || itemShippedQuantity.compareTo(itemOrderedQuantity) == 1) {
                		String newStatus = "ITEM_PROCESSING";
                    	// now set the new order status
                        if (newStatus != null && !newStatus.equals(orderItemFrom.get("statusId"))) {
                            Map<String, Object> serviceContext = UtilMisc.<String, Object>toMap("orderId", orderItemFrom.getString("orderId"), "orderItemSeqId", orderItemFrom.getString("orderItemSeqId"), "statusId", newStatus, "userLogin", userLogin);
                            Map<String, Object> newSttsResult = null;
                            try {
                                newSttsResult = dispatcher.runSync("changeOrderItemStatus", serviceContext);
                            } catch (GenericServiceException e) {
                                Debug.logError(e, "Problem calling the changeOrderStatus service", module);
                            }
                            if (ServiceUtil.isError(newSttsResult)) {
                                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(newSttsResult));
                            }
                        }
                	}
                }
            }
        }
        
        // TODOCHANGE: if created by SalesAdmin then send notify for directive distributor
        if (SalesPartyUtil.isSalesAdminGTEmployee(userLogin, delegator) || SalesPartyUtil.isSalesAdminManagerEmployee(userLogin, delegator)) {
        	Map<String, Object> contextMap = FastMap.newInstance();
        	contextMap.put("orderId", orderId);
        	contextMap.put("statusId", "ORDER_SADAPPROVED");
        	contextMap.put("setItemStatus", "Y");
        	contextMap.put("changeReason", "");
        	contextMap.put("userLogin", userLogin);
        	contextMap.put("locale", locale);
	        try {
	        	Map<String, Object> tmpResult = dispatcher.runSync("changeOrderStatus", contextMap);
	        	if (ServiceUtil.isError(tmpResult)) {
        			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(tmpResult));
                }
	        } catch (Exception e) {
				Debug.logError(e, "Error when change order status", module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_module_error, "DAErrorWhenChangeOrderStatus", locale));
			}
        } else {
        	// find all parties in role OWNER
        	if (UtilValidate.isNotEmpty(context.get("productStoreId"))) {
        		boolean isOrderOfCompany = false;
        		try {
        			List<GenericValue> productStoreRoles = delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("roleTypeId", "OWNER", "productStoreId", context.get("productStoreId")), null, false);
        			productStoreRoles = EntityUtil.filterByDate(productStoreRoles, true);
        			if (UtilValidate.isNotEmpty(productStoreRoles)) {
        				List<String> partyOwnerIds = EntityUtil.getFieldListFromEntityList(productStoreRoles, "partyId", true);
        				if (UtilValidate.isNotEmpty(partyOwnerIds)) {
        					List<String> companyIds = SalesPartyUtil.getListCompanyInProperties(delegator);
        					if (UtilValidate.isNotEmpty(companyIds)) {
        						for (String companyId : companyIds) {
        							if (partyOwnerIds.contains(companyId)) {
        								isOrderOfCompany = true;
        								break;
        							}
        						}
        					}
        				}
        			}
        		} catch (GenericEntityException e) {
        			Debug.logError(e, "Error looking up Onwer for the current Product Store", module);
        		}
        		if (isOrderOfCompany) {
        			// create notify to SUP
                    List<String> partiesList = new ArrayList<String>();
                	String header = "";
                	String state = "open";
                	String action = "";
                	String targetLink = "";
                	try {
                		List<GenericValue> listPlacingCustomerGe = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "PLACING_CUSTOMER"), null, false);
                		if (listPlacingCustomerGe != null) {
             				List<String> listPlacingCustomer = EntityUtil.getFieldListFromEntityList(listPlacingCustomerGe, "partyId", true);
             				if (listPlacingCustomer != null) {
             					for (String placingCustomer : listPlacingCustomer) {
             						List<String> listPartyTmp = null;
             						if (SalesPartyUtil.isDistributor(placingCustomer, delegator)) {
             							listPartyTmp = SalesPartyUtil.getListSupPersonIdByDistributor(delegator, placingCustomer);
             						} else {
             							listPartyTmp = SalesPartyUtil.getListSupPersonIdByCustomerDirect(delegator, placingCustomer);
             						}
             	        			if (listPartyTmp != null) {
             	        				partiesList.addAll(listPartyTmp);
             	        			} 
             					}
             				}
             			}
                		header = UtilProperties.getMessage(resource_module, "DAApproveOrder",locale) + " [" + orderId +"]";
                		action = "orderView";
                		targetLink = "orderId="+orderId;
                	} catch (Exception e) {
                		Debug.logError(e, "Error when set value for notify", module);
                	}
                	try {
                		Map<String, Object> tmpResult = dispatcher.runSync("createNotification", UtilMisc.<String, Object>toMap("partiesList", partiesList, "header", header, "state", state, "action", action, "targetLink", targetLink, "dateTime", nowTimestamp, "userLogin", userLogin));
                		if (ServiceUtil.isError(tmpResult)) {
                			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(tmpResult));
                        }
                	} catch (Exception e) {
            			Debug.logError(e, "Error when create notify", module);
            			return ServiceUtil.returnError(UtilProperties.getMessage(resource_module_error, "DAErrorWhenCreateNotify", locale));
            		}
        		}
        	}
        }
        
        return successResult;
	}
    
     // TODOCHANGE custom method
     public static Map<String, Object> updateApprovedOrderItemsSales(DispatchContext dctx, Map<String, ? extends Object> context) {
         LocalDispatcher dispatcher = dctx.getDispatcher();
         Delegator delegator = dctx.getDelegator();
         GenericValue userLogin = (GenericValue) context.get("userLogin");
         Locale locale = (Locale) context.get("locale");
         String orderId = (String) context.get("orderId");
         /*Map<String, String> overridePriceMap = UtilGenerics.checkMap(context.get("overridePriceMap"));
         Map<String, String> itemDescriptionMap = UtilGenerics.checkMap(context.get("itemDescriptionMap"));
         Map<String, String> itemPriceMap = UtilGenerics.checkMap(context.get("itemPriceMap"));
         Map<String, String> itemQtyMap = UtilGenerics.checkMap(context.get("itemQtyMap"));
         Map<String, String> itemReasonMap = UtilGenerics.checkMap(context.get("itemReasonMap"));
         Map<String, String> itemCommentMap = UtilGenerics.checkMap(context.get("itemCommentMap"));
         Map<String, String> itemAttributesMap = UtilGenerics.checkMap(context.get("itemAttributesMap"));
         Map<String, String> itemEstimatedShipDateMap = UtilGenerics.checkMap(context.get("itemShipDateMap"));
         Map<String, String> itemEstimatedDeliveryDateMap = UtilGenerics.checkMap(context.get("itemDeliveryDateMap"));*/
         String strParam = (String) context.get("strParam");
         Boolean calcTax = (Boolean) context.get("calcTax");
         if (calcTax == null) {
             calcTax = Boolean.TRUE;
         }
         
         // TODOCHANGE check isDistributor
         boolean isDistributor = SalesPartyUtil.isDistributor(userLogin, delegator);
         if (isDistributor) {
        	 GenericValue orderHeader;
			try {
				orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
				if (!"ORDER_CREATED".equals(orderHeader.getString("statusId"))) {
					return ServiceUtil.returnError(UtilProperties.getMessage(resource_module_error, "DANotPermissionUpdateThisOrder", locale));
				}
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError("Error when get order header");
			}
         }

         // obtain a shopping cart object for updating
         ShoppingCart cart = null;
         try {
             cart = loadCartForUpdate(dispatcher, delegator, userLogin, orderId);
         } catch (GeneralException e) {
             return ServiceUtil.returnError(e.getMessage());
         }
         if (cart == null) {
             return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                     "OrderShoppingCartEmpty", locale));
         }

         Map<String, String> overridePriceMap = FastMap.newInstance(); 		//opm_${orderItem.orderItemSeqId} - checkbox - {}
         Map<String, String> itemDescriptionMap = FastMap.newInstance(); 	//idm_${orderItem.orderItemSeqId} - ${orderItem.itemDescription?if_exists} - {00001=Váng sữa Monte Schoko 4*55g, 00002=Váng sữa Monte Vani 4*55g}
         Map<String, String> itemPriceMap = FastMap.newInstance(); 			//ipm_${orderItem.orderItemSeqId} - orderItem.unitPrice - {00001=20.000,00, 00002=10.000,00}
         Map<String, String> itemQtyMap = FastMap.newInstance(); 			//iqm_${shipGroupAssoc.orderItemSeqId}:${shipGroupAssoc.shipGroupSeqId} - ${shipGroupAssoc.quantity?string.number} - {00001:00001=15, 00002:00001=5}
         Map<String, String> itemReasonMap = FastMap.newInstance(); 		//irm_${orderItem.orderItemSeqId} - ${reason.enumId} (orderItemChangeReasons) - {00001=, 00002=}
         Map<String, String> itemCommentMap = FastMap.newInstance(); //icm_	-  - {00001=, 00002=}
         Map<String, String> itemAttributesMap = FastMap.newInstance(); //iam_ -  - {}
         Map<String, String> itemEstimatedShipDateMap = FastMap.newInstance(); //isdm_ -  - {}
         Map<String, String> itemEstimatedDeliveryDateMap = FastMap.newInstance(); //iddm_ -  - {}
         Map<String, String> itemExpireDateMap = FastMap.newInstance();
         Map<String, String> itemAlternativeQtyMap = FastMap.newInstance();
         Map<String, String> itemQuantityUomIdMap = FastMap.newInstance();
         // BUILD MAP: Call multiple addToCart method
         if (UtilValidate.isNotEmpty(strParam)) {
         	String[] strParamLine = strParam.split("\\|OLBIUS\\|"); //item (productId - quantity - quantityUomId - expireDate - orderItemSeqId - shipGroupSeqId)
         	if ("N".equals(strParamLine[0]) && strParamLine.length > 1) {
         		for (int i = 1; i < strParamLine.length; i++) {
         			String[] lineValues = strParamLine[i].split("\\|SUIBLO\\|");
         			String productId = lineValues.length > 0 ? lineValues[0] : "";
         			String quantityStr = lineValues.length > 1 ? lineValues[1] : "";
         			String quantityUomId = lineValues.length > 2 ? lineValues[2] : "";
         			String expireDateStr = lineValues.length > 3 ? lineValues[3] : "";
         			String orderItemSeqId = lineValues.length > 4 ? lineValues[4] : "";
         			String shipGroupSeqId = lineValues.length > 5 ? lineValues[5] : "";
         			
         			//BigDecimal price = null;
         			BigDecimal quantity = BigDecimal.ZERO;
         			//Timestamp expireDate = null;
         			BigDecimal alternativeQuantity = null;
         			if (UtilValidate.isNotEmpty(orderItemSeqId) && UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(quantityStr)) {
        				// Check quantityUomId with productQuotation
        				BigDecimal quantityUomIdToDefault = BigDecimal.ONE;
    					GenericValue productItem = null;
    					try {
    						productItem = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
    						if (productItem == null) {
    							//alertMessageList.add(UtilProperties.getMessage(resource_error, "DAProductNotExists",locale));
    							continue;
    						}
        		        } catch (Exception e) {
        		            Debug.logWarning(e, "Problems [product not exists] get productId = " + productId, module);
        		        }
    					if (productItem.getString("quantityUomId") != null) {
    						if (!quantityUomId.equals(productItem.getString("quantityUomId"))) {
    							try {
        							Map<String, Object> resultValue = dispatcher.runSync("getConvertPackingNumber", UtilMisc.toMap("productId", productItem.getString("productId"), "uomFromId", quantityUomId, "uomToId", productItem.getString("quantityUomId"), "userLogin", userLogin));
            						if (ServiceUtil.isSuccess(resultValue)) {
            							quantityUomIdToDefault = (BigDecimal) resultValue.get("convertNumber");
            						}
    							} catch (Exception e) {
    	        		            Debug.logWarning(e, "Problems run service name = getConvertPackingNumber", module);
    	        		        }
    						} else {
    							quantityUomIdToDefault = BigDecimal.ONE;
    						}
    					}
    					
    					try {
        		            quantity = (BigDecimal) ObjectType.simpleTypeConvert(quantityStr, "BigDecimal", null, locale);
        		            //For quantity we should test if we allow to add decimal quantity for this product an productStore : if not then round to 0
        		            if(! ProductWorker.isDecimalQuantityOrderAllowed(delegator, productId, cart.getProductStoreId())){
        		                quantity = quantity.setScale(0, UtilNumber.getBigDecimalRoundingMode("order.rounding"));
        		            } else {
        		                quantity = quantity.setScale(UtilNumber.getBigDecimalScale("order.decimals"), UtilNumber.getBigDecimalRoundingMode("order.rounding"));
        		            }
        		            alternativeQuantity = new BigDecimal(quantity.doubleValue());
        		            quantity = quantity.multiply(quantityUomIdToDefault);
        		        } catch (Exception e) {
        		            Debug.logWarning(e, "Problems parsing quantity string: " + quantityStr, module);
        		            //quantity = BigDecimal.ONE;
        		        }
    					/*try {
        		            expireDate = (Timestamp) ObjectType.simpleTypeConvert(expireDateStr, "Timestamp", null, locale);
        		        } catch (Exception e) {
        		            Debug.logWarning(e, "Problems parsing expireDate string: " + expireDateStr, module);
        		        }*/
    					String qtyKey = orderItemSeqId + ":" + shipGroupSeqId;
    					String quantity1 = "";
    					String quantity2 = "";
						try {
							quantity1 = (String) ObjectType.simpleTypeConvert(quantity, "String", null, locale);
							quantity2 = (String) ObjectType.simpleTypeConvert(alternativeQuantity, "String", null, locale);
						} catch (GeneralException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    					itemQtyMap.put(qtyKey, quantity1);
    					itemExpireDateMap.put(orderItemSeqId, expireDateStr);
    					itemAlternativeQtyMap.put(qtyKey, quantity2);
    					itemQuantityUomIdMap.put(orderItemSeqId, quantityUomId);
         			}
         		}
         	}
         }
         
         // go through the item attributes map once to get a list of key names
         Set<String> attributeNames =FastSet.newInstance();
         Set<String> keys  = itemAttributesMap.keySet();
         for (String key : keys) {
             String[] attributeInfo = key.split(":");
             attributeNames.add(attributeInfo[0]);
         }

         // go through the item map and obtain the totals per item
         Map<String, BigDecimal> itemTotals = new HashMap<String, BigDecimal>();
         Map<String, BigDecimal> itemTotalsAlterQty = new HashMap<String, BigDecimal>();
         for (String key : itemQtyMap.keySet()) {
             String quantityStr = itemQtyMap.get(key);
             String alternativeQuantityStr = itemAlternativeQtyMap.get(key);
             BigDecimal groupQty = BigDecimal.ZERO;
             BigDecimal groupQtyAlterQty = BigDecimal.ZERO;
             try {
                 groupQty = (BigDecimal) ObjectType.simpleTypeConvert(quantityStr, "BigDecimal", null, locale);
                 groupQtyAlterQty = (BigDecimal) ObjectType.simpleTypeConvert(alternativeQuantityStr, "BigDecimal", null, locale);
             } catch (GeneralException e) {
                 Debug.logError(e, module);
                 return ServiceUtil.returnError(e.getMessage());
             }

             if (groupQty.compareTo(BigDecimal.ONE) < 0) {
                 return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                         "OrderItemQtyMustBePositive", locale));
             }
             
             if (groupQtyAlterQty.compareTo(BigDecimal.ONE) < 0) {
            	 return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                         "OrderItemQtyMustBePositive", locale));
             }

             String[] itemInfo = key.split(":");
             BigDecimal tally = itemTotals.get(itemInfo[0]);
             BigDecimal tallyAlterQty = itemTotalsAlterQty.get(itemInfo[0]);
             if (tally == null) {
                 tally = groupQty;
             } else {
                 tally = tally.add(groupQty);
             }
             itemTotals.put(itemInfo[0], tally);
             
             if (tallyAlterQty == null) {
            	 tallyAlterQty = groupQtyAlterQty;
             } else {
            	 tallyAlterQty = tallyAlterQty.add(groupQtyAlterQty);
             }
             itemTotalsAlterQty.put(itemInfo[0], tallyAlterQty);
         }

         // set the items amount/price
         for (String itemSeqId : itemTotals.keySet()) {
             ShoppingCartItem cartItem = cart.findCartItem(itemSeqId);

             if (cartItem != null) {
                 BigDecimal qty = itemTotals.get(itemSeqId);
                 BigDecimal alterQty = itemTotalsAlterQty.get(itemSeqId);
                 BigDecimal priceSave = cartItem.getBasePrice();

                 // set quantity
                 try {
                     cartItem.setQuantity(qty, dispatcher, cart, false, false); // trigger external ops, don't reset ship groups (and update prices for both PO and SO items)
                 } catch (CartItemModifyException e) {
                     Debug.logError(e, module);
                     return ServiceUtil.returnError(e.getMessage());
                 }
                 Debug.logInfo("Set item quantity: [" + itemSeqId + "] " + qty, module);

                 if (cartItem.getIsModifiedPrice()) // set price
                     cartItem.setBasePrice(priceSave);

                 if (overridePriceMap.containsKey(itemSeqId)) {
                     String priceStr = itemPriceMap.get(itemSeqId);
                     if (UtilValidate.isNotEmpty(priceStr)) {
                         BigDecimal price = null;
                         try {
                             price = (BigDecimal) ObjectType.simpleTypeConvert(priceStr, "BigDecimal", null, locale);
                         } catch (GeneralException e) {
                             Debug.logError(e, module);
                             return ServiceUtil.returnError(e.getMessage());
                         }
                         price = price.setScale(orderDecimals, orderRounding);
                         cartItem.setBasePrice(price);
                         cartItem.setIsModifiedPrice(true);
                         Debug.logInfo("Set item price: [" + itemSeqId + "] " + price, module);
                     }

                 }

                 // Update the item description
                 if (itemDescriptionMap != null && itemDescriptionMap.containsKey(itemSeqId)) {
                     String description = itemDescriptionMap.get(itemSeqId);
                     if (UtilValidate.isNotEmpty(description)) {
                         cartItem.setName(description);
                         Debug.logInfo("Set item description: [" + itemSeqId + "] " + description, module);
                     } else {
                         return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                                 "OrderItemDescriptionCannotBeEmpty", locale));
                     }
                 }

                 // update the order item attributes
                 if (itemAttributesMap != null) {
                     String attrValue = null;
                     for (String attrName : attributeNames) {
                         attrValue = itemAttributesMap.get(attrName + ":" + itemSeqId);
                         if (UtilValidate.isNotEmpty(attrName)) {
                             cartItem.setOrderItemAttribute(attrName, attrValue);
                             Debug.logInfo("Set item attribute Name: [" + itemSeqId + "] " + attrName + " , Value:" + attrValue, module);
                         }
                     }
                 }
                 
                 // Update the item expireDate
                 if (itemExpireDateMap != null && itemExpireDateMap.containsKey(itemSeqId)) {
                     String expireDateStr = itemExpireDateMap.get(itemSeqId);
                     if (UtilValidate.isNotEmpty(expireDateStr)) {
                    	 Timestamp expireDate = null;
                    	 try {
                    		 if (UtilValidate.isNotEmpty(expireDateStr)) {
         	    	        	Long expireDateL = Long.parseLong(expireDateStr);
         	    	        	expireDate = new Timestamp(expireDateL);
         	    	        }
     		            	//expireDate = (Timestamp) ObjectType.simpleTypeConvert(expireDateStr, "Timestamp", null, locale);
     		        	 } catch (Exception e) {
         		            Debug.logWarning(e, "Problems parsing expireDate string: " + expireDateStr, module);
     		        	 }
                		 cartItem.setAttribute("expireDate", expireDate);
                         Debug.logInfo("Set item expireDate: [" + itemSeqId + "] " + expireDate, module);
                     }
                 }
                 
                 // Update the item alternativeQuantity
                 if (UtilValidate.isNotEmpty(alterQty)) {
            		 cartItem.setAttribute("alternativeQuantity", alterQty);
            	 }
                 
                 // Update the item quantityUomId
                 if (itemQuantityUomIdMap != null && itemQuantityUomIdMap.containsKey(itemSeqId)) {
                     String quantityUomId = itemQuantityUomIdMap.get(itemSeqId);
                     if (UtilValidate.isNotEmpty(quantityUomId)) {
                		 cartItem.setAttribute("quantityUomId", quantityUomId);
                         Debug.logInfo("Set item quantityUomId: [" + itemSeqId + "] " + quantityUomId, module);
                     }
                 }
                 
             } else {
                 Debug.logInfo("Unable to locate shopping cart item for seqId #" + itemSeqId, module);
             }
         }
         // Create Estimated Delivery dates
         for (Map.Entry<String, String> entry : itemEstimatedDeliveryDateMap.entrySet()) {
             String itemSeqId =  entry.getKey();

             // ignore internationalised variant of dates
             if (!itemSeqId.endsWith("_i18n")) {
                 String estimatedDeliveryDate = entry.getValue();
                 if (UtilValidate.isNotEmpty(estimatedDeliveryDate)) {
                     Timestamp deliveryDate = Timestamp.valueOf(estimatedDeliveryDate);
                     ShoppingCartItem cartItem = cart.findCartItem(itemSeqId);
                     cartItem.setDesiredDeliveryDate(deliveryDate);
                 }
             }
         }

         // Create Estimated ship dates
         for (Map.Entry<String, String> entry : itemEstimatedShipDateMap.entrySet()) {
             String itemSeqId =  entry.getKey();

             // ignore internationalised variant of dates
             if (!itemSeqId.endsWith("_i18n")) {
                 String estimatedShipDate = entry.getValue();
                 if (UtilValidate.isNotEmpty(estimatedShipDate)) {
                     Timestamp shipDate = Timestamp.valueOf(estimatedShipDate);
                     ShoppingCartItem cartItem = cart.findCartItem(itemSeqId);
                     cartItem.setEstimatedShipDate(shipDate);
                 }
             }
         }

         // update the group amounts
         for (String key : itemQtyMap.keySet()) {
             String quantityStr = itemQtyMap.get(key);
             BigDecimal groupQty = BigDecimal.ZERO;
             try {
                 groupQty = (BigDecimal) ObjectType.simpleTypeConvert(quantityStr, "BigDecimal", null, locale);
             } catch (GeneralException e) {
                 Debug.logError(e, module);
                 return ServiceUtil.returnError(e.getMessage());
             }

             String[] itemInfo = key.split(":");
             int groupIdx = -1;
             try {
                 groupIdx = Integer.parseInt(itemInfo[1]);
             } catch (NumberFormatException e) {
                 Debug.logError(e, module);
                 return ServiceUtil.returnError(e.getMessage());
             }

             // set the group qty
             ShoppingCartItem cartItem = cart.findCartItem(itemInfo[0]);
             if (cartItem != null) {
                 Debug.logInfo("Shipping info (before) for group #" + (groupIdx-1) + " [" + cart.getShipmentMethodTypeId(groupIdx-1) + " / " + cart.getCarrierPartyId(groupIdx-1) + "]", module);
                 cart.setItemShipGroupQty(cartItem, groupQty, groupIdx - 1);
                 Debug.logInfo("Set ship group qty: [" + itemInfo[0] + " / " + itemInfo[1] + " (" + (groupIdx-1) + ")] " + groupQty, module);
                 Debug.logInfo("Shipping info (after) for group #" + (groupIdx-1) + " [" + cart.getShipmentMethodTypeId(groupIdx-1) + " / " + cart.getCarrierPartyId(groupIdx-1) + "]", module);
             }
         }
         

         // New method 25/11/2014, Find and add agreement into Shopping cart
         // get applicable agreements for order entry
         EntityCondition agreementCondition = null;
         String agreementId = null;
         List<GenericValue> agreements = null;
         Map<String, Object> result2 = null;
         try {
	        	// for a sales order, orderPartyId = billToCustomer (the customer)
	            String customerPartyId = cart.getOrderPartyId();
	            String companyPartyId = cart.getBillFromVendorPartyId();
	            
	            List<String> customerPartyIds = new ArrayList<String>();
	        	if (UtilValidate.isNotEmpty(customerPartyId)) {
	        		customerPartyIds.add(customerPartyId);
	        	}
	        	List<GenericValue> customerPartyRelList = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom", customerPartyId, "roleTypeIdFrom", "CHILD_MEMBER", "roleTypeIdTo", "PARENT_MEMBER"), null, false);
	        	if (customerPartyRelList != null) {
	        		for (GenericValue customerPartyRelItem : customerPartyRelList) {
	        			List<GenericValue> customerPartyRoleItem = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", customerPartyRelItem.getString("partyIdTo"), "roleTypeId", "CUSTOMER_GROUP"), null, false);
	        			if (customerPartyRoleItem != null && customerPartyRoleItem.size() > 0) {
	        				customerPartyIds.add(customerPartyRelItem.getString("partyIdTo"));
	        			}
	        		}
	        	}
	        	
	        	if ((customerPartyRelList != null) && (customerPartyIds.size() > 0)) {
	        		// the agreement for a sales order is from the customer to us
	        		agreementCondition = EntityCondition.makeCondition(
	        				UtilMisc.toList(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, companyPartyId),
	    					EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, customerPartyIds)), EntityOperator.AND);
	        	} else {
	        	    // the agreement for a sales order is from the customer to us
	        		agreementCondition = EntityCondition.makeCondition(
	        				UtilMisc.toList(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, companyPartyId),
							EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, customerPartyId)), EntityOperator.AND);
	        	}
 	        	
 	        
 	        agreements = delegator.findList("Agreement", agreementCondition, null, null, null, true);
 	        agreements = EntityUtil.filterByDate(agreements);
         } catch (GenericEntityException e) {
        	 return ServiceUtil.returnError(e.getMessage());
         }
         
         if (agreements != null) {
         	for (GenericValue agreementItem : agreements) {
         		if (agreementItem.containsKey("isAuto") && "Y".equals(agreementItem.getString("isAuto"))) {
         			agreementId = agreementItem.getString("agreementId");
         		}
         	}
         }
         ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
         if (UtilValidate.isNotEmpty(cart.getCurrency())) {
             cartHelper.setCurrency(cart.getCurrency());
         }
         if (UtilValidate.isNotEmpty(agreementId)) {
         	// set the agreement if specified otherwise set the currency
             if (UtilValidate.isNotEmpty(agreementId)) {
                 result2 = cartHelper.selectAgreement(agreementId);
             }
             if (ServiceUtil.isError(result2)) {
                 return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result2));
             }
         }

         // run promotions to handle all changes in the cart
         ProductPromoWorker.doPromotions(cart, dispatcher);
         // TODOCHANGE end change

         // save all the updated information
         try {
             saveUpdatedCartToOrder(dispatcher, delegator, cart, locale, userLogin, orderId, UtilMisc.<String, Object>toMap("itemReasonMap", itemReasonMap, "itemCommentMap", itemCommentMap), calcTax, false);
         } catch (GeneralException e) {
             return ServiceUtil.returnError(e.getMessage());
         }

         // log an order note
         try {
        	 String noteContent = UtilProperties.getMessage(resource_module, "DAUpdatedOrder", locale) + ".";
             dispatcher.runSync("createOrderNote", UtilMisc.<String, Object>toMap("orderId", orderId, "note", noteContent, "internalNote", "Y", "userLogin", userLogin));
         } catch (GenericServiceException e) {
             Debug.logError(e, module);
         }

         Map<String, Object> result = ServiceUtil.returnSuccess();
         result.put("shoppingCart", cart);
         result.put("orderId", orderId);
         
         /*
         // TODOCHANGE check isDistributor
         // create notify to SUP
         if (isDistributor) {
        	 Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
             List<String> partiesList = new ArrayList<String>();
     		 String header = "";
     		 String state = "open";
     		 String action = "";
     		 String targetLink = "";
     		 String ntfType = "ONE";
     		 try {
     			 List<GenericValue> listPlacingCustomerGe = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "PLACING_CUSTOMER"), null, false);
     			 if (listPlacingCustomerGe != null) {
     				 List<String> listPlacingCustomer = EntityUtil.getFieldListFromEntityList(listPlacingCustomerGe, "partyId", true);
     				 if (listPlacingCustomer != null) {
     					 for (String placingCustomer : listPlacingCustomer) {
     						 List<String> listPartyTmp = SalesPartyUtil.getListSupPersonIdByDistributor(delegator, placingCustomer);
     						 if (listPartyTmp != null) {
     							 partiesList.addAll(listPartyTmp);
     						 } 
     					 }
     				 }
     			 }
     			 header = UtilProperties.getMessage(resource_module, "DAOrderWasEditedByDistributor", UtilMisc.toMap("orderId", orderId, "userLoginId", userLogin.get("userLoginId")),locale);
     			 action = "orderView";
     			 targetLink = "orderId="+orderId;
     		 } catch (Exception e) {
     			 Debug.logError(e, "Error when set value for notify", module);
     		 }
     		 try {
     			 Map<String, Object> tmpResult = dispatcher.runSync("createNotification", 
      				UtilMisc.<String, Object>toMap("partiesList", partiesList, "header", header, "state", state, "action", action, 
      						"targetLink", targetLink, "dateTime", nowTimestamp, "ntfType", ntfType, "userLogin", userLogin));
     			 if (ServiceUtil.isError(tmpResult)) {
     				 return ServiceUtil.returnError(ServiceUtil.getErrorMessage(tmpResult));
     			 }
     		 } catch (Exception e) {
     			 Debug.logError(e, "Error when create notify", module);
     			 return ServiceUtil.returnError(UtilProperties.getMessage(resource_module_error, "DAErrorWhenCreateNotify", locale));
     		 }
         }
         */
         
         return result;
     }
     
     // TODOCHANGE custom method
     public static Map<String, Object> updateApprovedOrderItemsSalesUop(DispatchContext dctx, Map<String, ? extends Object> context) {
         LocalDispatcher dispatcher = dctx.getDispatcher();
         Delegator delegator = dctx.getDelegator();
         GenericValue userLogin = (GenericValue) context.get("userLogin");
         Locale locale = (Locale) context.get("locale");
         String orderId = (String) context.get("orderId");
         String strParam = (String) context.get("strParam");
         
         // get orderHeader
         if (UtilValidate.isEmpty(orderId)) {
			 return ServiceUtil.returnError(UtilProperties.getMessage(resource_module_error, "DAOrderIdIsEmpty", locale));
		 }
         try {
        	 GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
    		 if (UtilValidate.isEmpty(orderHeader)) {
    			 return ServiceUtil.returnError(UtilProperties.getMessage(resource_module_error, "DANotFoundOrderWithId", UtilMisc.toMap("orderId", orderId), locale));
    		 }
    		 
    		 // get orderItems
    		 List<GenericValue> toBeStored = new LinkedList<GenericValue>();
    		 List<GenericValue> orderItemShipGroupAssoc = new ArrayList<GenericValue>();
    		 if (UtilValidate.isNotEmpty(strParam)) {
		     	String[] strParamLine = strParam.split("\\|OLBIUS\\|"); //item (productId - quantity - quantityUomId - expireDate - orderItemSeqId - shipGroupSeqId)
		     	if ("N".equals(strParamLine[0]) && strParamLine.length > 1) {
		     		for (int i = 1; i < strParamLine.length; i++) {
		     			String[] lineValues = strParamLine[i].split("\\|SUIBLO\\|");
		     			//String productId = lineValues.length > 0 ? lineValues[0] : "";
		     			//String quantityStr = lineValues.length > 1 ? lineValues[1] : "";
		     			String quantityUomId = lineValues.length > 2 ? lineValues[2] : "";
		     			String expireDateStr = lineValues.length > 3 ? lineValues[3] : "";
		     			String orderItemSeqId = lineValues.length > 4 ? lineValues[4] : "";
		     			//String shipGroupSeqId = lineValues.length > 5 ? lineValues[5] : "";
		     			
		     			// get order item
		     			GenericValue orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId), false);
		     			if (orderItem != null) {
		     				// Update the item expireDate
			     			if (UtilValidate.isNotEmpty(expireDateStr)) {
		                    	 Timestamp expireDate = null;
		                    	 try {
		     		            	expireDate = (Timestamp) ObjectType.simpleTypeConvert(expireDateStr, "Timestamp", null, locale);
		     		        	 } catch (Exception e) {
		         		            Debug.logWarning(e, "Problems parsing expireDate string: " + expireDateStr, module);
		     		        	 }
		                		 orderItem.put("expireDate", expireDate);
		                		 if (UtilValidate.isNotEmpty(quantityUomId))
		                		 orderItem.put("quantityUomId", quantityUomId);
		                		 toBeStored.add(orderItem);
		                         Debug.logInfo("Set item expireDate: [" + orderItemSeqId + "] " + expireDate, module);
		                         GenericValue orderItemShipGroupAssoc2 = EntityUtil.getFirst(delegator.findByAnd("OrderItemShipGroupAssoc", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId), null, false));
		                         if (orderItemShipGroupAssoc2 != null) {
		                        	 orderItemShipGroupAssoc.add(orderItemShipGroupAssoc2);
		                         }
			     			}
		     			}
		     		}
		     	}
		     }
		     if (UtilValidate.isNotEmpty(toBeStored)) {
		    	 // store the new items/adjustments/order item attributes
		         try {
		             delegator.storeAll(toBeStored);
		         } catch (GenericEntityException e) {
		             Debug.logError(e, module);
		         }
		     }
		     
		     // update shipping group
		     // make the order item object map & the ship group assoc list
	         Map<String, GenericValue> itemValuesBySeqId = new HashMap<String, GenericValue>();
	         for (GenericValue v : toBeStored) {
	             if ("OrderItem".equals(v.getEntityName())) {
	                 itemValuesBySeqId.put(v.getString("orderItemSeqId"), v);
	             }
	         }
	                 
	         // reserve the inventory
	         String productStoreId = orderHeader.getString("productStoreId");
	         String orderTypeId = orderHeader.getString("orderTypeId");
	         List<String> resErrorMessages = new LinkedList<String>();
	         try {
	             Debug.logInfo("Calling reserve inventory...", module);
	             reserveInventory(delegator, dispatcher, userLogin, locale, orderItemShipGroupAssoc, null, itemValuesBySeqId,
	                     orderTypeId, productStoreId, resErrorMessages);
	         } catch (GeneralException e) {
	             Debug.logError(e, module);
	         }
         } catch (GenericEntityException e) {
	    	 Debug.logError (e, "Get updateApprovedOrderItemsSalesUop: " + e.toString(), module);
	         return ServiceUtil.returnError("Error when get updateApprovedOrderItemsSalesUop info");
         }
		 
         /*
         // TODOCHANGE check isDistributor
         // create notify to SUP
         if (isDistributor) {
        	 Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
             List<String> partiesList = new ArrayList<String>();
     		 String header = "";
     		 String state = "open";
     		 String action = "";
     		 String targetLink = "";
     		 String ntfType = "ONE";
     		 try {
     			 List<GenericValue> listPlacingCustomerGe = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "PLACING_CUSTOMER"), null, false);
     			 if (listPlacingCustomerGe != null) {
     				 List<String> listPlacingCustomer = EntityUtil.getFieldListFromEntityList(listPlacingCustomerGe, "partyId", true);
     				 if (listPlacingCustomer != null) {
     					 for (String placingCustomer : listPlacingCustomer) {
     						 List<String> listPartyTmp = SalesPartyUtil.getListSupPersonIdByDistributor(delegator, placingCustomer);
     						 if (listPartyTmp != null) {
     							 partiesList.addAll(listPartyTmp);
     						 } 
     					 }
     				 }
     			 }
     			 header = UtilProperties.getMessage(resource_module, "DAOrderWasEditedByDistributor", UtilMisc.toMap("orderId", orderId, "userLoginId", userLogin.get("userLoginId")),locale);
     			 action = "orderView";
     			 targetLink = "orderId="+orderId;
     		 } catch (Exception e) {
     			 Debug.logError(e, "Error when set value for notify", module);
     		 }
     		 try {
     			 Map<String, Object> tmpResult = dispatcher.runSync("createNotification", 
      				UtilMisc.<String, Object>toMap("partiesList", partiesList, "header", header, "state", state, "action", action, 
      						"targetLink", targetLink, "dateTime", nowTimestamp, "ntfType", ntfType, "userLogin", userLogin));
     			 if (ServiceUtil.isError(tmpResult)) {
     				 return ServiceUtil.returnError(ServiceUtil.getErrorMessage(tmpResult));
     			 }
     		 } catch (Exception e) {
     			 Debug.logError(e, "Error when create notify", module);
     			 return ServiceUtil.returnError(UtilProperties.getMessage(resource_module_error, "DAErrorWhenCreateNotify", locale));
     		 }
         }
         */
         Map<String, Object> result = ServiceUtil.returnSuccess();
         result.put("orderId", orderId);
         return result;
     }
     
     /*
      *  Warning: loadCartForUpdate(...) and saveUpdatedCartToOrder(...) must always
      *           be used together in this sequence.
      *           In fact loadCartForUpdate(...) will remove or cancel data associated to the order,
      *           before returning the ShoppingCart object; for this reason, the cart
      *           must be stored back using the method saveUpdatedCartToOrder(...),
      *           because that method will recreate the data.
      */
     private static ShoppingCart loadCartForUpdate(LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin, String orderId) throws GeneralException {
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
         
         // New method 25/11/2014, Find and add agreement into Shopping cart
         // get applicable agreements for order entry
         EntityCondition agreementCondition = null;
         String agreementId = null;
         List<GenericValue> agreements = null;
         Map<String, Object> result2 = null;
         try {
	        	// for a sales order, orderPartyId = billToCustomer (the customer)
	            String customerPartyId = cart.getOrderPartyId();
	            String companyPartyId = cart.getBillFromVendorPartyId();
	            
	            List<String> customerPartyIds = new ArrayList<String>();
	        	if (UtilValidate.isNotEmpty(customerPartyId)) {
	        		customerPartyIds.add(customerPartyId);
	        	}
	        	List<GenericValue> customerPartyRelList = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom", customerPartyId, "roleTypeIdFrom", "CHILD_MEMBER", "roleTypeIdTo", "PARENT_MEMBER"), null, false);
	        	if (customerPartyRelList != null) {
	        		for (GenericValue customerPartyRelItem : customerPartyRelList) {
	        			List<GenericValue> customerPartyRoleItem = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", customerPartyRelItem.getString("partyIdTo"), "roleTypeId", "CUSTOMER_GROUP"), null, false);
	        			if (customerPartyRoleItem != null && customerPartyRoleItem.size() > 0) {
	        				customerPartyIds.add(customerPartyRelItem.getString("partyIdTo"));
	        			}
	        		}
	        	}
	        	
	        	if ((customerPartyRelList != null) && (customerPartyIds.size() > 0)) {
	        		// the agreement for a sales order is from the customer to us
	        		agreementCondition = EntityCondition.makeCondition(
	        				UtilMisc.toList(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, companyPartyId),
	    					EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, customerPartyIds)), EntityOperator.AND);
	        	} else {
	        	    // the agreement for a sales order is from the customer to us
	        		agreementCondition = EntityCondition.makeCondition(
	        				UtilMisc.toList(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, companyPartyId),
							EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, customerPartyId)), EntityOperator.AND);
	        	}
 	        	
 	        
 	        agreements = delegator.findList("Agreement", agreementCondition, null, null, null, true);
 	        agreements = EntityUtil.filterByDate(agreements);
         } catch (GenericEntityException e) {
        	 Debug.logError(e, module);
             throw new GeneralException(e.getMessage());
         }
         
         if (agreements != null) {
         	for (GenericValue agreementItem : agreements) {
         		if (agreementItem.containsKey("isAuto") && "Y".equals(agreementItem.getString("isAuto"))) {
         			agreementId = agreementItem.getString("agreementId");
         		}
         	}
         }
         ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
         if (UtilValidate.isNotEmpty(cart.getCurrency())) {
             cartHelper.setCurrency(cart.getCurrency());
         }
         if (UtilValidate.isNotEmpty(agreementId)) {
         	// set the agreement if specified otherwise set the currency
             if (UtilValidate.isNotEmpty(agreementId)) {
                 result2 = cartHelper.selectAgreement(agreementId);
             }
             if (ServiceUtil.isError(result2)) {
            	 Debug.logError(ServiceUtil.getErrorMessage(result2), module);
                 throw new GeneralException(ServiceUtil.getErrorMessage(result2));
             }
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
             promoItems = delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId, "isPromo", "Y"), null, false);
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

         return cart;
     }
     
     private static void saveUpdatedCartToOrder(LocalDispatcher dispatcher, Delegator delegator, ShoppingCart cart,
             Locale locale, GenericValue userLogin, String orderId, Map<String, Object> changeMap, boolean calcTax,
             boolean deleteItems) throws GeneralException {
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
         
         // Creating objects for New Shipping and Handling Charges Adjustment and Sales Tax Adjustment
         toStore.addAll(cart.makeAllShipGroupInfos());
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
                 if ("Y".equals(valueObj.getString("isPromo"))) {
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
             reserveInventory(delegator, dispatcher, userLogin, locale, orderItemShipGroupAssoc, dropShipGroupIds, itemValuesBySeqId,
                     orderTypeId, productStoreId, resErrorMessages);
         } catch (GeneralException e) {
             Debug.logError(e, module);
             throw new GeneralException(e.getMessage());
         }

         if (resErrorMessages.size() > 0) {
             throw new GeneralException(ServiceUtil.getErrorMessage(ServiceUtil.returnError(resErrorMessages)));
         }
     }
     
     public static Map<String, Object> addItemToApprovedOrderSales(DispatchContext dctx, Map<String, ? extends Object> context) {
         LocalDispatcher dispatcher = dctx.getDispatcher();
         Delegator delegator = dctx.getDelegator();
         Locale locale = (Locale) context.get("locale");
         String orderId = (String) context.get("orderId");
         GenericValue userLogin = (GenericValue) context.get("userLogin");
         //Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
 		 try {
 			 if (UtilValidate.isEmpty(orderId)) {
 				 return ServiceUtil.returnError(UtilProperties.getMessage(resource_module_error, "DAOrderIdIsEmpty", locale));
 			 }
 			 GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
 			 if (UtilValidate.isEmpty(orderHeader)) {
 				 return ServiceUtil.returnError(UtilProperties.getMessage(resource_module_error, "DANotFoundOrderWithId", UtilMisc.toMap("orderId", orderId), locale));
 			 }
 			 String expireDateStr = (String) context.get("expireDate");
 			 Timestamp expireDate = null;
 			 try {
    	        if (UtilValidate.isNotEmpty(expireDateStr)) {
    	        	Long expireDateL = Long.parseLong(expireDateStr);
    	        	expireDate = new Timestamp(expireDateL);
    	        }
 			 } catch (Exception e) {
            	//request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "DAErrorWhenFormatDateTime", locale));
            	//return "error";
            	Debug.logWarning(e, "Problems parsing expireDate string: " + expireDateStr, module);
 			 }
 			 
 			 Map<String, Object> contextMap = FastMap.newInstance();
 			 contextMap.putAll(context);
 			 contextMap.put("expireDate", expireDate);
 			 
 			 if (SalesPartyUtil.isDistributor(userLogin, delegator)) {
 				 if (!"ORDER_CREATED".equals(orderHeader.getString("statusId"))) {
 					 return ServiceUtil.returnError(UtilProperties.getMessage(resource_module_error, "DANotPermissionUpdateThisOrder", locale));
 				 }
				 Map<String, Object> result = dispatcher.runSync("appendOrderItem", contextMap);
 		 		 result.remove("shoppingCart");  //remove extra parameter
 		 		 
 		 		 /*if (ServiceUtil.isSuccess(result)) {
 		 			 // create notify to SUP
 	 		 		 List<String> partiesList = new ArrayList<String>();
 	 		 		 String header = "";
 	 		 		 String state = "open";
 	 		 		 String action = "";
 	 		 		 String targetLink = "";
 	 		 		 String ntfType = "ONE";
 	 		 		 try {
 	 	        		List<GenericValue> listPlacingCustomerGe = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "PLACING_CUSTOMER"), null, false);
 	 	        		if (listPlacingCustomerGe != null) {
 	 	     				List<String> listPlacingCustomer = EntityUtil.getFieldListFromEntityList(listPlacingCustomerGe, "partyId", true);
 	 	     				if (listPlacingCustomer != null) {
 	 	     					for (String placingCustomer : listPlacingCustomer) {
 	 	     						List<String> listPartyTmp = SalesPartyUtil.getListSupPersonIdByDistributor(delegator, placingCustomer);
 	 	     	        			if (listPartyTmp != null) {
 	 	     	        				partiesList.addAll(listPartyTmp);
 	 	     	        			} 
 	 	     					}
 	 	     				}
 	 	     			}
 	 	        		header = UtilProperties.getMessage(resource_module, "DAOrderWasEditedByDistributor", UtilMisc.toMap("orderId", orderId, "userLoginId", userLogin.get("userLoginId")),locale);
 	 	        		action = "orderView";
 	 	        		targetLink = "orderId="+orderId;
 	 		 		 } catch (Exception e) {
 	 	        		Debug.logError(e, "Error when set value for notify", module);
 	 		 		 }
 	 		 		 try {
 	 	        		Map<String, Object> tmpResult = dispatcher.runSync("createNotification", 
 	 	        				UtilMisc.<String, Object>toMap("partiesList", partiesList, "header", header, "state", state, "action", action, 
 	 	        						"targetLink", targetLink, "dateTime", nowTimestamp, "ntfType", ntfType, "userLogin", userLogin));
 	 	        		if (ServiceUtil.isError(tmpResult)) {
 	 	        			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(tmpResult));
 	 	                }
 	 		 		 } catch (Exception e) {
 	 	    			Debug.logError(e, "Error when create notify", module);
 	 	    			return ServiceUtil.returnError(UtilProperties.getMessage(resource_module_error, "DAErrorWhenCreateNotify", locale));
 	 		 		 }
 		 		 }*/
 		 		 
 		 		 return result;
 			 } else {
 				 Map<String, Object> result = dispatcher.runSync("appendOrderItem", contextMap);
		 		 result.remove("shoppingCart");  //remove extra parameter
		 		 return result;
 			 }
	     } catch (GenericServiceException e) {
	         Debug.logError (e, "Add item into order items: " + e.toString(), module);
	         return ServiceUtil.returnError(UtilProperties.getMessage(resource_module_error, "DAErrorAddItemToOrder",
	                 UtilMisc.toMap("reason", e.toString()), locale));
	     } catch (GenericEntityException e) {
	    	 Debug.logError (e, "Get orderHeader: " + e.toString(), module);
	         return ServiceUtil.returnError("Error when get orderHeader info");
		}
     }
     
     /*HUNGNC START EDIT*/
     @SuppressWarnings("unchecked")
	public static Map<String, Object> jqxGetProductListInOrder(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
     	Delegator delegator = ctx.getDelegator();
     	Map<String, Object> successResult = ServiceUtil.returnSuccess();
     	EntityListIterator listIterator = null;
 		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
     	List<String> listSortFields = (List<String>) context.get("listSortFields");
     	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
     	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
     	String orderId = parameters.get("orderId")[0];
     	try {
     		listAllConditions.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
     		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
 	    	listIterator = delegator.find("OrderItem", cond, null, null, listSortFields, opts);
 	    } catch (Exception e) {
 			String errMsg = "Fatal error calling jqGetLocationByProductId service: " + e.toString();
 			Debug.logError(e, errMsg, module);
 		}
 		successResult.put("listIterator", listIterator);
 		return successResult;
     }
 	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqxGetProductListInDelivery(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
     	Delegator delegator = ctx.getDelegator();
     	Map<String, Object> successResult = ServiceUtil.returnSuccess();
     	EntityListIterator listIterator = null;
 		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
     	List<String> listSortFields = (List<String>) context.get("listSortFields");
     	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
     	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
     	String orderId = parameters.get("orderId")[0];
     	String deliveryId = parameters.get("deliveryId")[0];
     	BigDecimal valueBig = new BigDecimal("0");
     	try {
     		listAllConditions.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
     		listAllConditions.add(EntityCondition.makeCondition("deliveryId", EntityOperator.EQUALS, deliveryId));
     		listAllConditions.add(EntityCondition.makeCondition("actualExportedQuantity", EntityOperator.GREATER_THAN, valueBig));
     		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
 	    	listIterator = delegator.find("ListProductByDeliveryItemAndOrderItem", cond, null, null, listSortFields, opts);
 	    } catch (Exception e) {
 			String errMsg = "Fatal error calling jqGetLocationByProductId service: " + e.toString();
 			Debug.logError(e, errMsg, module);
 		}
 		successResult.put("listIterator", listIterator);
 		return successResult;
     }
 	
	public static Map<String, Object> addNewProductByInventoryItem(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
     	Delegator delegator = ctx.getDelegator();
     	Map<String, Object> result = new FastMap<String, Object>();
     	String productId = (String)context.get("productId");
     	String expireDate = (String)context.get("expireDate");
     	String facilityId = (String)context.get("facilityId");
     	String quantity = (String)context.get("quantity");
     	BigDecimal quantityBig = new BigDecimal(quantity);
     	String orderId = (String)context.get("orderId");
     	String deliveryId = (String)context.get("deliveryId");
     	
     	String quantityUom = (String)context.get("uomId");
     	
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = (String) userLogin.get("partyId");
		String userLoginId = (String) userLogin.get("userLoginId");
		GenericValue inventoryItem = delegator.makeValue("InventoryItem");
		GenericValue shipmentReceipt = delegator.makeValue("ShipmentReceipt");
		String inventoryItemId = delegator.getNextSeqId("InventoryItem");;
		long expireDateLong = Long.parseLong(expireDate);
		Timestamp expireDateTime = new Timestamp(expireDateLong);
		Date dataSys = new Date();
		Timestamp dateReceived = new Timestamp(dataSys.getTime());
		/*List<GenericValue> listInventoryItem = delegator.findList("InventoryItem", EntityCondition.makeCondition(UtilMisc.toMap("expireDate", expireDateTime, "productId", productId, "facilityId", facilityId)), null, null, null, false);
		GenericValue listProductByDeliveryInOrder = delegator.findOne("ListProductByDeliveryInOrder", UtilMisc.toMap("deliveryId", deliveryId, "orderId", orderId), false);
		if(listProductByDeliveryInOrder != null){
			String productIdDataByDelivery = (String) listProductByDeliveryInOrder.get("productId");
			if(productId.equals(productIdDataByDelivery)){
				result.put("value", "exits");
			}else{*/
				GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
				BigDecimal convertNumber = ProductUtil.getConvertPackingNumber(delegator, productId, quantityUom, product.getString("quantityUomId"));
				inventoryItem.put("inventoryItemId", inventoryItemId);
				inventoryItem.put("expireDate", expireDateTime);
				inventoryItem.put("quantityOnHandTotal", quantityBig.multiply(convertNumber));
				inventoryItem.put("availableToPromiseTotal", quantityBig.multiply(convertNumber));
				inventoryItem.put("datetimeReceived", dateReceived);
				inventoryItem.put("productId", productId);
				inventoryItem.put("uomId", product.getString("quantityUomId"));
				inventoryItem.put("facilityId", facilityId);
				inventoryItem.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
				inventoryItem.put("ownerPartyId", partyId);
				
				delegator.create(inventoryItem);
				try {
					String receiptId = delegator.getNextSeqId("ShipmentReceipt");
					shipmentReceipt.put("receiptId", receiptId);
					shipmentReceipt.put("inventoryItemId", inventoryItemId);
					shipmentReceipt.put("productId", productId);
					shipmentReceipt.put("orderId", orderId);
					shipmentReceipt.put("receivedByUserLoginId", userLoginId);
					shipmentReceipt.put("quantityAccepted", quantityBig);
					shipmentReceipt.put("datetimeReceived", dateReceived);
					delegator.create(shipmentReceipt);
				} catch (GenericEntityException e) {
					result.put("value", "error");
					return result;
				}
				result.put("value", "success");
			/*}
		}else{
			inventoryItem.put("inventoryItemId", inventoryItemId);
			inventoryItem.put("expireDate", expireDateTime);
			inventoryItem.put("quantityOnHandTotal", quantityBig);
			inventoryItem.put("availableToPromiseTotal", quantityBig);
			inventoryItem.put("datetimeReceived", dateReceived);
			inventoryItem.put("productId", productId);
			inventoryItem.put("uomId", quantityUom);
			inventoryItem.put("facilityId", facilityId);
			inventoryItem.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
			inventoryItem.put("ownerPartyId", partyId);
			
			delegator.create(inventoryItem);
			try {
				String receiptId = delegator.getNextSeqId("ShipmentReceipt");
				shipmentReceipt.put("receiptId", receiptId);
				shipmentReceipt.put("inventoryItemId", inventoryItemId);
				shipmentReceipt.put("productId", productId);
				shipmentReceipt.put("orderId", orderId);
				shipmentReceipt.put("receivedByUserLoginId", userLoginId);
				shipmentReceipt.put("quantityAccepted", quantityBig);
				shipmentReceipt.put("datetimeReceived", dateReceived);
				delegator.create(shipmentReceipt);
			} catch (GenericEntityException e) {
				result.put("value", "error");
				return result;
			}
			result.put("value", "success");
		}*/
		// update delivery status 
		GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
		if (delivery != null){
			delivery.put("statusId", "DLV_CONFIRMED");
			delegator.store(delivery);
			List<GenericValue> listDeliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
			if (!listDeliveryItems.isEmpty()){
				for (GenericValue item : listDeliveryItems){
					item.put("statusId", "DELI_ITEM_CONFIRMED");
					delegator.store(item);
				}
			}
		} else {
			ServiceUtil.returnError("Delivery not found!");
		}
		
 		return result;
    }
 	
 	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqxGetProductListInInventoryItem(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
     	Delegator delegator = ctx.getDelegator();
     	Map<String, Object> successResult = ServiceUtil.returnSuccess();
     	EntityListIterator listIterator = null;
 		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
     	List<String> listSortFields = (List<String>) context.get("listSortFields");
     	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
     	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
     	//GenericValue userLogin = (GenericValue) context.get("userLogin");
     	String orderId = null;
     	if (parameters != null && parameters.get("orderId") != null && parameters.get("orderId").length > 0){
     		orderId = parameters.get("orderId")[0];
     	}
     	if (orderId != null && "".equals(orderId)){
     		listAllConditions.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
     	}
     	try {
     		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
 	    	listIterator = delegator.find("ListProductByOrderInShipmentReceipt", cond, null, null, listSortFields, opts);
 	    } catch (Exception e) {
 			String errMsg = "Fatal error calling jqGetLocationByProductId service: " + e.toString();
 			Debug.logError(e, errMsg, module);
 		}
 		successResult.put("listIterator", listIterator);
 		return successResult;
    }
 	public static Map<String, Object> loadFacilityIdByPartyId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = (String) userLogin.get("partyId");
		List<GenericValue> listFacility = delegator.findList("Facility", EntityCondition.makeCondition(UtilMisc.toMap("ownerPartyId", partyId)), null, null, null, false);
		result.put("listFacility", listFacility);
		return result;
	}
 	public static Map<String, Object> checkProductInInventoryItemExists(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
     	Delegator delegator = ctx.getDelegator();
     	Map<String, Object> result = new FastMap<String, Object>();
     	String productId = (String)context.get("productId");
     	String expireDate = (String)context.get("expireDate");
     	String facilityId = (String)context.get("facilityId");
     	long expireDateLong = Long.parseLong(expireDate);
		Timestamp expireDateTime = new Timestamp(expireDateLong);
		List<GenericValue> listInventoryItem = delegator.findList("InventoryItem", EntityCondition.makeCondition(UtilMisc.toMap("expireDate", expireDateTime, "productId", productId, "facilityId", facilityId)), null, null, null, false);
		if(listInventoryItem.isEmpty()){
			result.put("value", "exits");
		}else{
			result.put("value", "notExits");
		}
 		return result;
    }
 	public static Map<String, Object> loadListProduct(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		List<GenericValue> listProduct = delegator.findList("Product", EntityCondition.makeCondition(UtilMisc.toMap("productTypeId", "FINISHED_GOOD")), null, null, null, false);
		result.put("listProduct", listProduct);   
		return result;
	}
 	
 	public static Map<String, Object> loadUomIdByProductId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		List<GenericValue> listConfigPacking = delegator.findList("ConfigPacking", null, null, null, null, false);
		Set<String> setProductId = FastSet.newInstance();
		for (GenericValue configPacking : listConfigPacking) {
			String productIdStr = (String) configPacking.get("productId");
			setProductId.add(productIdStr);
		}
		Map<String, Object> mapProductWithUom = new HashMap<String, Object>();
		for (String string : setProductId) {
			Set<String> setUomId = FastSet.newInstance();
			for (GenericValue configPacking : listConfigPacking) {
				String productIdStr = (String) configPacking.get("productId");
				if(string.equals(productIdStr)){
					String uomFromId = (String) configPacking.get("uomFromId");
					String uomToId = (String) configPacking.get("uomToId");
					setUomId.add(uomFromId);
					setUomId.add(uomToId);
				}
			}
			mapProductWithUom.put(string, setUomId);
		}
		result.put("mapProductWithUom", mapProductWithUom);   
		return result;
	}
 	public static Map<String, Object> fecthDeliveryIdByOrderId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		String orderId = (String)context.get("orderId");
		List<GenericValue> listDelivery  = new ArrayList<GenericValue>();
		List<GenericValue> listDeliveries  = new ArrayList<GenericValue>();
		GenericValue order = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
		if (order != null){
			String orderStatus = order.getString("statusId");
			if (orderStatus != null){
				listDelivery = delegator.findList("Delivery", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
				if (!listDelivery.isEmpty()){
					for (GenericValue dlv : listDelivery){
						String deliveryStatus = dlv.getString("statusId");
						if (deliveryStatus != null && "DLV_EXPORTED".equals(deliveryStatus)){
							String shipmentId = dlv.getString("shipmentId");
							if (shipmentId != null){
								GenericValue shipment = delegator.findOne("Shipment", false, UtilMisc.toMap("shipmentId", shipmentId));
								if (shipment != null){
									String deliveryEntryId = shipment.getString("deliveryEntryId");
									if (deliveryEntryId != null){
										GenericValue deliveryEntry = delegator.findOne("DeliveryEntry", false, UtilMisc.toMap("deliveryEntryId", deliveryEntryId));
										if (deliveryEntry != null){
											String statusEntry = deliveryEntry.getString("statusId");
											if (statusEntry != null && "DELI_ENTRY_SHIPED".equals(statusEntry)){
												listDeliveries.add(dlv);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		Map<String, Object> result = new FastMap<String, Object>();
		result.put("listDelivery", listDeliveries);
		return result;
	}
 	/*HUNGNC END EDIT*/
 	
 	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListOrder(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Security security = ctx.getSecurity();
		
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	try {
    		//check permission for each order type
			if (!security.hasPermission("DELYS_ORDER_VIEW", userLogin)) {
				Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to run manual payment transaction!", module);
	            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "DATransactionNotAuthorized", locale));
			}
    		
    		Map<String, Object> tmpResult = dispatcher.runSync("getListStoreCompanyViewedByUserLogin", UtilMisc.toMap("userLogin", userLogin));
    		if (ServiceUtil.isError(tmpResult)) return tmpResult;
    		List<GenericValue> listStore = (List<GenericValue>) tmpResult.get("listProductStore");
    		List<String> productStoreIds = null;
    		if (UtilValidate.isNotEmpty(listStore)) {
    			productStoreIds = EntityUtil.getFieldListFromEntityList(listStore, "productStoreId", true);
    		}
    		
    		String partyId = null;
    		String statusId = null;
    		if (parameters.containsKey("statusId") && parameters.get("statusId").length > 0) {
    			statusId = parameters.get("statusId")[0];
			}
			if (parameters.containsKey("partyId") && parameters.get("partyId").length > 0) {
				partyId = parameters.get("partyId")[0];
			}
			listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
			if (SalesPartyUtil.isDistributor(userLogin, delegator)) {
				listAllConditions.add(EntityCondition.makeCondition("customerId", EntityOperator.EQUALS, userLogin.getString("partyId")));
			} else if (SalesPartyUtil.isSupervisorEmployee(userLogin, delegator)) {
				EntityFindOptions findOptions = new EntityFindOptions();
				findOptions.setDistinct(true);
				List<String> listPartyId = SalesPartyUtil.getListDistOrCustomerDirectIdBySup(delegator, userLogin.getString("partyId"), null, null, findOptions);
				if (UtilValidate.isNotEmpty(listPartyId)) {
					if (partyId != null) {
						if (!listPartyId.contains(partyId)) {
							successResult.put("listIterator", listIterator);
					    	return successResult;
						} else {
							listAllConditions.add(EntityCondition.makeCondition("customerId", EntityOperator.EQUALS, partyId));
						}
					} else {
						listAllConditions.add(EntityCondition.makeCondition("customerId", EntityOperator.IN, listPartyId));
					}
				}
			}
			if (statusId != null) {
				listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
			}
			listSortFields.add("orderDate DESC");
			if (UtilValidate.isNotEmpty(productStoreIds)) {
				listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
		        listIterator = delegator.find("OrderHeaderAndOrderRoleFromTo", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListOrder service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
 	
 	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductSalesCond(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	//EntityListIterator listIterator = null;
    	List<Map<String, Object>> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			List<GenericValue> listProduct = null;
			List<EntityCondition> listCondTypeOr = FastList.newInstance();
			listCondTypeOr.add(EntityCondition.makeCondition("productTypeId", "FINISHED_GOOD"));
			listAllConditions.add(EntityCondition.makeCondition(listCondTypeOr, EntityOperator.OR));
			EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			if (parameters.containsKey("orderId") && parameters.get("orderId").length > 0) {
				String orderId = parameters.get("orderId")[0];
				if (UtilValidate.isNotEmpty(orderId)) {
					listAllConditions.add(EntityCondition.makeCondition("orderId", orderId));
					listAllConditions.add(EntityCondition.makeCondition("isPromo", "N"));
					listProduct = delegator.findList("OrderItemAndProduct", tmpConditon, null, listSortFields, opts, false);
				}
			} else {
				listProduct = delegator.findList("Product", tmpConditon, null, listSortFields, opts, false);
			}
			if (UtilValidate.isNotEmpty(listProduct)) {
				for (GenericValue itemProd : listProduct) {
					Map<String, Object> row = FastMap.newInstance();
					row.put("productId", itemProd.get("productId"));
    				row.put("productName", itemProd.get("productName"));
    				row.put("productPackingUomId", itemProd.get("productPackingUomId"));
    				row.put("quantityUomIdRequire", itemProd.get("productPackingUomId"));
    				row.put("internalName", itemProd.getString("internalName"));
    				row.put("quantityUomId", itemProd.getString("quantityUomId"));
    				if (itemProd.containsKey("expireDate")) row.put("expireDate", itemProd.getTimestamp("expireDate"));
    				if (itemProd.containsKey("quantity")) row.put("quantityOrd", itemProd.getBigDecimal("quantity"));
    				if (itemProd.containsKey("orderId")) row.put("orderId", itemProd.getString("orderId"));
    				if (itemProd.containsKey("orderItemSeqId")) row.put("orderItemSeqId", itemProd.getString("orderItemSeqId"));
    				if (UtilValidate.isNotEmpty(itemProd.get("alternativeQuantity"))) {
    					row.put("quantityUomIdOrd", itemProd.getString("quantityUomIdOrd"));
        				row.put("alternativeQuantityOrd", itemProd.getBigDecimal("alternativeQuantity"));
        				row.put("quantityUomIdRequire", itemProd.get("quantityUomIdOrd"));
    				}
    				
					// column: packingUomId
    				EntityCondition condsItem = EntityCondition.makeCondition(UtilMisc.toMap("productId", itemProd.get("productId"), "uomToId", itemProd.get("quantityUomId")));
    				EntityFindOptions optsItem = new EntityFindOptions();
    				
					List<GenericValue> listConfigPacking = FastList.newInstance();
    				listConfigPacking.addAll(delegator.findList("ConfigPackingAndUom", condsItem, null, null, optsItem, false));
					List<Map<String, Object>> listQuantityUomIdByProduct = new ArrayList<Map<String, Object>>();
					for (GenericValue conPackItem : listConfigPacking) {
						Map<String, Object> packingUomIdMap = FastMap.newInstance();
						packingUomIdMap.put("description", conPackItem.getString("descriptionFrom"));
						packingUomIdMap.put("uomId", conPackItem.getString("uomFromId"));
						listQuantityUomIdByProduct.add(packingUomIdMap);
					}
					GenericValue quantityUom = delegator.findOne("Uom", UtilMisc.toMap("uomId", itemProd.get("quantityUomId")), false);
					if (quantityUom != null) {
						Map<String, Object> packingUomIdMap = FastMap.newInstance();
						packingUomIdMap.put("description", quantityUom.getString("description"));
						packingUomIdMap.put("uomId", quantityUom.getString("uomId"));
						listQuantityUomIdByProduct.add(packingUomIdMap);
					}
    				row.put("packingUomId", listQuantityUomIdByProduct);
    				listIterator.add(row);
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductSales service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
}
