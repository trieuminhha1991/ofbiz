import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import com.olbius.basehr.util.MultiOrganizationUtil;


List<GenericValue> listOrderRole = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "SUPPLIER_AGENT")), null, null, null, false);
if(UtilValidate.isNotEmpty(listOrderRole)){
	String partyId = listOrderRole.get(0).getString("partyId");
	GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
	GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
	if (party.getString("partyCode")){ 
		context.partyName = "[" + party.getString("partyCode") +"]" + " " + partyGroup.getString("groupName");	
	} else { 
		context.partyName = "[" + party.getString("partyId") +"]" + " " + partyGroup.getString("groupName");
	}
}
