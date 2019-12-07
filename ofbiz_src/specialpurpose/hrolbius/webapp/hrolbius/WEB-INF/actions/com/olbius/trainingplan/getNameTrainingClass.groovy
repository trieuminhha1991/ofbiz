import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.base.util.UtilMisc;
listTl=delegator.findList("TrainingClass",EntityCondition.makeCondition("trainingClassId",classId),null,null,null,false);
epl=delegator.findOne("Person",false,UtilMisc.toMap("partyId",empl));
	String employeeName="";
	if(epl.getString("firstName")!=null){
			 employeeName=epl.getString("firstName")+" ";
		if(epl.getString("middleName")!=null){
			employeeName+=epl.getString("middleName")+" ";
			if(epl.getString("lastName")!=null){
				employeeName+=epl.getString("lastName");
			}
		}
	}

TrainingClassName=listTl.get(0).getString("trainingClassName");
context.employeeName=employeeName;
context.TrainingClassName=TrainingClassName;