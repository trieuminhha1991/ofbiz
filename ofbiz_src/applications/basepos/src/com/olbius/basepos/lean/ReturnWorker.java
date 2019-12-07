package com.olbius.basepos.lean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.service.GenericServiceException;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ReturnWorker {
	private String returnId;
	private Locale locale;
	private DataWorker dataWorker;
	
	public ReturnWorker(DataWorker dataWorker){
		this.dataWorker = dataWorker;
		this.locale = dataWorker.posSession.getLocale();
	}
	public boolean determineReturnAble() throws Exception{
		dataWorker.cart.haveContainReturnItem();
		if(dataWorker.cart.isWholeCartIsReturn()){
			return false;
		}
		return true;
	}
	public void createAndCompleteReturn() throws Exception{
		// There are some misunderstood 
		if(dataWorker.cart.isWholeCartIsReturn()){
			dataWorker.cart.setCartContainedReturn(true);
			dataWorker.setReturnGrandTotal(dataWorker.cart.getGranTotalReturn());
			createReturn();
		} else{
			if(dataWorker.cart.isCartContainedReturn()){
				dataWorker.cart.setCartContainedReturn(false);
				dataWorker.setReturnGrandTotal(dataWorker.cart.getGranTotalReturn());
				createReturn();
				dataWorker.cart.setCartContainedReturn(true);
			}
		}
	}
	private void createReturn() throws Exception{
		ShoppingCart cart = dataWorker.posSession.getCart();
		List<ShoppingCartItem> cartItemList = getCartItemReturnList(cart);
		returnId = createReturnHeader();
		dataWorker.setReturnId(returnId);
		dataWorker.setOrderId(dataWorker.cart.getOrderId());
		dataWorker.webPosTransaction.setReturnId(returnId);
		//create Return Item
		createReturnItem(cartItemList);
    	//update Return header
    	updateReturnHeader("RETURN_ACCEPTED");
    	// FIXME loyaltypoint
    	Map<String, Object> processLoyaltyPointMap = FastMap.newInstance();
        processLoyaltyPointMap.put("isReturnOrder", "Y");
        processLoyaltyPointMap.put("returnId", returnId);
        processLoyaltyPointMap.put("userLogin", dataWorker.posSession.getUserLogin());
        dataWorker.dispatcher.runSync("processLoyaltyPoint", processLoyaltyPointMap); 
	}
	
	public String createReturnItem (List<ShoppingCartItem> cartItemList) throws Exception{
		String returnTypeId = "RTN_REFUND_IMMEDIATE";
		String returnItemTypeId = "RET_FPROD_ITEM";
		for (ShoppingCartItem cartItem : cartItemList) {
			Map<String, Object> createCartItemMap = FastMap.newInstance();
			GenericValue product = cartItem.getProduct();
			String productId = product.getString("productId");
			createCartItemMap.put("userLogin", dataWorker.posSession.getUserLogin());
			createCartItemMap.put("returnId", returnId);
			createCartItemMap.put("returnTypeId", returnTypeId);
			createCartItemMap.put("returnItemTypeId", returnItemTypeId);
			createCartItemMap.put("productId", cartItem.getProductId());
			String quantityUomId = (String) cartItem.getAttribute("quantityUomId");
			BigDecimal returnPrice = BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(quantityUomId)){
				BigDecimal alternativeQuantity = cartItem.getAlternativeQuantity();
				returnPrice = cartItem.getAlternativeUnitPrice();
				createCartItemMap.put("returnQuantity", alternativeQuantity.negate());
				createCartItemMap.put("quantityUomId", quantityUomId);
			}else{
				createCartItemMap.put("returnQuantity", cartItem.getQuantity().negate());
				returnPrice = (BigDecimal) cartItem.getBasePrice();
			}
			createCartItemMap.put("returnPrice", returnPrice);
			Map<String, Object> createReturnItemDirectly = FastMap.newInstance();
			createReturnItemDirectly = dataWorker.dispatcher.runSync("createReturnItemDirectly", createCartItemMap);
			//create return adjustment
			List<GenericValue> productTax = FastList.newInstance();
			productTax = dataWorker.delegator.findByAnd("ProductAndTaxAuthorityRate", UtilMisc.toMap("productId", productId), null, false);
			
			if (UtilValidate.isNotEmpty(productTax)){
				BigDecimal taxPercent = productTax.get(0).getBigDecimal("taxPercentage");
				if (taxPercent.compareTo(BigDecimal.ZERO) > 0){
					Map<String, Object> createCartItemAdjMap = FastMap.newInstance(); 
					createCartItemAdjMap.put("returnId", returnId);
					createCartItemAdjMap.put("returnItemSeqId", createReturnItemDirectly.get("returnItemSeqId"));
					createCartItemAdjMap.put("returnTypeId", returnTypeId);
					createCartItemAdjMap.put("returnAdjustmentTypeId", "RET_SALES_TAX_ADJ");
					createCartItemAdjMap.put("description", UtilProperties.getMessage("BasePosUiLabels", "BSReturnSalesTaxNoOrder", this.getLocale()));
					createCartItemAdjMap.put("shipGroupSeqId", "_NA_");
					createCartItemAdjMap.put("createdDate", UtilDateTime.nowTimestamp());
					createCartItemAdjMap.put("createdByUserLogin", dataWorker.posSession.getUserLogin().getString("userLoginId"));
					createCartItemAdjMap.put("taxAuthorityRateSeqId", productTax.get(0).getString("taxAuthorityRateSeqId"));
					createCartItemAdjMap.put("sourcePercentage", taxPercent);
					createCartItemAdjMap.put("primaryGeoId", productTax.get(0).getString("originGeoId")); 
					createCartItemAdjMap.put("taxAuthGeoId", productTax.get(0).getString("taxAuthGeoId"));
					createCartItemAdjMap.put("taxAuthPartyId", productTax.get(0).getString("taxAuthPartyId"));
					
					BigDecimal taxAmount = returnPrice.multiply(taxPercent).divide(new BigDecimal(100));
					createCartItemAdjMap.put("amount", taxAmount);
					createCartItemAdjMap.put("userLogin", dataWorker.posSession.getUserLogin());
					
					dataWorker.dispatcher.runSync("createReturnAdjustment", createCartItemAdjMap);
				}
			}
		}
		return "success";
	}
	public void updateReturnHeader(String statusId) throws GenericServiceException{
    	Map<String, Object> updateReturnHeaderMap = FastMap.newInstance();
    	updateReturnHeaderMap.put("userLogin", dataWorker.posSession.getUserLogin());
    	updateReturnHeaderMap.put("returnId", returnId);
    	updateReturnHeaderMap.put("statusId", statusId);
    	updateReturnHeaderMap.put("needsInventoryReceive", "Y");
    	dataWorker.dispatcher.runSync("updateReturnHeaderDirectly", updateReturnHeaderMap);
	}
	public String createReturnHeader() throws GenericServiceException{
		String facilityId = dataWorker.cart.getFacilityId();
		String currencyUomId = dataWorker.cart.getCurrency();
		String partyId = dataWorker.cart.getBillToCustomerPartyId();
		Map<String, Object> createReturnHeaderMap = FastMap.newInstance();
		createReturnHeaderMap.put("userLogin", dataWorker.posSession.getUserLogin());
		createReturnHeaderMap.put("returnHeaderTypeId", "CUSTOMER_RETURN");
		createReturnHeaderMap.put("statusId", "RETURN_REQUESTED");
		createReturnHeaderMap.put("fromPartyId", partyId);
		createReturnHeaderMap.put("grandTotal", dataWorker.cart.getGranTotalReturn());
		createReturnHeaderMap.put("taxTotal", dataWorker.cart.getTotalSalesTax());
		createReturnHeaderMap.put("productStoreId", dataWorker.posSession.getProductStoreId());
		createReturnHeaderMap.put("destinationFacilityId", facilityId);
		createReturnHeaderMap.put("needsInventoryReceive", "Y");
		createReturnHeaderMap.put("currencyUomId", currencyUomId);
		Map<String, Object> createReturnHeader = FastMap.newInstance();
		createReturnHeader = dataWorker.dispatcher.runSync("createReturnHeader", createReturnHeaderMap);
		String returnId = (String) createReturnHeader.get("returnId");
		return returnId;
	}
	public List<ShoppingCartItem> getCartItemReturnList(ShoppingCart cart){
		List<ShoppingCartItem> cartItemReturnList = FastList.newInstance();
		List<ShoppingCartItem> cartItemList = cart.items();
		if(UtilValidate.isNotEmpty(cartItemList)){
			for (ShoppingCartItem cartItem : cartItemList) {
				BigDecimal quantity = cartItem.getQuantity();
				if(quantity.compareTo(BigDecimal.ZERO)<0){
					
					cartItemReturnList.add(cartItem);
				}
			}
		}
		return cartItemReturnList;
	}
	public Locale getLocale() {
		return locale;
	}
}
