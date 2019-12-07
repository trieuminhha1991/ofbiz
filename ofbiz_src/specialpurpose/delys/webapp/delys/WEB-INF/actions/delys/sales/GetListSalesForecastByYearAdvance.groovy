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
import org.ofbiz.product.inventory.InventoryWorker;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.accounting.payment.*;

/**
 * Get period children of a Period parameter.
 * @param customTimePeriodId Period want get children
 * @return List Strings of customTimePeriodId
 */
public List<String> getPeriodChildren(String customTimePeriodId) {
	List<String> result = new ArrayList<String>();
	periodCond = EntityCondition.makeCondition("parentPeriodId", EntityOperator.EQUALS, customTimePeriodId);
	periodOrder = ["periodName"];
	//periodFields = ["customTimePeriodId"] as Set; //, "periodName", "periodTypeId", "parentPeriodId"
	periodFindOptions = new EntityFindOptions();
	periodFindOptions.setDistinct(true);
	periodChildren = delegator.findList("CustomTimePeriod", periodCond, null, periodOrder, periodFindOptions, false);
	for (child in periodChildren) {
		ppItems = getPeriodChildren(child.customTimePeriodId);
		result.addAll(ppItems);
		result.add(child.customTimePeriodId);
	}
	return result;
}

/**
 * 
 * @param partyId
 * @param customTimePeriodId
 * @param listSalesForeCast
 * @param listProduct
 * @return
 */
