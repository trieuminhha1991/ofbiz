
import java.util.*;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.service.ServiceUtil;
import java.util.Calendar;
import javolution.util.FastMap;

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
		listProductQuotationRule = delegator.findList("ProductQuotationRulesAndTax", findCond, null, UtilMisc.toList("productCode"), findOptions, false);
		for (GenericValue quotationRule in listProductQuotationRule) {
			Map<String, Object> itemData = FastMap.newInstance();
			String productWeightStr = "";
			String brandName = "";
			String barcode = "";
			String productQC = "";
			BigDecimal taxPercentage = null;
			BigDecimal listPrice = quotationRule.listPrice;
			BigDecimal listPriceAfterVAT = null;
			if (quotationRule.productId) {
				itemData.put("productId", quotationRule.productId);
				itemData.put("productCode", quotationRule.productCode);
				itemData.put("productName", quotationRule.productName);
				itemData.put("quantityUomId", quotationRule.quantityUomId);
				itemData.put("listPrice", listPrice);
				if (quotationRule.quantityUomId) {
					GenericValue quotationUom = delegator.findOne("Uom", ["uomId" : quotationRule.quantityUomId], false);
					if (quotationUom != null) itemData.put("quantityUomIdStr", quotationUom.get("description", locale));
				}
				
				GenericValue product = delegator.findOne("Product", ["productId" : quotationRule.productId], false);
				if (product != null) {
					BigDecimal quantityPackingConvertToDefault = null;
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
					
					if (quotationRule.taxPercentage != null) {
						taxPercentage = quotationRule.taxPercentage;
						if (listPrice != null) {
							BigDecimal taxPercentageAdd = taxPercentage.add(new BigDecimal(100));
							taxPercentageAdd = taxPercentageAdd.divide(new BigDecimal(100));
							BigDecimal priceToDistNormalTax = listPrice.multiply(taxPercentageAdd);
							listPriceAfterVAT = priceToDistNormalTax.setScale(-2, BigDecimal.ROUND_HALF_UP);
						}
					}
				}
			}
			itemData.put("productWeightStr", productWeightStr);
			itemData.put("barcode", barcode);
			itemData.put("brandName", brandName);
			itemData.put("taxPercentage", taxPercentage);
			itemData.put("listPriceAfterVAT", listPriceAfterVAT);
			listProductQuotationRuleData.add(itemData);
		}
		if (productQuotation.fromDate != null) {
			cal.setTimeInMillis(productQuotation.fromDate.getTime());
		}
		
		/*List<GenericValue> roleTypesSelected = delegator.findByAnd("ProductQuotationRoleTypeAppl", ["productQuotationId" : productQuotationId], null, false);
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
		}*/
	}
}
context.listProductQuotationRuleData = listProductQuotationRuleData;
context.productQuotation = productQuotation;
context.fromDateDateTime = cal;
context.listRoleApply = listRoleApply;
