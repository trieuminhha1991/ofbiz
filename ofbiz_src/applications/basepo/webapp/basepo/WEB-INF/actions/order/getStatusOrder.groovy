import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

String orderId = parameters.orderId;
GenericValue order = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);

GenericValue status = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", order.getString("statusId")), false);
//String statusDescription = (String) status.get("description", locale);
String statusDescription = (String)status.get("description", locale);
context.statusDescription = statusDescription;
context.statusId = order.getString("statusId");
List<GenericValue> soAssoc = delegator.findList("OrderItemAssoc", EntityCondition.makeCondition(UtilMisc.toMap("toOrderId", orderId)), null, null, null, false);
if(UtilValidate.isNotEmpty(soAssoc)){
	context.soAssoc = soAssoc;
}
