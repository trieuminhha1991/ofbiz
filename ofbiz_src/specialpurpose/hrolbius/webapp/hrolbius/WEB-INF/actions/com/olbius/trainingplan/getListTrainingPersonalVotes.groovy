import org.ofbiz.entity.condition.EntityCondition;

emplName=delegator.findList("Person",EntityCondition.makeCondition("partyId",partyId),null,null,null,false);
String fullName="";
if(!emplName.isEmpty()){
if(emplName.get(0).getString("lastName")!=null){
fullName=emplName.get(0).getString("lastName")+" ";
	if(emplName.get(0).getString("middleName")!=null){
		fullName+=emplName.get(0).getString("middleName")+" ";
		if(emplName.get(0).getString("firstName")!=null){
			fullName+=emplName.get(0).getString("firstName")+ " ";
		}
	}
}
}	
context.fullName=fullName;
