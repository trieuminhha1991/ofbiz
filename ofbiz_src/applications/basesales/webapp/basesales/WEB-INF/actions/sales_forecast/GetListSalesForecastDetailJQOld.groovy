/*
 * SalesForecastAndCustomTimePeriod
 * Get list sales forecast by custom time period (SALES_YEAR)
 * Get list all product category, products in category in system.
 * Get list custom time period is children of custom time period was selected.
 */

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.sql.Timestamp;
import java.sql.Date;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.order.order.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.accounting.payment.*;

import com.olbius.basesales.util.SalesUtil ;

import javolution.util.FastList;
import javolution.util.FastMap;

import com.olbius.basesales.product.ProductWorker;

/**
 * Get period children of a Period parameter.
 * @param customTimePeriodId Period want get children
 * @return List Strings of customTimePeriodId
 */
public List<String> getPeriodChildren(String customTimePeriodId) {
	List<String> result = new ArrayList<String>();
	EntityFindOptions periodFindOptions = new EntityFindOptions();
	periodFindOptions.setDistinct(true);
	List<GenericValue> periodChildren = delegator.findList("CustomTimePeriod", 
		EntityCondition.makeCondition("parentPeriodId", EntityOperator.EQUALS, customTimePeriodId), null, ["periodName"], periodFindOptions, false);
	for (child in periodChildren) {
		List<String> ppItems = getPeriodChildren(child.getString("customTimePeriodId"));
		if (ppItems != null) result.addAll(ppItems);
		result.add(child.getString("customTimePeriodId"));
	}
	return result;
}
/*
 * List<EntityCondition> listAllCondition = FastList.newInstance();
	listAllCondition.add(SALES_MONTH);
 */
public List<GenericValue> getPeriodChildrenFull(String customTimePeriodId) {
	List<GenericValue> result = new ArrayList<GenericValue>();
	EntityFindOptions periodFindOptions = new EntityFindOptions();
	periodFindOptions.setDistinct(true);
	List<GenericValue> periodChildren = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition("parentPeriodId", customTimePeriodId), null, ["periodName"], periodFindOptions, false);
	for (child in periodChildren) {
		if ("SALES_MONTH" != child.periodTypeId) {
			List<GenericValue> ppItems = getPeriodChildrenFull(child.getString("customTimePeriodId"));
			if (ppItems != null) result.addAll(ppItems);
		} else {
			result.add(child);
		}
	}
	return result;
}

public List<GenericValue> getDescendantSalesForecast(Delegator delegator, String salesForecastId) {
	List<GenericValue> returnValue = new ArrayList<GenericValue>();
	GenericValue sf = delegator.findOne("SalesForecast", UtilMisc.toMap("salesForecastId", salesForecastId), false);
	if (sf != null) {
		returnValue.add(sf);
		List<GenericValue> sfChilds = delegator.findByAnd("SalesForecast", UtilMisc.toMap("parentSalesForecastId", salesForecastId), null, false);
		if (sfChilds) {
			for (GenericValue item : sfChilds) {
				returnValue.addAll(getDescendantSalesForecast(delegator, item.getString("salesForecastId")));
			}
		}
	}
	return returnValue;
}

