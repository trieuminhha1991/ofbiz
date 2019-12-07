/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package com.olbius.product.price;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.uom.UomWorker;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * PriceServices - Workers and Services class for product price related functionality
 */
public class PricePurchaseServices {

    public static final String module = PricePurchaseServices.class.getName();
    public static final String resource = "ProductUiLabels";
    public static final BigDecimal ONE_BASE = BigDecimal.ONE;
    public static final BigDecimal PERCENT_SCALE = new BigDecimal("100.000");

    public static final int taxCalcScale = UtilNumber.getBigDecimalScale("salestax.calc.decimals");
    public static final int taxFinalScale = UtilNumber.getBigDecimalScale("salestax.final.decimals");
    public static final int taxRounding = UtilNumber.getBigDecimalRoundingMode("salestax.rounding");

    /**
     * Calculates the purchase price of a product
     */
    public static Map<String, Object> calculatePurchasePrice(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = FastMap.newInstance();
        Locale locale = (Locale) context.get("locale");

        List<GenericValue> orderItemPriceInfos = FastList.newInstance();
        boolean validPriceFound = false;
        BigDecimal price = BigDecimal.ZERO;
        BigDecimal minimumOrderQuantity = BigDecimal.ZERO; // thangnv

        GenericValue product = (GenericValue)context.get("product");
        String productId = (String) context.get("productId");
        if (product == null && UtilValidate.isEmpty(productId)) {
        	return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSProductNotFound", locale));
        }
        if (product == null && UtilValidate.isNotEmpty(productId)) {
        	try {
				product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSProductNotFound", locale));
			}
        }
        if (product == null) {
        	return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSProductNotFound", locale));
        }
        
        productId = product.getString("productId");
        String currencyUomId = (String)context.get("currencyUomId");
        String partyId = (String)context.get("partyId");
        BigDecimal quantity = (BigDecimal)context.get("quantity");
        String quantityUomId = (String) context.get("quantityUomId");
        
        //TODOCHANGE moment time
        Timestamp moment = (Timestamp) context.get("moment");
        if (moment == null) moment = UtilDateTime.nowTimestamp();
        // end new
        
        // TODOCHANGE calculate quantity convert
        BigDecimal quantityCalc = null;
        BigDecimal quantityConvert = null;
        String quantityUomIdDefault = product.getString("quantityUomId");
        if (quantityUomIdDefault != null && !quantityUomIdDefault.equals(quantityUomId)) {
        	quantityConvert = UomWorker.customConvertUom(productId, quantityUomId, quantityUomIdDefault, BigDecimal.ONE, delegator);
        }
        if (quantityConvert == null) quantityConvert = BigDecimal.ONE;
        if (quantity != null) quantityCalc = quantity.multiply(quantityConvert);
        // END CHANGE

        // a) Get the Price from the Agreement* data model
        // TODO: Implement this

        // b) If no price can be found, get the lastPrice from the SupplierProduct entity
        if (!validPriceFound) {
            Map<String, Object> priceContext = UtilMisc.toMap("currencyUomId", currencyUomId, "partyId", partyId, "productId", productId, "quantity", quantityCalc);
            List<GenericValue> productSuppliers = null;
            try {
                Map<String, Object> priceResult = dispatcher.runSync("getSuppliersForProduct", priceContext);
                if (ServiceUtil.isError(priceResult)) {
                    String errMsg = ServiceUtil.getErrorMessage(priceResult);
                    Debug.logError(errMsg, module);
                    return ServiceUtil.returnError(errMsg);
                }
                productSuppliers = UtilGenerics.checkList(priceResult.get("supplierProducts"));
            } catch (GenericServiceException gse) {
                Debug.logError(gse, module);
                return ServiceUtil.returnError(gse.getMessage());
            }
            if (productSuppliers != null) {
                for (GenericValue productSupplier: productSuppliers) {
                    if (!validPriceFound) {
                        price = ((BigDecimal)productSupplier.get("lastPrice"));
                        // thangnv add
                        if (UtilValidate.isNotEmpty(productSupplier.get("minimumOrderQuantity"))) {
                            minimumOrderQuantity = ((BigDecimal)productSupplier.get("minimumOrderQuantity"));
						}
                        validPriceFound = true;
                    }
                    // add a orderItemPriceInfo element too, without orderId or orderItemId
                    StringBuilder priceInfoDescription = new StringBuilder();
                    priceInfoDescription.append(UtilProperties.getMessage(resource, "ProductSupplier", locale));
                    priceInfoDescription.append(" [");
                    priceInfoDescription.append(UtilProperties.getMessage(resource, "ProductSupplierMinimumOrderQuantity", locale));
                    priceInfoDescription.append(productSupplier.getBigDecimal("minimumOrderQuantity"));
                    priceInfoDescription.append(UtilProperties.getMessage(resource, "ProductSupplierLastPrice", locale));
                    priceInfoDescription.append(productSupplier.getBigDecimal("lastPrice"));
                    priceInfoDescription.append("]");
                    GenericValue orderItemPriceInfo = delegator.makeValue("OrderItemPriceInfo");
                    //orderItemPriceInfo.set("productPriceRuleId", productPriceAction.get("productPriceRuleId"));
                    //orderItemPriceInfo.set("productPriceActionSeqId", productPriceAction.get("productPriceActionSeqId"));
                    //orderItemPriceInfo.set("modifyAmount", modifyAmount);
                    // make sure description is <= than 250 chars
                    String priceInfoDescriptionString = priceInfoDescription.toString();
                    if (priceInfoDescriptionString.length() > 250) {
                        priceInfoDescriptionString = priceInfoDescriptionString.substring(0, 250);
                    }
                    orderItemPriceInfo.set("description", priceInfoDescriptionString);
                    orderItemPriceInfos.add(orderItemPriceInfo);
                }
                if (productSuppliers.isEmpty()){
                	// thangnv
                	List<GenericValue> listSupp = FastList.newInstance();
    				try {
    					listSupp = delegator.findList("SupplierProductGroupAndProduct", EntityCondition.makeCondition(UtilMisc.toMap("currencyUomId", currencyUomId, "partyId", partyId, "productId", productId)), null, null, null, false);
    					if (!listSupp.isEmpty()){
    						minimumOrderQuantity = ((BigDecimal)listSupp.get(0).get("minimumOrderQuantity"));
    					}
    				} catch (GenericEntityException e) {
    					String errMsg = "OLBIUS: Fatal error when findList SupplierProductGroupAndProduct: " + e.toString();
    					Debug.logError(e, errMsg, module);
    					return ServiceUtil.returnError(errMsg);
    				}
                }
            } 
        }

        // c) If no price can be found, get the averageCost from the ProductPrice entity
        if (!validPriceFound) {
            List<GenericValue> prices = null;
            try {
                prices = delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", productId,
                        "productPricePurposeId", "PURCHASE"), UtilMisc.toList("-fromDate"), false);

                // if no prices are found; find the prices of the parent product
                if (UtilValidate.isEmpty(prices)) {
                    GenericValue parentProduct = ProductWorker.getParentProduct(productId, delegator);
                    if (parentProduct != null) {
                        String parentProductId = parentProduct.getString("productId");
                        prices = delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", parentProductId,
                                "productPricePurposeId", "PURCHASE"), UtilMisc.toList("-fromDate"), false);
                    }
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }

            // filter out the old prices
            prices = EntityUtil.filterByDate(prices);

            // first check for the AVERAGE_COST price type
            List<GenericValue> pricesToUse = EntityUtil.filterByAnd(prices, UtilMisc.toMap("productPriceTypeId", "AVERAGE_COST"));
            if (UtilValidate.isEmpty(pricesToUse)) {
                // next go with default price
                pricesToUse = EntityUtil.filterByAnd(prices, UtilMisc.toMap("productPriceTypeId", "DEFAULT_PRICE"));
                if (UtilValidate.isEmpty(pricesToUse)) {
                    // finally use list price
                    pricesToUse = EntityUtil.filterByAnd(prices, UtilMisc.toMap("productPriceTypeId", "LIST_PRICE"));
                }
            }

            // use the most current price
            GenericValue thisPrice = EntityUtil.getFirst(pricesToUse);
            if (thisPrice != null) {
                price = thisPrice.getBigDecimal("price");
                validPriceFound = true;
            }
        }
        result.put("price", price);
        result.put("minimumOrderQuantity", minimumOrderQuantity); // thangnv
        
        // TODOCHANGE COPY FROM OTHER PLACE
        if (UtilValidate.isNotEmpty(quantityUomId) && quantityCalc != null) {
        	try {
    	        Map<String, Object> calculateLastPriceCtx = FastMap.newInstance();
    	        calculateLastPriceCtx.put("productId", productId);
    	        calculateLastPriceCtx.put("partyId", partyId);
    	        calculateLastPriceCtx.put("currencyUomId", currencyUomId);
    	        calculateLastPriceCtx.put("uomId", quantityUomId);
    	        calculateLastPriceCtx.put("quantity", String.valueOf(quantityCalc.doubleValue()));
    			Map<String, Object> calculateLastPrice = dispatcher.runSync("getLastPriceBySupplierProductAndQuantity", calculateLastPriceCtx);
    			if (ServiceUtil.isError(calculateLastPrice)) {
    				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(calculateLastPrice));
    			}
    			price = (BigDecimal) calculateLastPrice.get("lastPrice");
    		} catch (GenericServiceException e1) {
    			Debug.logError(e1, "Fatal error calling getLastPriceBySupplierProductAndQuantity service", module);
                return ServiceUtil.returnError("Error when calculate price of product: " + e1.toString());
    		}
        }
        // END CHANGE
        
        // TODOCHANGE BIG NEW PROCESS
        if (price != null && UtilValidate.isNotEmpty(quantityUomId)) {
        	try {
        		boolean optimizeForLargeRuleSet = "Y".equals(context.get("optimizeForLargeRuleSet"));
        		
        		// if this product is variant, find the virtual product and apply checks to it as well
                String virtualProductId = null;
                if ("Y".equals(product.getString("isVariant"))) {
                    try {
                        virtualProductId = ProductWorker.getVariantVirtualId(product);
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Error getting virtual product id from the database while calculating price" + e.toString(), module);
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                                "ProductPriceCannotRetrieveVirtualProductId", UtilMisc.toMap("errorString", e.toString()) , locale));
                    }
                }
                // if currencyUomId is null get from properties file, if nothing there assume USD (USD: American Dollar) for now
                String currencyDefaultUomId = (String) context.get("currencyUomId");
                //String currencyUomIdTo = (String) context.get("currencyUomIdTo"); 
                if (UtilValidate.isEmpty(currencyDefaultUomId)) {
                    currencyDefaultUomId = EntityUtilProperties.getPropertyValue("general", "currency.uom.id.default", "USD", delegator);
                }
                String productStoreGroupId = null;
                String prodCatalogId = null;
                String webSiteId = null;
                String termUomId = quantityUomId;
                String productStoreId = null;
                boolean findAllQuantityPrices = true;
                BigDecimal listPrice = price;
                GenericValue maximumPriceValue = null;
                GenericValue minimumPriceValue = null;
                GenericValue averageCostValue = null;
                GenericValue competitivePriceValue = null;
                GenericValue specialPromoPriceValue = null;
                GenericValue productStore = null;
                String checkIncludeVat = "N";
                // end process parameters
        		
                List<GenericValue> allProductPriceRules = makeProducePriceRuleListCustom(delegator, optimizeForLargeRuleSet, productId, virtualProductId, prodCatalogId, productStoreGroupId, webSiteId, partyId, currencyDefaultUomId, termUomId, productStoreId, moment); //TODOCHANGE moment time, contactMechId
                allProductPriceRules = EntityUtil.filterByCondition(allProductPriceRules, EntityCondition.makeCondition("fromDate", EntityOperator.NOT_EQUAL, null)); // TODOCHANGE filter fromDate of pricing rule
                allProductPriceRules = EntityUtil.filterByDate(allProductPriceRules, moment);

                List<GenericValue> quantityProductPriceRules = null;
                List<GenericValue> nonQuantityProductPriceRules = null;
                if (findAllQuantityPrices) {
                    // split into list with quantity conditions and list without, then iterate through each quantity cond one
                    quantityProductPriceRules = FastList.newInstance();
                    nonQuantityProductPriceRules = FastList.newInstance();
                    for (GenericValue productPriceRule: allProductPriceRules) {
                        List<GenericValue> productPriceCondList = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("productPriceRuleId", productPriceRule.get("productPriceRuleId")), null, true);

                        boolean foundQuantityInputParam = false;
                        // only consider a rule if all conditions except the quantity condition are true
                        boolean allExceptQuantTrue = true;
                        for (GenericValue productPriceCond: productPriceCondList) {
                            if ("PRIP_QUANTITY".equals(productPriceCond.getString("inputParamEnumId"))) {
                                foundQuantityInputParam = true;
                            } else {
                                //old code: if (!checkPriceConditionCustom(productPriceCond, productId, virtualProductId, prodCatalogId, productStoreGroupId, webSiteId, partyId, quantity, listPrice, currencyDefaultUomId, termUomId, delegator, nowTimestamp)) { //TODOCHANGE moment time
                            	if (!checkPriceConditionCustom(productPriceCond, productId, virtualProductId, prodCatalogId, productStoreGroupId, webSiteId, partyId, quantity, listPrice, currencyDefaultUomId, termUomId, delegator, moment)) { //TODOCHANGE moment time
                                    allExceptQuantTrue = false;
                                }
                            }
                        }

                        if (foundQuantityInputParam && allExceptQuantTrue) {
                            quantityProductPriceRules.add(productPriceRule);
                        } else {
                            nonQuantityProductPriceRules.add(productPriceRule);
                        }
                    }
                }

                if (findAllQuantityPrices) {
                    List<Map<String, Object>> allQuantityPrices = FastList.newInstance();

                    // if findAllQuantityPrices then iterate through quantityProductPriceRules
                    // foreach create an entry in the out list and eval that rule and all nonQuantityProductPriceRules rather than a single rule
                    for (GenericValue quantityProductPriceRule: quantityProductPriceRules) {
                        List<GenericValue> ruleListToUse = FastList.newInstance();
                        ruleListToUse.add(quantityProductPriceRule);
                        ruleListToUse.addAll(nonQuantityProductPriceRules);

                        Map<String, Object> quantCalcResults = calcPriceResultFromRulesCustom(ruleListToUse, listPrice, 
                        		maximumPriceValue, minimumPriceValue, validPriceFound,
                                averageCostValue, productId, virtualProductId, prodCatalogId, productStoreGroupId,
                                webSiteId, partyId, null, currencyDefaultUomId, termUomId, delegator, moment, locale);
                        Map<String, Object> quantErrorResult = org.ofbiz.product.price.PriceServices.addGeneralResults(quantCalcResults, competitivePriceValue, specialPromoPriceValue, productStore,
                            checkIncludeVat, currencyDefaultUomId, productId, quantity, partyId, dispatcher, locale);
                        if (quantErrorResult != null) return quantErrorResult;
                        // also add the quantityProductPriceRule to the Map so it can be used for quantity break information
                        quantCalcResults.put("quantityProductPriceRule", quantityProductPriceRule);

                        allQuantityPrices.add(quantCalcResults);
                    }
                    result.put("allQuantityPrices", allQuantityPrices);

                    // use a quantity 1 to get the main price, then fill in the quantity break prices
                    Map<String, Object> calcResults = calcPriceResultFromRulesCustom(allProductPriceRules, listPrice, maximumPriceValue, minimumPriceValue, validPriceFound,
                            averageCostValue, productId, virtualProductId, prodCatalogId, productStoreGroupId,
                            webSiteId, partyId, BigDecimal.ONE, currencyDefaultUomId, termUomId, delegator, moment, locale);
                    result.putAll(calcResults);
                    // The orderItemPriceInfos out parameter requires a special treatment:
                    // the list of OrderItemPriceInfos generated by the price rule is appended to
                    // the existing orderItemPriceInfos list and the aggregated list is returned.
                    List<GenericValue> orderItemPriceInfosFromRule = UtilGenerics.checkList(calcResults.get("orderItemPriceInfos"));
                    if (UtilValidate.isNotEmpty(orderItemPriceInfosFromRule)) {
                        orderItemPriceInfos.addAll(orderItemPriceInfosFromRule);
                    }
                    result.put("orderItemPriceInfos", orderItemPriceInfos);

                    Map<String, Object> errorResult = org.ofbiz.product.price.PriceServices.addGeneralResults(result, competitivePriceValue, specialPromoPriceValue, productStore,
                            checkIncludeVat, currencyDefaultUomId, productId, quantity, partyId, dispatcher, locale);
                    if (errorResult != null) return errorResult;
                } else {
                	Map<String, Object> calcResults = calcPriceResultFromRulesCustom(allProductPriceRules, listPrice, maximumPriceValue, minimumPriceValue, validPriceFound,
                            averageCostValue, productId, virtualProductId, prodCatalogId, productStoreGroupId,
                            webSiteId, partyId, quantity, currencyDefaultUomId, termUomId, delegator, moment, locale);
                    result.putAll(calcResults);
                    // The orderItemPriceInfos out parameter requires a special treatment:
                    // the list of OrderItemPriceInfos generated by the price rule is appended to
                    // the existing orderItemPriceInfos list and the aggregated list is returned.
                    List<GenericValue> orderItemPriceInfosFromRule = UtilGenerics.checkList(calcResults.get("orderItemPriceInfos"));
                    if (UtilValidate.isNotEmpty(orderItemPriceInfosFromRule)) {
                        orderItemPriceInfos.addAll(orderItemPriceInfosFromRule);
                    }
                    result.put("orderItemPriceInfos", orderItemPriceInfos);

                    Map<String, Object> errorResult = org.ofbiz.product.price.PriceServices.addGeneralResults(result, competitivePriceValue, specialPromoPriceValue, productStore,
                        checkIncludeVat, currencyDefaultUomId, productId, quantity, partyId, dispatcher, locale);
                    if (errorResult != null) return errorResult;
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error getting rules from the database while calculating price", module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                        "ProductPriceCannotRetrievePriceRules", UtilMisc.toMap("errorString", e.toString()) , locale));
            }
        }
        // END NEW

        result.put("validPriceFound", Boolean.valueOf(validPriceFound));
        result.put("orderItemPriceInfos", orderItemPriceInfos);
        return result;
    }
    
    /* Add new parameter "nowTimestamp" */
    public static List<GenericValue> makeProducePriceRuleListCustom(Delegator delegator, boolean optimizeForLargeRuleSet, String productId, String virtualProductId, String prodCatalogId, 
			String productStoreGroupId, String webSiteId, String partyId, String currencyUomId, String quantityUomId, String productStoreId, Timestamp nowTimestamp) throws GenericEntityException {
		List<GenericValue> productPriceRules = null;
		
		// At this point we have two options: optimize for large ruleset, or optimize for small ruleset
		// NOTE: This only effects the way that the rules to be evaluated are selected.
		// For large rule sets we can do a cached pre-filter to limit the rules that need to be evaled for a specific product.
		// Genercally I don't think that rule sets will get that big though, so the default is optimize for smaller rule set.
		if (optimizeForLargeRuleSet) {
			// ========= find all rules that must be run for each input type; this is kind of like a pre-filter to slim down the rules to run =========
			// utilTimer.timerString("Before create rule id list", module);
			TreeSet<String> productPriceRuleIds = new TreeSet<String>();
			
			// ------- These are all of the conditions that DON'T depend on the current inputs -------
			
			// by productCategoryId
			// for we will always include any rules that go by category, shouldn't be too many to iterate through each time and will save on cache entries
			// note that we always want to put the category, quantity, etc ones that find all rules with these conditions in separate cache lists so that they can be easily cleared
			Collection<GenericValue> productCategoryIdConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_PROD_CAT_ID"), null, true);
			if (UtilValidate.isNotEmpty(productCategoryIdConds)) {
				for (GenericValue productCategoryIdCond: productCategoryIdConds) {
					productPriceRuleIds.add(productCategoryIdCond.getString("productPriceRuleId"));
				}
			}
			
			// by productFeatureId
			Collection<GenericValue> productFeatureIdConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_PROD_FEAT_ID"), null, true);
			if (UtilValidate.isNotEmpty(productFeatureIdConds)) {
				for (GenericValue productFeatureIdCond: productFeatureIdConds) {
					productPriceRuleIds.add(productFeatureIdCond.getString("productPriceRuleId"));
				}
			}
			
			// by quantity -- should we really do this one, ie is it necessary?
			// we could say that all rules with quantity on them must have one of these other values
			// but, no we'll do it the other way, any that have a quantity will always get compared
			Collection<GenericValue> quantityConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_QUANTITY"), null, true);
			if (UtilValidate.isNotEmpty(quantityConds)) {
				for (GenericValue quantityCond: quantityConds) {
					productPriceRuleIds.add(quantityCond.getString("productPriceRuleId"));
				}
			}
			
			// by roleTypeId
			Collection<GenericValue> roleTypeIdConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_ROLE_TYPE"), null, true);
			if (UtilValidate.isNotEmpty(roleTypeIdConds)) {
				for (GenericValue roleTypeIdCond: roleTypeIdConds) {
					productPriceRuleIds.add(roleTypeIdCond.getString("productPriceRuleId"));
				}
			}
			
			// TODO, not supported yet: by groupPartyId
			// TODO, not supported yet: by partyClassificationGroupId
			// later: (by partyClassificationTypeId)
			
			// by listPrice
			Collection<GenericValue> listPriceConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_LIST_PRICE"), null, true);
			if (UtilValidate.isNotEmpty(listPriceConds)) {
				for (GenericValue listPriceCond: listPriceConds) {
					productPriceRuleIds.add(listPriceCond.getString("productPriceRuleId"));
				}
			}
			
			// ------- These are all of them that DO depend on the current inputs -------
			
			// by productId
			Collection<GenericValue> productIdConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_PRODUCT_ID", "condValue", productId), null, true);
			if (UtilValidate.isNotEmpty(productIdConds)) {
				for (GenericValue productIdCond: productIdConds) {
					productPriceRuleIds.add(productIdCond.getString("productPriceRuleId"));
				}
			}
			
			// by virtualProductId, if not null
			if (virtualProductId != null) {
				Collection<GenericValue> virtualProductIdConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_PRODUCT_ID", "condValue", virtualProductId), null, true);
				if (UtilValidate.isNotEmpty(virtualProductIdConds)) {
					for (GenericValue virtualProductIdCond: virtualProductIdConds) {
						productPriceRuleIds.add(virtualProductIdCond.getString("productPriceRuleId"));
					}
				}
			}
			
			// by prodCatalogId - which is optional in certain cases
			if (UtilValidate.isNotEmpty(prodCatalogId)) {
				Collection<GenericValue> prodCatalogIdConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_PROD_CLG_ID", "condValue", prodCatalogId), null, true);
				if (UtilValidate.isNotEmpty(prodCatalogIdConds)) {
					for (GenericValue prodCatalogIdCond: prodCatalogIdConds) {
						productPriceRuleIds.add(prodCatalogIdCond.getString("productPriceRuleId"));
					}
				}
			}
			
			// by productStoreGroupId
			if (UtilValidate.isNotEmpty(productStoreGroupId)) {
				Collection<GenericValue> storeGroupConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_PROD_SGRP_ID", "condValue", productStoreGroupId), null, true);
				if (UtilValidate.isNotEmpty(storeGroupConds)) {
					for (GenericValue storeGroupCond: storeGroupConds) {
						productPriceRuleIds.add(storeGroupCond.getString("productPriceRuleId"));
					}
				}
			}
			
			// by webSiteId
			if (UtilValidate.isNotEmpty(webSiteId)) {
				Collection<GenericValue> webSiteIdConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_WEBSITE_ID", "condValue", webSiteId), null, true);
				if (UtilValidate.isNotEmpty(webSiteIdConds)) {
					for (GenericValue webSiteIdCond: webSiteIdConds) {
						productPriceRuleIds.add(webSiteIdCond.getString("productPriceRuleId"));
					}
				}
			}
			
			// by partyId
			if (UtilValidate.isNotEmpty(partyId)) {
				Collection<GenericValue> partyIdConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_PARTY_ID", "condValue", partyId), null, true);
				if (UtilValidate.isNotEmpty(partyIdConds)) {
					for (GenericValue partyIdCond: partyIdConds) {
						productPriceRuleIds.add(partyIdCond.getString("productPriceRuleId"));
					}
				}
			}
			
			// by currencyUomId
			Collection<GenericValue> currencyUomIdConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_CURRENCY_UOMID", "condValue", currencyUomId), null, true);
			if (UtilValidate.isNotEmpty(currencyUomIdConds)) {
				for (GenericValue currencyUomIdCond: currencyUomIdConds) {
					productPriceRuleIds.add(currencyUomIdCond.getString("productPriceRuleId"));
				}
			}
			
			// TODOCHANGE new "quantityUomId"
			// by quantityUomId
			Collection<GenericValue> quantityUomIdConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_QUANTITY_UOMID", "condValue", quantityUomId), null, true);
			if (UtilValidate.isNotEmpty(quantityUomIdConds)) {
				for (GenericValue quantityUomIdCond: quantityUomIdConds) {
					productPriceRuleIds.add(quantityUomIdCond.getString("productPriceRuleId"));
				}
			}
			
			productPriceRules = FastList.newInstance();
			for (String productPriceRuleId: productPriceRuleIds) {
				GenericValue productPriceRule = delegator.findOne("ProductPriceRule", UtilMisc.toMap("productPriceRuleId", productPriceRuleId), true);
				if (productPriceRule == null) continue;
					productPriceRules.add(productPriceRule);
			}
		} else {
			// this would be nice, but we can't cache this so easily...
			// List pprExprs = UtilMisc.toList(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
			// EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp()));
			// productPriceRules = delegator.findByOr("ProductPriceRule", pprExprs);
			
			// Code old
			// productPriceRules = delegator.findList("ProductPriceRule", null, null, null, null, true);
			// if (productPriceRules == null) productPriceRules = FastList.newInstance();
			
			// TODOCHANGE Code new
			// check quotation ProductQuotationStoreAppl, ProductQuotationStoreGroupAppl
			List<EntityCondition> listAllCondition = FastList.newInstance();
			
			// get all quotation
			listAllCondition.clear();
			listAllCondition.add(EntityCondition.makeCondition("productQuotationModuleTypeId", "PURCHASE_QUOTATION"));
			listAllCondition.add(EntityCondition.makeCondition("statusId", "QUOTATION_ACCEPTED"));
			listAllCondition.add(EntityUtil.getFilterByDateExpr(nowTimestamp));
			List<String> productQuotationIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductQuotation", 
							EntityCondition.makeCondition(listAllCondition), UtilMisc.toSet("productQuotationId"), null, null, false), "productQuotationId", true); //UtilMisc.toList("-createDate")
			if (UtilValidate.isNotEmpty(productQuotationIds)) {
				// TODOCHANGE 3: new process, filter by product or category
				List<GenericValue> productPriceRulesOrigin = delegator.findList("ProductPriceRule", EntityCondition.makeCondition("productQuotationId", EntityOperator.IN, productQuotationIds), null, null, null, true);
				List<String> productPriceRuleIds = EntityUtil.getFieldListFromEntityList(productPriceRulesOrigin, "productPriceRuleId", true);
				if (UtilValidate.isNotEmpty(productPriceRuleIds)) {
					// TODOCHANGE check product id in product price condition
					// by productId or productCategoryId
					Set<String> prodCategoryIds = com.olbius.product.product.ProductWorker.getAllCategoryIdByProduct(delegator, productId, true, "CATALOG_CATEGORY");
					
					List<EntityCondition> conds = FastList.newInstance();
					List<EntityCondition> condsOr1 = FastList.newInstance();
					List<EntityCondition> condsOr2 = FastList.newInstance();
					condsOr1.add(EntityCondition.makeCondition("inputParamEnumId", "PRIP_PRODUCT_ID"));
					condsOr1.add(EntityCondition.makeCondition("condValue", productId));
					if (UtilValidate.isNotEmpty(prodCategoryIds)) {
						condsOr2.add(EntityCondition.makeCondition("inputParamEnumId", "PRIP_PROD_CAT_ID"));
						condsOr2.add(EntityCondition.makeCondition("condValue", EntityOperator.IN, UtilMisc.toList(prodCategoryIds)));
					}
					conds.add(EntityCondition.makeCondition(EntityCondition.makeCondition(condsOr1), EntityOperator.OR, EntityCondition.makeCondition(condsOr2)));
					conds.add(EntityCondition.makeCondition("productPriceRuleId", EntityOperator.IN, productPriceRuleIds));
					List<GenericValue> productIdConds = delegator.findList("ProductPriceCond", EntityCondition.makeCondition(conds), null, null, null, true);
					if (UtilValidate.isNotEmpty(productIdConds)) {
						List<String> productPriceRuleIdsProcess = EntityUtil.getFieldListFromEntityList(productIdConds, "productPriceRuleId", true);
						productPriceRules = EntityUtil.filterByCondition(productPriceRulesOrigin, EntityCondition.makeCondition("productPriceRuleId", EntityOperator.IN, productPriceRuleIdsProcess));
						productPriceRules = EntityUtil.orderBy(productPriceRules, UtilMisc.toList("-createdStamp"));
					}
				}
			}
		}
		// end change
		
		if (productPriceRules == null) productPriceRules = FastList.newInstance();
		
		return productPriceRules;
    }
    
    public static boolean checkPriceConditionCustom(GenericValue productPriceCond, String productId, String virtualProductId, String prodCatalogId,
            String productStoreGroupId, String webSiteId, String partyId, BigDecimal quantity, BigDecimal listPrice,
            String currencyUomId, String quantityUomId, Delegator delegator, Timestamp nowTimestamp) throws GenericEntityException {
        if (Debug.verboseOn()) Debug.logVerbose("Checking price condition: " + productPriceCond, module);
        int compare = 0;

        if ("PRIP_PRODUCT_ID".equals(productPriceCond.getString("inputParamEnumId"))) {
            compare = productId.compareTo(productPriceCond.getString("condValue"));
        } else if ("PRIP_PROD_CAT_ID".equals(productPriceCond.getString("inputParamEnumId"))) {
            // if a ProductCategoryMember exists for this productId and the specified productCategoryId
            String productCategoryId = productPriceCond.getString("condValue");
            /* CODE OLD
            List<GenericValue> productCategoryMembers = delegator.findByAnd("ProductCategoryMember",
                    UtilMisc.toMap("productId", productId, "productCategoryId", productCategoryId), null, true);
            // and from/thru date within range
            productCategoryMembers = EntityUtil.filterByDate(productCategoryMembers, nowTimestamp, null, null, true);
            // then 0 (equals), otherwise 1 (not equals)
            if (UtilValidate.isNotEmpty(productCategoryMembers)) {
                compare = 0;
            } else {
                compare = 1;
            }
            
            // if there is a virtualProductId, try that given that this one has failed
            // NOTE: this is important becuase of the common scenario where a virtual product is a member of a category but the variants will typically NOT be
            // NOTE: we may want to parameterize this in the future, ie with an indicator on the ProductPriceCond entity
            if (compare == 1 && UtilValidate.isNotEmpty(virtualProductId)) {
                List<GenericValue> virtualProductCategoryMembers = delegator.findByAnd("ProductCategoryMember",
                        UtilMisc.toMap("productId", virtualProductId, "productCategoryId", productCategoryId), null, true);
                // and from/thru date within range
                virtualProductCategoryMembers = EntityUtil.filterByDate(virtualProductCategoryMembers, nowTimestamp, null, null, true);
                if (UtilValidate.isNotEmpty(virtualProductCategoryMembers)) {
                    // we found a member record? great, then this condition is satisfied
                    compare = 0;
                }
            }
            */
            
            // TODOCHANGE process product include sub categories
            Set<String> productCategoryIdes = com.olbius.product.product.ProductWorker.getAllCategoryIdByProduct(delegator, productId, true, null);
            // then 0 (equals), otherwise 1 (not equals)
            if (UtilValidate.isNotEmpty(productCategoryIdes)) {
            	if (productCategoryIdes.contains(productCategoryId)) {
            		compare = 0;
            	} else {
            		compare = 1;
            	}
            } else {
            	compare = 1;
            }
        } else if ("PRIP_PROD_FEAT_ID".equals(productPriceCond.getString("inputParamEnumId"))) {
            // NOTE: DEJ20070130 don't retry this condition with the virtualProductId as well; this breaks various things you might want to do with price rules, like have different pricing for a variant products with a certain distinguishing feature

            // if a ProductFeatureAppl exists for this productId and the specified productFeatureId
            String productFeatureId = productPriceCond.getString("condValue");
            List<GenericValue> productFeatureAppls = delegator.findByAnd("ProductFeatureAppl",
                    UtilMisc.toMap("productId", productId, "productFeatureId", productFeatureId), null, true);
            // and from/thru date within range
            productFeatureAppls = EntityUtil.filterByDate(productFeatureAppls, nowTimestamp, null, null, true);
            // then 0 (equals), otherwise 1 (not equals)
            if (UtilValidate.isNotEmpty(productFeatureAppls)) {
                compare = 0;
            } else {
                compare = 1;
            }
        } else if ("PRIP_PROD_CLG_ID".equals(productPriceCond.getString("inputParamEnumId"))) {
            if (UtilValidate.isNotEmpty(prodCatalogId)) {
                compare = prodCatalogId.compareTo(productPriceCond.getString("condValue"));
            } else {
                // this shouldn't happen because if prodCatalogId is null no PRIP_PROD_CLG_ID prices will be in the list
                compare = 1;
            }
        } else if ("PRIP_PROD_SGRP_ID".equals(productPriceCond.getString("inputParamEnumId"))) {
            if (UtilValidate.isNotEmpty(productStoreGroupId)) {
                compare = productStoreGroupId.compareTo(productPriceCond.getString("condValue"));
            } else {
                compare = 1;
            }
        } else if ("PRIP_WEBSITE_ID".equals(productPriceCond.getString("inputParamEnumId"))) {
            if (UtilValidate.isNotEmpty(webSiteId)) {
                compare = webSiteId.compareTo(productPriceCond.getString("condValue"));
            } else {
                compare = 1;
            }
        } else if ("PRIP_QUANTITY".equals(productPriceCond.getString("inputParamEnumId"))) {
            if (quantity == null) {
                // if no quantity is passed in, assume all quantity conditions pass
                // NOTE: setting compare = 0 won't do the trick here because the condition won't always be or include and equal
                return true;
            } else {
                compare = quantity.compareTo(new BigDecimal(productPriceCond.getString("condValue")));
            }
        } else if ("PRIP_PARTY_ID".equals(productPriceCond.getString("inputParamEnumId"))) {
            if (UtilValidate.isNotEmpty(partyId)) {
                compare = partyId.compareTo(productPriceCond.getString("condValue"));
            } else {
                compare = 1;
            }
        } else if ("PRIP_PARTY_GRP_MEM".equals(productPriceCond.getString("inputParamEnumId"))) {
            if (UtilValidate.isEmpty(partyId)) {
                compare = 1;
            } else {
                String groupPartyId = productPriceCond.getString("condValue");
                if (partyId.equals(groupPartyId)) {
                    compare = 0;
                } else {
                    // look for PartyRelationship with
                    // partyRelationshipTypeId=GROUP_ROLLUP, the partyIdTo is
                    // the group member, so the partyIdFrom is the groupPartyId
                    List<GenericValue> partyRelationshipList = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom", groupPartyId, "partyIdTo", partyId, "partyRelationshipTypeId", "GROUP_ROLLUP"), null, true);
                    // and from/thru date within range
                    partyRelationshipList = EntityUtil.filterByDate(partyRelationshipList, nowTimestamp, null, null, true);
                    // then 0 (equals), otherwise 1 (not equals)
                    if (UtilValidate.isNotEmpty(partyRelationshipList)) {
                        compare = 0;
                    } else {
                        compare = checkConditionPartyHierarchy(delegator, nowTimestamp, groupPartyId, partyId);
                    }
                }
            }
        } else if ("PRIP_PARTY_CLASS".equals(productPriceCond.getString("inputParamEnumId"))) {
            if (UtilValidate.isEmpty(partyId)) {
                compare = 1;
            } else {
                String partyClassificationGroupId = productPriceCond.getString("condValue");
                // find any PartyClassification
                List<GenericValue> partyClassificationList = delegator.findByAnd("PartyClassification", UtilMisc.toMap("partyId", partyId, "partyClassificationGroupId", partyClassificationGroupId), null, true);
                // and from/thru date within range
                partyClassificationList = EntityUtil.filterByDate(partyClassificationList, nowTimestamp, null, null, true);
                // then 0 (equals), otherwise 1 (not equals)
                if (UtilValidate.isNotEmpty(partyClassificationList)) {
                    compare = 0;
                } else {
                    compare = 1;
                }
            }
        } else if ("PRIP_ROLE_TYPE".equals(productPriceCond.getString("inputParamEnumId"))) {
            if (partyId != null) {
                // if a PartyRole exists for this partyId and the specified roleTypeId
                GenericValue partyRole = delegator.findOne("PartyRole",
                        UtilMisc.toMap("partyId", partyId, "roleTypeId", productPriceCond.getString("condValue")), true);

                // then 0 (equals), otherwise 1 (not equals)
                if (partyRole != null) {
                    compare = 0;
                } else {
                    compare = 1;
                }
            } else {
                compare = 1;
            }
        } else if ("PRIP_LIST_PRICE".equals(productPriceCond.getString("inputParamEnumId"))) {
            BigDecimal listPriceValue = listPrice;

            compare = listPriceValue.compareTo(new BigDecimal(productPriceCond.getString("condValue")));
        } else if ("PRIP_CURRENCY_UOMID".equals(productPriceCond.getString("inputParamEnumId"))) {
            compare = currencyUomId.compareTo(productPriceCond.getString("condValue"));
        } else if ("PRIP_QUANTITY_UOMID".equals(productPriceCond.getString("inputParamEnumId"))) {
            compare = quantityUomId.compareTo(productPriceCond.getString("condValue"));
        } else {
            Debug.logWarning("An un-supported productPriceCond input parameter (lhs) was used: " + productPriceCond.getString("inputParamEnumId") + ", returning false, ie check failed", module);
            return false;
        }

        if (Debug.verboseOn()) Debug.logVerbose("Price Condition compare done, compare=" + compare, module);

        if ("PRC_EQ".equals(productPriceCond.getString("operatorEnumId"))) {
            if (compare == 0) return true;
        } else if ("PRC_NEQ".equals(productPriceCond.getString("operatorEnumId"))) {
            if (compare != 0) return true;
        } else if ("PRC_LT".equals(productPriceCond.getString("operatorEnumId"))) {
            if (compare < 0) return true;
        } else if ("PRC_LTE".equals(productPriceCond.getString("operatorEnumId"))) {
            if (compare <= 0) return true;
        } else if ("PRC_GT".equals(productPriceCond.getString("operatorEnumId"))) {
            if (compare > 0) return true;
        } else if ("PRC_GTE".equals(productPriceCond.getString("operatorEnumId"))) {
            if (compare >= 0) return true;
        } else {
            Debug.logWarning("An un-supported productPriceCond condition was used: " + productPriceCond.getString("operatorEnumId") + ", returning false, ie check failed", module);
            return false;
        }
        return false;
    }
    
    private static int checkConditionPartyHierarchy(Delegator delegator, Timestamp nowTimestamp, String groupPartyId, String partyId) throws GenericEntityException{
        List<GenericValue> partyRelationshipList = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partyId, "partyRelationshipTypeId", "GROUP_ROLLUP"), null, true);
        partyRelationshipList = EntityUtil.filterByDate(partyRelationshipList, nowTimestamp, null, null, true);
        for (GenericValue genericValue : partyRelationshipList) {
            String partyIdFrom = (String)genericValue.get("partyIdFrom");
            if (partyIdFrom.equals(groupPartyId)) {
                return 0;
            }
            if (0 == checkConditionPartyHierarchy(delegator, nowTimestamp, groupPartyId, partyIdFrom)) {
                return 0;
            }
        }
        
        return 1;
    }
    
    public static Map<String, Object> calcPriceResultFromRulesCustom(List<GenericValue> productPriceRules, BigDecimal listPrice, 
    		GenericValue maximumPriceValue, GenericValue minimumPriceValue, boolean validPriceFound,
            GenericValue averageCostValue, String productId, String virtualProductId, String prodCatalogId, String productStoreGroupId,
            String webSiteId, String partyId, BigDecimal quantity, String currencyUomId, String quantityUomId, Delegator delegator, Timestamp nowTimestamp,
            Locale locale) throws GenericEntityException {

        Map<String, Object> calcResults = FastMap.newInstance();

        List<GenericValue> orderItemPriceInfos = FastList.newInstance();
        boolean isSale = false;

        // ========= go through each price rule by id and eval all conditions =========
        // utilTimer.timerString("Before eval rules", module);
        int totalConds = 0;
        int totalActions = 0;
        int totalRules = 0;

        // get some of the base values to calculate with
        BigDecimal averageCost = (averageCostValue != null && averageCostValue.get("price") != null) ? averageCostValue.getBigDecimal("price") : listPrice;
        BigDecimal margin = listPrice.subtract(averageCost);

        // calculate running sum based on listPrice and rules found
        BigDecimal price = listPrice;

        for (GenericValue productPriceRule: productPriceRules) {
            String productPriceRuleId = productPriceRule.getString("productPriceRuleId");

            // check from/thru dates
            java.sql.Timestamp fromDate = productPriceRule.getTimestamp("fromDate");
            java.sql.Timestamp thruDate = productPriceRule.getTimestamp("thruDate");

            if (fromDate != null && fromDate.after(nowTimestamp)) {
                // hasn't started yet
                continue;
            }
            if (thruDate != null && thruDate.before(nowTimestamp)) {
                // already expired
                continue;
            }

            // check all conditions
            boolean allTrue = true;
            StringBuilder condsDescription = new StringBuilder();
            List<GenericValue> productPriceConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("productPriceRuleId", productPriceRuleId), null, true);
            for (GenericValue productPriceCond: productPriceConds) {

                totalConds++;

                if (!checkPriceConditionCustom(productPriceCond, productId, virtualProductId, prodCatalogId, productStoreGroupId, webSiteId, partyId, quantity, listPrice, currencyUomId, quantityUomId, delegator, nowTimestamp)) {
                    allTrue = false;
                    break;
                }

                // add condsDescription string entry
                condsDescription.append("[");
                GenericValue inputParamEnum = productPriceCond.getRelatedOne("InputParamEnumeration", true);

                condsDescription.append(inputParamEnum.getString("enumCode"));
                // condsDescription.append(":");
                GenericValue operatorEnum = productPriceCond.getRelatedOne("OperatorEnumeration", true);

                condsDescription.append(operatorEnum.getString("description"));
                // condsDescription.append(":");
                condsDescription.append(productPriceCond.getString("condValue"));
                condsDescription.append("] ");
            }

            // add some info about the prices we are calculating from
            condsDescription.append("[list:");
            condsDescription.append(listPrice);
            condsDescription.append(";avgCost:");
            condsDescription.append(averageCost);
            condsDescription.append(";margin:");
            condsDescription.append(margin);
            condsDescription.append("] ");

            boolean foundFlatOverride = false;

            // if all true, perform all actions
            if (allTrue) {
                // check isSale
                if ("Y".equals(productPriceRule.getString("isSale"))) {
                    isSale = true;
                }

                List<GenericValue> productPriceActions = delegator.findByAnd("ProductPriceAction", UtilMisc.toMap("productPriceRuleId", productPriceRuleId), null, true);
                for (GenericValue productPriceAction: productPriceActions) {

                    totalActions++;

                    // yeah, finally here, perform the action, ie, modify the price
                    BigDecimal modifyAmount = BigDecimal.ZERO;

                    if ("PRICE_FOD".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                    	// TODOCHANGE add new
                    	if (productPriceAction.get("amount") != null) {
                    		modifyAmount = productPriceAction.getBigDecimal("amount");
                    		if (price != null && modifyAmount != null) price = price.add(modifyAmount);
                    	}
                    } else if ("PRICE_FLAT".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                        // this one is a bit different, break out of the loop because we now have our final price
                        foundFlatOverride = true;
                        if (productPriceAction.get("amount") != null) {
                            price = productPriceAction.getBigDecimal("amount");
                        } else {
                            Debug.logInfo("ProductPriceAction had null amount, using default price: " + price + " for product with id " + productId, module);
                            //price = defaultPrice;
                            isSale = false;                // reverse isSale flag, as this sale rule was actually not applied
                        }
                    }

                    // add a orderItemPriceInfo element too, without orderId or orderItemId
                    StringBuilder priceInfoDescription = new StringBuilder();

                    
                    priceInfoDescription.append(condsDescription.toString());
                    priceInfoDescription.append("[");
                    priceInfoDescription.append(UtilProperties.getMessage(resource, "ProductPriceConditionType", locale));
                    priceInfoDescription.append(productPriceAction.getString("productPriceActionTypeId"));
                    priceInfoDescription.append("]");

                    GenericValue orderItemPriceInfo = delegator.makeValue("OrderItemPriceInfo");

                    orderItemPriceInfo.set("productPriceRuleId", productPriceAction.get("productPriceRuleId"));
                    orderItemPriceInfo.set("productPriceActionSeqId", productPriceAction.get("productPriceActionSeqId"));
                    orderItemPriceInfo.set("modifyAmount", modifyAmount);
                    orderItemPriceInfo.set("rateCode", productPriceAction.get("rateCode"));
                    // make sure description is <= than 250 chars
                    String priceInfoDescriptionString = priceInfoDescription.toString();

                    if (priceInfoDescriptionString.length() > 250) {
                        priceInfoDescriptionString = priceInfoDescriptionString.substring(0, 250);
                    }
                    orderItemPriceInfo.set("description", priceInfoDescriptionString);
                    orderItemPriceInfos.add(orderItemPriceInfo);

                    if (foundFlatOverride) {
                        break;
                    } else {
                        price = price.add(modifyAmount);
                    }
                }
            }

            totalRules++;

            if (foundFlatOverride) {
                break;
            }
        }

        if (Debug.verboseOn()) {
            Debug.logVerbose("Unchecked Calculated price: " + price, module);
            Debug.logVerbose("PriceInfo:", module);
            for (GenericValue orderItemPriceInfo: orderItemPriceInfos) {
                Debug.logVerbose(" --- " + orderItemPriceInfo.toString(), module);
            }
        }

        // if no actions were run on the list price, then use the default price
        if (totalActions == 0) {
            //price = defaultPrice;
            // here we will leave validPriceFound as it was originally set for the defaultPrice since that is what we are setting the price to...
        } else {
            // at least one price rule action was found, so we will consider it valid
            validPriceFound = true;
        }

        // ========= ensure calculated price is not below minSalePrice or above maxSalePrice =========
        BigDecimal maxSellPrice = maximumPriceValue != null ? maximumPriceValue.getBigDecimal("price") : null;
        if (maxSellPrice != null && price.compareTo(maxSellPrice) > 0) {
            price = maxSellPrice;
        }
        // min price second to override max price, safety net
        BigDecimal minSellPrice = minimumPriceValue != null ? minimumPriceValue.getBigDecimal("price") : null;
        if (minSellPrice != null && price.compareTo(minSellPrice) < 0) {
            price = minSellPrice;
            // since we have found a minimum price that has overriden a the defaultPrice, even if no valid one was found, we will consider it as if one had been...
            validPriceFound = true;
        }

        if (Debug.verboseOn()) Debug.logVerbose("Final Calculated price: " + price + ", rules: " + totalRules + ", conds: " + totalConds + ", actions: " + totalActions, module);

        calcResults.put("price", price);
        calcResults.put("orderItemPriceInfos", orderItemPriceInfos);
        calcResults.put("isSale", Boolean.valueOf(isSale));
        calcResults.put("validPriceFound", Boolean.valueOf(validPriceFound));

        return calcResults;
    }
}
