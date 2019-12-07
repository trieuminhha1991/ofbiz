import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilMisc;

GenericValue person= delegator.findOne("PartyNameView",UtilMisc.toMap("partyId",partyId),false);
if (person){
	firstName = person.getString("firstName");
	lastName = person.getString("lastName");
	middleName = person.getString("middleName");
	if (middleName){
		fullName = firstName + " " + middleName + " " + lastName;
	} else{
		fullName = firstName + " " + lastName;
	}
}

if (fullName){
	context.fullName=fullName;
}
