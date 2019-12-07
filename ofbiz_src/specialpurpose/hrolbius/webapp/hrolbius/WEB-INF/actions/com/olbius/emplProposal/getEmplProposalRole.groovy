import javolution.util.FastList;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

if(emplProposal){
	List<EntityCondition> conditions = FastList.newInstance();
	conditions.add(EntityUtil.getFilterByDateExpr());
	conditions.add(EntityCondition.makeCondition("emplProposalId", emplProposal.emplProposalId));
	List<GenericValue> emplProposalRoleType = delegator.findList("EmplProposalRoleType", EntityCondition.makeCondition(conditions), null, null, null, false);
	List<String> partyInProposalRole = EntityUtil.getFieldListFromEntityList(emplProposalRoleType, "partyId", true);
	context.hasViewProposalPermission = false;
	if(partyInProposalRole.contains(userLogin.partyId) || userLogin.partyId.equals(emplProposal.getString("partyId"))){
		context.hasViewProposalPermission = true;		
		
		//role of userLogin.partyId in emplProposal
		List<GenericValue> partyRoleInEmplProposal = EntityUtil.filterByCondition(emplProposalRoleType, EntityCondition.makeCondition("partyId", userLogin.partyId));
		List<String> partyRoleInEmplProposalStr = EntityUtil.getFieldListFromEntityList(partyRoleInEmplProposal, "roleTypeId", true);
		
		//list all party approve emplProposal
		List<GenericValue> allPartyApproval = delegator.findList("EmplProposalApprovalAndRoleType", EntityCondition.makeCondition(conditions), null, null, null, false);
		List<String> allPartyApprovalStr = EntityUtil.getFieldListFromEntityList(allPartyApproval, "partyId", true);
		context.allPartyApproval = allPartyApproval;
		
		EntityCondition condition1 = EntityCondition.makeCondition("roleTypeId", "PPSL_APPROVER");
		if(UtilValidate.isNotEmpty(allPartyApprovalStr)){
			condition1 = EntityCondition.makeCondition(condition1, EntityOperator.AND, EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN, allPartyApprovalStr));
		}
		//list party have role PPSL_APPROVER not approved
		List<GenericValue> listApproverRoleApproved = EntityUtil.filterByCondition(emplProposalRoleType, condition1);
		/*List<String> listAllowedApprove = FastList.toList("PPSL_APPROVER", "PPSL_CONFIRMER", "PPSL_DECIDER");*/
		
		if(!allPartyApprovalStr.contains(userLogin.partyId)){//userLogin.party have update permission when userLogin.party haven't approve  
			if(UtilValidate.isEmpty(listApproverRoleApproved)){//if all party have role PPSL_APPROVER approved, party have role PPSL_CONFIRMER will approve
				//check whether the party CONFIRMER have approved or not
				EntityCondition condition2 = EntityCondition.makeCondition("roleTypeId", "PPSL_CONFIRMER");
				if(UtilValidate.isNotEmpty(allPartyApprovalStr)){
					condition2 = EntityCondition.makeCondition(condition2, EntityOperator.AND, EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN, allPartyApprovalStr));
				}
				List<GenericValue> listConfirmerRoleApproved = EntityUtil.filterByCondition(emplProposalRoleType, condition2);
				if(UtilValidate.isEmpty(listConfirmerRoleApproved)){//if all party have PPSL_CONFIRMER role approve, the party have role PPSL_DECIDER will update permission to approve 
					if(partyRoleInEmplProposalStr.contains("PPSL_DECIDER")){
						context.hasUpdateProposalPermission = true;
					}
				}else if(partyRoleInEmplProposalStr.contains("PPSL_CONFIRMER")){
					context.hasUpdateProposalPermission = true;
				}
			}else if(partyRoleInEmplProposalStr.contains("PPSL_APPROVER")){
				context.hasUpdateProposalPermission = true;
			}
		}
	}
}