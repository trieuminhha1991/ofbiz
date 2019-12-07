package com.olbius.order;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.ItemNotFoundException;
import org.ofbiz.security.Security;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

@SuppressWarnings("serial")
public class DeliveryRequirementCart implements Iterable<DeliveryRequirementItem>, Serializable {
	public static final String module = DeliveryRequirementCart.class.getName();
	public static final String resource = "DelysAdminUiLabels";
	public static final String resource_error = "DelysAdminUiLabels";
	
	private static final String NO_ERROR = "noerror";
    private static final String NON_CRITICAL_ERROR = "noncritical";
    private static final String ERROR = "error";
	
	private transient Delegator delegator = null;
	private String delegatorName = null;
	protected Locale locale; // holds the locale from the user session
	private boolean readOnlyCart = false;
	
	private String deliveryReqId = null;
	private String deliveryReqName = null;
	private String deliveryReqStatus = null;
	private String deliveryReqDescription = null;
	private String deliveryReqType = null;
	private Timestamp requirementStartDate = null; //start date
	private Timestamp requiredByDate = null; //create date
	private GenericValue userLogin = null;
	private GenericValue autoUserLogin = null;
	private boolean viewCartOnAdd = false;
	private long nextItemSeq = 1;
	
	private List<DeliveryRequirementItem> cartLines = FastList.newInstance();
	
	public DeliveryRequirementCart() {
		// TODO Auto-generated constructor stub
	}
	
	public DeliveryRequirementCart(DeliveryRequirementCart cart) {
		this.delegator = cart.getDelegator();
		this.delegatorName = cart.getDelegatorName();
		this.deliveryReqId = cart.getDeliveryReqId();
		this.deliveryReqName = cart.getDeliveryReqName();
		this.deliveryReqStatus = cart.getDeliveryReqStatus();
		this.deliveryReqDescription = cart.getDeliveryReqDescription();
		this.deliveryReqType = cart.getDeliveryReqType();
		this.locale = cart.getLocale();
		
		for (DeliveryRequirementItem item : cart) {
			cartLines.add(new DeliveryRequirementItem(item));
		}
	}
	
	
	
	public String getDeliveryReqType() {
		return deliveryReqType;
	}

	public void setDeliveryReqType(String deliveryReqType) {
		this.deliveryReqType = deliveryReqType;
	}

	public DeliveryRequirementCart(HttpServletRequest request, Locale locale, String deliveryReqId, String deliveryReqName, String deliveryReqDescription, String deliveryReqStatus, String deliveryReqType) {
        // for purchase orders, bill to customer partyId must be set - otherwise, no way to know who we're purchasing for.  supplierPartyId is furnished
        // by order manager for PO entry.
        
		this.delegator = (Delegator) request.getAttribute("delegator");
		this.locale = UtilHttp.getLocale(request);
		this.deliveryReqId = deliveryReqId;
		this.deliveryReqName = deliveryReqName;
		this.deliveryReqDescription = deliveryReqDescription;
		this.deliveryReqStatus = deliveryReqStatus;
		this.deliveryReqType = deliveryReqType;
		
        HttpSession session = request.getSession(true);
        this.userLogin = (GenericValue) session.getAttribute("userLogin");
        this.autoUserLogin = (GenericValue) session.getAttribute("autoUserLogin");
    }
	
	public DeliveryRequirementCart(HttpServletRequest request, Locale locale) {
        // for purchase orders, bill to customer partyId must be set - otherwise, no way to know who we're purchasing for.  supplierPartyId is furnished
        // by order manager for PO entry.
        
		this.delegator = (Delegator) request.getAttribute("delegator");
		this.locale = UtilHttp.getLocale(request);
		
        HttpSession session = request.getSession(true);
        this.userLogin = (GenericValue) session.getAttribute("userLogin");
        this.autoUserLogin = (GenericValue) session.getAttribute("autoUserLogin");
    }
	
	/** Main get cart method; uses the locale & currency from the session */
    public static DeliveryRequirementCart getCartObject(HttpServletRequest request) {
        return getCartObject(request, null);
    }
	
