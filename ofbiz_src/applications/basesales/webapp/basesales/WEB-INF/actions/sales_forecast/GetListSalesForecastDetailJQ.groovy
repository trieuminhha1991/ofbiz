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
import com.olbius.basesales.forecast.SalesForecastWorker;

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
			listPeriodThisAndChildren.addAll(SalesForecastWorker.getPeriodChildrenFull(customTimePeriodId, delegator));
			listPeriodThisAndChildren.add(customTimePeriod);
			context.listPeriodThisAndChildren = listPeriodThisAndChildren;
		}
		List<GenericValue> listSalesForeCast = SalesForecastWorker.getDescendantSalesForecast(delegator, salesForecast.getString("salesForecastId"));
		
		List<String> internalPartyIds = new ArrayList<String>();
		List<Map<String, Object>> sfTabsContent = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listProduct = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> localData = new ArrayList<Map<String, Object>>();
		
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
