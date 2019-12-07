package com.olbius.basepo.product;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ProductUtils {
	public static final String module = ProductUtils.class.getName();
	public static final BigDecimal PERCENT_SCALE = new BigDecimal("100.000");
    public static int salestaxFinalDecimals = UtilNumber.getBigDecimalScale("salestax.final.decimals");
    public static int salestaxCalcDecimals = UtilNumber.getBigDecimalScale("salestax.calc.decimals");
    public static int salestaxRounding = UtilNumber.getBigDecimalRoundingMode("salestax.rounding");

	@SuppressWarnings("unchecked")
	public static List<EntityCondition> makeCondition(Map<String, ? extends Object> context, Delegator delegator)
			throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		for (EntityCondition e : listAllConditions) {
			String condition = e.toString();
			String[] conditionSplitted = condition.split(" ");
			if (conditionSplitted.length < 2) {
				continue;
			}
			String fieldName = conditionSplitted[0];
			fieldName = cleanFieldName(fieldName);

			String operator = conditionSplitted[1];
			if ("LIKE".equals(operator)) {
				conditionSplitted = condition.split(" '%");
				if (conditionSplitted.length < 2) {
					continue;
				}
				String value = conditionSplitted[1].trim();
				value = cleanValue(value).toUpperCase();
				if (UtilValidate.isEmpty(value)) {
					continue;
				}
				switch (fieldName) {
				case "productCode":
					conditions.add(EntityCondition.makeCondition(
							UtilMisc.toList(EntityCondition.makeCondition("productId", EntityJoinOperator.IN,
									getByProductCodeOfChild(delegator, value)), e),
							EntityJoinOperator.OR));
					break;
				default:
					conditions.add(e);
					break;
				}
			} else if ("=".equals(operator)) {

			} else if (">=".equals(operator)) {

			} else {
				conditions.add(e);
			}
		}
		return conditions;
	}

	public static String cleanValue(String value) {
		if (value.contains("(")) {
			value = value.replace("(", "");
		}
		if (value.contains(")")) {
			value = value.replace(")", "");
		}
		if (value.contains("'")) {
			value = value.replace("'", "");
		}
		if (value.contains("%")) {
			value = value.replace("%", "");
		}
		return value;
	}

	public static String cleanFieldName(String fieldName) {
		if (fieldName.contains("(")) {
			fieldName = fieldName.replace("(", "");
		}
		if (fieldName.contains(")")) {
			fieldName = fieldName.replace(")", "");
		}
		return fieldName;
	}

	public static List<String> getByProductCodeOfChild(Delegator delegator, String productCode)
			throws GenericEntityException {
		List<GenericValue> products = delegator.findList("Product",
				EntityCondition.makeCondition("productCode", EntityJoinOperator.LIKE, "%" + productCode.trim() + "%"),
				UtilMisc.toSet("productId"), null, null, false);
		List<String> productIds = EntityUtil.getFieldListFromEntityList(products, "productId", true);
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("productIdTo", EntityJoinOperator.IN, productIds));
		conditions
				.add(EntityCondition.makeCondition("productAssocTypeId", EntityJoinOperator.EQUALS, "PRODUCT_VARIANT"));
		List<GenericValue> productAssocs = delegator.findList("ProductAssoc", EntityCondition.makeCondition(conditions),
				UtilMisc.toSet("productId"), null, null, false);
		return EntityUtil.getFieldListFromEntityList(productAssocs, "productId", true);
	}
	
	public static List<String> getByProductCodeOfProductAndChild(Delegator delegator, String productCode) throws GenericEntityException {
		List<GenericValue> products = delegator.findList("Product", EntityCondition.makeCondition("productCode", EntityJoinOperator.LIKE, "%" + productCode.trim() + "%"), UtilMisc.toSet("productId"), null, null, false);
		List<String> productIds = EntityUtil.getFieldListFromEntityList(products, "productId", true);
		
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("productIdTo", EntityJoinOperator.IN, productIds));
		conditions.add(EntityCondition.makeCondition("productAssocTypeId", EntityJoinOperator.EQUALS, "PRODUCT_VARIANT"));
		List<GenericValue> productAssocs = delegator.findList("ProductAssoc", EntityCondition.makeCondition(conditions), UtilMisc.toSet("productId"), null, null, false);
		List<String> productChildIds = null;
		if (UtilValidate.isNotEmpty(productAssocs)) {
			productChildIds = EntityUtil.getFieldListFromEntityList(productAssocs, "productId", true);
		}
		List<String> resultValue = FastList.newInstance();
		if (UtilValidate.isNotEmpty(productIds)) resultValue.addAll(productIds);
		if (UtilValidate.isNotEmpty(productChildIds)) resultValue.addAll(productChildIds);
		
		return resultValue;
	}

	public static Map<String, Object> createOrStoreSupplierProduct(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, 
			String productId, String supplierProduct) throws GenericServiceException, GenericEntityException {
		return createOrStoreSupplierProduct(delegator, dispatcher, userLogin, productId, supplierProduct, null, false, null);
	}
	
	/**
	 * TODOCHANGE for SGC-MCS, check supplier id by party code
	 * @param delegator
	 * @param dispatcher
	 * @param userLogin
	 * @param productId
	 * @param supplierProduct
	 * @throws GenericServiceException
	 * @throws GenericEntityException
	 */
	public static Map<String, Object> createOrStoreSupplierProduct(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, 
			String productId, String supplierProduct, String quantityUomId, Boolean checkSupplierReference, Locale locale) throws GenericServiceException, GenericEntityException {
		Map<String, Object> returnSuccess = ServiceUtil.returnSuccess();
			 		
		List<EntityCondition> condsSup = FastList.newInstance();
		condsSup.add(EntityCondition.makeCondition("productId", productId));
		condsSup.add(EntityCondition.makeCondition(EntityCondition.makeCondition("availableThruDate", null), EntityOperator.OR, EntityCondition.makeCondition("availableThruDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp())));
		if (!checkSupplierReference) condsSup.add(EntityCondition.makeCondition("supplierPrefOrderId", EntityOperator.NOT_EQUAL, "80_SGC_SUPPL"));
		List<GenericValue> supplierProductsThru = delegator.findList("SupplierProduct", EntityCondition.makeCondition(condsSup), null, null, null, false);
		
		if (UtilValidate.isNotEmpty(supplierProduct)) {
			JSONArray childrenArray = JSONArray.fromObject((String) supplierProduct);
			Map<String, Object> productMap = FastMap.newInstance();
			for(int i = 0; i< childrenArray.size(); i++){
				JSONObject o = childrenArray.getJSONObject(i);
				productMap = processJsonSupplierProduct(o, productId);
				
				Timestamp availableFromDate = (Timestamp) productMap.get("availableFromDate");
				Timestamp availableThruDate = (Timestamp) productMap.get("availableThruDate");
				BigDecimal minimumOrderQuantity = (BigDecimal) productMap.get("minimumOrderQuantity");
				String supplierPrefOrderId = (String) productMap.get("supplierPrefOrderId");
				String currencyUomId = (String) productMap.get("currencyUomId");
				
				if ("80_SGC_SUPPL".equals(supplierPrefOrderId)) {
					if (!checkSupplierReference) {
						continue;
					}
				}
				
				String partyId = (String) productMap.get("partyId");
				String partyCode = (String) productMap.get("partyCode");
				GenericValue party = null;
				if (UtilValidate.isNotEmpty(partyId)) {
					party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
				}
				if (party == null && UtilValidate.isNotEmpty(partyCode)) {
					party = EntityUtil.getFirst(delegator.findByAnd("Party", UtilMisc.toMap("partyCode", partyCode), null, false));
				}
				if (party == null) {
					if ("80_SGC_SUPPL".equals(supplierPrefOrderId)) {
						// create party
						try {
							partyId = "NCC" + delegator.getNextSeqId("Party");
							Map<String, Object> newPartyMap = UtilMisc.toMap("partyId", partyId, 
									"partyTypeId", "PARTY_GROUP", "preferredCurrencyUomId", currencyUomId, 
									"createdDate", UtilDateTime.nowTimestamp(), "lastModifiedDate", UtilDateTime.nowTimestamp(),
									"partyCode", partyCode, "statusId", "PARTY_ENABLED");
			                if (userLogin != null) {
			                    newPartyMap.put("createdByUserLogin", userLogin.get("userLoginId"));
			                    newPartyMap.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
			                }
			                party = delegator.makeValue("Party", newPartyMap);
			                delegator.create(party);
						} catch (Exception e) {
							Debug.logWarning("Error: create supplier reference error", module);
							continue;
						}
					} else {
						continue;
					}
				}
				
				GenericValue supplierProductGV = delegator.findOne("SupplierProduct",
						UtilMisc.toMap("productId", productMap.get("productId"), "partyId", party.get("partyId"), "availableFromDate", availableFromDate,
								"minimumOrderQuantity", minimumOrderQuantity, "currencyUomId", currencyUomId), false);
				if (UtilValidate.isNotEmpty(supplierProductGV)) {
					if (supplierProductsThru != null) supplierProductsThru.remove(supplierProductGV);
					
					supplierProductGV.put("availableFromDate", availableFromDate);
					supplierProductGV.put("availableThruDate", availableThruDate);
					supplierProductGV.put("minimumOrderQuantity", minimumOrderQuantity);
					supplierProductGV.put("lastPrice", (BigDecimal) productMap.get("lastPrice"));
					supplierProductGV.put("shippingPrice", (BigDecimal) productMap.get("shippingPrice"));
					delegator.store(supplierProductGV);
				} else {
					String quantityUomIdParam = (String) productMap.get("quantityUomId");
					if (UtilValidate.isEmpty(quantityUomIdParam)) quantityUomIdParam = quantityUomId;
					
					Map<String, Object> prodCtx = FastMap.newInstance();
					prodCtx.put("productId", productId);
					prodCtx.put("supplierPrefOrderId", productMap.get("supplierPrefOrderId"));
					prodCtx.put("quantityUomId", quantityUomIdParam);
					prodCtx.put("partyId", party.get("partyId"));
					prodCtx.put("supplierProductId", productMap.get("supplierProductId"));
					prodCtx.put("availableFromDate", availableFromDate);
					prodCtx.put("availableThruDate", availableThruDate);
					prodCtx.put("minimumOrderQuantity", minimumOrderQuantity);
					prodCtx.put("currencyUomId", productMap.get("currencyUomId"));
					prodCtx.put("lastPrice", productMap.get("lastPrice"));
					prodCtx.put("shippingPrice", productMap.get("shippingPrice"));
					prodCtx.put("canDropShip", productMap.get("canDropShip"));
					prodCtx.put("comments", productMap.get("comments"));
					prodCtx.put("userLogin", userLogin);
					prodCtx.put("locale", locale);
					Map<String, Object> supplierProdCreateResult = dispatcher.runSync("createSupplierProduct", prodCtx);
					if (ServiceUtil.isError(supplierProdCreateResult)) {
						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(supplierProdCreateResult));
					}
				}
			}
		}
		if (UtilValidate.isNotEmpty(supplierProductsThru)) {
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			for (GenericValue supplierProd : supplierProductsThru) {
				supplierProd.set("availableThruDate", nowTimestamp);
			}
			delegator.storeAll(supplierProductsThru);
		}
		
		return returnSuccess;
	}
	
	public static String cleanString(Object originalString ) {
		String pureString = null;
		if (UtilValidate.isNotEmpty(originalString)) {
			if (!UtilMisc.toList("null", "undefined").contains(originalString)) {
				pureString = originalString.toString().trim();
			}
		}
		return pureString;
	}
	
	private static Map<String, Object> processJsonSupplierProduct(JSONObject obj, String productId){
		Map<String, Object> product = FastMap.newInstance();
		
		String partyId = null;
		if (obj.containsKey("partyId")) {
			partyId = cleanString(obj.getString("partyId"));
		}
		String partyCode = null;
		if (obj.containsKey("partyCode")) {
			partyCode = cleanString(obj.getString("partyCode"));
		}
		
		String supplierPrefOrderId = null;
		if (obj.containsKey("supplierPrefOrderId")) {
			supplierPrefOrderId = cleanString(obj.getString("supplierPrefOrderId"));
		}
		if (UtilValidate.isEmpty(supplierPrefOrderId)) supplierPrefOrderId = "10_MAIN_SUPPL";
		
		String quantityUomId = null;
		if (obj.containsKey("quantityUomId")) {
			quantityUomId = cleanString(obj.getString("quantityUomId"));
		}
		
		// available from date
		Timestamp availableFromDate = null;
		String availableFromDateStr = null;
		if (obj.containsKey("availableFromDate")) {
			availableFromDateStr = cleanString(obj.getString("availableFromDate"));
		}
		if (UtilValidate.isNotEmpty(availableFromDateStr)) {
			Long availableFromDateP = Long.valueOf(availableFromDateStr);
			availableFromDate = new Timestamp(availableFromDateP);
		}
		
		// available thru date
		Timestamp availableThruDate = null;
		String availableThruDateStr = null;
		if (obj.containsKey("availableThruDate")) {
			availableThruDateStr = cleanString(obj.getString("availableThruDate"));
		}
		if (UtilValidate.isNotEmpty(availableThruDateStr)) {
			Long availableThruDateP = Long.valueOf(availableThruDateStr);
			availableThruDate = new Timestamp(availableThruDateP);
		}
		
		// minimum order quantity
		BigDecimal minimumOrderQuantity = BigDecimal.ZERO;
		String minimumOrderQuantityStr = null;
		if (obj.containsKey("minimumOrderQuantity")) minimumOrderQuantityStr = cleanString(obj.getString("minimumOrderQuantity"));
		if (UtilValidate.isNotEmpty(minimumOrderQuantityStr)) minimumOrderQuantity = new BigDecimal(minimumOrderQuantityStr);
		
		// last price
		BigDecimal lastPrice = BigDecimal.ZERO;
		String lastPriceStr = null;
		if (obj.containsKey("lastPrice")) lastPriceStr = cleanString(obj.getString("lastPrice"));
		if (UtilValidate.isNotEmpty(lastPriceStr)) lastPrice = new BigDecimal(lastPriceStr);
		
		// shipping price
		BigDecimal shippingPrice = BigDecimal.ZERO;
		String shippingPriceStr = null;
		if (obj.containsKey("shippingPrice")) shippingPriceStr = cleanString(obj.getString("shippingPrice"));
		if (UtilValidate.isNotEmpty(shippingPriceStr)) shippingPrice = new BigDecimal(shippingPriceStr);
		
		String currencyUomId = null;
		if (obj.containsKey("currencyUomId")) {
			currencyUomId = cleanString(obj.getString("currencyUomId"));
		}
		String supplierProductId = null;
		if (obj.containsKey("supplierProductId")) {
			supplierProductId = cleanString(obj.getString("supplierProductId"));
		}
		String canDropShip = null;
		if (obj.containsKey("canDropShip")) {
			canDropShip = cleanString(obj.getString("canDropShip"));
		}
		String comments = null;
		if (obj.containsKey("comments")) {
			comments = cleanString(obj.getString("comments"));
		}
		product.put("productId", productId);
		product.put("partyId", partyId);
		product.put("partyCode", partyCode);
		product.put("supplierPrefOrderId", supplierPrefOrderId);
		product.put("quantityUomId", quantityUomId);
		product.put("availableFromDate", availableFromDate);
		product.put("availableThruDate", availableThruDate);
		product.put("minimumOrderQuantity", minimumOrderQuantity);
		product.put("lastPrice", lastPrice);
		product.put("lastPriceStr", lastPriceStr);
		product.put("shippingPrice", shippingPrice);
		product.put("shippingPriceStr", shippingPriceStr);
		product.put("currencyUomId", currencyUomId);
		product.put("supplierProductId", supplierProductId);
		product.put("canDropShip", canDropShip);
		product.put("comments", comments);
		
		return product;
	}
	
	public static List<GenericValue> getProductFeatureTypes(Delegator delegator) throws GenericEntityException {
		List<GenericValue> productFeatureTypes = FastList.newInstance();
		String featureType = EntityUtilProperties.getPropertyValue("po.properties", "po.product.ProductFeatureType",
				delegator);
		if (UtilValidate.isNotEmpty(featureType)) {
			productFeatureTypes = delegator
					.findList(
							"ProductFeatureType", EntityCondition.makeCondition("productFeatureTypeId",
									EntityJoinOperator.IN, Arrays.asList(featureType.split(";"))),
							null, null, null, false);
		}
		return productFeatureTypes;
	}
	
	public static BigDecimal calculatePriceBeforeTax(BigDecimal priceAfterTax, BigDecimal taxPercentage) {
		if (taxPercentage == null || priceAfterTax == null) return priceAfterTax;
		return priceAfterTax.multiply(PERCENT_SCALE).divide(taxPercentage.add(PERCENT_SCALE), salestaxCalcDecimals, salestaxRounding);
	}
	public static BigDecimal calculatePriceAfterTax(BigDecimal priceBeforeTax, BigDecimal taxPercentage) {
		if (taxPercentage == null || priceBeforeTax == null) return priceBeforeTax;
		return priceBeforeTax.multiply(taxPercentage.add(PERCENT_SCALE)).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding);
	}
	
	public static Map<String, Object> createOrStoreProductPrice(String productId, String currencyUomId, String quantityUomId, String taxInPrice,
			BigDecimal productDefaultPrice, BigDecimal productListPrice, BigDecimal taxAmountDefaultPrice, BigDecimal taxAmountListPrice, 
			Boolean isPriceIncludedVat, BigDecimal taxPercentage, Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin) throws GenericServiceException, GenericEntityException {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		Map<String, Object> productCtx = FastMap.newInstance();
		List<EntityCondition> conditions = FastList.newInstance();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		// create or update product price with type is "DEFAULT_PRICE"
		if (isPriceIncludedVat && taxPercentage != null) {
			BigDecimal productDefaultPriceNew = calculatePriceBeforeTax(productDefaultPrice, taxPercentage);
			taxAmountDefaultPrice = productDefaultPrice.subtract(productDefaultPriceNew);
			productDefaultPrice = productDefaultPriceNew;
		}
		conditions.clear();
		conditions.add(EntityUtil.getFilterByDateExpr());
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, 
							"productPriceTypeId", "DEFAULT_PRICE", "productPricePurposeId", "PURCHASE", 
							"termUomId", quantityUomId, "productStoreGroupId", "_NA_"))); //"currencyUomId", currencyUomId, "taxInPrice", context.get("taxInPrice")
		List<GenericValue> listDefaultPrice = delegator.findList("ProductPrice", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("-fromDate"), null, false);
		boolean isFoundDefaultPrice = false;
		if (UtilValidate.isNotEmpty(listDefaultPrice)) {
			BigDecimal tmpPriceSum = null;
			for (GenericValue item : listDefaultPrice) {
				tmpPriceSum = item.getBigDecimal("price");
				//if (isPriceIncludedVat && item.get("taxAmount") != null && tmpPriceSum != null) tmpPriceSum = tmpPriceSum.add(item.getBigDecimal("taxAmount"));
				
				if (isFoundDefaultPrice || productDefaultPrice == null || tmpPriceSum == null 
						|| productDefaultPrice.compareTo(tmpPriceSum) != 0 
						|| (currencyUomId != null && !currencyUomId.equals(item.getString("currencyUomId")))) {
					item.set("thruDate", nowTimestamp);
				} else {
					isFoundDefaultPrice = true;
				}
			}
			delegator.storeAll(listDefaultPrice);
			successResult.put("listDefaultPrice", listDefaultPrice);
		}
		if (!isFoundDefaultPrice && UtilValidate.isNotEmpty(productDefaultPrice)) {
			// create product price with type is "DEFAULT_PRICE"
			
			productCtx.clear();
			productCtx.put("productId", productId);
			productCtx.put("productPriceTypeId", "DEFAULT_PRICE");
			productCtx.put("productPricePurposeId", "PURCHASE");
			productCtx.put("currencyUomId", currencyUomId);
			productCtx.put("termUomId", quantityUomId);
			productCtx.put("productStoreGroupId", "_NA_");
			productCtx.put("price", productDefaultPrice);
			productCtx.put("taxInPrice", taxInPrice);
			//productCtx.put("taxAmount", taxAmountDefaultPrice);
			productCtx.put("userLogin", userLogin);
			Map<String, Object> defaultPriceResult = dispatcher.runSync("createProductPrice", productCtx);
			if (ServiceUtil.isError(defaultPriceResult)) {
				Debug.logError("Error occur when create default price = " + productDefaultPrice + " to product", module);
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(defaultPriceResult));
			}
		}
		/*if (UtilValidate.isEmpty(listProductDefaultPrice) && UtilValidate.isNotEmpty(productDefaultPrice)) {
			// create product price with type is "DEFAULT_PRICE"
			productCtx.clear();
			productCtx.put("productId", productId);
			productCtx.put("productPriceTypeId", "DEFAULT_PRICE");
			productCtx.put("productPricePurposeId", "PURCHASE");
			productCtx.put("currencyUomId", currencyUomId);
			productCtx.put("termUomId", quantityUomId);
			productCtx.put("productStoreGroupId", "_NA_");
			productCtx.put("price", productDefaultPrice);
			productCtx.put("taxInPrice", "N"); //context.get("taxInPrice")
			productCtx.put("userLogin", userLogin);
			Map<String, Object> defaultPriceResult = dispatcher.runSync("createProductPrice", productCtx);
			if (ServiceUtil.isError(defaultPriceResult)) {
				Debug.logError("Error occur when create default price = " + productDefaultPrice + " to product", module);
			}
		} else {
			for (GenericValue x : listProductDefaultPrice) {
				x.set("price", productDefaultPrice);
			}
			delegator.storeAll(listProductDefaultPrice);
		}*/
		
		// create or update product price with type is "LIST_PRICE"
		if (isPriceIncludedVat && taxPercentage != null) {
			BigDecimal productListPriceNew = calculatePriceBeforeTax(productListPrice, taxPercentage);
			taxAmountListPrice = productListPrice.subtract(productListPriceNew);
			productListPrice = productListPriceNew;
		}
		conditions.clear();
		conditions.add(EntityUtil.getFilterByDateExpr());
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, 
							"productPriceTypeId", "LIST_PRICE", "productPricePurposeId", "PURCHASE", 
							"termUomId", quantityUomId, "productStoreGroupId", "_NA_"))); //"currencyUomId", currencyUomId, "taxInPrice", context.get("taxInPrice")
		List<GenericValue> listListPrice = delegator.findList("ProductPrice", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("-fromDate"), null, false);
		boolean isFoundListPrice = false;
		if (UtilValidate.isNotEmpty(listListPrice)) {
			BigDecimal tmpPriceSum = null;
			for (GenericValue item : listListPrice) {
				tmpPriceSum = item.getBigDecimal("price");
				//if (isPriceIncludedVat && item.get("taxAmount") != null && tmpPriceSum != null) tmpPriceSum = tmpPriceSum.add(item.getBigDecimal("taxAmount"));
				
				if (isFoundListPrice || productListPrice == null || tmpPriceSum == null 
						|| productListPrice.compareTo(tmpPriceSum) != 0
						|| (currencyUomId != null && !currencyUomId.equals(item.getString("currencyUomId")))) {
					item.set("thruDate", nowTimestamp);
				} else {
					isFoundListPrice = true;
				}
			}
			delegator.storeAll(listListPrice);
			successResult.put("listListPrice", listListPrice);
		}
		if (!isFoundListPrice && UtilValidate.isNotEmpty(productListPrice)) {
			// create product price with type is "LIST_PRICE"
			
			productCtx.clear();
			productCtx.put("productId", productId);
			productCtx.put("productPriceTypeId", "LIST_PRICE");
			productCtx.put("productPricePurposeId", "PURCHASE");
			productCtx.put("currencyUomId", currencyUomId);
			productCtx.put("termUomId", quantityUomId);
			productCtx.put("productStoreGroupId", "_NA_");
			productCtx.put("price", productListPrice);
			productCtx.put("taxInPrice", taxInPrice);
			//productCtx.put("taxAmount", taxAmountListPrice);
			productCtx.put("userLogin", userLogin);
			Map<String, Object> listPriceResult = dispatcher.runSync("createProductPrice", productCtx);
			if (ServiceUtil.isError(listPriceResult)) {
				Debug.logError("Error occur when create default price = " + productListPrice + " to product", module);
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(listPriceResult));
			}
		}
		/*if (UtilValidate.isEmpty(listProductListPrice) && UtilValidate.isNotEmpty(productListPrice)) {
			// create product price with type is "LIST_PRICE"
			productCtx.clear();
			productCtx.put("productId", productId);
			productCtx.put("productPriceTypeId", "LIST_PRICE");
			productCtx.put("productPricePurposeId", "PURCHASE");
			productCtx.put("currencyUomId", currencyUomId);
			productCtx.put("termUomId", quantityUomId);
			productCtx.put("productStoreGroupId", "_NA_");
			productCtx.put("price", productListPrice);
			productCtx.put("taxInPrice", "N"); //context.get("taxInPrice")
			productCtx.put("userLogin", userLogin);
			Map<String, Object> defaultPriceResult = dispatcher.runSync("createProductPrice", productCtx);
			if (ServiceUtil.isError(defaultPriceResult)) {
				Debug.logError("Error occur when create default price = " + productListPrice + " to product", module);
			}
		} else {
			for (GenericValue x : listProductListPrice) {
				x.set("price", productListPrice);
			}
			delegator.storeAll(listProductListPrice);
		}*/
		
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createOrStoreProductAlterUom(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, Locale locale, 
			String productId, String quantityUomId, String alterUomData, String currencyUomId, Boolean isPriceIncludedVat, BigDecimal taxPercentage) throws GenericServiceException, GenericEntityException {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		EntityCondition condsUom = EntityCondition.makeCondition(EntityCondition.makeCondition("productId", productId), EntityOperator.AND, EntityUtil.getFilterByDateExpr());
		List<GenericValue> configUomProductsThru = delegator.findList("ConfigPacking", condsUom, null, null, null, false);
		
		List<EntityCondition> condsPrice = FastList.newInstance();
		condsPrice.add(EntityCondition.makeCondition("productId", productId));
		condsPrice.add(EntityCondition.makeCondition("termUomId", EntityOperator.NOT_EQUAL, quantityUomId));
		condsPrice.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> pricesProductThru = delegator.findList("ProductPrice", EntityCondition.makeCondition(condsPrice), null, null, null, false);
		
		List<EntityCondition> condsBarcode = FastList.newInstance();
		condsBarcode.add(EntityCondition.makeCondition("goodIdentificationTypeId", "SKU"));
		condsBarcode.add(EntityCondition.makeCondition("productId", productId));
		condsBarcode.add(EntityCondition.makeCondition("uomId", EntityOperator.NOT_EQUAL, quantityUomId));
		List<GenericValue> barcodeThru = delegator.findList("GoodIdentification", EntityCondition.makeCondition(condsBarcode), null, null, null, false);
		
		if (UtilValidate.isNotEmpty(alterUomData)) {
			JSONArray childrenArray = JSONArray.fromObject((String) alterUomData);
			for(int i = 0; i< childrenArray.size(); i++){
				JSONObject o = childrenArray.getJSONObject(i);
				String uomFromId = null;
				if (o.containsKey("uomFromId")) uomFromId = o.getString("uomFromId");
				if (UtilValidate.isEmpty(uomFromId) || quantityUomId.equals(uomFromId)) {
					continue;
				}
				
				// quantity convert
				BigDecimal quantityConvert = BigDecimal.ZERO;
				String quantityConvertStr = null;
				if (o.containsKey("quantityConvert")) quantityConvertStr = cleanString(o.getString("quantityConvert"));
				if (UtilValidate.isNotEmpty(quantityConvertStr)) quantityConvert = new BigDecimal(quantityConvertStr);
				
				// available from date
				Timestamp fromDate = null;
				String fromDateStr = null;
				if (o.containsKey("fromDate")) {
					fromDateStr = cleanString(o.getString("fromDate"));
				}
				if (UtilValidate.isNotEmpty(fromDateStr)) {
					Long fromDateP = Long.valueOf(fromDateStr);
					fromDate = new Timestamp(fromDateP);
				}
				
				// available thru date
				Timestamp thruDate = null;
				String thruDateStr = null;
				if (o.containsKey("thruDate")) {
					thruDateStr = cleanString(o.getString("thruDate"));
				}
				if (UtilValidate.isNotEmpty(thruDateStr)) {
					Long thruDateP = Long.valueOf(thruDateStr);
					thruDate = new Timestamp(thruDateP);
				}
				
				GenericValue configPackingGV = delegator.findOne("ConfigPacking", UtilMisc.toMap("productId", productId, "uomFromId", uomFromId, "uomToId", quantityUomId, "fromDate", fromDate), false);
				if (UtilValidate.isNotEmpty(configPackingGV)) {
					if (configUomProductsThru != null) configUomProductsThru.remove(configPackingGV);
					
					configPackingGV.put("thruDate", thruDate);
					configPackingGV.put("quantityConvert", quantityConvert);
					delegator.store(configPackingGV);
				} else {
					GenericValue configPackingNew = delegator.makeValue("ConfigPacking");
					configPackingNew.put("productId", productId);
					configPackingNew.put("uomFromId", uomFromId);
					configPackingNew.put("uomToId", quantityUomId);
					configPackingNew.put("fromDate", UtilDateTime.nowTimestamp());
					configPackingNew.put("quantityConvert", quantityConvert);
					delegator.create(configPackingNew);
				}
				
				// price
				BigDecimal price = null;
				String priceStr = null;
				if (o.containsKey("price")) priceStr = cleanString(o.getString("price"));
				if (UtilValidate.isNotEmpty(priceStr)) price = new BigDecimal(priceStr);
				
				if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
					// tax amount
					BigDecimal taxAmount = null;
					String taxAmountStr = null;
					if (o.containsKey("taxAmount")) taxAmountStr = cleanString(o.getString("taxAmount"));
					if (UtilValidate.isNotEmpty(taxAmountStr)) taxAmount = new BigDecimal(taxAmountStr);
					
					Map<String, Object> resultProductPrice = ProductUtils.createOrStoreProductPrice(productId, currencyUomId, uomFromId, null, 
							price, price, taxAmount, taxAmount, isPriceIncludedVat, taxPercentage, delegator, dispatcher, userLogin);
					if (ServiceUtil.isError(resultProductPrice)) {
						Debug.logError(ServiceUtil.getErrorMessage(resultProductPrice), module);
					}
					List<GenericValue> listDefaultPrice = (List<GenericValue>) resultProductPrice.get("listDefaultPrice");
					List<GenericValue> listListPrice = (List<GenericValue>) resultProductPrice.get("listListPrice");
					if (listDefaultPrice != null) {
						pricesProductThru.removeAll(listDefaultPrice);
					}
					if (listListPrice != null) {
						pricesProductThru.removeAll(listListPrice);
					}
					/*List<EntityCondition> conds = FastList.newInstance();
					conds.add(EntityCondition.makeCondition("productId", productId));
					conds.add(EntityCondition.makeCondition("productPriceTypeId", "DEFAULT_PRICE"));
					conds.add(EntityCondition.makeCondition("termUomId", uomFromId));
					conds.add(EntityCondition.makeCondition("currencyUomId", currencyUomId));
					conds.add(EntityUtil.getFilterByDateExpr());
					List<GenericValue> listDefaultPrice = delegator.findList("ProductPrice", EntityCondition.makeCondition(conds), null, null, null, false);
					if (UtilValidate.isNotEmpty(listDefaultPrice)) {
						pricesProductThru.removeAll(listDefaultPrice);
						for (GenericValue item : listDefaultPrice) {
							if (item.getBigDecimal("price") != null && item.getBigDecimal("price").compareTo(price) != 0) {
								item.set("price", price);
								item.set("taxAmount", taxAmount);
							}
						}
						delegator.storeAll(listDefaultPrice);
					} else {
						Map<String, Object> productCtx = FastMap.newInstance();
						productCtx.put("productId", productId);
						productCtx.put("productPriceTypeId", "DEFAULT_PRICE");
						productCtx.put("productPricePurposeId", "PURCHASE");
						productCtx.put("currencyUomId", currencyUomId);
						productCtx.put("termUomId", uomFromId);
						productCtx.put("productStoreGroupId", "_NA_");
						productCtx.put("price", price);
						productCtx.put("taxAmount", taxAmount);
						//productCtx.put("taxInPrice", "N");
						productCtx.put("userLogin", userLogin);
						Map<String, Object> defaultPriceResult = dispatcher.runSync("createProductPrice", productCtx);
						if (ServiceUtil.isError(defaultPriceResult)) {
							Debug.logError("Error occur when create default price = " + price + " to product", module);
						}
					}
					
					conds.clear();
					conds.add(EntityCondition.makeCondition("productId", productId));
					conds.add(EntityCondition.makeCondition("productPriceTypeId", "LIST_PRICE"));
					conds.add(EntityCondition.makeCondition("termUomId", uomFromId));
					conds.add(EntityCondition.makeCondition("currencyUomId", currencyUomId));
					conds.add(EntityUtil.getFilterByDateExpr());
					List<GenericValue> listListPrice = delegator.findList("ProductPrice", EntityCondition.makeCondition(conds), null, null, null, false);
					if (UtilValidate.isNotEmpty(listListPrice)) {
						pricesProductThru.removeAll(listListPrice);
						for (GenericValue item : listListPrice) {
							if (item.getBigDecimal("price") != null && item.getBigDecimal("price").compareTo(price) != 0) {
								item.set("price", price);
								item.set("taxAmount", taxAmount);
							}
						}
						delegator.storeAll(listListPrice);
					} else {
						Map<String, Object> productCtx = FastMap.newInstance();
						productCtx.put("productId", productId);
						productCtx.put("productPriceTypeId", "LIST_PRICE");
						productCtx.put("productPricePurposeId", "PURCHASE");
						productCtx.put("currencyUomId", currencyUomId);
						productCtx.put("termUomId", uomFromId);
						productCtx.put("productStoreGroupId", "_NA_");
						productCtx.put("price", price);
						productCtx.put("taxAmount", taxAmount);
						//productCtx.put("taxInPrice", "N");
						productCtx.put("userLogin", userLogin);
						Map<String, Object> defaultPriceResult = dispatcher.runSync("createProductPrice", productCtx);
						if (ServiceUtil.isError(defaultPriceResult)) {
							Debug.logError("Error occur when create default price = " + price + " to product", module);
						}
					}*/
				}
				
				// barcode
				String barcodesStr = o.containsKey("barcode") ? o.getString("barcode") : null;
				List<String> barcodes = null;
				if (UtilValidate.isNotEmpty(barcodesStr)) {
					String[] barcodesArr = barcodesStr.split(",");
					if (barcodesArr.length > 0) {
						barcodes = new ArrayList<String>();
						for (int j = 0; j < barcodesArr.length; j++) {
							String bc = barcodesArr[j];
							bc = bc.trim();
							barcodes.add(bc);
						}
					}
				}
				List<GenericValue> listBarcodeGV = delegator.findByAnd("GoodIdentification", UtilMisc.toMap("productId", productId, "uomId", uomFromId, "goodIdentificationTypeId", "SKU"), null, false);
				List<String> barcodesNew = new ArrayList<String>();
				if (UtilValidate.isNotEmpty(barcodes)) {
					// check barcode's existence
					condsBarcode.clear();
					condsBarcode.add(EntityCondition.makeCondition("goodIdentificationTypeId", "SKU"));
					condsBarcode.add(EntityCondition.makeCondition("idValue", EntityOperator.IN, barcodes));
					List<GenericValue> barcodeExisted = delegator.findList("GoodIdentification", EntityCondition.makeCondition(condsBarcode), null, null, null, false);
					if (UtilValidate.isNotEmpty(barcodeExisted)) {
						barcodeExisted.removeAll(listBarcodeGV);
					}
					if (UtilValidate.isNotEmpty(barcodeExisted)) {
						Debug.logWarning("Barcode " + barcodes + " is exists", module);
						return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSBarcodeHasBeenUsedParam", UtilMisc.toMap("barcode", barcodes.toString()), locale));
					}
					
					barcodesNew.addAll(barcodes);
					if (UtilValidate.isNotEmpty(listBarcodeGV)) {
						for (GenericValue item : listBarcodeGV) {
							String itemBarcode = item.getString("idValue");
							if (itemBarcode != null) {
								if (barcodes.contains(itemBarcode)) {
									barcodeThru.remove(item);
									barcodesNew.remove(itemBarcode);
								}
							}
						}
					}
				}
				if (barcodesNew.size() > 0) {
					for (String itemBarcode : barcodesNew) {
						Map<String, Object> goodIdentificationCtx = UtilMisc.toMap("productId", productId, "goodIdentificationTypeId", "SKU", "uomId", uomFromId, "idValue", itemBarcode, "userLogin", userLogin);
						Map<String, Object> createGoodIdenResult = dispatcher.runSync("createGoodIdentification", goodIdentificationCtx);
						if (ServiceUtil.isError(createGoodIdenResult)) {
							return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createGoodIdenResult));
						}
					}
				}
			}
		}
		if (UtilValidate.isNotEmpty(configUomProductsThru)) {
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			for (GenericValue configPackingItem : configUomProductsThru) {
				configPackingItem.set("thruDate", nowTimestamp);
			}
			delegator.storeAll(configUomProductsThru);
		}
		if (UtilValidate.isNotEmpty(pricesProductThru)) {
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			for (GenericValue item : pricesProductThru) {
				item.set("thruDate", nowTimestamp);
			}
			delegator.storeAll(pricesProductThru);
		}
		if (UtilValidate.isNotEmpty(barcodeThru)) {
			delegator.removeAll(barcodeThru);
		}
		return successResult;
	}
	
	public static List<String> splitBarcodeArrayString(String barcodesStr) {
		List<String> barcodes = null;
		if (UtilValidate.isNotEmpty(barcodesStr)) {
			String[] barcodesArr = barcodesStr.split(",");
			if (barcodesArr.length > 0) {
				barcodes = new ArrayList<String>();
				for (int j = 0; j < barcodesArr.length; j++) {
					String bc = barcodesArr[j];
					bc = bc.trim();
					barcodes.add(bc);
				}
			}
		}
		return barcodes;
	}
	
	public static Map<String, String> splitBarcodeWithIupprmArrayString(String barcodesStr) {
		Map<String, String> parts = null;
		if (UtilValidate.isNotEmpty(barcodesStr)) {
			String[] barcodesArr = barcodesStr.split(",");
			if (barcodesArr.length > 0) {
				parts = FastMap.newInstance();
				for (int j = 0; j < barcodesArr.length; j++) {
					String bc = barcodesArr[j];
					bc = bc.trim();
					if (UtilValidate.isNotEmpty(bc)) {
						String[] partArr = bc.split("@");
						if (partArr != null && partArr.length > 0) {
							String barcode = partArr[0];
							String iupprm = partArr.length > 1 ? partArr[1] : null;
							if (UtilValidate.isNotEmpty(barcode)) {
								parts.put(barcode, iupprm);
							}
						}
					}
				}
			}
		}
		return parts;
	}
	
	public static Map<String, Object> createOrStoreBarcodeProduct(Delegator delegator, LocalDispatcher dispatcher, Locale locale, GenericValue userLogin, 
			String productId, String uomId, String barcodesStr, Boolean isSyncThread) throws GenericEntityException, GenericServiceException{
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		List<EntityCondition> condsBarcode = FastList.newInstance();
		condsBarcode.add(EntityCondition.makeCondition("goodIdentificationTypeId", "SKU"));
		condsBarcode.add(EntityCondition.makeCondition("productId", productId));
		condsBarcode.add(EntityCondition.makeCondition("uomId", uomId));
		List<GenericValue> barcodeThru = delegator.findList("GoodIdentification", EntityCondition.makeCondition(condsBarcode), null, null, null, false);
		
		List<String> barcodes = null;
		Map<String, String> barcodesIupprmMap = FastMap.newInstance();
		if (isSyncThread) {
			// process barcode format with iupprm
			barcodesIupprmMap = splitBarcodeWithIupprmArrayString(barcodesStr);
			if (UtilValidate.isNotEmpty(barcodesIupprmMap)) {
				Set<String> keyTmp = barcodesIupprmMap.keySet();
				if (keyTmp != null) barcodes = new ArrayList<String>(keyTmp);
			}
		} else {
			// normal process
			barcodes = splitBarcodeArrayString(barcodesStr);
		}
		List<GenericValue> listBarcodeGV = delegator.findByAnd("GoodIdentification", UtilMisc.toMap("productId", productId, "uomId", uomId, "goodIdentificationTypeId", "SKU"), null, false);
		if (UtilValidate.isNotEmpty(barcodes)) {
			// check barcode's existence
			condsBarcode.clear();
			condsBarcode.add(EntityCondition.makeCondition("goodIdentificationTypeId", "SKU"));
			condsBarcode.add(EntityCondition.makeCondition("idValue", EntityOperator.IN, barcodes));
			List<GenericValue> barcodeExisted = delegator.findList("GoodIdentification", EntityCondition.makeCondition(condsBarcode), null, null, null, false);
			if (UtilValidate.isNotEmpty(barcodeExisted)) {
				barcodeExisted.removeAll(listBarcodeGV);
			}
			if (UtilValidate.isNotEmpty(barcodeExisted)) {
				Debug.logWarning("Barcode " + barcodes + " is exists", module);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSBarcodeHasBeenUsedParam", UtilMisc.toMap("barcode", barcodes.toString()), locale));
			}
			
			if (isSyncThread) {
				// process barcode format with iupprm
				List<GenericValue> barcodesUpdate = FastList.newInstance();
				Map<String, String> barcodesNewMap = FastMap.newInstance();
				barcodesNewMap.putAll(barcodesIupprmMap);
				if (UtilValidate.isNotEmpty(listBarcodeGV)) {
					for (GenericValue item : listBarcodeGV) {
						String itemBarcode = item.getString("idValue");
						if (itemBarcode != null) {
							if (barcodes.contains(itemBarcode)) {
								barcodeThru.remove(item);
								barcodesNewMap.remove(itemBarcode);
								
								Long iupprm = item.getLong("iupprm");
								Long iupprmNew = null;
								String iupprmNewStr = barcodesIupprmMap.get(itemBarcode);
								if (UtilValidate.isNotEmpty(iupprmNewStr)) {
									try {
										iupprmNew = new Long(iupprmNewStr);
									} catch (Exception e) {
										Debug.logWarning(e, module);
									}
								}
								if ((iupprmNew != null && !iupprmNew.equals(iupprm)) || (iupprmNew == null && iupprm != null)) {
									GenericValue itemCopy = delegator.makeValue("GoodIdentification");
									itemCopy.putAll(item.getAllFields());
									itemCopy.put("iupprm", iupprmNew);
									barcodesUpdate.add(itemCopy);
								}
							}
						}
					}
				}
				if (barcodesNewMap.size() > 0) {
					for (Map.Entry<String, String> entry : barcodesNewMap.entrySet()) {
						String itemBarcode = entry.getKey();
						Long iupprmNew = null;
						String iupprmNewStr = entry.getValue();
						if (UtilValidate.isNotEmpty(iupprmNewStr)) {
							try {
								iupprmNew = new Long(iupprmNewStr);
							} catch (Exception e) {
								Debug.logWarning(e, module);
							}
						}
						Map<String, Object> goodIdentificationCtx = UtilMisc.toMap("productId", productId, "goodIdentificationTypeId", "SKU", "uomId", uomId, 
								"idValue", itemBarcode, "iupprm", iupprmNew, "userLogin", userLogin);
						Map<String, Object> createGoodIdenResult = dispatcher.runSync("createGoodIdentification", goodIdentificationCtx);
						if (ServiceUtil.isError(createGoodIdenResult)) {
							return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createGoodIdenResult));
						}
					}
				}
				if (UtilValidate.isNotEmpty(barcodesUpdate)) {
					delegator.storeAll(barcodesUpdate);
				}
			} else {
				// normal process
				List<String> barcodesNew = new ArrayList<String>();
				barcodesNew.addAll(barcodes);
				if (UtilValidate.isNotEmpty(listBarcodeGV)) {
					for (GenericValue item : listBarcodeGV) {
						String itemBarcode = item.getString("idValue");
						if (itemBarcode != null) {
							if (barcodes.contains(itemBarcode)) {
								barcodeThru.remove(item);
								barcodesNew.remove(itemBarcode);
							}
						}
					}
				}
				if (barcodesNew.size() > 0) {
					for (String itemBarcode : barcodesNew) {
						Map<String, Object> goodIdentificationCtx = UtilMisc.toMap("productId", productId, "goodIdentificationTypeId", "SKU", "uomId", uomId, "idValue", itemBarcode, "userLogin", userLogin);
						Map<String, Object> createGoodIdenResult = dispatcher.runSync("createGoodIdentification", goodIdentificationCtx);
						if (ServiceUtil.isError(createGoodIdenResult)) {
							return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createGoodIdenResult));
						}
					}
				}
			}
		}
		
		if (UtilValidate.isNotEmpty(barcodeThru)) {
			delegator.removeAll(barcodeThru);
		}
		
		return successResult;
	}
	
	public static Map<String, Object> createOrStorePLUCodeProduct(Delegator delegator, LocalDispatcher dispatcher, Locale locale, GenericValue userLogin, String productId, String uomId, String idPLUCodesStr) throws GenericEntityException, GenericServiceException{
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		List<EntityCondition> condsBarcode = FastList.newInstance();
		condsBarcode.add(EntityCondition.makeCondition("goodIdentificationTypeId", "PLU"));
		condsBarcode.add(EntityCondition.makeCondition("productId", productId));
		condsBarcode.add(EntityCondition.makeCondition("uomId", uomId));
		List<GenericValue> barcodeThru = delegator.findList("GoodIdentification", EntityCondition.makeCondition(condsBarcode), null, null, null, false);
		
		List<String> idPLUCodes = null;
		if (UtilValidate.isNotEmpty(idPLUCodesStr)) {
			String[] barcodesArr = idPLUCodesStr.split(",");
			if (barcodesArr.length > 0) {
				idPLUCodes = new ArrayList<String>();
				for (int j = 0; j < barcodesArr.length; j++) {
					String bc = barcodesArr[j];
					bc = bc.trim();
					idPLUCodes.add(bc);
				}
			}
		}
		List<GenericValue> listIdPLUCodeGV = delegator.findByAnd("GoodIdentification", UtilMisc.toMap("productId", productId, "uomId", uomId, "goodIdentificationTypeId", "PLU"), null, false);
		List<String> barcodesNew = new ArrayList<String>();
		if (UtilValidate.isNotEmpty(idPLUCodes)) {
			// check barcode's existence
			condsBarcode.clear();
			condsBarcode.add(EntityCondition.makeCondition("goodIdentificationTypeId", "PLU"));
			condsBarcode.add(EntityCondition.makeCondition("idValue", EntityOperator.IN, idPLUCodes));
			List<GenericValue> idPLUCodeExisted = delegator.findList("GoodIdentification", EntityCondition.makeCondition(condsBarcode), null, null, null, false);
			if (UtilValidate.isNotEmpty(idPLUCodeExisted)) {
				idPLUCodeExisted.removeAll(listIdPLUCodeGV);
			}
			if (UtilValidate.isNotEmpty(idPLUCodeExisted)) {
				Debug.logWarning("PLU code " + idPLUCodes + " is exists", module);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSPLUCodeHasBeenUsedParam", UtilMisc.toMap("idPLUCode", idPLUCodes.toString()), locale));
			}
			
			barcodesNew.addAll(idPLUCodes);
			if (UtilValidate.isNotEmpty(listIdPLUCodeGV)) {
				for (GenericValue item : listIdPLUCodeGV) {
					String itemBarcode = item.getString("idValue");
					if (itemBarcode != null) {
						if (idPLUCodes.contains(itemBarcode)) {
							barcodeThru.remove(item);
							barcodesNew.remove(itemBarcode);
						}
					}
				}
			}
		}
		if (barcodesNew.size() > 0) {
			for (String itemBarcode : barcodesNew) {
				Map<String, Object> goodIdentificationCtx = UtilMisc.toMap("productId", productId, "goodIdentificationTypeId", "PLU", "uomId", uomId, "idValue", itemBarcode, "userLogin", userLogin);
				Map<String, Object> createGoodIdenResult = dispatcher.runSync("createGoodIdentification", goodIdentificationCtx);
				if (ServiceUtil.isError(createGoodIdenResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createGoodIdenResult));
				}
			}
		}
		
		return successResult;
	}
	
	public static Map<String, Object> createOrStoreProductConfigItem(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, Locale locale, 
			String productId, String productConfigItemData) throws GenericServiceException, GenericEntityException, GeneralServiceException {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		if (UtilValidate.isNotEmpty(productConfigItemData)) {
			// product config item
			String configItemId = null;
			List<GenericValue> listProductConfig = delegator.findList("ProductConfig", 
					EntityCondition.makeCondition(EntityCondition.makeCondition("productId", productId), EntityOperator.AND, EntityUtil.getFilterByDateExpr()), 
					UtilMisc.toSet("configItemId"), UtilMisc.toList("sequenceNum"), null, false);
			if (UtilValidate.isNotEmpty(listProductConfig)) {
				configItemId = listProductConfig.get(0).getString("configItemId");
			} else {
				// create new product config item
				String configItemName = "Config Item for product " + productId;
				Map<String, Object> prodConfigItemCtx = UtilMisc.toMap("configItemTypeId", "SINGLE", "configItemName", configItemName, "userLogin", userLogin, "locale", locale);
				Map<String, Object> createProdConfigItemResult = dispatcher.runSync("createProductConfigItem", prodConfigItemCtx);
				if (ServiceUtil.isError(createProdConfigItemResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createProdConfigItemResult));
				}
				configItemId = (String) createProdConfigItemResult.get("configItemId");
				
				Map<String, Object> prodConfigCtx = UtilMisc.toMap("productId", productId, "configItemId", configItemId, 
						"description", "Default", "configTypeId", "STANDARD", "isMandatory", "Y", "sequenceNum", new Long(1), 
						"userLogin", userLogin, "locale", locale);
				Map<String, Object> createProdConfigResult = dispatcher.runSync("createProductConfig", prodConfigCtx);
				if (ServiceUtil.isError(createProdConfigResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createProdConfigResult));
				}
			}
			
			if (UtilValidate.isEmpty(configItemId)) {
				return ServiceUtil.returnError("Error when process config item default!");
			}
			
			// product config option
			String configOptionId = null;
			List<GenericValue> listProductConfigOption = delegator.findList("ProductConfigOption", 
					EntityCondition.makeCondition(EntityCondition.makeCondition("configItemId", configItemId)), 
					UtilMisc.toSet("configOptionId"), UtilMisc.toList("sequenceNum"), null, false);
			if (UtilValidate.isNotEmpty(listProductConfigOption)) {
				configOptionId = listProductConfigOption.get(0).getString("configOptionId");
			} else {
				// create new product config option
				String configOptionName = "Config Option for product " + productId;
				Map<String, Object> prodConfigOptionCtx = UtilMisc.toMap("configItemId", configItemId, "configOptionName", configOptionName, "userLogin", userLogin, "locale", locale);
				Map<String, Object> createProdConfigOptionResult = dispatcher.runSync("createProductConfigOption", prodConfigOptionCtx);
				if (ServiceUtil.isError(createProdConfigOptionResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createProdConfigOptionResult));
				}
				configOptionId = (String) createProdConfigOptionResult.get("configOptionId");
			}
			
			if (UtilValidate.isEmpty(configOptionId)) {
				return ServiceUtil.returnError("Error when process config option default!");
			}
			
			List<GenericValue> prodConfigProductThru = delegator.findList("ProductConfigProduct", EntityCondition.makeCondition(UtilMisc.toMap("configItemId", configItemId, "configOptionId", configOptionId)), null, null, null, false);
			
			// process list item
			JSONArray childrenArray = JSONArray.fromObject((String) productConfigItemData);
			for(int i = 0; i< childrenArray.size(); i++){
				JSONObject o = childrenArray.getJSONObject(i);
				
				String productChildId = null;
				if (o.containsKey("productId")) productChildId = o.getString("productId");
				
				// quantity
				BigDecimal quantity = BigDecimal.ZERO;
				String quantityStr = null;
				if (o.containsKey("quantity")) quantityStr = cleanString(o.getString("quantity"));
				if (UtilValidate.isNotEmpty(quantityStr)) quantity = new BigDecimal(quantityStr);
				
				// amount
				BigDecimal amount = null;
				String amountStr = null;
				if (o.containsKey("amount")) amountStr = cleanString(o.getString("amount"));
				if (UtilValidate.isNotEmpty(amountStr)) amount = new BigDecimal(amountStr);
				if (amount != null && amount.compareTo(BigDecimal.ZERO) < 0) amount = null;
				
				// sequence number
				Long sequenceNum = null;
				String sequenceNumStr = null;
				if (o.containsKey("sequenceNum")) sequenceNumStr = cleanString(o.getString("sequenceNum"));
				if (UtilValidate.isNotEmpty(sequenceNumStr)) sequenceNum = new Long(sequenceNumStr);
				if (sequenceNum != null && sequenceNum <= 0) sequenceNum = null;
				
				// product config products
				List<GenericValue> listProdConfigProduct = EntityUtil.filterByCondition(prodConfigProductThru, EntityCondition.makeCondition("productId", productChildId));
				if (UtilValidate.isNotEmpty(listProdConfigProduct)) {
					// update
					for (GenericValue item : listProdConfigProduct) {
						prodConfigProductThru.remove(item);
						Map<String, Object> prodConfigProductCtx = item.getAllFields();
						prodConfigProductCtx.put("quantity", quantity);
						prodConfigProductCtx.put("amount", amount);
						prodConfigProductCtx.put("sequenceNum", sequenceNum);
						prodConfigProductCtx.put("userLogin", userLogin);
						prodConfigProductCtx.put("locale", locale);
						prodConfigProductCtx.remove("createdStamp");
						prodConfigProductCtx.remove("createdTxStamp");
						prodConfigProductCtx.remove("lastUpdatedStamp");
						prodConfigProductCtx.remove("lastUpdatedTxStamp");
						Map<String, Object> updateProdConfigProdResult = dispatcher.runSync("updateProductConfigProduct", prodConfigProductCtx);
						if (ServiceUtil.isError(updateProdConfigProdResult)) {
							return ServiceUtil.returnError(ServiceUtil.getErrorMessage(updateProdConfigProdResult));
						}
					}
				} else {
					// create
					Map<String, Object> prodConfigProductCtx = UtilMisc.toMap(
							"configItemId", configItemId, "configOptionId", configOptionId, "productId", productChildId, 
							"quantity", quantity, "amount", amount, "sequenceNum", sequenceNum, 
							"userLogin", userLogin, "locale", locale);
					Map<String, Object> createProdConfigProdResult = dispatcher.runSync("createProductConfigProduct", prodConfigProductCtx);
					if (ServiceUtil.isError(createProdConfigProdResult)) {
						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createProdConfigProdResult));
					}
				}
			}
			
			if (UtilValidate.isNotEmpty(prodConfigProductThru)) {
				delegator.removeAll(prodConfigProductThru);
			}
		}
		
		return successResult;
	}
}