	/** Gets or creates the shopping cart object */
    public static DeliveryRequirementCart getCartObject(HttpServletRequest request, Locale locale) {
//        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DeliveryRequirementCart cart = (DeliveryRequirementCart) request.getAttribute("deliveryCart");
        HttpSession session = request.getSession(true);
        if (cart == null) {
            cart = (DeliveryRequirementCart) session.getAttribute("deliveryCart");
        } else {
            session.setAttribute("deliveryCart", cart);
        }

        if (cart == null) {
            cart = new DeliveryRequirementCart(request, locale);
            session.setAttribute("deliveryCart", cart);
        } else {
            if (locale != null && !locale.equals(cart.getLocale())) {
                cart.setLocale(locale);
            }
        }
        return cart;
    }
	
    /** Route order entry **/
    public static String routeDeliveryReqEntry(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        // if the order mode is not set in the attributes, then order entry has not been initialized
        if (session.getAttribute("deliveryCart") == null) {
            return "init";
        }
        
        return "cart";
    }
    
	/** Initialize order entry **/
    public static String initializeDeliveryRequirement(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
//        HttpSession session = request.getSession();
//        Security security = (Security) request.getAttribute("security");
//        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);
//        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
//        Map<String, Object> result = null;

        String deliveryReqId = request.getParameter("deliveryReqId");
        String deliveryReqName = request.getParameter("deliveryReqName");
        String deliveryReqDescription = request.getParameter("deliveryReqDescription");
        String requirementStartDateStr = null;
        String requiredByDateStr = null;
        Timestamp requirementStartDate = null;
        Timestamp requiredByDate = null;
        // Get the parameters as a MAP, remove the productId and quantity params.
        Map<String, Object> paramMap = UtilHttp.getCombinedMap(request);
        
        if (UtilValidate.isNotEmpty(deliveryReqId)) {
        	GenericValue deliveryReq = null;
			try {
				deliveryReq = delegator.findOne("Requirement", UtilMisc.toMap("requirementId", deliveryReqId), true);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	if (UtilValidate.isNotEmpty(deliveryReq)) {
        		request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"DAThisDeliveryProposalIdIsExisted", locale));
                return "error";
            }
        }
        
//        List<Object> errMsgList = FastList.newInstance();
//        if (UtilValidate.isEmpty(request.getParameter("partyId"))) {
//        	errMsgList.add(UtilProperties.getMessage(resource,"DACustomerMustNotBeEmpty", locale));
//        }
//        if (UtilValidate.isEmpty(request.getParameter("desiredDeliveryDate"))) {
//        	errMsgList.add(UtilProperties.getMessage(resource,"DADesiredDeliveryDateMustNotBeEmpty", locale));
//        }
//        if (UtilValidate.isNotEmpty(errMsgList)) {
//        	request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
//        	return "error";
//        }
        
        // get the create requirement date (handles both yyyy-mm-dd input and full timestamp)
        requirementStartDateStr = (String) paramMap.remove("requirementStartDate");
        if (UtilValidate.isEmpty(requirementStartDateStr)) {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "DARequirementStartDateMustNotBeEmpty", locale));
            return "error"; // not critical return to same page
        } else {
            if (requirementStartDateStr.length() == 10) requirementStartDateStr += " 00:00:00.000";
            try {
            	requirementStartDate = java.sql.Timestamp.valueOf(requirementStartDateStr);
            } catch (IllegalArgumentException e) {
                Debug.logWarning(e, "Bad shipAfterDate input: " + e.getMessage(), module);
                requirementStartDate = null;
            }
        }
        
        DeliveryRequirementCart cart = getCartObject(request);
        cart.setDeliveryReqStatus("DVER_REQ_CREATED");
        cart.setDeliveryReqType("DELIVERY_SALES_REQ");
        cart.setDeliveryReqId(deliveryReqId);
        cart.setDeliveryReqName(deliveryReqName);
        cart.setDeliveryReqDescription(deliveryReqDescription);

        // get requirement execute date (handles both yyyy-mm-dd input and full timestamp)
        requiredByDateStr = (String) paramMap.remove("requiredByDate");
        if (UtilValidate.isNotEmpty(requiredByDateStr)) {
            if (requiredByDateStr.length() == 10) requiredByDateStr += " 00:00:00.000";
            try {
            	requiredByDate = java.sql.Timestamp.valueOf(requiredByDateStr);
            } catch (IllegalArgumentException e) {
                Debug.logWarning(e, "Bad requiredByDate input: " + e.getMessage(), module);
                requiredByDateStr = null;
            }
        }

        cart.setRequiredByDate(requiredByDate);
        cart.setRequirementStartDate(requirementStartDate);
        return "success";
    }
    
    /** Event to add an item to the shopping cart. */
    public static String addToCart(HttpServletRequest request, HttpServletResponse response) {
//        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DeliveryRequirementCart cart = getCartObject(request);
        String controlDirective = null;
        Map<String, Object> result = null;
        String orderId = null;
        String itemDescription = null;

        Locale locale = UtilHttp.getLocale(request);
        
        // Get the parameters as a MAP, remove the productId and quantity params.
        Map<String, Object> paramMap = UtilHttp.getCombinedMap(request);
        
        if (paramMap.containsKey("ORDER_ID")) {
            orderId = (String) paramMap.remove("ORDER_ID");
        } else if (paramMap.containsKey("order_id")) {
        	orderId = (String) paramMap.remove("order_id");
        }

        Debug.logInfo("adding item order " + orderId, module);

        if (paramMap.containsKey("ADD_ITEM_DESCRIPTION")) {
            orderId = (String) paramMap.remove("ADD_ITEM_DESCRIPTION");
        } else if (paramMap.containsKey("add_item_description")) {
        	orderId = (String) paramMap.remove("add_item_description");
        }
        
        if (UtilValidate.isEmpty(orderId)) {
            // before returning error; check make sure we aren't adding a special item type
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "DAOrderMustNotBeEmpty", locale));
            return "success"; // not critical return to same page
        }