public Map getForecastHeader(String partyId, String customTimePeriodId, List<GenericValue> listSalesForeCast, List<Map> listProduct) {
	mapTemp = [:];
	Map<String, Object> filterParty = UtilMisc.toMap("internalPartyId", partyId);
	Map<String, Object> filterAll = UtilMisc.toMap("internalPartyId", partyId, "customTimePeriodId", customTimePeriodId);
	List<GenericValue> getListForecastFilteredByParty = EntityUtil.filterByAnd(listSalesForeCast, filterParty);
	List<GenericValue> getListForecastFiltered = EntityUtil.filterByAnd(listSalesForeCast, filterAll);
	
	GenericValue pCustomTimePeriod = delegator.findOne("CustomTimePeriod", ["customTimePeriodId" : customTimePeriodId], false);
	Calendar cal = Calendar.getInstance();
	cal.setTime(pCustomTimePeriod.getDate("fromDate"));
	/*DateTime: cal.setTimeInMillis(pCustomTimePeriod.getTimestamp("fromDate").getTime());*/
	cal.add(Calendar.YEAR, -1); // to get previous year add -1
	Date beforeDate = new java.sql.Date(cal.getTimeInMillis());
	List<GenericValue> ayoListCustomTimePeriod = delegator.findByAnd("CustomTimePeriod", ["fromDate" : beforeDate, "periodTypeId" : pCustomTimePeriod.getString("periodTypeId")], null, false);
	/*DateTime: java.sql.Date beforeDate = new java.sql.Date(cal.getTimeInMillis());
	java.sql.Timestamp beforeDate2 = new java.sql.Timestamp(beforeDate.getTime());
	List<GenericValue> ayoListCustomTimePeriod = delegator.findByAnd("CustomTimePeriod", ["fromDate" : beforeDate2, "periodTypeId" : pCustomTimePeriod.getString("periodTypeId")], null, false);*/
	GenericValue ayoCustomTimePeriod = null;
	if (ayoListCustomTimePeriod) {
		ayoCustomTimePeriod = EntityUtil.getFirst(ayoListCustomTimePeriod);
	}
	List<GenericValue> ayoListForecast = new ArrayList<GenericValue>();
	if (ayoCustomTimePeriod) {
		//ayoCustomTimePeriodYear = getCustomTimePeriodParentNameYear(ayoCustomTimePeriod.customTimePeriodId);
		ayoListForecast = delegator.findByAnd("SalesForecast", ["internalPartyId" : partyId, "customTimePeriodId" : customTimePeriodId], null, false);
	}
	if (getListForecastFilteredByParty != null) {
		/* Get sales forecasts for 2015
		 * if forecast 2014 have forecast for (this customTimePeriodId parameter - a year)
		 * or if forecast 2015 have forecast for this customTimePeriodId parameter
		 * then this customTimePeriod is SALES_MONTH
		 * and get salesForecast and find list salesForecastItem.
		 */
		if ((getListForecastFiltered != null && getListForecastFiltered.size() > 0) || (ayoListForecast != null && ayoListForecast.size() > 0)) {
			GenericValue forecastFirst = delegator.makeValue("SalesForecast");
			List<GenericValue> listForecastItem = new ArrayList<GenericValue>();
			if (getListForecastFiltered != null && getListForecastFiltered.size() > 0) {
				forecastFirst = EntityUtil.getFirst(getListForecastFiltered);
				listForecastItem = delegator.findByAnd("SalesForecastDetail", ["salesForecastId" : forecastFirst.salesForecastId], null, false);
			} else {
				forecastFirst.put("internalPartyId", partyId);
				forecastFirst.put("customTimePeriodId", customTimePeriodId);
			}
			
			GenericValue ayoForecastFirst = delegator.makeValue("SalesForecast");
			List<GenericValue> ayoListForecastItem = new ArrayList<GenericValue>();
			String ayoCustomTimePeriodYear = "";
			if (ayoCustomTimePeriod) {
				ayoCustomTimePeriodYear = getCustomTimePeriodParentNameYear(ayoCustomTimePeriod.customTimePeriodId);
				ayoListForecast = delegator.findByAnd("SalesForecast", ["internalPartyId" : partyId, "customTimePeriodId" : ayoCustomTimePeriod.customTimePeriodId], null, false);
				if (ayoListForecast) {
					ayoForecastFirst = EntityUtil.getFirst(ayoListForecast);
					ayoListForecastItem = delegator.findByAnd("SalesForecastDetail", ["salesForecastId" : ayoForecastFirst.salesForecastId], null, false);
				}
			}
			
			arrayForecastItems = [];
			arrayAyoForecastItems = [];
			arrayPercentItems = [];
			for (productCate in listProduct) {
				productList = productCate.productList;
				for (productItem in productList) {
					GenericValue newForecastItem = delegator.makeValue("SalesForecastDetail");
					GenericValue ayoForecastItem = delegator.makeValue("SalesForecastDetail");
					BigDecimal percent = BigDecimal.ZERO;
					BigDecimal quantityBig = null;
					BigDecimal ayoQuantity = null;
					boolean isAyoAdded = false;
					for (forecastItem in listForecastItem) {
						if ((forecastItem.productId != null) && (forecastItem.productId == productItem.productId)) {
							arrayForecastItems.add(forecastItem);
							quantityBig = new BigDecimal(forecastItem.quantity);
							break;
						}
					}
					if (quantityBig == null) {
						newForecastItem.put("productId", productItem.productId);
						newForecastItem.put("quantity", BigDecimal.ZERO);
						arrayForecastItems.add(newForecastItem);
					}
					for (forecastItem in ayoListForecastItem) {
						if ((forecastItem.productId != null) && (forecastItem.productId == productItem.productId)) {
							arrayAyoForecastItems.add(forecastItem);
							ayoQuantity = forecastItem.quantity;
							break;
						}
					}
					if (ayoQuantity == null) {
						ayoForecastItem.put("productId", productItem.productId);
						ayoForecastItem.put("quantity", BigDecimal.ZERO);
						arrayAyoForecastItems.add(ayoForecastItem);
					}
					if ((quantityBig != null) && (quantityBig.compareTo(BigDecimal.ZERO) > 0)) {
						if (ayoQuantity && (BigDecimal.ZERO.compareTo(ayoQuantity) != 0)) {
							percent = quantityBig.divide(ayoQuantity, 4, RoundingMode.HALF_UP).multiply(100);
						} else {
							percent = BigDecimal.ZERO;
						}
					} else {
						percent = BigDecimal.ZERO;
					}
					arrayPercentItems.add(percent);
				}
			}
			mapTemp.ayoForecast = ayoForecastFirst ?: "";
			mapTemp.ayoForecastItems = arrayAyoForecastItems ?: "";
			mapTemp.ayoYear = ayoCustomTimePeriodYear ?: "";
			mapTemp.forecast = forecastFirst ?: "";
			mapTemp.forecastItems = arrayForecastItems ?: "";
			mapTemp.percentItems = arrayPercentItems ?: "";
			mapTemp.year = getCustomTimePeriodParentNameYear(customTimePeriodId) ?: "";
		} else {
		/* Get sales forecasts for 2015
		 * if forecast 2014 not have forecast for (this customTimePeriodId parameter - a year)
		 * and if forecast 2015 not have forecast for this customTimePeriodId parameter
		 * then this customTimePeriod is SALES_QUARTER or SALES_YEAR
		 * and get customTimePeriod children of this customTimePeriodId parameter and (find list salesForecast and find list salesForecastItem)
		 * after calculate list salesForecastItem by product list.
		 */
			Map<String, Object> newForecastMap = UtilMisc.toMap("internalPartyId", partyId, "customTimePeriodId", customTimePeriodId);
			List<GenericValue> getListForecastOk2 = new ArrayList<GenericValue>();
			List<String> listPeriodChild = getPeriodChildren(customTimePeriodId);
			for (periodChild in listPeriodChild) {
				getListForecastOk2.addAll(getForecastHeaderInner(partyId, periodChild, listSalesForeCast));
			}
			//get list sales forecast details
			List<GenericValue> getListForecastDetailOk2 = new ArrayList<GenericValue>();
			for (item in getListForecastOk2) {
				listForecastItem = delegator.findByAnd("SalesForecastDetail", ["salesForecastId" : item.salesForecastId], null, false);
				getListForecastDetailOk2.addAll(listForecastItem);
			}
			List<Map> getListForecastDetailOk3 = new ArrayList<Map>();
			for (productCate in listProduct) {
				if (productCate.productList) {
					for (product in productCate.productList) {
						BigDecimal quantity = BigDecimal.ZERO;
						Map<String, Object> newForecastDetailMap = UtilMisc.toMap("productId", product.productId);
						for (item in getListForecastDetailOk2) {
							if (product.productId.equals(item.productId)) {
								quantity = quantity.add(item.quantity);
							}
						}
						newForecastDetailMap.put("quantity", quantity);
						getListForecastDetailOk3.add(newForecastDetailMap);
					}
				}
			}
			
			GenericValue ayoForecastFirst = delegator.makeValue("SalesForecast");
			List<GenericValue> ayoListForecastItem = new ArrayList<GenericValue>();
			String ayoCustomTimePeriodYear = "";
			List<Map> ayoGetListForecastDetailOk3 = new ArrayList<Map>();
			if (ayoCustomTimePeriod) {
				List<GenericValue> ayoGetListForecastOk2 = new ArrayList<GenericValue>();
				List<String> ayoListPeriodChild = getPeriodChildren(ayoCustomTimePeriod.customTimePeriodId);
				ayoCustomTimePeriodYear = getCustomTimePeriodParentNameYear(ayoCustomTimePeriod.customTimePeriodId);
				for (periodChild in ayoListPeriodChild) {
					ayoListForecastTemp = delegator.findByAnd("SalesForecast", ["internalPartyId" : partyId, "customTimePeriodId" : periodChild], null, false);
					ayoGetListForecastOk2.addAll(ayoListForecastTemp);
				}
				List<GenericValue> ayoGetListForecastDetailOk2 = new ArrayList<GenericValue>();
				//get list sales forecast details
				for (item in ayoGetListForecastOk2) {
					ayoListForecastItem = delegator.findByAnd("SalesForecastDetail", ["salesForecastId" : item.salesForecastId], null, false);
					ayoGetListForecastDetailOk2.addAll(ayoListForecastItem);
				}
				for (productCate in listProduct) {
					if (productCate.productList) {
						for (product in productCate.productList) {
							BigDecimal quantity = BigDecimal.ZERO;
							Map<String, Object> newAyoForecastDetailMap = UtilMisc.toMap("productId", product.productId);
							for (item in ayoGetListForecastDetailOk2) {
								if (product.productId.equals(item.productId)) {
									quantity = quantity.add(item.quantity);
								}
							}
							newAyoForecastDetailMap.put("quantity", quantity);
							ayoGetListForecastDetailOk3.add(newAyoForecastDetailMap);
						}
					}
				}
			}
			arrayForecastItems = [];
			arrayAyoForecastItems = [];
			arrayPercentItems = [];
			for (productCate in listProduct) {
				productList = productCate.productList;
				for (productItem in productList) {
					GenericValue newForecastItem = delegator.makeValue("SalesForecastDetail");
					GenericValue ayoForecastItem = delegator.makeValue("SalesForecastDetail");
					BigDecimal percent = BigDecimal.ZERO;
					BigDecimal quantityBig = null;
					BigDecimal ayoQuantity = null;
					boolean isAyoAdded = false;
					for (forecastItem in getListForecastDetailOk3) {
						if ((forecastItem.productId != null) && (forecastItem.productId == productItem.productId)) {
							arrayForecastItems.add(forecastItem);
							quantityBig = new BigDecimal(forecastItem.quantity);
							break;
						}
					}
					if (quantityBig == null) {
						newForecastItem.put("productId", productItem.productId);
						newForecastItem.put("quantity", BigDecimal.ZERO);
						arrayForecastItems.add(newForecastItem);
					}
					for (forecastItem in ayoGetListForecastDetailOk3) {
						if ((forecastItem.productId != null) && (forecastItem.productId == productItem.productId)) {
							arrayAyoForecastItems.add(forecastItem);
							ayoQuantity = new BigDecimal(forecastItem.quantity);
							break;
						}
					}
					if (ayoQuantity == null) {
						ayoForecastItem.put("productId", productItem.productId);
						ayoForecastItem.put("quantity", BigDecimal.ZERO);
						arrayAyoForecastItems.add(ayoForecastItem);
					}
					if ((quantityBig != null) && (quantityBig.compareTo(BigDecimal.ZERO) > 0)) {
						if (ayoQuantity && (BigDecimal.ZERO.compareTo(ayoQuantity) != 0) && (BigDecimal.ZERO.compareTo(quantityBig) != 0)) {
							percent = quantityBig.divide(ayoQuantity, 4, RoundingMode.HALF_UP).multiply(100);
						} else {
							percent = BigDecimal.ZERO;
						}
					} else {
						percent = BigDecimal.ZERO;
					}
					arrayPercentItems.add(percent);
				}
			}
			
			mapTemp.ayoForecast = ayoForecastFirst ?: "";
			mapTemp.ayoForecastItems = arrayAyoForecastItems ?: "";
			mapTemp.ayoYear = ayoCustomTimePeriodYear ?: "";
			mapTemp.forecast = newForecastMap ?: "";
			mapTemp.forecastItems = arrayForecastItems ?: "";
			mapTemp.percentItems = arrayPercentItems ?: "";
			mapTemp.year = getCustomTimePeriodParentNameYear(customTimePeriodId) ?: "";
		}
	}
	return mapTemp;
}

