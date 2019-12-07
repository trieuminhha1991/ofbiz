import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

psCond = EntityCondition.makeCondition([EntityCondition.makeCondition("productId", EntityOperator.EQUALS, parameters.productId),
                                         EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.partyId)], EntityOperator.AND);
psList = EntityUtil.filterByDate(delegator.findList("ProductSellerFriend", psCond, null, null, null, true));
if(psList.isEmpty()){
	context.productSellerFriend = null;
}else{
	context.productSellerFriend = psList.get(0);
}