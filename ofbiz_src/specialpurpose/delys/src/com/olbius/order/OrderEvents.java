package com.olbius.order;

import java.math.BigDecimal;
import java.math.MathContext;
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
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;

import com.olbius.order.ShoppingCartHelper;

import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.order.shoppingcart.WebShoppingCart;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.config.ProductConfigWorker;
import org.ofbiz.product.config.ProductConfigWrapper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.store.ProductStoreSurveyWrapper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.control.RequestHandler;

public class OrderEvents {
	public static String module = ShoppingCartEvents.class.getName();
    public static final String resource = "OrderUiLabels";
    public static final String resource_delys = "DelysAdminUiLabels";
    public static final String resource_error = "OrderErrorUiLabels";

    private static final String NO_ERROR = "noerror";
    private static final String NON_CRITICAL_ERROR = "noncritical";
    private static final String ERROR = "error";

    public static final MathContext generalRounding = new MathContext(10);
    
    public enum OrderCategory {
    	ORDER_COMPANY_SALES, ORDER_DISTRIBUTOR_SALES
    }
	
    /** Route order entry **/
    public static String routeOrderEntry(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        // if the order mode is not set in the attributes, then order entry has not been initialized
        if (session.getAttribute("orderMode") == null) {
            return "init";
        }

        // if the request is coming from the init page, then orderMode will be in the request parameters
//        if (request.getParameter("orderMode") != null) {
//            return "agreements"; // next page after init is always agreements
//        }

        // orderMode is set and there is an order in progress, so go straight to the cart
        return "cart";
    }
    
	/** Initialize order entry **/
    public static String initializeOrderEntry(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        Security security = (Security) request.getAttribute("security");
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);

        String productStoreId = request.getParameter("productStoreId");
        
        if (UtilValidate.isNotEmpty(productStoreId)) {
            session.setAttribute("productStoreId", productStoreId);
        }
        List<Object> errMsgList = FastList.newInstance();
        if (UtilValidate.isEmpty(request.getParameter("partyId"))) {
        	errMsgList.add(UtilProperties.getMessage(resource_delys,"DACustomerMustNotBeEmpty", locale));
        }
        if (UtilValidate.isEmpty(request.getParameter("desiredDeliveryDate"))) {
        	errMsgList.add(UtilProperties.getMessage(resource_delys,"DADesiredDeliveryDateMustNotBeEmpty", locale));
        }
        if (UtilValidate.isNotEmpty(errMsgList)) {
        	request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
        	return "error";
        }
        ShoppingCart cart = getCartObject(request);

        // TODO: re-factor and move this inside the ShoppingCart constructor
        String orderMode = "SALES_ORDER"; //fix order type = "sales order"
        cart.setOrderType(orderMode);
        session.setAttribute("orderMode", orderMode);

