import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastList;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;


List<GenericValue> listDitributor = delegator.findList("PartyDistributor", EntityCondition.makeCondition("supervisorId", userLogin.getString("userLoginId")), null, null, null, false);
if(UtilValidate.isNotEmpty(listDitributor)) {
    List<String> owners = EntityUtil.getFieldListFromEntityList(listDitributor, "partyId", false);
    List<EntityCondition> conditions = FastList.newInstance();
    conditions.add(EntityCondition.makeCondition("ownerPartyId", EntityJoinOperator.IN, owners))
    def dummy = delegator.findList("Facility", EntityCondition.makeCondition(conditions),
            null, null, null, false);
    List<String> facilities = EntityUtil.getFieldListFromEntityList(dummy, "facilityId", false);
    System.out.println("facilities" + facilities)
    context.facilities = facilities
    def facilityJson = "[";
    flag = false;
    for (value in dummy) {
        if (flag) {
            facilityJson += ",";
        }
        facilityJson += "{facilityId:"  + value.get("facilityId") + "}"
        flag = true;
    }
    facilityJson += "]"
    context.facilityJson = facilityJson

}
