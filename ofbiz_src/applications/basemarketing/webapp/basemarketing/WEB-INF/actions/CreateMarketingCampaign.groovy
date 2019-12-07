import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import com.olbius.basehr.util.PartyHelper;
import com.olbius.basehr.util.SecurityUtil;
import javolution.util.FastList;
import javolution.util.FastMap;

List<String> listPartyFromId = SecurityUtil.getPartiesByRoles("EMPLOYEE", delegator);
List<GenericValue> listPerson = delegator.findList("Person", EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, listPartyFromId), null, null, null, false);

def listEmployee = FastList.newInstance();
for (GenericValue x : listPerson) {
	Map<String, Object> mapEmployee = FastMap.newInstance();
	String partyId = x.getString("partyId");
	def partyFullName = PartyHelper.getPartyName(delegator, partyId, true, true);
	mapEmployee.put("partyId", partyId);
	mapEmployee.put("partyFullName", partyFullName);
	listEmployee.add(mapEmployee);
}
context.listEmployee = listEmployee;