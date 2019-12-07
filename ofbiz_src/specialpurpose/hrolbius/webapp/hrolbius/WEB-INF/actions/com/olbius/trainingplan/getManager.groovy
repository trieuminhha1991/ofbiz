import com.olbius.util.PartyUtil;
import java.util.ArrayList;
listProposeTraining=delegator.findList("TrainingProposal",null,null,null,null,false);
ArrayList<String> listDeptName=new ArrayList<String>();
for(int i=0;i<listProposeTraining.size();i++){
	listDeptName.add(i,listProposeTraining.get(i).getString("partyId"));
}
ArrayList<String> listManagerId=new ArrayList<String>();
for(int j=0;j<listDeptName.size();j++){
	if(!listManagerId.contains(PartyUtil.getManagerbyOrg(listDeptName.get(j),delegator))){
		listManagerId.add(PartyUtil.getManagerbyOrg(listDeptName.get(j),delegator));
	}
	//System.out.println(listManagerId.get(j));
}
context.listManagerId=listManagerId;