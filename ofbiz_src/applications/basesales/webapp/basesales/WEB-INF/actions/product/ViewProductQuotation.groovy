
import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.common.uom.UomWorker;
import org.ofbiz.service.ServiceUtil;
import java.util.Calendar;

boolean isPrint = parameters.isPrint;
productIds = parameters.productId;
productQuotationId = parameters.productQuotationId;
GenericValue productQuotation = null;
List<GenericValue> listProductQuotationRule = new ArrayList<GenericValue>();
List<Map<String, Object>> listProductQuotationRuleData = new ArrayList<Map<String, Object>>();
Calendar cal = Calendar.getInstance();

List<GenericValue> uomListGV = delegator.findByAnd("Uom", ["uomTypeId" : "PRODUCT_PACKING"], null, false);
if (productQuotationId) {
	productQuotation = delegator.findOne("ProductQuotation", ["productQuotationId" : productQuotationId], false);
	if (productQuotation != null) {
		if (productQuotation.productQuotationTypeId == "PROD_CAT_PRICE_FOD") {
			// get price rule of CATEGORY
			
			List<EntityCondition> listCond = new ArrayList<EntityCondition>();
			listCond.add(EntityCondition.makeCondition("productQuotationId", productQuotationId));
			findOptions = new EntityFindOptions();
			findOptions.setDistinct(true);
			listProductQuotationRule = delegator.findList("ProductQuotationRulesCateFOL", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, findOptions, false);
	
			for (GenericValue quotationRule in listProductQuotationRule) {
				Map<String, Object> itemData = UtilMisc.toMap("productQuotationId", quotationRule.productQuotationId, 
					"productCategoryId", quotationRule.productCategoryId,
					"categoryName", quotationRule.categoryName, 
					"amount", quotationRule.amount);
				listProductQuotationRuleData.add(itemData);
			}
		} else {
			// get price rule of PRODUCT
			List<EntityCondition> listCond = new ArrayList<EntityCondition>();
			if (isPrint) {
				List<String> listProductId = new ArrayList<String>();
				if (productIds instanceof String) {
					listProductId.add(productIds);
				} else {
					for (String productId : productIds) {
						listProductId.add(productId);
					}
				}
				listCond.add(EntityCondition.makeCondition("productId", EntityOperator.IN, listProductId));
			}
			
			listCond.add(EntityCondition.makeCondition("productQuotationId", EntityOperator.EQUALS, productQuotationId));
			findCond = EntityCondition.makeCondition(listCond, EntityOperator.AND);
			findOptions = new EntityFindOptions();
			findOptions.setDistinct(true);
			listProductQuotationRule = delegator.findList("ProductQuotationRulesAndTax", findCond, null, UtilMisc.toList("productPriceRuleId"), findOptions, false);
	
			for (GenericValue quotationRule in listProductQuotationRule) {
				/*productQuotationId, productId, productName, listPrice */
				Map<String, Object> itemData = UtilMisc.toMap("productQuotationId", quotationRule.productQuotationId, "productId", quotationRule.productId, 
					"productName", quotationRule.productName, "listPrice", quotationRule.listPrice);
				String productWeightStr = "";
				BigDecimal productQuantityPerTray = null;
				BigDecimal priceToDistNormal = null;
				BigDecimal priceToDistNormalAfterVAT = null;
				BigDecimal priceToMarketNormal = null;
				BigDecimal priceToConsumerNormal = null;
				BigDecimal priceToDistPerTray = null;
				BigDecimal priceToMarketPerTray = null;
				BigDecimal priceToConsumerPerTray = null;
				GenericValue product = null;
				if (quotationRule.productId) {
					product = delegator.findOne("Product", ["productId" : quotationRule.productId], false);
					if (product != null) {
						itemData.put("productCode", product.productCode);
						
						if (product.containsKey("productPackingUomId") && product.productPackingUomId != null) {
							Map<String, Object> resultValue2 = dispatcher.runSync("getConvertPackingNumber", ["productId" : product.productId, "uomFromId" : "DELYS_KHAY", "uomToId" : product.productPackingUomId, "userLogin" : userLogin]);
							if (ServiceUtil.isSuccess(resultValue2)) {
								productQuantityPerTray = resultValue2.convertNumber;
							}
						}
						
						BigDecimal quantityPackingConvertToDefault = null;
						/*BigDecimal quantityTrayConvertToDefault = null;*/
						BigDecimal quantityTrayConvertToPacking = null;
						if (product.quantityUomId != null && product.containsKey("productPackingUomId") && product.productPackingUomId != null) {
							Map<String, Object> resultValue3 = dispatcher.runSync("getConvertPackingNumber", ["productId" : product.productId, "uomFromId" : product.productPackingUomId, "uomToId" : product.quantityUomId, "userLogin" : userLogin]);
							if (ServiceUtil.isSuccess(resultValue3)) {
								quantityPackingConvertToDefault = resultValue3.convertNumber;
							}
							
							Map<String, Object> resultValue4 = dispatcher.runSync("getConvertPackingNumber", ["productId" : product.productId, "uomFromId" : "DELYS_KHAY", "uomToId" : product.productPackingUomId, "userLogin" : userLogin]);
							if (ServiceUtil.isSuccess(resultValue4)) {
								quantityTrayConvertToPacking = resultValue4.convertNumber;
							}
						}
						/*if (product.quantityUomId != null) {
							Map<String, Object> resultValue4 = dispatcher.runSync("getConvertPackingNumber", ["productId" : product.productId, "uomFromId" : "DELYS_KHAY", "uomToId" : product.quantityUomId, "userLogin" : userLogin]);
							if (ServiceUtil.isSuccess(resultValue4)) {
								quantityTrayConvertToDefault = resultValue4.convertNumber;
							}
						}*/
						if (quotationRule.listPrice != null) {
							priceToDistNormal = quotationRule.listPrice;
							priceToDistNormalAfterVAT = quotationRule.listPriceVAT;
							/*if (quotationRule.taxPercentage != null) {
								BigDecimal ruleTaxPercentage = quotationRule.getBigDecimal("taxPercentage");
								priceToDistNormalAfterVAT = priceToDistNormal.multiply(ruleTaxPercentage.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP).add(new BigDecimal(1)));
							} else {
								priceToDistNormalAfterVAT = priceToDistNormal;
							}*/
						}
						
						if (quantityTrayConvertToPacking != null) {
							if (quotationRule.listPrice != null) priceToDistPerTray = quotationRule.listPrice * quantityTrayConvertToPacking;
						}
						/*if (quantityTrayConvertToDefault != null) {
							if (quotationRule.listPrice != null) priceToDistPerTray = quotationRule.listPrice * quantityTrayConvertToDefault;
						}*/
						
						if (product.productWeight != null && product.weightUomId != null) {
							Map<String, Object> resultValue = dispatcher.runSync("convertUom", ["originalValue" : product.productWeight, "uomId" : product.weightUomId, "uomIdTo" : "WT_g"]);
							if (ServiceUtil.isSuccess(resultValue)) {
								BigDecimal productWeightToWTg = resultValue.convertedValue;
								if (quantityPackingConvertToDefault != null) {
									productWeightToWTg = productWeightToWTg.multiply(quantityPackingConvertToDefault);
								}
								if (productWeightToWTg != null) {
									//String template = UtilProperties.getPropertyValue("arithmetic", "accounting-number.format", "#,##0.00;(#,##0.00)");
									String template = "#,##0.###";
									productWeightStr = UtilFormatOut.formatDecimalNumber(productWeightToWTg.doubleValue(), template, locale);
									List<GenericValue> productWeightUom = delegator.findByAnd("UomAndType", ["uomId": "WT_g"], null, false);
									if (productWeightUom != null) {
										productWeightStr += " (" + productWeightUom[0].abbreviation + ")"; //(g)
									}
								}
							}
						}
					}
				}
				
				String quantityUomDesc = "";
				if (quotationRule.quantityUomId) {
					GenericValue uomGV = EntityUtil.getFirst(EntityUtil.filterByAnd(uomListGV, ["uomId": quotationRule.quantityUomId]));
					if (uomGV) quantityUomDesc = uomGV.getString("description");
					
					if (product != null && UtilValidate.isNotEmpty(product.quantityUomId)) {
						BigDecimal quantityTmp = UomWorker.customConvertUom(product.productId, quotationRule.quantityUomId, product.quantityUomId, BigDecimal.ONE, delegator);
						if (quantityTmp != null) {
							productWeightStr = quantityTmp.doubleValue() + " x " + productWeightStr;
						}
					}
				}
				itemData.put("quantityUomId", quotationRule.quantityUomId);
				itemData.put("quantityUomDesc", quantityUomDesc);
				itemData.put("productWeightStr", productWeightStr);
				itemData.put("productQuantityPerTray", productQuantityPerTray);
				itemData.put("taxPercentage", quotationRule.taxPercentage);
				itemData.put("priceToDistNormal", priceToDistNormal);
				itemData.put("priceToDistNormalAfterVAT", priceToDistNormalAfterVAT);
				itemData.put("priceToMarketNormal", priceToMarketNormal);
				itemData.put("priceToConsumerNormal", priceToConsumerNormal);
				itemData.put("priceToDistPerTray", priceToDistPerTray);
				itemData.put("priceToMarketPerTray", priceToMarketPerTray);
				itemData.put("priceToConsumerPerTray", priceToConsumerPerTray);
				listProductQuotationRuleData.add(itemData);
			}
		}
		if (productQuotation.fromDate != null) {
			cal.setTimeInMillis(productQuotation.fromDate.getTime());
		}
		
		// get roleTypeId apply price to market
		/*List<EntityCondition> listRuleRoleTypeMarket = new ArrayList<EntityCondition>();
		listRuleRoleTypeMarket.add(EntityCondition.makeCondition("productQuotationId", EntityOperator.EQUALS, productQuotationId));
		listRuleRoleTypeMarket.add(EntityCondition.makeCondition("isExtra", EntityOperator.EQUALS, "Y"));
		List<GenericValue> listProductPriceRuleMarket = delegator.findList("ProductPriceRule", EntityCondition.makeCondition(listRuleRoleTypeMarket, EntityOperator.AND), null, null, null, false);
		if (listProductPriceRuleMarket != null && listProductPriceRuleMarket.size() > 0) {
			GenericValue productPriceRuleIdMarket = EntityUtil.getFirst(listProductPriceRuleMarket);
			
			List<EntityCondition> listCondRoleTypeMarket2 = new ArrayList<EntityCondition>();
			listCondRoleTypeMarket2.add(EntityCondition.makeCondition("productPriceRuleId", EntityOperator.EQUALS, productPriceRuleIdMarket.get("productPriceRuleId")));
			listCondRoleTypeMarket2.add(EntityCondition.makeCondition("inputParamEnumId", EntityOperator.EQUALS, "PRIP_ROLE_TYPE"));
			List<GenericValue> listProductPriceCondMarket = delegator.findList("ProductPriceCond", EntityCondition.makeCondition(listCondRoleTypeMarket2, EntityOperator.AND), null, null, null, false);
			if (listProductPriceCondMarket != null && listProductPriceCondMarket.size() > 0) {
				List<String> roleTypeMarketes = EntityUtil.getFieldListFromEntityList(listProductPriceCondMarket, "condValue", true);
				List<GenericValue> listRoleTypeMarket = delegator.findList("RoleType", EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roleTypeMarketes), null, null, null, false);
				context.listRoleTypeMarket = listRoleTypeMarket;
			}
		}*/
		
		// get partyId condition
		List<EntityCondition> condsPartyApply = new ArrayList<EntityCondition>();
		condsPartyApply.add(EntityCondition.makeCondition("productQuotationId", EntityOperator.EQUALS, productQuotationId));
		List<EntityCondition> listRuleRoleTypeMarketOr3 = new ArrayList<EntityCondition>();
		listRuleRoleTypeMarketOr3.add(EntityCondition.makeCondition("isExtra", EntityOperator.EQUALS, "N"));
		listRuleRoleTypeMarketOr3.add(EntityCondition.makeCondition("isExtra", EntityOperator.EQUALS, null));
		condsPartyApply.add(EntityCondition.makeCondition(listRuleRoleTypeMarketOr3, EntityOperator.OR));
		GenericValue pprFirstRule = EntityUtil.getFirst(delegator.findList("ProductPriceRule", EntityCondition.makeCondition(condsPartyApply, EntityOperator.AND), null, null, null, false));
		if (pprFirstRule != null) {
			/*List<EntityCondition> listCondRoleTypeMarket3 = new ArrayList<EntityCondition>();
			listCondRoleTypeMarket3.add(EntityCondition.makeCondition("productPriceRuleId", EntityOperator.EQUALS, pprFirstRule.get("productPriceRuleId")));
			listCondRoleTypeMarket3.add(EntityCondition.makeCondition("inputParamEnumId", EntityOperator.EQUALS, "PRIP_PARTY_ID"));
			List<GenericValue> listProductPriceCondMarket3 = delegator.findList("ProductPriceCond", EntityCondition.makeCondition(listCondRoleTypeMarket3, EntityOperator.AND), null, null, null, false);
			if (listProductPriceCondMarket3 != null && listProductPriceCondMarket3.size() > 0) {
				List<String> listPartyIdApply = EntityUtil.getFieldListFromEntityList(listProductPriceCondMarket3, "condValue", true);
				context.listPartyIdApply = listPartyIdApply;
			}*/
			List<EntityCondition> condsPartyApplyCond = new ArrayList<EntityCondition>();
			condsPartyApplyCond.add(EntityCondition.makeCondition("productPriceRuleId", EntityOperator.EQUALS, pprFirstRule.get("productPriceRuleId")));
			condsPartyApplyCond.add(EntityCondition.makeCondition("inputParamEnumId", EntityOperator.EQUALS, "PRIP_PARTY_ID"));
			GenericValue ppcPartyApply = EntityUtil.getFirst(delegator.findList("ProductPriceCond", EntityCondition.makeCondition(condsPartyApplyCond, EntityOperator.AND), null, null, null, false));
			if (ppcPartyApply != null) {
				context.partyIdApply = ppcPartyApply.condValue;
			}
			
			List<EntityCondition> condsPartyGroupApplyCond = new ArrayList<EntityCondition>();
			condsPartyGroupApplyCond.add(EntityCondition.makeCondition("productPriceRuleId", EntityOperator.EQUALS, pprFirstRule.get("productPriceRuleId")));
			condsPartyGroupApplyCond.add(EntityCondition.makeCondition("inputParamEnumId", EntityOperator.EQUALS, "PRIP_PARTY_GRP_MEM"));
			GenericValue ppcPartyGroupApply = EntityUtil.getFirst(delegator.findList("ProductPriceCond", EntityCondition.makeCondition(condsPartyGroupApplyCond, EntityOperator.AND), null, null, null, false));
			if (ppcPartyGroupApply != null) {
				context.partyGroupIdApply = ppcPartyGroupApply.condValue;
			}
		}
	}
	
	List<EntityCondition> listCond = new ArrayList<EntityCondition>();
	listCond.add(EntityCondition.makeCondition("productQuotationId", productQuotationId));
	List<EntityCondition> listCondOr = new ArrayList<EntityCondition>();
	listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp));
	listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
	listCond.add(EntityCondition.makeCondition(listCondOr, EntityOperator.OR));
	
	List<GenericValue> productStoreAppls = delegator.findList("ProductQuotationStoreAppl", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
	context.productStoreAppls = productStoreAppls;
	
	List<GenericValue> productStoreGroupAppls = delegator.findList("ProductQuotationStoreGroupAppl", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
	context.productStoreGroupAppls = productStoreGroupAppls;
}
context.listProductQuotationRuleData = listProductQuotationRuleData;
context.productQuotation = productQuotation;
context.fromDateDateTime = cal;