//        
//        GenericValue order = null;
//        try {
//        	order = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), true);
//		} catch (GenericEntityException e1) {
//			Debug.logWarning(e1.toString(), module);
//			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "DAOrderMustNotBeEmpty", locale));
//            return "error"; // not critical return to same page
//		}
//        
//        if (UtilValidate.isEmpty(order)) {
//			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "DAOrderNoOrderFound", locale));
//            return "error"; // not critical return to same page
//        }

        // check for an itemDescription
        if (paramMap.containsKey("ADD_ITEM_DESCRIPTION")) {
            itemDescription = (String) paramMap.remove("ADD_ITEM_DESCRIPTION");
        } else if (paramMap.containsKey("add_item_description")) {
            itemDescription = (String) paramMap.remove("add_item_description");
        }
        if (itemDescription != null && itemDescription.length() == 0) {
            itemDescription = null;
        }

        // Translate the parameters and add to the cart
        result = cart.addToCart(orderId, itemDescription, paramMap, dispatcher);
       
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
    
	
	public Delegator getDelegator() {
		if (delegator == null) {
			delegator = DelegatorFactory.getDelegator(delegatorName);
		}
		return delegator;
	}
	
	public Locale getLocale() {
		return locale;
	}
	
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	public boolean isReadOnlyCart() {
		return readOnlyCart;
	}
	
	public int addItem(int index, DeliveryRequirementItem item) throws CartItemModifyException {
		if (isReadOnlyCart()) {
			throw new CartItemModifyException("Cart items cannot be changed");
		}
		if (!cartLines.contains(item)) {
			cartLines.add(index, item);
			return index;
		} else {
			return this.getItemIndex(item);
		}
	}
	
	/** add rental (with accommodation) item to cart and order item attributes*/
    public int addOrIncreaseItem(String orderId, String description, LocalDispatcher dispatcher) throws CartItemModifyException, ItemNotFoundException {
        if (isReadOnlyCart()) {
           throw new CartItemModifyException("Cart items cannot be changed");
        }

        // Check for existing cart item.
        for (int i = 0; i < this.cartLines.size(); i++) {
            DeliveryRequirementItem sci = cartLines.get(i);

            if (sci.equals(orderId)) {
            	Map<String, Object> messageMap = UtilMisc.<String, Object> toMap("orderId", orderId);
    			String excMsg = UtilProperties.getMessage(resource_error, "DAOrderIsExisted", messageMap, this.getLocale());
    			Debug.logWarning(excMsg, module);
    			throw new ItemNotFoundException(excMsg);
            }
        }
        // Add the new item to the cart if it wasn't found.
        DeliveryRequirementItem item = null;
        if (orderId == null) {
			String excMsg = UtilProperties.getMessage(resource_error, "DAOrderMustNotBeEmpty", this.getLocale());
			Debug.logWarning(excMsg, module);
			throw new ItemNotFoundException(excMsg);
        }
        
        item = DeliveryRequirementItem.makeDeliveryRequirementItem(Integer.valueOf(0), orderId, description, dispatcher, this);

        return this.addItem(0, item);
    }
    
    /** Event to add an item to the shopping cart with accommodation. */
    public Map<String, Object> addToCart(String orderId, String description, Map<String, ? extends Object> context, LocalDispatcher dispatcher) {
        Map<String, Object> result = null;
        
        // price sanity check
        if (orderId == null) {
            String errMsg = UtilProperties.getMessage(resource_error, "DAOrderMustNotBeEmpty", this.getLocale());
            result = ServiceUtil.returnError(errMsg);
            return result;
        }

        // check for required amount flag; if amount and no flag set to 0
        GenericValue order = null;
        try {
            order = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), true);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to lookup order : " + orderId, module);
        }
        
        if (order == null) {
        	String errMsg = UtilProperties.getMessage(resource_error, "DAOrderNoOrderFound", this.getLocale());
            result = ServiceUtil.returnError(errMsg);
            return result;
        }

        // add or increase the item to the cart
        int itemId = -1;
        try {
        	itemId = this.addOrIncreaseItem(orderId, description, dispatcher);
        } catch (CartItemModifyException e) {
            result = ServiceUtil.returnError(e.getMessage());
            return result;
        } catch (ItemNotFoundException e) {
            result = ServiceUtil.returnError(e.getMessage());
            return result;
        }

        // Indicate there were no critical errors
        result = ServiceUtil.returnSuccess();
        if (itemId != -1) {
            result.put("itemId", new Integer(itemId));
        }
        return result;
    }
	
	public void removeCartItem(int index, LocalDispatcher dispatcher) throws CartItemModifyException {
		if (isReadOnlyCart()) {
           throw new CartItemModifyException("Cart items cannot be changed");
        }
        if (index < 0) return;
        if (cartLines.size() <= index) return;
        cartLines.remove(index);
//        DeliveryRequirementItem item = cartLines.remove(index);
	}
	
	public void removeCartItem(int index) throws CartItemModifyException {
		if (isReadOnlyCart()) {
           throw new CartItemModifyException("Cart items cannot be changed");
        }
        if (index < 0) return;
        if (cartLines.size() <= index) return;
        cartLines.remove(index);
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
	
	/** Update the items in the shopping cart. */
    public static String modifyCart(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        DeliveryRequirementCart cart = getCartObject(request);
        Locale locale = UtilHttp.getLocale(request);
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
//        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Security security = (Security) request.getAttribute("security");
//        ShoppingCartHelper cartHelper = new ShoppingCartHelper(null, dispatcher, cart);
        String controlDirective;
        Map<String, Object> result;
        // not used yet: Locale locale = UtilHttp.getLocale(request);

        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);

        String removeSelectedFlag = request.getParameter("removeSelected");
        String selectedItems[] = request.getParameterValues("selectedItem");
        boolean removeSelected = ("true".equals(removeSelectedFlag) && selectedItems != null && selectedItems.length > 0);
        result = cart.modifyCart(security, userLogin, paramMap, removeSelected, selectedItems, locale);
        controlDirective = processResult(result, request);

        //Determine where to send the browser
        if (controlDirective.equals(ERROR)) {
            return "error";
        } else {
            return "success";
        }
    }
    
    /** Get a ShoppingCartItem from the cart object. */
    public DeliveryRequirementItem findCartItem(int index) {
        if (cartLines.size() <= index) {
            return null;
        }
        return cartLines.get(index);
    }
    
    /** Update the items in the shopping cart. */
    public Map<String, Object> modifyCart(Security security, GenericValue userLogin, Map<String, ? extends Object> context, boolean removeSelected, String[] selectedItems, Locale locale) {
        Map<String, Object> result = null;
        if (locale == null) {
            locale = this.getLocale();
        }

        ArrayList<DeliveryRequirementItem> deleteList = new ArrayList<DeliveryRequirementItem>();
        ArrayList<String> errorMsgs = new ArrayList<String>();

        if (this.isReadOnlyCart()) {
            String errMsg = UtilProperties.getMessage(resource_error, "cart.cart_is_in_read_only_mode", this.getLocale());
            errorMsgs.add(errMsg);
            result = ServiceUtil.returnError(errorMsgs);
            return result;
        }

        // get a list of the items to delete
        if (removeSelected) {
            for (int si = 0; si < selectedItems.length; si++) {
                String indexStr = selectedItems[si];
                DeliveryRequirementItem item = null;
                try {
                    int index = Integer.parseInt(indexStr);
                    item = this.findCartItem(index);
                } catch (Exception e) {
                    Debug.logWarning(e, UtilProperties.getMessage(resource_error, "OrderProblemsGettingTheCartItemByIndex", this.getLocale()));
                }
                if (item != null) {
                    deleteList.add(item);
                }
            }
        }

        for (DeliveryRequirementItem item : deleteList) {
            int itemIndex = this.getItemIndex(item);

            if (Debug.infoOn())
                Debug.logInfo("Removing item index: " + itemIndex, module);
            try {
                this.removeCartItem(itemIndex);
            } catch (CartItemModifyException e) {
                result = ServiceUtil.returnError(new ArrayList<String>());
                errorMsgs.add(e.getMessage());
            }
        }
        
        if (errorMsgs.size() > 0) {
            result = ServiceUtil.returnError(errorMsgs);
            return result;
        }

        result = ServiceUtil.returnSuccess();
        return result;
    }
	
	/** Totally wipe out the cart, removes all stored info. */
    public static String destroyCart(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        clearCart(request, response);
        session.removeAttribute("deliveryCart");
        return "success";
    }
    
    /** Empty the shopping cart. */
    public static String clearCart(HttpServletRequest request, HttpServletResponse response) {
        DeliveryRequirementCart cart = getCartObject(request);
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
    
    /** Clears out the cart. */
    public void clear() {
    	this.delegator = null;
		this.delegatorName = null;
		this.deliveryReqId = null;
		this.deliveryReqName = null;
		this.deliveryReqStatus = null;
		this.deliveryReqDescription = null;
		this.deliveryReqType = null;
		this.locale = null;
		this.nextItemSeq = 1;
		this.cartLines.clear();
    }
    
    public void setNextItemSeq(long seq) throws GeneralException {
        if (this.nextItemSeq != 1) {
            throw new GeneralException("Cannot set the item sequence once the sequence has been incremented!");
        } else {
            this.nextItemSeq = seq;
        }
    }
//    
//    public void clearAllItemStatus() {
//        for (DeliveryRequirementItem item : this) {
//            item.setStatusId(null);
//        }
//    }
    /** Returns a Map of cart values to pass to the storeOrder service */
    public Map<String, Object> makeCartMap(LocalDispatcher dispatcher) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("requirementId", this.getDeliveryReqId());
        result.put("requirementTypeId", this.getDeliveryReqType());
        result.put("statusId", this.getDeliveryReqStatus());
        result.put("description", this.getDeliveryReqDescription());
        result.put("requirementStartDate", this.getRequirementStartDate());
//        result.put("requiredByDate", UtilDateTime.nowTimestamp());
        result.put("deliveryReqItems", this.makeDeliveryReqItems(Boolean.TRUE, dispatcher));
        
        return result;
    }
    
