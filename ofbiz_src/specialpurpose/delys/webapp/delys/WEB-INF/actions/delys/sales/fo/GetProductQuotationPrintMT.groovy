
import java.util.*;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.service.ServiceUtil;
import java.util.Calendar;

boolean isPrint = parameters.isPrint;
productIds = parameters.productId;
productQuotationId = parameters.productQuotationId;
GenericValue productQuotation = null;
List<GenericValue> listProductQuotationRule = new ArrayList<GenericValue>();
List<Map<String, Object>> listProductQuotationRuleData = new ArrayList<Map<String, Object>>();
Calendar cal = Calendar.getInstance();
String listRoleApply = "";
if (productQuotationId) {
	productQuotation = delegator.findOne("ProductQuotation", ["productQuotationId" : productQuotationId], false);
	if (productQuotation != null) {
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
		listProductQuotationRule = delegator.findList("ProductQuotationRulesAndTax", findCond, null, UtilMisc.toList("productId"), findOptions, false);
		for (GenericValue quotationRule in listProductQuotationRule) {
			/*productQuotationId, productId, productName, priceToDist, priceToMarket, priceToConsumer */
			Map<String, Object> itemData = UtilMisc.toMap("productQuotationId", quotationRule.productQuotationId, "productId", quotationRule.productId, 
				"productName", quotationRule.productName, "priceToDist", quotationRule.priceToDist, "priceToMarket", quotationRule.priceToMarket, "priceToConsumer", quotationRule.priceToConsumer);
			String productWeightStr = "";
			String brandName = "";
			String barcode = "";
			String productQC = "";
			BigDecimal taxPercentage = null;
			BigDecimal productQuantityPerTray = null;
			BigDecimal priceToDistNormal = null;
			BigDecimal priceToMarketNormal = null;
			BigDecimal priceToConsumerNormal = null;
			BigDecimal priceToDistPerTray = null;
			BigDecimal priceToMarketPerTray = null;
			BigDecimal priceToConsumerPerTray = null;
			BigDecimal priceToDistNormalTax = null;
			if (quotationRule.productId) {
				GenericValue product = delegator.findOne("Product", ["productId" : quotationRule.productId], false);
				if (product != null) {
					if (product.productPackingUomId != null) {
						Map<String, Object> resultValue2 = dispatcher.runSync("getConvertPackingNumber", ["productId" : product.productId, "uomFromId" : "DELYS_KHAY", "uomToId" : product.productPackingUomId, "userLogin" : userLogin]);
						if (ServiceUtil.isSuccess(resultValue2)) {
							productQuantityPerTray = resultValue2.convertNumber;
						}
						GenericValue productPackingUom = delegator.findOne("Uom", ["uomId" : product.productPackingUomId], false);
						if (productPackingUom != null) {
							productQC = productPackingUom.description;
						}
					}
					
					BigDecimal quantityPackingConvertToDefault = null;
					/*BigDecimal quantityTrayConvertToDefault = null;*/
					BigDecimal quantityTrayConvertToPacking = null;
					if (product.quantityUomId != null && product.productPackingUomId != null) {
						Map<String, Object> resultValue3 = dispatcher.runSync("getConvertPackingNumber", ["productId" : product.productId, "uomFromId" : product.productPackingUomId, "uomToId" : product.quantityUomId, "userLogin" : userLogin]);
						if (ServiceUtil.isSuccess(resultValue3)) {
							quantityPackingConvertToDefault = resultValue3.convertNumber;
						}
					}
					/*if (product.quantityUomId != null) {
						Map<String, Object> resultValue4 = dispatcher.runSync("getConvertPackingNumber", ["productId" : product.productId, "uomFromId" : "DELYS_KHAY", "uomToId" : product.quantityUomId, "userLogin" : userLogin]);
						if (ServiceUtil.isSuccess(resultValue4)) {
							quantityTrayConvertToDefault = resultValue4.convertNumber;
						}
					}*/
					if (product.quantityUomId != null) {
						Map<String, Object> resultValue4 = dispatcher.runSync("getConvertPackingNumber", ["productId" : product.productId, "uomFromId" : "DELYS_KHAY", "uomToId" : product.productPackingUomId, "userLogin" : userLogin]);
						if (ServiceUtil.isSuccess(resultValue4)) {
							quantityTrayConvertToPacking = resultValue4.convertNumber;
						}
					}
					/*if (quantityPackingConvertToDefault != null) {
						if (quotationRule.priceToDist != null) priceToDistNormal = quotationRule.priceToDist * quantityPackingConvertToDefault;
						if (quotationRule.priceToMarket != null) priceToMarketNormal = quotationRule.priceToMarket * quantityPackingConvertToDefault;
						if (quotationRule.priceToConsumer != null) priceToConsumerNormal = quotationRule.priceToConsumer * quantityPackingConvertToDefault;
					}*/
					if (quotationRule.priceToDist != null) priceToDistNormal = quotationRule.priceToDist;
					if (quotationRule.priceToMarket != null) priceToMarketNormal = quotationRule.priceToMarket;
					if (quotationRule.priceToConsumer != null) priceToConsumerNormal = quotationRule.priceToConsumer;
					
					if (quantityTrayConvertToPacking != null) {
						if (quotationRule.priceToDist != null) priceToDistPerTray = quotationRule.priceToDist * quantityTrayConvertToPacking;
						if (quotationRule.priceToMarket != null) priceToMarketPerTray = quotationRule.priceToMarket * quantityTrayConvertToPacking;
						if (quotationRule.priceToConsumer != null) priceToConsumerPerTray = quotationRule.priceToConsumer * quantityTrayConvertToPacking;
					}
					/*if (quantityTrayConvertToDefault != null) {
						if (quotationRule.priceToDist != null) priceToDistPerTray = quotationRule.priceToDist * quantityTrayConvertToDefault;
						if (quotationRule.priceToMarket != null) priceToMarketPerTray = quotationRule.priceToMarket * quantityTrayConvertToDefault;
						if (quotationRule.priceToConsumer != null) priceToConsumerPerTray = quotationRule.priceToConsumer * quantityTrayConvertToDefault;
					}*/
					
					if (product.productWeight != null && product.weightUomId != null) {
						Map<String, Object> resultValue = dispatcher.runSync("convertUom", ["originalValue" : product.productWeight, "uomId" : product.weightUomId, "uomIdTo" : "WT_g"]);
						if (ServiceUtil.isSuccess(resultValue)) {
							BigDecimal productWeightToWTg = resultValue.convertedValue;
							BigDecimal productWeightToWTgAdd = null;
							if (quantityPackingConvertToDefault != null) {
								productWeightToWTgAdd = productWeightToWTg.multiply(quantityPackingConvertToDefault);
								if (productWeightToWTgAdd != null) {
									//String template = UtilProperties.getPropertyValue("arithmetic", "accounting-number.format", "#,##0.00;(#,##0.00)");
									String template = "#,##0.###";
									List<GenericValue> productWeightUom = delegator.findByAnd("UomAndType", ["uomId": "WT_g"], null, false);
									if (productWeightUom != null) {
										productWeightStr = UtilFormatOut.formatDecimalNumber(quantityPackingConvertToDefault.doubleValue(), template, locale) + " x " + UtilFormatOut.formatDecimalNumber(productWeightToWTg.doubleValue(), template, locale) + productWeightUom[0].abbreviation; //(g)
										productWeightStr += " = " +  UtilFormatOut.formatDecimalNumber(productWeightToWTgAdd.doubleValue(), template, locale) + productWeightUom[0].abbreviation;
									}
								}
							}
						}
						if (product.brandName != null) {
							brandName = product.brandName;
						}
					}
					
					GenericValue productBarcode = delegator.findOne("GoodIdentification", ["goodIdentificationTypeId": "SKU", "productId": quotationRule.productId], false);
					if (productBarcode != null) {
						barcode = productBarcode.getString("idValue");
					}
					if (quotationRule.taxPercentage != null) {
						taxPercentage = quotationRule.taxPercentage;
						if (priceToDistNormal != null) {
							BigDecimal taxPercentageAdd = taxPercentage.add(new BigDecimal(100));
							taxPercentageAdd = taxPercentageAdd.divide(new BigDecimal(100));
							priceToDistNormalTax = priceToDistNormal.multiply(taxPercentageAdd);
							priceToDistNormalTax = priceToDistNormalTax.setScale(-2, BigDecimal.ROUND_HALF_UP);
						}
					}
				}
			}
			itemData.put("productWeightStr", productWeightStr);
			itemData.put("barcode", barcode);
			itemData.put("brandName", brandName);
			itemData.put("taxPercentage", taxPercentage);
			itemData.put("productQC", productQC);
			itemData.put("priceToDistNormalTax", priceToDistNormalTax);
			itemData.put("productQuantityPerTray", productQuantityPerTray);
			itemData.put("priceToDistNormal", priceToDistNormal);
			itemData.put("priceToMarketNormal", priceToMarketNormal);
			itemData.put("priceToConsumerNormal", priceToConsumerNormal);
			itemData.put("priceToDistPerTray", priceToDistPerTray);
			itemData.put("priceToMarketPerTray", priceToMarketPerTray);
			itemData.put("priceToConsumerPerTray", priceToConsumerPerTray);
			listProductQuotationRuleData.add(itemData);
		}
		if (productQuotation.fromDate != null) {
			cal.setTimeInMillis(productQuotation.fromDate.getTime());
		}
		
		List<GenericValue> roleTypesSelected = delegator.findByAnd("ProductQuotationRoleType", ["productQuotationId" : productQuotationId], null, false);
		if (roleTypesSelected != null) {
			for (int i = 0; i < roleTypesSelected.size(); i++) {
				GenericValue roleType = roleTypesSelected.get(i);
				GenericValue roleTypeRelate = roleType.getRelatedOne("RoleType", false);
				if (i > 0) {
					listRoleApply += ", " + roleTypeRelate.getString("description");
				} else {
					listRoleApply += roleTypeRelate.getString("description");
				}
			}
		}
		
		// get roleTypeId apply price to market
		List<EntityCondition> listRuleRoleTypeMarket = new ArrayList<EntityCondition>();
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
		}
		
		// get partyId condition
		List<EntityCondition> listRuleRoleTypeMarket3 = new ArrayList<EntityCondition>();
		listRuleRoleTypeMarket3.add(EntityCondition.makeCondition("productQuotationId", EntityOperator.EQUALS, productQuotationId));
		List<EntityCondition> listRuleRoleTypeMarketOr3 = new ArrayList<EntityCondition>();
		listRuleRoleTypeMarketOr3.add(EntityCondition.makeCondition("isExtra", EntityOperator.EQUALS, "N"));
		listRuleRoleTypeMarketOr3.add(EntityCondition.makeCondition("isExtra", EntityOperator.EQUALS, null));
		listRuleRoleTypeMarket3.add(EntityCondition.makeCondition(listRuleRoleTypeMarketOr3, EntityOperator.OR));
		List<GenericValue> listProductPriceRuleMarket3 = delegator.findList("ProductPriceRule", EntityCondition.makeCondition(listRuleRoleTypeMarket3, EntityOperator.AND), null, null, null, false);
		if (listProductPriceRuleMarket3 != null && listProductPriceRuleMarket3.size() > 0) {
			GenericValue productPriceRuleIdMarket3 = EntityUtil.getFirst(listProductPriceRuleMarket3);
			
			List<EntityCondition> listCondRoleTypeMarket3 = new ArrayList<EntityCondition>();
			listCondRoleTypeMarket3.add(EntityCondition.makeCondition("productPriceRuleId", EntityOperator.EQUALS, productPriceRuleIdMarket3.get("productPriceRuleId")));
			listCondRoleTypeMarket3.add(EntityCondition.makeCondition("inputParamEnumId", EntityOperator.EQUALS, "PRIP_PARTY_ID"));
			List<GenericValue> listProductPriceCondMarket3 = delegator.findList("ProductPriceCond", EntityCondition.makeCondition(listCondRoleTypeMarket3, EntityOperator.AND), null, null, null, false);
			if (listProductPriceCondMarket3 != null && listProductPriceCondMarket3.size() > 0) {
				List<String> listPartyIdApply = EntityUtil.getFieldListFromEntityList(listProductPriceCondMarket3, "condValue", true);
				context.listPartyIdApply = listPartyIdApply;
			}
		}
	}
}
context.listProductQuotationRuleData = listProductQuotationRuleData;
context.productQuotation = productQuotation;
context.fromDateDateTime = cal;
context.listRoleApply = listRoleApply;
