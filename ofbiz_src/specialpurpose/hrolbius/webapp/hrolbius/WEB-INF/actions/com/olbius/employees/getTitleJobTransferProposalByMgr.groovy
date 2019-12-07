import org.ofbiz.base.util.*;

jobTransferProposalTypeId = parameters.jobTransferProposalTypeId;

uiLabelMap = UtilProperties.getResourceBundleMap("EmployeeUiLabels", locale);

if (jobTransferProposalTypeId == 'TRANSFER_POSITION'){
	titleJobTransfer = uiLabelMap.CreateTransPositionProposalByMgr;
}

if (jobTransferProposalTypeId == 'TRANSFER_DEPT'){
	titleJobTransfer = uiLabelMap.CreateTransDeptProposalByMgr;
}

context.titleJobTransfer = titleJobTransfer;
