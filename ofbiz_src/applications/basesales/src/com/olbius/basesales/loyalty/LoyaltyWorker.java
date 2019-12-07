package com.olbius.basesales.loyalty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.product.product.ProductSearch;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basesales.order.OrderReadHelper;

import javolution.util.FastList;
import javolution.util.FastSet;

public class LoyaltyWorker {
	public static final String module = LoyaltyWorker.class.getName();
    public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
    
    private ShoppingCart cart;
    private Delegator delegator;
    private LocalDispatcher dispatcher;
    private Locale locale;
	private String orderId;
    private String returnId;
    private String isReturnOrder; //Apply for sales order if isReturnOrder = 'N' and return order if isReturnOrder = 'Y'
    private GenericValue userLogin;
    
    Timestamp nowTimestamp;
	BigDecimal totalPoint = BigDecimal.ZERO;
	
	public LoyaltyWorker(Delegator delegator, LocalDispatcher dispatcher, Locale locale){
    	this.delegator = delegator;
    	this.dispatcher = dispatcher;
    	this.locale = locale;
    }
    
	public String getIsReturnOrder() {
		return isReturnOrder;
	}

	public void setIsReturnOrder(String isReturnOrder) {
		this.isReturnOrder = isReturnOrder;
	}

    public GenericValue getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(GenericValue userLogin) {
		this.userLogin = userLogin;
	}

	public String getReturnId() {
		return returnId;
	}

	public void setReturnId(String returnId) {
		this.returnId = returnId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Delegator getDelegator() {
		return delegator;
	}

	public void setDelegator(Delegator delegator) {
		this.delegator = delegator;
	}

	public LocalDispatcher getDispatcher() {
		return dispatcher;
	}

	public void setDispatcher(LocalDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}
	
	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public ShoppingCart getCart() {
		return cart;
	}

	public void setCart(ShoppingCart cart) {
		this.cart = cart;
	}
	
	public BigDecimal getTotalPointOrder(){
		return totalPoint;
	}
	
	public Map<String, Object> getInformationOrder() throws GenericEntityException{
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String partyId = null;
		String productStoreId = null;
		if (UtilValidate.isNotEmpty(cart)){
			partyId = cart.getPartyId();
			productStoreId = cart.getProductStoreId();
		} else {
			if (UtilValidate.isNotEmpty(orderId)){
				OrderReadHelper orderHelper = new OrderReadHelper(delegator, orderId);
				partyId = orderHelper.getPlacingParty().getString("partyId");
				productStoreId = orderHelper.getProductStoreId();
			}else if(UtilValidate.isNotEmpty(returnId)){
				GenericValue returnHeader = delegator.findOne("ReturnHeader", UtilMisc.toMap("returnId", returnId), false);
				partyId = returnHeader.getString("fromPartyId");
				productStoreId = returnHeader.getString("productStoreId");
			}
		}
		
		if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(productStoreId)){
			successResult.put("partyId", partyId);
			successResult.put("productStoreId", productStoreId);
		} else {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSCanNotGetInformationOrder", locale));
		}
		
		return successResult;
	}
	
	public BigDecimal getTotalPointCustomer() throws GenericEntityException{
		Map<String, Object> info = getInformationOrder();
		String partyId = (String) info.get("partyId");
		String productStoreId = (String) info.get("productStoreId");
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator,(String) userLogin.get("userLoginId"));
		GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
		String payToPartyId = "";
		if (UtilValidate.isNotEmpty(productStore)){
			payToPartyId = productStore.getString("payToPartyId");
		}
		
		if (UtilValidate.isEmpty(organizationPartyId)&&!payToPartyId.equals("")){
			organizationPartyId = payToPartyId;
		}
		BigDecimal totalPointCustomer = BigDecimal.ZERO;
		
		List<GenericValue> listLoyaltyPoint = delegator.findByAnd("LoyaltyPoint", UtilMisc.toMap("partyId", partyId, "productStoreId", productStoreId, 
				"ownerPartyId", organizationPartyId), UtilMisc.toList("loyaltyPointId"), false);
		
		if (UtilValidate.isNotEmpty(listLoyaltyPoint)){
			totalPointCustomer = totalPointCustomer.add(listLoyaltyPoint.get(0).getBigDecimal("point"));
		}
		
