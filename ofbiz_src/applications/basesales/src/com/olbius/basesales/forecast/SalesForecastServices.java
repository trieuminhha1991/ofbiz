package com.olbius.basesales.forecast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
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
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.product.ProductWorker;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.security.util.SecurityUtil;

public class SalesForecastServices {
	public static final String module = SalesForecastServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListSalesForecast(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Security security = ctx.getSecurity();
		
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	try {
    		//check permission for each order type
    		if (!SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "SALESFORECAST_VIEW")) {
				Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to run manual payment transaction!", module);
	            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSTransactionNotAuthorized", locale));
			}
    		/*String partyId = null;
			if (parameters.containsKey("partyId") && parameters.get("partyId").length > 0) {
				partyId = parameters.get("partyId")[0];
			}*/
			List<String> periodTypeIds = new ArrayList<String>();
			String customTimePeriodId = SalesUtil.getParameter(parameters, "customTimePeriodId");
			if (UtilValidate.isNotEmpty(customTimePeriodId)) {
				periodTypeIds.add("SALES_YEAR");
				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
				if (customTimePeriod != null) {
					java.sql.Date fromDate = customTimePeriod.getDate("fromDate");
					Calendar cal = Calendar.getInstance();
					cal.setTime(fromDate);
					cal.set(Calendar.MONTH, 0);
					cal.set(Calendar.DATE, 1);
					java.sql.Date newDate = new java.sql.Date(cal.getTime().getTime());
					GenericValue customTimePeriodD = EntityUtil.getFirst(delegator.findByAnd("CustomTimePeriod", UtilMisc.toMap("fromDate", newDate, "periodTypeId", "SALES_YEAR"), null, false));
					if (customTimePeriodD != null) listAllConditions.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodD.getString("customTimePeriodId")));
				}
			} else {
				periodTypeIds = SalesUtil.getCurrentCustomTimePeriodTypeSales(delegator);
			}
			if (periodTypeIds != null) listAllConditions.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.IN, periodTypeIds));
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("-fromDate");
				listSortFields.add("thruDate");
			}
			listAllConditions.add(EntityCondition.makeCondition("parentSalesForecastId", null));
			listAllConditions.add(EntityCondition.makeCondition("organizationPartyId", SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"))));
			listIterator = delegator.find("SalesForecastAndCustomTimePeriod", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListSalesForecast service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
    public static Map<String, Object> createSalesForecastCustom(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Security security = ctx.getSecurity();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		//check permission for each order type
		if (!SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "SALESFORECAST_NEW")) {
			Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to run manual payment transaction!", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSTransactionNotAuthorized", locale));
		}
		
		String salesForecastId = (String) context.get("salesForecastId");
		String organizationPartyId = (String) context.get("organizationPartyId");
		String internalPartyId = (String) context.get("internalPartyId");
		String customTimePeriodId = (String) context.get("customTimePeriodId");
		/*String parentSalesForecastId = (String) context.get("parentSalesForecastId");
		String currencyUomId = (String) context.get("currencyUomId");
		String quotaAmount = (String) context.get("quotaAmount");
		String forecastAmount = (String) context.get("forecastAmount");
		String bestCastAmount = (String) context.get("bestCastAmount");
		String closedAmount = (String) context.get("closedAmount");
		String percentOfQuotaForecast = (String) context.get("percentOfQuotaForecast");
		String percentOfQuotaClosed = (String) context.get("percentOfQuotaClosed");
		String pinelineAmount = (String) context.get("pinelineAmount");*/
		
		try {
			if (UtilValidate.isNotEmpty(salesForecastId)) {
				GenericValue sf = delegator.findOne("SalesForecast", UtilMisc.toMap("salesForecastId", salesForecastId), false);
				if (sf != null) return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSSalesForecastIdWasExisted", locale));
			}
			if (UtilValidate.isEmpty(organizationPartyId)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSOrganizationIdMustNotBeEmpty", locale));
			}
			if (UtilValidate.isEmpty(customTimePeriodId)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSSalesCustomTimePeriodIdMustNotBeEmpty", locale));
			}
			
			if (UtilValidate.isEmpty(internalPartyId)) {
				internalPartyId = organizationPartyId;
			}
			
			List<EntityCondition> findCondsSF = FastList.newInstance();
			findCondsSF.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
			findCondsSF.add(EntityCondition.makeCondition("internalPartyId", internalPartyId));
			findCondsSF.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId));
			List<GenericValue> listSF = delegator.findList("SalesForecast", EntityCondition.makeCondition(findCondsSF, EntityOperator.AND), null, null, null, false);
			if (UtilValidate.isNotEmpty(listSF)) {
				List<String> listSalesForecastId = EntityUtil.getFieldListFromEntityList(listSF, "salesForecastId", true);
				String errorStr = UtilProperties.getMessage(resource_error, "BSSalesForecastWasExistedWithIdIs", UtilMisc.toMap("salesForecastId", listSalesForecastId.toString()), locale);
				return ServiceUtil.returnError(errorStr);
			}
			
			if (UtilValidate.isEmpty(salesForecastId)) salesForecastId = delegator.getNextSeqId("SalesForecast");
			GenericValue salesForecast = delegator.makeValue("SalesForecast");
			salesForecast.set("salesForecastId", salesForecastId);
			salesForecast.setNonPKFields(context);
			salesForecast.set("internalPartyId", internalPartyId);
			salesForecast.set("createdByUserLoginId", userLogin.get("userLoginId"));
			salesForecast.set("modifiedByUserLoginId", userLogin.get("userLoginId"));
			salesForecast.create();
		} catch (Exception e) {
			String errMsg = "Fatal error calling createSalesForecastCustom service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("salesForecastId", salesForecastId);
		return successResult;
	}
    
    public static Map<String, Object> updateSalesForecastCustom(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	Security security = ctx.getSecurity();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	
    	//check permission for each order type
    	if (!SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "SALESFORECAST_EDIT")) {
    		Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to run manual payment transaction!", module);
    		return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSTransactionNotAuthorized", locale));
    	}
    	
    	String salesForecastId = (String) context.get("salesForecastId");
    	String organizationPartyId = (String) context.get("organizationPartyId");
    	//String internalPartyId = (String) context.get("internalPartyId");
    	String customTimePeriodId = (String) context.get("customTimePeriodId");
    	String parentSalesForecastId = (String) context.get("parentSalesForecastId");
    	
    	try {
    		GenericValue salesForecast = null;
    		if (UtilValidate.isNotEmpty(salesForecastId)) {
    			salesForecast = delegator.findOne("SalesForecast", UtilMisc.toMap("salesForecastId", salesForecastId), false);
    			if (salesForecast == null) return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSNotFoundSalesForecastHasIdIs", locale));
    		}
    		if (UtilValidate.isEmpty(organizationPartyId)) {
    			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSOrganizationIdMustNotBeEmpty", locale));
    		}
    		if (UtilValidate.isEmpty(customTimePeriodId)) {
    			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSSalesCustomTimePeriodIdMustNotBeEmpty", locale));
    		}
    		if (UtilValidate.isNotEmpty(parentSalesForecastId) && parentSalesForecastId.equals(salesForecastId)) {
    			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSCannotChangeSalesForecastIntoIts", locale));
    		}
    		
    		salesForecast.setNonPKFields(context);
    		salesForecast.set("modifiedByUserLoginId", userLogin.get("userLoginId"));
    		salesForecast.store();
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling updateSalesForecastCustom service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("salesForecastId", salesForecastId);
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetSalesForecastContent(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	//List<Map<String, Object>> listIterator = null; => sfTabsContent
    	List<Map<String, Object>> sfTabsContent = new ArrayList<Map<String, Object>>();
		//List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	//List<String> listSortFields = (List<String>) context.get("listSortFields");
    	//EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	try {
    		String salesForecastId = SalesUtil.getParameter(parameters, "salesForecastId");
    		String internalPartyId = SalesUtil.getParameter(parameters, "internalPartyId");
    		if(UtilValidate.isNotEmpty(salesForecastId) && UtilValidate.isNotEmpty(internalPartyId)){
    			GenericValue salesForecast = delegator.findOne("SalesForecast", UtilMisc.toMap("salesForecastId", salesForecastId), false);
    			if (salesForecast != null) {
    				String customTimePeriodId = salesForecast.getString("customTimePeriodId");
    				//String organizationPartyId = salesForecast.getString("organizationPartyId");
    				//String currencyUomId = salesForecast.getString("currencyUomId");
    				
    				List<GenericValue> listPeriodThisAndChildren = new ArrayList<GenericValue>();
    				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
    				if (customTimePeriod != null) {
    					listPeriodThisAndChildren.addAll(SalesForecastWorker.getPeriodChildrenFull(customTimePeriodId, delegator));
    					listPeriodThisAndChildren.add(customTimePeriod);
    				}
    				List<GenericValue> listSalesForeCast = SalesForecastWorker.getDescendantSalesForecast(delegator, salesForecast.getString("salesForecastId"));
    				
    				//List<String> internalPartyIds = new ArrayList<String>();
    				//List<Map<String, Object>> sfTabsContent = new ArrayList<Map<String, Object>>();
    				List<Map<String, Object>> listProduct = new ArrayList<Map<String, Object>>();
    				//List<Map<String, Object>> localData = new ArrayList<Map<String, Object>>(); => sfTabsContent
    				
    				// get list category - list product
    				//String catalogId = SalesUtil.getProductCatalogDefault(delegator);
    				List<String> prodCatalogIdsTmp = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("ProdCatalog", null, null, false), "prodCatalogId", true);
    				if (UtilValidate.isNotEmpty(prodCatalogIdsTmp)) {
    					List<String> listSortFields = UtilMisc.toList("productCode");
    					Map<String, Object> productResult = ProductWorker.getPartListProductByCatalogAndPeriod(parameters, listSortFields, prodCatalogIdsTmp, false, customTimePeriodId, null, null, true, delegator, locale);
    					if (productResult != null) {
    						successResult.put("TotalRows", productResult.get("TotalRows"));
    						listProduct = (List<Map<String, Object>>) productResult.get("listIterator");
    					}
    				}
    				
    				if (UtilValidate.isNotEmpty(listSalesForeCast)) {
    					/*for (GenericValue forecast : listSalesForeCast) {
    						String internalPartyId = forecast.getString("internalPartyId");
    						if (internalPartyId != null && !internalPartyIds.contains(internalPartyId)) {
    							internalPartyIds.add(forecast.getString("internalPartyId"));
    							//currencyUomId = forecast.getString("currencyUomId");
    						} else if (internalPartyId == null && !internalPartyIds.contains("Other")) {
    							internalPartyIds.add("Other");
    						}
    					}*/

						//List<Map<String, Object>> sfRowsContent = new ArrayList<Map<String, Object>>();
						for (Map<String, Object> productItem : listProduct) {
							if (UtilValidate.isNotEmpty(productItem.get("listProduct"))) {
								List<Map<String, Object>> listProductChild = (List<Map<String, Object>>) productItem.get("listProduct");
								if (UtilValidate.isNotEmpty(listProductChild)) {
									for (Map<String, Object>productChildItem : listProductChild) {
    									Map<String, Object> rowDetail = FastMap.newInstance();
    									Map<String, Object> mapResult0 = FastMap.newInstance();
    									Map<String, Object> mapResult = SalesForecastWorker.getForecastHeaderVertical(internalPartyId, listPeriodThisAndChildren, listSalesForeCast, productChildItem, delegator);
    									mapResult0.put("product", productChildItem);
    									mapResult0.put("colData", mapResult);
    									rowDetail.put((String) productChildItem.get("productId"), mapResult0);
    									//sfRowsContent.add(rowDetail);
    									Map<String, Object> rowDataTmp = SalesForecastWorker.processRowData(productChildItem, mapResult);
    									sfTabsContent.add(rowDataTmp);
    								}
    								Map<String, Object> rowDetail2 = FastMap.newInstance();
    								Map<String, Object> mapResult0 = FastMap.newInstance();
    								Map<String, Object> mapResult2 = SalesForecastWorker.getForecastHeaderVertical(internalPartyId, listPeriodThisAndChildren, listSalesForeCast, productItem, delegator);
    								mapResult0.put("product", productItem);
    								mapResult0.put("colData", mapResult2);
    								rowDetail2.put((String) productItem.get("productId"), mapResult0);
    								//sfRowsContent.add(rowDetail2);
    								Map<String, Object> rowDataTmp = SalesForecastWorker.processRowData(productItem, mapResult2);
    								sfTabsContent.add(rowDataTmp);
								}
							} else {
								Map<String, Object> rowDetail = FastMap.newInstance();
								Map<String, Object> mapResult0 = FastMap.newInstance();
								Map<String, Object> mapResult = SalesForecastWorker.getForecastHeaderVertical(internalPartyId, listPeriodThisAndChildren, listSalesForeCast, productItem, delegator);
								mapResult0.put("product", productItem);
								mapResult0.put("colData", mapResult);
								rowDetail.put((String) productItem.get("productId"), mapResult0);
								//sfRowsContent.add(rowDetail);
								Map<String, Object> rowDataTmp = SalesForecastWorker.processRowData(productItem, mapResult);
								sfTabsContent.add(rowDataTmp);
							}
						}
						//Map<String, Object> mapTemp = FastMap.newInstance();
						//mapTemp.put("forecastAndItems", sfRowsContent);
						//mapTemp.put("localData", localData);
						//sfTabsContent.add(mapTemp);
    				}
    			}
    		}
    		
    	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetSalesForecastContent service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", sfTabsContent);
    	return successResult;
    }
    
    /*
    public static Map<String, Object> jqGetSalesForecastContent(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	//List<Map<String, Object>> listIterator = null; => sfTabsContent
    	List<Map<String, Object>> sfTabsContent = new ArrayList<Map<String, Object>>();
		//List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	//List<String> listSortFields = (List<String>) context.get("listSortFields");
    	//EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	try {
    		String salesForecastId = SalesUtil.getParameter(parameters, "salesForecastId");
    		String internalPartyId = SalesUtil.getParameter(parameters, "internalPartyId");
    		if(UtilValidate.isNotEmpty(salesForecastId) && UtilValidate.isNotEmpty(internalPartyId)){
    			GenericValue salesForecast = delegator.findOne("SalesForecast", UtilMisc.toMap("salesForecastId", salesForecastId), false);
    			if (salesForecast != null) {
    				String customTimePeriodId = salesForecast.getString("customTimePeriodId");
    				String organizationPartyId = salesForecast.getString("organizationPartyId");
    				//String currencyUomId = salesForecast.getString("currencyUomId");
    				
    				List<GenericValue> listPeriodThisAndChildren = new ArrayList<GenericValue>();
    				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
    				if (customTimePeriod != null) {
    					listPeriodThisAndChildren.addAll(SalesForecastWorker.getPeriodChildrenFull(customTimePeriodId, delegator));
    					listPeriodThisAndChildren.add(customTimePeriod);
    				}
    				List<GenericValue> listSalesForeCast = SalesForecastWorker.getDescendantSalesForecast(delegator, salesForecast.getString("salesForecastId"));
    				
    				List<String> internalPartyIds = new ArrayList<String>();
    				List<Map<String, Object>> sfTabsContent = new ArrayList<Map<String, Object>>();
    				List<Map<String, Object>> listProduct = new ArrayList<Map<String, Object>>();
    				List<Map<String, Object>> localData = new ArrayList<Map<String, Object>>();
    				
    				// get list category - list product
    				//String catalogId = SalesUtil.getProductCatalogDefault(delegator);
    				List<String> prodCatalogIdsTmp = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("ProdCatalog", null, null, false), "prodCatalogId", true);
    				if (UtilValidate.isNotEmpty(prodCatalogIdsTmp)) {
    					Map<String, Object> productResult = ProductWorker.getPartListProductByCatalogAndPeriod(parameters, delegator, locale, prodCatalogIdsTmp, false, customTimePeriodId, null, null);
    					if (productResult != null) {
    						successResult.put("TotalRows", productResult.get("TotalRows"));
    						listProduct = (List<Map<String, Object>>) productResult.get("listIterator");
    					}
    				}
    				
    				if (UtilValidate.isNotEmpty(listSalesForeCast)) {
    					for (GenericValue forecast : listSalesForeCast) {
    						String internalPartyId = forecast.getString("internalPartyId");
    						if (internalPartyId != null && !internalPartyIds.contains(internalPartyId)) {
    							internalPartyIds.add(forecast.getString("internalPartyId"));
    							//currencyUomId = forecast.getString("currencyUomId");
    						} else if (internalPartyId == null && !internalPartyIds.contains("Other")) {
    							internalPartyIds.add("Other");
    						}
    					}

    					for (String itemParty : internalPartyIds) {
    						List<Map<String, Object>> sfRowsContent = new ArrayList<Map<String, Object>>();
    						for (Map<String, Object> productItem : listProduct) {
    							if (UtilValidate.isNotEmpty(productItem.get("listProduct"))) {
    								List<Map<String, Object>> listProductChild = (List<Map<String, Object>>) productItem.get("listProduct");
    								if (UtilValidate.isNotEmpty(listProductChild)) {
    									for (Map<String, Object>productChildItem : listProductChild) {
        									Map<String, Object> rowDetail = FastMap.newInstance();
        									Map<String, Object> mapResult0 = FastMap.newInstance();
        									Map<String, Object> mapResult = SalesForecastWorker.getForecastHeaderVertical(itemParty, listPeriodThisAndChildren, listSalesForeCast, productChildItem, delegator);
        									mapResult0.put("product", productChildItem);
        									mapResult0.put("colData", mapResult);
        									rowDetail.put((String) productChildItem.get("productId"), mapResult0);
        									sfRowsContent.add(rowDetail);
        									Map<String, Object> rowDataTmp = SalesForecastWorker.processRowData(productChildItem, mapResult);
        									localData.add(rowDataTmp);
        								}
        								Map<String, Object> rowDetail2 = FastMap.newInstance();
        								Map<String, Object> mapResult0 = FastMap.newInstance();
        								Map<String, Object> mapResult2 = SalesForecastWorker.getForecastHeaderVertical(itemParty, listPeriodThisAndChildren, listSalesForeCast, productItem, delegator);
        								mapResult0.put("product", productItem);
        								mapResult0.put("colData", mapResult2);
        								rowDetail2.put((String) productItem.get("productId"), mapResult0);
        								sfRowsContent.add(rowDetail2);
        								Map<String, Object> rowDataTmp = SalesForecastWorker.processRowData(productItem, mapResult2);
        								localData.add(rowDataTmp);
    								}
    							} else {
    								Map<String, Object> rowDetail = FastMap.newInstance();
    								Map<String, Object> mapResult0 = FastMap.newInstance();
    								Map<String, Object> mapResult = SalesForecastWorker.getForecastHeaderVertical(itemParty, listPeriodThisAndChildren, listSalesForeCast, productItem, delegator);
    								mapResult0.put("product", productItem);
    								mapResult0.put("colData", mapResult);
    								rowDetail.put((String) productItem.get("productId"), mapResult0);
    								sfRowsContent.add(rowDetail);
    								Map<String, Object> rowDataTmp = SalesForecastWorker.processRowData(productItem, mapResult);
    								localData.add(rowDataTmp);
    							}
    						}
    						Map<String, Object> mapTemp = FastMap.newInstance();
    						if (itemParty == null) itemParty = ""; 
    						if (organizationPartyId == null) organizationPartyId = ""; 
    						mapTemp.put("internalPartyIds", itemParty);
    						mapTemp.put("organizationPartyId", organizationPartyId);
    						mapTemp.put("forecastAndItems", sfRowsContent);
    						mapTemp.put("localData", localData);
    						sfTabsContent.add(mapTemp);
    					}
    				}
    				listIterator = sfTabsContent;
    			}
    		}
    		
    	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetSalesForecastContent service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", sfTabsContent);
    	return successResult;
    }
     */
}