        // check the selected product store
        GenericValue productStore = null;
        if (UtilValidate.isNotEmpty(productStoreId)) {
            productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
            if (productStore != null) {
                // check permission for taking the order
                boolean hasPermission = false;
                if (cart.getOrderType().equals("SALES_ORDER")) {
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

        if ("SALES_ORDER".equals(cart.getOrderType()) && UtilValidate.isEmpty(cart.getProductStoreId())) {
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderAProductStoreMustBeSelectedForASalesOrder", locale));
            cart.clear();
            session.removeAttribute("orderMode");
            return "error";
        }

        String salesChannelEnumId = "WEB_SALES_CHANNEL"; //fix request.getParameter("salesChannelEnumId");
        cart.setChannelType(salesChannelEnumId);

        // set party info
        String partyId = "";
        String originOrderId = request.getParameter("originOrderId");
        cart.setAttribute("originOrderId", originOrderId);
        
        // customer = request.getParameter("partyId")
        if (!UtilValidate.isEmpty(request.getParameter("partyId"))) {
            partyId = request.getParameter("partyId");
        }
        /*
        if (!UtilValidate.isEmpty(request.getParameter("partyId"))) {
            partyId = request.getParameter("partyId");
        } else {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_delys,"DACustomerMustNotBeEmpty", locale));
            return "error";
        }
         */
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
        
        
        // After complete step 1 (per 3 step) -----------------------------------------------
        // get applicable agreements for order entry
        // for a sales order, orderPartyId = billToCustomer (the customer)
        /*
        String customerPartyId = cart.getOrderPartyId();
        String companyPartyId = cart.getBillFromVendorPartyId();
        */
        
        // the agreement for a sales order is from the customer to us
        /*
        EntityCondition agreementCondition = EntityCondition.makeCondition(
        		UtilMisc.toList(
    				EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, companyPartyId), 
    				EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, customerPartyId)
				), EntityOperator.AND);

        EntityCondition agreementRoleCondition = EntityCondition.makeCondition( 
        		UtilMisc.toList(
        				EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, customerPartyId),
        				EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CUSTOMER")
        		), EntityOperator.AND);
        		
        Map<String, Object> context = UtilHttp.getParameterMap(request);
        List<GenericValue> agreements = null;
		try {
			agreements = delegator.findList("Agreement", agreementCondition, null, null, null, true);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        agreements = EntityUtil.filterByDate(agreements);
        if (agreements != null) {
            context.put("agreements", agreements);
        }

        List<GenericValue> agreementRoles = null;
		try {
			agreementRoles = delegator.findList("AgreementRole", agreementRoleCondition, null, null, null, true);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (agreementRoles != null) {
            context.put("agreementRoles", agreementRoles);
        }
        */

        // catalog id collection, current catalog id and name
        productStoreId = cart.getProductStoreId();
        List<String> catalogCol = null;
        if ("SALES_ORDER" == cart.getOrderType() && productStoreId != null) {
        	catalogCol = CatalogWorker.getCatalogIdsAvailable(delegator, productStoreId, cart.getOrderPartyId());
        } else {
        	catalogCol = CatalogWorker.getAllCatalogIds(request);
        }

        if (catalogCol != null) {
            String currentCatalogId = catalogCol.get(0);
            // String currentCatalogName = CatalogWorker.getCatalogName(request, currentCatalogId);
            session.setAttribute("CURRENT_CATALOG_ID", currentCatalogId);
        }

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        String currencyUomId = request.getParameter("currencyUomId");
        String shipBeforeDateStr = request.getParameter("shipBeforeDate");
        String shipAfterDateStr = request.getParameter("shipAfterDate");
        String orderId = request.getParameter("orderId");
        String orderName = request.getParameter("orderName");
        String correspondingPoId = request.getParameter("correspondingPoId");
        /*
		String agreementId = request.getParameter("agreementId");
      	String workEffortId = request.getParameter("workEffortId");
      	String cancelBackOrderDateStr = request.getParameter("cancelBackOrderDate"); ---- for purchase order
      	Locale locale = UtilHttp.getLocale(request);
         */

        Map<String, Object> result = null;
		
        // set the agreement if specified otherwise set the currency
        /*
        if (UtilValidate.isNotEmpty(agreementId)) {
            result = cartHelper.selectAgreement(agreementId);
        } 
         */
        if (UtilValidate.isNotEmpty(cart.getCurrency()) && UtilValidate.isNotEmpty(currencyUomId)) {
            result = cartHelper.setCurrency(currencyUomId);
        }
        if (ServiceUtil.isError(result)) {
            request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(result));
            return "error";
        }

        // set the work effort id - cart.setWorkEffortId(workEffortId);

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

        // set the corresponding purchase order id
        cart.setPoNumber(correspondingPoId);

        // set the default ship before and after dates if supplied
        try {
            if (UtilValidate.isNotEmpty(shipBeforeDateStr)) {
                if (shipBeforeDateStr.length() == 10) shipBeforeDateStr += " 00:00:00.000";
                cart.setDefaultShipBeforeDate(java.sql.Timestamp.valueOf(shipBeforeDateStr));
            }
            if (UtilValidate.isNotEmpty(shipAfterDateStr)) {
                if (shipAfterDateStr.length() == 10) shipAfterDateStr += " 00:00:00.000";
                cart.setDefaultShipAfterDate(java.sql.Timestamp.valueOf(shipAfterDateStr));
            }
            /* ---- for purchase order
            if (UtilValidate.isNotEmpty(cancelBackOrderDateStr)) {
                if (cancelBackOrderDateStr.length() == 10) cancelBackOrderDateStr += " 00:00:00.000";
                cart.setCancelBackOrderDate(java.sql.Timestamp.valueOf(cancelBackOrderDateStr));
            }
             */
        } catch (IllegalArgumentException e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        String desiredDeliveryDate = request.getParameter("desiredDeliveryDate");
        cart.setDefaultItemDeliveryDate(desiredDeliveryDate);
        
        /* New method 25/11/2014
         * Find and add agreement into Shopping cart
         */
        // get applicable agreements for order entry
        EntityCondition agreementCondition = null;
        /*EntityCondition agreementRoleCondition = null;*/
        String agreementId = null;
        List<GenericValue> agreements = null;
        Map<String, Object> result2 = null;
        try {
	        if ("PURCHASE_ORDER".equals(cart.getOrderType())) {
	        	// for a purchase order, orderPartyId = billFromVendor (the supplier)
	        	String supplierPartyId = cart.getOrderPartyId();
	        	String customerPartyId = cart.getBillToCustomerPartyId();
	        	
	        	// the agreement for a purchse order is from us to the supplier
	        	agreementCondition = EntityCondition.makeCondition(
	        			UtilMisc.toList(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, supplierPartyId), 
						EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, customerPartyId)), EntityOperator.AND);
	
	        	/*agreementRoleCondition = EntityCondition.makeCondition(
	                    UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, supplierPartyId),
	            		EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SUPPLIER")), EntityOperator.AND);*/
	        } else {
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
	        	
	        	/*agreementRoleCondition = EntityCondition.makeCondition(
	        			UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, customerPartyId),
	                    EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CUSTOMER")), EntityOperator.AND);*/
	        }
	        
	        agreements = delegator.findList("Agreement", agreementCondition, null, null, null, true);
	        agreements = EntityUtil.filterByDate(agreements);
	        // List<GenericValue> agreementRoles = delegator.findList("AgreementRole", agreementRoleCondition, null, null, null, true);
        } catch (GenericEntityException e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        if (agreements != null) {
        	for (GenericValue agreementItem : agreements) {
        		if (agreementItem.containsKey("isAuto") && "Y".equals(agreementItem.getString("isAuto"))) {
        			agreementId = agreementItem.getString("agreementId");
        		}
        	}
        }
        
        if (UtilValidate.isNotEmpty(agreementId)) {
        	// set the agreement if specified otherwise set the currency
            if (UtilValidate.isNotEmpty(agreementId)) {
                result2 = cartHelper.selectAgreement(agreementId);
            }
            if (ServiceUtil.isError(result2)) {
                request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(result2));
                return "error";
            }
        }
        
        return "success";
    }
    
