import org.ofbiz.entity.condition.EntityCondition;import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

List<EntityCondition> conds = new ArrayList<EntityCondition>();
conds.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.partyId));
conds.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "OWNER"));
EntityFindOptions findOpts = new EntityFindOptions();
findOpts.setDistinct(true);

List<String> listPromoId = new ArrayList<String>();
List<GenericValue> listPromoAppl = new ArrayList<GenericValue>();
List<GenericValue> listProductStore = EntityUtil.filterByDate(delegator.findList("ProductStoreRoleDetail", EntityCondition.makeCondition(conds, EntityOperator.AND), null, null, findOpts, false));
if (listProductStore) {
	for (storeItem in listProductStore) {
		List<GenericValue> storePromoApplTemp0 = delegator.findByAnd("ProductStorePromoAppl", ["productStoreId" : storeItem.productStoreId], null, false);
		List<GenericValue> storePromoApplTemp = EntityUtil.filterByDate(delegator.findByAnd("ProductStorePromoAppl", ["productStoreId" : storeItem.productStoreId], null, false));
		if (storePromoApplTemp) {
			listPromoAppl.addAll(storePromoApplTemp);
		}
	}
	for (promoItem in listPromoAppl) {
		if (!listPromoId.contains(promoItem.productPromoId)) {
			listPromoId.add(promoItem.productPromoId);
		}
	}
}
if (parameters.productPromoId) {
	if (listPromoId.contains(parameters.productPromoId)) {
		listPromoId.clear();
		listPromoId.add(parameters.productPromoId);
	} else {
		listPromoId.clear();
		listPromoId.add("");
	}
}
context.listPromoId = listPromoId;