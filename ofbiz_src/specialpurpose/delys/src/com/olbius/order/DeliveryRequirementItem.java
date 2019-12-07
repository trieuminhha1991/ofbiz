package com.olbius.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.ItemNotFoundException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.service.LocalDispatcher;

@SuppressWarnings("serial")
public class DeliveryRequirementItem implements java.io.Serializable {
	public static String module = DeliveryRequirementItem.class.getName();
	public static final String resource = "DelysAdminUiLabels";
	public static final String resource_error = "DelysAdminUiLabels";
	
	private transient Delegator delegator = null;
	private String delegatorName = null;
	private Locale locale = null;
	private String orderId = null;
	private GenericValue order = null;
	private String description = null;
	private String deliveryReqItemSeqId = null;
	
	public DeliveryRequirementItem(DeliveryRequirementItem item) {
		this.delegator = item.getDelegator();
		this.delegatorName = item.getDelegatorName();
		this.locale = item.getLocale();
		this.order = item.getOrder();
		this.orderId = item.getOrderId();
		this.description = item.getDescription();
	}
	
	protected DeliveryRequirementItem(Delegator delegator, GenericValue order, String description) {
		this.delegator = delegator;
		this.order = order;
		this.orderId = order.getString("orderId");
		this.description = description;
	}
	
	protected DeliveryRequirementItem(String deliveryReqItemSeqId, String orderId, String description) {
		this.deliveryReqItemSeqId = deliveryReqItemSeqId;
		this.orderId = orderId;
		this.description = description;
	}
	
	protected DeliveryRequirementItem(Delegator delegator, GenericValue order, String description, Locale locale) {
		this.delegator = delegator;
		this.order = order;
		this.orderId = order.getString("orderId");
		this.description = description;
		this.locale = locale;
	}
	
	public static DeliveryRequirementItem makeDeliveryRequirementItem(Integer cartLocation, String orderId, String description, 
			LocalDispatcher dispatcher, DeliveryRequirementCart cart) throws CartItemModifyException, ItemNotFoundException {
		Delegator delegator = cart.getDelegator();
		GenericValue order = null;
		
		try {
			order = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), true);
		} catch (GenericEntityException e) {
			Debug.logWarning(e.toString(), module);
		}
		
		if (order == null) {
			Map<String, Object> messageMap = UtilMisc.<String, Object> toMap("orderId", orderId);
			
			String excMsg = UtilProperties.getMessage(resource_error, "DAOrderNotFound", messageMap, cart.getLocale());
			
			Debug.logWarning(excMsg, module);
			throw new ItemNotFoundException(excMsg);
		}
		
		DeliveryRequirementItem newItem = new DeliveryRequirementItem(delegator, order, description, cart.getLocale());
		
		// add to cart before setting quantity so that we can get order total, etc
        if (cartLocation == null) {
            cart.addItemToEnd(newItem);
        } else {
            cart.addItem(cartLocation.intValue(), newItem);
        }
        
//        try {
//        } catch (CartItemModifyException e) {
//            cart.removeCartItem(cart.getItemIndex(newItem), dispatcher);
//            throw e;
//        }
        
        return newItem;
	}
	
	public static GenericValue findOrder(Delegator delegator, boolean skipOrderChecks, String orderId, Locale locale) throws CartItemModifyException, ItemNotFoundException {
		GenericValue order = null;
		
		try {
			order = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), true);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (order == null) {
			Map<String, Object> messageMap = UtilMisc.<String, Object> toMap("orderId", orderId);
			String excMsg = UtilProperties.getMessage(resource_error, "DAOrderNotFound", messageMap, locale);
			
			Debug.logWarning(excMsg, module);
			throw new ItemNotFoundException(excMsg);
		}
		
		return order;
	}
	
	public boolean equals(String orderId) {
		if (orderId == null || this.orderId == null) {
			return false;
		}
		if (!this.orderId.equals(orderId)) {
			return false;
		}
		return true;
	}
	
