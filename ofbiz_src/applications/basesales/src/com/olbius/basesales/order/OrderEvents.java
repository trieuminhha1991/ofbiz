package com.olbius.basesales.order;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.CheckOutEvents;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartHelper;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.order.shoppingcart.shipping.ShippingEvents;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.shoppingcart.CheckOutWithoutAccTransEvents;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.security.util.SecurityUtil;

public class OrderEvents {
	public static final String module = OrderEvents.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
    public static final String resource_order_origin = "OrderUiLabels";
	
	public static String initializeSalesOrderEntry(HttpServletRequest request, HttpServletResponse response) {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	HttpSession session = request.getSession();
    	List<String> alertMessageList = FastList.newInstance();
    	Locale locale = UtilHttp.getLocale(request);
        org.ofbiz.order.shoppingcart.ShoppingCartEvents.destroyCart(request, response);
        
		String orderId = request.getParameter("orderId");
		String orderName = request.getParameter("orderName");
		String customerId = request.getParameter("partyId");
		String productStoreId = request.getParameter("productStoreId");
		String desiredDeliveryDate = request.getParameter("desiredDeliveryDate");
		String shipAfterDate = request.getParameter("shipAfterDate");
		String shipBeforeDate = request.getParameter("shipBeforeDate");
		/*String shipToCustomerPartyId = request.getParameter("shipToCustomerPartyId");
		String shippingContactMechId = request.getParameter("shippingContactMechId");
		String shippingMethodTypeId = request.getParameter("shippingMethodTypeId");
		String checkOutPaymentId = request.getParameter("checkOutPaymentId");*/
        
		String listProd = request.getParameter("listProd");
		
		request.setAttribute("may_split", "false");
		request.setAttribute("is_gift", "false");
		
		JSONArray jsonArray = new JSONArray();
		if(UtilValidate.isNotEmpty(listProd)){
			jsonArray = JSONArray.fromObject(listProd);
		}
		List<Map<String, Object>> listProduct = FastList.newInstance();
		if (jsonArray != null && jsonArray.size() > 0) {
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject prodItem = jsonArray.getJSONObject(i);
				Map<String, Object> productItem = FastMap.newInstance();
				productItem.put("productId", prodItem.getString("productId"));
				productItem.put("quantityUomId", prodItem.getString("quantityUomId"));
				productItem.put("quantityStr", prodItem.getString("quantity"));
				productItem.put("quantityReturnPromoStr", prodItem.getString("quantityReturnPromo"));
				listProduct.add(productItem);
			}
		}
		
    	boolean resultCheckValidParams = checkValidateOrderEntry(request, response, locale);
    	if (!resultCheckValidParams) return "error";
    	
    	session.setAttribute("productStoreId", productStoreId);
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
    	
        GenericValue productStore = null;
        if (UtilValidate.isNotEmpty(productStoreId)) {
            productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
        }
        
    	String result1 = initializeOrderInfoGeneral(request, delegator, locale, alertMessageList, cart, productStore, 
    			customerId, orderId, orderName, desiredDeliveryDate, shipAfterDate, shipBeforeDate, listProduct);
    	if ("error".equals(result1)) return result1;
    	
    	// TODOCHANGE usePriceWithTax promo condition
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	ProductPromoWorker.doPromotions(cart, dispatcher);
    	// end
    	
    	String requestFavorDelivery = request.getParameter("requestFavorDelivery");
        if (requestFavorDelivery != null) {
        	if ("true".equals(requestFavorDelivery)) {
        		String result4 = initRequestFavorDelivery(request, response, alertMessageList, delegator, cart);
            	if ("error".equals(result4)) return result4;
        	}
        }
    	
        // check payToParty has account yes/no
        String payToParty = cart.getBillFromVendorPartyId();
        GenericValue payToPartyAcctgPreference = null;
        try {
			payToPartyAcctgPreference = delegator.findOne("PartyAcctgPreference", UtilMisc.toMap("partyId", payToParty), false);
		} catch (GenericEntityException e) {
        	Debug.logWarning("Error when select payToParty from PartyAcctgPreference", module);
		}
        if (payToPartyAcctgPreference != null) {
        	// old code
        	String result3 = initializeOrderCheckoutOption(request, response, alertMessageList);
        	if ("error".equals(result3)) return result3;
        } else {
        	// new code
        	String result3 = initializeOrderCheckoutOptionWithoutAccTrans(request, response, alertMessageList);
        	if ("error".equals(result3)) return result3;
        }
        
    	String internalOrderNotes = request.getParameter("internal_order_notes");
    	String shippingNotes = request.getParameter("shippingNotes");
    	if (UtilValidate.isNotEmpty(internalOrderNotes)) {
            cart.addInternalOrderNote(internalOrderNotes);
        }
    	// Shipping Notes for order will be public
        if (UtilValidate.isNotEmpty(shippingNotes)) {
            cart.addOrderNote(shippingNotes);
        }
        // shipping instructions => add to global notes
    	String shippingInstructions = request.getParameter("shipping_instructions");
    	if (UtilValidate.isNotEmpty(shippingInstructions)) {
    		cart.addOrderNote(shippingInstructions);
    	}
    	
    	if (UtilValidate.isNotEmpty(alertMessageList)) {
        	request.setAttribute("_ERROR_MESSAGE_LIST_", alertMessageList);
        }
    	
