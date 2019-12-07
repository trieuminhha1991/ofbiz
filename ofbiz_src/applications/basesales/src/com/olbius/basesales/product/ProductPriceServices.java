package com.olbius.basesales.product;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.util.SalesUtil;

import javolution.util.FastList;
import javolution.util.FastSet;

public class ProductPriceServices {
	public static final String module = ProductPriceServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
	
	public static Map<String, Object> updateProductSalesPriceLog(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		try {
			String infoUpdate = (String) context.get("infoUpdate");
			
			List<GenericValue> tobeStored = new LinkedList<GenericValue>();
			
			if ("ProductPrice".equals(infoUpdate)) {
				String productId = (String) context.get("productId");
				
				Timestamp fromDate = (Timestamp) context.get("fromDate");
				Timestamp thruDate = (Timestamp) context.get("thruDate");
				String productPriceTypeId = (String) context.get("productPriceTypeId");
				if (fromDate != null) {
					GenericValue product = EntityUtil.getFirst(delegator.findByAnd("ProductSalesPriceLog", UtilMisc.toMap("productId", productId, "fromDate", fromDate), null, false));
					if (product == null) {
						// create new value
						String logId = delegator.getNextSeqId("ProductSalesPriceLog");
						product = delegator.makeValue("ProductSalesPriceLog", UtilMisc.toMap("logId", logId, "productId", productId, "fromDate", fromDate, "thruDate", thruDate, "productPriceTypeId", productPriceTypeId));
						tobeStored.add(product);
						
						/*if (thruDate != null) {
							// make 1 record with fromDate = thruDate parameter
							String logId2 = delegator.getNextSeqId("ProductSalesPriceLog");
							GenericValue product2 = delegator.makeValue("ProductSalesPriceLog", UtilMisc.toMap("logId", logId2, "productId", productId, "fromDate", thruDate));
							tobeStored.add(product2);
						}*/
					} else {
						if (thruDate != null) {
							// store new value
							product.put("thruDate", thruDate);
							tobeStored.add(product);
							
							// make 1 record with fromDate = thruDate parameter
							String logId2 = delegator.getNextSeqId("ProductSalesPriceLog");
							GenericValue product2 = delegator.makeValue("ProductSalesPriceLog", UtilMisc.toMap("logId", logId2, "productId", productId, "fromDate", thruDate, "productPriceTypeId", product.get("productPriceTypeId"), "parentLogId", product.get("logId")));
							tobeStored.add(product2);
						}
					}
				}
			} else if ("ProductQuotation".equals(infoUpdate)) {
				String productQuotationId = (String) context.get("productQuotationId");
				GenericValue productQuotation = delegator.findOne("ProductQuotation", UtilMisc.toMap("productQuotationId", productQuotationId), false);
				if (productQuotation != null && "QUOTATION_ACCEPTED".equals(productQuotation.getString("statusId"))) {
					List<String> productPriceRuleIds = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("ProductPriceRule", UtilMisc.toMap("productQuotationId", productQuotationId), null, false), "productPriceRuleId", true);
					if (UtilValidate.isNotEmpty(productPriceRuleIds)) {
						List<EntityCondition> conds = FastList.newInstance();
						conds.add(EntityCondition.makeCondition("productPriceRuleId", EntityOperator.IN, productPriceRuleIds));
						conds.add(EntityCondition.makeCondition("inputParamEnumId", "PRIP_PRODUCT_ID"));
						List<GenericValue> productPriceCondsProd = delegator.findList("ProductPriceCond", EntityCondition.makeCondition(conds), null, null, null, false);
						if (UtilValidate.isNotEmpty(productPriceCondsProd)) {
							Timestamp fromDate = productQuotation.getTimestamp("fromDate");
							Timestamp thruDate = productQuotation.getTimestamp("thruDate");
							
							for (GenericValue prodItem : productPriceCondsProd) {
								String productId = prodItem.getString("condValue");
								GenericValue product = EntityUtil.getFirst(delegator.findByAnd("ProductSalesPriceLog", UtilMisc.toMap("productId", productId, "fromDate", fromDate), null, false));
								if (product == null) {
									// create new value
									String logId = delegator.getNextSeqId("ProductSalesPriceLog");
									product = delegator.makeValue("ProductSalesPriceLog", UtilMisc.toMap("logId", logId, "productId", productId, "fromDate", fromDate, "thruDate", thruDate, "productQuotationId", productQuotationId));
									tobeStored.add(product);
									
									if (thruDate != null) {
										// make 1 record with fromDate = thruDate parameter
										String logId2 = delegator.getNextSeqId("ProductSalesPriceLog");
										GenericValue product2 = delegator.makeValue("ProductSalesPriceLog", UtilMisc.toMap("logId", logId2, "productId", productId, "fromDate", thruDate, "productQuotationId", productQuotationId, "parentLogId", product.get("logId")));
										tobeStored.add(product2);
									}
								} else {
									if (thruDate != null) {
										// store new value
										product.put("thruDate", thruDate);
										tobeStored.add(product);
										
										// make 1 record with fromDate = thruDate parameter
										// find record has fromDate = thruDate
										List<GenericValue> childLogs = delegator.findByAnd("ProductSalesPriceLog", UtilMisc.toMap("parentLogId", product.get("logId")), null, false);
										if (UtilValidate.isNotEmpty(childLogs)) {
											for (GenericValue itemLog : childLogs) {
												itemLog.put("fromDate", thruDate);
											}
											tobeStored.addAll(childLogs);
										} else {
											String logId2 = delegator.getNextSeqId("ProductSalesPriceLog");
											GenericValue product2 = delegator.makeValue("ProductSalesPriceLog", UtilMisc.toMap("logId", logId2, "productId", productId, "fromDate", thruDate, "productQuotationId", product.get("productQuotationId"), "parentLogId", product.get("logId")));
											tobeStored.add(product2);
										}
									}
								}
							}
						}
					}
				}
			}
			
			if (!tobeStored.isEmpty()) {
				delegator.storeAll(tobeStored);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling updateProductSalesPriceLog service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductSalesPriceChange(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	
    	List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
		try {
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			
			Timestamp fromDate = null;
			Timestamp thruDate = null;
			
			Map<String, Object> processDateResult = processFromDateThruDate(parameters, nowTimestamp, locale);
			if (ServiceUtil.isError(processDateResult)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(processDateResult));
			}
			fromDate = (Timestamp) processDateResult.get("fromDate");
			thruDate = (Timestamp) processDateResult.get("thruDate");
			
			listAllConditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.BETWEEN, UtilMisc.toList(fromDate, thruDate)));
			
			String productStoreId = SalesUtil.getParameter(parameters, "productStoreId");
			if (UtilValidate.isEmpty(productStoreId)) {
				String organizationId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
				GenericValue productStore = EntityUtil.getFirst(delegator.findByAnd("ProductStore", UtilMisc.toMap("payToPartyId", organizationId), null, false));
				if (productStore != null) productStoreId = productStore.getString("productStoreId");
			}
			
			Set<String> selectFields = FastSet.newInstance();
            selectFields.add("productId");
            selectFields.add("productCode");
            selectFields.add("internalName");
            selectFields.add("productName");
            selectFields.add("uomId");
            selectFields.add("isVirtual");
            selectFields.add("isVariant");
            selectFields.add("taxPercentage");
            selectFields.add("taxAuthPartyId");
            selectFields.add("taxAuthGeoId");
            selectFields.add("barcode");
            selectFields.add("currencyUomId");
            if (UtilValidate.isEmpty(listSortFields)) {
            	listSortFields.add("productId");
            }
            //EntityFindOptions opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
            opts.setDistinct(true);
            EntityListIterator iterator = delegator.find("ProductSalesPriceLogAndUoms", EntityCondition.makeCondition(listAllConditions), null, selectFields, listSortFields, opts);
            List<GenericValue> listProduct = SalesUtil.processIterator(iterator, parameters, successResult);
            
            if (listProduct != null) {
            	//List<EntityCondition> conds = FastList.newInstance();
            	for (GenericValue itemProd : listProduct) {
            		/*Timestamp moment = null;
            		conds.clear();
            		conds.add(EntityCondition.makeCondition("productId", itemProd.get("productId")));
            		conds.add(EntityCondition.makeCondition("fromDate", EntityOperator.BETWEEN, UtilMisc.toList(fromDate, thruDate)));
            		GenericValue minFromDate = EntityUtil.getFirst(delegator.findList("ProductSalesPriceLog", EntityCondition.makeCondition(conds), null, UtilMisc.toList("fromDate"), null, false));
            		if (minFromDate != null) moment = minFromDate.getTimestamp("fromDate");*/
            		
        			Map<String, Object> itemMap = itemProd.getAllFields();
        			
        			Map<String, Object> contextMapFind = UtilMisc.<String, Object>toMap("productId", itemProd.get("productId"),
				        					"productStoreId", productStoreId, "partyId", null, 
				        					"quantityUomId", itemProd.get("uomId"));
        			Map<String, Object> resultValue = dispatcher.runSync("calculateProductPriceCustom", contextMapFind);
        			if (ServiceUtil.isSuccess(resultValue)) {
        				BigDecimal taxPercentage = itemProd.getBigDecimal("taxPercentage");
        				BigDecimal basePrice = (BigDecimal) resultValue.get("basePrice");
        				BigDecimal listPrice = (BigDecimal) resultValue.get("listPrice");
        				
        				itemMap.put("price", basePrice);
        				itemMap.put("unitListPrice", listPrice);
        				itemMap.put("priceVAT", ProductWorker.calculatePriceAfterTax(basePrice, taxPercentage));
        				itemMap.put("unitListPriceVAT", ProductWorker.calculatePriceAfterTax(listPrice, taxPercentage));
        			}
        			
        			listIterator.add(itemMap);
				}
            }
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductSalesPriceChange service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> calculateProductPriceWithTax(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	try {
    		String productId = (String) context.get("productId");
    		
    		Map<String, Object> contextMapFind = ServiceUtil.setServiceFields(dispatcher, "calculateProductPriceCustom", context, userLogin, null, locale);
			Map<String, Object> priceResult = dispatcher.runSync("calculateProductPriceCustom", contextMapFind);
			if (ServiceUtil.isError(priceResult)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(priceResult));
			}
			
			BigDecimal taxPercentage = null;
			Map<String, Object> productTax = ProductWorker.getTaxCategoryInfo(delegator, productId, null);
			if (productTax != null) {
				taxPercentage = (BigDecimal) productTax.get("taxPercentage");
			}
			
			BigDecimal basePrice = (BigDecimal) priceResult.get("basePrice");
			BigDecimal price = (BigDecimal) priceResult.get("price");
			BigDecimal listPrice = (BigDecimal) priceResult.get("listPrice");
			BigDecimal defaultPrice = (BigDecimal) priceResult.get("defaultPrice");
			
			successResult.put("basePrice", basePrice);
			successResult.put("price", price);
			successResult.put("listPrice", listPrice);
			successResult.put("defaultPrice", defaultPrice);
			successResult.put("competitivePrice", priceResult.get("competitivePrice"));
			successResult.put("averageCost", priceResult.get("averageCost"));
			successResult.put("promoPrice", priceResult.get("promoPrice"));
			successResult.put("specialPromoPrice", priceResult.get("specialPromoPrice"));
			successResult.put("isSale", priceResult.get("isSale"));
			successResult.put("validPriceFound", priceResult.get("validPriceFound"));
			successResult.put("currencyUsed", priceResult.get("currencyUsed"));
			successResult.put("orderItemPriceInfos", priceResult.get("orderItemPriceInfos"));
			successResult.put("allQuantityPrices", priceResult.get("allQuantityPrices"));
			
			successResult.put("basePriceTax", ProductWorker.calculatePriceAfterTax(basePrice, taxPercentage));
			successResult.put("priceTax", ProductWorker.calculatePriceAfterTax(price, taxPercentage));
			successResult.put("listPriceTax", ProductWorker.calculatePriceAfterTax(listPrice, taxPercentage));
			successResult.put("defaultPriceTax", ProductWorker.calculatePriceAfterTax(defaultPrice, taxPercentage));
		} catch (Exception e) {
			String errMsg = "Fatal error calling calculateProductPriceWithTax service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return successResult;
	}
	
	public static Map<String, Object> processFromDateThruDate(Map<String,String[]> parameters, Timestamp nowTimestamp, Locale locale) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		Timestamp fromDate = null;
		Timestamp thruDate = null;
		
		Timestamp fromDateParam = null;
		Timestamp thruDateParam = null;
		
		String changeDateTypeId = SalesUtil.getParameter(parameters, "changeDateTypeId");
		if (UtilValidate.isNotEmpty(changeDateTypeId)) {
			if ("TODAY".equals(changeDateTypeId)) {
				fromDateParam = thruDateParam = nowTimestamp;
			} else if ("YESTERDAY".equals(changeDateTypeId)) {
				fromDateParam = new Timestamp(nowTimestamp.getTime() - 86400000);
			} else if ("TOMORROW".equals(changeDateTypeId)) {
				fromDateParam = new Timestamp(nowTimestamp.getTime() + 86400000);
			}
		} else {
			try {
				String fromDateStr = SalesUtil.getParameter(parameters, "fromDate");
		        if (UtilValidate.isNotEmpty(fromDateStr)) {
		        	Long fromDateL = Long.parseLong(fromDateStr);
		        	fromDateParam = new Timestamp(fromDateL);
		        }
		        String thruDateStr = SalesUtil.getParameter(parameters, "thruDate");
		        if (UtilValidate.isNotEmpty(thruDateStr)) {
		        	Long thruDateL = Long.parseLong(thruDateStr);
		        	thruDateParam = new Timestamp(thruDateL);
		        }
	        } catch (Exception e) {
	        	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenFormatDateTime", locale));
	        }
		}
		
		// default value if from date is null
		if (UtilValidate.isEmpty(fromDateParam)) {
			fromDateParam = nowTimestamp;
		}
		
		// fromDate: set to time 00:00:00.000
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fromDateParam);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		fromDate = new Timestamp(calendar.getTimeInMillis());
		
		// thruDate: set to time 23:59:59.999
		if (UtilValidate.isNotEmpty(thruDateParam)) {
			calendar = Calendar.getInstance();
			calendar.setTime(thruDateParam);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 999);
			thruDate = new Timestamp(calendar.getTimeInMillis());
		} else {
			calendar = Calendar.getInstance();
			calendar.setTime(fromDateParam);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 999);
			thruDate = new Timestamp(calendar.getTimeInMillis());
		}
		
		successResult.put("fromDate", fromDate);
		successResult.put("thruDate", thruDate);
		return successResult;
	}
}