		return totalPointCustomer;
	}
	
	public void doLoyalties() throws GeneralException {
		nowTimestamp = UtilDateTime.nowTimestamp();
		Map<String, Object> info = getInformationOrder();
		String partyId = (String) info.get("partyId");
		String productStoreId = (String) info.get("productStoreId");
         
        List<GenericValue> loyaltyList = getListLoyaltyApply(productStoreId, partyId);
        
        runLoyalties(loyaltyList);
        
        List<GenericValue> loyaltyForRateList = delegator.findByAnd("ProductStoreLoyaltyApplFilter", UtilMisc.toMap("productStoreId", productStoreId, 
        		"loyaltyTypeId", "RATING"), UtilMisc.toList("sequenceNum"), false);
        loyaltyForRateList = EntityUtil.filterByDate(loyaltyForRateList, nowTimestamp);
        //rating customer
        runLoyalties(loyaltyForRateList);
	}
	
	public void runLoyalties(List<GenericValue> loyaltyList) throws GeneralException{
        long maxIterations = 1000;
        long numberOfIterations = 0;
        boolean cartChanged = true;
        
        while (cartChanged) {
            cartChanged = false;
            numberOfIterations++;
            if (numberOfIterations > maxIterations) {
                Debug.logError("ERROR: While calculating loyalty the loyalty rules where run more than " + maxIterations + " times, so the calculation has been ended. This should generally never happen unless you have bad rule definitions.", module);
                break;
            }

            for (GenericValue loyalty : loyaltyList) {
                String loyaltyId = loyalty.getString("loyaltyId");
                List<GenericValue> loyaltyRules = delegator.findByAnd("LoyaltyRule", UtilMisc.toMap("loyaltyId", loyaltyId), UtilMisc.toList("loyaltyRuleId"), false);
                if (UtilValidate.isNotEmpty(loyaltyRules)) {
                    try {
                        if (runLoyaltyRules(cartChanged, loyalty, loyaltyRules)) {
                            cartChanged = true;
                        }
                    } catch (RuntimeException e) {
                        throw new GeneralException("Error running loyalty with ID [" + loyaltyId + "]", e);
                    }
                }
            }
        }
	}

	public List<GenericValue> getListLoyaltyApply(String productStoreId, String partyId) throws GenericEntityException {
		List<GenericValue> loyaltyApplyList = FastList.newInstance();
        
        if (productStoreId.equals("")) {
            Debug.logWarning(UtilProperties.getMessage(resource_error,"BSOrderNoStoreFoundWithIdNotDoingLoyalty", UtilMisc.toMap("productStoreId",productStoreId), cart.getLocale()), module);
            return loyaltyApplyList;
        }
        
        //get all role type of party
        List<GenericValue> partyRole = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", partyId), null, false);
        List<String> listRoleType = FastList.newInstance();
        if (UtilValidate.isNotEmpty(partyRole)){
        	for (GenericValue role : partyRole){
            	listRoleType.add(role.getString("roleTypeId"));
            }
        }

        try {
        	List<GenericValue> loyaltyStoreList = delegator.findByAnd("ProductStoreLoyaltyApplFilter", UtilMisc.toMap("productStoreId", productStoreId, "loyaltyTypeId", "POINT"), UtilMisc.toList("sequenceNum"), false);
        	loyaltyStoreList = EntityUtil.filterByDate(loyaltyStoreList, nowTimestamp);
        	
            if (UtilValidate.isEmpty(loyaltyStoreList)) {
                if (Debug.verboseOn()) Debug.logVerbose("Not doing loyalty, none applied to store with ID " + productStoreId, module);
            } else {
            	for (GenericValue loyaltyStore : loyaltyStoreList){
            		List<GenericValue> loyaltyRoleList = delegator.findByAnd("LoyaltyRoleTypeAppl", UtilMisc.toMap("loyaltyId", loyaltyStore.getString("loyaltyId")), null, false);
            		loyaltyRoleList = EntityUtil.filterByDate(loyaltyRoleList, nowTimestamp);
            		if (UtilValidate.isNotEmpty(loyaltyRoleList)){
            			for (GenericValue loyaltyRole : loyaltyRoleList){
            				String roleTypeId = loyaltyRole.getString("roleTypeId");
            				if (listRoleType.contains(roleTypeId)){
            					loyaltyApplyList.add(loyaltyStore);
            					break;
            				}
            			}
            		} else {
            			loyaltyApplyList.add(loyaltyStore);
            		}
            	}
            }
            
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up loyalty data while doing loyalty", module);
        }
        return loyaltyApplyList;
	}
	
	public boolean runLoyaltyRules(boolean cartChanged, GenericValue loyalty, List<GenericValue> loyaltyRules) throws GenericEntityException {
        Iterator<GenericValue> loyaltyRulesIter = loyaltyRules.iterator();
        while (loyaltyRulesIter != null && loyaltyRulesIter.hasNext()) {
            GenericValue loyaltyRule = loyaltyRulesIter.next();
            boolean performActions = true;
            List<GenericValue> loyaltyConds = delegator.findByAnd("LoyaltyCondition", UtilMisc.toMap("loyaltyId", loyalty.get("loyaltyId")), UtilMisc.toList("loyaltyCondSeqId"), true);
            loyaltyConds = EntityUtil.filterByAnd(loyaltyConds, UtilMisc.toMap("loyaltyRuleId", loyaltyRule.get("loyaltyRuleId")));

            Iterator<GenericValue> loyaltyCondIter = UtilMisc.toIterator(loyaltyConds);
            BigDecimal rate = BigDecimal.ONE;
            List<BigDecimal> listRate = FastList.newInstance();
            while (loyaltyCondIter != null && loyaltyCondIter.hasNext()) {
                GenericValue loyaltyCond = loyaltyCondIter.next();
                Map<String, Object> condResult = checkCondition(loyaltyCond);
                boolean result = (boolean) condResult.get("result");
                BigDecimal count = (BigDecimal) condResult.get("count");
                if (result == false) {
                    performActions = false;
                    break;
                } else {
                	if (UtilValidate.isNotEmpty(count)){
                		listRate.add(count);
                	}
                }
            }
            
            if (UtilValidate.isNotEmpty(listRate)){
            	rate = listRate.get(0);
            	for (BigDecimal item : listRate){
            		if (item.compareTo(rate) == -1){
            			rate = item;
            		}
            	}
            }
            
            if (performActions) {
                List<GenericValue> loyaltyActions = loyaltyRule.getRelated("LoyaltyAction", null, UtilMisc.toList("loyaltyActionSeqId"), true);
                Iterator<GenericValue> loyaltyActionIter = UtilMisc.toIterator(loyaltyActions);
                while (loyaltyActionIter != null && loyaltyActionIter.hasNext()) {
                    GenericValue loyaltyAction = loyaltyActionIter.next();
                    try {
                    	if (UtilValidate.isNotEmpty(cart)){
                    		getPointAction(loyaltyAction, rate);
                    	} else {
                    		performAction(loyaltyAction, rate);
                    	}
                    } catch (CartItemModifyException e) {
                        Debug.logError(e, "Error modifying the cart while performing loyalty action [" + loyaltyAction.getPrimaryKey() + "]", module);
                    }
                }
            }
        }

        return cartChanged;
    }
	
	public Map<String, Object> checkCondition(GenericValue loyaltyCond) throws GenericEntityException{
		String condValue = loyaltyCond.getString("condValue");
        String inputParamEnumId = loyaltyCond.getString("inputParamEnumId");
        String operatorEnumId = loyaltyCond.getString("operatorEnumId");
        String returnOrder = loyaltyCond.getString("isReturnOrder");
        Map<String, Object> info = getInformationOrder();
		String partyId = (String) info.get("partyId");
		String productStoreId = (String) info.get("productStoreId");
		
		BigDecimal count = null;
		boolean result = false;
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
        
        if (returnOrder.equals(isReturnOrder)){
        	Integer compareBase = null;
            if ("LPIP_PRODUCT_TOTAL".equals(inputParamEnumId)) {
            	if (UtilValidate.isNotEmpty(condValue)) {
            		BigDecimal amountNeeded = new BigDecimal(condValue);
                    BigDecimal amountAvailable = BigDecimal.ZERO;

                    Set<String> productIds = getLoyaltyRuleCondProductIds(loyaltyCond);
                    
                    if (UtilValidate.isNotEmpty(cart)){
                    	List<ShoppingCartItem> lineOrderedByBasePriceList = cart.getLineListOrderedByBasePrice(false);
                        for (ShoppingCartItem cartItem : lineOrderedByBasePriceList) {
                            String parentProductId = cartItem.getParentProductId();
                            if (!cartItem.getIsPromo()&&(productIds.contains(cartItem.getProductId()) || (parentProductId != null && productIds.contains(parentProductId)))) {
                                BigDecimal itemSubTotal = cartItem.getItemSubTotal();
                            	if (loyaltyCond.containsKey("usePriceWithTax") && "Y".equals(loyaltyCond.getString("usePriceWithTax"))) {
                            		List<ShoppingCart.CartShipInfo> csis = cart.getShipGroups();
                            		for (ShoppingCart.CartShipInfo csi : csis) {
                            			ShoppingCart.CartShipInfo.CartShipItemInfo itemInfo = csi.shipItemInfo.get(cartItem);
                                        for (GenericValue taxAdj : itemInfo.itemTaxAdj) {
                                        	itemSubTotal = itemSubTotal.add(taxAdj.getBigDecimal("amount"));
                                        }
                            		}
                            	}
                            	amountAvailable = amountAvailable.add(itemSubTotal);
                            }
                        }
                    } else {
                    	OrderReadHelper orderHelper = new OrderReadHelper(delegator, orderId);
                    	List<GenericValue> orderItems = orderHelper.getOrderItems();
                    	for (GenericValue item : orderItems){
                    		GenericValue parentProduct = ProductWorker.getParentProduct(item.getString("productId"), delegator);
                    		String parentProductId = null;
                    		if (UtilValidate.isNotEmpty(parentProduct)){
                    			parentProductId = parentProduct.getString("productId");
                    		}
                    		if ((productIds.contains(item.getString("productId"))||(parentProductId != null && productIds.contains(parentProductId)))
                    				&& item.getString("isPromo").equals("N")){
                    			BigDecimal itemSubTotal = orderHelper.getOrderItemSubTotal(item);
                    			if (loyaltyCond.containsKey("usePriceWithTax") && "Y".equals(loyaltyCond.getString("usePriceWithTax"))) {
                    				itemSubTotal = itemSubTotal.add(orderHelper.getOrderItemTax(item));
                            	}
                            	amountAvailable = amountAvailable.add(itemSubTotal);
                    		}
                    	}
                    }

                    compareBase = Integer.valueOf(amountAvailable.compareTo(amountNeeded));
            	}
            } else if ("LPIP_PRODUCT_QUANT".equals(inputParamEnumId)) {
            	if (UtilValidate.isNotEmpty(condValue)) {
            		operatorEnumId = "LPC_EQ";
                    BigDecimal quantityNeeded = new BigDecimal(condValue);
                    BigDecimal quantityAvailable = BigDecimal.ZERO;

                    Set<String> productIds = getLoyaltyRuleCondProductIds(loyaltyCond);
                    
                    if (UtilValidate.isNotEmpty(cart)){
                    	List<ShoppingCartItem> lineOrderedByBasePriceList = cart.getLineListOrderedByBasePrice(false);
                        Iterator<ShoppingCartItem> lineOrderedByBasePriceIter = lineOrderedByBasePriceList.iterator();
                        while (quantityNeeded.compareTo(BigDecimal.ZERO) > 0 && lineOrderedByBasePriceIter.hasNext()) {
                            ShoppingCartItem cartItem = lineOrderedByBasePriceIter.next();
                            String parentProductId = cartItem.getParentProductId();
                            if (!cartItem.getIsPromo()&&(productIds.contains(cartItem.getProductId()) || (parentProductId != null && productIds.contains(parentProductId)))) {
                            	quantityAvailable = cartItem.getQuantity();
                            }
                        }
                    } else {
                    	OrderReadHelper orderHelper = new OrderReadHelper(delegator, orderId);
                    	List<GenericValue> orderItems = orderHelper.getOrderItems();
                    	for (GenericValue item : orderItems){
                    		GenericValue parentProduct = ProductWorker.getParentProduct(item.getString("productId"), delegator);
                    		String parentProductId = null;
                    		if (UtilValidate.isNotEmpty(parentProduct)){
                    			parentProductId = parentProduct.getString("productId");
                    		}
                    		if ((productIds.contains(item.getString("productId"))||(parentProductId != null && productIds.contains(parentProductId)))
                    				&& item.getString("isPromo").equals("N")){
                    			quantityAvailable = item.getBigDecimal("quantity");
                    		}
                    	}
                    }
                    
                    compareBase = Integer.valueOf(quantityAvailable.compareTo(quantityNeeded));
            	}
            } else if ("LPIP_ORDER_TOTAL".equals(inputParamEnumId)) {
                if (UtilValidate.isNotEmpty(condValue)) {
                	BigDecimal orderSubTotal = null;
                	if (UtilValidate.isEmpty(cart)){
                		if(orderId != null && !orderId.isEmpty()){
	                		OrderReadHelper orderHelper = new OrderReadHelper(delegator, orderId);
	                		if(orderHelper != null){
		                		if (loyaltyCond.containsKey("usePriceWithTax") && "Y".equals(loyaltyCond.getString("usePriceWithTax"))) {
		                			orderSubTotal = orderHelper.getOrderGrandTotal();
		                		} else {
		                    		BigDecimal taxTotal = orderHelper.getTaxTotal();
		                    		orderSubTotal = orderHelper.getOrderGrandTotal().subtract(taxTotal);
		                    	}
	                		}
                		} else {
                			GenericValue returnHeader = delegator.findOne("ReturnHeader", UtilMisc.toMap("returnId", returnId), false);
                			if (loyaltyCond.containsKey("usePriceWithTax") && "Y".equals(loyaltyCond.getString("usePriceWithTax"))) {
                				orderSubTotal = returnHeader.getBigDecimal("grandTotal");
                			}else{
                				orderSubTotal = returnHeader.getBigDecimal("grandTotal").subtract(returnHeader.getBigDecimal("taxTotal"));
                			}
                		}
                	} else {
                		if (loyaltyCond.containsKey("usePriceWithTax") && "Y".equals(loyaltyCond.getString("usePriceWithTax"))) {
                    		orderSubTotal = cart.getSubTotalForPromotions(true);
                    	} else {
                    		orderSubTotal = cart.getSubTotalForPromotions();
                    	}
                	}
                	
                	compareBase = Integer.valueOf(orderSubTotal.compareTo(new BigDecimal(condValue)));
                } 
            } else if ("LPIP_PARTY_ID".equals(inputParamEnumId)) {
            	if (partyId != null && UtilValidate.isNotEmpty(condValue)) {
                    compareBase = Integer.valueOf(partyId.compareTo(condValue));
                } else {
                    compareBase = Integer.valueOf(1);
                }
            } else if ("LPIP_PARTY_GRP_MEM".equals(inputParamEnumId)) {
            	if (UtilValidate.isEmpty(partyId) || UtilValidate.isEmpty(condValue)) {
                    compareBase = Integer.valueOf(1);
                } else {
                    String groupPartyId = condValue;
                    if (partyId.equals(groupPartyId)) {
                        compareBase = Integer.valueOf(0);
                    } else {
                        List<GenericValue> partyRelationshipList = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom", groupPartyId, "partyIdTo", partyId, "partyRelationshipTypeId", "GROUP_ROLLUP"), null, true);
                        partyRelationshipList = EntityUtil.filterByDate(partyRelationshipList, true);

                        if (UtilValidate.isNotEmpty(partyRelationshipList)) {
                            compareBase = Integer.valueOf(0);
                        } else {
                            compareBase = Integer.valueOf(checkConditionPartyHierarchy(groupPartyId, partyId));
                        }
                    }
                }
            } else if ("LPIP_ROLE_TYPE".equals(inputParamEnumId)) {
            	if (partyId != null && UtilValidate.isNotEmpty(condValue)) {
                    GenericValue partyRole = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", condValue), true);
                    if (partyRole != null) {
                        compareBase = Integer.valueOf(0);
                    } else {
                        compareBase = Integer.valueOf(1);
                    }
                } else {
                    compareBase = Integer.valueOf(1);
                }
            } else if ("LPIP_TOTAL_POINT".equals(inputParamEnumId)) {
            	if (UtilValidate.isNotEmpty(condValue)) {
                	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator,(String) userLogin.get("userLoginId"));
                	GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
            		String payToPartyId = "";
            		if (UtilValidate.isNotEmpty(productStore)){
            			payToPartyId = productStore.getString("payToPartyId");
            		}
            		
            		if (UtilValidate.isEmpty(organizationPartyId)&&!payToPartyId.equals("")){
            			organizationPartyId = payToPartyId;
            		}
                	List<GenericValue> loyaltyPoint = delegator.findByAnd("LoyaltyPoint", UtilMisc.toMap("partyId", partyId, "ownerPartyId", organizationPartyId, "productStoreId", productStoreId), null, true);
                	if (UtilValidate.isNotEmpty(loyaltyPoint)){
                		BigDecimal partyPoint = loyaltyPoint.get(0).getBigDecimal("point");
                		compareBase = Integer.valueOf(partyPoint.compareTo(new BigDecimal(condValue)));
                	}
            	}
            } else if ("LPIP_EACH_ORDER_TOTA".equals(inputParamEnumId)) {
            	if (UtilValidate.isNotEmpty(condValue)) {
            		operatorEnumId = "LPC_GTE";
            		BigDecimal orderSubTotal = null;
                	if (UtilValidate.isEmpty(cart)){
                		if(orderId == null || orderId.isEmpty()){
                			GenericValue returnHeader = delegator.findOne("ReturnHeader", UtilMisc.toMap("returnId", returnId), false);
                			if (loyaltyCond.containsKey("usePriceWithTax") && "Y".equals(loyaltyCond.getString("usePriceWithTax"))) {
                				orderSubTotal = returnHeader.getBigDecimal("grandTotal");
                			}else{
                				orderSubTotal = returnHeader.getBigDecimal("grandTotal").subtract(returnHeader.getBigDecimal("taxTotal"));
                			}
                		}else{
	                		OrderReadHelper orderHelper = new OrderReadHelper(delegator, orderId);
	                		if (loyaltyCond.containsKey("usePriceWithTax") && "Y".equals(loyaltyCond.getString("usePriceWithTax"))) {
	                			orderSubTotal = orderHelper.getOrderGrandTotal();
	                		} else {
	                    		BigDecimal taxTotal = orderHelper.getTaxTotal();
	                    		orderSubTotal = orderHelper.getOrderGrandTotal().subtract(taxTotal);
	                    	}
                		}
                	} else {
                		if (loyaltyCond.containsKey("usePriceWithTax") && "Y".equals(loyaltyCond.getString("usePriceWithTax"))) {
                    		orderSubTotal = cart.getSubTotalForPromotions(true);
                    	} else {
                    		orderSubTotal = cart.getSubTotalForPromotions();
                    	}
                	}
                	
                	BigDecimal condValueB = new BigDecimal(condValue);
                	
                	compareBase = Integer.valueOf(orderSubTotal.compareTo(condValueB));
                	if (!condValueB.equals(BigDecimal.ZERO)){
                		count = orderSubTotal.divide(condValueB, 0, RoundingMode.DOWN);
                	}
                } 
            } else if ("LPIP_EACH_PROD_QUANT".equals(inputParamEnumId)) {
            	if (UtilValidate.isNotEmpty(condValue)) {
            		operatorEnumId = "LPC_GTE";
                    BigDecimal quantityNeeded = new BigDecimal(condValue);
                    BigDecimal quantityAvailable = BigDecimal.ZERO;

                    Set<String> productIds = getLoyaltyRuleCondProductIds(loyaltyCond);
                    
                    if (UtilValidate.isNotEmpty(cart)){
                    	List<ShoppingCartItem> lineOrderedByBasePriceList = cart.getLineListOrderedByBasePrice(false);
                        Iterator<ShoppingCartItem> lineOrderedByBasePriceIter = lineOrderedByBasePriceList.iterator();
                        while (quantityNeeded.compareTo(BigDecimal.ZERO) > 0 && lineOrderedByBasePriceIter.hasNext()) {
                            ShoppingCartItem cartItem = lineOrderedByBasePriceIter.next();
                            String parentProductId = cartItem.getParentProductId();
                            if (!cartItem.getIsPromo()&&(productIds.contains(cartItem.getProductId()) || (parentProductId != null && productIds.contains(parentProductId)))) {
                            	quantityAvailable = cartItem.getQuantity();
                            }
                        }
                    } else {
                    	OrderReadHelper orderHelper = new OrderReadHelper(delegator, orderId);
                    	List<GenericValue> orderItems = orderHelper.getOrderItems();
                    	for (GenericValue item : orderItems){
                    		GenericValue parentProduct = ProductWorker.getParentProduct(item.getString("productId"), delegator);
                    		String parentProductId = null;
                    		if (UtilValidate.isNotEmpty(parentProduct)){
                    			parentProductId = parentProduct.getString("productId");
                    		}
                    		if ((productIds.contains(item.getString("productId"))||(parentProductId != null && productIds.contains(parentProductId)))
                    				&& item.getString("isPromo").equals("N")){
                    			quantityAvailable = item.getBigDecimal("quantity");
                    		}
                    	}
                    }
                    
                    compareBase = Integer.valueOf(quantityAvailable.compareTo(quantityNeeded));
                    if (!quantityNeeded.equals(BigDecimal.ZERO)){
                		count = quantityAvailable.divide(quantityNeeded, 0, RoundingMode.DOWN);
                	}
            	}
            } else {
                Debug.logWarning(UtilProperties.getMessage(resource_error,"BSOrderAnUnSupportedLoyaltyCondInputParameterLhs", UtilMisc.toMap("inputParamEnumId", loyaltyCond.getString("inputParamEnumId")), cart.getLocale()), module);
            }

            if (compareBase != null) {
                int compare = compareBase.intValue();
                if ("LPC_EQ".equals(operatorEnumId)) {
                    if (compare == 0) result = true;
                } else if ("LPC_NEQ".equals(operatorEnumId)) {
                    if (compare != 0) result = true;
                } else if ("LPC_LT".equals(operatorEnumId)) {
                    if (compare < 0) result = true;
                } else if ("LPC_LTE".equals(operatorEnumId)) {
                    if (compare <= 0) result = true;
                } else if ("LPC_GT".equals(operatorEnumId)) {
                    if (compare > 0) result = true;
                } else if ("LPC_GTE".equals(operatorEnumId)) {
                    if (compare >= 0) result = true;
                } else {
                    Debug.logWarning(UtilProperties.getMessage(resource_error,"BSOrderAnUnSupportedLoyaltyCondCondition", UtilMisc.toMap("operatorEnumId", operatorEnumId) , cart.getLocale()), module);
                }
            } 
        } 
        
        successResult.put("result", result);
        successResult.put("count", count);
        
        return successResult;
	}
	
	public void performAction(GenericValue loyaltyAction, BigDecimal rate) throws GenericEntityException, CartItemModifyException{
		String loyaltyActionEnumId = loyaltyAction.getString("loyaltyActionEnumId");
		BigDecimal quantity = loyaltyAction.getBigDecimal("quantity");
		Map<String, Object> info = getInformationOrder();
		String partyId = (String) info.get("partyId");
		String productStoreId = (String) info.get("productStoreId");
		
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator,(String) userLogin.get("userLoginId"));
		GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
		String payToPartyId = "";
		if (UtilValidate.isNotEmpty(productStore)){
			payToPartyId = productStore.getString("payToPartyId");
		}
		
		if (UtilValidate.isEmpty(organizationPartyId)&&!payToPartyId.equals("")){
			organizationPartyId = payToPartyId;
		}
		
        if ("LOY_ASWP".equals(loyaltyActionEnumId)) {
        	BigDecimal point = quantity.multiply(rate);
        	totalPoint = totalPoint.add(point);
            String loyaltyPointId = "";
            List<GenericValue> listLoyaltyPoint = delegator.findByAnd("LoyaltyPoint", UtilMisc.toMap("partyId", partyId, "productStoreId", productStoreId, "ownerPartyId", organizationPartyId), UtilMisc.toList("loyaltyPointId"), false);
           
            //create loyalty point
            if (UtilValidate.isNotEmpty(listLoyaltyPoint)){
            	GenericValue loyaltyPoint = listLoyaltyPoint.get(0);
            	loyaltyPointId = loyaltyPoint.getString("loyaltyPointId");
            	BigDecimal oldPoint = loyaltyPoint.getBigDecimal("point");
            	loyaltyPoint.set("point", oldPoint.add(point));
            	loyaltyPoint.set("createdDate", nowTimestamp);
            	try {
            		loyaltyPoint.store();
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                }
            } else {
            	GenericValue loyaltyPoint = delegator.makeValue("LoyaltyPoint");
            	loyaltyPointId = delegator.getNextSeqId("LoyaltyPoint");
            	loyaltyPoint.set("loyaltyPointId", loyaltyPointId);
            	loyaltyPoint.set("partyId", partyId);
            	loyaltyPoint.set("createdDate", nowTimestamp);
            	loyaltyPoint.set("productStoreId", productStoreId);
            	loyaltyPoint.set("ownerPartyId", organizationPartyId);
            	loyaltyPoint.set("point", point);
            	try {
            		loyaltyPoint.create();
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                }
            }
           
            //create loyalty point detail
            GenericValue loyaltyPointDetail = delegator.makeValue("LoyaltyPointDetail");
            loyaltyPointDetail.set("loyaltyPointId", loyaltyPointId);
            loyaltyPointDetail.set("loyaltyPointDetailSeqId", delegator.getNextSeqId("LoyaltyPointDetail"));
            loyaltyPointDetail.set("effectiveDate", nowTimestamp);
            loyaltyPointDetail.set("pointDiff", point);
            loyaltyPointDetail.set("orderId", orderId);
            loyaltyPointDetail.set("returnId", returnId);
            try {
            	loyaltyPointDetail.create();
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
            
            //loyalty point use
            if(orderId != null && !orderId.isEmpty()){
	            GenericValue loyaltyUse = delegator.makeValue("LoyaltyUse");
	            loyaltyUse.set("orderId", orderId);
	            loyaltyUse.set("loyaltySequenceId", delegator.getNextSeqId("LoyaltyUse"));
	            loyaltyUse.set("loyaltyId", loyaltyAction.getString("loyaltyId"));
	            loyaltyUse.set("partyId", partyId);
	            loyaltyUse.set("quantityLeftInActions", point);
	            try {
	                loyaltyUse.create();
	            } catch (GenericEntityException e) {
	                Debug.logError(e, module);
	            }
            }
        } else if ("LOY_RATE".equals(loyaltyActionEnumId)) {
        	String actionValue = loyaltyAction.getString("actionValue");
        	GenericValue userLoginS = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
        	List<GenericValue> partyClassification = delegator.findByAnd("PartyClassification", UtilMisc.toMap("partyId", partyId), null, false);
        	if (UtilValidate.isNotEmpty(partyClassification)){
        		for (GenericValue party : partyClassification){
    				Map<String, Object> mapDelContext = new HashMap<String, Object>();
    	    		mapDelContext.put("partyClassificationGroupId", party.getString("partyClassificationGroupId"));
    	    		mapDelContext.put("partyId", partyId);
    	    		mapDelContext.put("fromDate", party.getTimestamp("fromDate"));
    	    		mapDelContext.put("userLogin", userLoginS);
    	    		try {
    					dispatcher.runSync("deletePartyClassification", mapDelContext);
    				} catch (GenericServiceException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
        		}
        	}
        	
        	Map<String, Object> mapContext = new HashMap<String, Object>();
    		mapContext.put("partyClassificationGroupId", actionValue);
    		mapContext.put("partyId", partyId);
    		mapContext.put("fromDate", nowTimestamp);
    		mapContext.put("userLogin", userLoginS);
    		try {
				dispatcher.runSync("createPartyClassification", mapContext);
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else {
            Debug.logError("An un-supported loyaltyActionType was used: " + loyaltyActionEnumId + ", not performing any action", module);
        }
	}
	
	public void getPointAction(GenericValue loyaltyAction, BigDecimal rate){
		String loyaltyActionEnumId = loyaltyAction.getString("loyaltyActionEnumId");
		BigDecimal quantity = loyaltyAction.getBigDecimal("quantity");
		if ("LOY_ASWP".equals(loyaltyActionEnumId)) {
			BigDecimal point = quantity.multiply(rate);
        	totalPoint = totalPoint.add(point);
		}
	}
	
	public Set<String> getLoyaltyRuleCondProductIds(GenericValue loyaltyCond) throws GenericEntityException{
        List<GenericValue> loyaltyCategories = delegator.findByAnd("LoyaltyCategory", UtilMisc.toMap("loyaltyId", loyaltyCond.get("loyaltyId"),
        		"loyaltyRuleId", loyaltyCond.get("loyaltyRuleId"), "loyaltyCondSeqId", loyaltyCond.get("loyaltyCondSeqId")), null, true);

        List<GenericValue> loyaltyProducts = delegator.findByAnd("LoyaltyProduct", UtilMisc.toMap("loyaltyId", loyaltyCond.get("loyaltyId"),
        		"loyaltyRuleId", loyaltyCond.get("loyaltyRuleId"), "loyaltyCondSeqId", loyaltyCond.get("loyaltyCondSeqId")), null, true);

        Set<String> productIds = FastSet.newInstance();
        
        //get all category and sub category
        Set<String> productCategoryIds = FastSet.newInstance();
        for (GenericValue loyaltyCategory : loyaltyCategories) {
            Set<String> tempCatIdSet = FastSet.newInstance();
            ProductSearch.getAllSubCategoryIds(loyaltyCategory.getString("productCategoryId"), tempCatIdSet, delegator, nowTimestamp);
            productCategoryIds.addAll(tempCatIdSet);
        }
        
        //get all product in categories
        for (String productCategoryId : productCategoryIds) {
            List<GenericValue> productCategoryMembers = delegator.findByAnd("ProductCategoryMember", UtilMisc.toMap("productCategoryId", productCategoryId), null, true);
            productCategoryMembers = EntityUtil.filterByDate(productCategoryMembers, nowTimestamp);
            for (GenericValue productCategoryMember : productCategoryMembers) {
                String productId = productCategoryMember.getString("productId");
                productIds.add(productId);
            }
        }
        
        //get products
		for (GenericValue loyaltyProduct : loyaltyProducts) {
			String productId = loyaltyProduct.getString("productId");
            productIds.add(productId);
        }
		
        return productIds;
	}
	
	public int checkConditionPartyHierarchy(String groupPartyId, String partyId) throws GenericEntityException{
        List<GenericValue> partyRelationshipList = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partyId, "partyRelationshipTypeId", "GROUP_ROLLUP"), null, true);
        partyRelationshipList = EntityUtil.filterByDate(partyRelationshipList, nowTimestamp, null, null, true);
        for (GenericValue genericValue : partyRelationshipList) {
            String partyIdFrom = (String) genericValue.get("partyIdFrom");
            if (partyIdFrom.equals(groupPartyId)) {
                return 0;
            }
            if (0 == checkConditionPartyHierarchy(groupPartyId, partyIdFrom)) {
                return 0;
            }
        }
        
        return 1;
    }
	
}