    /** Event to add an item to the shopping cart. */
    public static String addToCart(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = getCartObject(request);
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
        
        if (UtilValidate.isNotEmpty(cart.getDefaultItemDeliveryDate())) {
        	paramMap.put("itemDesiredDeliveryDate", cart.getDefaultItemDeliveryDate());
        	paramMap.put("useAsDefaultDesiredDeliveryDate", "true");
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
    
    public static String BulkAddProducts(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        String controlDirective = null;
        Map<String, Object> result = null;
        String productId = null;
        String productCategoryId = null;
        String quantityStr = null;
        String itemDesiredDeliveryDateStr = null;
        BigDecimal quantity = BigDecimal.ZERO;
        String catalogId = CatalogWorker.getCurrentCatalogId(request);
        String itemType = null;
        String itemDescription = "";

        // Get the parameters as a MAP, remove the productId and quantity params.
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);

        String itemGroupNumber = request.getParameter("itemGroupNumber");

        // Get shoppingList info if passed.  I think there can only be one shoppingList per request
        String shoppingListId = request.getParameter("shoppingListId");
        String shoppingListItemSeqId = request.getParameter("shoppingListItemSeqId");

        // The number of multi form rows is retrieved
        int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
        if (rowCount < 1) {
            Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
        } else {
            for (int i = 0; i < rowCount; i++) {
                controlDirective = null;                // re-initialize each time
                String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;        // current suffix after each field id

                // get the productId
                if (paramMap.containsKey("productId" + thisSuffix)) {
                    productId = (String) paramMap.remove("productId" + thisSuffix);
                }

                if (paramMap.containsKey("quantity" + thisSuffix)) {
                    quantityStr = (String) paramMap.remove("quantity" + thisSuffix);
                }
                if ((quantityStr == null) || (quantityStr.equals(""))) {    // otherwise, every empty value causes an exception and makes the log ugly
                    quantityStr = "0";  // default quantity is 0, so without a quantity input, this field will not be added
                }

                // parse the quantity
                try {
                    quantity = new BigDecimal(quantityStr);
                } catch (Exception e) {
                    Debug.logWarning(e, "Problems parsing quantity string: " + quantityStr, module);
                    quantity = BigDecimal.ZERO;
                }

                // get the selected amount
                String selectedAmountStr = null;
                if (paramMap.containsKey("amount" + thisSuffix)) {
                    selectedAmountStr = (String) paramMap.remove("amount" + thisSuffix);
                }

                // parse the amount
                BigDecimal amount = null;
                if (UtilValidate.isNotEmpty(selectedAmountStr)) {
                    try {
                        amount = new BigDecimal(selectedAmountStr);
                    } catch (Exception e) {
                        Debug.logWarning(e, "Problem parsing amount string: " + selectedAmountStr, module);
                        amount = null;
                    }
                } else {
                    amount = BigDecimal.ZERO;
                }

                if (paramMap.containsKey("itemDesiredDeliveryDate" + thisSuffix)) {
                    itemDesiredDeliveryDateStr = (String) paramMap.remove("itemDesiredDeliveryDate" + thisSuffix);
                }
                // get the item type
                if (paramMap.containsKey("itemType" + thisSuffix)) {
                    itemType = (String) paramMap.remove("itemType" + thisSuffix);
                }

                if (paramMap.containsKey("itemDescription" + thisSuffix)) {
                    itemDescription = (String) paramMap.remove("itemDescription" + thisSuffix);
                }

                Map<String, Object> itemAttributes = UtilMisc.<String, Object>toMap("itemDesiredDeliveryDate", itemDesiredDeliveryDateStr);

                if (quantity.compareTo(BigDecimal.ZERO) > 0) {
                    Debug.logInfo("Attempting to add to cart with productId = " + productId + ", categoryId = " + productCategoryId +
                            ", quantity = " + quantity + ", itemType = " + itemType + " and itemDescription = " + itemDescription, module);
                    if (UtilValidate.isNotEmpty(cart.getDefaultItemDeliveryDate())) {
                    	itemAttributes.put("itemDesiredDeliveryDate", cart.getDefaultItemDeliveryDate());
                    	itemAttributes.put("useAsDefaultDesiredDeliveryDate", "true");
                    }
                    result = cartHelper.addToCart(catalogId, shoppingListId, shoppingListItemSeqId, productId,
                                                  productCategoryId, itemType, itemDescription, null,
                                                  amount, quantity, null, null, null, null, null, null,
                                                  itemGroupNumber, itemAttributes,null);
                    // no values for price and paramMap (a context for adding attributes)
                    controlDirective = processResult(result, request);
                    if (controlDirective.equals(ERROR)) {    // if the add to cart failed, then get out of this loop right away
                        return "error";
                    }
                }
            }
        }

        // Determine where to send the browser
        return cart.viewCartOnAdd() ? "viewcart" : "success";
    }
 
    /** Gets or creates the shopping cart object */
    public static ShoppingCart getCartObject(HttpServletRequest request, Locale locale, String currencyUom) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = (ShoppingCart) request.getAttribute("shoppingCart");
        HttpSession session = request.getSession(true);
        if (cart == null) {
            cart = (ShoppingCart) session.getAttribute("shoppingCart");
        } else {
            session.setAttribute("shoppingCart", cart);
        }

        if (cart == null) {
            cart = new WebShoppingCart(request, locale, currencyUom);
            session.setAttribute("shoppingCart", cart);
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

    /** Main get cart method; uses the locale & currency from the session */
    public static ShoppingCart getCartObject(HttpServletRequest request) {
        return getCartObject(request, null, null);
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
    
    private static String processResultCustom(Map<String, Object> result, HttpServletRequest request) {
        //Check for errors
    	
        //See whether there was an error
        if (result.containsKey(ModelService.ERROR_MESSAGE_LIST) || result.containsKey(ModelService.ERROR_MESSAGE)) {
        	if (result.containsKey(ModelService.ERROR_MESSAGE_LIST)) {
        		List<String> errorMsgs = UtilGenerics.checkList(result.get(ModelService.ERROR_MESSAGE_LIST));
        		request.setAttribute("_ERROR_MESSAGE_LIST_", errorMsgs);
        	} else if (result.containsKey(ModelService.ERROR_MESSAGE)) {
        		request.setAttribute("_ERROR_MESSAGE_", result.get(ModelService.ERROR_MESSAGE));
        	}
            if (result.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_SUCCESS)) {
                return NON_CRITICAL_ERROR;
            } else {
                return ERROR;
            }
        } else {
            return NO_ERROR;
        }
    }
    
    private static void processResultAddMessage(Map<String, Object> result, HttpServletRequest request) {
        //Check for errors
        if (result.containsKey(ModelService.ERROR_MESSAGE)) {
        	if (result.get(ModelService.ERROR_MESSAGE) != null) {
        		List<String> errorMsgs = new ArrayList<String>();
        		if (result.containsKey(ModelService.ERROR_MESSAGE_LIST)) {
        			errorMsgs = UtilGenerics.checkList(result.get(ModelService.ERROR_MESSAGE_LIST));
        			errorMsgs.add((String) result.get(ModelService.ERROR_MESSAGE));
        			result.remove(ModelService.ERROR_MESSAGE);
        			result.put(ModelService.ERROR_MESSAGE_LIST, errorMsgs);
        		} else {
        			errorMsgs.add((String) result.get(ModelService.ERROR_MESSAGE));
        			result.remove(ModelService.ERROR_MESSAGE);
        			result.put(ModelService.ERROR_MESSAGE_LIST, errorMsgs);
        		}
        	}
        }
    }
    
    // extends from bulkAddProducs method
    public static String BulkAddOrderItems(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);
        String controlDirective = null;
        Map<String, Object> result = FastMap.newInstance();
        String productId = null;
        String productCategoryId = null;
        String quantityStr = null;
        String itemDesiredDeliveryDateStr = null;
        BigDecimal quantity = BigDecimal.ZERO;
        String itemType = null;
        String itemDescription = "";
        String selectedItemStr = "";
        boolean selectedItem = false;
        String orderId = "";
        String orderItemSeqId = "";
        
        // Get the parameters as a MAP, remove the productId and quantity params.
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);

        // String itemGroupNumber = request.getParameter("itemGroupNumber");

        // Get shoppingList info if passed.  I think there can only be one shoppingList per request
        /* String shoppingListId = request.getParameter("shoppingListId");
        String shoppingListItemSeqId = request.getParameter("shoppingListItemSeqId"); */

        // The number of multi form rows is retrieved
        int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
        if (rowCount < 1) {
            Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
        } else {
            for (int i = 0; i < rowCount; i++) {
            	orderId = "";
            	orderItemSeqId = "";
            	selectedItem = false;
                controlDirective = null;                // re-initialize each time
                String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;        // current suffix after each field id

                // get the productId
                if (paramMap.containsKey("productId" + thisSuffix)) {
                    productId = (String) paramMap.remove("productId" + thisSuffix);
                }

                if (paramMap.containsKey("quantity" + thisSuffix)) {
                    quantityStr = (String) paramMap.remove("quantity" + thisSuffix);
                }
                if ((quantityStr == null) || (quantityStr.equals(""))) {    // otherwise, every empty value causes an exception and makes the log ugly
                    quantityStr = "0";  // default quantity is 0, so without a quantity input, this field will not be added
                }

                // parse the quantity
                try {
                    quantity = new BigDecimal(quantityStr);
                } catch (Exception e) {
                    Debug.logWarning(e, "Problems parsing quantity string: " + quantityStr, module);
                    quantity = BigDecimal.ZERO;
                }

                // get the selected amount
                /* String selectedAmountStr = null;
                if (paramMap.containsKey("amount" + thisSuffix)) {
                    selectedAmountStr = (String) paramMap.remove("amount" + thisSuffix);
                } */

                // parse the amount
                /* BigDecimal amount = null;
                if (UtilValidate.isNotEmpty(selectedAmountStr)) {
                    try {
                        amount = new BigDecimal(selectedAmountStr);
                    } catch (Exception e) {
                        Debug.logWarning(e, "Problem parsing amount string: " + selectedAmountStr, module);
                        amount = null;
                    }
                } else {
                    amount = BigDecimal.ZERO;
                } */

                if (paramMap.containsKey("itemDesiredDeliveryDate" + thisSuffix)) {
                    itemDesiredDeliveryDateStr = (String) paramMap.remove("itemDesiredDeliveryDate" + thisSuffix);
                }
                // get the item type
                if (paramMap.containsKey("itemType" + thisSuffix)) {
                    itemType = (String) paramMap.remove("itemType" + thisSuffix);
                }

                if (paramMap.containsKey("itemDescription" + thisSuffix)) {
                    itemDescription = (String) paramMap.remove("itemDescription" + thisSuffix);
                }
                
                if (paramMap.containsKey("selectedItem" + thisSuffix)) {
                	selectedItemStr = (String) paramMap.remove("selectedItem" + thisSuffix);
                	if (UtilValidate.isNotEmpty(selectedItemStr) && "Y".equals(selectedItemStr)) {
                		selectedItem = true;
                	}
                }
                
                if (paramMap.containsKey("orderId" + thisSuffix)) {
                    orderId = (String) paramMap.remove("orderId" + thisSuffix);
                }
                if (paramMap.containsKey("orderItemSeqId" + thisSuffix)) {
                    orderItemSeqId = (String) paramMap.remove("orderItemSeqId" + thisSuffix);
                }

                Map<String, Object> itemAttributes = UtilMisc.<String, Object>toMap("itemDesiredDeliveryDate", itemDesiredDeliveryDateStr);

                if (selectedItem && quantity.compareTo(BigDecimal.ZERO) > 0) {
                    Debug.logInfo("Attempting to add to cart with productId = " + productId + ", categoryId = " + productCategoryId +
                            ", quantity = " + quantity + ", itemType = " + itemType + " and itemDescription = " + itemDescription, module);
                    if (UtilValidate.isNotEmpty(cart.getDefaultItemDeliveryDate())) {
                    	itemAttributes.put("itemDesiredDeliveryDate", cart.getDefaultItemDeliveryDate());
                    	itemAttributes.put("useAsDefaultDesiredDeliveryDate", "true");
                    }
                    
                    
                	Map<String, Object> outMap = null;
                    try {
                    	// the products have already been checked in the order, no need to check their validity again
                    	outMap = dispatcher.runSync("loadCartFromOrderItem",
						        UtilMisc.<String, Object>toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId, 
						                "shoppingCart", cart, "skipProductChecks", Boolean.TRUE, "includePromoItems", true, "userLogin", userLogin, "locale", locale));
					} catch (GenericServiceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    if (outMap != null) {
                    	result.putAll(outMap);
                    }
                    
                    processResultAddMessage(result, request);
                    /* if (UtilValidate.isNotEmpty(outMap) && UtilValidate.isNotEmpty(outMap.get("shoppingCart"))) {
                    	cart = (ShoppingCart) outMap.get("shoppingCart");
                    }
                    result.put("itemId", new Integer(orderItemSeqId));
                    result = cartHelper.addToCart(catalogId, shoppingListId, shoppingListItemSeqId, productId,
                                                  productCategoryId, itemType, itemDescription, null,
                                                  amount, quantity, null, null, null, null, null, null,
                                                  itemGroupNumber, itemAttributes,null); */
                }
            }
            // no values for price and paramMap (a context for adding attributes)
            controlDirective = processResultCustom(result, request);
            if (controlDirective.equals(ERROR)) {    // if the add to cart failed, then get out of this loop right away
                return "error";
            }
        }

        // Determine where to send the browser
        return cart.viewCartOnAdd() ? "viewcart" : "success";
    }
    
    
    /* Create sales order of Distributor (is purchase order of store customer)
     * No relation with accounting
     */
    public static String routeSalesOrderEntryDis(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        // if the order mode is not set in the attributes, then order entry has not been initialized
        if (session.getAttribute("orderMode") == null) {
            return "init";
        }
        /*// if the request is coming from the init page, then orderMode will be in the request parameters
        if (request.getParameter("orderMode") != null) {
            return "agreements"; // next page after init is always agreements
        }*/

        // orderMode is set and there is an order in progress, so go straight to the cart
        return "cart";
    }
    
