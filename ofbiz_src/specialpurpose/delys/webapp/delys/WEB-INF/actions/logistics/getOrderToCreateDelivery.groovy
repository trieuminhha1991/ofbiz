import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.util.EntityUtil;

userLogin = session.getAttribute("userLogin");
partyId = userLogin.get('partyId');
context.partyId = partyId;

java.util.Date date= new java.util.Date();
taxPercentage = 0;
listOrders = listOrders;
listTmp = [] as ArrayList;
if (!listOrders.isEmpty()){
	for (GenericValue order : listOrders){
		List<GenericValue> listOrderByDelivery = delegator.findList("Delivery", EntityCondition.makeCondition(UtilMisc.toMap("orderId", order.get("orderId"))), null, null, null, false);
		if (!listOrderByDelivery.isEmpty()){
			listTmp.add(order);
		}
	}
}
if (!listTmp.isEmpty()){
	listOrders.removeAll(listTmp);
}
List<String> listOrderItemTypes = new ArrayList<GenericValue>();
String orderId = (String)context.get("orderId");
if (orderId != null){
	List<GenericValue> listOrderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
	if (!listOrderItems.isEmpty()){
		for (GenericValue item : listOrderItems){
			if (!listOrderItemTypes.contains((String)item.get("orderItemTypeId"))){
				listOrderItemTypes.add((String)item.get("orderItemTypeId"));
			}
		}
	}
}
context.listOrderItemTypes = listOrderItemTypes;
context.listOrders = listOrders;
