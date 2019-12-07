import org.ofbiz.entity.condition.EntityCondition;
	emplID=eplId;
	ClassId=trainingClassId;
	emplName=delegator.findList("Person",EntityCondition.makeCondition("partyId",eplId),null,null,null,false);
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
	tcl=delegator.findList("TrainingClass",null,null,null,null,false);
	for(GenericValue tclItem:tcl){
		if(tclItem.getString("trainingClassId").equals(ClassId)){
			tclName=tclItem.getString("trainingClassName");
		}
	}

	context.fullName=fullName;
	context.emplID=emplID;
	context.tclName=tclName;