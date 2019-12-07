//delete
import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;

import com.olbius.employee.helper.EmployeeHelper;
import com.olbius.util.PartyUtil;


jobTransferProposalId = parameters.jobTransferProposalId;
if(jobTransferProposalId){
	GenericValue jobTransferProposal = delegator.findOne("JobTransferProposal", UtilMisc.toMap("jobTransferProposalId", jobTransferProposalId), false);
	if(UtilValidate.isNotEmpty(jobTransferProposal)){
		String partyIdTransfer = jobTransferProposal.getString("partyIdTransfer");
		String managerOfpartyTransfer = PartyUtil.getManagerOfEmpl(delegator, partyIdTransfer);
		String ceoId = PartyUtil.getCEO(delegator);
		String headOfDept = PartyUtil.getHrmAdmin(delegator);
		jobTransferProposal = EmployeeHelper.getJobTransProposal(delegator, jobTransferProposalId);
		context.jobTransferProposal = jobTransferProposal;
		context.employeeName = PartyHelper.getPartyName(delegator, partyIdTransfer, false);
		context.currDept = PartyHelper.getPartyName(delegator, PartyUtil.getDepartmentOfEmployee(delegator, partyIdTransfer).getString("partyIdFrom"), false) ;
		context.toDept = PartyHelper.getPartyName(delegator, jobTransferProposal.getString("internalOrgUnitToId"), false);
		//println ("context.toDept: " + context.toDept);
		String statusId = jobTransferProposal.getString("statusId");
		context.currStatusId = statusId;
		context.statusEdit = false;
		if(jobTransferProposal.getString("jobTransferProposalTypeId").equalsIgnoreCase("TRANSFER_DEPT")){
			String managerOfDeptTo = PartyUtil.getManagerbyOrg(jobTransferProposal.getString("internalOrgUnitToId"), delegator);
			if(statusId.equalsIgnoreCase("JTP_CREATED")) {
				if(managerOfpartyTransfer.equalsIgnoreCase(userLogin.partyId)){
					context.statusList = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusTypeId", "JTP_TRANS_DEPT_FDM"), UtilMisc.toList("sequenceId"), false);
					context.statusEdit = true;
				}else{
					context.statusEdit = false;
				}
			}else if(statusId.equalsIgnoreCase("TRANS_DEPT_FDM_A")){
				if(managerOfDeptTo.equalsIgnoreCase(userLogin.partyId)){
					context.statusList = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusTypeId", "JTP_TRANS_DEPT_TDM"), UtilMisc.toList("sequenceId"), false);
					context.statusEdit = true;
				}else{
					context.statusEdit = false;
				}
			}else if(statusId.equalsIgnoreCase("TRANS_DEPT_TDM_A")){
				if(headOfDept.equalsIgnoreCase(userLogin.partyId)){
					context.statusList = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusTypeId", "JTP_TRANS_DEPT_HHR"), UtilMisc.toList("sequenceId"), false);
					context.statusEdit = true;
				}else{
					context.statusEdit = false;
				}
			}else if(statusId.equalsIgnoreCase("TRANS_DEPT_HHR_A")){
				if(ceoId.equalsIgnoreCase(userLogin.partyId)){
					context.statusList = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusTypeId", "JTP_TRANS_DEPT_CEO"), UtilMisc.toList("sequenceId"), false);
					context.statusEdit = true;
				}else{
					context.statusEdit = false;
				}
			}
		}else if(jobTransferProposal.getString("jobTransferProposalTypeId").equalsIgnoreCase("TRANSFER_POSITION")){
			if(statusId.equalsIgnoreCase("JTP_CREATED")){
				//transfer position proposal created, so it must approve by manager
				if(managerOfpartyTransfer.equalsIgnoreCase(userLogin.partyId)){
					context.statusEdit = true;
					context.statusList = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusTypeId", "JTP_TRANS_POS_MGR"), UtilMisc.toList("sequenceId"), false);
				}else{
					context.statusEdit = false;
				}
			}else if(statusId.equalsIgnoreCase("TRANS_POS_MGR_A")){
				// transfer position proposal accepted by manager, so it must approve by head of HR
				if(headOfDept.equalsIgnoreCase(userLogin.partyId)){
					context.statusList = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusTypeId", "JTP_TRANS_POS_HR"), UtilMisc.toList("sequenceId"), false);
					context.statusEdit = true;
				}else{
					context.statusEdit = false;
				}
			}else if(statusId.equalsIgnoreCase("TRANS_POS_HHR_A")){
				//transfer position proposal accepted by head of HR, so it must approve by ceo
				if(ceoId.equalsIgnoreCase(userLogin.partyId)){
					context.statusList = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusTypeId", "JTP_TRANS_POS_CEO"), UtilMisc.toList("sequenceId"), false);
					context.statusEdit = true;
				}else{
					context.statusEdit = false;
				}
			}
		}
		
	}else{
		context.jobTransferProposal = delegator.makeValue("JobTransferProposal");	
		/*request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage("HrCommonUiLabels", "NoFindRecord", locale));*/
	} 
	
}