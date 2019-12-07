import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;


//<entity-condition list="listInventoryItem" entity-name="InventoryItem">
//<condition-expr field-name="facilityId" operator="in" from-field="listFacilityId"/>
//</entity-condition>

//conditionList = [];
//condition = EntityCondition.makeCondition("facilityId", EntityOperator.IN, context.listFacilityId);
//findOptions = new EntityFindOptions();
//findOptions.setDistinct(true);
//selectList = ["productId"] as Set;
//List<GenericValue> listProduct = delegator.findList("InventoryItem", condition, selectList, ["productId"], findOptions, false);
List<Map> resultMap = new ArrayList<Map>();

listFacilityId = context.listFacilityId;
for (facilityIdItem in listFacilityId) {
	resultItem = [:];
	//find list inventoryItem in facility, not distinct, select all
	condition = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityIdItem);
	List<GenericValue> listInventoryItem = delegator.findList("InventoryItem", condition, null, ["productId"], null, false);
	List<Map> listProductIdDate = new ArrayList<Map>();
	for (inventoryItem in listInventoryItem) {
		productMapTemp = [:];
		productMapTemp.productId = inventoryItem.productId ?: "";
		productMapTemp.expireDate = inventoryItem.expireDate ?: "";
		if (!listProductIdDate.contains(productMapTemp)) {
			listProductIdDate.add(productMapTemp);
		}
	}
	resultItem.facilityId = facilityIdItem;
	//browse listProductIdDate, get list inventory item same product and expireDate other and sum QOH, ATP
	List<Map> resultMap2 = new ArrayList<Map>();
	for (productIdDateItem in listProductIdDate) {
		resultItem2 = [:];
		BigDecimal quantityOnOrder = BigDecimal.ZERO;
		BigDecimal quantityOnHandTotal = BigDecimal.ZERO;
		BigDecimal availableToPromiseTotal = BigDecimal.ZERO;
		BigDecimal accountingQuantityTotal = BigDecimal.ZERO;
		Map listInventoryItemByProduct = [:];
		List<GenericValue> listBrowsed = new ArrayList<GenericValue>();
		for (inventoryItem in listInventoryItem) {
			productMapTemp = [:];
			productMapTemp.productId = inventoryItem.productId ?: "";
			productMapTemp.expireDate = inventoryItem.expireDate ?: "";
			if (productIdDateItem == productMapTemp) {
				if (inventoryItem.getBigDecimal("quantityOnHandTotal")){
					quantityOnHandTotal = quantityOnHandTotal.add(inventoryItem.getBigDecimal("quantityOnHandTotal"));
				}
				if (inventoryItem.getBigDecimal("availableToPromiseTotal")){
					availableToPromiseTotal = availableToPromiseTotal.add(inventoryItem.getBigDecimal("availableToPromiseTotal"));
				}
				if (inventoryItem.getBigDecimal("accountingQuantityTotal")){
					accountingQuantityTotal = accountingQuantityTotal.add(inventoryItem.getBigDecimal("accountingQuantityTotal"));
				}
				listBrowsed.add(inventoryItem);
			}
		}
		quantityOnOrder = quantityOnHandTotal.subtract(availableToPromiseTotal);
		listInventoryItemByProduct = ["QOH" : quantityOnHandTotal, "ATP" : availableToPromiseTotal, "AQT" : accountingQuantityTotal, "QOO" : quantityOnOrder];
		//delete item browsed
		for (deleteItem in listBrowsed) {
			listInventoryItem.remove(deleteItem);
		}
		int count = 1;
		for (productIdDateItemInner in listProductIdDate) {
			if ((productIdDateItem.productId == productIdDateItemInner.productId) && (productIdDateItem.expireDate != productIdDateItemInner.expireDate)) {
				count = count + 1;
			}
		}
		resultItem2.productIdDateMap = productIdDateItem;
		resultItem2.listInventoryItemByProductSize = count;
		resultItem2.listInventoryItemByProduct = listInventoryItemByProduct;
		resultMap2.add(resultItem2);
	}
	resultItem.productIdDateMapSize = resultMap2.size();
	resultItem.productIdDateMapValue = resultMap2;
	resultMap.add(resultItem);
}
context.resultMap = resultMap;
