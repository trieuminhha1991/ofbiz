package com.olbius.basesales.shoppingcart;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartHelper;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.product.config.ProductConfigWorker;
import org.ofbiz.product.config.ProductConfigWrapper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.product.ProductConfig2Worker;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.security.util.SecurityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ShoppingCartServices {
	public static final String module = ShoppingCartServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
    public static final String resource_order_origin = "OrderUiLabels";
	public static final String resource_order_error_origin = "OrderErrorUiLabels";
    
	@SuppressWarnings("unchecked")
	public static Map<String, Object> initializeSalesOrderEntry(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	Security security = ctx.getSecurity();
    	//HttpServletRequest request = (HttpServletRequest) context.get("request");
    	//HttpServletResponse response = (HttpServletResponse) context.get("response");
    	
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	//Map<String, Object> returnMessage = FastMap.newInstance();
    	
    	ShoppingCart cart = (ShoppingCart) context.get("shoppingCart");
    	
		try {
	    	
	    	//List<String> alertMessageList = FastList.newInstance();
	    	//Locale locale = UtilHttp.getLocale(request);
	        
			Map<String, Object> resultCheckValidParams = checkValidateOrderEntry(context);
	    	if (ServiceUtil.isError(resultCheckValidParams)) {
	    		return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCheckValidParams));
	    	}
	    	
	    	String orderMode = "SALES_ORDER";
	    	String customerId = (String) context.get("partyId");
	    	String productStoreId = (String) context.get("productStoreId");
	    	GenericValue productStore = null;
	        if (UtilValidate.isNotEmpty(productStoreId)) {
	            productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
	        }
	    	if (productStore == null) {
	    		return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSNotFoundProductStore", locale));
	    	}
	    	boolean hasPermission = hasPermissionOrder(delegator, security, userLogin, locale, productStore, orderMode, customerId);
	    	if (!hasPermission) {
	    		if (cart != null) cart.clear();
                return ServiceUtil.returnError(UtilProperties.getMessage(resource,"BSOrderYouDoNotHavePermissionToTakeOrdersForThisStore", locale));
            }
	    	
	        // process customer Id
	        String billingCustomerId = null;
	        // Check party parent payment order of this customer
			EntityFindOptions optsLimitOne = new EntityFindOptions();
			optsLimitOne.setLimit(1);
			List<EntityCondition> exprs = FastList.newInstance();
			exprs.add(EntityCondition.makeCondition("partyIdTo", customerId));
			exprs.add(EntityCondition.makeCondition("roleTypeIdFrom", "CUSTOMER"));
			exprs.add(EntityCondition.makeCondition("roleTypeIdTo", "CHILD_MEMBER"));
			exprs.add(EntityCondition.makeCondition("partyRelationshipTypeId", "OWNER"));
			exprs.add(EntityUtil.getFilterByDateExpr());
			GenericValue listCustomerMember = EntityUtil.getFirst(delegator.findList("PartyRelationship", EntityCondition.makeCondition(exprs), null, null, optsLimitOne, false));
			if (UtilValidate.isEmpty(listCustomerMember)) {
				billingCustomerId = customerId;
			} else {
				billingCustomerId = listCustomerMember.getString("partyIdFrom");
			}
			// check role placing customer is customer of product store
			exprs.clear();
			exprs.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyId", customerId)));
			exprs.add(EntityCondition.makeCondition("roleTypeId", "CUSTOMER"));
			exprs.add(EntityCondition.makeCondition("productStoreId", productStoreId));
			exprs.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> listCustomer = null;
			try {
				List<GenericValue> listProductStoreCustomer = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(exprs), null, null, optsLimitOne, false);
				listCustomer = delegator.findList("PartyFullNameDetailSimple", EntityCondition.makeCondition("partyId", customerId), null, null, null, false);
				if (UtilValidate.isEmpty(listProductStoreCustomer)) {
					if (listCustomer.size()>0){
						return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSCustomerIsNotExistInStore", UtilMisc.toMap("customerId", listCustomer.get(0).getString("partyCode")), locale));
					}else{
						return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSCustomerIsNotExistInStore", UtilMisc.toMap("customerId", customerId), locale));
					}
				}
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorCheckRoleCustomerInStore", UtilMisc.toMap("customerId", customerId), locale));
			}
	    	
	    	String orderId = (String) context.get("orderId");
			String orderName = (String) context.get("orderName");
			String externalId = (String) context.get("externalId");
			//String desiredDeliveryDate = (String) context.get("desiredDeliveryDate");
			//String shipAfterDate = (String) context.get("shipAfterDate");
			//String shipBeforeDate = (String) context.get("shipBeforeDate");
			/*
			String shippingContactMechId = (String) context.get("shippingContactMechId");
			String shippingMethodTypeId = (String) context.get("shippingMethodTypeId");
			String checkOutPaymentId = (String) context.get("checkOutPaymentId");*/
			List<Object> listProd = (List<Object>) context.get("listProd");
	    	
			List<Map<String, Object>> listProduct = FastList.newInstance();
			
			boolean isJson = false;
	    	if (UtilValidate.isNotEmpty(listProd) && listProd.size() > 0){
	    		if (listProd.get(0) instanceof String) isJson = true;
	    	}
			if (isJson) {
				String listProductStr = "[" + (String) listProd.get(0) + "]";
				JSONArray jsonArray = new JSONArray();
				if (UtilValidate.isNotEmpty(listProductStr)) {
					jsonArray = JSONArray.fromObject(listProductStr);
				}
				if (jsonArray != null && jsonArray.size() > 0) {
					for (int i = 0; i < jsonArray.size(); i++) {
						JSONObject prodItem = jsonArray.getJSONObject(i);
						Map<String, Object> productItem = FastMap.newInstance();
						productItem.put("productId", prodItem.getString("productId"));
						if (prodItem.containsKey("quantityUomId")) productItem.put("quantityUomId", prodItem.getString("quantityUomId"));
						if (prodItem.containsKey("quantity")) productItem.put("quantityStr", prodItem.getString("quantity"));
						if (prodItem.containsKey("quantityReturnPromo")) productItem.put("quantityReturnPromoStr", prodItem.getString("quantityReturnPromo"));
						//if (prodItem.containsKey("idUPCA")) productItem.put("idUPCA", prodItem.getString("idUPCA"));
						if (prodItem.containsKey("idEAN")) productItem.put("idEAN", prodItem.getString("idEAN"));
						if (prodItem.containsKey("amount")) productItem.put("amount", prodItem.getString("amount"));
						if (prodItem.containsKey("itemComment")) productItem.put("itemComment", prodItem.getString("itemComment"));
						listProduct.add(productItem);
					}
				}
			} else {
				// listProd instance of List<Map<String, Object>>
				listProduct = (List<Map<String, Object>>) context.get("listProd");
			}
			
			//too request.setAttribute("may_split", "false");
			//too request.setAttribute("is_gift", "false");
	    	
	    	/* session.setAttribute("productStoreId", productStoreId);
	    	 * purpose:
	    	 * - get locale: defaultLocaleString attribute
	    	 * - get currencyUom: defaultCurrencyUomId attribute
	    	 */
			String currencyUom = (String) context.get("currencyUom");
			if (cart == null) {
				cart = com.olbius.basesales.shoppingcart.ShoppingCartEvents.getCartObject(delegator, dispatcher, 
		        		cart, locale, currencyUom, productStore, null, null, null, null, 
		        		userLogin, null, null);
			}
	        
	        if (cart == null) {
	        	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSCreateCartIsNotSuccess", locale));
	        }
	        
	        //cart.setProductStoreId(productStoreId);
	        
         	// cart = ShoppingCartEvents.getCartObject(request, null, productStore.getString("defaultCurrencyUomId"));
	        
	        String salesChannelEnumId = (String) context.get("salesChannelEnumId");
	        String originOrderId = (String) context.get("originOrderId");
	        String correspondingPoId = (String) context.get("correspondingPoId");
	        String agreementId = (String) context.get("agreementId");
	        String desiredDeliveryDateStr = (String) context.get("desiredDeliveryDate");
	        String shipAfterDateStr = (String) context.get("shipAfterDate");
	        String shipBeforeDateStr = (String) context.get("shipBeforeDate");
	        
	        Map<String, Object> otherParams = FastMap.newInstance();
	        String salesExecutiveId = (String) context.get("salesExecutiveId");
	        String requestFavorDelivery = (String) context.get("requestFavorDelivery");
	        String favorSupplierPartyId = (String) context.get("favorSupplierPartyId");
	        String shipGroupFacilityId = (String) context.get("shipGroupFacilityId");
	        
	        String shipFromFacilityConsign = (String) context.get("shipFromFacilityConsign");
	        String favorDistributorPartyId = (String) context.get("favorDistributorPartyId");
	        String facilityConsignId = (String) context.get("facilityConsignId");
	        String priority = (String) context.get("orderPriorityId");
	        
	        otherParams.put("salesExecutiveId", salesExecutiveId);
	        otherParams.put("requestFavorDelivery", requestFavorDelivery);
	        otherParams.put("favorSupplierPartyId", favorSupplierPartyId);
	        otherParams.put("shipGroupFacilityId", shipGroupFacilityId);
	        otherParams.put("priority", priority);
	        otherParams.put("externalId", externalId);
	        otherParams.put("billingCustomerId", billingCustomerId);
	        
	    	Map<String, Object> result1 = initializeOrderInfoGeneral(delegator, dispatcher, security, userLogin, locale, orderMode, cart, 
	    			productStore, customerId, orderId, orderName, desiredDeliveryDateStr, shipAfterDateStr, shipBeforeDateStr, listProduct, 
	    			salesChannelEnumId, originOrderId, correspondingPoId, agreementId, otherParams);
	    	if (ServiceUtil.isError(result1)) {
	    		return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result1));
	    	}
	    	
	    	if ("true".equals(shipFromFacilityConsign)) {
	    		cart.setFacilityId(facilityConsignId);
	    		cart.setAttribute("isFavorDelivery", "Y");
	    		cart.setAttribute("favorDistributorPartyId", favorDistributorPartyId);
	    	}
	    	
	    	// TODOCHANGE apply usePriceWithTax attribute in promotion condition
	    	//ProductPromoWorker.doPromotions(cart, dispatcher);
	    	// end TODOCHANGE
	    	
    		// drop ship
	    	String shippingContactMechId = (String) context.get("shipping_contact_mech_id");
	    	if (UtilValidate.isEmpty(shippingContactMechId)) shippingContactMechId = (String) context.get("contactMechId");
	        if (requestFavorDelivery != null) {
	        	if ("true".equals(requestFavorDelivery)) {
	        		Map<String, Object> result4 = initRequestFavorDelivery(delegator, dispatcher, cart, shippingContactMechId, favorSupplierPartyId, shipGroupFacilityId);
	            	if (ServiceUtil.isError(result4)) {
    	    			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result4));
	            	}
	        	}
	        }
	        
	        String shipToCustomerPartyId = (String) context.get("shipToCustomerPartyId");
	    	cart.setShipToCustomerPartyId(shipToCustomerPartyId);
		    
	        String shippingInstructions = (String) context.get("shipping_instructions");
	    	if (UtilValidate.isNotEmpty(listProduct)) {
		        // process accounting transaction
		        // check payToParty has account: yes/no
		        String payToParty = cart.getBillFromVendorPartyId();
		        GenericValue payToPartyAcctgPreference = null;
		        try {
					payToPartyAcctgPreference = delegator.findOne("PartyAcctgPreference", UtilMisc.toMap("partyId", payToParty), false);
				} catch (GenericEntityException e) {
		        	Debug.logWarning("Error when select payToParty from PartyAcctgPreference", module);
				}
		        
		        boolean runPromo = false;
		        
		        String shippingMethod = (String) context.get("shipping_method");
		        String orderAdditionalEmails = (String) context.get("order_additional_emails");
		        String maySplit = "false"; // (String) context.get("may_split" );
		        String giftMessage = (String) context.get("gift_message");
		        String isGift = "false"; // request.getParameter("is_gift");
		        String internalCode = (String) context.get("internalCode");

		        List<String> paymentMethods = (List<String>) context.get("checkOutPaymentId");
		        String issuerId = (String) context.get("issuerId");
		        
		        String billingAccountId = (String) context.get("billingAccountId");
		    	String billingAccountAmount = (String) context.get("billingAccountAmount");
		    	String taxAuthPartyGeoIds = (String) context.get("taxAuthPartyGeoIds");
		    	String partyTaxId = (String) context.get("partyTaxId");
		    	String isExempt = (String) context.get("isExempt");
		    	Map<String, Object> securityCodeMap = (Map<String, Object>) context.get("securityCodeMap");
		    	Map<String, Object> amountMap = (Map<String, Object>) context.get("amountMap");
		    	String addGiftCard = (String) context.get("addGiftCard");
		    	String giftCardNumber = (String) context.get("giftCardNumber");
		    	String giftCardPin = (String) context.get("giftCardPin");
		    	String giftCardAmount = (String) context.get("giftCardAmount");
		    	String singleUseGiftCard = (String) context.get("singleUseGiftCard");
		        
		        if (payToPartyAcctgPreference != null) {
		        	Map<String, Object> result3 = initializeOrderCheckoutOption(delegator, dispatcher, cart, runPromo, 
		        			billingAccountId, billingAccountAmount, shippingMethod, shippingContactMechId, taxAuthPartyGeoIds, partyTaxId, 
		        			isExempt, shippingInstructions, orderAdditionalEmails, maySplit, giftMessage, isGift, internalCode, shipBeforeDateStr, shipAfterDateStr, 
		        			paymentMethods, issuerId, securityCodeMap, amountMap, 
		        			addGiftCard, giftCardNumber, giftCardPin, giftCardAmount, customerId, singleUseGiftCard);
		        	if (ServiceUtil.isError(result3)) {
		    			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result3));
		        	}
		        } else {
		        	// process without accounting transaction
		        	Map<String, Object> result3 = initializeOrderCheckoutOptionWithoutAccTrans(delegator, dispatcher, cart, runPromo, 
		        			billingAccountId, billingAccountAmount, shippingMethod, shippingContactMechId, taxAuthPartyGeoIds, partyTaxId, 
		        			isExempt, shippingInstructions, orderAdditionalEmails, maySplit, giftMessage, isGift, internalCode, shipBeforeDateStr, shipAfterDateStr, 
		        			paymentMethods, issuerId, securityCodeMap, amountMap, 
		        			addGiftCard, giftCardNumber, giftCardPin, giftCardAmount, customerId, singleUseGiftCard);
		        	if (ServiceUtil.isError(result3)) {
		    			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result3));
		        	}
		        }
	    	}
	        
	    	String internalOrderNotes = (String) context.get("internal_order_notes");
	    	String shippingNotes = (String) context.get("shippingNotes");
	    	if (UtilValidate.isNotEmpty(internalOrderNotes)) {
	            cart.addInternalOrderNote(internalOrderNotes);
	        }
	    	// Shipping Notes for order will be public
	        if (UtilValidate.isNotEmpty(shippingNotes)) {
	            cart.addOrderNote(shippingNotes);
	        }
	        // shipping instructions => add to global notes
	    	if (UtilValidate.isNotEmpty(shippingInstructions)) {
	    		cart.addOrderNote(shippingInstructions);
	    	}
		} catch (Exception e) {
			String errMsg = "Fatal error calling initializeSalesOrderEntry service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("shoppingCart", cart);
		return successResult;
	}
	
	private static boolean hasPermissionOrder(Delegator delegator, Security security, GenericValue userLogin, Locale locale, 
			GenericValue productStore, String orderMode, String customerId) {
		if (productStore == null) return false;
		
    	String productStoreId = productStore.getString("productStoreId");
        // check permission for taking the order
        boolean hasPermission = false;
        if ("SALES_ORDER".equals(orderMode)) {
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
        return hasPermission;
	}
    
    private static Map<String, Object> checkValidateOrderEntry(Map<String, Object> context) {
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	
    	Locale locale = (Locale) context.get("locale");
        String productStoreId = (String) context.get("productStoreId");
        String partyId = (String) context.get("partyId");
        //String currencyUomId = (String) context.get("currencyUomId");
        String salesChannelEnumId = (String) context.get("salesChannelEnumId");
        String desiredDeliveryDate = (String) context.get("desiredDeliveryDate");
        String shipAfterDate = (String) context.get("shipAfterDate");
        String shipBeforeDate = (String) context.get("shipBeforeDate");
        
        List<String> errMsgList = FastList.newInstance();
        if (UtilValidate.isEmpty(productStoreId)) {
        	errMsgList.add(UtilProperties.getMessage(resource_error,"OrderAProductStoreMustBeSelectedForASalesOrder", locale));
        	//errMsgList.add(UtilProperties.getMessage(resource_error,"BSProductStoreNotBeEmpty", locale));
        }
        if (UtilValidate.isEmpty(partyId)) {
        	errMsgList.add(UtilProperties.getMessage(resource_error,"BSCustomerMustNotBeEmpty", locale));
        }
        /*if (UtilValidate.isEmpty(currencyUomId)) {
        	errMsgList.add(UtilProperties.getMessage(resource_error, "BSCurrencyUomIdMustNotBeEmpty", locale));
        }*/
        if (!"MOBILE_SALES_CHANNEL".equals(salesChannelEnumId) 
        		&& UtilValidate.isEmpty(desiredDeliveryDate) 
        		&& UtilValidate.isEmpty(shipAfterDate) 
        		&& UtilValidate.isEmpty(shipBeforeDate)) {
        	errMsgList.add(UtilProperties.getMessage(resource_error,"BSDesiredDeliveryDateMustNotBeEmpty", locale));
        }
        if (UtilValidate.isNotEmpty(errMsgList)) {
        	return ServiceUtil.returnError(errMsgList);
        }
        
    	return successResult;
    }
    
    private static Map<String, Object> initializeOrderInfoGeneral(Delegator delegator, LocalDispatcher dispatcher, Security security, GenericValue userLogin, Locale locale, 
    		String orderMode, ShoppingCart cart, GenericValue productStore, String customerId, String orderId, String orderName, String desiredDeliveryDateStr,
			String shipAfterDateStr, String shipBeforeDateStr, List<Map<String, Object>> listProduct, 
			String salesChannelEnumId, String originOrderId, String correspondingPoId, String agreementId, Map<String, Object> otherParams) {
    	if (cart == null) return ServiceUtil.returnError("Cart is null");
    	
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
        
        // TODO: re-factor and move this inside the ShoppingCart constructor
        cart.setOrderType(orderMode);
        String productStoreId = null;
        if (productStore != null) {
        	productStoreId = productStore.getString("productStoreId");
        	cart.setProductStoreId(productStoreId);
        } else {
            cart.setProductStoreId(null);
        }
        
        cart.setChannelType(salesChannelEnumId);
        cart.setAttribute("originOrderId", originOrderId);
        
        // set party info
        String partyId = customerId;
        if (partyId != null) {
            if (UtilValidate.isNotEmpty(partyId)) {
                GenericValue thisParty = null;
                try {
                    thisParty = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
                } catch (GenericEntityException gee) {
                    Debug.logWarning("Error when get party info", module);
                }
                if (thisParty == null) {
                	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderCouldNotLocateTheSelectedParty", locale));
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
        cart.setBillToCustomerPartyId((String) otherParams.get("billingCustomerId"));
        
    	// STEP 2: ShoppingCartEvents.java - method setOrderCurrencyAgreementShipDates
        // request method for setting the currency, agreement, OrderId and shipment dates at once
    	// have parameters: agreementId, shipBeforeDateStr, shipAfterDateStr,
        // not get parameters: workEffortId, cancelBackOrderDateStr,
        // move up: currencyUomId
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        String currencyUomId = productStore.getString("defaultCurrencyUomId");
        Map<String, Object> result = null;
        if (UtilValidate.isNotEmpty(cart.getCurrency()) && UtilValidate.isNotEmpty(currencyUomId)) {
            result = cartHelper.setCurrency(currencyUomId);
            if (ServiceUtil.isError(result)) {
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
            }
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
                return ServiceUtil.returnError(UtilProperties.getMessage("OrderErrorUiLabels", "OrderIdAlreadyExistsPleaseChooseAnother", locale));
            }
        }
        
        String externalId = null;
        if (UtilValidate.isNotEmpty(otherParams.get("externalId"))) externalId = (String) otherParams.get("externalId");
    	cart.setExternalId(externalId);
        
        cart.setOrderName(orderName);
        cart.setPoNumber(correspondingPoId);
        // end STEP 2 -----------------------------------------------
        
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
        	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenFormatDateTime", locale));
        }
        if (UtilValidate.isNotEmpty(desiredDeliveryDate)) cart.setDefaultItemDeliveryDate(desiredDeliveryDate.toString());
        if (UtilValidate.isNotEmpty(shipAfterDate)) cart.setDefaultShipAfterDate(shipAfterDate);
        if (UtilValidate.isNotEmpty(shipBeforeDate)) cart.setDefaultShipBeforeDate(shipBeforeDate);
        
        // New method 25/11/2014, Find and add agreement into Shopping cart
        // Update method 19/02/2016
        // get applicable agreements for order entry
        if (UtilValidate.isEmpty(agreementId)) {
        	// default select
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
                return ServiceUtil.returnError("Error when get info agreement of order party");
            }
        }
        // set the agreement if specified otherwise set the currency
        if (UtilValidate.isNotEmpty(agreementId)) {
            Map<String, Object> resultAgreement = cartHelper.selectAgreement(agreementId);
            if (ServiceUtil.isError(resultAgreement)) {
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultAgreement));
            }
        }
        // END TODOCHANGE
        
        // TODOCHANGE add new attribute: "salesMethodChannelEnumId", "salesExecutiveId", "isFavorDelivery"
        /*String salesMethodChannelEnumId = null;
        if (productStore != null && productStore.containsKey("salesMethodChannelEnumId")) salesMethodChannelEnumId = productStore.getString("salesMethodChannelEnumId");
        if (salesMethodChannelEnumId != null) {
            cart.setAttribute("salesMethodChannelEnumId", salesMethodChannelEnumId);
        }*/
        String salesExecutiveId = (String) otherParams.get("salesExecutiveId");
        if (salesExecutiveId != null) {
        	cart.setAttribute("salesExecutiveId", salesExecutiveId);
        }
        String requestFavorDelivery = (String) otherParams.get("requestFavorDelivery");
        if (requestFavorDelivery != null) {
        	if ("true".equals(requestFavorDelivery)) cart.setAttribute("isFavorDelivery", "Y");
        	else cart.setAttribute("isFavorDelivery", "N");
        } else {
        	cart.setAttribute("isFavorDelivery", "N");
        }
        String favorSupplierPartyId = (String) otherParams.get("favorSupplierPartyId");
        if (favorSupplierPartyId != null) {
        	cart.setAttribute("favorSupplierPartyId", favorSupplierPartyId);
        }
        String shipGroupFacilityId = (String) otherParams.get("shipGroupFacilityId");
        if (shipGroupFacilityId != null) {
        	cart.setAttribute("shipGroupFacilityId", shipGroupFacilityId);
        }
        String priority = (String) otherParams.get("priority");
        if (priority != null) {
        	cart.setAttribute("priority", priority);
        }
        
        // STEP 3: ShoppingCartEvents.java - method addToCart
        // Event to add an item to the shopping cart
        // not get parameters: controlDirective, xxx, parentProductId, itemType, itemDescription, productCategoryId, xxx, 
        //				price(BigDecimal), xxx, quantity(BigDecimal), reservStartStr, reservEndStr, reservStart(Timestamp), reservEnd(Timestamp),
        //				reservLengthStr, reservLength(BigDecimal), reservPersonsStr, reservPersons(BigDecimal), accommodationMapId, accommodationSpotId, 
        //				xxx, xxx, shipBeforeDate(Timestamp), shipAfterDate(Timestamp), numberOfDay
        // use parameters: productId, priceStr, quantityStr, shipBeforeDateStr, shipAfterDateStr
        String catalogId = ShoppingCartWorker.getCurrentCatalogId(delegator, userLogin, null, productStoreId, null);
        
        // remove paramMap (UtilHttp.getCombinedMap(request))
        
        // Call multiple addToCart method
        
        if (UtilValidate.isNotEmpty(listProduct)) {
        	for (Map<String, Object> productItem : listProduct) {
            	String productId = (String) productItem.get("productId");
    			String quantityUomId = (String) productItem.get("quantityUomId");
    			String quantityStr = (String) productItem.get("quantityStr");
    			String quantityReturnPromoStr = (String) productItem.get("quantityReturnPromoStr");
    			//String idUPCA = (String) productItem.get("idUPCA");
    			String idEAN = (String) productItem.get("idEAN");
    			String amountStr = (String) productItem.get("amount");
    			String itemComment = (String) productItem.get("itemComment");
    			
    			BigDecimal price = null;
    			BigDecimal amount = null;
    			BigDecimal quantity = BigDecimal.ZERO;

    			BigDecimal priceReturnPromo = null;
    			BigDecimal quantityReturnPromo = BigDecimal.ZERO;
    			
    			GenericValue product = null;
				try {
					product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
				} catch (Exception e) {
		            Debug.logWarning(e, "Problems get product: " + productId, module);
		        }
    			
    			if (product != null && UtilValidate.isNotEmpty(productId) && (UtilValidate.isNotEmpty(quantityStr) || UtilValidate.isNotEmpty(quantityReturnPromoStr))) {
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
    				
    				boolean isModifyPrice = false;
    				BigDecimal newPrice = null;
    				if ("Y".equals(product.getString("requireAmount"))) {
    					try {
	    					String amountUomTypeId = product.getString("amountUomTypeId");
	    					/*if (UtilValidate.isNotEmpty(idUPCA) && UtilValidate.isNotEmpty(amountUomTypeId)) {
	    						newPrice = com.olbius.basesales.product.ProductWorker.getPriceProductUpc(product, idUPCA, delegator, locale);
	    						if (newPrice == null) {
	    							amount = com.olbius.basesales.product.ProductWorker.getAmountProductUpc(product, idUPCA, delegator, dispatcher, locale);
	    						} else {
	    							isModifyPrice = true;
	    						}
	    					}*/
	    					if (UtilValidate.isNotEmpty(idEAN) && UtilValidate.isNotEmpty(amountUomTypeId)) {
    							amount = com.olbius.basesales.product.ProductWorker.getAmountProductEan(idEAN, delegator, locale);
	    					}
	    					if (newPrice == null && amount == null) {
	    						if (UtilValidate.isNotEmpty(amountStr)) {
	    							try {
	    	        		            amount = (BigDecimal) ObjectType.simpleTypeConvert(amountStr, "BigDecimal", null, locale);
	    	        		        } catch (Exception e) {
	    	        		            Debug.logWarning(e, "Problems parsing amount string: " + amountStr, module);
	    	        		            amount = null;
	    	        		        }
	    						}
	    						if (amount == null) {
	    							return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSPriceOfProductNotFound", locale) + " " + product.getString("productCode"));
	    						}
	    					}
    					} catch (Exception e) {
    						return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSPriceOfProductNotFound", locale) + " " + productId);
    					}
    				} else {
    					try {
        		            price = (BigDecimal) ObjectType.simpleTypeConvert(priceStr, "BigDecimal", null, locale);
        		            priceReturnPromo = (BigDecimal) ObjectType.simpleTypeConvert(priceStr, "BigDecimal", null, locale);
        		        } catch (Exception e) {
        		            Debug.logWarning(e, "Problems parsing price string: " + priceStr, module);
        		            price = null;
        		            priceReturnPromo = null;
        		        }
    				}
    				
    				try {
    					if (UtilValidate.isNotEmpty(quantityStr) && !"null".equals(quantityStr)) {
    						quantity = (BigDecimal) ObjectType.simpleTypeConvert(quantityStr, "BigDecimal", null, locale);
        		            //For quantity we should test if we allow to add decimal quantity for this product an productStore : if not then round to 0
        		            if(! ProductWorker.isDecimalQuantityOrderAllowed(delegator, productId, cart.getProductStoreId())){
        		                quantity = quantity.setScale(0, UtilNumber.getBigDecimalRoundingMode("order.rounding"));
        		            } else {
        		                quantity = quantity.setScale(UtilNumber.getBigDecimalScale("order.decimals"), UtilNumber.getBigDecimalRoundingMode("order.rounding"));
        		            }
    					}
    		            
    					if (UtilValidate.isNotEmpty(quantityReturnPromoStr) && !"null".equals(quantityReturnPromoStr)) {
    						quantityReturnPromo = (BigDecimal) ObjectType.simpleTypeConvert(quantityReturnPromoStr, "BigDecimal", null, locale);
        		            if (quantityReturnPromo != null) {
        		            	//For quantity we should test if we allow to add decimal quantity for this product an productStore : if not then round to 0
            		            if(! ProductWorker.isDecimalQuantityOrderAllowed(delegator, productId, cart.getProductStoreId())){
            		                quantityReturnPromo = quantityReturnPromo.setScale(0, UtilNumber.getBigDecimalRoundingMode("order.rounding"));
            		            } else {
            		                quantityReturnPromo = quantityReturnPromo.setScale(UtilNumber.getBigDecimalScale("order.decimals"), UtilNumber.getBigDecimalRoundingMode("order.rounding"));
            		            }
        		            }
    					}
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
    		                    if (UtilValidate.isNotEmpty(productList)) {
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
    				if (quantity != null && BigDecimal.ZERO.compareTo(quantity) < 0) {
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
        				
        				// Get the ProductConfigWrapper (it's not null only for configurable items)
        		        ProductConfigWrapper configWrapper = null;
        		        // configWrapper = ProductConfigWorker.getProductConfigWrapper(productId, cart.getCurrency())
        		        configWrapper = ProductConfig2Worker.getProductConfigWrapper(productId, currencyUomId, delegator, dispatcher, locale, catalogId, null, catalogId, productStoreId, null, quantityUomId);

        		        if (configWrapper != null) {
        		            if (paramMap.containsKey("configId")) {
        		                try {
        		                    configWrapper.loadConfig(delegator, (String) paramMap.remove("configId"));
        		                } catch (Exception e) {
        		                    Debug.logWarning(e, "Could not load configuration", module);
        		                }
        		            } else {
        		                // The choices selected by the user are taken from request and set in the wrapper
        		            	Map<String, Object> fillProductResult = ProductConfig2Worker.fillProductConfigWrapper(configWrapper, delegator, null, locale);
        		            	if (ServiceUtil.isError(fillProductResult)) return ServiceUtil.returnError(ServiceUtil.getErrorMessage(fillProductResult));
        		            }
        		            if (!configWrapper.isCompleted()) {
        		                // The configuration is not valid
        		                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "cart.addToCart.configureProductBeforeAddingToCart", locale));
        		            } else {
        		                // load the Config Id
        		            	ProductConfigWorker.storeProductConfigWrapper(configWrapper, delegator);
        		            }
        		        }
        		        
        		        // add order item attribute
        		        /*if (UtilValidate.isNotEmpty(idUPCA)) {
        		        	String orderItemAttributePrefix = UtilProperties.getPropertyValue("order.properties", "order.item.attr.prefix");
        		        	paramMap.put(orderItemAttributePrefix + "idUPCA", idUPCA);
        		        }*/
        		        /*if (UtilValidate.isNotEmpty(idUPCA)) {
        					paramMap.put("idUPCA", idUPCA);
        				}*/
        		        if (UtilValidate.isNotEmpty(idEAN)) {
        					paramMap.put("idEAN", idEAN);
        				}
        		        if (UtilValidate.isNotEmpty(itemComment)) {
        		        	paramMap.put("itemComment", itemComment);
        		        }
        				
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
        		        result = cartHelper.addToCart(catalogId, null, null, productId, null, null, null, price, amount, quantity, 
        		        		null, null, null, null, null, shipBeforeDate, shipAfterDate, configWrapper, null, paramMap, null, Boolean.FALSE);
        				
        		        if (ServiceUtil.isError(result)) {
        		        	return result;
        		        }
        		        if (isModifyPrice) {
        		        	Integer itemId = (Integer) result.get("itemId");
            		        if (itemId != null) {
            		        	ShoppingCartItem item = cart.findCartItem(itemId);
            		        	if (item != null) {
            		        		item.setBasePrice(newPrice); // this is quantity because the parsed number variable is the same as quantity
                                    item.setDisplayPrice(newPrice); // or the amount shown the cart items page won't be right
                                    item.setAlternativeUnitPrice(newPrice);
                                    item.setIsModifiedPrice(isModifyPrice); // flag as a modified price
            		        	}
            		        }
        		        }
        		        
        		        /* controlDirective = processResult(result, request);
    				        Integer itemId = (Integer)result.get("itemId");
    				        if (UtilValidate.isNotEmpty(itemId)) {request.setAttribute("itemId", itemId);}
    				        // Determine where to send the browser
    				        if (controlDirective.equals(ERROR)) {return "error";} else {if (cart.viewCartOnAdd()) {return "viewcart";} else {return "success";}} 
        		         */
    				}
    		        if (quantityReturnPromo != null && BigDecimal.ZERO.compareTo(quantityReturnPromo) < 0) {
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
        				String parentProductId2 = null;
        				// Translate the parameters and add to the cart
        		        result = cartHelper.addToCart(catalogId, null, null, productId, null,
        		        		"PRODPROMO_ORDER_ITEM", null, priceReturnPromo, null, quantityReturnPromo, null, null, null, null, null,
        		                shipBeforeDate, shipAfterDate, null, null, paramMap2, parentProductId2, Boolean.FALSE, Boolean.TRUE);
    		        }
    			}
            }
        }
        
        return successResult;
    }
    
    private static Map<String, Object> initRequestFavorDelivery(Delegator delegator, LocalDispatcher dispatcher, ShoppingCart cart, 
    		String shippingContactMechId, String supplierPartyId, String shipGroupFacilityId) {
		// org.ofbiz.order.shoppingcart.CheckOutEvents.finalizeOrderEntry(HttpServletRequest, HttpServletResponse)
		// Reassign items requiring drop-shipping to new or existing drop-ship groups
		
        // initialization
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
        for (int shipGroupIndex = 0; shipGroupIndex < cart.getShipGroupSize(); shipGroupIndex++) {
            // set the shipping method
            /* Old: 
        	shippingContactMechId = request.getParameter(shipGroupIndex + "_shipping_contact_mech_id");
        	String supplierPartyId = request.getParameter(shipGroupIndex + "_supplierPartyId");
            String facilityId = request.getParameter(shipGroupIndex + "_shipGroupFacilityId");
            */
            if (UtilValidate.isNotEmpty(shipGroupFacilityId)) {
                cart.setShipGroupFacilityId(shipGroupIndex, shipGroupFacilityId);
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
        /*
        // generate any messages required
        ServiceUtil.getMessages(request, callResult, null);
        // determine whether it was a success or not
        if (callResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR)) {
            return "shipping";
        }*/
        
		return callResult;
	}
    
    private static Map<String, Object> initializeOrderCheckoutOption(Delegator delegator, LocalDispatcher dispatcher, ShoppingCart cart, boolean runPromo, 
    		String billingAccountId, String billingAccountAmount, String shippingMethod, String shippingContactMechId, 
    		String taxAuthPartyGeoIds, String partyTaxId, String isExempt, String shippingInstructions, String orderAdditionalEmails, 
    		String maySplit, String giftMessage, String isGift, String internalCode, String shipBeforeDate, String shipAfterDate, 
    		List<String> paymentMethods, String issuerId, Map<String, Object> securityCodeMap, Map<String, Object> amountMap, 
    		String addGiftCard, String giftCardNumber, String giftCardPin, String giftCardAmount, String partyId, String singleUseGiftCard) {
    	// STEP 3: updateCheckoutOptions: When user selects a shipping method, this automatically reloads quick checkout page with shipping estimates filled in.
        // Click Quick ship
		
		// uri="updateCheckoutOptions" > call update shipping address before
		/*Map<String, Object> updateCheckoutOptionsResult = com.olbius.basesales.shoppingcart.CheckOutWorker.setPartialCheckOutOptions(delegator, dispatcher, cart, 
				billingAccountId, billingAccountAmount, shippingMethod, shippingContactMechId, taxAuthPartyGeoIds, partyTaxId, isExempt, shippingInstructions, 
				orderAdditionalEmails, maySplit, giftMessage, isGift, internalCode, shipBeforeDate, shipAfterDate, paymentMethods, issuerId, securityCodeMap, amountMap, 
				addGiftCard, giftCardNumber, giftCardPin, giftCardAmount, partyId, singleUseGiftCard);
        if (ServiceUtil.isError(updateCheckoutOptionsResult)) return ServiceUtil.returnError(ServiceUtil.getErrorMessage(updateCheckoutOptionsResult));*/
        
        // uri="checkout" > CheckOutEvents.setQuickCheckOutOptions
    	boolean runCalcTax = false;
        Map<String, Object> checkoutResult = com.olbius.basesales.shoppingcart.CheckOutWorker.setQuickCheckOutOptions(
        		delegator, dispatcher, cart, billingAccountId, billingAccountAmount, shippingMethod, shippingContactMechId, 
        		taxAuthPartyGeoIds, partyTaxId, isExempt, shippingInstructions, orderAdditionalEmails, 
        		maySplit, giftMessage, isGift, internalCode, shipBeforeDate, shipAfterDate, 
        		paymentMethods, issuerId, securityCodeMap, amountMap, 
        		addGiftCard, giftCardNumber, giftCardPin, giftCardAmount, partyId, singleUseGiftCard, runPromo, runCalcTax);
        if (ServiceUtil.isError(checkoutResult)) return ServiceUtil.returnError(ServiceUtil.getErrorMessage(checkoutResult));
        
        if (!runPromo) {
        	// TODOCHANGE apply usePriceWithTax attribute in promotion condition
        	ProductPromoWorker.doPromotions(cart, dispatcher);
        	// end TODOCHANGE
        }
        
        Map<String, Object> calcShippingResult = com.olbius.basesales.shoppingcart.ShippingWorker.getShipEstimate(delegator, dispatcher, cart, runPromo);
        if (ServiceUtil.isError(calcShippingResult)) return ServiceUtil.returnError(ServiceUtil.getErrorMessage(calcShippingResult));
        
        if (!runCalcTax) {
        	Map<String, Object> calcTax = com.olbius.basesales.shoppingcart.CheckOutWorker.calcTax(delegator, dispatcher, cart);
            if (ServiceUtil.isError(calcTax)) return ServiceUtil.returnError(ServiceUtil.getErrorMessage(calcTax));
            if (!runPromo) {
            	// TODOCHANGE apply usePriceWithTax attribute in promotion condition
            	ProductPromoWorker.doPromotions(cart, dispatcher);
            	// end TODOCHANGE
            }
        }
        
    	return ServiceUtil.returnSuccess();
    }
    
    private static Map<String, Object> initializeOrderCheckoutOptionWithoutAccTrans(Delegator delegator, LocalDispatcher dispatcher, ShoppingCart cart, boolean runPromo, 
    		String billingAccountId, String billingAccountAmount, String shippingMethod, String shippingContactMechId, 
    		String taxAuthPartyGeoIds, String partyTaxId, String isExempt, String shippingInstructions, String orderAdditionalEmails, 
    		String maySplit, String giftMessage, String isGift, String internalCode, String shipBeforeDate, String shipAfterDate, 
    		List<String> paymentMethods, String issuerId, Map<String, Object> securityCodeMap, Map<String, Object> amountMap, 
    		String addGiftCard, String giftCardNumber, String giftCardPin, String giftCardAmount, String partyId, String singleUseGiftCard) {
    	// STEP 3: updateCheckoutOptions: When user selects a shipping method, this automatically reloads quick checkout page with shipping estimates filled in.
        // Click Quick ship
		
    	boolean runCalcTax = false;
		// uri="updateCheckoutOptions" > call update shipping address before
		//String updateCheckoutOptionsResult = CheckOutEvents.setPartialCheckOutOptions(request, response);
        //if ("error".equals(updateCheckoutOptionsResult)) return "error";
        // uri="checkout" > CheckOutEvents.setQuickCheckOutOptions
        Map<String, Object> checkoutResult = com.olbius.basesales.shoppingcart.CheckOutWithoutAccTransWorker.setQuickCheckOutOptions(
        		delegator, dispatcher, cart, billingAccountId, billingAccountAmount, shippingMethod, shippingContactMechId, 
        		taxAuthPartyGeoIds, partyTaxId, isExempt, shippingInstructions, orderAdditionalEmails, 
        		maySplit, giftMessage, isGift, internalCode, shipBeforeDate, shipAfterDate, 
        		paymentMethods, issuerId, securityCodeMap, amountMap, 
        		addGiftCard, giftCardNumber, giftCardPin, giftCardAmount, partyId, singleUseGiftCard, runPromo, runCalcTax);
        if (ServiceUtil.isError(checkoutResult)) return ServiceUtil.returnError(ServiceUtil.getErrorMessage(checkoutResult));
        
        if (!runPromo) {
        	// TODOCHANGE apply usePriceWithTax attribute in promotion condition
        	ProductPromoWorker.doPromotions(cart, dispatcher);
        	// end TODOCHANGE
        }
        
        Map<String, Object> calcShippingResult = com.olbius.basesales.shoppingcart.ShippingWorker.getShipEstimate(delegator, dispatcher, cart, runPromo);
        if (ServiceUtil.isError(calcShippingResult)) return ServiceUtil.returnError(ServiceUtil.getErrorMessage(calcShippingResult));
        
        if (!runCalcTax) {
        	Map<String, Object> calcTax = com.olbius.basesales.shoppingcart.CheckOutWorker.calcTax(delegator, dispatcher, cart);
            if (ServiceUtil.isError(calcTax)) return ServiceUtil.returnError(ServiceUtil.getErrorMessage(calcTax));
        }
        
    	return ServiceUtil.returnSuccess();
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> modifyCart(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	Security security = ctx.getSecurity();
    	
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	
    	ShoppingCart cart = (ShoppingCart) context.get("shoppingCart");
    	if (cart == null) {
    		ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSCartIsNull", locale));
    	}
    	
    	boolean removeItemOut = (Boolean) context.get("removeItemOut");
		try {
			String productStoreId = cart.getProductStoreId();
			GenericValue productStore = null;
	        if (UtilValidate.isNotEmpty(productStoreId)) {
	            productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
	        }
	    	if (productStore == null) {
	    		return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSNotFoundProductStore", locale));
	    	}
	    	boolean hasPermission = hasPermissionOrder(delegator, security, userLogin, locale, productStore, cart.getOrderType(), cart.getOrderPartyId());
	    	if (!hasPermission) {
	    		if (cart != null) cart.clear();
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_order_error_origin,"OrderYouDoNotHavePermissionToTakeOrdersForThisStore", locale));
            }
	    	
			List<Object> listProd = (List<Object>) context.get("listProd");
	    	
			List<Map<String, Object>> listProduct = FastList.newInstance();
			
			boolean isJson = false;
			if (UtilValidate.isNotEmpty(listProd) && listProd.size() > 0){
	    		if (listProd.get(0) instanceof String) isJson = true;
	    	}
			if (isJson) {
				String listProductStr = "[" + (String) listProd.get(0) + "]";
				JSONArray jsonArray = new JSONArray();
				if (UtilValidate.isNotEmpty(listProductStr)) {
					jsonArray = JSONArray.fromObject(listProductStr);
				}
				if (jsonArray != null && jsonArray.size() > 0) {
					try {
						for (int i = 0; i < jsonArray.size(); i++) {
							JSONObject prodItem = jsonArray.getJSONObject(i);
							Map<String, Object> productItem = FastMap.newInstance();
							String productId = prodItem.getString("productId");
							String quantityStr = prodItem.getString("quantity");
							String quantityUomId = null;
							if (prodItem.containsKey("quantityUomId")) quantityUomId = prodItem.getString("quantityUomId");
							if (UtilValidate.isEmpty(quantityUomId)) {
								GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
								if (product != null) quantityUomId = product.getString("quantityUomId");
							}
							
							if (UtilValidate.isEmpty(productId) && UtilValidate.isEmpty(quantityStr) && UtilValidate.isEmpty(quantityUomId)) {
								continue;
							}
							
							productItem.put("productId", productId);
							productItem.put("quantityUomId", quantityUomId);
							
							if (prodItem.containsKey("itemIndex")) productItem.put("itemIndex", prodItem.getString("itemIndex"));
							if (prodItem.containsKey("description")) productItem.put("description", prodItem.getString("description"));
							if (prodItem.containsKey("shipBeforeDate")) {
								Timestamp shipBeforeDate = null;
								String shipBeforeDateStr = prodItem.getString("shipBeforeDate");
				    	        if (UtilValidate.isNotEmpty(shipBeforeDateStr)) {
				    	        	Long shipBeforeDateL = Long.parseLong(shipBeforeDateStr);
				    	        	shipBeforeDate = new Timestamp(shipBeforeDateL);
				    	        }
								productItem.put("shipBeforeDate", shipBeforeDate);
							}
							if (prodItem.containsKey("shipAfterDate")) {
								Timestamp shipAfterDate = null;
								String shipAfterDateStr = prodItem.getString("shipAfterDate");
								if (UtilValidate.isNotEmpty(shipAfterDateStr)) {
									Long shipAfterDateL = Long.parseLong(shipAfterDateStr);
									shipAfterDate = new Timestamp(shipAfterDateL);
								}
								productItem.put("shipAfterDate", shipAfterDate);
							}
							if (prodItem.containsKey("amount")) {
								String amountStr = prodItem.getString("amount");
								BigDecimal amount = null;
			    	        	if (UtilValidate.isNotEmpty(amountStr)) {
				                	// parse the quantity
				                    try {
				                    	amount = (BigDecimal) ObjectType.simpleTypeConvert(amountStr, "BigDecimal", null, locale);
				                    } catch (Exception e) {
				                        Debug.logWarning(e, "Problems parsing quantity string: " + amountStr, module);
				                        amount = BigDecimal.ZERO;
				                    }
				                }
			    	        	productItem.put("amount", amount);
							}
							if (prodItem.containsKey("itemType")) productItem.put("itemType", prodItem.getString("itemType"));
							
							BigDecimal quantity = (BigDecimal) ObjectType.simpleTypeConvert(quantityStr, "BigDecimal", null, locale);
				            //For quantity we should test if we allow to add decimal quantity for this product an productStore : if not then round to 0
				            if(!ProductWorker.isDecimalQuantityOrderAllowed(delegator, productId, cart.getProductStoreId())){
				                quantity = quantity.setScale(0, UtilNumber.getBigDecimalRoundingMode("order.rounding"));
				            } else {
				                quantity = quantity.setScale(UtilNumber.getBigDecimalScale("order.decimals"), UtilNumber.getBigDecimalRoundingMode("order.rounding"));
				            }
				            productItem.put("quantity", quantity);
				            
							if (prodItem.containsKey("isDelete")) productItem.put("isDelete", prodItem.getString("isDelete"));
							
							listProduct.add(productItem);
						}
					} catch (Exception e) {
		            	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenFormatData", locale));
		            }
				}
			} else {
				// listProd instance of List<Map<String, Object>>
				listProduct = (List<Map<String, Object>>) context.get("listProd");
			}
			boolean removeSelected = (Boolean) context.get("removeSelected");
			List<String> selectedItems = (List<String>) context.get("selectedItems");
			if (selectedItems == null) {
				selectedItems = FastList.newInstance();
			}
			if (UtilValidate.isNotEmpty(listProduct)) {
				Map<String, Object> resultUpdate = com.olbius.basesales.shoppingcart.ShoppingCartWorker.modifyCart(delegator, dispatcher, cart, security, userLogin, listProduct, true, removeSelected, selectedItems, locale, removeItemOut);
				if (ServiceUtil.isError(resultUpdate)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultUpdate));
				}
				
				String shippingContactMechId = (String) context.get("shipping_contact_mech_id");
				String shippingMethod = (String) context.get("shipping_method");
				List<String> paymentMethods = (List<String>) context.get("checkOutPaymentId");
				String maySplit = (String) context.get("may_split");
				String isGift = (String) context.get("is_gift");
				String shipBeforeDate = (String) context.get("shipBeforeDate");
				String shipAfterDate = (String) context.get("shipAfterDate");
				
				boolean runPromo = false;
				boolean runCalcTax = false;
				Map<String, Object> checkoutResult = com.olbius.basesales.shoppingcart.CheckOutWithoutAccTransWorker.setQuickCheckOutOptions(
		        		delegator, dispatcher, cart, null, null, shippingMethod, shippingContactMechId, 
		        		null, null, null, null, null, 
		        		maySplit, null, isGift, null, shipBeforeDate, shipAfterDate, 
		        		paymentMethods, null, null, null, 
		        		null, null, null, null, cart.getOrderPartyId(), null, runPromo, runCalcTax);
		        if (ServiceUtil.isError(checkoutResult)) return ServiceUtil.returnError(ServiceUtil.getErrorMessage(checkoutResult));
		        
	        	ProductPromoWorker.doPromotions(cart, dispatcher);
		        
		        Map<String, Object> calcShippingResult = com.olbius.basesales.shoppingcart.ShippingWorker.getShipEstimate(delegator, dispatcher, cart, runPromo);
		        if (ServiceUtil.isError(calcShippingResult)) return ServiceUtil.returnError(ServiceUtil.getErrorMessage(calcShippingResult));
		        
	        	Map<String, Object> calcTax = com.olbius.basesales.shoppingcart.CheckOutWorker.calcTax(delegator, dispatcher, cart);
	            if (ServiceUtil.isError(calcTax)) return ServiceUtil.returnError(ServiceUtil.getErrorMessage(calcTax));
			} else {
				return ServiceUtil.returnError("List product is empty");
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling modifyCart service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("shoppingCart", cart);
		return successResult;
	}
}
