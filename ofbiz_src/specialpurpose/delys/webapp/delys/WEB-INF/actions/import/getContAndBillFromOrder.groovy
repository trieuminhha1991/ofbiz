import java.util.List;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
	
String containerId = null;
String billId = null;
GenericValue container = null;
GenericValue bill = null;
if (orderId != null){
    List<GenericValue> listOrderConts = delegator.findList("OrderAndContainer", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
	listOrderConts = EntityUtil.filterByDate(listOrderConts);
	if (!listOrderConts.isEmpty()){
		containerId = (String)listOrderConts.get(0).get("containerId");
		if (containerId != null){
			container = delegator.findOne("Container", false, UtilMisc.toMap("containerId", containerId));
		}
		billId = (String)listOrderConts.get(0).get("billId");
		if (billId != null){
			bill = delegator.findOne("BillOfLading", false, UtilMisc.toMap("billId", billId));
		}
	}
}
context.container = container;
context.bill = bill;
