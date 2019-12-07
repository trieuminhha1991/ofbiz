import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityJoinOperator;
import javolution.util.FastList;

List<GenericValue> partyList = FastList.newInstance();

String organizationPartyId = userLogin.lastOrg;

List<EntityCondition> conditions = FastList.newInstance();
List<String> listPartyRole = new ArrayList();
listPartyRole.add("CUSTOMER");
listPartyRole.add("EMPLOYEE");
listPartyRole.add("SUPPLIER");
conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, organizationPartyId));
conditions.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, listPartyRole));
partyList = delegator.findList("PartyRelationshipAndDetail", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
context.partyList = partyList;