public Map<String, Object> getForecastHeaderVertical(String partyId, List<GenericValue> listCustomTimePeriod, List<GenericValue> listSalesForeCast, Map<String, Object> product) {
	String productId = product.productId;
	Map<String, Object> returnValue = FastMap.newInstance(); // [month1 = value, month2 = value]
	
	List<EntityCondition> filterParty = FastList.newInstance();
	if ("Other" == partyId) {
		filterParty.add(EntityCondition.makeCondition("internalPartyId", EntityOperator.EQUALS, null));
	} else {
		filterParty.add(EntityCondition.makeCondition("internalPartyId", partyId));
	}
	EntityCondition filterAll = EntityCondition.makeCondition(filterParty, EntityOperator.AND);
	List<GenericValue> getListForecastFiltered = EntityUtil.filterByCondition(listSalesForeCast, filterAll);
	
	if (getListForecastFiltered != null) {
		if ((getListForecastFiltered != null && getListForecastFiltered.size() > 0)) {
			for (GenericValue periodItem : listCustomTimePeriod) {
				String periodId = periodItem.customTimePeriodId;
				BigDecimal quantityTmp = BigDecimal.ZERO;
				Map<String, Object> valueMap = FastMap.newInstance();
				
				List<GenericValue> getListForecast2 = FastList.newInstance();
				if (periodItem.periodTypeId == "SALES_MONTH") {
					EntityCondition filterByPeriod = EntityCondition.makeCondition("customTimePeriodId", periodId);
					getListForecast2 = EntityUtil.filterByCondition(getListForecastFiltered, filterByPeriod);
				} else {
					List<String> m_customTimePeriodIds = EntityUtil.getFieldListFromEntityList(getPeriodChildrenFull(periodId), "customTimePeriodId", true);
					if (m_customTimePeriodIds) {
						EntityCondition filterByPeriod = EntityCondition.makeCondition("customTimePeriodId", EntityOperator.IN, m_customTimePeriodIds);
						getListForecast2 = EntityUtil.filterByCondition(getListForecastFiltered, filterByPeriod);
					}
				}
				if (periodItem.periodTypeId == "SALES_MONTH") {
					if (getListForecast2) {
						for (GenericValue forecast : getListForecast2) {
							List<GenericValue> forecastDetailItems = delegator.findByAnd("SalesForecastDetail", ["salesForecastId" : forecast.salesForecastId], null, false);
							List<String> productListFinded = EntityUtil.getFieldListFromEntityList(forecastDetailItems, "productId", true);
							if (productListFinded != null && productListFinded.size() > 0) {
								if (productListFinded.contains(productId)) {
									boolean isFinded = false;
									for (GenericValue forecastItem : forecastDetailItems) {
										String productIdTmp = forecastItem.productId;
										if (UtilValidate.isNotEmpty(productIdTmp)) {
											if (productId.equals(productIdTmp)) {
												quantityTmp = forecastItem.getBigDecimal("quantity");
												valueMap.put("salesForecastId", forecast.salesForecastId);
												valueMap.put("salesForecastDetailId", forecastItem.salesForecastDetailId);
												isFinded = true;
												break;
											}
										}
									}
									if (isFinded) break;
								}
							}
						}
					}
				} else {
					if (getListForecast2) {
						for (GenericValue forecast : getListForecast2) {
							List<GenericValue> forecastDetailItems = delegator.findByAnd("SalesForecastDetail", ["salesForecastId" : forecast.salesForecastId], null, false);
							List<String> productListFinded = EntityUtil.getFieldListFromEntityList(forecastDetailItems, "productId", true);
							if (productListFinded != null && productListFinded.size() > 0) {
								if (productListFinded.contains(productId)) {
									for (GenericValue forecastItem : forecastDetailItems) {
										String productIdTmp = forecastItem.productId;
										if (UtilValidate.isNotEmpty(productIdTmp)) {
											if (productId.equals(productIdTmp)) {
												quantityTmp = quantityTmp.add(forecastItem.getBigDecimal("quantity"));
											}
										}
									}
								}
							}
						}
					}
				}
				valueMap.put("customTimePeriod", periodItem);
				valueMap.put("quantity", quantityTmp);
				returnValue.put(periodId, valueMap);
			}
		} else {
			println("Bo qua, QUY, NAM");
		}
	}
	return returnValue;
}
private processRowData(Map<String, Object> productItem, Map<String, Object> rowDetailCommon) {
	Map<String, Object> rowDetailFinal = FastMap.newInstance();
	rowDetailFinal.put("productId", productItem.productId);
	rowDetailFinal.put("productCode", productItem.productCode);
	rowDetailFinal.put("internalName", productItem.internalName);
	rowDetailFinal.put("productName", productItem.productName);
	rowDetailFinal.put("quantityUomId", productItem.quantityUomId);
	rowDetailFinal.put("parentProductId", productItem.parentProductId);
	rowDetailFinal.put("isVirtual", productItem.isVirtual);
	rowDetailFinal.put("isVariant", productItem.isVariant);
	rowDetailFinal.put("features", productItem.features);
	for (entry in rowDetailCommon.entrySet()) {
		Map<String, Object> cellValue = entry.getValue();
		String m_customTimePeriodId = entry.getKey();
		String m_salesForecastId = cellValue.salesForecastId;
		String m_salesForecastDetailId = cellValue.salesForecastDetailId;
		if (UtilValidate.isNotEmpty(m_salesForecastId)) rowDetailFinal.put(m_customTimePeriodId + "_sf", m_salesForecastId);
		if (UtilValidate.isNotEmpty(m_salesForecastDetailId)) rowDetailFinal.put(m_customTimePeriodId + "_sfi", m_salesForecastDetailId);
		rowDetailFinal.put(m_customTimePeriodId, cellValue.quantity);
		rowDetailFinal.put(m_customTimePeriodId + "_old", cellValue.quantity);
	}
	return rowDetailFinal;
}

