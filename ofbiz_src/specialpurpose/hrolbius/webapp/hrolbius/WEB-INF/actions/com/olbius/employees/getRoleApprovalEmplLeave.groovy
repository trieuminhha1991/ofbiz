
import java.sql.Timestamp;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

/*if(parameters.partyIdLeave == null){
	parameters.partyIdLeave = parameters.partyId;
}
if(parameters.leaveFromDate == null){
	parameters.leaveFromDate = parameters.fromDate.toString();
}*/
//GenericValue emplLeave = delegator.findOne("EmplLeave", UtilMisc.toMap("fromDate", Timestamp.valueOf(parameters.leaveFromDate), "partyId", parameters.partyIdLeave, "leaveTypeId", parameters.leaveTypeId), false);
GenericValue emplLeave = delegator.findOne("EmplLeave", UtilMisc.toMap("emplLeaveId", parameters.emplLeaveId), false);
if(emplLeave){
	context.emplLeave =emplLeave;
	List<EntityCondition> conditions = FastList.newInstance();
	conditions.add(EntityUtil.getFilterByDateExpr());
	/*conditions.add(EntityCondition.makeCondition("partyIdLeave", parameters.partyIdLeave));
	conditions.add(EntityCondition.makeCondition("leaveFromDate", Timestamp.valueOf(parameters.leaveFromDate)));
	conditions.add(EntityCondition.makeCondition("leaveTypeId", parameters.leaveTypeId));*/
	conditions.add(EntityCondition.makeCondition("emplLeaveId", parameters.emplLeaveId));
	
	//all party approve emplLeave
	List<GenericValue> listPartyApprovalEmplLeave = delegator.findList("EmplLeaveApprovalAndRoleType", EntityCondition.makeCondition(conditions), null, null, null, false); 
	List<String> listPartyApprovedStr = EntityUtil.getFieldListFromEntityList(listPartyApprovalEmplLeave, "partyId", true);
	context.listPartyApprovalEmplLeave = listPartyApprovalEmplLeave;
	
	List<GenericValue> allRoleTypeApprEmplLeave = delegator.findList("EmplLeaveApprovalRoleType", EntityCondition.makeCondition(conditions), null, null, null, false); 
	
	//role userLogin.partyId in EmplLeaveApprovalRoleType
	List<GenericValue> partyRoleInEmplLeaveAppr = EntityUtil.filterByCondition(allRoleTypeApprEmplLeave, EntityCondition.makeCondition("partyId", userLogin.partyId));
																								
    if(UtilValidate.isNotEmpty(partyRoleInEmplLeaveAppr) || userLogin.partyId.equals(emplLeave.partyId)){
		List<String> partyRoleAppr = EntityUtil.getFieldListFromEntityList(partyRoleInEmplLeaveAppr, "roleTypeId", true);
		EntityCondition condition1 = EntityCondition.makeCondition("roleTypeId", "ELA_APPROVER");
		if(UtilValidate.isNotEmpty(listPartyApprovedStr)){
			condition1 = EntityCondition.makeCondition(condition1, EntityOperator.AND, EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN, listPartyApprovedStr))
		}
		List<GenericValue> listApproverRoleAppr = EntityUtil.filterByCondition(allRoleTypeApprEmplLeave, condition1);
			/*delegator.findList("EmplLeaveApprovalAndRoleType", EntityCondition.makeCondition(
			EntityCondition.makeCondition(conditions),
			EntityOperator.AND,
			EntityCondition.makeCondition("roleTypeId", "ELA_APPROVER")), null, null, null, false);*/
		//List<String> listRoleType = EntityUtil.getFieldListFromEntityList(listRoleTypeApprEmplLeave, "roleTypeId", true);
		if(UtilValidate.isEmpty(listApproverRoleAppr)){
			//if all party have role ELA_APPROVER have approved and userLogin.partyId haven't approved, so now the party have role ELA_DECIDER can have permission approval
			if(partyRoleAppr.contains("ELA_DECIDER")  && !listPartyApprovedStr.contains(userLogin.partyId)){
				context.updateEmplLeavePerm = true;
			}
		}else if(partyRoleAppr.contains("ELA_APPROVER") && !listPartyApprovedStr.contains(userLogin.partyId)){
			context.updateEmplLeavePerm = true;
		}
		context.viewEmplLeavePerm = true;
	}else{
		context.viewEmplLeavePerm = false;
	}																							
	
	
}
