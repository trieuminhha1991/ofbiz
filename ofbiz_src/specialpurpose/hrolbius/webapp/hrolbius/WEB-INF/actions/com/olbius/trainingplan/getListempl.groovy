import org.ofbiz.entity.condition.EntityCondition;

listEmplId=delegator.findList("EmplTrainingProposal",EntityCondition.makeCondition("trainingProposalId",trainingProposalId) ,null,null,null,false);
String ListNameEmplId="";
String fullName="";
String lastName="";
String middleName="";
String firstName="";
for(int i=0;i<listEmplId.size();i++){
	String emplIdtemp=listEmplId.get(i).getString("partyId");
	list=delegator.findList("Person",EntityCondition.makeCondition("partyId",emplIdtemp) ,null,null,null,false);
	
	lastName=list.get(0).getString("lastName");
	middleName=list.get(0).getString("middleName");
	firstName=list.get(0).getString("firstName");
	if(lastName!=null){
		fullName=lastName+" ";
		if(middleName!=null){
			fullName+=middleName+" ";
				if(firstName!=null){
					fullName+=firstName;
				}
		}
	}
	ListNameEmplId=ListNameEmplId.concat("-"+fullName+"\n");
}

context.ListNameEmplId = ListNameEmplId;