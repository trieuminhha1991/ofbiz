import com.olbius.util.PartyUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;

userLogin=context.get("userLogin");
partyId=userLogin.getString("partyId");
partyIdCEO=PartyUtil.getCEO(delegator);
if(!partyId.equals(partyIdCEO)){
	DeptId=PartyUtil.getOrgByManager(partyId,delegator);
System.out.println("ppId="+ppId);
EntityCondition condition1=EntityCondition.makeCondition("trainingProposalId",ppId);
EntityCondition condition2=EntityCondition.makeCondition("partyId",DeptId);
EntityCondition condition=EntityCondition.makeCondition(EntityJoinOperator.AND,condition1,condition2);
//if(!partyId.equals(PartyUtil.getHrmAdmin(delegator))){
	listDeptName=delegator.findList("TrainingProposal",condition,null,null,null,false);
//}
//else listDeptName=delegator.findList("TrainingProposal",null,null,null,null,false);
	
}else listDeptName=delegator.findList("TrainingProposal",null,null,null,null,false);
context.listDeptName=listDeptName;