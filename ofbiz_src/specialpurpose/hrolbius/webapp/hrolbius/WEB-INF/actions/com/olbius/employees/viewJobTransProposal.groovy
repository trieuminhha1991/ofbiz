import com.olbius.employee.helper.EmployeeHelper;

jobTransferProposalId = parameters.jobTransferProposalId;
if(jobTransferProposalId){
	context.jobTransferProposal = EmployeeHelper.getJobTransProposal(delegator, jobTransferProposalId);
}