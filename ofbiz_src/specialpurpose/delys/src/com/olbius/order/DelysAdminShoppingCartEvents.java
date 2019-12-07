package com.olbius.order;

import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartHelper;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.config.ProductConfigWorker;
import org.ofbiz.product.config.ProductConfigWrapper;
import org.ofbiz.product.imagemanagement.ImageManagementServices;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.store.ProductStoreSurveyWrapper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.control.RequestHandler;

/**
 * Shopping cart events.
 */
public class DelysAdminShoppingCartEvents {

    public static String module = ShoppingCartEvents.class.getName();
    public static final String resource = "OrderUiLabels";
    public static final String resource_error = "OrderErrorUiLabels";
    private static final String NO_ERROR = "noerror";
    private static final String NON_CRITICAL_ERROR = "noncritical";
    private static final String ERROR = "error";
    public static String currencyUom = null;
    public static final MathContext generalRounding = new MathContext(10);
    //private static String imagePath;
    
    /** Initialize step 1: info common - order entry **/
    public static String dainitializeOrderEntry(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        Security security = (Security) request.getAttribute("security");
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

        String productStoreId = request.getParameter("productStoreId");
        if (UtilValidate.isNotEmpty(productStoreId)) {
            session.setAttribute("productStoreId", productStoreId);
        } else {
        	if (session.getAttribute("productStoreId") != null) {
        		productStoreId = (String) session.getAttribute("productStoreId");
        	}
        }
        
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);

//        String agreementId = request.getParameter("agreementId");
//        String currencyUomId = request.getParameter("currencyUomId");
//        String workEffortId = request.getParameter("workEffortId");
//        String shipBeforeDateStr = request.getParameter("shipBeforeDate");
//        String shipAfterDateStr = request.getParameter("shipAfterDate");
//        String cancelBackOrderDateStr = request.getParameter("cancelBackOrderDate");
//        String orderId = request.getParameter("orderId");
//        String orderName = request.getParameter("orderName");
//        String correspondingPoId = request.getParameter("correspondingPoId");
        Map<String, Object> result = null;
        
        String currencyUomId = request.getParameter("currencyUomId");
        String createOrderDateStr = request.getParameter("createOrderDate");
        String shipInDateStr = request.getParameter("shipInDate");
//        String partyId_customer = request.getParameter("partyId_customer");
        //String partyId_sup = request.getParameter("partyId_sup");
        String orderId = request.getParameter("orderId");
        
        // TODO: re-factor and move this inside the ShoppingCart constructor
        String orderMode = request.getParameter("orderMode");
        if (orderMode != null) {
            cart.setOrderType(orderMode);
            session.setAttribute("orderMode", orderMode);
        } else {
        	if (session.getAttribute("orderMode") == null) {
        		request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderPleaseSelectEitherSaleOrPurchaseOrder", locale));
        		return "error";
        	}
        }
        
        String orderName = request.getParameter("orderName");
        if (UtilValidate.isEmpty(orderName)) {
        	orderName = cart.getOrderName();
        }
        
        // check the selected product store
        GenericValue productStore = null;
        if (UtilValidate.isNotEmpty(productStoreId)) {
            productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
            if (productStore != null) {

                // check permission for taking the order
                boolean hasPermission = false;
                if ((cart.getOrderType().equals("PURCHASE_ORDER")) && (security.hasEntityPermission("ORDERMGR", "_PURCHASE_CREATE", session))) {
                    hasPermission = true;
                } else if (cart.getOrderType().equals("SALES_ORDER")) {
                    if (security.hasEntityPermission("ORDERMGR", "_SALES_CREATE", session)) {
                        hasPermission = true;
                    } else {
                        // if the user is a rep of the store, then he also has permission
                        List<GenericValue> storeReps = null;
                        try {
                            storeReps = delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("productStoreId", productStore.getString("productStoreId"),
                                                            "partyId", userLogin.getString("partyId"), "roleTypeId", "SALES_REP"), null, false);
                        } catch (GenericEntityException gee) {
                            //
                        }
                        storeReps = EntityUtil.filterByDate(storeReps);
                        if (UtilValidate.isNotEmpty(storeReps)) {
                            hasPermission = true;
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
        }

//        if ("SALES_ORDER".equals(cart.getOrderType()) && UtilValidate.isEmpty(cart.getProductStoreId())) {
//            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderAProductStoreMustBeSelectedForASalesOrder", locale));
//            cart.clear();
//            session.removeAttribute("orderMode");
//            return "error";
//        }
        cart.setOrderType("SALES_ORDER");

        String salesChannel = "WEB_SALES_CHANNEL";
        cart.setChannelType(salesChannel);
        
        // set party info
        String partyId = request.getParameter("supplierPartyId");
        cart.setAttribute("supplierPartyId", partyId);
        String originOrderId = request.getParameter("originOrderId");
        cart.setAttribute("originOrderId", originOrderId);

        if (!UtilValidate.isEmpty(request.getParameter("partyId"))) {
            partyId = request.getParameter("partyId");
        }

        String partyId_customer = request.getParameter("partyId_customer");
        if (!UtilValidate.isEmpty(partyId_customer)) {
        	partyId = partyId_customer;
        } else {
        	if (cart.getEndUserCustomerPartyId() == null) {
        		request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"Ma nha phan phoi khong duoc de trong", locale));
                return "error";
        	}
        }
        
        
//        String partyId_sup = request.getParameter("partyId_sup");
//        if (!UtilValidate.isEmpty(partyId_sup)) {
//        	
//        } else {
//        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"Ma SUP khong duoc de trong", locale));
//            return "error";
//        }
        
        String userLoginId = request.getParameter("userLoginId");
        if (partyId != null || userLoginId != null) {
            if (UtilValidate.isEmpty(partyId) && UtilValidate.isNotEmpty(userLoginId)) {
                GenericValue thisUserLogin = null;
                try {
                    thisUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
                } catch (GenericEntityException gee) {
                    //
                }
                if (thisUserLogin != null) {
                    partyId = thisUserLogin.getString("partyId");
                } else {
                    partyId = userLoginId;
                }
            }
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
            } else if (partyId != null && partyId.length() == 0) {
                cart.setOrderPartyId("_NA_");
                partyId = null;
            }
        } else {
            partyId = cart.getPartyId();
            if (partyId != null && partyId.equals("_NA_")) partyId = null;
        }
        

//public static String setOrderCurrencyAgreementShipDates(HttpServletRequest request, HttpServletResponse response)
        // set the agreement if specified otherwise set the currency
//        if (UtilValidate.isNotEmpty(agreementId)) {
//            result = cartHelper.selectAgreement(agreementId);
//        } 
        if (UtilValidate.isNotEmpty(cart.getCurrency()) && UtilValidate.isNotEmpty(currencyUomId)) {
            result = cartHelper.setCurrency(currencyUomId);
        }
        if (ServiceUtil.isError(result)) {
            request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(result));
            return "error";
        }

        // set the work effort id
