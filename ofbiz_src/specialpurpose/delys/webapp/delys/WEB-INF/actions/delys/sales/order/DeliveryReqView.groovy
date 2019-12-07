import java.util.List;

import com.olbius.order.DeliveryRequirementItem;
import com.olbius.order.OrderReadHelper;

deliveryReqId = parameters.requirementId;
context.deliveryReqId = deliveryReqId;
context.viewPageId = "VIEW_DELIVERY_REQ";

deliveryReq = null;
if (deliveryReqId) {
	deliveryReq = delegator.findOne("Requirement", ["requirementId" : deliveryReqId], false);
	context.deliveryReq = deliveryReq;
}
List<GenericValue> deliveryReqItems = new ArrayList<GenericValue>();
deliveryCartSize = null;
if (deliveryReq) {
	//find list requirement - order
	deliveryReqItems = delegator.findByAnd("OrderRequirementDelivery", ["requirementId" : deliveryReqId], ["orderId"], false);
	context.deliveryReqItems = deliveryReqItems;
	deliveryCartSize = deliveryReqItems.size();
}
context.deliveryCartSize = deliveryCartSize;
List<DeliveryRequirementItem> listItemsGet = new ArrayList<DeliveryRequirementItem>();
for (deliveryReqItem in deliveryReqItems) {
	DeliveryRequirementItem item = new DeliveryRequirementItem(deliveryReqItem.requirementId, deliveryReqItem.orderId, deliveryReqItem.description);
	if (item) {
		listItemsGet.add(item);
	}
}

List<GenericValue> listAllItems = new ArrayList<GenericValue>();
listItemByOrder = [];
cartLines = listItemsGet;
for (cartLine in cartLines) {
	orderHeader = null;
	orderId = cartLine.getOrderId();
	if (orderId) {
		orderHeader = delegator.findOne("OrderHeader", [orderId : orderId], false);
	}
	if (orderHeader) {
		orderReadHelper = new OrderReadHelper(orderHeader);
		orderItems = orderReadHelper.getOrderItemsDetails();
		if (orderItems) {
			listAllItems.addAll(orderItems);
			itemmap = [:];
			itemmap.orderId = orderHeader.orderId;
			itemmap.listValue = orderItems;
			listItemByOrder.add(itemmap);
		}
	}
}
context.listAllItems = listAllItems;
context.listItemByOrder = listItemByOrder;

//Sort by Facility
List<String> listFacility = new ArrayList<String>();
for (item in listAllItems) {
	if ((item.facilityId) && !listFacility.contains(item.facilityId)) {
		listFacility.add(item.facilityId);
	}
}

listItemByFacility = [];
//get item by each facility
for (itemFacility in listFacility) {
	itemmap = [:];
	List<GenericValue> listItem = new ArrayList<GenericValue>();
	for (item in listAllItems) {
		if (item.facilityId && item.facilityId == itemFacility) {
			listItem.add(item);
		}
	}
	itemmap.facilityId = itemFacility;
	itemmap.listValue = listItem;
	listItemByFacility.add(itemmap);
}

itemmapNull = [:];
List<GenericValue> listItemNull = new ArrayList<GenericValue>();
for (item in listAllItems) {
	if (item.facilityId == "" || item.facilityId == null) {
		listItemNull.add(item);
	}
}
itemmapNull.facilityId = "";
itemmapNull.listValue = listItemNull;
listItemByFacility.add(itemmapNull);

context.listItemByFacility = listItemByFacility;