public List<GenericValue> getForecastHeaderInner(String partyId, String customTimePeriodId, List<GenericValue> listSalesForeCast) {
	List<GenericValue> result = new ArrayList<GenericValue>();
	Map<String, Object> filter = UtilMisc.toMap("internalPartyId", partyId, "customTimePeriodId", customTimePeriodId);
	result = EntityUtil.filterByAnd(listSalesForeCast, filter);
	return result;
}

public String getCustomTimePeriodParentYear(String customTimePeriodId) {
	String result = "";
	GenericValue thisPeriod = delegator.findOne("CustomTimePeriod", ["customTimePeriodId" : customTimePeriodId], false);
	if (thisPeriod) {
		if (thisPeriod.getString("periodTypeId") == "SALES_YEAR") {
			return customTimePeriodId;
		} else if (thisPeriod.getString("parentPeriodId")) {
			result = getCustomTimePeriodParentYear(thisPeriod.getString("parentPeriodId"));
		}
	} else {
		result = customTimePeriodId;
	}
	return result;
}

public String getCustomTimePeriodParentNameYear(String customTimePeriodId) {
	String result = "";
	GenericValue thisPeriod = delegator.findOne("CustomTimePeriod", ["customTimePeriodId" : customTimePeriodId], false);
	if (thisPeriod) {
		if (thisPeriod.getString("periodTypeId") == "SALES_YEAR") {
			return thisPeriod.getString("periodName");
		} else if (thisPeriod.getString("parentPeriodId")) {
			result = getCustomTimePeriodParentNameYear(thisPeriod.getString("parentPeriodId"));
		}
	} else {
		result = "_NA_";
	}
	return result;
}

