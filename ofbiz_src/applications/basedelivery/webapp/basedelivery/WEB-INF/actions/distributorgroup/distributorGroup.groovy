import javolution.util.FastList
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
/**
 * Created by user on 11/29/17.
 */

productStoreGroupId = parameters.productStoreGroupId
if(productStoreGroupId) {
    GenericValue productStoreGroup = delegator.findOne("ProductStoreGroup", UtilMisc.toMap("productStoreGroupId", productStoreGroupId), false)
    List<GenericValue> productStores = delegator.findList("ProductStore", EntityCondition.makeCondition("primaryStoreGroupId", productStoreGroupId), null, null, null, false);
    List<String> partyIds = new ArrayList<>()
    for(GenericValue productStore : productStores) {
        String payToPartyId = productStore.get("payToPartyId")
        partyIds.add(payToPartyId)
    }

    List<EntityCondition> listAllConditions = FastList.newInstance()
    listAllConditions.add(EntityCondition.makeCondition("roleTypeId", "DISTRIBUTOR"))
    listAllConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIds))
    List<GenericValue> listDistributor = delegator.findList("PartyRoleNameDetail", EntityCondition.makeCondition(listAllConditions), null, null, null, false)
    context.productStoreGroup = productStoreGroup
    context.productStoreGroupId = productStoreGroupId
    context.listDistributor = listDistributor
}