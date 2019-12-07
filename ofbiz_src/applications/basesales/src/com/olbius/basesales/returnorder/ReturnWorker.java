package com.olbius.basesales.returnorder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.DataModelConstants;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ReturnWorker {
	public static final String module = ReturnWorker.class.getName();
	
	public static List<GenericValue> getReturnHeaderStatuses(Delegator delegator, String returnId) {
		List<GenericValue> returnStatuses = null;
		try {
			List<EntityCondition> listCondsOr = FastList.newInstance();
			listCondsOr.add(EntityCondition.makeCondition("returnItemSeqId", EntityOperator.EQUALS, null));
			listCondsOr.add(EntityCondition.makeCondition("returnItemSeqId", EntityOperator.EQUALS, ""));
			listCondsOr.add(EntityCondition.makeCondition("returnItemSeqId", EntityOperator.EQUALS, DataModelConstants.SEQ_ID_NA));
			returnStatuses = delegator.findList("ReturnStatus", EntityCondition.makeCondition(EntityCondition.makeCondition("returnId", returnId), EntityOperator.AND, EntityCondition.makeCondition(listCondsOr, EntityOperator.OR)), null, UtilMisc.toList("-statusDatetime"), null, false);
		} catch (GenericEntityException e) {
            Debug.logWarning(e, "Problems getting Party entity", module);
        }
		return returnStatuses;
	}
	
	public static BigDecimal getGrandTotalReturn(Delegator delegator, String returnId) {
		BigDecimal grandTotal = BigDecimal.ZERO;
		try {
			List<GenericValue> returnItems = delegator.findByAnd("ReturnItem", UtilMisc.toMap("returnId", returnId), null, false);
			if (UtilValidate.isNotEmpty(returnItems)) {
				for (GenericValue returnItem : returnItems) {
					if (returnItem.get("returnPrice") != null && returnItem.get("returnQuantity") != null) {
						grandTotal = grandTotal.add(returnItem.getBigDecimal("returnQuantity").multiply(returnItem.getBigDecimal("returnPrice")));
					}
				}
			}
			
			// check item adjustment
			List<GenericValue> returnAdjustments = delegator.findByAnd("ReturnAdjustment", UtilMisc.toMap("returnId", returnId), null, false);
			if (UtilValidate.isNotEmpty(returnAdjustments)) {
				for (GenericValue returnAdjustment : returnAdjustments) {
					if (returnAdjustment.get("amount") != null) {
						grandTotal = grandTotal.add(returnAdjustment.getBigDecimal("amount"));
					}
				}
			}
		} catch (GenericEntityException e) {
            Debug.logWarning(e, "Problems getting grand total return", module);
        }
		return grandTotal;
	}
	
	public void createReturn(ShoppingCart cart, Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, Locale locale) throws Exception{
		 //ShoppingCart cart = dataWorker.posSession.getCart();
		List<ShoppingCartItem> cartItemList = getCartItemReturnList(cart);
		String returnId = createReturnHeader(cart, dispatcher, userLogin);
		//create Return Item
		createReturnItem(returnId, cartItemList, delegator, dispatcher, userLogin, locale);
    	//update Return header
    	updateReturnHeader(returnId, "RETURN_ACCEPTED", dispatcher, userLogin);
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
	
	public String createReturnHeader(ShoppingCart cart, LocalDispatcher dispatcher, GenericValue userLogin) throws GenericServiceException{
		String facilityId = cart.getFacilityId();
		String currencyUomId = cart.getCurrency();
		String partyId = cart.getBillToCustomerPartyId();
		String productStoreId = cart.getProductStoreId();
		Map<String, Object> createReturnHeaderMap = FastMap.newInstance();
		createReturnHeaderMap.put("userLogin", userLogin);
		createReturnHeaderMap.put("returnHeaderTypeId", "CUSTOMER_RETURN");
		createReturnHeaderMap.put("statusId", "RETURN_REQUESTED");
		createReturnHeaderMap.put("fromPartyId", partyId);
		createReturnHeaderMap.put("destinationFacilityId", facilityId);
		createReturnHeaderMap.put("needsInventoryReceive", "Y");
		createReturnHeaderMap.put("currencyUomId", currencyUomId);
		createReturnHeaderMap.put("productStoreId", productStoreId);
		createReturnHeaderMap.put("grandTotal", cart.getGranTotalReturn());
		createReturnHeaderMap.put("taxTotal", cart.getTotalSalesTax());
		Map<String, Object> createReturnHeader = FastMap.newInstance();
		createReturnHeader = dispatcher.runSync("createReturnHeader", createReturnHeaderMap);
		String returnId = (String) createReturnHeader.get("returnId");
		return returnId;
	}
	
	public void updateReturnHeader(String returnId, String statusId, LocalDispatcher dispatcher, GenericValue userLogin) throws GenericServiceException{
    	Map<String, Object> updateReturnHeaderMap = FastMap.newInstance();
    	updateReturnHeaderMap.put("userLogin", userLogin);
    	updateReturnHeaderMap.put("returnId", returnId);
    	updateReturnHeaderMap.put("statusId", statusId);
    	updateReturnHeaderMap.put("needsInventoryReceive", "Y");
    	dispatcher.runSync("updateReturnHeaderDirectly", updateReturnHeaderMap);
	}
	
	public String createReturnItem (String returnId, List<ShoppingCartItem> cartItemList, Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, Locale locale) throws Exception{
		String returnTypeId = "RTN_REFUND_IMMEDIATE";
		String returnItemTypeId = "RET_FPROD_ITEM";
		for (ShoppingCartItem cartItem : cartItemList) {
			Map<String, Object> createCartItemMap = FastMap.newInstance();
			GenericValue product = cartItem.getProduct();
			String productId = product.getString("productId");
			createCartItemMap.put("userLogin", userLogin);
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
			createReturnItemDirectly = dispatcher.runSync("createReturnItemDirectly", createCartItemMap);
			//create return adjustment
			List<GenericValue> productTax = FastList.newInstance();
			productTax = delegator.findByAnd("ProductAndTaxAuthorityRate", UtilMisc.toMap("productId", productId), null, false);
			
			if (UtilValidate.isNotEmpty(productTax)){
				BigDecimal taxPercent = productTax.get(0).getBigDecimal("taxPercentage");
				if (taxPercent.compareTo(BigDecimal.ZERO) > 0){
					Map<String, Object> createCartItemAdjMap = FastMap.newInstance(); 
					createCartItemAdjMap.put("returnId", returnId);
					createCartItemAdjMap.put("returnItemSeqId", createReturnItemDirectly.get("returnItemSeqId"));
					createCartItemAdjMap.put("returnTypeId", returnTypeId);
					createCartItemAdjMap.put("returnAdjustmentTypeId", "RET_SALES_TAX_ADJ");
					createCartItemAdjMap.put("description", UtilProperties.getMessage("BasePosUiLabels", "BSReturnSalesTaxNoOrder", locale));
					createCartItemAdjMap.put("shipGroupSeqId", "_NA_");
					createCartItemAdjMap.put("createdDate", UtilDateTime.nowTimestamp());
					createCartItemAdjMap.put("createdByUserLogin", userLogin.getString("userLoginId"));
					createCartItemAdjMap.put("taxAuthorityRateSeqId", productTax.get(0).getString("taxAuthorityRateSeqId"));
					createCartItemAdjMap.put("sourcePercentage", taxPercent);
					createCartItemAdjMap.put("primaryGeoId", productTax.get(0).getString("originGeoId")); 
					createCartItemAdjMap.put("taxAuthGeoId", productTax.get(0).getString("taxAuthGeoId"));
					createCartItemAdjMap.put("taxAuthPartyId", productTax.get(0).getString("taxAuthPartyId"));
					
					BigDecimal taxAmount = returnPrice.multiply(taxPercent).divide(new BigDecimal(100));
					createCartItemAdjMap.put("amount", taxAmount);
					createCartItemAdjMap.put("userLogin", userLogin);
					
					dispatcher.runSync("createReturnAdjustment", createCartItemAdjMap);
				}
			}
		}
		return "success";
	}
}