    /** Route order entry **/
    public static String routeOrderEntrySales (HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        // if the order mode is not set in the attributes, then order entry has not been initialized
        if (session.getAttribute("orderMode") == null) {
            return "init";
        }
        
        // if the request is coming from the init page, then orderMode will be in the request parameters
        if (request.getParameter("orderMode") != null) {
            return "agreements"; // next page after init is always agreements
        }
        
        // orderMode is set and there is an order in progress, so go straight to the cart
        return "cart";
    }
    
    /** Initialize order entry **/
    public static String initializeSalesOrderEntryDis(HttpServletRequest request, HttpServletResponse response) {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	HttpSession session = request.getSession();
    	List<String> alertMessageList = FastList.newInstance();
    	String productStoreId = request.getParameter("productStoreId");
    	
    	String result0 = checkValidateOrderEntry(request, response);
    	if ("error".equals(result0)) return result0;
    	
    	session.setAttribute("productStoreId", productStoreId);
        ShoppingCart cart = getCartObject(request);
    	
        GenericValue productStore = null;
        if (UtilValidate.isNotEmpty(productStoreId)) {
            productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
        }
        
    	// STEP 1 & 2
    	String result1 = initializeOrderEntryFirstLoadInfoGeneral(request, response, cart, productStore, alertMessageList);
    	if ("error".equals(result1)) return result1;
        
    	// STEP 3
    	String result3 = initializeOrderEntrySecondCheckoutOption(request, response, alertMessageList, OrderCategory.ORDER_DISTRIBUTOR_SALES);
    	if ("error".equals(result3)) return result3;
        
        return "success";
    }
    
