import org.ofbiz.entity.condition.EntityCondition;import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

List<EntityCondition> conds = new ArrayList<EntityCondition>();
conds.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.partyId));
conds.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CUSTOMER"));
EntityFindOptions findOpts = new EntityFindOptions();
findOpts.setDistinct(true);

List<String> listPromoId = new ArrayList<String>();
List<GenericValue> listPromoAppl = new ArrayList<GenericValue>();
List<GenericValue> listProductStore = EntityUtil.filterByDate(delegator.findList("ProductStoreRoleDetail", EntityCondition.makeCondition(conds, EntityOperator.AND), null, null, findOpts, false));
if (listProductStore) {
	for (storeItem in listProductStore) {
		//List<GenericValue> storePromoApplTemp = EntityUtil.filterByDate(delegator.findByAnd("ProductStorePromoApplFilterLoose", ["productStoreId" : storeItem.productStoreId], null, false));
		List<GenericValue> storePromoApplTemp = delegator.findByAnd("ProductStorePromoApplFilterLoose", ["productStoreId" : storeItem.productStoreId], null, false);
		if (storePromoApplTemp) {
			listPromoAppl.addAll(storePromoApplTemp);
		}
	}
	
	for (promoItem in listPromoAppl) {
		if (!listPromoId.contains(promoItem.productPromoId)) {
			List<GenericValue> productPromoCondSe = delegator.findByAnd("ProductPromoCond", ["inputParamEnumId" : "PPIP_ROLE_TYPE", "operatorEnumId" : "PPC_EQ", "productPromoId" : promoItem.productPromoId], null, false);
			String roleTypeTemp = "";
			boolean isAdd = false;
			if (productPromoCondSe) {
				GenericValue firstProductPromoCond = EntityUtil.getFirst(productPromoCondSe);
				roleTypeTemp = firstProductPromoCond.condValue;
			}
			if (roleTypeTemp != null && "".equals(roleTypeTemp)) {
				List<GenericValue> partyRoleSe = delegator.findByAnd("PartyRole", ["partyId" : userLogin.partyId, "roleTypeId" : roleTypeTemp], null, false);
				if (partyRoleSe != null && partyRoleSe.size() > 0) {
					isAdd = true;
				}
			}
			if (!isAdd) {
				List<GenericValue> productPromoRoleTypeApplySe = delegator.findByAnd("ProductPromoRoleTypeAppl", ["productPromoId" : promoItem.productPromoId], null, false);
				roleTypeTemp = "";
				for (pprtaItem in productPromoRoleTypeApplySe) {
					roleTypeTemp = pprtaItem.roleTypeId;
					if (roleTypeTemp != null && !"".equals(roleTypeTemp)) {
						List<GenericValue> partyRoleSe2 = delegator.findByAnd("PartyRole", ["partyId" : userLogin.partyId, "roleTypeId" : roleTypeTemp], null, false);
						if (partyRoleSe2 != null && partyRoleSe2.size() > 0) {
							isAdd = true;
							break;
						}
					}
				}
			}
			if (isAdd) {
				listPromoId.add(promoItem.productPromoId);
			}
		}
	}
}
if (listPromoId.size() <= 0) {
	listPromoId.add("");
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