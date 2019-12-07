import javolution.util.FastMap;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

boolean showColumns = false;
String partyId = userLogin.getString("partyId");
if(UtilValidate.isNotEmpty(partyId)){
	Map<String, String> mapCond = FastMap.newInstance();
	mapCond.put("partyId", partyId);
	mapCond.put("roleTypeId", "DELYS_PROCUREMENT");
	/*EntityCondition mainCond = EntityCondition.makeCondition(mapCond);*/
	GenericValue partyRole = delegator.findOne("PartyRole", mapCond, false);	
	if(UtilValidate.isNotEmpty(partyRole)){
		showColumns = true;
	}

}
context.showColumns = showColumns;