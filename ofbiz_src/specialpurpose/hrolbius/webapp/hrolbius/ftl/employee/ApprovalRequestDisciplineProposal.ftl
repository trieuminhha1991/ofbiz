<div class="row-fluid">
	<#if hasViewProposalPermission>
		<div class="row-fluid">
			<div class="span12 mgt20 boder-all-profile">
				<span class="text-header">${uiLabelMap.DetailsRequestDisciplineProposal}</span>
				<div class="form-horizontal">
					<div class="span6">
						<div class="control-group no-left-margin">
							<label class="control-label">
								${uiLabelMap.PartyRequestedDisciplining}
							</label>
							<div class="controls">
								<#if partyRequestedDisciplines?has_content>
									<#assign partyRequestedDisciplining = partyRequestedDisciplines[0]>
									<#assign disciplined = delegator.findOne("Person", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyRequestedDisciplining.partyId), false)>
									${disciplined.firstName?if_exists} ${disciplined.middleName?if_exists} ${disciplined.lastName?if_exists}
								<#else>
									&nbsp;
								</#if>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">
								${uiLabelMap.HREmployeePosition}
							</label>
							<div class="controls">
								<#assign disciplinedPosition = Static["com.olbius.util.PartyUtil"].getCurrPosTypeOfEmplOverview(delegator, disciplined.partyId)>
								${disciplinedPosition?if_exists}&nbsp;
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">
								${uiLabelMap.EmployeeCurrentDept}
							</label>
							<div class="controls">
								<#assign currDeptDisciplined = Static["com.olbius.util.PartyUtil"].getDepartmentOfEmployee(delegator, disciplined.partyId)>
								<#if currDeptDisciplined?exists>
									<#assign deptNameDisciplined = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, currDeptDisciplined.getString("partyIdFrom"), false)>
									${deptNameDisciplined}
								<#else>
									&nbsp;	
								</#if>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">
								${uiLabelMap.HREmplWarningLevel}
							</label>
							<div class="controls">
								<#assign partyPunishmentRemindCount = delegator.findOne("PartyPunishmentRemindCount", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", disciplined.partyId), false)>
								<#if partyPunishmentRemindCount?exists>
									${partyPunishmentRemindCount.punishmentCountSum?if_exists}
								<#else>
									&nbsp;	
								</#if>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">
								${uiLabelMap.HREmplPunishmentLevel}
							</label>
							<div class="controls">
								 <#assign partyPunishmentLevel = delegator.findOne("PartyPunishmentLevel", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", disciplined.partyId), false)>
								 <#if partyPunishmentLevel?exists>
								 	${partyPunishmentLevel.punishmentLevel?if_exists}
								 <#else>
								 	&nbsp;	
								 </#if>
							</div>
						</div>
						<#if emplProposal.comment?exists>
							<div class="control-group no-left-margin">
								<label class="control-label">
									${uiLabelMap.HRNotes}
								</label>
								<div class="controls">
									${emplProposal.comment}
								</div>
							</div>
						</#if>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.CommonStatus}</label>
							<div class="controls">
								<#assign currStatus = delegator.findOne("StatusItem" Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", emplProposal.statusId), false)>
								${currStatus.description?if_exists}&nbsp;					
							</div>
						</div>
					</div>
					<div class="span6">
						<div class="control-group no-left-margin">
							<label class="control-label">
								${uiLabelMap.HREmplProposer}
							</label>
							<div class="controls">
								${proposer.lastName?if_exists} ${proposer.middleName?if_exists} ${proposer.firstName?if_exists}
							</div>
						</div>
						<div class="control-group no-left-margin">
							<#assign proposerPosition = Static["com.olbius.util.PartyUtil"].getCurrPosTypeOfEmplOverview(delegator, proposer.partyId)>
							<label class="control-label">
								${uiLabelMap.HREmployeePosition}
							</label>
							<div class="controls">
								${proposerPosition?if_exists} &nbsp;
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">
								${uiLabelMap.EmployeeCurrentDept}
							</label>
							<div class="controls">
								<#assign currDept = Static["com.olbius.util.PartyUtil"].getDepartmentOfEmployee(delegator, proposer.partyId)>
								<#if currDept?exists>
									<#assign deptName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, currDept.getString("partyIdFrom"), false)>
									${deptName}
								<#else>
									&nbsp;	
								</#if>
							</div>
						</div>					
						<div class="control-group no-left-margin">
							<label class="control-label">
								${uiLabelMap.HRApprover} 
							</label>
							<div class="controls">
								<#if partyApproverRequest?has_content>
									<#assign partyIdApproverRequest = partyApproverRequest[0].partyId>
									<#assign approver = delegator.findOne("Person", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyIdApproverRequest), false)>
									${approver.lastName?if_exists} ${approver.middleName?if_exists} ${approver.firstName?if_exists}
								<#else>
									&nbsp;
								</#if>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">
								${uiLabelMap.HREmployeePosition}
							</label>
							<div class="controls">
								<#assign approverPosition = Static["com.olbius.util.PartyUtil"].getCurrPosTypeOfEmplOverview(delegator, partyIdApproverRequest)>
								${approverPosition?if_exists}
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">
								${uiLabelMap.EmployeeCurrentDept}
							</label>
							<div class="controls">
								<#assign currDeptApprover = Static["com.olbius.util.PartyUtil"].getDepartmentOfEmployee(delegator, partyIdApproverRequest)>
								<#if currDeptApprover?exists>
									<#assign approverDeptName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, currDeptApprover.getString("partyIdFrom"), false)>
									${approverDeptName}
								<#else>
									&nbsp;	
								</#if>
								
							</div>
						</div>
						<#if allPartyApproval?has_content && allPartyApproval[0].comment?exists>
							<div class="control-group no-left-margin">
								<label class="control-label">
									${uiLabelMap.NoteOfApprove}
								</label>
								<div class="controls">
									${allPartyApproval[0].comment}
								</div>
							</div>
						</#if>
					</div>
				</div>
			</div>
		</div>
		<#if hasUpdateProposalPermission?exists && hasUpdateProposalPermission && emplProposal.statusId != "PPSL_ACCEPTED" && emplProposal.statusId != "PPSL_REJECTED">
		<div class="row-fluid">
			<div class="span12 mgt20 boder-all-profile">
				<form action="<@ofbizUrl>updateRequestDisciplineProposal</@ofbizUrl>" method="post" name="approveRequestDiscipline" id="approveRequestDiscipline" class="form-horizontal">
					<input type="hidden" name="emplProposalId" value="${emplProposal.emplProposalId}">
					<input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
					<div class="control-group no-left-margin">
						<label class="control-label">
							${uiLabelMap.HRApprove}
						</label>
						<div class="controls">
							<select name="approvalStatusId">
								<#list statusList as status>
									<option value="${status.statusId}">${status.description}</option>
								</#list>
							</select>					
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">
							${uiLabelMap.HRNotes}
						</label>
						<div class="controls">
							<input type="text" name="comment">
						</div>
					</div>
					<!-- <div class="control-group no-left-margin">
						<label class="control-label">
							&nbsp;
						</label>
						<div class="controls">
							<label>
								<input name="createProposalSacking" type="checkbox" value="Y"/>
								<span class="lbl">${uiLabelMap.CreateProposalSackingToHRMAdmin}</span>
							</label>
						</div>
					</div> -->
					<div class="control-group no-left-margin">
						<label class="control-label">
							&nbsp;
						</label>
						<div class="controls">
							<button type="submit" class="btn btn-small btn-primary" name="submitButton" id="btnSubmit">
								<i class="icon-ok"></i>
								${uiLabelMap.CommonSubmit}	
							</button>
						</div>
					</div>
				</form>
			</div>
		</div>
		</#if>
	<#else>
		<div class="alert alert-info">
			<h4>${uiLabelMap.NotHavePermissionView}</h4>
		</div>	
	</#if>
</div>