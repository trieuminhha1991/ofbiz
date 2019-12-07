import java.util.ArrayList;
import java.util.List;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.UtilMisc;

orderId = orderHeaderTemp.orderId;
listOrderRole = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId), null, false);
List<EntityCondition> listCond = new ArrayList<EntityCondition>();
for(int i = 0; i < listOrderRole.size(); i++){
    listCond.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, listOrderRole.get(i).get("partyId")));
}
context.listOParty = delegator.findList("PartyNameView", EntityCondition.makeCondition(listCond, EntityOperator.OR), null, null, null, false);