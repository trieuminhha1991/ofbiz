/*
 * SalesForecastAndCustomTimePeriod
 * Get list IMPORT PLAN by custom time period (IMPORT_YEAR)
 * Get list all product category, products in category in system.
 * Get list custom time period is children of custom time period was selected.
 */

import java.math.BigDecimal;
import java.util.*;
import java.security.Policy.Parameters;
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
import com.lowagie.text.pdf.PRAcroForm;

/**
 * Get period children of a Period parameter.
 * @param customTimePeriodId Period want get children
 * @return List Strings of customTimePeriodId
 */
public List<String> getPeriodChildren(String customTimePeriodId) {
	List<String> result = new ArrayList<String>();
	periodCond = EntityCondition.makeCondition("parentPeriodId", EntityOperator.EQUALS, customTimePeriodId);
	periodOrder = ["customTimePeriodId"];
	periodFields = ["customTimePeriodId"] as Set; //, "periodName", "periodTypeId", "parentPeriodId"
//	periodFindOptions = new EntityFindOptions();
//	periodFindOptions.setDistinct(true);
	periodChildren = delegator.findList("CustomTimePeriod", periodCond, periodFields, periodOrder, null, false);
	for (child in periodChildren) {
		ppItems = getPeriodChildren(child.customTimePeriodId);
		result.addAll(ppItems);
		result.add(child.customTimePeriodId);
	}
	return result;
}

// chua su dung bang product plan and sales forecast de tim ra 1 plan thuoc 1 sales forecast nao sau do la tim cacs internalpartyid
// tam thoi su dung bang product plan de lay internal party id

customTimePeriodId = parameters.customTimePeriodId
List<String> listPeriodTimeChild = getPeriodChildren(customTimePeriodId);

partyConds = [];
partyConds.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.IN, listPeriodTimeChild));
partyConds.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, "company"));
osisCond = EntityCondition.makeCondition(partyConds, EntityOperator.AND);
List<GenericValue> listPlans = delegator.findList("ProductPlanHeader", osisCond, null, null, null, false);
List<String> internalPartyIds = new ArrayList<String>();
List<Map> listProductPlans = new ArrayList<Map>();
Map planInternalPartyId = [:];
String checkInternalPartyId = "";
for(listPlan in listPlans){
	if(!checkInternalPartyId.equals(listPlan.internalPartyId)){
		internalPartyIds.add(listPlan.internalPartyId);
		checkInternalPartyId = listPlan.internalPartyId;
	}
}
//System.out.println("abc:"+internalPartyIds);
List<String> productPlans = new ArrayList<String>();
List<Map> resultProItem = new ArrayList<Map>();
for(internal in internalPartyIds){
	Map<String, Object> filterByInternal = UtilMisc.toMap("internalPartyId",internal);
	List<GenericValue> listProductPlanPartyIds = EntityUtil.filterByAnd(listPlans, filterByInternal);
	
	for(listProduct in listProductPlanPartyIds){
		productPlans.add(listProduct.productPlanId);
		orderField = ["productId"];
		conditionItem = EntityCondition.makeCondition("productPlanId", EntityOperator.EQUALS, listProduct.productPlanId);
		fieldItems = ["productPlanId","productPlanItemSeqId","productId","internalName","productCategoryId","recentPlanQuantity","planQuantity","quantityUomId","statusId"] as Set;
		productPlanItems = delegator.findList("ProductAndProductPlanItem", conditionItem, fieldItems, orderField, null, false);
		mapProductItems = [:];
		mapProductItems.prPlanIds = listProduct.productPlanId;
		mapProductItems.aMcc = productPlanItems;
//		mapProductItems.internalPartyIds = 
		resultProItem.addAll(mapProductItems);
	}
	planInternalPartyId.internalPartyId = internal;
	planInternalPartyId.item = resultProItem;
	listProductPlans.addAll(planInternalPartyId);
}

	List<Map> product = listProductPlans.get(0).item;
//	System.out.println("mm:" +listProductPlans);
context.listProduct = product.get(0).aMcc;
context.listProductPlans = listProductPlans;