//        cart.setWorkEffortId(workEffortId);

        // set the order id if given
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
                request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderIdAlreadyExistsPleaseChooseAnother", locale));
                return "error";
            }
        }

        // set the order name
        cart.setOrderName(orderName);
    	
        if (UtilValidate.isNotEmpty(partyId)) {
        	cart.setBillFromVendorPartyId((String)productStore.get("payToPartyId"));
        	cart.setBillToCustomerPartyId(partyId);
    		cart.setPlacingCustomerPartyId(partyId);
    		cart.setShipToCustomerPartyId(partyId);
        }
        
//        cart.setShipmentMethodTypeId(0,"NO_SHIPPING");
//        cart.setAllShipmentMethodTypeId("NO_SHIPPING");        
//        cart.setShippingContactMechId(0,"POSTAL_ADDRESS");
//        cart.setAllShippingContactMechId("POSTAL_ADDRESS");   
        
        
        if(currencyUom == null){ // (String) context.get("currencyUomId");
			currencyUom = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD", delegator);
		}
        if (UtilValidate.isNotEmpty(currencyUomId)) {
        	currencyUom = currencyUomId;
        }
        try {
			cart.setCurrency(dispatcher, currencyUom);
		} catch (CartItemModifyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        // set the corresponding purchase order id
//        cart.setPoNumber(correspondingPoId);
        
        // set the default ship before and after dates if supplied
        try {
            if (UtilValidate.isNotEmpty(shipInDateStr)) {
                if (shipInDateStr.length() == 10) shipInDateStr += " 00:00:00.000";
                cart.setShipBeforeDate(java.sql.Timestamp.valueOf(shipInDateStr));
            }
            if (UtilValidate.isNotEmpty(createOrderDateStr)) {
                if (createOrderDateStr.length() == 10) createOrderDateStr += " 00:00:00.000";
                cart.setOrderDate(java.sql.Timestamp.valueOf(createOrderDateStr));
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    public static String calcShipping(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute("shoppingCart");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");       
//        CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);
        
        if (UtilValidate.isNotEmpty(cart.getPartyId()) && UtilValidate.isEmpty(cart.getShipToCustomerPartyId())){
       	 	cart.setShipToCustomerPartyId(cart.getPartyId());
        }        
   	
        if (UtilValidate.isEmpty(cart.getCurrency())) {
        	currencyUom = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD", delegator);
        	try {
				cart.setCurrency(dispatcher, currencyUom);
			} catch (CartItemModifyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
//        String shippingContactMechId = request.getParameter("shippingAddressContactMechId");
        
//     // Set the payment options
//        Map<String, Map<String, Object>> selectedPaymentMethods = CheckOutEvents.getSelectedPaymentMethods(request);
//        List<String> singleUsePayments = new ArrayList<String>();
//        String billingAccountId = request.getParameter("billingAccountId");
//        String shippingInstructions = request.getParameter("shipping_instructions");
//        String shippingMethod = request.getParameter("shipping_method");
//        String checkOutPaymentId = request.getParameter("checkOutPaymentId");
//        shippingMethod = "Default";
//        String orderAdditionalEmails = "";
//        String maySplit = "false";
//        String giftMessage = "";
//        String isGift = "";
//        String internalCode = "";
        
//        (String shippingMethod, String shippingContactMechId, Map<String, Map<String, Object>> selectedPaymentMethods,
//                List<String> singleUsePayments, String billingAccountId, String shippingInstructions,
//                String orderAdditionalEmails, String maySplit, String giftMessage, String isGift, String internalCode, String shipBeforeDate, String shipAfterDate)
        
//            checkOutHelper.setCheckOutOptions(shippingMethod, shippingContactMechId, selectedPaymentMethods,
//                singleUsePayments, billingAccountId, shippingInstructions,
//                orderAdditionalEmails, maySplit, giftMessage, isGift, internalCode, cart.getShipBeforeDate().toString(), cart.getShipAfterDate().toString());
//        cart.setShippingContactMechId(0, shippingContactMechId);
//        cart.setShipmentMethodTypeId(0, shippingMethod);
//        CheckOutHelper.setCheckOutPaymentInternal(selectedPaymentMethods, singleUsePayments, billingAccountId);
//        shipToParty = delegator.findOne("Party", [partyId : shoppingCart.getShipToCustomerPartyId()], true);
//        context.shippingContactMechList = ContactHelper.getContactMech(shipToParty, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false);
//        <#if shippingContactMechList?has_content>
//        <tr><td colspan="2"><hr /></td></tr>
//        <#list shippingContactMechList as shippingContactMech>
//          <#assign shippingAddress = shippingContactMech.getRelatedOne("PostalAddress", false)>
//          <tr>
//            <td valign="top" width="1%">
//            	<label>
//					<input type="radio" name="shipping_contact_mech_id" value="${shippingAddress.contactMechId}" onclick="javascript:submitForm(document.checkoutInfoForm, 'SA', null);"<#if shoppingCart.getShippingContactMechId()?default("") == shippingAddress.contactMechId> checked="checked"</#if>/><span class="lbl"></span>
//				</label>
//            </td>
//            <td valign="top" width="99%">
//              <div>
//                <#if shippingAddress.toName?has_content><b>${uiLabelMap.CommonTo}:</b>&nbsp;${shippingAddress.toName}<br /></#if>
//                <#if shippingAddress.attnName?has_content><b>${uiLabelMap.PartyAddrAttnName}:</b>&nbsp;${shippingAddress.attnName}<br /></#if>
//                <#if shippingAddress.address1?has_content>${shippingAddress.address1}<br /></#if>
//                <#if shippingAddress.address2?has_content>${shippingAddress.address2}<br /></#if>
//                <#if shippingAddress.city?has_content>${shippingAddress.city}</#if>
//                <#if shippingAddress.stateProvinceGeoId?has_content><br />${shippingAddress.stateProvinceGeoId}</#if>
//                <#if shippingAddress.postalCode?has_content><br />${shippingAddress.postalCode}</#if>
//                <#if shippingAddress.countryGeoId?has_content><br />${shippingAddress.countryGeoId}</#if>
//                <a href="javascript:submitForm(document.checkoutInfoForm, 'EA', '${shippingAddress.contactMechId}');" class="btn btn-primary btn-mini">${uiLabelMap.CommonUpdate}</a>
//              </div>
//            </td>
//          </tr>
//          <#if shippingContactMech_has_next>
//            <tr><td colspan="2"><hr /></td></tr>
//          </#if>
//        </#list>
//      </#if>
      
      //Set ship to party: shipping_contact_mech_id
//        try {
//			GenericValue shipToParty = delegator.findOne("Party", UtilMisc.toMap("partyId", cart.getShipToCustomerPartyId()), false);
//			List<GenericValue>
//			ContactHelper.getContactMech(shipToParty, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false);
//			
//		} catch (GenericEntityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
        
        
        /*
         * 
         * public static String getShipEstimate(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute("shoppingCart");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        int shipGroups = cart.getShipGroupSize();
        for (int i = 0; i < shipGroups; i++) {
            String shipmentMethodTypeId = cart.getShipmentMethodTypeId(i);
            if (UtilValidate.isEmpty(shipmentMethodTypeId)) {
                continue;
            }
            Map<String, Object> result = getShipGroupEstimate(dispatcher, delegator, cart, i);
            ServiceUtil.getMessages(request, result, null, "", "", "", "", null, null);
            if (result.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR)) {
                return "error";
            }

            BigDecimal shippingTotal = (BigDecimal) result.get("shippingTotal");
            if (shippingTotal == null) {
                shippingTotal = BigDecimal.ZERO;
            }
            cart.setItemShipGroupEstimate(shippingTotal, i);
        }

        ProductPromoWorker.doPromotions(cart, dispatcher);
        // all done
        return "success";
    }
         */
        
        
        
        
        
        return "success";
   }

    /** Event to add an item to the shopping cart. */
    public static String addToCart(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        String controlDirective = null;
        Map<String, Object> result = null;
        String productId = null;
        String parentProductId = null;
        String itemType = null;
        String itemDescription = null;
        String productCategoryId = null;
        String priceStr = null;
        BigDecimal price = null;
        String quantityStr = null;
        BigDecimal quantity = BigDecimal.ZERO;
        String reservStartStr = null;
        String reservEndStr = null;
        Timestamp reservStart = null;
        Timestamp reservEnd = null;
        String reservLengthStr = null;
        BigDecimal reservLength = null;
        String reservPersonsStr = null;
        BigDecimal reservPersons = null;
        String accommodationMapId = null;
        String accommodationSpotId = null;
        String shipBeforeDateStr = null;
        String shipAfterDateStr = null;
        Timestamp shipBeforeDate = null;
        Timestamp shipAfterDate = null;
        String numberOfDay = null;

        // not used right now: Map attributes = null;
        String catalogId = CatalogWorker.getCurrentCatalogId(request);
        Locale locale = UtilHttp.getLocale(request);
       
        // Get the parameters as a MAP, remove the productId and quantity params.
        Map<String, Object> paramMap = UtilHttp.getCombinedMap(request);

        String itemGroupNumber = (String) paramMap.get("itemGroupNumber");
        
        //String orderId = cart.getOrderId();
        
        // Get shoppingList info if passed
        String shoppingListId = (String) paramMap.get("shoppingListId");
        String shoppingListItemSeqId = (String) paramMap.get("shoppingListItemSeqId");
        if (paramMap.containsKey("ADD_PRODUCT_ID")) {
            productId = (String) paramMap.remove("ADD_PRODUCT_ID");
        } else if (paramMap.containsKey("add_product_id")) {
            Object object = paramMap.remove("add_product_id");
            try {
                productId = (String) object;
            } catch (ClassCastException e) {
                List<String> productList = UtilGenerics.checkList(object);
                productId = productList.get(0);
            }
        }
        if (paramMap.containsKey("PRODUCT_ID")) {
            parentProductId = (String) paramMap.remove("PRODUCT_ID");
        } else if (paramMap.containsKey("product_id")) {
            parentProductId = (String) paramMap.remove("product_id");
        }

        Debug.logInfo("adding item product " + productId, module);
        Debug.logInfo("adding item parent product " + parentProductId, module);

        if (paramMap.containsKey("ADD_CATEGORY_ID")) {
            productCategoryId = (String) paramMap.remove("ADD_CATEGORY_ID");
        } else if (paramMap.containsKey("add_category_id")) {
            productCategoryId = (String) paramMap.remove("add_category_id");
        }
        if (productCategoryId != null && productCategoryId.length() == 0) {
            productCategoryId = null;
        }

        if (paramMap.containsKey("ADD_ITEM_TYPE")) {
            itemType = (String) paramMap.remove("ADD_ITEM_TYPE");
        } else if (paramMap.containsKey("add_item_type")) {
            itemType = (String) paramMap.remove("add_item_type");
        }

        if (UtilValidate.isEmpty(productId)) {
            // before returning error; check make sure we aren't adding a special item type
            if (UtilValidate.isEmpty(itemType)) {
                request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.noProductInfoPassed", locale));
                return "success"; // not critical return to same page
            }
        } else {
            try {
                String pId = ProductWorker.findProductId(delegator, productId);
                if (pId != null) {
                    productId = pId;
                }
            } catch (Throwable e) {
                Debug.logWarning(e, module);
            }
        }

        // check for an itemDescription
        if (paramMap.containsKey("ADD_ITEM_DESCRIPTION")) {
            itemDescription = (String) paramMap.remove("ADD_ITEM_DESCRIPTION");
        } else if (paramMap.containsKey("add_item_description")) {
            itemDescription = (String) paramMap.remove("add_item_description");
        }
        if (itemDescription != null && itemDescription.length() == 0) {
            itemDescription = null;
        }

        // Get the ProductConfigWrapper (it's not null only for configurable items)
        ProductConfigWrapper configWrapper = null;
        configWrapper = ProductConfigWorker.getProductConfigWrapper(productId, cart.getCurrency(), request);

        if (configWrapper != null) {
            if (paramMap.containsKey("configId")) {
                try {
                    configWrapper.loadConfig(delegator, (String) paramMap.remove("configId"));
                } catch (Exception e) {
                    Debug.logWarning(e, "Could not load configuration", module);
                }
            } else {
                // The choices selected by the user are taken from request and set in the wrapper
                ProductConfigWorker.fillProductConfigWrapper(configWrapper, request);
            }
            if (!configWrapper.isCompleted()) {
                // The configuration is not valid
                request.setAttribute("product_id", productId);
                request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.configureProductBeforeAddingToCart", locale));
                return "product";
            } else {
                // load the Config Id
                ProductConfigWorker.storeProductConfigWrapper(configWrapper, delegator);
            }
        }


        //Check for virtual products
        if (ProductWorker.isVirtual(delegator, productId)) {

            if ("VV_FEATURETREE".equals(ProductWorker.getProductVirtualVariantMethod(delegator, productId))) {
                // get the selected features.
                List<String> selectedFeatures = new LinkedList<String>();
                Enumeration<String> paramNames = UtilGenerics.cast(request.getParameterNames());
                while (paramNames.hasMoreElements()) {
                    String paramName = paramNames.nextElement();
                    if (paramName.startsWith("FT")) {
                        selectedFeatures.add(request.getParameterValues(paramName)[0]);
                    }
                }

                // check if features are selected
                if (UtilValidate.isEmpty(selectedFeatures)) {
                    request.setAttribute("paramMap", paramMap);
                    request.setAttribute("product_id", productId);
                    request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.chooseVariationBeforeAddingToCart", locale));
                    return "product";
                }

                String variantProductId = ProductWorker.getVariantFromFeatureTree(productId, selectedFeatures, delegator);
                if (UtilValidate.isNotEmpty(variantProductId)) {
                    productId = variantProductId;
                } else {
                    request.setAttribute("paramMap", paramMap);
                    request.setAttribute("product_id", productId);
                    request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.incompatibilityVariantFeature", locale));
                    return "product";
                }

            } else {
                request.setAttribute("paramMap", paramMap);
                request.setAttribute("product_id", productId);
                request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.chooseVariationBeforeAddingToCart", locale));
                return "product";
            }
        }

        // get the override price
        if (paramMap.containsKey("PRICE")) {
            priceStr = (String) paramMap.remove("PRICE");
        } else if (paramMap.containsKey("price")) {
            priceStr = (String) paramMap.remove("price");
        }
        if (priceStr == null) {
            priceStr = "0";  // default price is 0
        }
        
        if ("ASSET_USAGE_OUT_IN".equals(ProductWorker.getProductTypeId(delegator, productId))) {
            if (paramMap.containsKey("numberOfDay")) {
                numberOfDay = (String) paramMap.remove("numberOfDay");
                reservStart = UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(), 1);
                reservEnd = UtilDateTime.addDaysToTimestamp(reservStart, Integer.valueOf(numberOfDay));
            }
        }
        
        // get the renting data
        if ("ASSET_USAGE".equals(ProductWorker.getProductTypeId(delegator, productId)) || "ASSET_USAGE_OUT_IN".equals(ProductWorker.getProductTypeId(delegator, productId))) {
            if (paramMap.containsKey("reservStart")) {
                reservStartStr = (String) paramMap.remove("reservStart");
                if (reservStartStr.length() == 10) // only date provided, no time string?
                    reservStartStr += " 00:00:00.000000000"; // should have format: yyyy-mm-dd hh:mm:ss.fffffffff
                if (reservStartStr.length() > 0) {
                    try {
                        reservStart = java.sql.Timestamp.valueOf(reservStartStr);
                    } catch (Exception e) {
                        Debug.logWarning(e, "Problems parsing Reservation start string: "
                                + reservStartStr, module);
                        reservStart = null;
                        request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.rental.startDate", locale));
                        return "error";
                    }
                } else reservStart = null;
            }

            if (paramMap.containsKey("reservEnd")) {
                reservEndStr = (String) paramMap.remove("reservEnd");
                if (reservEndStr.length() == 10) // only date provided, no time string?
                    reservEndStr += " 00:00:00.000000000"; // should have format: yyyy-mm-dd hh:mm:ss.fffffffff
                if (reservEndStr.length() > 0) {
                    try {
                        reservEnd = java.sql.Timestamp.valueOf(reservEndStr);
                    } catch (Exception e) {
                        Debug.logWarning(e, "Problems parsing Reservation end string: " + reservEndStr, module);
                        reservEnd = null;
                        request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.rental.endDate", locale));
                        return "error";
                    }
                } else reservEnd = null;
            }

            if (reservStart != null && reservEnd != null) {
                reservLength = new BigDecimal(UtilDateTime.getInterval(reservStart, reservEnd)).divide(new BigDecimal("86400000"), generalRounding);
            }

            if (reservStart != null && paramMap.containsKey("reservLength")) {
                reservLengthStr = (String) paramMap.remove("reservLength");
                // parse the reservation Length
                try {
                    reservLength = (BigDecimal) ObjectType.simpleTypeConvert(reservLengthStr, "BigDecimal", null, locale);
                } catch (Exception e) {
                    Debug.logWarning(e, "Problems parsing reservation length string: "
                            + reservLengthStr, module);
                    reservLength = BigDecimal.ONE;
                    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "OrderReservationLengthShouldBeAPositiveNumber", locale));
                    return "error";
                }
            }

            if (reservStart != null && paramMap.containsKey("reservPersons")) {
                reservPersonsStr = (String) paramMap.remove("reservPersons");
                // parse the number of persons
                try {
                    reservPersons = (BigDecimal) ObjectType.simpleTypeConvert(reservPersonsStr, "BigDecimal", null, locale);
                } catch (Exception e) {
                    Debug.logWarning(e, "Problems parsing reservation number of persons string: " + reservPersonsStr, module);
                    reservPersons = BigDecimal.ONE;
                    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "OrderNumberOfPersonsShouldBeOneOrLarger", locale));
                    return "error";
                }
            }

            //check for valid rental parameters
            if (UtilValidate.isEmpty(reservStart) && UtilValidate.isEmpty(reservLength) && UtilValidate.isEmpty(reservPersons)) {
                request.setAttribute("product_id", productId);
                request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.enterBookingInforamtionBeforeAddingToCart", locale));
                return "product";
            }

            //check accommodation for reservations
            if ((paramMap.containsKey("accommodationMapId")) && (paramMap.containsKey("accommodationSpotId"))) {
                accommodationMapId = (String) paramMap.remove("accommodationMapId");
                accommodationSpotId = (String) paramMap.remove("accommodationSpotId");
            }
        }

        // get the quantity
        if (paramMap.containsKey("QUANTITY")) {
            quantityStr = (String) paramMap.remove("QUANTITY");
        } else if (paramMap.containsKey("quantity")) {
            quantityStr = (String) paramMap.remove("quantity");
        }
        if (UtilValidate.isEmpty(quantityStr)) {
            quantityStr = "1";  // default quantity is 1
        }

        // parse the price
        try {
            price = (BigDecimal) ObjectType.simpleTypeConvert(priceStr, "BigDecimal", null, locale);
        } catch (Exception e) {
            Debug.logWarning(e, "Problems parsing price string: " + priceStr, module);
            price = null;
        }

        // parse the quantity
        try {
            quantity = (BigDecimal) ObjectType.simpleTypeConvert(quantityStr, "BigDecimal", null, locale);
            //For quantity we should test if we allow to add decimal quantity for this product an productStore : if not then round to 0
            if(! ProductWorker.isDecimalQuantityOrderAllowed(delegator, productId, cart.getProductStoreId())){
                quantity = quantity.setScale(0, UtilNumber.getBigDecimalRoundingMode("order.rounding"));
            }
            else {
                quantity = quantity.setScale(UtilNumber.getBigDecimalScale("order.decimals"), UtilNumber.getBigDecimalRoundingMode("order.rounding"));
            }
        } catch (Exception e) {
            Debug.logWarning(e, "Problems parsing quantity string: " + quantityStr, module);
            quantity = BigDecimal.ONE;
        }

        // get the selected amount
        String selectedAmountStr = null;
        if (paramMap.containsKey("ADD_AMOUNT")) {
            selectedAmountStr = (String) paramMap.remove("ADD_AMOUNT");
        } else if (paramMap.containsKey("add_amount")) {
            selectedAmountStr = (String) paramMap.remove("add_amount");
        }

        // parse the amount
        BigDecimal amount = null;
        if (UtilValidate.isNotEmpty(selectedAmountStr)) {
            try {
                amount = (BigDecimal) ObjectType.simpleTypeConvert(selectedAmountStr, "BigDecimal", null, locale);
            } catch (Exception e) {
                Debug.logWarning(e, "Problem parsing amount string: " + selectedAmountStr, module);
                amount = null;
            }
        } else {
            amount = BigDecimal.ZERO;
        }

        // check for required amount
        if ((ProductWorker.isAmountRequired(delegator, productId)) && (amount == null || amount.doubleValue() == 0.0)) {
            request.setAttribute("product_id", productId);
            request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.enterAmountBeforeAddingToCart", locale));
            return "product";
        }

        // get the ship before date (handles both yyyy-mm-dd input and full timestamp)
        shipBeforeDateStr = (String) paramMap.remove("shipBeforeDate");
        if (UtilValidate.isNotEmpty(shipBeforeDateStr)) {
            if (shipBeforeDateStr.length() == 10) shipBeforeDateStr += " 00:00:00.000";
            try {
                shipBeforeDate = java.sql.Timestamp.valueOf(shipBeforeDateStr);
            } catch (IllegalArgumentException e) {
                Debug.logWarning(e, "Bad shipBeforeDate input: " + e.getMessage(), module);
                shipBeforeDate = null;
            }
        }

        // get the ship after date (handles both yyyy-mm-dd input and full timestamp)
        shipAfterDateStr = (String) paramMap.remove("shipAfterDate");
        if (UtilValidate.isNotEmpty(shipAfterDateStr)) {
            if (shipAfterDateStr.length() == 10) shipAfterDateStr += " 00:00:00.000";
            try {
                shipAfterDate = java.sql.Timestamp.valueOf(shipAfterDateStr);
            } catch (IllegalArgumentException e) {
                Debug.logWarning(e, "Bad shipAfterDate input: " + e.getMessage(), module);
                shipAfterDate = null;
            }
        }

        // check for an add-to cart survey
        List<String> surveyResponses = null;
        if (productId != null) {
            String productStoreId = ProductStoreWorker.getProductStoreId(request);
            List<GenericValue> productSurvey = ProductStoreWorker.getProductSurveys(delegator, productStoreId, productId, "CART_ADD", parentProductId);
            if (UtilValidate.isNotEmpty(productSurvey)) {
                // TODO: implement multiple survey per product
                GenericValue survey = EntityUtil.getFirst(productSurvey);
                String surveyResponseId = (String) request.getAttribute("surveyResponseId");
                if (surveyResponseId != null) {
                    surveyResponses = UtilMisc.toList(surveyResponseId);
                } else {
                    String origParamMapId = UtilHttp.stashParameterMap(request);
                    Map<String, Object> surveyContext = UtilMisc.<String, Object>toMap("_ORIG_PARAM_MAP_ID_", origParamMapId);
                    GenericValue userLogin = cart.getUserLogin();
                    String partyId = null;
                    if (userLogin != null) {
                        partyId = userLogin.getString("partyId");
                    }
                    String formAction = "/additemsurvey";
                    String nextPage = RequestHandler.getOverrideViewUri(request.getPathInfo());
                    if (nextPage != null) {
                        formAction = formAction + "/" + nextPage;
                    }
                    ProductStoreSurveyWrapper wrapper = new ProductStoreSurveyWrapper(survey, partyId, surveyContext);
                    request.setAttribute("surveyWrapper", wrapper);
                    request.setAttribute("surveyAction", formAction); // will be used as the form action of the survey
                    return "survey";
                }
            }
        }
        if (surveyResponses != null) {
            paramMap.put("surveyResponses", surveyResponses);
        }

        GenericValue productStore = ProductStoreWorker.getProductStore(request);
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
        
        // check for alternative packing
        if(ProductWorker.isAlternativePacking(delegator, productId , parentProductId)){
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
        }

        // Translate the parameters and add to the cart
        result = cartHelper.addToCart(catalogId, shoppingListId, shoppingListItemSeqId, productId, productCategoryId,
                itemType, itemDescription, price, amount, quantity, reservStart, reservLength, reservPersons,
                accommodationMapId, accommodationSpotId,
                shipBeforeDate, shipAfterDate, configWrapper, itemGroupNumber, paramMap, parentProductId);
        controlDirective = processResult(result, request);

        Integer itemId = (Integer)result.get("itemId");
        if (UtilValidate.isNotEmpty(itemId)) {
            request.setAttribute("itemId", itemId);
        }

        // Determine where to send the browser
        if (controlDirective.equals(ERROR)) {
            return "error";
        } else {
            if (cart.viewCartOnAdd()) {
                return "viewcart";
            } else {
                return "success";
            }
        }
    }
    
    private static String processResult(Map<String, Object> result, HttpServletRequest request) {
        //Check for errors
        StringBuilder errMsg = new StringBuilder();
        if (result.containsKey(ModelService.ERROR_MESSAGE_LIST)) {
            List<String> errorMsgs = UtilGenerics.checkList(result.get(ModelService.ERROR_MESSAGE_LIST));
            Iterator<String> iterator = errorMsgs.iterator();
            errMsg.append("<ul>");
            while (iterator.hasNext()) {
                errMsg.append("<li>");
                errMsg.append(iterator.next());
                errMsg.append("</li>");
            }
            errMsg.append("</ul>");
        } else if (result.containsKey(ModelService.ERROR_MESSAGE)) {
            errMsg.append(result.get(ModelService.ERROR_MESSAGE));
            request.setAttribute("_ERROR_MESSAGE_", errMsg.toString());
        }

        //See whether there was an error
        if (errMsg.length() > 0) {
            request.setAttribute("_ERROR_MESSAGE_", errMsg.toString());
            if (result.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_SUCCESS)) {
                return NON_CRITICAL_ERROR;
            } else {
                return ERROR;
            }
        } else {
            return NO_ERROR;
        }
    }
    