//	public void explodeItem(ShoppingCart cart, LocalDispatcher dispatcher) throws CartItemModifyException {
////        BigDecimal baseQuantity = this.getQuantity();
//        int thisIndex = cart.items().indexOf(this);
//        List<DeliveryRequirementItem> newItems = new ArrayList<DeliveryRequirementItem>();
//
//        if (baseQuantity.compareTo(BigDecimal.ONE) > 0) {
//            for (int i = 1; i < baseQuantity.intValue(); i++) {
//                // clone the item
//                ShoppingCartItem item = new ShoppingCartItem(this);
//
//                // set the new item's quantity
//                item.setQuantity(BigDecimal.ONE, dispatcher, cart, false);
//
//                // now copy/calc the adjustments
//                Debug.logInfo("Clone's adj: " + item.getAdjustments(), module);
//                if (UtilValidate.isNotEmpty(item.getAdjustments())) {
//                    List<GenericValue> adjustments = UtilMisc.makeListWritable(item.getAdjustments());
//                    for (GenericValue adjustment: adjustments) {
//
//                        if (adjustment != null) {
//                            item.removeAdjustment(adjustment);
//                            GenericValue newAdjustment = GenericValue.create(adjustment);
//                            BigDecimal adjAmount = newAdjustment.getBigDecimal("amount");
//
//                            // we use != because adjustments can be +/-
//                            if (adjAmount != null && adjAmount.compareTo(BigDecimal.ZERO) != 0)
//                                newAdjustment.set("amount", adjAmount.divide(baseQuantity, generalRounding));
//                            Debug.logInfo("Cloned adj: " + newAdjustment, module);
//                            item.addAdjustment(newAdjustment);
//                        } else {
//                            Debug.logInfo("Clone Adjustment is null", module);
//                        }
//                    }
//                }
//                newItems.add(item);
//            }
//
//            // set this item's quantity
//            this.setQuantity(BigDecimal.ONE, dispatcher, cart, false);
//
//            Debug.logInfo("BaseQuantity: " + baseQuantity, module);
//            Debug.logInfo("Item's Adj: " + this.getAdjustments(), module);
//
//            // re-calc this item's adjustments
//            if (UtilValidate.isNotEmpty(this.getAdjustments())) {
//                List<GenericValue> adjustments = UtilMisc.makeListWritable(this.getAdjustments());
//                for (GenericValue adjustment: adjustments) {
//
//                    if (adjustment != null) {
//                        this.removeAdjustment(adjustment);
//                        GenericValue newAdjustment = GenericValue.create(adjustment);
//                        BigDecimal adjAmount = newAdjustment.getBigDecimal("amount");
//
//                        // we use != becuase adjustments can be +/-
//                        if (adjAmount != null && adjAmount.compareTo(BigDecimal.ZERO) != 0)
//                            newAdjustment.set("amount", adjAmount.divide(baseQuantity, generalRounding));
//                        Debug.logInfo("Updated adj: " + newAdjustment, module);
//                        this.addAdjustment(newAdjustment);
//                    }
//                }
//            }
//
//            // add the cloned item(s) to the cart
//            for (ShoppingCartItem sci : newItems) {
//                cart.addItem(thisIndex, sci);
//            }
//        }
//    }
	
	/** Returns the item's orderId. */
    public String getOrderId() {
        return orderId;
    }
    
    /** Returns the item's description. */
    public String getDescription() {
    	return description;
    }

	public static String getModule() {
		return module;
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

	public GenericValue getOrder() {
		return order;
	}
	
	public String getDelegatorName() {
		return delegatorName;
	}

	public String getDeliveryReqItemSeqId() {
		return deliveryReqItemSeqId;
	}

	public void setDeliveryReqItemSeqId(String deliveryReqItemSeqId) {
		this.deliveryReqItemSeqId = deliveryReqItemSeqId;
	}
}