    /** Initialize order entry Sales Order JQ **/
    public static String initializeOrderEntrySales(HttpServletRequest request, HttpServletResponse response) {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	HttpSession session = request.getSession();
    	List<String> alertMessageList = FastList.newInstance();
        org.ofbiz.order.shoppingcart.ShoppingCartEvents.destroyCart(request, response);
    	String productStoreId = request.getParameter("productStoreId");
    	
    	String result0 = checkValidateOrderEntry(request, response);
    	if ("error".equals(result0)) return result0;
    	
    	session.setAttribute("productStoreId", productStoreId);
        ShoppingCart cart = getCartObject(request);
    	
        GenericValue productStore = null;
        if (UtilValidate.isNotEmpty(productStoreId)) {
            productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
        }
        
    	// STEP 1 & 2
    	String result1 = initializeOrderEntryFirstLoadInfoGeneral(request, response, cart, productStore, alertMessageList);
    	if ("error".equals(result1)) return result1;
        
    	// STEP 3
    	String result3 = initializeOrderEntrySecondCheckoutOption(request, response, alertMessageList, OrderCategory.ORDER_COMPANY_SALES);
    	if ("error".equals(result3)) return result3;
        
        return "success";
    }
    
    public static String checkValidateOrderEntry(HttpServletRequest request, HttpServletResponse response) {
        Locale locale = UtilHttp.getLocale(request);
        org.ofbiz.order.shoppingcart.ShoppingCartEvents.destroyCart(request, response);
        String productStoreId = request.getParameter("productStoreId");
        List<Object> errMsgList = FastList.newInstance();
        if (UtilValidate.isEmpty(productStoreId)) {
        	errMsgList.add(UtilProperties.getMessage(resource_delys,"DAProductStoreNotBeEmpty", locale));
        }
        if (UtilValidate.isEmpty(request.getParameter("partyId"))) {
        	errMsgList.add(UtilProperties.getMessage(resource_delys,"DACustomerMustNotBeEmpty", locale));
        }
        if (UtilValidate.isEmpty(request.getParameter("currencyUomId"))) {
        	errMsgList.add(UtilProperties.getMessage(resource_delys, "DACurrencyUomIdMustNotBeEmpty", locale));
        }
        if (UtilValidate.isEmpty(request.getParameter("desiredDeliveryDate"))) {
        	errMsgList.add(UtilProperties.getMessage(resource_delys,"DADesiredDeliveryDateMustNotBeEmpty", locale));
        }
        if (UtilValidate.isNotEmpty(errMsgList)) {
        	request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
        	return "error";
        }
        
    	return "success";
    }
    