/* Method "attachPaymentOrder" - code old
    if (UtilValidate.isNotEmpty(uploadFileName)) {
	String imageFilenameFormat = UtilProperties.getPropertyValue("order", "image.filename.format");
    String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("order", "image.management.path"), context);
    String imageServerUrl = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("order", "image.management.url"), context);
    
    String rootTargetDirectory = imageServerPath;
    File rootTargetDir = new File(rootTargetDirectory);
    if (!rootTargetDir.exists()) {
        boolean created = rootTargetDir.mkdirs();
        if (!created) {
            String errMsg = "Cannot create the target directory";
            Debug.logFatal(errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
    }
    
    String sizeType = null;
    if (UtilValidate.isNotEmpty(imageResize)) {
        sizeType = imageResize;
    }
    
    Map<String, Object> contentCtx = FastMap.newInstance();
    contentCtx.put("contentTypeId", "DOCUMENT");
    contentCtx.put("userLogin", userLogin);
    Map<String, Object> contentResult = FastMap.newInstance();
    try {
        contentResult = dispatcher.runSync("createContent", contentCtx);
    } catch (GenericServiceException e) {
        Debug.logError(e, module);
        return ServiceUtil.returnError(e.getMessage());
    }
    
    String contentId = (String) contentResult.get("contentId");
    //result.put("contentFrameId", contentId);
    //result.put("contentId", contentId);
    
 	// File to use for original image
    FlexibleStringExpander filenameExpander = FlexibleStringExpander.getInstance(imageFilenameFormat);
    String fileLocation = filenameExpander.expandString(UtilMisc.toMap("location", "orders", "type", sizeType, "id", contentId));
    String filenameToUse = fileLocation;
    if (fileLocation.lastIndexOf("/") != -1) {
        filenameToUse = fileLocation.substring(fileLocation.lastIndexOf("/") + 1);
    }
    
    String fileContentType = (String) context.get("_uploadedFile_contentType");
    if (fileContentType.equals("image/pjpeg")) {
        fileContentType = "image/jpeg";
    } else if (fileContentType.equals("image/x-png")) {
        fileContentType = "image/png";
    }
    
    List<GenericValue> fileExtension = FastList.newInstance();
    try {
        fileExtension = delegator.findByAnd("FileExtension", UtilMisc.toMap("mimeTypeId", fileContentType ), null, false);
    } catch (GenericEntityException e) {
        Debug.logError(e, module);
        return ServiceUtil.returnError(e.getMessage());
    }
    
    GenericValue extension = EntityUtil.getFirst(fileExtension);
    if (extension != null) {
        filenameToUse += "." + extension.getString("fileExtensionId");
    }
    
 	// Create folder product id.
    String targetDirectory = imageServerPath + "/" + orderId;
    File targetDir = new File(targetDirectory);
    if (!targetDir.exists()) {
        boolean created = targetDir.mkdirs();
        if (!created) {
            String errMsg = "Cannot create the target directory";
            Debug.logFatal(errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
    }
    
    File file = new File(imageServerPath + "/" + orderId + "/" + uploadFileName);
    String imageName = null;
    imagePath = imageServerPath + "/" + orderId + "/" + uploadFileName;
    file = ImageManagementServices.checkExistsImage(file);
    if (UtilValidate.isNotEmpty(file)) {
        imageName = file.getPath();
        imageName = imageName.substring(imageName.lastIndexOf("/") + 1);
    }
    
    //if (UtilValidate.isEmpty(imageResize)) {
    // Create image file original to folder product id.
    try {
        RandomAccessFile out = new RandomAccessFile(file, "rw");
        out.write(imageData.array());
        out.close();
    } catch (FileNotFoundException e) {
        Debug.logError(e, module);
        return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                "ProductImageViewUnableWriteFile", UtilMisc.toMap("fileName", file.getAbsolutePath()), locale));
    } catch (IOException e) {
        Debug.logError(e, module);
        return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                "ProductImageViewUnableWriteBinaryData", UtilMisc.toMap("fileName", file.getAbsolutePath()), locale));
    }
    //}
    
    //Map<String, Object> contentThumbnail = ImageManagementServices.createContentThumbnail(dctx, context, userLogin, imageData, orderId, imageName);
    //String filenameToUseThumb = (String) contentThumbnail.get("filenameToUseThumb");
    //String contentIdThumb = (String) contentThumbnail.get("contentIdThumb");
    
    imageName = uploadFileName;
    
    String imageUrl = imageServerUrl + "/" + orderId + "/" + imageName;
    //String imageUrlThumb = imageServerUrl + "/" + orderId + "/" + filenameToUseThumb;
    
    ImageManagementServices.createContentAndDataResource(dctx, userLogin, imageName, imageUrl, contentId, fileContentType);
    //ImageManagementServices.createContentAndDataResource(dctx, userLogin, filenameToUseThumb, imageUrlThumb, contentIdThumb, fileContentType);
    
    //Map<String, Object> createContentAssocMap = FastMap.newInstance();
    //createContentAssocMap.put("contentAssocTypeId", "IMAGE_THUMBNAIL");
    //createContentAssocMap.put("contentId", contentId);
    //createContentAssocMap.put("contentIdTo", contentIdThumb);
    //createContentAssocMap.put("userLogin", userLogin);
    //createContentAssocMap.put("mapKey", "100");
    //try {
    //    dispatcher.runSync("createContentAssoc", createContentAssocMap);
    //} catch (GenericServiceException e) {
    //    Debug.logError(e, module);
    //    return ServiceUtil.returnError(e.getMessage());
    //}
    
    Map<String, Object> orderContentCtx = FastMap.newInstance();
    orderContentCtx.put("orderId", orderId);
    orderContentCtx.put("userLogin", userLogin);
    orderContentCtx.put("contentId", contentId);
    orderContentCtx.put("orderContentTypeId", orderContentTypeId);
    try {
        dispatcher.runSync("createOrderContent", orderContentCtx);
    } catch (GenericServiceException e) {
        Debug.logError(e, module);
        return ServiceUtil.returnError(e.getMessage());
    }
    
    Map<String, Object> contentApprovalCtx = FastMap.newInstance();
    contentApprovalCtx.put("contentId", contentId);
    contentApprovalCtx.put("userLogin", userLogin);
    try {
        dispatcher.runSync("createImageContentApproval", contentApprovalCtx);
    } catch (GenericServiceException e) {
        Debug.logError(e, module);
        return ServiceUtil.returnError(e.getMessage());
    }
    
    //String autoApproveImage = UtilProperties.getPropertyValue("catalog", "image.management.autoApproveImage");
    //if (autoApproveImage.equals("Y")) {
    //    Map<String, Object> autoApproveCtx = FastMap.newInstance();
    //    autoApproveCtx.put("contentId", contentId);
    //    autoApproveCtx.put("userLogin", userLogin);
    //    autoApproveCtx.put("checkStatusId", "IM_APPROVED");
    //    try {
    //        dispatcher.runSync("updateStatusImageManagement", autoApproveCtx);
    //    } catch (GenericServiceException e) {
    //        Debug.logError(e, module);
    //        return ServiceUtil.returnError(e.getMessage());
    //    }
    //}
}*/
    
    /**
	 * edit attach Payment order
	 * @param dctx
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> attachPaymentOrder(DispatchContext dctx, Map<String, ? extends Object> context){
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        ByteBuffer imageData = (ByteBuffer) context.get("uploadedFile");
        String uploadFileNameStr = (String) context.get("_uploadedFile_fileName");
		String orderContentTypeId = "IMAGE_URL";
		//String imageResize = (String) context.get("imageResize");
		Locale locale = (Locale) context.get("locale");
		String ntfId = (String) context.get("ntfId");
		if (UtilValidate.isNotEmpty(ntfId)) {
			result.put("ntfId", ntfId);
		}
		if (UtilValidate.isNotEmpty(orderId)) {
			try {
				GenericValue order = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
				if (order == null) {
					return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
	                        "DAErrorCouldNotGetOrderByOrderId" + " = " + orderId, locale) + ").");
				}
				String uploadFileName = orderId.trim() + "_" + UtilDateTime.nowTimestamp().getTime() + "_";
				uploadFileName += uploadFileNameStr.replace("\\s+", "");
				
				if(UtilValidate.isNotEmpty(uploadFileName)) {
					Map<String, Object> contentCtx = FastMap.newInstance();
		            contentCtx.put("contentTypeId", "DOCUMENT");
		            Map<String, Object> contentResult = FastMap.newInstance();
		            GenericValue userLoginTemp = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "distributorsuper"), false);
		            contentCtx.put("userLogin", userLogin);
		            if (userLoginTemp != null) {
		            	try {
			                contentResult = dispatcher.runSync("createContent", contentCtx);
			            } catch (GenericServiceException e) {
			                Debug.logError(e, module);
			                return ServiceUtil.returnError(e.getMessage());
			            }
		            } else {
		            	return ServiceUtil.returnError("You haven't permission to create Payment order!");
		            }
		            String contentId = (String) contentResult.get("contentId");
		            String fileContentType = (String) context.get("_uploadedFile_contentType");
		            //String imageUrl = "";
		            
		            contentCtx.clear();
		            contentCtx.put("userLogin", userLogin);
		            contentCtx.put("uploadedFile", imageData);
		            contentCtx.put("_uploadedFile_fileName", uploadFileName);
		            contentCtx.put("_uploadedFile_contentType", fileContentType);
		            contentCtx.put("public", "Y");
		            contentCtx.put("folder", "/delys/order");
		            
		            try {
		                contentResult = dispatcher.runSync("jackrabbitUploadFile", contentCtx);
		            } catch (GenericServiceException e) {
		                Debug.logError(e, module);
		                return ServiceUtil.returnError(e.getMessage());
		            }
		            ImageManagementServices.createContentAndDataResource(dctx, userLoginTemp, uploadFileName, (String)contentResult.get("path"), contentId, fileContentType);
		            Map<String, Object> orderContentCtx = FastMap.newInstance();
		            orderContentCtx.put("orderId", orderId);
		            orderContentCtx.put("userLogin", userLogin);
		            orderContentCtx.put("contentId", contentId);
		            orderContentCtx.put("orderContentTypeId", orderContentTypeId);
		            orderContentCtx.put("fromDate", UtilDateTime.nowTimestamp());
		            try {
		                dispatcher.runSync("createOrderContent", orderContentCtx);
		            } catch (GenericServiceException e) {
		                Debug.logError(e, module);
		                return ServiceUtil.returnError(e.getMessage());
		            }
		            Map<String, Object> contentApprovalCtx = FastMap.newInstance();
		            contentApprovalCtx.put("contentId", contentId);
		            contentApprovalCtx.put("userLogin", userLogin);
		            try {
		                dispatcher.runSync("createImageContentApproval", contentApprovalCtx);
		            } catch (GenericServiceException e) {
		                Debug.logError(e, module);
		                return ServiceUtil.returnError(e.getMessage());
		            }
				}
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                        "DAErrorCouldNotGetOrderByOrderId" + " = " + orderId, locale) + e.getMessage() + ").");
			}
		}
		
		result.put("orderId", orderId);
		return result;
	}
	
	public static Map<String, Object> removePaymentOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		String orderId = (String) context.get("orderId");
		//String contentId = (String) context.get("contentId");
		String dataResourceId = (String) context.get("dataResourceId");
		String userLoginId = (String) context.get("userLoginId");
		String orderContentTypeId = "IMAGE_URL";
		
		if (UtilValidate.isNotEmpty(userLogin)) {
			try {
				userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
			} catch (GenericEntityException e) {
				String errMsg = "Fatal error userLogin is null: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
			if (userLogin != null) {
				if (UtilValidate.isNotEmpty(orderId) && UtilValidate.isNotEmpty(dataResourceId) && UtilValidate.isNotEmpty(orderContentTypeId)) {
					try {
						GenericValue order = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
						if (order == null) {
							return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
			                        "DAErrorCouldNotGetOrderByOrderId" + " = " + orderId, locale));
						}
						
						List<GenericValue> listContent = delegator.findByAnd("Content", UtilMisc.toMap("dataResourceId", dataResourceId), null, false);
						if (UtilValidate.isNotEmpty(listContent)) {
							for (GenericValue content : listContent) {
								// Delete content approval - image content approval
								List<GenericValue> listContentApproval = delegator.findByAnd("ContentApproval", UtilMisc.toMap("contentId", content.getString("contentId"), "partyId", userLogin.getString("partyId")), null, false);
								if (listContentApproval != null) {
									delegator.removeAll(listContentApproval);
								}
								
								// Delete order content
								Map<String, Object> contentCtx = FastMap.newInstance();
								contentCtx.put("orderId", orderId);
								contentCtx.put("contentId", content.getString("contentId"));
								contentCtx.put("orderContentTypeId", orderContentTypeId);
								contentCtx.put("userLogin", userLogin);
								
								Map<String, Object> resultService = dispatcher.runSync("removeOrderContent", contentCtx);
								if (ServiceUtil.isError(resultService)) {
									return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DADeleteIsFail", locale));
								}
								
								// Delete content
								//GenericValue content = delegator.findOne("Content", UtilMisc.toMap("contentId", contentId), false);
								if (content != null) {
									List<GenericValue> listContentKeyword = delegator.findByAnd("ContentKeyword", UtilMisc.toMap("contentId", content.get("contentId")), null, false);
									if (listContentKeyword != null) delegator.removeAll(listContentKeyword);
									List<GenericValue> listContentRole = delegator.findByAnd("ContentRole", UtilMisc.toMap("contentId", content.get("contentId")), null, false);
									if (listContentRole != null) delegator.removeAll(listContentRole);
									delegator.removeValue(content);
									
									// Delete dataResource
									GenericValue dataResource = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", content.getString("dataResourceId")), false);
									if (dataResource != null) {
										List<GenericValue> listDataResourceRole = delegator.findByAnd("DataResourceRole", UtilMisc.toMap("dataResourceId", content.getString("dataResourceId")), null, false);
										if (listDataResourceRole != null) delegator.removeAll(listDataResourceRole);
										delegator.removeValue(dataResource);
										
										// Delete jcr file
										String path = dataResource.getString("objectInfo");
										try {
											dispatcher.runSync("jackrabbitDeleteNode", UtilMisc.toMap("nodePath", path, "public", "N", "userLogin", userLogin));
										} catch (GenericServiceException e) {
											throw new GenericServiceException(e);
										}
									}
								}
							}
						}
					} catch (GenericEntityException e) {
						String errMsg = "Fatal error calling removePaymentOrder service: " + e.toString();
						Debug.logError(e, errMsg, module);
					} catch (GenericServiceException e) {
						String errMsg = "Fatal error calling removePaymentOrder service (run service internal): " + e.toString();
						Debug.logError(e, errMsg, module);
					}
				} else {
					return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
		                    "DAErrorCouldNotGetContentOfThisImage", locale) + ").");
				}
			}
		}
		
		result.put("orderId", orderId);
		return result;
	}
}