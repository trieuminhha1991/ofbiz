package com.olbius.basesales.shoppingcart;

import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class ShoppingCartEvents {
	public static String module = ShoppingCartEvents.class.getName();
    public static final String resource = "OrderUiLabels";
    public static final String resource_error = "OrderErrorUiLabels";
	
    public static String initializeSalesOrderEntryService(HttpServletRequest request, HttpServletResponse response) {
    	//Delegator delegator = (Delegator) request.getAttribute("delegator");
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
    	//List<String> alertMessageList = FastList.newInstance();
    	Locale locale = UtilHttp.getLocale(request);
        org.ofbiz.order.shoppingcart.ShoppingCartEvents.destroyCart(request, response);
        
        try {
        	String orderMode = "SALES_ORDER";
        	ShoppingCart cart = null; // ShoppingCartEvents.getCartObject(request);
        	
        	Map<String, Object> paramMapCtx = UtilHttp.getCombinedMap(request);
        	if (paramMapCtx == null) paramMapCtx = FastMap.newInstance();
        	paramMapCtx.put("shoppingCart", cart);
        	paramMapCtx.put("userLogin", userLogin);
        	paramMapCtx.put("locale", locale);
        	paramMapCtx.put("salesChannelEnumId", "WEB_SALES_CHANNEL");
        	
        	String[] checkOutPaymentIdArr = request.getParameterValues("checkOutPaymentId");
        	List<String> checkOutPaymentId = Arrays.asList(checkOutPaymentIdArr);
        	paramMapCtx.put("checkOutPaymentId", checkOutPaymentId);
        	
        	// currency uom
        	String iso = UtilHttp.getCurrencyUom(session, null);
        	paramMapCtx.put("currencyUom", iso);
        	
        	Map<String, Object> shoppingCartCtx = ServiceUtil.setServiceFields(dispatcher, "initializeSalesOrderEntry", paramMapCtx, userLogin, null, locale);
        	
        	Map<String, Object> resultValue = dispatcher.runSync("initializeSalesOrderEntry", shoppingCartCtx);
        	if (ServiceUtil.isError(resultValue)) {
        		request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(resultValue));
    			org.ofbiz.order.shoppingcart.ShoppingCartEvents.destroyCart(request, response);
    			session.removeAttribute("orderMode");
    			return "error";
        	}
        	cart = (ShoppingCart) resultValue.get("shoppingCart");
            session.setAttribute("shoppingCart", cart);
            
            if (cart != null) {
            	session.setAttribute("orderMode", orderMode);
            	session.setAttribute("productStoreId", cart.getProductStoreId());
            } else {
            	session.removeAttribute("orderMode");
            }
            
            if (UtilValidate.isNotEmpty(request.getParameter("issuerId"))) {
                request.setAttribute("issuerId", request.getParameter("issuerId"));
            }
            
        } catch (Exception e) {
			String errMsg = "Fatal error calling initializeSalesOrderEntryService service: " + e.toString();
			Debug.logError(e, errMsg, module);
			
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			org.ofbiz.order.shoppingcart.ShoppingCartEvents.destroyCart(request, response);
			session.removeAttribute("orderMode");
			return "error";
		}
        
        return "success";
    }
    
    /** Update the items in the shopping cart. */
    public static String modifyCart(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        Locale locale = UtilHttp.getLocale(request);
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

        try {
        	ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");
        	if (cart == null) {
        		request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSCartIsNull", locale));
				return "error";
        	}
    	
	    	Map<String, Object> paramMapCtx = UtilHttp.getCombinedMap(request);
	    	if (paramMapCtx == null) paramMapCtx = FastMap.newInstance();
	    	paramMapCtx.put("shoppingCart", cart);
	    	paramMapCtx.put("userLogin", userLogin);
	    	paramMapCtx.put("locale", locale);
	    	
	    	String selectedItemsArr[] = request.getParameterValues("selectedItem");
	    	List<String> selectedItems = null;
	    	if (selectedItemsArr != null) Arrays.asList(selectedItemsArr);
	    	paramMapCtx.put("selectedItems", selectedItems);
	    	String removeSelectedFlag = request.getParameter("removeSelected");
	        boolean removeSelected = ("true".equals(removeSelectedFlag) && UtilValidate.isNotEmpty(selectedItems));
	    	paramMapCtx.put("removeSelected", removeSelected);
	    	paramMapCtx.put("listProd", request.getParameter("products"));
	        
	    	// currency UOM
	    	String iso = UtilHttp.getCurrencyUom(session, null);
	    	paramMapCtx.put("currencyUom", iso);
	    	
	    	paramMapCtx.put("shipping_contact_mech_id", session.getAttribute("shipping_contact_mech_id"));
	    	paramMapCtx.put("shipping_method", session.getAttribute("shipping_method"));
	    	paramMapCtx.put("checkOutPaymentId", session.getAttribute("checkOutPaymentId"));
	    	paramMapCtx.put("may_split", session.getAttribute("may_split"));
	    	paramMapCtx.put("is_gift", session.getAttribute("is_gift"));
	    	
	    	Map<String, Object> shoppingCartCtx = ServiceUtil.setServiceFields(dispatcher, "modifyCart", paramMapCtx, userLogin, null, locale);
	    	
	    	boolean removeItemOut = true;
	    	String removeItemOutStr = request.getParameter("removeItemOut");
	    	if ("N".equals(removeItemOutStr)) removeItemOut = false;
	    	shoppingCartCtx.put("removeItemOut", removeItemOut);
	    	
	    	Map<String, Object> resultValue = dispatcher.runSync("modifyCart", shoppingCartCtx);
	    	if (ServiceUtil.isError(resultValue)) {
	    		request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(resultValue));
				return "error";
	    	}
	    } catch (Exception e) {
			String errMsg = "Fatal error calling modifyCart event: " + e.toString();
			Debug.logError(e, errMsg, module);
			
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}
	    
	    return "success";
    }
    
	/** Gets or creates the shopping cart object */
    public static ShoppingCart getCartUpdateObject(HttpServletRequest request, Locale locale, String currencyUom) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = (ShoppingCart) request.getAttribute("shoppingCartUpdate");
        HttpSession session = request.getSession(true);
        if (cart == null) {
            cart = (ShoppingCart) session.getAttribute("shoppingCartUpdate");
        } else {
            session.setAttribute("shoppingCartUpdate", cart);
        }

        if (cart != null) {
            if (locale != null && !locale.equals(cart.getLocale())) {
                cart.setLocale(locale);
            }
            if (currencyUom != null && !currencyUom.equals(cart.getCurrency())) {
                try {
                    cart.setCurrency(dispatcher, currencyUom);
                } catch (CartItemModifyException e) {
                    Debug.logError(e, "Unable to modify currency in cart", module);
                }
            }
        }
        return cart;
    }

    /** Main get cart method; uses the locale & currency from the session */
    public static ShoppingCart getCartUpdateObject(HttpServletRequest request) {
        return getCartUpdateObject(request, null, null);
    }
    
    public static void saveCartUpdateObject(HttpServletRequest request, ShoppingCart cart) {
    	HttpSession session = request.getSession(true);
    	session.setAttribute("shoppingCartUpdate", cart);
    	request.setAttribute("shoppingCartUpdate", cart);
    }
    
    /** Empty the shopping cart. */
    public static String clearCartUpdate(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = getCartUpdateObject(request);
        cart.clear();

        // if this was an anonymous checkout process, go ahead and clear the session and such now that the order is placed; we don't want this to mess up additional orders and such
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        if (userLogin != null && "anonymous".equals(userLogin.get("userLoginId"))) {
            // here we want to do a full logout, but not using the normal logout stuff because it saves things in the UserLogin record that we don't want changed for the anonymous user
            session.invalidate();
            session = request.getSession(true);

            // to allow the display of the order confirmation page put the userLogin in the request, but leave it out of the session
            request.setAttribute("temporaryAnonymousUserLogin", userLogin);

            Debug.logInfo("Doing clearCart for anonymous user, so logging out but put anonymous userLogin in temporaryAnonymousUserLogin request attribute", module);
        }

        return "success";
    }

    /** Totally wipe out the cart, removes all stored info. */
    public static String destroyCartUpdate(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        clearCartUpdate(request, response);
        session.removeAttribute("shoppingCartUpdate");
        return "success";
    }
    
    /**
     * Gets or creates the shopping cart object 
     * @param delegator
     * @param dispatcher
     * @param cart
     * @param locale
     * @param currencyUom
     * @param productStoreId
     * @param webSiteId
     * @param billToCustomerPartyId
     * @param supplierPartyId
     * @param billFromVendorPartyId
     * @param userLogin
     * @param autoUserLogin
     * @param orderPartyId
     * @return
     * @throws GenericEntityException
     * @throws CartItemModifyException 
     */
    public static ShoppingCart getCartObject(Delegator delegator, LocalDispatcher dispatcher, ShoppingCart cart, Locale locale, String currencyUom, 
    		GenericValue productStore, String webSiteId, String billToCustomerPartyId, String supplierPartyId, String billFromVendorPartyId, 
    		GenericValue userLogin, GenericValue autoUserLogin, String orderPartyId) throws GenericEntityException, CartItemModifyException {
        if (cart == null) {
            cart = createWebShoppingCart(delegator, dispatcher, locale, currencyUom, productStore, webSiteId, billToCustomerPartyId, supplierPartyId, billFromVendorPartyId, userLogin, autoUserLogin, orderPartyId);
        } else {
            if (locale != null && !locale.equals(cart.getLocale())) {
                cart.setLocale(locale);
            }
            if (currencyUom != null && !currencyUom.equals(cart.getCurrency())) {
                try {
                    cart.setCurrency(dispatcher, currencyUom);
                } catch (CartItemModifyException e) {
                    Debug.logError(e, "Unable to modify currency in cart", module);
                }
            }
        }
        return cart;
    }
    
    private static ShoppingCart createWebShoppingCart(Delegator delegator, LocalDispatcher dispatcher, Locale locale, String currencyUom, GenericValue productStore, String webSiteId, 
    		String billToCustomerPartyId, String supplierPartyId, String billFromVendorPartyId, GenericValue userLogin, GenericValue autoUserLogin, String orderPartyId) throws CartItemModifyException {
        // for purchase orders, bill to customer partyId must be set - otherwise, no way to know who we're purchasing for.  supplierPartyId is furnished
        // by order manager for PO entry.
        // TODO: refactor constructor and the getCartObject method which calls them to multiple constructors for different types of orders
    	
    	// productStoreId = ProductStoreWorker.getProductStoreId(request);
    	// webSiteId = WebSiteWorker.getWebSiteId(request);
    	// locale = locale != null ? locale : ProductStoreWorker.getStoreLocale(request);
    	// currencyUom = currencyUom != null ? currencyUom : ProductStoreWorker.getStoreCurrencyUomId(request);
    	// billToCustomerPartyId = request.getParameter("billToCustomerPartyId");
    	// supplierPartyId = request.getParameter("supplierPartyId");
    	// billFromVendorPartyId = supplierPartyId != null ? supplierPartyId : request.getParameter("billFromVendorPartyId");
    	// userLogin = (GenericValue) session.getAttribute("userLogin");
    	// autoUserLogin = (GenericValue) session.getAttribute("autoUserLogin");
    	// orderPartyId = (String) session.getAttribute("orderPartyId");
    	
    	Locale locale2 = locale != null ? locale : getStoreLocale(productStore);
    	String currencyUom2 = currencyUom != null ? currencyUom : getStoreCurrencyUomId(currencyUom, userLogin, autoUserLogin, productStore, locale);
    	String billFromVendorPartyId2 = supplierPartyId != null ? supplierPartyId : billFromVendorPartyId;
    	
        ShoppingCart cart = new ShoppingCart(delegator, productStore.getString("productStoreId"), webSiteId, locale2, currencyUom2, billToCustomerPartyId, billFromVendorPartyId2);

        if (cart != null) {
        	cart.setUserLogin(userLogin, dispatcher);
        	cart.setAutoUserLogin(autoUserLogin);
        	cart.setOrderPartyId(orderPartyId);
        }
        
        return cart;
    }
    
    private static Locale getStoreLocale(GenericValue productStore) {
        if (UtilValidate.isEmpty(productStore)) {
            Debug.logWarning("No product store found in request, cannot set locale!", module);
            return null;
        } else {
            return UtilHttp.getLocale(null, null, productStore.getString("defaultLocaleString"));
        }
    }
    
    private static String getStoreCurrencyUomId(String currencyUom, GenericValue paramUserLogin, GenericValue paramAutoUserLogin, GenericValue productStore, Locale locale) {
        if (UtilValidate.isEmpty(productStore)) {
            Debug.logWarning("No product store found in request, cannot set CurrencyUomId!", module);
            return null;
        } else {
            //return UtilHttp.getCurrencyUom(session, productStore.getString("defaultCurrencyUomId"));
        	
        	String appDefaultCurrencyUom = productStore.getString("defaultCurrencyUomId");
        	// session, should override all if set there
            String iso = currencyUom;

            // check userLogin next, ie if nothing to override in the session
            if (iso == null) {
                Map<String, ?> userLogin = UtilGenerics.cast(paramUserLogin);
                if (userLogin == null) {
                    userLogin = UtilGenerics.cast(paramAutoUserLogin);
                }

                if (userLogin != null) {
                    iso = (String) userLogin.get("lastCurrencyUom");
                }
            }

            // no user currency? before global default try appDefaultCurrencyUom if specified
            if (iso == null && !UtilValidate.isEmpty(appDefaultCurrencyUom)) {
                iso = appDefaultCurrencyUom;
            }

            // if none is set we will use the configured default
            if (iso == null) {
                try {
                    iso = UtilProperties.getPropertyValue("general", "currency.uom.id.default", "USD");
                } catch (Exception e) {
                    Debug.logWarning("Error getting the general:currency.uom.id.default value: " + e.toString(), module);
                }
            }


            // if still none we will use the default for whatever locale we can get...
            if (iso == null) {
                Currency cur = Currency.getInstance(locale);
                iso = cur.getCurrencyCode();
            }

            return iso;
        }
    }
}
