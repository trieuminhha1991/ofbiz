<div class="row-fluid">
	<#if hasViewProposalPermission>
		<div class="row-fluid">
			<div class="span12 mgt20 boder-all-profile">
				<span class="text-header">${uiLabelMap.DetailsSackingProposal}</span>
				<div class="form-horizontal">
					<div class="span6">
						<div class="control-group no-left-margin">
							<label class="control-label">
								${uiLabelMap.EmplProposedProposalSacking}
							</label>
							<div class="controls">
								<#if proposedRoleTypeList?has_content>
									<#assign proposedId = proposedRoleTypeList[0].partyId>
									<#assign proposed = delegator.findOne("Person", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", proposedId), false)>
									${proposed.lastName?if_exists} ${proposed.middleName?if_exists} ${proposed.firstName?if_exists}
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
								<#if proposed?exists>
									<#assign proposedPosition = Static["com.olbius.util.PartyUtil"].getCurrPosTypeOfEmplOverview(delegator, proposed.partyId)>
									<#if proposedPosition?has_content>
										${proposedPosition}
									<#else>
										${uiLabelMap.NotBelongAnyPosition}
									</#if>
									
								<#else>
									&nbsp;	 
								</#if>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">
								${uiLabelMap.EmployeeCurrentDept}
							</label>
							<div class="controls">
								<#if proposed?exists>
									<#assign currDeptProposed = Static["com.olbius.util.PartyUtil"].getDepartmentOfEmployee(delegator, proposed.partyId)>
									<#if currDeptProposed?exists>
										<#assign deptNameProposed = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, currDeptProposed.getString("partyIdFrom"), false)>
										${deptNameProposed}
									<#else>
										${uiLabelMap.NotBelongAnyDepartment}	
									</#if>
								<#else>
									&nbsp;	
								</#if>
							</div>
						</div>
						<#if proposed?exists>
							<div class="control-group no-left-margin">
								<label class="control-label">
									${uiLabelMap.HREmplWarningLevel}
								</label>
								<div class="controls">
									<#assign partyPunishmentRemindCount = delegator.findOne("PartyPunishmentRemindCount", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", proposed.partyId), false)>
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
									 <#assign partyPunishmentLevel = delegator.findOne("PartyPunishmentLevel", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", proposed.partyId), false)>
									 <#if partyPunishmentLevel?exists>
									 	${partyPunishmentLevel.punishmentLevel?if_exists}
									 <#else>
									 	&nbsp;	
									 </#if>
								</div>
							</div>
						</#if>
					</div>
					<div class="span6">
						<div class="control-group no-left-margin">
							<label class="control-label">
								${uiLabelMap.HREmplSackingReason}
							</label>
							<div class="controls">
								<#if emplTerminationProposal.terminationReasonId?exists>
									<#assign terminationReason = delegator.findOne("TerminationReason", Static["org.ofbiz.base.util.UtilMisc"].toMap("terminationReasonId", emplTerminationProposal.terminationReasonId), false)>
									${terminationReason.description?if_exists}
								<#else>
									&nbsp;	
								</#if>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.HREmplDateSacking}</label>
							<div class="controls">
								<#if emplTerminationProposal.dateTermination?exists>
									${emplTerminationProposal.dateTermination?string["dd/MM/yyyy"]}
								<#else>
									&nbsp;
								</#if>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.CommonStatus}</label>
							<div class="controls">
								<#assign currStatus = delegator.findOne("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", emplProposal.statusId), false)>
								${currStatus.description?if_exists}&nbsp;
							</div>
						</div>
						<#if emplProposer?exists>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.HREmplProposer}</label>
								<div class="controls">
									${emplProposer.lastName?if_exists} ${emplProposer.middleName?if_exists} ${emplProposer.firstName?if_exists}	
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">
									${uiLabelMap.HREmployeePosition}
								</label>
								<div class="controls">
									<#assign emplProposerPosition = Static["com.olbius.util.PartyUtil"].getCurrPosTypeOfEmplOverview(delegator, emplProposer.partyId)>
									${emplProposerPosition}
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">
									${uiLabelMap.EmployeeCurrentDept}
								</label>
								<div class="controls">
									<#assign currDeptProposer = Static["com.olbius.util.PartyUtil"].getDepartmentOfEmployee(delegator, emplProposer.partyId)>
									<#if currDeptProposer?exists>
										<#assign deptNameProposer = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, currDeptProposer.getString("partyIdFrom"), false)>
										${deptNameProposer}
									</#if>
								</div>
							</div>
							<#if emplProposal.comment?exists>
								<div class="control-group no-left-margin">
									<label class="control-label">
										${uiLabelMap.NoteOfProposer}
									</label>
									<div class="controls">
										${emplProposal.comment}
									</div>
								</div>
							</#if>
						</#if>
					</div>
				</div>
			</div>
		</div>
		<#if allPartyApproval?has_content>
			 <div class="row-fluid">
				<div class="span12 mgt20 boder-all-profile">
					<span class="text-header">${uiLabelMap.DetailProposalSackingApproval}</span>
					<div class="form-horizontal">
						<#list allPartyApproval as tempApproval>
							<#if tempApproval_index%2 == 0 >
								<#if tempApproval_index &gt; 0>
									<hr/>
								</#if>							
								<div class="row-fluid">
									<div class="span12">
							</#if>
							<div class="span6">
								<div class="control-group no-left-margin">
									<label class="control-label">
										${uiLabelMap.HRApprover}
									</label>
									<div class="controls">
										<#assign partyApprover = delegator.findOne("Person", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", tempApproval.partyId), false)>
										${partyApprover.lastName?if_exists} ${partyApprover.middleName?if_exists} ${partyApprover.firstName?if_exists}
									</div>
								</div>
								<div class="control-group no-left-margin">
									<label class="control-label">${uiLabelMap.HREmployeePosition}</label>
									<div class="controls">
										${Static["com.olbius.util.PartyUtil"].getCurrPosTypeOfEmplOverview(delegator, tempApproval.partyId)}
									</div>
								</div>
								<div class="control-group no-left-margin">
									<label class="control-label">
										${uiLabelMap.CommonStatus}
									</label>
									<div class="controls">
										<#assign currStatus = delegator.findOne("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", tempApproval.approvalStatusId), false)>
										${currStatus.description?if_exists}
									</div>
								</div>
								<div class="control-group no-left-margin">	
									<label class="control-label">${uiLabelMap.ApprovalDate}</label>
									<div class="controls">
										${tempApproval.approvalDate?string["dd-MM-yyyy"]}
									</div>
								</div>
								<#if tempApproval.comment?exists>
									<div class="control-group no-left-margin">
										<label class="control-label">${uiLabelMap.HRNotes}</label>
										<div class="controls">
											${tempApproval.comment}
										</div>
									</div>
								</#if>
							</div>	
							<#if tempApproval_index%2 == 1 || !tempApproval_has_next>
									</div>
								</div>
							</#if>
						</#list>
					</div>
				</div>
			</div>	
		</#if>
		<#if hasUpdateProposalPermission?exists && hasUpdateProposalPermission && emplProposal.statusId != "PPSL_ACCEPTED" && emplProposal.statusId != "PPSL_REJECTED">
			<div class="row-fluid">
				<div class="span12 mgt20 boder-all-profile">
					<form action="<@ofbizUrl>updateApprovalSackingProposal</@ofbizUrl>" method="post" name="updateApprovalSackingProposal" id="updateApprovalSackingProposal" class="form-horizontal">
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
		<#if (!emplTerminationProposal.isCompleteFormality?exists || !emplTerminationProposal.isCompleteFormality == "Y") 
				&& userLogin.partyId == emplProposal.partyId && emplProposal.statusId == "PPSL_ACCEPTED">
			<div class="row-fluid">
				<div class="span12 mgt20 boder-all-profile">
					<form action="<@ofbizUrl>removeEmplFromOrg</@ofbizUrl>" method="post" class="form-horizontal">
						<input type="hidden" name="emplProposalId" value="${emplProposal.emplProposalId}">
						<div class="control-group">
							<label class="control-label">&nbsp;</label>
							<div class="controls">
								<label style="text-align: left; margin-bottom: 20px">
									<input name="disableUserLogin" type="checkbox" value="Y"/>
									<span class="lbl">&nbsp;${uiLabelMap.DisbleEmplUserLogin}</span>
								</label>
		
								<label style="text-align: left;">
									<input name="expireEmpl" type="checkbox" value="Y"/>
									<span class="lbl">&nbsp;${uiLabelMap.RemoveEmplFromOrg}</span>
								</label>
							</div>
						</div>
						<!-- <div class="control-group">
							<label class="control-label">&nbsp;</label>
							<div class="controls">
								
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