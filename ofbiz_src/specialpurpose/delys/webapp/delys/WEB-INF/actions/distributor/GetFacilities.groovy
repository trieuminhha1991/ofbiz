import org.ofbiz.entity.condition.EntityCondition;import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

List<EntityCondition> conds = new ArrayList<EntityCondition>();
conds.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.partyId));
conds.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "OWNER"));
EntityFindOptions findOpts = new EntityFindOptions();
findOpts.setDistinct(true);

List<GenericValue> listFacility = new ArrayList<GenericValue>();
List<GenericValue> listStoreFacility = new ArrayList<GenericValue>();
List<GenericValue> listProductStore = EntityUtil.filterByDate(delegator.findList("ProductStoreRoleDetail", EntityCondition.makeCondition(conds, EntityOperator.AND), null, null, findOpts, false));
if (listProductStore) {
	for (storeItem in listProductStore) {
		List<GenericValue> storeFacilityTemp = EntityUtil.filterByDate(delegator.findByAnd("ProductStoreFacility", ["productStoreId" : storeItem.productStoreId], ["sequenceNum", "fromDate"], false));
		if (storeFacilityTemp) {
			listStoreFacility.addAll(storeFacilityTemp);
		}
	}
	for (storeFacilityItem in listStoreFacility) {
		GenericValue facilityTemp = delegator.findOne("Facility", ["facilityId" : storeFacilityItem.facilityId], false);
		if (facilityTemp) {
			listFacility.addAll(facilityTemp);
		}
	}
}
context.listFacility = listFacility;