<div class="row-fluid">
<div class="row-fluid">
	<div class="span12 mgt20 boder-all-profile">
		<span class="text-header">${uiLabelMap.DetailsProposal}</span>
		<div class="form-horizontal">
			<div class="control-group no-left-margin">
				<label class="control-label">
					${uiLabelMap.EmployeeName}
				</label>
				<div class="controls">
					${emplProposal.lastName?if_exists} ${emplProposal.middleName?if_exists} ${emplProposal.lastName?if_exists} [${emplTerminationProposal.partyId}]
				</div>
			</div>
			<div class="control-group no-left-margin">
				<label class="control-label">
					${uiLabelMap.EmployeeCurrentDept}
				</label>
				<div class="controls">
					${currDept?if_exists}
				</div>
			</div>
			<div class="control-group no-left-margin">
				<label class="control-label">
					${uiLabelMap.HREmplFromPositionType}
				</label>
				<div class="controls">
					${currPositionsStr?if_exists}
				</div>
			</div>
			<div class="control-group no-left-margin">
				<label class="control-label">
					${uiLabelMap.HREmplTerminalTypeId}
				</label>
				<div class="controls">
					${terminationType.description?if_exists}
				</div>
			</div>
			<div class="control-group no-left-margin">
				<label class="control-label">
					${uiLabelMap.HREmplDateResign}
				</label>
				<div class="controls">
					${emplTerminationProposal.dateTermination?string["dd/MM/yyyy"]}
				</div>
			</div>
			<div class="control-group no-left-margin">
				<label class="control-label">
					${uiLabelMap.HREmplReasonResign}
				</label>
				<div class="controls">
					<#if emplTerminationProposal.terminationReasonId?exists>
						<#assign terminationReason = delegator.findOne("TerminationReason", Static["org.ofbiz.base.util.UtilMisc"].toMap("terminationReasonId",  emplTerminationProposal.terminationReasonId), false)>
						${terminationReason.description?if_exists}
					<#else>
						&nbsp;	
					</#if>
				</div>
			</div>
			<div class="control-group no-left-margin">
				<label class="control-label">
					${uiLabelMap.CommonStatus}
				</label>
				<div class="controls">
					<#assign status = delegator.findOne("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId",  emplTerminationProposal.statusId), false)>
					${status.description?if_exists}
				</div>
			</div>
		</div>
	</div>
</div>
<div class="row-fluid">
	<div class="span12 mgt20 boder-all-profile">
		<span class="text-header">${uiLabelMap.DetailsApproveProposal}</span>
		<div class="form-horizontal">
			<#list terminationApprovalRoleTypeList as approvalRoleType>
				<#if approvalRoleType_index%2 == 0 >
					<#if approvalRoleType_index &gt; 0>
						<hr/>
					</#if>							
					<div class="row-fluid">
						<div class="span12">
				</#if>
				<div class="span6">
					<div class="control-group no-left-margin">
						<label>
							<label>${uiLabelMap.HRApprover}</label>
						</label>
						<div class="controls">
							<#assign partyApprover = delegator.findOne("Person", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", approvalRoleType.partyId), false)>
							${partyApprover.lastName} ${partyApprover.middleName} ${partyApprover.firstName}  
						</div>
					</div>
					<#assign approvalList = delegator.findByAnd("EmplTerminationApproval", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", approvalRoleType.partyId, "emplTerminationProposalId", approvalRoleType.emplTerminationProposalId, "roleTypeId", approvalRoleType.roleTypeId))>
					<#assign approval = "">
					<#if approvalList?has_content>
						<#assign approval = approvalList.get(0)>
						<#assign statusApproval = delegator.findOne("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", approval.approvalStatusId), false)>
						<#assign currStatusApproval = statusApproval.description> 
					<#else>
						<#assign currStatusApproval = uiLabelMap.HRNotApproval>
					</#if>
					<div class="control-group no-left-margin">
						<label>
							<label>${uiLabelMap.CommonStatus}</label>
						</label>
						<div class="controls">
							${currStatusApproval}
						</div>
					</div>
					<#if approval?has_content>
						<div class="control-group no-left-margin">	
							<label>
								<label>${uiLabelMap.ApprovalDate}</label>
							</label>
							<div class="controls">
								${approval.approvalDate?string["dd-MM-yyyy"]}
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label>
								<label>${uiLabelMap.HRNotes}</label>
							</label>
							<div class="controls">
								${approval.comments?if_exists}&nbsp;
							</div>
						</div>
					</#if>
				</div>
				<#if approvalRoleType_index%2 == 1 || !approvalRoleType_has_next>
						</div>
					</div>
				</#if>
			</#list>
		</div>	
	</div>
</div>
<#if partyEmplTermApproval?has_content>
	<#assign partyApprover =  partyEmplTermApproval.get(0)>
	<#if partyApprover.isApproved?exists && partyApprover.isApproved == "N">
		<hr/>
		<div class="row-fluid">
		<form method="post" action="<@ofbizUrl>updateEmplTerminationProposal</@ofbizUrl>" class="basic-form form-horizontal">
			<input type="hidden" name="emplTerminationProposalId" value="${emplTerminationProposal.emplTerminationProposalId}">
			<input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
			<div class="span12">
				<div class="control-group no-left-margin">	
					<label>
						<label>${uiLabelMap.HRApprove}</label>
					</label>
					<div class="controls">
						<select name="approvalStatusId">
							<#list statusApprovalList as status>
								<option value="${status.statusId}">${status.description}</option>
							</#list>
						</select>
					</div>
				</div>	
				<div class="control-group no-left-margin">
					<label>
						<label>${uiLabelMap.HRNotes}</label>
					</label>
					<div class="controls">
						<input type="text" name="comments">
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label" for="">&nbsp;</label>
					<div class="controls">
						<button class="btn btn-small btn-primary icon-ok">
							${uiLabelMap.CommonSubmit}
						</button>
					</div>
				</div>
			</div>
			
		</form>
		</div>
	</#if>
</#if>
</div>