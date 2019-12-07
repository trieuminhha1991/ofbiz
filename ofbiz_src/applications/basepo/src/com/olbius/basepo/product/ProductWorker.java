package com.olbius.basepo.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

public class ProductWorker {
	public static final String module = ProductWorker.class.getName();
	
	public static List<Map<String, Object>> getListQuantityUomIdsPO(GenericValue product, String quantityUomId, String currencyUomId, Delegator delegator, LocalDispatcher dispatcher) throws GenericEntityException{
		List<Map<String, Object>> listQuantityUomIdByProduct = new ArrayList<Map<String, Object>>();
		if (UtilValidate.isEmpty(quantityUomId)) {
			return listQuantityUomIdByProduct;
		}
		String productId = product.getString("productId");
		
		// get field is list packing uom id of product // column: packingUomId
		EntityCondition condsItem = EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "uomToId", quantityUomId));
		EntityFindOptions optsItem = new EntityFindOptions();
		optsItem.setDistinct(true);
		List<GenericValue> listConfigPacking = FastList.newInstance();
		listConfigPacking.addAll(delegator.findList("ConfigPackingAndUom", condsItem, null, null, optsItem, false));
		Set<String> uomIds = FastSet.newInstance();
		BigDecimal quantityConvert = BigDecimal.ONE;
		for (GenericValue conPackItem : listConfigPacking) {
			String uomFromId = conPackItem.getString("uomFromId");
			if (uomIds.contains(uomFromId)) {
				continue;
			}
			uomIds.add(uomFromId);
			// Check by quantity uom
			quantityConvert = conPackItem.getBigDecimal("quantityConvert");
			if (quantityConvert == null) quantityConvert = BigDecimal.ONE;
			BigDecimal unitPrice = BigDecimal.ZERO;
			if (dispatcher != null && UtilValidate.isNotEmpty(currencyUomId)) {
				try {
					Map<String, Object> calPriceCtx = UtilMisc.<String, Object>toMap("product", product, 
							"currencyUomId", currencyUomId, "partyId", null, "quantity", quantityConvert, "amount", null, "quantityUomId", uomFromId);
					Map<String, Object> resultCalPrice = dispatcher.runSync("calculatePurchasePrice", calPriceCtx);
		        	if (!ServiceUtil.isError(resultCalPrice)) {
		        		unitPrice = (BigDecimal) resultCalPrice.get("price");
		        	}
				} catch (Exception e) {
					Debug.logWarning("Error when calculate price of uom: " + uomFromId, module);
				}
			}
        	
        	// add to result
			Map<String, Object> packingUomIdMap = FastMap.newInstance();
			packingUomIdMap.put("description", conPackItem.getString("descriptionFrom"));
			packingUomIdMap.put("uomId", uomFromId);
			packingUomIdMap.put("quantityConvert", quantityConvert);
			packingUomIdMap.put("unitPriceConvert", unitPrice);
			listQuantityUomIdByProduct.add(packingUomIdMap);
		}
		if (!uomIds.contains(quantityUomId)) {
			GenericValue quantityUom = delegator.findOne("Uom", UtilMisc.toMap("uomId", quantityUomId), false);
			if (quantityUom != null) {
				BigDecimal unitPrice = BigDecimal.ZERO;
				if (dispatcher != null && UtilValidate.isNotEmpty(currencyUomId)) {
					try {
						Map<String, Object> calPriceCtx = UtilMisc.<String, Object>toMap("product", product, 
								"currencyUomId", currencyUomId, "partyId", null, "quantity", null, "amount", null, "quantityUomId", quantityUomId);
						Map<String, Object> resultCalPrice = dispatcher.runSync("calculatePurchasePrice", calPriceCtx);
			        	if (!ServiceUtil.isError(resultCalPrice)) {
			        		unitPrice = (BigDecimal) resultCalPrice.get("price");
			        	}
					} catch (Exception e) {
						Debug.logWarning("Error when calculate price of uom: " + quantityUomId, module);
					}
				}
				
				Map<String, Object> packingUomIdMap = FastMap.newInstance();
				packingUomIdMap.put("description", quantityUom.getString("description"));
				packingUomIdMap.put("uomId", quantityUom.getString("uomId"));
				packingUomIdMap.put("quantityConvert", BigDecimal.ONE);
				packingUomIdMap.put("unitPriceConvert", unitPrice);
				listQuantityUomIdByProduct.add(packingUomIdMap);
			}
		}
		return listQuantityUomIdByProduct;
	}
}
