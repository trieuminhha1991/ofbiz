//SalesForecastAndCustomTimePeriod

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

public List<Map<String, String>> getPeriodParents(String customTimePeriodId) {
	List<Map<String, String>> result = new ArrayList<Map<String, String>>();
	posisCond = EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId);
	posisOrder = ["periodName"];
	posisFields = ["customTimePeriodId", "periodName", "periodTypeId", "parentPeriodId"] as Set;
	posisFindOptions = new EntityFindOptions();
	posisFindOptions.setDistinct(true);
	parentItems = delegator.findList("CustomTimePeriod", posisCond, posisFields, posisOrder, posisFindOptions, false);
	for (parentItem in parentItems) {
		if (parentItem.parentPeriodId != null) {
			ppItems = getPeriodParents(parentItem.parentPeriodId);
			result.addAll(ppItems);
		} else {
			result.add(parentItem);
		}
	}
	return result;
}

osisCond = EntityCondition.makeCondition("customTimePeriodId", EntityOperator.NOT_EQUAL, null);  
osisOrder = ["periodName"];
osisFields = ["customTimePeriodId", "periodName", "periodTypeId", "parentPeriodId"] as Set;
osisFindOptions = new EntityFindOptions();
osisFindOptions.setDistinct(true);
listCustomTimePeriod = delegator.findList("SalesForecastAndCustomTimePeriod", osisCond, osisFields, osisOrder, osisFindOptions, false);

//[[periodTypeId:SALES_MONTH, customTimePeriodId:10060, periodName:Thang 1]]
List<Map<String, String>> listCustomTimePeriodAndParent = new ArrayList<Map<String, String>>();
for (item in listCustomTimePeriod) {
	listCustomTimePeriodAndParent.add(item);
	if (item.parentPeriodId != null) {
		listGet = getPeriodParents(item.parentPeriodId);
		if (listGet != null) {
			for (item3 in listGet) {
				boolean flag = false;
				for (item2 in listCustomTimePeriodAndParent) {
					if (item3.customTimePeriodId == item2.customTimePeriodId) {
						flag = true; break;
					}
				}
				if (!flag) {
					listCustomTimePeriodAndParent.add(item3);
				}
			}
		}
	}
}

// filter the issuances
Map<String, Object> filter = UtilMisc.toMap("periodTypeId", "SALES_YEAR");
listCustomTimePeriodByForecast = EntityUtil.filterByAnd(listCustomTimePeriodAndParent, filter);
context.listCustomTimePeriodByForecast = listCustomTimePeriodByForecast;