        return "success";
    }
	
	public static String initRequestFavorDelivery(HttpServletRequest request, HttpServletResponse response, List<String> alertMessageList, Delegator delegator, ShoppingCart cart) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String shippingContactMechId = null;
		
		// org.ofbiz.order.shoppingcart.CheckOutEvents.finalizeOrderEntry(HttpServletRequest, HttpServletResponse)
		// Reassign items requiring drop-shipping to new or existing drop-ship groups
        // init
        try {
            cart.createDropShipGroups(dispatcher);
        } catch (CartItemModifyException e) {
            Debug.logError(e, module);
        }
        
        CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);

        // ship
        Map<String, Object> callResult = ServiceUtil.returnSuccess();
        List<String> errorMessages = new ArrayList<String>();
        Map<String, Object> errorMaps = new HashMap<String, Object>();
        shippingContactMechId = request.getParameter("shipping_contact_mech_id");
        if (shippingContactMechId == null) {
            shippingContactMechId = (String) request.getAttribute("contactMechId");
        }
        String supplierPartyId = request.getParameter("favorSupplierPartyId");
        String facilityId = request.getParameter("shipGroupFacilityId");
        for (int shipGroupIndex = 0; shipGroupIndex < cart.getShipGroupSize(); shipGroupIndex++) {
            // set the shipping method
            // Old: shippingContactMechId = request.getParameter(shipGroupIndex + "_shipping_contact_mech_id");
        	// if (shippingContactMechId == null) {
            	// shippingContactMechId = (String) request.getAttribute("contactMechId");
        	// }
        	// Old: String supplierPartyId = request.getParameter(shipGroupIndex + "_supplierPartyId");
            // Old: String facilityId = request.getParameter(shipGroupIndex + "_shipGroupFacilityId");
            if (UtilValidate.isNotEmpty(facilityId)) {
                cart.setShipGroupFacilityId(shipGroupIndex, facilityId);
            }
            callResult = checkOutHelper.finalizeOrderEntryShip(shipGroupIndex, shippingContactMechId, supplierPartyId);
            ServiceUtil.addErrors(errorMessages, errorMaps, callResult);
        }
        //See whether we need to return an error or not
        callResult = ServiceUtil.returnSuccess();
        if (errorMessages.size() > 0) {
            callResult.put(ModelService.ERROR_MESSAGE_LIST,  errorMessages);
            callResult.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
        }
        if (errorMaps.size() > 0) {
            callResult.put(ModelService.ERROR_MESSAGE_MAP, errorMaps);
            callResult.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
        }
        // generate any messages required
        ServiceUtil.getMessages(request, callResult, null);
        // determine whether it was a success or not
        if (callResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR)) {
            return "shipping";
        }
        
		return "success";
	}
	
	public static boolean checkValidateOrderEntry(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        List<Object> errMsgList = FastList.newInstance();
        if (UtilValidate.isEmpty(request.getParameter("productStoreId"))) {
        	errMsgList.add(UtilProperties.getMessage(resource_error,"BSProductStoreNotBeEmpty", locale));
        }
        if (UtilValidate.isEmpty(request.getParameter("partyId"))) {
        	errMsgList.add(UtilProperties.getMessage(resource_error,"BSCustomerMustNotBeEmpty", locale));
        }
        /*if (UtilValidate.isEmpty(request.getParameter("currencyUomId"))) {
        	errMsgList.add(UtilProperties.getMessage(resource_error, "BSCurrencyUomIdMustNotBeEmpty", locale));
        }*/
        if (UtilValidate.isEmpty(request.getParameter("desiredDeliveryDate")) 
        		&& UtilValidate.isEmpty(request.getParameter("shipAfterDate")) 
        		&& UtilValidate.isEmpty(request.getParameter("shipBeforeDate"))) {
        	errMsgList.add(UtilProperties.getMessage(resource_error,"BSDesiredDeliveryDateMustNotBeEmpty", locale));
        }
        if (UtilValidate.isNotEmpty(errMsgList)) {
        	request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
        	return false;
        }
        
    	return true;
    }
	
	public static String initializeOrderInfoGeneral(HttpServletRequest request, Delegator delegator, Locale locale, List<String> alertMessageList, 
			ShoppingCart cart, GenericValue productStore, String customerId, String orderId, String orderName, String desiredDeliveryDateStr,
			String shipAfterDateStr, String shipBeforeDateStr, List<Map<String, Object>> listProduct) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        Security security = (Security) request.getAttribute("security");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        
        // TODO: re-factor and move this inside the ShoppingCart constructor
        String orderMode = "SALES_ORDER";
        cart.setOrderType(orderMode);
        session.setAttribute("orderMode", orderMode);
        
        // check the selected product store
        if (productStore != null) {
        	String productStoreId = productStore.getString("productStoreId");
        	
            // check permission for taking the order
            boolean hasPermission = false;
            if (cart.getOrderType().equals("SALES_ORDER")) {
            	hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "CREATE", "ENTITY", "SALESORDER");
                if (!hasPermission) {
                    // if the user is a rep of the store, then he also has permission
                    List<GenericValue> storeReps = null;
                    try {
                        storeReps = delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("productStoreId", productStore.getString("productStoreId"), "partyId", userLogin.getString("partyId"), "roleTypeId", "SALES_REP"), null, false);
                    } catch (GenericEntityException gee) {
                    	Debug.logError(gee.getMessage(), module);
                    }
                    storeReps = EntityUtil.filterByDate(storeReps);
                    if (UtilValidate.isNotEmpty(storeReps)) {
                        hasPermission = true;
                    }
                }
                if (!hasPermission) {
                	if (UtilValidate.isNotEmpty(userLogin.getString("partyId")) && customerId != null && customerId.equals(userLogin.getString("partyId"))) {
                		if (SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "DIS_PURCHORDER_NEW")) {
                    		try {
                        		String roleCustomer = EntityUtilProperties.getPropertyValue(SalesUtil.RESOURCE_PROPERTIES, "role.customer.in.store", delegator);
                        		List<EntityCondition> psrCustomerConds = FastList.newInstance();
                        		psrCustomerConds.add(EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", productStoreId, "partyId", userLogin.getString("partyId"), "roleTypeId", roleCustomer)));
                        		psrCustomerConds.add(EntityUtil.getFilterByDateExpr());
                        		List<GenericValue> psrCustomers = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(psrCustomerConds, EntityOperator.AND), null, null, null, false);
                        		if (UtilValidate.isNotEmpty(psrCustomers)) {
                        			hasPermission = true;
                        		}
                        	} catch (GenericEntityException gee) {
                            	Debug.logError(gee.getMessage(), module);
                            }
                    	}
                	} else {
                		if (!hasPermission) hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "DIS_SALESORDER_NEW");
                	}
                }
            }

            if (hasPermission) {
                cart = ShoppingCartEvents.getCartObject(request, null, productStore.getString("defaultCurrencyUomId"));
            } else {
                request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderYouDoNotHavePermissionToTakeOrdersForThisStore", locale));
                cart.clear();
                session.removeAttribute("orderMode");
                return "error";
            }
            cart.setProductStoreId(productStoreId);
        } else {
            cart.setProductStoreId(null);
        }
        
        if ("SALES_ORDER".equals(cart.getOrderType()) && UtilValidate.isEmpty(cart.getProductStoreId())) {
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderAProductStoreMustBeSelectedForASalesOrder", locale));
            cart.clear();
            session.removeAttribute("orderMode");
            return "error";
        }
        String salesChannelEnumId = "WEB_SALES_CHANNEL"; //fix request.getParameter("salesChannelEnumId");
        cart.setChannelType(salesChannelEnumId);
        
        String originOrderId = request.getParameter("originOrderId");
        cart.setAttribute("originOrderId", originOrderId);
        
        // set party info
        String partyId = customerId;
        if (partyId != null) {
            if (UtilValidate.isNotEmpty(partyId)) {
                GenericValue thisParty = null;
                try {
                    thisParty = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
                } catch (GenericEntityException gee) {
                    //
                }
                if (thisParty == null) {
                    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderCouldNotLocateTheSelectedParty", locale));
                    return "error";
                } else {
                    cart.setOrderPartyId(partyId);
                }
            } else if (partyId.length() == 0) {
                cart.setOrderPartyId("_NA_");
                partyId = null;
            }
        } else {
            partyId = cart.getPartyId();
            if (partyId != null && partyId.equals("_NA_")) partyId = null;
        }
        
        if (UtilValidate.isNotEmpty(cart)) {
        	// STEP 2: ShoppingCartEvents.java - method setOrderCurrencyAgreementShipDates
            // request method for setting the currency, agreement, OrderId and shipment dates at once
        	// have parameters: agreementId, shipBeforeDateStr, shipAfterDateStr,
            // not get parameters: workEffortId, cancelBackOrderDateStr,
            // move up: currencyUomId
            ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
            String currencyUomId = productStore.getString("defaultCurrencyUomId");
            String correspondingPoId = request.getParameter("correspondingPoId");
            Map<String, Object> result = null;
            if (UtilValidate.isNotEmpty(cart.getCurrency()) && UtilValidate.isNotEmpty(currencyUomId)) {
                result = cartHelper.setCurrency(currencyUomId);
            }
            if (ServiceUtil.isError(result)) {
                request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(result));
                return "error";
            }
            if (UtilValidate.isNotEmpty(orderId)) {
                GenericValue thisOrder = null;
                try {
                    thisOrder = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
                } catch (GenericEntityException e) {
                    Debug.logError(e.getMessage(), module);
                }
                if (thisOrder == null) {
                    cart.setOrderId(orderId);
                } else {
                    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage("OrderErrorUiLabels", "OrderIdAlreadyExistsPleaseChooseAnother", locale));
                    return "error";
                }
            }
            cart.setOrderName(orderName);
            cart.setPoNumber(correspondingPoId);
            // End STEP 1 -----------------------------------------------
            
            // get applicable agreements for order entry
            // for a sales order, orderPartyId = billToCustomer (the customer)
            // ... view "initializeOrderEntry" method in this file ...
            // the agreement for a sales order is from the customer to us
            // ...
            // catalog id collection, current catalog id and name
            // ...
            
            Timestamp desiredDeliveryDate = null;
            Timestamp shipAfterDate = null;
            Timestamp shipBeforeDate = null;
            try {
    	        if (UtilValidate.isNotEmpty(desiredDeliveryDateStr)) {
    	        	Long desiredDeliveryDateL = Long.parseLong(desiredDeliveryDateStr);
    	        	desiredDeliveryDate = new Timestamp(desiredDeliveryDateL);
    	        }
    	        if (UtilValidate.isNotEmpty(shipAfterDateStr)) {
    	        	Long shipAfterDateL = Long.parseLong(shipAfterDateStr);
    	        	shipAfterDate = new Timestamp(shipAfterDateL);
    	        }
    	        if (UtilValidate.isNotEmpty(shipBeforeDateStr)) {
    	        	Long shipBeforeDateL = Long.parseLong(shipBeforeDateStr);
    	        	shipBeforeDate = new Timestamp(shipBeforeDateL);
    	        }
            } catch (Exception e) {
            	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSErrorWhenFormatDateTime", locale));
            	return "error";
            }
            if (UtilValidate.isNotEmpty(desiredDeliveryDate)) cart.setDefaultItemDeliveryDate(desiredDeliveryDate.toString());
            if (UtilValidate.isNotEmpty(shipAfterDate)) cart.setDefaultShipAfterDate(shipAfterDate);
            if (UtilValidate.isNotEmpty(shipBeforeDate)) cart.setDefaultShipBeforeDate(shipBeforeDate);
            
            // New method 25/11/2014, Find and add agreement into Shopping cart
            // Update method 19/02/2016
            // get applicable agreements for order entry
            String agreementId = request.getParameter("agreementId");
            if (!UtilValidate.isNotEmpty(agreementId)) {
            	// default select
            	agreementId = null;
            	try {
            		List<EntityCondition> agreementConds = new ArrayList<EntityCondition>();
            		agreementConds.add(EntityUtil.getFilterByDateExpr());
	            	if ("SALES_ORDER".equals(cart.getOrderType())) {
	            		// for a sales order, orderPartyId = billToCustomer (the customer)
	    	            String customerPartyId = cart.getOrderPartyId();
	    	            String companyPartyId = cart.getBillFromVendorPartyId();
	    	            
	    	            List<String> customerPartyIds = new ArrayList<String>();
	    	        	if (UtilValidate.isNotEmpty(customerPartyId)) {
	    	        		customerPartyIds.add(customerPartyId);
	    	        	}
	    	        	List<String> customerGroupIds = EntityUtil.getFieldListFromEntityList(
	    	        					delegator.findByAnd("PartyRelationship", UtilMisc.<String, Object>toMap("partyIdTo", customerPartyId, "roleTypeIdFrom", "PARENT_MEMBER", "roleTypeIdTo", "CHILD_MEMBER", "partyRelationshipTypeId", "GROUP_ROLLUP"), null, false), 
    	        						"partyIdFrom", true);
	    	        	if (UtilValidate.isNotEmpty(customerGroupIds)) {
	    	        		//customerPartyIds.addAll(customerGroupIds);
	    	        		
	    	        		// check party type
	    	        		for (String customerGroupId : customerGroupIds) {
	    	        			GenericValue customerGroupRole = delegator.findOne("Party", UtilMisc.toMap("partyId", customerGroupId), false);
	    	        			if (customerGroupRole != null && "CUSTOMER_GROUP".equals(customerGroupRole.getString("partyTypeId"))) {
	    	        				customerPartyIds.add(customerGroupId);
	    	        			}
	    	        		}
	    	        		// check party role
	    	        		/*for (String customerGroupId : customerGroupIds) {
	    	        			GenericValue customerGroupRole = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", customerGroupId, "roleTypeId", "CUSTOMER_GROUP"), false);
	    	        			if (UtilValidate.isNotEmpty(customerGroupRole)) {
	    	        				customerPartyIds.add(customerGroupId);
	    	        			}
	    	        		}*/
	    	        	}
	    	        	if (UtilValidate.isNotEmpty(customerGroupIds)) {
	    	        		// the agreement for a sales order is from the customer group to company
	    	        		agreementConds.add(EntityCondition.makeCondition(
	    	        				UtilMisc.toList(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, companyPartyId),
	    	    					EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, customerPartyIds)), EntityOperator.AND));
	    	        	} else {
	    	        	    // the agreement for a sales order is from the customer to company
	    	        		agreementConds.add(EntityCondition.makeCondition(
	    	        				UtilMisc.toList(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, companyPartyId),
	    							EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, customerPartyId)), EntityOperator.AND));
	    	        	}
	    	        	/*agreementRoleCondition = EntityCondition.makeCondition(
	    	        			UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, customerPartyId),
	    	                    EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CUSTOMER")), EntityOperator.AND);*/
	            	}
	            	List<GenericValue> agreements = delegator.findList("Agreement", EntityCondition.makeCondition(agreementConds, EntityOperator.AND), null, null, null, true);
	    	        // List<GenericValue> agreementRoles = delegator.findList("AgreementRole", agreementRoleCondition, null, null, null, true);
	            	
	                if (agreements != null) {
	                	for (GenericValue agreementItem : agreements) {
	                		if (agreementItem.containsKey("isAuto") && "Y".equals(agreementItem.getString("isAuto"))) {
	                			agreementId = agreementItem.getString("agreementId");
	                			break;
	                		}
	                	}
	                }
	            } catch (GenericEntityException e) {
	                request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
	                return "error";
	            }
            }
            // set the agreement if specified otherwise set the currency
            if (UtilValidate.isNotEmpty(agreementId)) {
                Map<String, Object> resultAgreement = cartHelper.selectAgreement(agreementId);
                if (ServiceUtil.isError(resultAgreement)) {
                    request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(resultAgreement));
                    return "error";
                }
            }
            // END TODOCHANGE
            
            // TODOCHANGE add new attribute: "salesMethodChannelEnumId", "salesExecutiveId", "isFavorDelivery"
            /*String salesMethodChannelEnumId = null;
            if (productStore != null && productStore.containsKey("salesMethodChannelEnumId")) salesMethodChannelEnumId = productStore.getString("salesMethodChannelEnumId");
            if (salesMethodChannelEnumId != null) {
                cart.setAttribute("salesMethodChannelEnumId", salesMethodChannelEnumId);
            }*/
            String salesExecutiveId = request.getParameter("salesExecutiveId");
            if (salesExecutiveId != null) {
            	cart.setAttribute("salesExecutiveId", salesExecutiveId);
            }
            String requestFavorDelivery = request.getParameter("requestFavorDelivery");
            if (requestFavorDelivery != null) {
            	if ("true".equals(requestFavorDelivery)) cart.setAttribute("isFavorDelivery", "Y");
            	else cart.setAttribute("isFavorDelivery", "N");
            } else {
            	cart.setAttribute("isFavorDelivery", "N");
            }
            String favorSupplierPartyId = request.getParameter("favorSupplierPartyId");
            if (favorSupplierPartyId != null) {
            	cart.setAttribute("favorSupplierPartyId", favorSupplierPartyId);
            }
            String shipGroupFacilityId = request.getParameter("shipGroupFacilityId");
            if (shipGroupFacilityId != null) {
            	cart.setAttribute("shipGroupFacilityId", shipGroupFacilityId);
            }
            
            // STEP 3: ShoppingCartEvents.java - method addToCart
            // Event to add an item to the shopping cart
            // not get parameters: controlDirective, xxx, parentProductId, itemType, itemDescription, productCategoryId, xxx, 
            //				price(BigDecimal), xxx, quantity(BigDecimal), reservStartStr, reservEndStr, reservStart(Timestamp), reservEnd(Timestamp),
            //				reservLengthStr, reservLength(BigDecimal), reservPersonsStr, reservPersons(BigDecimal), accommodationMapId, accommodationSpotId, 
            //				xxx, xxx, shipBeforeDate(Timestamp), shipAfterDate(Timestamp), numberOfDay
            // use parameters: productId, priceStr, quantityStr, shipBeforeDateStr, shipAfterDateStr
            String catalogId = CatalogWorker.getCurrentCatalogId(request);
            
            // remove paramMap (UtilHttp.getCombinedMap(request))
            
            // Call multiple addToCart method
            
            for (Map<String, Object> productItem : listProduct) {
            	String productId = (String) productItem.get("productId");
    			String quantityUomId = (String) productItem.get("quantityUomId");
    			String quantityStr = (String) productItem.get("quantityStr");
    			String quantityReturnPromoStr = (String) productItem.get("quantityReturnPromoStr");
    			
    			BigDecimal price = null;
    			BigDecimal quantity = BigDecimal.ZERO;
    			//BigDecimal alternativeQuantity = null;

    			BigDecimal priceReturnPromo = null;
    			BigDecimal quantityReturnPromo = BigDecimal.ZERO;
    			//BigDecimal alternativeQuantityReturnPromo = null;
    			
    			if (UtilValidate.isNotEmpty(productId) && (UtilValidate.isNotEmpty(quantityStr) || UtilValidate.isNotEmpty(quantityReturnPromoStr))) {
    				// Check quantityUomId with productQuotation
    				/*BigDecimal quantityUomIdToDefault = BigDecimal.ONE;
					GenericValue product = null;
					try {
						product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
						if (product == null) {
							alertMessageList.add(UtilProperties.getMessage(resource_error, "BSProductNotExists",locale));
							continue;
						}
    		        } catch (Exception e) {
    		            Debug.logWarning(e, "Problems [product not exists] get productId = " + productId, module);
    		        }
					if (product.getString("quantityUomId") != null) {
						if (!quantityUomId.equals(product.getString("quantityUomId"))) {
							try {
    							Map<String, Object> resultValue = dispatcher.runSync("getConvertPackingNumber", UtilMisc.toMap("productId", product.getString("productId"), "uomFromId", quantityUomId, "uomToId", product.getString("quantityUomId"), "userLogin", userLogin));
        						if (ServiceUtil.isSuccess(resultValue)) {
        							quantityUomIdToDefault = (BigDecimal) resultValue.get("convertNumber");
        						}
							} catch (Exception e) {
	        		            Debug.logWarning(e, "Problems run service name = getConvertPackingNumber", module);
	        		        }
						} else {
							quantityUomIdToDefault = BigDecimal.ONE;
						}
					}*/
        			
    				// add_product_id, product_id => productId
    	            // add_category_id, add_item_type, add_item_description
    				// Get the ProductConfigWrapper (it's not null only for configurable items) => this case is null
    	            // ...
    	            // Check for virtual products => this case is null
    	            // ...
    				String priceStr = "0";  // default price is 0
    				// product_type ASSET_USAGE_OUT_IN: Fixed Asset Usage For Rental of an asset which is shipped from and returned to inventory => this case is null
    				// ...
    				// product_type ASSET_USAGE: Fixed Asset Usage || ASSET_USAGE_OUT_IN => this case is null
    				// ...
    				// quantityStr = "1";  // default quantity is 1
    				try {
    		            price = (BigDecimal) ObjectType.simpleTypeConvert(priceStr, "BigDecimal", null, locale);
    		            priceReturnPromo = (BigDecimal) ObjectType.simpleTypeConvert(priceStr, "BigDecimal", null, locale);
    		        } catch (Exception e) {
    		            Debug.logWarning(e, "Problems parsing price string: " + priceStr, module);
    		            price = null;
    		            priceReturnPromo = null;
    		        }
    				try {
    		            quantity = (BigDecimal) ObjectType.simpleTypeConvert(quantityStr, "BigDecimal", null, locale);
    		            //For quantity we should test if we allow to add decimal quantity for this product an productStore : if not then round to 0
    		            if(! ProductWorker.isDecimalQuantityOrderAllowed(delegator, productId, cart.getProductStoreId())){
    		                quantity = quantity.setScale(0, UtilNumber.getBigDecimalRoundingMode("order.rounding"));
    		            } else {
    		                quantity = quantity.setScale(UtilNumber.getBigDecimalScale("order.decimals"), UtilNumber.getBigDecimalRoundingMode("order.rounding"));
    		            }
    		            //alternativeQuantity = new BigDecimal(quantity.doubleValue());
    		            //quantity = quantity.multiply(quantityUomIdToDefault);

    		            quantityReturnPromo = (BigDecimal) ObjectType.simpleTypeConvert(quantityReturnPromoStr, "BigDecimal", null, locale);
    		            //For quantity we should test if we allow to add decimal quantity for this product an productStore : if not then round to 0
    		            if(! ProductWorker.isDecimalQuantityOrderAllowed(delegator, productId, cart.getProductStoreId())){
    		                quantityReturnPromo = quantityReturnPromo.setScale(0, UtilNumber.getBigDecimalRoundingMode("order.rounding"));
    		            } else {
    		                quantityReturnPromo = quantityReturnPromo.setScale(UtilNumber.getBigDecimalScale("order.decimals"), UtilNumber.getBigDecimalRoundingMode("order.rounding"));
    		            }
    		            //alternativeQuantityReturnPromo = new BigDecimal(quantityReturnPromo.doubleValue());
    		            //quantityReturnPromo = quantityReturnPromo.multiply(quantityUomIdToDefault);
    		        } catch (Exception e) {
    		            Debug.logWarning(e, "Problems parsing quantity string: " + quantityStr, module);
    		            //quantity = BigDecimal.ONE;
    		        }
    				
    				// add_amount. Amount required => this case is null
    				// get the ship before date
    				// ...
    				// get the ship after date
    				// ...
    				// check for an add-to cart survey
    				// ...
    				if (productStore != null) {
    					String addToCartRemoveIncompat = productStore.getString("addToCartRemoveIncompat");
    		            String addToCartReplaceUpsell = productStore.getString("addToCartReplaceUpsell");
    		            try {
    		                if ("Y".equals(addToCartRemoveIncompat)) {
    		                    List<GenericValue> productAssocs = null;
    		                    EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toList(
    		                            EntityCondition.makeCondition(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId), EntityOperator.OR, EntityCondition.makeCondition("productIdTo", EntityOperator.EQUALS, productId)),
    		                            EntityCondition.makeCondition("productAssocTypeId", EntityOperator.EQUALS, "PRODUCT_INCOMPATABLE")), EntityOperator.AND);
    		                    productAssocs = delegator.findList("ProductAssoc", cond, null, null, null, false);
    		                    productAssocs = EntityUtil.filterByDate(productAssocs);
    		                    List<String> productList = FastList.newInstance();
    		                    for (GenericValue productAssoc : productAssocs) {
    		                        if (productId.equals(productAssoc.getString("productId"))) {
    		                            productList.add(productAssoc.getString("productIdTo"));
    		                            continue;
    		                        }
    		                        if (productId.equals(productAssoc.getString("productIdTo"))) {
    		                            productList.add(productAssoc.getString("productId"));
    		                            continue;
    		                        }
    		                    }
    		                    for (ShoppingCartItem sci : cart) {
    		                        if (productList.contains(sci.getProductId())) {
    		                            try {
    		                                cart.removeCartItem(sci, dispatcher);
    		                            } catch (CartItemModifyException e) {
    		                                Debug.logError(e.getMessage(), module);
    		                            }
    		                        }
    		                    }
    		                }
    		                if ("Y".equals(addToCartReplaceUpsell)) {
    		                    List<GenericValue> productList = null;
    		                    EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toList(
    		                            EntityCondition.makeCondition("productIdTo", EntityOperator.EQUALS, productId),
    		                            EntityCondition.makeCondition("productAssocTypeId", EntityOperator.EQUALS, "PRODUCT_UPGRADE")), EntityOperator.AND);
    		                    productList = delegator.findList("ProductAssoc", cond, UtilMisc.toSet("productId"), null, null, false);
    		                    if (productList != null) {
    		                        for (ShoppingCartItem sci : cart) {
    		                            if (productList.contains(sci.getProductId())) {
    		                                try {
    		                                    cart.removeCartItem(sci, dispatcher);
    		                                } catch (CartItemModifyException e) {
    		                                    Debug.logError(e.getMessage(), module);
    		                                }
    		                            }
    		                        }
    		                    }
    		                }
    		            } catch (GenericEntityException e) {
    		                Debug.logError(e.getMessage(), module);
    		            }
    				}
    				// check for alternative packing (ProductWorker.isAlternativePacking)
    				/* ProductAssoc: productId == parentProductId == virtualVariantId. productIdTo == productId
    				 * 
    				 * if(ProductWorker.isAlternativePacking(delegator, productId , parentProductId)){
    		            GenericValue parentProduct = null;
    		            try {
    		                parentProduct = delegator.findOne("Product", UtilMisc.toMap("productId", parentProductId), false);
    		            } catch (GenericEntityException e) {
    		                Debug.logError(e, "Error getting parent product", module);
    		            }
    		            BigDecimal piecesIncluded = BigDecimal.ZERO;
    		            if(parentProduct != null){
    		                piecesIncluded = new BigDecimal(parentProduct.getLong("piecesIncluded"));
    		                quantity = quantity.multiply(piecesIncluded);
    		            }
    		        }*/
    				if (BigDecimal.ZERO.compareTo(quantity) < 0) {
    					Map<String, Object> paramMap = new FastMap<String, Object>();
        				if (UtilValidate.isNotEmpty(cart.getDefaultItemDeliveryDate())) {
        		        	paramMap.put("itemDesiredDeliveryDate", cart.getDefaultItemDeliveryDate());
        		        	paramMap.put("useAsDefaultDesiredDeliveryDate", "true");
        		        } else {
        		        	paramMap.put("itemDesiredDeliveryDate", "");
        		        }
        				if (UtilValidate.isNotEmpty(quantityUomId)) {
        					paramMap.put("quantityUomId", quantityUomId);
        				}
        				/*if (alternativeQuantity != null) {
        					paramMap.put("alternativeQuantity", alternativeQuantity);
        				}*/
        				
        				// Translate the parameters and add to the cart
        				/*
        				 * 1. catalogId <String>, 						catalogId
        				 * 2. shoppingListId <String>, 					null
        				 * 3. shoppingListItemSeqId <String>, 			null
        				 * 4. productId <String>, 						productId
        				 * 5. productCategoryId <String>,				null		//parameter: add_category_id
        				 * 6. itemType <String>,						null 
        				 * 7. itemDescription <String>, 				null
        				 * 8. price <BigDecimal>, 						price
        				 * 9. amount <BigDecimal>,						null 
        				 * 10. quantity <BigDecimal>, 					quantity
        				 * 11. reservStart <Timestamp>, 				null
        				 * 12. reservLength <BigDecimal>, 				null
        				 * 13. reservPersons <BigDecimal>,				null
        				 * 14. accommodationMapId <String>, 			null
        				 * 15. accommodationSpotId <String>,			null
        				 * 16. shipBeforeDate <Timestamp>, 				shipBeforeDate
        				 * 17. shipAfterDate <Timestamp>, 				shipAfterDate
        				 * 18. configWrapper <ProductConfigWrapper>, 	configWrapper
        				 * 19. itemGroupNumber <String>, 				null 		//itemGroupNumber
        				 * 20. paramMap <Map<String, Object>>,			paramMap
        				 * 21. parentProductId <String>,				null 
        				 * 22. triggerExternalOpsBool					Boolean.FALSE // no run promotion when add to cart each item
        				 * */
        		        result = cartHelper.addToCart(catalogId, null, null, productId, null, null, null, price, null, quantity, 
        		        		null, null, null, null, null, shipBeforeDate, shipAfterDate, null, null, paramMap, null, Boolean.FALSE);
        				
        		        /* controlDirective = processResult(result, request);
    				        Integer itemId = (Integer)result.get("itemId");
    				        if (UtilValidate.isNotEmpty(itemId)) {request.setAttribute("itemId", itemId);}
    				        // Determine where to send the browser
    				        if (controlDirective.equals(ERROR)) {return "error";} else {if (cart.viewCartOnAdd()) {return "viewcart";} else {return "success";}} 
        		         */
    				}
    		        if (BigDecimal.ZERO.compareTo(quantityReturnPromo) < 0) {
    		        	/*String shoppingListId2 = null;
        				String shoppingListItemSeqId2 = null;
        				String productCategoryId2 = null; //parameter: add_category_id
        				String itemType2 = "PRODPROMO_ORDER_ITEM";
        				String itemDescription2 = null;
        				BigDecimal amount2 = null;
        				Timestamp reservStart2 = null;
        				BigDecimal reservLength2 = null;
        				BigDecimal reservPersons2 = null;
        				String accommodationMapId2 = null;
        				String accommodationSpotId2 = null;
        				ProductConfigWrapper configWrapper2 = null;
        				String itemGroupNumber2 = null; //itemGroupNumber */
    		        	
    		        	Map<String, Object> paramMap2 = new FastMap<String, Object>();
        				if (UtilValidate.isNotEmpty(cart.getDefaultItemDeliveryDate())) {
        					paramMap2.put("itemDesiredDeliveryDate", cart.getDefaultItemDeliveryDate());
        					paramMap2.put("useAsDefaultDesiredDeliveryDate", "true");
        		        } else {
        		        	paramMap2.put("itemDesiredDeliveryDate", "");
        		        }
        				if (UtilValidate.isNotEmpty(quantityUomId)) {
        					paramMap2.put("quantityUomId", quantityUomId);
        				}
        				/*if (alternativeQuantity != null) {
        					paramMap2.put("alternativeQuantity", alternativeQuantityReturnPromo);
        				}*/
        				String parentProductId2 = null;
        				// Translate the parameters and add to the cart
        		        result = cartHelper.addToCart(catalogId, null, null, productId, null,
        		        		"PRODPROMO_ORDER_ITEM", null, priceReturnPromo, null, quantityReturnPromo, null, null, null, null, null,
        		                shipBeforeDate, shipAfterDate, null, null, paramMap2, parentProductId2, Boolean.FALSE, Boolean.TRUE);
    		        }
    			}
            }
        }
        return "success";
    }
	
	public static String initializeOrderCheckoutOption(HttpServletRequest request, HttpServletResponse response, List<String> alertMessageList) {
    	// STEP 3: updateCheckoutOptions: When user selects a shipping method, this automatically reloads quick checkout page with shipping estimates filled in.
        // Click Quick ship
		
		// uri="updateCheckoutOptions" > call update shipping address before
		String updateCheckoutOptionsResult = CheckOutEvents.setPartialCheckOutOptions(request, response);
        if ("error".equals(updateCheckoutOptionsResult)) return "error";
        // uri="checkout" > CheckOutEvents.setQuickCheckOutOptions
        String checkoutResult = CheckOutEvents.setQuickCheckOutOptions(request, response);
        if ("error".equals(checkoutResult)) return "error";
        String calcShippingResult = ShippingEvents.getShipEstimate(request, response);
        if ("error".equals(calcShippingResult)) return "error";
        String calcTax = CheckOutEvents.calcTax(request, response);
        if ("error".equals(calcTax)) return "error";
        
    	return "success";
    }
	
	public static String initializeOrderCheckoutOptionWithoutAccTrans(HttpServletRequest request, HttpServletResponse response, List<String> alertMessageList) {
    	// STEP 3: updateCheckoutOptions: When user selects a shipping method, this automatically reloads quick checkout page with shipping estimates filled in.
        // Click Quick ship
		
		// uri="updateCheckoutOptions" > call update shipping address before
		//String updateCheckoutOptionsResult = CheckOutEvents.setPartialCheckOutOptions(request, response);
        //if ("error".equals(updateCheckoutOptionsResult)) return "error";
        // uri="checkout" > CheckOutEvents.setQuickCheckOutOptions
        String checkoutResult = CheckOutWithoutAccTransEvents.setQuickCheckOutOptions(request, response);
        if ("error".equals(checkoutResult)) return "error";
        String calcShippingResult = ShippingEvents.getShipEstimate(request, response);
        if ("error".equals(calcShippingResult)) return "error";
        String calcTax = CheckOutEvents.calcTax(request, response);
        if ("error".equals(calcTax)) return "error";
        
    	return "success";
    }
	
	public static String updateSalesOrderCustom(HttpServletRequest request, HttpServletResponse response){
		String orderId = request.getParameter("orderId");
		String listProd = request.getParameter("listProd");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		Locale locale = UtilHttp.getLocale(request);
		//String calcTaxStr = request.getParameter("calcTax");
        //Security security = (Security) request.getAttribute("security");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        
        if (UtilValidate.isEmpty(orderId)) {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSOrderIdMustNotBeEmpty", locale));
            return "error";
        }
        
        GenericValue orderHeader;
		try {
			orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			if (UtilValidate.isEmpty(orderHeader)) {
	        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSNotFoundOrderHasOrderIdIs", UtilMisc.toList(orderId), locale));
	            return "error";
	        }
		} catch (GenericEntityException e1) {
			Debug.logError(e1, "Fatal occur when find order", module);
			request.setAttribute("_ERROR_MESSAGE_", "Fatal occur when find order");
			return "error";
		}
        
		JSONArray jsonArray = new JSONArray();
		if (UtilValidate.isNotEmpty(listProd)) {
			jsonArray = JSONArray.fromObject(listProd);
		}
		List<Map<String, Object>> listProduct = FastList.newInstance();
		if (jsonArray != null && jsonArray.size() > 0) {
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject prodItem = jsonArray.getJSONObject(i);
				Map<String, Object> productItem = FastMap.newInstance();
				productItem.put("orderItemSeqId", prodItem.getString("orderItemSeqId"));
				productItem.put("shipGroupSeqId", prodItem.getString("shipGroupSeqId"));
				productItem.put("productId", prodItem.getString("productId"));
				productItem.put("unitPriceStr", prodItem.getString("unitPrice"));
				productItem.put("quantityUomId", prodItem.getString("quantityUomId"));
				productItem.put("quantityStr", prodItem.getString("quantity"));
				listProduct.add(productItem);
			}
		}
		
		if (UtilValidate.isEmpty(listProduct)) {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSListOrderIsEmpty", locale));
            return "error";
        }
		
		List<Object> errMsgList = FastList.newInstance();
		
		Map<String, String> itemQtyMap = FastMap.newInstance();
		Map<String, String> itemExpireDateMap = FastMap.newInstance();
        Map<String, String> itemAlternativeQtyMap = FastMap.newInstance();
        Map<String, String> itemQuantityUomIdMap = FastMap.newInstance();
        Map<String, String> itemPriceMap = FastMap.newInstance();
        Map<String, String> overridePriceMap = FastMap.newInstance();
        
        for (Map<String, Object> prodItem : listProduct) {
        	String orderItemSeqId = (String) prodItem.get("orderItemSeqId");
        	String shipGroupSeqId = (String) prodItem.get("shipGroupSeqId");
        	String productId = (String) prodItem.get("productId");
        	String quantityUomId = (String) prodItem.get("quantityUomId");
        	String quantityStr = (String) prodItem.get("quantityStr");
        	String expireDateStr = (String) prodItem.get("expireDateStr");
        	
    		BigDecimal quantity = BigDecimal.ZERO;
    		BigDecimal alternativeQuantity = null;
    		if (UtilValidate.isNotEmpty(orderItemSeqId) && UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(quantityStr)) {
	    		// Check quantityUomId with productQuotation
	    		BigDecimal quantityUomIdToDefault = BigDecimal.ONE;
	    		GenericValue productItem = null;
	    		try {
	    			productItem = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
	    			if (productItem == null) {
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
	                if(! ProductWorker.isDecimalQuantityOrderAllowed(delegator, productId, orderHeader.getString("productStoreId"))){
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
		
		Map<String, Object> contextMap = new HashMap<String, Object>();
		contextMap.put("orderId", orderId);
		contextMap.put("orderTypeId", orderHeader.get("orderTypeId"));
		contextMap.put("itemQtyMap", itemQtyMap);
		contextMap.put("itemExpireDateMap", itemExpireDateMap);
		contextMap.put("itemAlternativeQtyMap", itemAlternativeQtyMap);
		contextMap.put("itemQuantityUomIdMap", itemQuantityUomIdMap);
		contextMap.put("itemPriceMap", itemPriceMap);
		contextMap.put("overridePriceMap", overridePriceMap);
		contextMap.put("userLogin", userLogin);
		contextMap.put("locale", locale);
		try {
			Map<String, Object> resultValue = dispatcher.runSync("updateOrderItemsCustom", contextMap);
			if (ServiceUtil.isError(resultValue)) {
				ServiceUtil.getMessages(request, resultValue, "Fatal occur when run service updateOrderItemsCustom");
				return "error";
			} else {
				if (orderHeader.getString("orderTypeId").equals("PURCHASE_ORDER")){
					dispatcher.runSync("sendNotifyToAcc", UtilMisc.toMap("orderId", orderId, "oldUserLogin", userLogin, 
							"userLogin", userLogin, "isEdit", "Y"));
				}
			}
		} catch (GenericServiceException e) {
			Debug.logError(e, e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", "Fatal occur when run service updateOrderItemsCustom");
        	return "error";
		}
         
		if (UtilValidate.isNotEmpty(errMsgList)) {
        	request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
        	return "error";
        }
		return "success";
	}
	
	public static String processEditSalesOrderLoadToCart(HttpServletRequest request, HttpServletResponse response){
		String orderId = request.getParameter("orderId");
		String changeSO = null;
		if(request.getParameter("changeSO") != null){
			changeSO = request.getParameter("changeSO");
		}
		String listProd = request.getParameter("listProd");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		Locale locale = UtilHttp.getLocale(request);
		//String calcTaxStr = request.getParameter("calcTax");
        //Security security = (Security) request.getAttribute("security");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        
        if (UtilValidate.isEmpty(orderId)) {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "DAOrderIdMustNotBeEmpty", locale));
            return "error";
        }
        
        GenericValue orderHeader;
		try {
			orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			if (UtilValidate.isEmpty(orderHeader)) {
	        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "DANotFoundOrderHasOrderIdIs", UtilMisc.toList(orderId), locale));
	            return "error";
	        }
		} catch (GenericEntityException e1) {
			Debug.logError(e1, "Fatal occur when find order", module);
			request.setAttribute("_ERROR_MESSAGE_", "Fatal occur when find order");
			return "error";
		}
        
		if (UtilValidate.isNotEmpty(com.olbius.basesales.shoppingcart.ShoppingCartEvents.getCartUpdateObject(request))) {
        	com.olbius.basesales.shoppingcart.ShoppingCartEvents.destroyCartUpdate(request, response);
		}
		
		JSONArray jsonArray = new JSONArray();
		if (UtilValidate.isNotEmpty(listProd)) {
			jsonArray = JSONArray.fromObject(listProd);
		}
		List<Map<String, Object>> listProduct = FastList.newInstance();
		if (jsonArray != null && jsonArray.size() > 0) {
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject prodItem = jsonArray.getJSONObject(i);
				Map<String, Object> productItem = FastMap.newInstance();
				productItem.put("orderItemSeqId", prodItem.getString("orderItemSeqId"));
				productItem.put("shipGroupSeqId", prodItem.getString("shipGroupSeqId"));
				productItem.put("productId", prodItem.getString("productId"));
				productItem.put("unitPriceStr", prodItem.getString("unitPrice"));
				productItem.put("quantityUomId", prodItem.getString("quantityUomId"));
				productItem.put("quantityStr", prodItem.getString("quantity"));
				
				//add reasonEnumId and comment by datnv
				if(prodItem.containsKey("reasonEnumId")){
					productItem.put("reasonEnumId", prodItem.getString("reasonEnumId"));
				}
				if(prodItem.containsKey("comment")){
					productItem.put("comment", prodItem.getString("comment"));
				}
				listProduct.add(productItem);
			}
		}
		
		if (UtilValidate.isEmpty(listProduct)) {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "DAListOrderIsEmpty", locale));
            return "error";
        }
		
		List<Object> errMsgList = FastList.newInstance();
		
		Map<String, String> itemQtyMap = FastMap.newInstance();
		Map<String, String> itemExpireDateMap = FastMap.newInstance();
        Map<String, String> itemAlternativeQtyMap = FastMap.newInstance();
        Map<String, String> itemQuantityUomIdMap = FastMap.newInstance();
        Map<String, String> itemPriceMap = FastMap.newInstance();
        Map<String, String> overridePriceMap = FastMap.newInstance();
        
        Map<String, String> itemReasonMap = FastMap.newInstance();
        Map<String, String> itemCommentMap = FastMap.newInstance();
        
        for (Map<String, Object> prodItem : listProduct) {
        	String orderItemSeqId = (String) prodItem.get("orderItemSeqId");
        	String shipGroupSeqId = (String) prodItem.get("shipGroupSeqId");
        	String productId = (String) prodItem.get("productId");
        	String quantityUomId = (String) prodItem.get("quantityUomId");
        	String quantityStr = (String) prodItem.get("quantityStr");
        	String expireDateStr = (String) prodItem.get("expireDateStr");
        	//add reasonEnumId and comment by datnv
        	String reasonEnumId = (String) prodItem.get("reasonEnumId");
        	String comment = (String) prodItem.get("comment");
        	
    		BigDecimal quantity = BigDecimal.ZERO;
    		BigDecimal alternativeQuantity = null;
    		if (UtilValidate.isNotEmpty(orderItemSeqId) && UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(quantityStr)) {
	    		// Check quantityUomId with productQuotation
	    		BigDecimal quantityUomIdToDefault = BigDecimal.ONE;
	    		GenericValue productItem = null;
	    		try {
	    			productItem = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
	    			if (productItem == null) {
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
	                if(! ProductWorker.isDecimalQuantityOrderAllowed(delegator, productId, orderHeader.getString("productStoreId"))){
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
	    		//add reasonEnumId and comment by datnv
	    		itemReasonMap.put(orderItemSeqId, reasonEnumId);
	    		itemCommentMap.put(orderItemSeqId, comment);
    		}
        }
		
		Map<String, Object> contextMap = new HashMap<String, Object>();
		contextMap.put("orderId", orderId);
		contextMap.put("itemQtyMap", itemQtyMap);
		contextMap.put("itemExpireDateMap", itemExpireDateMap);
		contextMap.put("itemAlternativeQtyMap", itemAlternativeQtyMap);
		contextMap.put("itemQuantityUomIdMap", itemQuantityUomIdMap);
		contextMap.put("itemPriceMap", itemPriceMap);
		contextMap.put("overridePriceMap", overridePriceMap);
		contextMap.put("userLogin", userLogin);
		contextMap.put("locale", locale);
//		add reasonEnumId and comment by datnv
		contextMap.put("itemReasonMap", itemReasonMap);
		contextMap.put("itemCommentMap", itemCommentMap);
		if(changeSO != null){
			contextMap.put("changeSO", changeSO);
		}
		try {
			Map<String, Object> resultValue = dispatcher.runSync("updateOrderItemsLoadToCart", contextMap);
			if (ServiceUtil.isError(resultValue)) {
				ServiceUtil.getMessages(request, resultValue, "Fatal occur when run service updateOrderItemsUpdateCustom");
				return "error";
			}
			ShoppingCart cart = (ShoppingCart) resultValue.get("shoppingCart");
			com.olbius.basesales.shoppingcart.ShoppingCartEvents.saveCartUpdateObject(request, cart);
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", "Fatal occur when run service updateOrderItemsCustom");
        	return "error";
		}
        
		if (UtilValidate.isNotEmpty(errMsgList)) {
        	request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
        	return "error";
        }
		String resultUpdateStatus = OrderWorker.updateStatusStartEdit(delegator, request, orderId);
		if (resultUpdateStatus == "error") return resultUpdateStatus;
		return "success";
	}
	
	public static String processEditSalesOrderSaveToOrder(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String orderId = null;
		try {
			Map<String, Object> resultValue = dispatcher.runSync("updateOrderItemsSaveToOrder", UtilMisc.toMap("request", request, "userLogin", userLogin, "locale", locale));
			if (ServiceUtil.isError(resultValue)) {
				ServiceUtil.getMessages(request, resultValue, "Fatal occur when run service updateOrderItemsSaveToOrder");
				return "error";
			}
			orderId = (String) resultValue.get("orderId");
			com.olbius.basesales.shoppingcart.ShoppingCartEvents.destroyCartUpdate(request, response);
			request.setAttribute("orderId", orderId);
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", "Fatal occur when run service updateOrderItemsSaveToOrder");
        	return "error";
		}

		String resultUpdateStatus = OrderWorker.updateStatusFinishEdit(delegator, request, orderId);
		if (resultUpdateStatus == "error") return resultUpdateStatus;
        return "success";
	}
	
	public static String appendOrderItemsLoadToCart(HttpServletRequest request, HttpServletResponse response){
		HttpSession session = request.getSession();
		Locale locale = UtilHttp.getLocale(request);
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        String orderId = (String) request.getParameter("orderId");
        String productList = (String) request.getParameter("productList");
        
        if (productList != null) {
        	if (productList.startsWith("[")) productList = productList.substring(1, productList.length());
    		if (productList.endsWith("]")) productList = productList.substring(0, productList.length() - 1);
        }

        if (UtilValidate.isNotEmpty(com.olbius.basesales.shoppingcart.ShoppingCartEvents.getCartUpdateObject(request))) {
        	com.olbius.basesales.shoppingcart.ShoppingCartEvents.destroyCartUpdate(request, response);
		}
		try {
			Map<String, Object> contextMap = new HashMap<String, Object>();
			contextMap.put("orderId", orderId);
			contextMap.put("productList", UtilMisc.toList(productList));
			contextMap.put("userLogin", userLogin);
			contextMap.put("locale", locale);
			contextMap.put("request", request);
			Map<String, Object> resultValue = dispatcher.runSync("appendOrderItemsAdvanceLoadToCart", contextMap);
			if (ServiceUtil.isError(resultValue)) {
				ServiceUtil.getMessages(request, resultValue, "Fatal occur when run service appendOrderItemsCustomAdvance");
				return "error";
			}
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", "Fatal occur when run service appendOrderItemsCustomAdvance");
        	return "error";
		}
		if (!UtilValidate.isNotEmpty(com.olbius.basesales.shoppingcart.ShoppingCartEvents.getCartUpdateObject(request))) {
			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "DANoActionToExecute", locale));
			return "error";
		}
		String resultUpdateStatus = OrderWorker.updateStatusStartEdit(delegator, request, orderId);
		if (resultUpdateStatus == "error") return resultUpdateStatus;
		return "success";
	}
	
	public static String cancelOrderItemLoadToCart(HttpServletRequest request, HttpServletResponse response){
		HttpSession session = request.getSession();
		Locale locale = UtilHttp.getLocale(request);
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		
		String orderId = (String) request.getParameter("orderId");
		String orderItemSeqId = (String) request.getParameter("orderItemSeqId");
		String shipGroupSeqId = (String) request.getParameter("shipGroupSeqId");
		String cancelQuantity = (String) request.getParameter("cancelQuantity");
		
		if (UtilValidate.isNotEmpty(com.olbius.basesales.shoppingcart.ShoppingCartEvents.getCartUpdateObject(request))) {
        	com.olbius.basesales.shoppingcart.ShoppingCartEvents.destroyCartUpdate(request, response);
		}
		try {
			Map<String, Object> contextMap = new HashMap<String, Object>();
			contextMap.put("orderId", orderId);
			contextMap.put("orderItemSeqId", orderItemSeqId);
			contextMap.put("shipGroupSeqId", shipGroupSeqId);
			contextMap.put("cancelQuantity", cancelQuantity);
			contextMap.put("userLogin", userLogin);
			contextMap.put("locale", locale);
			contextMap.put("request", request);
			Map<String, Object> resultValue = dispatcher.runSync("cancelOrderItemLoadToCart", contextMap);
			if (ServiceUtil.isError(resultValue)) {
				ServiceUtil.getMessages(request, resultValue, "Fatal occur when run service cancelOrderItemLoadToCart");
				return "error";
			}
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", "Fatal occur when run event cancelOrderItemLoadToCart");
			return "error";
		}
		if (!UtilValidate.isNotEmpty(com.olbius.basesales.shoppingcart.ShoppingCartEvents.getCartUpdateObject(request))) {
			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "DANoActionToExecute", locale));
			return "error";
		}
		String resultUpdateStatus = OrderWorker.updateStatusStartEdit(delegator, request, orderId);
		if (resultUpdateStatus == "error") return resultUpdateStatus;
		return "success";
	}
	
	public static String recalculateOrderPromoUpdate(HttpServletRequest request, HttpServletResponse response){
		ShoppingCart cart = com.olbius.basesales.shoppingcart.ShoppingCartEvents.getCartUpdateObject(request);
		return recalculateOrderPromoCore(request, response, cart);
	}
	
	public static String recalculateOrderPromo(HttpServletRequest request, HttpServletResponse response){
		ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
		return recalculateOrderPromoCore(request, response, cart);
	}
	
	@SuppressWarnings("unchecked")
	public static String recalculateOrderPromoCore(HttpServletRequest request, HttpServletResponse response, ShoppingCart cart) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		if (cart == null) {
			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSCartIsEmpty", locale));
        	return "error";
		}
		Set<GenericPK> listActionPKSelected = new HashSet<GenericPK>();
		
		// Get the parameters as a MAP, remove the productId and quantity params.
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        
        // The number of multi form rows is retrieved
        int promoCount = UtilHttp.getMultiFormRowCount(paramMap);
        if (promoCount < 1) {
            Debug.logWarning("No rows to process, as rowCount = " + promoCount, module);
        } else {
            for (int i = 0; i < promoCount; i++) {
            	// process list rule (condition, action)
            	List<String> actionsAnd = null;
        		List<String> actionsOr = null;
    	        
                String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;        // current suffix after each field id
                
                if (paramMap.containsKey("actionsAnd" + thisSuffix)) {
                	Object actionsAndObj = paramMap.remove("actionsAnd" + thisSuffix);
                	if (actionsAndObj instanceof String) {
                		actionsAnd = UtilMisc.toList((String) actionsAndObj);
                	} else {
                		actionsAnd = (List<String>) actionsAndObj;
                	}
                }
                if (paramMap.containsKey("actionsOr" + thisSuffix)) {
                	Object actionsOrObj = paramMap.remove("actionsOr" + thisSuffix);
                	if (actionsOrObj instanceof String) {
                		actionsOr = UtilMisc.toList((String) actionsOrObj);
                	} else {
                		actionsOr = (List<String>) actionsOrObj;
                	}
                }
                
                if (actionsAnd != null) {
                	for (int j = 0; j < actionsAnd.size(); j++) {
                		GenericPK tmp = SalesUtil.processStringGenericPK(delegator, actionsAnd.get(j));
                		if (tmp != null) listActionPKSelected.add(tmp);
                	}
                }
                if (actionsOr != null) {
                	for (int j = 0; j < actionsOr.size(); j++) {
                		GenericPK tmp = SalesUtil.processStringGenericPK(delegator, actionsOr.get(j));
                		if (tmp != null) listActionPKSelected.add(tmp);
                	}
                }
            }
        }
        
        if (listActionPKSelected != null) {
            cart.setAttribute("promoActionSelected", listActionPKSelected);
            ProductPromoWorker.doPromotions(cart, dispatcher);

            // calc the sales tax  
            CheckOutHelper coh = new CheckOutHelper(dispatcher, delegator, cart);
            try {
                coh.calcAndAddTax();
            } catch (GeneralException e) {
                Debug.logError(e, module);
            }
        }
        
        /*String[] actionsAnd = (String[]) request.getParameterValues("actionsAnd");
        String actionsOr = (String) request.getParameter("actionsOr");
		
        Set<GenericPK> listActionPKSelected = new HashSet<GenericPK>();
        if (actionsAnd != null) {
        	for (int i = 0; i < actionsAnd.length; i++) {
        		GenericPK tmp = processStringGenericPK(delegator, actionsAnd[i]);
        		if (tmp != null) listActionPKSelected.add(tmp);
        	}
        }
        if (actionsOr != null) {
        	GenericPK tmp = processStringGenericPK(delegator, actionsOr);
    		if (tmp != null) listActionPKSelected.add(tmp);
        }
        
        if (listActionPKSelected != null) {
            cart.setAttribute("promoActionSelected", listActionPKSelected);
            ProductPromoWorker.doPromotions(cart, dispatcher);
        }*/
        
		return "success";
	}
	
	public static String updateCartItemComment(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        String itemComment = request.getParameter("itemComment");
        String alternateGwpLineStrTmp = request.getParameter("cartLine");
        Locale locale = UtilHttp.getLocale(request);
        
        if (UtilValidate.isEmpty(alternateGwpLineStrTmp)) {
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage("OrderErrorUiLabels","OrderCouldNotSelectAlternateGiftNoAlternateGwpLinePassed", locale));
            return "error";
        }
        
        List<String> alternateGwpLineStres = FastList.newInstance();
		JSONArray jsonArray = new JSONArray();
		if (UtilValidate.isNotEmpty(alternateGwpLineStrTmp)) {
			jsonArray = JSONArray.fromObject(alternateGwpLineStrTmp);
		}
		if (jsonArray != null && jsonArray.size() > 0) {
			for (int i = 0; i < jsonArray.size(); i++) {
				alternateGwpLineStres.add(jsonArray.getString(i));
			}
		}

        if (UtilValidate.isNotEmpty(alternateGwpLineStres)) {
        	for (String alternateGwpLineStr : alternateGwpLineStres) {
        		int alternateGwpLine = 0;
                try {
                    alternateGwpLine = Integer.parseInt(alternateGwpLineStr);
                } catch (Exception e) {
                    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage("OrderErrorUiLabels","OrderCouldNotSelectAlternateGiftAlternateGwpLineIsNotAValidNumber", locale));
                    return "error";
                }

                ShoppingCartItem cartLine = cart.findCartItem(alternateGwpLine);
                if (cartLine == null) {
                    request.setAttribute("_ERROR_MESSAGE_", "Could not select alternate gift, no cart line item found for #" + alternateGwpLine + ".");
                    return "error";
                }

                cartLine.setItemComment(itemComment);
                request.setAttribute("newComment", itemComment);
        	}
        }
        return "success";
    }
    
	/** For GWP Promotions with multiple alternatives, selects an alternative to the current GWP */
    public static String setDesiredAlternateGwpProductIdUpdate(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = com.olbius.basesales.shoppingcart.ShoppingCartEvents.getCartUpdateObject(request);
        return setDesiredAlternateGwpProductIdCore(request, response, cart);
    }
    
    /** For GWP Promotions with multiple alternatives, selects an alternative to the current GWP */
    public static String setDesiredAlternateGwpProductId(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        return setDesiredAlternateGwpProductIdCore(request, response, cart);
    }
    
    /** For GWP Promotions with multiple alternatives, selects an alternative to the current GWP */
    public static String setDesiredAlternateGwpProductIdCore(HttpServletRequest request, HttpServletResponse response, ShoppingCart cart) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String alternateGwpProductId = request.getParameter("alternateGwpProductId");
        String alternateGwpLineStrTmp = request.getParameter("alternateGwpLine");
        Locale locale = UtilHttp.getLocale(request);

        if (UtilValidate.isEmpty(alternateGwpProductId)) {
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderCouldNotSelectAlternateGiftNoAlternateGwpProductIdPassed", locale));
            return "error";
        }
        if (UtilValidate.isEmpty(alternateGwpLineStrTmp)) {
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderCouldNotSelectAlternateGiftNoAlternateGwpLinePassed", locale));
            return "error";
        }
        
        List<String> alternateGwpLineStres = FastList.newInstance();
		JSONArray jsonArray = new JSONArray();
		if (UtilValidate.isNotEmpty(alternateGwpLineStrTmp)) {
			jsonArray = JSONArray.fromObject(alternateGwpLineStrTmp);
		}
		if (jsonArray != null && jsonArray.size() > 0) {
			for (int i = 0; i < jsonArray.size(); i++) {
				alternateGwpLineStres.add(jsonArray.getString(i));
			}
		}
		boolean isUpdate = false;
        if (UtilValidate.isNotEmpty(alternateGwpLineStres)) {
        	for (String alternateGwpLineStr : alternateGwpLineStres) {
        		int alternateGwpLine = 0;
                try {
                    alternateGwpLine = Integer.parseInt(alternateGwpLineStr);
                } catch (Exception e) {
                    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderCouldNotSelectAlternateGiftAlternateGwpLineIsNotAValidNumber", locale));
                    return "error";
                }

                ShoppingCartItem cartLine = cart.findCartItem(alternateGwpLine);
                if (cartLine == null) {
                    request.setAttribute("_ERROR_MESSAGE_", "Could not select alternate gift, no cart line item found for #" + alternateGwpLine + ".");
                    return "error";
                }

                if (cartLine.getIsPromo()) {
                    // note that there should just be one promo adjustment, the reversal of the GWP, so use that to get the promo action key
                    Iterator<GenericValue> checkOrderAdjustments = UtilMisc.toIterator(cartLine.getAdjustments());
                    while (checkOrderAdjustments != null && checkOrderAdjustments.hasNext()) {
                        GenericValue checkOrderAdjustment = checkOrderAdjustments.next();
                        if (UtilValidate.isNotEmpty(checkOrderAdjustment.getString("productPromoId")) &&
                                UtilValidate.isNotEmpty(checkOrderAdjustment.getString("productPromoRuleId")) &&
                                UtilValidate.isNotEmpty(checkOrderAdjustment.getString("productPromoActionSeqId"))) {
                            GenericPK productPromoActionPk = delegator.makeValidValue("ProductPromoAction", checkOrderAdjustment).getPrimaryKey();
                            cart.setDesiredAlternateGiftByAction(productPromoActionPk, alternateGwpProductId);
                            isUpdate = true;
                            break;
                        }
                    }
                }
        	}
        	if (isUpdate) {
        		if (cart.getOrderType().equals("SALES_ORDER")) {
                    org.ofbiz.order.shoppingcart.product.ProductPromoWorker.doPromotions(cart, dispatcher);
                }
        	}
        }

        return "success";
        /*request.setAttribute("_ERROR_MESSAGE_", "Could not select alternate gift, cart line item found for #" + alternateGwpLine + " does not appear to be a valid promotional gift.");
        return "error";*/
    }
    
    public static String attachFilesPaymentOrder(HttpServletRequest request, HttpServletResponse response){
    	 // Delegator delegator = (Delegator) request.getAttribute("delegator");
         LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
         Locale locale = UtilHttp.getLocale(request);
         HttpSession session = request.getSession();
         Security security = (Security) request.getAttribute("security");
         GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
         
         if (!SecurityUtil.getOlbiusSecurity(security).olbiusEntityPermission(session, "ATTACH_PAYMENT_CREATE", "SALESORDER")) {
         	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSYouHavenotCreatePermission", locale));
         	return "error";
         }
         
         String orderId = null;
         
         List<Object> errMsgList = FastList.newInstance();
         
         boolean beganTx = false;
         // String productPromoIdSuccess = "";
         try {
         	// begin the transaction
         	beganTx = TransactionUtil.begin(7200);
         	// String controlDirective = null;
         	
         	/*Map<String, Object> contextMap = FastMap.newInstance();
         	Map<String, Object> result0 = dispatcher.runSync("createProductPromoCustom", contextMap);
         	// no values for price and paramMap (a context for adding attributes)
             controlDirective = SalesUtil.processResult(result0, request);
             if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
             	try {
                     TransactionUtil.rollback(beganTx, "Failure in processing Create product promo callback", null);
                 } catch (Exception e1) {
                     Debug.logError(e1, module);
                 }
                 return "error";
             }*/
             
         	Map<String, Object> paramMap = SalesUtil.getParameterMapFileUpload(request);
         	if (paramMap == null) {
         		return "success";
         	}
         	
         	// Get parameter information general
            orderId = (String) paramMap.get("orderId");
         	
 	        // Get the parameters as a MAP, remove the productId and quantity params.
 	        //Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
 	        
 	        // The number of multi form rows is retrieved
 	        int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
 	        if (rowCount < 1) {
 	            Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
 	        } else {
 	        	List<Map<String, Object>> listFiles = new ArrayList<Map<String, Object>>();
 	            for (int i = 0; i < rowCount; i++) {
 	            	// process list rule (condition, action)
 	            	ByteBuffer uploadedFile = null;
 	            	String _uploadedFile_fileName = null;
 	            	String _uploadedFile_contentType = null;
 	    	        
 	    	        // controlDirective = null;                // re-initialize each time
 	                String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;        // current suffix after each field id
 	                
 	                // get the productId
 	                if (paramMap.containsKey("uploadedFile" + thisSuffix)) {
 	                	uploadedFile = (ByteBuffer) paramMap.remove("uploadedFile" + thisSuffix);
 	                }
 	                
 	                if (paramMap.containsKey("_uploadedFile" + thisSuffix + "_fileName")) {
 	                	_uploadedFile_fileName = (String) paramMap.remove("_uploadedFile" + thisSuffix + "_fileName");
 	                }
 	                
 	                if (paramMap.containsKey("_uploadedFile" + thisSuffix + "_contentType")) {
 	                	_uploadedFile_contentType = (String) paramMap.remove("_uploadedFile" + thisSuffix + "_contentType");
 	                }
 	                
 	                if (uploadedFile != null) {
 	                	Map<String, Object> fileItem = UtilMisc.<String, Object>toMap(
 	                				"uploadedFile", uploadedFile, 
 	                				"_uploadedFile_fileName", _uploadedFile_fileName, 
 	                				"_uploadedFile_contentType", _uploadedFile_contentType
 	                			);
 	                	listFiles.add(fileItem);
 	                }
 	            }
 	            
 	            if (listFiles.size() > 0) {
                	Map<String, Object> resultValue = dispatcher.runSync("attachFilesPaymentOrder", UtilMisc.toMap("orderId", orderId, "listFiles", listFiles, "userLogin", userLogin, "locale", locale));
                	if (ServiceUtil.isError(resultValue)) {
                		errMsgList.add(ServiceUtil.getErrorMessage(resultValue));
                	}
                }
 	        }
		} catch (Exception e) {
			Debug.logError(e, module);
			try {
			    TransactionUtil.rollback(beganTx, e.getMessage(), e);
			} catch (Exception e1) {
			    Debug.logError(e1, module);
			}
			errMsgList.add(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
			request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
			return "error";
		} catch (Throwable t) {
			Debug.logError(t, module);
			request.setAttribute("_ERROR_MESSAGE_", t.getMessage());
			try {
			    TransactionUtil.rollback(beganTx, t.getMessage(), t);
			} catch (Exception e2) {
			    Debug.logError(e2, module);
			}
			errMsgList.add(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
			request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
			return "error";
		} finally {
			if (UtilValidate.isNotEmpty(errMsgList)) {
				try {
			        TransactionUtil.rollback(beganTx, "Have error when process", null);
			    } catch (Exception e2) {
			        Debug.logError(e2, module);
			    }
				request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
				return "error";
			} else {
				// commit the transaction
			    try {
			        TransactionUtil.commit(beganTx);
			    } catch (Exception e) {
			        Debug.logError(e, module);
			    }
			}
		}
		request.setAttribute("orderId", orderId);
		return "success";
    }
}
