package com.olbius.acc.cost;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CostServices {
	public static Map<String, Object> getProductAverageCost(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		String facilityId = (String)context.get("facilityId");
		String productId = (String)context.get("productId");
		String ownerPartyId = (String)context.get("ownerPartyId");
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("facilityId", facilityId));
		conds.add(EntityCondition.makeCondition("productId", productId));
		conds.add(EntityCondition.makeCondition("organizationPartyId", ownerPartyId));
		conds.add(EntityUtil.getFilterByDateExpr());
		try {
			List<GenericValue> productAverageCostList = delegator.findList("ProductAverageCost", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-fromDate"), null, false);
			String defaultUomId = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD", delegator);
			GenericValue partyAcctgPreference = delegator.findOne("PartyAcctgPreference", UtilMisc.toMap("partyId", ownerPartyId), false);
			String currencyUomId = partyAcctgPreference != null? partyAcctgPreference.getString("baseCurrencyUomId") : defaultUomId;  
			if(UtilValidate.isNotEmpty(productAverageCostList)){
				GenericValue productAverageCost = productAverageCostList.get(0);
				retMap.put("unitCost", productAverageCost.get("averageCost") != null? productAverageCost.get("averageCost") : BigDecimal.ZERO);
				retMap.put("purCost", productAverageCost.get("averagePurCost") != null? productAverageCost.get("averagePurCost") : BigDecimal.ZERO);
				retMap.put("currencyUomId", currencyUomId);
				return retMap;
			}
			conds.clear();
			conds.add(EntityUtil.getFilterByDateExpr("availableFromDate", "availableThruDate"));
			conds.add(EntityCondition.makeCondition("supplierPrefOrderId", "10_MAIN_SUPPL"));
			conds.add(EntityCondition.makeCondition("productId", productId));
			List<GenericValue> supplierProductList = delegator.findList("SupplierProduct", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-availableFromDate"), null, false);
			if(UtilValidate.isNotEmpty(supplierProductList)){
				GenericValue supplierProduct = supplierProductList.get(0);
				BigDecimal unitCost = supplierProduct.getBigDecimal("lastPrice") != null? supplierProduct.getBigDecimal("lastPrice") : BigDecimal.ZERO;
				retMap.put("unitCost", unitCost);
				retMap.put("purCost", BigDecimal.ZERO);
				retMap.put("currencyUomId", supplierProduct.get("currencyUomId"));
				
				GenericValue productAverageCost = delegator.makeValue("ProductAverageCost", UtilMisc.toMap("productId", productId, "facilityId", facilityId, "organizationPartyId", ownerPartyId, "productAverageCostTypeId", "SIMPLE_AVG_COST", "averageCost", unitCost, "fromDate", UtilDateTime.nowTimestamp()));
				productAverageCost.create();
				return retMap;
			}
			retMap.put("unitCost", BigDecimal.ZERO);
			retMap.put("purCost", BigDecimal.ZERO);
			retMap.put("currencyUomId", defaultUomId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> calcWeightProductAverageCost(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		String productId = (String)context.get("productId");
		String facilityId = (String)context.get("facilityId");
		String ownerPartyId = (String)context.get("ownerPartyId");
		String currencyUomId = null;
		BigDecimal totalAmountOnHandTotal = BigDecimal.ZERO, totalInventoryCost = BigDecimal.ZERO, 
				absValOfTotalQOH = BigDecimal.ZERO, absValOfTotalInvPurCost = BigDecimal.ZERO, absValOfTotalInvCost = BigDecimal.ZERO;
		Boolean differentCurrencies = false;
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("productId", productId));
		conds.add(EntityCondition.makeCondition("facilityId", facilityId));
		conds.add(EntityCondition.makeCondition("ownerPartyId", ownerPartyId));
		conds.add(EntityCondition.makeCondition("unitCost", EntityJoinOperator.NOT_EQUAL, null));
		conds.add(EntityCondition.makeCondition("purCost", EntityJoinOperator.NOT_EQUAL, null));
		try {
			List<GenericValue> inventoryItems = delegator.findList("InventoryItem", EntityCondition.makeCondition(conds), null, null, null, false);
			for(GenericValue inventoryItem: inventoryItems){
				BigDecimal tempAmountOnHandTotal = inventoryItem.get("amountOnHandTotal") != null? inventoryItem.getBigDecimal("amountOnHandTotal") : BigDecimal.ZERO;
				if(inventoryItem.get("quantityOnHandTotal") != null){
					tempAmountOnHandTotal = tempAmountOnHandTotal.multiply(inventoryItem.getBigDecimal("quantityOnHandTotal"));
				}
				totalAmountOnHandTotal = totalAmountOnHandTotal.add(tempAmountOnHandTotal);
				if(currencyUomId == null){
					currencyUomId = inventoryItem.getString("currencyUomId");
				}
				if(!differentCurrencies){
					if(inventoryItem.getString("currencyUomId").equals(currencyUomId)){
						totalInventoryCost = totalInventoryCost.add(tempAmountOnHandTotal.multiply(inventoryItem.getBigDecimal("unitCost")));
						totalInventoryCost = totalInventoryCost.add(tempAmountOnHandTotal.multiply(inventoryItem.getBigDecimal("purCost")));
						/** calculation of absolute values of QOH and total inventory cost **/
						if(tempAmountOnHandTotal.compareTo(BigDecimal.ZERO) < 0){
							absValOfTotalQOH = absValOfTotalQOH.add(tempAmountOnHandTotal.negate());
							absValOfTotalInvCost = absValOfTotalInvCost.add(inventoryItem.getBigDecimal("unitCost").multiply(tempAmountOnHandTotal.negate()));
							absValOfTotalInvPurCost = absValOfTotalInvPurCost.add(inventoryItem.getBigDecimal("purCost").multiply(tempAmountOnHandTotal.negate()));
						}else{
							absValOfTotalQOH = absValOfTotalQOH.add(tempAmountOnHandTotal);
							absValOfTotalInvCost = absValOfTotalInvCost.add(inventoryItem.getBigDecimal("unitCost").multiply(tempAmountOnHandTotal));
							absValOfTotalInvPurCost = absValOfTotalInvPurCost.add(inventoryItem.getBigDecimal("purCost").multiply(tempAmountOnHandTotal));
						}
					}else{
						differentCurrencies = true;
					}
				}
			}
			BigDecimal productAverageCost = BigDecimal.ZERO;
			BigDecimal productAveragePurCost = BigDecimal.ZERO;
			
			if(absValOfTotalQOH.compareTo(BigDecimal.ZERO) != 0){
				productAverageCost = absValOfTotalInvCost.divide(absValOfTotalQOH);
				productAveragePurCost = absValOfTotalInvPurCost.divide(absValOfTotalQOH);
			}
			retMap.put("totalAmountOnHandTotal", totalAmountOnHandTotal);
			if(!differentCurrencies){
				retMap.put("totalInventoryCost", totalInventoryCost);
				retMap.put("productAverageCost", productAverageCost);
				retMap.put("productAveragePurCost", productAveragePurCost);
				retMap.put("currencyUomId", currencyUomId);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
}