//    private void explodeItems(LocalDispatcher dispatcher) {
//        if (dispatcher == null) return;
//        synchronized (cartLines) {
//            List<ShoppingCartItem> cartLineItems = new LinkedList<ShoppingCartItem>(cartLines);
//            for (ShoppingCartItem item : cartLineItems) {
//                //Debug.logInfo("Item qty: " + item.getQuantity(), module);
//                try {
//                    item.explodeItem(this, dispatcher);
//                } catch (CartItemModifyException e) {
//                    Debug.logError(e, "Problem exploding item! Item not exploded.", module);
//                }
//            }
//        }
//    }
    
    public List<GenericValue> makeDeliveryReqItems(boolean replaceAggregatedId, LocalDispatcher dispatcher) {
        // do the explosion
    	// ...

        // now build the lines
        synchronized (cartLines) {
            List<GenericValue> result = FastList.newInstance();

            for (DeliveryRequirementItem item : cartLines) {
                if (UtilValidate.isEmpty(item.getDeliveryReqItemSeqId())) {
                    String deliveryReqItemSeqId = UtilFormatOut.formatPaddedNumber(nextItemSeq, 5);
                    item.setDeliveryReqItemSeqId(deliveryReqItemSeqId);
                    nextItemSeq++;
                } else {
                    try {
                        int thisSeqId = Integer.parseInt(item.getDeliveryReqItemSeqId());
                        if (thisSeqId > nextItemSeq) {
                            nextItemSeq = thisSeqId + 1;
                        }
                    } catch (NumberFormatException e) {
                        Debug.logError(e, module);
                    }
                }
                
                GenericValue deliveryReqItem = getDelegator().makeValue("OrderRequirementDelivery");
                deliveryReqItem.set("orderId", item.getOrderId());
                deliveryReqItem.set("description", item.getDescription());

                result.add(deliveryReqItem);
                // don't do anything with adjustments here, those will be added below in makeAllAdjustments
            }
            return result;
        }
    }
    
    /** Returns the number of items in the cart object. */
    public int size() {
        return cartLines.size();
    }
    
    /** Returns a Collection of items in the cart object. */
    public List<DeliveryRequirementItem> items() {
        List<DeliveryRequirementItem> result = FastList.newInstance();
        result.addAll(cartLines);
        return result;
    }

	
	/** Returns true if the user wishes to view the cart everytime an item is added. */
    public boolean viewCartOnAdd() {
        return viewCartOnAdd;
    }
    
    /** Returns true if the user wishes to view the cart everytime an item is added. */
    public void setViewCartOnAdd(boolean viewCartOnAdd) {
        this.viewCartOnAdd = viewCartOnAdd;
    }

	
	public int addItemToEnd(DeliveryRequirementItem item) throws CartItemModifyException {
		return addItem(cartLines.size(), item);
	}
	
	public int getItemIndex(DeliveryRequirementItem item) {
		return cartLines.indexOf(item);
	}
	
	public String getDelegatorName() {
		return delegatorName;
	}
	
	@Override
	public Iterator<DeliveryRequirementItem> iterator() {
		// TODO Auto-generated method stub
		return cartLines.iterator();
	}
	
	public String getDeliveryReqId() {
		return this.deliveryReqId;
	}

	public static String getModule() {
		return module;
	}

	public static String getResourceError() {
		return resource_error;
	}

	public String getDeliveryReqName() {
		return deliveryReqName;
	}

	public String getDeliveryReqStatus() {
		return deliveryReqStatus;
	}

	public String getDeliveryReqDescription() {
		return deliveryReqDescription;
	}

	public Timestamp getRequirementStartDate() {
		return requirementStartDate;
	}
	
	public Timestamp getRequiredByDate() {
		return requiredByDate;
	}

	public List<DeliveryRequirementItem> getCartLines() {
		return cartLines;
	}

	public GenericValue getUserLogin() {
		return userLogin;
	}

	public GenericValue getAutoUserLogin() {
		return autoUserLogin;
	}

	public void setDeliveryReqStatus(String deliveryReqStatus) {
		this.deliveryReqStatus = deliveryReqStatus;
	}

	public void setDelegator(Delegator delegator) {
		this.delegator = delegator;
	}

	public void setDelegatorName(String delegatorName) {
		this.delegatorName = delegatorName;
	}

	public void setReadOnlyCart(boolean readOnlyCart) {
		this.readOnlyCart = readOnlyCart;
	}

	public void setDeliveryReqId(String deliveryReqId) {
		this.deliveryReqId = deliveryReqId;
	}

	public void setDeliveryReqName(String deliveryReqName) {
		this.deliveryReqName = deliveryReqName;
	}

	public void setDeliveryReqDescription(String deliveryReqDescription) {
		this.deliveryReqDescription = deliveryReqDescription;
	}

	public void setRequirementStartDate(Timestamp requirementStartDate) {
		this.requirementStartDate = requirementStartDate;
	}

	public void setRequiredByDate(Timestamp requiredByDate) {
		this.requiredByDate = requiredByDate;
	}

	public void setUserLogin(GenericValue userLogin) {
		this.userLogin = userLogin;
	}

	public void setAutoUserLogin(GenericValue autoUserLogin) {
		this.autoUserLogin = autoUserLogin;
	}

	public void setCartLines(List<DeliveryRequirementItem> cartLines) {
		this.cartLines = cartLines;
	}
	
	
}