    public static String initializeOrderEntryFirstLoadInfoGeneral(HttpServletRequest request, HttpServletResponse response, ShoppingCart cart, GenericValue productStore, List<String> alertMessageList) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        Security security = (Security) request.getAttribute("security");
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);
        
        //checkValidateOrderEntry
        
        // TODO: re-factor and move this inside the ShoppingCart constructor
        String orderMode = "SALES_ORDER"; //fix order type = "sales order"
        cart.setOrderType(orderMode);
        session.setAttribute("orderMode", orderMode);
        
        // check the selected product store
        if (productStore != null) {
        	String productStoreId = productStore.getString("productStoreId");
        	
            // check permission for taking the order
            boolean hasPermission = false;
            if (cart.getOrderType().equals("SALES_ORDER")) {
            	hasPermission = security.hasEntityPermission("ORDERMGR", "_SALES_CREATE", session);
            	if (!hasPermission) hasPermission = security.hasEntityPermission("ORDERSAL", "_DIS_CREATE", userLogin);
                if (!hasPermission) {
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
        
        if ("SALES_ORDER".equals(cart.getOrderType()) && UtilValidate.isEmpty(cart.getProductStoreId())) {
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderAProductStoreMustBeSelectedForASalesOrder", locale));
            cart.clear();
            session.removeAttribute("orderMode");
            return "error";
        }
        String salesChannelEnumId = "WEB_SALES_CHANNEL"; //fix request.getParameter("salesChannelEnumId");
        cart.setChannelType(salesChannelEnumId);
        // set party info
        String partyId = "";
        String originOrderId = request.getParameter("originOrderId");
        cart.setAttribute("originOrderId", originOrderId);
        // customer = request.getParameter("partyId")
        if (!UtilValidate.isEmpty(request.getParameter("partyId"))) {
            partyId = request.getParameter("partyId");
        }
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
            // not get parameters: agreementId, workEffortId, shipBeforeDateStr, shipAfterDateStr, cancelBackOrderDateStr,
            // move up: currencyUomId
            ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
            String currencyUomId = request.getParameter("currencyUomId");
            String orderId = request.getParameter("orderId");
            String orderName = request.getParameter("orderName");
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
                    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderIdAlreadyExistsPleaseChooseAnother", locale));
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
            String desiredDeliveryDateStr = request.getParameter("desiredDeliveryDate");
            
            Timestamp desiredDeliveryDate = null;
            try {
    	        if (UtilValidate.isNotEmpty(desiredDeliveryDateStr)) {
    	        	Long fromDateL = Long.parseLong(desiredDeliveryDateStr);
    	        	desiredDeliveryDate = new Timestamp(fromDateL);
    	        }
            } catch (Exception e) {
            	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "DAErrorWhenFormatDateTime", locale));
            	return "error";
            }
            
            //Timestamp desiredDeliveryDateDate = Timestamp.valueOf(desiredDeliveryDate);
            cart.setDefaultItemDeliveryDate(desiredDeliveryDate.toString());
            
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
                request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
                return "error";
            }
            
            if (agreements != null) {
            	for (GenericValue agreementItem : agreements) {
            		if (agreementItem.containsKey("isAuto") && "Y".equals(agreementItem.getString("isAuto"))) {
            			agreementId = agreementItem.getString("agreementId");
            		}
            	}
            }
            
            if (UtilValidate.isNotEmpty(agreementId)) {
            	// set the agreement if specified otherwise set the currency
                if (UtilValidate.isNotEmpty(agreementId)) {
                    result2 = cartHelper.selectAgreement(agreementId);
                }
                if (ServiceUtil.isError(result2)) {
                    request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(result2));
                    return "error";
                }
            }
            
            // TODOCHANGE add new attribute: "salesMethodChannelEnumId"
            //String salesMethodChannelEnumId = (String) request.getParameter("salesMethodChannelEnumId");
            String salesMethodChannelEnumId = null;
            if (productStore != null && productStore.containsKey("salesMethodChannelEnumId")) salesMethodChannelEnumId = productStore.getString("salesMethodChannelEnumId");
            if (salesMethodChannelEnumId != null) {
                cart.setAttribute("salesMethodChannelEnumId", salesMethodChannelEnumId);
            }
            
            // STEP 3: ShoppingCartEvents.java - method addToCart
            // Event to add an item to the shopping cart
            // not get parameters: controlDirective, xxx, parentProductId, itemType, itemDescription, productCategoryId, xxx, 
            //				price(BigDecimal), xxx, quantity(BigDecimal), reservStartStr, reservEndStr, reservStart(Timestamp), reservEnd(Timestamp),
            //				reservLengthStr, reservLength(BigDecimal), reservPersonsStr, reservPersons(BigDecimal), accommodationMapId, accommodationSpotId, 
            //				shipBeforeDateStr, shipAfterDateStr, shipBeforeDate(Timestamp), shipAfterDate(Timestamp), numberOfDay
            // use parameters: productId, priceStr, quantityStr
            String catalogId = request.getParameter("catalogId");
            if (UtilValidate.isNotEmpty(catalogId)) {
            	catalogId = CatalogWorker.getCurrentCatalogId(request);
            }
            // remove paramMap (UtilHttp.getCombinedMap(request))
            
            // Call multiple addToCart method
            String strParam = request.getParameter("strParam");
            if (UtilValidate.isNotEmpty(strParam)) {
            	String[] strParamLine = strParam.split("\\|OLBIUS\\|"); //item (productId - quantity - quantityUomId - expireDate)
            	if ("N".equals(strParamLine[0]) && strParamLine.length > 1) {
            		for (int i = 1; i < strParamLine.length; i++) {
            			String[] lineValues = strParamLine[i].split("\\|SUIBLO\\|");
            			String productId = lineValues.length > 0 ? lineValues[0] : "";
            			String quantityStr = lineValues.length > 1 ? lineValues[1] : "";
            			String quantityUomId = lineValues.length > 2 ? lineValues[2] : "";
            			String expireDateStr = lineValues.length > 3 ? lineValues[3] : "";
            			BigDecimal price = null;
            			BigDecimal quantity = BigDecimal.ZERO;
            			Timestamp expireDate = null;
            			BigDecimal alternativeQuantity = null;
            			if (UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(quantityStr)) {
            				// Check quantityUomId with productQuotation
            				BigDecimal quantityUomIdToDefault = BigDecimal.ONE;
        					GenericValue productItem = null;
        					try {
        						productItem = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
        						if (productItem == null) {
        							alertMessageList.add(UtilProperties.getMessage(resource_error, "DAProductNotExists",locale));
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
            		        } catch (Exception e) {
            		            Debug.logWarning(e, "Problems parsing price string: " + priceStr, module);
            		            price = null;
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
            				String shoppingListId = null;
            				String shoppingListItemSeqId = null;
            				String productCategoryId = null; //parameter: add_category_id
            				String itemType = null;
            				String itemDescription = null;
            				BigDecimal amount = null;
            				Timestamp reservStart = null;
            				BigDecimal reservLength = null;
            				BigDecimal reservPersons = null;
            				String accommodationMapId = null;
            				String accommodationSpotId = null;
            				Timestamp shipBeforeDate = null;
            				Timestamp shipAfterDate = null;
            				ProductConfigWrapper configWrapper = null;
            				String itemGroupNumber = null; //itemGroupNumber
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
            				if (alternativeQuantity != null) {
            					paramMap.put("alternativeQuantity", alternativeQuantity);
            				}
            				if (expireDate != null) {
            					paramMap.put("expireDate", expireDate);
            				}
            				String parentProductId = null;
            				// Translate the parameters and add to the cart
            		        result = cartHelper.addToCart(catalogId, shoppingListId, shoppingListItemSeqId, productId, productCategoryId,
            		                itemType, itemDescription, price, amount, quantity, reservStart, reservLength, reservPersons,
            		                accommodationMapId, accommodationSpotId,
            		                shipBeforeDate, shipAfterDate, configWrapper, itemGroupNumber, paramMap, parentProductId);
            				
            		        /* controlDirective = processResult(result, request);
    					        Integer itemId = (Integer)result.get("itemId");
    					        if (UtilValidate.isNotEmpty(itemId)) {request.setAttribute("itemId", itemId);}
    					        // Determine where to send the browser
    					        if (controlDirective.equals(ERROR)) {return "error";} else {if (cart.viewCartOnAdd()) {return "viewcart";} else {return "success";}} 
            		         */
            			}
                    }
            	}
            }
        }
        return "success";
    }
    
    public static String initializeOrderEntrySecondCheckoutOption(HttpServletRequest request, HttpServletResponse response, List<String> alertMessageList, OrderCategory orderCate) {
    	// STEP 3: updateCheckoutOptions: When user selects a shipping method, this automatically reloads quick checkout page with shipping estimates filled in.
        /**
        -> Click Quick ship
        <request-map uri="quickcheckout">
	        <security https="true" auth="true"/>
	        <response name="success" type="view" value="quickcheckout" save-home-view="true"/>
	    </request-map>
	    -> Select 1 address
        <request-map uri="updateCheckoutOptions">
	        <description>When user selects a shipping method, this automatically reloads quick checkout page with shipping estimates filled in.</description>
	        <security https="true" auth="true"/>
	        <event type="java" path="org.ofbiz.order.shoppingcart.CheckOutEvents" invoke="setPartialCheckOutOptions"/>
	        <response name="success" type="view" value="checkoutshippingaddress"/>
	        <response name="error" type="view" value="showcart"/>
	    </request-map>
	    -> Continue to final order review
	    
        <request-map uri="checkout">
	        <security https="true" auth="true"/>
	        <event type="java" path="org.ofbiz.order.shoppingcart.CheckOutEvents" invoke="setQuickCheckOutOptions"/>
	        <response name="success" type="request" value="calcShipping"/>
	        <response name="error" type="view-last"/>
	    </request-map>
		<request-map uri="calcShipping">
	        <security direct-request="false"/>
	        <event type="java" path="org.ofbiz.order.shoppingcart.shipping.ShippingEvents" invoke="getShipEstimate"/>
	        <response name="success" type="request" value="calcTax"/>
	        <response name="error" type="request" value="orderentry"/>
	    </request-map>
	    <request-map uri="calcTax">
	        <security direct-request="false"/>
	        <event type="java" path="org.ofbiz.order.shoppingcart.CheckOutEvents" invoke="calcTax"/>
	        <response name="success" type="view" value="confirm"/>
	        <response name="error" type="request" value="orderentry"/>
	    </request-map>
         */
        // uri="updateCheckoutOptions" > call update shipping address before
    	if (OrderCategory.ORDER_COMPANY_SALES.equals(orderCate)) {
    		// company buy for distributor
    		String updateCheckoutOptionsResult = CheckOutEvents.setPartialCheckOutOptionsSales(request, response);
            if ("error".equals(updateCheckoutOptionsResult)) return "error";
            // uri="checkout" > CheckOutEvents.setQuickCheckOutOptions
            String checkoutResult = CheckOutEvents.setQuickCheckOutOptionsSales(request, response);//String checkoutResult = CheckOutEventsDis.setQuickCheckOutOptions(request, response);
            if ("error".equals(checkoutResult)) return "error";
            String calcShippingResult = org.ofbiz.order.shoppingcart.shipping.ShippingEvents.getShipEstimate(request, response);
            if ("error".equals(calcShippingResult)) return "error";
            String calcTax = org.ofbiz.order.shoppingcart.CheckOutEvents.calcTax(request, response);
            if ("error".equals(calcTax)) return "error";
    	} else if (OrderCategory.ORDER_DISTRIBUTOR_SALES.equals(orderCate)) {
    		// distributor buy for store customer
    		String checkoutResult = CheckOutEventsDis.setQuickCheckOutOptions(request, response);
            if ("error".equals(checkoutResult)) return "error";
            String calcShippingResult = org.ofbiz.order.shoppingcart.shipping.ShippingEvents.getShipEstimate(request, response);
            if ("error".equals(calcShippingResult)) return "error";
            String calcTax = org.ofbiz.order.shoppingcart.CheckOutEvents.calcTax(request, response);
            if ("error".equals(calcTax)) return "error";
    	}
        
        if (UtilValidate.isNotEmpty(alertMessageList)) {
        	request.setAttribute("_ERROR_MESSAGE_LIST_", alertMessageList);
        }
    	
    	return "success";
    }
}