customTimePeriodId = parameters.customTimePeriodId;

List<GenericValue> listSalesForeCast = null;
List<String> listPeriodThisAndChildren = new ArrayList<Map>();
String organizationPartyId = "company";
String currencyUomId = UtilProperties.getPropertyValue("general.properties", "currency.uom.id.default");
if (customTimePeriodId) {
	customTimePeriodId = getCustomTimePeriodParentYear(customTimePeriodId);
	
	listPeriodChildren = getPeriodChildren(customTimePeriodId);
	if (listPeriodChildren != null) {
		listPeriodThisAndChildren.addAll(listPeriodChildren);
	}
	listPeriodThisAndChildren.add(customTimePeriodId);
	context.listPeriodThisAndChildren = listPeriodThisAndChildren;
	
	// get list salesForecast and it's children
	osisConds = [];
	osisConds.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.IN, listPeriodThisAndChildren));
	osisConds.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId));
	osisCond = EntityCondition.makeCondition(osisConds, EntityOperator.AND);
	listSalesForeCast = delegator.findList("SalesForecast", osisCond, null, null, null, false);
}

List<String> internalPartyIds = new ArrayList<String>();
List<Map> listSalesForecastAndItems = new ArrayList<Map>();
List<Map> listProduct = new ArrayList<Map>();
// get list product from all category
categoryOrder = ["productCategoryId"];
categoryFields = ["productCategoryId"] as Set;
categoryFindOptions = new EntityFindOptions();
categoryFindOptions.setDistinct(true);
listCategory = delegator.findList("ProductAndCategoryMember", null, categoryFields, categoryOrder, categoryFindOptions, false);
for (cateItem in listCategory) {
	productCond = EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, cateItem.productCategoryId);
	productOrder = ["productId"];
	productFields = ["productId", "internalName"] as Set;
	productFindOptions = new EntityFindOptions();
	productFindOptions.setDistinct(true);
	listProductIn = delegator.findList("ProductAndCategoryMember", productCond, productFields, productOrder, productFindOptions, false);
	cateMap = [:];
	cateMap.categoryId = cateItem.productCategoryId;
	cateMap.productList = listProductIn;
	listProduct.add(cateMap);
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
		List<Map> listInnerSalesForecastAndItems = new ArrayList<Map>();
		for (customTimeItem in listPeriodThisAndChildren) {
			mapResult = getForecastHeader(itemParty, customTimeItem, listSalesForeCast, listProduct);
			listInnerSalesForecastAndItems.add(mapResult);
		}
		mapTemp = [:];
		mapTemp.internalPartyIds = itemParty ?: "";
		mapTemp.organizationPartyId = organizationPartyId ?: "";
		mapTemp.forecastAndItems = listInnerSalesForecastAndItems ?: "";
		listSalesForecastAndItems.add(mapTemp);
	}
}
context.customTimePeriodId = customTimePeriodId;
context.currencyUomId = currencyUomId;
context.listProduct = listProduct;
context.listSalesForecastAndItems = listSalesForecastAndItems;
