package com.olbius.basesales.product;

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
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.uom.UomWorker;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class PriceCustomServices {
	public static final String module = PriceCustomServices.class.getName();
	public static final String resource_old = "ProductUiLabels";
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> calculateProductPriceGroup(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> returnSuccess = ServiceUtil.returnSuccess();

        String productId = (String) context.get("productId");
        String productStoreId = (String) context.get("productStoreId");
        GenericValue product = (GenericValue) context.get("product");
        String quantityUomId = (String) context.get("quantityUomId");
        
        if (product == null && UtilValidate.isEmpty(productId)) {
        	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSProductNotFound", locale));
        }
        try {
	        if (product == null && UtilValidate.isNotEmpty(productId)) {
	        	product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
	        }
	        if (product == null) {
	        	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSProductNotFound", locale));
	        }
	        
	        BigDecimal basePrice = BigDecimal.ZERO; // require
	        BigDecimal price = BigDecimal.ZERO; // require
	        BigDecimal listPrice = null;
	        BigDecimal defaultPrice = null;
	        BigDecimal competitivePrice = null;
	        BigDecimal averageCost = null;
	        BigDecimal promoPrice = null;
	        BigDecimal specialPromoPrice = null;
	        Boolean isSale = Boolean.FALSE; // require
	        Boolean validPriceFound = Boolean.FALSE; // require
	        String currencyUsed = ""; // require
	        List<GenericValue> orderItemPriceInfos = FastList.newInstance(); // require
	        List<Map<String, Object>> allQuantityPrices = FastList.newInstance();
	        
	        String partyId = (String) context.get("partyId");
	        if (UtilValidate.isEmpty(partyId)) {
	        	// get a customer of product store
		        List<EntityCondition> conds = FastList.newInstance();
		        conds.add(EntityCondition.makeCondition("productStoreId", productStoreId));
		        conds.add(EntityCondition.makeCondition("roleTypeId", "CUSTOMER"));
		        conds.add(EntityUtil.getFilterByDateExpr());
		        EntityFindOptions opts = new EntityFindOptions(true,
						EntityFindOptions.TYPE_SCROLL_INSENSITIVE,
						EntityFindOptions.CONCUR_READ_ONLY, false);
				opts.setMaxRows(1);
				opts.setLimit(1);
				opts.setOffset(0);
		        GenericValue customerFirst = EntityUtil.getFirst(delegator.findList("ProductStoreRole", EntityCondition.makeCondition(conds), UtilMisc.toSet("partyId"), null, opts, false));
		        if (customerFirst != null) {
		        	partyId = customerFirst.getString("partyId");
		        }
	        }
	        //if (partyId != null) {
	        	Map<String, Object> calPriceCtx = UtilMisc.<String, Object>toMap(
	        			"product", product, 
	        			"partyId", partyId, 
	        			"productStoreId", productStoreId,
	        			"quantityUomId", quantityUomId);
	        	Map<String, Object> resultCalPrice = dispatcher.runSync("calculateProductPriceCustom", calPriceCtx);
	        	if (ServiceUtil.isError(resultCalPrice)) {
	        		return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCalPrice));
	        	}
	        	basePrice = (BigDecimal) resultCalPrice.get("basePrice");
	        	price = (BigDecimal) resultCalPrice.get("price");
	        	listPrice = (BigDecimal) resultCalPrice.get("listPrice");
	        	defaultPrice = (BigDecimal) resultCalPrice.get("defaultPrice");
	        	competitivePrice = (BigDecimal) resultCalPrice.get("competitivePrice");
	        	averageCost = (BigDecimal) resultCalPrice.get("averageCost");
	        	promoPrice = (BigDecimal) resultCalPrice.get("promoPrice");
	        	specialPromoPrice = (BigDecimal) resultCalPrice.get("specialPromoPrice");
	        	isSale = (Boolean) resultCalPrice.get("isSale");
	        	validPriceFound = (Boolean) resultCalPrice.get("validPriceFound");
	        	currencyUsed = (String) resultCalPrice.get("currencyUsed");
	        	orderItemPriceInfos = (List<GenericValue>) resultCalPrice.get("orderItemPriceInfos");
	        	allQuantityPrices = (List<Map<String, Object>>) resultCalPrice.get("allQuantityPrices");
	        //}
	        
	        returnSuccess.put("basePrice", basePrice);
	        returnSuccess.put("price", price);
	        returnSuccess.put("listPrice", listPrice);
	        returnSuccess.put("defaultPrice", defaultPrice);
	        returnSuccess.put("competitivePrice", competitivePrice);
	        returnSuccess.put("averageCost", averageCost);
	        returnSuccess.put("promoPrice", promoPrice);
	        returnSuccess.put("specialPromoPrice", specialPromoPrice);
	        returnSuccess.put("isSale", isSale);
	        returnSuccess.put("validPriceFound", validPriceFound);
	        returnSuccess.put("currencyUsed", currencyUsed);
	        returnSuccess.put("orderItemPriceInfos", orderItemPriceInfos);
	        returnSuccess.put("allQuantityPrices", allQuantityPrices);
        } catch (Exception e) {
	        Debug.logError(e, "An error occurred while calculate product price for group customer party (calculateProductPriceGroup)", module);
	        return ServiceUtil.returnError("An error occurred while calculate product price for group customer party");
	    }
        
        return returnSuccess;
    }
    
    // TODOCHANE add new "quantityUomId"
    /**
     * <p>Calculates the price of a product from pricing rules given the following input, and of course access to the database:</p>
     * <ul>
     *   <li>productId
     *   <li>partyId
     *   <li>prodCatalogId
     *   <li>webSiteId
     *   <li>productStoreId
     *   <li>productStoreGroupId
     *   <li>agreementId
     *   <li>quantity
     *   <li>currencyUomId
     *   <li>quantityUomId
     *   <li>checkIncludeVat
     * </ul>
     */
	public static Map<String, Object> calculateProductPrice(DispatchContext dctx, Map<String, ? extends Object> context) {
        // UtilTimer utilTimer = new UtilTimer();
        // utilTimer.timerString("Starting price calc", module);
        // utilTimer.setLog(false);

        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = FastMap.newInstance();
        //TODOCHANGE moment time old code: Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        Locale locale = (Locale) context.get("locale");

        GenericValue product = (GenericValue) context.get("product");
        // TODOCHANGE get product by id (parameter)
        String productId = null;
        String productIdParam = (String) context.get("productId");
        if (product == null) {
        	if (UtilValidate.isNotEmpty(productIdParam)) {
        		try {
    				product = delegator.findOne("Product", UtilMisc.toMap("productId", productIdParam), false);
    				if (product != null) productId = product.getString("productId");
    			} catch (GenericEntityException e) {
    				Debug.logError("Error when find product by id is " + productIdParam, module);
    			}
        	}
        } else {
        	productId = product.getString("productId");
        }
        if (product == null) {
        	return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSProductNotFound", locale));
        }
        // end change
        
        //TODOCHANGE moment time
        Timestamp moment = (Timestamp) context.get("moment");
        if (moment == null) moment = UtilDateTime.nowTimestamp();
        // end new
        
        String prodCatalogId = (String) context.get("prodCatalogId");
        String webSiteId = (String) context.get("webSiteId");
        String checkIncludeVat = (String) context.get("checkIncludeVat");
        String surveyResponseId = (String) context.get("surveyResponseId");
        Map<String, Object> customAttributes = UtilGenerics.checkMap(context.get("customAttributes"));

        String findAllQuantityPricesStr = (String) context.get("findAllQuantityPrices");
        boolean findAllQuantityPrices = "Y".equals(findAllQuantityPricesStr);
        boolean optimizeForLargeRuleSet = "Y".equals(context.get("optimizeForLargeRuleSet"));

        String agreementId = (String) context.get("agreementId");

        String productStoreId = (String) context.get("productStoreId");
        String productStoreGroupId = (String) context.get("productStoreGroupId");
        
        GenericValue productStore = null;
        try {
            // we have a productStoreId, if the corresponding ProductStore.primaryStoreGroupId is not empty, use that
            productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), true);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error getting product store info from the database while calculating price" + e.toString(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_old, 
                    "ProductPriceCannotRetrieveProductStore", UtilMisc.toMap("errorString", e.toString()) , locale));
        }
        if (UtilValidate.isEmpty(productStoreGroupId)) {
            if (productStore != null) {
                try {
                    if (UtilValidate.isNotEmpty(productStore.getString("primaryStoreGroupId"))) {
                        productStoreGroupId = productStore.getString("primaryStoreGroupId");
                    } else {
                        // no ProductStore.primaryStoreGroupId, try ProductStoreGroupMember
                        List<GenericValue> productStoreGroupMemberList = delegator.findByAnd("ProductStoreGroupMember", UtilMisc.toMap("productStoreId", productStoreId), UtilMisc.toList("sequenceNum", "-fromDate"), true);
                        //old code: productStoreGroupMemberList = EntityUtil.filterByDate(productStoreGroupMemberList, true); //TODOCHANGE moment time
                        productStoreGroupMemberList = EntityUtil.filterByDate(productStoreGroupMemberList, moment); //TODOCHANGE moment time
                        if (productStoreGroupMemberList.size() > 0) {
                            GenericValue productStoreGroupMember = EntityUtil.getFirst(productStoreGroupMemberList);
                            productStoreGroupId = productStoreGroupMember.getString("productStoreGroupId");
                        }
                    }
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Error getting product store info from the database while calculating price" + e.toString(), module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_old, 
                            "ProductPriceCannotRetrieveProductStore", UtilMisc.toMap("errorString", e.toString()) , locale));
                }
            }

            // still empty, default to _NA_
            if (UtilValidate.isEmpty(productStoreGroupId)) {
                productStoreGroupId = "_NA_";
            }
        }

        // if currencyUomId is null get from properties file, if nothing there assume USD (USD: American Dollar) for now
        String currencyDefaultUomId = (String) context.get("currencyUomId");
        String currencyUomIdTo = (String) context.get("currencyUomIdTo"); 
        if (UtilValidate.isEmpty(currencyDefaultUomId)) {
            currencyDefaultUomId = EntityUtilProperties.getPropertyValue("general", "currency.uom.id.default", "USD", delegator);
        }
        
        // productPricePurposeId is null assume "PURCHASE", which is equivalent to what prices were before the purpose concept
        String productPricePurposeId = (String) context.get("productPricePurposeId");
        if (UtilValidate.isEmpty(productPricePurposeId)) {
            productPricePurposeId = "PURCHASE";
        }

        // termUomId, for things like recurring prices specifies the term (time/frequency measure for example) of the recurrence
        // if this is empty it will simply not be used to constrain the selection
        String termUomId = (String) context.get("termUomId");
        
        // TODOCHANGE add new quantityUomId
        String quantityUomIdDefault = product.getString("quantityUomId");
        String quantityUomIdParam = (String) context.get("quantityUomId");
        if (UtilValidate.isEmpty(quantityUomIdParam)) quantityUomIdParam = quantityUomIdDefault;
        if (UtilValidate.isEmpty(termUomId)) {
        	termUomId = quantityUomIdParam;
        }

        // if this product is variant, find the virtual product and apply checks to it as well
        String virtualProductId = null;
        if ("Y".equals(product.getString("isVariant"))) {
            try {
                virtualProductId = ProductWorker.getVariantVirtualId(product);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error getting virtual product id from the database while calculating price" + e.toString(), module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_old, 
                        "ProductPriceCannotRetrieveVirtualProductId", UtilMisc.toMap("errorString", e.toString()) , locale));
            }
        }

        // get prices for virtual product if one is found; get all ProductPrice entities for this productId and currencyUomId
        List<GenericValue> virtualProductPrices = null;
        if (virtualProductId != null) {
            try {
                virtualProductPrices = delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", virtualProductId, "currencyUomId", currencyDefaultUomId, "productStoreGroupId", productStoreGroupId), UtilMisc.toList("-fromDate"), true);
            } catch (GenericEntityException e) {
                Debug.logError(e, "An error occurred while getting the product prices", module);
            }
            //old code: virtualProductPrices = EntityUtil.filterByDate(virtualProductPrices, true); //TODOCHANGE moment time
            virtualProductPrices = EntityUtil.filterByDate(virtualProductPrices, moment); //TODOCHANGE moment time
        }

        // NOTE: partyId CAN be null
        String partyId = (String) context.get("partyId");
        if (UtilValidate.isEmpty(partyId) && context.get("userLogin") != null) {
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            partyId = userLogin.getString("partyId");
        }

        // check for auto-userlogin for price rules
        if (UtilValidate.isEmpty(partyId) && context.get("autoUserLogin") != null) {
            GenericValue userLogin = (GenericValue) context.get("autoUserLogin");
            partyId = userLogin.getString("partyId");
        }

        BigDecimal quantity = (BigDecimal) context.get("quantity");
        if (quantity == null) quantity = BigDecimal.ONE;

        BigDecimal amount = (BigDecimal) context.get("amount");

        List<EntityCondition> productPriceEcList = FastList.newInstance();
        List<EntityCondition> productPriceEcListBefore = FastList.newInstance(); //TODOCHANGE new list condition without quantity UOM condition
        List<EntityCondition> productPriceEcListStoreGroup = FastList.newInstance(); //TODOCHANGE new list condition without Product store group condition
        productPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
        // this funny statement is for backward compatibility purposes; the productPricePurposeId is a new pk field on the ProductPrice entity and in order databases may not be populated, until the pk is updated and such; this will ease the transition somewhat
        if ("PURCHASE".equals(productPricePurposeId)) {
            productPriceEcList.add(EntityCondition.makeCondition(
                    EntityCondition.makeCondition("productPricePurposeId", EntityOperator.EQUALS, productPricePurposeId),
                    EntityOperator.OR,
                    EntityCondition.makeCondition("productPricePurposeId", EntityOperator.EQUALS, null)));
        } else {
            productPriceEcList.add(EntityCondition.makeCondition("productPricePurposeId", EntityOperator.EQUALS, productPricePurposeId));
        }
        productPriceEcList.add(EntityCondition.makeCondition("currencyUomId", EntityOperator.EQUALS, currencyDefaultUomId));
        productPriceEcListStoreGroup.addAll(productPriceEcList); //TODOCHANGE productStoreGroupId
        productPriceEcList.add(EntityCondition.makeCondition("productStoreGroupId", EntityOperator.EQUALS, productStoreGroupId));
        productPriceEcListBefore.addAll(productPriceEcList); //TODOCHANGE quantityUomId
        if (UtilValidate.isNotEmpty(termUomId)) {//TODOCHANGE used to is quantityUomId
            productPriceEcList.add(EntityCondition.makeCondition("termUomId", EntityOperator.EQUALS, termUomId));
        }
        EntityCondition productPriceEc = EntityCondition.makeCondition(productPriceEcList, EntityOperator.AND);
        
        //TODOCHANGE new quantityUomId
        BigDecimal quantityConvert = BigDecimal.ONE;
        boolean jumpToCalcPriceByUomDefault = false;
        
        // for prices, get all ProductPrice entities for this productId and currencyUomId
        List<GenericValue> productPrices = null;
        try {
            // old filterByDate after: productPrices = delegator.findList("ProductPrice", productPriceEc, null, UtilMisc.toList("-fromDate"), null, true);
        	productPrices = delegator.findList("ProductPrice", EntityCondition.makeCondition(productPriceEc, EntityOperator.AND, EntityUtil.getFilterByDateExpr(moment)), null, UtilMisc.toList("-fromDate"), null, true); //TODOCHANGE moment time
            
            // try again, with default UOM of product, TODOCHANGE add new process
            if (UtilValidate.isEmpty(productPrices) && UtilValidate.isNotEmpty(termUomId)) {
            	// no price has founded with termUomId, let found product prices with default UOM of product
				if (UtilValidate.isNotEmpty(quantityUomIdDefault) && !termUomId.equals(quantityUomIdDefault)) {
					jumpToCalcPriceByUomDefault = true;
					
					BigDecimal quantityConvertTmp = UomWorker.getValueConvertUom(productId, termUomId, quantityUomIdDefault, delegator);
					if (quantityConvertTmp != null) {
						List<EntityCondition> productPriceEcList2 = FastList.newInstance();
						productPriceEcList2.addAll(productPriceEcListBefore);
						productPriceEcList2.add(EntityCondition.makeCondition("termUomId", EntityOperator.EQUALS, quantityUomIdDefault));
						EntityCondition productPriceEc2 = EntityCondition.makeCondition(productPriceEcList2, EntityOperator.AND);
						productPrices = delegator.findList("ProductPrice", EntityCondition.makeCondition(productPriceEc2, EntityOperator.AND, EntityUtil.getFilterByDateExpr(moment)), null, UtilMisc.toList("-fromDate"), null, true); //TODOCHANGE moment time
						if (UtilValidate.isNotEmpty(productPrices)) {
							quantityConvert = quantityConvertTmp;
						}
					}
				}
            }
            
            // TODOCHANGE productStoreGroupId, if list price with ProductStoreGroup of ProductStore is null then check with ProductStoreGroup default is "_NA_"
            if (UtilValidate.isEmpty(productPrices)) {
            	// TODOCHANGE duplicate code
            	List<EntityCondition> condsTmp = FastList.newInstance();
            	condsTmp.addAll(productPriceEcListStoreGroup);
            	if (UtilValidate.isNotEmpty(termUomId)) condsTmp.add(EntityCondition.makeCondition("termUomId", EntityOperator.EQUALS, termUomId));
                condsTmp.add(EntityCondition.makeCondition("productStoreGroupId", "_NA_"));
                condsTmp.add(EntityUtil.getFilterByDateExpr(moment));
                // old filterByDate after: productPrices = delegator.findList("ProductPrice", productPriceEc, null, UtilMisc.toList("-fromDate"), null, true);
            	productPrices = delegator.findList("ProductPrice", EntityCondition.makeCondition(condsTmp, EntityOperator.AND), null, UtilMisc.toList("-fromDate"), null, true);
                
                // try again, with default UOM of product, TODOCHANGE add new process
                if (UtilValidate.isEmpty(productPrices) && UtilValidate.isNotEmpty(termUomId)) {
                	// no price has founded with termUomId, let found product prices with default UOM of product
    				if (UtilValidate.isNotEmpty(quantityUomIdDefault) && !termUomId.equals(quantityUomIdDefault)) {
    					jumpToCalcPriceByUomDefault = true;
    					
    					BigDecimal quantityConvertTmp = UomWorker.getValueConvertUom(productId, termUomId, quantityUomIdDefault, delegator);
    					if (quantityConvertTmp != null) {
    						condsTmp.clear();
    						condsTmp.addAll(productPriceEcListStoreGroup);
    		            	if (UtilValidate.isNotEmpty(termUomId)) condsTmp.add(EntityCondition.makeCondition("termUomId", EntityOperator.EQUALS, quantityUomIdDefault));
    		                condsTmp.add(EntityCondition.makeCondition("productStoreGroupId", "_NA_"));
    		                condsTmp.add(EntityUtil.getFilterByDateExpr(moment));
    						productPrices = delegator.findList("ProductPrice", EntityCondition.makeCondition(condsTmp, EntityOperator.AND), null, UtilMisc.toList("-fromDate"), null, true);
    						if (UtilValidate.isNotEmpty(productPrices)) {
    							quantityConvert = quantityConvertTmp;
    						}
    					}
    				}
                }
                // end duplicate
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "An error occurred while getting the product prices UomWorker.getValueConvertUom", module);
        }
        
        // old filterByDate after: productPrices = EntityUtil.filterByDate(productPrices, true);

        // ===== get the prices we need: list, default, average cost, promo, min, max =====
        // if any of these prices is missing and this product is a variant, default to the corresponding price on the virtual product
        GenericValue listPriceValue = getPriceValueForType("LIST_PRICE", productPrices, virtualProductPrices);
        GenericValue defaultPriceValue = getPriceValueForType("DEFAULT_PRICE", productPrices, virtualProductPrices);

        // If there is an agreement between the company and the client, and there is
        // a price for the product in it, it will override the default price of the
        // ProductPrice entity.
        if (UtilValidate.isNotEmpty(agreementId)) {
            try {
                List<GenericValue> agreementPrices = delegator.findByAnd("AgreementItemAndProductAppl", UtilMisc.toMap("agreementId", agreementId, "productId", productId, "currencyUomId", currencyDefaultUomId), null, false);
                GenericValue agreementPriceValue = EntityUtil.getFirst(agreementPrices);
                if (agreementPriceValue != null && agreementPriceValue.get("price") != null) {
                    defaultPriceValue = agreementPriceValue;
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error getting agreement info from the database while calculating price" + e.toString(), module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_old, 
                        "ProductPriceCannotRetrieveAgreementInfo", UtilMisc.toMap("errorString", e.toString()) , locale));
            }
        }

        GenericValue competitivePriceValue = getPriceValueForType("COMPETITIVE_PRICE", productPrices, virtualProductPrices);
        GenericValue averageCostValue = getPriceValueForType("AVERAGE_COST", productPrices, virtualProductPrices);
        GenericValue promoPriceValue = getPriceValueForType("PROMO_PRICE", productPrices, virtualProductPrices);
        GenericValue minimumPriceValue = getPriceValueForType("MINIMUM_PRICE", productPrices, virtualProductPrices);
        GenericValue maximumPriceValue = getPriceValueForType("MAXIMUM_PRICE", productPrices, virtualProductPrices);
        GenericValue wholesalePriceValue = getPriceValueForType("WHOLESALE_PRICE", productPrices, virtualProductPrices);
        GenericValue specialPromoPriceValue = getPriceValueForType("SPECIAL_PROMO_PRICE", productPrices, virtualProductPrices);

        // now if this is a virtual product check each price type, if doesn't exist get from variant with lowest DEFAULT_PRICE
        if ("Y".equals(product.getString("isVirtual"))) {
            // only do this if there is no default price, consider the others optional for performance reasons
            if (defaultPriceValue == null) {
                // Debug.logInfo("Product isVirtual and there is no default price for ID " + productId + ", trying variant prices", module);

                //use the cache to find the variant with the lowest default price
                try {
                	//old code: List<GenericValue> variantAssocList = EntityUtil.filterByDate(delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId", product.get("productId"), "productAssocTypeId", "PRODUCT_VARIANT"), UtilMisc.toList("-fromDate"), true)); //TODOCHANGE moment time
                	List<GenericValue> variantAssocList = EntityUtil.filterByDate(delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId", product.get("productId"), "productAssocTypeId", "PRODUCT_VARIANT"), UtilMisc.toList("-fromDate"), true), moment); //TODOCHANGE moment time
                    BigDecimal minDefaultPrice = null;
                    List<GenericValue> variantProductPrices = null;
                    @SuppressWarnings("unused")
					String variantProductId = null;
                    for (GenericValue variantAssoc: variantAssocList) {
                        String curVariantProductId = variantAssoc.getString("productIdTo");
                        //old code: List<GenericValue> curVariantPriceList = EntityUtil.filterByDate(delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", curVariantProductId), UtilMisc.toList("-fromDate"), true), nowTimestamp); //TODOCHANGE moment time
                        List<GenericValue> curVariantPriceList = EntityUtil.filterByDate(delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", curVariantProductId), UtilMisc.toList("-fromDate"), true), moment); //TODOCHANGE moment time
                        List<GenericValue> tempDefaultPriceList = EntityUtil.filterByAnd(curVariantPriceList, UtilMisc.toMap("productPriceTypeId", "DEFAULT_PRICE"));
                        GenericValue curDefaultPriceValue = EntityUtil.getFirst(tempDefaultPriceList);
                        if (curDefaultPriceValue != null) {
                            BigDecimal curDefaultPrice = curDefaultPriceValue.getBigDecimal("price");
                            if (minDefaultPrice == null || curDefaultPrice.compareTo(minDefaultPrice) < 0) {
                                // check to see if the product is discontinued for sale before considering it the lowest price
                                GenericValue curVariantProduct = delegator.findOne("Product", UtilMisc.toMap("productId", curVariantProductId), true);
                                if (curVariantProduct != null) {
                                    Timestamp salesDiscontinuationDate = curVariantProduct.getTimestamp("salesDiscontinuationDate");
                                    // old code: if (salesDiscontinuationDate == null || salesDiscontinuationDate.after(nowTimestamp)) { //TODOCHANGE moment time
                                	if (salesDiscontinuationDate == null || salesDiscontinuationDate.after(moment)) {
                                        minDefaultPrice = curDefaultPrice;
                                        variantProductPrices = curVariantPriceList;
                                        variantProductId = curVariantProductId;
                                        // Debug.logInfo("Found new lowest price " + minDefaultPrice + " for variant with ID " + variantProductId, module);
                                    }
                                }
                            }
                        }
                    }

                    if (variantProductPrices != null) {
                        // we have some other options, give 'em a go...
                        if (listPriceValue == null) {
                            listPriceValue = getPriceValueForType("LIST_PRICE", variantProductPrices, null);
                        }
                        if (defaultPriceValue == null) {
                            defaultPriceValue = getPriceValueForType("DEFAULT_PRICE", variantProductPrices, null);
                        }
                        if (competitivePriceValue == null) {
                            competitivePriceValue = getPriceValueForType("COMPETITIVE_PRICE", variantProductPrices, null);
                        }
                        if (averageCostValue == null) {
                            averageCostValue = getPriceValueForType("AVERAGE_COST", variantProductPrices, null);
                        }
                        if (promoPriceValue == null) {
                            promoPriceValue = getPriceValueForType("PROMO_PRICE", variantProductPrices, null);
                        }
                        if (minimumPriceValue == null) {
                            minimumPriceValue = getPriceValueForType("MINIMUM_PRICE", variantProductPrices, null);
                        }
                        if (maximumPriceValue == null) {
                            maximumPriceValue = getPriceValueForType("MAXIMUM_PRICE", variantProductPrices, null);
                        }
                        if (wholesalePriceValue == null) {
                            wholesalePriceValue = getPriceValueForType("WHOLESALE_PRICE", variantProductPrices, null);
                        }
                        if (specialPromoPriceValue == null) {
                            specialPromoPriceValue = getPriceValueForType("SPECIAL_PROMO_PRICE", variantProductPrices, null);
                        }
                    }
                } catch (GenericEntityException e) {
                    Debug.logError(e, "An error occurred while getting the product prices", module);
                }
            }
        }

        //boolean validPromoPriceFound = false;
        BigDecimal promoPrice = BigDecimal.ZERO;
        if (promoPriceValue != null && promoPriceValue.get("price") != null) {
            promoPrice = promoPriceValue.getBigDecimal("price");
            //validPromoPriceFound = true;
        }

        //boolean validWholesalePriceFound = false;
        BigDecimal wholesalePrice = BigDecimal.ZERO;
        if (wholesalePriceValue != null && wholesalePriceValue.get("price") != null) {
            wholesalePrice = wholesalePriceValue.getBigDecimal("price");
            //validWholesalePriceFound = true;
        }

        boolean validPriceFound = false;
        BigDecimal defaultPrice = BigDecimal.ZERO;
        List<GenericValue> orderItemPriceInfos = FastList.newInstance();
        if (defaultPriceValue != null) {
            // If a price calc formula (service) is specified, then use it to get the unit price
            if ("ProductPrice".equals(defaultPriceValue.getEntityName()) && UtilValidate.isNotEmpty(defaultPriceValue.getString("customPriceCalcService"))) {
                GenericValue customMethod = null;
                try {
                    customMethod = defaultPriceValue.getRelatedOne("CustomMethod", false);
                } catch (GenericEntityException gee) {
                    Debug.logError(gee, "An error occurred while getting the customPriceCalcService", module);
                }
                if (UtilValidate.isNotEmpty(customMethod) && UtilValidate.isNotEmpty(customMethod.getString("customMethodName"))) {
                    Map<String, Object> inMap = UtilMisc.toMap("userLogin", context.get("userLogin"), "product", product);
                    inMap.put("initialPrice", defaultPriceValue.getBigDecimal("price"));
                    inMap.put("currencyUomId", currencyDefaultUomId);
                    inMap.put("quantity", quantity);
                    inMap.put("amount", amount);
                    if (UtilValidate.isNotEmpty(surveyResponseId)) {
                        inMap.put("surveyResponseId", surveyResponseId);
                    }
                    if (UtilValidate.isNotEmpty(customAttributes)) {
                        inMap.put("customAttributes", customAttributes);
                    }
                    try {
                        Map<String, Object> outMap = dispatcher.runSync(customMethod.getString("customMethodName"), inMap);
                        if (!ServiceUtil.isError(outMap)) {
                            BigDecimal calculatedDefaultPrice = (BigDecimal)outMap.get("price");
                            orderItemPriceInfos = UtilGenerics.checkList(outMap.get("orderItemPriceInfos"));
                            if (UtilValidate.isNotEmpty(calculatedDefaultPrice)) {
                                defaultPrice = calculatedDefaultPrice;
                                validPriceFound = true;
                            }
                        }
                    } catch (GenericServiceException gse) {
                        Debug.logError(gse, "An error occurred while running the customPriceCalcService [" + customMethod.getString("customMethodName") + "]", module);
                    }
                }
            }
            if (!validPriceFound && defaultPriceValue.get("price") != null) {
                defaultPrice = defaultPriceValue.getBigDecimal("price");
                validPriceFound = true;
            }
        }

        BigDecimal listPrice = listPriceValue != null ? listPriceValue.getBigDecimal("price") : null;
        
        if (listPrice == null) {
            // no list price, use defaultPrice for the final price

            // ========= ensure calculated price is not below minSalePrice or above maxSalePrice =========
            BigDecimal maxSellPrice = maximumPriceValue != null ? maximumPriceValue.getBigDecimal("price") : null;
            if (maxSellPrice != null && defaultPrice.compareTo(maxSellPrice) > 0) {
                defaultPrice = maxSellPrice;
            }
            // min price second to override max price, safety net
            BigDecimal minSellPrice = minimumPriceValue != null ? minimumPriceValue.getBigDecimal("price") : null;
            if (minSellPrice != null && defaultPrice.compareTo(minSellPrice) < 0) {
                defaultPrice = minSellPrice;
                // since we have found a minimum price that has overriden a the defaultPrice, even if no valid one was found, we will consider it as if one had been...
                validPriceFound = true;
            }

            // TODOCHANGE process price, multiply price with quantity converted (if has)
            BigDecimal competitivePriceFinish = competitivePriceValue != null ? competitivePriceValue.getBigDecimal("price") : null;
            BigDecimal averageCostFinish = averageCostValue != null ? averageCostValue.getBigDecimal("price") : null;
            BigDecimal promoPriceFisnish = promoPriceValue != null ? promoPriceValue.getBigDecimal("price") : null;
            BigDecimal specialPromoPriceFinish = specialPromoPriceValue != null ? specialPromoPriceValue.getBigDecimal("price") : null;
            if (quantityConvert != null && quantityConvert.compareTo(BigDecimal.ONE) > 0) {
            	if (defaultPrice != null) defaultPrice = defaultPrice.multiply(quantityConvert);
            	if (competitivePriceFinish != null) competitivePriceFinish = competitivePriceFinish.multiply(quantityConvert);
            	if (averageCostFinish != null) averageCostFinish = averageCostFinish.multiply(quantityConvert);
            	if (promoPriceFisnish != null) promoPriceFisnish = promoPriceFisnish.multiply(quantityConvert);
            	if (specialPromoPriceFinish != null) specialPromoPriceFinish = specialPromoPriceFinish.multiply(quantityConvert);
            }
            result.put("basePrice", defaultPrice);
            result.put("price", defaultPrice);
            result.put("defaultPrice", defaultPrice);
            result.put("competitivePrice", competitivePriceFinish);
            result.put("averageCost", averageCostFinish);
            result.put("promoPrice", promoPriceFisnish);
            result.put("specialPromoPrice", specialPromoPriceFinish);
            result.put("validPriceFound", Boolean.valueOf(validPriceFound));
            result.put("isSale", Boolean.FALSE);
            result.put("orderItemPriceInfos", orderItemPriceInfos);

            Map<String, Object> errorResult = org.ofbiz.product.price.PriceServices.addGeneralResults(result, competitivePriceValue, specialPromoPriceValue, productStore,
                    checkIncludeVat, currencyDefaultUomId, productId, quantity, partyId, dispatcher, locale);
            if (errorResult != null) return errorResult;
        } else {
        	if (jumpToCalcPriceByUomDefault) {
            	// BIG NEW PROCESS
            	try {
            		@SuppressWarnings("unchecked")
        			Map<String, Object> priceCalcCtx = ServiceUtil.setServiceFields(dispatcher, "calculateProductPriceCustom", (Map<String, Object>) context, null, null, locale);
            		priceCalcCtx.put("termUomId", quantityUomIdDefault);
            		priceCalcCtx.put("quantityUomId", quantityUomIdDefault);
            		Map<String, Object> priceCalcResult = dispatcher.runSync("calculateProductPriceCustom", priceCalcCtx);
            		if (ServiceUtil.isError(priceCalcResult)) {
            			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(priceCalcResult));
            		}
            		
            		listPrice = (BigDecimal) priceCalcResult.get("listPrice");
            		defaultPrice = (BigDecimal) priceCalcResult.get("basePrice");
            		validPriceFound = (Boolean) priceCalcResult.get("validPriceFound");
            	} catch (Exception ex) {
                    Debug.logError(ex, "An error occurred while running the calculateProductPriceCustom by quantity uom default [" + quantityUomIdDefault + "]", module);
                }
            }
        	
            try {
                List<GenericValue> allProductPriceRules = makeProducePriceRuleListCustom(delegator, optimizeForLargeRuleSet, productId, virtualProductId, prodCatalogId, productStoreGroupId, webSiteId, partyId, currencyDefaultUomId, termUomId, productStoreId, moment); //TODOCHANGE moment time
                allProductPriceRules = EntityUtil.filterByCondition(allProductPriceRules, EntityCondition.makeCondition("fromDate", EntityOperator.NOT_EQUAL, null)); // TODOCHANGE filter fromDate of pricing rule
                // old code: allProductPriceRules = EntityUtil.filterByDate(allProductPriceRules, true); //TODOCHANGE moment time
                allProductPriceRules = EntityUtil.filterByDate(allProductPriceRules, moment); //TODOCHANGE moment time

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

                        /*old code: Map<String, Object> quantCalcResults = calcPriceResultFromRulesCustom(ruleListToUse, listPrice, defaultPrice, promoPrice,
                            wholesalePrice, maximumPriceValue, minimumPriceValue, validPriceFound,
                            averageCostValue, productId, virtualProductId, prodCatalogId, productStoreGroupId,
                            webSiteId, partyId, null, currencyDefaultUomId, termUomId, delegator, nowTimestamp, locale); //TODOCHANGE moment time */
                        Map<String, Object> quantCalcResults = calcPriceResultFromRulesCustom(ruleListToUse, listPrice, defaultPrice, promoPrice,
                                wholesalePrice, maximumPriceValue, minimumPriceValue, validPriceFound,
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
                    /*old code: Map<String, Object> calcResults = calcPriceResultFromRulesCustom(allProductPriceRules, listPrice, defaultPrice, promoPrice,
                        wholesalePrice, maximumPriceValue, minimumPriceValue, validPriceFound,
                        averageCostValue, productId, virtualProductId, prodCatalogId, productStoreGroupId,
                        webSiteId, partyId, BigDecimal.ONE, currencyDefaultUomId, termUomId, delegator, nowTimestamp, locale); TODOCHANGE moment time*/
                    Map<String, Object> calcResults = calcPriceResultFromRulesCustom(allProductPriceRules, listPrice, defaultPrice, promoPrice,
                            wholesalePrice, maximumPriceValue, minimumPriceValue, validPriceFound,
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
                	if (quantityConvert != null && quantityConvert.compareTo(BigDecimal.ONE) > 0) {
                		if (listPrice != null) listPrice = listPrice.multiply(quantityConvert);
                    	if (defaultPrice != null) defaultPrice = defaultPrice.multiply(quantityConvert);
                    	if (promoPrice != null) promoPrice = promoPrice.multiply(quantityConvert);
                    	if (wholesalePrice != null) wholesalePrice = wholesalePrice.multiply(quantityConvert);
                    }
                    /*old code: Map<String, Object> calcResults = calcPriceResultFromRulesCustom(allProductPriceRules, listPrice, defaultPrice, promoPrice,
                        wholesalePrice, maximumPriceValue, minimumPriceValue, validPriceFound,
                        averageCostValue, productId, virtualProductId, prodCatalogId, productStoreGroupId,
                        webSiteId, partyId, quantity, currencyDefaultUomId, termUomId, delegator, nowTimestamp, locale); //TODOCHANGE moment time */
                	Map<String, Object> calcResults = calcPriceResultFromRulesCustom(allProductPriceRules, listPrice, defaultPrice, promoPrice,
                            wholesalePrice, maximumPriceValue, minimumPriceValue, validPriceFound,
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
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_old, 
                        "ProductPriceCannotRetrievePriceRules", UtilMisc.toMap("errorString", e.toString()) , locale));
            }
        }

        // Convert the value to the price currency, if required
        if("true".equals(UtilProperties.getPropertyValue("ecommerce.properties", "convertProductPriceCurrency"))){
            if (UtilValidate.isNotEmpty(currencyDefaultUomId) && UtilValidate.isNotEmpty(currencyUomIdTo) && !currencyDefaultUomId.equals(currencyUomIdTo)) {
                if(UtilValidate.isNotEmpty(result)){
                    Map<String, Object> convertPriceMap = FastMap.newInstance();
                    for (Map.Entry<String, Object> entry : result.entrySet()) {
                        BigDecimal tempPrice = BigDecimal.ZERO;
                        if(entry.getKey() == "basePrice")
                            tempPrice = (BigDecimal) entry.getValue();
                        else if (entry.getKey() == "price")
                            tempPrice = (BigDecimal) entry.getValue();
                        else if (entry.getKey() == "defaultPrice")
                            tempPrice = (BigDecimal) entry.getValue();
                        else if (entry.getKey() == "competitivePrice")
                            tempPrice = (BigDecimal) entry.getValue();
                        else if (entry.getKey() == "averageCost")
                            tempPrice = (BigDecimal) entry.getValue();
                        else if (entry.getKey() == "promoPrice")
                            tempPrice = (BigDecimal) entry.getValue();
                        else if (entry.getKey() == "specialPromoPrice")
                            tempPrice = (BigDecimal) entry.getValue();
                        else if (entry.getKey() == "listPrice")
                            tempPrice = (BigDecimal) entry.getValue();
                        
                        if(tempPrice != null && tempPrice != BigDecimal.ZERO){
                            Map<String, Object> priceResults = FastMap.newInstance();
                            try {
                                priceResults = dispatcher.runSync("convertUom", UtilMisc.<String, Object>toMap("uomId", currencyDefaultUomId, "uomIdTo", currencyUomIdTo, "originalValue", tempPrice , "defaultDecimalScale" , Long.valueOf(2) , "defaultRoundingMode" , "HalfUp"));
                                if (ServiceUtil.isError(priceResults) || (priceResults.get("convertedValue") == null)) {
                                    Debug.logWarning("Unable to convert " + entry.getKey() + " for product  " + productId , module);
                                } 
                            } catch (GenericServiceException e) {
                                Debug.logError(e, module);
                            }
                            convertPriceMap.put(entry.getKey(), priceResults.get("convertedValue"));
                        }else{
                            convertPriceMap.put(entry.getKey(), entry.getValue());
                        }
                    }
                    if(UtilValidate.isNotEmpty(convertPriceMap)){
                        convertPriceMap.put("currencyUsed", currencyUomIdTo);
                        result = convertPriceMap;
                    }
                }
            }
        }
        
        // utilTimer.timerString("Finished price calc [productId=" + productId + "]", module);
        return result;
    }
    
    private static GenericValue getPriceValueForType(String productPriceTypeId, List<GenericValue> productPriceList, List<GenericValue> secondaryPriceList) {
        List<GenericValue> filteredPrices = EntityUtil.filterByAnd(productPriceList, UtilMisc.toMap("productPriceTypeId", productPriceTypeId));
        GenericValue priceValue = EntityUtil.getFirst(filteredPrices);
        if (filteredPrices != null && filteredPrices.size() > 1) {
            if (Debug.infoOn()) Debug.logInfo("There is more than one " + productPriceTypeId + " with the currencyUomId " + priceValue.getString("currencyUomId") + " and productId " + priceValue.getString("productId") + ", using the latest found with price: " + priceValue.getBigDecimal("price"), module);
        }
        if (priceValue == null && secondaryPriceList != null) {
            return getPriceValueForType(productPriceTypeId, secondaryPriceList, null);
        }
        return priceValue;
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
			if (UtilValidate.isNotEmpty(productStoreId)) {
				List<String> productQuotationIds = FastList.newInstance();
				List<EntityCondition> listAllCondition = FastList.newInstance();
				
				// get all quotation of product store group parent of this product store
				listAllCondition.add(EntityCondition.makeCondition("productStoreId", productStoreId));
				listAllCondition.add(EntityUtil.getFilterByDateExpr(nowTimestamp));
				List<String> productStoreGroupIds = EntityUtil.getFieldListFromEntityList(
						delegator.findList("ProductStoreGroupMember", EntityCondition.makeCondition(listAllCondition), UtilMisc.toSet("productStoreGroupId"), null, null, false), "productStoreGroupId", true);
				if (UtilValidate.isNotEmpty(productStoreGroupIds)) {
					listAllCondition.clear();
					listAllCondition.add(EntityCondition.makeCondition("productQuotationModuleTypeId", "SALES_QUOTATION"));
					listAllCondition.add(EntityCondition.makeCondition("productStoreGroupId", EntityOperator.IN, productStoreGroupIds));
					listAllCondition.add(EntityCondition.makeCondition("statusId", "QUOTATION_ACCEPTED"));
					listAllCondition.add(EntityUtil.getFilterByDateExpr(nowTimestamp));
					listAllCondition.add(EntityUtil.getFilterByDateExpr(nowTimestamp, "fromDateAppl", "thruDateAppl"));
					List<String> tmpQuotationIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductQuotationStoreGroupApplDetailQuotation", 
									EntityCondition.makeCondition(listAllCondition), UtilMisc.toSet("productQuotationId"), null, null, false), "productQuotationId", true); //UtilMisc.toList("-createDate")
					if (UtilValidate.isNotEmpty(tmpQuotationIds)) {
						productQuotationIds.addAll(tmpQuotationIds);
					}
				}
				
				// get all quotation of this product store
				listAllCondition.clear();
				listAllCondition.add(EntityCondition.makeCondition("productQuotationModuleTypeId", "SALES_QUOTATION"));
				listAllCondition.add(EntityCondition.makeCondition("productStoreId", productStoreId));
				listAllCondition.add(EntityCondition.makeCondition("statusId", "QUOTATION_ACCEPTED"));
				listAllCondition.add(EntityUtil.getFilterByDateExpr(nowTimestamp));
				listAllCondition.add(EntityUtil.getFilterByDateExpr(nowTimestamp, "fromDateAppl", "thruDateAppl"));
				List<String> tmpQuotationIds2 = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductQuotationStoreApplDetailQuotation", 
								EntityCondition.makeCondition(listAllCondition), UtilMisc.toSet("productQuotationId"), null, null, false), "productQuotationId", true); //UtilMisc.toList("-createDate")
				if (UtilValidate.isNotEmpty(tmpQuotationIds2)) {
					productQuotationIds.addAll(tmpQuotationIds2);
				}
				if (UtilValidate.isNotEmpty(productQuotationIds)) {
					/* TODOCHANGE 1:
					List<String> productPriceRuleIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductPriceRule", EntityCondition.makeCondition("productQuotationId", EntityOperator.IN, productQuotationIds), UtilMisc.toSet("productPriceRuleId"), null, null, true), "productPriceRuleId", true);
					
					if (UtilValidate.isNotEmpty(productPriceRuleIds)) {
						// TODOCHANGE check product id in product price condition
						// by productId
						List<EntityCondition> conds = new ArrayList<EntityCondition>();
						conds.add(EntityCondition.makeCondition("inputParamEnumId", "PRIP_PRODUCT_ID"));
						conds.add(EntityCondition.makeCondition("condValue", productId));
						conds.add(EntityCondition.makeCondition("productPriceRuleId", EntityOperator.IN, productPriceRuleIds));
						List<GenericValue> productIdConds = delegator.findList("ProductPriceCond", EntityCondition.makeCondition(conds), null, null, null, true);
						if (UtilValidate.isNotEmpty(productIdConds)) {
							List<String> productPriceRuleIdsProcess = EntityUtil.getFieldListFromEntityList(productIdConds, "productPriceRuleId", true);
							productPriceRules = delegator.findList("ProductPriceRule", EntityCondition.makeCondition("productPriceRuleId", EntityOperator.IN, productPriceRuleIdsProcess), null, UtilMisc.toList("-productPriceRuleId"), null, true);
						}
					}*/
					//TODOCHANGE 2: productPriceRules = delegator.findList("ProductPriceRule", EntityCondition.makeCondition("productQuotationId", EntityOperator.IN, productQuotationIds), null, UtilMisc.toList("-createdStamp"), null, true);
					
					// TODOCHANGE 3: new process, filter by product or category
					List<GenericValue> productPriceRulesOrigin = delegator.findList("ProductPriceRule", EntityCondition.makeCondition("productQuotationId", EntityOperator.IN, productQuotationIds), null, null, null, true);
					List<String> productPriceRuleIds = EntityUtil.getFieldListFromEntityList(productPriceRulesOrigin, "productPriceRuleId", true);
					if (UtilValidate.isNotEmpty(productPriceRuleIds)) {
						// TODOCHANGE check product id in product price condition
						// by productId or productCategoryId
						Set<String> prodCategoryIds = com.olbius.basesales.product.ProductWorker.getAllCategoryIdByProduct(delegator, productId, true, "CATALOG_CATEGORY");
						
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
			/*GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
			if (productStore != null) {
				String salesMethodChannelEnumId = productStore.getString("salesMethodChannelEnumId");
				if (UtilValidate.isNotEmpty(salesMethodChannelEnumId)) {
					List<EntityCondition> listAllCondition = FastList.newInstance();
					listAllCondition.add(EntityCondition.makeCondition("salesMethodChannelEnumId", salesMethodChannelEnumId));
					listAllCondition.add(EntityCondition.makeCondition("statusId", "QUOTATION_ACCEPTED"));
					listAllCondition.add(EntityUtil.getFilterByDateExpr());
					List<String> productQuotationIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductQuotation", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), null, UtilMisc.toList("-createDate"), null, false), 
							"productQuotationId", true);
					if (UtilValidate.isNotEmpty(productQuotationIds)) {
						productPriceRules = delegator.findList("ProductPriceRule", EntityCondition.makeCondition("productQuotationId", EntityOperator.IN, productQuotationIds), null, UtilMisc.toList("-productPriceRuleId"), null, true);
					}
				}
			}*/
			// end change
			
			if (productPriceRules == null) productPriceRules = FastList.newInstance();
		}
		
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
            Set<String> productCategoryIdes = com.olbius.basesales.product.ProductWorker.getAllCategoryIdByProduct(delegator, productId, true);
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
    
    public static Map<String, Object> calcPriceResultFromRulesCustom(List<GenericValue> productPriceRules, BigDecimal listPrice, BigDecimal defaultPrice, BigDecimal promoPrice,
            BigDecimal wholesalePrice, GenericValue maximumPriceValue, GenericValue minimumPriceValue, boolean validPriceFound,
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

                    if ("PRICE_POD".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                        if (productPriceAction.get("amount") != null) {
                            modifyAmount = defaultPrice.multiply(productPriceAction.getBigDecimal("amount").movePointLeft(2));
                            price = defaultPrice;
                        }
                    } else if ("PRICE_POL".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                        if (productPriceAction.get("amount") != null) {
                            modifyAmount = listPrice.multiply(productPriceAction.getBigDecimal("amount").movePointLeft(2));
                        }
                    } else if ("PRICE_POAC".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                        if (productPriceAction.get("amount") != null) {
                            modifyAmount = averageCost.multiply(productPriceAction.getBigDecimal("amount").movePointLeft(2));
                        }
                    } else if ("PRICE_POM".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                        if (productPriceAction.get("amount") != null) {
                            modifyAmount = margin.multiply(productPriceAction.getBigDecimal("amount").movePointLeft(2));
                        }
                    } else if ("PRICE_POWHS".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                        if (productPriceAction.get("amount") != null && wholesalePrice != null) {
                            modifyAmount = wholesalePrice.multiply(productPriceAction.getBigDecimal("amount").movePointLeft(2));
                        }
                    } else if ("PRICE_FOL".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                        if (productPriceAction.get("amount") != null) {
                            modifyAmount = productPriceAction.getBigDecimal("amount");
                        }
                    } else if ("PRICE_FOD".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                    	// TODOCHANGE add new
                    	if (productPriceAction.get("amount") != null) {
                    		modifyAmount = productPriceAction.getBigDecimal("amount");
                    		price = defaultPrice;
                    	}
                    } else if ("PRICE_FLAT".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                        // this one is a bit different, break out of the loop because we now have our final price
                        foundFlatOverride = true;
                        if (productPriceAction.get("amount") != null) {
                            price = productPriceAction.getBigDecimal("amount");
                        } else {
                            Debug.logInfo("ProductPriceAction had null amount, using default price: " + defaultPrice + " for product with id " + productId, module);
                            price = defaultPrice;
                            isSale = false;                // reverse isSale flag, as this sale rule was actually not applied
                        }
                    } else if ("PRICE_PFLAT".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                        // this one is a bit different too, break out of the loop because we now have our final price
                        foundFlatOverride = true;
                        price = promoPrice;
                        if (productPriceAction.get("amount") != null) {
                            price = price.add(productPriceAction.getBigDecimal("amount"));
                        }
                        if (price.compareTo(BigDecimal.ZERO) == 0) {
                            if (defaultPrice.compareTo(BigDecimal.ZERO) != 0) {
                                Debug.logInfo("PromoPrice and ProductPriceAction had null amount, using default price: " + defaultPrice + " for product with id " + productId, module);
                                price = defaultPrice;
                            } else if (listPrice.compareTo(BigDecimal.ZERO) != 0) {
                                Debug.logInfo("PromoPrice and ProductPriceAction had null amount and no default price was available, using list price: " + listPrice + " for product with id " + productId, module);
                                price = listPrice;
                            } else {
                                Debug.logError("PromoPrice and ProductPriceAction had null amount and no default or list price was available, so price is set to zero for product with id " + productId, module);
                                price = BigDecimal.ZERO;
                            }
                            isSale = false;                // reverse isSale flag, as this sale rule was actually not applied
                        }
                    } else if ("PRICE_WFLAT".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                        // same as promo price but using the wholesale price instead
                        foundFlatOverride = true;
                        price = wholesalePrice;
                        if (productPriceAction.get("amount") != null) {
                            price = price.add(productPriceAction.getBigDecimal("amount"));
                        }
                        if (price.compareTo(BigDecimal.ZERO) == 0) {
                            if (defaultPrice.compareTo(BigDecimal.ZERO) != 0) {
                                Debug.logInfo("WholesalePrice and ProductPriceAction had null amount, using default price: " + defaultPrice + " for product with id " + productId, module);
                                price = defaultPrice;
                            } else if (listPrice.compareTo(BigDecimal.ZERO) != 0) {
                                Debug.logInfo("WholesalePrice and ProductPriceAction had null amount and no default price was available, using list price: " + listPrice + " for product with id " + productId, module);
                                price = listPrice;
                            } else {
                                Debug.logError("WholesalePrice and ProductPriceAction had null amount and no default or list price was available, so price is set to zero for product with id " + productId, module);
                                price = BigDecimal.ZERO;
                            }
                            isSale = false; // reverse isSale flag, as this sale rule was actually not applied
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
            price = defaultPrice;
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

        calcResults.put("basePrice", price);
        calcResults.put("price", price);
        calcResults.put("listPrice", listPrice);
        calcResults.put("defaultPrice", defaultPrice);
        calcResults.put("averageCost", averageCost);
        calcResults.put("orderItemPriceInfos", orderItemPriceInfos);
        calcResults.put("isSale", Boolean.valueOf(isSale));
        calcResults.put("validPriceFound", Boolean.valueOf(validPriceFound));

        return calcResults;
    }
    
    
	/*
	// TODOCHANE add new "quantityUomId"
     * <p>Calculates the price of a product from pricing rules given the following input, and of course access to the database:</p>
     * <ul>
     *   <li>productId
     *   <li>partyId
     *   <li>prodCatalogId
     *   <li>webSiteId
     *   <li>productStoreId
     *   <li>productStoreGroupId
     *   <li>agreementId
     *   <li>quantity
     *   <li>currencyUomId
     *   <li>quantityUomId
     *   <li>checkIncludeVat
     * </ul>
    public static Map<String, Object> calculateProductPriceCustom(DispatchContext dctx, Map<String, ? extends Object> context) {
        // UtilTimer utilTimer = new UtilTimer();
        // utilTimer.timerString("Starting price calc", module);
        // utilTimer.setLog(false);

        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = FastMap.newInstance();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        GenericValue product = (GenericValue) context.get("product");
        String productId = product.getString("productId");
        String prodCatalogId = (String) context.get("prodCatalogId");
        String webSiteId = (String) context.get("webSiteId");
        String checkIncludeVat = (String) context.get("checkIncludeVat");
        String surveyResponseId = (String) context.get("surveyResponseId");
        Map<String, Object> customAttributes = UtilGenerics.checkMap(context.get("customAttributes"));

        String findAllQuantityPricesStr = (String) context.get("findAllQuantityPrices");
        boolean findAllQuantityPrices = "Y".equals(findAllQuantityPricesStr);
        boolean optimizeForLargeRuleSet = "Y".equals(context.get("optimizeForLargeRuleSet"));

        String agreementId = (String) context.get("agreementId");

        String productStoreId = (String) context.get("productStoreId");
        String productStoreGroupId = (String) context.get("productStoreGroupId");
        Locale locale = (Locale) context.get("locale");
        
        GenericValue productStore = null;
        try {
            // we have a productStoreId, if the corresponding ProductStore.primaryStoreGroupId is not empty, use that
            productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), true);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error getting product store info from the database while calculating price" + e.toString(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_old, 
                    "ProductPriceCannotRetrieveProductStore", UtilMisc.toMap("errorString", e.toString()) , locale));
        }
        if (UtilValidate.isEmpty(productStoreGroupId)) {
            if (productStore != null) {
                try {
                    if (UtilValidate.isNotEmpty(productStore.getString("primaryStoreGroupId"))) {
                        productStoreGroupId = productStore.getString("primaryStoreGroupId");
                    } else {
                        // no ProductStore.primaryStoreGroupId, try ProductStoreGroupMember
                        List<GenericValue> productStoreGroupMemberList = delegator.findByAnd("ProductStoreGroupMember", UtilMisc.toMap("productStoreId", productStoreId), UtilMisc.toList("sequenceNum", "-fromDate"), true);
                        productStoreGroupMemberList = EntityUtil.filterByDate(productStoreGroupMemberList, true);
                        if (productStoreGroupMemberList.size() > 0) {
                            GenericValue productStoreGroupMember = EntityUtil.getFirst(productStoreGroupMemberList);
                            productStoreGroupId = productStoreGroupMember.getString("productStoreGroupId");
                        }
                    }
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Error getting product store info from the database while calculating price" + e.toString(), module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_old, 
                            "ProductPriceCannotRetrieveProductStore", UtilMisc.toMap("errorString", e.toString()) , locale));
                }
            }

            // still empty, default to _NA_
            if (UtilValidate.isEmpty(productStoreGroupId)) {
                productStoreGroupId = "_NA_";
            }
        }

        // if currencyUomId is null get from properties file, if nothing there assume USD (USD: American Dollar) for now
        String currencyDefaultUomId = (String) context.get("currencyUomId");
        String currencyUomIdTo = (String) context.get("currencyUomIdTo"); 
        if (UtilValidate.isEmpty(currencyDefaultUomId)) {
            currencyDefaultUomId = EntityUtilProperties.getPropertyValue("general", "currency.uom.id.default", "USD", delegator);
        }
        
        // TODOCHANGE new quantityUomId
        String quantityDefaultUomId = (String) context.get("quantityUomId");

        // productPricePurposeId is null assume "PURCHASE", which is equivalent to what prices were before the purpose concept
        String productPricePurposeId = (String) context.get("productPricePurposeId");
        if (UtilValidate.isEmpty(productPricePurposeId)) {
            productPricePurposeId = "PURCHASE";
        }

        // termUomId, for things like recurring prices specifies the term (time/frequency measure for example) of the recurrence
        // if this is empty it will simply not be used to constrain the selection
        String termUomId = (String) context.get("termUomId");

        // if this product is variant, find the virtual product and apply checks to it as well
        String virtualProductId = null;
        if ("Y".equals(product.getString("isVariant"))) {
            try {
                virtualProductId = ProductWorker.getVariantVirtualId(product);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error getting virtual product id from the database while calculating price" + e.toString(), module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_old, 
                        "ProductPriceCannotRetrieveVirtualProductId", UtilMisc.toMap("errorString", e.toString()) , locale));
            }
        }

        // get prices for virtual product if one is found; get all ProductPrice entities for this productId and currencyUomId
        List<GenericValue> virtualProductPrices = null;
        if (virtualProductId != null) {
            try {
                virtualProductPrices = delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", virtualProductId, "currencyUomId", currencyDefaultUomId, "productStoreGroupId", productStoreGroupId), UtilMisc.toList("-fromDate"), true);
            } catch (GenericEntityException e) {
                Debug.logError(e, "An error occurred while getting the product prices", module);
            }
            virtualProductPrices = EntityUtil.filterByDate(virtualProductPrices, true);
        }

        // NOTE: partyId CAN be null
        String partyId = (String) context.get("partyId");
        if (UtilValidate.isEmpty(partyId) && context.get("userLogin") != null) {
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            partyId = userLogin.getString("partyId");
        }

        // check for auto-userlogin for price rules
        if (UtilValidate.isEmpty(partyId) && context.get("autoUserLogin") != null) {
            GenericValue userLogin = (GenericValue) context.get("autoUserLogin");
            partyId = userLogin.getString("partyId");
        }

        BigDecimal quantity = (BigDecimal) context.get("quantity");
        if (quantity == null) quantity = BigDecimal.ONE;

        BigDecimal amount = (BigDecimal) context.get("amount");

        List<EntityCondition> productPriceEcList = FastList.newInstance();
        productPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
        // this funny statement is for backward compatibility purposes; the productPricePurposeId is a new pk field on the ProductPrice entity and in order databases may not be populated, until the pk is updated and such; this will ease the transition somewhat
        if ("PURCHASE".equals(productPricePurposeId)) {
            productPriceEcList.add(EntityCondition.makeCondition(
                    EntityCondition.makeCondition("productPricePurposeId", EntityOperator.EQUALS, productPricePurposeId),
                    EntityOperator.OR,
                    EntityCondition.makeCondition("productPricePurposeId", EntityOperator.EQUALS, null)));
        } else {
            productPriceEcList.add(EntityCondition.makeCondition("productPricePurposeId", EntityOperator.EQUALS, productPricePurposeId));
        }
        productPriceEcList.add(EntityCondition.makeCondition("currencyUomId", EntityOperator.EQUALS, currencyDefaultUomId));
        productPriceEcList.add(EntityCondition.makeCondition("productStoreGroupId", EntityOperator.EQUALS, productStoreGroupId));
        List<EntityCondition> productPriceEcListBefore = FastList.newInstance();
        productPriceEcListBefore.addAll(productPriceEcList);
        if (UtilValidate.isNotEmpty(termUomId)) {//TODOCHANGE used to is quantityUomId
            productPriceEcList.add(EntityCondition.makeCondition("termUomId", EntityOperator.EQUALS, termUomId));
        }
        EntityCondition productPriceEc = EntityCondition.makeCondition(productPriceEcList, EntityOperator.AND);
        
        //TODOCHANGE new quantityUomId
        BigDecimal quantityUomIdToDefault = BigDecimal.ONE;
        
        // for prices, get all ProductPrice entities for this productId and currencyUomId
        List<GenericValue> productPrices = null;
        try {
            productPrices = delegator.findList("ProductPrice", productPriceEc, null, UtilMisc.toList("-fromDate"), null, true);
            
            // TODOCHANGE add new process
            if (UtilValidate.isEmpty(productPrices)) {
            	// convert quantityUomId (input) to productPackingUomId
				if (product.containsKey("productPackingUomId") && !quantityDefaultUomId.equals(product.getString("productPackingUomId"))) {
					try {
						GenericValue userLogin = (GenericValue) context.get("userLogin");
						Map<String, Object> resultValue = dispatcher.runSync("getConvertPackingNumber", UtilMisc.toMap("productId", productId, "uomFromId", quantityDefaultUomId, "uomToId", product.getString("productPackingUomId"), "userLogin", userLogin));
						if (ServiceUtil.isSuccess(resultValue)) {
							quantityUomIdToDefault = (BigDecimal) resultValue.get("convertNumber");
							
							List<EntityCondition> productPriceEcList2 = FastList.newInstance();
							productPriceEcList2.addAll(productPriceEcListBefore);
							productPriceEcList2.add(EntityCondition.makeCondition("termUomId", EntityOperator.EQUALS, product.getString("productPackingUomId")));
							EntityCondition productPriceEc2 = EntityCondition.makeCondition(productPriceEcList2, EntityOperator.AND);
							productPrices = delegator.findList("ProductPrice", productPriceEc2, null, UtilMisc.toList("-fromDate"), null, true);
							if (UtilValidate.isEmpty(productPrices)) {
								if (!quantityDefaultUomId.equals(product.getString("quantityUomId"))) {
									Map<String, Object> resultValue2 = dispatcher.runSync("getConvertPackingNumber", UtilMisc.toMap("productId", productId, "uomFromId", quantityDefaultUomId, "uomToId", product.getString("quantityUomId"), "userLogin", userLogin));
									if (ServiceUtil.isSuccess(resultValue2)) {
										quantityUomIdToDefault = (BigDecimal) resultValue.get("convertNumber");
										
										List<EntityCondition> productPriceEcList3 = FastList.newInstance();
										productPriceEcList3.addAll(productPriceEcListBefore);
										productPriceEcList3.add(EntityCondition.makeCondition("termUomId", EntityOperator.EQUALS, product.getString("productPackingUomId")));
										EntityCondition productPriceEc3 = EntityCondition.makeCondition(productPriceEcList3, EntityOperator.AND);
										productPrices = delegator.findList("ProductPrice", productPriceEc3, null, UtilMisc.toList("-fromDate"), null, true);
									}
								}
							}
						}
					} catch (Exception e) {
    		            Debug.logWarning(e, "Problems run service name = getConvertPackingNumber", module);
    		        }
				} else {
					quantityUomIdToDefault = BigDecimal.ONE;
				}
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "An error occurred while getting the product prices", module);
        }
        productPrices = EntityUtil.filterByDate(productPrices, true);

        // ===== get the prices we need: list, default, average cost, promo, min, max =====
        // if any of these prices is missing and this product is a variant, default to the corresponding price on the virtual product
        GenericValue listPriceValue = getPriceValueForType("LIST_PRICE", productPrices, virtualProductPrices);
        GenericValue defaultPriceValue = getPriceValueForType("DEFAULT_PRICE", productPrices, virtualProductPrices);

        // If there is an agreement between the company and the client, and there is
        // a price for the product in it, it will override the default price of the
        // ProductPrice entity.
        if (UtilValidate.isNotEmpty(agreementId)) {
            try {
                List<GenericValue> agreementPrices = delegator.findByAnd("AgreementItemAndProductAppl", UtilMisc.toMap("agreementId", agreementId, "productId", productId, "currencyUomId", currencyDefaultUomId), null, false);
                GenericValue agreementPriceValue = EntityUtil.getFirst(agreementPrices);
                if (agreementPriceValue != null && agreementPriceValue.get("price") != null) {
                    defaultPriceValue = agreementPriceValue;
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error getting agreement info from the database while calculating price" + e.toString(), module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_old, 
                        "ProductPriceCannotRetrieveAgreementInfo", UtilMisc.toMap("errorString", e.toString()) , locale));
            }
        }

        GenericValue competitivePriceValue = getPriceValueForType("COMPETITIVE_PRICE", productPrices, virtualProductPrices);
        GenericValue averageCostValue = getPriceValueForType("AVERAGE_COST", productPrices, virtualProductPrices);
        GenericValue promoPriceValue = getPriceValueForType("PROMO_PRICE", productPrices, virtualProductPrices);
        GenericValue minimumPriceValue = getPriceValueForType("MINIMUM_PRICE", productPrices, virtualProductPrices);
        GenericValue maximumPriceValue = getPriceValueForType("MAXIMUM_PRICE", productPrices, virtualProductPrices);
        GenericValue wholesalePriceValue = getPriceValueForType("WHOLESALE_PRICE", productPrices, virtualProductPrices);
        GenericValue specialPromoPriceValue = getPriceValueForType("SPECIAL_PROMO_PRICE", productPrices, virtualProductPrices);

        // now if this is a virtual product check each price type, if doesn't exist get from variant with lowest DEFAULT_PRICE
        if ("Y".equals(product.getString("isVirtual"))) {
            // only do this if there is no default price, consider the others optional for performance reasons
            if (defaultPriceValue == null) {
                // Debug.logInfo("Product isVirtual and there is no default price for ID " + productId + ", trying variant prices", module);

                //use the cache to find the variant with the lowest default price
                try {
                    List<GenericValue> variantAssocList = EntityUtil.filterByDate(delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId", product.get("productId"), "productAssocTypeId", "PRODUCT_VARIANT"), UtilMisc.toList("-fromDate"), true));
                    BigDecimal minDefaultPrice = null;
                    List<GenericValue> variantProductPrices = null;
                    @SuppressWarnings("unused")
					String variantProductId = null;
                    for (GenericValue variantAssoc: variantAssocList) {
                        String curVariantProductId = variantAssoc.getString("productIdTo");
                        List<GenericValue> curVariantPriceList = EntityUtil.filterByDate(delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", curVariantProductId), UtilMisc.toList("-fromDate"), true), nowTimestamp);
                        List<GenericValue> tempDefaultPriceList = EntityUtil.filterByAnd(curVariantPriceList, UtilMisc.toMap("productPriceTypeId", "DEFAULT_PRICE"));
                        GenericValue curDefaultPriceValue = EntityUtil.getFirst(tempDefaultPriceList);
                        if (curDefaultPriceValue != null) {
                            BigDecimal curDefaultPrice = curDefaultPriceValue.getBigDecimal("price");
                            if (minDefaultPrice == null || curDefaultPrice.compareTo(minDefaultPrice) < 0) {
                                // check to see if the product is discontinued for sale before considering it the lowest price
                                GenericValue curVariantProduct = delegator.findOne("Product", UtilMisc.toMap("productId", curVariantProductId), true);
                                if (curVariantProduct != null) {
                                    Timestamp salesDiscontinuationDate = curVariantProduct.getTimestamp("salesDiscontinuationDate");
                                    if (salesDiscontinuationDate == null || salesDiscontinuationDate.after(nowTimestamp)) {
                                        minDefaultPrice = curDefaultPrice;
                                        variantProductPrices = curVariantPriceList;
                                        variantProductId = curVariantProductId;
                                        // Debug.logInfo("Found new lowest price " + minDefaultPrice + " for variant with ID " + variantProductId, module);
                                    }
                                }
                            }
                        }
                    }

                    if (variantProductPrices != null) {
                        // we have some other options, give 'em a go...
                        if (listPriceValue == null) {
                            listPriceValue = getPriceValueForType("LIST_PRICE", variantProductPrices, null);
                        }
                        if (defaultPriceValue == null) {
                            defaultPriceValue = getPriceValueForType("DEFAULT_PRICE", variantProductPrices, null);
                        }
                        if (competitivePriceValue == null) {
                            competitivePriceValue = getPriceValueForType("COMPETITIVE_PRICE", variantProductPrices, null);
                        }
                        if (averageCostValue == null) {
                            averageCostValue = getPriceValueForType("AVERAGE_COST", variantProductPrices, null);
                        }
                        if (promoPriceValue == null) {
                            promoPriceValue = getPriceValueForType("PROMO_PRICE", variantProductPrices, null);
                        }
                        if (minimumPriceValue == null) {
                            minimumPriceValue = getPriceValueForType("MINIMUM_PRICE", variantProductPrices, null);
                        }
                        if (maximumPriceValue == null) {
                            maximumPriceValue = getPriceValueForType("MAXIMUM_PRICE", variantProductPrices, null);
                        }
                        if (wholesalePriceValue == null) {
                            wholesalePriceValue = getPriceValueForType("WHOLESALE_PRICE", variantProductPrices, null);
                        }
                        if (specialPromoPriceValue == null) {
                            specialPromoPriceValue = getPriceValueForType("SPECIAL_PROMO_PRICE", variantProductPrices, null);
                        }
                    }
                } catch (GenericEntityException e) {
                    Debug.logError(e, "An error occurred while getting the product prices", module);
                }
            }
        }

        //boolean validPromoPriceFound = false;
        BigDecimal promoPrice = BigDecimal.ZERO;
        if (promoPriceValue != null && promoPriceValue.get("price") != null) {
            promoPrice = promoPriceValue.getBigDecimal("price");
            //validPromoPriceFound = true;
        }

        //boolean validWholesalePriceFound = false;
        BigDecimal wholesalePrice = BigDecimal.ZERO;
        if (wholesalePriceValue != null && wholesalePriceValue.get("price") != null) {
            wholesalePrice = wholesalePriceValue.getBigDecimal("price");
            //validWholesalePriceFound = true;
        }

        boolean validPriceFound = false;
        BigDecimal defaultPrice = BigDecimal.ZERO;
        List<GenericValue> orderItemPriceInfos = FastList.newInstance();
        if (defaultPriceValue != null) {
            // If a price calc formula (service) is specified, then use it to get the unit price
            if ("ProductPrice".equals(defaultPriceValue.getEntityName()) && UtilValidate.isNotEmpty(defaultPriceValue.getString("customPriceCalcService"))) {
                GenericValue customMethod = null;
                try {
                    customMethod = defaultPriceValue.getRelatedOne("CustomMethod", false);
                } catch (GenericEntityException gee) {
                    Debug.logError(gee, "An error occurred while getting the customPriceCalcService", module);
                }
                if (UtilValidate.isNotEmpty(customMethod) && UtilValidate.isNotEmpty(customMethod.getString("customMethodName"))) {
                    Map<String, Object> inMap = UtilMisc.toMap("userLogin", context.get("userLogin"), "product", product);
                    inMap.put("initialPrice", defaultPriceValue.getBigDecimal("price"));
                    inMap.put("currencyUomId", currencyDefaultUomId);
                    inMap.put("quantity", quantity);
                    inMap.put("amount", amount);
                    if (UtilValidate.isNotEmpty(surveyResponseId)) {
                        inMap.put("surveyResponseId", surveyResponseId);
                    }
                    if (UtilValidate.isNotEmpty(customAttributes)) {
                        inMap.put("customAttributes", customAttributes);
                    }
                    try {
                        Map<String, Object> outMap = dispatcher.runSync(customMethod.getString("customMethodName"), inMap);
                        if (!ServiceUtil.isError(outMap)) {
                            BigDecimal calculatedDefaultPrice = (BigDecimal)outMap.get("price");
                            orderItemPriceInfos = UtilGenerics.checkList(outMap.get("orderItemPriceInfos"));
                            if (UtilValidate.isNotEmpty(calculatedDefaultPrice)) {
                                defaultPrice = calculatedDefaultPrice;
                                validPriceFound = true;
                            }
                        }
                    } catch (GenericServiceException gse) {
                        Debug.logError(gse, "An error occurred while running the customPriceCalcService [" + customMethod.getString("customMethodName") + "]", module);
                    }
                }
            }
            if (!validPriceFound && defaultPriceValue.get("price") != null) {
                defaultPrice = defaultPriceValue.getBigDecimal("price");
                validPriceFound = true;
            }
        }

        BigDecimal listPrice = listPriceValue != null ? listPriceValue.getBigDecimal("price") : null;
        
        if (listPrice == null) {
            // no list price, use defaultPrice for the final price

            // ========= ensure calculated price is not below minSalePrice or above maxSalePrice =========
            BigDecimal maxSellPrice = maximumPriceValue != null ? maximumPriceValue.getBigDecimal("price") : null;
            if (maxSellPrice != null && defaultPrice.compareTo(maxSellPrice) > 0) {
                defaultPrice = maxSellPrice;
            }
            // min price second to override max price, safety net
            BigDecimal minSellPrice = minimumPriceValue != null ? minimumPriceValue.getBigDecimal("price") : null;
            if (minSellPrice != null && defaultPrice.compareTo(minSellPrice) < 0) {
                defaultPrice = minSellPrice;
                // since we have found a minimum price that has overriden a the defaultPrice, even if no valid one was found, we will consider it as if one had been...
                validPriceFound = true;
            }

            result.put("basePrice", defaultPrice);
            result.put("price", defaultPrice);
            result.put("defaultPrice", defaultPrice);
            result.put("competitivePrice", competitivePriceValue != null ? competitivePriceValue.getBigDecimal("price") : null);
            result.put("averageCost", averageCostValue != null ? averageCostValue.getBigDecimal("price") : null);
            result.put("promoPrice", promoPriceValue != null ? promoPriceValue.getBigDecimal("price") : null);
            result.put("specialPromoPrice", specialPromoPriceValue != null ? specialPromoPriceValue.getBigDecimal("price") : null);
            result.put("validPriceFound", Boolean.valueOf(validPriceFound));
            result.put("isSale", Boolean.FALSE);
            result.put("orderItemPriceInfos", orderItemPriceInfos);

            Map<String, Object> errorResult = org.ofbiz.product.price.PriceServices.addGeneralResults(result, competitivePriceValue, specialPromoPriceValue, productStore,
                    checkIncludeVat, currencyDefaultUomId, productId, quantity, partyId, dispatcher, locale);
            if (errorResult != null) return errorResult;
        } else {
            try {
                List<GenericValue> allProductPriceRules = makeProducePriceRuleListCustom(delegator, optimizeForLargeRuleSet, productId, virtualProductId, prodCatalogId, productStoreGroupId, webSiteId, partyId, currencyDefaultUomId, quantityDefaultUomId, productStoreId);
                // TODOCHANGE filter fromDate of pricing rule
                EntityCondition cond = EntityCondition.makeCondition("fromDate", EntityOperator.NOT_EQUAL, null);
                allProductPriceRules = EntityUtil.filterByCondition(allProductPriceRules, cond);
                allProductPriceRules = EntityUtil.filterByDate(allProductPriceRules, true);

                List<GenericValue> quantityProductPriceRules = null;
                List<GenericValue> nonQuantityProductPriceRules = null;
                String quantityUomId = quantityDefaultUomId;
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
                                if (!checkPriceConditionCustom(productPriceCond, productId, virtualProductId, prodCatalogId, productStoreGroupId, webSiteId, partyId, quantity, listPrice, currencyDefaultUomId, quantityDefaultUomId, delegator, nowTimestamp)) {
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

                        Map<String, Object> quantCalcResults = calcPriceResultFromRulesCustom(ruleListToUse, listPrice, defaultPrice, promoPrice,
                            wholesalePrice, maximumPriceValue, minimumPriceValue, validPriceFound,
                            averageCostValue, productId, virtualProductId, prodCatalogId, productStoreGroupId,
                            webSiteId, partyId, null, currencyDefaultUomId, quantityDefaultUomId, delegator, nowTimestamp, locale);
                        Map<String, Object> quantErrorResult = org.ofbiz.product.price.PriceServices.addGeneralResults(quantCalcResults, competitivePriceValue, specialPromoPriceValue, productStore,
                            checkIncludeVat, currencyDefaultUomId, productId, quantity, partyId, dispatcher, locale);
                        if (quantErrorResult != null) return quantErrorResult;
                        quantCalcResults.remove("isNext");
                        // also add the quantityProductPriceRule to the Map so it can be used for quantity break information
                        quantCalcResults.put("quantityProductPriceRule", quantityProductPriceRule);

                        allQuantityPrices.add(quantCalcResults);
                    }
                    
                    result.put("allQuantityPrices", allQuantityPrices);

                    // use a quantity 1 to get the main price, then fill in the quantity break prices
                    Map<String, Object> calcResults = calcPriceResultFromRulesCustom(allProductPriceRules, listPrice, defaultPrice, promoPrice,
                        wholesalePrice, maximumPriceValue, minimumPriceValue, validPriceFound,
                        averageCostValue, productId, virtualProductId, prodCatalogId, productStoreGroupId,
                        webSiteId, partyId, BigDecimal.ONE, currencyDefaultUomId, quantityDefaultUomId, delegator, nowTimestamp, locale);
                    calcResults.remove("isNext");
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
                    Map<String, Object> calcResults = calcPriceResultFromRulesCustom(allProductPriceRules, listPrice, defaultPrice, promoPrice,
                        wholesalePrice, maximumPriceValue, minimumPriceValue, validPriceFound,
                        averageCostValue, productId, virtualProductId, prodCatalogId, productStoreGroupId,
                        webSiteId, partyId, quantity, currencyDefaultUomId, quantityDefaultUomId, delegator, nowTimestamp, locale);
                    
                    // TODOCHANGE new code
                    if (!quantityDefaultUomId.equals(product.getString("quantityUomId"))) {
                    	if (calcResults.containsKey("isNext")) {
                        	boolean isNext = (Boolean) calcResults.get("isNext");
                        	if (isNext) {
                        		quantityUomId = product.getString("productPackingUomId");
                        		calcResults = calcPriceResultFromRulesCustom(allProductPriceRules, listPrice, defaultPrice, promoPrice,
                                        wholesalePrice, maximumPriceValue, minimumPriceValue, validPriceFound,
                                        averageCostValue, productId, virtualProductId, prodCatalogId, productStoreGroupId,
                                        webSiteId, partyId, quantity, currencyDefaultUomId, quantityUomId, delegator, nowTimestamp, locale);
                        		if (calcResults.containsKey("isNext")) {
                                	isNext = (Boolean) calcResults.get("isNext");
                                	if (isNext) {
                                		quantityUomId = product.getString("quantityUomId");
                                		calcResults = calcPriceResultFromRulesCustom(allProductPriceRules, listPrice, defaultPrice, promoPrice,
                                                wholesalePrice, maximumPriceValue, minimumPriceValue, validPriceFound,
                                                averageCostValue, productId, virtualProductId, prodCatalogId, productStoreGroupId,
                                                webSiteId, partyId, quantity, currencyDefaultUomId, quantityUomId, delegator, nowTimestamp, locale);
                                	}
                        		}
                        	}
                        }
                    }
    				if (!quantityDefaultUomId.equals(quantityUomId)) {
    					// convert quantityDefaultUomId (input) to quantityUomId
    					try {
    						Map<String, Object> resultValue = dispatcher.runSync("getConvertPackingNumber", UtilMisc.toMap("productId", productId, "uomFromId", quantityDefaultUomId, "uomToId", quantityUomId));
    						if (ServiceUtil.isSuccess(resultValue)) {
    							quantityUomIdToDefault = (BigDecimal) resultValue.get("convertNumber");
    						}
    					} catch (Exception e) {
        		            Debug.logWarning(e, "Problems run service name = getConvertPackingNumber", module);
        		        }
    				} else {
    					quantityUomIdToDefault = BigDecimal.ONE;
    				}
                    if (quantityUomIdToDefault.compareTo(BigDecimal.ONE) > 0) {
                		String[] listPriceType = new String[]{"basePrice", "price", "listPrice", "defaultPrice", "averageCost"};
                		for (String priceType : listPriceType) {
                			BigDecimal valueNew = ((BigDecimal) calcResults.get(priceType)).multiply(quantityUomIdToDefault);
                			calcResults.put(priceType, valueNew);
                		}
                    }
                    calcResults.remove("isNext");
                    // End new code
                    
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
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_old, 
                        "ProductPriceCannotRetrievePriceRules", UtilMisc.toMap("errorString", e.toString()) , locale));
            }
        }

        // Convert the value to the price currency, if required
        if("true".equals(UtilProperties.getPropertyValue("ecommerce.properties", "convertProductPriceCurrency"))){
            if (UtilValidate.isNotEmpty(currencyDefaultUomId) && UtilValidate.isNotEmpty(currencyUomIdTo) && !currencyDefaultUomId.equals(currencyUomIdTo)) {
                if(UtilValidate.isNotEmpty(result)){
                    Map<String, Object> convertPriceMap = FastMap.newInstance();
                    for (Map.Entry<String, Object> entry : result.entrySet()) {
                        BigDecimal tempPrice = BigDecimal.ZERO;
                        if(entry.getKey() == "basePrice")
                            tempPrice = (BigDecimal) entry.getValue();
                        else if (entry.getKey() == "price")
                            tempPrice = (BigDecimal) entry.getValue();
                        else if (entry.getKey() == "defaultPrice")
                            tempPrice = (BigDecimal) entry.getValue();
                        else if (entry.getKey() == "competitivePrice")
                            tempPrice = (BigDecimal) entry.getValue();
                        else if (entry.getKey() == "averageCost")
                            tempPrice = (BigDecimal) entry.getValue();
                        else if (entry.getKey() == "promoPrice")
                            tempPrice = (BigDecimal) entry.getValue();
                        else if (entry.getKey() == "specialPromoPrice")
                            tempPrice = (BigDecimal) entry.getValue();
                        else if (entry.getKey() == "listPrice")
                            tempPrice = (BigDecimal) entry.getValue();
                        
                        if(tempPrice != null && tempPrice != BigDecimal.ZERO){
                            Map<String, Object> priceResults = FastMap.newInstance();
                            try {
                                priceResults = dispatcher.runSync("convertUom", UtilMisc.<String, Object>toMap("uomId", currencyDefaultUomId, "uomIdTo", currencyUomIdTo, "originalValue", tempPrice , "defaultDecimalScale" , Long.valueOf(2) , "defaultRoundingMode" , "HalfUp"));
                                if (ServiceUtil.isError(priceResults) || (priceResults.get("convertedValue") == null)) {
                                    Debug.logWarning("Unable to convert " + entry.getKey() + " for product  " + productId , module);
                                } 
                            } catch (GenericServiceException e) {
                                Debug.logError(e, module);
                            }
                            convertPriceMap.put(entry.getKey(), priceResults.get("convertedValue"));
                        }else{
                            convertPriceMap.put(entry.getKey(), entry.getValue());
                        }
                    }
                    if(UtilValidate.isNotEmpty(convertPriceMap)){
                        convertPriceMap.put("currencyUsed", currencyUomIdTo);
                        result = convertPriceMap;
                    }
                }
            }
        }
        
        // utilTimer.timerString("Finished price calc [productId=" + productId + "]", module);
        return result;
    }
	*/
}
