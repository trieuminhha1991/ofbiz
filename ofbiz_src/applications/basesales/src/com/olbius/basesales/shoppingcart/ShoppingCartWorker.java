package com.olbius.basesales.shoppingcart;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartHelper;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.security.Security;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ShoppingCartWorker {
	public static String module = ShoppingCartWorker.class.getName();
    public static final String resource = "OrderUiLabels";
    public static final String resource_error = "OrderErrorUiLabels";
    
	public static String getCurrentCatalogId(Delegator delegator, GenericValue userLogin, GenericValue autoUserLogin, String productStoreId, String currentCatalogId) {
        String prodCatalogId = null;

        // first see if a new catalog was specified as a parameter
        prodCatalogId = currentCatalogId; // attribute CURRENT_CATALOG_ID
        
        // get it from the database
        if (prodCatalogId == null) {
            List<String> catalogIds = getCatalogIdsAvailable(delegator, userLogin, autoUserLogin, productStoreId);
            if (UtilValidate.isNotEmpty(catalogIds)) prodCatalogId = catalogIds.get(0);
        }

        // if (Debug.verboseOn()) Debug.logVerbose("[CatalogWorker.getCurrentCatalogId] Setting new catalog name: " + prodCatalogId, module);
        // CategoryWorker.setTrail(request, FastList.<String>newInstance()); _BREAD_CRUMB_TRAIL_
        return prodCatalogId;
    }
	
	public static List<String> getCatalogIdsAvailable(Delegator delegator, GenericValue userLogin, GenericValue autoUserLogin, String productStoreId) {
        List<GenericValue> partyCatalogs = getPartyCatalogs(delegator, userLogin, autoUserLogin);
        List<GenericValue> storeCatalogs = getStoreCatalogs(delegator, productStoreId);
        return getCatalogIdsAvailable(partyCatalogs, storeCatalogs);
    }
	
	public static List<GenericValue> getPartyCatalogs(Delegator delegator, GenericValue userLogin, GenericValue autoUserLogin) {
        if (userLogin == null) userLogin = autoUserLogin;
        if (userLogin == null) return null;
        String partyId = userLogin.getString("partyId");
        if (partyId == null) return null;
        return getPartyCatalogs(delegator, partyId);
    }

    public static List<GenericValue> getPartyCatalogs(Delegator delegator, String partyId) {
        if (delegator == null || partyId == null) {
            return null;
        }

        try {
            return EntityUtil.filterByDate(delegator.findByAnd("ProdCatalogRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "CUSTOMER"), UtilMisc.toList("sequenceNum", "prodCatalogId"), true), true);
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "Error looking up ProdCatalog Roles for party with id " + partyId, module);
        }
        return null;
    }

    public static List<GenericValue> getStoreCatalogs(Delegator delegator, String productStoreId) {
        try {
            return EntityUtil.filterByDate(delegator.findByAnd("ProductStoreCatalog", UtilMisc.toMap("productStoreId", productStoreId), UtilMisc.toList("sequenceNum", "prodCatalogId"), true), true);
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "Error looking up store catalogs for store with id " + productStoreId, module);
        }
        return null;
    }
    
    public static List<String> getCatalogIdsAvailable(List<GenericValue> partyCatalogs, List<GenericValue> storeCatalogs) {
        List<String> categoryIds = FastList.newInstance();
        List<GenericValue> allCatalogLinks = FastList.newInstance();
        if (partyCatalogs != null) allCatalogLinks.addAll(partyCatalogs);
        if (storeCatalogs != null) allCatalogLinks.addAll(storeCatalogs);

        if (allCatalogLinks.size() > 0) {
            for (GenericValue catalogLink: allCatalogLinks) {
                categoryIds.add(catalogLink.getString("prodCatalogId"));
            }
        }
        return categoryIds;
    }
    
    /** Update the items in the shopping cart. */
    public static Map<String, Object> modifyCart(Delegator delegator, LocalDispatcher dispatcher, ShoppingCart cart, Security security, GenericValue userLogin, 
    		List<Map<String, Object>> productList, boolean alwaysShowcart, boolean removeSelected, List<String> selectedItems, Locale locale, Boolean removeItemOut) {
        Map<String, Object> result = null;
        if (locale == null) {
            locale = cart.getLocale();
        }

        ArrayList<ShoppingCartItem> deleteList = new ArrayList<ShoppingCartItem>();
        ArrayList<ShoppingCartItem> processedList = new ArrayList<ShoppingCartItem>();
        ArrayList<Map<String, Object>> addList = new ArrayList<Map<String, Object>>();
        ArrayList<String> errorMsgs = new ArrayList<String>();

        BigDecimal oldQuantity = BigDecimal.ONE.negate();
        String oldDescription = "";
        BigDecimal oldPrice = BigDecimal.ONE.negate();

        if (cart.isReadOnlyCart()) {
            String errMsg = UtilProperties.getMessage(resource_error, "cart.cart_is_in_read_only_mode", cart.getLocale());
            errorMsgs.add(errMsg);
            result = ServiceUtil.returnError(errorMsgs);
            return result;
        }

        try {
	        for (Map<String, Object> productItem : productList) {
	        	String indexStr = (String) productItem.get("itemIndex");
	        	int index = -1;
	        	BigDecimal quantity = BigDecimal.ONE.negate();
	        	String itemDescription = "";
	        	String productId = (String) productItem.get("productId");
	        	String quantityUomId = (String) productItem.get("quantityUomId");
	        	
	        	if (UtilValidate.isNotEmpty(indexStr)) {
	        		index = Integer.parseInt(indexStr);
	        	}
	        	
	        	// get the cart item
	            ShoppingCartItem item = null;
	            if (index > -1) item = cart.findCartItem(index);
	        	
	            if (item == null) {
	            	item = cart.findCartItem(productId, null, null, null, null, null, null, UtilMisc.<String, Object>toMap("quantityUomId", quantityUomId), null, null, null, null, null, null);
	        	}
	            if (item == null) {
	            	// add item
	            	addList.add(productItem);
	        		continue;
            	}
	            
	            processedList.add(item);
	            
	        	// update
	        	
	            /* DELETE
	             * parameter parameterName.toUpperCase().startsWith("OPTION")
	             * Feature apply
	             * removeAdditionalProductFeatureAndAppl
	             * putAdditionalProductFeatureAndAppl
	             */
	            
	            /* parameterName.toUpperCase().startsWith("DESCRIPTION") */
	            if (UtilValidate.isNotEmpty(productItem.get("description"))) {
	            	itemDescription = (String) productItem.get("description"); // the quantString is actually the description if the field name starts with DESCRIPTION
	            }
	            
	            // parameterName.startsWith("reservStart") 
	            // parameterName.startsWith("reservLength")
	            // parameterName.startsWith("reservPersons")
	            
	            // parameterName.startsWith("shipBeforeDate")
	            Timestamp shipBeforeDate = null;
	            if (UtilValidate.isNotEmpty(productItem.get("shipBeforeDate"))) {
	            	shipBeforeDate = (Timestamp) productItem.get("shipBeforeDate");
	            }
	            
	            // parameterName.startsWith("shipAfterDate")
	            Timestamp shipAfterDate = null;
	            if (UtilValidate.isNotEmpty(productItem.get("shipAfterDate"))) {
	            	shipAfterDate = (Timestamp) productItem.get("shipAfterDate");
	            }
            	
	            // parameterName.startsWith("amount")
	            if (UtilValidate.isNotEmpty(productItem.get("amount"))) {
	            	BigDecimal amount = (BigDecimal) productItem.get("amount");
		            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
		                String errMsg = UtilProperties.getMessage(resource_error, "cart.amount_not_positive_number", cart.getLocale());
		                errorMsgs.add(errMsg);
		                result = ServiceUtil.returnError(errorMsgs);
		                return result;
		            }
		            item.setSelectedAmount(amount);
	            }
	            
	            // parameterName.startsWith("itemType")
	            if (UtilValidate.isNotEmpty(productItem.get("itemType"))) {
		            String itemType = (String) productItem.get("itemType");
		            item.setItemType(itemType);
	            }
	            
	            // quantity
	            if (UtilValidate.isNotEmpty(productItem.get("quantity"))) {
	            	quantity = (BigDecimal) productItem.get("quantity");
		            if (quantity.compareTo(BigDecimal.ZERO) < 0) {
		                String errMsg = UtilProperties.getMessage(resource_error, "cart.quantity_not_positive_number", cart.getLocale());
		                errorMsgs.add(errMsg);
		                result = ServiceUtil.returnError(errorMsgs);
		                return result;
		            }
	            }
	            
	            // perhaps we need to reset the ship groups' before and after dates based on new dates for the items
	            if (shipAfterDate != null || shipBeforeDate != null) {
	                cart.setShipGroupShipDatesFromItem(item);
	            }
	            
	            // parameterName.toUpperCase().startsWith("UPDATE")
	            if (quantity.compareTo(BigDecimal.ZERO) == 0) {
	                deleteList.add(item);
	            } else {
	                if (item != null) {
	                    try {
	                        // if, on a purchase order, the quantity has changed, get the new SupplierProduct entity for this quantity level.
	                        if (cart.getOrderType().equals("PURCHASE_ORDER")) {
	                            oldQuantity = item.getQuantity();
	                            if (oldQuantity.compareTo(quantity) != 0) {
	                                // save the old description and price, in case the user wants to change those as well
	                                oldDescription = item.getName();
	                                oldPrice = item.getBasePrice();
	
	
	                                GenericValue supplierProduct = cart.getSupplierProduct(item.getProductId(), quantity, dispatcher);
	
	                                if (supplierProduct == null) {
	                                    if ("_NA_".equals(cart.getPartyId())) {
	                                        // no supplier does not require the supplier product
	                                        item.setQuantity(quantity, dispatcher, cart);
	                                        item.setName(item.getProduct().getString("internalName"));
	                                    } else {
	                                        // in this case, the user wanted to purchase a quantity which is not available (probably below minimum)
	                                        String errMsg = UtilProperties.getMessage(resource_error, "cart.product_not_valid_for_supplier", cart.getLocale());
	                                        errMsg = errMsg + " (" + item.getProductId() + ", " + quantity + ", " + cart.getCurrency() + ")";
	                                        errorMsgs.add(errMsg);
	                                    }
	                                } else {
	                                    item.setSupplierProductId(supplierProduct.getString("supplierProductId"));
	                                    item.setQuantity(quantity, dispatcher, cart);
	                                    item.setBasePrice(supplierProduct.getBigDecimal("lastPrice"));
	                                    item.setName(ShoppingCartItem.getPurchaseOrderItemDescription(item.getProduct(), supplierProduct, cart.getLocale()));
	                                }
	                            }
	                        } else {
	                            BigDecimal minQuantity = ShoppingCart.getMinimumOrderQuantity(delegator, item.getBasePrice(), item.getProductId());
	                            if (quantity.compareTo(minQuantity) < 0) {
	                                quantity = minQuantity;
	                            }
	                            item.setQuantity(quantity, dispatcher, cart, true, false);
	                            cart.setItemShipGroupQty(item, quantity, 0);
	                        }
	                    } catch (CartItemModifyException e) {
	                        errorMsgs.add(e.getMessage());
	                    }
	                }
	            }
	            
	            // parameterName.toUpperCase().startsWith("DESCRIPTION")
	            if (!oldDescription.equals(itemDescription)) {
                    if (security.hasEntityPermission("ORDERMGR", "_CREATE", userLogin)) {
                        if (item != null) {
                            item.setName(itemDescription);
                        }
                    }
                }
	            
	            // parameterName.toUpperCase().startsWith("PRICE")
	            NumberFormat pf = NumberFormat.getCurrencyInstance(locale);
                String tmpQuantity = pf.format(quantity);
                String tmpOldPrice = pf.format(oldPrice);
                if (!tmpOldPrice.equals(tmpQuantity)) {
                    if (security.hasEntityPermission("ORDERMGR", "_CREATE", userLogin)) {
                        if (item != null) {
                            item.setBasePrice(quantity); // this is quantity because the parsed number variable is the same as quantity
                            item.setDisplayPrice(quantity); // or the amount shown the cart items page won't be right
                            item.setIsModifiedPrice(true); // flag as a modified price
                        }
                    }
                }
                
                // parameterName.toUpperCase().startsWith("DELETE")
                if (UtilValidate.isNotEmpty(productItem.get("isDelete"))) {
                	boolean isDelete = (Boolean) productItem.get("isDelete");
                    if (isDelete) deleteList.add(cart.findCartItem(index));
                }
	        }

	        // get a list of the items to delete
	        if (removeSelected) {
	            for (int si = 0; si < selectedItems.size(); si++) {
	                String indexStr = selectedItems.get(si);
	                ShoppingCartItem item = null;
	                try {
	                    int index = Integer.parseInt(indexStr);
	                    item = cart.findCartItem(index);
	                } catch (Exception e) {
	                    Debug.logWarning(e, UtilProperties.getMessage(resource_error, "OrderProblemsGettingTheCartItemByIndex", cart.getLocale()));
	                }
	                if (item != null) {
	                    deleteList.add(item);
	                }
	            }
	        }
	        
	        if (removeItemOut) {
	        	// remove item not in list product
	        	for (ShoppingCartItem item : cart.items()) {
	        		if (!processedList.contains(item)) {
	        			deleteList.add(item);
	        		}
	        	}
	        }
	
	        for (ShoppingCartItem item : deleteList) {
	            int itemIndex = cart.getItemIndex(item);
	
	            if (Debug.infoOn())
	                Debug.logInfo("Removing item index: " + itemIndex, module);
	            try {
	                cart.removeCartItem(itemIndex, dispatcher);
	            } catch (CartItemModifyException e) {
	                result = ServiceUtil.returnError(new ArrayList<String>());
	                errorMsgs.add(e.getMessage());
	            }
	        }
	        
	        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
	        String productCatalogId = getCurrentCatalogId(delegator, userLogin, null, cart.getProductStoreId(), null);
	        Map<String, Object> paramMap = new FastMap<String, Object>();
			if (UtilValidate.isNotEmpty(cart.getDefaultItemDeliveryDate())) {
	        	paramMap.put("itemDesiredDeliveryDate", cart.getDefaultItemDeliveryDate());
	        	paramMap.put("useAsDefaultDesiredDeliveryDate", "true");
	        } else {
	        	paramMap.put("itemDesiredDeliveryDate", "");
	        }
	        for (Map<String, Object> item : addList) {
	        	String productId = (String) item.get("productId");
	        	BigDecimal price = null;
	        	BigDecimal quantity = (BigDecimal) item.get("quantity");
	        	Timestamp shipBeforeDate = (Timestamp) item.get("shipBeforeDate");
	        	Timestamp shipAfterDate = (Timestamp) item.get("shipAfterDate");
	        	String quantityUomId = (String) item.get("quantityUomId");
	        	paramMap.put("quantityUomId", quantityUomId);
	        	Map<String, Object> resultAddItem = cartHelper.addToCart(productCatalogId, null, null, productId, null, null, null, price, null, quantity, 
		        		null, null, null, null, null, shipBeforeDate, shipAfterDate, null, null, paramMap, null, Boolean.FALSE);
	        	if (ServiceUtil.isError(resultAddItem)) {
	        		return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultAddItem));
	        	}
	        }
	
	        if (alwaysShowcart) {
	            cart.setViewCartOnAdd(true);
	        } else {
	            cart.setViewCartOnAdd(false);
	        }
	
	        // Promotions are run again.
	        // ProductPromoWorker.doPromotions(cart, dispatcher);
	
	        if (errorMsgs.size() > 0) {
	            result = ServiceUtil.returnError(errorMsgs);
	            return result;
	        }
        
        } catch (NumberFormatException nfe) {
            Debug.logWarning(nfe, UtilProperties.getMessage(resource_error, "OrderCaughtNumberFormatExceptionOnCartUpdate", cart.getLocale()), module);
        } catch (Exception e) {
            Debug.logWarning(e, UtilProperties.getMessage(resource_error, "OrderCaughtExceptionOnCartUpdate", cart.getLocale()), module);
        }

        result = ServiceUtil.returnSuccess();
        return result;
    }
    
    public List<GenericValue> makeOrderItemAttributes(ShoppingCart cart, String orderItemSeqId) {
        return makeOrderItemAttributes(cart, null, orderItemSeqId);
    }

    public List<GenericValue> makeOrderItemAttributes(ShoppingCart cart, String orderId, String orderItemSeqId) {
    	List<ShoppingCartItem> cartLines = cart.items();
    	
        // now build order item attributes
        synchronized (cartLines) {
            List<GenericValue> result = FastList.newInstance();

            for (ShoppingCartItem item : cartLines) {
            	
                Map<String, String> orderItemAttributes = item.getOrderItemAttributes();
                for (String key : orderItemAttributes.keySet()) {
                    String value = orderItemAttributes.get(key);

                    GenericValue orderItemAttribute = cart.getDelegator().makeValue("OrderItemAttribute");
                    if (UtilValidate.isNotEmpty(orderId)) {
                        orderItemAttribute.set("orderId", orderId);
                    }

                    orderItemAttribute.set("orderItemSeqId", item.getOrderItemSeqId());
                    orderItemAttribute.set("attrName", key);
                    orderItemAttribute.set("attrValue", value);

                    result.add(orderItemAttribute);
                }
            }
            return result;
        }
    }
    
    public static List<Map<String, Object>> getOrderItemsInfo(ShoppingCart cart) {
        return getOrderItemsInfo(cart, false, false, null);
    }

    public static List<Map<String, Object>> getOrderItemsInfo(ShoppingCart cart, boolean explodeItems, boolean replaceAggregatedId, LocalDispatcher dispatcher) {
    	List<ShoppingCartItem> cartLines = cart.items();
    	
    	// now build the lines
        synchronized (cartLines) {
            List<Map<String, Object>> result = FastList.newInstance();
            //Timestamp nowStamp = UtilDateTime.nowTimestamp();
            for (ShoppingCartItem item : cartLines) {
            	if(item.getQuantity().compareTo(new BigDecimal(0)) < 0){
            		continue;
            	}
                
                // the initial status for all item types
                String initialStatus = "ITEM_CREATED";
                String status = item.getStatusId();
                if (status == null) {
                    status = initialStatus;
                }
                
                //check for aggregated products
                String aggregatedInstanceId = null;
                if (replaceAggregatedId && UtilValidate.isNotEmpty(item.getConfigWrapper())) {
                    aggregatedInstanceId = cart.getAggregatedInstanceId(item, dispatcher);
                }

                Map<String, Object> orderItem = new HashMap<String, Object>();
                orderItem.put("orderItemSeqId", item.getOrderItemSeqId());
                orderItem.put("externalId", item.getExternalId());
                orderItem.put("orderItemTypeId", item.getItemType());
                if (item.getItemGroup() != null) orderItem.put("orderItemGroupSeqId", item.getItemGroup().getGroupNumber());
                orderItem.put("productId", UtilValidate.isNotEmpty(aggregatedInstanceId) ? aggregatedInstanceId : item.getProductId());
                orderItem.put("supplierProductId", item.getSupplierProductId());
                orderItem.put("prodCatalogId", item.getProdCatalogId());
                orderItem.put("productCategoryId", item.getProductCategoryId());
                orderItem.put("quantity", item.getQuantity());
                orderItem.put("selectedAmount", item.getSelectedAmount());
                orderItem.put("unitPrice", item.getBasePrice());
                orderItem.put("unitListPrice", item.getListPrice());
                orderItem.put("isModifiedPrice",item.getIsModifiedPrice() ? "Y" : "N");
                orderItem.put("isPromo", item.getIsPromo() ? "Y" : "N");

                orderItem.put("shoppingListId", item.getShoppingListId());
                orderItem.put("shoppingListItemSeqId", item.getShoppingListItemSeqId());

                orderItem.put("itemDescription", item.getName());
                orderItem.put("comments", item.getItemComment());
                orderItem.put("estimatedDeliveryDate", item.getDesiredDeliveryDate());
                orderItem.put("correspondingPoId", cart.getPoNumber());
                orderItem.put("quoteId", item.getQuoteId());
                orderItem.put("quoteItemSeqId", item.getQuoteItemSeqId());
                orderItem.put("statusId", status);

                orderItem.put("shipBeforeDate", item.getShipBeforeDate());
                orderItem.put("shipAfterDate", item.getShipAfterDate());
                orderItem.put("estimatedShipDate", item.getEstimatedShipDate());
                orderItem.put("cancelBackOrderDate", item.getCancelBackOrderDate());
                if (cart.getUserLogin() != null) {
                    orderItem.put("changeByUserLoginId", cart.getUserLogin().get("userLoginId"));
                }

                String fromInventoryItemId = (String) item.getAttribute("fromInventoryItemId");
                if (fromInventoryItemId != null) {
                    orderItem.put("fromInventoryItemId", fromInventoryItemId);
                }
                
                // TODOCHANGE add new attribute: "quantityUomId", "alternativeQuantity", "alternativeUnitPrice", "expireDate"
                String quantityUomId = (String) item.getAttribute("quantityUomId");
                if (quantityUomId != null) {
                    orderItem.put("quantityUomId", quantityUomId);
                }
                BigDecimal alternativeQuantity = item.getAlternativeQuantity();
                if (alternativeQuantity != null) {
                	orderItem.put("alternativeQuantity", alternativeQuantity);
                }
                BigDecimal alternativeUnitPrice = item.getAlternativeUnitPrice();
                if (alternativeUnitPrice != null) {
                	orderItem.put("alternativeUnitPrice", alternativeUnitPrice);
                }
                Timestamp expireDate = (Timestamp) item.getAttribute("expireDate");
                if (expireDate != null) {
                	orderItem.put("expireDate", expireDate);
                }
                
                /*if (item.getOrderItemAttribute("idUPCA") != null) {
                	orderItem.put("idUPCA", item.getOrderItemAttribute("idUPCA"));
                }*/
                /*if (item.getAttribute("idUPCA") != null) {
                	orderItem.put("idUPCA", item.getAttribute("idUPCA"));
                }*/
                if (item.getAttribute("idEAN") != null) {
                	orderItem.put("idEAN", item.getAttribute("idEAN"));
                }
                
                //edit by dunglv for pos
                //check quantity > 0 add to order item
                if (((BigDecimal) orderItem.get("alternativeQuantity")).compareTo(BigDecimal.ZERO) > 0){
                	result.add(orderItem);
                }
                
                // don't do anything with adjustments here, those will be added below in makeAllAdjustments
            }
            return result;
        }
    }
}