salesForecastId = parameters.salesForecastId;
context.salesForecastId = salesForecastId;
if(salesForecastId){
	GenericValue salesForecast = delegator.findOne("SalesForecast", ["salesForecastId" : salesForecastId], false);
	if (salesForecast) {
		context.salesForecast = salesForecast;
		
		String customTimePeriodId = salesForecast.getString("customTimePeriodId");
		GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", ["customTimePeriodId" : customTimePeriodId], false);
		String organizationPartyId = salesForecast.getString("organizationPartyId");
		String currencyUomId = salesForecast.getString("currencyUomId");
		
		List<GenericValue> listPeriodThisAndChildren = new ArrayList<GenericValue>();
		if (customTimePeriodId) {
			listPeriodThisAndChildren.addAll(getPeriodChildrenFull(customTimePeriodId));
			listPeriodThisAndChildren.add(customTimePeriod);
			context.listPeriodThisAndChildren = listPeriodThisAndChildren;
		}
		List<GenericValue> listSalesForeCast = getDescendantSalesForecast(delegator, salesForecast.getString("salesForecastId"));
		
		List<String> internalPartyIds = new ArrayList<String>();
		List<Map<String, Object>> sfTabsContent = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listProduct = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> localData = new ArrayList<Map<String, Object>>();
		
		// get list category - list product
		//String catalogId = SalesUtil.getProductCatalogDefault(delegator);
		List<String> prodCatalogIdsTmp = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("ProdCatalog", null, null, false), "prodCatalogId", true);
		if (prodCatalogIdsTmp) {
			listProduct = ProductWorker.getListProductByCatalogAndPeriod(delegator, locale, prodCatalogIdsTmp, false, customTimePeriodId, null, null);
		}
		
		if (listSalesForeCast) {
			for (forecast in listSalesForeCast) {
				if (forecast.internalPartyId != null && !internalPartyIds.contains(forecast.internalPartyId)) {
					internalPartyIds.add(forecast.internalPartyId);
					currencyUomId = forecast.currencyUomId;
				} else if (forecast.internalPartyId == null && !internalPartyIds.contains("Other")) {
					internalPartyIds.add("Other");
				}
			}

			for (itemParty in internalPartyIds) {
				List<Map<String, Object>> sfRowsContent = new ArrayList<Map<String, Object>>();
				for (productItem in listProduct) {
					if (UtilValidate.isNotEmpty(productItem.listProduct)) {
						List<Map<String, Object>> listProductChild = productItem.listProduct;
						for (productChildItem in listProductChild) {
							Map<String, Object> rowDetail = FastMap.newInstance();
							Map<String, Object> mapResult0 = FastMap.newInstance();
							Map<String, Object> mapResult = getForecastHeaderVertical(itemParty, listPeriodThisAndChildren, listSalesForeCast, productChildItem);
							mapResult0.put("product", productChildItem);
							mapResult0.put("colData", mapResult);
							rowDetail.put(productChildItem.productId, mapResult0);
							sfRowsContent.add(rowDetail);
							Map<String, Object> rowDataTmp = processRowData(productChildItem, mapResult);
							localData.add(rowDataTmp);
						}
						Map<String, Object> rowDetail2 = FastMap.newInstance();
						Map<String, Object> mapResult0 = FastMap.newInstance();
						Map<String, Object> mapResult2 = getForecastHeaderVertical(itemParty, listPeriodThisAndChildren, listSalesForeCast, productItem);
						mapResult0.put("product", productItem);
						mapResult0.put("colData", mapResult2);
						rowDetail2.put(productItem.productId, mapResult0);
						sfRowsContent.add(rowDetail2);
						Map<String, Object> rowDataTmp = processRowData(productItem, mapResult2);
						localData.add(rowDataTmp);
					} else {
						Map<String, Object> rowDetail = FastMap.newInstance();
						Map<String, Object> mapResult0 = FastMap.newInstance();
						Map<String, Object> mapResult = getForecastHeaderVertical(itemParty, listPeriodThisAndChildren, listSalesForeCast, productItem);
						mapResult0.put("product", productItem);
						mapResult0.put("colData", mapResult);
						rowDetail.put(productItem.productId, mapResult0);
						sfRowsContent.add(rowDetail);
						Map<String, Object> rowDataTmp = processRowData(productItem, mapResult);
						localData.add(rowDataTmp);
					}
				}
				mapTemp = [:];
				mapTemp.internalPartyIds = itemParty ?: "";
				mapTemp.organizationPartyId = organizationPartyId ?: "";
				mapTemp.forecastAndItems = sfRowsContent ?: "";
				mapTemp.localData = localData ?: [];
				sfTabsContent.add(mapTemp);
			}
		}
		context.customTimePeriodId = customTimePeriodId;
		context.currencyUomId = currencyUomId;
		context.sfTabsContent = sfTabsContent;
	}
}
