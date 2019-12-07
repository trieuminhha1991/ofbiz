import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.product.inventory.InventoryWorker;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.accounting.payment.*;
import com.olbius.order.OrderReadHelper;

import javolution.util.FastMap;

orderReadHelper = new OrderReadHelper(orderHeader);
orderItems = orderReadHelper.getOrderItems();
orderAdjustments = orderReadHelper.getAdjustments();
context.orderAdjustments = orderAdjustments;
context.orderReadHelper = orderReadHelper;

// get inventory summary for each shopping cart product item
inventorySummary = dispatcher.runSync("getProductInventorySummaryForItems", [orderItems : orderItems]);
context.availableToPromiseMap = inventorySummary.availableToPromiseMap;
context.quantityOnHandMap = inventorySummary.quantityOnHandMap;
context.mktgPkgATPMap = inventorySummary.mktgPkgATPMap;
context.mktgPkgQOHMap = inventorySummary.mktgPkgQOHMap;

// INVENTORY: construct a Set of productIds in the order for use in querying for inventory, otherwise these queries can get expensive
productIds = orderReadHelper.getOrderProductIds();

// INVENTORY: get the production quantity for each product and store the results in a map of productId -> quantity
productionMap = [:];
productIds.each { productId ->
	if (productId) {  // avoid order items without productIds, such as bulk order items
		contextInput = [productId : productId, userLogin : userLogin];
		resultOutput = dispatcher.runSync("getProductManufacturingSummaryByFacility", contextInput);
		manufacturingInQuantitySummaryByFacility = resultOutput.summaryInByFacility;
		Double productionQuantity = 0;
		manufacturingInQuantitySummaryByFacility.values().each { manQuantity ->
			productionQuantity += manQuantity.estimatedQuantityTotal;
		}
		productionMap[productId] = productionQuantity;
	}
}
context.productionProductQuantityMap = productionMap;

if (productIds.size() > 0) {
	// INVENTORY: find the number of products in outstanding sales orders for the same product store
	requiredMap = InventoryWorker.getOutstandingProductQuantitiesForSalesOrders(productIds, delegator);
	context.requiredProductQuantityMap = requiredMap;

	// INVENTORY: find the quantity of each product in outstanding purchase orders
	onOrderMap = InventoryWorker.getOutstandingProductQuantitiesForPurchaseOrders(productIds, delegator);
	context.onOrderProductQuantityMap = onOrderMap;
} else {
	context.requiredProductQuantityMap = FastMap.newInstance();
	context.onOrderProductQuantityMap = FastMap.newInstance();
}

// get a list of all shipments, and a list of ItemIssuances per order item
allShipmentsMap = [:];
primaryShipments = orderHeader.getRelated("PrimaryShipment", null, null, false);
primaryShipments.each { primaryShipment ->
	allShipmentsMap[primaryShipment.shipmentId] = primaryShipment;
}
itemIssuancesPerItem = [:];
itemIssuances = orderHeader.getRelated("ItemIssuance", null, ["shipmentId", "shipmentItemSeqId"], false);
itemIssuances.each { itemIssuance ->
	if (!allShipmentsMap.containsKey(itemIssuance.shipmentId)) {
		iiShipment = itemIssuance.getRelatedOne("Shipment", false);
		if (iiShipment) {
			allShipmentsMap[iiShipment.shipmentId] = iiShipment;
		}
	}

	perItemList = itemIssuancesPerItem[itemIssuance.orderItemSeqId];
	if (!perItemList) {
		perItemList = [];
		itemIssuancesPerItem[itemIssuance.orderItemSeqId] = perItemList;
	}
	perItemList.add(itemIssuance);
}
context.allShipments = allShipmentsMap.values();
context.itemIssuancesPerItem = itemIssuancesPerItem;

