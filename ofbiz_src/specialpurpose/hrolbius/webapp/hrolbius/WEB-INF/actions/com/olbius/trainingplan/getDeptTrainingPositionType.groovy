import com.olbius.util.PartyUtil;
import org.ofbiz.entity.condition.EntityCondition;

partyId = userLogin.get("partyId");
internalOrgId = PartyUtil.getOrgByManager(partyId, delegator);

deptTrainingPosTypeList = delegator.findList("TrainingDeptPosition", 
					EntityCondition.makeCondition("trainingDeptId", internalOrgId), null, null, null, false);
context.deptTrainingPosTypeList = deptTrainingPosTypeList;