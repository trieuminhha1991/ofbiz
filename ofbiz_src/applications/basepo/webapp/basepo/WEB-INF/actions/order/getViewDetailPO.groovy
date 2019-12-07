import org.ofbiz.entity.GenericValue;
import com.olbius.basehr.util.MultiOrganizationUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
List<GenericValue> listOrderItemShipGroup = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
Timestamp shipByDate = null;
Timestamp shipAfterDate = null;
for (GenericValue orderItemShipGroup : listOrderItemShipGroup) {
	shipByDate = orderItemShipGroup.getTimestamp("shipByDate");
	shipAfterDate = orderItemShipGroup.getTimestamp("shipAfterDate");
}

GenericValue orderHeader2 = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);


GenericValue status = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", orderHeader2.getString("statusId")), false);
String statusDescription = (String)status.get("description", locale);
context.statusDescription = statusDescription;
context.statusId = orderHeader2.getString("statusId");
List<GenericValue> soAssoc = delegator.findList("OrderItemAssoc", EntityCondition.makeCondition(UtilMisc.toMap("toOrderId", orderId)), null, null, null, false);
String isFavorDelivery = null;
String statusSOId = null;
if(UtilValidate.isNotEmpty(soAssoc)){
	context.soAssoc = soAssoc;
	GenericValue orderItemAssoc = soAssoc.get(0);
	GenericValue orderSO = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderItemAssoc.getString("orderId")), false);
	isFavorDelivery = orderSO.getString("isFavorDelivery");
	statusSOId = orderSO.getString("statusId");
}

context.statusSOId = statusSOId;
context.isFavorDelivery = isFavorDelivery;
context.shipByDate = shipByDate;
context.shipAfterDate = shipAfterDate;
if(shipByDate != null){
	context.shipByDateLong = shipByDate.getTime();	
}

if(shipAfterDate != null){
	context.shipAfterDateLong = shipAfterDate.getTime();
}
