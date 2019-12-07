/*
 * SalesForecastAndCustomTimePeriod
 * Get list sales forecast by custom time period (SALES_YEAR)
 * Get list all product category, products in category in system.
 * Get list custom time period is children of custom time period was selected.
 */

import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;

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
	if (getListForecastFilteredByParty != null) {
		if (getListForecastFiltered != null && getListForecastFiltered.size() > 0) {
			forecastFirst = EntityUtil.getFirst(getListForecastFiltered);
			listForecastItem = delegator.findByAnd("SalesForecastDetail", ["salesForecastId" : forecastFirst.salesForecastId], null, false);
			mapTemp.forecast = forecastFirst ?: "";
			mapTemp.forecastItems = listForecastItem ?: "";
		} else {
			Map<String, Object> newForecastMap = UtilMisc.toMap("internalPartyId", partyId, "customTimePeriodId", customTimePeriodId);
			List<GenericValue> getListForecastOk2 = new ArrayList<GenericValue>();
			List<String> listPeriodChild = getPeriodChildren(customTimePeriodId);
			for (periodChild in listPeriodChild) {
				getListForecastOk2.addAll(getForecastHeaderInner(partyId, periodChild, listSalesForeCast));
			}
			//get list sales forecast details
			List<GenericValue> getListForecastDetailOk2 = new ArrayList<GenericValue>();
			List<Map> getListForecastDetailOk3 = new ArrayList<Map>();
			for (item in getListForecastOk2) {
				listForecastItem = delegator.findByAnd("SalesForecastDetail", ["salesForecastId" : item.salesForecastId], null, false);
				getListForecastDetailOk2.addAll(listForecastItem);
			}
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
			mapTemp.forecast = newForecastMap ?: "";
			mapTemp.forecastItems = getListForecastDetailOk3 ?: "";
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

customTimePeriodId = parameters.customTimePeriodId;

List<GenericValue> listSalesForeCast = null;
List<String> listPeriodThisAndChildren = new ArrayList<Map>();
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
	osisConds.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, "company"));
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
		mapTemp.forecastAndItems = listInnerSalesForecastAndItems ?: "";
		listSalesForecastAndItems.add(mapTemp);
	}
}
context.customTimePeriodId = customTimePeriodId;
context.listProduct = listProduct;
context.listSalesForecastAndItems = listSalesForecastAndItems;
